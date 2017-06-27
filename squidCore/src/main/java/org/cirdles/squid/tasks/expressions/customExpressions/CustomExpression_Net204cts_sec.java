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

import static org.cirdles.squid.shrimp.SquidSpeciesModel.SQUID_DEFAULT_BACKGROUND_ISOTOPE_LABEL;
import org.cirdles.squid.tasks.expressions.ExpressionTree;
import org.cirdles.squid.tasks.expressions.ExpressionTreeBuilderInterface;
import org.cirdles.squid.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.operations.Operation;

/**
 *
 * @author James F. Bowring
 */
public class CustomExpression_Net204cts_sec {

    /**
     * Squid Excel format is ["Total204cts/sec"] - ["Bkrdcts/sec"]
     */
    public final static ExpressionTreeInterface EXPRESSION = new ExpressionTree("Net204cts/sec");

    static {
        ((ExpressionTreeBuilderInterface) EXPRESSION).addChild(0, new ShrimpSpeciesNode(ExpressionTree.squidProject.lookUpSpeciesByName("204"), "getTotalCps"));
        ((ExpressionTreeBuilderInterface) EXPRESSION).addChild(new ShrimpSpeciesNode(ExpressionTree.squidProject.lookUpSpeciesByName(SQUID_DEFAULT_BACKGROUND_ISOTOPE_LABEL), "getTotalCps"));
        ((ExpressionTreeBuilderInterface) EXPRESSION).setOperation(Operation.subtract());

        ((ExpressionTree) EXPRESSION).setRootExpressionTree(true);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSCSummaryCalculation(false);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSTReferenceMaterialCalculation(true);
        ((ExpressionTree) EXPRESSION).setSquidSwitchSAUnknownCalculation(false);
    }
}
