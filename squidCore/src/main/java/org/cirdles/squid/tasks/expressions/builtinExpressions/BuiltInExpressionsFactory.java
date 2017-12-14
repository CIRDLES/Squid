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
package org.cirdles.squid.tasks.expressions.builtinExpressions;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
public abstract class BuiltInExpressionsFactory {

    /**
     * TODO: these guys are hard coded for now, but need to be calculated from
     * reference materials, physical constants, etc.
     *
     * @return
     */
    public static Map<String, ExpressionTreeInterface> generateParameterConstants() {
        Map<String, ExpressionTreeInterface> parameterConstants = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        ExpressionTreeInterface constantStdUPbRatio = new ConstantNode("StdUPbRatio", 0.0906025999827849);
        parameterConstants.put(constantStdUPbRatio.getName(), constantStdUPbRatio);

        ExpressionTreeInterface constantStd_76 = new ConstantNode("Std_76", 0.0587838486664528);
        parameterConstants.put(constantStd_76.getName(), constantStd_76);

        ExpressionTreeInterface constantStdThPbRatio = new ConstantNode("StdThPbRatio", 0.0280476031222372);
        parameterConstants.put(constantStdThPbRatio.getName(), constantStdThPbRatio);

        ExpressionTreeInterface constantStdRad86fact = new ConstantNode("StdRad86fact", 0.309567309630921);
        parameterConstants.put(constantStdRad86fact.getName(), constantStdRad86fact);

        ExpressionTreeInterface constantsComm_74 = new ConstantNode("sComm_74", 15.5773361);
        parameterConstants.put(constantsComm_74.getName(), constantsComm_74);

        ExpressionTreeInterface constantsComm_64 = new ConstantNode("sComm_64", 17.821);
        parameterConstants.put(constantsComm_64.getName(), constantsComm_64);

        ExpressionTreeInterface constantsComm_84 = new ConstantNode("sComm_84", 37.5933995);
        parameterConstants.put(constantsComm_84.getName(), constantsComm_84);

        return parameterConstants;
    }

    /**
     * TODO: These should probably be segregated to the end of the expression
     * list and not be sorted each time?
     *
     * @return
     */
    public static SortedSet<Expression> generateOverCountExpressions() {
        SortedSet<Expression> overCountExpressionsOrdered = new TreeSet<>();

        Expression expressionOverCount4_6_7 = buildExpression(
                "204/206 (fr. 207)",
                "([\"207/206\"] - Std_76 ) / ( sComm_74  - (Std_76 * sComm_64)) ", true, false);
        overCountExpressionsOrdered.add(expressionOverCount4_6_7);

        Expression expressionOverCount4_6_7U = buildExpression(
                "204/206 (fr. 207) %err",
                "ABS( [%\"207/206\"] * [\"207/206\"] / ([\"207/206\"] - Std_76) )", true, false);
        overCountExpressionsOrdered.add(expressionOverCount4_6_7U);

        Expression expressionOverCount4_6_8 = buildExpression(
                "204/206 (fr. 208)",
                "( [\"208/206\"] - StdRad86fact * [\"232Th/238U\"] ) / (sComm_84 - StdRad86fact * [\"232Th/238U\"] * sComm_64 )", true, false);
        overCountExpressionsOrdered.add(expressionOverCount4_6_8);

        Expression expressionOverCountPerSec4_7 = buildExpression(
                "204 overcts/sec (fr. 207)",
                "TotalCps([\"204\"]) - TotalCps([\"BKG\"]) - [\"204/206 (fr. 207)\"] * ( TotalCps([\"206\"]) - TotalCps([\"BKG\"]))", true, false);
        overCountExpressionsOrdered.add(expressionOverCountPerSec4_7);

        Expression expressionOverCountPerSec4_8 = buildExpression(
                "204 overcts/sec (fr. 208)",
                "TotalCps([\"204\"]) - TotalCps([\"BKG\"]) - [\"204/206 (fr. 208)\"] * ( TotalCps([\"206\"]) - TotalCps([\"BKG\"]))", true, false);
        overCountExpressionsOrdered.add(expressionOverCountPerSec4_8);

        Expression expressionOverCount7CorrCalib = buildExpression(
                "7-corr primary calib const. delta%",
                "100 * ( (1 - sComm_64 * [\"204/206\"]) / (1 - sComm_64 * [\"204/206 (fr. 207)\"]) - 1 )", true, false);
        overCountExpressionsOrdered.add(expressionOverCount7CorrCalib);

        Expression expressionOverCount8CorrCalib = buildExpression(
                "8-corr primary calib const. delta%",
                "100 * ( (1 - sComm_64 * [\"204/206\"]) / (1 - sComm_64 * [\"204/206 (fr. 208)\"]) - 1 ) ", true, false);
        overCountExpressionsOrdered.add(expressionOverCount8CorrCalib);

        return overCountExpressionsOrdered;
    }

    /**
     * TODO: These should probably be segregated to the end of the expression
     * list and not be sorted each time?
     *
     * @return
     */
    public static SortedSet<Expression> generatePerSpotExpressions() {
        SortedSet<Expression> perSpotExpressionsOrdered = new TreeSet<>();

        Expression expression7Corr46 = buildExpression(
                "7-corr204Pb/206Pb",
                "Pb46cor7( [\"207/206\"], sComm_64, sComm_74, [\"207corr206Pb/238UAge\"] )", false, true);
        perSpotExpressionsOrdered.add(expression7Corr46);

        return perSpotExpressionsOrdered;
    }

    private static Expression buildExpression(String name, String excelExpression, boolean isRefMatCalc, boolean isSampleCalc) {
        Expression expression = new Expression(name, excelExpression);
        expression.setSquidSwitchNU(false);

        ExpressionTreeInterface expressionTree = expression.getExpressionTree();
        expressionTree.setSquidSwitchSTReferenceMaterialCalculation(isRefMatCalc);
        expressionTree.setSquidSwitchSAUnknownCalculation(isSampleCalc);

        expressionTree.setSquidSwitchSCSummaryCalculation(false);
        expressionTree.setSquidSpecialUPbThExpression(true);
        expressionTree.setRootExpressionTree(true);
        System.out.println(">>>>>   " + expressionTree.getName());

        return expression;
    }

}
