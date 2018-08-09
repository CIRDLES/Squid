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
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.tasks.expressions.Expression;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.EXP_8CORR_238_206_STAR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_ASSIGNED_PBU_EXTERNAL_ONE_SIGMA_PCT_ERR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_MEAN_PPM_PARENT_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_PPM_PARENT_EQN_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_PPM_PARENT_EQN_NAME_TH;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_PPM_PARENT_EQN_NAME_TH_S;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_PPM_PARENT_EQN_NAME_U;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_PRIMARY_UTH_EQN_NAME_TH;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_PRIMARY_UTH_EQN_NAME_U;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_TH_U_EQN_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_TH_U_EQN_NAME_S;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_TOTAL_206_238_NAME;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_TOTAL_206_238_NAME_S;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_TOTAL_208_232_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_TOTAL_208_232_NAME_S;

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

        ExpressionTreeInterface r238_235s = new ConstantNode("r238_235s", Squid3Constants.uRatio);//          137.88);
        constants.put(r238_235s.getName(), r238_235s);

        ExpressionTreeInterface squidTrue = new ConstantNode("TRUE", true);
        constants.put(squidTrue.getName(), squidTrue);

        ExpressionTreeInterface squidFalse = new ConstantNode("FALSE", false);
        constants.put(squidFalse.getName(), squidFalse);

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

        ExpressionTreeInterface lambda238 = new ConstantNode("lambda238", Squid3Constants.lambda238);
        parameters.put(lambda238.getName(), lambda238);

        ExpressionTreeInterface lambda232 = new ConstantNode("lambda232", Squid3Constants.lambda232);
        parameters.put(lambda232.getName(), lambda232);

        ExpressionTreeInterface L859 = new ConstantNode("L859", 0.859);
        parameters.put("L859", L859);

        ExpressionTreeInterface extPErr = new ConstantNode(SQUID_ASSIGNED_PBU_EXTERNAL_ONE_SIGMA_PCT_ERR, 0.75);
        parameters.put(SQUID_ASSIGNED_PBU_EXTERNAL_ONE_SIGMA_PCT_ERR, extPErr);

        return parameters;
    }

    public static SortedSet<Expression> generateParameterValues() {
        SortedSet<Expression> parameterValues = new TreeSet<>();

        Expression expressionsComm_64 = buildExpression("sComm_64",
                "17.821", true, true, true);
        expressionsComm_64.setParameterValue(true);
        parameterValues.add(expressionsComm_64);

        Expression expressionsComm_74 = buildExpression("sComm_74",
                "15.5773361", true, true, true);
        expressionsComm_74.setParameterValue(true);
        parameterValues.add(expressionsComm_74);

        Expression expressionsComm_84 = buildExpression("sComm_84",
                "37.5933995", true, true, true);
        expressionsComm_84.setParameterValue(true);
        parameterValues.add(expressionsComm_84);

        Expression expressionsComm_76 = buildExpression("sComm_76",
                "0.8741", true, true, true);
        expressionsComm_76.setParameterValue(true);
        parameterValues.add(expressionsComm_76);

        Expression expressionsComm_86 = buildExpression("sComm_86",
                "2.1095", true, true, true);
        expressionsComm_86.setParameterValue(true);
        parameterValues.add(expressionsComm_86);

        Expression expressionsComm_68 = buildExpression("sComm_68",
                "1.0 / sComm_86", true, true, true);
        expressionsComm_68.setParameterValue(true);
        parameterValues.add(expressionsComm_68);

        return parameterValues;

    }

    public static SortedSet<Expression> generateReferenceMaterialValues() {
        SortedSet<Expression> referenceMaterialValues = new TreeSet<>();

        Expression expressionStdUConcPpm = buildExpression("StdUConcPpm",
                "903", true, true, true);
        expressionStdUConcPpm.setReferenceMaterialValue(true);
        referenceMaterialValues.add(expressionStdUConcPpm);

        Expression expressionStdThConcPpm = buildExpression("StdThConcPpm",
                "0", true, true, true);
        expressionStdThConcPpm.setReferenceMaterialValue(true);
        referenceMaterialValues.add(expressionStdThConcPpm);

        Expression expressionStdAgeUPb = buildExpression("StdAgeUPb",
                "559.1e6", true, true, true);
        expressionStdAgeUPb.setReferenceMaterialValue(true);
        referenceMaterialValues.add(expressionStdAgeUPb);

        Expression expressionStdAgeThPb = buildExpression("StdAgeThPb",
                "559.1e6", true, true, true);
        expressionStdAgeThPb.setReferenceMaterialValue(true);
        referenceMaterialValues.add(expressionStdAgeThPb);

        Expression expressionStdAgePbPb = buildExpression("StdAgePbPb",
                "559.1e6", true, true, true);
        expressionStdAgePbPb.setReferenceMaterialValue(true);
        referenceMaterialValues.add(expressionStdAgePbPb);

        Expression expressionStdUPbRatio = buildExpression("StdUPbRatio",
                "EXP(lambda238 * StdAgeUPb)-1", true, true, true);
        expressionStdUPbRatio.setReferenceMaterialValue(true);
        referenceMaterialValues.add(expressionStdUPbRatio);

        Expression expressionStdThPbRatio = buildExpression("StdThPbRatio",
                "EXP(lambda232 * StdAgeThPb)-1", true, true, true);
        expressionStdThPbRatio.setReferenceMaterialValue(true);
        referenceMaterialValues.add(expressionStdThPbRatio);

        Expression expressionStd_76 = buildExpression("Std_76",
                "Pb76( StdAgePbPb )", true, true, true);
        expressionStd_76.setReferenceMaterialValue(true);
        referenceMaterialValues.add(expressionStd_76);

        Expression expressionStdRad86fact = buildExpression("StdRad86fact",
                "StdThPbRatio / StdUPbRatio", true, true, true);
        expressionStdRad86fact.setReferenceMaterialValue(true);
        referenceMaterialValues.add(expressionStdRad86fact);

        return referenceMaterialValues;
    }

    public static SortedSet<Expression> generatePlaceholderExpressions(String parentNuclide, boolean isDirectAltPD) {
        SortedSet<Expression> placeholderExpressions = new TreeSet<>();

        if (isDirectAltPD) {
            Expression expressionSQUID_TH_U_EQN_NAME = buildExpression(SQUID_TH_U_EQN_NAME,
                    "1", true, false, false);
            placeholderExpressions.add(expressionSQUID_TH_U_EQN_NAME);

            Expression expressionSQUID_TH_U_EQN_NAMEs = buildExpression(SQUID_TH_U_EQN_NAME_S,
                    "1", false, true, false);
            placeholderExpressions.add(expressionSQUID_TH_U_EQN_NAMEs);
//
//            Expression expressionSQUID_TH_U_EQN_NAMEPerr = buildExpression(SQUID_TH_U_EQN_NAME + " %err",
//                    "1", true, false, false);
//            placeholderExpressions.add(expressionSQUID_TH_U_EQN_NAMEPerr);
//
//            Expression expressionSQUID_TH_U_EQN_NAMEsPerr = buildExpression(SQUID_TH_U_EQN_NAME_S + " %err",
//                    "1", false, true, false);
//            placeholderExpressions.add(expressionSQUID_TH_U_EQN_NAMEsPerr);
        }

        // placeholder expressions
        Expression expressionSQUID_TOTAL_206_238_NAME = buildExpression(SQUID_TOTAL_206_238_NAME,
                "1", false, true, false);
        placeholderExpressions.add(expressionSQUID_TOTAL_206_238_NAME);

        Expression expressionSQUID_TOTAL_206_238_NAME_S = buildExpression(SQUID_TOTAL_206_238_NAME_S,
                "1", false, true, false);
        placeholderExpressions.add(expressionSQUID_TOTAL_206_238_NAME_S);

        Expression expressionSQUID_TOTAL_208_232_NAME = buildExpression(SQUID_TOTAL_208_232_NAME,
                "1", false, true, false);
        placeholderExpressions.add(expressionSQUID_TOTAL_208_232_NAME);

        Expression expressionSQUID_TOTAL_208_232_NAME_S = buildExpression(SQUID_TOTAL_208_232_NAME_S,
                "1", false, true, false);
        placeholderExpressions.add(expressionSQUID_TOTAL_208_232_NAME_S);

        return placeholderExpressions;

    }

    /**
     * Ludwig Q3 - additional equations to calculate 232Th/238U Includes Ludwig
     * Q4 definitions calls
     *
     * @param parentNuclide
     * @param isDirectAltPD
     * @return
     */
    public static SortedSet<Expression> generatePpmUandPpmTh(String parentNuclide, boolean isDirectAltPD) {
        SortedSet<Expression> concentrationExpressionsOrdered = new TreeSet<>();

        // ppmU calcs belong to both cases of isDirectAltPD
        Expression expressionPpmU = buildExpression(SQUID_PPM_PARENT_EQN_NAME_U,
                "[\"" + SQUID_PPM_PARENT_EQN_NAME + "\"] / [\"" + SQUID_MEAN_PPM_PARENT_NAME + "\"] * StdUConcPpm", true, true, false);
        concentrationExpressionsOrdered.add(expressionPpmU);

        if (!isDirectAltPD) {
            // TODO: promote this and tie to physical constants model
            // handles SecondaryParentPpmFromThU
            String uConstant = "1.033"; // 1.033 gives perfect fidelity to Squid 2.5 //((238/232) * r238_235s / (r238_235s - 1.0))";
            Expression expressionPpmTh = buildExpression(SQUID_PPM_PARENT_EQN_NAME_TH,
                    "[\"" + SQUID_PPM_PARENT_EQN_NAME_U + "\"] * [\"" + SQUID_TH_U_EQN_NAME + "\"] / " + uConstant, true, false, false);
            concentrationExpressionsOrdered.add(expressionPpmTh);

            Expression expressionPpmThS = buildExpression(SQUID_PPM_PARENT_EQN_NAME_TH_S,
                    "[\"" + SQUID_PPM_PARENT_EQN_NAME_U + "\"] * [\"" + SQUID_TH_U_EQN_NAME_S + "\"] / " + uConstant, false, true, false);
            concentrationExpressionsOrdered.add(expressionPpmThS);

            if (!parentNuclide.contains("232")) {
                // does contain Uranium such as 238
                concentrationExpressionsOrdered.addAll(generate204207MeansAndAgesForRefMaterialsU());
                concentrationExpressionsOrdered.addAll(generate208MeansAndAgesForRefMaterialsU());
            } else {
                concentrationExpressionsOrdered.addAll(generate204207MeansAndAgesForRefMaterialsTh());
            }

        } else {
            // directlAltPD is true
            // this code for ppmTh comes from SQ2.50 Procedral Framework: Part 5
            // see: https://github.com/CIRDLES/ET_Redux/wiki/SQ2.50-Procedural-Framework:-Part-5

            Expression expressionPpmTh = buildExpression(SQUID_PPM_PARENT_EQN_NAME_TH,
                    "[\"232Th/238U\"] * [\"ppmU\"] * 0.9678", true, false, false);
            concentrationExpressionsOrdered.add(expressionPpmTh);

            Expression expressionPpmThS = buildExpression(SQUID_PPM_PARENT_EQN_NAME_TH_S,
                    "[\"232Th/238US\"] * [\"ppmU\"] * 0.9678", false, true, false);
            concentrationExpressionsOrdered.add(expressionPpmThS);

            concentrationExpressionsOrdered.addAll(generate204207MeansAndAgesForRefMaterialsU());
            concentrationExpressionsOrdered.addAll(generate204207MeansAndAgesForRefMaterialsTh());
            // for unknown samples
//            concentrationExpressionsOrdered.addAll(generatePbIsotope204207CorrectionsForU());
//            concentrationExpressionsOrdered.addAll(generatePbIsotope204207CorrectionsForTh());

            // math for ThUfromA1A2 follows here
            // for 204Pb ref material
            String exp238 = "( EXP ( Lambda238 * [\"4-corr 206Pb/238U Age\"] ) - 1 )";
            String exp232 = "( EXP ( Lambda232 * [\"4-corr 208Pb/232Th Age\"] ) - 1 )";

            Expression expression4corrSQUID_TH_U_EQN_NAME = buildExpression("4-corr" + SQUID_TH_U_EQN_NAME,
                    "ValueModel("
                    + "[\"4-corr208Pb*/206Pb*\"] * " + exp238 + " / " + exp232 + ","
                    + "SQRT( [%\"4-corr208Pb*/206Pb*\"]^2 + \n"
                    + "[%\"4-corr 206Pb/238Ucalibr.const\"]^2 +\n"
                    + "[%\"4-corr 208Pb/232Thcalibr.const\"]^2 ),"
                    + "false)", true, false, false);
            concentrationExpressionsOrdered.add(expression4corrSQUID_TH_U_EQN_NAME);

            // for 207Pb ref material
            exp238 = "( EXP ( Lambda238 * [\"7-corr 206Pb/238U Age\"] ) - 1 )";
            exp232 = "( EXP ( Lambda232 * [\"7-corr 208Pb/232Th Age\"] ) - 1 )";

            Expression expression7corrSQUID_TH_U_EQN_NAME = buildExpression("7-corr" + SQUID_TH_U_EQN_NAME,
                    "ValueModel([\"7-corr208Pb*/206Pb*\"] * " + exp238 + " / " + exp232 + ","
                    + "SQRT( [\"7-corr208Pb*/206Pb* %err\"]^2 + \n"
                    + "[%\"7-corr 206Pb/238Ucalibr.const\"]^2 + \n"
                    + "[%\"7-corr 208Pb/232Thcalibr.const\"]^2 ),"
                    + "false)", true, false, false);
            concentrationExpressionsOrdered.add(expression7corrSQUID_TH_U_EQN_NAME);

            // for samples
            Expression expression4corrSQUID_TH_U_EQN_NAMEs = buildExpression("4-corr" + SQUID_TH_U_EQN_NAME_S,
                    "ValueModel("
                    + "[\"208/206\"] * [\"4-corrTotal 206Pb/238US\"] / [\"4-corrTotal 208Pb/232ThS\"],"
                    + "SQRT( [%\"208/206\"]^2 + [%\"4-corrTotal 206Pb/238US\"]^2 + \n"
                    + "[%\"4-corrTotal 208Pb/232ThS\"]^2 ),"
                    + "false)", false, true, false);
            concentrationExpressionsOrdered.add(expression4corrSQUID_TH_U_EQN_NAMEs);

            Expression expression7corrSQUID_TH_U_EQN_NAMEs = buildExpression("7-corr" + SQUID_TH_U_EQN_NAME_S,
                    "ValueModel("
                    + "[\"208/206\"] * [\"7-corrTotal 206Pb/238US\"] / [\"7-corrTotal 208Pb/232ThS\"],"
                    + "SQRT( [%\"208/206\"]^2 + [%\"7-corrTotal 206Pb/238US\"]^2 + \n"
                    + "[%\"7-corrTotal 208Pb/232ThS\"]^2 ),"
                    + "false)", false, true, false);
            concentrationExpressionsOrdered.add(expression7corrSQUID_TH_U_EQN_NAMEs);

        } // end test of directAltD

        // perm 1,2,4 expressions here
        // need discussion abot perm3
        //if (!parentNuclide.contains("232") || isDirectAltPD) {
            Expression expression4corrExtPerrA = buildExpression("4-corrExtPerrA",
                    "Max(ExtPErr, [\"4-corr 206Pb/238Ucalibr.const WM\"][1] / [\"4-corr 206Pb/238Ucalibr.const WM\"][0] * 100)", true, false, true);
            concentrationExpressionsOrdered.add(expression4corrExtPerrA);

            Expression expression7corrExtPerrA = buildExpression("7-corrExtPerrA",
                    "Max(ExtPErr, [\"7-corr 206Pb/238Ucalibr.const WM\"][1] / [\"7-corr 206Pb/238Ucalibr.const WM\"][0] * 100)", true, false, true);
            concentrationExpressionsOrdered.add(expression7corrExtPerrA);
        //}

        return concentrationExpressionsOrdered;
    }

