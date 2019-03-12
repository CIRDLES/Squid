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
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToBooleans;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToDoubles;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class ValueModel extends Function {

    private static final long serialVersionUID = -5439896482911219791L;

    /**
     * This wrapper operation combines a value and its absolute uncertainty
     * under a common name to support Squid3 Expressions of the form [%"xxx"]
     * and [±"xxx"]
     */
    public ValueModel() {
        super();
        name = "ValueModel";
        argumentCount = 3;
        precedence = 10;
        rowCount = 1;
        colCount = 2;
        labelsForInputValues = new String[]{"value", "1\u03C3 uncertainty", "absolute = true, percent = false"};
        labelsForOutputValues = new String[][]{{"Value", "1\u03C3 abs"}};
        definition = "ValueModel creates an expression encapsulating \n"
                + DEF_TAB + "a calculated value and its 1-sigma absolute uncertainty \n"
                + DEF_TAB + "as the first two arguments and a boolean flag (true or false) \n"
                + DEF_TAB + "signalling whether the provided uncertainty is absolute (true) \n"
                + DEF_TAB + "or per cent (false) as the thrid argument.";
    }

    /**
     *
     * @param childrenET the value of childrenET where child 0 is the value,
     * child 1 is the 1 sigma uncertainty and child 3 is true for ABS and false
     * for PCT uncertainty.
     * @param shrimpFractions the value of shrimpFraction
     * @param task
     * @return the double[][]
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {

        Object[][] retVal;
        try {
            double[] valueCalc = convertObjectArrayToDoubles(childrenET.get(0).eval(shrimpFractions, task)[0]);
            double[] unctCalc = convertObjectArrayToDoubles(childrenET.get(1).eval(shrimpFractions, task)[0]);
            boolean[] unctFlag = convertObjectArrayToBooleans(childrenET.get(2).eval(shrimpFractions, task)[0]);
            double unct = unctCalc[0];
            if (!unctFlag[0]) {
                // convert to absolute uncertainty
                unct = unctCalc[0] / 100.0 * valueCalc[0];
            }
            retVal = new Object[][]{{valueCalc[0], unct}};
        } catch (ArithmeticException | IndexOutOfBoundsException | NullPointerException e) {
            retVal = new Object[][]{{0.0, 0.0}};
        }
        return retVal;
    }

    /**
     *
     * @param rightET the value of rightET
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
