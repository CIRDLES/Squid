/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.core;

import java.io.File;
import java.util.List;
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
            originalTask.serializeXMLObject(folderPath + "ConvertedOriginalTask.XML");
            Task convertedTask = (Task) originalTask.readXMLObject(folderPath + "ConvertedOriginalTask.XML", false);
            convertedTask.serializeXMLObject(folderPath + "ConvertedConvertedTask.XML");

            SAXBuilder builder = new SAXBuilder();
            Document originalFile = (Document) builder.build(new File(folderPath + "ConvertedOriginalTask.XML"));
            Document convertedFile = (Document) builder.build(new File(folderPath + "ConvertedConvertedTask.XML"));
            Element originalElement = originalFile.getRootElement();
            Element convertedElement = convertedFile.getRootElement();
            assertTrue(ElementComparer.compareElements(originalElement, convertedElement));
        } catch (Exception e) {
            e.printStackTrace();
            fail("something went wrong" + e.getMessage());
        }
    }
}
