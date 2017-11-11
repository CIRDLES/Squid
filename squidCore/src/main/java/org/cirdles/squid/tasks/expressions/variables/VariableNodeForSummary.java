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
import java.util.List;
import java.util.Map;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertArrayToObjects;

/**
 *
 * @author James F. Bowring
 */
public class VariableNodeForSummary extends ExpressionTree {

    private static final long serialVersionUID = -868256637199178058L;
    
    private int index;

    /**
     *
     */
    public VariableNodeForSummary() {
        this(null);
    }

    /**
     *
     * @param name
     */
    public VariableNodeForSummary(String name) {
        this(name, 0);
    }

    public VariableNodeForSummary(String name, int index) {
        this.name = name;
        this.index = index;
    }
    
    

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public boolean amHealthy() {
        return (name.length() > 0);
    }

    @Override
    public boolean isValid() {
        return (name.length() > 0);
    }

    @Override
    public boolean usesAnotherExpression(ExpressionTreeInterface exp) {
        return false;
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
        xstream.alias("VariableNodeForSummary", VariableNodeForSummary.class);
    }

    /**
     * Returns an array of values from a summary expression that has been
     * evaluated on a set of spots and stored in the task by using the specified
     * lookup Method getTaskExpressionsEvaluationsPerSpotSet on the Task object
     * that takes an expression's name as argument = name of variable.
     *
     * @param shrimpFractions
     * @param task
     * @return
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    @Override
    public Object[][] eval(List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {

        Map<String, SpotSummaryDetails> detailsMap = task.getTaskExpressionsEvaluationsPerSpotSet();
        SpotSummaryDetails detail = detailsMap.get(name);
        double[][] valuesAll = detail.getValues();
        double[][] values = valuesAll;
        if (index > 0){
            // we have a call to retrieve into [0][0] another output of this expression, such as 1-sigmaabs
            values = new double[1][valuesAll[0].length - index];
            for (int i = index; i < valuesAll[0].length; i ++){
                values[0][i - index] = valuesAll[0][i];
            }
        }
        Object[][] retVal = convertArrayToObjects(values);

        return retVal;
    }

    /**
     *
     * @return
     */
    @Override
    public String toStringMathML() {
        String retVal
                = "<mtext>\n"
                + name +  "[" + index + "]" 
                + "</mtext>\n";

        return retVal;
    }

    @Override
    public boolean isRootExpressionTree() {
        return false;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isTypeFunction() {
        return false;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isTypeFunctionOrOperation() {
        return false;
    }

    /**
     *
     * @return
     */
    @Override
    public int argumentCount() {
        return 0;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }
}
