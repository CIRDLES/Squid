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

/**
 *
 * @author James F. Bowring
 */
public class CustomExpression_LnPbR_U extends ExpressionTree implements BuiltInExpressionInterface {

    /**
     * Squid Excel format is ln(["206/238"])
     */
    public static String excelExpressionString = "ln([\"206/238\"]";

    public CustomExpression_LnPbR_U() {
        super("LnPbR_U/U");
    }

    @Override
    public void buildExpression(TaskInterface task) {
        ratiosOfInterest.add("206/238");

        childrenET.clear();
        
        addChild(0, task.findNamedExpression("206/238"));
        setOperation(Function.ln());

        setRootExpressionTree(true);
        setSquidSwitchSCSummaryCalculation(false);
        setSquidSwitchSTReferenceMaterialCalculation(true);
        setSquidSwitchSAUnknownCalculation(true);
    }
}
