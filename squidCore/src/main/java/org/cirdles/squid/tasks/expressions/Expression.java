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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import static org.cirdles.squid.constants.Squid3Constants.XML_HEADER_FOR_SQUIDTASK_EXPRESSION_FILES_USING_REMOTE_SCHEMA;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.tasks.TaskXMLConverterVariables.SquidSpeciesModelXMLConverter;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.constants.ConstantNodeXMLConverter;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeXMLConverter;
import org.cirdles.squid.tasks.expressions.functions.Function;
import org.cirdles.squid.tasks.expressions.functions.FunctionXMLConverter;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNodeXMLConverter;
import org.cirdles.squid.tasks.expressions.operations.Operation;
import org.cirdles.squid.tasks.expressions.operations.OperationXMLConverter;
import org.cirdles.squid.tasks.expressions.parsing.ExpressionParser;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForIsotopicRatios;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForPerSpotTaskExpressions;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForSummary;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForSummaryXMLConverter;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;

/**
 *
 * @author James F. Bowring
 */
public class Expression implements Comparable<Expression>, XMLSerializerInterface, Serializable {

    private static final long serialVersionUID = 2614344042503810733L;

    private String name;
    private String excelExpressionString;
    private boolean squidSwitchNU;
    private ExpressionTreeInterface expressionTree;
    private String parsingStatusReport;
    private List<String> argumentAudit;

    /**
     * Needed for XML unmarshal
     */
    public Expression() {
        this("NONE", "");
    }

    public Expression(String name, String excelExpressionString) {
        this(new ExpressionTree(name), excelExpressionString, false);
    }

    public Expression(ExpressionTreeInterface expressionTree, String excelExpressionString, boolean squidSwitchNU) {
        this.name = expressionTree.getName();
        this.excelExpressionString = excelExpressionString;
        this.squidSwitchNU = squidSwitchNU;
        this.expressionTree = expressionTree;
        this.parsingStatusReport = "";
        this.argumentAudit = new ArrayList<>();
    }

    @Override
    public int compareTo(Expression expression) {
        int retVal = 0;
        if (!((this == expression) || this.equals(expression))) {
            retVal = ((ExpressionTree) expressionTree).compareTo((ExpressionTree) expression.getExpressionTree());
        }
        
        // in case expressiontrees have same name but expressions do not
        if (retVal == 0) {
            retVal = name.compareToIgnoreCase(expression.getName());
        }

        return retVal;
    }

    @Override
    public boolean equals(Object obj) {
        boolean retVal = false;
        if (this == obj) {
            retVal = true;
        } else if (obj instanceof Expression && (expressionTree != null)) {
            // note checking if expressionTree is null due to bad parsing
            retVal = ((ExpressionTree) expressionTree).equals((ExpressionTree) ((Expression) obj).getExpressionTree());
        }

        // in case expressiontrees have same name but expressions do not
        if (retVal) {
            retVal = name.compareToIgnoreCase(((Expression) obj).getName()) == 0;
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
            retVal = expressionTree.amHealthy();
        }
        return retVal;
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

        xstream.registerConverter(new ExpressionXMLConverter());
        xstream.alias("Expression", Expression.class);

        // Note: http://cristian.sulea.net/blog.php?p=2014-11-12-xstream-object-references
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.autodetectAnnotations(true);
    }

    public void parseOriginalExpressionStringIntoExpressionTree(Map<String, ExpressionTreeInterface> namedExpressionsMap) {
        ExpressionParser expressionParser = new ExpressionParser(namedExpressionsMap);
        expressionTree = expressionParser.parseExpressionStringAndBuildExpressionTree(this);
    }

    public String produceExpressionTreeAudit() {

        String auditReport = "";
        if (!((ExpressionTreeInterface) expressionTree).isValid()) {
            auditReport
                    += "Errors occurred in parsing:\n" + ((parsingStatusReport.trim().length()==0)? "expression not valid" : parsingStatusReport);
        } else {
            auditExpressionTreeDependencies();
            auditReport
                    += "Expression healthy: "
                    + String.valueOf(expressionTree.amHealthy()).toUpperCase(Locale.US);
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
     * @return the excelExpressionString
     */
    public String getExcelExpressionString() {
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
}
