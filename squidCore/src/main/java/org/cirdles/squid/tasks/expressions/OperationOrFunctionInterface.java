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
package org.cirdles.squid.tasks.expressions;

import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import java.util.List;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;

/**
 *
 * @author James F. Bowring
 */
public interface OperationOrFunctionInterface {

    public static final String DEF_TAB = "           ";
    
    /**
     *
     * @param childrenET the value of childrenET
     * @param shrimpFractions the value of shrimpFraction
     * @param task
     * @return the double[][]
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    public abstract Object[][] eval(
            List<ExpressionTreeInterface> childrenET,
            List<ShrimpFractionExpressionInterface> shrimpFractions,
            TaskInterface task)
            throws SquidException;

    /**
     * @return the precedence
     */
    public int getPrecedence();

    /**
     * @return the argumentCount
     */
    public int getArgumentCount();

    /**
     *
     * @param childrenET the value of childrenET
     * @return
     */
    public abstract String toStringMathML(
            List<ExpressionTreeInterface> childrenET);

    /**
     * @return the name
     */
    public String getName();

    /**
     * @return the labelsForOutputValues
     */
    public String[][] getLabelsForOutputValues();

    /**
     * @return the labelsForInputValues
     */
    public String[] getLabelsForInputValues();

    public default String printOutputValues() {
        String retVal = "None Specified";
        String[] outputArray = getLabelsForOutputValues()[0];

        if (outputArray.length > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(" [");

            for (int i = 0; i < outputArray.length; i++) {
                builder.append(outputArray[i]).append((String) (i < (outputArray.length - 1) ? ", " : ""));
            }
            builder.append("]");
            retVal = builder.toString();
        }

        return retVal;
    }

    public default String printInputValues() {
        String retVal = "None Specified";
        String[] inputArray = getLabelsForInputValues();

        if (inputArray.length > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(" [");

            for (int i = 0; i < inputArray.length; i++) {
                builder.append(inputArray[i]).append((String) (i < (inputArray.length - 1) ? ", " : ""));
            }
            builder.append("]");
            retVal = builder.toString();
        }

        return retVal;
    }

    /**
     * @return the rowCount
     */
    public int getRowCount();

    /**
     * @return the colCount
     */
    public int getColCount();

    /**
     * Determines is this function/operation is "NU-switched" if
     * ratiosOfInterest are present
     *
     * @return
     */
    public default boolean isScalarResult() {
        return (getRowCount() == 1) && (getColCount() == 1);
    }
    
    public String getDefinition();
}
