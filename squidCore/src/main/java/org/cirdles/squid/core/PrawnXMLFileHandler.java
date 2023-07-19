/*
 * Copyright 2016 James F. Bowring and CIRDLES.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.squid.core;

import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.prawn.PrawnFileRunFractionParser;
import org.cirdles.squid.prawnLegacy.PrawnLegacyFile;
import org.cirdles.squid.prawnLegacy.PrawnLegacyFileHandler;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.shrimp.ShrimpDataFileInterface;
import org.cirdles.squid.shrimp.ShrimpDataLegacyFileInterface;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;

import static org.cirdles.squid.utilities.fileUtilities.TextFileUtilities.writeTextFileFromListOfStringsWithUnixLineEnd;

/**
 * Handles common operations involving Prawn files.
 */
public class PrawnXMLFileHandler implements Serializable {

    private static final long serialVersionUID = -581339876224458493L;
    private static final PrawnFileRunFractionParser PRAWN_FILE_RUN_FRACTION_PARSER
            = new PrawnFileRunFractionParser();
    private transient Unmarshaller jaxbUnmarshaller;
    private transient Marshaller jaxbMarshaller;
    private final SquidProject squidProject;
    private CalamariReportsEngine reportsEngine;
    private String currentPrawnSourceFileLocation;

    /**
     * Creates a new {@link PrawnFileHandler} using a new reports engine.
     *
     * @param squidProject
     */
    public PrawnXMLFileHandler(SquidProject squidProject) {
        this(squidProject, new CalamariReportsEngine(squidProject));
    }

    /**
     * Creates a new {@link PrawnFileHandler}.
     *
     * @param squidProject
     * @param reportsEngine the reports engine to use
     */
    public PrawnXMLFileHandler(SquidProject squidProject, CalamariReportsEngine reportsEngine) {
        this.squidProject = squidProject;
        this.reportsEngine = reportsEngine;
    }

