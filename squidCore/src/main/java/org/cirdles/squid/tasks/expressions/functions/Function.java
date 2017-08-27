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
package org.cirdles.squid.tasks.expressions.functions;

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
public abstract class Function
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
    // establish size of array resulting from evaluation

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
    protected String[][] labelsForValues;

    /**
     *
     * @param xstream
     */
    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new FunctionXMLConverter());
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface ln() {
        return new Ln();
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface sqrt() {
        return new Sqrt();
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface exp() {
        return new Exp();
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface robReg() {
        return new RobReg();
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface sqBiweight() {
        return new SqBiweight();
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface agePb76() {
        return new AgePb76();
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface sqWtdAv() {
        return new SqWtdAv();
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface concordiaTW() {
        return new ConcordiaTW();
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface and() {
        return new And();
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface sqIf() {
        return new If();
    }

    /**
     *
     * @param operationName
     * @return
     */
    public static OperationOrFunctionInterface operationFactory(String operationName) {
        Function retVal = null;
        Method method;
        if (operationName != null) {
            try {
                method = Function.class.getMethod(//
                        operationName,
                        new Class[0]);
                retVal = (Function) method.invoke(null, new Object[0]);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException noSuchMethodException) {
                // do nothing for now
            }
        }
        return retVal;
    }

    /**
     *
     * @param columnVector
     * @param colIndex
     * @return
     */
    protected double[] transposeColumnVector(Object[][] columnVector, int colIndex) {
        double[] rowVector = new double[columnVector.length];
        for (int i = 0; i < rowVector.length; i++) {
            rowVector[i] = (double) columnVector[i][colIndex];
        }

        return rowVector;
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

    /**
     * @return the labelsForValues
     */
    public String[][] getLabelsForValues() {
        return labelsForValues;
    }

    /**
     * @param labelsForValues the labelsForValues to set
     */
    public void setLabelsForValues(String[][] labelsForValues) {
        this.labelsForValues = labelsForValues;
    }
}
