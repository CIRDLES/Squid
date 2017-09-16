/*
 * Copyright 2017 CIRDLES.org.
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
import java.util.Map;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.parsing.ExpressionParser;
import org.cirdles.squid.tasks.expressions.spots.SpotNode;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;

/**
 *
 * @author James F. Bowring
 */
public class Expression implements Comparable<Expression>, XMLSerializerInterface, Serializable {

    private static final long serialVersionUID = 2614344042503810733L;

    private String name;
    private String excelExpressionString;
    private ExpressionTreeInterface expressionTree;
    private String parsingStatusReport;
    private List<String> argumentAudit;

    private Expression() {
    }

    public Expression(String name, String excelExpressionString) {
        this(new ExpressionTree(name), excelExpressionString);
    }

    public Expression(ExpressionTreeInterface expressionTree, String excelExpressionString) {
        this.name = expressionTree.getName();
        this.excelExpressionString = excelExpressionString;
        this.expressionTree = expressionTree;
        this.parsingStatusReport = "";
        this.argumentAudit = new ArrayList<>();
    }

    @Override
    public int compareTo(Expression expression) {
        return ((ExpressionTree) expressionTree).compareTo((ExpressionTree) expression.getExpressionTree());
    }

    @Override
    public boolean equals(Object obj) {
        boolean retVal = false;
        if (this == obj) {
            retVal = true;
        } else if (obj instanceof Expression && (expressionTree instanceof ExpressionTreeInterface)) {
            // note checking if expressionTree is null due to bad parsing
            retVal = ((ExpressionTree) expressionTree).equals((ExpressionTree) ((Expression) obj).getExpressionTree());
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
        return XMLSerializerInterface.super.customizeXML(xml); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void customizeXstream(XStream xstream) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void parseOriginalExpressionStringIntoExpressionTree(Map<String, ExpressionTreeInterface> namedExpressionsMap) {
        ExpressionParser expressionParser = new ExpressionParser(namedExpressionsMap);
        expressionTree = expressionParser.parseExpressionStringAndBuildExpressionTree(this);
        if (!(expressionTree instanceof ConstantNode) && !(expressionTree instanceof SpotNode) && !(expressionTree instanceof ShrimpSpeciesNode)) {
            // ConstantNode and SpotNode has name already and plays role of toplevel expression here
            expressionTree.setName(name);
        }
    }

    public String produceExpressionTreeAudit() {

        String auditReport = "";
        if (!((ExpressionTreeInterface) expressionTree).isValid()) {
            auditReport
                    += "Errors occurred in parsing:\n" + parsingStatusReport;
        } else {
            auditExpressionTreeDependencies();
            auditReport
                    += "Expression healthy: "
                    + String.valueOf(expressionTree.amHealthy()).toUpperCase();
            if (argumentAudit.size() > 0) {
                auditReport += "\nAudit:\n";
                for (String audit : argumentAudit) {
                    auditReport += audit + "\n";
                }
            } else {
                // case of no arguments = ConstantNode, ShrimpSpeciesNode or SpotNode ALL Default if missing to ConstantNode with name "Missing Expression"
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
            signature.append(((ExpressionTree) expressionTree).getName());
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
