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

import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.BuiltInExpressionInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.functions.Function;
import org.cirdles.squid.tasks.expressions.spots.SpotNode;

/**
 *
 * @author James F. Bowring
 */
public class CustomExpression_Hours extends ExpressionTree implements BuiltInExpressionInterface {

//    private static final long serialVersionUID = 4503819747092653761L;

    /**
     * Squid Excel format is ["Hours"]
     */
    public static final String excelExpressionString = "[\"Hours\"]";

    public CustomExpression_Hours() {
        super("Hours");
    }

    @Override
    public void buildExpression(TaskInterface task) {

        operation = Function.ln();

        childrenET.clear();

        addChild(0, SpotNode.buildSpotNode("getHours"));

        setRootExpressionTree(true);
        setSquidSwitchSCSummaryCalculation(false);
        setSquidSwitchSTReferenceMaterialCalculation(true);
        setSquidSwitchSAUnknownCalculation(true);
    }
}
