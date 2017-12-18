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
import static org.cirdles.squid.constants.Squid3Constants.SQUID_MEAN_PPM_PARENT_NAME;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_PPM_PARENT_EQN_NAME;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_PPM_PARENT_EQN_NAME_TH;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_PPM_PARENT_EQN_NAME_U;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_TH_U_EQN_NAME;
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
    public static Map<String, ExpressionTreeInterface> generateConstants() {
        Map<String, ExpressionTreeInterface> constants = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        ExpressionTreeInterface r238_235s = new ConstantNode("r238_235s", 137.88);
        constants.put(r238_235s.getName(), r238_235s);

        return constants;
    }

    /**
     * TODO: these guys are hard coded for now, but need to be calculated from
     * reference materials, physical constants, etc.
     *
     * @return
     */
    public static Map<String, ExpressionTreeInterface> generateParameters() {
        Map<String, ExpressionTreeInterface> parameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        ExpressionTreeInterface stdUPbRatio = new ConstantNode("StdUPbRatio", 0.0906025999827849);
        parameters.put(stdUPbRatio.getName(), stdUPbRatio);

        ExpressionTreeInterface stdPpmU = new ConstantNode("Std_ppmU", 903);
        parameters.put(stdPpmU.getName(), stdPpmU);

        ExpressionTreeInterface std_76 = new ConstantNode("Std_76", 0.0587838486664528);
        parameters.put(std_76.getName(), std_76);

        ExpressionTreeInterface stdThPbRatio = new ConstantNode("StdThPbRatio", 0.0280476031222372);
        parameters.put(stdThPbRatio.getName(), stdThPbRatio);

        ExpressionTreeInterface stdRad86fact = new ConstantNode("StdRad86fact", 0.309567309630921);
        parameters.put(stdRad86fact.getName(), stdRad86fact);

        ExpressionTreeInterface sComm_74 = new ConstantNode("sComm_74", 15.5773361);
        parameters.put(sComm_74.getName(), sComm_74);

        ExpressionTreeInterface sComm_64 = new ConstantNode("sComm_64", 17.821);
        parameters.put(sComm_64.getName(), sComm_64);

        ExpressionTreeInterface sComm_84 = new ConstantNode("sComm_84", 37.5933995);
        parameters.put(sComm_84.getName(), sComm_84);

        return parameters;
    }

    public static SortedSet<Expression> generatePpmUandPpmTh(String parentNuclide) {
        SortedSet<Expression> concentrationExpressionsOrdered = new TreeSet<>();

        // TODO: promote this and tie to physical constants model
        String uConstant = "((238/232) * r238_235s / (r238_235s - 1.0))";
        
        String ppmEqnName = SQUID_PPM_PARENT_EQN_NAME_U;
        String ppmEquation = "[\"" + SQUID_PPM_PARENT_EQN_NAME + "\"] / [\"" + SQUID_MEAN_PPM_PARENT_NAME + "\"] * Std_ppmU";

        String ppmOtherEqnName = SQUID_PPM_PARENT_EQN_NAME_TH;
        String ppmOtherEqn = "[\"" + SQUID_PPM_PARENT_EQN_NAME_U + "\"] * [\"" + SQUID_TH_U_EQN_NAME + "\"] / " + uConstant;

        if (parentNuclide.contains("232")) {
            ppmEqnName = SQUID_PPM_PARENT_EQN_NAME_TH;

            ppmOtherEqnName = SQUID_PPM_PARENT_EQN_NAME_U;
            ppmOtherEqn = "[\"" + SQUID_PPM_PARENT_EQN_NAME_TH + "\"] / [\"" + SQUID_TH_U_EQN_NAME + "\"] * " + uConstant;
        }

        Expression expressionPpmU = buildExpression(
                ppmEqnName,
                ppmEquation, true, true);
        concentrationExpressionsOrdered.add(expressionPpmU);

        Expression expressionPpmTh = buildExpression(
                ppmOtherEqnName,
                ppmOtherEqn, true, true);
        concentrationExpressionsOrdered.add(expressionPpmTh);

        return concentrationExpressionsOrdered;
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
    public static SortedSet<Expression> generatePerSpotPbCorrections() {
        SortedSet<Expression> perSpotPbCorrectionsOrdered = new TreeSet<>();

        Expression expression7Corr46 = buildExpression(
                "7-corr204Pb/206Pb",
                "Pb46cor7( [\"207/206\"], sComm_64, sComm_74, [\"207corr206Pb/238UAge\"] )", false, true);
        perSpotPbCorrectionsOrdered.add(expression7Corr46);

        Expression expression8Corr46 = buildExpression(
                "8-corr204Pb/206Pb",
                "Pb46cor8( [\"208/206\"], [\"232Th/238U\"], sComm_64, sComm_84, [\"208corr206Pb/238UAge\"] )", false, true);
        perSpotPbCorrectionsOrdered.add(expression8Corr46);

        // for ref materials
        Expression expression4corCom206 = buildExpression(
                "4-corr%com206",
                "100 * sComm_64 * [\"204/206\"]", true, true);
        perSpotPbCorrectionsOrdered.add(expression4corCom206);

        Expression expression7corCom206 = buildExpression(
                "7-corr%com206",
                "100 * sComm_64 * [\"204/206 (fr. 207)\"]", true, false);
        perSpotPbCorrectionsOrdered.add(expression7corCom206);

        Expression expression8corCom206 = buildExpression(
                "8-corr%com206",
                "100 * sComm_64 * [\"204/206 (fr. 208)\"]", true, false);
        perSpotPbCorrectionsOrdered.add(expression8corCom206);

        Expression expression4corCom208 = buildExpression(
                "4-corr%com208",
                "100 * sComm_84 / [\"208/206\"] * [\"204/206\"]", true, true);
        perSpotPbCorrectionsOrdered.add(expression4corCom208);

        Expression expression7corCom208 = buildExpression(
                "7-corr%com208",
                "100 * sComm_84 / [\"208/206\"] * [\"204/206 (fr. 207)\"]", true, false);
        perSpotPbCorrectionsOrdered.add(expression7corCom208);

        // for samples
        Expression expression7corCom206S = buildExpression(
                "7-corr%com206S",
                "100 * sComm_64 * [\"7-corr204Pb/206Pb\"]", false, true);
        perSpotPbCorrectionsOrdered.add(expression7corCom206S);

        Expression expression8corCom206S = buildExpression(
                "8-corr%com206S",
                "100 * sComm_64 * [\"8-corr204Pb/206Pb\"]", false, true);
        perSpotPbCorrectionsOrdered.add(expression8corCom206S);

        return perSpotPbCorrectionsOrdered;
    }

    private static Expression buildExpression(String name, String excelExpression, boolean isRefMatCalc, boolean isSampleCalc) {
        Expression expression = new Expression(name, excelExpression);
        expression.setSquidSwitchNU(false);

        ExpressionTreeInterface expressionTree = expression.getExpressionTree();
        expressionTree.setSquidSwitchSTReferenceMaterialCalculation(isRefMatCalc);
        expressionTree.setSquidSwitchSAUnknownCalculation(isSampleCalc);

        expressionTree.setSquidSwitchConcentrationReferenceMaterialCalculation(false);
        expressionTree.setSquidSwitchSCSummaryCalculation(false);
        expressionTree.setSquidSpecialUPbThExpression(true);
        expressionTree.setRootExpressionTree(true);
        System.out.println(">>>>>   " + expressionTree.getName());

        return expression;
    }

}
