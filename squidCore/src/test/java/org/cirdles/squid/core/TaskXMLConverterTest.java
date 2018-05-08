/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.core;

import java.io.File;
import org.cirdles.squid.tasks.Task;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author ryanb
 */
public class TaskXMLConverterTest {

    @Test
    public void TaskXMLConverterTest() {
        try {
            String folderPath = "src/test/java/org/cirdles/squid/core/TaskXMLFiles/";
            Task originalTask = new Task();
            originalTask = (Task) originalTask.readXMLObject(folderPath + "SampleTask.XML", false);
            File convertedOriginal = new File(folderPath + "ConvertedOriginalTask.XML");
            originalTask.serializeXMLObject(convertedOriginal.getAbsolutePath());
            Task convertedTask = (Task) originalTask.readXMLObject(convertedOriginal.getAbsolutePath(), false);          
            File convertedConverted = new File(folderPath + "ConvertedConvertedTask.XML");
            convertedTask.serializeXMLObject(convertedConverted.getAbsolutePath());

            SAXBuilder builder = new SAXBuilder();
            Document originalFile = (Document) builder.build(new File(folderPath + "ConvertedOriginalTask.XML"));
            Document convertedFile = (Document) builder.build(new File(folderPath + "ConvertedConvertedTask.XML"));
            Element originalElement = originalFile.getRootElement();
            Element convertedElement = convertedFile.getRootElement();
            convertedOriginal.delete();
            convertedConverted.delete();
            assertTrue(ElementComparer.compareElements(originalElement, convertedElement));
        } catch (Exception e) {
            e.printStackTrace();
            fail("something went wrong: " + e.getMessage());
        }
    }
}
