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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertArrayToObjects;

/**
 *
 * @author James F. Bowring
 */
public class SpotFieldNode extends ExpressionTree {

    private static final long serialVersionUID = 2173277234623108736L;

    private String fieldName;
    private String methodNameForShrimpFraction;

    /**
     * Used in unmarshalling
     */
    public SpotFieldNode() {
    }

    private SpotFieldNode(String fieldName, String methodNameForShrimpFraction) {
        this.fieldName = fieldName;
        this.name = fieldName;
        this.methodNameForShrimpFraction = methodNameForShrimpFraction;
        this.parentET = null;
    }

    public static SpotFieldNode buildSpotNode(String methodNameForShrimpFraction) {
        SpotFieldNode spotNode = new SpotFieldNode(methodNameForShrimpFraction.replace("get", ""), methodNameForShrimpFraction);
        return spotNode;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode(); 
    }

    @Override
    public boolean amHealthy() {
        return (methodNameForShrimpFraction.length() > 0);
    }

    @Override
    public boolean isValid() {
        return amHealthy();
    }

//    @Override
//    public boolean usesAnotherExpression(ExpressionTreeInterface exp) {
//        return false;
//    }

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
        xstream.registerConverter(new SpotFieldNodeNodeXMLConverter());
        xstream.alias("SpotFieldNode", SpotFieldNode.class);
    }

    /**
     * Returns an array of values from a column (name) of spots
     * (shrimpFractions) by using the specified lookup Method (a getter) that
     * takes an expression's name as argument = name of variable.
     *
     * @param shrimpFractions
     * @param task
     * @return object array
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    @Override
    public Object[][] eval(List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {
        Object[][] retVal = new Object[shrimpFractions.size()][];

        try {
            Method method = ShrimpFractionExpressionInterface.class.getMethod(//
                    methodNameForShrimpFraction,
                    new Class[]{});
            for (int i = 0; i < shrimpFractions.size(); i++) {
                double[] value = new double[]{(double) method.invoke(shrimpFractions.get(i), new Object[]{})};
                retVal[i] = convertArrayToObjects(value);
            }

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException | NullPointerException methodException) {
            throw new SquidException("Could not find variable " + fieldName);
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
    public String getMethodNameForShrimpFraction() {
        return methodNameForShrimpFraction;
    }

    /**
     * @param methodNameForShrimpFraction the methodNameForShrimpFraction to set
     */
    public void setMethodNameForShrimpFraction(String methodNameForShrimpFraction) {
        this.methodNameForShrimpFraction = methodNameForShrimpFraction;
    }

    @Override
    public boolean isRootExpressionTree() {
        return false;
    }

    @Override
    public String toStringMathML() {
        String retVal
                = "<mrow>"
                + "<mi>" + fieldName + "</mi>"
                + "</mrow>\n";

        return retVal;
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
