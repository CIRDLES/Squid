/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.tasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.Squid;
import org.cirdles.squid.utilities.fileUtilities.FileValidator;
import static org.cirdles.squid.constants.Squid3Constants.XML_HEADER_FIX_FOR_SQUIDTASK_FILES_IN_LIBRARY_USING_LOCAL_SCHEMA;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import org.xml.sax.SAXException;

/**
 *
 * @author Noah
 */
public class TaskXMLHeaderFixTest {
    private ResourceExtractor taskXMLResourceExtractor = new ResourceExtractor(TaskXMLValidationTest.class);
    private ResourceExtractor taskSchemaResourceExtractor = new ResourceExtractor(Squid.class);
    private SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    
    @Test
    public void fixHeaderTest(){
        FileReader reader;
        FileWriter writer;
        BufferedReader buffIn = null;
        BufferedWriter buffOut = null;
        File tempTaskXML;
        File headlessTaskXML;
        
        try {
            Schema taskXMLSchema = sf.newSchema(taskSchemaResourceExtractor.extractResourceAsFile("schema/SquidTask_XMLSchema.xsd"));
            tempTaskXML = File.createTempFile("tempTaskXML", ".tmp");
            tempTaskXML.deleteOnExit();
            headlessTaskXML = taskXMLResourceExtractor.extractResourceAsFile("Z6266_=_11pk_Perm1_Headless.xml");
            
            reader = new FileReader(headlessTaskXML);
            writer = new FileWriter(tempTaskXML);
            buffIn = new BufferedReader(reader);
            buffOut = new BufferedWriter(writer);
            String line = buffIn.readLine();
            while(line != null){
                line = line.replaceFirst("<Task>",
                    XML_HEADER_FIX_FOR_SQUIDTASK_FILES_IN_LIBRARY_USING_LOCAL_SCHEMA);
                buffOut.write(line);
                buffOut.newLine();
                
                line = buffIn.readLine();
            }
            buffIn.close();
            buffOut.close();
            
            assertTrue(FileValidator.validateFileIsXMLSerializedEntity(tempTaskXML, taskXMLSchema));
            //assertTrue(FileValidator.validateFileIsXMLSerializedEntity(
                    //tempTaskXML, taskXMLSchema));
            
        } catch(IOException | SAXException e ) {
        } finally {
            try {
                if (buffIn != null) {
                    buffIn.close();
                }
                if (buffOut != null) {
                    buffOut.close();
                }
            } catch(IOException e){
            }
        }
    }
}
