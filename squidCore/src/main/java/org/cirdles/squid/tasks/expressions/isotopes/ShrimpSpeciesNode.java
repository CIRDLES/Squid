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
package org.cirdles.squid.tasks.expressions.isotopes;

import com.thoughtworks.xstream.XStream;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.shrimp.SquidSpeciesModelXMLConverter;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.functions.ShrimpSpeciesNodeFunction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertArrayToObjects;

/**
 * @author James F. Bowring
 */
public class ShrimpSpeciesNode extends ExpressionTree {

    private static final long serialVersionUID = 3592579554999155473L;

    private String isotopeName;
    private SquidSpeciesModel squidSpeciesModel;
    private String methodNameForShrimpFraction;

    /**
     *
     */
    private ShrimpSpeciesNode() {
    }

    /**
     * @param squidSpeciesModel
     * @param methodNameForShrimpFraction
     */
    private ShrimpSpeciesNode(SquidSpeciesModel squidSpeciesModel, String methodNameForShrimpFraction) {
        this.squidSpeciesModel = squidSpeciesModel;
        this.methodNameForShrimpFraction = methodNameForShrimpFraction;
        this.isotopeName = squidSpeciesModel.getIsotopeName();
        this.squidSwitchSTReferenceMaterialCalculation = true;
        this.squidSwitchSAUnknownCalculation = true;
    }

    @Override
    public boolean equals(Object obj) {
        boolean retVal = false;
        if (this == obj) {
            retVal = true;
        } else if (obj instanceof ShrimpSpeciesNode) {
            retVal = super.equals(obj);
        }

        return retVal;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.isotopeName);
        hash = 37 * hash + Objects.hashCode(this.squidSpeciesModel);
        hash = 37 * hash + Objects.hashCode(this.methodNameForShrimpFraction);
        return hash;
    }

    public static ShrimpSpeciesNode buildShrimpSpeciesNode(SquidSpeciesModel squidSpeciesModel, String methodNameForShrimpFraction) {
        ShrimpSpeciesNode retVal = null;
        if (squidSpeciesModel != null) {
            retVal = new ShrimpSpeciesNode(squidSpeciesModel, methodNameForShrimpFraction);
        }
        return retVal;
    }

    public static ShrimpSpeciesNode buildShrimpSpeciesNode(SquidSpeciesModel squidSpeciesModel) {
        ShrimpSpeciesNode retVal = null;
        if (squidSpeciesModel != null) {
            retVal = new ShrimpSpeciesNode(squidSpeciesModel, "");
        }
        return retVal;
    }

    public static ShrimpSpeciesNode buildEmptyShrimpSpeciesNode() {
        return new ShrimpSpeciesNode();
    }

    @Override
    public boolean amHealthy() {
        boolean retVal = false;
        if (parentET instanceof ExpressionTree) {
            if (((ExpressionTree) parentET).getOperation() instanceof ShrimpSpeciesNodeFunction) {
                retVal = ((ShrimpSpeciesNodeFunction) ((ExpressionTree) parentET).getOperation()).getMethodNameForShrimpFraction().length() > 0;
            } else {
                retVal = (methodNameForShrimpFraction.length() > 0);
            }
        } else {
            // Node is top of expressiontree
            retVal = true;
        }

        return retVal;
    }

    @Override
    public boolean isValid() {
        return (squidSpeciesModel != null);
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
     * @param xstream
     */
    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new ShrimpSpeciesNodeXMLConverter());
        xstream.alias("ShrimpSpeciesNode", ShrimpSpeciesNode.class);

        xstream.registerConverter(new SquidSpeciesModelXMLConverter());
        xstream.alias("SquidSpeciesModel", SquidSpeciesModel.class);
    }

    /**
     * Assumes a list of shrimpFractions - can be a singleton if needed
     *
     * @param shrimpFractions the value of shrimpFraction
     * @return the double[][]
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    @Override
    public Object[][] eval(List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {
        Method method = null;
        // two ways to get methodname : 1) by construction, and 2) by ShrimpSpeciesNodeFunction supplying it
        if (methodNameForShrimpFraction != null) {
            try {
                method = ShrimpFractionExpressionInterface.class.getMethod(//
                        methodNameForShrimpFraction,
                        new Class[0]);
            } catch (NoSuchMethodException | SecurityException ignored) {
            }
        }

        Object[][] retVal = new Object[shrimpFractions.size()][];
        if (method != null) {
            int index = squidSpeciesModel.getMassStationIndex();
            if (index != -1) {
                for (int i = 0; i < shrimpFractions.size(); i++) {
                    double retVala = 0.0;
                    try {
                        double[] isotopeValues
                                = (double[]) method.invoke(shrimpFractions.get(i), new Object[0]);
                        if (index < isotopeValues.length) {
                            retVala = isotopeValues[index];
                        }
                        retVal[i] = convertArrayToObjects(new double[]{retVala});
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SquidException ignored) {
                    }
                }
            }
        }

        return retVal;
    }

    /**
     * @return
     */
    @Override
    public String toStringMathML() {

        return "<msubsup>\n"
        + "<mstyle mathsize='90%'>\n"
        + "<mtext>\n"
        + squidSpeciesModel.getIsotopeName()
        + "\n</mtext>\n"
        + "</mstyle>\n"
        + "<mstyle  mathsize='150%'>\n"
        + "<mtext>\n"
        + squidSpeciesModel.getElementName()//.split("2")[0]
        + "\n</mtext>\n"
        + "</mstyle>\n"
        + "<mtext/>"
        + "</msubsup>\n";
    }

    /**
     * @return
     */
    @Override
    public String getName() {
        return isotopeName;//   squidSpeciesModel.getMassStationSpeciesName();
    }

    @Override
    public void setName(String name) {
        isotopeName = name;
    }

    /**
     * @param squidSpeciesModel
     */
    public void setsquidSpeciesModel(SquidSpeciesModel squidSpeciesModel) {
        this.squidSpeciesModel = squidSpeciesModel;
    }

    /**
     * @return the squidSpeciesModel
     */
    public SquidSpeciesModel getSquidSpeciesModel() {
        return squidSpeciesModel;
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

    /**
     * @return
     */
    @Override
    public boolean isTypeFunction() {
        return false;
    }

    /**
     * @return
     */
    @Override
    public boolean isTypeFunctionOrOperation() {
        return false;
    }

    /**
     * @return
     */
    @Override
    public int argumentCount() {
        return 0;
    }

    /**
     * @return the isotopeName
     */
    // for populating iists
    public String toString() {
        return isotopeName;
    }
}