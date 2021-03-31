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
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

import java.util.List;

import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertArrayToObjects;

/**
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class SqWtdAv extends Function {

    private static final long serialVersionUID = 2338965097822849460L;

    /**
     * Provides the basic functionality of Squid2.5's sqWtdAv by calculating
     * WeightedAverage and returning mean, 1-sigmaAbs, err68, err95,
     * MSWD, probability, externalFlag and encoding the labels for each cell of the
     * values array produced by eval.
     *
     * @see https://github.com/CIRDLES/LudwigLibrary/blob/master/vbaCode/squid2.5Basic/MathUtils.bas
     * @see https://github.com/CIRDLES/LudwigLibrary/blob/master/vbaCode/isoplot3Basic/Means.bas
     */
    public SqWtdAv() {
        name = "WtdAv";
        argumentCount = 1;
        precedence = 4;
        rowCount = 1;
        colCount = 7;
        labelsForOutputValues = new String[][]{{"mean", "1-sigmaAbs", "err68", "err95", "MSWD", "probability", "externalFlag"}};
        labelsForInputValues = new String[]{"per-spot expression with values and uncertainties"};
        summaryCalc = true;
        definition = "Provides the basic functionality of Squid's sqWtdAv by calculating\n"
                + "   WeightedAverage and returning intMean, intSigmaMean, intErr68, intMeanErr95, \n"
                + "   MSWD, and probability. Input must be ValueModel containing expression and error";
    }

    /**
     * Requires that child 0 is a VariableNode that evaluates to a double array
     * with one column and a row for each member of shrimpFractions and that
     * child 1 is a ConstantNode that evaluates to an integer.
     *
     * @param childrenET      list containing child 0 and child 1
     * @param shrimpFractions a list of shrimpFractions
     * @param task
     * @return the double[1][6] array of intMean, intSigmaMean, MSWD,
     * probability, intErr68, intMeanErr95
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {

        Object[][] retVal;
        try {
            Object[][] valuesAndUncertainties = childrenET.get(0).eval(shrimpFractions, task);
            double[] variableValues = transposeColumnVectorOfDoubles(valuesAndUncertainties, 0);
            double[] uncertaintyValues = transposeColumnVectorOfDoubles(valuesAndUncertainties, 1);
            double[] weightedAverage = org.cirdles.ludwig.isoplot3.Means.weightedAverage(variableValues, uncertaintyValues, false, false)[0];
            retVal = new Object[][]{convertArrayToObjects(weightedAverage)};
        } catch (ArithmeticException | NullPointerException e) {
            retVal = new Object[][]{{0.0, 0.0, 0.0, 0.0, 0.0, 0.0}};
        }

        return retVal;
    }

    /**
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
