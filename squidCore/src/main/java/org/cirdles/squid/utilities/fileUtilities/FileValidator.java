package org.cirdles.squid.utilities.fileUtilities;


import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;
import static org.cirdles.squid.constants.Squid3Constants.URL_STRING_FOR_SQUIDTASK_EXPRESSION_XML_SCHEMA_LOCAL;
import static org.cirdles.squid.constants.Squid3Constants.URL_STRING_FOR_SQUIDTASK_XML_SCHEMA_LOCAL;
import static org.cirdles.squid.constants.Squid3Constants.XML_HEADER_FOR_SQUIDTASK_FILES_USING_LOCAL_SCHEMA;
import static org.cirdles.squid.utilities.fileUtilities.TextFileUtilities.writeTextFileFromListOfStringsWithUnixLineEnd;
import org.xml.sax.SAXException;

public class FileValidator {

    /* not possible (unless you use a if else ladder, switch, etc to match class to schema)
    public static boolean validateFileIsXMLSerializedEntity(File serializedFile, Class entityClass) {
        AtomicBoolean retVal = new AtomicBoolean(false);
        try {
            Scanner scanner = new Scanner(new FileInputStream(serializedFile));
            String extractor = null;
            while (scanner.hasNextLine() && extractor == null) {
                extractor = scanner.findWithinHorizon("xsi:schemaLocation=\".+\"", 300);
            }

            if (extractor != null) {
                extractor = extractor.replaceAll(".*xsi:schemaLocation=\"", "");
                extractor = extractor.substring(0, extractor.indexOf('"'));
                String[] extractors = extractor.split("(\\s|\\n)");
                for (String cur : extractors) {
                    System.out.println(cur);
                }
                List<String> completeSchemas = new ArrayList<>();
                String lastURL = null;
                for (String current : extractors) {
                    boolean isURL = current.startsWith("http");
                    boolean isSchema = current.toLowerCase().endsWith(".xsd");
                    if (isURL && isSchema) {
                        completeSchemas.add(current);
                    } else if (isURL) {
                        lastURL = current;
                    } else if (isSchema && lastURL != null) {
                        completeSchemas.add(lastURL + "/" + current);
                    }
                }

                SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                completeSchemas.forEach(schemaLocation -> {
                    System.out.println(schemaLocation);
                    URL url = null;
                    try {
                        new URL(schemaLocation);
                    } catch (MalformedURLException e) {
                    }
                    if (url != null) {
                        boolean passed;
                        System.out.println(url.toString());
                        Schema schema = null;
                        try {
                            sf.newSchema(url);
                        } catch (SAXException e) {
                        }
                        if (schema != null) {
                            String rootElementType = schema.newValidatorHandler().getTypeInfoProvider().getElementTypeInfo().getTypeName();
                            rootElementType = rootElementType.replaceAll("_", "");
                            passed = rootElementType.toLowerCase().equals(rootElementType.toLowerCase());
                            if (passed) {
                                Validator validator = schema.newValidator();
                                Source source = new StreamSource(serializedFile);
                                try {
                                    validator.validate(source);
                                    passed = true;
                                } catch (IOException | SAXException e) {
                                    passed = false;
                                }
                            }
                            if (passed) {
                                retVal.set(passed);
                            }
                        }
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retVal.get();
    }
    */

    public static boolean validateXML(File serializedFile, Schema schema, String header) {
        boolean validates = false;
        String[] headerArray = header.split("\\n");
        File tempSerializedFile = null; // Temp file with corresponding header for XML validation
        
        // Exception thrown if file is xml but validation fails
        try {
            List<String> lines = Files.readAllLines(serializedFile.toPath(), Charset.defaultCharset());
            // Change header
            lines.set(2, headerArray[2]);
            lines.set(3, headerArray[3]);
            lines.set(4, headerArray[4]);
            lines.set(5, headerArray[5]);
            lines.set(6, headerArray[6]);
            
            tempSerializedFile = writeTextFileFromListOfStringsWithUnixLineEnd(lines, "squidTempXML", ".xml");
            Validator validator = schema.newValidator();
            Source source = new StreamSource(tempSerializedFile);
            validator.validate(source);
            validates = true; // True if no exception is thrown
            
        } catch (IOException | ArrayIndexOutOfBoundsException |
                NullPointerException | SAXException e) {
            // No info as to why XML didn't validate... need to fix.
            e.getMessage();
        }
        return validates;
    }
    
    public static boolean validateXML(File serializedFile, Schema schema) {
        boolean validates = false;
        File tempSerializedFile = null; // Temp file with corresponding header for XML validation
        
        // Exception thrown if file is xml but validation fails
        try {
            List<String> lines = Files.readAllLines(serializedFile.toPath(), Charset.defaultCharset());
            writeTextFileFromListOfStringsWithUnixLineEnd(lines, "squidTempXML", ".xml");
            Validator validator = schema.newValidator();
            Source source = new StreamSource(tempSerializedFile);
            validator.validate(source);
            validates = true; // True if no exception is thrown
            
        } catch (IOException | ArrayIndexOutOfBoundsException |
                NullPointerException | SAXException e) {
            // No info as to why XML didn't validate... need to fix.
            e.getMessage();
        }
        return validates;
        
    }
}
        /*  XStream stream = new XStream();
            stream.allowTypeHierarchy(entityClass);
            stream.useAttributeFor(entityClass);
            stream.getReflectionProvider().newInstance(entityClass);
            Object ob = stream.fromXML(serializedFile);
            retVal = entityClass.isInstance(ob);*/

           /* SAXBuilder builder = new SAXBuilder();
            Document doc = (Document) builder.build(serializedFile);
            Element root = doc.getRootElement();
            Content content = root.getContent(0);
            System.out.println(content.getClass()); */

            /*XStream xstream = new XStream();
            xstream.setClassLoader(entityClass.getClassLoader());
            Object conversion = xstream.fromXML(serializedFile.getAbsoluteFile());
            retVal = entityClass.isInstance(conversion);*/