/*
 * Copyright 2017 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.tasks.expressions;

import com.thoughtworks.xstream.XStream;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.shrimp.SquidSpeciesModelXMLConverter;
import org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsNotes;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.constants.ConstantNodeXMLConverter;
import org.cirdles.squid.tasks.expressions.expressionTrees.*;
import org.cirdles.squid.tasks.expressions.functions.Function;
import org.cirdles.squid.tasks.expressions.functions.FunctionXMLConverter;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNodeXMLConverter;
import org.cirdles.squid.tasks.expressions.operations.Operation;
import org.cirdles.squid.tasks.expressions.operations.OperationXMLConverter;
import org.cirdles.squid.tasks.expressions.parsing.ExpressionParser;
import org.cirdles.squid.tasks.expressions.spots.SpotFieldNode;
import org.cirdles.squid.tasks.expressions.spots.SpotFieldNodeNodeXMLConverter;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForIsotopicRatios;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForPerSpotTaskExpressions;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForSummary;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForSummaryXMLConverter;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.cirdles.squid.constants.Squid3Constants.*;
import static org.cirdles.squid.constants.Squid3Constants.SpotTypes.UNKNOWN;
import static org.cirdles.squid.tasks.expressions.functions.Function.replaceAliasedFunctionNamesInExpressionString;

/**
 * @author James F. Bowring
 */
public class Expression implements Comparable<Expression>, XMLSerializerInterface, Serializable {

    private static final long serialVersionUID = 2614344042503810733L;

    public static final String MSG_POORLY_FORMED_EXPRESSION = "Expression missing or poorly formed.";

    private String name;
    private String excelExpressionString;
    private boolean squidSwitchNU;
    private boolean referenceMaterialValue;
    private boolean parameterValue;
    private ExpressionTreeInterface expressionTree;
    private String notes;
    private String sourceModelNameAndVersion;

    private transient String parsingStatusReport;
    private transient List<String> argumentAudit;
    private transient List<String> targetAudit;

    /**
     * Needed for XML unmarshal
     */
    public Expression() {
        this("NONE", "");
    }

    public Expression(String name, String excelExpressionString) {
        this(new ExpressionTree(name), excelExpressionString, false, false, false);
    }

    public Expression(
            ExpressionTreeInterface expressionTree,
            String excelExpressionString,
            boolean squidSwitchNU,
            boolean referenceMaterialValue,
            boolean parameterValue) {

        this.name = expressionTree.getName();
        this.excelExpressionString = excelExpressionString;
        this.squidSwitchNU = squidSwitchNU;
        this.referenceMaterialValue = referenceMaterialValue;
        this.parameterValue = parameterValue;
        this.expressionTree = expressionTree;
        this.parsingStatusReport = "";
        this.argumentAudit = new ArrayList<>();
        this.targetAudit = new ArrayList<>();

        this.notes = "";
        this.sourceModelNameAndVersion = "";
    }

    @Override
    public int compareTo(Expression expression) {
        int retVal = 0;
        if (!((this == expression) || this.equals(expression)) && expression.getExpressionTree() != null) {
            retVal = ((ExpressionTree) getExpressionTree()).compareTo((ExpressionTree) expression.getExpressionTree());
        }
        return retVal;
    }