//    private static SortedSet<Expression> generatePbIsotope204207CorrectionsForU() {
//        SortedSet<Expression> pbIsotopeCorrectionsForU = new TreeSet<>();
//
//        // calculate 204 PbU flavor from Tot68_82_fromA
//        Expression expression4corrTotal206Pb238U = buildExpression("4-corrTotal 206Pb/238US",
//                "[\"UncorrPb/Uconst\"] / [\"4-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio", false, true, false);
//        pbIsotopeCorrectionsForU.add(expression4corrTotal206Pb238U);
//
//        String t2 = "[\"4-corr 206Pb/238Ucalibr.const WM\"][2] / [\"4-corr 206Pb/238Ucalibr.const WM\"][0] * 100";
//        Expression expression4corrTotal206Pb238UpErr = buildExpression("4-corrTotal 206Pb/238US %err",
//                "SQRT( [%\"UncorrPb/Uconst\"]^2 + (" + t2 + ")^2 )", false, true, false);
//        pbIsotopeCorrectionsForU.add(expression4corrTotal206Pb238UpErr);
//
//        // calculate 207 PbU flavor from Tot68_82_fromA
//        Expression expression7corrTotal206Pb238U = buildExpression("7-corrTotal 206Pb/238US",
//                "[\"UncorrPb/Uconst\"] / [\"7-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio", false, true, false);
//        pbIsotopeCorrectionsForU.add(expression7corrTotal206Pb238U);
//
//        t2 = "[\"7-corr 206Pb/238Ucalibr.const WM\"][2] / [\"7-corr 206Pb/238Ucalibr.const WM\"][0] * 100";
//        Expression expression7corrTotal206Pb238UpErr = buildExpression("7-corrTotal 206Pb/238US %err",
//                "SQRT( [%\"UncorrPb/Uconst\"]^2 + (" + t2 + ")^2 )", false, true, false);
//        pbIsotopeCorrectionsForU.add(expression7corrTotal206Pb238UpErr);
//
//        return pbIsotopeCorrectionsForU;
//    }
//    private static SortedSet<Expression> generatePbIsotope204207CorrectionsForTh() {
//        SortedSet<Expression> pbIsotopeCorrectionsForTh = new TreeSet<>();
//
//        // calculate 204 PbTh flavors from Tot68_82_fromA
//        Expression expression4corrTotal208Pb232Th = buildExpression("4-corrTotal 208Pb/232ThS",
//                "[\"UncorrPb/Thconst\"] / [\"4-corr 208Pb/232Thcalibr.const WM\"][0] * StdThPbRatio", false, true, false);
//        pbIsotopeCorrectionsForTh.add(expression4corrTotal208Pb232Th);
//
//        String t2 = "[\"4-corr 208Pb/232Thcalibr.const WM\"][2] / [\"4-corr 208Pb/232Thcalibr.const WM\"][0] * 100";
//        Expression expression4corrTotal208Pb232ThpErr = buildExpression("4-corrTotal 208Pb/232ThS %err",
//                "SQRT( [%\"UncorrPb/Thconst\"]^2 + (" + t2 + ")^2 )", false, true, false);
//        pbIsotopeCorrectionsForTh.add(expression4corrTotal208Pb232ThpErr);
//
//        // calculate 207 PbTh flavors from Tot68_82_fromA
//        Expression expression7corrTotal208Pb232Th = buildExpression("7-corrTotal 208Pb/232ThS",
//                "[\"UncorrPb/Thconst\"] / [\"7-corr 208Pb/232Thcalibr.const WM\"][0] * StdThPbRatio", false, true, false);
//        pbIsotopeCorrectionsForTh.add(expression7corrTotal208Pb232Th);
//
//        t2 = "[\"7-corr 208Pb/232Thcalibr.const WM\"][2] / [\"7-corr 208Pb/232Thcalibr.const WM\"][0] * 100";
//        Expression expression7corrTotal208Pb232ThpErr = buildExpression("7-corrTotal 208Pb/232ThS %err",
//                "SQRT( [%\"UncorrPb/Thconst\"]^2 + (" + t2 + ")^2 )", false, true, false);
//        pbIsotopeCorrectionsForTh.add(expression7corrTotal208Pb232ThpErr);
//
//        return pbIsotopeCorrectionsForTh;
//    }
    /**
     * TODO: These should probably be segregated to the end of the expression
     * list and not be sorted each time?
     *
     * Ludwig Q4 part 1
     *
     * @return
     */
    public static SortedSet<Expression> generateOverCountExpressions() {
        SortedSet<Expression> overCountExpressionsOrdered = new TreeSet<>();

        Expression expressionOverCount4_6_7 = buildExpression("204/206 (fr. 207)",
                "ValueModel("
                + "([\"207/206\"] - Std_76 ) / ( sComm_74  - (Std_76 * sComm_64)),"
                + "ABS( [%\"207/206\"] * [\"207/206\"] / ([\"207/206\"] - Std_76) ),"
                + "false)", true, false, false);
        overCountExpressionsOrdered.add(expressionOverCount4_6_7);

        Expression expressionOverCount4_6_8 = buildExpression("204/206 (fr. 208)",
                "( [\"208/206\"] - StdRad86fact * [\"232Th/238U\"] ) / (sComm_84 - StdRad86fact * [\"232Th/238U\"] * sComm_64 )", true, false, false);
        overCountExpressionsOrdered.add(expressionOverCount4_6_8);

        Expression expressionOverCountPerSec4_7 = buildExpression("204 overcts/sec (fr. 207)",
                "TotalCps([\"204\"]) - TotalCps([\"BKG\"]) - [\"204/206 (fr. 207)\"] * ( TotalCps([\"206\"]) - TotalCps([\"BKG\"]))", true, false, false);
        overCountExpressionsOrdered.add(expressionOverCountPerSec4_7);

        Expression expressionOverCountPerSec4_8 = buildExpression("204 overcts/sec (fr. 208)",
                "TotalCps([\"204\"]) - TotalCps([\"BKG\"]) - [\"204/206 (fr. 208)\"] * ( TotalCps([\"206\"]) - TotalCps([\"BKG\"]))", true, false, false);
        overCountExpressionsOrdered.add(expressionOverCountPerSec4_8);

        Expression expressionOverCount7CorrCalib = buildExpression("7-corrPrimary calib const. delta%",
                "100 * ( (1 - sComm_64 * [\"204/206\"]) / (1 - sComm_64 * [\"204/206 (fr. 207)\"]) - 1 )", true, false, false);
        overCountExpressionsOrdered.add(expressionOverCount7CorrCalib);

        Expression expressionOverCount8CorrCalib = buildExpression("8-corrPrimary calib const. delta%",
                "100 * ( (1 - sComm_64 * [\"204/206\"]) / (1 - sComm_64 * [\"204/206 (fr. 208)\"]) - 1 ) ", true, false, false);
        overCountExpressionsOrdered.add(expressionOverCount8CorrCalib);

        return overCountExpressionsOrdered;
    }

    /**
     * TODO: These should probably be segregated to the end of the expression
     * list and not be sorted each time?
     *
     * Ludwig Q4 - part 2
     *
     * @return the
     * java.util.SortedSet<org.cirdles.squid.tasks.expressions.Expression>
     */
    public static SortedSet<Expression> generateExperimentalExpressions() {
        SortedSet<Expression> experimentalExpressions = new TreeSet<>();

        return experimentalExpressions;
    }

    /**
     * Squid2.5 Framework: Part 4 up to means
     *
     * @return
     */
    public static SortedSet<Expression> generatePerSpotProportionsOfCommonPb() {
        SortedSet<Expression> perSpotPbCorrectionsOrdered = new TreeSet<>();

        // Calculate a couple of "SampleData-only" columns:
        Expression expression7Corr46 = buildExpression("7-corr204Pb/206Pb",
                "Pb46cor7( "
                + "[\"207/206\"],"
                + " [\"207corr 206Pb/238U Age\"],"
                + "sComm_64,"
                + "sComm_74 )", false, true, false);
        perSpotPbCorrectionsOrdered.add(expression7Corr46);

        Expression expression8Corr46 = buildExpression("8-corr204Pb/206Pb",
                "Pb46cor8( "
                + "[\"208/206\"],"
                + " [\"232Th/238US\"],"
                + " [\"208corr 206Pb/238U Age\"],"
                + "sComm_64,"
                + "sComm_84 )", false, true, false);
        perSpotPbCorrectionsOrdered.add(expression8Corr46);

        /**
         * calculate ALL of the applicable proportions of common Pb (which in
         * turn reflect all the permutations of index isotope (204-, 207-, and
         * 208-corrected) and daughter-Pb isotope (206Pb and 208Pb, for Pb/U and
         * Pb/Th respectively), firstly for the StandardData sheet and secondly
         * for the SampleData sheet:
         *
         */
        // for ref materials
        Expression expression4corCom206 = buildExpression("4-corr %com206",
                "100 * sComm_64 * [\"204/206\"]", true, true, false);
        perSpotPbCorrectionsOrdered.add(expression4corCom206);

        Expression expression7corCom206 = buildExpression("7-corr %com206",
                "100 * sComm_64 * [\"204/206 (fr. 207)\"]", true, false, false);
        perSpotPbCorrectionsOrdered.add(expression7corCom206);

        Expression expression8corCom206 = buildExpression("8-corr %com206",
                "100 * sComm_64 * [\"204/206 (fr. 208)\"]", true, false, false);
        perSpotPbCorrectionsOrdered.add(expression8corCom206);

        Expression expression4corCom208 = buildExpression("4-corr %com208",
                "100 * sComm_84 / [\"208/206\"] * [\"204/206\"]", true, true, false);
        perSpotPbCorrectionsOrdered.add(expression4corCom208);

        Expression expression7corCom208 = buildExpression("7-corr %com208",
                "100 * sComm_84 / [\"208/206\"] * [\"204/206 (fr. 207)\"]", true, false, false);
        perSpotPbCorrectionsOrdered.add(expression7corCom208);

        // for samples
        Expression expression7corCom206S = buildExpression("7-corr %com206S",
                "100 * sComm_64 * [\"7-corr204Pb/206Pb\"]", false, true, false);
        perSpotPbCorrectionsOrdered.add(expression7corCom206S);

        Expression expression8corCom206S = buildExpression("8-corr %com206S",
                "100 * sComm_64 * [\"8-corr204Pb/206Pb\"]", false, true, false);
        perSpotPbCorrectionsOrdered.add(expression8corCom206S);

        Expression expression7corCom208S = buildExpression("7-corr %com208S",
                "100 * sComm_84 / [\"208/206\"] * [\"7-corr204Pb/206Pb\"]", false, true, false);
        perSpotPbCorrectionsOrdered.add(expression7corCom208S);

        // The next step is to calculate all the applicable radiogenic 208Pb/206Pb values. 
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 4-corr208Pb*/206Pb*  *** Start
        // ref material and sample version
        Expression expression4corr208Pb206Pb = buildExpression("4-corr208Pb*/206Pb*",
                "ValueModel("
                + "( [\"208/206\"] / [\"204/206\"] - sComm_84 ) / ( 1 / [\"204/206\"] - sComm_64),"
                + "100 * sqrt( ( ([%\"208/206\"] / 100 * [\"208/206\"])^2 +\n"
                + "(( [\"208/206\"] / [\"204/206\"] - sComm_84 ) / ( 1 / [\"204/206\"] - sComm_64)"
                + " * sComm_64 - sComm_84)^2 * ([%\"204/206\"] / 100 * [\"204/206\"])^2 )  \n"
                + " / (1 - sComm_64 * [\"204/206\"]) ^2  ) / "
                + "abs( ( [\"208/206\"] / [\"204/206\"] - sComm_84 ) / ( 1 / [\"204/206\"] - sComm_64) ),"
                + "false)", true, true, false);
        perSpotPbCorrectionsOrdered.add(expression4corr208Pb206Pb);

        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 4-corr208Pb*/206Pb*  *** End
        //
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 7-corr208Pb*/206Pb*  *** Start
        // ref material version
        Expression expression7corr208Pb206Pb = buildExpression("7-corr208Pb*/206Pb*",
                "( [\"208/206\"] / [\"204/206 (fr. 207)\"] - sComm_84 ) / \n"
                + "( 1 / [\"204/206 (fr. 207)\"] - sComm_64) ", true, false, false);
        perSpotPbCorrectionsOrdered.add(expression7corr208Pb206Pb);

        // sample version
        Expression expression7corr208Pb206PbS = buildExpression("7-corr208Pb*/206Pb*S",
                "( [\"208/206\"] / [\"7-corr204Pb/206Pb\"] - sComm_84 ) / \n"
                + "( 1 / [\"7-corr204Pb/206Pb\"] - sComm_64)  ", false, true, false);
        perSpotPbCorrectionsOrdered.add(expression7corr208Pb206PbS);

        // ref material version
        Expression expression7corr208Pb206PbPctErr = buildExpression("7-corr208Pb*/206Pb* %err",
                "StdPb86radCor7per("
                + "[\"208/206\"], "
                + "[\"207/206\"], "
                + "[\"7-corr208Pb*/206Pb*\"], "
                + "[\"204/206 (fr. 207)\"],"
                + "Std_76,"
                + "sComm_64,"
                + "sComm_74,"
                + "sComm_84)", true, false, false);
        perSpotPbCorrectionsOrdered.add(expression7corr208Pb206PbPctErr);

        // sample material version
        Expression expression7corr208Pb206PbSPctErr = buildExpression("7-corr208Pb*/206Pb*S %err",
                "Pb86radCor7per("
                + "[\"208/206\"], "
                + "[\"207/206\"], "
                + "[\"Total 206Pb/238US\"], "
                + "[%\"Total 206Pb/238US\"],"
                + "[\"207corr 206Pb/238U Age\"],"
                + "sComm_64,"
                + "sComm_74,"
                + "sComm_84)", false, true, false);
        perSpotPbCorrectionsOrdered.add(expression7corr208Pb206PbSPctErr);

        return perSpotPbCorrectionsOrdered;
    }

    /**
     * Squid2.5 Framework: Part 4 means
     *
     * @return
     */
    public static SortedSet<Expression> generate204207MeansAndAgesForRefMaterialsU() {
        SortedSet<Expression> meansAndAgesForRefMaterials = new TreeSet<>();

        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 4-corr  206/238  *** Start
        Expression expression4corr206Pb238Ucalibrconst = buildExpression("4-corr 206Pb/238Ucalibr.const",
                "ValueModel("
                + "(1 - [\"204/206\"] * sComm_64) * [\"UncorrPb/Uconst\"],"
                + "sqrt([%\"UncorrPb/Uconst\"]^2 + \n"
                + "( sComm_64 / ( 1 / [\"204/206\"] - sComm_64 ) )^2 * [%\"204/206\"]^2 ),"
                + "false)", true, false, false);
        meansAndAgesForRefMaterials.add(expression4corr206Pb238Ucalibrconst);

        // weighted mean
        Expression expression4corr206Pb238UcalibrconstWM = buildExpression("4-corr 206Pb/238Ucalibr.const WM",
                "WtdMeanACalc( [\"4-corr 206Pb/238Ucalibr.const\"], [%\"4-corr 206Pb/238Ucalibr.const\"], FALSE, FALSE )", true, false, true);
        meansAndAgesForRefMaterials.add(expression4corr206Pb238UcalibrconstWM);

        // age calc
        Expression expression4corr206Pb238UAge = buildExpression("4-corr 206Pb/238U Age",
                "ValueModel("
                + "LN( 1.0 + [\"4-corr 206Pb/238Ucalibr.const\"] / [\"4-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio ) / lambda238,"
                + "[%\"4-corr 206Pb/238Ucalibr.const\"] / 100 * ( EXP(lambda238 * \n"
                + "LN( 1.0 + [\"4-corr 206Pb/238Ucalibr.const\"] / [\"4-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio ) / lambda238    ) - 1 )"
                + "/ lambda238 / EXP(lambda238 * "
                + "LN( 1.0 + [\"4-corr 206Pb/238Ucalibr.const\"] / [\"4-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio ) / lambda238     ),"
                + "true)", true, false, false);
        meansAndAgesForRefMaterials.add(expression4corr206Pb238UAge);

        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 4-corr  206/238  *** END
        //
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 7-corr  206/238  *** Start
        Expression expression7corr206Pb238Ucalibrconst = buildExpression("7-corr 206Pb/238Ucalibr.const",
                "ValueModel("
                + "(1 - [\"204/206 (fr. 207)\"] * sComm_64) * [\"UncorrPb/Uconst\"],"
                + "sqrt([%\"UncorrPb/Uconst\"]^2 +\n"
                + "( sComm_64 / (1 / [\"204/206 (fr. 207)\"] - sComm_64 ) )^2 * \n"
                + "[%\"204/206 (fr. 207)\"]^2),"
                + "false)", true, false, false);
        meansAndAgesForRefMaterials.add(expression7corr206Pb238Ucalibrconst);

        // weighted mean
        Expression expression7corr206Pb238UcalibrconstWM = buildExpression("7-corr 206Pb/238Ucalibr.const WM",
                "WtdMeanACalc( [\"7-corr 206Pb/238Ucalibr.const\"], [%\"7-corr 206Pb/238Ucalibr.const\"], FALSE, FALSE )", true, false, true);
        meansAndAgesForRefMaterials.add(expression7corr206Pb238UcalibrconstWM);

        // age calc
        Expression expression7corr206Pb238UAge = buildExpression("7-corr 206Pb/238U Age",
                "ValueModel("
                + "LN( 1.0 + [\"7-corr 206Pb/238Ucalibr.const\"] / [\"7-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio ) / lambda238,"
                + "[%\"7-corr 206Pb/238Ucalibr.const\"] / 100 * ( EXP(lambda238 * "
                + "LN( 1.0 + [\"7-corr 206Pb/238Ucalibr.const\"] / [\"7-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio ) / lambda238 "
                + ") - 1 )\n"
                + "/ lambda238 / EXP(lambda238 * "
                + "LN( 1.0 + [\"7-corr 206Pb/238Ucalibr.const\"] / [\"7-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio ) / lambda238"
                + " ),"
                + "true)", true, false, false);
        meansAndAgesForRefMaterials.add(expression7corr206Pb238UAge);

        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 7-corr  206/238  *** End
        //   
        return meansAndAgesForRefMaterials;
    }

    /**
     * Squid2.5 Framework: Part 4 means
     *
     * @return
     */
    public static SortedSet<Expression> generate208MeansAndAgesForRefMaterialsU() {
        SortedSet<Expression> meansAndAgesForRefMaterials = new TreeSet<>();

        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 8-corr  206/238  *** Start
        String term2 = "( sComm_64 * [\"UncorrPb/Uconst\"] * [\"204/206 (fr. 208)\"] "
                + "/ (1 - [\"204/206 (fr. 208)\"] * sComm_64) * [\"UncorrPb/Uconst\"])^2 ";
        String term3 = "( [\"208/206\"] * [%\"208/206\"] / \n"
                + "( [\"208/206\"] - StdRad86fact * [\"232Th/238U\"] ) )^2 ";
        String term4 = "1 / ( [\"208/206\"] - StdRad86fact * [\"232Th/238U\"] ) +  \n"
                + "sComm_64 / ( sComm_84 - sComm_64 * StdRad86fact * [\"232Th/238U\"] )";
        String term6 = "((" + term4 + ")* StdRad86fact * [\"232Th/238U\"] * [%\"232Th/238U\"] )^2";

        Expression expression8corr206Pb238Ucalibrconst = buildExpression("8-corr 206Pb/238Ucalibr.const",
                "ValueModel((1 - [\"204/206 (fr. 208)\"] * sComm_64) * [\"UncorrPb/Uconst\"],"
                + "sqrt( [%\"UncorrPb/Uconst\"]^2 + ((" + term2 + ") * ((" + term3 + ") + (" + term6 + "))) ),"
                + "false)", true, false, false);
        meansAndAgesForRefMaterials.add(expression8corr206Pb238Ucalibrconst);

        // weighted mean
        Expression expression8corr206Pb238UcalibrconstWM = buildExpression("8-corr 206Pb/238Ucalibr.const WM",
                "WtdMeanACalc( [\"8-corr 206Pb/238Ucalibr.const\"], [%\"8-corr 206Pb/238Ucalibr.const\"], FALSE, FALSE )", true, false, true);
        meansAndAgesForRefMaterials.add(expression8corr206Pb238UcalibrconstWM);

        Expression expression8corrExtPerrA = buildExpression("8-corrExtPerrA",
                "Max(ExtPErr, [\"8-corr 206Pb/238Ucalibr.const WM\"][1] / [\"8-corr 206Pb/238Ucalibr.const WM\"][0] * 100)", true, false, true);
        meansAndAgesForRefMaterials.add(expression8corrExtPerrA);

        // age calc
        Expression expression8corr206Pb238UAge = buildExpression("8-corr 206Pb/238U Age",
                "ValueModel("
                + "LN( 1.0 + [\"8-corr 206Pb/238Ucalibr.const\"] / [\"8-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio ) / lambda238,"
                + "[%\"8-corr 206Pb/238Ucalibr.const\"] / 100 * ( EXP(lambda238 * "
                + "LN( 1.0 + [\"8-corr 206Pb/238Ucalibr.const\"] / [\"8-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio ) / lambda238"
                + " ) - 1 )\n"
                + "/ lambda238 / EXP(lambda238 * "
                + "LN( 1.0 + [\"8-corr 206Pb/238Ucalibr.const\"] / [\"8-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio ) / lambda238"
                + " ),"
                + "true)", true, false, false);
        meansAndAgesForRefMaterials.add(expression8corr206Pb238UAge);

        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 8-corr  206/238  *** End
        return meansAndAgesForRefMaterials;
    }

    /**
     * Squid2.5 Framework: Part 4 means
     *
     * @return
     */
    public static SortedSet<Expression> generate204207MeansAndAgesForRefMaterialsTh() {
        SortedSet<Expression> meansAndAgesForRefMaterials = new TreeSet<>();

        //
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 4-corr  208/232  *** Start
        Expression expression4corr208Pb232Thcalibrconst = buildExpression("4-corr 208Pb/232Thcalibr.const",
                "ValueModel("
                + "(1 - [\"204/206\"] / [\"208/206\"] * sComm_84) * [\"UncorrPb/Thconst\"],"
                + "sqrt([%\"UncorrPb/Thconst\"]^2 + \n"
                + "( sComm_84 / ( [\"208/206\"] / [\"204/206\"] - sComm_84 ) )^2 * [%\"204/206\"]^2 ),"
                + "false)", true, false, false);
        meansAndAgesForRefMaterials.add(expression4corr208Pb232Thcalibrconst);

        // weighted mean
        Expression expression4corr208Pb232ThcalibrconstWM = buildExpression("4-corr 208Pb/232Thcalibr.const WM",
                "WtdMeanACalc( [\"4-corr 208Pb/232Thcalibr.const\"], [%\"4-corr 208Pb/232Thcalibr.const\"], FALSE, FALSE )", true, false, true);
        meansAndAgesForRefMaterials.add(expression4corr208Pb232ThcalibrconstWM);

        // age calc
        Expression expression4corr208Pb232ThAge = buildExpression("4-corr 208Pb/232Th Age",
                "ValueModel("
                + "LN( 1.0 + [\"4-corr 208Pb/232Thcalibr.const\"] / [\"4-corr 208Pb/232Thcalibr.const WM\"][0] * StdThPbRatio ) / lambda232,"
                + "[%\"4-corr 208Pb/232Thcalibr.const\"] / 100 * ( EXP(lambda232 * "
                + "LN( 1.0 + [\"4-corr 208Pb/232Thcalibr.const\"] / [\"4-corr 208Pb/232Thcalibr.const WM\"][0] * StdThPbRatio ) / lambda232"
                + ") - 1 ) \n"
                + "/ lambda232 / EXP(lambda232 * "
                + "LN( 1.0 + [\"4-corr 208Pb/232Thcalibr.const\"] / [\"4-corr 208Pb/232Thcalibr.const WM\"][0] * StdThPbRatio ) / lambda232"
                + " ),"
                + "true)", true, false, false);
        meansAndAgesForRefMaterials.add(expression4corr208Pb232ThAge);

        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 4-corr  208/232  *** END
        //
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 7-corr  208/232  *** Start
        Expression expression7corr208Pb232Thcalibrconst = buildExpression("7-corr 208Pb/232Thcalibr.const",
                "ValueModel("
                + "(1 - [\"204/206 (fr. 207)\"] / [\"208/206\"] * sComm_84) * [\"UncorrPb/Thconst\"],"
                + "sqrt([%\"UncorrPb/Thconst\"]^2 +  \n"
                + "( sComm_84 / ( [\"208/206\"] / [\"204/206 (fr. 207)\"] - sComm_84 ) )^2 * \n"
                + "( [%\"208/206\"]^2 + [%\"204/206 (fr. 207)\"]^2 )),"
                + "false)", true, false, false);
        meansAndAgesForRefMaterials.add(expression7corr208Pb232Thcalibrconst);

        // weighted mean
        Expression expression7corr208Pb232ThcalibrconstWM = buildExpression("7-corr 208Pb/232Thcalibr.const WM",
                "WtdMeanACalc( [\"7-corr 208Pb/232Thcalibr.const\"], [%\"7-corr 208Pb/232Thcalibr.const\"], FALSE, FALSE )", true, false, true);
        meansAndAgesForRefMaterials.add(expression7corr208Pb232ThcalibrconstWM);

        // age calc
        Expression expression7corr208Pb232ThAge = buildExpression("7-corr 208Pb/232Th Age",
                "ValueModel("
                + "LN( 1.0 + [\"7-corr 208Pb/232Thcalibr.const\"] / [\"7-corr 208Pb/232Thcalibr.const WM\"][0] * StdThPbRatio ) / lambda232,"
                + "[%\"7-corr 208Pb/232Thcalibr.const\"] / 100 * ( EXP(lambda232 * "
                + "LN( 1.0 + [\"7-corr 208Pb/232Thcalibr.const\"] / [\"7-corr 208Pb/232Thcalibr.const WM\"][0] * StdThPbRatio ) / lambda232"
                + " ) - 1 ) \n"
                + "/ lambda232 / EXP(lambda232 * "
                + "LN( 1.0 + [\"7-corr 208Pb/232Thcalibr.const\"] / [\"7-corr 208Pb/232Thcalibr.const WM\"][0] * StdThPbRatio ) / lambda232"
                + " ),"
                + "true)", true, false, false);
        meansAndAgesForRefMaterials.add(expression7corr208Pb232ThAge);

        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 7-corr  208/232  *** End
        return meansAndAgesForRefMaterials;
    }

    /**
     * This subroutine (which is solely for the Standard) does a bit more than
     * the name implies. Firstly it places, row-by-row, formulae to calculate
     * the 204-corrected 207Pb/206Pb ratio and its 1sigma percentage
     * uncertainty, as well as invoking (row-by-row) the relevant LudwigLibrary
     * functions to calculate the associated 204-corrected 207Pb/206Pb date and
     * its 1sigma absolute uncertainty.
     *
     * Secondly, it identifies which columns can usefully have robust means
     * calculated, performs those calculations (using LudwigLibrary function
     * TukeysBiweight, with tuning 9) and places the output of the expression as
     * a 3 x 1 array beneath the relevant column. The SQUID 2.50 subroutine
     * requires the index number of the last row of analytical data as an input,
     * so it can determine in which rows the "summary" results should be placed
     * so that the calculated biweights appear directly beneath the input data.
     *
     * @return
     */
    public static SortedSet<Expression> overCountMeans() {
        SortedSet<Expression> overCountMeansRefMaterials = new TreeSet<>();

        String term1 = "(([\"207/206\"]*[%\"207/206\"])^2 \n"
                + "+ ([\"204/206\"]*(([\"207/206\"]/[\"204/206\"]-sComm_74 )"
                + "/(1/[\"204/206\"]-sComm_64) * sComm_64 - sComm_74) \n "
                + "* [%\"204/206\"])^2)";

        String term2 = "([\"207/206\"] - [\"204/206\"] * sComm_74)^2";

        Expression expression4corr207Pb206Pb = buildExpression("4-corr 207Pb/206Pb",
                "ValueModel(([\"207/206\"]/[\"204/206\"]-sComm_74 )/(1/[\"204/206\"]-sComm_64),"
                + "sqrt(" + term1 + "/" + term2 + "),"
                + "false)", true, false, false);
        overCountMeansRefMaterials.add(expression4corr207Pb206Pb);

        Expression expression4corr207Pb206PbAge = buildExpression("4-corr207Pb/206Pbage",
                "AgePb76WithErr( [\"4-corr 207Pb/206Pb\"],"
                + "([\"4-corr 207Pb/206Pb\"] * [%\"4-corr 207Pb/206Pb\"] / 100 ))", true, false, false);
        overCountMeansRefMaterials.add(expression4corr207Pb206PbAge);

        // The second part of the subroutine calculates the various biweight means
        Expression expressionPb204OverCts7corr = buildExpression("Pb204OverCts7corr",
                "sqBiweight([\"204 overcts/sec (fr. 207)\"], 9)", true, false, true);
        expressionPb204OverCts7corr.setNotes("Robust avg 204 overcts assuming 206Pb/238U-207Pb/235U age concordance");
        overCountMeansRefMaterials.add(expressionPb204OverCts7corr);

        Expression expressionPb204OverCts8corr = buildExpression("Pb204OverCts8corr",
                "sqBiweight([\"204 overcts/sec (fr. 208)\"], 9)", true, false, true);
        expressionPb204OverCts8corr.setNotes("Robust avg 204 overcts assuming 206Pb/238U-208Pb/232Th age concordance");
        overCountMeansRefMaterials.add(expressionPb204OverCts8corr);

        Expression expressionOverCtsDeltaP7corr = buildExpression("OverCtsDeltaP7corr",
                "sqBiweight([\"7-corrPrimary calib const. delta%\"], 9)", true, false, true);
        expressionOverCtsDeltaP7corr.setNotes("Robust avg of diff. between 207-corr. and 204-corr. calibr. const.");
        overCountMeansRefMaterials.add(expressionOverCtsDeltaP7corr);

        Expression expressionOverCtsDeltaP8corr = buildExpression("OverCtsDeltaP8corr",
                "sqBiweight([\"8-corrPrimary calib const. delta%\"], 9)", true, false, true);
        expressionOverCtsDeltaP8corr.setNotes("Robust avg of diff. between 208-corr. and 204-corr. calibr. const.");
        overCountMeansRefMaterials.add(expressionOverCtsDeltaP8corr);

        Expression expressionOverCts4corr207Pb206Pbagecorr = buildExpression("OverCts4-corr207Pb/206Pbagecorr",
                "sqBiweight([\"4-corr207Pb/206Pbage\"], 9)", true, false, true);
        expressionOverCts4corr207Pb206Pbagecorr.setNotes("Robust average of 204-corrected 207/206 age");
        overCountMeansRefMaterials.add(expressionOverCts4corr207Pb206Pbagecorr);

        return overCountMeansRefMaterials;

    }

    public static SortedSet<Expression> stdRadiogenicCols(String parentNuclide, boolean isDirectAltPD) {
        SortedSet<Expression> stdRadiogenicCols = new TreeSet<>();
//
//        Expression expression4corr206Pb238U = buildExpression("4-corr 206*/238",
//                "ValueModel("
//                + "[\"4-corr 206Pb/238Ucalibr.const\"] / [\"4-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio,"
//                + "[%\"4-corr 206Pb/238Ucalibr.const\"],"
//                + "false)", true, false, false);
//        stdRadiogenicCols.add(expression4corr206Pb238U);
//
//        Expression expression4corr207Pb235U = buildExpression("4-corr 207Pb/235U",
//                "ValueModel("
//                + "[\"4-corr 207Pb/206Pb\"] * [\"4-corr 206*/238\"] * r238_235s,"
//                + "sqrt( [%\"4-corr 206*/238\"]^2 + [%\"4-corr 207Pb/206Pb\"]^2 ),"
//                + "false)", true, false, false);
//        stdRadiogenicCols.add(expression4corr207Pb235U);
//
//        Expression expression4correrrcorr = buildExpression("4-corr-errcorr",
//                "[%\"4-corr 206*/238\"] / [%\"4-corr 207Pb/235U\"]", true, false, false);
//        stdRadiogenicCols.add(expression4correrrcorr);

        // additional expressions by Simon Bodorkos 8 Aug 2018 ***********************
        /**
         * Before resuming the SQUID 2.50 code, I have inserted some code to
         * calculate ["Total 206Pb/238U"] (and its %err) as well as ["Total
         * 208Pb/232Th"] (and its %err) for the Standard. Note that these
         * calculations do not exist in SQUID 2.50, but they are required in
         * order to repair some SQUID 2.50 bugs affecting Perm3, and to sensibly
         * extend the functionality of SQUID 2.50 (and Squid3) in Perm1. These
         * newly-defined parameters appear to have no direct use in Perm2 or
         * Perm4, but I see no harm in calculating them anyway. By analogy with
         * other similar code in SQUID 2.50, the first part of the new insertion
         * is a For loop that closely resembles that employed in subroutine
         * SamRadiogenicCols
         */
        // Case Perm1 and Perm2, i.e. has uranium as parent nuclide ***************************
        if (!parentNuclide.contains("232")) {
            // see email from Bodorkos 20 July 2018 [\"UncorrPb/Uconst\"]
            Expression expression4corrTotal206Pb238U = buildExpression("4-corrTotal 206Pb/238U",
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"] / [\"4-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio,"
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + [\"4-corrExtPerrA\"] ^ 2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression4corrTotal206Pb238U);

            Expression expression7corrTotal206Pb238U = buildExpression("7-corrTotal 206Pb/238U",
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"] / [\"7-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio,"
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + [\"7-corrExtPerrA\"] ^ 2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression7corrTotal206Pb238U);

            // perm1 only
            if (!isDirectAltPD) {
                Expression expression8corrTotal206Pb238U = buildExpression("8-corrTotal 206Pb/238U",
                        "ValueModel("
                        + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"] / [\"8-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio,"
                        + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + [\"8-corrExtPerrA\"] ^ 2),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression8corrTotal206Pb238U);

                // special case
                Expression expression4corrTotal208Pb232Th = buildExpression("4-corrTotal 208Pb/232Th",
                        "ValueModel("
                        + "[\"Total 206Pb/238U\"] * [\"208/206\"] / [\"232Th/238U\"],"
                        + "SQRT([%\"208/206\"]^2 + [%\"Total 206Pb/238U\"]^2 + [%\"232Th/238U\"]^2),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression4corrTotal208Pb232Th);

                Expression expression7corrTotal208Pb232Th = buildExpression("7-corrTotal 208Pb/232Th",
                        "ValueModel("
                        + "[\"Total 206Pb/238U\"] * [\"208/206\"] / [\"232Th/238U\"],"
                        + "SQRT([%\"208/206\"]^2 + [%\"Total 206Pb/238U\"]^2 + [%\"232Th/238U\"]^2),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression7corrTotal208Pb232Th);

                Expression expression8corrTotal208Pb232Th = buildExpression("8-corrTotal 208Pb/232Th",
                        "ValueModel("
                        + "[\"Total 206Pb/238U\"] * [\"208/206\"] / [\"232Th/238U\"],"
                        + "SQRT([%\"208/206\"]^2 + [%\"Total 206Pb/238U\"]^2 + [%\"232Th/238U\"]^2),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression8corrTotal208Pb232Th);

            } else {
                // perm2 only
                Expression expression4corrTotal208Pb232Th = buildExpression("4-corrTotal 208Pb/232Th",
                        "ValueModel("
                        + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"] / [\"4-corr 208Pb/232Thcalibr.const WM\"][0] * StdThPbRatio,"
                        + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"]^2 + [\"4-corrExtPerrA\"] ^ 2),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression4corrTotal208Pb232Th);

                Expression expression7corrTotal208Pb232Th = buildExpression("7-corrTotal 208Pb/232Th",
                        "ValueModel("
                        + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"] / [\"7-corr 208Pb/232Thcalibr.const WM\"][0] * StdThPbRatio,"
                        + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"]^2 + [\"7-corrExtPerrA\"] ^ 2),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression7corrTotal208Pb232Th);
            }
        } else {
            // perm3 and perm4
            Expression expression4corrTotal208Pb232Th = buildExpression("4-corrTotal 208Pb/232Th",
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"] / [\"4-corr 208Pb/232Thcalibr.const WM\"][0] * StdThPbRatio,"
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"]^2 + [\"4-corrExtPerrA\"] ^ 2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression4corrTotal208Pb232Th);

            Expression expression7corrTotal208Pb232Th = buildExpression("7-corrTotal 208Pb/232Th",
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"] / [\"7-corr 208Pb/232Thcalibr.const WM\"][0] * StdThPbRatio,"
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"]^2 + [\"7-corrExtPerrA\"] ^ 2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression7corrTotal208Pb232Th);

            // perm3 special case - needs confirmation
            if (!isDirectAltPD) {
                Expression expression4corrTotal238U206Pb = buildExpression("4-corrTotal 206Pb/238U",
                        "ValueModel("
                        + "[\"Total 208Pb/232Th\"] / [\"208/206\"] * [\"232Th/238U\"],"
                        + "SQRT( [%\"208/206\"]^2 + [%\"Total 208Pb/232Th\"]^2 + \n"
                        + "[%\"232Th/238U\"]^2 ),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression4corrTotal238U206Pb);

                // repeat the math so the replacement engine works when creating "Total...
                Expression expression7corrTotal206PbS238U = buildExpression("7-corrTotal 206Pb/238U",
                        "ValueModel("
                        + "[\"Total 208Pb/232Th\"] / [\"208/206\"] * [\"232Th/238U\"],"
                        + "SQRT( [%\"208/206\"]^2 + [%\"Total 208Pb/232Th\"]^2 + \n"
                        + "[%\"232Th/238U\"]^2 ),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression7corrTotal206PbS238U);
            }

        }
        // perm4
        if (parentNuclide.contains("232") && isDirectAltPD) {
            Expression expression4corrTotal206Pb238U = buildExpression("4-corrTotal 206Pb/238U",
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"] / [\"4-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio,"
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + [\"4-corrExtPerrA\"] ^ 2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression4corrTotal206Pb238U);

            Expression expression7corrTotal206Pb238U = buildExpression("7-corrTotal 206Pb/238U",
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]  / [\"7-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio,"
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + [\"7-corrExtPerrA\"] ^ 2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression7corrTotal206Pb238U);

        }

        if (parentNuclide.contains("232") & !isDirectAltPD) {
            // perm3 only
            Expression expression4corr206238 = buildExpression("4-corr 206*/238",
                    "ValueModel("
                    + "[\"Total 206Pb/238U\"] * ( 1 - sComm_64 * [\"204/206\"] ),"
                    + "SQRT( [%\"Total 206Pb/238U\"]^2 + \n"
                    + "      ( sComm_64 * [%\"204/206\"] / ( 1/[\"204/206\"] - sComm_64 ) )^2 ),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression4corr206238);

            Expression expression7corr206Pb238UAge = buildExpression("7-corr 206Pb/238U Age",
                    "Age7corrWithErr("
                    + "[\"Total 206Pb/238U\"],"
                    + "[±\"Total 206Pb/238U\"], "
                    + "[\"207/206\"],"
                    + "[±\"207/206\"],"
                    + "sComm_76)",
                    true, false, false);
            stdRadiogenicCols.add(expression7corr206Pb238UAge);

            Expression expression7corr206238 = buildExpression("7-corr 206*/238",
                    "ValueModel("
                    + "EXP( lambda238 * [\"7-corr 206Pb/238U Age\"] ) - 1,"
                    + "lambda238 * EXP(lambda238 * [\"7-corr 206Pb/238U Age\"] ) *\n"
                    + "[±\"7-corr 206Pb/238U Age\"] / "
                    + "(EXP( lambda238 * [\"7-corr 206Pb/238U Age\"] ) - 1)"
                    + "* 100 ,"
                    + "false)",
                    true, false, false);
            stdRadiogenicCols.add(expression7corr206238);

        } else {
            // perm 1,2,4

            Expression expression4corr206Pb238U = buildExpression("4-corr 206*/238",
                    "ValueModel("
                    + "[\"4-corr 206Pb/238Ucalibr.const\"] / [\"4-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio,"
                    + "[%\"4-corr 206Pb/238Ucalibr.const\"],"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression4corr206Pb238U);

            Expression expression4corr207Pb235U = buildExpression("4-corr 207*/235",
                    "ValueModel("
                    + "[\"4-corr 207Pb/206Pb\"] * [\"4-corr 206*/238\"] * r238_235s,"
                    + "sqrt( [%\"4-corr 206*/238\"]^2 + [%\"4-corr 207Pb/206Pb\"]^2 ),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression4corr207Pb235U);

            Expression expression4correrrcorr = buildExpression("4-corr-errcorr",
                    "[%\"4-corr 206*/238\"] / [%\"4-corr 207*/235\"]", true, false, false);
            stdRadiogenicCols.add(expression4correrrcorr);

            Expression expression7corr206238 = buildExpression("7-corr 206*/238",
                    "ValueModel("
                    + "[\"7-corr 206Pb/238Ucalibr.const\"] / [\"7-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio,"
                    + "[%\"7-corr 206Pb/238Ucalibr.const\"],"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression7corr206238);

            // perm1 only
            if (!parentNuclide.contains("232") & !isDirectAltPD) {
                Expression expression8corr206238 = buildExpression("8-corr 206*/238",
                        "ValueModel("
                        + "[\"8-corr 206Pb/238Ucalibr.const\"] / [\"8-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio,"
                        + "[%\"8-corr 206Pb/238Ucalibr.const\"],"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression8corr206238);

                Expression expression8corr207235 = buildExpression("8-corr 207*/235",
                        "Rad8corPb7U5WithErr( "
                        + "[\"Total 206Pb/238U\"],"
                        + "[%\"Total 206Pb/238U\"],"
                        + "[\"8-corr 206*/238\"],"
                        + "[\"Total 206Pb/238U\"] * [\"207/206\"] / r238_235s,"
                        + "[\"232Th/238U\"], "
                        + "[%\"232Th/238U\"],"
                        + "[\"207/206\"],"
                        + "[%\"207/206\"],"
                        + "[\"208/206\"],"
                        + "[%\"208/206\"],"
                        + "sComm_76,"
                        + "sComm_86)", true, false, false);
                stdRadiogenicCols.add(expression8corr207235);

                Expression expression8correrrcorr = buildExpression("8-corr-errcorr",
                        "Rad8corConcRho( "
                        + "[\"Total 206Pb/238U\"], "
                        + "[%\"Total 206Pb/238U\"],"
                        + "[\"8-corr 206*/238\"],"
                        + "[\"232Th/238U\"],"
                        + "[%\"232Th/238U\"],"
                        + "[\"207/206\"],"
                        + "[%\"207/206\"],"
                        + "[\"208/206\"],"
                        + "[%\"208/206\"],"
                        + "sComm_76,"
                        + "sComm_86)", true, false, false);
                stdRadiogenicCols.add(expression8correrrcorr);

                Expression expression8corr207206 = buildExpression("8-corr 207*/206*",
                        "ValueModel("
                        + "[\"8-corr 207*/235\"] / [\"8-corr 206*/238\"] / r238_235s,"
                        + "SQRT([%\"8-corr 207*/235\"]^2 + [%\"8-corr 206*/238\"]^2 -\n"
                        + " 2 * [%\"8-corr 207*/235\"] * [%\"8-corr 206*/238\"] * [\"8-corr-errcorr\"] ),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression8corr207206);

                Expression expression208corr207Pb206PbAge = buildExpression("8-corr 207Pb/206Pb Age",
                        "AgePb76WithErr( [\"8-corr 207*/206*\"], "
                        + "([\"8-corr 207*/206*\"] * [%\"8-corr 207*/206*\"] / 100))", true, false, false);
                stdRadiogenicCols.add(expression208corr207Pb206PbAge);

            }
        }

        return stdRadiogenicCols;
    }

    /**
     * https://github.com/CIRDLES/ET_Redux/wiki/SHRIMP:-Sub-SamRadiogenicCols
     *
     * @param parentNuclide
     * @param isDirectAltPD
     * @return
     */
    public static SortedSet<Expression> samRadiogenicCols(String parentNuclide, boolean isDirectAltPD) {
        SortedSet<Expression> samRadiogenicCols = new TreeSet<>();

        Expression expressionAlpha = buildExpression("Alpha",
                "1 / [\"204/206\"]", false, true, false);
        samRadiogenicCols.add(expressionAlpha);

        Expression expressionNetAlpha = buildExpression("NetAlpha",
                "Alpha - sComm_64", false, true, false);
        samRadiogenicCols.add(expressionNetAlpha);

        Expression expressionBeta = buildExpression("Beta",
                "[\"207/206\"] / [\"204/206\"]", false, true, false);
        samRadiogenicCols.add(expressionBeta);

        Expression expressionNetBeta = buildExpression("NetBeta",
                "Beta - sComm_74", false, true, false);
        samRadiogenicCols.add(expressionNetBeta);

        Expression expressionGamma = buildExpression("Gamma",
                "[\"208/206\"] / [\"204/206\"]", false, true, false);
        samRadiogenicCols.add(expressionGamma);

        Expression expressionNetGamma = buildExpression("NetGamma",
                "Gamma - sComm_84", false, true, false);
        samRadiogenicCols.add(expressionNetGamma);

        Expression expressionRadd6 = buildExpression("radd6",
                "NetAlpha / Alpha", false, true, false);
        samRadiogenicCols.add(expressionRadd6);

        Expression expressionRadd8 = buildExpression("radd8",
                "NetGamma / Gamma", false, true, false);
        samRadiogenicCols.add(expressionRadd8);

        // Case Perm1 and Perm2, i.e. has uranium as parent nuclide ***************************
        if (!parentNuclide.contains("232")) {
            // this is same as for RM above, so add "S" for Sample
            // see email from Bodorkos 20 July 2018 [\"UncorrPb/Uconst\"]
            Expression expression4corrTotal206Pb238US = buildExpression("4-corrTotal 206Pb/238US",
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"] / [\"4-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio,"
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + [\"4-corrExtPerrA\"] ^ 2),"
                    + "false)", false, true, false);
            samRadiogenicCols.add(expression4corrTotal206Pb238US);

            // this is same as for RM above, so add "S" for Sample
            Expression expression7corrTotal206Pb238US = buildExpression("7-corrTotal 206Pb/238US",
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"] / [\"7-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio,"
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + [\"7-corrExtPerrA\"] ^ 2),"
                    + "false)", false, true, false);
            samRadiogenicCols.add(expression7corrTotal206Pb238US);

            // perm1 only
            if (!isDirectAltPD) {
                // this is same as for RM above, so add "S" for Sample
                Expression expression8corrTotal206Pb238US = buildExpression("8-corrTotal 206Pb/238US",
                        "ValueModel("
                        + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"] / [\"8-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio,"
                        + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + [\"8-corrExtPerrA\"] ^ 2),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression8corrTotal206Pb238US);

                // special case
                Expression expression4corrTotal208Pb232ThS = buildExpression("4-corrTotal 208Pb/232ThS",
                        "ValueModel("
                        + "[\"Total 206Pb/238US\"] * [\"208/206\"] / [\"232Th/238US\"],"
                        + "SQRT([%\"208/206\"]^2 + [%\"Total 206Pb/238US\"]^2 + [%\"232Th/238US\"]^2),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression4corrTotal208Pb232ThS);

                Expression expression7corrTotal208Pb232ThS = buildExpression("7-corrTotal 208Pb/232ThS",
                        "ValueModel("
                        + "[\"Total 206Pb/238US\"] * [\"208/206\"] / [\"232Th/238US\"],"
                        + "SQRT([%\"208/206\"]^2 + [%\"Total 206Pb/238US\"]^2 + [%\"232Th/238US\"]^2),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression7corrTotal208Pb232ThS);

                Expression expression8corrTotal208Pb232ThS = buildExpression("8-corrTotal 208Pb/232ThS",
                        "ValueModel("
                        + "[\"Total 206Pb/238US\"] * [\"208/206\"] / [\"232Th/238US\"],"
                        + "SQRT([%\"208/206\"]^2 + [%\"Total 206Pb/238US\"]^2 + [%\"232Th/238US\"]^2),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression8corrTotal208Pb232ThS);
            } else {
                // perm2
                Expression expression4corrTotal208Pb232ThS = buildExpression("4-corrTotal 208Pb/232ThS",
                        "ValueModel("
                        + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"] / [\"4-corr 208Pb/232Thcalibr.const WM\"][0] * StdThPbRatio,"
                        + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"]^2 + [\"4-corrExtPerrA\"] ^ 2),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression4corrTotal208Pb232ThS);

                Expression expression7corrTotal208Pb232ThS = buildExpression("7-corrTotal 208Pb/232ThS",
                        "ValueModel("
                        + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"] / [\"7-corr 208Pb/232Thcalibr.const WM\"][0] * StdThPbRatio,"
                        + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"]^2 + [\"7-corrExtPerrA\"] ^ 2),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression7corrTotal208Pb232ThS);
            }
        } else {
            // perm3 and perm4
            Expression expression4corrTotal208Pb232ThS = buildExpression("4-corrTotal 208Pb/232ThS",
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"] / [\"4-corr 208Pb/232Thcalibr.const WM\"][0] * StdThPbRatio,"
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"]^2 + [\"4-corrExtPerrA\"] ^ 2),"
                    + "false)", false, true, false);
            samRadiogenicCols.add(expression4corrTotal208Pb232ThS);

            Expression expression7corrTotal208Pb232ThS = buildExpression("7-corrTotal 208Pb/232ThS",
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"] / [\"7-corr 208Pb/232Thcalibr.const WM\"][0] * StdThPbRatio,"
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"]^2 + [\"7-corrExtPerrA\"] ^ 2),"
                    + "false)", false, true, false);
            samRadiogenicCols.add(expression7corrTotal208Pb232ThS);

            // perm3 special case - needs confirmation
            if (!isDirectAltPD) {
                Expression expression4corrTotal238U206PbS = buildExpression("4-corrTotal 206Pb/238US",
                        "ValueModel("
                        + "[\"Total 208Pb/232ThS\"] / [\"208/206\"] * [\"232Th/238US\"],"
                        + "SQRT( [%\"208/206\"]^2 + [%\"Total 208Pb/232ThS\"]^2 + \n"
                        + "[%\"232Th/238US\"]^2 ),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression4corrTotal238U206PbS);

                // repreat the math so the replacement engine works when creating "Total...
                Expression expression7corrTotal206PbS238U = buildExpression("7-corrTotal 206Pb/238US",
                        "ValueModel("
                        + "[\"Total 208Pb/232ThS\"] / [\"208/206\"] * [\"232Th/238US\"],"
                        + "SQRT( [%\"208/206\"]^2 + [%\"Total 208Pb/232ThS\"]^2 + \n"
                        + "[%\"232Th/238US\"]^2 ),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression7corrTotal206PbS238U);
            }
        }
        // perm4
        if (parentNuclide.contains("232") && isDirectAltPD) {
            // this is same as for RM above, so add "S" for Sample
            Expression expression4corrTotal206Pb238US = buildExpression("4-corrTotal 206Pb/238US",
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"] / [\"4-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio,"
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + [\"4-corrExtPerrA\"] ^ 2),"
                    + "false)", false, true, false);
            samRadiogenicCols.add(expression4corrTotal206Pb238US);

            // this is same as for RM above, so add "S" for Sample
            Expression expression7corrTotal206Pb238US = buildExpression("7-corrTotal 206Pb/238US",
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]  / [\"7-corr 206Pb/238Ucalibr.const WM\"][0] * StdUPbRatio,"
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + [\"7-corrExtPerrA\"] ^ 2),"
                    + "false)", false, true, false);
            samRadiogenicCols.add(expression7corrTotal206Pb238US);
        }

        // ppm values of radiogenic Pb
        Expression expression4corrPPM206 = buildExpression("4-corr ppm 206*",
                "[\"Total 206Pb/238US\"] * [\"ppmU\"] * L859 *\n"
                + " ( 1 - [\"204/206\"] * sComm_64 )", false, true, false);
        samRadiogenicCols.add(expression4corrPPM206);

        Expression expression7corrPPM206 = buildExpression("7-corr ppm 206*",
                "[\"Total 206Pb/238US\"] * [\"ppmU\"] * L859 *\n"
                + " ( 1 - [\"7-corr204Pb/206Pb\"] * sComm_64 )", false, true, false);
        samRadiogenicCols.add(expression7corrPPM206);

        Expression expression8corrPPM206 = buildExpression("8-corr ppm 206*",
                "[\"Total 206Pb/238US\"] * [\"ppmU\"] * L859 *\n"
                + " ( 1 - [\"8-corr204Pb/206Pb\"] * sComm_64 )", false, true, false);
        samRadiogenicCols.add(expression8corrPPM206);

        Expression expression4corrPPM208 = buildExpression("4-corr ppm 208*",
                "[\"4-corr ppm 206*\"] * [\"4-corr208Pb*/206Pb*\"] * 208 / 206", false, true, false);
        samRadiogenicCols.add(expression4corrPPM208);

        Expression expression7corrPPM208 = buildExpression("7-corr ppm 208*",
                "[\"7-corr ppm 206*\"] * [\"7-corr208Pb*/206Pb*S\"] * 208 / 206", false, true, false);
        samRadiogenicCols.add(expression7corrPPM208);

        Expression expression4corr206238 = buildExpression("4-corr 206*/238S",
                "ValueModel("
                + "[\"Total 206Pb/238US\"] * radd6,"
                + "SQRT( [%\"Total 206Pb/238US\"]^2 + ( sComm_64 *  \n"
                + " [%\"204/206\"] / ( 1 / [\"204/206\"] - sComm_64) )^2 ),"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression4corr206238);

        Expression expression4corr238206 = buildExpression("4-corr 238/206",
                "ValueModel("
                + "1 / [\"4-corr 206*/238S\"],"
                + "[%\"4-corr 206*/238S\"],"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression4corr238206);

        //some ages
        String d1 = "( NetAlpha * [%\"Total 206Pb/238US\"] / 100 )^2";
        String d3 = "( [%\"204/206\"] * sComm_64 / 100 )^2";
        String d4 = "(( [\"Total 206Pb/238US\"] * [\"204/206\"] )^2)";
        String d5 = "(( 1 / lambda238 / \n"
                + "EXP( lambda238 * (LN( 1 + [\"4-corr 206*/238S\"] ) / lambda238) ) )^2)";
        Expression expression204corr206Pb238UAge = buildExpression("204corr 206Pb/238U Age",
                "ValueModel("
                + "(LN( 1 + [\"4-corr 206*/238S\"] ) / lambda238),"
                + "SQRT(" + d5 + " * " + d4 + " *  (" + d1 + " + " + d3 + ") ),"
                + "true)", false, true, false);
        samRadiogenicCols.add(expression204corr206Pb238UAge);

        /**
         * The next step is to perform the superset of calculations enabled by
         * the measurement of ["207/206"]. These start with 4corr 207
         */
        Expression expressionTotal207Pb206PbS = buildExpression("Total 207Pb/206PbS",
                "[\"207/206\"]", false, true, false);
        samRadiogenicCols.add(expressionTotal207Pb206PbS);

        String t1 = "( ( [\"207/206\"] - ABS(NetBeta / NetAlpha) ) * [%\"204/206\"] \n"
                + "/ 100 / [\"204/206\"] )^2";
        String t3 = "( [%\"207/206\"] / [\"204/206\"] / 100 * [\"207/206\"] )^2";

        Expression expression4corr207206 = buildExpression("4-corr 207*/206*",
                "ValueModel("
                + "ABS(NetBeta / NetAlpha),"
                + "ABS( SQRT(" + t1 + " + " + t3 + ") / NetAlpha * 100 / ABS(NetBeta / NetAlpha) ),"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression4corr207206);

        Expression expression204corr207P206PbAge = buildExpression("204corr 207Pb/206Pb Age",
                "AgePb76WithErr( [\"4-corr 207*/206*\"], "
                + "([\"4-corr 207*/206*\"] * [%\"4-corr 207*/206*\"] / 100))", false, true, false);
        samRadiogenicCols.add(expression204corr207P206PbAge);

        // QUESTIONS HERE ABOUT LOGIC
        Expression expression4corr207235 = buildExpression("4-corr 207*/235S",
                "ValueModel("
                + "([\"4-corr 207*/206*\"] * [\"4-corr 206*/238S\"] * r238_235s),"
                + "SQRT( [%\"4-corr 207*/206*\"]^2 + \n"
                + "[%\"4-corr 206*/238S\"]^2 ),"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression4corr207235);

        Expression expression4corrErrCorr = buildExpression("4-corr err corr",
                "[%\"4-corr 206*/238S\"] / [%\"4-corr 207*/235S\"]", false, true, false);
        samRadiogenicCols.add(expression4corrErrCorr);

        String R68i = "(EXP(lambda238 * [\"204corr 207Pb/206Pb Age\"] ) - 1)";
        Expression expression204corrDiscordance = buildExpression("204corr Discordance",
                "100 * ( 1 - [\"4-corr 206*/238S\"] / " + R68i + ")", false, true, false);
        samRadiogenicCols.add(expression204corrDiscordance);

        Expression expression207corr206Pb238UAgeWithErr = buildExpression("207corr 206Pb/238U Age",
                "Age7corrWithErr("
                + "[\"Total 206Pb/238US\"],"
                + "[%\"Total 206Pb/238US\"] / 100 * [\"Total 206Pb/238US\"], "
                + "[\"Total 207Pb/206PbS\"],"
                + "[±\"Total 207Pb/206PbS\"],"
                + "sComm_76)",
                false, true, false);
        samRadiogenicCols.add(expression207corr206Pb238UAgeWithErr);

        Expression expression4corr208232 = buildExpression("4-corr 208*/232",
                "ValueModel("
                + "[\"Total 208Pb/232ThS\"] * radd8,"
                + "SQRT( [%\"Total 208Pb/232ThS\"]^2 + \n"
                + " ( sComm_84 / NetGamma )^2 * [%\"204/206\"]^2),"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression4corr208232);

        Expression expression204corr208Pb232ThAge = buildExpression("204corr 208Pb/232Th Age",
                "ValueModel("
                + "(LN( 1 + [\"4-corr 208*/232\"] ) / lambda232),"
                + "([\"4-corr 208*/232\"] / lambda232 / "
                + "(1 + [\"4-corr 208*/232\"]) * [%\"4-corr 208*/232\"] / 100),"
                + "true)", false, true, false);
        samRadiogenicCols.add(expression204corr208Pb232ThAge);

        Expression expression7corr206238 = buildExpression("7-corr 206*/238S",
                "EXP (lambda238 * [\"207corr 206Pb/238U Age\"] ) - 1", false, true, false);
        samRadiogenicCols.add(expression7corr206238);

        Expression expression7corr206238PctErr = buildExpression("7-corr 206*/238S %err",
                "lambda238 * EXP(lambda238 * [\"207corr 206Pb/238U Age\"] ) *\n"
                + "[±\"207corr 206Pb/238U Age\"] / [\"7-corr 206*/238S\"] * 100", false, true, false);
        samRadiogenicCols.add(expression7corr206238PctErr);

        Expression expression207corr208Pb232ThAge = buildExpression("207corr 208Pb/232Th Age",
                "Age7CorrPb8Th2WithErr("
                + "[\"Total 206Pb/238US\"],"
                + "[%\"Total 206Pb/238US\"],"
                + "[\"Total 208Pb/232ThS\"], "
                + "[%\"Total 208Pb/232ThS\"],\n"
                + "[\"208/206\"], "
                + "[%\"208/206\"], "
                + "[\"207/206\"], "
                + "[%\"207/206\"],"
                + "sComm_64,"
                + "sComm_76,"
                + "sComm_86) ", false, true, false);
        samRadiogenicCols.add(expression207corr208Pb232ThAge);

        Expression expression7corr208232 = buildExpression("7-corr 208*/232",
                "EXP ( lambda232 * [\"207corr 208Pb/232Th Age\"] ) - 1", false, true, false);
        samRadiogenicCols.add(expression7corr208232);

        Expression expression7corr208232PctErr = buildExpression("7-corr 208*/232 %err",
                "lambda232 * EXP( lambda232 *\n"
                + "[\"207corr 208Pb/232Th Age\"] ) *\n"
                + "[±\"207corr 208Pb/232Th Age\"] / [\"7-corr 208*/232\"] * 100", false, true, false);
        samRadiogenicCols.add(expression7corr208232PctErr);

        Expression expression208corr206Pb238UAge1SigmaErr = buildExpression("208corr 206Pb/238U Age",
                "Age8corrWithErr( "
                + "[\"Total 206Pb/238US\"],"
                + "[%\"Total 206Pb/238US\"] / 100 * [\"Total 206Pb/238US\"],"
                + "[\"Total 208Pb/232ThS\"],"
                + "[%\"Total 208Pb/232ThS\"] / 100 * [\"Total 208Pb/232ThS\"],"
                + "[\"232Th/238US\"], "
                + "[±\"232Th/238US\"],"
                + "sComm_86)", false, true, false);
        samRadiogenicCols.add(expression208corr206Pb238UAge1SigmaErr);

        Expression expression8corr206238 = buildExpression("8-corr 206*/238S",
                "ValueModel("
                + "Pb206U238rad( [\"208corr 206Pb/238U Age\"]),"
                + "lambda238 * ( 1 + "
                + "Pb206U238rad( [\"208corr 206Pb/238U Age\"])"
                + " ) * \n"
                + "[±\"208corr 206Pb/238U Age\"] * 100 / "
                + "Pb206U238rad( [\"208corr 206Pb/238U Age\"]),"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression8corr206238);

        Expression expressionEXP_8CORR_238_206_STAR = buildExpression(EXP_8CORR_238_206_STAR,
                "VALUEMODEL("
                + "1 / [\"8-corr 206*/238S\"],"
                + "[%\"8-corr 206*/238S\"],"
                + "false)", false, true, false);
        samRadiogenicCols.add(expressionEXP_8CORR_238_206_STAR);

        Expression expression8corr207235 = buildExpression("8-corr 207*/235S",
                "Rad8corPb7U5WithErr( "
                + "[\"Total 206Pb/238US\"],"
                + "[%\"Total 206Pb/238US\"],"
                + "[\"8-corr 206*/238S\"],"
                + "[\"Total 206Pb/238US\"] * [\"207/206\"] / r238_235s,"
                + "[\"232Th/238US\"], "
                + "[%\"232Th/238US\"],"
                + "[\"207/206\"],"
                + "[%\"207/206\"],"
                + "[\"208/206\"],"
                + "[%\"208/206\"],"
                + "sComm_76,"
                + "sComm_86)", false, true, false);
        samRadiogenicCols.add(expression8corr207235);

        Expression expression8correrrcorr = buildExpression("8-corr err corr",
                "Rad8corConcRho( "
                + "[\"Total 206Pb/238US\"], "
                + "[%\"Total 206Pb/238US\"],"
                + "[\"8-corr 206*/238S\"],"
                + "[\"232Th/238US\"],"
                + "[%\"232Th/238US\"],"
                + "[\"207/206\"],"
                + "[%\"207/206\"],"
                + "[\"208/206\"],"
                + "[%\"208/206\"],"
                + "sComm_76,"
                + "sComm_86)", false, true, false);
        samRadiogenicCols.add(expression8correrrcorr);

        Expression expression8corr207206 = buildExpression("8-corr 207*/206*S",
                "[\"8-corr 207*/235S\"] / [\"8-corr 206*/238S\"] / r238_235s ", false, true, false);
        samRadiogenicCols.add(expression8corr207206);

        Expression expression8corr207206PctErr = buildExpression("8-corr 207*/206*S %err",
                "SQRT([%\"8-corr 207*/235S\"]^2 + [%\"8-corr 206*/238S\"]^2 -\n"
                + " 2 * [%\"8-corr 207*/235S\"] * [%\"8-corr 206*/238S\"] * [\"8-corr err corr\"] )", false, true, false);
        samRadiogenicCols.add(expression8corr207206PctErr);

        Expression expression208corr207Pb206PbAge = buildExpression("208corr 207Pb/206PbS Age",
                "AgePb76WithErr( [\"8-corr 207*/206*S\"], "
                + "([\"8-corr 207*/206*S\"] * [\"8-corr 207*/206*S %err\"] / 100))", false, true, false);
        samRadiogenicCols.add(expression208corr207Pb206PbAge);

        R68i = "(EXP(lambda238 * [\"208corr 207Pb/206PbS Age\"] ) - 1)";
        Expression expression208corrDiscordance = buildExpression("208corr Discordance",
                "100 * ( 1 - [\"8-corr 206*/238S\"] / " + R68i + ")", false, true, false);
        samRadiogenicCols.add(expression208corrDiscordance);

        Expression expressionTotal238U206Pb = buildExpression("Total 238U/206PbS",
                "ValueModel("
                + "1 / [\"Total 206Pb/238US\"],"
                + "[%\"Total 206Pb/238US\"],"
                + "false)", false, true, false);
        samRadiogenicCols.add(expressionTotal238U206Pb);

        return samRadiogenicCols;
    }

    private static Expression buildExpression(String name, String excelExpression, boolean isRefMatCalc, boolean isSampleCalc, boolean isSummaryCalc) {
        Expression expression = new Expression(name, excelExpression);
        expression.setSquidSwitchNU(false);

        ExpressionTreeInterface expressionTree = expression.getExpressionTree();
        expressionTree.setSquidSwitchSTReferenceMaterialCalculation(isRefMatCalc);
        expressionTree.setSquidSwitchSAUnknownCalculation(isSampleCalc);
        expressionTree.setSquidSwitchSCSummaryCalculation(isSummaryCalc);

        expressionTree.setSquidSwitchConcentrationReferenceMaterialCalculation(false);
        expressionTree.setSquidSpecialUPbThExpression(true);
        expressionTree.setRootExpressionTree(true);

        return expression;
    }

}
