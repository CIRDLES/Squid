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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.BuiltInExpressionInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.functions.Function;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForPerSpotTaskExpressions;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("ExpressionTree")
public class CustomExpression_Net204BiWt extends ExpressionTree implements BuiltInExpressionInterface {

    private static final long serialVersionUID = -5952836801278720275L;

    /**
     * Squid Excel format is sqBiweight(["Net204cts/sec"],9)
     */
    public static final String excelExpressionString = "sqBiweight([\"Net204cts/sec\"],9)";

    public CustomExpression_Net204BiWt() {
        super("Net204BiWt");
    }

    @Override
    public void buildExpression(TaskInterface task) {

        operation = Function.sqBiweight();

        childrenET.clear();

        // causes amHealthy to fail if expression bad
        ExpressionTreeInterface exp = task.findNamedExpression("Net204cts/sec");
        addChild(0, new VariableNodeForPerSpotTaskExpressions(exp.getName()));

//        addChild(0, new VariableNodeForPerSpotTaskExpressions("Net204cts/sec"));
        addChild(new ConstantNode("9", 9));

        setRootExpressionTree(true);
        setSquidSwitchSCSummaryCalculation(true);
        setSquidSwitchSTReferenceMaterialCalculation(true);
        setSquidSwitchSAUnknownCalculation(false);
        setSquidSwitchConcentrationReferenceMaterialCalculation(false);
    }
}
