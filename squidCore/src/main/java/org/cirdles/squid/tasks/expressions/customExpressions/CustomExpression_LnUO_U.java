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

import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.tasks.expressions.ExpressionTree;
import org.cirdles.squid.tasks.expressions.ExpressionTreeBuilderInterface;
import org.cirdles.squid.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.ExpressionTreeWithRatiosInterface;
import org.cirdles.squid.tasks.expressions.functions.Function;

/**
 *
 * @author James F. Bowring
 */
public class CustomExpression_LnUO_U {

    /**
     * Squid Excel format is ln(["254/238"])
     */
    public final static ExpressionTreeInterface EXPRESSION = new ExpressionTree("LnUO/U");

    static {
        ((ExpressionTreeWithRatiosInterface) EXPRESSION).getRatiosOfInterest().add("254/238");

        ((ExpressionTreeBuilderInterface) EXPRESSION).addChild(0, SquidRatiosModel.buildRatioExpression("254/238"));
        ((ExpressionTreeBuilderInterface) EXPRESSION).setOperation(Function.ln());

        ((ExpressionTree) EXPRESSION).setRootExpressionTree(true);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSCSummaryCalculation(false);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSTReferenceMaterialCalculation(true);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSAUnknownCalculation(true);
    }
}
