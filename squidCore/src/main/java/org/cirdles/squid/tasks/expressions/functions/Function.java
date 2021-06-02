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
package org.cirdles.squid.tasks.expressions.functions;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.cirdles.squid.tasks.expressions.OperationOrFunctionInterface;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import static org.cirdles.squid.utilities.conversionUtilities.CloningUtilities.clone2dArray;

/**
 * @author James F. Bowring
 */
@XStreamAlias("Operation")
public abstract class Function
        implements
        OperationOrFunctionInterface,
        Serializable,
        XMLSerializerInterface {

    /**
     *
     */
    public static final Map<String, String> FUNCTIONS_MAP = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    public static final Map<String, String> MATH_FUNCTIONS_MAP = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    public static final Map<String, String> SQUID_COMMMON_FUNCTIONS_MAP = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    public static final Map<String, String> SQUID_FUNCTIONS_MAP = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    // establish size of array resulting from evaluation
    public static final Map<String, String> LOGIC_FUNCTIONS_MAP = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    public static final Map<String, String> ALIASED_FUNCTIONS_MAP = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private static final long serialVersionUID = 5737437390502874465L;

    static {

        // key is case-insensitive name and value is case-sensitive method name
        SQUID_COMMMON_FUNCTIONS_MAP.put("AgePb76", "agePb76");
        SQUID_COMMMON_FUNCTIONS_MAP.put("AgePb76WithErr", "agePb76WithErr");
        SQUID_COMMMON_FUNCTIONS_MAP.put("Pb76", "pb76");
        SQUID_COMMMON_FUNCTIONS_MAP.put("Pb206U238rad", "pb206U238rad");
        SQUID_COMMMON_FUNCTIONS_MAP.put("ConcordiaTW", "concordiaTW");
        SQUID_COMMMON_FUNCTIONS_MAP.put("Concordia", "concordia");
        SQUID_COMMMON_FUNCTIONS_MAP.put("RobReg", "robReg");
        SQUID_COMMMON_FUNCTIONS_MAP.put("SqBiweight", "sqBiweight");
        SQUID_COMMMON_FUNCTIONS_MAP.put("Biweight", "sqBiweight");
        SQUID_COMMMON_FUNCTIONS_MAP.put("SqWtdAv", "sqWtdAv");
        SQUID_COMMMON_FUNCTIONS_MAP.put("WtdAv", "sqWtdAv");
        SQUID_COMMMON_FUNCTIONS_MAP.put("ValueModel", "valueModel");

        SQUID_FUNCTIONS_MAP.put("Age7corrWithErr", "age7corrWithErr");
        SQUID_FUNCTIONS_MAP.put("Age8corrWithErr", "age8corrWithErr");
        SQUID_FUNCTIONS_MAP.put("Age7CorrPb8Th2WithErr", "age7CorrPb8Th2WithErr");
        SQUID_FUNCTIONS_MAP.put("Rad8corPb7U5WithErr", "rad8corPb7U5WithErr");
        SQUID_FUNCTIONS_MAP.put("Rad8corConcRho", "rad8corConcRho");
        SQUID_FUNCTIONS_MAP.put("Pb46cor7", "pb46cor7");
        SQUID_FUNCTIONS_MAP.put("Pb46cor8", "pb46cor8");
        SQUID_FUNCTIONS_MAP.put("CalculateMeanConcStd", "calculateMeanConcStd");
        SQUID_FUNCTIONS_MAP.put("StdPb86radCor7per", "stdPb86radCor7per");
        SQUID_FUNCTIONS_MAP.put("Pb86radCor7per", "pb86radCor7per");
        SQUID_FUNCTIONS_MAP.put("TotalCps", "totalCps");
        SQUID_FUNCTIONS_MAP.put("TotalCpsTime", "totalCpsTime");
        SQUID_FUNCTIONS_MAP.put("WtdMeanACalc", "wtdMeanACalc");
        SQUID_FUNCTIONS_MAP.put("Orig", "orig");

        LOGIC_FUNCTIONS_MAP.put("and", "and");
        LOGIC_FUNCTIONS_MAP.put("or", "or");
        LOGIC_FUNCTIONS_MAP.put("if", "sqIf");
        LOGIC_FUNCTIONS_MAP.put("sqIf", "sqIf");
        LOGIC_FUNCTIONS_MAP.put("not", "not");

        MATH_FUNCTIONS_MAP.put("exp", "exp");
        MATH_FUNCTIONS_MAP.put("sqrt", "sqrt");
        MATH_FUNCTIONS_MAP.put("ln", "ln");
        MATH_FUNCTIONS_MAP.put("log", "log");
        MATH_FUNCTIONS_MAP.put("max", "max");
        MATH_FUNCTIONS_MAP.put("min", "min");
        MATH_FUNCTIONS_MAP.put("abs", "abs");
        MATH_FUNCTIONS_MAP.put("average", "average");
        MATH_FUNCTIONS_MAP.put("median", "median");
        MATH_FUNCTIONS_MAP.put("sum", "sum");
        MATH_FUNCTIONS_MAP.put("count", "count");
        MATH_FUNCTIONS_MAP.put("countif", "countif");
        MATH_FUNCTIONS_MAP.put("tinv", "tinv");
        MATH_FUNCTIONS_MAP.put("round", "round");
        MATH_FUNCTIONS_MAP.put("stdev", "stdev");
        MATH_FUNCTIONS_MAP.put("stdevp", "stdevp");

        FUNCTIONS_MAP.putAll(MATH_FUNCTIONS_MAP);
        FUNCTIONS_MAP.putAll(SQUID_COMMMON_FUNCTIONS_MAP);
        FUNCTIONS_MAP.putAll(SQUID_FUNCTIONS_MAP);
        FUNCTIONS_MAP.putAll(LOGIC_FUNCTIONS_MAP);

        ALIASED_FUNCTIONS_MAP.put("SqBiweight", "Biweight");
        ALIASED_FUNCTIONS_MAP.put("SqWtdAv", "WtdAv");
        ALIASED_FUNCTIONS_MAP.put("TotalCps", null);
        // July 2019 for 204 count corrections
        ALIASED_FUNCTIONS_MAP.put("TotalCpsTime", null);

    }

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

    public Function() {
        this.definition = "todo";
    }

    public static String replaceAliasedFunctionNamesInExpressionString(String excelExpressionString) {
        String retVal = excelExpressionString;
        for (Entry<String, String> entry : ALIASED_FUNCTIONS_MAP.entrySet()) {
            if (retVal != null && retVal.matches(".*(?i)" + entry.getKey() + "(.*)")) {
                // replace alias if not null
                if (entry.getValue() != null) {
                    retVal = retVal.replaceAll("(?i)" + entry.getKey(), entry.getValue());
                }
            }
        }

        return retVal;
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface ln() {
        return new Ln();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface log() {
        return new Log();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface sqrt() {
        return new Sqrt();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface exp() {
        return new Exp();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface robReg() {
        return new RobReg();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface sqBiweight() {
        return new SqBiweight();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface biweight() {
        return new SqBiweight();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface sqWtdAv() {
        return new SqWtdAv();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface wtdAv() {
        return new SqWtdAv();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface agePb76() {
        return new AgePb76();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface agePb76WithErr() {
        return new AgePb76WithErr();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface age7corrWithErr() {
        return new Age7corrWithErr();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface age8corrWithErr() {
        return new Age8corrWithErr();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface age7CorrPb8Th2WithErr() {
        return new Age7CorrPb8Th2WithErr();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface rad8corPb7U5WithErr() {
        return new Rad8corPb7U5WithErr();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface rad8corConcRho() {
        return new Rad8corConcRho();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface pb76() {
        return new Pb76();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface pb46cor7() {
        return new Pb46cor7();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface pb46cor8() {
        return new Pb46cor8();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface pb206U238rad() {
        return new Pb206U238rad();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface stdPb86radCor7per() {
        return new StdPb86radCor7per();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface pb86radCor7per() {
        return new Pb86radCor7per();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface concordiaTW() {
        return new ConcordiaTW();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface concordia() {
        return new Concordia();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface and() {
        return new And();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface or() {
        return new Or();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface sqIf() {
        return new If();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface not() {
        return new Not();
    }

    public static OperationOrFunctionInterface totalCps() {
        return new ShrimpSpeciesNodeFunction("getTotalCps");
    }

    public static OperationOrFunctionInterface totalCpsTime() {
        return new ShrimpSpeciesNodeFunction("getNscansTimesCountTimeSec");
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface max() {
        return new Max();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface min() {
        return new Min();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface abs() {
        return new Abs();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface average() {
        return new Average();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface median() {
        return new Median();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface sum() {
        return new Sum();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface count() {
        return new Count();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface tinv() {
        return new TInv();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface round() {
        return new Round();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface stdev() {
        return new Stdev();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface stdevp() {
        return new StdevP();
    }

    public static OperationOrFunctionInterface calculateMeanConcStd() {
        return new CalculateMeanConcStd();
    }

    public static OperationOrFunctionInterface wtdMeanACalc() {
        return new WtdMeanACalc();
    }

    public static OperationOrFunctionInterface orig() {
        return new Orig();
    }

    /**
     * @return
     */
    public static OperationOrFunctionInterface valueModel() {
        return new ValueModel();
    }

    /**
     * @param myOperationName
     * @return
     */
    public static OperationOrFunctionInterface operationFactory(String myOperationName) {
        Function retVal = null;
        String operationName = FUNCTIONS_MAP.get(myOperationName);
        Method method;
        if (operationName != null) {
            try {
                method = Function.class.getMethod(//
                        operationName
                );
                retVal = (Function) method.invoke(null, new Object[0]);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException noSuchMethodException) {
                //System.out.println(noSuchMethodException.getMessage());
            }
        }
        return retVal;
    }

    /**
     * @param xstream
     */
    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new FunctionXMLConverter());
    }

    /**
     * @param columnVector
     * @param colIndex
     * @return
     */
    protected double[] transposeColumnVectorOfDoubles(Object[][] columnVector, int colIndex) {
        double[] rowVector = new double[columnVector.length];

        try {
            for (int i = 0; i < rowVector.length; i++) {
                if (columnVector[i][colIndex] instanceof Integer) {
                    rowVector[i] = (double) (Integer) columnVector[i][colIndex];
                } else {
                    rowVector[i] = (double) columnVector[i][colIndex];
                }
            }
        } catch (ClassCastException | ArrayIndexOutOfBoundsException e) {
        }

        return rowVector;
    }

    /**
     * @return the name
     */
    @Override
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
    @Override
    public boolean isSummaryCalc() {
        return summaryCalc;
    }
}