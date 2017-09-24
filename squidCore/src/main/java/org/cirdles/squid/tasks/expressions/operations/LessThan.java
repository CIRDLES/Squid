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
package org.cirdles.squid.tasks.expressions.operations;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.List;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class LessThan extends Operation {

    /**
     *
     */
    public LessThan() {
        super();
        name = "lessThan";
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
            retVal = (double)childrenET.get(0).eval(shrimpFractions, task)[0][0]
                    < (double)childrenET.get(1).eval(shrimpFractions, task)[0][0];
        } catch (Exception e) {
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
                + "<mo>&#60;</mo>\n"
                + toStringAnotherExpression(childrenET.get(1))
                + "</mrow>\n";

        return retVal;
    }

}
