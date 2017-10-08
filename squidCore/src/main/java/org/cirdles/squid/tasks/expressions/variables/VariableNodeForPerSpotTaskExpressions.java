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

import com.thoughtworks.xstream.XStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertArrayToObjects;

/**
 *
 * @author James F. Bowring
 */
public class VariableNodeForPerSpotTaskExpressions extends VariableNodeForSummary {

    private static final long serialVersionUID = -7828719973319583270L;

    private static String lookupMethodNameForShrimpFraction = "getTaskExpressionsEvaluationsPerSpotByField";

    private boolean healthy;

    /**
     *
     */
    public VariableNodeForPerSpotTaskExpressions() {
        this(null);
    }

    /**
     *
     * @param name
     */
    public VariableNodeForPerSpotTaskExpressions(String name) {
        this.name = name;
        this.healthy = true;
    }

    @Override
    public boolean amHealthy() {
        return (name.length() > 0) && healthy;
    }

    @Override
    public boolean isValid() {
        return (name.length() > 0);
    }

    /**
     *
     * @param xstream
     */
    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new VariableNodeForSummaryXMLConverter());
        xstream.alias("VariableNodeForPerSpotTaskExpressions", VariableNodeForPerSpotTaskExpressions.class);
    }

    /**
     * Returns an array of values from a column (name) of spots
     * (shrimpFractions) by using the specified lookup Method
     * getTaskExpressionsEvaluationsPerSpotByField that takes an expression's
     * name as argument = name of variable.
     *
     * @param shrimpFractions
     * @param task
     * @return
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    @Override
    public Object[][] eval(List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {
        Object[][] retVal = new Object[shrimpFractions.size()][];
        if ((task.getNamedExpressionsMap().get(name) != null)
                && task.getNamedExpressionsMap().get(name).amHealthy()) {
            healthy = true;
            try {
                Method method = ShrimpFractionExpressionInterface.class.getMethod(//
                        lookupMethodNameForShrimpFraction,
                        new Class[]{String.class});
                for (int i = 0; i < shrimpFractions.size(); i++) {
                    double[] values = ((double[][]) method.invoke(shrimpFractions.get(i), new Object[]{name}))[0];
                    retVal[i] = convertArrayToObjects(values);
                }

            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException | NullPointerException methodException) {
                throw new SquidException("Could not find variable " + name);
            }
        } else {
            healthy = false;
        }

        return retVal;
    }
}
