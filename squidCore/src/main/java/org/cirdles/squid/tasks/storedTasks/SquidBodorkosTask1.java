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
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
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
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;

/**
 *
 * @author James F. Bowring
 */
public class SquidBodorkosTask1 extends Task {

    /**
     *
     * @param squidProject
     */
    public SquidBodorkosTask1() {
        super("SquidBodorkosTask1");

        ExpressionTree.TASK = this;

        ExpressionTreeInterface exp = new CustomExpression_LnUO_U();
        taskExpressionsOrdered.add(exp);
        NAMED_EXPRESSIONS_MAP.put(exp.getName(), exp);

//        taskExpressionsOrdered.add(CustomExpression_LnPbR_U.EXPRESSION);
//
//        taskExpressionsOrdered.add(SquidExpressionMinus1.EXPRESSION);
//
//        taskExpressionsOrdered.add(SquidExpressionMinus4.EXPRESSION);
//
//        taskExpressionsOrdered.add(SquidExpressionMinus3.EXPRESSION);
//
//        taskExpressionsOrdered.add(CustomExpression_Net204cts_sec.EXPRESSION);
//
//        taskExpressionsOrdered.add(CustomExpression_Net204BiWt.EXPRESSION);
//
//        taskExpressionsOrdered.add(CustomExpression_Expo.EXPRESSION);
//
//        taskExpressionsOrdered.add(CustomExpression_TestIf.EXPRESSION);
//
//        taskExpressionsOrdered.add(CustomExpression_RawPb76Age.EXPRESSION);
//
//        taskExpressionsOrdered.add(CustomExpression_Mean7_6.EXPRESSION);
//
//        taskExpressionsOrdered.add(CustomExpression_RawConcAge.EXPRESSION);

        // experiment
//        ((XMLSerializerInterface) SquidExpressionMinus3.EXPRESSION).serializeXMLObject(CustomExpression_LnUO_U.EXPRESSION, "CustomExpression_LnUO_U.xml");
//        ((XMLSerializerInterface) SquidExpressionMinus3.EXPRESSION).serializeXMLObject(CustomExpression_LnPbR_U.EXPRESSION, "CustomExpression_LnPbR_U.xml");
//        ((XMLSerializerInterface) SquidExpressionMinus3.EXPRESSION).serializeXMLObject(SquidExpressionMinus1.EXPRESSION, "SquidExpressionMinus1.xml");
//        ((XMLSerializerInterface) SquidExpressionMinus3.EXPRESSION).serializeXMLObject(SquidExpressionMinus4.EXPRESSION, "SquidExpressionMinus4.xml");
//        ((XMLSerializerInterface) SquidExpressionMinus3.EXPRESSION).serializeXMLObject(SquidExpressionMinus3.EXPRESSION, "SquidExpressionMinus3.xml");
//
//        ((XMLSerializerInterface) SquidExpressionMinus3.EXPRESSION).serializeXMLObject(CustomExpression_Net204cts_sec.EXPRESSION, "CustomExpression_Net204cts_sec.xml");
//        ((XMLSerializerInterface) SquidExpressionMinus3.EXPRESSION).serializeXMLObject(CustomExpression_Net204BiWt.EXPRESSION, "CustomExpression_Net204BiWt.xml");
//        ((XMLSerializerInterface) SquidExpressionMinus3.EXPRESSION).serializeXMLObject(CustomExpression_Expo.EXPRESSION, "CustomExpression_Expo.xml");
//        ((XMLSerializerInterface) SquidExpressionMinus3.EXPRESSION).serializeXMLObject(CustomExpression_TestIf.EXPRESSION, "CustomExpression_TestIf.xml");
//        ((XMLSerializerInterface) SquidExpressionMinus3.EXPRESSION).serializeXMLObject(CustomExpression_RawPb76Age.EXPRESSION, "CustomExpression_RawPb76Age.xml");
//        ((XMLSerializerInterface) SquidExpressionMinus3.EXPRESSION).serializeXMLObject(CustomExpression_Mean7_6.EXPRESSION, "CustomExpression_Mean7_6.xml");
//        ((XMLSerializerInterface) SquidExpressionMinus3.EXPRESSION).serializeXMLObject(CustomExpression_RawConcAge.EXPRESSION, "CustomExpression_RawConcAge.xml");
//        
//        ((XMLSerializerInterface) this).serializeXMLObject(this, "SquidBodorkosTask1.xml");
//
//        TaskInterface test = new Task();
//        test = (TaskInterface)((XMLSerializerInterface) test).readXMLObject("SquidBodorkosTask1.xml", false);
//        ((XMLSerializerInterface)test).serializeXMLObject(test, "SquidBodorkosTask1XXX.xml");
//        
        
//        ((ExpressionTree) test).setName("TESTSquidExpressionMinus3");
//        ((ExpressionTree) test).setRootExpressionTree(true);
//        taskExpressionsOrdered.add(test);
    }
}
