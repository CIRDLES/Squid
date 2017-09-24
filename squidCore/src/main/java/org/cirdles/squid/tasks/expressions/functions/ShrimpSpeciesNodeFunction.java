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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.List;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class ShrimpSpeciesNodeFunction extends Function {

    private static final long serialVersionUID = -7371492154923797766L;

    private String methodNameForShrimpFraction;
    private ShrimpSpeciesNode shrimpSpeciesNode;

    public ShrimpSpeciesNodeFunction(String methodNameForShrimpFraction) {
        name = methodNameForShrimpFraction.replace("get", "");
        argumentCount = 1;
        precedence = 4;
        rowCount = 1;
        colCount = 1;
        this.methodNameForShrimpFraction = methodNameForShrimpFraction;
        labelsForOutputValues = new String[][]{{"Calculated Field: " + name}};
    }

    /**
     * Only child is a ShrimpSpeciesNode containing a SquidSpeciesModel.
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
        shrimpSpeciesNode = ((ShrimpSpeciesNode) childrenET.get(0));
        shrimpSpeciesNode.setMethodNameForShrimpFraction(methodNameForShrimpFraction);

        Object[][] results = shrimpSpeciesNode.eval(shrimpFractions, task);
        // restore the node to anonymous
        shrimpSpeciesNode.setMethodNameForShrimpFraction("");
        return results;
    }

    @Override
    public String toStringMathML(List<ExpressionTreeInterface> childrenET) {
        shrimpSpeciesNode = ((ShrimpSpeciesNode) childrenET.get(0));
        shrimpSpeciesNode.setMethodNameForShrimpFraction(methodNameForShrimpFraction);

        String retVal
                = "<mrow>"
                + "<mi>" + name + "</mi>"
                + "<mfenced>"
                + shrimpSpeciesNode.toStringMathML()
                + "</mfenced></mrow>\n";

        // restore the node to anonymous
        shrimpSpeciesNode.setMethodNameForShrimpFraction("");

        return retVal;
    }

    /**
     * @return the methodNameForShrimpFraction
     */
    public String getMethodNameForShrimpFraction() {
        return methodNameForShrimpFraction;
    }

}
