/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.squidReports.squidReportTables;

import java.io.File;
import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.Squid;
import static org.cirdles.squid.constants.Squid3Constants.XML_HEADER_FOR_SQUIDREPORTTABLE_FILES_USING_LOCAL_SCHEMA;
import static org.cirdles.squid.utilities.fileUtilities.FileValidator.validateXML;
import org.junit.Test;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertTrue;

/**
 * @author Noah
 */
public class SquidReportTableXSDValidationTest {
    @Test
    public void defaultRefMatTableTest(){
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        ResourceExtractor squidReportTableSchemaExtractor = new ResourceExtractor(Squid.class);
        ResourceExtractor testSquidReportTableExtractor = new ResourceExtractor(SquidReportTableXSDValidationTest.class);
        try {
            final File squidReportTableSchema = squidReportTableSchemaExtractor.extractResourceAsFile("schema/SquidReportTable.xsd");
            final Schema schema = sf.newSchema(squidReportTableSchema);
            final File defaultRefMatXML = testSquidReportTableExtractor.extractResourceAsFile("Example.xml");
            final File defaultRefMatXMLHeadless = testSquidReportTableExtractor.extractResourceAsFile("Example_Headless.xml");
            validateXML(defaultRefMatXMLHeadless, schema, XML_HEADER_FOR_SQUIDREPORTTABLE_FILES_USING_LOCAL_SCHEMA);
            validateXML(defaultRefMatXML, schema);
            assertTrue(true);
        } catch (SAXException | IOException e) {
            assertTrue(false);
        }
    }
}
