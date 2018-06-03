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
public class Concordia extends Function {

    private static final long serialVersionUID = 4127169946137305310L;

    /**
     * Provides the functionality of Squid's agePb76 by calling pbPbAge and
     * returning "Age" and "AgeErr" and encoding the labels for each cell of the
     * values array produced by eval.
     *
     * @see
     * https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/isoplot3Basic/Pub.bas
     * @see
     * https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/isoplot3Basic/UPb.bas
     */
    public Concordia() {
        name = "concordia";
        argumentCount = 3;
        precedence = 4;
        rowCount = 1;
        colCount = 4;
        labelsForOutputValues = new String[][]{{"Age", "1-sigma abs", "MSWD", "Prob Of MSWD"}};
        labelsForInputValues = new String[]{"ratioXAndUnct","ratioYAndUnct","rho"};
    }

    /**
     * Requires that child 0 and 1 each is VariableNode that evaluates to a
     * double array with one column representing an IsotopicRatio and a row for
     * each member of shrimpFractions. child 2 evaluates to a double
     * representing rho
     *
     * @param childrenET list containing child 0 and 1
     * @param shrimpFractions a list of shrimpFractions
     * @param task
     * @return the double[1][4]{"Age", "1-sigma abs", "MSWD", "Prob Of MSWD"}
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {

        Object[][] retVal;
        try {
            double[] ratioXAndUnct = convertObjectArrayToDoubles(childrenET.get(0).eval(shrimpFractions, task)[0]);
            double[] ratioYAndUnct = convertObjectArrayToDoubles(childrenET.get(1).eval(shrimpFractions, task)[0]);
            double[] rho = convertObjectArrayToDoubles(childrenET.get(2).eval(shrimpFractions, task)[0]);
            double[] concordia
                    = org.cirdles.ludwig.isoplot3.Pub.concordia(ratioXAndUnct[0], ratioXAndUnct[1], ratioYAndUnct[0], ratioYAndUnct[1], rho[0]);
            retVal = new Object[][]{convertArrayToObjects(concordia)};
        } catch (ArithmeticException | NullPointerException e) {
            retVal = new Object[][]{{0.0, 0.0, 0.0, 0.0}};
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
