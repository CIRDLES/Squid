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
import org.cirdles.ludwig.squid25.SquidConstants;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.AV_PARENT_ELEMENT_CONC_CONST;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class CalculateMeanConcStd extends Function {

    private static final long serialVersionUID = -5093949907505008415L;

    /**
     * Provides the functionality of Squid2.5's GetConcStdData, calculating the
     * mean concentration of the parent isotope of the concentration reference
     * standard, known as Av_ParentElement_ConcenConst.
     *
     * @see
     * https://github.com/CIRDLES/ET_Redux/wiki/SQ2.50-Procedural-Framework:-Part-1
     */
    public CalculateMeanConcStd() {
        name = "CalculateMeanConcStd";
        argumentCount = 1;
        precedence = 4;
        rowCount = 1;
        colCount = 2;
        labelsForOutputValues = new String[][]{{AV_PARENT_ELEMENT_CONC_CONST}};
        labelsForInputValues = new String[]{"per-spot expression with concentration values in ppm"};
        summaryCalc = true;
        definition = "Calculates the sum divided by the count, ignoring tiny values < 1.0E-30.";
    }

    /**
     * Requires that child 0 is a VariableNode that evaluates to a double array
     * with one column and a row for each member of shrimpFractions.
     *
     * @param childrenET list containing child 0
     * @param shrimpFractions a list of shrimpFractions from the concentration
     * reference material
     * @param task
     * @return the double[1][1] array of Av_ParentElement_ConcenConst
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {

        Object[][] retVal;
        try {
            double[] xValues = transposeColumnVectorOfDoubles(childrenET.get(0).eval(shrimpFractions, task), 0);
            int counter = 0;
            double sumOfConcentrations = 0.0;
            double Av_ParentElement_ConcenConst = 0.0;
            for (int i = 0; i < xValues.length; i++) {
                double concentration = xValues[i];
                if (concentration > SquidConstants.SQUID_TINY_VALUE) {
                    sumOfConcentrations += concentration;
                    counter++;
                }
            }
            if (counter > 0) {
                Av_ParentElement_ConcenConst = sumOfConcentrations / counter;
            }

            retVal = new Object[][]{{Av_ParentElement_ConcenConst}};
        } catch (ArithmeticException | NullPointerException e) {
            retVal = new Object[][]{{0}};
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