    @Override
    public boolean equals(Object obj) {
        boolean retVal = false;
        if (this == obj) {
            retVal = true;
        } else if (obj instanceof Expression && (getExpressionTree() != null)) {
            // note checking if expressionTree is null due to bad parsing
            retVal = (getName().compareToIgnoreCase(((Expression) obj).getName()) == 0);
        }
        return retVal;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public boolean amHealthy() {
        boolean retVal = false;
        if (expressionTree != null) {
            retVal = expressionTree.amHealthy() && (haveMatchedTargetSpots() == 0);
        }
        return retVal;
    }

    /**
     * @return -1 = no match, 0 = match, 1 = mismatch
     */
    private int haveMatchedTargetSpots() {
        int goalTargetBits = expressionTree.makeTargetBits();
        // jan 2021
        if (((ExpressionTree) expressionTree).doesHaveNoTargetSpots()) {
            goalTargetBits = 0;
        }
        int targetBits = expressionTree.auditTargetMatchingII(goalTargetBits);

        // make sure summary refers to correct target
        if ((targetBits > 0) && expressionTree.isSquidSwitchSCSummaryCalculation()) {
            if (expressionTree.isSquidSwitchSTReferenceMaterialCalculation() || expressionTree.isSquidSwitchConcentrationReferenceMaterialCalculation()) {
                if (targetBits != 2) {
                    targetBits = 3;
                }
            }
            if (expressionTree.isSquidSwitchSAUnknownCalculation()) {
                if (targetBits != 1) {
                    targetBits = 3;
                }
            }
        }

        return ((goalTargetBits == 0) ? -1 : ((goalTargetBits > targetBits)) ? 1 : 0);
    }

    @Override
    public String customizeXML(String xml) {
        String xmlR = xml;

        xmlR = xmlR.replaceFirst("<Expression>",
                XML_HEADER_FOR_SQUIDTASK_EXPRESSION_FILES_USING_REMOTE_SCHEMA);

        return xmlR;
    }

    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new ShrimpSpeciesNodeXMLConverter());
        xstream.alias("ShrimpSpeciesNode", ShrimpSpeciesNode.class);

        xstream.registerConverter(new SquidSpeciesModelXMLConverter());
        xstream.alias("SquidSpeciesModel", SquidSpeciesModel.class);

        xstream.registerConverter(new ConstantNodeXMLConverter());
        xstream.alias("ConstantNode", ConstantNode.class);

        xstream.registerConverter(new SpotFieldNodeNodeXMLConverter());
        xstream.alias("SpotFieldNode", SpotFieldNode.class);

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
        xstream.alias("ExpressionTree", ExpressionTreeParsedFromExcelString.class);

        xstream.registerConverter(new ExpressionXMLConverter());
        xstream.alias("Expression", Expression.class);

