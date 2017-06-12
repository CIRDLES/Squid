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
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.SpotSummaryDetails;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;
import static org.cirdles.squid.tasks.expressions.ExpressionTreeInterface.convertArrayToObjects;

/**
 *
 * @author James F. Bowring
 */
public class VariableNodeForSummary implements ExpressionTreeInterface, XMLSerializerInterface {

    private String name;
    private ExpressionTreeInterface parentET;

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
        this.name = name;
    }

    /**
     *
     * @param xstream
     */
    @Override
    public void customizeXstream(XStream xstream) {
//        xstream.registerConverter(new ShrimpSpeciesNodeXMLConverter());
//        xstream.alias("ShrimpSpeciesNode", VariableNode.class);
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
     */
    @Override
    public Object[][] eval(List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) {

        Map<String, SpotSummaryDetails> detailsMap = task.getTaskExpressionsEvaluationsPerSpotSet();
        SpotSummaryDetails detail = detailsMap.get(name);
        double[][] values = detail.getValues();
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
                + name
                + "</mtext>\n";

        return retVal;
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isRootExpressionTree() {
        return false;
    }

    /**
     * @return the parentET
     */
    @Override
    public ExpressionTreeInterface getParentET() {
        return parentET;
    }

    /**
     * @param parentET the parentET to set
     */
    @Override
    public void setParentET(ExpressionTreeInterface parentET) {
        this.parentET = parentET;
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
}
