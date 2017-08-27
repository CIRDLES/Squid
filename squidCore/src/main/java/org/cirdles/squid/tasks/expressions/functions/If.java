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
import static org.cirdles.squid.tasks.expressions.ExpressionTreeInterface.convertObjectArrayToBooleans;

/**
 *
 * @author James F. Bowring
 */
public class If extends Function {

    /**
     *
     */
    public If() {
        name = "sqIf";
        argumentCount = 3;
        precedence = 4;
        rowCount = 1;
        colCount = 1;
    }

    /**
     * If expects child 1 as boolean and child 1 and 2 as double
     * @param childrenET the value of childrenET
     * @param shrimpFractions the value of shrimpFraction
     * @param task
     * @return the Object[][] containing doubles
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) {

        Object[][] retVal;
        try {     
            if (convertObjectArrayToBooleans(childrenET.get(0).eval(shrimpFractions, task)[0])[0]){
                retVal = childrenET.get(1).eval(shrimpFractions, task);
            } else {
                 retVal = childrenET.get(2).eval(shrimpFractions, task);
            }           
        } catch (Exception e) {
            retVal = new Object[][]{{}};
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
                + "<mi>if</mi>"
                + "<mfenced>";

        for (int i = 0; i < childrenET.size(); i++) {
            retVal += toStringAnotherExpression(childrenET.get(i)) + "&nbsp;\n";
        }

        retVal += "</mfenced></mrow>\n";

        return retVal;
    }

}
