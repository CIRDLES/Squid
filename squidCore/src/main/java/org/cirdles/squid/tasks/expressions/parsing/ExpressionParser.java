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
package org.cirdles.squid.tasks.expressions.parsing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cirdles.squid.ExpressionsForSquid2Lexer;
import org.cirdles.squid.ExpressionsForSquid2Parser;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.OperationOrFunctionInterface;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import static org.cirdles.squid.tasks.expressions.constants.ConstantNode.MISSING_EXPRESSION_STRING;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeBuilderInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeParsedFromExcelString;
import org.cirdles.squid.tasks.expressions.functions.Function;
import static org.cirdles.squid.tasks.expressions.functions.Function.FUNCTIONS_MAP;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.operations.Operation;
import static org.cirdles.squid.tasks.expressions.operations.Operation.OPERATIONS_MAP;
import org.cirdles.squid.tasks.expressions.parsing.ShuntingYard.TokenTypes;
import org.cirdles.squid.tasks.expressions.spots.SpotFieldNode;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForIsotopicRatios;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForPerSpotTaskExpressions;

/**
 *
 * @author James F. Bowring
 */
public class ExpressionParser {

    private Map<String, ExpressionTreeInterface> namedExpressionsMap;
    private boolean isScalarFunctionFlag;

    public ExpressionParser() {
        this.namedExpressionsMap = new HashMap<>();
    }

    public ExpressionParser(Map<String, ExpressionTreeInterface> namedExpressionsMap) {
        this.namedExpressionsMap = namedExpressionsMap;
    }

    /**
     *
     * @param expression
     * @param expressionString
     * @return
     */
    public ExpressionTreeInterface parseExpressionStringAndBuildExpressionTree(Expression expression) {
        // true until reset by function
        isScalarFunctionFlag = true;
        ExpressionTreeInterface returnExpressionTree = new ExpressionTreeParsedFromExcelString(expression.getName());

        // Get our lexer
        ExpressionsForSquid2Lexer lexer = new ExpressionsForSquid2Lexer(new ANTLRInputStream(expression.getExcelExpressionString()));

        // Get a list of matched tokens
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Pass the tokens to the parser
        ExpressionsForSquid2Parser parser = new ExpressionsForSquid2Parser(tokens);

        // https://stackoverflow.com/questions/18132078/handling-errors-in-antlr4
        lexer.removeErrorListeners();
        DescriptiveErrorListener descriptiveErrorListenerLexer = new DescriptiveErrorListener(true);
        lexer.addErrorListener(descriptiveErrorListenerLexer);

        parser.removeErrorListeners();
        DescriptiveErrorListener descriptiveErrorListenerParser = new DescriptiveErrorListener(true);
        parser.addErrorListener(descriptiveErrorListenerParser);

        // Specify our entry point
        ExpressionsForSquid2Parser.ExprContext expSentenceContext = parser.expr();

        // we don't want to build expressiontree if any bad parsing present
        if (descriptiveErrorListenerLexer.getSyntaxErrors().length() + descriptiveErrorListenerParser.getSyntaxErrors().length() > 0) {
            expression.setParsingStatusReport(
                    descriptiveErrorListenerLexer.getSyntaxErrors()
                    + (String) (descriptiveErrorListenerLexer.getSyntaxErrors().length() > 0 ? descriptiveErrorListenerLexer.getSyntaxErrors() + "\n" : "")
                    + descriptiveErrorListenerParser.getSyntaxErrors());
        } else {
            parser.setBuildParseTree(true);
            List<ParseTree> children = expSentenceContext.children;

            List<String> parsed = new ArrayList<>();
            List<String> parsedRPN = new ArrayList<>();

            if (children != null) {
                for (int i = 0; i < children.size(); i++) {
                    printTree(parser, children.get(i), parsed);
                }
                parsedRPN = ShuntingYard.infixToPostfix(parsed);
            }

            Collections.reverse(parsedRPN);

            returnExpressionTree = buildTree(parsedRPN);
            if (returnExpressionTree != null) {
                // if single objects are the actual expression, don't change
                if (!(returnExpressionTree instanceof ConstantNode) && !(returnExpressionTree instanceof SpotFieldNode) && !(returnExpressionTree instanceof ShrimpSpeciesNode)) {
                    try {
                        returnExpressionTree.setName(expression.getName());
                    } catch (Exception e) {
                    }
                }
                returnExpressionTree.setRootExpressionTree(true);
            }
        }

        return returnExpressionTree;

    }

