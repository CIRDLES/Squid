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
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForIsotopicRatios;

import java.util.List;

/**
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class Orig extends Function {

    /**
     *
     */
    public Orig() {
        name = "orig";
        argumentCount = 1;
        precedence = 4;
        rowCount = 1;
        colCount = 1;
        labelsForOutputValues = new String[][]{{"Orig Value"}};
        definition = "Orig is designed to provide the original interpolated value of a ratio of interest\n "
                + DEF_TAB + "when column swapping or overcount corrections have been applied.";
    }

    /**
     * Returns childrenET(0) - no changes
     *
     * @param childrenET      the value of childrenET
     * @param shrimpFractions the value of shrimpFraction
     * @param task
     * @return the double[][]
     */
    @Override
    public Object[][] eval(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) {

        Object[][] retVal;
        try {
            if (childrenET.get(0) instanceof VariableNodeForIsotopicRatios) {
                VariableNodeForIsotopicRatios.switchToOrigValue();
            }
            retVal = childrenET.get(0).eval(shrimpFractions, task);
            VariableNodeForIsotopicRatios.switchToUsedValue();
        } catch (Exception e) {
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
                = "<mrow>\n"
                + toStringAnotherExpression(childrenET.get(0))
                + "</mrow>\n";

        return retVal;
    }

}