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
import org.cirdles.squid.tasks.expressions.expressionTrees.BuiltInExpressionInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.functions.Function;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("ExpressionTree")
public class CustomExpression_LnUO_U extends ExpressionTree implements BuiltInExpressionInterface {

    private static final long serialVersionUID = -7191987794192109215L;

    /**
     * Squid Excel format is ln(["254/238"])
     */
    public static final String excelExpressionString = "ln([\"254/238\"])";

    public CustomExpression_LnUO_U() {
        super("LnUO/U");
    }

    @Override
    public void buildExpression(TaskInterface task) {
        
        ratiosOfInterest.clear();
        ratiosOfInterest.add("254/238");

        operation = Function.ln();

        childrenET.clear();

        addChild(0, task.findNamedExpression("254/238"));

        rootExpressionTree = true;
        squidSwitchSCSummaryCalculation = false;
        squidSwitchSTReferenceMaterialCalculation = true;
        squidSwitchSAUnknownCalculation = true;
    }

}
