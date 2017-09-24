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
import static org.cirdles.squid.shrimp.SquidSpeciesModel.SQUID_DEFAULT_BACKGROUND_ISOTOPE_LABEL;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.BuiltInExpressionInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.functions.Function;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.operations.Operation;

/**
 * This class demonstrate the build strategy used by the expression
 * parser.
 *
 * @author James F. Bowring
 */
@XStreamAlias("ExpressionTree")
public class CustomExpression_Net204cts_sec extends ExpressionTree implements BuiltInExpressionInterface {

    private static final long serialVersionUID = -1823847703052156911L;

    /**
     * Squid Excel format is ["Total204cts/sec"] - ["Bkrdcts/sec"]
     */
    public static final String excelExpressionString = "totalCps([\"204\"]) - totalCps([\"BKG\"])";

    public CustomExpression_Net204cts_sec() {
        super("Net204cts/sec");
    }

    @Override
    public void buildExpression(TaskInterface task) {

        operation = Operation.subtract();

        childrenET.clear();

        ExpressionTree leftExp = new ExpressionTree(Function.totalCps());
        leftExp.addChild(0, ShrimpSpeciesNode.buildShrimpSpeciesNode(task.lookUpSpeciesByName("204")));

        ExpressionTree rightExp = new ExpressionTree(Function.totalCps());
        rightExp.addChild(0, ShrimpSpeciesNode.buildShrimpSpeciesNode(task.lookUpSpeciesByName(SQUID_DEFAULT_BACKGROUND_ISOTOPE_LABEL)));

        addChild(0, leftExp);
        addChild(rightExp);

        setRootExpressionTree(true);
        setSquidSwitchSCSummaryCalculation(false);
        setSquidSwitchSTReferenceMaterialCalculation(true);
        setSquidSwitchSAUnknownCalculation(false);
    }
}
