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
        this(name, "");
    }

    /**
     *
     * @param name
     * @param uncertaintyDirective
     */
    public VariableNodeForPerSpotTaskExpressions(String name, String uncertaintyDirective) {
        this(name, uncertaintyDirective, 0);
    }

    public VariableNodeForPerSpotTaskExpressions(String name, String uncertaintyDirective, int index) {
        this.name = name;
        this.uncertaintyDirective = uncertaintyDirective;
        this.healthy = true;
        this.index = index;
        if (uncertaintyDirective.length() > 0) {
            this.index = 1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (this.healthy ? 1 : 0);
        return hash;
    }

    @Override
    public boolean amHealthy() {
        return (name.length() > 0) && healthy;
    }

    @Override
    public boolean isValid() {
        return (name.length() > 0);
    }

    @Override
    public boolean usesOtherExpression() {
        return true;
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
                    double[] values = ((double[][]) method.invoke(shrimpFractions.get(i), new Object[]{name}))[0].clone();
                    if (values.length > 1) {

                        if (uncertaintyDirective.compareTo("%") == 0) {
                            // index should be 1 from constructor
                            values[1] = values[1] / values[0] * 100;
                        }

                        // july 2018
                        if (task.expressionIsNuSwitched(name)) {
                            values[1] = org.cirdles.ludwig.squid25.Utilities.roundedToSize(values[1], 12);
                        }

                        if (index > 0) {
                            // we have a call to retrieve into [0] another output of this expression, such as 1-sigma abs
                            for (int j = index; j < values.length; j++) {
                                values[j - index] = values[j];
                            }
                        }
                    } else {
                        // return 0 for uncertainty if none exists
                        if (uncertaintyDirective.length() > 0) {
                            values[0] = 0.0;
                        }
                    }

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
