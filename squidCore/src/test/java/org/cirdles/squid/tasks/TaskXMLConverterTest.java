/*
 * Copyright 2018 James F. Bowring and CIRDLES.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.squid.tasks;

import org.cirdles.squid.ElementComparer;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author ryanb
 */
public class TaskXMLConverterTest {

    @Test
    public void TaskXMLConverterTest() {
        try {
            String folderPath = "src/test/resources/org/cirdles/squid/TaskXMLFiles/";

            //create task from sample document
            Task originalTask = new Task();
            originalTask = (Task) originalTask.readXMLObject(folderPath + "SampleTask.XML", false);

            //write out original task to file and create another task using that file then write out that new task to another file
            File convertedOriginal = new File(folderPath + "ConvertedOriginalTask.XML");
            originalTask.serializeXMLObject(convertedOriginal.getAbsolutePath());
            Task convertedTask = (Task) originalTask.readXMLObject(convertedOriginal.getAbsolutePath(), false);
            File convertedConverted = new File(folderPath + "ConvertedConvertedTask.XML");
            convertedTask.serializeXMLObject(convertedConverted.getAbsolutePath());

            //compare files  using jdom
            SAXBuilder builder = new SAXBuilder();
            Document originalFile = builder.build(new File(folderPath + "ConvertedOriginalTask.XML"));
            Document convertedFile = builder.build(new File(folderPath + "ConvertedConvertedTask.XML"));
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