        // Note: http://cristian.sulea.net/blog.php?p=2014-11-12-xstream-object-references
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.autodetectAnnotations(true);
    }

    public void parseOriginalExpressionStringIntoExpressionTree(Map<String, ExpressionTreeInterface> namedExpressionsMap) {
        ExpressionParser expressionParser = new ExpressionParser(namedExpressionsMap);
        if (excelExpressionString != null) {
            expressionTree = expressionParser.parseExpressionStringAndBuildExpressionTree(this);
        }
    }

    /**
     * @param expressionName      the value of expressionName
     * @param expressionString    the value of expressionString
     * @param namedExpressionsMap the value of namedExpressionsMap
     * @return
     */
    public static Expression makeExpressionForAudit(
            String expressionName, final String expressionString, Map<String, ExpressionTreeInterface> namedExpressionsMap) {
        Expression exp = new Expression(expressionName, expressionString);

        exp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);

        ExpressionTreeInterface expTree = exp.getExpressionTree();

        expTree.setSquidSwitchSTReferenceMaterialCalculation(true);
        expTree.setSquidSwitchSAUnknownCalculation(true);
        expTree.setSquidSwitchConcentrationReferenceMaterialCalculation(false);
        expTree.setSquidSwitchSCSummaryCalculation(false);
        expTree.setSquidSpecialUPbThExpression(true);
        expTree.setUnknownsGroupSampleName(UNKNOWN.getSpotTypeName());

        // to detect ratios of interest
        if (expTree instanceof BuiltInExpressionInterface) {
            ((BuiltInExpressionInterface) expTree).buildExpression();
        }

        return exp;
    }

    public String produceExpressionTreeAudit() {
        String auditReport = "";
        if (!((ExpressionTreeInterface) expressionTree).isValid()) {
            if (((ExpressionTree) expressionTree).getOperation() == null) {
                auditReport
                        += MSG_POORLY_FORMED_EXPRESSION;
            } else {
                auditReport
                        += "Errors occurred in parsing:\n"
                        + ((parsingStatusReport.trim().length() == 0) ? "expression not valid" : parsingStatusReport);
            }
        } else {
            auditExpressionTreeDependencies();
            auditExpressionTreeTargetCompatibility();

            int match = haveMatchedTargetSpots();

            auditReport
                    += "Target Spots: "
                    + (String) ((match == -1) ? "MISSING - Please select" : ((match == 1))
                    ? "NOT MATCHED" : "MATCHED")
                    + "\n";
            if (targetAudit.size() > 0) {
                auditReport += "Target Spots Audit:\n";
                for (String audit : targetAudit) {
                    auditReport += audit + "\n";
                }
                auditReport += "\n";
            }

            auditReport
                    += "Expression healthy: "
                    + String.valueOf(expressionTree.amHealthy()).toUpperCase(Locale.ENGLISH);
            if (argumentAudit.size() > 0) {
                auditReport += "\nAudit:\n";
                for (String audit : argumentAudit) {
                    auditReport += audit + "\n";
                }
            } else {
                // case of no arguments = ConstantNode, ShrimpSpeciesNode or SpotFieldNode ALL Default if missing to ConstantNode with name "Missing Expression"
                auditReport += "\n  " + (expressionTree.amHealthy() ? "Found " : "") + expressionTree.getName();
                if ((expressionTree instanceof ConstantNode) && expressionTree.amHealthy()) {
                    auditReport += ", value = " + String.valueOf((double) ((ConstantNode) expressionTree).getValue());
                }
            }
        }

        return auditReport;
    }

    private void auditExpressionTreeTargetCompatibility() {
        if (((ExpressionTreeInterface) expressionTree).isValid()) {
            if (expressionTree instanceof ExpressionTree) {
                this.targetAudit = new ArrayList<>();
                ((ExpressionTree) expressionTree).auditExpressionTreeTargetMatching(targetAudit);
            }
        }
    }

    private void auditExpressionTreeDependencies() {
        if (((ExpressionTreeInterface) expressionTree).isValid()) {
            if (expressionTree instanceof ExpressionTree) {
                this.argumentAudit = new ArrayList<>();
                ((ExpressionTree) expressionTree).auditExpressionTreeDependencies(argumentAudit);
            }
        }
    }

    public String buildSignatureString() {
        StringBuilder signature = new StringBuilder();
        if (((ExpressionTree) expressionTree).isValid()) {
            signature.append(((ExpressionTree) expressionTree).hasRatiosOfInterest() ? "  " + String.valueOf(((ExpressionTree) expressionTree).getRatiosOfInterest().size()) : "  -");
            signature.append(((ExpressionTree) expressionTree).isSquidSwitchSCSummaryCalculation() ? "  +" : "  -");
            signature.append(((ExpressionTree) expressionTree).isSquidSwitchSTReferenceMaterialCalculation() ? "  +" : "  -");
            signature.append(((ExpressionTree) expressionTree).isSquidSwitchSAUnknownCalculation() ? "  +" : "  -");
            signature.append(((ExpressionTree) expressionTree).isSquidSpecialUPbThExpression() ? "  +  " : "  -  ");
            signature.append(name);
        } else {
            signature.append("  Parsing Error! ").append(name);
        }

        return signature.toString();
    }

    public String buildShortSignatureString() {
        StringBuilder signature = new StringBuilder();
        if (((ExpressionTree) expressionTree).isValid()) {
            if (!(referenceMaterialValue || parameterValue)) {
                signature.append(((ExpressionTree) expressionTree).isSquidSwitchSTReferenceMaterialCalculation() ? SUPERSCRIPT_R_FOR_REFMAT
                        : ((ExpressionTree) expressionTree).isSquidSwitchConcentrationReferenceMaterialCalculation() ? SUPERSCRIPT_C_FOR_CONCREFMAT : SUPERSCRIPT_SPACE);
                signature.append(((ExpressionTree) expressionTree).isSquidSwitchSAUnknownCalculation() ? SUPERSCRIPT_U_FOR_UNKNOWN : SUPERSCRIPT_SPACE);
                signature.append(" ");
            }
            signature.append(name);
        } else {
            signature.append("  Parsing Error! ").append(name);
        }

        return signature.toString();
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

    public String getNotes() {
        if (this.expressionTree.isSquidSpecialUPbThExpression()) {
            notes = BuiltInExpressionsNotes.BUILTIN_EXPRESSION_NOTES.get(name);
        }
        if (this.isParameterValue()
                || this.isReferenceMaterialValue()) {
            notes = "from Model: " + sourceModelNameAndVersion + "\n\n" + BuiltInExpressionsNotes.BUILTIN_EXPRESSION_NOTES.get(name);
        }

        if (notes == null) {
            notes = "none yet provided";
        }

        return notes;
    }

    public void setNotes(String comments) {
        this.notes = comments;
    }

    /**
     * @return the sourceModelNameAndVersion
     */
    public String getSourceModelNameAndVersion() {
        return sourceModelNameAndVersion;
    }

    /**
     * @param sourceModelNameAndVersion the sourceModelNameAndVersion to set
     */
    public void setSourceModelNameAndVersion(String sourceModelNameAndVersion) {
        this.sourceModelNameAndVersion = sourceModelNameAndVersion;
    }

    /**
     * @return the excelExpressionString
     */
    public String getExcelExpressionString() {
        excelExpressionString = replaceAliasedFunctionNamesInExpressionString(excelExpressionString);
        return excelExpressionString;
    }

    /**
     * @param excelExpressionString the excelExpressionString to set
     */
    public void setExcelExpressionString(String excelExpressionString) {
        this.excelExpressionString = excelExpressionString;
    }

    /**
     * @return the squidSwitchNU
     */
    public boolean isSquidSwitchNU() {
        return squidSwitchNU;
    }

    /**
     * @param squidSwitchNU the squidSwitchNU to set
     */
    public void setSquidSwitchNU(boolean squidSwitchNU) {
        this.squidSwitchNU = squidSwitchNU;
    }

    /**
     * @return the referenceMaterialValue
     */
    public boolean isReferenceMaterialValue() {
        return referenceMaterialValue;
    }

    /**
     * @param referenceMaterialValue the referenceMaterialValue to set
     */
    public void setReferenceMaterialValue(boolean referenceMaterialValue) {
        this.referenceMaterialValue = referenceMaterialValue;
    }

    /**
     * @return the parameterValue
     */
    public boolean isParameterValue() {
        return parameterValue;
    }

    /**
     * @param parameterValue the parameterValue to set
     */
    public void setParameterValue(boolean parameterValue) {
        this.parameterValue = parameterValue;
    }

    /**
     * @return the expressionTree
     */
    public ExpressionTreeInterface getExpressionTree() {
        return expressionTree;
    }

    /**
     * @param expressionTree the expressionTree to set
     */
    public void setExpressionTree(ExpressionTreeInterface expressionTree) {
        this.expressionTree = expressionTree;
    }

    /**
     * @return the parsingStatusReport
     */
    public String getParsingStatusReport() {
        return parsingStatusReport;
    }

    /**
     * @param parsingStatusReport the parsingStatusReport to set
     */
    public void setParsingStatusReport(String parsingStatusReport) {
        this.parsingStatusReport = parsingStatusReport;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isCustom() {
        return !getExpressionTree().isSquidSpecialUPbThExpression();// && !isSquidSwitchNU();
    }

    public boolean isAgeExpression() {
        return name.toUpperCase(Locale.ENGLISH).contains("AGE");
    }

    public boolean aliasedExpression() {
        return false; //BUILTIN_EXPRESSION_ALIASEDNAMES.contains(this.name);
    }
}