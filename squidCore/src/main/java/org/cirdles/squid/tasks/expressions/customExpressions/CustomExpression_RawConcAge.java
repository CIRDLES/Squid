/* 
 * Copyright 2016 James F. Bowring and CIRDLES.org.
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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeBuilderInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.functions.Function;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForIsotopicRatios;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("ExpressionTree")
public class CustomExpression_RawConcAge {

    /**
     * Squid Excel format is concordiaTW(["238/206"],[±"238/206"],["207/206"],[±"207/206"],,false,true,1)
     */
    public final static ExpressionTreeInterface EXPRESSION = new ExpressionTree("RawConcAge");
//wrong if NU required
    static {
        ((ExpressionTreeBuilderInterface) EXPRESSION)
                .addChild(0, new VariableNodeForIsotopicRatios("238/206"));
        ((ExpressionTreeBuilderInterface) EXPRESSION)
                .addChild(new VariableNodeForIsotopicRatios("207/206"));
        ((ExpressionTreeBuilderInterface) EXPRESSION).setOperation(Function.concordiaTW());

        ((ExpressionTree) EXPRESSION).setRootExpressionTree(true);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSCSummaryCalculation(false);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSTReferenceMaterialCalculation(true);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSAUnknownCalculation(false);
        ((ExpressionTree) EXPRESSION).setSquidSwitchConcentrationReferenceMaterialCalculation(false);
    }
}
