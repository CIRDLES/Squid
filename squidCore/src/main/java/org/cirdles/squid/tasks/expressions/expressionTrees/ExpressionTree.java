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
package org.cirdles.squid.tasks.expressions.expressionTrees;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.TaskXMLConverterVariables.SquidSpeciesModelXMLConverter;
import org.cirdles.squid.tasks.expressions.OperationOrFunctionInterface;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.constants.ConstantNodeXMLConverter;
import org.cirdles.squid.tasks.expressions.functions.Function;
import org.cirdles.squid.tasks.expressions.functions.FunctionXMLConverter;
import org.cirdles.squid.tasks.expressions.functions.ShrimpSpeciesNodeFunction;
import org.cirdles.squid.tasks.expressions.functions.SpotNodeLookupFunction;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNodeXMLConverter;
import org.cirdles.squid.tasks.expressions.operations.BlankOperation;
import org.cirdles.squid.tasks.expressions.operations.Operation;
import org.cirdles.squid.tasks.expressions.operations.OperationXMLConverter;
import org.cirdles.squid.tasks.expressions.spots.SpotFieldNode;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForPerSpotTaskExpressions;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForIsotopicRatios;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForSummary;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForSummaryXMLConverter;

/**
 *
 * @author James F. Bowring
 */
