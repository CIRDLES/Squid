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
package org.cirdles.squid.tasks.expressions;

import java.io.IOException;
import org.cirdles.squid.shrimp.RawRatioNamesSHRIMP;
import org.cirdles.squid.tasks.expressions.builtinExpressions.SquidExpressionMinus3;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.functions.Ln;
import org.cirdles.squid.tasks.expressions.operations.Add;
import org.cirdles.squid.tasks.expressions.operations.Divide;
import org.cirdles.squid.tasks.expressions.operations.Multiply;
import org.cirdles.squid.tasks.expressions.operations.Operation;
import org.cirdles.squid.tasks.expressions.operations.Subtract;


/**
 *
 * @author James F. Bowring
 */
public class Driver {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        ExpressionTreeInterface expT = new ExpressionTree("NONAME", new ExpressionTree("five"), new ExpressionTree("six"), new Add());
//        ExpressionTreeInterface expT2 = new ExpressionTree("NONAME", expT, expT, new Subtract());
//        ExpressionTreeInterface expT3 = new ExpressionTree("NONAME", expT, expT, new Multiply());
//        ExpressionTreeInterface expT4 = new ExpressionTree("NONAME", expT3, expT, new Divide());
//        ExpressionTreeInterface expT5 = new ExpressionTree("NONAME", expT3, null, new Ln());
//
//        ExpressionTreeInterface EXPRESSION = new ExpressionTree("test");
//
//        ((ExpressionTreeWithRatiosInterface) EXPRESSION).getRatiosOfInterest().add("r238_196w");
//        ExpressionTreeInterface r238_196w = ExpressionTreeWithRatiosInterface.buildRatioExpression(RawRatioNamesSHRIMP.r238_196w);
//
//        ((ExpressionTreeWithRatiosInterface) EXPRESSION).getRatiosOfInterest().add(RawRatioNamesSHRIMP.r254_238w);
//        ExpressionTreeInterface r254_238w = ExpressionTreeWithRatiosInterface.buildRatioExpression(RawRatioNamesSHRIMP.r254_238w);
//
//        ExpressionTreeInterface r254_238wPow = new ExpressionTree("254/238^0.66", r254_238w, new ConstantNode("0.66", 0.66), Operation.pow());
//
//        ((ExpressionTreeBuilderInterface) EXPRESSION).addChild(0, r238_196w);
//        ((ExpressionTreeBuilderInterface) EXPRESSION).addChild(r254_238wPow);
//        ((ExpressionTreeBuilderInterface) EXPRESSION).setOperation(Operation.divide());
//
//        ExpressionTreeInterface EXPRESSION2 = new ExpressionTree("test");
//        ((ExpressionTreeBuilderInterface) EXPRESSION2).addChild(0, EXPRESSION);
//        ((ExpressionTreeBuilderInterface) EXPRESSION2).addChild(SquidExpressionMinus3.EXPRESSION);
//        ((ExpressionTreeBuilderInterface) EXPRESSION2).setOperation(Operation.pExp());
//
//        ((ExpressionTree) EXPRESSION2).setRootExpressionTree(true);
//        System.out.println(EXPRESSION2.toStringMathML());
//        
//        try {
//            ExpressionWriterMathML.writeExpressionToFileHTML(EXPRESSION2);
//        } catch (IOException iOException) {
//        }
//        System.out.println("RESULTS = " + expT.eval(null, null) + "  " + expT2.eval(null, null) + "  " + expT3.eval(null, null) + "  " + expT4.eval(null, null) + "  " + expT5.eval(null, null));
    }
}