    public ExpressionTreeInterface buildTree(List<String> parsedRPNreversed) {
        Iterator<String> parsedRPNreversedIterator = parsedRPNreversed.iterator();

        ExpressionTreeInterface exp = null;
        ExpressionTreeInterface savedExp = null;

        boolean firstPass = true;
        while (parsedRPNreversedIterator.hasNext()) {
            String token = parsedRPNreversedIterator.next();

            if (exp != null) {
                // find next available empty left child
                exp = walkUpTreeToEmptyLeftChild(exp);
            }

            exp = walkTree(token, exp);
            if (firstPass) {
                savedExp = exp;
                firstPass = false;
            }
        }

        // so can re-parse later
        if (savedExp == null) {
            savedExp = new ExpressionTreeParsedFromExcelString("PARSE_ERROR");
        }

        if (savedExp instanceof ExpressionTreeParsedFromExcelString) {
            ((ExpressionTreeParsedFromExcelString) savedExp).setParsedRPNreversedExcelString(parsedRPNreversed);
        }

        return savedExp;
    }

    private ExpressionTreeInterface walkUpTreeToEmptyLeftChild(ExpressionTreeInterface exp) {
        ExpressionTreeInterface savedExp = exp;
        ExpressionTreeInterface expParent = exp;

        boolean didAscend = true;
        while (didAscend && (savedExp != null)) {
            if ((savedExp instanceof ExpressionTreeBuilderInterface)) {//&& (!savedExp.isTypeFunction())) {
                if (((ExpressionTreeBuilderInterface) savedExp).getCountOfChildren() == savedExp.argumentCount()) {//    2) {
                    expParent = savedExp.getParentET();
                    savedExp = expParent;
                } else {
                    didAscend = false;
                }
            } else if (savedExp instanceof ConstantNode) {
                expParent = savedExp.getParentET();
                savedExp = expParent;
            } else if (savedExp instanceof ShrimpSpeciesNode) {
                expParent = savedExp.getParentET();
                savedExp = expParent;
            } else if (savedExp.isRootExpressionTree()) {
                // when referrring to stored expression
                expParent = savedExp.getParentET();
                savedExp = expParent;
            } else {
                didAscend = false;
            }
        }

        return expParent;
    }

    private ExpressionTreeInterface walkTree(String token, ExpressionTreeInterface myExp) {
        TokenTypes tokenType = TokenTypes.getType(token);
        ExpressionTreeInterface exp = myExp;

        ExpressionTreeInterface retExpTree = null;

        switch (tokenType) {
            case OPERATOR_A:
            case OPERATOR_M:
            case OPERATOR_E:
                OperationOrFunctionInterface operation = Operation.operationFactory(OPERATIONS_MAP.get(token));
                retExpTree = new ExpressionTreeParsedFromExcelString(operation);
                break;

            case FUNCTION:
                OperationOrFunctionInterface function = Function.operationFactory(FUNCTIONS_MAP.get(token));
                isScalarFunctionFlag = function.isScalarResult();
                retExpTree = new ExpressionTreeParsedFromExcelString(function);
                break;

            case NUMBER:
                retExpTree = new ConstantNode(token, Double.parseDouble(token));
                break;

            case NAMED_CONSTANT:
                retExpTree = namedExpressionsMap.get(token);
                if (retExpTree == null) {
                    retExpTree = new ConstantNode(MISSING_EXPRESSION_STRING, token);
                }
                break;

            case NAMED_EXPRESSION:
                ExpressionTreeInterface retExpTreeKnown = namedExpressionsMap.get(token.replace("[\"", "").replace("\"]", ""));
                if (retExpTreeKnown == null) {
                    retExpTree = new ConstantNode(MISSING_EXPRESSION_STRING, token);
                } else if (((ExpressionTree) retExpTreeKnown).hasRatiosOfInterest()
                        && (((ExpressionTree) retExpTreeKnown).getLeftET() instanceof ShrimpSpeciesNode)
                        && isScalarFunctionFlag) {
                    // this is the NU switch case
                    retExpTree = retExpTreeKnown;
                } else if (((ExpressionTree) retExpTreeKnown).hasRatiosOfInterest()
                        && (((ExpressionTree) retExpTreeKnown).getLeftET() instanceof ShrimpSpeciesNode)
                        && !isScalarFunctionFlag) {
                    // this is the non NU switch case
                    retExpTree = new VariableNodeForIsotopicRatios(retExpTreeKnown.getName());
                } else if ((retExpTreeKnown instanceof ShrimpSpeciesNode) || (retExpTreeKnown instanceof SpotFieldNode)) {
                    retExpTree = retExpTreeKnown;
                } else {
                    retExpTree = new VariableNodeForPerSpotTaskExpressions(retExpTreeKnown.getName());
                }
                break;
        }

        if (exp != null) {
            // this insertion enforces correct order as children arrive in reverse polish notation order
            ((ExpressionTree) exp).addChild(0, retExpTree);
        }

        return retExpTree;
    }

    private void printTree(ExpressionsForSquid2Parser parser, ParseTree tree, List<String> parsed) {
        if (tree.getChildCount() < 1) {
            parsed.add(tree.toStringTree(parser));
        } else {
            for (int i = 0; i < tree.getChildCount(); i++) {
                printTree(parser, tree.getChild(i), parsed);
            }
        }
    }

}
