/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.tasks;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.Squid;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;

import static org.cirdles.squid.constants.Squid3Constants.XML_HEADER_FOR_SQUIDTASK_FILES_USING_LOCAL_SCHEMA;
import static org.cirdles.squid.utilities.fileUtilities.FileValidator.validateXML;
import static org.junit.Assert.assertTrue;


/**
 * @author Noah
 */
public class TaskXMLValidationTest {
    @Test
    public void validateXMLTest() {
        try {
            ResourceExtractor taskResourceExtractor = new ResourceExtractor(TaskXMLValidationTest.class);
            ResourceExtractor schemaResourceExtractor = new ResourceExtractor(Squid.class);
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema taskXMLSchema = sf.newSchema(
                    schemaResourceExtractor.extractResourceAsFile(
                            "schema/SquidTask_XMLSchema.xsd"));
            File taskFile = taskResourceExtractor.extractResourceAsFile(
                    "Z6266_=_11pk_Perm1.xml");
            File taskFileHeadless = taskResourceExtractor.extractResourceAsFile(
                    "Z6266_=_11pk_Perm1.xml");
            validateXML(taskFileHeadless, taskXMLSchema, XML_HEADER_FOR_SQUIDTASK_FILES_USING_LOCAL_SCHEMA);
            validateXML(taskFile, taskXMLSchema);
            assertTrue(true);
        } catch (SAXException | IOException e) {
            assertTrue(false);
        }

    }
}