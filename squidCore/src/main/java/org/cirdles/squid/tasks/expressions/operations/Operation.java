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
package org.cirdles.squid.tasks.expressions.operations;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.cirdles.squid.tasks.expressions.OperationOrFunctionInterface;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

import static org.cirdles.squid.utilities.conversionUtilities.CloningUtilities.clone2dArray;

/**
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public abstract class Operation
        implements
        OperationOrFunctionInterface,
        Serializable,
        XMLSerializerInterface {

    /**
     *
     */
    public final static Map<String, String> OPERATIONS_MAP = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private static final long serialVersionUID = 7752181552732562245L;

    static {

        OPERATIONS_MAP.put("+", add().getName());
        OPERATIONS_MAP.put("-", subtract().getName());
        OPERATIONS_MAP.put("/", divide().getName());
        OPERATIONS_MAP.put("*", multiply().getName());
        OPERATIONS_MAP.put("^", pow().getName());
        OPERATIONS_MAP.put("==", equal().getName());
        OPERATIONS_MAP.put("<", lessThan().getName());
        OPERATIONS_MAP.put("<=", lessThanEqual().getName());
        OPERATIONS_MAP.put(">", greaterThan().getName());
        OPERATIONS_MAP.put(">=", greaterThanEqual().getName());
        OPERATIONS_MAP.put("$$", value().getName());
    }

    /**
     *
     */
    protected String name;
    // establish size of array resulting from evaluation
    /**
     *
     */
    protected int argumentCount;
    /**
     *
     */
    protected int precedence;
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
    protected String[] labelsForInputValues = new String[]{};
    protected String definition;
    protected boolean summaryCalc;

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
     * @return
     */
    public static OperationOrFunctionInterface add() {
        return new Add();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface subtract() {
        return new Subtract();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface divide() {
        return new Divide();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface multiply() {
        return new Multiply();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface pow() {
        return new Pow();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface pExp() {
        return new Pexp();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface value() {
        return new Value();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface equal() {
        return new Equal();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface lessThan() {
        return new LessThan();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface lessThanEqual() {
        return new LessThanEqual();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface greaterThan() {
        return new GreaterThan();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface greaterThanEqual() {
        return new GreaterThanEqual();
    }

    /**
     * @param operationName
     * @return
     */
    public static OperationOrFunctionInterface operationFactory(String operationName) {
        Operation retVal = null;
        Method method;
        if (operationName != null) {
            try {
                method = Operation.class.getMethod(//
                        operationName
                );
                retVal = (Operation) method.invoke(null, new Object[0]);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
                     InvocationTargetException noSuchMethodException) {
                // do nothing for now
            }
        }
        return retVal;
    }

    /**
     * @param xstream
     */
    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new OperationXMLConverter());
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
    @Override
    public int getRowCount() {
        return rowCount;
    }

    /**
     * @return the colCount
     */
    @Override
    public int getColCount() {
        return colCount;
    }

    /**
     * @return the labelsForOutputValues
     */
    @Override
    public String[][] getLabelsForOutputValues() {
        return clone2dArray(labelsForOutputValues);
    }

    @Override
    public String[] getLabelsForInputValues() {
        return labelsForInputValues.clone();
    }

    @Override
    public String getDefinition() {
        return definition;
    }

    /**
     * @return the summaryCalc
     */
    public boolean isSummaryCalc() {
        return summaryCalc;
    }


}