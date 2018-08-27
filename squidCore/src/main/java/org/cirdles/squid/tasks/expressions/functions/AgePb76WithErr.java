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
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA_235_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA_238_NAME;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToDoubles;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PRESENT_R238_235S_NAME;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class AgePb76WithErr extends Function {

    private static final long serialVersionUID = 1356960783530914047L;

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
    public AgePb76WithErr() {

        name = "AgePb76WithErr";
        argumentCount = 2;
        precedence = 4;
        rowCount = 1;
        colCount = 2;
        labelsForOutputValues = new String[][]{{"Age", "1SigmaUnct"}};
        labelsForInputValues = new String[]{"207/206 Ratio, 207/206 1SigmaUnct"};
    }

    /**
     *
     * Requires that child 0 is a VariableNode that evaluates to a double array
     * with column 1 representing the 207/206 IsotopicRatio and that child 1
     * evaluates to a double array with column 1 containing the 1sigma abs unct
     * with a row for each member of shrimpFractions.
     *
     * @param childrenET list containing child 0
     * @param shrimpFractions a list of shrimpFractions
     * @param task
     * @return the double[1][2] array of age, ageErr
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {

        Object[][] retVal;

        try {
            double[] pb207_206Ratio = convertObjectArrayToDoubles(childrenET.get(0).eval(shrimpFractions, task)[0]);
            double[] pb207_206Unct1Sigma = convertObjectArrayToDoubles(childrenET.get(1).eval(shrimpFractions, task)[0]);

            double lambda235 = (Double) ((ConstantNode) task.getNamedParametersMap().get(LAMBDA_235_NAME)).getValue();
            double lambda238 = (Double) ((ConstantNode) task.getNamedParametersMap().get(LAMBDA_238_NAME)).getValue();
            double PRESENT_R238_235S = (Double) ((ConstantNode) task.getNamedParametersMap().get(PRESENT_R238_235S_NAME)).getValue();

            double[] agePb76 = org.cirdles.ludwig.isoplot3.UPb.pbPbAge(pb207_206Ratio[0],
                    pb207_206Unct1Sigma[0],
                    lambda235, lambda238, PRESENT_R238_235S);
            retVal = new Object[][]{{agePb76[0], agePb76[1]}};
        } catch (ArithmeticException | IndexOutOfBoundsException | NullPointerException e) {
            retVal = new Object[][]{{0.0, 0.0}};
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

        StringBuilder retVal = new StringBuilder();
        retVal.append("<mrow>");
        retVal.append("<mi>").append(name).append("</mi>");
        retVal.append("<mfenced>");
        for (int i = 0; i < childrenET.size(); i++) {
            retVal.append(toStringAnotherExpression(childrenET.get(i))).append("&nbsp;\n");
        }

        retVal.append("</mfenced></mrow>\n");

        return retVal.toString();
    }

}
