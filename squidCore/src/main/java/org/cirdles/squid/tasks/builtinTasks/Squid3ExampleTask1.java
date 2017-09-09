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
package org.cirdles.squid.tasks.builtinTasks;

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

        ratioNames.add("204/206");
        ratioNames.add("207/206");
        ratioNames.add("208/206");
        ratioNames.add("238/196");
        ratioNames.add("206/238");
        ratioNames.add("254/238");
        ratioNames.add("248/254");
        ratioNames.add("206/270");
        ratioNames.add("270/254");
        ratioNames.add("206/254");
        ratioNames.add("238/206");

        taskExpressionsOrdered.add(
                new Expression(new CustomExpression_LnUO_U(), CustomExpression_LnUO_U.excelExpressionString));
        taskExpressionsOrdered.add(
                new Expression(new CustomExpression_LnPbR_U(), CustomExpression_LnPbR_U.excelExpressionString));
        taskExpressionsOrdered.add(
                new Expression(new SquidExpressionMinus1(), SquidExpressionMinus1.excelExpressionString));
        taskExpressionsOrdered.add(
                new Expression(new SquidExpressionMinus4(), SquidExpressionMinus4.excelExpressionString));
        taskExpressionsOrdered.add(
                new Expression(new SquidExpressionMinus3(), SquidExpressionMinus3.excelExpressionString));
        taskExpressionsOrdered.add(
                new Expression(new CustomExpression_Net204cts_sec(), CustomExpression_Net204cts_sec.excelExpressionString));
        taskExpressionsOrdered.add(
                new Expression(new CustomExpression_Net204BiWt(), CustomExpression_Net204BiWt.excelExpressionString));

//
//        taskExpressionTreesOrdered.add(CustomExpression_Expo.EXPRESSION);
//
//        taskExpressionTreesOrdered.add(CustomExpression_TestIf.EXPRESSION);
//
//        taskExpressionTreesOrdered.add(CustomExpression_RawPb76Age.EXPRESSION);
//
//        taskExpressionTreesOrdered.add(CustomExpression_Mean7_6.EXPRESSION);
//
//        taskExpressionTreesOrdered.add(CustomExpression_RawConcAge.EXPRESSION);
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
//        ((XMLSerializerInterface) this).serializeXMLObject(this, "Squid3ExampleTask1.xml");
//
//        TaskInterface test = new Task();
//        test = (TaskInterface)((XMLSerializerInterface) test).readXMLObject("Squid3ExampleTask1.xml", false);
//        ((XMLSerializerInterface)test).serializeXMLObject(test, "SquidBodorkosTask1XXX.xml");
//        
//        ((ExpressionTree) test).setName("TESTSquidExpressionMinus3");
//        ((ExpressionTree) test).setRootExpressionTree(true);
//        taskExpressionTreesOrdered.add(test);
    }
}
