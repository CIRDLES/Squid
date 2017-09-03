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
package org.cirdles.squid.tasks.expressions.customExpressions;

import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeBuilderInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.functions.Function;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForPerSpotTaskExpressions;

/**
 *
 * @author James F. Bowring
 */
public class CustomExpression_Net204BiWt {

    /**
     * Squid Excel format is sqBiweight(["Net204cts/sec"],9)
     */
    public final static ExpressionTreeInterface EXPRESSION = new ExpressionTree("Net204BiWt");

    static {
        ((ExpressionTreeBuilderInterface) EXPRESSION)
                .addChild(0, new VariableNodeForPerSpotTaskExpressions(CustomExpression_Net204cts_sec.EXPRESSION.getName()));
        ((ExpressionTreeBuilderInterface) EXPRESSION).addChild(new ConstantNode("9", 9));
        ((ExpressionTreeBuilderInterface) EXPRESSION).setOperation(Function.sqBiweight());

        ((ExpressionTree) EXPRESSION).setRootExpressionTree(true);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSCSummaryCalculation(true);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSTReferenceMaterialCalculation(true);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSAUnknownCalculation(false);
    }
}
