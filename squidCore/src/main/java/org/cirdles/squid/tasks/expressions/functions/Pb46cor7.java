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
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToDoubles;

/**
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class Pb46cor7 extends Function {

    private static final long serialVersionUID = 8731067364281915559L;

    /**
     * Provides the functionality of Squid2.5's pb46cor7. Returns 204Pb/206Pb
     * required to force 206Pb/238U-207Pb/206Pb ages to concordance.
     *
     * @see https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/src/main/java/org/cirdles/ludwig/squid25/PbUTh_2.java
     */
    public Pb46cor7() {

        name = "Pb46cor7";
        argumentCount = 2;
        precedence = 4;
        rowCount = 1;
        colCount = 1;
        labelsForOutputValues = new String[][]{{"pb46cor7"}};
        labelsForInputValues = new String[]{"207/206Ratio with 1\u03C3 abs", "per-spot expression for 207corr206Pb/238UAge"};
    }

    /**
     * Requires that child 0 is a VariableNode that evaluates to a double array
     * with one column representing the 207/206 IsotopicRatio and a row for each
     * member of shrimpFractions. Requires that child 1 is a VariableNode that
     * evaluates to a double array with one column representing the
     * 207corr206Pb/238UAge and a row for each member of shrimpFractions.
     *
     * @param childrenET      list containing child 0 and 1
     * @param shrimpFractions a list of shrimpFractions
     * @param task
     * @return the double[1][1] array of pb46cor7
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {

        Object[][] retVal;
        try {
            double[] pb207_206RatioAndUnct = convertObjectArrayToDoubles(childrenET.get(0).eval(shrimpFractions, task)[0]);
            double[] pb207corr206_238Age = convertObjectArrayToDoubles(childrenET.get(1).eval(shrimpFractions, task)[0]);

            //double sComm_64 = task.getTaskExpressionsEvaluationsPerSpotSet().get(DEFCOM_64).getValues()[0][0];
            double sComm_64 = shrimpFractions.get(0).getCom_206Pb204Pb();
//            double sComm_74 = task.getTaskExpressionsEvaluationsPerSpotSet().get(DEFCOM_74).getValues()[0][0];
            double sComm_74 = shrimpFractions.get(0).getCom_207Pb204Pb();

            double present238U235U = task.getTaskExpressionsEvaluationsPerSpotSet().get(REF_238U235U).getValues()[0][0];
            double lambda235 = task.getTaskExpressionsEvaluationsPerSpotSet().get(LAMBDA235).getValues()[0][0];
            double lambda238 = task.getTaskExpressionsEvaluationsPerSpotSet().get(LAMBDA238).getValues()[0][0];

            double[] pb46cor7 = org.cirdles.ludwig.squid25.PbUTh_2.pb46cor7(
                    pb207_206RatioAndUnct[0],
                    sComm_64,
                    sComm_74,
                    pb207corr206_238Age[0],
                    lambda235, lambda238, present238U235U);
            retVal = new Object[][]{{pb46cor7[0]}};
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
