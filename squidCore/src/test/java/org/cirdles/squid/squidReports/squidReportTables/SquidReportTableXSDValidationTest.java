/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.squidReports.squidReportTables;

import java.io.File;
import java.util.Arrays;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.Squid;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.utilities.fileUtilities.FileValidator;
import org.junit.Test;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author Noah
 */
public class SquidReportTableXSDValidationTest {
   @Test
    public void defaultRefMatTableTest(){
        System.out.println("Testing for defaultRefMat...");
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        ResourceExtractor squidReportTableExtractor = new ResourceExtractor(SquidReportTableXSDValidationTest.class);
        boolean validates = false;
        try {
            final File squidReportTableSchema = squidReportTableExtractor.extractResourceAsFile("SquidReportTable.xsd");
            final Schema schema = sf.newSchema(squidReportTableSchema);
            System.out.println(squidReportTableSchema.getName());
            final File defaultRefMatXML = squidReportTableExtractor.extractResourceAsFile("DefaultRefMat.xml");
            System.out.println(defaultRefMatXML.getName());
            validates = FileValidator.validateFileIsXMLSerializedEntity(defaultRefMatXML, schema);
            System.out.println(Boolean.toString(validates));
        } catch (SAXException e) {
            System.out.println("Failed to validate against schema");
        }
        finally {
            assertTrue(validates);
        }
    }
}
