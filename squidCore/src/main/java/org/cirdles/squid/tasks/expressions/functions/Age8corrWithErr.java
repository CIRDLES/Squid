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
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA_232_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA_235_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA_238_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SCOMM_86_NAME;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToDoubles;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class Age8corrWithErr extends Function {

    private static final long serialVersionUID = -5789418956709687251L;

    /**
     * This method combines Ludwig's Age8Corr and AgeEr8Corr.
     *
     * Ludwig specifies Age8Corr: Age from uncorrected Tera-Wasserburg ratios,
     * assuming the specified common-Pb 207/206.
     *
     * Ludwig specifies AgeEr8Corr: Error in 208-corrected age (input-ratio
     * errors are absolute).
     *
     * @see
     * https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/isoplot3Basic/Pub.bas
     * @see
     * https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/isoplot3Basic/UPb.bas
     */
    public Age8corrWithErr() {

        name = "Age8corrWithErr";
        argumentCount = 6;
        precedence = 4;
        rowCount = 1;
        colCount = 2;
        labelsForOutputValues = new String[][]{{"Age", "1\u03C3 abs"}};
        labelsForInputValues = new String[]{
            "Total 206/238, Total 206/238 1\u03C3 abs, "
            + "Total 208/232, Total 208/232 1\u03C3 abs,"
            + "232/238, 232/238 1\u03C3 abs"};
    }

    /**
     *
     * Requires that children 0 - 5 are VariableNodes that evaluate to a double
     * array with column 1 representing the values for Total 206/238, Total
     * 206/238 1SigmaUnct, Total 208/232, Total 208/232 1SigmaUnct 232/238,
     * 232/238 1SigmaUnct with a row for each member of shrimpFractions.
     *
     * @param childrenET list containing child 0-5
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
            double[] totPb6U8 = convertObjectArrayToDoubles(childrenET.get(0).eval(shrimpFractions, task)[0]);
            double[] totPb6U8err = convertObjectArrayToDoubles(childrenET.get(1).eval(shrimpFractions, task)[0]);
            double[] totPb8Th2 = convertObjectArrayToDoubles(childrenET.get(2).eval(shrimpFractions, task)[0]);
            double[] totPb8Th2err = convertObjectArrayToDoubles(childrenET.get(3).eval(shrimpFractions, task)[0]);
            double[] th2U8 = convertObjectArrayToDoubles(childrenET.get(4).eval(shrimpFractions, task)[0]);
            double[] th2U8err = convertObjectArrayToDoubles(childrenET.get(5).eval(shrimpFractions, task)[0]);
            
            double sComm_86 = task.getTaskExpressionsEvaluationsPerSpotSet().get(SCOMM_86_NAME).getValues()[0][0];

            double lambda232 = task.getTaskExpressionsEvaluationsPerSpotSet().get(LAMBDA_232_NAME).getValues()[0][0];
            double lambda238 = task.getTaskExpressionsEvaluationsPerSpotSet().get(LAMBDA_238_NAME).getValues()[0][0];

            double[] age8corrWithErr = org.cirdles.ludwig.isoplot3.Pub.age8corrWithErr(
                    totPb6U8[0],
                    totPb6U8err[0],
                    totPb8Th2[0],
                    totPb8Th2err[0],
                    th2U8[0],
                    th2U8err[0],
                    sComm_86,
                    lambda232, lambda238);
            retVal = new Object[][]{{age8corrWithErr[0], age8corrWithErr[1]}};
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
        for (int i = 0; i < childrenET.size(); i++) {
            retVal.append(toStringAnotherExpression(childrenET.get(i))).append("&nbsp;\n");
        }

        retVal.append("</mfenced></mrow>\n");

        return retVal.toString();
    }

}
