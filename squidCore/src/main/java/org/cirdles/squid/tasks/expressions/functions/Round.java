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

import static org.cirdles.squid.utilities.conversionUtilities.RoundingUtilities.squid3RoundedToSize;

/**
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class Round extends Function {

    private static final long serialVersionUID = -5584187007895831370L;

    /**
     *
     */
    public Round() {
        super();
        name = "round";
        argumentCount = 2;
        precedence = 2;
        rowCount = 1;
        colCount = 1;
        labelsForOutputValues = new String[][]{{"rounded value"}};
        labelsForInputValues = new String[]{"value", "sigDigCount"};
        definition = "rounds value to sigDigCount significant digits using half-up mode.";
    }

    /**
     * Round expects double and integer
     *
     * @param childrenET      the value of childrenET
     * @param shrimpFractions the value of shrimpFraction
     * @param task
     * @return the double[][]
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) {

        double retVal;
        try {
            Object term1Object = childrenET.get(0).eval(shrimpFractions, task)[0][0];
            Object term2Object = childrenET.get(1).eval(shrimpFractions, task)[0][0];

            double number;
            int sigDigits;

            if (term1Object instanceof Integer) {
                number = ((Integer) term1Object).doubleValue();
            } else {
                number = (double) term1Object;
            }

            if (term2Object instanceof Double) {
                sigDigits = (int) StrictMath.floor(((Double) term2Object).doubleValue());
            } else {
                sigDigits = (Integer) term2Object;
            }

            retVal = squid3RoundedToSize(number, sigDigits);
        } catch (NullPointerException | SquidException e) {
            retVal = 0.0;
        }
        return new Object[][]{{retVal}};
    }

    /**
     * @param rightET    the value of rightET
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