    /**
     * Unmarshalls currentPrawn file xml to object of class PrawnFile.
     *
     * @return object of class PrawnFile
     * @throws IOException
     * @throws MalformedURLException
     * @throws JAXBException
     * @throws SAXException
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    public ShrimpDataFileInterface unmarshallCurrentPrawnFileXML()
            throws IOException, MalformedURLException, JAXBException, SAXException, SquidException {
        return unmarshallPrawnFileXML(currentPrawnSourceFileLocation, false);
    }

    /**
     * Unmarshalls prawn file xml to object of class PrawnFile.
     *
     * @param prawnFileLocation String path to prawn file location
     * @param isTestMode        the value of isTestMode
     * @return the org.cirdles.squid.prawn.PrawnFile
     * @throws IOException
     * @throws MalformedURLException
     * @throws JAXBException
     * @throws SAXException
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    public ShrimpDataFileInterface unmarshallPrawnFileXML(String prawnFileLocation, boolean isTestMode)
            throws IOException, MalformedURLException, JAXBException, SAXException, SquidException {

        // modified Jan 2021 to detect and translate Legacy Prawn files
        String localPrawnXMLFile = prawnFileLocation;
        ShrimpDataFileInterface myPrawnFile = null;

        // test for URL such as "https://raw.githubusercontent.com/bowring/XSD/master/SHRIMP/EXAMPLE_100142_G6147_10111109.43_10.33.37%20AM.xml"
        boolean isURL = false;
        if (prawnFileLocation.toLowerCase(Locale.ENGLISH).startsWith("http")) {
            java.net.URL prawnDataURL;
            prawnDataURL = new URL(prawnFileLocation);
            localPrawnXMLFile = "tempURLtoXML.xml";
            isURL = true;

            ReadableByteChannel rbc = Channels.newChannel(prawnDataURL.openStream());
            FileOutputStream fOutStream = new FileOutputStream(localPrawnXMLFile);
            fOutStream.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fOutStream.close();
            rbc.close();
        }

        // localPrawnXMLFile is now a local file
        // swap out bad header
        Path pathToLocalPrawnXMLFile = FileSystems.getDefault().getPath(localPrawnXMLFile);

        boolean isPrawnLegacyFile = false;
        // read localPrawnXMLFile and determine location of required tag
        List<String> lines = Files.readAllLines(pathToLocalPrawnXMLFile, Charset.defaultCharset());
        int indexOfSoftwareTagLine = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains("<software")) {
                indexOfSoftwareTagLine = i;
                isPrawnLegacyFile = lines.get(i).contains("<software_version>SHRIMP II v2 SW");
                isPrawnLegacyFile = isPrawnLegacyFile && !lines.get(i).contains("SHR 2");
                break;
            }
        }

        // delete tempURLtoXML.xml 
        if (isURL) {
            if (!pathToLocalPrawnXMLFile.toFile().delete()) {
                throw new IOException("Failed to delete temp file '" + pathToLocalPrawnXMLFile + "'");
            }
        }

        // remove header up to <software tag
        for (int i = 0; i < indexOfSoftwareTagLine; i++) {
            lines.remove(0);
        }

        JAXBContext jaxbContext = JAXBContext.newInstance(isPrawnLegacyFile ? PrawnLegacyFile.class : PrawnFile.class);
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        if (!isTestMode) {
            // force validation against schema
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            // JULY 2017 Team decided to make schema and validation local because of security concerns at Geoscience Australia
            File schemaFile = new File(isPrawnLegacyFile
                    ? Squid3Constants.URL_STRING_FOR_PRAWN_LEGACY_XML_SCHEMA_LOCAL
                    : Squid3Constants.URL_STRING_FOR_PRAWN_XML_SCHEMA_LOCAL);
            Schema schema = sf.newSchema(schemaFile);
            jaxbUnmarshaller.setSchema(schema);
        }

        // July 2017 Team decided to make schema local because of issues of security at Geoscience Australia
        String[] headerArray = isPrawnLegacyFile
                ? Squid3Constants.XML_HEADER_FOR_PRAWN_LEGACY_FILES_USING_LOCAL_SCHEMA.split("\\n")
                : Squid3Constants.XML_HEADER_FOR_PRAWN_FILES_USING_LOCAL_SCHEMA.split("\\n");

        // add correct header
        for (int i = 0; i < headerArray.length; i++) {
            lines.add(i, headerArray[i]);
        }

        File prawnDataFile = writeTextFileFromListOfStringsWithUnixLineEnd(lines, "tempPrawnXMLFileName", ".xml");

        if (isPrawnLegacyFile) {
            ShrimpDataLegacyFileInterface myPrawnLegacyFile = readRawDataLegacyFile(prawnDataFile);
            // now translate to Prawn File ***************************************

            myPrawnFile = PrawnLegacyFileHandler.convertPrawnLegacyFileToPrawnFile(myPrawnLegacyFile);
        } else {
            myPrawnFile = readRawDataFile(prawnDataFile);
        }

        if (!prawnDataFile.delete()) {
            throw new SquidException("Unable to delete temporary prawnfile.");
        }

        return myPrawnFile;
    }

    /**
     * Deserializes xml file to a PrawnFile object.
     *
     * @param prawnDataFile the value of prawnDataFile
     * @return the PrawnFile
     * @throws javax.xml.bind.JAXBException
     */
    private ShrimpDataFileInterface readRawDataFile(File prawnDataFile) throws JAXBException {
        ShrimpDataFileInterface myPrawnFile = (PrawnFile) jaxbUnmarshaller.unmarshal(prawnDataFile);
        return myPrawnFile;
    }

    /**
     * Deserializes xml file to a PrawnLegacyFile object.
     *
     * @param prawnLegacyDataFile the value of prawnLegacyDataFile
     * @return the PrawnLegacyFile
     * @throws javax.xml.bind.JAXBException
     */
    private ShrimpDataLegacyFileInterface readRawDataLegacyFile(File prawnLegacyDataFile) throws JAXBException {
        ShrimpDataLegacyFileInterface myPrawnLegacyFile = (PrawnLegacyFile) jaxbUnmarshaller.unmarshal(prawnLegacyDataFile);
        return myPrawnLegacyFile;
    }

