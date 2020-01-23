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
package org.cirdles.squid.tasks.expressions.functions;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.List;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA235;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA238;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToDoubles;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REF_238U235U;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class AgePb76 extends Function {

    private static final long serialVersionUID = -6711265919551953531L;

    /**
     * Provides the functionality of Squid2.5's agePb76 by calling pbPbAge and
     * returning "Age" and "AgeErr" and encoding the labels for each cell of the
     * values array produced by eval.
     *
     * @see
     * https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/isoplot3Basic/Pub.bas
     * @see
     * https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/isoplot3Basic/UPb.bas
     */
    public AgePb76() {

        name = "AgePb76";
        argumentCount = 1;
        precedence = 4;
        rowCount = 1;
        colCount = 2;
        labelsForInputValues = new String[]{"[\"207/206\"] (includes Ratio and 1\u03C3abs)"};
        labelsForOutputValues = new String[][]{{"Age", "1\u03C3 abs"}}; 
        definition
                = "This function is based on Isoplot's AgePb76, which calculates the 207/206 age from the 207/206 ratio.\n "
                + DEF_TAB + "However, since Squid3 ratios of interest of the form [\"206/207\"] have an associated uncertainty,\n"
                + DEF_TAB + "invoking AgePb76([\"206/207\"]) will also return the 1\u03C3 abs uncertainty of the age.\n"
                + DEF_TAB + "To calculate a 76 age with uncertainty from separate values of ratio and ratio uncertainty,\n"
                + DEF_TAB + "use the Squid3 function AgePb76WithErr(ratio, unct).\n"
                + DEF_TAB + "The underlying function is Isoplot's PbPbAge, found at \n"
                + DEF_TAB + "https://github.com/CIRDLES/LudwigLibrary/blob/master/vbaCode/isoplot3Basic/UPb.bas";
    }

    /**
     * Requires that child 0 is a VariableNode that evaluates to a double array
     * with column 1 representing the 207/206 IsotopicRatio and column 2
     * containing the 1sigma abs unct with a row for each member of
     * shrimpFractions.
     *
     * @param childrenET list containing child 0
     * @param shrimpFractions a list of shrimpFractions
     * @param task
     * @return the double[1][2] array of age, ageErr
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {

        Object[][] retVal;
        try {
            double[] pb207_206RatioAndUnct = convertObjectArrayToDoubles(childrenET.get(0).eval(shrimpFractions, task)[0]);

            double present238U235U = task.getTaskExpressionsEvaluationsPerSpotSet().get(REF_238U235U).getValues()[0][0];
            double lambda235 = task.getTaskExpressionsEvaluationsPerSpotSet().get(LAMBDA235).getValues()[0][0];
            double lambda238 = task.getTaskExpressionsEvaluationsPerSpotSet().get(LAMBDA238).getValues()[0][0];

            double[] agePb76 = org.cirdles.ludwig.isoplot3.UPb.pbPbAge(pb207_206RatioAndUnct[0],
                    (pb207_206RatioAndUnct.length > 1) ? pb207_206RatioAndUnct[1] : 0.0,
                    lambda235, lambda238, present238U235U);
            retVal = new Object[][]{{agePb76[0], agePb76[1]}};
        } catch (ArithmeticException | IndexOutOfBoundsException | NullPointerException e) {
            retVal = new Object[][]{{0.0, 0.0}};
        }

        return retVal;
    }

    /**
     *
     * @param childrenET the value of childrenET
     * @return
     */
    @Override
    public String toStringMathML(List<ExpressionTreeInterface> childrenET) {

        StringBuilder retVal = new StringBuilder();
        retVal.append("<mrow>");
        retVal.append("<mi>").append(name).append("</mi>");
        retVal.append("<mfenced>");
        retVal.append(buildChildrenToMathML(childrenET));

        retVal.append("</mfenced></mrow>\n");

        return retVal.toString();
    }

}
