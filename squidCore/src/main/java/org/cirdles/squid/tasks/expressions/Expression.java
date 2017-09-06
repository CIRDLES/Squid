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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.tasks.expressions.builtinExpressions.SquidExpressionMinus4;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.parsing.ExpressionParser;

/**
 *
 * @author James F. Bowring
 */
public class Expression {

    private String name;
    private String originalExpressionString;
    private ExpressionTreeInterface expressionTree;
    private String parsingStatusReport;
    private Map<String, ExpressionTreeInterface> expressionTreeDependencies;
    private List<String> missingExpressionsByName;
    private List<String> argumentAudit;

    public Expression() {
        this.argumentAudit = new ArrayList<>();
    }

    public Expression(String name, String originalStringExpression, Map<String, ExpressionTreeInterface> namedExpressionsMap) {
        this.name = name;
        this.originalExpressionString = originalStringExpression;
        this.expressionTree = null;
        this.parsingStatusReport = "";
        this.expressionTreeDependencies = new HashMap<>();
        this.missingExpressionsByName = new ArrayList<>();
        this.argumentAudit = new ArrayList<>();

        parseOriginalExpressionStringIntoExpression(namedExpressionsMap);

    }

    private void parseOriginalExpressionStringIntoExpression(Map<String, ExpressionTreeInterface> namedExpressionsMap) {
        ExpressionParser expressionParser = new ExpressionParser(namedExpressionsMap);
        expressionTree = expressionParser.parseExpressionStringAndBuildExpressionTree(this);
    }

    public String produceExpressionTreeAudit() {

        String auditReport = "\n*** Expression Audit Report ***\n";
        if (expressionTree == null) {
            auditReport
                    += "Errors occurred in parsing:\n" + parsingStatusReport;
        } else {
            auditExpressionTreeDependencies();
            auditReport
                    += "Argument Count Audit:\n";
            for (String audit : argumentAudit) {
                auditReport += audit + "\n";
            }
        }

        return auditReport;
    }

    private void auditExpressionTreeDependencies() {
        if (expressionTree != null) {
            if (expressionTree instanceof ExpressionTree) {
                this.argumentAudit = new ArrayList<>();
                ((ExpressionTree) expressionTree).auditExpressionTreeDependencies(argumentAudit);
            }
        }
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
     * @return the originalExpressionString
     */
    public String getOriginalExpressionString() {
        return originalExpressionString;
    }

    /**
     * @param originalExpressionString the originalExpressionString to set
     */
    public void setOriginalExpressionString(String originalExpressionString) {
        this.originalExpressionString = originalExpressionString;
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

    /**
     * @return the expressionTreeDependencies
     */
    public Map<String, ExpressionTreeInterface> getExpressionTreeDependencies() {
        return expressionTreeDependencies;
    }

    /**
     * @param expressionTreeDependencies the expressionTreeDependencies to set
     */
    public void setExpressionTreeDependencies(Map<String, ExpressionTreeInterface> expressionTreeDependencies) {
        this.expressionTreeDependencies = expressionTreeDependencies;
    }

    /**
     * @return the missingExpressionsByName
     */
    public List<String> getMissingExpressionsByName() {
        return missingExpressionsByName;
    }

    /**
     * @param missingExpressionsByName the missingExpressionsByName to set
     */
    public void setMissingExpressionsByName(List<String> missingExpressionsByName) {
        this.missingExpressionsByName = missingExpressionsByName;
    }

//    public static void main(String[] args) {
//        Expression test = new Expression("TEST", "ln(ln([\"expression\"]))");
//        System.out.println(test.produceExpressionTreeAudit());
//
//    }
}
