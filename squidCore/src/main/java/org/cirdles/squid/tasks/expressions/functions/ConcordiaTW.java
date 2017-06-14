/* 
 * Copyright 2006-2017 CIRDLES.org.
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

import java.util.List;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.ExpressionTreeInterface;
import static org.cirdles.squid.tasks.expressions.ExpressionTreeInterface.convertObjectArrayToDoubles;
import static org.cirdles.squid.tasks.expressions.ExpressionTreeInterface.convertArrayToObjects;

/**
 *
 * @author James F. Bowring
 */
public class ConcordiaTW extends Function {

    /**
     * Provides the functionality of Squid's agePb76 by calling pbPbAge and
 returning "Age" and "AgeErr" and encoding the labels for each cell of the
 values array produced by eval.
     *
     * @see
     * https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/isoplot3Basic/Pub.bas
     * @see
     * https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/isoplot3Basic/UPb.bas
     */
    public ConcordiaTW() {
        name = "concordiaTW";
        argumentCount = 2;
        precedence = 4;
        rowCount = 1;
        colCount = 4;
        labelsForValues = new String[][]{{"Raw Conc Age", "1-sigma abs", "MSWD Conc", "Prob Conc"}};
    }

    /**
     * Requires that child 0 and 1 each is VariableNode that evaluates to a
     * double array with one column representing an IsotopicRatio and a row for
     * each member of shrimpFractions.
     *
     * @param childrenET list containing child 0 and 1
     * @param shrimpFractions a list of shrimpFractions
     * @param task
     * @return the double[1][4]{Raw Conc Age, 1-sigma abs, MSWD Conc, Prob Conc}
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) {

        Object[][] retVal;
        try {
            double[] ratioXAndUnct = convertObjectArrayToDoubles(childrenET.get(0).eval(shrimpFractions, task)[0]);
            double[] ratioYAndUnct = convertObjectArrayToDoubles(childrenET.get(1).eval(shrimpFractions, task)[0]);
            double[] concordiaTW
                    = org.cirdles.ludwig.isoplot3.Pub.concordiaTW(1.0 / ratioXAndUnct[0], ratioXAndUnct[1], ratioYAndUnct[0], ratioYAndUnct[1]);
            retVal = new Object[][]{convertArrayToObjects( concordiaTW)};
        } catch (ArithmeticException e) {
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
                + "<mi>AgePb76</mi>"
                + "<mfenced>";

        for (int i = 0; i < childrenET.size(); i++) {
            retVal += toStringAnotherExpression(childrenET.get(i)) + "&nbsp;\n";
        }

        retVal += "</mfenced></mrow>\n";

        return retVal;
    }
}
