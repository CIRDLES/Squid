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

import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToBooleans;

/**
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class Or extends Function {

    private static final long serialVersionUID = -5740761443384313262L;

    /**
     *
     */
    public Or() {
        name = "OR";
        argumentCount = 2;
        precedence = 4;
        rowCount = 1;
        colCount = 1;
        labelsForOutputValues = new String[][]{{"Disjunction"}};
        labelsForInputValues = new String[]{"condition1", "condition2"};
        definition = "Boolean OR: returns TRUE if either argument is TRUE.";
    }

    /**
     * Or expects two boolean children.
     *
     * @param childrenET      the value of childrenET
     * @param shrimpFractions the value of shrimpFraction
     * @param task
     * @return the Object[1][1] containing boolean
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) {

        boolean retVal;
        try {
            retVal = (convertObjectArrayToBooleans(childrenET.get(0).eval(shrimpFractions, task)[0])[0])
                    || (convertObjectArrayToBooleans(childrenET.get(1).eval(shrimpFractions, task)[0])[0]);
        } catch (SquidException squidException) {
            retVal = false;
        }

        return new Object[][]{{retVal}};
    }

    /**
     * @param childrenET the value of childrenET
     * @return
     */
    @Override
    public String toStringMathML(List<ExpressionTreeInterface> childrenET) {

        String retVal = "<mrow>" +
                "<mi>" + name + "</mi>" +
                "<mfenced>" +
                buildChildrenToMathML(childrenET) +
                "</mfenced></mrow>\n";

        return retVal;
    }

}