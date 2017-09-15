/*
 * Copyright 2017 CIRDLES.org.
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
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.spots.SpotNode;

/**
 *
 * @author James F. Bowring
 */
public class SpotNodeLookupFunction extends Function {

    private String methodNameForShrimpFraction;
    private SpotNode spotNode;

    public SpotNodeLookupFunction() {
        name = "Lookup";
        argumentCount = 1;
        precedence = 4;
        rowCount = 1;
        colCount = 1;
    }

    /**
     * Only child is a SpotNode specifying the lookup method for a shrimpfraction (spot).
     *
     * @param childrenET
     * @param shrimpFractions
     * @param task
     * @return
     * @throws SquidException
     */
    @Override
    public Object[][] eval(List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {
        //TODO refactor duplicate code
        spotNode = ((SpotNode) childrenET.get(0));
        methodNameForShrimpFraction = spotNode.getMethodNameForShrimpFraction();
        labelsForOutputValues = new String[][]{{"Lookup Field: " + methodNameForShrimpFraction.replace("get", "")}};

        Object[][] results = spotNode.eval(shrimpFractions, task);

        return results;
    }

    @Override
    public String toStringMathML(List<ExpressionTreeInterface> childrenET) {
        spotNode = ((SpotNode) childrenET.get(0));
        methodNameForShrimpFraction = spotNode.getMethodNameForShrimpFraction();
        labelsForOutputValues = new String[][]{{"Lookup Field: " + methodNameForShrimpFraction.replace("get", "")}};


        String retVal
                = "<mrow>"
                + "<mi>" + name + "</mi>"
                + "<mfenced>"
                + spotNode.toStringMathML()
                + "</mfenced></mrow>\n";

        return retVal;
    }

    /**
     * @return the methodNameForShrimpFraction
     */
    public String getMethodNameForShrimpFraction() {
        return methodNameForShrimpFraction;
    }

}
