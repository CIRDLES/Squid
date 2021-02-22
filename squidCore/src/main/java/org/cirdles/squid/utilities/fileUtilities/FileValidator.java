package org.cirdles.squid.utilities.fileUtilities;


import javax.xml.validation.Schema;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;
import static org.cirdles.squid.utilities.fileUtilities.TextFileUtilities.writeTextFileFromListOfStringsWithUnixLineEnd;
import org.xml.sax.SAXException;

public class FileValidator {

    /**
     *
     * @param serializedFile
     * @param schema
     * @param header
     * @return
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    public static boolean validateXML(File serializedFile, Schema schema, String squid3ConstantHeader) throws SAXException,
            IOException, ArrayIndexOutOfBoundsException {
        String[] headerArray = squid3ConstantHeader.split("\\n");
        File tempSerializedFile = null; // Temp file with corresponding header for XML validation
        List<String> lines = Files.readAllLines(serializedFile.toPath(), Charset.defaultCharset());
        if (!(lines.get(2).equals(headerArray[2]))) { // Does file already have header?
            // Change header
            for (int x = 2; x < 7; x++) {
                lines.set(x, headerArray[x]);
                lines.add(x, headerArray[x]);
                lines.add(x, headerArray[x]);
                lines.add(x, headerArray[x]);
                lines.add(x, headerArray[x]);
            }
        }     
        tempSerializedFile = writeTextFileFromListOfStringsWithUnixLineEnd(lines, "squidTempXML", ".xml");
        Validator validator = schema.newValidator();
        Source source = new StreamSource(tempSerializedFile);
        validator.validate(source);
        return true; // Return if no exception
    }
    
    /**
     *
     * @param serializedFile
     * @param schema
     * @return
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    public static boolean validateXML(File serializedFile, Schema schema) throws SAXException, IOException {
        boolean validates = false;
        File tempSerializedFile = null; // Temp file with corresponding header for XML validation
        
        // Exception thrown if file is xml but validation fails
        List<String> lines = Files.readAllLines(serializedFile.toPath(), Charset.defaultCharset());
        tempSerializedFile = writeTextFileFromListOfStringsWithUnixLineEnd(lines, "squidTempXML", ".xml");
        Validator validator = schema.newValidator();
        Source source = new StreamSource(tempSerializedFile);
        validator.validate(source);
        validates = true; // True if no exception is thrown
        return validates;
    }
}
