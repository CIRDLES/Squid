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
package org.cirdles.squid.tasks.expressions.isotopes;

import com.thoughtworks.xstream.XStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.shrimp.SquidSpeciesModelXMLConverter;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.functions.ShrimpSpeciesNodeFunction;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;

/**
 *
 * @author James F. Bowring
 */
public class ShrimpSpeciesNode implements ExpressionTreeInterface, Serializable, XMLSerializerInterface {

    private static final long serialVersionUID = 3592579554999155473L;

    private String isotopeName;
    private SquidSpeciesModel squidSpeciesModel;
    private String methodNameForShrimpFraction;
    // used for parsing expressions
    private ExpressionTreeInterface parentET;

    /**
     *
     */
    public ShrimpSpeciesNode() {
        this(null);
    }

    /**
     *
     * @param squidSpeciesModel
     * @param name
     */
    public ShrimpSpeciesNode(SquidSpeciesModel squidSpeciesModel) {
        this(squidSpeciesModel, "");
    }

    /**
     *
     * @param squidSpeciesModel
     * @param methodNameForShrimpFraction
     */
    public ShrimpSpeciesNode(SquidSpeciesModel squidSpeciesModel, String methodNameForShrimpFraction) {
        this.squidSpeciesModel = squidSpeciesModel;
        this.methodNameForShrimpFraction = methodNameForShrimpFraction;
        this.isotopeName = squidSpeciesModel.getIsotopeName();
    }

    public boolean amHealthy() {
        boolean retVal = false;
        if (parentET instanceof ExpressionTree) {
            if (((ExpressionTree) parentET).getOperation() instanceof ShrimpSpeciesNodeFunction) {
                retVal = ((ShrimpSpeciesNodeFunction) ((ExpressionTree) parentET).getOperation()).getMethodNameForShrimpFraction().length() > 0;
            } else {
                retVal = ((squidSpeciesModel instanceof SquidSpeciesModel) && methodNameForShrimpFraction.length() > 0);
            }
        }

        return retVal;
    }

    @Override
    public boolean usesAnotherExpression(ExpressionTreeInterface exp) {
        return false;
    }

    /**
     *
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
     * Assumes a one-element list of shrimpFractions
     *
     * @param shrimpFractions the value of shrimpFraction
     * @return the double[][]
     */
    @Override
    public Object[][] eval(List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) {
        double retVal = 0.0;
        Integer index = squidSpeciesModel.getMassStationIndex();
        if (index != -1) {
            double[] isotopeValues
                    = methodFactory(shrimpFractions.get(0), methodNameForShrimpFraction);
            if (index < isotopeValues.length) {
                retVal = isotopeValues[index];
            }
        }

        return new Object[][]{{retVal}};
    }

    /**
     *
     * @param shrimpFraction
     * @param methodNameForShrimpFraction
     * @return
     */
    public static double[] methodFactory(ShrimpFractionExpressionInterface shrimpFraction, String methodNameForShrimpFraction) {
        double[] retVal = new double[0];
        Method method;
        if (methodNameForShrimpFraction != null) {
            try {
                method = ShrimpFractionExpressionInterface.class.getMethod(//
                        methodNameForShrimpFraction,
                        new Class[0]);
                retVal = (double[]) method.invoke(shrimpFraction, new Object[0]);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException noSuchMethodException) {
                // do nothing for now
            }
        }
        return retVal;
    }

    /**
     *
     * @return
     */
    @Override
    public String toStringMathML() {
        String retVal
                = "<msubsup>\n"
                + "<mstyle mathsize='90%'>\n"
                + "<mtext>\n"
                + squidSpeciesModel.getIsotopeName()
                + "\n</mtext>\n"
                + "</mstyle>\n"
                + "<mstyle  mathsize='150%'>\n"
                + "<mtext>\n"
                + squidSpeciesModel.getElementName()
                + "\n</mtext>\n"
                + "</mstyle>\n"
                + "</msubsup>\n";

        return retVal;
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return squidSpeciesModel.getMassStationSpeciesName();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @param squidSpeciesModel
     * @param name the name to set
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
     * @return the parentET
     */
    @Override
    public ExpressionTreeInterface getParentET() {
        return parentET;
    }

    /**
     * @param parentET the parentET to set
     */
    @Override
    public void setParentET(ExpressionTreeInterface parentET) {
        this.parentET = parentET;
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

    public static void main(String[] args) {
        ShrimpSpeciesNode test = new ShrimpSpeciesNode(new SquidSpeciesModel(0, "196Zr2O", "196", "Zr2O", false), "getPkInterpScanArray");

        ((XMLSerializerInterface) test).serializeXMLObject(test, "ShrimpSpeciesNode.xml");

        ShrimpSpeciesNode deserialize = new ShrimpSpeciesNode();
        deserialize = (ShrimpSpeciesNode) ((XMLSerializerInterface) deserialize).readXMLObject("ShrimpSpeciesNode.xml", false);

        ((XMLSerializerInterface) deserialize).serializeXMLObject(deserialize, "ShrimpSpeciesNodeBBB.xml");
    }

    /**
     * @return the isotopeName
     */
    // for populating iists
    public String toString() {
        return isotopeName;
    }
}
