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
import static org.cirdles.squid.parameters.util.Lambdas.LAMBDA_232;
import static org.cirdles.squid.parameters.util.Lambdas.LAMBDA_238;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToDoubles;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFCOM_76;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFCOM_86;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R206PB_238U;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REF_238U235U;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class Rad8corConcRho extends Function {

    private static final long serialVersionUID = 1126819217616132995L;

    /**
     * Ludwig specifies: Returns the error correlation for 208-corrected
     * 206PbSTAR/238U-207PbSTAR/235U ratio-pairs.
     *
     * @see
     * https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/isoplot3Basic/Pub.bas
     * @see
     * https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/isoplot3Basic/UPb.bas
     */
    public Rad8corConcRho() {

        name = "Rad8corConcRho";
        argumentCount = 9;
        precedence = 4;
        rowCount = 1;
        colCount = 2;
        labelsForOutputValues = new String[][]{{"rho"}};
        labelsForInputValues = new String[]{
            "Total 206/238, Total 206/238 1%Unct,"
            + PB8CORR + R206PB_238U + ","
            + "Total 208/232, Total 208/232 1%Unct, "
            + "Total 207/206, Total 207/206 1%Unct,"
            + "Total 208/206, Total 208/206 1%Unct"};
    }

    /**
     *
     * Requires that children 0 - 8 are VariableNodes that evaluate to a double
     * array with column 1 representing the values for Total 206/238, Total
     * 206/238 1%Unct, 8-corr 206/238, Total 208/232, Total 208/232 1%Unct,
     * Total 207/206, Total 207/206 1%Unct, Total 208/206, Total 208/206 1%Unct
     * with a row for each member of shrimpFractions.
     *
     * @param childrenET list containing child 0-8
     * @param shrimpFractions a list of shrimpFractions
     * @param task
     * @return the double[1][1] array of rho
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {
        Object[][] retVal;
        try {
            double[] totPb6U8 = convertObjectArrayToDoubles(childrenET.get(0).eval(shrimpFractions, task)[0]);
            double[] totPb6U8per = convertObjectArrayToDoubles(childrenET.get(1).eval(shrimpFractions, task)[0]);
            double[] radPb6U8 = convertObjectArrayToDoubles(childrenET.get(2).eval(shrimpFractions, task)[0]);
            double[] th2U8 = convertObjectArrayToDoubles(childrenET.get(3).eval(shrimpFractions, task)[0]);
            double[] th2U8per = convertObjectArrayToDoubles(childrenET.get(4).eval(shrimpFractions, task)[0]);
            double[] totPb76 = convertObjectArrayToDoubles(childrenET.get(5).eval(shrimpFractions, task)[0]);
            double[] totPb76per = convertObjectArrayToDoubles(childrenET.get(6).eval(shrimpFractions, task)[0]);
            double[] totPb86 = convertObjectArrayToDoubles(childrenET.get(7).eval(shrimpFractions, task)[0]);
            double[] totPb86per = convertObjectArrayToDoubles(childrenET.get(8).eval(shrimpFractions, task)[0]);

            double sComm_76 = task.getTaskExpressionsEvaluationsPerSpotSet().get(DEFCOM_76).getValues()[0][0];
            double sComm_86 = task.getTaskExpressionsEvaluationsPerSpotSet().get(DEFCOM_86).getValues()[0][0];

            double present238U235U = task.getTaskExpressionsEvaluationsPerSpotSet().get(REF_238U235U).getValues()[0][0];
            double lambda232 = task.getTaskExpressionsEvaluationsPerSpotSet().get(LAMBDA_232.getName()).getValues()[0][0];
            double lambda238 = task.getTaskExpressionsEvaluationsPerSpotSet().get(LAMBDA_238.getName()).getValues()[0][0];

            double[] rad8corConcRho = org.cirdles.ludwig.squid25.PbUTh_2.rad8corConcRho(totPb6U8[0],
                    totPb6U8per[0],
                    radPb6U8[0],
                    th2U8[0],
                    th2U8per[0],
                    totPb76[0],
                    totPb76per[0],
                    totPb86[0],
                    totPb86per[0],
                    sComm_76, sComm_86,
                    present238U235U, lambda232, lambda238);

            retVal = new Object[][]{{rad8corConcRho[0]}};
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

        StringBuilder retVal = new StringBuilder();
        retVal.append("<mrow>");
        retVal.append("<mi>").append(name).append("</mi>");
        retVal.append("<mfenced>");
        retVal.append(buildChildrenToMathML(childrenET));

        retVal.append("</mfenced></mrow>\n");

        return retVal.toString();
    }

}
