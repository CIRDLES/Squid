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
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * @author James F. Bowring
 */
public class ShuntingYard {

    private static final Pattern numberPattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        List<String> infixList = new ArrayList<>();

//        // Input: 3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3
//        infixList.add("3");
//        infixList.add("+");
//        infixList.add("4");
//        infixList.add("*");
//        infixList.add("2");
//        infixList.add("/");
//        infixList.add("(");
//        infixList.add("1");
//        infixList.add("-");
//        infixList.add("5");
//        infixList.add(")");
//        infixList.add("^");
//        infixList.add("2");
//        infixList.add("^");
//        infixList.add("3");
//        infixList.add("(");
//        infixList.add("1");
//        infixList.add("/");
//        infixList.add("2");
//        infixList.add(")");
//        infixList.add("/");
//        infixList.add("(");
//        infixList.add("c");
//        infixList.add("/");
//        infixList.add("4");
//        infixList.add(")");
//        infixList.add("2");
//        infixList.add("+");
//        infixList.add("ln");
//        infixList.add("(");
//        infixList.add("1");
//        infixList.add(")");
//        infixList.add("/");
//        infixList.add("[");
//        infixList.add("\"");
//        infixList.add("a");
//        infixList.add("b");
//        infixList.add("c");
//        infixList.add("\"");
        infixList.add("w");
        infixList.add("+");
        infixList.add("w");
        System.out.println("Shunt " + infixToPostfix(infixList));

