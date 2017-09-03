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
package org.cirdles.squid.tasks.expressions.variables;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertArrayToObjects;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;

/**
 *
 * @author James F. Bowring
 */
public class VariableNodeForIsotopicRatios extends VariableNodeForSummary implements ExpressionTreeInterface, XMLSerializerInterface {

    private static String lookupMethodNameForShrimpFraction = "getIsotopicRatioValuesByStringName";

    /**
     *
     */
    public VariableNodeForIsotopicRatios() {
        this(null);
    }

    /**
     *
     * @param name
     */
    public VariableNodeForIsotopicRatios(String name) {
        this.name = name;
    }

    /**
     * Returns an array of values from a column (name) of spots
     * (shrimpFractions) by using the specified lookup Method of
     * ShrimpFractionExpressionInterface
     *
     * @param shrimpFractions
     * @param task
     * @return
     */
    @Override
    public Object[][] eval(List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {
        Object[][] retVal = new Object[shrimpFractions.size()][];

        try {
            Method method = ShrimpFractionExpressionInterface.class.getMethod(//
                    lookupMethodNameForShrimpFraction,
                    new Class[]{String.class});
            for (int i = 0; i < shrimpFractions.size(); i++) {
                double[] values = ((double[][]) method.invoke(shrimpFractions.get(i), new Object[]{name}))[0];
                retVal[i] = convertArrayToObjects(values);
            }

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException methodException) {
        }

        return retVal;
    }
}
