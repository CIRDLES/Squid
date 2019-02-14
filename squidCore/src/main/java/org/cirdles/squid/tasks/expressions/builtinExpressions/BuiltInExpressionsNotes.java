/*
 * Copyright 2019 James F. Bowring and CIRDLES.org.
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
import java.util.TreeMap;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.AV_PARENT_ELEMENT_CONC_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.BIWT_204_OVR_CTS_FROM_207;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.BIWT_204_OVR_CTS_FROM_208;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.BIWT_4COR_207_206_AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.BIWT_7COR_PRIMARY_CALIB_CONST_DELTA_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.BIWT_8COR_PRIMARY_CALIB_CONST_DELTA_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.COM206PB_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.COM206PB_PCT_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.COM208PB_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.COM208PB_PCT_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.CORR_7_PRIMARY_CALIB_CONST_DELTA_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.CORR_8_PRIMARY_CALIB_CONST_DELTA_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFCOM_64;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFCOM_68;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFCOM_74;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFCOM_76;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFCOM_84;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFCOM_86;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.ERR_CORREL_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA230;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA232;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA234;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA235;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.LAMBDA238;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.OVER_COUNTS_PERSEC_4_7;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.OVER_COUNTS_PERSEC_4_8;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.OVER_COUNT_4_6_7;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.OVER_COUNT_4_6_8;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR206_238AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR206_238CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR206_238CALIB_CONST_WM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR207_206AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR208_232AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR208_232CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR208_232CALIB_CONST_WM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR206_238AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR206_238CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR206_238CALIB_CONST_WM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR208_232AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR208_232CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR208_232CALIB_CONST_WM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8COR206_238AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8COR206_238CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8COR206_238CALIB_CONST_WM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8COR207_206AGE_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PBTH_EXT_1_SIGMA_ERR_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PBU_EXT_1_SIGMA_ERR_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R206PB_238U_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207PB_206PB_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207PB_235U_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R208PB206PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R208PB206PB_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REFRAD_7_6;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REFRAD_8_6_FACT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REFRAD_AGE_PB_PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REFRAD_AGE_TH_PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REFRAD_AGE_U_PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REFRAD_TH_PB_RATIO;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REFRAD_U_PB_RATIO;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REF_TH_CONC_PPM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REF_U_CONC_PPM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_CONCEN_PPM_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TOTAL_206_238_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TOTAL_208_232_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR206PB238U_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR208PB232TH_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.U_CONCEN_PPM_RM;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class BuiltInExpressionsNotes {

    public static final Map<String, String> BUILTIN_EXPRESSION_NOTES = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    static {
        BUILTIN_EXPRESSION_NOTES.put(AV_PARENT_ELEMENT_CONC_CONST,
                "CM spots: Arithmetic mean of Parent Element concentration constants (for nominated Parent Element), as measured SOLELY on CM.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + R208PB206PB,
                "RM and Sample spots: Radiogenic 208Pb/206Pb, based on the 204Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + COM206PB_PCT,
                "RM and Sample spots: Common 206Pb as a percentage of total 206Pb, based on the 204Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + COM208PB_PCT,
                "RM and Sample spots: Common 208Pb as a percentage of total 208Pb, based on the 204Pb-correction.");

        BUILTIN_EXPRESSION_NOTES.put(DEFCOM_64,
                "Assumed 206Pb/204Pb of default common Pb, as applied to RM and (as first approximation of common Pb) to Samples.");
        BUILTIN_EXPRESSION_NOTES.put(DEFCOM_68,
                "Assumed 206Pb/208Pb of default common Pb, as applied to RM and (as first approximation of common Pb) to Samples.");
        BUILTIN_EXPRESSION_NOTES.put(DEFCOM_74,
                "Assumed 207Pb/204Pb of default common Pb, as applied to RM and (as first approximation of common Pb) to Samples.");
        BUILTIN_EXPRESSION_NOTES.put(DEFCOM_76,
                "Assumed 207Pb/206Pb of default common Pb, as applied to RM and (as first approximation of common Pb) to Samples.");
        BUILTIN_EXPRESSION_NOTES.put(DEFCOM_84,
                "Assumed 208Pb/204Pb of default common Pb, as applied to RM and (as first approximation of common Pb) to Samples.");
        BUILTIN_EXPRESSION_NOTES.put(DEFCOM_86,
                "Assumed 208Pb/206Pb of default common Pb, as applied to RM and (as first approximation of common Pb) to Samples.");

        BUILTIN_EXPRESSION_NOTES.put(LAMBDA230,
                "Decay constant for 230Th.");
        BUILTIN_EXPRESSION_NOTES.put(LAMBDA232,
                "Decay constant for 232Th.");
        BUILTIN_EXPRESSION_NOTES.put(LAMBDA234,
                "Decay constant for 234U.");
        BUILTIN_EXPRESSION_NOTES.put(LAMBDA235,
                "Decay constant for 235U.");
        BUILTIN_EXPRESSION_NOTES.put(LAMBDA238,
                "Decay constant for 238U.");

        BUILTIN_EXPRESSION_NOTES.put(PARENT_ELEMENT_CONC_CONST,
                "RM and Sample spots: Parent Element concentration constant, "
                + "for nominated Parent Element of Concentration RM, evaluated via Special U-Th-Pb Expression.");
        BUILTIN_EXPRESSION_NOTES.put(REF_TH_CONC_PPM,
                "Reference Th content of CM.");
        BUILTIN_EXPRESSION_NOTES.put(REF_U_CONC_PPM,
                "Reference U content of CM.");
        BUILTIN_EXPRESSION_NOTES.put(REFRAD_AGE_U_PB,
                "Reference radiogenic 206Pb/238U age of RM.");
        BUILTIN_EXPRESSION_NOTES.put(REFRAD_AGE_TH_PB,
                "Reference radiogenic 208Pb/232Th age of RM.");
        BUILTIN_EXPRESSION_NOTES.put(REFRAD_AGE_PB_PB,
                "Reference radiogenic 207Pb/206Pb age of RM.");
        BUILTIN_EXPRESSION_NOTES.put(REFRAD_U_PB_RATIO,
                "Reference radiogenic 206Pb/238U of RM.");
        BUILTIN_EXPRESSION_NOTES.put(REFRAD_TH_PB_RATIO,
                "Reference radiogenic 208Pb/232Th of RM.");
        BUILTIN_EXPRESSION_NOTES.put(REFRAD_7_6,
                "Reference radiogenic 207Pb/206Pb of RM.");
        BUILTIN_EXPRESSION_NOTES.put(REFRAD_8_6_FACT,
                "Factor defined as (radiogenic 208Pb/206Pb) * (238U/232Th) for RM;"
                + "\n determined arithmetically as (exp[Lambda232 * RefRad_208Pb232Th_Age] - 1)"
                + "\n / (exp[Lambda238 * RefRad_206Pb238U_Age] - 1).");

        BUILTIN_EXPRESSION_NOTES.put(UNCOR206PB238U_CALIB_CONST,
                "RM and Sample spots: 206Pb/238U calibration constant, uncorrected for common Pb,"
                + "\n evaluated via Special U-Th-Pb Expression. Calculated by all Tasks, "
                + "\n except Th-Pb Tasks that calculate only a single 208Pb/232Th calibration (\"Perm3\").");
        BUILTIN_EXPRESSION_NOTES.put(UNCOR208PB232TH_CALIB_CONST,
                "RM and Sample spots: 208Pb/232Th calibration constant, uncorrected for common Pb,"
                + "\n evaluated via Special U-Th-Pb Expression. Calculated by all Tasks, "
                + "\n except U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");

        BUILTIN_EXPRESSION_NOTES.put(OVER_COUNT_4_6_7,
                "RM spots: 204Pb/206Pb corrected for overcounts at mass 204, based on counts at 207Pb.");
        BUILTIN_EXPRESSION_NOTES.put(OVER_COUNT_4_6_8,
                "RM spots: 204Pb/206Pb corrected for overcounts at mass 204, based on counts at 208Pb. "
                + "\n In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\"), this value "
                + "\n depends on the index isotope (204Pb or 207Pb) chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(OVER_COUNTS_PERSEC_4_8,
                "RM spots: Overcounts per second at mass 204, based on counts at 208Pb. "
                + "\n In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\"), "
                + "\n this value depends on the index isotope (204Pb or 207Pb) "
                + "\n chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(OVER_COUNTS_PERSEC_4_7,
                "RM spots: Overcounts per second at mass 204, based on counts at 207Pb.");

        BUILTIN_EXPRESSION_NOTES.put(TH_U_EXP_RM,
                "RM spots: Calculated 232Th/238U. In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\"),"
                + "\n this value depends on the index isotope (204Pb or 207Pb) chosen for the common Pb correction.");

        BUILTIN_EXPRESSION_NOTES.put(PB4COR206_238AGE_RM,
                "RM spots: 204Pb-corrected 206Pb/238U age. Calculated for RMs in all "
                + "Tasks except Th-Pb Tasks that calculate only a single 208Pb/232Th calibration (\"Perm3\").");
        BUILTIN_EXPRESSION_NOTES.put(PB4COR206_238CALIB_CONST,
                "RM spots: 204Pb-corrected 206Pb/238U calibration constant. Calculated for RMs"
                + "\n  in all Tasks except Th-Pb Tasks that calculate only a single "
                + "\n 208Pb/232Th calibration (\"Perm3\").");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + PBU_EXT_1_SIGMA_ERR_PCT,
                "RM dataset: Calculated external (spot-to-spot) 1sigma uncertainty "
                + "\n (expressed as a percentage), derived from WtdAv_4cor_206Pb238U_CalibConst, "
                + "\n subject to a user-defined minimum value. Calculated by all Tasks except "
                + "\n Th-Pb Tasks that calculate only a single 208Pb/232Th calibration (\"Perm3\").");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + R206PB_238U_RM,
                "RM spots: 204Pb-corrected 206Pb/238U ratio. Calculated for RMs in all Tasks.");
        BUILTIN_EXPRESSION_NOTES.put(PB4COR207_206AGE_RM,
                "RM spots: 204Pb-corrected 207Pb/206Pb age. Calculated for RMs in all Tasks.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + R207PB_206PB_RM,
                "RM spots: 204Pb-corrected 207Pb/206Pb ratio. Calculated for RMs in all Tasks.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + R207PB_235U_RM,
                "RM spots: 204Pb-corrected 207Pb/235U ratio. Calculated for RMs in all Tasks.");
        BUILTIN_EXPRESSION_NOTES.put(PB4COR208_232AGE_RM,
                "RM spots: 204Pb-corrected 208Pb/232Th age. Calculated for RMs in all Tasks "
                + "\n except U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB4COR208_232CALIB_CONST,
                "RM spots: 204Pb-corrected 208Pb/232Th calibration constant. "
                + "\n Calculated for RMs in all Tasks except U-Pb Tasks that calculate "
                + "\n only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + PBTH_EXT_1_SIGMA_ERR_PCT,
                "RM dataset: Calculated external (spot-to-spot) 1sigma uncertainty "
                + "\n (expressed as a percentage), derived from WtdAv_4cor_208Pb232Th_CalibConst,"
                + "\n  subject to a user-defined minimum value. Calculated by all Tasks except "
                + "\n U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + ERR_CORREL_RM,
                "RM spots: Error correlation (for Wetherill concordia plot) between "
                + "\n 204Pb-corrected values of 207Pb/235U and 206Pb/238U.");

        BUILTIN_EXPRESSION_NOTES.put(PB7COR206_238AGE_RM,
                "RM spots: 207Pb-corrected 206Pb/238U age");
        BUILTIN_EXPRESSION_NOTES.put(PB7COR206_238CALIB_CONST,
                "RM spots: 207Pb-corrected 206Pb/238U calibration constant. "
                + "\n Calculated by all Tasks except Th-Pb Tasks that calculate "
                + "\n only a single 208Pb/232Th calibration (\"Perm3\").");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + PBU_EXT_1_SIGMA_ERR_PCT,
                "RM dataset: Calculated external (spot-to-spot) 1sigma uncertainty "
                + "\n (expressed as a percentage), derived from WtdAv_7cor_206Pb238U_CalibConst,"
                + "\n subject to a user-defined minimum value. Calculated by all Tasks "
                + "\n except Th-Pb Tasks that calculate only a single 208Pb/232Th calibration (\"Perm3\").");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + R206PB_238U_RM,
                "RM spots: 207Pb-corrected 206Pb/238U");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + R208PB206PB_RM,
                "RM spots: Radiogenic 208Pb/206Pb, based on the 207Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB7COR208_232AGE_RM,
                "RM spots: 207Pb-corrected 208Pb/232Th age. Calculated by all Tasks"
                + "\n  except U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB7COR208_232CALIB_CONST,
                "RM spots: 207Pb-corrected 208Pb/232Th calibration constant. Calculated "
                + "\n by all Tasks except U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + PBTH_EXT_1_SIGMA_ERR_PCT,
                "RM dataset: Calculated external (spot-to-spot) 1sigma uncertainty (expressed as a percentage),"
                + "\n derived from WtdAv_7cor_208Pb232Th_CalibConst, subject to a user-defined minimum value. "
                + "\n Calculated by all Tasks except U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + COM206PB_PCT_RM,
                "RM spots: Common 206Pb as a percentage of total 206Pb, based on the 207Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + COM208PB_PCT_RM,
                "RM spots: Common 208Pb as a percentage of total 208Pb, based on the 207Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(CORR_7_PRIMARY_CALIB_CONST_DELTA_PCT,
                "RM spots: Offset (expressed as a percentage) of the primary calibration constant, "
                + "\n based on overcounts at mass 204 as calculated from counts at 207Pb.");

        BUILTIN_EXPRESSION_NOTES.put(PB8COR206_238AGE_RM,
                "RM spots: 208Pb-corrected 206Pb/238U age. Calculated solely "
                + "\n in U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB8COR206_238CALIB_CONST,
                "RM spots: 208Pb-corrected 206Pb/238U calibration constant. Calculated solely "
                + "\n in U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + PBU_EXT_1_SIGMA_ERR_PCT,
                "RM dataset: Calculated external (spot-to-spot) 1sigma uncertainty (expressed as a percentage),"
                + "\n derived from WtdAv_8cor_206Pb238U_CalibConst, subject to a user-defined minimum value. "
                + "\n Calculated solely in U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + R206PB_238U_RM,
                "RM spots: 208Pb-corrected 206Pb/238U. Calculated solely in U-Pb Tasks that calculate only a "
                + "\n single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB8COR207_206AGE_RM,
                "RM spots: 208Pb-corrected 207Pb/206Pb age. Calculated solely in U-Pb Tasks that calculate only "
                + "\n a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + R207PB_206PB_RM,
                "RM spots: 208Pb-corrected 207Pb/206Pb. Calculated solely in U-Pb Tasks that calculate "
                + "\n only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + R207PB_235U_RM,
                "RM spots: 208Pb-corrected 207Pb/235U. Calculated solely in U-Pb "
                + "\n Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + COM206PB_PCT_RM,
                "RM spots: Common 206Pb as a percentage of total 206Pb, based on the 208Pb-correction. "
                + "\n Calculated solely in U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + ERR_CORREL_RM,
                "RM spots: Error correlation (for Wetherill concordia plot) between 208Pb-corrected values "
                + "\n of 207Pb/235U and 206Pb/238U. Calculated solely in U-Pb Tasks that calculate only a "
                + "\n single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(CORR_8_PRIMARY_CALIB_CONST_DELTA_PCT,
                "RM spots: Offset (expressed as a percentage) of the primary calibration constant, "
                + "\n based on overcounts at mass 204 as calculated from counts at 208Pb. "
                + "\n In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\"), this value "
                + "\n depends on the index isotope (204Pb or 207Pb) chosen for the common Pb correction.");

        BUILTIN_EXPRESSION_NOTES.put(BIWT_204_OVR_CTS_FROM_207,
                "RM dataset: Biweight mean of values of overcounts-per-second at mass 204, based on counts at 207Pb in the RM.");
        BUILTIN_EXPRESSION_NOTES.put(BIWT_204_OVR_CTS_FROM_208,
                "RM dataset: Biweight mean of values of overcounts-per-second at mass 204, based on counts at 208Pb. "
                + "\n In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\"), this value depends on the "
                + "\n index isotope (204Pb or 207Pb) chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(BIWT_4COR_207_206_AGE,
                "RM dataset: Biweight mean of 204Pb-corrected 207Pb/206Pb ages.");
        BUILTIN_EXPRESSION_NOTES.put(BIWT_7COR_PRIMARY_CALIB_CONST_DELTA_PCT,
                "RM dataset: Biweight mean of offsets (expressed as percentages) of the primary calibration constant, "
                + "\n based on overcounts at mass 204 as calculated from counts at 207Pb.");
        BUILTIN_EXPRESSION_NOTES.put(BIWT_8COR_PRIMARY_CALIB_CONST_DELTA_PCT,
                "RM dataset: Biweight mean of offsets (expressed as percentages) of the primary calibration constant,"
                + "\n based on overcounts at mass 204 as calculated from counts at 208Pb. In dual-calibration Tasks"
                + "\n (i.e. \"Perm2\" and \"Perm4\"), this value depends on the index isotope (204Pb or 207Pb) chosen "
                + "\n for the common Pb correction.");

        BUILTIN_EXPRESSION_NOTES.put(TH_CONCEN_PPM_RM,
                "RM spots: Calculated Th content. In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\") "
                + "\n where the CM is defined in terms of its U content, this value depends on the index isotope "
                + "\n (204Pb or 207Pb) chosen for the common Pb correction.");

        BUILTIN_EXPRESSION_NOTES.put(TOTAL_206_238_RM,
                "RM spots: Calculated total 206Pb/238U. Always depends on the index isotope (204Pb, 207Pb, or in "
                + "\n the case of \"Perm1\", possibly 208Pb)  chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(TOTAL_208_232_RM,
                "RM spots: Calculated total 208Pb/232Th. Always depends on the index isotope (204Pb, 207Pb, or in "
                + "\n the case of \"Perm1\", possibly 208Pb)  chosen for the common Pb correction.");

        BUILTIN_EXPRESSION_NOTES.put(U_CONCEN_PPM_RM,
                "RM spots: Calculated U content. In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\") "
                + "\n where the CM is defined in terms of its Th content, this value depends on the index "
                + "\n isotope (204Pb or 207Pb) chosen for the common Pb correction.");

        BUILTIN_EXPRESSION_NOTES.put(PB4COR206_238CALIB_CONST_WM,
                "RM dataset: Weighted mean of 204Pb-corrected 206Pb/238U calibration constants. "
                + "\n Calculated by all Tasks except Th-Pb Tasks that calculate only a single 208Pb/232Th calibration (\"Perm3\").");
        BUILTIN_EXPRESSION_NOTES.put(PB4COR208_232CALIB_CONST_WM,
                "RM dataset: Weighted mean of 204Pb-corrected 208Pb/232Th calibration constants. Calculated by all Tasks "
                + "\n except U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB7COR206_238CALIB_CONST_WM,
                "RM datatset: Weighted mean of 207Pb-corrected 206Pb/238U calibration constants. Calculated by all Tasks "
                + "\n except Th-Pb Tasks that calculate only a single 208Pb/232Th calibration (\"Perm3\").");
        BUILTIN_EXPRESSION_NOTES.put(PB7COR208_232CALIB_CONST_WM,
                "RM dataset: Weighted mean of 207Pb-corrected 208Pb/232Th calibration constants. Calculated by all Tasks "
                + "\n except U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB8COR206_238CALIB_CONST_WM,
                "RM dataset: Weighted mean of 208Pb-corrected 206Pb/238U calibration constants. Calculated solely in U-Pb Tasks "
                + "\n that calculate only a single 206Pb/238U calibration (\"Perm1\").");

        // start unknowns        
        BUILTIN_EXPRESSION_NOTES.put("",
                "");
        BUILTIN_EXPRESSION_NOTES.put("",
                "");
        BUILTIN_EXPRESSION_NOTES.put("",
                "");
        BUILTIN_EXPRESSION_NOTES.put("",
                "");
        BUILTIN_EXPRESSION_NOTES.put("",
                "");

    }
}
