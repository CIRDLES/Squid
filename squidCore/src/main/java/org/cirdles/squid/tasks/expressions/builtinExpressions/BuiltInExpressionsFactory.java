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
import static org.cirdles.squid.constants.Squid3Constants.PRESENT_238U235U_DEFAULT;
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
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.ALPHA;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.L1033;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.L859;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA230;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA232;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA234;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA235;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA238;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.OVER_COUNTS_PERSEC_4_8;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.OVER_COUNT_4_6_8;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R206_204B;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207_204B;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207_206B;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R208_204B;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R208_206B;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.AV_PARENT_ELEMENT_CONC_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.BETA;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.BIWT_204_OVR_CTS_FROM_207;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.BIWT_204_OVR_CTS_FROM_208;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.BIWT_4COR_207_206_AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.COM206PB_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.COM206PB_PCT_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.COM208PB_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.COM208PB_PCT_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R208PB206PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R208PB206PB_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFCOM_64;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFCOM_68;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFCOM_74;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFCOM_76;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFCOM_84;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFCOM_86;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.GAMMA;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.NETALPHA;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.NETBETA;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.NETGAMMA;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.OVER_COUNTS_PERSEC_4_7;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.OVER_COUNT_4_6_7;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R206PB_238U;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R206PB_238U_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.RADD6;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.RADD8;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REF_U_CONC_PPM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REF_TH_CONC_PPM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.U_CONCEN_PPM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_CONCEN_PPM_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_CONCEN_PPM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR206PB238U_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR208PB232TH_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TOTAL_206_238_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TOTAL_208_232_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TOTAL_206_238;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TOTAL_207_206;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TOTAL_208_232;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TOTAL_238_206;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR206_238AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR206_238CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR206_238CALIB_CONST_WM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR207_206AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR208_232AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR208_232CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR208_232CALIB_CONST_WM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR206_238AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR206_238CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR206_238CALIB_CONST_WM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR208_232AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR208_232CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR208_232CALIB_CONST_WM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8COR206_238AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8COR206_238CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8COR206_238CALIB_CONST_WM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8COR207_206AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207PB_206PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207PB_206PB_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207PB_235U;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207PB_235U_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.CORR_8_PRIMARY_CALIB_CONST_DELTA_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.ERR_CORREL;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.ERR_CORREL_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PBU_EXT_1_SIGMA_ERR_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.BIWT_7COR_PRIMARY_CALIB_CONST_DELTA_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.BIWT_8COR_PRIMARY_CALIB_CONST_DELTA_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.CONCEN_206PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.CONCEN_208PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.CORR_7_PRIMARY_CALIB_CONST_DELTA_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR206_238AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR207_206AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR208_232AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR_DISCORDANCE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR206_238AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR208_232AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8COR206_238AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8COR207_206AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8COR_DISCORDANCE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R204PB_206PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R208PB_232TH;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R238U_206PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.ASSIGNED_PBU_EXTERNAL_ONE_SIGMA_PCT_ERR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.L09678;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R204206;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207206;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R208206;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PRESENT_238U235U_RM_MODEL_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PRESENT_238U235U;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.U_CONCEN_PPM_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PBTH_EXT_1_SIGMA_ERR_PCT;
import static org.cirdles.squid.tasks.expressions.spots.SpotFieldNode.buildSpotNode;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REFRAD_AGE_U_PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REFRAD_AGE_TH_PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REFRAD_AGE_PB_PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REFRAD_U_PB_RATIO;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REFRAD_TH_PB_RATIO;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REFRAD_7_6;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REFRAD_8_6_FACT;

/**
 *
 * @author James F. Bowring
 */
public abstract class BuiltInExpressionsFactory {

    /**
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

        ExpressionTreeInterface L859exp = new ConstantNode(L859, 0.859);
        parameters.put(L859, L859exp);

        ExpressionTreeInterface L1033exp = new ConstantNode(L1033, 1.033);
        parameters.put(L1033, L1033exp);

        ExpressionTreeInterface extPErr = new ConstantNode(ASSIGNED_PBU_EXTERNAL_ONE_SIGMA_PCT_ERR, 0.75);
        parameters.put(ASSIGNED_PBU_EXTERNAL_ONE_SIGMA_PCT_ERR, extPErr);

        return parameters;
    }

    /**
     *
     * @return Map<String, ExpressionTreeInterface> spotLookupFields
     */
    public static Map<String, ExpressionTreeInterface> generateSpotLookupFields() {
        Map<String, ExpressionTreeInterface> spotLookupFields = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        ExpressionTreeInterface expHours = buildSpotNode("getHours");
        spotLookupFields.put(expHours.getName(), expHours);
        ExpressionTreeInterface expQt1Y = buildSpotNode("getQt1Y");
        spotLookupFields.put(expQt1Y.getName(), expQt1Y);
        ExpressionTreeInterface expQt1Z = buildSpotNode("getQt1Z");
        spotLookupFields.put(expQt1Z.getName(), expQt1Z);
        ExpressionTreeInterface expPrimaryBeam = buildSpotNode("getPrimaryBeam");
        spotLookupFields.put(expPrimaryBeam.getName(), expPrimaryBeam);

        return spotLookupFields;
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
        Expression expressionsComm_64 = buildExpression(DEFCOM_64,
                String.valueOf(commonPbModel.getDatumByName(R206_204B).getValue().doubleValue()), true, true, true);
        expressionsComm_64.setParameterValue(true);
        expressionsComm_64.setNotes(notes);
        parameterValues.add(expressionsComm_64);

        Expression expressionsComm_74 = buildExpression(DEFCOM_74,
                String.valueOf(commonPbModel.getDatumByName(R207_204B).getValue().doubleValue()), true, true, true);
        expressionsComm_74.setParameterValue(true);
        expressionsComm_74.setNotes(notes);
        parameterValues.add(expressionsComm_74);

        Expression expressionsComm_84 = buildExpression(DEFCOM_84,
                String.valueOf(commonPbModel.getDatumByName(R208_204B).getValue().doubleValue()), true, true, true);
        expressionsComm_84.setParameterValue(true);
        expressionsComm_84.setNotes(notes);
        parameterValues.add(expressionsComm_84);

        Expression expressionsComm_76 = buildExpression(DEFCOM_76,
                String.valueOf(commonPbModel.getDatumByName(R207_206B).getValue().doubleValue()), true, true, true);
        expressionsComm_76.setParameterValue(true);
        expressionsComm_76.setNotes(notes);
        parameterValues.add(expressionsComm_76);

        Expression expressionsComm_86 = buildExpression(DEFCOM_86,
                String.valueOf(commonPbModel.getDatumByName(R208_206B).getValue().doubleValue()), true, true, true);
        expressionsComm_86.setParameterValue(true);
        expressionsComm_86.setNotes(notes);
        parameterValues.add(expressionsComm_86);

        Expression expressionsComm_68 = buildExpression(DEFCOM_68,
                "1.0/" + String.valueOf(commonPbModel.getDatumByName(R208_206B).getValue().doubleValue()), true, true, true);
        expressionsComm_68.setParameterValue(true);
        expressionsComm_68.setNotes(notes);
        parameterValues.add(expressionsComm_68);

        return parameterValues;
    }

