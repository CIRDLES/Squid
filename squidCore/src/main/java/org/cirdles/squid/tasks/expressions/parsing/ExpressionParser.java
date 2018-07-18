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
package org.cirdles.squid.tasks.expressions.parsing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.CharStreams;
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
import static org.cirdles.squid.tasks.expressions.parsing.ShuntingYard.TokenTypes.NAMED_EXPRESSION_INDEXED;
import org.cirdles.squid.tasks.expressions.spots.SpotFieldNode;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForIsotopicRatios;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForPerSpotTaskExpressions;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForSummary;

/**
 *
 * @author James F. Bowring
 */
public class ExpressionParser {

    private Map<String, ExpressionTreeInterface> namedExpressionsMap;
    private boolean eqnSwitchNU;

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
        eqnSwitchNU = expression.isSquidSwitchNU();

        ExpressionTreeInterface returnExpressionTree = new ExpressionTree(expression.getName());

        // Get our lexer
        // updated due to deprecations Jul 2018
        ExpressionsForSquid2Lexer lexer;
        try {
            InputStream stream = new ByteArrayInputStream(expression.getExcelExpressionString().getBytes(StandardCharsets.UTF_8));
            lexer = new ExpressionsForSquid2Lexer(CharStreams.fromStream(stream, StandardCharsets.UTF_8));

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

                // detect if top-level singleton and if so, wrap in expression with hidden Value operation, '$$'
                if (parsedRPN.size() == 1) {
                    parsedRPN.add(0, "$$");
                }

                returnExpressionTree = buildTree(parsedRPN);

                // if single objects are the actual expression, don't change
                if (!(returnExpressionTree instanceof SpotFieldNode)
                        && !(returnExpressionTree instanceof ShrimpSpeciesNode)
                        && !(returnExpressionTree instanceof VariableNodeForIsotopicRatios)
                        && !(returnExpressionTree instanceof VariableNodeForSummary)
                        && returnExpressionTree.isValid()) {

                    returnExpressionTree.setName(expression.getName());

                }

                // be sure top level expression is root
                returnExpressionTree.setRootExpressionTree(!(((ExpressionTree) returnExpressionTree).getLeftET() instanceof ShrimpSpeciesNode));

            }
        } catch (IOException iOException) {
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

            if (TokenTypes.getType(token).compareTo(TokenTypes.FORMATTER) != 0) {
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
        }

        // so can re-parse later
        if (savedExp == null) {
            savedExp = new ExpressionTree("PARSE_ERROR");
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
        int index = 0;

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

            case NAMED_EXPRESSION_INDEXED:
                // form is ["XXX"][n], remove indexing and capture value of index
                // the single integer index is next to last character
                // we are not supporting for ratios for now as they are show as ratios in MathML
                String indexString = token.substring(token.length() - 2, token.length() - 1);
                index = Integer.parseInt(indexString);
                token = token.replaceFirst("\\[\\d\\]", "");

            case NAMED_EXPRESSION:
                // handle special cases of array index references and ± references to uncertainty
                String uncertaintyDirective = "";
                if (token.startsWith("[±")) {
                    uncertaintyDirective = "±";
                } else if (token.startsWith("[%")) {
                    uncertaintyDirective = "%";
                }
                String expressionName = token.replace("[\"", "").replace("[±\"", "").replace("[%\"", "").replace("\"]", "");
                ExpressionTreeInterface retExpTreeKnown = namedExpressionsMap.get(expressionName);

                if (retExpTreeKnown == null) {
                    retExpTree = new ConstantNode(MISSING_EXPRESSION_STRING, token);
                    // let's see if we have an array reference in the form of SUMMARY named_expression00
                    // this would be hard to catch with regex since ratios fit the pattern too
                    index = 0;
                    if (expressionName.length() > 2) {
                        String lastTwo = expressionName.substring(expressionName.length() - 2);
                        if (ShuntingYard.isNumber(lastTwo)) {
                            // index = first digit - 1 (converting from vertical 1-based excel to horiz 0-based java
                            index = Integer.parseInt(lastTwo.substring(0, 1)) - 1;
                            String baseExpressionName = expressionName.substring(0, expressionName.length() - 2);
                            if (index >= 0) {
                                retExpTreeKnown = namedExpressionsMap.get(baseExpressionName);
                                if (retExpTreeKnown != null) {
                                    retExpTree = new VariableNodeForSummary(baseExpressionName, index);
                                }
                            }
                        }
                    }
                } else if (retExpTreeKnown instanceof ConstantNode) {
                    retExpTree = retExpTreeKnown;
                } else if (((ExpressionTree) retExpTreeKnown).hasRatiosOfInterest()
                        && (((ExpressionTree) retExpTreeKnown).getLeftET() instanceof ShrimpSpeciesNode)
                        && eqnSwitchNU) {
                    // this is the NU switch case for ratio where ratio is processed specially vs below where it is just looked up
                    retExpTree = retExpTreeKnown;

                } else if (((ExpressionTree) retExpTreeKnown).hasRatiosOfInterest()
                        && (((ExpressionTree) retExpTreeKnown).getLeftET() instanceof ShrimpSpeciesNode)
                        && !eqnSwitchNU) {
                    // this is the non NU switch case - ratios just looked up - math is already done
                    retExpTree = new VariableNodeForIsotopicRatios(
                            retExpTreeKnown.getName(),
                            (ShrimpSpeciesNode) ((ExpressionTree) retExpTreeKnown).getLeftET(),
                            (ShrimpSpeciesNode) ((ExpressionTree) retExpTreeKnown).getRightET(),
                            uncertaintyDirective);

                } else if ((retExpTreeKnown instanceof ShrimpSpeciesNode)
                        || (retExpTreeKnown instanceof SpotFieldNode)) {
                    retExpTree = retExpTreeKnown;
                    retExpTree.setUncertaintyDirective(uncertaintyDirective);

                } else if (retExpTreeKnown.isSquidSwitchSCSummaryCalculation()) {
                    retExpTree = new VariableNodeForSummary(retExpTreeKnown.getName(), index, uncertaintyDirective);

                } else {
                    retExpTree = new VariableNodeForPerSpotTaskExpressions(retExpTreeKnown.getName(), uncertaintyDirective, index);
                }
                break;

            default:
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
