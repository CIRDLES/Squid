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

import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeBuilderInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeWithRatiosInterface;
import org.cirdles.squid.tasks.expressions.functions.Function;

/**
 *
 * @author James F. Bowring
 */
public class CustomExpression_LnPbR_U {

    /**
     * Squid Excel format is ln(["206/238"])
     */
    public final static ExpressionTreeInterface EXPRESSION = new ExpressionTree("LnPbR_U");

//    static {
//        ((ExpressionTreeWithRatiosInterface) EXPRESSION).getRatiosOfInterest().add("206/238");
//
//        ((ExpressionTreeBuilderInterface) EXPRESSION).addChild(0, Task.buildRatioExpression("206/238"));
//        ((ExpressionTreeBuilderInterface) EXPRESSION).setOperation(Function.ln());
//
//        ((ExpressionTree) EXPRESSION).setRootExpressionTree(true);
//        ((ExpressionTree) EXPRESSION).setSquidSwitchSCSummaryCalculation(false);
//        ((ExpressionTree) EXPRESSION).setSquidSwitchSTReferenceMaterialCalculation(true);
//        ((ExpressionTree) EXPRESSION).setSquidSwitchSAUnknownCalculation(true);
//    }
}
