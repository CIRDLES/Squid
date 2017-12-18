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
package org.cirdles.squid.tasks.expressions.constants;

import com.thoughtworks.xstream.XStream;
import java.util.List;
import java.util.Objects;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
public class ConstantNode extends ExpressionTree {

    private static final long serialVersionUID = -2914393295564269277L;
    public static final String MISSING_EXPRESSION_STRING = "Missing Expression";

    private Object value;

    /**
     *
     */
    public ConstantNode() {
        this("", 0.0);
    }

    /**
     *
     * @param name
     * @param value
     */
    public ConstantNode(String name, Object value) {
        this.name = name;
        if (value instanceof Integer){
            value = ((int)value) * 1.0;
        }
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {

        boolean retVal = false;
        if (obj == null) {
            retVal = false;
        } else {
            if (this == obj) {
                retVal = true;
            } else if (obj.getClass().equals(this.getClass())) {
                retVal = (name.compareTo(((ConstantNode) obj).getName()) == 0);
                if (retVal) {
                    if (value instanceof Integer) {
                        retVal = (Integer.compare((int) value, (int) ((ConstantNode) obj).getValue()) == 0);
                    } else if (value instanceof Double) {
                        retVal = (Double.compare((double) value, (double) ((ConstantNode) obj).getValue()) == 0);
                    } else if (value instanceof String) {
                        retVal = (((String) value).compareToIgnoreCase(((String) ((ConstantNode) obj).getValue())) == 0);
                    } else if (value instanceof Boolean) {
                        retVal = (((Boolean) value).compareTo(((Boolean) ((ConstantNode) obj).getValue())) == 0);
                    }
                }
            }
        }
        return retVal;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public boolean amHealthy() {
        return name.compareTo(MISSING_EXPRESSION_STRING) != 0;
    }

    @Override
    public boolean isValid() {
        return (name.length() > 0) && (value != null) && amHealthy();
    }

    @Override
    public boolean usesAnotherExpression(ExpressionTreeInterface exp) {
        return false;
    }

    @Override
    public boolean usesOtherExpression() {
        return false;
    }

    /**
     *
     * @param xstream
     */
    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new ConstantNodeXMLConverter());
        xstream.alias("ConstantNode", ConstantNode.class);
    }

    /**
     *
     * @param shrimpFractions the value of shrimpFraction
     * @return the double[][]
     */
    @Override
    public Object[][] eval(List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) {
        return new Object[][]{{value}};
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     *
     * @return
     */
    @Override
    public String toStringMathML() {
        return "<mn>" + name + "</mn>\n";
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

    public boolean isMissingExpression() {
        return (name.compareTo(MISSING_EXPRESSION_STRING) == 0);
    }
}
