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
public class SquidExpressionMinus3 extends ExpressionTree implements BuiltInExpressionInterface {

    private static final long serialVersionUID = -8990134118105371171L;

    /**
     * Squid Excel format is (0.03446*["254/238"]+0.868)*["248/254"] EqNum -3
     */
    public static final String excelExpressionString = "(0.03446*[\"254/238\"]+0.868)*[\"248/254\"]";

    public SquidExpressionMinus3() {
        super("232/238");
    }

    @Override
    public void buildExpression(TaskInterface task) {

        ratiosOfInterest.clear();
        ratiosOfInterest.add("254/238");
        ratiosOfInterest.add("248/254");

        operation = Operation.multiply();

        childrenET.clear();
        ExpressionTreeInterface term1 = new ExpressionTree(
                "0.03446 * 254/238",
                new ConstantNode("0.03446", 0.03446),
                task.findNamedExpression("254/238"),
                Operation.multiply());
        ExpressionTreeInterface term2 = new ExpressionTree(
                "0.03446 * 254/238 + 0.868",
                term1,
                new ConstantNode("0.868", 0.868),
                Operation.add());

        addChild(0, term2);
        addChild(task.findNamedExpression("248/254"));

        setRootExpressionTree(true);
        setSquidSwitchSCSummaryCalculation(false);
        setSquidSwitchSTReferenceMaterialCalculation(true);
        setSquidSwitchSAUnknownCalculation(true);
        setSquidSpecialUPbThExpression(true);

    }
}
