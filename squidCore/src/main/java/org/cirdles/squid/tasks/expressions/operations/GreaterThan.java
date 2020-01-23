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
package org.cirdles.squid.tasks.expressions.operations;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.List;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class GreaterThan extends Operation {

    /**
     *
     */
    public GreaterThan() {
        super();
        name = "greaterThan";
        argumentCount = 2;
        precedence = 1;
        rowCount = 1;
        colCount = 1;
        labelsForOutputValues = new String[][]{{"Predicate"}};
    }

    /**
     *
     * @param childrenET the value of childrenET
     * @param shrimpFractions the value of shrimpFraction
     * @param task
     * @return the double[][]
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) {

        boolean retVal;
        try {
                        Object term1Object = childrenET.get(0).eval(shrimpFractions, task)[0][0];
            Object term2Object = childrenET.get(1).eval(shrimpFractions, task)[0][0];

            double term1;
            double term2;

            if (term1Object instanceof Integer) {
                term1 = ((Integer) term1Object).doubleValue();
            } else {
                term1 = (double) term1Object;
            }

            if (term2Object instanceof Integer) {
                term2 = ((Integer) term2Object).doubleValue();
            } else {
                term2 = (double) term2Object;
            }
            retVal = term1 > term2;
        } catch (SquidException e) {
            retVal = false;
        }
        return new Object[][]{{retVal}};
    }

    /**
     *
     * @param childrenET the value of childrenET
     * @return
     */
    @Override
    public String toStringMathML(List<ExpressionTreeInterface> childrenET) {
        String retVal
                = "<mrow>\n"
                + toStringAnotherExpression(childrenET.get(0))
                + "<mo>></mo>\n"
                + toStringAnotherExpression(childrenET.get(1))
                + "</mrow>\n";

        return retVal;
    }

}