    /**
     * Serializes a PrawnFile object to xml and intended for saving edits to
     * original data.
     *
     * @param prawnFile for serialization
     * @param fileName
     * @throws java.io.IOException
     * @throws PropertyException
     * @throws JAXBException
     */
    public void writeRawDataFileAsXML(ShrimpDataFileInterface prawnFile, String fileName)
            throws IOException, PropertyException, JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(PrawnFile.class);
        jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        File outputPrawnFile = new File(fileName);
        jaxbMarshaller.marshal(prawnFile, outputPrawnFile);

        // jan 2021 per issue #574, remove leading white space and add a space after last attribute value plus add 2 comment lines
        // to make files ingestible by Squid25
        Path originalPrawnOutput = outputPrawnFile.toPath();
        List<String> lines = Files.readAllLines(originalPrawnOutput, Charset.defaultCharset());
        for (int i = 0; i < lines.size(); i++) {
            lines.set(i, lines.get(i).replaceAll("\"/>", "\" />").trim());
        }
        lines.add(1, "<!-- SHRIMP SW PRAWN Data File -->");
        lines.add(2, "<!-- SQUID3-generated PRAWN Data File copy -->");

        File updatedPrawnFile = writeTextFileFromListOfStringsWithUnixLineEnd(lines, "updatedPrawnFile", ".xml");
        Path updatedPrawnOutput = updatedPrawnFile.toPath();
        Files.copy(updatedPrawnOutput, originalPrawnOutput, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * @return
     */
    public boolean currentPrawnFileLocationIsFile() {
        return new File(currentPrawnSourceFileLocation).isFile();
    }

    /**
     *
     */
    public void initReportsEngineWithCurrentPrawnFileName() {
        // strip .xml from file name
        if (currentPrawnSourceFileLocation != null) {
            initReportsEngineWithCurrentPrawnFileName(currentPrawnSourceFileLocation);
        }
    }

    /**
     * @param prawnFileLocation
     */
    public void initReportsEngineWithCurrentPrawnFileName(String prawnFileLocation) {
        //removes directories from path
        prawnFileLocation = prawnFileLocation.replaceAll(".*[\\\\/]", "");

        //to be extra sure
        prawnFileLocation = new File(prawnFileLocation).getName();

        //get rid of whitespace and replace with underscores
        prawnFileLocation = prawnFileLocation.replaceAll("\\s", "_");

        // strip .xml from file name
        prawnFileLocation = prawnFileLocation.substring(0, prawnFileLocation.lastIndexOf('.'));

        //finally set name of prawn source file
        reportsEngine.setNameOfPrawnSourceFile(prawnFileLocation);
    }

    /**
     * @return the currentPrawnSourceFileLocation
     */
    public String getCurrentPrawnSourceFileLocation() {
        return currentPrawnSourceFileLocation;
    }

    /**
     * @param aCurrentPrawnFileLocation the currentPrawnSourceFileLocation to
     *                                  set
     */
    public void setCurrentPrawnSourceFileLocation(String aCurrentPrawnFileLocation) {
        currentPrawnSourceFileLocation = aCurrentPrawnFileLocation;
    }

    public String getCurrentPrawnFileLocationFolder() {
        String retVal = "";
        File prawnFileLocation = new File(currentPrawnSourceFileLocation);
        if (prawnFileLocation.exists()) {
            retVal = prawnFileLocation.getParent();
        }

        return retVal;
    }

    /**
     * @return
     */
    public File currentPrawnFileLocationFolder() {
        // TODO: make more elegant for OP file handling
        if (currentPrawnSourceFileLocation == null) {
            currentPrawnSourceFileLocation = ".";
        }
        File retVal = new File(currentPrawnSourceFileLocation);
        if (currentPrawnFileLocationIsFile()) {
            retVal = retVal.getParentFile();
        }

        return retVal;
    }

    /**
     * @return
     */
    public CalamariReportsEngine getReportsEngine() {
        if (reportsEngine == null) {
            reportsEngine = new CalamariReportsEngine(squidProject);
        }
        initReportsEngineWithCurrentPrawnFileName();
        return reportsEngine;
    }

    public CalamariReportsEngine getNewReportsEngine() {
        reportsEngine = new CalamariReportsEngine(squidProject);
        initReportsEngineWithCurrentPrawnFileName();
        return reportsEngine;
    }
}