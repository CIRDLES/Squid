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
import static org.cirdles.squid.parameters.util.Lambdas.LAMBDA_235;
import static org.cirdles.squid.parameters.util.Lambdas.LAMBDA_238;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToDoubles;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFCOM_64;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFCOM_74;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFCOM_84;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REF_238U235U;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class Pb86radCor7per extends Function {

    private static final long serialVersionUID = -6404035232270915370L;

    /**
     * Provides the functionality of Squid2.5's Pb86radCor7per and returning
     * radiogenic 208Pb/206Pb %err and encoding the labels for each cell of the
     * values array produced by eval.
     *
     * @see
     * https://github.com/CIRDLES/LudwigLibrary/blob/master/vbaCode/squid2.5Basic/PbUTh_2.bas
     */
    public Pb86radCor7per() {

        name = "Pb86radCor7per";
        argumentCount = 5;
        precedence = 10;
        rowCount = 1;
        colCount = 1;
        labelsForOutputValues = new String[][]{{"pb86radCor7per"}};
        labelsForInputValues = new String[]{
            "208/206 Ratio with Unct", "207/206 Ratio with 1\u03C3 abs", "Total206Pb/238U", "Total206Pb/238U%err", "207corr206Pb/238UAge"};
    }

    /**
     * Requires that child 0 is a VariableNode that evaluates to a double array
     * with one column representing the 208/206 IsotopicRatio and a row for each
     * member of shrimpFractions. Requires that child 1 is a VariableNode that
     * evaluates to a double array with one column representing 207/206 and a
     * row for each member of shrimpFractions. Likewise, child 2 is
     * Total206Pb/238U, child 3 is Total206Pb/238U %err, and child 4 is
     * 207corr206Pb/238UAge.
     *
     * @param childrenET list containing children 0 - 4
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
            double[] pb6U8tot = convertObjectArrayToDoubles(childrenET.get(2).eval(shrimpFractions, task)[0]);
            double[] pb6U8totPerr = convertObjectArrayToDoubles(childrenET.get(3).eval(shrimpFractions, task)[0]);
            double[] age7corPb6U8 = convertObjectArrayToDoubles(childrenET.get(4).eval(shrimpFractions, task)[0]);

            // convert uncertainties to percents for function call
            double pb208_206Unct = pb208_206RatioAndUnct[1] / pb208_206RatioAndUnct[0] * 100.0;
            double pb207_206Unct = pb207_206RatioAndUnct[1] / pb207_206RatioAndUnct[0] * 100.0;
            
            double sComm_64 = task.getTaskExpressionsEvaluationsPerSpotSet().get(DEFCOM_64).getValues()[0][0];
            double sComm_74 = task.getTaskExpressionsEvaluationsPerSpotSet().get(DEFCOM_74).getValues()[0][0];
            double sComm_84 = task.getTaskExpressionsEvaluationsPerSpotSet().get(DEFCOM_84).getValues()[0][0];

            double present238U235U = task.getTaskExpressionsEvaluationsPerSpotSet().get(REF_238U235U).getValues()[0][0];
            double lambda235 = task.getTaskExpressionsEvaluationsPerSpotSet().get(LAMBDA_235.getName()).getValues()[0][0];
            double lambda238 = task.getTaskExpressionsEvaluationsPerSpotSet().get(LAMBDA_238.getName()).getValues()[0][0];

            double[] pb86radCor7per = org.cirdles.ludwig.squid25.PbUTh_2.pb86radCor7per(pb208_206RatioAndUnct[0], pb208_206Unct, pb207_206RatioAndUnct[0], pb207_206Unct,
                    pb6U8tot[0], pb6U8totPerr[0], age7corPb6U8[0],
                    sComm_64, sComm_74, sComm_84,
                    lambda235, lambda238, present238U235U);

            retVal = new Object[][]{{pb86radCor7per[0]}};
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
                + "<mi>" + name + "</mi>"
                + "<mfenced>";

        retVal += buildChildrenToMathML(childrenET);

        retVal += "</mfenced></mrow>\n";

        return retVal;
    }

}
