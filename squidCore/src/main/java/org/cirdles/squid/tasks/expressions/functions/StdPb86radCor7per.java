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
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SCOMM_64_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SCOMM_74_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SCOMM_84_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.STD_7_6;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToDoubles;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class StdPb86radCor7per extends Function {

    private static final long serialVersionUID = 3474777008380697077L;

    /**
     * Provides the functionality of Squid's StdPb86radCor7per and returning
     * radiogenic 208Pb/206Pb %err and encoding the labels for each cell of the
     * values array produced by eval.
     *
     * @see
     * https://github.com/CIRDLES/LudwigLibrary/blob/master/vbaCode/squid2.5Basic/PbUTh_2.bas
     */
    public StdPb86radCor7per() {

        name = "stdPb86radCor7per";
        argumentCount = 4;
        precedence = 10;
        rowCount = 1;
        colCount = 1;
        labelsForOutputValues = new String[][]{{"stdPb86radCor7per"}};
        labelsForInputValues = new String[]{
            "208/206RatioAnd1\u03C3 abs", "207/206RatioAnd1\u03C3 abs", "radPb86cor7", "pb46cor7"};
    }

    /**
     * Requires that child 0 is a VariableNode that evaluates to a double array
     * with one column representing the 208/206 IsotopicRatio and a row for each
     * member of shrimpFractions. Requires that child 1 is a VariableNode that
     * evaluates to a double array with one column representing 207/206 and a
     * row for each member of shrimpFractions. Likewise, child 4 is radPb86cor7,
     * and child 3 is pb46cor7.
     *
     * @param childrenET list containing child 0 - 2
     * @param shrimpFractions a list of shrimpFractions
     * @param task
     * @return the double[1][1] array of 208Pb/206Pb %err
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {
        Object[][] retVal;
        try {
            double[] pb208_206RatioAndUnct = convertObjectArrayToDoubles(childrenET.get(0).eval(shrimpFractions, task)[0]);
            double[] pb207_206RatioAndUnct = convertObjectArrayToDoubles(childrenET.get(1).eval(shrimpFractions, task)[0]);
            double[] radPb86cor7 = convertObjectArrayToDoubles(childrenET.get(2).eval(shrimpFractions, task)[0]);
            double[] pb46cor7 = convertObjectArrayToDoubles(childrenET.get(3).eval(shrimpFractions, task)[0]);

            // convert uncertainties to percents for function call
            double pb208_206Unct = pb208_206RatioAndUnct[1] / pb208_206RatioAndUnct[0] * 100.0;
            double pb207_206Unct = pb207_206RatioAndUnct[1] / pb207_206RatioAndUnct[0] * 100.0;

            double std_76 = task.getTaskExpressionsEvaluationsPerSpotSet().get(STD_7_6).getValues()[0][0];
            double sComm_64 = task.getTaskExpressionsEvaluationsPerSpotSet().get(SCOMM_64_NAME).getValues()[0][0];
            double sComm_74 = task.getTaskExpressionsEvaluationsPerSpotSet().get(SCOMM_74_NAME).getValues()[0][0];
            double sComm_84 = task.getTaskExpressionsEvaluationsPerSpotSet().get(SCOMM_84_NAME).getValues()[0][0];

            double[] stdPb86radCor7per = org.cirdles.ludwig.squid25.PbUTh_2.stdPb86radCor7per(
                    pb208_206RatioAndUnct[0], pb208_206Unct, pb207_206RatioAndUnct[0], pb207_206Unct,
                    radPb86cor7[0], pb46cor7[0], std_76, sComm_64, sComm_74, sComm_84);

            retVal = new Object[][]{{stdPb86radCor7per[0]}};
        } catch (ArithmeticException | IndexOutOfBoundsException | NullPointerException e) {
            retVal = new Object[][]{{0.0}};
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
        String retVal
                = "<mrow>"
                + "<mi>StdPb86radCor7per</mi>"
                + "<mfenced>";

        for (int i = 0; i < childrenET.size(); i++) {
            retVal += toStringAnotherExpression(childrenET.get(i)) + "&nbsp;\n";
        }

        retVal += "</mfenced></mrow>\n";

        return retVal;
    }

}
