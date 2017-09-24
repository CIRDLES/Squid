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
import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.OperationOrFunctionInterface;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public abstract class Function
        implements
        OperationOrFunctionInterface,
        Serializable,
        XMLSerializerInterface {

    private static final long serialVersionUID = 5737437390502874465L;

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
    protected String[][] labelsForOutputValues = new String[][]{{}};
    /**
     *
     */
    public static final Map<String, String> FUNCTIONS_MAP = new HashMap<>();

    static {

        FUNCTIONS_MAP.put("agePb76", "agePb76");
        FUNCTIONS_MAP.put("AgePb76", "agePb76");

        FUNCTIONS_MAP.put("and", "and");
        FUNCTIONS_MAP.put("And", "and");

        FUNCTIONS_MAP.put("concordiaTW", "concordiaTW");
        FUNCTIONS_MAP.put("ConcordiaTW", "concordiaTW");

        FUNCTIONS_MAP.put("exp", "exp");
        FUNCTIONS_MAP.put("Exp", "exp");

        FUNCTIONS_MAP.put("if", "sqIf");
        FUNCTIONS_MAP.put("If", "sqIf");

        FUNCTIONS_MAP.put("ln", "ln");
        FUNCTIONS_MAP.put("Ln", "ln");

        FUNCTIONS_MAP.put("robReg", "robReg");
        FUNCTIONS_MAP.put("RobReg", "robReg");
        FUNCTIONS_MAP.put("robreg", "robReg");

        FUNCTIONS_MAP.put("sqBiweight", "sqBiweight");
        FUNCTIONS_MAP.put("SqBiweight", "sqBiweight");

        FUNCTIONS_MAP.put("sqWtdAv", "sqWtdAv");
        FUNCTIONS_MAP.put("SqWtdAv", "sqWtdAv");

        FUNCTIONS_MAP.put("sqrt", "sqrt");
        FUNCTIONS_MAP.put("Sqrt", "sqrt");

        FUNCTIONS_MAP.put("TotalCps", "totalCps");
        FUNCTIONS_MAP.put("totalCps", "totalCps");

        FUNCTIONS_MAP.put("lookup", "lookup");

        FUNCTIONS_MAP.put("max", "max");

        FUNCTIONS_MAP.put("abs", "abs");
        FUNCTIONS_MAP.put("xxx", "abs");
    }

    public Function() {

    }

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

    public static OperationOrFunctionInterface totalCps() {
        return new ShrimpSpeciesNodeFunction("getTotalCps");
    }

    public static OperationOrFunctionInterface lookup() {
        return new SpotNodeLookupFunction();
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface max() {
        return new Max();
    }

    /**
     *
     * @return
     */
    public static OperationOrFunctionInterface abs() {
        return new Abs();
    }
    
       /**
     *
     * @return
     */
    public static OperationOrFunctionInterface xxx() {
        return new Abs();
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
                System.out.println(noSuchMethodException.getMessage());
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
     * @return the argumentCount
     */
    @Override
    public int getArgumentCount() {
        return argumentCount;
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
     * @return the colCount
     */
    public int getColCount() {
        return colCount;
    }

    /**
     * @return the labelsForOutputValues
     */
    public String[][] getLabelsForOutputValues() {
        return labelsForOutputValues;
    }
}
