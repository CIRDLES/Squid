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

/**
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class RobReg extends Function {

    private static final long serialVersionUID = -198041668841495965L;

    /**
     * Provides the functionality of Squid2.5's robReg by calling robustReg2 and
     * returning "Slope", "SlopeErr", "Y-Intercept", "Y-IntErr" and encoding the
     * labels for each cell of the values array produced by eval.
     *
     * @see https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/squid2.5Basic/Resistant.bas
     */
    public RobReg() {
        name = "RobReg";
        argumentCount = 2;
        precedence = 4;
        rowCount = 1;
        colCount = 4;
        labelsForOutputValues = new String[][]{{"Slope", "SlopeErr", "Y-Intercept", "Y-IntErr"}};
        labelsForInputValues = new String[]{"per-spot expression for 'X'", "per-spot expression for 'Y'"};
        summaryCalc = true;
        definition = "Provides the functionality of Squid's robReg by calling robustReg2 and\n"
                + "   returning \"Slope\", \"SlopeErr\", \"Y-Intercept\", \"Y-IntErr\".";
    }

    /**
     * Requires that child 0 and child 1 are each a VariableNode that evaluates
     * to a double array with one column and a row for each member of
     * shrimpFractions. Child 2 and child 3 are each a BooleanNode that
     * evaluates to true or false. Child 2 and 3 are currently ignored but exist
     * for compatibility with Squid2.5.
     *
     * @param childrenET      list containing child 0 through 3
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
            double[] yValues = transposeColumnVectorOfDoubles(childrenET.get(1).eval(shrimpFractions, task), 0);
            double[] robustReg2 = org.cirdles.ludwig.isoplot3.Pub.robustReg2(xValues, yValues);
            double slopeErr = StrictMath.abs(robustReg2[2] - robustReg2[1]) / 2.0;
            double yIntErr = StrictMath.abs(robustReg2[6] - robustReg2[5]) / 2.0;
            retVal = new Object[][]{{robustReg2[0], slopeErr, robustReg2[3], yIntErr}};
        } catch (ArithmeticException | NullPointerException e) {
            retVal = new Object[][]{{0.0, 0.0, 0.0, 0.0}};
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
