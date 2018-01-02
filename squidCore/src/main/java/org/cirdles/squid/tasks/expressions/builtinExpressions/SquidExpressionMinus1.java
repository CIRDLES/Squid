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
package org.cirdles.squid.tasks.expressions.builtinExpressions;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.BuiltInExpressionInterface;
import org.cirdles.squid.tasks.expressions.operations.Operation;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("ExpressionTree")
public class SquidExpressionMinus1 extends ExpressionTree implements BuiltInExpressionInterface {

    private static final long serialVersionUID = -6450797263330239509L;

    /**
     * Squid Excel format is ["206/238"]/["254/238"]^2 has EqNum = -1
     */
    public static final String excelExpressionString = "[\"206/238\"]/[\"254/238\"]^2";

    public SquidExpressionMinus1() {
        super("206/238 Calib Const");
    }

    @Override
    public void buildExpression(TaskInterface task) {

        ratiosOfInterest.clear();
        ratiosOfInterest.add("206/238");
        ratiosOfInterest.add("254/238");

        operation = Operation.divide();

        childrenET.clear();
        addChild(0, task.findNamedExpression("206/238"));
        ExpressionTreeInterface r254_238wSquared = new ExpressionTree("254/238^2", task.findNamedExpression("254/238"), new ConstantNode("2", 2.0), Operation.pow());
        addChild(r254_238wSquared);

        setRootExpressionTree(true);
        setSquidSwitchSCSummaryCalculation(false);
        setSquidSwitchSTReferenceMaterialCalculation(true);
        setSquidSwitchSAUnknownCalculation(true);
        setSquidSwitchConcentrationReferenceMaterialCalculation(false);
        setSquidSpecialUPbThExpression(true);

    }

}
