/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.core;

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.util.List;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.customExpressions.CustomExpression_LnUO_U;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.functions.Function;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.operations.Operation;
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

            SquidSpeciesModel sm1 = new SquidSpeciesModel(0, "254", "254", "Uranium", false, "");
            SquidSpeciesModel sm2 = new SquidSpeciesModel(0, "238", "238", "Uranium", false, "");

            ExpressionTreeInterface sn1 = ShrimpSpeciesNode.buildShrimpSpeciesNode(sm1, "getPkInterpScanArray");
            ExpressionTreeInterface sn2 = ShrimpSpeciesNode.buildShrimpSpeciesNode(sm2, "getPkInterpScanArray");

            ExpressionTreeInterface rm = new ExpressionTree("254/238", sn1, sn2, Operation.divide());

            ExpressionTree expTree = (ExpressionTree) initialExpression.getExpressionTree();

            expTree.getRatiosOfInterest().clear();
            expTree.getRatiosOfInterest().add("254/238");
            expTree.setOperation(Function.ln());
            expTree.getChildrenET().clear();
            expTree.addChild(0, rm);

            initialExpression.customizeXstream(xstream);
            File initialFile = new File("InitialCreation.XML");

            initialExpression.serializeXMLObject(initialFile.getAbsolutePath());

            Expression convertedExpression = (Expression) (new Expression()).readXMLObject(initialFile.getAbsolutePath(), false);
            File convertedFile = new File("ConvertedCreation.XML");
            convertedExpression.serializeXMLObject(convertedFile.getAbsolutePath());

            SAXBuilder builder = new SAXBuilder();
            Document initialDocument = (Document) builder.build(initialFile);
            Document convertedDocument = (Document) builder.build(convertedFile);
            Element initialRootElement = initialDocument.getRootElement();
            Element convertedRootElement = convertedDocument.getRootElement();

            assertTrue(compareElements(initialRootElement, convertedRootElement));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }

    private boolean compareElements(Element firstElement, Element secondElement) {
        boolean returnValue = true;
        List<Element> firstList = firstElement.getChildren();
        List<Element> secondList = secondElement.getChildren();

        if (firstList.size() == secondList.size()) {
            if (!firstList.isEmpty()) {
                while (!firstList.isEmpty()) {
                    Element firstChild = firstList.remove(0);
                    Element secondChild = secondList.remove(0);
                    if (compareElements(firstChild, secondChild) == false) {
                        returnValue = false;
                    }
                }
            } else {
                if (!(firstElement.getText().equals(secondElement.getText()))) {
                    returnValue = false;
                }
            }
        } else {
            returnValue = false;
        }

        return returnValue;
    }
}
