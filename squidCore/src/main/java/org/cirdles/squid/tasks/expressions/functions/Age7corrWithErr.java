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
public class Age7corrWithErr extends Function {

    private static final long serialVersionUID = -8516207588649068367L;

    /**
     * Provides the functionality of Isoplot3's Age7corrWithErr by calling
     * pbPbAge and returning "Age" and "AgeErr" and encoding the labels for each
     * cell of the values array produced by eval.
     *
     * @see
     * https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/isoplot3Basic/Pub.bas
     * @see
     * https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/isoplot3Basic/UPb.bas
     */
    public Age7corrWithErr() {

        name = "Age7corrWithErr";
        argumentCount = 4;
        precedence = 4;
        rowCount = 1;
        colCount = 2;
        labelsForOutputValues = new String[][]{{"Age", "1\u03C3 abs"}};
        labelsForInputValues = new String[]{"Total 206/238, Total 206/238 1\u03C3 abs, Total 207/206, Total 207/206 1\u03C3 abs"};
    }

    /**
     *
     * Requires that children 0 -3 are VariableNodes that evaluate to a double
     * array with column 1 representing the values for Total 206/238, Total
     * 206/238 1SigmaUnct, Total 207/206, Total 207/206 1SigmaUnct with a row
     * for each member of shrimpFractions.
     *
     * @param childrenET list containing child 0-3
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
            double[] totPb76 = convertObjectArrayToDoubles(childrenET.get(2).eval(shrimpFractions, task)[0]);
            double[] totPb76err = convertObjectArrayToDoubles(childrenET.get(3).eval(shrimpFractions, task)[0]);
            
//            double sComm_76 = task.getTaskExpressionsEvaluationsPerSpotSet().get(DEFCOM_76).getValues()[0][0];
            double sComm_76 = shrimpFractions.get(0).getCom_207Pb206Pb();

            double present238U235U = task.getTaskExpressionsEvaluationsPerSpotSet().get(REF_238U235U).getValues()[0][0];
            double lambda235 = task.getTaskExpressionsEvaluationsPerSpotSet().get(LAMBDA235).getValues()[0][0];
            double lambda238 = task.getTaskExpressionsEvaluationsPerSpotSet().get(LAMBDA238).getValues()[0][0];

            double[] age7corrWithErr = org.cirdles.ludwig.isoplot3.Pub.age7corrWithErr(totPb6U8[0],
                    totPb6U8err[0],
                    totPb76[0],
                    totPb76err[0],
                    sComm_76,
                    lambda235, lambda238, present238U235U);
            retVal = new Object[][]{{age7corrWithErr[0], age7corrWithErr[1]}};
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
