package org.cirdles.squid.utilities.fileUtilities;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.prawn.PrawnFile;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileValidator {


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
                List<String> completeScemas = new ArrayList<>();
                String lastURL = null;
                for (String current : extractors) {
                    boolean isURL = current.startsWith("http");
                    boolean isSchema = current.toLowerCase().endsWith(".xsd");
                    if (isURL && isSchema) {
                        completeScemas.add(current);
                    } else if (isURL) {
                        lastURL = current;
                    } else if (isSchema && lastURL != null) {
                        completeScemas.add(lastURL + "/" + current);
                    }
                }

                SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                completeScemas.forEach(schemaLocation -> {
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

    public static boolean validateFileIsXMLSerializedEntity(File serializedFile, Schema schema) {
        boolean retVal = false;
        Source source = new StreamSource(serializedFile);
        Validator validator = schema.newValidator();
        try {
            validator.validate(source);
            retVal = true;
        } catch(SAXException | IOException e) {
        }
        return retVal;
    }

    public static void main(String[] args) {
        ResourceExtractor extractor = new ResourceExtractor(PrawnFile.class);
        File file = extractor.extractResourceAsFile("836_1_2016_Nov_28_09.50.xml");
        System.out.println(validateFileIsXMLSerializedEntity(file, PrawnFile.class));
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