@XStreamAlias("ExpressionTree")
public class ExpressionTree
        implements
        Comparable<ExpressionTree>,
        ExpressionTreeInterface,
        ExpressionTreeBuilderInterface,
        ExpressionTreeWithRatiosInterface,
        Serializable,
        XMLSerializerInterface {

    private static final long serialVersionUID = 69881766695649050L;

    /**
     *
     */
    protected String name;

    /**
     *
     */
    protected List<ExpressionTreeInterface> childrenET;

    /**
     *
     */
    @XStreamOmitField
    protected ExpressionTreeInterface parentET;

    /**
     *
     */
    protected OperationOrFunctionInterface operation = new BlankOperation();

    /**
     *
     */
    protected List<String> ratiosOfInterest;

    /**
     *
     */
    protected boolean squidSwitchSCSummaryCalculation;

    /**
     *
     */
    protected boolean squidSwitchSTReferenceMaterialCalculation;

    /**
     *
     */
    protected boolean squidSwitchSAUnknownCalculation;

    /**
     *
     */
    protected boolean squidSpecialUPbThExpression;

    /**
     *
     */
    protected boolean rootExpressionTree;

    protected boolean squidSwitchConcentrationReferenceMaterialCalculation;

    protected String uncertaintyDirective;

    protected int index;

    /**
     *
     */
    public ExpressionTree() {
        this("Anonymous");
    }

    /**
     *
     * @param prettyName the value of prettyName
     */
    public ExpressionTree(String prettyName) {
        this(prettyName, null, null, null);
    }

    /**
     *
     * @param operation
     */
    public ExpressionTree(OperationOrFunctionInterface operation) {
        this();
        if (operation != null) {
            this.operation = operation;
        } else {
            this.operation = new BlankOperation();
        }
    }

    /**
     *
     * @param prettyName the value of name
     * @param leftET the value of leftET
     * @param rightET the value of rightET
     * @param operation the value of operation
     */
    public ExpressionTree(String prettyName, ExpressionTreeInterface leftET, ExpressionTreeInterface rightET, OperationOrFunctionInterface operation) {
        this(prettyName, leftET, rightET, operation, new ArrayList<String>());
    }

    /**
     *
     * @param prettyName the value of prettyName
     * @param leftET the value of leftET
     * @param rightET the value of rightET
     * @param operation the value of operation
     * @param ratiosOfInterest the value of ratiosOfInterest
     */
    private ExpressionTree(String prettyName, ExpressionTreeInterface leftET, ExpressionTreeInterface rightET, OperationOrFunctionInterface operation, List<String> ratiosOfInterest) {
        this.name = prettyName;
        this.childrenET = new ArrayList<>();
        populateChildrenET(leftET, rightET);
        this.parentET = null;
        if (operation != null) {
            this.operation = operation;
        } else {
            this.operation = new BlankOperation();
        }
        this.ratiosOfInterest = ratiosOfInterest;
        this.squidSwitchSCSummaryCalculation = false;
        this.squidSwitchSTReferenceMaterialCalculation = false;
        this.squidSwitchSAUnknownCalculation = false;
        this.squidSpecialUPbThExpression = false;
        this.squidSwitchConcentrationReferenceMaterialCalculation = false;
        this.rootExpressionTree = false;
        this.uncertaintyDirective = "";
    }

    public ExpressionTree copy() {
        ExpressionTree target = new ExpressionTree();
        target.setName(name);
        target.setChildrenET(childrenET);
        target.setOperation(operation);
        target.setSquidSwitchSCSummaryCalculation(squidSwitchSCSummaryCalculation);
        target.setSquidSwitchSTReferenceMaterialCalculation(squidSwitchSTReferenceMaterialCalculation);
        target.setSquidSwitchSAUnknownCalculation(squidSwitchSAUnknownCalculation);
        target.setSquidSpecialUPbThExpression(squidSpecialUPbThExpression);
        target.setSquidSwitchConcentrationReferenceMaterialCalculation(squidSwitchConcentrationReferenceMaterialCalculation);
        target.setRootExpressionTree(rootExpressionTree);

        return target;
    }

    private void populateChildrenET(ExpressionTreeInterface leftET, ExpressionTreeInterface rightET) {
        addChild(leftET);
        addChild(rightET);
    }

    @Override
    public boolean amHealthy() {
        boolean retVal = (isValid());
        // check for correct number of operands for operation
        if (retVal) {
            retVal = retVal && (getCountOfChildren() == argumentCount());
            if (retVal) {
                for (ExpressionTreeInterface exp : childrenET) {
                    retVal = retVal && exp.amHealthy();
                    if (!retVal) {
                        break;
                    }
                }
            }
        }
        return retVal;
    }

    @Override
    public boolean usesAnotherExpression(ExpressionTreeInterface expTarget) {
        boolean retVal = false;
        for (ExpressionTreeInterface exp : childrenET) {
            // checking for same object
            retVal = retVal || exp.usesAnotherExpression(expTarget) || (exp.equals(expTarget));
            if (retVal) {
                break;
            }
        }
        return retVal;
    }

    @Override
    public boolean usesOtherExpression() {
        boolean retVal = false;
        for (ExpressionTreeInterface exp : childrenET) {
            retVal = retVal || exp.usesOtherExpression() || (exp instanceof VariableNodeForPerSpotTaskExpressions);
            if (retVal) {
                break;
            }
        }
        return retVal;
    }

    @Override
    /**
     * This arranges expressions in ascending order of evaluation. Checking for
     * circular references is done elsewhere.
     */
    public int compareTo(ExpressionTree exp) {
        int retVal = 0;

        if (!((this == exp) || (this.equals(exp)))) {
            if (!amHealthy() && exp.amHealthy()) {
                // this object comes after exp
                retVal = 1;
            } else if (amHealthy() && !exp.amHealthy()) {
                // this object comes before exp
                retVal = -1;
            }
            if (retVal == 0) {
                if ((this instanceof ConstantNode) && !(exp instanceof ConstantNode)) {
                    // this object comes before exp
                    retVal = -1;
                } else if (exp instanceof ConstantNode) {
                    // this object comes after exp
                    retVal = 1;
                }
            }
            if (retVal == 0) {
                // then compare has ratios of interest
                if (hasRatiosOfInterest() && !exp.hasRatiosOfInterest()) {
                    // this object comes before exp
                    retVal = -1;
                } else if (!hasRatiosOfInterest() && exp.hasRatiosOfInterest()) {
                    retVal = 1;
                }
            }
            if (retVal == 0) {
                if (exp.usesAnotherExpression(this) && !usesAnotherExpression(exp)) {
                    // this object comes before exp
                    retVal = -1;
                } else if (usesAnotherExpression(exp)) {
                    // this object comes after exp
                    retVal = 1;
                }
            }

            if (retVal == 0) {
                // then compare on names so we have a complete ordering
                retVal = 1;//getName().compareToIgnoreCase(exp.getName());
            }
        }
        return retVal;
    }

    @Override
    public boolean equals(Object obj) {
        boolean retVal = false;
        if (this == obj) {
            retVal = true;
        } else if (obj instanceof ExpressionTree) {
            retVal = name.equals(((ExpressionTree) obj).getName());
        }
//            ExpressionTree expTree = (ExpressionTree) obj;
//
//            boolean childrenETIsSame = false;
//            if (childrenET.equals(expTree.childrenET)) {
//                childrenETIsSame = true;
//            }
//
//            boolean indexIsSame = false;
//            if (index == expTree.index) {
//                indexIsSame = true;
//            }
//
//            boolean nameIsSame = false;
//            if (name.equals(expTree.getName())) {
//                nameIsSame = true;
//            }
//
//            boolean operationIsSame = false;
//            if (operation.equals(expTree.operation)) {
//                operationIsSame = true;
//            }
//
//            boolean parentETIsSame = false;
//            if (parentET.equals(expTree.parentET)) {
//                parentETIsSame = true;
//            }
//
//            boolean ratiosOfInterestIsSame = false;
//            if (ratiosOfInterest.equals(expTree.ratiosOfInterest)) {
//                ratiosOfInterestIsSame = true;
//            }
//
//            boolean rootExpressionTreeIsSame = false;
//            if (rootExpressionTree == expTree.rootExpressionTree) {
//                rootExpressionTreeIsSame = true;
//            }
//
//            boolean squidSpecialUPbThExpressionIsSame = false;
//            if (squidSpecialUPbThExpression == expTree.squidSpecialUPbThExpression) {
//                squidSpecialUPbThExpressionIsSame = true;
//            }
//
//            boolean squidSwitchConcentrationReferenceMaterialCalculationIsSame = false;
//            if (squidSwitchConcentrationReferenceMaterialCalculation == expTree.squidSwitchConcentrationReferenceMaterialCalculation) {
//                squidSwitchConcentrationReferenceMaterialCalculationIsSame = true;
//            }
//
//            boolean squidSwitchSAUnknownCalculationIsSame = false;
//            if (squidSwitchSAUnknownCalculation == expTree.squidSwitchSAUnknownCalculation) {
//                squidSwitchSAUnknownCalculationIsSame = true;
//            }
//
//            boolean squidSwitchSCSummaryCalculationIsSame = false;
//            if (squidSwitchSCSummaryCalculation == expTree.squidSwitchSCSummaryCalculation) {
//                squidSwitchSCSummaryCalculationIsSame = true;
//            }
//
//            boolean squidSwitchSTReferenceMaterialCalculationIsSame = false;
//            if (squidSwitchSTReferenceMaterialCalculation == expTree.squidSwitchSTReferenceMaterialCalculation) {
//                squidSwitchSTReferenceMaterialCalculationIsSame = true;
//            }
//
//            boolean uncertaintyDirectiveIsSame = false;
//            if (uncertaintyDirective.equals(expTree.uncertaintyDirective)) {
//                uncertaintyDirectiveIsSame = true;
//            }
//
//            if (childrenETIsSame && indexIsSame && nameIsSame && operationIsSame && parentETIsSame
//                    && ratiosOfInterestIsSame && rootExpressionTreeIsSame && squidSpecialUPbThExpressionIsSame
//                    && squidSwitchConcentrationReferenceMaterialCalculationIsSame && squidSwitchSAUnknownCalculationIsSame
//                    && squidSwitchSCSummaryCalculationIsSame && squidSwitchSTReferenceMaterialCalculationIsSame && uncertaintyDirectiveIsSame) {
//                retVal = true;
//            }
//        }

        return retVal;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.name);
        return hash;
    }

    public boolean isValid() {
        return (operation != null);
    }

    @Override
    public String auditOperationArgumentCount() {
        StringBuilder audit = new StringBuilder();
        if (operation == null) {
            if (!(this instanceof ConstantNode)
                    && !(this instanceof SpotFieldNode)
                    && !(((ExpressionTreeInterface) this) instanceof ShrimpSpeciesNode)
                    && !(((ExpressionTreeInterface) this) instanceof VariableNodeForIsotopicRatios)
                    && !(((ExpressionTreeInterface) this) instanceof VariableNodeForSummary)) {
                audit.append("    ").append(this.getName()).append(" is unhealthy expression");
            } else {
                if (this.amHealthy()) {
                    audit.append(this.getName()).append(" is healthy ").append(this.getClass().getSimpleName());
                } else if (this.getParentET() == null) {// only if ConstantNode is top of tree vs within an already autided expressiontree
                    audit.append("    ").append(this.getName()); // = Missing Expression becasue if unhealthy, it was forced to be a constantNode
                }
            }
        } else {

            int requiredChildren = argumentCount();
            int suppliedChildren = getCountOfChildren();

            audit.append("Op ").append(operation.getName()).append(" requires/provides: ")
                    .append(requiredChildren).append(" / ").append(suppliedChildren).append(" arguments.");

            for (ExpressionTreeInterface child : getChildrenET()) {
                if (child instanceof ConstantNode) {
                    if (((ConstantNode) child).isMissingExpression()) {
                        audit.append("\n    Expression '").append((String) ((ConstantNode) child).getValue()).append("' is missing.");
                    }
                }

                // backwards compatible with use of ShrimpSpeciesNodes directly
                if (child instanceof ShrimpSpeciesNode) {
                    if (!(((ExpressionTree) child.getParentET()).getOperation() instanceof ShrimpSpeciesNodeFunction)
                            && (((ShrimpSpeciesNode) child).getMethodNameForShrimpFraction().length() == 0)) {
                        audit.append("\n    Expression '").append((String) ((ShrimpSpeciesNode) child).getName()).append("' is not a valid argument.");
                    }
                }
            }

            audit.append("\n    returns ").append(operation.printOutputValues());
        }

        return audit.toString();
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

        xstream.registerConverter(new ConstantNodeXMLConverter());
        xstream.alias("ConstantNode", ConstantNode.class);

        xstream.registerConverter(new VariableNodeForSummaryXMLConverter());
        xstream.alias("VariableNodeForSummary", VariableNodeForSummary.class);
        xstream.alias("VariableNodeForPerSpotTaskExpressions", VariableNodeForPerSpotTaskExpressions.class);
        xstream.alias("VariableNodeForIsotopicRatios", VariableNodeForIsotopicRatios.class);

        xstream.registerConverter(new OperationXMLConverter());
        xstream.alias("Operation", Operation.class);
        xstream.registerConverter(new FunctionXMLConverter());
        xstream.alias("Operation", Function.class);
        xstream.alias("Operation", OperationOrFunctionInterface.class);

        xstream.registerConverter(new ExpressionTreeXMLConverter());
        xstream.alias("ExpressionTree", ExpressionTree.class);
        xstream.alias("ExpressionTree", ExpressionTreeInterface.class);

        // Note: http://cristian.sulea.net/blog.php?p=2014-11-12-xstream-object-references
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.autodetectAnnotations(true);
    }

    /**
     *
     * @param xml
     * @return
     */
    @Override
    public String customizeXML(String xml) {
        return XMLSerializerInterface.super.customizeXML(xml);
    }

    /**
     *
     * @param shrimpFractions the value of shrimpFraction
     * @param task
     * @return the double[][]
     */
    @Override
    public Object[][] eval(List<ShrimpFractionExpressionInterface> shrimpFractions, TaskInterface task) throws SquidException {
        // backwards compatable
        if (uncertaintyDirective == null) {
            uncertaintyDirective = "";
        }

        return operation == null ? new Object[][]{{0.0, 0.0}} : operation.eval(childrenET, shrimpFractions, task);
    }

    /**
     *
     * @return
     */
    @Override
    public int getOperationPrecedence() {
        int retVal = 100;

        if (operation != null) {
            retVal = operation.getPrecedence();
        }

        return retVal;
    }

    public String getUncertaintyDirective() {
        return uncertaintyDirective;
    }

    public int getIndex() {
        return index;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isTypeFunction() {
        return (operation instanceof Function);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isTypeFunctionOrOperation() {
        return (operation instanceof Function) || (operation instanceof Operation);
    }

    /**
     *
     * @return
     */
    @Override
    public String toStringMathML() {
        String retVal = "";
        if (operation == null) {
            retVal = "<mtext>No expression selected.</mtext>\n";
        } else {
            try {
                retVal = operation.toStringMathML(childrenET);
            } catch (Exception e) {
            }
        }
        return retVal;
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the childrenET
     */
    @Override
    public List<ExpressionTreeInterface> getChildrenET() {
        return childrenET;
    }

    /**
     * @param childrenET the childrenET to set
     */
    public void setChildrenET(List<ExpressionTreeInterface> childrenET) {
        this.childrenET = childrenET;
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
     * @return the leftET
     */
    @Override
    public ExpressionTreeInterface getLeftET() {
        ExpressionTreeInterface retVal = null;
        try {
            retVal = childrenET.get(0);
        } catch (Exception e) {
        }
        return retVal;
    }

    /**
     * @return the rightET
     */
    @Override
    public ExpressionTreeInterface getRightET() {
        ExpressionTreeInterface retVal = null;
        try {
            retVal = childrenET.get(1);
        } catch (Exception e) {
        }
        return retVal;
    }

    /**
     *
     * @return
     */
    @Override
    public int getCountOfChildren() {
        return childrenET.size();// - (int) ((leftET == null) ? 1 : 0);
    }

    /**
     *
     * @param childET
     */
    @Override
    public void addChild(ExpressionTreeInterface childET) {
        if (childET != null) {
            childrenET.add(childET);
            childET.setParentET(this);
        }
    }

    /**
     *
     * @param index
     * @param childET
     */
    @Override
    public void addChild(int index, ExpressionTreeInterface childET) {
        if (childET != null) {
            childrenET.add(index, childET);
            childET.setParentET(this);
        }
    }

    /**
     * @return the operation
     */
    @Override
    public OperationOrFunctionInterface getOperation() {
        OperationOrFunctionInterface retOp;
        if (operation == null) {
            retOp = new BlankOperation();
        } else {
            retOp = operation;
        }
        return retOp;
    }

    /**
     * @param operation the operation to set
     */
    @Override
    public void setOperation(OperationOrFunctionInterface operation) {
        if (operation != null) {
            this.operation = operation;
        }
    }

    /**
     * @return the ratiosOfInterest
     */
    @Override
    public List<String> getRatiosOfInterest() {
        return ratiosOfInterest;
    }

    /**
     * @param ratiosOfInterest the ratiosOfInterest to set
     */
    public void setRatiosOfInterest(List<String> ratiosOfInterest) {
        this.ratiosOfInterest = ratiosOfInterest;
    }

    /**
     *
     * @return
     */
    public boolean hasRatiosOfInterest() {
        return ratiosOfInterest.size() > 0;
    }

    public List<String> getAllRatiosOfInterest() {
        return getAllRatiosOfInterest(new ArrayList<>());
    }

    private List<String> getAllRatiosOfInterest(List<String> ratiosList) {
        if (operation != null) {
            if (!(operation instanceof SpotNodeLookupFunction)) {
                addOnlyNewRatios(ratiosList, ratiosOfInterest);
                for (ExpressionTreeInterface child : childrenET) {
                    addOnlyNewRatios(ratiosList, ((ExpressionTree) child).getAllRatiosOfInterest(ratiosList));
                }
            }
        }

        return ratiosList;
    }

    private List<String> addOnlyNewRatios(List<String> target, List<String> source) {
        for (int i = 0; i < source.size(); i++) {
            if (!target.contains(source.get(i))) {
                target.add(source.get(i));
            }
        }

        return target;
    }

    /**
     * @return the rootExpressionTree
     */
    @Override
    public boolean isRootExpressionTree() {
        return rootExpressionTree;
    }

    /**
     * @param rootExpressionTree the rootExpressionTree to set
     */
    public void setRootExpressionTree(boolean rootExpressionTree) {
        this.rootExpressionTree = rootExpressionTree;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * @return the squidSwitchSCSummaryCalculation
     */
    @Override
    public boolean isSquidSwitchSCSummaryCalculation() {
        return squidSwitchSCSummaryCalculation;
    }

    /**
     * @param squidSwitchSCSummaryCalculation the
     * squidSwitchSCSummaryCalculation to set
     */
    @Override
    public void setSquidSwitchSCSummaryCalculation(boolean squidSwitchSCSummaryCalculation) {
        this.squidSwitchSCSummaryCalculation = squidSwitchSCSummaryCalculation;
    }

    /**
     * @return the squidSwitchSTReferenceMaterialCalculation
     */
    @Override
    public boolean isSquidSwitchSTReferenceMaterialCalculation() {
        return squidSwitchSTReferenceMaterialCalculation;
    }

    /**
     * @param squidSwitchSTReferenceMaterialCalculation the
     * squidSwitchSTReferenceMaterialCalculation to set
     */
    @Override
    public void setSquidSwitchSTReferenceMaterialCalculation(boolean squidSwitchSTReferenceMaterialCalculation) {
        this.squidSwitchSTReferenceMaterialCalculation = squidSwitchSTReferenceMaterialCalculation;
    }

    /**
     * @return the squidSwitchSAUnknownCalculation
     */
    @Override
    public boolean isSquidSwitchSAUnknownCalculation() {
        return squidSwitchSAUnknownCalculation;
    }

    /**
     * @param squidSwitchSAUnknownCalculation the
     * squidSwitchSAUnknownCalculation to set
     */
    @Override
    public void setSquidSwitchSAUnknownCalculation(boolean squidSwitchSAUnknownCalculation) {
        this.squidSwitchSAUnknownCalculation = squidSwitchSAUnknownCalculation;
    }

    /**
     * @return the squidSpecialUPbThExpression
     */
    @Override
    public boolean isSquidSpecialUPbThExpression() {
        return squidSpecialUPbThExpression;
    }

    /**
     * @param squidSpecialUPbThExpression the squidSpecialUPbThExpression to set
     */
    @Override
    public void setSquidSpecialUPbThExpression(boolean squidSpecialUPbThExpression) {
        this.squidSpecialUPbThExpression = squidSpecialUPbThExpression;
    }

    /**
     * @return the squidSwitchConcentrationReferenceMaterialCalculation
     */
    @Override
    public boolean isSquidSwitchConcentrationReferenceMaterialCalculation() {
        return squidSwitchConcentrationReferenceMaterialCalculation;
    }

    /**
     * @param squidSwitchConcentrationReferenceMaterialCalculation the
     * squidSwitchConcentrationReferenceMaterialCalculation to set
     */
    @Override
    public void setSquidSwitchConcentrationReferenceMaterialCalculation(boolean squidSwitchConcentrationReferenceMaterialCalculation) {
        this.squidSwitchConcentrationReferenceMaterialCalculation = squidSwitchConcentrationReferenceMaterialCalculation;
    }

    /**
     * @param uncertaintyDirective the uncertaintyDirective to set
     */
    @Override
    public void setUncertaintyDirective(String uncertaintyDirective) {
        this.uncertaintyDirective = uncertaintyDirective;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }
}