        //1+(ln(3) +4)
        //ln  (  ln(3) + ln(4)  )  
        // ln(1)/(ln(3) + 1)
    }

    /**
     * @see
     * https://en.wikipedia.org/wiki/Reverse_Polish_notation#The_postfix_algorithm
     * @see
     * https://blog.kallisti.net.nz/2008/02/extension-to-the-shunting-yard-algorithm-to-allow-variable-numbers-of-arguments-to-functions/
     * @see http://www.reedbeta.com/blog/the-shunting-yard-algorithm/
     * @see https://en.wikipedia.org/wiki/Shunting-yard_algorithm
     * @param infix
     * @return
     */
    public static List<String> infixToPostfix(List<String> infix) {
        Stack<String> operatorStack = new Stack<>();
        List<String> outputQueue = new ArrayList<>();
        Stack<Boolean> wereValues = new Stack<>();
        Stack<Integer> argCount = new Stack<>();
        boolean lastWasOperationOrFunction = true;

        for (String token : infix) {
            // classify token
            TokenTypes tokenType = TokenTypes.getType(token);
            switch (tokenType) {
                case OPERATOR_A:
                    /* while there is an operator token o2, at the top of the operator stack and either
                    o1 is left-associative and its precedence is less than or equal to that of o2, or
                    o1 is right associative, and has precedence less than that of o2,
                    pop o2 off the operator stack, onto the output queue;
                    at the end of iteration push o1 onto the operator stack.
                     */

                    boolean keepLooking = true;

                    // allow for negative expressions by inserting -1 *
                    if (lastWasOperationOrFunction && token.compareTo("-") == 0) {
                        outputQueue.add("-1");
                        operatorStack.push("*");
                        lastWasOperationOrFunction = true;
                    } else {
                        while (!operatorStack.empty() && keepLooking) {
                            TokenTypes peek = TokenTypes.getType(operatorStack.peek());
                            if ((peek.compareTo(TokenTypes.OPERATOR_A) == 0)
                                    || (peek.compareTo(TokenTypes.OPERATOR_M) == 0)
                                    || (peek.compareTo(TokenTypes.OPERATOR_E) == 0)) {
                                outputQueue.add(operatorStack.pop());
                            } else {
                                keepLooking = false;
                            }
                        }
                        operatorStack.push(token);
                        lastWasOperationOrFunction = true;
                    }
                    break;
                case OPERATOR_M:
                    /* while there is an operator token o2, at the top of the operator stack and either
                    o1 is left-associative and its precedence is less than or equal to that of o2, or
                    o1 is right associative, and has precedence less than that of o2,
                    pop o2 off the operator stack, onto the output queue;
                    at the end of iteration push o1 onto the operator stack.
                     */
                    keepLooking = true;
                    while (!operatorStack.empty() && keepLooking) {
                        TokenTypes peek = TokenTypes.getType(operatorStack.peek());
                        if ((peek.compareTo(TokenTypes.OPERATOR_M) == 0)
                                || (peek.compareTo(TokenTypes.OPERATOR_E) == 0)) {
                            outputQueue.add(operatorStack.pop());
                        } else {
                            keepLooking = false;
                        }
                    }
                    operatorStack.push(token);
                    lastWasOperationOrFunction = true;
                    break;
                case OPERATOR_E:
                    /* while there is an operator token o2, at the top of the operator stack and either
                    o1 is left-associative and its precedence is less than or equal to that of o2, or
                    o1 is right associative, and has precedence less than that of o2,
                    pop o2 off the operator stack, onto the output queue;
                    at the end of iteration push o1 onto the operator stack.
                     */
                    operatorStack.push(token);
                    lastWasOperationOrFunction = true;
                    break;
                case LEFT_PAREN:
                    operatorStack.push(token);
                    lastWasOperationOrFunction = true;
                    break;
                case RIGHT_PAREN:
                    /* Until the token at the top of the stack is a left parenthesis, pop operators off the stack onto the output queue.
                    Pop the left parenthesis from the stack, but not onto the output queue.
                    If the token at the top of the stack is a function token, pop it onto the output queue.
                    If the stack runs out without finding a left parenthesis, then there are mismatched parentheses.
                     */
                    keepLooking = true;
                    while (!operatorStack.empty() && keepLooking) {
                        TokenTypes peek = TokenTypes.getType(operatorStack.peek());
                        if ((peek.compareTo(TokenTypes.OPERATOR_A) == 0)
                                || (peek.compareTo(TokenTypes.OPERATOR_M) == 0)
                                || (peek.compareTo(TokenTypes.OPERATOR_E) == 0)) {
                            outputQueue.add(operatorStack.pop());
                        } else {
                            keepLooking = false;
                            if (peek.compareTo(TokenTypes.LEFT_PAREN) == 0) {
                                operatorStack.pop();
                                try {
                                    String func;
                                    try {
                                        peek = TokenTypes.getType(operatorStack.peek());
                                        if (peek.compareTo(TokenTypes.FUNCTION) == 0) {
                                            func = operatorStack.pop();
                                            int a = argCount.pop();
                                            boolean w = wereValues.pop();
                                            if (w) {
                                                a++;
                                                String funcWithArgCount = func + ":" + String.valueOf(a);
                                                outputQueue.add(func);// temp simplify(funcWithArgCount);
                                            }
                                        }
                                    } catch (Exception e) {
                                    }
//                                    outputQueue.add(func);// temp simplify(funcWithArgCount);
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                    lastWasOperationOrFunction = false;
                    break;
                case CONSTANT:
                    outputQueue.add(token);
                    if (!wereValues.empty()) {
                        wereValues.pop();
                        wereValues.push(true);
                    }
                    lastWasOperationOrFunction = false;
                    break;
                case VARIABLE:
                    outputQueue.add(token);
                    if (!wereValues.empty()) {
                        wereValues.pop();
                        wereValues.push(true);
                    }
                    lastWasOperationOrFunction = false;
                    break;
                case FUNCTION:
                    operatorStack.push(token);
                    if (!wereValues.empty()) {
                        wereValues.pop();
                        wereValues.push(true);
                    }
                    wereValues.push(false);
                    argCount.push(0);
                    lastWasOperationOrFunction = true;
                    break;
                case COMMA:
                    /*If the token is a function argument separator (e.g., a comma):
                        Until the token at the top of the stack is a left parenthesis, 
                    pop operators off the stack onto the output queue. If no left parentheses 
                    are encountered, either the separator was misplaced or parentheses were mismatched.
                     */
                    keepLooking = true;
                    while (!operatorStack.empty() && keepLooking) {
                        TokenTypes peek = TokenTypes.getType(operatorStack.peek());
                        if (peek.compareTo(TokenTypes.LEFT_PAREN) == 0) {
                            keepLooking = false;
                        } else {
                            outputQueue.add(operatorStack.pop());
                        }
                    }

                    boolean w = false;
                    try {
                        w = wereValues.pop();
                    } catch (Exception e) {
                    }
                    if (w) {
                        int a = 0;
                        try {
                            a = argCount.pop();
                        } catch (Exception e) {
                        }
                        argCount.push(a + 1);
                    }
                    wereValues.push(false);
                    lastWasOperationOrFunction = false;
                    break;

                case NAMED_EXPRESSION:
                    outputQueue.add(token);
                    if (!wereValues.empty()) {
                        wereValues.pop();
                        wereValues.push(true);
                    }
                    lastWasOperationOrFunction = false;
                    break;
                default:
                    break;
            }
        }

        while (!operatorStack.empty()) {
            outputQueue.add(operatorStack.pop());
        }

        return outputQueue;
    }

    /**
     *
     */
    public enum TokenTypes {

        /**
         *
         */
        OPERATOR_A,

        /**
         *
         */
        OPERATOR_M,

        /**
         *
         */
        OPERATOR_E,

        /**
         *
         */
        LEFT_PAREN,

        /**
         *
         */
        RIGHT_PAREN,

        /**
         *
         */
        CONSTANT,

        /**
         *
         */
        VARIABLE,

        /**
         *
         */
        FUNCTION,

        /**
         *
         */
        NAMED_EXPRESSION,

        /**
         *
         */
        COMMA;

        private TokenTypes() {
        }

        /**
         *
         * @param token
         * @return
         */
        public static TokenTypes getType(String token) {
            TokenTypes retVal = VARIABLE;

            if ("|+|-|==|<|<=|>|>=|<>|".contains("|" + token + "|")) {
                retVal = OPERATOR_A;
            } else if ("*/".contains(token)) {
                retVal = OPERATOR_M;
            } else if ("^".contains(token)) {
                retVal = OPERATOR_E;
            } else if ("(".contains(token)) {
                retVal = LEFT_PAREN;
            } else if (")".contains(token)) {
                retVal = RIGHT_PAREN;
            } else if (token.equals(",")) {
                retVal = COMMA;
            } else if ("|ln|Ln|sqrt|Sqrt|exp|Exp|RobReg|robReg|robreg|and|if|".contains("|" + token + "|")) {
                retVal = FUNCTION;
            } else if (token.matches("\\[\"(.*?)\"\\]")) {
                retVal = NAMED_EXPRESSION;
            } else if (isNumber(token)) {
                retVal = CONSTANT;

            }

            return retVal;
        }
    }

    /**
     *
     * @param string
     * @return
     */
    public static boolean isNumber(String string) {
        return string != null && numberPattern.matcher(string).matches();
    }
}
