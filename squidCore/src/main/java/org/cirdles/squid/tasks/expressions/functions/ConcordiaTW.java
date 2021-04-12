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

import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.*;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertArrayToObjects;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToDoubles;

/**
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class ConcordiaTW extends Function {

    private static final long serialVersionUID = -1637184737851510733L;

    /**
     * Provides the functionality of Squid2.5's agePb76 by calling pbPbAge and
     * returning "Age" and "AgeErr" and encoding the labels for each cell of the
     * values array produced by eval.
     *
     * @see https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/isoplot3Basic/Pub.bas
     * @see https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/isoplot3Basic/UPb.bas
     */
    public ConcordiaTW() {
        name = "ConcordiaTW";
        argumentCount = 2;
        precedence = 4;
        rowCount = 1;
        colCount = 4;
        labelsForOutputValues = new String[][]{{"Raw Conc Age", "1\u03C3 abs", "MSWD Conc", "Prob Conc"}};
        labelsForInputValues = new String[]{"ratioX with 1\u03C3 abs", "ratioY with 1\u03C3 abs"};
    }

    /**
     * Requires that child 0 and 1 each is VariableNode that evaluates to a
     * double array with one column representing an IsotopicRatio and a row for
     * each member of shrimpFractions.
     *
     * @param childrenET      list containing child 0 and 1
     * @param shrimpFractions a list of shrimpFractions
     * @param task
     * @return the double[1][4]{Raw Conc Age, 1-sigma abs, MSWD Conc, Prob Conc}
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {

        Object[][] retVal;
        try {
            double[] ratioXAndUnct = convertObjectArrayToDoubles(childrenET.get(0).eval(shrimpFractions, task)[0]);
            double[] ratioYAndUnct = convertObjectArrayToDoubles(childrenET.get(1).eval(shrimpFractions, task)[0]);

            double present238U235U = task.getTaskExpressionsEvaluationsPerSpotSet().get(REF_238U235U).getValues()[0][0];
            double lambda235 = task.getTaskExpressionsEvaluationsPerSpotSet().get(LAMBDA235).getValues()[0][0];
            double lambda238 = task.getTaskExpressionsEvaluationsPerSpotSet().get(LAMBDA238).getValues()[0][0];

            double[] concordiaTW
                    = org.cirdles.ludwig.isoplot3.Pub.concordiaTW(ratioXAndUnct[0],
                    ratioXAndUnct[1], ratioYAndUnct[0], ratioYAndUnct[1], lambda235, lambda238, present238U235U);
            retVal = new Object[][]{convertArrayToObjects(concordiaTW)};
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
