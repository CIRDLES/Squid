/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.core;

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.customExpressions.CustomExpression_LnUO_U;
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
public class ExpressionXMLConverterTest {

    @Test
    public void ExpressionXMLConverterTest() {
        try {
            XStream xstream = new XStream();
            Expression initialExpression = new Expression(new CustomExpression_LnUO_U(), CustomExpression_LnUO_U.excelExpressionString, true);
            initialExpression.customizeXstream(xstream);
            File initialFile = new File("src/test/resources/org/cirdles/squid/XMLSerializationTest/InitialCreation.XML");
            FileOutputStream fosInitial = new FileOutputStream(initialFile);
            PrintWriter initialWriter = new PrintWriter(fosInitial);
            initialWriter.print(xstream.toXML(initialExpression));

            Expression convertedExpression = (Expression) xstream.fromXML(initialFile);
            File convertedFile = new File("src/test/resources/org/cirdles/squid/XMLSerializationTest/ConvertedCreation.XML");
            FileOutputStream fosConverted = new FileOutputStream(convertedFile);
            PrintWriter convertedWriter = new PrintWriter(fosConverted);
            convertedWriter.print(xstream.toXML(convertedExpression));

            SAXBuilder builder = new SAXBuilder();
            Document initialDocument = (Document) builder.build(initialFile);
            Document convertedDocument = (Document) builder.build(convertedFile);
            Element initialRootElement = initialDocument.getRootElement();
            Element convertedRootElement = convertedDocument.getRootElement();

            List<Element> initialElements = getAllLeafNodes(initialRootElement);
            List<Element> convertedElements = getAllLeafNodes(convertedRootElement);

            assertTrue(compareAll(initialElements, convertedElements));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }

    private List<Element> getAllLeafNodes(Element passedElement) {
        List<Element> returnElements = new ArrayList<>();
        List<Element> elementParents = new ArrayList<>();
        elementParents.add(passedElement);

        while (!elementParents.isEmpty()) {
            Element currentElement = elementParents.remove(0);
            List<Element> currentElements = currentElement.getChildren();
            if (currentElements.isEmpty()) {
                returnElements.add(currentElement);
            } else {
                elementParents.addAll(currentElements.subList(0, currentElements.size()));
            }
        }

        return returnElements;
    }

    private boolean compareAll(List<Element> firstList, List<Element> secondList) {
        boolean returnValue = true;

        int i = 0;
        while (returnValue && i < firstList.size()) {
            boolean foundEqualNode = false;
            int j = 0;
            while (!foundEqualNode && j < secondList.size()) {
                if (firstList.get(i).equals(secondList.get(j))) {
                    foundEqualNode = true;
                }
                j++;
            }
            if (!foundEqualNode) {
                returnValue = false;
            }
            i++;
        }

        return returnValue;
    }
}
