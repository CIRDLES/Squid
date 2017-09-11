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
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.BuiltInExpressionInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.operations.Operation;

/**
 *
 * @author James F. Bowring
 */
public class CustomExpression_Net204cts_sec extends ExpressionTree implements BuiltInExpressionInterface {

    /**
     * Squid Excel format is ["Total204cts/sec"] - ["Bkrdcts/sec"]
     */
    public static String excelExpressionString = "[\"Total204cts/sec\"] - [\"Bkrdcts/sec\"]";

    public CustomExpression_Net204cts_sec() {
        super("Net204cts/sec");
    }

    @Override
    public void buildExpression(TaskInterface task) {

        operation = Operation.subtract();

        childrenET.clear();

        addChild(0, ShrimpSpeciesNode.buildShrimpSpeciesNode(task.lookUpSpeciesByName("204"), "getTotalCps"));
        addChild(ShrimpSpeciesNode.buildShrimpSpeciesNode(task.lookUpSpeciesByName(SQUID_DEFAULT_BACKGROUND_ISOTOPE_LABEL), "getTotalCps"));

        setRootExpressionTree(true);
        setSquidSwitchSCSummaryCalculation(false);
        setSquidSwitchSTReferenceMaterialCalculation(true);
        setSquidSwitchSAUnknownCalculation(false);
    }
}
