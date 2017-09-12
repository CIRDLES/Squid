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

import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.BuiltInExpressionInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.operations.Operation;

/**
 *
 * @author James F. Bowring
 */
public class SquidExpressionMinus4 extends ExpressionTree implements BuiltInExpressionInterface {

    private static final long serialVersionUID = 1036410631333809397L;

    /**
     * Squid Excel format is ["238/196"]/["254/238"]^0.66 has EqNum = -4
     */
    public static final String excelExpressionString = "[\"238/196\"]/[\"254/238\"]^0.66";

    public SquidExpressionMinus4() {
        super("U Conc Const");
    }

    @Override
    public void buildExpression(TaskInterface task) {

        ratiosOfInterest.add("238/196");
        ratiosOfInterest.add("254/238");

        operation = Operation.divide();

        childrenET.clear();
        ExpressionTreeInterface r254_238wPow = new ExpressionTree(
                "254/238^0.66",
                task.findNamedExpression("254/238"),
                new ConstantNode("0.66", 0.66),
                Operation.pow());

        addChild(0, task.findNamedExpression("238/196"));
        addChild(r254_238wPow);

        setRootExpressionTree(true);
        setSquidSwitchSCSummaryCalculation(false);
        setSquidSwitchSTReferenceMaterialCalculation(true);
        setSquidSwitchSAUnknownCalculation(true);
        setSquidSpecialUPbThExpression(true);

    }
}
