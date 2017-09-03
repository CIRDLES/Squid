/* 
 * Copyright 2006-2017 CIRDLES.org.
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
package org.cirdles.squid.tasks.expressions.builtinExpressions;

import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeBuilderInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeWithRatiosInterface;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.operations.Operation;

/**
 *
 * @author James F. Bowring
 */
public class SquidExpressionMinus3 {

    /**
     * Squid Excel format is (0.03446*["254/238"]+0.868)*["248/254"] EqNum -3
     */
    public final static ExpressionTreeInterface EXPRESSION = new ExpressionTree("232/238");

    static {
        ((ExpressionTreeWithRatiosInterface) EXPRESSION).getRatiosOfInterest().add("254/238");
        ((ExpressionTreeWithRatiosInterface) EXPRESSION).getRatiosOfInterest().add("248/254");

        ExpressionTreeInterface term1 = new ExpressionTree(
                "0.03446 * 254/238",
                new ConstantNode("0.03446", 0.03446),
                ExpressionTree.squidProject.buildRatioExpression("254/238"),
                Operation.multiply());
        ExpressionTreeInterface term2 = new ExpressionTree(
                "0.03446 * 254/238 + 0.868",
                term1,
                new ConstantNode("0.868", 0.868),
                Operation.add());

        ((ExpressionTreeBuilderInterface) EXPRESSION).addChild(0, term2);
        ((ExpressionTreeBuilderInterface) EXPRESSION).addChild(ExpressionTree.squidProject.buildRatioExpression("248/254"));
        ((ExpressionTreeBuilderInterface) EXPRESSION).setOperation(Operation.multiply());

        ((ExpressionTree) EXPRESSION).setRootExpressionTree(true);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSCSummaryCalculation(false);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSTReferenceMaterialCalculation(true);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSAUnknownCalculation(true);
    }
}
