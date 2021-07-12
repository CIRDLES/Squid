package org.cirdles.squid.utilities.fileUtilities;

import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.cirdles.squid.utilities.fileUtilities.TextFileUtilities.writeTextFileFromListOfStringsWithUnixLineEnd;

public class FileValidator {

    /**
     * @param serializedFile
     * @param schema
     * @param squid3ConstantHeader
     * @return
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    public static void validateXML(File serializedFile, Schema schema, String squid3ConstantHeader) throws SAXException,
            IOException, ArrayIndexOutOfBoundsException {
        String[] headerArray = squid3ConstantHeader.split("\\n");
        File tempSerializedFile; // Temp file with corresponding header for XML validation
        // make mutable
        List<String> lines = new ArrayList<>();
        lines.addAll(Files.readAllLines(serializedFile.toPath(), Charset.defaultCharset()));

        // backwards compatible remove all nulls
        List<String> myLines = new ArrayList<>();
        for (String line : lines) {
            String myLine = line.replace(">null<", "><");
            // check for bogus extra line from legacy files
            if (myLine.compareToIgnoreCase("<!-- visit: github.com/CIRDLES/Squid -->") != 0) {
                myLines.add(myLine);
            }
        }

        if (myLines.get(0).trim().compareToIgnoreCase(myLines.get(myLines.size() - 1).trim().replace("/", "")) == 0) {
            // checking to see if no header
            // if (lines.get(0).startsWith("<SquidReportTable>")) {
            // Change header
            myLines.set(0, headerArray[0]);
            for (int i = 1; i < headerArray.length; i++) {
                myLines.add(i, headerArray[i]);
            }
        } else {
            if (!(myLines.get(2).equals(headerArray[2]))) { // Does file already have header?
                // Change header
                myLines.set(2, headerArray[2]);
                for (int i = 3; i < headerArray.length; i++) {
                    myLines.add(i, headerArray[i]);
                }
            }
        }

        tempSerializedFile = writeTextFileFromListOfStringsWithUnixLineEnd(myLines, "squidTempXML", ".xml");
        Validator validator = schema.newValidator();
        Source source = new StreamSource(tempSerializedFile);
        validator.validate(source);
    }

    /**
     * @param serializedFile
     * @param schema
     * @return
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    public static void validateXML(File serializedFile, Schema schema) throws SAXException, IOException {
        File tempSerializedFile; // Temp file with corresponding header for XML validation

        // Exception thrown if file is xml but validation fails
        List<String> lines = Files.readAllLines(serializedFile.toPath(), Charset.defaultCharset());
        tempSerializedFile = writeTextFileFromListOfStringsWithUnixLineEnd(lines, "squidTempXML", ".xml");
        Validator validator = schema.newValidator();
        Source source = new StreamSource(tempSerializedFile);
        validator.validate(source);
    }
}