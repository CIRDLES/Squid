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

import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.*;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class BuiltInExpressionsNotes {

    public static final Map<String, String> BUILTIN_EXPRESSION_NOTES = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    static {
        BUILTIN_EXPRESSION_NOTES.put(L1033,
                "Concentration coefficient linking present-day total U content to present-day 232Th content.");
        BUILTIN_EXPRESSION_NOTES.put(L859,
                "Concentration coefficient linking radiogenic 206Pb content to total U content in a closed system of known age. "
                        + "The coefficient is used in conjunction with the 206Pb/238U age equation.");
        BUILTIN_EXPRESSION_NOTES.put(L9678,
                "Concentration coefficient linking present-day 232Th content to present-day total U content.");

        BUILTIN_EXPRESSION_NOTES.put(NUKEMASS206PB,
                "Atomic mass in amu.");
        BUILTIN_EXPRESSION_NOTES.put(NUKEMASS232TH,
                "Atomic mass in amu.");
        BUILTIN_EXPRESSION_NOTES.put(NUKEMASS238U,
                "Atomic mass in amu.");

        BUILTIN_EXPRESSION_NOTES.put(REF_238U235U,
                "Reference 238U/235U of Isotopic RM.");

        BUILTIN_EXPRESSION_NOTES.put(AV_PARENT_ELEMENT_CONC_CONST,
                "CM spots: Arithmetic mean of Parent Element concentration constants (for nominated Parent Element), as measured SOLELY on CM.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + R208PB206PB_RM,
                "RM spots: Radiogenic 208Pb/206Pb, based on the 204Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + R208PB206PB,
                "Sample spots: Radiogenic 208Pb/206Pb, based on the 204Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + COM206PB_PCT_RM,
                "RM spots: Common 206Pb as a percentage of total 206Pb, based on the 204Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + COM206PB_PCT,
                "Sample spots: Common 206Pb as a percentage of total 206Pb, based on the 204Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + COM208PB_PCT_RM,
                "RM spots: Common 208Pb as a percentage of total 208Pb, based on the 204Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + COM208PB_PCT,
                "Sample spots: Common 208Pb as a percentage of total 208Pb, based on the 204Pb-correction.");

        BUILTIN_EXPRESSION_NOTES.put(DEFCOM_64,
                "Assumed 206Pb/204Pb of default common Pb, as applied to RM and (as first approximation of common Pb) to Samples.");
        BUILTIN_EXPRESSION_NOTES.put(COM_64,
                "Assigned 206Pb/204Pb of common Pb customized for each Sample and defaulting to DefCom_206Pb204Pb for each RM.");
        BUILTIN_EXPRESSION_NOTES.put(DEFCOM_68,
                "Assumed 206Pb/208Pb of default common Pb, as applied to RM and (as first approximation of common Pb) to Samples.");
        BUILTIN_EXPRESSION_NOTES.put(COM_68,
                "Assigned 206Pb/208Pb of common Pb customized for each Sample and defaulting to DefCom_206Pb208Pb for each RM.");
        BUILTIN_EXPRESSION_NOTES.put(DEFCOM_74,
                "Assumed 207Pb/204Pb of default common Pb, as applied to RM and (as first approximation of common Pb) to Samples.");
        BUILTIN_EXPRESSION_NOTES.put(COM_74,
                "Assigned 207Pb/204Pb of common Pb customized for each Sample and defaulting to DefCom_207Pb204Pb for each RM.");
        BUILTIN_EXPRESSION_NOTES.put(DEFCOM_76,
                "Assumed 207Pb/206Pb of default common Pb, as applied to RM and (as first approximation of common Pb) to Samples.");
        BUILTIN_EXPRESSION_NOTES.put(COM_76,
                "Assigned 207Pb/206Pb of common Pb customized for each Sample and defaulting to DefCom_207Pb206Pb for each RM.");
        BUILTIN_EXPRESSION_NOTES.put(DEFCOM_84,
                "Assumed 208Pb/204Pb of default common Pb, as applied to RM and (as first approximation of common Pb) to Samples.");
        BUILTIN_EXPRESSION_NOTES.put(COM_84,
                "Assigned 208Pb/204Pb of common Pb customized for each Sample and defaulting to DefCom_208Pb204Pb for each RM.");
        BUILTIN_EXPRESSION_NOTES.put(DEFCOM_86,
                "Assumed 208Pb/206Pb of default common Pb, as applied to RM and (as first approximation of common Pb) to Samples.");
        BUILTIN_EXPRESSION_NOTES.put(COM_86,
                "Assigned 208Pb/206Pb of common Pb customized for each Sample and defaulting to DefCom_208Pb206Pb for each RM.");

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
                        + " determined arithmetically as (exp[Lambda232 * RefRad_208Pb232Th_Age] - 1)"
                        + " / (exp[Lambda238 * RefRad_206Pb238U_Age] - 1).");

        BUILTIN_EXPRESSION_NOTES.put(UNCOR206PB238U_CALIB_CONST,
                "RM and Sample spots: 206Pb/238U calibration constant, uncorrected for common Pb,"
                        + " evaluated via Special U-Th-Pb Expression. Calculated by all Tasks, "
                        + " except Th-Pb Tasks that calculate only a single 208Pb/232Th calibration (\"Perm3\").");
        BUILTIN_EXPRESSION_NOTES.put(UNCOR208PB232TH_CALIB_CONST,
                "RM and Sample spots: 208Pb/232Th calibration constant, uncorrected for common Pb,"
                        + " evaluated via Special U-Th-Pb Expression. Calculated by all Tasks, "
                        + " except U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");

        BUILTIN_EXPRESSION_NOTES.put(OVER_COUNT_4_6_7,
                "RM spots: 204Pb/206Pb corrected for overcounts at mass 204, based on counts at 207Pb.");
        BUILTIN_EXPRESSION_NOTES.put(OVER_COUNT_4_6_8,
                "RM spots: 204Pb/206Pb corrected for overcounts at mass 204, based on counts at 208Pb. "
                        + " In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\"), this value "
                        + " depends on the index isotope (204Pb or 207Pb) chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(OVER_COUNTS_PERSEC_4_8,
                "RM spots: Overcounts per second at mass 204, based on counts at 208Pb. "
                        + " In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\"), "
                        + " this value depends on the index isotope (204Pb or 207Pb) "
                        + " chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(OVER_COUNTS_PERSEC_4_7,
                "RM spots: Overcounts per second at mass 204, based on counts at 207Pb.");

        BUILTIN_EXPRESSION_NOTES.put(TH_U_EXP_RM,
                "RM spots: Calculated 232Th/238U. In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\"),"
                        + " this value depends on the index isotope (204Pb or 207Pb) chosen for the common Pb correction.");

        BUILTIN_EXPRESSION_NOTES.put(PB4COR206_238AGE_RM,
                "RM spots: 204Pb-corrected 206Pb/238U age. Calculated for RMs in all "
                        + "Tasks except Th-Pb Tasks that calculate only a single 208Pb/232Th calibration (\"Perm3\").");
        BUILTIN_EXPRESSION_NOTES.put(PB4COR206_238CALIB_CONST,
                "RM spots: 204Pb-corrected 206Pb/238U calibration constant. Calculated for RMs"
                        + "  in all Tasks except Th-Pb Tasks that calculate only a single "
                        + " 208Pb/232Th calibration (\"Perm3\").");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + PBU_EXT_1_SIGMA_ERR_PCT,
                "RM dataset: Calculated external (spot-to-spot) 1sigma uncertainty "
                        + " (expressed as a percentage), derived from WtdAv_4cor_206Pb238U_CalibConst, "
                        + " subject to a user-defined minimum value. Calculated by all Tasks except "
                        + " Th-Pb Tasks that calculate only a single 208Pb/232Th calibration (\"Perm3\").");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + R206PB_238U_RM,
                "RM spots: 204Pb-corrected 206Pb/238U.");
        BUILTIN_EXPRESSION_NOTES.put(PB4COR207_206AGE_RM,
                "RM spots: 204Pb-corrected 207Pb/206Pb age.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + R207PB_206PB_RM,
                "RM spots: 204Pb-corrected 207Pb/206Pb ratio.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + R207PB_235U_RM,
                "RM spots: 204Pb-corrected 207Pb/235U ratio.");
        BUILTIN_EXPRESSION_NOTES.put(PB4COR208_232AGE_RM,
                "RM spots: 204Pb-corrected 208Pb/232Th age. Calculated for RMs in all Tasks "
                        + " except U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB4COR208_232CALIB_CONST,
                "RM spots: 204Pb-corrected 208Pb/232Th calibration constant. "
                        + " Calculated for RMs in all Tasks except U-Pb Tasks that calculate "
                        + " only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + PBTH_EXT_1_SIGMA_ERR_PCT,
                "RM dataset: Calculated external (spot-to-spot) 1sigma uncertainty "
                        + " (expressed as a percentage), derived from WtdAv_4cor_208Pb232Th_CalibConst,"
                        + "  subject to a user-defined minimum value. Calculated by all Tasks except "
                        + " U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + ERR_CORREL_RM,
                "RM spots: Error correlation (for Wetherill concordia plot) between "
                        + " 204Pb-corrected values of 207Pb/235U and 206Pb/238U.");

        BUILTIN_EXPRESSION_NOTES.put(PB7COR206_238AGE_RM,
                "RM spots: 207Pb-corrected 206Pb/238U age");
        BUILTIN_EXPRESSION_NOTES.put(PB7COR206_238CALIB_CONST,
                "RM spots: 207Pb-corrected 206Pb/238U calibration constant. "
                        + " Calculated by all Tasks except Th-Pb Tasks that calculate "
                        + " only a single 208Pb/232Th calibration (\"Perm3\").");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + PBU_EXT_1_SIGMA_ERR_PCT,
                "RM dataset: Calculated external (spot-to-spot) 1sigma uncertainty "
                        + " (expressed as a percentage), derived from WtdAv_7cor_206Pb238U_CalibConst,"
                        + " subject to a user-defined minimum value. Calculated by all Tasks "
                        + " except Th-Pb Tasks that calculate only a single 208Pb/232Th calibration (\"Perm3\").");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + R206PB_238U_RM,
                "RM spots: 207Pb-corrected 206Pb/238U");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + R208PB206PB_RM,
                "RM spots: Radiogenic 208Pb/206Pb, based on the 207Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB7COR208_232AGE_RM,
                "RM spots: 207Pb-corrected 208Pb/232Th age. Calculated by all Tasks"
                        + "  except U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB7COR208_232CALIB_CONST,
                "RM spots: 207Pb-corrected 208Pb/232Th calibration constant. Calculated "
                        + " by all Tasks except U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + PBTH_EXT_1_SIGMA_ERR_PCT,
                "RM dataset: Calculated external (spot-to-spot) 1sigma uncertainty (expressed as a percentage),"
                        + " derived from WtdAv_7cor_208Pb232Th_CalibConst, subject to a user-defined minimum value. "
                        + " Calculated by all Tasks except U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + COM206PB_PCT_RM,
                "RM spots: Common 206Pb as a percentage of total 206Pb, based on the 207Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + COM208PB_PCT_RM,
                "RM spots: Common 208Pb as a percentage of total 208Pb, based on the 207Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(CORR_7_PRIMARY_CALIB_CONST_DELTA_PCT,
                "RM spots: Offset (expressed as a percentage) of the primary calibration constant, "
                        + " based on overcounts at mass 204 as calculated from counts at 207Pb.");

        BUILTIN_EXPRESSION_NOTES.put(PB8COR206_238AGE_RM,
                "RM spots: 208Pb-corrected 206Pb/238U age. Calculated solely in U-Pb Tasks that calculate "
                        + "only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB8COR206_238CALIB_CONST,
                "RM spots: 208Pb-corrected 206Pb/238U calibration constant. Calculated solely "
                        + " in U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + PBU_EXT_1_SIGMA_ERR_PCT,
                "RM dataset: Calculated external (spot-to-spot) 1sigma uncertainty (expressed as a percentage),"
                        + " derived from WtdAv_8cor_206Pb238U_CalibConst, subject to a user-defined minimum value. "
                        + " Calculated solely in U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + R206PB_238U_RM,
                "RM spots: 208Pb-corrected 206Pb/238U. Calculated solely in U-Pb Tasks that calculate only a "
                        + " single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB8COR207_206AGE_RM,
                "RM spots: 208Pb-corrected 207Pb/206Pb age. Calculated solely in U-Pb Tasks that calculate only "
                        + " a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + R207PB_206PB_RM,
                "RM spots: 208Pb-corrected 207Pb/206Pb. Calculated solely in U-Pb Tasks that calculate "
                        + " only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + R207PB_235U_RM,
                "RM spots: 208Pb-corrected 207Pb/235U. Calculated solely in U-Pb "
                        + " Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + COM206PB_PCT_RM,
                "RM spots: Common 206Pb as a percentage of total 206Pb, based on the 208Pb-correction. "
                        + " Calculated solely in U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + ERR_CORREL_RM,
                "RM spots: Error correlation (for Wetherill concordia plot) between 208Pb-corrected values "
                        + " of 207Pb/235U and 206Pb/238U. Calculated solely in U-Pb Tasks that calculate only a "
                        + " single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(CORR_8_PRIMARY_CALIB_CONST_DELTA_PCT,
                "RM spots: Offset (expressed as a percentage) of the primary calibration constant, "
                        + " based on overcounts at mass 204 as calculated from counts at 208Pb. "
                        + " In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\"), this value "
                        + " depends on the index isotope (204Pb or 207Pb) chosen for the common Pb correction.");

        BUILTIN_EXPRESSION_NOTES.put(BIWT_204_OVR_CTS_FROM_207,
                "RM dataset: Biweight mean of values of overcounts-per-second at mass 204, based on counts at 207Pb in the RM.");
        BUILTIN_EXPRESSION_NOTES.put(BIWT_204_OVR_CTS_FROM_208,
                "RM dataset: Biweight mean of values of overcounts-per-second at mass 204, based on counts at 208Pb. "
                        + " In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\"), this value depends on the "
                        + " index isotope (204Pb or 207Pb) chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(BIWT_4COR_207_206_AGE,
                "RM dataset: Biweight mean of 204Pb-corrected 207Pb/206Pb ages.");
        BUILTIN_EXPRESSION_NOTES.put(BIWT_7COR_PRIMARY_CALIB_CONST_DELTA_PCT,
                "RM dataset: Biweight mean of offsets (expressed as percentages) of the primary calibration constant, "
                        + " based on overcounts at mass 204 as calculated from counts at 207Pb.");
        BUILTIN_EXPRESSION_NOTES.put(BIWT_8COR_PRIMARY_CALIB_CONST_DELTA_PCT,
                "RM dataset: Biweight mean of offsets (expressed as percentages) of the primary calibration constant,"
                        + " based on overcounts at mass 204 as calculated from counts at 208Pb. In dual-calibration Tasks"
                        + " (i.e. \"Perm2\" and \"Perm4\"), this value depends on the index isotope (204Pb or 207Pb) chosen "
                        + " for the common Pb correction.");

        BUILTIN_EXPRESSION_NOTES.put(TH_CONCEN_PPM_RM,
                "RM spots: Calculated Th content. In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\") "
                        + " where the CM is defined in terms of its U content, this value depends on the index isotope "
                        + " (204Pb or 207Pb) chosen for the common Pb correction.");

        BUILTIN_EXPRESSION_NOTES.put(TOTAL_206_238_RM,
                "RM spots: Calculated total 206Pb/238U. Always depends on the index isotope (204Pb, 207Pb, "
                        + "or in the case of U-Pb Tasks that calculate only a single 206Pb/238U calibration "
                        + "[\"Perm1\"], possibly 208Pb)  chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(TOTAL_208_232_RM,
                "RM spots: Calculated total 208Pb/232Th. Always depends on the index isotope (204Pb, 207Pb, "
                        + "or in the case of U-Pb Tasks that calculate only a single 206Pb/238U calibration "
                        + "[\"Perm1\"], possibly 208Pb)  chosen for the common Pb correction.");

        BUILTIN_EXPRESSION_NOTES.put(U_CONCEN_PPM_RM,
                "RM spots: Calculated U content. In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\") "
                        + " where the CM is defined in terms of its Th content, this value depends on the index "
                        + " isotope (204Pb or 207Pb) chosen for the common Pb correction.");

        BUILTIN_EXPRESSION_NOTES.put(PB4COR206_238CALIB_CONST_WM,
                "RM dataset: Weighted mean of 204Pb-corrected 206Pb/238U calibration constants. "
                        + " Calculated by all Tasks except Th-Pb Tasks that calculate only a single 208Pb/232Th calibration (\"Perm3\").");
        BUILTIN_EXPRESSION_NOTES.put(PB4COR208_232CALIB_CONST_WM,
                "RM dataset: Weighted mean of 204Pb-corrected 208Pb/232Th calibration constants. Calculated by all Tasks "
                        + " except U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB7COR206_238CALIB_CONST_WM,
                "RM datatset: Weighted mean of 207Pb-corrected 206Pb/238U calibration constants. Calculated by all Tasks "
                        + " except Th-Pb Tasks that calculate only a single 208Pb/232Th calibration (\"Perm3\").");
        BUILTIN_EXPRESSION_NOTES.put(PB7COR208_232CALIB_CONST_WM,
                "RM dataset: Weighted mean of 207Pb-corrected 208Pb/232Th calibration constants. Calculated by all Tasks "
                        + " except U-Pb Tasks that calculate only a single 206Pb/238U calibration (\"Perm1\").");
        BUILTIN_EXPRESSION_NOTES.put(PB8COR206_238CALIB_CONST_WM,
                "RM dataset: Weighted mean of 208Pb-corrected 206Pb/238U calibration constants. Calculated solely in U-Pb Tasks "
                        + " that calculate only a single 206Pb/238U calibration (\"Perm1\").");

        // start unknowns        
        BUILTIN_EXPRESSION_NOTES.put(TH_U_EXP,
                "Sample spots: 232Th/238U. In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\"), this value depends "
                        + "on the index isotope (204Pb or 207Pb) chosen for common Pb correction of the RM.");

        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + CONCEN_206PB,
                "Sample spots: Radiogenic 206Pb content, based on the 204Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + R206PB_238U,
                "Sample spots: 204Pb-corrected 206Pb/238U.");
        BUILTIN_EXPRESSION_NOTES.put(PB4COR206_238AGE,
                "Sample spots: 204Pb-corrected 206Pb/238U age.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + R207PB_206PB,
                "Sample spots: 204Pb-corrected 207Pb/206Pb.");
        BUILTIN_EXPRESSION_NOTES.put(PB4COR207_206AGE,
                "Sample spots: 204Pb-corrected 207Pb/206Pb age.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + R207PB_235U,
                "Sample spots: 204Pb-corrected 207Pb/235U.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + CONCEN_208PB,
                "Sample spots: Radiogenic 208Pb content, based on the 204Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + R208PB_232TH,
                "Sample spots: 204Pb-corrected 208Pb/232Th.");
        BUILTIN_EXPRESSION_NOTES.put(PB4COR208_232AGE,
                "Sample spots: 204Pb-corrected 208Pb/232Th age.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + R238U_206PB,
                "Sample spots: 204Pb-corrected 238U/206Pb.");
        BUILTIN_EXPRESSION_NOTES.put(PB4COR_DISCORDANCE,
                "Sample spots: Discordance of 204Pb-corrected 206Pb/238U and 207Pb/206Pb, "
                        + " defined as 100 * [ 1 - { (206Pb/238U) / exp( [ Lambda238 * {207Pb/206Pb age} ] - 1 ) } ],"
                        + " and expressed as a percentage.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + ERR_CORREL,
                "Sample spots: Error correlation (for Wetherill concordia plot) between "
                        + " 204Pb-corrected values of 207Pb/235U and 206Pb/238U.");

        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + R204PB_206PB,
                "Sample spots: 204Pb/206Pb corrected for biweight mean of overcounts at mass 204, based on counts at 207Pb in the RM dataset.");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + CONCEN_206PB,
                "Sample spots: Radiogenic 206Pb content, based on the 207Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + R206PB_238U,
                "Sample spots: 207Pb-corrected 206Pb/238U.");
        BUILTIN_EXPRESSION_NOTES.put(PB7COR206_238AGE,
                "Sample spots: 207Pb-corrected 206Pb/238U age.");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + CONCEN_208PB,
                "Sample spots: Radiogenic 208Pb content, based on the 207Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + R208PB206PB,
                "Sample spots: Radiogenic 208Pb/206Pb, based on the 207Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + R208PB_232TH,
                "Sample spots: 207Pb-corrected 208Pb/232Th.");
        BUILTIN_EXPRESSION_NOTES.put(PB7COR208_232AGE,
                "Sample spots: 207Pb-corrected 208Pb/232Th age.");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + COM206PB_PCT,
                "Sample spots: Common 206Pb as a percentage of total 206Pb, based on the 207Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + COM208PB_PCT,
                "Sample spots: Common 208Pb as a percentage of total 208Pb, based on the 207Pb-correction.");

        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + R204PB_206PB,
                "Sample spots: 204Pb/206Pb corrected for biweight mean of overcounts at "
                        + "/n mass 204, based on counts at 208Pb in the RM dataset.");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + CONCEN_206PB,
                "Sample spots: Radiogenic 206Pb content, based on the 208Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + R206PB_238U,
                "Sample spots: 208Pb-corrected 206Pb/238U.");
        BUILTIN_EXPRESSION_NOTES.put(PB8COR206_238AGE,
                "Sample spots: 208Pb-corrected 206Pb/238U age.");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + R207PB_206PB,
                "Sample spots: 208Pb-corrected 207Pb/206Pb.");
        BUILTIN_EXPRESSION_NOTES.put(PB8COR207_206AGE,
                "Sample spots: 208Pb-corrected 207Pb/206Pb age.");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + R207PB_235U,
                "Sample spots: 208Pb-corrected 207Pb/235U.");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + R238U_206PB,
                "CM spots: Arithmetic mean of Parent Element concentration constants "
                        + " (for nominated Parent Element), as measured SOLELY on CM");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + COM206PB_PCT,
                "Sample spots: Common 206Pb as a percentage of total 206Pb, based on the 208Pb-correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB8COR_DISCORDANCE,
                "Sample spots: Discordance of 208Pb-corrected 206Pb/238U and 207Pb/206Pb, "
                        + " defined as 100 * [ 1 - { (206Pb/238U) / exp( [ Lambda238 * {207Pb/206Pb age} ] - 1 ) } ], "
                        + "\nand expressed as a percentage");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + ERR_CORREL,
                "Sample spots: Error correlation (for Wetherill concordia plot) between 208Pb-corrected "
                        + " values of 207Pb/235U and 206Pb/238U.");

        BUILTIN_EXPRESSION_NOTES.put(DEFRAD_206PB204PB,
                "Sample spots: Default (radiogenic 206Pb)/204Pb, defined as (Total_206Pb204Pb) - (DefCom_206Pb204Pb).");
        BUILTIN_EXPRESSION_NOTES.put(DEFRAD_207PB204PB,
                "Sample spots: Default (radiogenic 207Pb)/204Pb, defined as (Total_207Pb204Pb) - (DefCom_207Pb204Pb).");
        BUILTIN_EXPRESSION_NOTES.put(DEFRAD_208PB204PB,
                "Sample spots: Default (radiogenic 208Pb)/204Pb, defined as (Total_208Pb204Pb) - (DefCom_208Pb204Pb).");

        BUILTIN_EXPRESSION_NOTES.put(RAD_206PB204PB_FACTOR,
                "Sample spots: Radiogenic 206Pb/204Pb proportionality factor, defined as (DefRad_206Pb204Pb)/(Total_206Pb204Pb).");
        BUILTIN_EXPRESSION_NOTES.put(RAD_208PB204PB_FACTOR,
                "Sample spots: Radiogenic 208Pb/204Pb proportionality factor, defined as (DefRad_208Pb204Pb)/(Total_208Pb204Pb).");

        BUILTIN_EXPRESSION_NOTES.put(TH_CONCEN_PPM,
                "Sample spots: Calculated Th content.");

        BUILTIN_EXPRESSION_NOTES.put(TOTAL_206_204,
                "Sample spots: Measured 206Pb/204Pb.");
        BUILTIN_EXPRESSION_NOTES.put(TOTAL_206_238,
                "Sample spots: Calculated total 206Pb/238U. Always depends on the index isotope (204Pb, 207Pb, "
                        + "or in the case of U-Pb Tasks that calculate only a single 206Pb/238U calibration "
                        + "[\"Perm1\"], possibly 208Pb)  chosen for common Pb correction of the RM.");
        BUILTIN_EXPRESSION_NOTES.put(TOTAL_207_204,
                "Sample spots: Measured 207Pb/204Pb.");
        BUILTIN_EXPRESSION_NOTES.put(TOTAL_207_206,
                "Sample spots: Measured 207Pb/206Pb.");
        BUILTIN_EXPRESSION_NOTES.put(TOTAL_208_204,
                "Sample spots: Measured 208Pb/204Pb.");
        BUILTIN_EXPRESSION_NOTES.put(TOTAL_208_232,
                "Sample spots: Calculated total 208Pb/232Th. Always depends on the index isotope (204Pb, 207Pb, "
                        + "or in the case of U-Pb Tasks that calculate only a single 206Pb/238U calibration "
                        + "[\"Perm1\"], possibly 208Pb)  chosen for common Pb correction of the RM.");
        BUILTIN_EXPRESSION_NOTES.put(TOTAL_238_206,
                "Sample spots: Calculated total 238U/206Pb. Always depends on the index isotope (204Pb, 207Pb, "
                        + "or in the case of U-Pb Tasks that calculate only a single 206Pb/238U calibration "
                        + "[\"Perm1\"], possibly 208Pb)  chosen for common Pb correction of the RM.");

        BUILTIN_EXPRESSION_NOTES.put(U_CONCEN_PPM,
                "Sample spots: Calculated U content.");

        // aliased RM expressions start here
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + OVER_COUNTS_PERSEC_4_8,
                "RM spots: Overcounts per second at mass 204, based on counts at 208Pb. "
                        + " In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\"), "
                        + " this value depends on the index isotope (204Pb or 207Pb) chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + OVER_COUNT_4_6_8,
                "RM spots: 204Pb/206Pb corrected for overcounts at mass 204, based on counts at 208Pb. "
                        + " In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\"), this value depends "
                        + " on the index isotope (204Pb or 207Pb) chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + TH_U_EXP_RM,
                "RM spots: Calculated 232Th/238U. In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\"), "
                        + " this value depends on the index isotope (204Pb or 207Pb) chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + CORR_8_PRIMARY_CALIB_CONST_DELTA_PCT,
                "RM spots: Offset (expressed as a percentage) of the primary calibration constant, "
                        + " based on overcounts at mass 204 as calculated from counts at 208Pb. "
                        + " In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\"), this value depends "
                        + " on the index isotope (204Pb or 207Pb) chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + TH_CONCEN_PPM_RM,
                "RM spots: Calculated Th content. In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\") "
                        + " where the CM is defined in terms of its U content, this value depends on the "
                        + " index isotope (204Pb or 207Pb) chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + TOTAL_206_238_RM,
                "RM spots: Calculated total 206Pb/238U. Always depends on the index isotope (204Pb, 207Pb, "
                        + "or in the case of U-Pb Tasks that calculate only a single 206Pb/238U calibration "
                        + "[\"Perm1\"], possibly 208Pb)  chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + TOTAL_208_232_RM,
                "RM spots: Calculated total 208Pb/232Th. Always depends on the index isotope (204Pb, 207Pb, "
                        + "or in the case of U-Pb Tasks that calculate only a single 206Pb/238U calibration "
                        + "[\"Perm1\"], possibly 208Pb)  chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + U_CONCEN_PPM_RM,
                "RM spots: Calculated U content. In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\") "
                        + " where the CM is defined in terms of its Th content, this value depends "
                        + " on the index isotope (204Pb or 207Pb) chosen for the common Pb correction.");

        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + OVER_COUNTS_PERSEC_4_8,
                "RM spots: Overcounts per second at mass 204, based on counts at 208Pb. In dual-calibration "
                        + " Tasks (i.e. \"Perm2\" and \"Perm4\"), this value depends on the index "
                        + " isotope (204Pb or 207Pb) chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + OVER_COUNT_4_6_8,
                "RM spots: 204Pb/206Pb corrected for overcounts at mass 204, based on counts at 208Pb. "
                        + " In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\"), this value depends "
                        + " on the index isotope (204Pb or 207Pb) chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + TH_U_EXP_RM,
                "RM spots: Calculated 232Th/238U. In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\"), "
                        + " this value depends on the index isotope (204Pb or 207Pb) chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + CORR_8_PRIMARY_CALIB_CONST_DELTA_PCT,
                "RM spots: Offset (expressed as a percentage) of the primary calibration constant, "
                        + " based on overcounts at mass 204 as calculated from counts at 208Pb. "
                        + " In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\"), "
                        + " this value depends on the index isotope (204Pb or 207Pb) chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + TH_CONCEN_PPM_RM,
                "RM spots: Calculated Th content. In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\") "
                        + " where the CM is defined in terms of its U content, this value depends on the "
                        + " index isotope (204Pb or 207Pb) chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + TOTAL_206_238_RM,
                "RM spots: Calculated total 206Pb/238U. Always depends on the index isotope (204Pb, 207Pb, "
                        + "or in the case of U-Pb Tasks that calculate only a single 206Pb/238U calibration "
                        + "[\"Perm1\"], possibly 208Pb)  chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + TOTAL_208_232_RM,
                "RM spots: Calculated total 208Pb/232Th. Always depends on the index isotope (204Pb, 207Pb, "
                        + "or in the case of U-Pb Tasks that calculate only a single 206Pb/238U calibration "
                        + "[\"Perm1\"], possibly 208Pb)  chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + U_CONCEN_PPM_RM,
                "RM spots: Calculated U content. In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\") "
                        + " where the CM is defined in terms of its Th content, this value depends on the "
                        + " index isotope (204Pb or 207Pb) chosen for the common Pb correction.");

        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + TOTAL_206_238_RM,
                "RM spots: Calculated total 206Pb/238U. Always depends on the index isotope (204Pb, 207Pb, "
                        + "or in the case of U-Pb Tasks that calculate only a single 206Pb/238U calibration "
                        + "[\"Perm1\"], possibly 208Pb)  chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + TOTAL_208_232_RM,
                "RM spots: Calculated total 208Pb/232Th. Always depends on the index isotope (204Pb, 207Pb, "
                        + "or in the case of U-Pb Tasks that calculate only a single 206Pb/238U calibration "
                        + "[\"Perm1\"], possibly 208Pb)  chosen for the common Pb correction.");

        // aliased UNKNOWN expressions start here
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + TH_U_EXP,
                "Sample spots: Calculated 232Th/238U. In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\"), "
                        + "this value depends on the index isotope (204Pb or 207Pb) chosen for common Pb correction of the RM.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + TOTAL_206_238,
                "Sample spots: Calculated total 206Pb/238U. Always depends on the index isotope (204Pb, 207Pb, "
                        + "or in the case of U-Pb Tasks that calculate only a single 206Pb/238U calibration "
                        + "[\"Perm1\"], possibly 208Pb)  chosen for common Pb correction of the RM.");
        BUILTIN_EXPRESSION_NOTES.put(PB4CORR + TOTAL_208_232,
                "Sample spots: Calculated total 208Pb/232Th. Always depends on the index isotope (204Pb, 207Pb, "
                        + "or in the case of U-Pb Tasks that calculate only a single 206Pb/238U calibration "
                        + "[\"Perm1\"], possibly 208Pb)  chosen for common Pb correction of the RM.");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + TH_U_EXP,
                "Sample spots: Calculated 232Th/238U. In dual-calibration Tasks (i.e. \"Perm2\" and \"Perm4\"), "
                        + "this value depends on the index isotope (204Pb or 207Pb) chosen for common Pb correction of the RM.");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + TOTAL_206_238,
                "Sample spots: Calculated total 206Pb/238U. Always depends on the index isotope (204Pb, 207Pb, "
                        + "or in the case of U-Pb Tasks that calculate only a single 206Pb/238U calibration "
                        + "[\"Perm1\"], possibly 208Pb)  chosen for common Pb correction of the RM.");
        BUILTIN_EXPRESSION_NOTES.put(PB7CORR + TOTAL_208_232,
                "Sample spots: Calculated total 208Pb/232Th. Always depends on the index isotope (204Pb, 207Pb, "
                        + "or in the case of U-Pb Tasks that calculate only a single 206Pb/238U calibration "
                        + "[\"Perm1\"], possibly 208Pb)  chosen for common Pb correction of the RM.");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + TOTAL_206_238,
                "Sample spots: Calculated total 206Pb/238U. Always depends on the index isotope "
                        + " (204Pb, 207Pb, or in the case of \"Perm1\", possibly 208Pb)  chosen for the common Pb correction.");
        BUILTIN_EXPRESSION_NOTES.put(PB8CORR + TOTAL_208_232,
                "Sample spots: Calculated total 208Pb/232Th. Always depends on the index isotope "
                        + " (204Pb, 207Pb, or in the case of \"Perm1\", possibly 208Pb)  chosen for the common Pb correction.");

        BUILTIN_EXPRESSION_NOTES.put(MIN_206PB238U_EXT_1SIGMA_ERR_PCT,
                "User-defined minimum value for external (spot-to-spot) 1sigma uncertainty (expressed as a percentage), "
                        + "intended to supersede any smaller value of WtdAv_Xcor_206Pb238U_CalibConst[2] (i.e. the third"
                        + " element of the WtdAv vector output) calculated from any RM dataset of "
                        + "WtdAv_Xcor_206Pb238U_CalibConst values. In this context, Xcor denotes the "
                        + "index isotope used for the common Pb correction in the RM (i.e. 204Pb, 207Pb, "
                        + "or in the case of U-Pb Tasks that calculate only a single 206Pb/238U calibration "
                        + "(i.e. \"Perm1\"), possibly 208Pb).");

        BUILTIN_EXPRESSION_NOTES.put(MIN_208PB232TH_EXT_1SIGMA_ERR_PCT,
                "User-defined minimum value for external (spot-to-spot) 1sigma uncertainty (expressed as a percentage),"
                        + " intended to supersede any smaller value of WtdAv_Xcor_208Pb232Th_CalibConst[2]"
                        + " (i.e. the third element of the WtdAv vector output) calculated from "
                        + "any RM dataset of WtdAv_Xcor_208Pb232Th_CalibConst values. In this context, "
                        + "Xcor denotes the index isotope used for the common Pb correction in the RM (i.e. 204Pb or 207Pb).");
    }
}