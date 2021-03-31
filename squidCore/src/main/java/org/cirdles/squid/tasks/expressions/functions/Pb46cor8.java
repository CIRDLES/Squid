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

import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA232;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA238;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToDoubles;

/**
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class Pb46cor8 extends Function {

    private static final long serialVersionUID = 6770836871819373387L;

    /**
     * Provides the functionality of Squid2.5's pb46cor8. Returns 204Pb/206Pb
     * required to force 206Pb/238U-208Pb/232Th ages to concordance.
     *
     * @see https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/src/main/java/org/cirdles/ludwig/squid25/PbUTh_2.java
     */
    public Pb46cor8() {

        name = "Pb46cor8";
        argumentCount = 3;
        precedence = 4;
        rowCount = 1;
        colCount = 1;
        labelsForOutputValues = new String[][]{{"pb46cor8"}};
        labelsForInputValues = new String[]{"208/206RatioAnd1\u03C3 abs", "232Th/238U", "208corr206Pb/238UAge"};
    }

    /**
     * Requires that child 0 is a VariableNode that evaluates to a double array
     * with one column representing the 208/206 IsotopicRatio and a row for each
     * member of shrimpFractions. Requires that child 1 is a VariableNode that
     * evaluates to a double array with one column representing ["232Th/238U"]
     * and a row for each member of shrimpFractions. Requires that child 2 is a
     * VariableNode that evaluates to a double array with one column
     * representing ["208corr206Pb/238UAge"] and a row for each member of
     * shrimpFractions.
     *
     * @param childrenET      list containing child 0 - 2
     * @param shrimpFractions a list of shrimpFractions
     * @param task
     * @return the double[1][1] array of pb46cor8
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {

        Object[][] retVal;
        try {
            double[] pb208_206RatioAndUnct = convertObjectArrayToDoubles(childrenET.get(0).eval(shrimpFractions, task)[0]);
            double[] thU = convertObjectArrayToDoubles(childrenET.get(1).eval(shrimpFractions, task)[0]);
            double[] pb208corr206_238Age = convertObjectArrayToDoubles(childrenET.get(2).eval(shrimpFractions, task)[0]);

//            double sComm_64 = task.getTaskExpressionsEvaluationsPerSpotSet().get(DEFCOM_64).getValues()[0][0];
            double sComm_64 = shrimpFractions.get(0).getCom_206Pb204Pb();
//            double sComm_84 = task.getTaskExpressionsEvaluationsPerSpotSet().get(DEFCOM_84).getValues()[0][0];
            double sComm_84 = shrimpFractions.get(0).getCom_208Pb204Pb();
            double lambda232 = task.getTaskExpressionsEvaluationsPerSpotSet().get(LAMBDA232).getValues()[0][0];
            double lambda238 = task.getTaskExpressionsEvaluationsPerSpotSet().get(LAMBDA238).getValues()[0][0];

            double[] pb46cor8 = org.cirdles.ludwig.squid25.PbUTh_2.pb46cor8(
                    pb208_206RatioAndUnct[0],
                    thU[0],
                    sComm_64,
                    sComm_84,
                    pb208corr206_238Age[0], lambda232, lambda238);
            retVal = new Object[][]{{pb46cor8[0]}};
        } catch (ArithmeticException | IndexOutOfBoundsException | NullPointerException e) {
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
        String retVal
                = "<mrow>"
                + "<mi>" + name + "</mi>"
                + "<mfenced>";

        retVal += buildChildrenToMathML(childrenET);

        retVal += "</mfenced></mrow>\n";

        return retVal;
    }

}
