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
package org.cirdles.squid.tasks.expressions.variables;

import com.thoughtworks.xstream.XStream;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static org.cirdles.squid.constants.Squid3Constants.ABS_UNCERTAINTY_DIRECTIVE;
import static org.cirdles.squid.constants.Squid3Constants.PCT_UNCERTAINTY_DIRECTIVE;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertArrayToObjects;
import static org.cirdles.squid.utilities.conversionUtilities.RoundingUtilities.squid3RoundedToSize;

/**
 * @author James F. Bowring
 */
public class VariableNodeForIsotopicRatios extends VariableNodeForSummary {

    public final static String LOOKUP_METHODNAME_FOR_SHRIMPFRACTION = "getIsotopicRatioValuesByStringName";
    private static final long serialVersionUID = -2296889996415038672L;
    private final static String LOOKUP_METHODNAME_FOR_SHRIMPFRACTION_ORIG_VALUE = "getOriginalIsotopicRatioValuesByStringName";
    private static String LOOKUP_METHODNAME_FOR_SHRIMPFRACTION_CHOICE = LOOKUP_METHODNAME_FOR_SHRIMPFRACTION;
    private ShrimpSpeciesNode numerator;
    private ShrimpSpeciesNode denominator;

    /**
     *
     */
    public VariableNodeForIsotopicRatios() {
        this(null);
    }

    /**
     * @param name
     */
    public VariableNodeForIsotopicRatios(String name) {
        this(name, null, null);
    }

    public VariableNodeForIsotopicRatios(String name, ShrimpSpeciesNode numerator, ShrimpSpeciesNode denominator) {
        this(name, numerator, denominator, "");
    }

    public VariableNodeForIsotopicRatios(String name, ShrimpSpeciesNode numerator, ShrimpSpeciesNode denominator, String uncertaintyDirective) {
        this.name = name;
        this.numerator = numerator;
        this.denominator = denominator;
        this.uncertaintyDirective = uncertaintyDirective;
        this.index = 0;
        if (uncertaintyDirective.length() > 0) {
            this.index = 1;
        }
    }

    public static void switchToOrigValue() {
        LOOKUP_METHODNAME_FOR_SHRIMPFRACTION_CHOICE = LOOKUP_METHODNAME_FOR_SHRIMPFRACTION_ORIG_VALUE;
    }

    public static void switchToUsedValue() {
        LOOKUP_METHODNAME_FOR_SHRIMPFRACTION_CHOICE = LOOKUP_METHODNAME_FOR_SHRIMPFRACTION;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.numerator);
        hash = 31 * hash + Objects.hashCode(this.denominator);
        return hash;
    }

    /**
     * @param xstream
     */
    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new VariableNodeForSummaryXMLConverter());
        xstream.alias("VariableNodeForIsotopicRatios", VariableNodeForIsotopicRatios.class);
    }

    /**
     * Returns an array of values from a column (name) of spots
     * (shrimpFractions) by using the specified lookup Method of
     * ShrimpFractionExpressionInterface
     *
     * @param shrimpFractions
     * @param task
     * @return
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    @Override
    public Object[][] eval(List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {
        Object[][] retVal = new Object[shrimpFractions.size()][];

        try {
            Method method = ShrimpFractionExpressionInterface.class.getMethod(//
                    LOOKUP_METHODNAME_FOR_SHRIMPFRACTION_CHOICE,
                    new Class[]{String.class});
            for (int i = 0; i < shrimpFractions.size(); i++) {
                double[] values = ((double[][]) method.invoke(shrimpFractions.get(i), new Object[]{name}))[0].clone();
                if (values.length > 1) {
                    // to return uncertainty, copy index 1 to index 0
                    // July 2018 force to 12 sig digs - abs value should be already
                    if (uncertaintyDirective.compareTo(PCT_UNCERTAINTY_DIRECTIVE) == 0) {
                        values[0] = squid3RoundedToSize(values[1] / values[0] * 100, 12);
                    } else if (uncertaintyDirective.compareTo(ABS_UNCERTAINTY_DIRECTIVE) == 0) {
                        values[0] = squid3RoundedToSize(values[1], 12);
                    }
                } else {
                    // return 0 for uncertainty if none exists
                    if (uncertaintyDirective.length() > 0) {
                        values[0] = 0.0;
                    }
                }
                retVal[i] = convertArrayToObjects(values);
            }

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException |
                 NullPointerException methodException) {
        }

        return retVal;
    }

    /**
     * @return
     */
    @Override
    public String toStringMathML() {
        String retVal
                = "<mfrac>\n"
                + "<mrow>\n"
                + numerator.toStringMathML()
                + "\n</mrow>\n"
                + "<mrow>\n"
                + denominator.toStringMathML()
                + "\n</mrow>\n"
                + "</mfrac>\n";

        if (uncertaintyDirective.length() > 0) {
            retVal = "<mrow>\n<msup>\n<mfenced>\n"
                    + retVal
                    + "</mfenced>\n"
                    + "<mtext>" + uncertaintyDirective + "</mtext>\n"
                    + "</msup>\n</mrow>\n";
        }

        return retVal;
    }
}