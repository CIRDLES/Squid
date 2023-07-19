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
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

import java.util.List;

/**
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class Stdev extends Function {

    private static final long serialVersionUID = -8052587481829150937L;

    /**
     * Provides the functionality of Excel's STDEV and returns "stdev" and
     * encoding the labels for each cell of the values array produced by eval.
     *
     * @see http://commons.apache.org/proper/commons-math/javadocs/api-3.6/org/apache/commons/math3/stat/descriptive/moment/StandardDeviation.html
     */
    public Stdev() {
        name = "stdev";
        argumentCount = 1;
        precedence = 4;
        rowCount = 1;
        colCount = 2;
        labelsForOutputValues = new String[][]{{"stdev"}};
        labelsForInputValues = new String[]{"per-spot expression"};
        summaryCalc = true;
        definition = "Calculates the standard deviation of a sample.";
    }

    /**
     * Requires that child 0 is a VariableNode that evaluates to a double array
     * with one column and a row for each member of shrimpFractions.
     *
     * @param childrenET      list containing child 0
     * @param shrimpFractions a list of shrimpFractions
     * @param task
     * @return the double[1][3] array of slope, slopeErr, y-Intercept, y-IntErr
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {

        Object[][] retVal;
        try {
            double[] xValues = transposeColumnVectorOfDoubles(childrenET.get(0).eval(shrimpFractions, task), 0);
            StandardDeviation stdDevSample = new StandardDeviation();
            double stdDev = stdDevSample.evaluate(xValues);

            retVal = new Object[][]{{stdDev}};
        } catch (ArithmeticException | MathIllegalArgumentException | NullPointerException e) {
            retVal = new Object[][]{{0.0}};
        }

        return retVal;
    }

    /**
     * @param childrenET the value of childrenET
     * @return
     */
    @Override
    public String toStringMathML(List<ExpressionTreeInterface> childrenET) {

        String retVal = "<mrow>" +
                "<mi>" + name + "</mi>" +
                "<mfenced>" +
                buildChildrenToMathML(childrenET) +
                "</mfenced></mrow>\n";

        return retVal;
    }
}