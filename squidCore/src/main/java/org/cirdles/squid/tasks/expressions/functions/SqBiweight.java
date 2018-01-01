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
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToDoubles;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertArrayToObjects;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class SqBiweight extends Function {

    private static final long serialVersionUID = -1534611458354369530L;

    /**
     * Provides the functionality of Squid's sqBiweight and biWt by calculating
     * TukeysBiweight and returning mean, sigma, and 95% confidence and encoding
     * the labels for each cell of the values array produced by eval.
     *
     * @see
     * https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/squid2.5Basic/Resistant.bas
     * @see
     * https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/squid2.5Basic/StringUtils.bas
     */
    public SqBiweight() {
        name = "sqBiweight";
        argumentCount = 2;
        precedence = 4;
        rowCount = 1;
        colCount = 3;
        labelsForOutputValues = new String[][]{{"Biwt Mean", "Biwt Sigma", "\u00B195%conf"}};
    }

    /**
     * Requires that child 0 is a VariableNode that evaluates to a double array
     * with one column and a row for each member of shrimpFractions and that
     * child 1 is a ConstantNode that evaluates to an integer.
     *
     * @param childrenET list containing child 0 and child 1
     * @param shrimpFractions a list of shrimpFractions
     * @param task
     * @return the double[1][3] array of mean, sigma, 95% confidence
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {

        Object[][] retVal = new Object[][]{{0.0, 0.0, 0.0}};
        // set healthy flag
        childrenET.get(0).eval(shrimpFractions, task);
        if (childrenET.get(0).amHealthy()) {
            try {
                double[] variableValues = transposeColumnVector(childrenET.get(0).eval(shrimpFractions, task), 0);
                double[] tuning = convertObjectArrayToDoubles(childrenET.get(1).eval(shrimpFractions, task)[0]);
                double[] tukeysBiweight = org.cirdles.ludwig.squid25.SquidMathUtils.tukeysBiweight(variableValues, tuning[0]);
                retVal = new Object[][]{convertArrayToObjects(tukeysBiweight)};
            } catch (ArithmeticException | NullPointerException e) {
            }
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

        for (int i = 0; i < childrenET.size(); i++) {
            retVal += toStringAnotherExpression(childrenET.get(i)) + "&nbsp;\n";
        }

        retVal += "</mfenced></mrow>\n";

        return retVal;
    }

}