    public static SortedSet<Expression> updateConcReferenceMaterialValuesFromModel(ReferenceMaterialModel concReferenceMaterialModel) {
        SortedSet<Expression> parameterValues = new TreeSet<>();

        String notes = "from Conc. Reference Material model: " + concReferenceMaterialModel.getModelNameWithVersion();

        Expression expressionStdUConcPpm = buildExpression(REF_U_CONC_PPM,
                String.valueOf(((ReferenceMaterialModel) concReferenceMaterialModel)
                        .getConcentrationByName("concU").getValue().doubleValue()), true, true, true);
        expressionStdUConcPpm.setReferenceMaterialValue(true);
        expressionStdUConcPpm.setNotes(notes);
        parameterValues.add(expressionStdUConcPpm);

        Expression expressionStdThConcPpm = buildExpression(REF_TH_CONC_PPM,
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

        Expression expressionStdAgeUPb = buildExpression(REFRAD_AGE_U_PB,
                String.valueOf(((ReferenceMaterialModel) referenceMaterialModel)
                        .getDateByName(age206_238r.getName()).getValue().doubleValue()), true, true, true);
        expressionStdAgeUPb.setReferenceMaterialValue(true);
        expressionStdAgeUPb.setNotes(notes);
        referenceMaterialValues.add(expressionStdAgeUPb);

        Expression expressionStdAgeThPb = buildExpression(REFRAD_AGE_TH_PB,
                String.valueOf(((ReferenceMaterialModel) referenceMaterialModel)
                        .getDateByName(age208_232r.getName()).getValue().doubleValue()), true, true, true);
        expressionStdAgeThPb.setReferenceMaterialValue(true);
        expressionStdAgeThPb.setNotes(notes);
        referenceMaterialValues.add(expressionStdAgeThPb);

        Expression expressionStdAgePbPb = buildExpression(REFRAD_AGE_PB_PB,
                String.valueOf(((ReferenceMaterialModel) referenceMaterialModel)
                        .getDateByName(age207_206r.getName()).getValue().doubleValue()), true, true, true);
        expressionStdAgePbPb.setReferenceMaterialValue(true);
        expressionStdAgePbPb.setNotes(notes);
        referenceMaterialValues.add(expressionStdAgePbPb);

        double lookup238U235U = ((ReferenceMaterialModel) referenceMaterialModel)
                .getDatumByName(PRESENT_238U235U_RM_MODEL_NAME).getValue().doubleValue();
        if (lookup238U235U == 0.0) {
            /*
            Noah McLean 6 Feb 2019: There should be a place to enter a r238 235s for each ref material. 
            Default for ref materials where there is no r238 235s should be 137.818, which is 
            the global average of zircon measurements.
             */
            lookup238U235U = PRESENT_238U235U_DEFAULT;
        }
        Expression expressionPresent238U235U = buildExpression(PRESENT_238U235U,
                String.valueOf(lookup238U235U), true, true, true);
        expressionPresent238U235U.setReferenceMaterialValue(true);
        expressionPresent238U235U.setNotes(notes);
        referenceMaterialValues.add(expressionPresent238U235U);

        notes = "Calculated from Reference Material model: " + referenceMaterialModel.getModelNameWithVersion();

        Expression expressionStdUPbRatio = buildExpression(REFRAD_U_PB_RATIO,
                "EXP(" + LAMBDA238 + "*" + REFRAD_AGE_U_PB + ")-1", true, true, true);
        expressionStdUPbRatio.setReferenceMaterialValue(true);
        expressionStdUPbRatio.setNotes(notes);
        referenceMaterialValues.add(expressionStdUPbRatio);

        Expression expressionStdThPbRatio = buildExpression(REFRAD_TH_PB_RATIO,
                "EXP(" + LAMBDA232 + "*" + REFRAD_AGE_TH_PB + ")-1", true, true, true);
        expressionStdThPbRatio.setReferenceMaterialValue(true);
        expressionStdThPbRatio.setNotes(notes);
        referenceMaterialValues.add(expressionStdThPbRatio);

        Expression expressionStd_76 = buildExpression(REFRAD_7_6,
                "Pb76(" + REFRAD_AGE_PB_PB + ")", true, true, true);
        expressionStd_76.setReferenceMaterialValue(true);
        expressionStd_76.setNotes(notes);
        referenceMaterialValues.add(expressionStd_76);

        Expression expressionStdRad86fact = buildExpression(REFRAD_8_6_FACT,
                REFRAD_TH_PB_RATIO + "/" + REFRAD_U_PB_RATIO + "", true, true, true);
        expressionStdRad86fact.setReferenceMaterialValue(true);
        expressionStdRad86fact.setNotes(notes);
        referenceMaterialValues.add(expressionStdRad86fact);

        return referenceMaterialValues;
    }

    public static SortedSet<Expression> generatePlaceholderExpressions(String parentNuclide, boolean isDirectAltPD) {
        SortedSet<Expression> placeholderExpressions = new TreeSet<>();

        if (isDirectAltPD) {
            Expression expressionSQUID_TH_U_EQN_NAME = buildExpression(TH_U_EXP_RM,
                    "1", true, false, false);
            placeholderExpressions.add(expressionSQUID_TH_U_EQN_NAME);

            Expression expressionSQUID_TH_U_EQN_NAMEs = buildExpression(TH_U_EXP,
                    "1", false, true, false);
            placeholderExpressions.add(expressionSQUID_TH_U_EQN_NAMEs);

            Expression expressionSQUID_PPM_PARENT_EQN_NAME_U = buildExpression(U_CONCEN_PPM_RM,
                    "1", true, false, false);
            placeholderExpressions.add(expressionSQUID_PPM_PARENT_EQN_NAME_U);

            Expression expressionSQUID_PPM_PARENT_EQN_NAME_TH = buildExpression(TH_CONCEN_PPM_RM,
                    "1", true, false, false);
            placeholderExpressions.add(expressionSQUID_PPM_PARENT_EQN_NAME_TH);

            Expression expressionOVER_COUNT_4_6_8 = buildExpression(OVER_COUNT_4_6_8,
                    "1", true, false, false);
            placeholderExpressions.add(expressionOVER_COUNT_4_6_8);

            Expression expressionOVER_COUNTS_PERSEC_4_8 = buildExpression(OVER_COUNTS_PERSEC_4_8,
                    "1", true, false, false);
            placeholderExpressions.add(expressionOVER_COUNTS_PERSEC_4_8);

            Expression expressionCORR_8_PRIMARY_CALIB_CONST_PCT_DELTA = buildExpression(CORR_8_PRIMARY_CALIB_CONST_DELTA_PCT,
                    "1", true, false, false);
            placeholderExpressions.add(expressionCORR_8_PRIMARY_CALIB_CONST_PCT_DELTA);

        }

        // placeholder expressions
        Expression expressionSQUID_TOTAL_206_238_NAME = buildExpression(TOTAL_206_238_RM,
                "1", false, true, false);
        placeholderExpressions.add(expressionSQUID_TOTAL_206_238_NAME);

        Expression expressionSQUID_TOTAL_206_238_NAME_S = buildExpression(TOTAL_206_238,
                "1", false, true, false);
        placeholderExpressions.add(expressionSQUID_TOTAL_206_238_NAME_S);

        Expression expressionSQUID_TOTAL_208_232_NAME = buildExpression(TOTAL_208_232_RM,
                "1", false, true, false);
        placeholderExpressions.add(expressionSQUID_TOTAL_208_232_NAME);

        Expression expressionSQUID_TOTAL_208_232_NAME_S = buildExpression(TOTAL_208_232,
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
        // As of 7 Feb 2019 we introduce two flavors per Bodorkos - see https://github.com/CIRDLES/Squid/issues/164
        // As of Feb 14, we will have 4cor and 7cor U_concen_RM for directAltPD = true
//        Expression expressionPpmURM = buildExpression(U_CONCEN_PPM_RM,
//                "[\"" + PARENT_ELEMENT_CONC_CONST + "\"]/[\"" + AV_PARENT_ELEMENT_CONC_CONST + "\"]*" + REF_U_CONC_PPM, true, false, false);
//        concentrationExpressionsOrdered.add(expressionPpmURM);
        Expression expressionPpmU = buildExpression(U_CONCEN_PPM,
                "[\"" + PARENT_ELEMENT_CONC_CONST + "\"]/[\"" + AV_PARENT_ELEMENT_CONC_CONST + "\"]*" + REF_U_CONC_PPM, false, true, false);
        concentrationExpressionsOrdered.add(expressionPpmU);

        if (!isDirectAltPD) {
            // Feb 14 2019
            Expression expressionPpmURM = buildExpression(U_CONCEN_PPM_RM,
                    "[\"" + PARENT_ELEMENT_CONC_CONST + "\"]/[\"" + AV_PARENT_ELEMENT_CONC_CONST + "\"]*" + REF_U_CONC_PPM, true, false, false);
            concentrationExpressionsOrdered.add(expressionPpmURM);

            // TODO: promote this and tie to physical constants model
            // handles SecondaryParentPpmFromThU
            //String uConstant = "1.033"; // 1.033 gives perfect fidelity to Squid 2.5 //((238/232) * r238_235s / (r238_235s - 1.0))";
            Expression expressionPpmTh_RM = buildExpression(TH_CONCEN_PPM_RM,
                    "[\"" + U_CONCEN_PPM_RM + "\"]*[\"" + TH_U_EXP_RM + "\"]/" + L1033, true, false, false);
            concentrationExpressionsOrdered.add(expressionPpmTh_RM);

            Expression expressionPpmTh = buildExpression(TH_CONCEN_PPM,
                    "[\"" + U_CONCEN_PPM + "\"]*[\"" + TH_U_EXP + "\"]/" + L1033, false, true, false);
            concentrationExpressionsOrdered.add(expressionPpmTh);

            if (!parentNuclide.contains("232")) {
                // does contain Uranium such as 238
                concentrationExpressionsOrdered.addAll(generate204207MeansAndAgesForRefMaterialsU());
                concentrationExpressionsOrdered.addAll(generate208MeansAndAgesForRefMaterialsU());
            } else {
                concentrationExpressionsOrdered.addAll(generate204207MeansAndAgesForRefMaterialsTh());
            }

        } else {
            // directlAltPD is true

            // Feb 2019 per @sbododorkos
            // for 204b ref mat
            Expression expression4corrPpmURM = buildExpression(PB4CORR + U_CONCEN_PPM_RM,
                    "[\"" + PARENT_ELEMENT_CONC_CONST + "\"]/[\"" + AV_PARENT_ELEMENT_CONC_CONST + "\"]*" + REF_U_CONC_PPM, true, false, false);
            concentrationExpressionsOrdered.add(expression4corrPpmURM);

            // Feb 2019 per @sbododorkos
            // for 207b ref mat
            Expression expression7corrPpmURM = buildExpression(PB7CORR + U_CONCEN_PPM_RM,
                    "[\"" + PARENT_ELEMENT_CONC_CONST + "\"]/[\"" + AV_PARENT_ELEMENT_CONC_CONST + "\"]*" + REF_U_CONC_PPM, true, false, false);
            concentrationExpressionsOrdered.add(expression7corrPpmURM);

            // this code for ppmTh comes from SQ2.50 Procedural Framework: Part 5
            // see: https://github.com/CIRDLES/ET_Redux/wiki/SQ2.50-Procedural-Framework:-Part-5
            concentrationExpressionsOrdered.addAll(generate204207MeansAndAgesForRefMaterialsU());
            concentrationExpressionsOrdered.addAll(generate204207MeansAndAgesForRefMaterialsTh());

            // math for ThUfromA1A2 follows here
            // for 204Pb ref material
            String exp238 = "(EXP(" + LAMBDA238 + "*[\"" + PB4COR206_238AGE_RM + "\"])-1)";
            String exp232 = "(EXP(" + LAMBDA232 + "*[\"" + PB4COR208_232AGE_RM + "\"])-1)";

            Expression expression4corrSQUID_TH_U_EQN_NAME = buildExpression(PB4CORR + TH_U_EXP_RM,
                    "ValueModel("
                    + "[\"" + PB4CORR + R208PB206PB + "\"]*" + exp238 + "/" + exp232 + ","
                    + "SQRT([%\"" + PB4CORR + R208PB206PB + "\"]^2+"
                    + "[%\"" + PB4COR206_238CALIB_CONST + "\"]^2+"
                    + "[%\"" + PB4COR208_232CALIB_CONST + "\"]^2),"
                    + "false)", true, false, false);
            concentrationExpressionsOrdered.add(expression4corrSQUID_TH_U_EQN_NAME);

            Expression expression4corrPpmTh = buildExpression(PB4CORR + TH_CONCEN_PPM_RM,
                    "[\"" + PB4CORR + TH_U_EXP_RM + "\"]*[\"" + U_CONCEN_PPM_RM + "\"]*" + L09678, true, false, false);
            concentrationExpressionsOrdered.add(expression4corrPpmTh);

            // for 207Pb ref material
            exp238 = "(EXP(" + LAMBDA238 + "*[\"" + PB7COR206_238AGE_RM + "\"])-1)";
            exp232 = "(EXP(" + LAMBDA232 + "*[\"" + PB7COR208_232AGE_RM + "\"])-1)";

            Expression expression7corrSQUID_TH_U_EQN_NAME = buildExpression(PB7CORR + TH_U_EXP_RM,
                    "ValueModel([\"" + PB7CORR + R208PB206PB_RM + "\"]*" + exp238 + "/" + exp232 + ","
                    + "SQRT([%\"" + PB7CORR + R208PB206PB_RM + "\"]^2+"
                    + "[%\"" + PB7COR206_238CALIB_CONST + "\"]^2+"
                    + "[%\"" + PB7COR208_232CALIB_CONST + "\"]^2),"
                    + "false)", true, false, false);
            concentrationExpressionsOrdered.add(expression7corrSQUID_TH_U_EQN_NAME);

            Expression expression7corrPpmTh = buildExpression(PB7CORR + TH_CONCEN_PPM_RM,
                    "[\"" + PB7CORR + TH_U_EXP_RM + "\"]*[\"" + U_CONCEN_PPM_RM + "\"]*" + L09678, true, false, false);
            concentrationExpressionsOrdered.add(expression7corrPpmTh);

            // for samples
            Expression expressionPpmTh = buildExpression(TH_CONCEN_PPM,
                    "[\"" + TH_U_EXP + "\"]*[\"" + U_CONCEN_PPM + "\"]*" + L09678, false, true, false);
            concentrationExpressionsOrdered.add(expressionPpmTh);

            Expression expression4corrSQUID_TH_U_EQN_NAMEs = buildExpression(PB4CORR + TH_U_EXP,
                    "ValueModel("
                    + "[" + R208206 + "]*[\"" + PB4CORR + TOTAL_206_238 + "\"]/[\"" + PB4CORR + TOTAL_208_232 + "\"],"
                    + "SQRT([%" + R208206 + "]^2+[%\"" + PB4CORR + TOTAL_206_238 + "\"]^2+"
                    + "[%\"" + PB4CORR + TOTAL_208_232 + "\"]^2),"
                    + "false)", false, true, false);
            concentrationExpressionsOrdered.add(expression4corrSQUID_TH_U_EQN_NAMEs);

            Expression expression7corrSQUID_TH_U_EQN_NAMEs = buildExpression(PB7CORR + TH_U_EXP,
                    "ValueModel("
                    + "[" + R208206 + "]*[\"" + PB7CORR + TOTAL_206_238 + "\"]/[\"" + PB7CORR + TOTAL_208_232 + "\"],"
                    + "SQRT([%" + R208206 + "]^2+[%\"" + PB7CORR + TOTAL_206_238 + "\"]^2+"
                    + "[%\"" + PB7CORR + TOTAL_208_232 + "\"]^2),"
                    + "false)", false, true, false);
            concentrationExpressionsOrdered.add(expression7corrSQUID_TH_U_EQN_NAMEs);

        } // end test of directAltD

        Expression expression4CorrExtPerrU = buildExpression(PB4CORR + PBU_EXT_1_SIGMA_ERR_PCT,
                "Max(ExtPErr,"
                + "[\"" + PB4COR206_238CALIB_CONST_WM + "\"][1]/[\"" + PB4COR206_238CALIB_CONST_WM + "\"][0]*100)", true, false, true);
        Expression expression7CorrExtPerrU = buildExpression(PB7CORR + PBU_EXT_1_SIGMA_ERR_PCT,
                "Max(ExtPErr,"
                + "[\"" + PB7COR206_238CALIB_CONST_WM + "\"][1]/[\"" + PB7COR206_238CALIB_CONST_WM + "\"][0]*100)", true, false, true);
        Expression expression8CorrExtPerrU = buildExpression(PB8CORR + PBU_EXT_1_SIGMA_ERR_PCT,
                "Max(ExtPErr,"
                + "[\"" + PB8COR206_238CALIB_CONST_WM + "\"][1]/[\"" + PB8COR206_238CALIB_CONST_WM + "\"][0]*100)", true, false, true);
        Expression expression4CorrExtPerrT = buildExpression("" + PB4CORR + PBTH_EXT_1_SIGMA_ERR_PCT,
                "Max(ExtPErr,"
                + "[\"" + PB4COR208_232CALIB_CONST_WM + "\"][1]/[\"" + PB4COR208_232CALIB_CONST_WM + "\"][0]*100)", true, false, true);
        Expression expression7CorrExtPerrT = buildExpression("" + PB7CORR + PBTH_EXT_1_SIGMA_ERR_PCT,
                "Max(ExtPErr,"
                + "[\"" + PB7COR208_232CALIB_CONST_WM + "\"][1]/[\"" + PB7COR208_232CALIB_CONST_WM + "\"][0]*100)", true, false, true);

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

        Expression expressionOverCount4_6_7 = buildExpression(OVER_COUNT_4_6_7,
                "ValueModel("
                + "([" + R207206 + "]-" + REFRAD_7_6 + ")/(" + DEFCOM_74 + "-(" + REFRAD_7_6 + "*" + DEFCOM_64 + ")),"
                + "ABS([%" + R207206 + "]*[" + R207206 + "]/([" + R207206 + "]-" + REFRAD_7_6 + ")),"
                + "false)", true, false, false);
        overCountExpressionsOrdered.add(expressionOverCount4_6_7);

        Expression expressionOverCountPerSec4_7 = buildExpression(OVER_COUNTS_PERSEC_4_7,
                "TotalCps([\"204\"])-TotalCps([\"BKG\"])-[\"" + OVER_COUNT_4_6_7 + "\"]*(TotalCps([\"206\"])-TotalCps([\"BKG\"]))", true, false, false);
        overCountExpressionsOrdered.add(expressionOverCountPerSec4_7);

        Expression expressionOverCount7CorrCalib = buildExpression(CORR_7_PRIMARY_CALIB_CONST_DELTA_PCT,
                "100*((1-" + DEFCOM_64 + "*[" + R204206 + "])/(1-" + DEFCOM_64 + "*[\"" + OVER_COUNT_4_6_7 + "\"])-1)", true, false, false);
        overCountExpressionsOrdered.add(expressionOverCount7CorrCalib);

        // new section to accommodate reporting corrections per Bodorkos 13 Aug 2018
        if (!isDirectAltPD) {
            Expression expressionOverCount4_6_8 = buildExpression(OVER_COUNT_4_6_8,
                    "([" + R208206 + "]-" + REFRAD_8_6_FACT + "*[\"" + TH_U_EXP_RM + "\"])/("
                    + DEFCOM_84 + "-" + REFRAD_8_6_FACT + "*[\"" + TH_U_EXP_RM + "\"]*"
                    + DEFCOM_64 + ")", true, false, false);
            overCountExpressionsOrdered.add(expressionOverCount4_6_8);

            Expression expressionOverCountPerSec4_8 = buildExpression(OVER_COUNTS_PERSEC_4_8,
                    "TotalCps([\"204\"])-TotalCps([\"BKG\"])-[\"" + OVER_COUNT_4_6_8 + "\"]*(TotalCps([\"206\"])-TotalCps([\"BKG\"]))", true, false, false);
            overCountExpressionsOrdered.add(expressionOverCountPerSec4_8);

            Expression expressionOverCount8CorrCalib = buildExpression(CORR_8_PRIMARY_CALIB_CONST_DELTA_PCT,
                    "100*((1-" + DEFCOM_64 + "*[" + R204206 + "])/(1-" + DEFCOM_64 + "*[\"" + OVER_COUNT_4_6_8 + "\"])-1) ", true, false, false);
            overCountExpressionsOrdered.add(expressionOverCount8CorrCalib);

        } else {
            // isDirectAltPD true
            Expression expression4CorrOverCount4_6_8 = buildExpression(PB4CORR + OVER_COUNT_4_6_8,
                    "([" + R208206 + "]-" + REFRAD_8_6_FACT + "*[\"" + PB4CORR + TH_U_EXP_RM + "\"])"
                    + "/(" + DEFCOM_84 + "-" + REFRAD_8_6_FACT + "*[\"" + PB4CORR + TH_U_EXP_RM + "\"]*" + DEFCOM_64 + ")", true, false, false);
            overCountExpressionsOrdered.add(expression4CorrOverCount4_6_8);

            Expression expression4CorrOverCountPerSec4_8 = buildExpression(PB4CORR + OVER_COUNTS_PERSEC_4_8,
                    "TotalCps([\"204\"])-TotalCps([\"BKG\"])-[\"" + PB4CORR + OVER_COUNT_4_6_8 + "\"]"
                    + "*(TotalCps([\"206\"])-TotalCps([\"BKG\"]))", true, false, false);
            overCountExpressionsOrdered.add(expression4CorrOverCountPerSec4_8);

            Expression expression4CorrOverCount8CorrCalib = buildExpression("" + PB4CORR + CORR_8_PRIMARY_CALIB_CONST_DELTA_PCT,
                    "100*((1-" + DEFCOM_64 + "*[" + R204206 + "])/(1-" + DEFCOM_64 + "*[\"" + PB4CORR + OVER_COUNT_4_6_8 + "\"])-1) ", true, false, false);
            overCountExpressionsOrdered.add(expression4CorrOverCount8CorrCalib);

            Expression expression7CorrOverCount4_6_8 = buildExpression(PB7CORR + OVER_COUNT_4_6_8,
                    "([" + R208206 + "]-" + REFRAD_8_6_FACT + "*[\"" + PB7CORR + TH_U_EXP_RM + "\"]) "
                    + "/(" + DEFCOM_84 + "-" + REFRAD_8_6_FACT + "*[\"" + PB7CORR + TH_U_EXP_RM + "\"]*" + DEFCOM_64 + ")", true, false, false);
            overCountExpressionsOrdered.add(expression7CorrOverCount4_6_8);

            Expression expression7CorrOverCountPerSec4_8 = buildExpression(PB7CORR + OVER_COUNTS_PERSEC_4_8,
                    "TotalCps([\"204\"])-TotalCps([\"BKG\"])-[\"" + PB7CORR + OVER_COUNT_4_6_8 + "\"]"
                    + "*(TotalCps([\"206\"])-TotalCps([\"BKG\"]))", true, false, false);
            overCountExpressionsOrdered.add(expression7CorrOverCountPerSec4_8);

            Expression expression7CorrOverCount8CorrCalib = buildExpression(PB7CORR + CORR_8_PRIMARY_CALIB_CONST_DELTA_PCT,
                    "100*((1-" + DEFCOM_64 + "*[" + R204206 + "])/(1-" + DEFCOM_64 + "*[\"" + PB7CORR + OVER_COUNT_4_6_8 + "\"])-1) ", true, false, false);
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
        Expression expression7Corr46 = buildExpression(PB7CORR + R204PB_206PB,
                "Pb46cor7("
                + "[" + R207206 + "],"
                + "[\"" + PB7COR206_238AGE + "\"])", false, true, false);
        perSpotPbCorrectionsOrdered.add(expression7Corr46);

        Expression expression8Corr46 = buildExpression(PB8CORR + R204PB_206PB,
                "Pb46cor8("
                + "[" + R208206 + "],"
                + "[\"" + TH_U_EXP + "\"],"
                + "[\"" + PB8COR206_238AGE + "\"])", false, true, false);
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
        Expression expression4corCom206 = buildExpression(PB4CORR + COM206PB_PCT,
                "100*" + DEFCOM_64 + "*[" + R204206 + "]", true, true, false);
        perSpotPbCorrectionsOrdered.add(expression4corCom206);

        Expression expression7corCom206RM = buildExpression(PB7CORR + COM206PB_PCT_RM,
                "100*" + DEFCOM_64 + "*[\"" + OVER_COUNT_4_6_7 + "\"]", true, false, false);
        perSpotPbCorrectionsOrdered.add(expression7corCom206RM);

        Expression expression8corCom206RM = buildExpression(PB8CORR + COM206PB_PCT_RM,
                "100*" + DEFCOM_64 + "*[\"" + OVER_COUNT_4_6_8 + "\"]", true, false, false);
        perSpotPbCorrectionsOrdered.add(expression8corCom206RM);

        Expression expression4corCom208 = buildExpression(PB4CORR + COM208PB_PCT,
                "100*" + DEFCOM_84 + "/[" + R208206 + "]*[" + R204206 + "]", true, true, false);
        perSpotPbCorrectionsOrdered.add(expression4corCom208);

        Expression expression7corCom208RM = buildExpression(PB7CORR + COM208PB_PCT_RM,
                "100*" + DEFCOM_84 + "/[" + R208206 + "]*[\"" + OVER_COUNT_4_6_7 + "\"]", true, false, false);
        perSpotPbCorrectionsOrdered.add(expression7corCom208RM);

        // for samples
        Expression expression7corCom206 = buildExpression(PB7CORR + COM206PB_PCT,
                "100*" + DEFCOM_64 + "*[\"" + PB7CORR + R204PB_206PB + "\"]", false, true, false);
        perSpotPbCorrectionsOrdered.add(expression7corCom206);

        Expression expression8corCom206 = buildExpression(PB8CORR + COM206PB_PCT,
                "100*" + DEFCOM_64 + "*[\"" + PB8CORR + R204PB_206PB + "\"]", false, true, false);
        perSpotPbCorrectionsOrdered.add(expression8corCom206);

        Expression expression7corCom208 = buildExpression(PB7CORR + COM208PB_PCT,
                "100*" + DEFCOM_84 + "/[" + R208206 + "]*[\"" + PB7CORR + R204PB_206PB + "\"]", false, true, false);
        perSpotPbCorrectionsOrdered.add(expression7corCom208);

        // The next step is to calculate all the applicable radiogenic 208Pb/206Pb values. 
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 4-corr 208Pb*/206Pb*  *** Start
        // ref material and sample version
        Expression expression4corr208Pb206Pb = buildExpression(PB4CORR + R208PB206PB,
                "ValueModel("
                + "([" + R208206 + "]/[" + R204206 + "]-" + DEFCOM_84 + ")/(1/[" + R204206 + "]-" + DEFCOM_64 + "),"
                + "100*sqrt((([%" + R208206 + "]/100*[" + R208206 + "])^2+"
                + "(([" + R208206 + "]/[" + R204206 + "]-" + DEFCOM_84 + ")/(1/[" + R204206 + "]-" + DEFCOM_64 + ")"
                + "*" + DEFCOM_64 + "-" + DEFCOM_84 + ")^2*([%" + R204206 + "]/100*[" + R204206 + "])^2)"
                + "/(1-" + DEFCOM_64 + "*[" + R204206 + "])^2)/"
                + "abs(([" + R208206 + "]/[" + R204206 + "]-" + DEFCOM_84 + ")/(1/[" + R204206 + "]-" + DEFCOM_64 + ")),"
                + "false)", true, true, false);
        perSpotPbCorrectionsOrdered.add(expression4corr208Pb206Pb);

        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 4-corr 208Pb*/206Pb*  *** End
        //
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 7-corr 208Pb*/206Pb*  *** Start
        // ref material version
        Expression expression7corr208Pb206PbRM = buildExpression(PB7CORR + R208PB206PB_RM,
                "ValueModel("
                + "([" + R208206 + "]/[\"" + OVER_COUNT_4_6_7 + "\"]-" + DEFCOM_84 + ")/"
                + "(1/[\"" + OVER_COUNT_4_6_7 + "\"]-" + DEFCOM_64 + "),"
                + "StdPb86radCor7per("
                + "[" + R208206 + "],"
                + "[" + R207206 + "],"
                + "([" + R208206 + "]/[\"" + OVER_COUNT_4_6_7 + "\"]-" + DEFCOM_84 + ")/"
                + "(1/[\"" + OVER_COUNT_4_6_7 + "\"]-" + DEFCOM_64 + "),"
                + "[\"" + OVER_COUNT_4_6_7 + "\"]),"
                + "false)", true, false, false);
        perSpotPbCorrectionsOrdered.add(expression7corr208Pb206PbRM);

        // sample version
        Expression expression7corr208Pb206Pb = buildExpression(PB7CORR + R208PB206PB,
                "ValueModel("
                + "([" + R208206 + "]/[\"" + PB7CORR + R204PB_206PB + "\"]-" + DEFCOM_84 + ")/"
                + "(1/[\"" + PB7CORR + R204PB_206PB + "\"]-" + DEFCOM_64 + "),"
                + "Pb86radCor7per("
                + "[" + R208206 + "],"
                + "[" + R207206 + "],"
                + "[\"" + TOTAL_206_238 + "\"],"
                + "[%\"" + TOTAL_206_238 + "\"],"
                + "[\"" + PB7COR206_238AGE + "\"]),"
                + "false)", false, true, false);
        perSpotPbCorrectionsOrdered.add(expression7corr208Pb206Pb);

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
        Expression expression4corr206Pb238Ucalibrconst = buildExpression(PB4COR206_238CALIB_CONST,
                "ValueModel("
                + "(1-[" + R204206 + "]*" + DEFCOM_64 + ")*[\"" + UNCOR206PB238U_CALIB_CONST + "\"],"
                + "sqrt([%\"" + UNCOR206PB238U_CALIB_CONST + "\"]^2+"
                + "(" + DEFCOM_64 + "/(1/[" + R204206 + "]-" + DEFCOM_64 + "))^2*[%" + R204206 + "]^2),"
                + "false)", true, false, false);
        meansAndAgesForRefMaterials.add(expression4corr206Pb238Ucalibrconst);

        // weighted mean
        Expression expression4corr206Pb238UcalibrconstWM = buildExpression(PB4COR206_238CALIB_CONST_WM,
                "WtdMeanACalc([\"" + PB4COR206_238CALIB_CONST + "\"],[%\"" + PB4COR206_238CALIB_CONST + "\"],FALSE,FALSE)", true, false, true);
        meansAndAgesForRefMaterials.add(expression4corr206Pb238UcalibrconstWM);

        // age calc
        Expression expression4corr206Pb238UAge = buildExpression(PB4COR206_238AGE_RM,
                "ValueModel("
                + "LN(1.0+[\"" + PB4COR206_238CALIB_CONST + "\"]/[\"" + PB4COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ")/" + LAMBDA238 + ","
                + "[%\"" + PB4COR206_238CALIB_CONST + "\"]/100*(EXP(" + LAMBDA238 + "*"
                + "LN(1.0+[\"" + PB4COR206_238CALIB_CONST + "\"]/[\"" + PB4COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ")/" + LAMBDA238 + ")-1)"
                + "/" + LAMBDA238 + "/EXP(" + LAMBDA238 + "*"
                + "LN(1.0+[\"" + PB4COR206_238CALIB_CONST + "\"]/[\"" + PB4COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ")/" + LAMBDA238 + "),"
                + "true)", true, false, false);
        meansAndAgesForRefMaterials.add(expression4corr206Pb238UAge);

        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 4-corr  206/238  *** END
        //
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 7-corr  206/238  *** Start
        Expression expression7corr206Pb238Ucalibrconst = buildExpression(PB7COR206_238CALIB_CONST,
                "ValueModel("
                + "(1-[\"" + OVER_COUNT_4_6_7 + "\"]*" + DEFCOM_64 + ")*[\"" + UNCOR206PB238U_CALIB_CONST + "\"],"
                + "sqrt([%\"" + UNCOR206PB238U_CALIB_CONST + "\"]^2+"
                + "(" + DEFCOM_64 + "/(1/[\"" + OVER_COUNT_4_6_7 + "\"]-" + DEFCOM_64 + "))^2*"
                + "[%\"" + OVER_COUNT_4_6_7 + "\"]^2),"
                + "false)", true, false, false);
        meansAndAgesForRefMaterials.add(expression7corr206Pb238Ucalibrconst);

        // weighted mean
        Expression expression7corr206Pb238UcalibrconstWM = buildExpression(PB7COR206_238CALIB_CONST_WM,
                "WtdMeanACalc([\"" + PB7COR206_238CALIB_CONST + "\"],[%\"" + PB7COR206_238CALIB_CONST + "\"],FALSE,FALSE)", true, false, true);
        meansAndAgesForRefMaterials.add(expression7corr206Pb238UcalibrconstWM);

        // age calc
        Expression expression7corr206Pb238UAge = buildExpression(PB7COR206_238AGE_RM,
                "ValueModel("
                + "LN(1.0+[\"" + PB7COR206_238CALIB_CONST + "\"]/[\"" + PB7COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ")/" + LAMBDA238 + ","
                + "[%\"" + PB7COR206_238CALIB_CONST + "\"]/100*(EXP(" + LAMBDA238 + "*"
                + "LN(1.0+[\"" + PB7COR206_238CALIB_CONST + "\"]/[\"" + PB7COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ")/" + LAMBDA238
                + ")-1)"
                + "/" + LAMBDA238 + "/EXP(" + LAMBDA238 + "*"
                + "LN(1.0+[\"" + PB7COR206_238CALIB_CONST + "\"]/[\"" + PB7COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ")/" + LAMBDA238
                + "),"
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
        Expression expression8corr206Pb238Ucalibrconst = buildExpression(PB8COR206_238CALIB_CONST,
                "ValueModel("
                + "(1-[\"" + OVER_COUNT_4_6_8 + "\"]*" + DEFCOM_64 + ")*[\"" + UNCOR206PB238U_CALIB_CONST + "\"],"
                + "SQRT([%\"" + UNCOR206PB238U_CALIB_CONST + "\"]^2+"
                + "(" + DEFCOM_64 + "*[\"" + UNCOR206PB238U_CALIB_CONST + "\"]*[\"" + OVER_COUNT_4_6_8 + "\"]/"
                + "((1-[\"" + OVER_COUNT_4_6_8 + "\"]*" + DEFCOM_64 + ")*[\"" + UNCOR206PB238U_CALIB_CONST + "\"]))^2*"
                + "(([" + R208206 + "]*[%" + R208206 + "]/([" + R208206 + "]-" + REFRAD_8_6_FACT + "*[\"" + TH_U_EXP_RM + "\"]))^2+"
                + "((1/([" + R208206 + "]-" + REFRAD_8_6_FACT + "*[\"" + TH_U_EXP_RM + "\"])+"
                + DEFCOM_64 + "/(" + DEFCOM_84 + "-" + DEFCOM_64 + "*" + REFRAD_8_6_FACT + "*[\"" + TH_U_EXP_RM + "\"]))*"
                + REFRAD_8_6_FACT + "*[\"" + TH_U_EXP_RM + "\"]*[%\"" + TH_U_EXP_RM + "\"]"
                + ")^2"
                + ")"
                + "),false)", true, false, false);
        meansAndAgesForRefMaterials.add(expression8corr206Pb238Ucalibrconst);

        // weighted mean
        Expression expression8corr206Pb238UcalibrconstWM = buildExpression(PB8COR206_238CALIB_CONST_WM,
                "WtdMeanACalc([\"" + PB8COR206_238CALIB_CONST + "\"],[%\"" + PB8COR206_238CALIB_CONST + "\"],FALSE,FALSE)", true, false, true);
        meansAndAgesForRefMaterials.add(expression8corr206Pb238UcalibrconstWM);

        // age calc
        Expression expression8corr206Pb238UAge = buildExpression(PB8COR206_238AGE_RM,
                "ValueModel("
                + "LN(1.0+[\"" + PB8COR206_238CALIB_CONST + "\"]/[\"" + PB8COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ")/" + LAMBDA238 + ","
                + "[%\"" + PB8COR206_238CALIB_CONST + "\"]/100*(EXP(" + LAMBDA238 + "*"
                + "LN(1.0+[\"" + PB8COR206_238CALIB_CONST + "\"]/[\"" + PB8COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ")/" + LAMBDA238
                + ")-1)"
                + "/" + LAMBDA238 + "/EXP(" + LAMBDA238 + "*"
                + "LN(1.0+[\"" + PB8COR206_238CALIB_CONST + "\"]/[\"" + PB8COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ")/" + LAMBDA238
                + "),"
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
        Expression expression4corr208Pb232Thcalibrconst = buildExpression(PB4COR208_232CALIB_CONST,
                "ValueModel("
                + "(1-[" + R204206 + "]/[" + R208206 + "]*" + DEFCOM_84 + ")*[\"" + UNCOR208PB232TH_CALIB_CONST + "\"],"
                + "sqrt([%\"" + UNCOR208PB232TH_CALIB_CONST + "\"]^2+"
                + "(" + DEFCOM_84 + "/([" + R208206 + "]/[" + R204206 + "]-" + DEFCOM_84 + "))^2*[%" + R204206 + "]^2),"
                + "false)", true, false, false);
        meansAndAgesForRefMaterials.add(expression4corr208Pb232Thcalibrconst);

        // weighted mean
        Expression expression4corr208Pb232ThcalibrconstWM = buildExpression(PB4COR208_232CALIB_CONST_WM,
                "WtdMeanACalc([\"" + PB4COR208_232CALIB_CONST + "\"],[%\"" + PB4COR208_232CALIB_CONST + "\"],FALSE,FALSE)", true, false, true);
        meansAndAgesForRefMaterials.add(expression4corr208Pb232ThcalibrconstWM);

        // age calc
        Expression expression4corr208Pb232ThAge = buildExpression(PB4COR208_232AGE_RM,
                "ValueModel("
                + "LN(1.0+[\"" + PB4COR208_232CALIB_CONST + "\"]/[\"" + PB4COR208_232CALIB_CONST_WM + "\"][0]*" + REFRAD_TH_PB_RATIO + ")/" + LAMBDA232 + ","
                + "[%\"" + PB4COR208_232CALIB_CONST + "\"]/100*(EXP(" + LAMBDA232 + "*"
                + "LN(1.0+[\"" + PB4COR208_232CALIB_CONST + "\"]/[\"" + PB4COR208_232CALIB_CONST_WM + "\"][0]*" + REFRAD_TH_PB_RATIO + ")/" + LAMBDA232
                + ")-1)"
                + "/" + LAMBDA232 + "/EXP(" + LAMBDA232 + "*"
                + "LN(1.0+[\"" + PB4COR208_232CALIB_CONST + "\"]/[\"" + PB4COR208_232CALIB_CONST_WM + "\"][0]*" + REFRAD_TH_PB_RATIO + ")/" + LAMBDA232
                + "),"
                + "true)", true, false, false);
        meansAndAgesForRefMaterials.add(expression4corr208Pb232ThAge);

        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 4-corr  208/232  *** END
        //
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 7-corr  208/232  *** Start
        Expression expression7corr208Pb232Thcalibrconst = buildExpression(PB7COR208_232CALIB_CONST,
                "ValueModel("
                + "(1-[\"" + OVER_COUNT_4_6_7 + "\"]/[" + R208206 + "]*" + DEFCOM_84 + ")*[\"" + UNCOR208PB232TH_CALIB_CONST + "\"],"
                + "sqrt([%\"" + UNCOR208PB232TH_CALIB_CONST + "\"]^2+"
                + "(" + DEFCOM_84 + "/([" + R208206 + "]/[\"" + OVER_COUNT_4_6_7 + "\"]-" + DEFCOM_84 + "))^2*"
                + "([%" + R208206 + "]^2+[%\"" + OVER_COUNT_4_6_7 + "\"]^2)),"
                + "false)", true, false, false);
        meansAndAgesForRefMaterials.add(expression7corr208Pb232Thcalibrconst);

        // weighted mean
        Expression expression7corr208Pb232ThcalibrconstWM = buildExpression(PB7COR208_232CALIB_CONST_WM,
                "WtdMeanACalc([\"" + PB7COR208_232CALIB_CONST + "\"],[%\"" + PB7COR208_232CALIB_CONST + "\"],FALSE,FALSE)", true, false, true);
        meansAndAgesForRefMaterials.add(expression7corr208Pb232ThcalibrconstWM);

        // age calc
        Expression expression7corr208Pb232ThAge = buildExpression(PB7COR208_232AGE_RM,
                "ValueModel("
                + "LN(1.0+[\"" + PB7COR208_232CALIB_CONST + "\"]/[\"" + PB7COR208_232CALIB_CONST_WM + "\"][0]*" + REFRAD_TH_PB_RATIO + ")/" + LAMBDA232 + ","
                + "[%\"" + PB7COR208_232CALIB_CONST + "\"]/100*(EXP(" + LAMBDA232 + "*"
                + "LN(1.0+[\"" + PB7COR208_232CALIB_CONST + "\"]/[\"" + PB7COR208_232CALIB_CONST_WM + "\"][0]*" + REFRAD_TH_PB_RATIO + ")/" + LAMBDA232
                + ")-1)"
                + "/" + LAMBDA232 + "/EXP(" + LAMBDA232 + "*"
                + "LN(1.0+[\"" + PB7COR208_232CALIB_CONST + "\"]/[\"" + PB7COR208_232CALIB_CONST_WM + "\"][0]*" + REFRAD_TH_PB_RATIO + ")/" + LAMBDA232
                + "),"
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

        String term1 = "(([" + R207206 + "]*[%" + R207206 + "])^2"
                + "+([" + R204206 + "]*(([" + R207206 + "]/[" + R204206 + "]-" + DEFCOM_74 + ")"
                + "/(1/[" + R204206 + "]-" + DEFCOM_64 + ") * " + DEFCOM_64 + "-" + DEFCOM_74 + ") "
                + "*[%" + R204206 + "])^2)";

        String term2 = "([" + R207206 + "]-[" + R204206 + "]*" + DEFCOM_74 + ")^2";

        Expression expression4corr207Pb206Pb = buildExpression(PB4CORR + R207PB_206PB_RM,
                "ValueModel(([" + R207206 + "]/[" + R204206 + "]-" + DEFCOM_74 + ")/(1/[" + R204206 + "]-" + DEFCOM_64 + "),"
                + "sqrt(" + term1 + "/" + term2 + "),"
                + "false)", true, false, false);
        overCountMeansRefMaterials.add(expression4corr207Pb206Pb);

        Expression expression4corr207Pb206PbAge = buildExpression(PB4COR207_206AGE_RM,
                "AgePb76WithErr([\"" + PB4CORR + R207PB_206PB_RM + "\"],"
                + "([\"" + PB4CORR + R207PB_206PB_RM + "\"]*[%\"" + PB4CORR + R207PB_206PB_RM + "\"]/100))", true, false, false);
        overCountMeansRefMaterials.add(expression4corr207Pb206PbAge);

        // The second part of the subroutine calculates the various biweight means
        Expression expressionPb204OverCts7corr = buildExpression(BIWT_204_OVR_CTS_FROM_207,
                "sqBiweight([\"" + OVER_COUNTS_PERSEC_4_7 + "\"],9)", true, false, true);
        expressionPb204OverCts7corr.setNotes("Robust avg 204 overcts assuming 206Pb/238U-207Pb/235U age concordance");
        overCountMeansRefMaterials.add(expressionPb204OverCts7corr);

        Expression expressionPb204OverCts8corr = buildExpression(BIWT_204_OVR_CTS_FROM_208,
                "sqBiweight([\"" + OVER_COUNTS_PERSEC_4_8 + "\"],9)", true, false, true);
        expressionPb204OverCts8corr.setNotes("Robust avg 204 overcts assuming 206Pb/238U-208Pb/232Th age concordance");
        overCountMeansRefMaterials.add(expressionPb204OverCts8corr);

        Expression expressionOverCtsDeltaP7corr = buildExpression(BIWT_7COR_PRIMARY_CALIB_CONST_DELTA_PCT,
                "sqBiweight([\"" + CORR_7_PRIMARY_CALIB_CONST_DELTA_PCT + "\"],9)", true, false, true);
        expressionOverCtsDeltaP7corr.setNotes("Robust avg of diff. between 207-corr. and 204-corr. calibr. const.");
        overCountMeansRefMaterials.add(expressionOverCtsDeltaP7corr);

        Expression expressionOverCtsDeltaP8corr = buildExpression(BIWT_8COR_PRIMARY_CALIB_CONST_DELTA_PCT,
                "sqBiweight([\"" + CORR_8_PRIMARY_CALIB_CONST_DELTA_PCT + "\"],9)", true, false, true);
        expressionOverCtsDeltaP8corr.setNotes("Robust avg of diff. between 208-corr. and 204-corr. calibr. const.");
        overCountMeansRefMaterials.add(expressionOverCtsDeltaP8corr);

        Expression expressionOverCts4corr207Pb206Pbagecorr = buildExpression(BIWT_4COR_207_206_AGE,
                "sqBiweight([\"" + PB4COR207_206AGE_RM + "\"],9)", true, false, true);
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
            Expression expression4corrTotal206Pb238U = buildExpression(PB4CORR + TOTAL_206_238_RM,
                    "ValueModel("
                    + "[\"" + UNCOR206PB238U_CALIB_CONST + "\"]/[\"" + PB4COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ","
                    + "SQRT([%\"" + UNCOR206PB238U_CALIB_CONST + "\"]^2+[\"" + PB4CORR + PBU_EXT_1_SIGMA_ERR_PCT + "\"]^2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression4corrTotal206Pb238U);

            Expression expression7corrTotal206Pb238U = buildExpression(PB7CORR + TOTAL_206_238_RM,
                    "ValueModel("
                    + "[\"" + UNCOR206PB238U_CALIB_CONST + "\"]/[\"" + PB7COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ","
                    + "SQRT([%\"" + UNCOR206PB238U_CALIB_CONST + "\"]^2+[\"" + PB7CORR + PBU_EXT_1_SIGMA_ERR_PCT + "\"]^2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression7corrTotal206Pb238U);

            // perm1 only
            if (!isDirectAltPD) {
                Expression expression8corrTotal206Pb238U = buildExpression(PB8CORR + TOTAL_206_238_RM,
                        "ValueModel("
                        + "[\"" + UNCOR206PB238U_CALIB_CONST + "\"]/[\"" + PB8COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ","
                        + "SQRT([%\"" + UNCOR206PB238U_CALIB_CONST + "\"]^2+[\"" + PB8CORR + PBU_EXT_1_SIGMA_ERR_PCT + "\"]^2),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression8corrTotal206Pb238U);

                // special case
                Expression expression4corrTotal208Pb232Th = buildExpression(PB4CORR + TOTAL_208_232_RM,
                        "ValueModel("
                        + "[\"" + TOTAL_206_238_RM + "\"]*[" + R208206 + "]/[\"" + TH_U_EXP_RM + "\"],"
                        + "SQRT([%" + R208206 + "]^2+[%\"" + TOTAL_206_238_RM + "\"]^2+[%\"" + TH_U_EXP_RM + "\"]^2),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression4corrTotal208Pb232Th);

                Expression expression7corrTotal208Pb232Th = buildExpression(PB7CORR + TOTAL_208_232_RM,
                        "ValueModel("
                        + "[\"" + TOTAL_206_238_RM + "\"]*[" + R208206 + "]/[\"" + TH_U_EXP_RM + "\"],"
                        + "SQRT([%" + R208206 + "]^2+[%\"" + TOTAL_206_238_RM + "\"]^2+[%\"" + TH_U_EXP_RM + "\"]^2),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression7corrTotal208Pb232Th);

                Expression expression8corrTotal208Pb232Th = buildExpression(PB8CORR + TOTAL_208_232_RM,
                        "ValueModel("
                        + "[\"" + TOTAL_206_238_RM + "\"]*[" + R208206 + "]/[\"" + TH_U_EXP_RM + "\"],"
                        + "SQRT([%" + R208206 + "]^2+[%\"" + TOTAL_206_238_RM + "\"]^2+[%\"" + TH_U_EXP_RM + "\"]^2),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression8corrTotal208Pb232Th);

            } else {
                // perm2 only
                Expression expression4corrTotal208Pb232Th = buildExpression(PB4CORR + TOTAL_208_232_RM,
                        "ValueModel("
                        + "[\"" + UNCOR208PB232TH_CALIB_CONST + "\"]/[\"" + PB4COR208_232CALIB_CONST_WM + "\"][0]*" + REFRAD_TH_PB_RATIO + ","
                        + "SQRT([%\"" + UNCOR208PB232TH_CALIB_CONST + "\"]^2+[\"" + PB4CORR + PBTH_EXT_1_SIGMA_ERR_PCT + "\"]^2),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression4corrTotal208Pb232Th);

                Expression expression7corrTotal208Pb232Th = buildExpression("" + PB7CORR + TOTAL_208_232_RM,
                        "ValueModel("
                        + "[\"" + UNCOR208PB232TH_CALIB_CONST + "\"]/[\"" + PB7COR208_232CALIB_CONST_WM + "\"][0]*" + REFRAD_TH_PB_RATIO + ","
                        + "SQRT([%\"" + UNCOR208PB232TH_CALIB_CONST + "\"]^2+[\"" + PB7CORR + PBTH_EXT_1_SIGMA_ERR_PCT + "\"]^2),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression7corrTotal208Pb232Th);
            }
        } else {
            // perm3 and perm4
            Expression expression4corrTotal208Pb232Th = buildExpression(PB4CORR + TOTAL_208_232_RM,
                    "ValueModel("
                    + "[\"" + UNCOR208PB232TH_CALIB_CONST + "\"]/[\"" + PB4COR208_232CALIB_CONST_WM + "\"][0]*" + REFRAD_TH_PB_RATIO + ","
                    + "SQRT([%\"" + UNCOR208PB232TH_CALIB_CONST + "\"]^2+[\"" + PB4CORR + PBTH_EXT_1_SIGMA_ERR_PCT + "\"]^2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression4corrTotal208Pb232Th);

            Expression expression7corrTotal208Pb232Th = buildExpression(PB7CORR + TOTAL_208_232_RM,
                    "ValueModel("
                    + "[\"" + UNCOR208PB232TH_CALIB_CONST + "\"]/[\"" + PB7COR208_232CALIB_CONST_WM + "\"][0]*" + REFRAD_TH_PB_RATIO + ","
                    + "SQRT([%\"" + UNCOR208PB232TH_CALIB_CONST + "\"]^2+[\"" + PB7CORR + PBTH_EXT_1_SIGMA_ERR_PCT + "\"]^2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression7corrTotal208Pb232Th);

            // perm3 special case - needs confirmation
            if (!isDirectAltPD) {
                Expression expression4corrTotal238U206Pb = buildExpression(PB4CORR + TOTAL_206_238_RM,
                        "ValueModel("
                        + "[\"" + TOTAL_208_232_RM + "\"]/[" + R208206 + "]*[\"" + TH_U_EXP_RM + "\"],"
                        + "SQRT([%" + R208206 + "]^2+[%\"" + TOTAL_208_232_RM + "\"]^2+"
                        + "[%\"" + TH_U_EXP_RM + "\"]^2),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression4corrTotal238U206Pb);

                // repeat the math so the replacement engine works when creating "Total...
                Expression expression7corrTotal206PbS238U = buildExpression(PB7CORR + TOTAL_206_238_RM,
                        "ValueModel("
                        + "[\"" + TOTAL_208_232_RM + "\"]/[" + R208206 + "]*[\"" + TH_U_EXP_RM + "\"],"
                        + "SQRT([%" + R208206 + "]^2+[%\"" + TOTAL_208_232_RM + "\"]^2+"
                        + "[%\"" + TH_U_EXP_RM + "\"]^2),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression7corrTotal206PbS238U);
            }

        }
        // perm4
        if (parentNuclide.contains("232") && isDirectAltPD) {
            Expression expression4corrTotal206Pb238U = buildExpression(PB4CORR + TOTAL_206_238_RM,
                    "ValueModel("
                    + "[\"" + UNCOR206PB238U_CALIB_CONST + "\"]/[\"" + PB4COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ","
                    + "SQRT([%\"" + UNCOR206PB238U_CALIB_CONST + "\"]^2 + [\"" + PB4CORR + PBU_EXT_1_SIGMA_ERR_PCT + "\"] ^ 2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression4corrTotal206Pb238U);

            Expression expression7corrTotal206Pb238U = buildExpression(PB7CORR + TOTAL_206_238_RM,
                    "ValueModel("
                    + "[\"" + UNCOR206PB238U_CALIB_CONST + "\"]/[\"" + PB7COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ","
                    + "SQRT([%\"" + UNCOR206PB238U_CALIB_CONST + "\"]^2+[\"" + PB7CORR + PBU_EXT_1_SIGMA_ERR_PCT + "\"]^2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression7corrTotal206Pb238U);

        }

        if (parentNuclide.contains("232") && !isDirectAltPD) {
            // perm3 only
            Expression expression4corr206238 = buildExpression(PB4CORR + R206PB_238U_RM,
                    "ValueModel("
                    + "[\"" + TOTAL_206_238_RM + "\"]*(1-" + DEFCOM_64 + "*[" + R204206 + "]),"
                    + "SQRT([%\"" + TOTAL_206_238_RM + "\"]^2+"
                    + "(" + DEFCOM_64 + "*[%" + R204206 + "]/(1/[" + R204206 + "]-" + DEFCOM_64 + "))^2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression4corr206238);

            Expression expression4corr207Pb235U = buildExpression("" + PB4CORR + R207PB_235U_RM,
                    "ValueModel("
                    + "[\"" + PB4CORR + R207PB_206PB_RM + "\"]*[\"" + PB4CORR + R206PB_238U_RM + "\"]*" + PRESENT_238U235U + ","
                    + "sqrt([%\"" + PB4CORR + R206PB_238U_RM + "\"]^2+[%\"" + PB4CORR + R207PB_206PB_RM + "\"]^2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression4corr207Pb235U);

            Expression expression4correrrcorr = buildExpression(PB4CORR + ERR_CORREL_RM,
                    "[%\"" + PB4CORR + R206PB_238U_RM + "\"]/[%\"" + PB4CORR + R207PB_235U_RM + "\"]", true, false, false);
            stdRadiogenicCols.add(expression4correrrcorr);

            Expression expression7corr206Pb238UAge = buildExpression(PB7COR206_238AGE_RM,
                    "Age7corrWithErr("
                    + "[\"" + TOTAL_206_238_RM + "\"],"
                    + "[±\"" + TOTAL_206_238_RM + "\"],"
                    + "[" + R207206 + "],"
                    + "[±" + R207206 + "])",
                    true, false, false);
            stdRadiogenicCols.add(expression7corr206Pb238UAge);

            Expression expression7corr206238 = buildExpression(PB7CORR + R206PB_238U_RM,
                    "ValueModel("
                    + "EXP(" + LAMBDA238 + "*[\"" + PB7COR206_238AGE_RM + "\"])-1,"
                    + LAMBDA238 + "*EXP(" + LAMBDA238 + "*[\"" + PB7COR206_238AGE_RM + "\"])*"
                    + "[±\"" + PB7COR206_238AGE_RM + "\"]/"
                    + "(EXP(" + LAMBDA238 + "*[\"" + PB7COR206_238AGE_RM + "\"])-1)"
                    + "*100 ,"
                    + "false)",
                    true, false, false);
            stdRadiogenicCols.add(expression7corr206238);

        } else {
            // perm 1,2,4

            Expression expression4corr206Pb238U = buildExpression(PB4CORR + R206PB_238U_RM,
                    "ValueModel("
                    + "[\"" + PB4COR206_238CALIB_CONST + "\"]/[\"" + PB4COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ","
                    + "[%\"" + PB4COR206_238CALIB_CONST + "\"],"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression4corr206Pb238U);

            Expression expression4corr207Pb235U = buildExpression(PB4CORR + R207PB_235U_RM,
                    "ValueModel("
                    + "[\"" + PB4CORR + R207PB_206PB_RM + "\"]*[\"" + PB4CORR + R206PB_238U_RM + "\"]*" + PRESENT_238U235U + ","
                    + "sqrt([%\"" + PB4CORR + R206PB_238U_RM + "\"]^2+[%\"" + PB4CORR + R207PB_206PB_RM + "\"]^2),"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression4corr207Pb235U);

            Expression expression4correrrcorr = buildExpression(PB4CORR + ERR_CORREL_RM,
                    "[%\"" + PB4CORR + R206PB_238U_RM + "\"]/[%\"" + PB4CORR + R207PB_235U_RM + "\"]", true, false, false);
            stdRadiogenicCols.add(expression4correrrcorr);

            Expression expression7corr206238 = buildExpression(PB7CORR + R206PB_238U_RM,
                    "ValueModel("
                    + "[\"" + PB7COR206_238CALIB_CONST + "\"]/[\"" + PB7COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ","
                    + "[%\"" + PB7COR206_238CALIB_CONST + "\"],"
                    + "false)", true, false, false);
            stdRadiogenicCols.add(expression7corr206238);

            // perm1 only
            if (!parentNuclide.contains("232") && !isDirectAltPD) {
                Expression expression8corr206238 = buildExpression(PB8CORR + R206PB_238U_RM,
                        "ValueModel("
                        + "[\"" + PB8COR206_238CALIB_CONST + "\"]/[\"" + PB8COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ","
                        + "[%\"" + PB8COR206_238CALIB_CONST + "\"],"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression8corr206238);

                Expression expression8corr207235 = buildExpression(PB8CORR + R207PB_235U_RM,
                        "Rad8corPb7U5WithErr("
                        + "[\"" + TOTAL_206_238_RM + "\"],"
                        + "[%\"" + TOTAL_206_238_RM + "\"],"
                        + "[\"" + PB8CORR + R206PB_238U_RM + "\"],"
                        + "[\"" + TOTAL_206_238_RM + "\"]*[" + R207206 + "]/" + PRESENT_238U235U + ","
                        + "[\"" + TH_U_EXP_RM + "\"],"
                        + "[%\"" + TH_U_EXP_RM + "\"],"
                        + "[" + R207206 + "],"
                        + "[%" + R207206 + "],"
                        + "[" + R208206 + "],"
                        + "[%" + R208206 + "])", true, false, false);
                stdRadiogenicCols.add(expression8corr207235);

                Expression expression8correrrcorr = buildExpression(PB8CORR + ERR_CORREL_RM,
                        "Rad8corConcRho("
                        + "[\"" + TOTAL_206_238_RM + "\"],"
                        + "[%\"" + TOTAL_206_238_RM + "\"],"
                        + "[\"" + PB8CORR + R206PB_238U_RM + "\"],"
                        + "[\"" + TH_U_EXP_RM + "\"],"
                        + "[%\"" + TH_U_EXP_RM + "\"],"
                        + "[" + R207206 + "],"
                        + "[%" + R207206 + "],"
                        + "[" + R208206 + "],"
                        + "[%" + R208206 + "])", true, false, false);
                stdRadiogenicCols.add(expression8correrrcorr);

                Expression expression8corr207206 = buildExpression(PB8CORR + R207PB_206PB_RM,
                        "ValueModel("
                        + "[\"" + PB8CORR + R207PB_235U_RM + "\"]/[\"" + PB8CORR + R206PB_238U_RM + "\"]/" + PRESENT_238U235U + ","
                        + "SQRT([%\"" + PB8CORR + R207PB_235U_RM + "\"]^2+[%\"" + PB8CORR + R206PB_238U_RM + "\"]^2-"
                        + "2*[%\"" + PB8CORR + R207PB_235U_RM + "\"]*[%\"" + PB8CORR + R206PB_238U_RM + "\"]*[\"" + PB8CORR + ERR_CORREL_RM + "\"]),"
                        + "false)", true, false, false);
                stdRadiogenicCols.add(expression8corr207206);

                Expression expression208corr207Pb206PbAge = buildExpression(PB8COR207_206AGE_RM,
                        "AgePb76WithErr([\"" + PB8CORR + R207PB_206PB_RM + "\"],"
                        + "([\"" + PB8CORR + R207PB_206PB_RM + "\"]*[%\"" + PB8CORR + R207PB_206PB_RM + "\"]/100))", true, false, false);
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

        Expression expressionAlpha = buildExpression(ALPHA,
                "1/[" + R204206 + "]", false, true, false);
        samRadiogenicCols.add(expressionAlpha);

        Expression expressionNetAlpha = buildExpression(NETALPHA,
                ALPHA + "-" + DEFCOM_64, false, true, false);
        samRadiogenicCols.add(expressionNetAlpha);

        Expression expressionBeta = buildExpression(BETA,
                "[" + R207206 + "]/[" + R204206 + "]", false, true, false);
        samRadiogenicCols.add(expressionBeta);

        Expression expressionNetBeta = buildExpression(NETBETA,
                BETA + "-" + DEFCOM_74, false, true, false);
        samRadiogenicCols.add(expressionNetBeta);

        Expression expressionGamma = buildExpression(GAMMA,
                "[" + R208206 + "]/[" + R204206 + "]", false, true, false);
        samRadiogenicCols.add(expressionGamma);

        Expression expressionNetGamma = buildExpression(NETGAMMA,
                GAMMA + "-" + DEFCOM_84, false, true, false);
        samRadiogenicCols.add(expressionNetGamma);

        Expression expressionRadd6 = buildExpression(RADD6,
                NETALPHA + "/" + ALPHA, false, true, false);
        samRadiogenicCols.add(expressionRadd6);

        Expression expressionRadd8 = buildExpression(RADD8,
                NETGAMMA + "/" + GAMMA, false, true, false);
        samRadiogenicCols.add(expressionRadd8);

        // Case Perm1 and Perm2, i.e. has uranium as parent nuclide ***************************
        if (!parentNuclide.contains("232")) {
            // this is same as for RM above, so add "S" for Sample
            // see email from Bodorkos 20 July 2018 [\"UncorrPb/Uconst\"]
            Expression expression4corrTotal206Pb238US = buildExpression(PB4CORR + TOTAL_206_238,
                    "ValueModel("
                    + "[\"" + UNCOR206PB238U_CALIB_CONST + "\"]/[\"" + PB4COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ","
                    + "SQRT([%\"" + UNCOR206PB238U_CALIB_CONST + "\"]^2+[\"" + PB4CORR + PBU_EXT_1_SIGMA_ERR_PCT + "\"]^2),"
                    + "false)", false, true, false);
            samRadiogenicCols.add(expression4corrTotal206Pb238US);

            // this is same as for RM above, so add "S" for Sample
            Expression expression7corrTotal206Pb238US = buildExpression(PB7CORR + TOTAL_206_238,
                    "ValueModel("
                    + "[\"" + UNCOR206PB238U_CALIB_CONST + "\"]/[\"" + PB7COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ","
                    + "SQRT([%\"" + UNCOR206PB238U_CALIB_CONST + "\"]^2+[\"" + PB7CORR + PBU_EXT_1_SIGMA_ERR_PCT + "\"]^2),"
                    + "false)", false, true, false);
            samRadiogenicCols.add(expression7corrTotal206Pb238US);

            // perm1 only
            if (!isDirectAltPD) {
                // this is same as for RM above, so add "S" for Sample
                Expression expression8corrTotal206Pb238US = buildExpression(PB8CORR + TOTAL_206_238,
                        "ValueModel("
                        + "[\"" + UNCOR206PB238U_CALIB_CONST + "\"]/[\"" + PB8COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ","
                        + "SQRT([%\"" + UNCOR206PB238U_CALIB_CONST + "\"]^2+[\"" + PB8CORR + PBU_EXT_1_SIGMA_ERR_PCT + "\"]^2),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression8corrTotal206Pb238US);

                // special case
                Expression expression4corrTotal208Pb232ThS = buildExpression(PB4CORR + TOTAL_208_232,
                        "ValueModel("
                        + "[\"" + TOTAL_206_238 + "\"]*[" + R208206 + "]/[\"" + TH_U_EXP + "\"],"
                        + "SQRT([%" + R208206 + "]^2+[%\"" + TOTAL_206_238 + "\"]^2+[%\"" + TH_U_EXP + "\"]^2),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression4corrTotal208Pb232ThS);

                Expression expression7corrTotal208Pb232ThS = buildExpression(PB7CORR + TOTAL_208_232,
                        "ValueModel("
                        + "[\"" + TOTAL_206_238 + "\"]*[" + R208206 + "]/[\"" + TH_U_EXP + "\"],"
                        + "SQRT([%" + R208206 + "]^2+[%\"" + TOTAL_206_238 + "\"]^2+[%\"" + TH_U_EXP + "\"]^2),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression7corrTotal208Pb232ThS);

                Expression expression8corrTotal208Pb232ThS = buildExpression(PB8CORR + TOTAL_208_232,
                        "ValueModel("
                        + "[\"" + TOTAL_206_238 + "\"]*[" + R208206 + "]/[\"" + TH_U_EXP + "\"],"
                        + "SQRT([%" + R208206 + "]^2+[%\"" + TOTAL_206_238 + "\"]^2+[%\"" + TH_U_EXP + "\"]^2),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression8corrTotal208Pb232ThS);
            } else {
                // perm2
                Expression expression4corrTotal208Pb232ThS = buildExpression(PB4CORR + TOTAL_208_232,
                        "ValueModel("
                        + "[\"" + UNCOR208PB232TH_CALIB_CONST + "\"]/[\"" + PB4COR208_232CALIB_CONST_WM + "\"][0]*" + REFRAD_TH_PB_RATIO + ","
                        + "SQRT([%\"" + UNCOR208PB232TH_CALIB_CONST + "\"]^2+[\"" + PB4CORR + PBTH_EXT_1_SIGMA_ERR_PCT + "\"]^2),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression4corrTotal208Pb232ThS);

                Expression expression7corrTotal208Pb232ThS = buildExpression(PB7CORR + TOTAL_208_232,
                        "ValueModel("
                        + "[\"" + UNCOR208PB232TH_CALIB_CONST + "\"]/[\"" + PB7COR208_232CALIB_CONST_WM + "\"][0]*" + REFRAD_TH_PB_RATIO + ","
                        + "SQRT([%\"" + UNCOR208PB232TH_CALIB_CONST + "\"]^2+[\"" + PB7CORR + PBTH_EXT_1_SIGMA_ERR_PCT + "\"]^2),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression7corrTotal208Pb232ThS);
            }
        } else {
            // perm3 and perm4
            Expression expression4corrTotal208Pb232ThS = buildExpression(PB4CORR + TOTAL_208_232,
                    "ValueModel("
                    + "[\"" + UNCOR208PB232TH_CALIB_CONST + "\"]/[\"" + PB4COR208_232CALIB_CONST_WM + "\"][0]*" + REFRAD_TH_PB_RATIO + ","
                    + "SQRT([%\"" + UNCOR208PB232TH_CALIB_CONST + "\"]^2+[\"" + PB4CORR + PBTH_EXT_1_SIGMA_ERR_PCT + "\"]^2),"
                    + "false)", false, true, false);
            samRadiogenicCols.add(expression4corrTotal208Pb232ThS);

            Expression expression7corrTotal208Pb232ThS = buildExpression(PB7CORR + TOTAL_208_232,
                    "ValueModel("
                    + "[\"" + UNCOR208PB232TH_CALIB_CONST + "\"]/[\"" + PB7COR208_232CALIB_CONST_WM + "\"][0]*" + REFRAD_TH_PB_RATIO + ","
                    + "SQRT([%\"" + UNCOR208PB232TH_CALIB_CONST + "\"]^2+[\"" + PB7CORR + PBTH_EXT_1_SIGMA_ERR_PCT + "\"]^2),"
                    + "false)", false, true, false);
            samRadiogenicCols.add(expression7corrTotal208Pb232ThS);

            // perm3 special case
            if (!isDirectAltPD) {
                Expression expression4corrTotal238U206PbS = buildExpression(PB4CORR + TOTAL_206_238,
                        "ValueModel("
                        + "[\"" + TOTAL_208_232 + "\"]/[" + R208206 + "]*[\"" + TH_U_EXP + "\"],"
                        + "SQRT([%" + R208206 + "]^2+[%\"" + TOTAL_208_232 + "\"]^2+"
                        + "[%\"" + TH_U_EXP + "\"]^2),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression4corrTotal238U206PbS);

                // repreat the math so the replacement engine works when creating "Total...
                Expression expression7corrTotal206PbS238U = buildExpression(PB7CORR + TOTAL_206_238,
                        "ValueModel("
                        + "[\"" + TOTAL_208_232 + "\"]/[" + R208206 + "]*[\"" + TH_U_EXP + "\"],"
                        + "SQRT([%" + R208206 + "]^2+[%\"" + TOTAL_208_232 + "\"]^2+"
                        + "[%\"" + TH_U_EXP + "\"]^2),"
                        + "false)", false, true, false);
                samRadiogenicCols.add(expression7corrTotal206PbS238U);
            }
        }
        // perm4
        if (parentNuclide.contains("232") && isDirectAltPD) {
            // this is same as for RM above, so add "S" for Sample
            Expression expression4corrTotal206Pb238US = buildExpression(PB4CORR + TOTAL_206_238,
                    "ValueModel("
                    + "[\"" + UNCOR206PB238U_CALIB_CONST + "\"]/[\"" + PB4COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ","
                    + "SQRT([%\"" + UNCOR206PB238U_CALIB_CONST + "\"]^2+[\"" + PB4CORR + PBU_EXT_1_SIGMA_ERR_PCT + "\"]^2),"
                    + "false)", false, true, false);
            samRadiogenicCols.add(expression4corrTotal206Pb238US);

            // this is same as for RM above, so add "S" for Sample
            Expression expression7corrTotal206Pb238US = buildExpression(PB7CORR + TOTAL_206_238,
                    "ValueModel("
                    + "[\"" + UNCOR206PB238U_CALIB_CONST + "\"]/[\"" + PB7COR206_238CALIB_CONST_WM + "\"][0]*" + REFRAD_U_PB_RATIO + ","
                    + "SQRT([%\"" + UNCOR206PB238U_CALIB_CONST + "\"]^2+[\"" + PB7CORR + PBU_EXT_1_SIGMA_ERR_PCT + "\"]^2),"
                    + "false)", false, true, false);
            samRadiogenicCols.add(expression7corrTotal206Pb238US);
        }

        // ppm values of radiogenic Pb
        Expression expression4corrPPM206 = buildExpression(PB4CORR + CONCEN_206PB,
                "[\"" + TOTAL_206_238 + "\"]*[\"" + U_CONCEN_PPM + "\"]*" + L859 + "*"
                + "(1-[" + R204206 + "]*" + DEFCOM_64 + ")", false, true, false);
        samRadiogenicCols.add(expression4corrPPM206);

        Expression expression7corrPPM206 = buildExpression(PB7CORR + CONCEN_206PB,
                "[\"" + TOTAL_206_238 + "\"]*[\"" + U_CONCEN_PPM + "\"]*" + L859 + "*"
                + " (1-[\"" + PB7CORR + R204PB_206PB + "\"]*" + DEFCOM_64 + ")", false, true, false);
        samRadiogenicCols.add(expression7corrPPM206);

        Expression expression8corrPPM206 = buildExpression(PB8CORR + CONCEN_206PB,
                "[\"" + TOTAL_206_238 + "\"]*[\"" + U_CONCEN_PPM + "\"]*" + L859 + "*"
                + "(1-[\"" + PB8CORR + R204PB_206PB + "\"]*" + DEFCOM_64 + ")", false, true, false);
        samRadiogenicCols.add(expression8corrPPM206);

        Expression expression4corrPPM208 = buildExpression(PB4CORR + CONCEN_208PB,
                "[\"" + PB4CORR + CONCEN_206PB + "\"]*[\"" + PB4CORR + R208PB206PB + "\"]*208/206", false, true, false);
        samRadiogenicCols.add(expression4corrPPM208);

        Expression expression7corrPPM208 = buildExpression(PB7CORR + CONCEN_208PB,
                "[\"" + PB7CORR + CONCEN_206PB + "\"]*[\"" + PB7CORR + R208PB206PB + "\"]*208/206", false, true, false);
        samRadiogenicCols.add(expression7corrPPM208);

        Expression expression4corr206238 = buildExpression(PB4CORR + R206PB_238U,
                "ValueModel("
                + "[\"" + TOTAL_206_238 + "\"]*" + RADD6 + ","
                + "SQRT([%\"" + TOTAL_206_238 + "\"]^2+(" + DEFCOM_64 + "*"
                + "[%" + R204206 + "]/(1/[" + R204206 + "]-" + DEFCOM_64 + "))^2),"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression4corr206238);

        Expression expression4corr238206 = buildExpression(PB4CORR + R238U_206PB,
                "ValueModel("
                + "1/[\"" + PB4CORR + R206PB_238U + "\"],"
                + "[%\"" + PB4CORR + R206PB_238U + "\"],"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression4corr238206);

        //some ages
        String d1 = "(" + NETALPHA + "*[%\"" + TOTAL_206_238 + "\"]/100)^2";
        String d3 = "([%" + R204206 + "]*" + DEFCOM_64 + "/100)^2";
        String d4 = "(([\"" + TOTAL_206_238 + "\"]*[" + R204206 + "])^2)";
        String d5 = "((1/" + LAMBDA238 + "/"
                + "EXP(" + LAMBDA238 + "*(LN(1+[\"" + PB4CORR + R206PB_238U + "\"])/" + LAMBDA238 + ")))^2)";
        Expression expression204corr206Pb238UAge = buildExpression(PB4COR206_238AGE,
                "ValueModel("
                + "(LN(1+[\"" + PB4CORR + R206PB_238U + "\"])/" + LAMBDA238 + "),"
                + "SQRT(" + d5 + "*" + d4 + "*(" + d1 + "+" + d3 + ")),"
                + "true)", false, true, false);
        samRadiogenicCols.add(expression204corr206Pb238UAge);

        /**
         * The next step is to perform the superset of calculations enabled by
         * the measurement of ["207/206"]. These start with 4corr 207
         */
        Expression expressionTotal207Pb206PbS = buildExpression(TOTAL_207_206,
                "[" + R207206 + "]", false, true, false);
        samRadiogenicCols.add(expressionTotal207Pb206PbS);

        String t1 = "(([" + R207206 + "]-ABS(" + NETBETA + "/" + NETALPHA + "))*[%" + R204206 + "]"
                + "/100/[" + R204206 + "])^2";
        String t3 = "([%" + R207206 + "]/[" + R204206 + "]/100*[" + R207206 + "])^2";

        Expression expression4corr207206 = buildExpression(PB4CORR + R207PB_206PB,
                "ValueModel("
                + "ABS(" + NETBETA + "/" + NETALPHA + "),"
                + "ABS(SQRT(" + t1 + " + " + t3 + ")/" + NETALPHA + "*100/ABS(" + NETBETA + "/" + NETALPHA + ")),"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression4corr207206);

        Expression expression204corr207P206PbAge = buildExpression(PB4COR207_206AGE,
                "AgePb76WithErr([\"" + PB4CORR + R207PB_206PB + "\"],"
                + "([\"" + PB4CORR + R207PB_206PB + "\"]*[%\"" + PB4CORR + R207PB_206PB + "\"]/100))", false, true, false);
        samRadiogenicCols.add(expression204corr207P206PbAge);

        // QUESTIONS HERE ABOUT LOGIC
        Expression expression4corr207235 = buildExpression(PB4CORR + R207PB_235U,
                "ValueModel("
                + "([\"" + PB4CORR + R207PB_206PB + "\"]*[\"" + PB4CORR + R206PB_238U + "\"]*" + PRESENT_238U235U + "),"
                + "SQRT([%\"" + PB4CORR + R207PB_206PB + "\"]^2+"
                + "[%\"" + PB4CORR + R206PB_238U + "\"]^2),"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression4corr207235);

        Expression expression4corrErrCorr = buildExpression(PB4CORR + ERR_CORREL,
                "[%\"" + PB4CORR + R206PB_238U + "\"]/[%\"" + PB4CORR + R207PB_235U + "\"]", false, true, false);
        samRadiogenicCols.add(expression4corrErrCorr);

        String R68i = "(EXP(" + LAMBDA238 + "*[\"" + PB4COR207_206AGE + "\"])-1)";
        Expression expression204corrDiscordance = buildExpression(PB4COR_DISCORDANCE,
                "100*(1-[\"" + PB4CORR + R206PB_238U + "\"]/" + R68i + ")", false, true, false);
        samRadiogenicCols.add(expression204corrDiscordance);

        Expression expression207corr206Pb238UAgeWithErr = buildExpression(PB7COR206_238AGE,
                "Age7corrWithErr("
                + "[\"" + TOTAL_206_238 + "\"],"
                + "[%\"" + TOTAL_206_238 + "\"]/100*[\"" + TOTAL_206_238 + "\"],"
                + "[\"" + TOTAL_207_206 + "\"],"
                + "[±\"" + TOTAL_207_206 + "\"])",
                false, true, false);
        samRadiogenicCols.add(expression207corr206Pb238UAgeWithErr);

        Expression expression4corr208232 = buildExpression(PB4CORR + R208PB_232TH,
                "ValueModel("
                + "[\"" + TOTAL_208_232 + "\"]*" + RADD8 + ","
                + "SQRT([%\"" + TOTAL_208_232 + "\"]^2+"
                + "(" + DEFCOM_84 + "/" + NETGAMMA + ")^2*[%" + R204206 + "]^2),"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression4corr208232);

        Expression expression204corr208Pb232ThAge = buildExpression(PB4COR208_232AGE,
                "ValueModel("
                + "(LN(1+[\"" + PB4CORR + R208PB_232TH + "\"])/" + LAMBDA232 + "),"
                + "([\"" + PB4CORR + R208PB_232TH + "\"]/" + LAMBDA232 + "/"
                + "(1+[\"" + PB4CORR + R208PB_232TH + "\"])*[%\"" + PB4CORR + R208PB_232TH + "\"]/100),"
                + "true)", false, true, false);
        samRadiogenicCols.add(expression204corr208Pb232ThAge);

        Expression expression7corr206238 = buildExpression(PB7CORR + R206PB_238U,
                "ValueModel("
                + "EXP(" + LAMBDA238 + "*[\"" + PB7COR206_238AGE + "\"])-1,"
                + LAMBDA238 + "*EXP(" + LAMBDA238 + "*[\"" + PB7COR206_238AGE + "\"])*"
                + "[±\"" + PB7COR206_238AGE + "\"]/"
                + "(EXP(" + LAMBDA238 + "*[\"" + PB7COR206_238AGE + "\"])-1)*100,"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression7corr206238);

        Expression expression207corr208Pb232ThAge = buildExpression(PB7COR208_232AGE,
                "Age7CorrPb8Th2WithErr("
                + "[\"" + TOTAL_206_238 + "\"],"
                + "[%\"" + TOTAL_206_238 + "\"],"
                + "[\"" + TOTAL_208_232 + "\"],"
                + "[%\"" + TOTAL_208_232 + "\"],"
                + "[" + R208206 + "],"
                + "[%" + R208206 + "],"
                + "[" + R207206 + "],"
                + "[%" + R207206 + "])", false, true, false);
        samRadiogenicCols.add(expression207corr208Pb232ThAge);

        Expression expression7corr208232 = buildExpression(PB7CORR + R208PB_232TH,
                "ValueModel("
                + "EXP(" + LAMBDA232 + "*[\"" + PB7COR208_232AGE + "\"])-1,"
                + LAMBDA232 + "*EXP(" + LAMBDA232 + "*"
                + "[\"" + PB7COR208_232AGE + "\"])*"
                + "[±\"" + PB7COR208_232AGE + "\"]/"
                + "(EXP(" + LAMBDA232 + "*[\"" + PB7COR208_232AGE + "\"])-1)*100,"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression7corr208232);

        Expression expression208corr206Pb238UAge1SigmaErr = buildExpression(PB8COR206_238AGE,
                "Age8corrWithErr("
                + "[\"" + TOTAL_206_238 + "\"],"
                + "[%\"" + TOTAL_206_238 + "\"]/100*[\"" + TOTAL_206_238 + "\"],"
                + "[\"" + TOTAL_208_232 + "\"],"
                + "[%\"" + TOTAL_208_232 + "\"]/100*[\"" + TOTAL_208_232 + "\"],"
                + "[\"" + TH_U_EXP + "\"],"
                + "[±\"" + TH_U_EXP + "\"])", false, true, false);
        samRadiogenicCols.add(expression208corr206Pb238UAge1SigmaErr);

        Expression expression8corr206238 = buildExpression(PB8CORR + R206PB_238U,
                "ValueModel("
                + "Pb206U238rad([\"" + PB8COR206_238AGE + "\"]),"
                + LAMBDA238 + "*(1+"
                + "Pb206U238rad([\"" + PB8COR206_238AGE + "\"])"
                + ")*"
                + "[±\"" + PB8COR206_238AGE + "\"]*100/"
                + "Pb206U238rad([\"" + PB8COR206_238AGE + "\"]),"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression8corr206238);

        Expression expressionEXP_8CORR_238_206_STAR = buildExpression(PB8CORR + R238U_206PB,
                "VALUEMODEL("
                + "1/[\"" + PB8CORR + R206PB_238U + "\"],"
                + "[%\"" + PB8CORR + R206PB_238U + "\"],"
                + "false)", false, true, false);
        samRadiogenicCols.add(expressionEXP_8CORR_238_206_STAR);

        Expression expression8corr207235 = buildExpression(PB8CORR + R207PB_235U,
                "Rad8corPb7U5WithErr("
                + "[\"" + TOTAL_206_238 + "\"],"
                + "[%\"" + TOTAL_206_238 + "\"],"
                + "[\"" + PB8CORR + R206PB_238U + "\"],"
                + "[\"" + TOTAL_206_238 + "\"]*[" + R207206 + "]/" + PRESENT_238U235U + ","
                + "[\"" + TH_U_EXP + "\"],"
                + "[%\"" + TH_U_EXP + "\"],"
                + "[" + R207206 + "],"
                + "[%" + R207206 + "],"
                + "[" + R208206 + "],"
                + "[%" + R208206 + "])", false, true, false);
        samRadiogenicCols.add(expression8corr207235);

        Expression expression8correrrcorr = buildExpression(PB8CORR + ERR_CORREL,
                "Rad8corConcRho("
                + "[\"" + TOTAL_206_238 + "\"],"
                + "[%\"" + TOTAL_206_238 + "\"],"
                + "[\"" + PB8CORR + R206PB_238U + "\"],"
                + "[\"" + TH_U_EXP + "\"],"
                + "[%\"" + TH_U_EXP + "\"],"
                + "[" + R207206 + "],"
                + "[%" + R207206 + "],"
                + "[" + R208206 + "],"
                + "[%" + R208206 + "])", false, true, false);
        samRadiogenicCols.add(expression8correrrcorr);

        Expression expression8corr207206 = buildExpression(PB8CORR + R207PB_206PB,
                "ValueModel("
                + "[\"" + PB8CORR + R207PB_235U + "\"]/[\"" + PB8CORR + R206PB_238U + "\"]/" + PRESENT_238U235U + ","
                + "SQRT([%\"" + PB8CORR + R207PB_235U + "\"]^2+[%\"" + PB8CORR + R206PB_238U + "\"]^2-"
                + "2*[%\"" + PB8CORR + R207PB_235U + "\"]*[%\"" + PB8CORR + R206PB_238U + "\"]*[\"" + PB8CORR + ERR_CORREL + "\"]),"
                + "false)", false, true, false);
        samRadiogenicCols.add(expression8corr207206);

        Expression expression208corr207Pb206PbAge = buildExpression(PB8COR207_206AGE,
                "AgePb76WithErr([\"" + PB8CORR + R207PB_206PB + "\"],"
                + "([\"" + PB8CORR + R207PB_206PB + "\"]*[%\"" + PB8CORR + R207PB_206PB + "\"]/100))", false, true, false);
        samRadiogenicCols.add(expression208corr207Pb206PbAge);

        R68i = "(EXP(" + LAMBDA238 + "*[\"" + PB8COR207_206AGE + "\"])-1)";
        Expression expression208corrDiscordance = buildExpression(PB8COR_DISCORDANCE,
                "100*(1-[\"" + PB8CORR + R206PB_238U + "\"]/" + R68i + ")", false, true, false);
        samRadiogenicCols.add(expression208corrDiscordance);

        Expression expressionTotal238U206Pb = buildExpression(TOTAL_238_206,
                "ValueModel("
                + "1/[\"" + TOTAL_206_238 + "\"],"
                + "[%\"" + TOTAL_206_238 + "\"],"
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
