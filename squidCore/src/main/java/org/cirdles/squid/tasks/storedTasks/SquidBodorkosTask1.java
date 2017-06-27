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
package org.cirdles.squid.tasks.storedTasks;

import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.expressions.ExpressionTree;
import org.cirdles.squid.tasks.expressions.builtinExpressions.SquidExpressionMinus1;
import org.cirdles.squid.tasks.expressions.builtinExpressions.SquidExpressionMinus3;
import org.cirdles.squid.tasks.expressions.builtinExpressions.SquidExpressionMinus4;
import org.cirdles.squid.tasks.expressions.customExpressions.CustomExpression_Expo;
import org.cirdles.squid.tasks.expressions.customExpressions.CustomExpression_LnPbR_U;
import org.cirdles.squid.tasks.expressions.customExpressions.CustomExpression_LnUO_U;
import org.cirdles.squid.tasks.expressions.customExpressions.CustomExpression_Mean7_6;
import org.cirdles.squid.tasks.expressions.customExpressions.CustomExpression_Net204BiWt;
import org.cirdles.squid.tasks.expressions.customExpressions.CustomExpression_Net204cts_sec;
import org.cirdles.squid.tasks.expressions.customExpressions.CustomExpression_RawConcAge;
import org.cirdles.squid.tasks.expressions.customExpressions.CustomExpression_RawPb76Age;
import org.cirdles.squid.tasks.expressions.customExpressions.CustomExpression_TestIf;

/**
 *
 * @author James F. Bowring
 */
public class SquidBodorkosTask1 extends Task {

    /**
     *
     * @param squidProject
     */
    public SquidBodorkosTask1(SquidProject squidProject) {
        super("SquidBodorkosTask1", squidProject);
        
        ExpressionTree.squidProject = squidProject;
        
        taskExpressionsOrdered.add(CustomExpression_LnUO_U.EXPRESSION);

        taskExpressionsOrdered.add(CustomExpression_LnPbR_U.EXPRESSION);

        taskExpressionsOrdered.add(SquidExpressionMinus1.EXPRESSION);

        taskExpressionsOrdered.add(SquidExpressionMinus4.EXPRESSION);

        taskExpressionsOrdered.add(SquidExpressionMinus3.EXPRESSION);
        
        taskExpressionsOrdered.add(CustomExpression_Net204cts_sec.EXPRESSION);
        
        taskExpressionsOrdered.add(CustomExpression_Net204BiWt.EXPRESSION);
        
        taskExpressionsOrdered.add(CustomExpression_Expo.EXPRESSION);
        
        taskExpressionsOrdered.add(CustomExpression_TestIf.EXPRESSION);
        
        taskExpressionsOrdered.add(CustomExpression_RawPb76Age.EXPRESSION);
        
        taskExpressionsOrdered.add(CustomExpression_Mean7_6.EXPRESSION);
        
        taskExpressionsOrdered.add(CustomExpression_RawConcAge.EXPRESSION);
        
        // experiment
//        ((XMLSerializerInterface) SquidExpressionMinus3.EXPRESSION).serializeXMLObject(SquidExpressionMinus3.EXPRESSION, "SquidExpressionMinus3.xml");
//        ExpressionTreeInterface test = new ExpressionTree();
//        test = (ExpressionTreeInterface)((XMLSerializerInterface) test).readXMLObject("SquidExpressionMinus3.xml", false);
//        ((ExpressionTree) test).setName("TESTSquidExpressionMinus3");
//        ((ExpressionTree) test).setRootExpressionTree(true);
//        taskExpressionsOrdered.add(test);
    }
}
