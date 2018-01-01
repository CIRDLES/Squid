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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import static java.nio.file.attribute.PosixFilePermission.GROUP_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.cirdles.squid.constants.Squid3Constants;
import static org.cirdles.squid.constants.Squid3Constants.URL_STRING_FOR_PRAWN_XML_SCHEMA_LOCAL;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.prawn.PrawnFileRunFractionParser;
import org.xml.sax.SAXException;

/**
 * Handles common operations involving Prawn files.
 */
public class PrawnFileHandler implements Serializable {

    private static final long serialVersionUID = -581339876224458493L;

    private transient Unmarshaller jaxbUnmarshaller;
    private transient Marshaller jaxbMarshaller;

    private CalamariReportsEngine reportsEngine;

    private String currentPrawnFileLocation;

    private static final PrawnFileRunFractionParser PRAWN_FILE_RUN_FRACTION_PARSER
            = new PrawnFileRunFractionParser();

    /**
     * Creates a new {@link PrawnFileHandler} using a new reports engine.
     */
    public PrawnFileHandler() {
        this(new CalamariReportsEngine());
    }

    /**
     * Creates a new {@link PrawnFileHandler}.
     *
     * @param reportsEngine the reports engine to use
     */
    public PrawnFileHandler(CalamariReportsEngine reportsEngine) {
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
     */
    public PrawnFile unmarshallCurrentPrawnFileXML()
            throws IOException, MalformedURLException, JAXBException, SAXException, SquidException {
        return unmarshallPrawnFileXML(currentPrawnFileLocation);
    }

    /**
     * Unmarshalls prawn file xml to object of class PrawnFile.
     *
     * @param prawnFileLocation String path to prawn file location
     * @return object of class PrawnFile
     * @throws IOException
     * @throws MalformedURLException
     * @throws JAXBException
     * @throws SAXException
     */
    public PrawnFile unmarshallPrawnFileXML(String prawnFileLocation)
            throws IOException, MalformedURLException, JAXBException, SAXException, SquidException{

        String localPrawnXMLFile = prawnFileLocation;
        PrawnFile myPrawnFile;

        JAXBContext jaxbContext = JAXBContext.newInstance(PrawnFile.class);
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        // force validation against schema
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        // JULY 2017 Team decided to make schema and validation local because of security concerns at Geoscience Australia
        File schemaFile = new File(URL_STRING_FOR_PRAWN_XML_SCHEMA_LOCAL);
        // during testing
        if (!schemaFile.isFile()) {
            schemaFile = new File(schemaFile.getAbsolutePath().replace("Core", "App"));
        }
        Schema schema = sf.newSchema(schemaFile);
        jaxbUnmarshaller.setSchema(schema);

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

        // read localPrawnXMLFile and determine location of required tag
        List<String> lines = Files.readAllLines(pathToLocalPrawnXMLFile, Charset.defaultCharset());
        int indexOfSoftwareTagLine = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains("<software")) {
                indexOfSoftwareTagLine = i;
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

        // July 2017 Team decided to make schema local because of issues of security at Geoscience Australia
        String[] headerArray = Squid3Constants.XML_HEADER_FOR_PRAWN_FILES_USING_LOCAL_SCHEMA.split("\\n");

        // add correct header
        for (int i = 0; i < headerArray.length; i++) {
            lines.add(i, headerArray[i]);
        }

        String tempPrawnXMLFileName = "tempPrawnXMLFileName.xml";
        File prawnDataFile;

        // detect Operating System ... we need POSIX code for use on Ubuntu Server
        String OS = System.getProperty("os.name").toLowerCase(Locale.US);
        if (OS.toLowerCase(Locale.US).contains("win")) {
            Path pathTempXML = Paths.get(tempPrawnXMLFileName).toAbsolutePath();
            try (BufferedWriter writer = Files.newBufferedWriter(pathTempXML, StandardCharsets.UTF_8)) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            prawnDataFile = new File(tempPrawnXMLFileName);
        } else {
            // Posix attributes added to support web service on Linux
            Set<PosixFilePermission> perms = EnumSet.of(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE, GROUP_READ);
            Path config = Files.createTempFile("tempPrawnXMLFileName", "xml", PosixFilePermissions.asFileAttribute(perms));
            try (BufferedWriter writer = Files.newBufferedWriter(config, StandardCharsets.UTF_8)) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            prawnDataFile = config.toFile();
        }

        myPrawnFile = readRawDataFile(prawnDataFile);

        if (!prawnDataFile.delete()){
            throw new SquidException("Unable to delete temporaty prawnfile.");
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
    private PrawnFile readRawDataFile(File prawnDataFile) throws JAXBException {
        PrawnFile myPrawnFile = (PrawnFile) jaxbUnmarshaller.unmarshal(prawnDataFile);
        return myPrawnFile;
    }

    /**
     * Serializes a PrawnFile object to xml and intended for saving edits to
     * original data.
     *
     * @param prawnFile for serialization
     * @param fileName
     * @throws PropertyException
     * @throws JAXBException
     */
    public void writeRawDataFileAsXML(PrawnFile prawnFile, String fileName)
            throws PropertyException, JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(PrawnFile.class);
        jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(prawnFile, new File(fileName));
    }

    /**
     *
     * @return
     */
    public boolean currentPrawnFileLocationIsFile() {
        return new File(currentPrawnFileLocation).isFile();
    }

    /**
     *
     */
    public void initReportsEngineWithCurrentPrawnFileName() {
        // strip .xml from file name
        if (currentPrawnFileLocation != null) {
            reportsEngine.setNameOfPrawnXMLFile(new File(currentPrawnFileLocation).getName().split("\\.")[0]);
        }
    }

    /**
     *
     * @param prawnFileLocation
     */
    public void initReportsEngineWithCurrentPrawnFileName(String prawnFileLocation) {
        // strip .xml from file name
        reportsEngine.setNameOfPrawnXMLFile(new File(prawnFileLocation).getName().split("\\.")[0]);
    }

    /**
     * @return the currentPrawnFileLocation
     */
    public String getCurrentPrawnFileLocation() {
        return currentPrawnFileLocation;
    }

    public String getCurrentPrawnFileLocationFolder() {
        String retVal = "";
        File prawnFileLocation = new File(currentPrawnFileLocation);
        if (prawnFileLocation.exists()) {
            retVal = prawnFileLocation.getParent();
        }

        return retVal;
    }

    /**
     *
     * @return
     */
    public File currentPrawnFileLocationFolder() {
        File retVal = new File(currentPrawnFileLocation);
        if (currentPrawnFileLocationIsFile()) {
            retVal = retVal.getParentFile();
        }

        return retVal;
    }

    /**
     * @param aCurrentPrawnFileLocation the currentPrawnFileLocation to set
     */
    public void setCurrentPrawnFileLocation(String aCurrentPrawnFileLocation) {
        currentPrawnFileLocation = aCurrentPrawnFileLocation;
    }

    /**
     *
     * @return
     */
    public CalamariReportsEngine getReportsEngine() {
        if (reportsEngine == null) {
            reportsEngine = new CalamariReportsEngine();
        }
        initReportsEngineWithCurrentPrawnFileName();
        return reportsEngine;
    }

    public CalamariReportsEngine getNewReportsEngine() {
        reportsEngine = new CalamariReportsEngine();
        initReportsEngineWithCurrentPrawnFileName();
        return reportsEngine;
    }

}
