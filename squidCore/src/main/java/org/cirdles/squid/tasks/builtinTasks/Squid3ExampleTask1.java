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
package org.cirdles.squid.tasks.builtinTasks;

import static org.cirdles.squid.constants.Squid3Constants.getDEFAULT_RATIOS_LIST_FOR_10_SPECIES;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.builtinExpressions.SquidExpressionMinus1;
import org.cirdles.squid.tasks.expressions.builtinExpressions.SquidExpressionMinus3;
import org.cirdles.squid.tasks.expressions.builtinExpressions.SquidExpressionMinus4;
import org.cirdles.squid.tasks.expressions.customExpressions.CustomExpression_LnPbR_U;
import org.cirdles.squid.tasks.expressions.customExpressions.CustomExpression_LnUO_U;
import org.cirdles.squid.tasks.expressions.customExpressions.CustomExpression_Net204BiWt;
import org.cirdles.squid.tasks.expressions.customExpressions.CustomExpression_Net204cts_sec;

/**
 *
 * @author James F. Bowring
 */
public class Squid3ExampleTask1 extends Task {

    private static final long serialVersionUID = -914383068154435665L;

    /**
     *
     */
    public Squid3ExampleTask1() {
        super("Squid3ExampleTask1");

        this.type = "Geochron";
        this.description = "Example Squid3 Task for 10-peak zircon.";
        this.authorName = "Squid3 Team";
        this.labName = "GA";
        this.provenance = "Builtin task.";
        this.dateRevised = 0l;

        for (String rn : getDEFAULT_RATIOS_LIST_FOR_10_SPECIES()){
            this.ratioNames.add(rn);
        }
        
        taskExpressionsOrdered.add(
                new Expression(new CustomExpression_LnUO_U(), CustomExpression_LnUO_U.excelExpressionString, true));
        taskExpressionsOrdered.add(
                new Expression(new CustomExpression_LnPbR_U(), CustomExpression_LnPbR_U.excelExpressionString, true));
        taskExpressionsOrdered.add(
                new Expression(new SquidExpressionMinus1(), SquidExpressionMinus1.excelExpressionString, true));
        // this next expression should be last in execution, but placement here serves as a test for sorting execution order
        taskExpressionsOrdered.add(
                new Expression(new CustomExpression_Net204BiWt(), CustomExpression_Net204BiWt.excelExpressionString, false));
        taskExpressionsOrdered.add(
                new Expression(new SquidExpressionMinus4(), SquidExpressionMinus4.excelExpressionString, true));
        taskExpressionsOrdered.add(
                new Expression(new SquidExpressionMinus3(), SquidExpressionMinus3.excelExpressionString, true));
        taskExpressionsOrdered.add(new Expression(new CustomExpression_Net204cts_sec(), CustomExpression_Net204cts_sec.excelExpressionString, false));
    }
}
