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
package org.cirdles.squid.tasks.expressions.operations;

import com.thoughtworks.xstream.XStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.cirdles.squid.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.OperationOrFunctionInterface;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;

/**
 *
 * @author James F. Bowring
 */
public abstract class Operation
        implements
        OperationOrFunctionInterface,
        XMLSerializerInterface {

    /**
     *
     */
    protected String name;

    /**
     *
     */
    protected int argumentCount;

    /**
     *
     */
    protected int precedence;
    // establish size of array resullting from evaluation

    /**
     *
     */
    protected int rowCount;

    /**
     *
     */
    protected int colCount;

    /**
     *
     */
    public Operation() {
        this.name = "no-op";
        this.argumentCount = 1;
        this.precedence = 1;
        this.rowCount = 1;
        this.colCount = 1;
    }

    /**
     *
     * @param xstream
     */
    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new OperationXMLConverter());
        xstream.alias("operation", Operation.class);
        xstream.alias("operation", this.getClass());
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface add() {
        return new Add();
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface subtract() {
        return new Subtract();
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface divide() {
        return new Divide();
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface multiply() {
        return new Multiply();
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface pow() {
        return new Pow();
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface pExp() {
        return new Pexp();
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface equal() {
        return new Equal();
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface lessThan() {
        return new LessThan();
    }

    /**
     *
     * @param operationName
     * @return
     */
    public static OperationOrFunctionInterface operationFactory(String operationName) {
        Operation retVal = null;
        Method method;
        if (operationName != null) {
            try {
                method = Operation.class.getMethod(//
                        operationName,
                        new Class[0]);
                retVal = (Operation) method.invoke(null, new Object[0]);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException noSuchMethodException) {
                // do nothing for now
            }
        }
        return retVal;
    }

    /**
     *
     * @param expression
     * @return
     */
    protected String toStringAnotherExpression(ExpressionTreeInterface expression) {

        String retVal = "<mtext>\nNot a valid expression</mtext>\n";

        if (expression != null) {
            retVal = expression.toStringMathML();
        }

        return retVal;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the argumentCount
     */
    @Override
    public int getArgumentCount() {
        return argumentCount;
    }

    /**
     * @param argumentCount the argumentCount to set
     */
    @Override
    public void setArgumentCount(int argumentCount) {
        this.argumentCount = argumentCount;
    }

    /**
     * @return the precedence
     */
    @Override
    public int getPrecedence() {
        return precedence;
    }

    /**
     * @return the rowCount
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * @param rowCount the rowCount to set
     */
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * @return the colCount
     */
    public int getColCount() {
        return colCount;
    }

    /**
     * @param colCount the colCount to set
     */
    public void setColCount(int colCount) {
        this.colCount = colCount;
    }
}
