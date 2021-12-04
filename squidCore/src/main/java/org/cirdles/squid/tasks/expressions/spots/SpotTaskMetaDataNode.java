/*
 * Copyright 2017 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.tasks.expressions.spots;

import com.thoughtworks.xstream.XStream;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author James F. Bowring
 */
public class SpotTaskMetaDataNode extends ExpressionTree {

    //private static final long serialVersionUID = 2173277234623108736L;

    private String fieldName;
    private String methodNameForTask;

    /**
     * Used in unmarshalling
     */
    public SpotTaskMetaDataNode() {
    }

    private SpotTaskMetaDataNode(String fieldName, String methodNameForTask) {
        this.fieldName = fieldName;
        this.name = fieldName;
        this.methodNameForTask = methodNameForTask;
        this.parentET = null;
        this.squidSwitchSTReferenceMaterialCalculation = true;
        this.squidSwitchSAUnknownCalculation = false;
    }

    /**
     *
     * @param methodNameForShrimpFraction
     * @return SpotFieldNode using getter name with leading get removed as name
     */
    public static SpotTaskMetaDataNode buildTaskMetaDataNode(String methodNameForTask) {
        return new SpotTaskMetaDataNode(methodNameForTask.replaceFirst("get", ""), methodNameForTask);
    }

    @Override
    public boolean amHealthy() {
        return (methodNameForTask.length() > 0);
    }

    @Override
    public boolean isValid() {
        return amHealthy();
    }

    @Override
    public boolean usesOtherExpression() {
        return false;
    }

    /**
     * @param xstream
     */
    @Override
    public void customizeXstream(XStream xstream) {
//        xstream.registerConverter(new SpotFieldNodeNodeXMLConverter());
//        xstream.alias("SpotFieldNode", SpotTaskMetaDataNode.class);
    }

    /**
     * Returns an array of values from a column (name) of spots
     * (shrimpFractions) by using the specified lookup Method (a getter) that
     * takes an expression's name as argument = name of variable.
     *
     * @param shrimpFractions
     * @param task
     * @return object array
     * @throws SquidException
     */
    @Override
    public Object[][] eval(List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {
        Object[][] retVal = new Object[shrimpFractions.size()][];

        try {
            Method method = TaskInterface.class.getMethod(//
                    methodNameForTask
            );
            for (int i = 0; i < shrimpFractions.size(); i++) {
                // this generalization handles various types
                retVal[i] = new Object[]{method.invoke(task)};
            }

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException | NullPointerException methodException) {
            throw new SquidException("Could not find Task field name: " + fieldName);
        }

        return retVal;
    }

    @Override
    public String getName() {
        return fieldName;
    }

    @Override
    public void setName(String name) {
        fieldName = name;
    }

    /**
     * @return the methodNameForShrimpFraction
     */
    public String getMethodNameForTask() {
        return methodNameForTask;
    }

    /**
     * @param methodNameForTask the methodNameForShrimpFraction to set
     */
    public void setMethodNameForTask(String methodNameForTask) {
        this.methodNameForTask = methodNameForTask;
    }

    @Override
    public boolean isRootExpressionTree() {
        return false;
    }

    @Override
    public String toStringMathML() {
        return "<mrow>"
                + "<mi>Task " + fieldName + "</mi>"
                + "</mrow>\n";
    }

    @Override
    public boolean isTypeFunction() {
        return false;
    }

    @Override
    public boolean isTypeFunctionOrOperation() {
        return false;
    }
}