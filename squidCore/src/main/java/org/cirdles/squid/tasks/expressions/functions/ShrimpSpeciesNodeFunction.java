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
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;

/**
 *
 * @author James F. Bowring
 */
public class ShrimpSpeciesNodeFunction extends Function {

    private String methodNameForShrimpFraction;
    private ShrimpSpeciesNode shrimpSpeciesNode;

    public ShrimpSpeciesNodeFunction(String methodNameForShrimpFraction) {
        name = "squidSpeciesModel";
        argumentCount = 1;
        precedence = 4;
        rowCount = 1;
        colCount = 1;
        this.methodNameForShrimpFraction = methodNameForShrimpFraction;
        labelsForOutputValues = new String[][]{{"Calculated Field: " + methodNameForShrimpFraction}};
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
        
        return shrimpSpeciesNode.eval(shrimpFractions, task);
//        
//        
//        double retVal = 0.0;
//        squidSpeciesModel = ((ShrimpSpeciesNode) childrenET.get(0)).getSquidSpeciesModel();
//        Integer index = squidSpeciesModel.getMassStationIndex();
//        if (index != -1) {
//            double[] isotopeValues
//                    = methodFactory(shrimpFractions.get(0), methodNameForShrimpFraction);
//            if (index < isotopeValues.length) {
//                retVal = isotopeValues[index];
//            }
//        }
//
//        return new Object[][]{{retVal}};
    }

    @Override
    public String toStringMathML(List<ExpressionTreeInterface> childrenET) {
        shrimpSpeciesNode = ((ShrimpSpeciesNode) childrenET.get(0));
        shrimpSpeciesNode.setMethodNameForShrimpFraction(methodNameForShrimpFraction);
        return shrimpSpeciesNode.toStringMathML();

//        String retVal
//                = "<msubsup>\n"
//                + "<mstyle mathsize='90%'>\n"
//                + "<mtext>\n"
//                + squidSpeciesModel.getIsotopeName()
//                + "\n</mtext>\n"
//                + "</mstyle>\n"
//                + "<mstyle  mathsize='150%'>\n"
//                + "<mtext>\n"
//                + squidSpeciesModel.getElementName()
//                + "\n</mtext>\n"
//                + "</mstyle>\n"
//                + "</msubsup>\n";
//
//        return retVal;
    }

    /**
     * @return the methodNameForShrimpFraction
     */
    public String getMethodNameForShrimpFraction() {
        return methodNameForShrimpFraction;
    }

}
