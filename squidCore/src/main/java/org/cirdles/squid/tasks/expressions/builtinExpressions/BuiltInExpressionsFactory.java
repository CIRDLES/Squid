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
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
import static org.cirdles.squid.parameters.util.Lambdas.LAMBDA_230;
import static org.cirdles.squid.parameters.util.Lambdas.LAMBDA_232;
import static org.cirdles.squid.parameters.util.Lambdas.LAMBDA_234;
import static org.cirdles.squid.parameters.util.Lambdas.LAMBDA_235;
import static org.cirdles.squid.parameters.util.Lambdas.LAMBDA_238;
import static org.cirdles.squid.parameters.util.RadDates.age206_238r;
import static org.cirdles.squid.parameters.util.RadDates.age207_206r;
import static org.cirdles.squid.parameters.util.RadDates.age208_232r;
import org.cirdles.squid.tasks.expressions.Expression;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.CORR_8_PRIMARY_CALIB_CONST_PCT_DELTA;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.EXP_8CORR_238_206_STAR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.L1033;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.L859;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA230;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA232;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA234;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA235;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA238;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.OVER_COUNTS_PERSEC_4_8;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.OVER_COUNT_4_6_8;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR206_238AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR208_232AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8COR_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PRESENT_R238_235S_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R206_204B;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207_204B;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207_206B;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R208_204B;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R208_206B;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SCOMM_64_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SCOMM_68_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SCOMM_74_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SCOMM_76_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SCOMM_84_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SCOMM_86_NAME;
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
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_TOTAL_206_238_NAME_S;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_TOTAL_208_232_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_TOTAL_208_232_NAME_S;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.STD_7_6;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.STD_AGE_PB_PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.STD_AGE_TH_PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.STD_AGE_U_PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.STD_RAD_8_6_FACT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.STD_TH_CONC_PPM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.STD_TH_PB_RATIO;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.STD_U_CONC_PPM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.STD_U_PB_RATIO;
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

        ExpressionTreeInterface PRESENT_R238_235S = new ConstantNode(PRESENT_R238_235S_NAME, Squid3Constants.PRESENT_R238_235S);
        parameters.put(PRESENT_R238_235S.getName(), PRESENT_R238_235S);

        ExpressionTreeInterface L859exp = new ConstantNode(L859, 0.859);
        parameters.put(L859, L859exp);

        ExpressionTreeInterface L1033exp = new ConstantNode(L1033, 1.033);
        parameters.put(L1033, L1033exp);

        ExpressionTreeInterface extPErr = new ConstantNode(SQUID_ASSIGNED_PBU_EXTERNAL_ONE_SIGMA_PCT_ERR, 0.75);
        parameters.put(SQUID_ASSIGNED_PBU_EXTERNAL_ONE_SIGMA_PCT_ERR, extPErr);

        return parameters;
    }

    public static SortedSet<Expression> updatePhysicalConstantsParameterValuesFromModel(ParametersModel physicalConstantsModel) {
        SortedSet<Expression> parameterValues = new TreeSet<>();

        String notes = "from Physical Constants model: " + physicalConstantsModel.getModelNameWithVersion();
        Expression expressionslambda230 = buildExpression(LAMBDA230,
                String.valueOf(physicalConstantsModel.getDatumByName(LAMBDA_230.getName()).getValue().doubleValue()), true, true, true);
        expressionslambda230.setParameterValue(true);
        expressionslambda230.setNotes(notes);
        parameterValues.add(expressionslambda230);

        Expression expressionslambda232 = buildExpression(LAMBDA232,
                String.valueOf(physicalConstantsModel.getDatumByName(LAMBDA_232.getName()).getValue().doubleValue()), true, true, true);
        expressionslambda232.setParameterValue(true);
        expressionslambda232.setNotes(notes);
        parameterValues.add(expressionslambda232);

        Expression expressionslambda234 = buildExpression(LAMBDA234,
                String.valueOf(physicalConstantsModel.getDatumByName(LAMBDA_234.getName()).getValue().doubleValue()), true, true, true);
        expressionslambda234.setParameterValue(true);
        expressionslambda234.setNotes(notes);
        parameterValues.add(expressionslambda234);

        Expression expressionslambda235 = buildExpression(LAMBDA235,
                String.valueOf(physicalConstantsModel.getDatumByName(LAMBDA_235.getName()).getValue().doubleValue()), true, true, true);
        expressionslambda235.setParameterValue(true);
        expressionslambda235.setNotes(notes);
        parameterValues.add(expressionslambda235);

        Expression expressionslambda238 = buildExpression(LAMBDA238,
                String.valueOf(physicalConstantsModel.getDatumByName(LAMBDA_238.getName()).getValue().doubleValue()), true, true, true);
        expressionslambda238.setParameterValue(true);
        expressionslambda238.setNotes(notes);
        parameterValues.add(expressionslambda238);

        return parameterValues;
    }

    public static SortedSet<Expression> updateCommonLeadParameterValuesFromModel(ParametersModel commonPbModel) {
        SortedSet<Expression> parameterValues = new TreeSet<>();

        String notes = "from Common Pb model: " + commonPbModel.getModelNameWithVersion();
        Expression expressionsComm_64 = buildExpression(SCOMM_64_NAME,
                String.valueOf(commonPbModel.getDatumByName(R206_204B).getValue().doubleValue()), true, true, true);
        expressionsComm_64.setParameterValue(true);
        expressionsComm_64.setNotes(notes);
        parameterValues.add(expressionsComm_64);

        Expression expressionsComm_74 = buildExpression(SCOMM_74_NAME,
                String.valueOf(commonPbModel.getDatumByName(R207_204B).getValue().doubleValue()), true, true, true);
        expressionsComm_74.setParameterValue(true);
        expressionsComm_74.setNotes(notes);
        parameterValues.add(expressionsComm_74);

        Expression expressionsComm_84 = buildExpression(SCOMM_84_NAME,
                String.valueOf(commonPbModel.getDatumByName(R208_204B).getValue().doubleValue()), true, true, true);
        expressionsComm_84.setParameterValue(true);
        expressionsComm_84.setNotes(notes);
        parameterValues.add(expressionsComm_84);

        Expression expressionsComm_76 = buildExpression(SCOMM_76_NAME,
                String.valueOf(commonPbModel.getDatumByName(R207_206B).getValue().doubleValue()), true, true, true);
        expressionsComm_76.setParameterValue(true);
        expressionsComm_76.setNotes(notes);
        parameterValues.add(expressionsComm_76);

        Expression expressionsComm_86 = buildExpression(SCOMM_86_NAME,
                String.valueOf(commonPbModel.getDatumByName(R208_206B).getValue().doubleValue()), true, true, true);
        expressionsComm_86.setParameterValue(true);
        expressionsComm_86.setNotes(notes);
        parameterValues.add(expressionsComm_86);

        Expression expressionsComm_68 = buildExpression(SCOMM_68_NAME,
                "1.0/" + String.valueOf(commonPbModel.getDatumByName(R208_206B).getValue().doubleValue()), true, true, true);
        expressionsComm_68.setParameterValue(true);
        expressionsComm_68.setNotes(notes);
        parameterValues.add(expressionsComm_68);

        return parameterValues;
    }

    public static SortedSet<Expression> updateConcReferenceMaterialValuesFromModel(ReferenceMaterialModel concReferenceMaterialModel) {
        SortedSet<Expression> parameterValues = new TreeSet<>();

        String notes = "from Conc. Reference Material model: " + concReferenceMaterialModel.getModelNameWithVersion();

        Expression expressionStdUConcPpm = buildExpression(STD_U_CONC_PPM,
                String.valueOf(((ReferenceMaterialModel) concReferenceMaterialModel)
                        .getConcentrationByName("concU").getValue().doubleValue()), true, true, true);
        expressionStdUConcPpm.setReferenceMaterialValue(true);
        expressionStdUConcPpm.setNotes(notes);
        parameterValues.add(expressionStdUConcPpm);

        Expression expressionStdThConcPpm = buildExpression(STD_TH_CONC_PPM,
                String.valueOf(((ReferenceMaterialModel) concReferenceMaterialModel)
                        .getConcentrationByName("concTh").getValue().doubleValue()), true, true, true);
        expressionStdThConcPpm.setReferenceMaterialValue(true);
        expressionStdThConcPpm.setNotes(notes);
        parameterValues.add(expressionStdThConcPpm);

        return parameterValues;
    }

    public static SortedSet<Expression> updateReferenceMaterialValuesFromModel(ReferenceMaterialModel referenceMaterialModel) {
        SortedSet<Expression> referenceMaterialValues = new TreeSet<>();

        String notes = "from Reference Material model: " + referenceMaterialModel.getModelNameWithVersion();

        Expression expressionStdAgeUPb = buildExpression(STD_AGE_U_PB,
                String.valueOf(((ReferenceMaterialModel) referenceMaterialModel)
                        .getDateByName(age206_238r.getName()).getValue().doubleValue()), true, true, true);
        expressionStdAgeUPb.setReferenceMaterialValue(true);
        expressionStdAgeUPb.setNotes(notes);
        referenceMaterialValues.add(expressionStdAgeUPb);

        Expression expressionStdAgeThPb = buildExpression(STD_AGE_TH_PB,
                String.valueOf(((ReferenceMaterialModel) referenceMaterialModel)
                        .getDateByName(age208_232r.getName()).getValue().doubleValue()), true, true, true);
        expressionStdAgeThPb.setReferenceMaterialValue(true);
        expressionStdAgeThPb.setNotes(notes);
        referenceMaterialValues.add(expressionStdAgeThPb);

        Expression expressionStdAgePbPb = buildExpression(STD_AGE_PB_PB,
                String.valueOf(((ReferenceMaterialModel) referenceMaterialModel)
                        .getDateByName(age207_206r.getName()).getValue().doubleValue()), true, true, true);
        expressionStdAgePbPb.setReferenceMaterialValue(true);
        expressionStdAgePbPb.setNotes(notes);
        referenceMaterialValues.add(expressionStdAgePbPb);

        notes = "Calculated from Reference Material model: " + referenceMaterialModel.getModelNameWithVersion();

        Expression expressionStdUPbRatio = buildExpression(STD_U_PB_RATIO,
                "EXP(" + LAMBDA238 + "*" + STD_AGE_U_PB + ")-1", true, true, true);
        expressionStdUPbRatio.setReferenceMaterialValue(true);
        expressionStdUPbRatio.setNotes(notes);
        referenceMaterialValues.add(expressionStdUPbRatio);

        Expression expressionStdThPbRatio = buildExpression(STD_TH_PB_RATIO,
                "EXP(" + LAMBDA232 + "*" + STD_AGE_TH_PB + ")-1", true, true, true);
        expressionStdThPbRatio.setReferenceMaterialValue(true);
        expressionStdThPbRatio.setNotes(notes);
        referenceMaterialValues.add(expressionStdThPbRatio);

        Expression expressionStd_76 = buildExpression(STD_7_6,
                "Pb76(" + STD_AGE_PB_PB + ")", true, true, true);
        expressionStd_76.setReferenceMaterialValue(true);
        expressionStd_76.setNotes(notes);
        referenceMaterialValues.add(expressionStd_76);

        Expression expressionStdRad86fact = buildExpression(STD_RAD_8_6_FACT,
                "" + STD_TH_PB_RATIO + "/" + STD_U_PB_RATIO + "", true, true, true);
        expressionStdRad86fact.setReferenceMaterialValue(true);
        expressionStdRad86fact.setNotes(notes);
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

            Expression expressionSQUID_PPM_PARENT_EQN_NAME_TH = buildExpression(SQUID_PPM_PARENT_EQN_NAME_TH,
                    "1", true, false, false);
            placeholderExpressions.add(expressionSQUID_PPM_PARENT_EQN_NAME_TH);

            Expression expressionOVER_COUNT_4_6_8 = buildExpression(OVER_COUNT_4_6_8,
                    "1", true, false, false);
            placeholderExpressions.add(expressionOVER_COUNT_4_6_8);

            Expression expressionOVER_COUNTS_PERSEC_4_8 = buildExpression(OVER_COUNTS_PERSEC_4_8,
                    "1", true, false, false);
            placeholderExpressions.add(expressionOVER_COUNTS_PERSEC_4_8);

            Expression expressionCORR_8_PRIMARY_CALIB_CONST_PCT_DELTA = buildExpression(CORR_8_PRIMARY_CALIB_CONST_PCT_DELTA,
                    "1", true, false, false);
            placeholderExpressions.add(expressionCORR_8_PRIMARY_CALIB_CONST_PCT_DELTA);

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
                "[\"" + SQUID_PPM_PARENT_EQN_NAME + "\"]/[\"" + SQUID_MEAN_PPM_PARENT_NAME + "\"]*" + STD_U_CONC_PPM, true, true, false);
        concentrationExpressionsOrdered.add(expressionPpmU);

        if (!isDirectAltPD) {
            // TODO: promote this and tie to physical constants model
            // handles SecondaryParentPpmFromThU
            //String uConstant = "1.033"; // 1.033 gives perfect fidelity to Squid 2.5 //((238/232) * r238_235s / (r238_235s - 1.0))";
            Expression expressionPpmTh = buildExpression(SQUID_PPM_PARENT_EQN_NAME_TH,
                    "[\"" + SQUID_PPM_PARENT_EQN_NAME_U + "\"]*[\"" + SQUID_TH_U_EQN_NAME + "\"]/" + L1033, true, false, false);
            concentrationExpressionsOrdered.add(expressionPpmTh);

            Expression expressionPpmThS = buildExpression(SQUID_PPM_PARENT_EQN_NAME_TH_S,
                    "[\"" + SQUID_PPM_PARENT_EQN_NAME_U + "\"]*[\"" + SQUID_TH_U_EQN_NAME_S + "\"]/" + L1033, false, true, false);
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

            concentrationExpressionsOrdered.addAll(generate204207MeansAndAgesForRefMaterialsU());
            concentrationExpressionsOrdered.addAll(generate204207MeansAndAgesForRefMaterialsTh());

            // math for ThUfromA1A2 follows here
            // for 204Pb ref material
            String exp238 = "(EXP(" + LAMBDA238 + "*[\"" + PB4COR206_238AGE + "\"])-1)";
            String exp232 = "(EXP(" + LAMBDA232 + "*[\"" + PB4COR208_232AGE + "\"])-1)";

            Expression expression4corrSQUID_TH_U_EQN_NAME = buildExpression(PB4COR_RM + SQUID_TH_U_EQN_NAME,
                    "ValueModel("
                    + "[\"" + PB4COR_RM + "208Pb*/206Pb*\"]*" + exp238 + "/" + exp232 + ","
                    + "SQRT([%\"" + PB4COR_RM + "208Pb*/206Pb*\"]^2+\n"
                    + "[%\"" + PB4COR_RM + "206Pb/238Ucalibr.const\"]^2+\n"
                    + "[%\"" + PB4COR_RM + "208Pb/232Thcalibr.const\"]^2),"
                    + "false)", true, false, false);
            concentrationExpressionsOrdered.add(expression4corrSQUID_TH_U_EQN_NAME);

            Expression expression4corrPpmTh = buildExpression(PB4COR_RM + SQUID_PPM_PARENT_EQN_NAME_TH,
                    "[\"" + PB4COR_RM + SQUID_TH_U_EQN_NAME + "\"]*[\"" + SQUID_PPM_PARENT_EQN_NAME_U + "\"]*0.9678", true, false, false);
            concentrationExpressionsOrdered.add(expression4corrPpmTh);

            // for 207Pb ref material
            exp238 = "(EXP(" + LAMBDA238 + "*[\"" + PB7COR_RM + "206Pb/238U Age\"])-1)";
            exp232 = "(EXP(" + LAMBDA232 + "*[\"" + PB7COR_RM + "208Pb/232Th Age\"])-1)";

            Expression expression7corrSQUID_TH_U_EQN_NAME = buildExpression(PB7COR_RM + SQUID_TH_U_EQN_NAME,
                    "ValueModel([\"" + PB7COR_RM + "208Pb*/206Pb*\"]*" + exp238 + "/" + exp232 + ","
                    + "SQRT([%\"" + PB7COR_RM + "208Pb*/206Pb*\"]^2+\n"
                    + "[%\"" + PB7COR_RM + "206Pb/238Ucalibr.const\"]^2+\n"
                    + "[%\"" + PB7COR_RM + "208Pb/232Thcalibr.const\"]^2),"
                    + "false)", true, false, false);
            concentrationExpressionsOrdered.add(expression7corrSQUID_TH_U_EQN_NAME);

            Expression expression7corrPpmTh = buildExpression(PB7COR_RM + SQUID_PPM_PARENT_EQN_NAME_TH,
                    "[\"" + PB7COR_RM + SQUID_TH_U_EQN_NAME + "\"]*[\"" + SQUID_PPM_PARENT_EQN_NAME_U + "\"]*0.9678", true, false, false);
            concentrationExpressionsOrdered.add(expression7corrPpmTh);

            // for samples
            Expression expressionPpmThS = buildExpression(SQUID_PPM_PARENT_EQN_NAME_TH_S,
                    "[\"" + SQUID_TH_U_EQN_NAME_S + "\"]*[\"" + SQUID_PPM_PARENT_EQN_NAME_U + "\"]*0.9678", false, true, false);
            concentrationExpressionsOrdered.add(expressionPpmThS);

            Expression expression4corrSQUID_TH_U_EQN_NAMEs = buildExpression(PB4COR_RM + SQUID_TH_U_EQN_NAME_S,
                    "ValueModel("
                    + "[\"208/206\"]*[\"" + PB4COR_RM + SQUID_TOTAL_206_238_NAME_S + "\"]/[\"" + PB4COR_RM + SQUID_TOTAL_208_232_NAME_S + "\"],"
                    + "SQRT([%\"208/206\"]^2+[%\"" + PB4COR_RM + SQUID_TOTAL_206_238_NAME_S + "\"]^2+\n"
                    + "[%\"" + PB4COR_RM + SQUID_TOTAL_208_232_NAME_S + "\"]^2),"
                    + "false)", false, true, false);
            concentrationExpressionsOrdered.add(expression4corrSQUID_TH_U_EQN_NAMEs);

            Expression expression7corrSQUID_TH_U_EQN_NAMEs = buildExpression(PB7COR_RM + SQUID_TH_U_EQN_NAME_S,
                    "ValueModel("
                    + "[\"208/206\"]*[\"" + PB7COR_RM + SQUID_TOTAL_206_238_NAME_S + "\"]/[\"" + PB7COR_RM + SQUID_TOTAL_208_232_NAME_S + "\"],"
                    + "SQRT( [%\"208/206\"]^2 + [%\"" + PB7COR_RM + SQUID_TOTAL_206_238_NAME_S + "\"]^2+\n"
                    + "[%\"" + PB7COR_RM + SQUID_TOTAL_208_232_NAME_S + "\"]^2),"
                    + "false)", false, true, false);
            concentrationExpressionsOrdered.add(expression7corrSQUID_TH_U_EQN_NAMEs);

        } // end test of directAltD

        Expression expression4CorrExtPerrU = buildExpression(PB4COR_RM + "ExtPerrU",
                "Max(ExtPErr, "
                + "[\"" + PB4COR_RM + "206Pb/238Ucalibr.const WM\"][1]/[\"" + PB4COR_RM + "206Pb/238Ucalibr.const WM\"][0]*100)", true, false, true);
        Expression expression7CorrExtPerrU = buildExpression(PB7COR_RM + "ExtPerrU",
                "Max(ExtPErr, "
                + "[\"" + PB7COR_RM + "206Pb/238Ucalibr.const WM\"][1]/[\"" + PB7COR_RM + "206Pb/238Ucalibr.const WM\"][0]*100)", true, false, true);
        Expression expression8CorrExtPerrU = buildExpression(PB8COR_RM + "ExtPerrU",
                "Max(ExtPErr, "
                + "[\"" + PB8COR_RM + "206Pb/238Ucalibr.const WM\"][1]/[\"" + PB8COR_RM + "206Pb/238Ucalibr.const WM\"][0]*100)", true, false, true);
        Expression expression4CorrExtPerrT = buildExpression("" + PB4COR_RM + "ExtPerrT",
                "Max(ExtPErr, "
                + "[\"" + PB4COR_RM + "208Pb/232Thcalibr.const WM\"][1]/[\"" + PB4COR_RM + "208Pb/232Thcalibr.const WM\"][0]*100)", true, false, true);
        Expression expression7CorrExtPerrT = buildExpression("" + PB7COR_RM + "ExtPerrT",
                "Max(ExtPErr, "
                + "[\"" + PB7COR_RM + "208Pb/232Thcalibr.const WM\"][1]/[\"" + PB7COR_RM + "208Pb/232Thcalibr.const WM\"][0]*100)", true, false, true);

        // perm 2,3,4
        if (parentNuclide.contains("232") || isDirectAltPD) {
            concentrationExpressionsOrdered.add(expression4CorrExtPerrT);
            concentrationExpressionsOrdered.add(expression7CorrExtPerrT);
        }
        // perm 1,2,4
        if (!parentNuclide.contains("232") || isDirectAltPD) {
            concentrationExpressionsOrdered.add(expression4CorrExtPerrU);
            concentrationExpressionsOrdered.add(expression7CorrExtPerrU);
        }
        // perm1
        if (!parentNuclide.contains("232") && !isDirectAltPD) {
            concentrationExpressionsOrdered.add(expression8CorrExtPerrU);
        }

        return concentrationExpressionsOrdered;
    }

    /**
     * TODO: These should probably be segregated to the end of the expression
     * list and not be sorted each time?
     *
     * Ludwig Q4 part 1
     *
     * @param isDirectAltPD
     * @return
     */
    public static SortedSet<Expression> generateOverCountExpressions(boolean isDirectAltPD) {
        SortedSet<Expression> overCountExpressionsOrdered = new TreeSet<>();

        Expression expressionOverCount4_6_7 = buildExpression("204/206 (fr. 207)",
                "ValueModel("
                + "([\"207/206\"] - " + STD_7_6 + " ) / ( " + SCOMM_74_NAME + "  - (" + STD_7_6 + " * " + SCOMM_64_NAME + ")),"
                + "ABS( [%\"207/206\"] * [\"207/206\"] / ([\"207/206\"] - " + STD_7_6 + ") ),"
                + "false)", true, false, false);
        overCountExpressionsOrdered.add(expressionOverCount4_6_7);

        Expression expressionOverCountPerSec4_7 = buildExpression("204 overcts/sec (fr. 207)",
                "TotalCps([\"204\"]) - TotalCps([\"BKG\"]) - [\"204/206 (fr. 207)\"] * ( TotalCps([\"206\"]) - TotalCps([\"BKG\"]))", true, false, false);
        overCountExpressionsOrdered.add(expressionOverCountPerSec4_7);

        Expression expressionOverCount7CorrCalib = buildExpression(PB7COR_RM + "Primary calib const. delta%",
                "100 * ( (1 - " + SCOMM_64_NAME + " * [\"204/206\"]) / (1 - " + SCOMM_64_NAME + " * [\"204/206 (fr. 207)\"]) - 1 )", true, false, false);
        overCountExpressionsOrdered.add(expressionOverCount7CorrCalib);

        // new section to accoommodate reporting corrections per Bodorkos 13 Aug 2018
        if (!isDirectAltPD) {
            Expression expressionOverCount4_6_8 = buildExpression(OVER_COUNT_4_6_8,
                    "( [\"208/206\"] - " + STD_RAD_8_6_FACT + " * [\"" + SQUID_TH_U_EQN_NAME + "\"] ) / (" + SCOMM_84_NAME + " - " + STD_RAD_8_6_FACT + " * [\"" + SQUID_TH_U_EQN_NAME + "\"] * " + SCOMM_64_NAME + " )", true, false, false);
            overCountExpressionsOrdered.add(expressionOverCount4_6_8);

            Expression expressionOverCountPerSec4_8 = buildExpression(OVER_COUNTS_PERSEC_4_8,
                    "TotalCps([\"204\"]) - TotalCps([\"BKG\"]) - [\"" + OVER_COUNT_4_6_8 + "\"] * ( TotalCps([\"206\"]) - TotalCps([\"BKG\"]))", true, false, false);
            overCountExpressionsOrdered.add(expressionOverCountPerSec4_8);

            Expression expressionOverCount8CorrCalib = buildExpression(CORR_8_PRIMARY_CALIB_CONST_PCT_DELTA,
                    "100 * ( (1 - " + SCOMM_64_NAME + " * [\"204/206\"]) / (1 - " + SCOMM_64_NAME + " * [\"" + OVER_COUNT_4_6_8 + "\"]) - 1 ) ", true, false, false);
            overCountExpressionsOrdered.add(expressionOverCount8CorrCalib);

        } else {
            // isDirectAltPD true
            Expression expression4CorrOverCount4_6_8 = buildExpression(PB4COR_RM + OVER_COUNT_4_6_8,
                    "( [\"208/206\"] - " + STD_RAD_8_6_FACT + " * [\"" + PB4COR_RM + SQUID_TH_U_EQN_NAME + "\"] ) "
                    + "/ (" + SCOMM_84_NAME + " - " + STD_RAD_8_6_FACT + " * [\"" + PB4COR_RM + SQUID_TH_U_EQN_NAME + "\"] * " + SCOMM_64_NAME + " )", true, false, false);
            overCountExpressionsOrdered.add(expression4CorrOverCount4_6_8);

            Expression expression4CorrOverCountPerSec4_8 = buildExpression(PB4COR_RM + OVER_COUNTS_PERSEC_4_8,
                    "TotalCps([\"204\"]) - TotalCps([\"BKG\"]) - [\"" + PB4COR_RM + OVER_COUNT_4_6_8 + "\"]"
                    + " * ( TotalCps([\"206\"]) - TotalCps([\"BKG\"]))", true, false, false);
            overCountExpressionsOrdered.add(expression4CorrOverCountPerSec4_8);

            Expression expression4CorrOverCount8CorrCalib = buildExpression("" + PB4COR_RM + CORR_8_PRIMARY_CALIB_CONST_PCT_DELTA,
                    "100 * ( (1 - " + SCOMM_64_NAME + " * [\"204/206\"]) / (1 - " + SCOMM_64_NAME + " * [\"" + PB4COR_RM + OVER_COUNT_4_6_8 + "\"]) - 1 ) ", true, false, false);
            overCountExpressionsOrdered.add(expression4CorrOverCount8CorrCalib);

            Expression expression7CorrOverCount4_6_8 = buildExpression(PB7COR_RM + OVER_COUNT_4_6_8,
                    "( [\"208/206\"] - " + STD_RAD_8_6_FACT + " * [\"" + PB7COR_RM + SQUID_TH_U_EQN_NAME + "\"] ) "
                    + "/ (" + SCOMM_84_NAME + " - " + STD_RAD_8_6_FACT + " * [\"" + PB7COR_RM + SQUID_TH_U_EQN_NAME + "\"] * " + SCOMM_64_NAME + " )", true, false, false);
            overCountExpressionsOrdered.add(expression7CorrOverCount4_6_8);

            Expression expression7CorrOverCountPerSec4_8 = buildExpression(PB7COR_RM + OVER_COUNTS_PERSEC_4_8,
                    "TotalCps([\"204\"]) - TotalCps([\"BKG\"]) - [\"" + PB7COR_RM + OVER_COUNT_4_6_8 + "\"]"
                    + " * ( TotalCps([\"206\"]) - TotalCps([\"BKG\"]))", true, false, false);
            overCountExpressionsOrdered.add(expression7CorrOverCountPerSec4_8);

            Expression expression7CorrOverCount8CorrCalib = buildExpression(PB7COR_RM + CORR_8_PRIMARY_CALIB_CONST_PCT_DELTA,
                    "100 * ( (1 - " + SCOMM_64_NAME + " * [\"204/206\"]) / (1 - " + SCOMM_64_NAME + " * [\"" + PB7COR_RM + OVER_COUNT_4_6_8 + "\"]) - 1 ) ", true, false, false);
            overCountExpressionsOrdered.add(expression7CorrOverCount8CorrCalib);
        }

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
        Expression expression7Corr46 = buildExpression(PB7COR_RM + "204Pb/206Pb",
                "Pb46cor7( "
                + "[\"207/206\"],"
                + " [\"207corr 206Pb/238U Age\"])", false, true, false);
        perSpotPbCorrectionsOrdered.add(expression7Corr46);

        Expression expression8Corr46 = buildExpression(PB8COR_RM + "204Pb/206Pb",
                "Pb46cor8( "
                + "[\"208/206\"],"
                + " [\"" + SQUID_TH_U_EQN_NAME_S + "\"],"
                + " [\"208corr 206Pb/238U Age\"])", false, true, false);
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
        Expression expression4corCom206 = buildExpression(PB4COR_RM + "%com206",
                "100 * " + SCOMM_64_NAME + " * [\"204/206\"]", true, true, false);
        perSpotPbCorrectionsOrdered.add(expression4corCom206);

        Expression expression7corCom206 = buildExpression(PB7COR_RM + "%com206",
                "100 * " + SCOMM_64_NAME + " * [\"204/206 (fr. 207)\"]", true, false, false);
        perSpotPbCorrectionsOrdered.add(expression7corCom206);

        Expression expression8corCom206 = buildExpression(PB8COR_RM + "%com206",
                "100 * " + SCOMM_64_NAME + " * [\"" + OVER_COUNT_4_6_8 + "\"]", true, false, false);
        perSpotPbCorrectionsOrdered.add(expression8corCom206);

        Expression expression4corCom208 = buildExpression(PB4COR_RM + "%com208",
                "100 * " + SCOMM_84_NAME + " / [\"208/206\"] * [\"204/206\"]", true, true, false);
        perSpotPbCorrectionsOrdered.add(expression4corCom208);

        Expression expression7corCom208 = buildExpression(PB7COR_RM + "%com208",
                "100 * " + SCOMM_84_NAME + " / [\"208/206\"] * [\"204/206 (fr. 207)\"]", true, false, false);
        perSpotPbCorrectionsOrdered.add(expression7corCom208);

        // for samples
        Expression expression7corCom206S = buildExpression(PB7COR_RM + "%com206S",
                "100 * " + SCOMM_64_NAME + " * [\"" + PB7COR_RM + "204Pb/206Pb\"]", false, true, false);
        perSpotPbCorrectionsOrdered.add(expression7corCom206S);

        Expression expression8corCom206S = buildExpression(PB8COR_RM + "%com206S",
                "100 * " + SCOMM_64_NAME + " * [\"" + PB8COR_RM + "204Pb/206Pb\"]", false, true, false);
        perSpotPbCorrectionsOrdered.add(expression8corCom206S);

        Expression expression7corCom208S = buildExpression(PB7COR_RM + "%com208S",
                "100 * " + SCOMM_84_NAME + " / [\"208/206\"] * [\"" + PB7COR_RM + "204Pb/206Pb\"]", false, true, false);
        perSpotPbCorrectionsOrdered.add(expression7corCom208S);

        // The next step is to calculate all the applicable radiogenic 208Pb/206Pb values. 
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 4-corr 208Pb*/206Pb*  *** Start
        // ref material and sample version
        Expression expression4corr208Pb206Pb = buildExpression(PB4COR_RM + "208Pb*/206Pb*",
                "ValueModel("
                + "( [\"208/206\"] / [\"204/206\"] - " + SCOMM_84_NAME + " ) / ( 1 / [\"204/206\"] - " + SCOMM_64_NAME + "),"
                + "100 * sqrt( ( ([%\"208/206\"] / 100 * [\"208/206\"])^2 +\n"
                + "(( [\"208/206\"] / [\"204/206\"] - " + SCOMM_84_NAME + " ) / ( 1 / [\"204/206\"] - " + SCOMM_64_NAME + ")"
                + " * " + SCOMM_64_NAME + " - " + SCOMM_84_NAME + ")^2 * ([%\"204/206\"] / 100 * [\"204/206\"])^2 )  \n"
                + " / (1 - " + SCOMM_64_NAME + " * [\"204/206\"]) ^2  ) / "
                + "abs( ( [\"208/206\"] / [\"204/206\"] - " + SCOMM_84_NAME + " ) / ( 1 / [\"204/206\"] - " + SCOMM_64_NAME + ") ),"
                + "false)", true, true, false);
        perSpotPbCorrectionsOrdered.add(expression4corr208Pb206Pb);

        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 4-corr 208Pb*/206Pb*  *** End
        //
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 7-corr 208Pb*/206Pb*  *** Start
        // ref material version
        Expression expression7corr208Pb206Pb = buildExpression(PB7COR_RM + "208Pb*/206Pb*",
                "ValueModel("
                + "( [\"208/206\"] / [\"204/206 (fr. 207)\"] - " + SCOMM_84_NAME + " ) / \n"
                + "( 1 / [\"204/206 (fr. 207)\"] - " + SCOMM_64_NAME + "),"
                + "StdPb86radCor7per("
                + "[\"208/206\"], "
                + "[\"207/206\"], "
                + "( [\"208/206\"] / [\"204/206 (fr. 207)\"] - " + SCOMM_84_NAME + " ) / \n"
                + "( 1 / [\"204/206 (fr. 207)\"] - " + SCOMM_64_NAME + "), "
                + "[\"204/206 (fr. 207)\"]),"
                + "false)", true, false, false);
        perSpotPbCorrectionsOrdered.add(expression7corr208Pb206Pb);

        // sample version
        Expression expression7corr208Pb206PbS = buildExpression(PB7COR_RM + "208Pb*/206Pb*S",
                "ValueModel("
                + "( [\"208/206\"] / [\"" + PB7COR_RM + "204Pb/206Pb\"] - " + SCOMM_84_NAME + " ) / \n"
                + "( 1 / [\"" + PB7COR_RM + "204Pb/206Pb\"] - " + SCOMM_64_NAME + "),"
                + "Pb86radCor7per("
                + "[\"208/206\"], "
                + "[\"207/206\"], "
                + "[\"" + SQUID_TOTAL_206_238_NAME_S + "\"], "
                + "[%\"" + SQUID_TOTAL_206_238_NAME_S + "\"],"
                + "[\"207corr 206Pb/238U Age\"]),"
                + "false)", false, true, false);
        perSpotPbCorrectionsOrdered.add(expression7corr208Pb206PbS);

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
        Expression expression4corr206Pb238Ucalibrconst = buildExpression(PB4COR_RM + "206Pb/238Ucalibr.const",
                "ValueModel("
                + "(1 - [\"204/206\"] * " + SCOMM_64_NAME + ") * [\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"],"
                + "sqrt([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + \n"
                + "( " + SCOMM_64_NAME + " / ( 1 / [\"204/206\"] - " + SCOMM_64_NAME + " ) )^2 * [%\"204/206\"]^2 ),"
                + "false)", true, false, false);
        meansAndAgesForRefMaterials.add(expression4corr206Pb238Ucalibrconst);

        // weighted mean
        Expression expression4corr206Pb238UcalibrconstWM = buildExpression(PB4COR_RM + "206Pb/238Ucalibr.const WM",
                "WtdMeanACalc( [\"" + PB4COR_RM + "206Pb/238Ucalibr.const\"], [%\"" + PB4COR_RM + "206Pb/238Ucalibr.const\"], FALSE, FALSE )", true, false, true);
        meansAndAgesForRefMaterials.add(expression4corr206Pb238UcalibrconstWM);

        // age calc
        Expression expression4corr206Pb238UAge = buildExpression(PB4COR206_238AGE,
                "ValueModel("
                + "LN( 1.0 + [\"" + PB4COR_RM + "206Pb/238Ucalibr.const\"] / [\"" + PB4COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + " ) / " + LAMBDA238 + ","
                + "[%\"" + PB4COR_RM + "206Pb/238Ucalibr.const\"] / 100 * ( EXP(" + LAMBDA238 + " * \n"
                + "LN( 1.0 + [\"" + PB4COR_RM + "206Pb/238Ucalibr.const\"] / [\"" + PB4COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + " ) / " + LAMBDA238 + "    ) - 1 )"
                + "/ " + LAMBDA238 + " / EXP(" + LAMBDA238 + " * "
                + "LN( 1.0 + [\"" + PB4COR_RM + "206Pb/238Ucalibr.const\"] / [\"" + PB4COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + " ) / " + LAMBDA238 + "     ),"
                + "true)", true, false, false);
        meansAndAgesForRefMaterials.add(expression4corr206Pb238UAge);

        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 4-corr  206/238  *** END
        //
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 7-corr  206/238  *** Start
        Expression expression7corr206Pb238Ucalibrconst = buildExpression(PB7COR_RM + "206Pb/238Ucalibr.const",
                "ValueModel("
                + "(1 - [\"204/206 (fr. 207)\"] * " + SCOMM_64_NAME + ") * [\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"],"
                + "sqrt([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 +\n"
                + "( " + SCOMM_64_NAME + " / (1 / [\"204/206 (fr. 207)\"] - " + SCOMM_64_NAME + " ) )^2 * \n"
                + "[%\"204/206 (fr. 207)\"]^2),"
                + "false)", true, false, false);
        meansAndAgesForRefMaterials.add(expression7corr206Pb238Ucalibrconst);

        // weighted mean
        Expression expression7corr206Pb238UcalibrconstWM = buildExpression(PB7COR_RM + "206Pb/238Ucalibr.const WM",
                "WtdMeanACalc( [\"" + PB7COR_RM + "206Pb/238Ucalibr.const\"], [%\"" + PB7COR_RM + "206Pb/238Ucalibr.const\"], FALSE, FALSE )", true, false, true);
        meansAndAgesForRefMaterials.add(expression7corr206Pb238UcalibrconstWM);

        // age calc
        Expression expression7corr206Pb238UAge = buildExpression(PB7COR_RM + "206Pb/238U Age",
                "ValueModel("
                + "LN( 1.0 + [\"" + PB7COR_RM + "206Pb/238Ucalibr.const\"] / [\"" + PB7COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + " ) / " + LAMBDA238 + ","
                + "[%\"" + PB7COR_RM + "206Pb/238Ucalibr.const\"] / 100 * ( EXP(" + LAMBDA238 + " * "
                + "LN( 1.0 + [\"" + PB7COR_RM + "206Pb/238Ucalibr.const\"] / [\"" + PB7COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + " ) / " + LAMBDA238 + " "
                + ") - 1 )\n"
                + "/ " + LAMBDA238 + " / EXP(" + LAMBDA238 + " * "
                + "LN( 1.0 + [\"" + PB7COR_RM + "206Pb/238Ucalibr.const\"] / [\"" + PB7COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + " ) / " + LAMBDA238 + ""
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
        Expression expression8corr206Pb238Ucalibrconst = buildExpression(PB8COR_RM + "206Pb/238Ucalibr.const",
                "ValueModel("
                + "(1 - [\"" + OVER_COUNT_4_6_8 + "\"] * " + SCOMM_64_NAME + ") * [\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"],"
                + "SQRT( [%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + \n"
                + "      ( " + SCOMM_64_NAME + " * [\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"] * [\"" + OVER_COUNT_4_6_8 + "\"] / "
                + "      ((1 - [\"" + OVER_COUNT_4_6_8 + "\"] * " + SCOMM_64_NAME + ") * [\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]) )^2 * \n"
                + "      ( ( [\"208/206\"] * [%\"208/206\"] /( [\"208/206\"] - " + STD_RAD_8_6_FACT + " * [\"" + SQUID_TH_U_EQN_NAME + "\"] ) )^2 +\n"
                + "        ( ( 1 /( [\"208/206\"] - " + STD_RAD_8_6_FACT + " * [\"" + SQUID_TH_U_EQN_NAME + "\"] ) + \n"
                + "          " + SCOMM_64_NAME + " / ( " + SCOMM_84_NAME + " - " + SCOMM_64_NAME + " * " + STD_RAD_8_6_FACT + " * [\"" + SQUID_TH_U_EQN_NAME + "\"] ) ) *\n"
                + "          " + STD_RAD_8_6_FACT + " * [\"" + SQUID_TH_U_EQN_NAME + "\"] * [%\"" + SQUID_TH_U_EQN_NAME + "\"]\n"
                + "        )^2\n"
                + "      )\n"
                + "    ), false)", true, false, false);
        meansAndAgesForRefMaterials.add(expression8corr206Pb238Ucalibrconst);

        // weighted mean
        Expression expression8corr206Pb238UcalibrconstWM = buildExpression(PB8COR_RM + "206Pb/238Ucalibr.const WM",
                "WtdMeanACalc( [\"" + PB8COR_RM + "206Pb/238Ucalibr.const\"], [%\"" + PB8COR_RM + "206Pb/238Ucalibr.const\"], FALSE, FALSE )", true, false, true);
        meansAndAgesForRefMaterials.add(expression8corr206Pb238UcalibrconstWM);

        // age calc
        Expression expression8corr206Pb238UAge = buildExpression(PB8COR_RM + "206Pb/238U Age",
                "ValueModel("
                + "LN( 1.0 + [\"" + PB8COR_RM + "206Pb/238Ucalibr.const\"] / [\"" + PB8COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + " ) / " + LAMBDA238 + ","
                + "[%\"" + PB8COR_RM + "206Pb/238Ucalibr.const\"] / 100 * ( EXP(" + LAMBDA238 + " * "
                + "LN( 1.0 + [\"" + PB8COR_RM + "206Pb/238Ucalibr.const\"] / [\"" + PB8COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + " ) / " + LAMBDA238 + ""
                + " ) - 1 )\n"
                + "/ " + LAMBDA238 + " / EXP(" + LAMBDA238 + " * "
                + "LN( 1.0 + [\"" + PB8COR_RM + "206Pb/238Ucalibr.const\"] / [\"" + PB8COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + " ) / " + LAMBDA238 + ""
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
        Expression expression4corr208Pb232Thcalibrconst = buildExpression(PB4COR_RM + "208Pb/232Thcalibr.const",
                "ValueModel("
                + "(1 - [\"204/206\"] / [\"208/206\"] * " + SCOMM_84_NAME + ") * [\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"],"
                + "sqrt([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"]^2 + \n"
                + "( " + SCOMM_84_NAME + " / ( [\"208/206\"] / [\"204/206\"] - " + SCOMM_84_NAME + " ) )^2 * [%\"204/206\"]^2 ),"
                + "false)", true, false, false);
        meansAndAgesForRefMaterials.add(expression4corr208Pb232Thcalibrconst);

        // weighted mean
        Expression expression4corr208Pb232ThcalibrconstWM = buildExpression(PB4COR_RM + "208Pb/232Thcalibr.const WM",
                "WtdMeanACalc( [\"" + PB4COR_RM + "208Pb/232Thcalibr.const\"], [%\"" + PB4COR_RM + "208Pb/232Thcalibr.const\"], FALSE, FALSE )", true, false, true);
        meansAndAgesForRefMaterials.add(expression4corr208Pb232ThcalibrconstWM);

        // age calc
        Expression expression4corr208Pb232ThAge = buildExpression(PB4COR208_232AGE,
                "ValueModel("
                + "LN( 1.0 + [\"" + PB4COR_RM + "208Pb/232Thcalibr.const\"] / [\"" + PB4COR_RM + "208Pb/232Thcalibr.const WM\"][0] * " + STD_TH_PB_RATIO + " ) / " + LAMBDA232 + ","
                + "[%\"" + PB4COR_RM + "208Pb/232Thcalibr.const\"] / 100 * ( EXP(" + LAMBDA232 + " * "
                + "LN( 1.0 + [\"" + PB4COR_RM + "208Pb/232Thcalibr.const\"] / [\"" + PB4COR_RM + "208Pb/232Thcalibr.const WM\"][0] * " + STD_TH_PB_RATIO + " ) / " + LAMBDA232 + ""
                + ") - 1 ) \n"
                + "/ " + LAMBDA232 + " / EXP(" + LAMBDA232 + " * "
                + "LN( 1.0 + [\"" + PB4COR_RM + "208Pb/232Thcalibr.const\"] / [\"" + PB4COR_RM + "208Pb/232Thcalibr.const WM\"][0] * " + STD_TH_PB_RATIO + " ) / " + LAMBDA232 + ""
                + " ),"
                + "true)", true, false, false);
        meansAndAgesForRefMaterials.add(expression4corr208Pb232ThAge);

        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 4-corr  208/232  *** END
        //
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 7-corr  208/232  *** Start
        Expression expression7corr208Pb232Thcalibrconst = buildExpression(PB7COR_RM + "208Pb/232Thcalibr.const",
                "ValueModel("
                + "(1 - [\"204/206 (fr. 207)\"] / [\"208/206\"] * " + SCOMM_84_NAME + ") * [\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"],"
                + "sqrt([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"]^2 +  \n"
                + "( " + SCOMM_84_NAME + " / ( [\"208/206\"] / [\"204/206 (fr. 207)\"] - " + SCOMM_84_NAME + " ) )^2 * \n"
                + "( [%\"208/206\"]^2 + [%\"204/206 (fr. 207)\"]^2 )),"
                + "false)", true, false, false);
        meansAndAgesForRefMaterials.add(expression7corr208Pb232Thcalibrconst);

        // weighted mean
        Expression expression7corr208Pb232ThcalibrconstWM = buildExpression(PB7COR_RM + "208Pb/232Thcalibr.const WM",
                "WtdMeanACalc( [\"" + PB7COR_RM + "208Pb/232Thcalibr.const\"], [%\"" + PB7COR_RM + "208Pb/232Thcalibr.const\"], FALSE, FALSE )", true, false, true);
        meansAndAgesForRefMaterials.add(expression7corr208Pb232ThcalibrconstWM);

        // age calc
        Expression expression7corr208Pb232ThAge = buildExpression(PB7COR_RM + "208Pb/232Th Age",
                "ValueModel("
                + "LN( 1.0 + [\"" + PB7COR_RM + "208Pb/232Thcalibr.const\"] / [\"" + PB7COR_RM + "208Pb/232Thcalibr.const WM\"][0] * " + STD_TH_PB_RATIO + " ) / " + LAMBDA232 + ","
                + "[%\"" + PB7COR_RM + "208Pb/232Thcalibr.const\"] / 100 * ( EXP(" + LAMBDA232 + " * "
                + "LN( 1.0 + [\"" + PB7COR_RM + "208Pb/232Thcalibr.const\"] / [\"" + PB7COR_RM + "208Pb/232Thcalibr.const WM\"][0] * " + STD_TH_PB_RATIO + " ) / " + LAMBDA232 + ""
                + " ) - 1 ) \n"
                + "/ " + LAMBDA232 + " / EXP(" + LAMBDA232 + " * "
                + "LN( 1.0 + [\"" + PB7COR_RM + "208Pb/232Thcalibr.const\"] / [\"" + PB7COR_RM + "208Pb/232Thcalibr.const WM\"][0] * " + STD_TH_PB_RATIO + " ) / " + LAMBDA232 + ""
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
                + "+ ([\"204/206\"]*(([\"207/206\"]/[\"204/206\"]-" + SCOMM_74_NAME + " )"
                + "/(1/[\"204/206\"]-" + SCOMM_64_NAME + ") * " + SCOMM_64_NAME + " - " + SCOMM_74_NAME + ") \n "
                + "* [%\"204/206\"])^2)";

        String term2 = "([\"207/206\"] - [\"204/206\"] * " + SCOMM_74_NAME + ")^2";

        Expression expression4corr207Pb206Pb = buildExpression(PB4COR_RM + "207Pb/206Pb",
                "ValueModel(([\"207/206\"]/[\"204/206\"]-" + SCOMM_74_NAME + " )/(1/[\"204/206\"]-" + SCOMM_64_NAME + "),"
                + "sqrt(" + term1 + "/" + term2 + "),"
                + "false)", true, false, false);
        overCountMeansRefMaterials.add(expression4corr207Pb206Pb);

        Expression expression4corr207Pb206PbAge = buildExpression(PB4COR_RM + "207Pb/206Pb age",
                "AgePb76WithErr( [\"" + PB4COR_RM + "207Pb/206Pb\"],"
                + "([\"" + PB4COR_RM + "207Pb/206Pb\"] * [%\"" + PB4COR_RM + "207Pb/206Pb\"] / 100 ))", true, false, false);
        overCountMeansRefMaterials.add(expression4corr207Pb206PbAge);

        // The second part of the subroutine calculates the various biweight means
        Expression expressionPb204OverCts7corr = buildExpression("Pb204OverCts7corr",
                "sqBiweight([\"204 overcts/sec (fr. 207)\"], 9)", true, false, true);
        expressionPb204OverCts7corr.setNotes("Robust avg 204 overcts assuming 206Pb/238U-207Pb/235U age concordance");
        overCountMeansRefMaterials.add(expressionPb204OverCts7corr);

        Expression expressionPb204OverCts8corr = buildExpression("Pb204OverCts8corr",
                "sqBiweight([\"" + OVER_COUNTS_PERSEC_4_8 + "\"], 9)", true, false, true);
        expressionPb204OverCts8corr.setNotes("Robust avg 204 overcts assuming 206Pb/238U-208Pb/232Th age concordance");
        overCountMeansRefMaterials.add(expressionPb204OverCts8corr);

        Expression expressionOverCtsDeltaP7corr = buildExpression("OverCtsDeltaP7corr",
                "sqBiweight([\"" + PB7COR_RM + "Primary calib const. delta%\"], 9)", true, false, true);
        expressionOverCtsDeltaP7corr.setNotes("Robust avg of diff. between 207-corr. and 204-corr. calibr. const.");
        overCountMeansRefMaterials.add(expressionOverCtsDeltaP7corr);

        Expression expressionOverCtsDeltaP8corr = buildExpression("OverCtsDeltaP8corr",
                "sqBiweight([\"" + CORR_8_PRIMARY_CALIB_CONST_PCT_DELTA + "\"], 9)", true, false, true);
        expressionOverCtsDeltaP8corr.setNotes("Robust avg of diff. between 208-corr. and 204-corr. calibr. const.");
        overCountMeansRefMaterials.add(expressionOverCtsDeltaP8corr);

        Expression expressionOverCts4corr207Pb206Pbagecorr = buildExpression("OverCts" + PB4COR_RM + "207Pb/206Pb agecorr",
                "sqBiweight([\"" + PB4COR_RM + "207Pb/206Pb age\"], 9)", true, false, true);
        expressionOverCts4corr207Pb206Pbagecorr.setNotes("Robust average of 204-corrected 207/206 age");
        overCountMeansRefMaterials.add(expressionOverCts4corr207Pb206Pbagecorr);

        return overCountMeansRefMaterials;

    }

    public static SortedSet<Expression> stdRadiogenicCols(String parentNuclide, boolean isDirectAltPD) {
        SortedSet<Expression> stdRadiogenicCols = new TreeSet<>();

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
            Expression expression4corrTotal206Pb238U = buildExpression(PB4COR_RM + SQUID_TOTAL_206_238_NAME,
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"] / [\"" + PB4COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + ","
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + [\"" + PB4COR_RM + "ExtPerrU\"] ^ 2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression4corrTotal206Pb238U);

            Expression expression7corrTotal206Pb238U = buildExpression(PB7COR_RM + SQUID_TOTAL_206_238_NAME,
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"] / [\"" + PB7COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + ","
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + [\"" + PB7COR_RM + "ExtPerrU\"] ^ 2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression7corrTotal206Pb238U);

            // perm1 only
            if (!isDirectAltPD) {
                Expression expression8corrTotal206Pb238U = buildExpression(PB8COR_RM + SQUID_TOTAL_206_238_NAME,
                        "ValueModel("
                        + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"] / [\"" + PB8COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + ","
                        + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + [\"" + PB8COR_RM + "ExtPerrU\"] ^ 2),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression8corrTotal206Pb238U);

                // special case
                Expression expression4corrTotal208Pb232Th = buildExpression(PB4COR_RM + SQUID_TOTAL_208_232_NAME,
                        "ValueModel("
                        + "[\"" + SQUID_TOTAL_206_238_NAME + "\"] * [\"208/206\"] / [\"" + SQUID_TH_U_EQN_NAME + "\"],"
                        + "SQRT([%\"208/206\"]^2 + [%\"" + SQUID_TOTAL_206_238_NAME + "\"]^2 + [%\"" + SQUID_TH_U_EQN_NAME + "\"]^2),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression4corrTotal208Pb232Th);

                Expression expression7corrTotal208Pb232Th = buildExpression(PB7COR_RM + SQUID_TOTAL_208_232_NAME,
                        "ValueModel("
                        + "[\"" + SQUID_TOTAL_206_238_NAME + "\"] * [\"208/206\"] / [\"" + SQUID_TH_U_EQN_NAME + "\"],"
                        + "SQRT([%\"208/206\"]^2 + [%\"" + SQUID_TOTAL_206_238_NAME + "\"]^2 + [%\"" + SQUID_TH_U_EQN_NAME + "\"]^2),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression7corrTotal208Pb232Th);

                Expression expression8corrTotal208Pb232Th = buildExpression(PB8COR_RM + SQUID_TOTAL_208_232_NAME,
                        "ValueModel("
                        + "[\"" + SQUID_TOTAL_206_238_NAME + "\"] * [\"208/206\"] / [\"" + SQUID_TH_U_EQN_NAME + "\"],"
                        + "SQRT([%\"208/206\"]^2 + [%\"" + SQUID_TOTAL_206_238_NAME + "\"]^2 + [%\"" + SQUID_TH_U_EQN_NAME + "\"]^2),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression8corrTotal208Pb232Th);

            } else {
                // perm2 only
                Expression expression4corrTotal208Pb232Th = buildExpression(PB4COR_RM + SQUID_TOTAL_208_232_NAME,
                        "ValueModel("
                        + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"] / [\"" + PB4COR_RM + "208Pb/232Thcalibr.const WM\"][0] * " + STD_TH_PB_RATIO + ","
                        + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"]^2 + [\"" + PB4COR_RM + "ExtPerrT\"] ^ 2),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression4corrTotal208Pb232Th);

                Expression expression7corrTotal208Pb232Th = buildExpression("" + PB7COR_RM + SQUID_TOTAL_208_232_NAME,
                        "ValueModel("
                        + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"] / [\"" + PB7COR_RM + "208Pb/232Thcalibr.const WM\"][0] * " + STD_TH_PB_RATIO + ","
                        + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"]^2 + [\"" + PB7COR_RM + "ExtPerrT\"] ^ 2),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression7corrTotal208Pb232Th);
            }
        } else {
            // perm3 and perm4
            Expression expression4corrTotal208Pb232Th = buildExpression(PB4COR_RM + SQUID_TOTAL_208_232_NAME,
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"] / [\"" + PB4COR_RM + "208Pb/232Thcalibr.const WM\"][0] * " + STD_TH_PB_RATIO + ","
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"]^2 + [\"" + PB4COR_RM + "ExtPerrT\"] ^ 2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression4corrTotal208Pb232Th);

            Expression expression7corrTotal208Pb232Th = buildExpression(PB7COR_RM + SQUID_TOTAL_208_232_NAME,
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"] / [\"" + PB7COR_RM + "208Pb/232Thcalibr.const WM\"][0] * " + STD_TH_PB_RATIO + ","
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"]^2 + [\"" + PB7COR_RM + "ExtPerrT\"] ^ 2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression7corrTotal208Pb232Th);

            // perm3 special case - needs confirmation
            if (!isDirectAltPD) {
                Expression expression4corrTotal238U206Pb = buildExpression(PB4COR_RM + SQUID_TOTAL_206_238_NAME,
                        "ValueModel("
                        + "[\"" + SQUID_TOTAL_208_232_NAME + "\"] / [\"208/206\"] * [\"" + SQUID_TH_U_EQN_NAME + "\"],"
                        + "SQRT( [%\"208/206\"]^2 + [%\"" + SQUID_TOTAL_208_232_NAME + "\"]^2 + \n"
                        + "[%\"" + SQUID_TH_U_EQN_NAME + "\"]^2 ),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression4corrTotal238U206Pb);

                // repeat the math so the replacement engine works when creating "Total...
                Expression expression7corrTotal206PbS238U = buildExpression(PB7COR_RM + SQUID_TOTAL_206_238_NAME,
                        "ValueModel("
                        + "[\"" + SQUID_TOTAL_208_232_NAME + "\"] / [\"208/206\"] * [\"" + SQUID_TH_U_EQN_NAME + "\"],"
                        + "SQRT( [%\"208/206\"]^2 + [%\"" + SQUID_TOTAL_208_232_NAME + "\"]^2 + \n"
                        + "[%\"" + SQUID_TH_U_EQN_NAME + "\"]^2 ),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression7corrTotal206PbS238U);
            }

        }
        // perm4
        if (parentNuclide.contains("232") && isDirectAltPD) {
            Expression expression4corrTotal206Pb238U = buildExpression(PB4COR_RM + SQUID_TOTAL_206_238_NAME,
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"] / [\"" + PB4COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + ","
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + [\"" + PB4COR_RM + "ExtPerrU\"] ^ 2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression4corrTotal206Pb238U);

            Expression expression7corrTotal206Pb238U = buildExpression(PB7COR_RM + SQUID_TOTAL_206_238_NAME,
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]  / [\"" + PB7COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + ","
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + [\"" + PB7COR_RM + "ExtPerrU\"] ^ 2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression7corrTotal206Pb238U);

        }

        if (parentNuclide.contains("232") && !isDirectAltPD) {
            // perm3 only
            Expression expression4corr206238 = buildExpression(PB4COR_RM + "206*/238",
                    "ValueModel("
                    + "[\"" + SQUID_TOTAL_206_238_NAME + "\"] * ( 1 - " + SCOMM_64_NAME + " * [\"204/206\"] ),"
                    + "SQRT( [%\"" + SQUID_TOTAL_206_238_NAME + "\"]^2 + \n"
                    + "      ( " + SCOMM_64_NAME + " * [%\"204/206\"] / ( 1/[\"204/206\"] - " + SCOMM_64_NAME + " ) )^2 ),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression4corr206238);

            Expression expression4corr207Pb235U = buildExpression("" + PB4COR_RM + "207*/235",
                    "ValueModel("
                    + "[\"" + PB4COR_RM + "207Pb/206Pb\"] * [\"" + PB4COR_RM + "206*/238\"] * r238_235s,"
                    + "sqrt( [%\"" + PB4COR_RM + "206*/238\"]^2 + [%\"" + PB4COR_RM + "207Pb/206Pb\"]^2 ),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression4corr207Pb235U);

            Expression expression4correrrcorr = buildExpression(PB4COR_RM + "errcorr",
                    "[%\"" + PB4COR_RM + "206*/238\"] / [%\"" + PB4COR_RM + "207*/235\"]", true, false, false);
            stdRadiogenicCols.add(expression4correrrcorr);

            Expression expression7corr206Pb238UAge = buildExpression(PB7COR_RM + "206Pb/238U Age",
                    "Age7corrWithErr("
                    + "[\"" + SQUID_TOTAL_206_238_NAME + "\"],"
                    + "[±\"" + SQUID_TOTAL_206_238_NAME + "\"], "
                    + "[\"207/206\"],"
                    + "[±\"207/206\"])",
                    true, false, false);
            stdRadiogenicCols.add(expression7corr206Pb238UAge);

            Expression expression7corr206238 = buildExpression(PB7COR_RM + "206*/238",
                    "ValueModel("
                    + "EXP( " + LAMBDA238 + " * [\"" + PB7COR_RM + "206Pb/238U Age\"] ) - 1,"
                    + "" + LAMBDA238 + " * EXP(" + LAMBDA238 + " * [\"" + PB7COR_RM + "206Pb/238U Age\"] ) *\n"
                    + "[±\"" + PB7COR_RM + "206Pb/238U Age\"] / "
                    + "(EXP( " + LAMBDA238 + " * [\"" + PB7COR_RM + "206Pb/238U Age\"] ) - 1)"
                    + "* 100 ,"
                    + "false)",
                    true, false, false);
            stdRadiogenicCols.add(expression7corr206238);

        } else {
            // perm 1,2,4

            Expression expression4corr206Pb238U = buildExpression(PB4COR_RM + "206*/238",
                    "ValueModel("
                    + "[\"" + PB4COR_RM + "206Pb/238Ucalibr.const\"] / [\"" + PB4COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + ","
                    + "[%\"" + PB4COR_RM + "206Pb/238Ucalibr.const\"],"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression4corr206Pb238U);

            Expression expression4corr207Pb235U = buildExpression(PB4COR_RM + "207*/235",
                    "ValueModel("
                    + "[\"" + PB4COR_RM + "207Pb/206Pb\"] * [\"" + PB4COR_RM + "206*/238\"] * r238_235s,"
                    + "sqrt( [%\"" + PB4COR_RM + "206*/238\"]^2 + [%\"" + PB4COR_RM + "207Pb/206Pb\"]^2 ),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression4corr207Pb235U);

            Expression expression4correrrcorr = buildExpression(PB4COR_RM + "errcorr",
                    "[%\"" + PB4COR_RM + "206*/238\"] / [%\"" + PB4COR_RM + "207*/235\"]", true, false, false);
            stdRadiogenicCols.add(expression4correrrcorr);

            Expression expression7corr206238 = buildExpression(PB7COR_RM + "206*/238",
                    "ValueModel("
                    + "[\"" + PB7COR_RM + "206Pb/238Ucalibr.const\"] / [\"" + PB7COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + ","
                    + "[%\"" + PB7COR_RM + "206Pb/238Ucalibr.const\"],"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression7corr206238);

            // perm1 only
            if (!parentNuclide.contains("232") && !isDirectAltPD) {
                Expression expression8corr206238 = buildExpression(PB8COR_RM + "206*/238",
                        "ValueModel("
                        + "[\"" + PB8COR_RM + "206Pb/238Ucalibr.const\"] / [\"" + PB8COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + ","
                        + "[%\"" + PB8COR_RM + "206Pb/238Ucalibr.const\"],"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression8corr206238);

                Expression expression8corr207235 = buildExpression(PB8COR_RM + "207*/235",
                        "Rad8corPb7U5WithErr( "
                        + "[\"" + SQUID_TOTAL_206_238_NAME + "\"],"
                        + "[%\"" + SQUID_TOTAL_206_238_NAME + "\"],"
                        + "[\"" + PB8COR_RM + "206*/238\"],"
                        + "[\"" + SQUID_TOTAL_206_238_NAME + "\"] * [\"207/206\"] / r238_235s,"
                        + "[\"" + SQUID_TH_U_EQN_NAME + "\"], "
                        + "[%\"" + SQUID_TH_U_EQN_NAME + "\"],"
                        + "[\"207/206\"],"
                        + "[%\"207/206\"],"
                        + "[\"208/206\"],"
                        + "[%\"208/206\"])", true, false, false);
                stdRadiogenicCols.add(expression8corr207235);

                Expression expression8correrrcorr = buildExpression(PB8COR_RM + "errcorr",
                        "Rad8corConcRho( "
                        + "[\"" + SQUID_TOTAL_206_238_NAME + "\"], "
                        + "[%\"" + SQUID_TOTAL_206_238_NAME + "\"],"
                        + "[\"" + PB8COR_RM + "206*/238\"],"
                        + "[\"" + SQUID_TH_U_EQN_NAME + "\"],"
                        + "[%\"" + SQUID_TH_U_EQN_NAME + "\"],"
                        + "[\"207/206\"],"
                        + "[%\"207/206\"],"
                        + "[\"208/206\"],"
                        + "[%\"208/206\"])", true, false, false);
                stdRadiogenicCols.add(expression8correrrcorr);

                Expression expression8corr207206 = buildExpression(PB8COR_RM + "207*/206*",
                        "ValueModel("
                        + "[\"" + PB8COR_RM + "207*/235\"]/[\"" + PB8COR_RM + "206*/238\"]/r238_235s,"
                        + "SQRT([%\"" + PB8COR_RM + "207*/235\"]^2+[%\"" + PB8COR_RM + "206*/238\"]^2-\n"
                        + "2*[%\"" + PB8COR_RM + "207*/235\"] * [%\"" + PB8COR_RM + "206*/238\"]*[\"" + PB8COR_RM + "errcorr\"]),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression8corr207206);

                Expression expression208corr207Pb206PbAge = buildExpression(PB8COR_RM + "207Pb/206Pb Age",
                        "AgePb76WithErr( [\"" + PB8COR_RM + "207*/206*\"], "
                        + "([\"" + PB8COR_RM + "207*/206*\"] * [%\"" + PB8COR_RM + "207*/206*\"] / 100))", true, false, false);
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
                "Alpha - " + SCOMM_64_NAME, false, true, false);
        samRadiogenicCols.add(expressionNetAlpha);

        Expression expressionBeta = buildExpression("Beta",
                "[\"207/206\"] / [\"204/206\"]", false, true, false);
        samRadiogenicCols.add(expressionBeta);

        Expression expressionNetBeta = buildExpression("NetBeta",
                "Beta - " + SCOMM_74_NAME, false, true, false);
        samRadiogenicCols.add(expressionNetBeta);

        Expression expressionGamma = buildExpression("Gamma",
                "[\"208/206\"] / [\"204/206\"]", false, true, false);
        samRadiogenicCols.add(expressionGamma);

        Expression expressionNetGamma = buildExpression("NetGamma",
                "Gamma - " + SCOMM_84_NAME, false, true, false);
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
            Expression expression4corrTotal206Pb238US = buildExpression(PB4COR_RM + SQUID_TOTAL_206_238_NAME_S,
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"] / [\"" + PB4COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + ","
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + [\"" + PB4COR_RM + "ExtPerrU\"] ^ 2),"
                    + "false)", false, true, false);
            samRadiogenicCols.add(expression4corrTotal206Pb238US);

            // this is same as for RM above, so add "S" for Sample
            Expression expression7corrTotal206Pb238US = buildExpression(PB7COR_RM + SQUID_TOTAL_206_238_NAME_S,
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"] / [\"" + PB7COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + ","
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + [\"" + PB7COR_RM + "ExtPerrU\"] ^ 2),"
                    + "false)", false, true, false);
            samRadiogenicCols.add(expression7corrTotal206Pb238US);

            // perm1 only
            if (!isDirectAltPD) {
                // this is same as for RM above, so add "S" for Sample
                Expression expression8corrTotal206Pb238US = buildExpression(PB8COR_RM + SQUID_TOTAL_206_238_NAME_S,
                        "ValueModel("
                        + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"] / [\"" + PB8COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + ","
                        + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + [\"" + PB8COR_RM + "ExtPerrU\"] ^ 2),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression8corrTotal206Pb238US);

                // special case
                Expression expression4corrTotal208Pb232ThS = buildExpression(PB4COR_RM + SQUID_TOTAL_208_232_NAME_S,
                        "ValueModel("
                        + "[\"" + SQUID_TOTAL_206_238_NAME_S + "\"] * [\"208/206\"] / [\"" + SQUID_TH_U_EQN_NAME_S + "\"],"
                        + "SQRT([%\"208/206\"]^2 + [%\"" + SQUID_TOTAL_206_238_NAME_S + "\"]^2 + [%\"" + SQUID_TH_U_EQN_NAME_S + "\"]^2),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression4corrTotal208Pb232ThS);

                Expression expression7corrTotal208Pb232ThS = buildExpression(PB7COR_RM + SQUID_TOTAL_208_232_NAME_S,
                        "ValueModel("
                        + "[\"" + SQUID_TOTAL_206_238_NAME_S + "\"] * [\"208/206\"] / [\"" + SQUID_TH_U_EQN_NAME_S + "\"],"
                        + "SQRT([%\"208/206\"]^2 + [%\"" + SQUID_TOTAL_206_238_NAME_S + "\"]^2 + [%\"" + SQUID_TH_U_EQN_NAME_S + "\"]^2),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression7corrTotal208Pb232ThS);

                Expression expression8corrTotal208Pb232ThS = buildExpression(PB8COR_RM + SQUID_TOTAL_208_232_NAME_S,
                        "ValueModel("
                        + "[\"" + SQUID_TOTAL_206_238_NAME_S + "\"] * [\"208/206\"] / [\"" + SQUID_TH_U_EQN_NAME_S + "\"],"
                        + "SQRT([%\"208/206\"]^2 + [%\"" + SQUID_TOTAL_206_238_NAME_S + "\"]^2 + [%\"" + SQUID_TH_U_EQN_NAME_S + "\"]^2),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression8corrTotal208Pb232ThS);
            } else {
                // perm2
                Expression expression4corrTotal208Pb232ThS = buildExpression(PB4COR_RM + SQUID_TOTAL_208_232_NAME_S,
                        "ValueModel("
                        + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"] / [\"" + PB4COR_RM + "208Pb/232Thcalibr.const WM\"][0] * " + STD_TH_PB_RATIO + ","
                        + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"]^2 + [\"" + PB4COR_RM + "ExtPerrT\"] ^ 2),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression4corrTotal208Pb232ThS);

                Expression expression7corrTotal208Pb232ThS = buildExpression(PB7COR_RM + SQUID_TOTAL_208_232_NAME_S,
                        "ValueModel("
                        + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"] / [\"" + PB7COR_RM + "208Pb/232Thcalibr.const WM\"][0] * " + STD_TH_PB_RATIO + ","
                        + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"]^2 + [\"" + PB7COR_RM + "ExtPerrT\"] ^ 2),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression7corrTotal208Pb232ThS);
            }
        } else {
            // perm3 and perm4
            Expression expression4corrTotal208Pb232ThS = buildExpression(PB4COR_RM + SQUID_TOTAL_208_232_NAME_S,
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"] / [\"" + PB4COR_RM + "208Pb/232Thcalibr.const WM\"][0] * " + STD_TH_PB_RATIO + ","
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"]^2 + [\"" + PB4COR_RM + "ExtPerrT\"] ^ 2),"
                    + "false)", false, true, false);
            samRadiogenicCols.add(expression4corrTotal208Pb232ThS);

            Expression expression7corrTotal208Pb232ThS = buildExpression(PB7COR_RM + SQUID_TOTAL_208_232_NAME_S,
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"] / [\"" + PB7COR_RM + "208Pb/232Thcalibr.const WM\"][0] * " + STD_TH_PB_RATIO + ","
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_TH + "\"]^2 + [\"" + PB7COR_RM + "ExtPerrT\"] ^ 2),"
                    + "false)", false, true, false);
            samRadiogenicCols.add(expression7corrTotal208Pb232ThS);

            // perm3 special case - needs confirmation
            if (!isDirectAltPD) {
                Expression expression4corrTotal238U206PbS = buildExpression(PB4COR_RM + SQUID_TOTAL_206_238_NAME_S,
                        "ValueModel("
                        + "[\"" + SQUID_TOTAL_208_232_NAME_S + "\"] / [\"208/206\"] * [\"" + SQUID_TH_U_EQN_NAME_S + "\"],"
                        + "SQRT( [%\"208/206\"]^2 + [%\"" + SQUID_TOTAL_208_232_NAME_S + "\"]^2 + \n"
                        + "[%\"" + SQUID_TH_U_EQN_NAME_S + "\"]^2 ),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression4corrTotal238U206PbS);

                // repreat the math so the replacement engine works when creating "Total...
                Expression expression7corrTotal206PbS238U = buildExpression(PB7COR_RM + SQUID_TOTAL_206_238_NAME_S,
                        "ValueModel("
                        + "[\"" + SQUID_TOTAL_208_232_NAME_S + "\"] / [\"208/206\"] * [\"" + SQUID_TH_U_EQN_NAME_S + "\"],"
                        + "SQRT( [%\"208/206\"]^2 + [%\"" + SQUID_TOTAL_208_232_NAME_S + "\"]^2 + \n"
                        + "[%\"" + SQUID_TH_U_EQN_NAME_S + "\"]^2 ),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression7corrTotal206PbS238U);
            }
        }
        // perm4
        if (parentNuclide.contains("232") && isDirectAltPD) {
            // this is same as for RM above, so add "S" for Sample
            Expression expression4corrTotal206Pb238US = buildExpression(PB4COR_RM + SQUID_TOTAL_206_238_NAME_S,
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"] / [\"" + PB4COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + ","
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + [\"" + PB4COR_RM + "ExtPerrU\"] ^ 2),"
                    + "false)", false, true, false);
            samRadiogenicCols.add(expression4corrTotal206Pb238US);

            // this is same as for RM above, so add "S" for Sample
            Expression expression7corrTotal206Pb238US = buildExpression(PB7COR_RM + SQUID_TOTAL_206_238_NAME_S,
                    "ValueModel("
                    + "[\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]  / [\"" + PB7COR_RM + "206Pb/238Ucalibr.const WM\"][0] * " + STD_U_PB_RATIO + ","
                    + "SQRT([%\"" + SQUID_PRIMARY_UTH_EQN_NAME_U + "\"]^2 + [\"" + PB7COR_RM + "ExtPerrU\"] ^ 2),"
                    + "false)", false, true, false);
            samRadiogenicCols.add(expression7corrTotal206Pb238US);
        }

        // ppm values of radiogenic Pb
        Expression expression4corrPPM206 = buildExpression(PB4COR_RM + "ppm 206*",
                "[\"" + SQUID_TOTAL_206_238_NAME_S + "\"] * [\"" + SQUID_PPM_PARENT_EQN_NAME_U + "\"] * " + L859 + " *\n"
                + " ( 1 - [\"204/206\"] * " + SCOMM_64_NAME + " )", false, true, false);
        samRadiogenicCols.add(expression4corrPPM206);

        Expression expression7corrPPM206 = buildExpression(PB7COR_RM + "ppm 206*",
                "[\"" + SQUID_TOTAL_206_238_NAME_S + "\"] * [\"" + SQUID_PPM_PARENT_EQN_NAME_U + "\"] * " + L859 + " *\n"
                + " ( 1 - [\"" + PB7COR_RM + "204Pb/206Pb\"] * " + SCOMM_64_NAME + " )", false, true, false);
        samRadiogenicCols.add(expression7corrPPM206);

        Expression expression8corrPPM206 = buildExpression(PB8COR_RM + "ppm 206*",
                "[\"" + SQUID_TOTAL_206_238_NAME_S + "\"] * [\"" + SQUID_PPM_PARENT_EQN_NAME_U + "\"] * " + L859 + " *\n"
                + " ( 1 - [\"" + PB8COR_RM + "204Pb/206Pb\"] * " + SCOMM_64_NAME + " )", false, true, false);
        samRadiogenicCols.add(expression8corrPPM206);

        Expression expression4corrPPM208 = buildExpression(PB4COR_RM + "ppm 208*",
                "[\"" + PB4COR_RM + "ppm 206*\"] * [\"" + PB4COR_RM + "208Pb*/206Pb*\"] * 208 / 206", false, true, false);
        samRadiogenicCols.add(expression4corrPPM208);

        Expression expression7corrPPM208 = buildExpression(PB7COR_RM + "ppm 208*",
                "[\"" + PB7COR_RM + "ppm 206*\"] * [\"" + PB7COR_RM + "208Pb*/206Pb*S\"] * 208 / 206", false, true, false);
        samRadiogenicCols.add(expression7corrPPM208);

        Expression expression4corr206238 = buildExpression(PB4COR_RM + "206*/238S",
                "ValueModel("
                + "[\"" + SQUID_TOTAL_206_238_NAME_S + "\"] * radd6,"
                + "SQRT( [%\"" + SQUID_TOTAL_206_238_NAME_S + "\"]^2 + ( " + SCOMM_64_NAME + " *  \n"
                + " [%\"204/206\"] / ( 1 / [\"204/206\"] - " + SCOMM_64_NAME + ") )^2 ),"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression4corr206238);

        Expression expression4corr238206 = buildExpression(PB4COR_RM + "238/206",
                "ValueModel("
                + "1 / [\"" + PB4COR_RM + "206*/238S\"],"
                + "[%\"" + PB4COR_RM + "206*/238S\"],"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression4corr238206);

        //some ages
        String d1 = "( NetAlpha * [%\"" + SQUID_TOTAL_206_238_NAME_S + "\"] / 100 )^2";
        String d3 = "( [%\"204/206\"] * " + SCOMM_64_NAME + " / 100 )^2";
        String d4 = "(( [\"" + SQUID_TOTAL_206_238_NAME_S + "\"] * [\"204/206\"] )^2)";
        String d5 = "(( 1 / " + LAMBDA238 + " / \n"
                + "EXP( " + LAMBDA238 + " * (LN( 1 + [\"" + PB4COR_RM + "206*/238S\"] ) / " + LAMBDA238 + ") ) )^2)";
        Expression expression204corr206Pb238UAge = buildExpression("204corr 206Pb/238U Age",
                "ValueModel("
                + "(LN( 1 + [\"" + PB4COR_RM + "206*/238S\"] ) / " + LAMBDA238 + "),"
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

        Expression expression4corr207206 = buildExpression(PB4COR_RM + "207*/206*",
                "ValueModel("
                + "ABS(NetBeta / NetAlpha),"
                + "ABS( SQRT(" + t1 + " + " + t3 + ") / NetAlpha * 100 / ABS(NetBeta / NetAlpha) ),"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression4corr207206);

        Expression expression204corr207P206PbAge = buildExpression("204corr 207Pb/206Pb Age",
                "AgePb76WithErr( [\"" + PB4COR_RM + "207*/206*\"], "
                + "([\"" + PB4COR_RM + "207*/206*\"] * [%\"" + PB4COR_RM + "207*/206*\"] / 100))", false, true, false);
        samRadiogenicCols.add(expression204corr207P206PbAge);

        // QUESTIONS HERE ABOUT LOGIC
        Expression expression4corr207235 = buildExpression(PB4COR_RM + "207*/235S",
                "ValueModel("
                + "([\"" + PB4COR_RM + "207*/206*\"] * [\"" + PB4COR_RM + "206*/238S\"] * r238_235s),"
                + "SQRT( [%\"" + PB4COR_RM + "207*/206*\"]^2 + \n"
                + "[%\"" + PB4COR_RM + "206*/238S\"]^2 ),"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression4corr207235);

        Expression expression4corrErrCorr = buildExpression(PB4COR_RM + "errcorrS",
                "[%\"" + PB4COR_RM + "206*/238S\"] / [%\"" + PB4COR_RM + "207*/235S\"]", false, true, false);
        samRadiogenicCols.add(expression4corrErrCorr);

        String R68i = "(EXP(" + LAMBDA238 + " * [\"204corr 207Pb/206Pb Age\"] ) - 1)";
        Expression expression204corrDiscordance = buildExpression("204corr Discordance",
                "100 * ( 1 - [\"" + PB4COR_RM + "206*/238S\"] / " + R68i + ")", false, true, false);
        samRadiogenicCols.add(expression204corrDiscordance);

        Expression expression207corr206Pb238UAgeWithErr = buildExpression("207corr 206Pb/238U Age",
                "Age7corrWithErr("
                + "[\"" + SQUID_TOTAL_206_238_NAME_S + "\"],"
                + "[%\"" + SQUID_TOTAL_206_238_NAME_S + "\"] / 100 * [\"" + SQUID_TOTAL_206_238_NAME_S + "\"], "
                + "[\"Total 207Pb/206PbS\"],"
                + "[±\"Total 207Pb/206PbS\"])",
                false, true, false);
        samRadiogenicCols.add(expression207corr206Pb238UAgeWithErr);

        Expression expression4corr208232 = buildExpression(PB4COR_RM + "208*/232",
                "ValueModel("
                + "[\"" + SQUID_TOTAL_208_232_NAME_S + "\"] * radd8,"
                + "SQRT( [%\"" + SQUID_TOTAL_208_232_NAME_S + "\"]^2 + \n"
                + " ( " + SCOMM_84_NAME + " / NetGamma )^2 * [%\"204/206\"]^2),"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression4corr208232);

        Expression expression204corr208Pb232ThAge = buildExpression("204corr 208Pb/232Th Age",
                "ValueModel("
                + "(LN( 1 + [\"" + PB4COR_RM + "208*/232\"] ) / " + LAMBDA232 + "),"
                + "([\"" + PB4COR_RM + "208*/232\"] / " + LAMBDA232 + " / "
                + "(1 + [\"" + PB4COR_RM + "208*/232\"]) * [%\"" + PB4COR_RM + "208*/232\"] / 100),"
                + "true)", false, true, false);
        samRadiogenicCols.add(expression204corr208Pb232ThAge);

        Expression expression7corr206238 = buildExpression(PB7COR_RM + "206*/238S",
                "ValueModel("
                + "EXP (" + LAMBDA238 + " * [\"207corr 206Pb/238U Age\"] ) - 1,"
                + "" + LAMBDA238 + " * EXP(" + LAMBDA238 + " * [\"207corr 206Pb/238U Age\"] ) *\n"
                + "[±\"207corr 206Pb/238U Age\"] / "
                + "(EXP (" + LAMBDA238 + " * [\"207corr 206Pb/238U Age\"] ) - 1) * 100,"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression7corr206238);

        Expression expression207corr208Pb232ThAge = buildExpression("207corr 208Pb/232Th Age",
                "Age7CorrPb8Th2WithErr("
                + "[\"" + SQUID_TOTAL_206_238_NAME_S + "\"],"
                + "[%\"" + SQUID_TOTAL_206_238_NAME_S + "\"],"
                + "[\"" + SQUID_TOTAL_208_232_NAME_S + "\"],"
                + "[%\"" + SQUID_TOTAL_208_232_NAME_S + "\"],\n"
                + "[\"208/206\"],"
                + "[%\"208/206\"],"
                + "[\"207/206\"],"
                + "[%\"207/206\"])", false, true, false);
        samRadiogenicCols.add(expression207corr208Pb232ThAge);

        Expression expression7corr208232 = buildExpression(PB7COR_RM + "208*/232S",
                "ValueModel("
                + "EXP ( " + LAMBDA232 + " * [\"207corr 208Pb/232Th Age\"] ) - 1,"
                + "" + LAMBDA232 + " * EXP( " + LAMBDA232 + " *\n"
                + "[\"207corr 208Pb/232Th Age\"] ) *\n"
                + "[±\"207corr 208Pb/232Th Age\"] / "
                + "(EXP ( " + LAMBDA232 + " * [\"207corr 208Pb/232Th Age\"] ) - 1) * 100,"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression7corr208232);

        Expression expression208corr206Pb238UAge1SigmaErr = buildExpression("208corr 206Pb/238U Age",
                "Age8corrWithErr( "
                + "[\"" + SQUID_TOTAL_206_238_NAME_S + "\"],"
                + "[%\"" + SQUID_TOTAL_206_238_NAME_S + "\"] / 100 * [\"" + SQUID_TOTAL_206_238_NAME_S + "\"],"
                + "[\"" + SQUID_TOTAL_208_232_NAME_S + "\"],"
                + "[%\"" + SQUID_TOTAL_208_232_NAME_S + "\"] / 100 * [\"" + SQUID_TOTAL_208_232_NAME_S + "\"],"
                + "[\"" + SQUID_TH_U_EQN_NAME_S + "\"], "
                + "[±\"" + SQUID_TH_U_EQN_NAME_S + "\"])", false, true, false);
        samRadiogenicCols.add(expression208corr206Pb238UAge1SigmaErr);

        Expression expression8corr206238 = buildExpression(PB8COR_RM + "206*/238S",
                "ValueModel("
                + "Pb206U238rad( [\"208corr 206Pb/238U Age\"]),"
                + "" + LAMBDA238 + " * ( 1 + "
                + "Pb206U238rad( [\"208corr 206Pb/238U Age\"])"
                + " ) * \n"
                + "[±\"208corr 206Pb/238U Age\"] * 100 / "
                + "Pb206U238rad( [\"208corr 206Pb/238U Age\"]),"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression8corr206238);

        Expression expressionEXP_8CORR_238_206_STAR = buildExpression(EXP_8CORR_238_206_STAR,
                "VALUEMODEL("
                + "1 / [\"" + PB8COR_RM + "206*/238S\"],"
                + "[%\"" + PB8COR_RM + "206*/238S\"],"
                + "false)", false, true, false);
        samRadiogenicCols.add(expressionEXP_8CORR_238_206_STAR);

        Expression expression8corr207235 = buildExpression(PB8COR_RM + "207*/235S",
                "Rad8corPb7U5WithErr( "
                + "[\"" + SQUID_TOTAL_206_238_NAME_S + "\"],"
                + "[%\"" + SQUID_TOTAL_206_238_NAME_S + "\"],"
                + "[\"" + PB8COR_RM + "206*/238S\"],"
                + "[\"" + SQUID_TOTAL_206_238_NAME_S + "\"] * [\"207/206\"] / r238_235s,"
                + "[\"" + SQUID_TH_U_EQN_NAME_S + "\"], "
                + "[%\"" + SQUID_TH_U_EQN_NAME_S + "\"],"
                + "[\"207/206\"],"
                + "[%\"207/206\"],"
                + "[\"208/206\"],"
                + "[%\"208/206\"])", false, true, false);
        samRadiogenicCols.add(expression8corr207235);

        Expression expression8correrrcorr = buildExpression(PB8COR_RM + "errcorrS",
                "Rad8corConcRho( "
                + "[\"" + SQUID_TOTAL_206_238_NAME_S + "\"], "
                + "[%\"" + SQUID_TOTAL_206_238_NAME_S + "\"],"
                + "[\"" + PB8COR_RM + "206*/238S\"],"
                + "[\"" + SQUID_TH_U_EQN_NAME_S + "\"],"
                + "[%\"" + SQUID_TH_U_EQN_NAME_S + "\"],"
                + "[\"207/206\"],"
                + "[%\"207/206\"],"
                + "[\"208/206\"],"
                + "[%\"208/206\"])", false, true, false);
        samRadiogenicCols.add(expression8correrrcorr);

        Expression expression8corr207206 = buildExpression(PB8COR_RM + "207*/206*S",
                "ValueModel("
                + "[\"" + PB8COR_RM + "207*/235S\"] / [\"" + PB8COR_RM + "206*/238S\"] / r238_235s,"
                + "SQRT([%\"" + PB8COR_RM + "207*/235S\"]^2 + [%\"" + PB8COR_RM + "206*/238S\"]^2 -\n"
                + " 2 * [%\"" + PB8COR_RM + "207*/235S\"] * [%\"" + PB8COR_RM + "206*/238S\"] * [\"" + PB8COR_RM + "errcorrS\"] ),"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression8corr207206);

        Expression expression208corr207Pb206PbAge = buildExpression("208corr 207Pb/206PbS Age",
                "AgePb76WithErr( [\"" + PB8COR_RM + "207*/206*S\"], "
                + "([\"" + PB8COR_RM + "207*/206*S\"] * [%\"" + PB8COR_RM + "207*/206*S\"] / 100))", false, true, false);
        samRadiogenicCols.add(expression208corr207Pb206PbAge);

        R68i = "(EXP(" + LAMBDA238 + " * [\"208corr 207Pb/206PbS Age\"] ) - 1)";
        Expression expression208corrDiscordance = buildExpression("208corr Discordance",
                "100 * ( 1 - [\"" + PB8COR_RM + "206*/238S\"] / " + R68i + ")", false, true, false);
        samRadiogenicCols.add(expression208corrDiscordance);

        Expression expressionTotal238U206Pb = buildExpression("Total 238U/206PbS",
                "ValueModel("
                + "1 / [\"" + SQUID_TOTAL_206_238_NAME_S + "\"],"
                + "[%\"" + SQUID_TOTAL_206_238_NAME_S + "\"],"
                + "false)", false, true, false);
        samRadiogenicCols.add(expressionTotal238U206Pb);

        return samRadiogenicCols;
    }

    public static Expression buildExpression(String name, String excelExpression, boolean isRefMatCalc, boolean isSampleCalc, boolean isSummaryCalc) {
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
