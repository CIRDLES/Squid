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
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeBuilderInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public class Divide extends Operation {

    /**
     *
     */
    public Divide() {
        name = "divide";
        argumentCount = 2;
        precedence = 3;
        rowCount = 1;
        colCount = 1;
        labelsForOutputValues = new String[][]{{"Quotient"}};
        definition = "Floating point divide.  Returns zero if division by zero.";
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

        double retVal;

        try {
            Object numeratorObject = childrenET.get(0).eval(shrimpFractions, task)[0][0];
            Object denominatorObject = childrenET.get(1).eval(shrimpFractions, task)[0][0];

            double numerator;
            double denominator;

            if (numeratorObject instanceof Integer) {
                numerator = ((Integer) numeratorObject).doubleValue();
            } else {
                numerator = (double) numeratorObject;
            }
            
            if (denominatorObject instanceof Integer) {
                denominator = ((Integer) denominatorObject).doubleValue();
            } else {
                denominator = (double) denominatorObject;
            }

            retVal = numerator / denominator;
        } catch (NullPointerException | SquidException e) {
            retVal = 0.0;
        }

        // April 2017 constrain quotient to mimic VBA results for isotopic ratios
        // by providing only 12 significant digits per Simon Bodorkos
        if (childrenET.get(0) instanceof ShrimpSpeciesNode) {
            retVal = org.cirdles.ludwig.squid25.Utilities.roundedToSize(retVal, 12);
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
        boolean leftChildHasLowerPrecedence = false;
        try {
            leftChildHasLowerPrecedence = precedence > ((ExpressionTreeBuilderInterface) childrenET.get(0)).getOperationPrecedence();
        } catch (Exception e) {
        }
        boolean rightChildHasLowerPrecedence = false;
        try {
            rightChildHasLowerPrecedence = precedence > ((ExpressionTreeBuilderInterface) childrenET.get(1)).getOperationPrecedence();
        } catch (Exception e) {
        }

        String retVal
                = "<mfrac>\n"
                + "<mrow>\n"
                + (leftChildHasLowerPrecedence ? "<mo>(</mo>\n" : "")
                + toStringAnotherExpression(childrenET.get(0))
                + (leftChildHasLowerPrecedence ? "<mo>)</mo>\n" : "")
                + "\n</mrow>\n"
                + "<mrow>\n"
                + (rightChildHasLowerPrecedence ? "<mo>(</mo>\n" : "")
                + toStringAnotherExpression(childrenET.get(1))
                + (rightChildHasLowerPrecedence ? "<mo>)</mo>\n" : "")
                + "\n</mrow>\n"
                + "</mfrac>\n";

        return retVal;
    }

}
