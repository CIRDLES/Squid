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
package org.cirdles.squid.tasks.expressions;

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.cirdles.squid.ElementComparer;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
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
            ExpressionTree LnUOU = new ExpressionTree("LnUO/U");
            List<String> ratiosOfInterest = new ArrayList<>();
            ratiosOfInterest.add("254/238");
            LnUOU.setRatiosOfInterest(ratiosOfInterest);
            LnUOU.setOperation(Function.ln());
            LnUOU.setRootExpressionTree(true);
            LnUOU.setSquidSwitchSCSummaryCalculation(false);
            LnUOU.setSquidSwitchSTReferenceMaterialCalculation(true);
            LnUOU.setSquidSwitchSAUnknownCalculation(true);

            Expression initialExpression = new Expression(LnUOU, "ln([\"254/238\"])", true);

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

            initialFile.delete();
            convertedFile.delete();
            assertTrue(ElementComparer.compareElements(initialRootElement, convertedRootElement));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
    }
}
