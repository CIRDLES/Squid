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

import org.cirdles.squid.shrimp.RawRatioNamesSHRIMP;
import org.cirdles.squid.tasks.expressions.ExpressionTree;
import org.cirdles.squid.tasks.expressions.ExpressionTreeBuilderInterface;
import org.cirdles.squid.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.ExpressionTreeWithRatiosInterface;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.operations.Operation;

/**
 *
 * @author James F. Bowring
 */
public class SquidExpressionMinus4 {

    /**
     * Squid Excel format is ["238/196"]/["254/238"]^0.66 has EqNum = -4
     */
    public final static ExpressionTreeInterface EXPRESSION = new ExpressionTree("U Conc Const");

    static {
        ((ExpressionTreeWithRatiosInterface) EXPRESSION).getRatiosOfInterest().add(RawRatioNamesSHRIMP.r238_196w);
        ((ExpressionTreeWithRatiosInterface) EXPRESSION).getRatiosOfInterest().add(RawRatioNamesSHRIMP.r254_238w);

        ExpressionTreeInterface r254_238wPow = new ExpressionTree(
                "254/238^0.66",
                RawRatioNamesSHRIMP.r254_238w.getExpression(),
                new ConstantNode("0.66", 0.66),
                Operation.pow());

        ((ExpressionTreeBuilderInterface) EXPRESSION).addChild(0, RawRatioNamesSHRIMP.r238_196w.getExpression());
        ((ExpressionTreeBuilderInterface) EXPRESSION).addChild(r254_238wPow);
        ((ExpressionTreeBuilderInterface) EXPRESSION).setOperation(Operation.divide());

        ((ExpressionTree) EXPRESSION).setRootExpressionTree(true);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSCSummaryCalculation(false);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSTReferenceMaterialCalculation(true);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSAUnknownCalculation(true);
    }
}
