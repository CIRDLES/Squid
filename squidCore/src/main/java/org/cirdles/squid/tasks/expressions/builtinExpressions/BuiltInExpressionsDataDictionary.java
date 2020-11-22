/*
 * Copyright 2018 James F. Bowring and CIRDLES.org.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class BuiltInExpressionsDataDictionary {

    // **********************  REQUIRED   **************************************
    public static final String DEFAULT_BACKGROUND_MASS_LABEL = "BKG";

    public static final List<String> REQUIRED_NOMINAL_MASSES
            = new ArrayList<>(Arrays.asList(new String[]{"204", "206", "207", "208"}));
    public static final List<String> REQUIRED_RATIO_NAMES
            = new ArrayList<>(Arrays.asList(new String[]{"204/206", "207/206", "208/206"}));

    // **********************  SPECIAL CONSTANTS ******************************
    // holding spot until models are implemented
    public static final String REF_238U235U_RM_MODEL_NAME = "r238_235s";
    public static final String REF_238U235U = "Ref_238U235U";

    public static final String NUKEMASS206PB = "NukeMass206Pb";
    public static final String NUKEMASS232TH = "NukeMass232Th";
    public static final String NUKEMASS238U = "NukeMass238U";

    // **********************  COMMON LEAD *************************************
    public static final String DEFCOM_64 = "DefCom_206Pb204Pb";
    public static final String COM_64 = "Com_206Pb204Pb";
    public static final String DEFCOM_76 = "DefCom_207Pb206Pb";
    public static final String COM_76 = "Com_207Pb206Pb";
    public static final String DEFCOM_86 = "DefCom_208Pb206Pb";
    public static final String COM_86 = "Com_208Pb206Pb";
    
    public static final String DEFCOM_68 = "DefCom_206Pb208Pb";
    public static final String COM_68 = "Com_206Pb208Pb";
    public static final String DEFCOM_74 = "DefCom_207Pb204Pb";   
    public static final String COM_74 = "Com_207Pb204Pb";   
    public static final String DEFCOM_84 = "DefCom_208Pb204Pb";
    public static final String COM_84 = "Com_208Pb204Pb";
    

    public static final String REF_U_CONC_PPM = "Ref_U_Concen";
    public static final String REF_TH_CONC_PPM = "Ref_Th_Concen";
    public static final String REFRAD_AGE_U_PB = "RefRad_206Pb238U_Age";
    public static final String REFRAD_AGE_TH_PB = "RefRad_208Pb232Th_Age";
    public static final String REFRAD_AGE_PB_PB = "RefRad_207Pb206Pb_Age";
    public static final String REFRAD_U_PB_RATIO = "RefRad_206Pb238U";
    public static final String REFRAD_TH_PB_RATIO = "RefRad_208Pb232Th";
    public static final String REFRAD_7_6 = "RefRad_207Pb206Pb";
    public static final String REFRAD_8_6_FACT = "RefRad_208Pb206Pb_Factor";

    // **********************  LAMBDAs *************************************
    public static final String LAMBDA230 = "Lambda230";
    public static final String LAMBDA232 = "Lambda232";
    public static final String LAMBDA234 = "Lambda234";
    public static final String LAMBDA235 = "Lambda235";
    public static final String LAMBDA238 = "Lambda238";

    // **********************  MISC ********************************************
    public static final String L859 = "L859";
    public static final String L1033 = "L1033";
    public static final String L9678 = "L9678";

    // **********************  RATIOS USED IN BUILTINS *************************
    public static final String R204206 = "\"204/206\"";
    public static final String R207206 = "\"207/206\"";
    public static final String R208206 = "\"208/206\"";

    // **********************  RATIOS USED IN COMMON PB MODELS *****************
    public static final String R206_204B = "r206_204b";
    public static final String R207_204B = "r207_204b";
    public static final String R207_206B = "r207_206b";
    public static final String R208_204B = "r208_204b";
    public static final String R208_206B = "r208_206b";

    // ********************** COR PREFIX ***************************************
    public static final String COR_PREFIX = "cor_";
    public static final String PB4CORR = "4" + COR_PREFIX;
    public static final String PB7CORR = "7" + COR_PREFIX;
    public static final String PB8CORR = "8" + COR_PREFIX;

    // ********************** CONCENTRATIONS ***********************************
    public static final String CONCEN_206PB = "206Pb_Concen";
    public static final String CONCEN_208PB = "208Pb_Concen";

    // **********************  RATIOS FOR PB CORRECTION ************************
    public static final String R206PB_238U_RM = "206Pb238U_RM";
    public static final String R206PB_238U = "206Pb238U";

    public static final String R207PB_235U_RM = "207Pb235U_RM";
    public static final String R207PB_235U = "207Pb235U";

    public static final String R207PB_206PB_RM = "207Pb206Pb_RM";
    public static final String R207PB_206PB = "207Pb206Pb";

    public static final String R208PB_232TH = "208Pb232Th";

    public static final String R238U_206PB = "238U206Pb";

    public static final String R204PB_206PB = "204Pb206Pb";

    public static final String R208PB206PB_RM = "208Pb206Pb_RM";
    public static final String R208PB206PB = "208Pb206Pb";

    public static final List<String> CORRECTED_RATIOS_EXPRESSION_NAMES = new ArrayList<>();
    static {
        CORRECTED_RATIOS_EXPRESSION_NAMES.add(PB4CORR + R206PB_238U);
        CORRECTED_RATIOS_EXPRESSION_NAMES.add(PB4CORR + R207PB_206PB);
        CORRECTED_RATIOS_EXPRESSION_NAMES.add(PB4CORR + R207PB_235U);
        CORRECTED_RATIOS_EXPRESSION_NAMES.add(PB4CORR + R208PB206PB);
        CORRECTED_RATIOS_EXPRESSION_NAMES.add(PB4CORR + R208PB_232TH);
        CORRECTED_RATIOS_EXPRESSION_NAMES.add(PB4CORR + R238U_206PB);
        
//        CORRECTED_RATIOS_EXPRESSION_NAMES.add(PB7CORR + R204PB_206PB); - no unct calculated
        CORRECTED_RATIOS_EXPRESSION_NAMES.add(PB7CORR + R206PB_238U);
        CORRECTED_RATIOS_EXPRESSION_NAMES.add(PB7CORR + R208PB206PB);
        CORRECTED_RATIOS_EXPRESSION_NAMES.add(PB7CORR + R208PB_232TH);
        
//        CORRECTED_RATIOS_EXPRESSION_NAMES.add(PB8CORR + R204PB_206PB); - no unct calculated
        CORRECTED_RATIOS_EXPRESSION_NAMES.add(PB8CORR + R206PB_238U);
        CORRECTED_RATIOS_EXPRESSION_NAMES.add(PB8CORR + R207PB_206PB);
        CORRECTED_RATIOS_EXPRESSION_NAMES.add(PB8CORR + R207PB_235U);
        CORRECTED_RATIOS_EXPRESSION_NAMES.add(PB8CORR + R238U_206PB);
        
    }
    // ********************** COMMON LEAD **************************************   
    public static final String COM206PB_PCT_RM = "Com206Pb_Pct_RM";
    public static final String COM206PB_PCT = "Com206Pb_Pct";

    public static final String COM208PB_PCT_RM = "Com208Pb_Pct_RM";
    public static final String COM208PB_PCT = "Com208Pb_Pct";

    // ********************** AGES *********************************************
    public static final String PB4COR206_238AGE_RM = PB4CORR + "206Pb238U_Age_RM";
    public static final String PB4COR206_238AGE = PB4CORR + "206Pb238U_Age";
    public static final String PB7COR206_238AGE_RM = PB7CORR + "206Pb238U_Age_RM";
    public static final String PB7COR206_238AGE = PB7CORR + "206Pb238U_Age";
    public static final String PB8COR206_238AGE_RM = PB8CORR + "206Pb238U_Age_RM";
    public static final String PB8COR206_238AGE = PB8CORR + "206Pb238U_Age";

    public static final String PB4COR208_232AGE_RM = PB4CORR + "208Pb232Th_Age_RM";
    public static final String PB4COR208_232AGE = PB4CORR + "208Pb232Th_Age";
    public static final String PB7COR208_232AGE_RM = PB7CORR + "208Pb232Th_Age_RM";
    public static final String PB7COR208_232AGE = PB7CORR + "208Pb232Th_Age";

    public static final String PB4COR207_206AGE_RM = PB4CORR + "207Pb206Pb_Age_RM";
    public static final String PB4COR207_206AGE = PB4CORR + "207Pb206Pb_Age";
    public static final String PB8COR207_206AGE_RM = PB8CORR + "207Pb206Pb_Age_RM";
    public static final String PB8COR207_206AGE = PB8CORR + "207Pb206Pb_Age";

    public static final String PB4COR_DISCORDANCE = PB4CORR + "Discord_Pct";
    public static final String PB8COR_DISCORDANCE = PB8CORR + "Discord_Pct";

    // ************************* ERROR CORRELATIONS ****************************  
    public static final String ERR_CORREL_RM = "ErrCorrel_RM";
    public static final String ERR_CORREL = "ErrCorrel";
    public static final String ERR_CORREL_TERA_WASSERBURG = "ZERO";

    // ************************* CALIBRATION CONSTANTS *************************   
    public static final String CALIB_CONST_206_238_ROOT = "206Pb238U";
    public static final String CALIB_CONST_208_232_ROOT = "208Pb232Th";
    public static final String WTDAV_PREFIX = "WtdAv_";

    private static final String R206_238CALIB_CONST = CALIB_CONST_206_238_ROOT + "_CalibConst";
    public static final String PB4COR206_238CALIB_CONST = PB4CORR + R206_238CALIB_CONST;
    public static final String PB7COR206_238CALIB_CONST = PB7CORR + R206_238CALIB_CONST;
    public static final String PB8COR206_238CALIB_CONST = PB8CORR + R206_238CALIB_CONST;

    public static final String PB4COR206_238CALIB_CONST_WM = WTDAV_PREFIX + PB4COR206_238CALIB_CONST;
    public static final String PB7COR206_238CALIB_CONST_WM = WTDAV_PREFIX + PB7COR206_238CALIB_CONST;
    public static final String PB8COR206_238CALIB_CONST_WM = WTDAV_PREFIX + PB8COR206_238CALIB_CONST;

    private static final String R208_232CALIB_CONST = CALIB_CONST_208_232_ROOT + "_CalibConst";
    public static final String PB4COR208_232CALIB_CONST = PB4CORR + R208_232CALIB_CONST;
    public static final String PB7COR208_232CALIB_CONST = PB7CORR + R208_232CALIB_CONST;

    public static final String PB4COR208_232CALIB_CONST_WM = WTDAV_PREFIX + PB4COR208_232CALIB_CONST;
    public static final String PB7COR208_232CALIB_CONST_WM = WTDAV_PREFIX + PB7COR208_232CALIB_CONST;

    public static final String TOTAL_206_238_RM = "Total_206Pb238U_RM";
    public static final String TOTAL_208_232_RM = "Total_208Pb232Th_RM";

    public static final String PBTH_EXT_1_SIGMA_ERR_PCT = "208Pb232Th_Ext1SigmaErr_Pct";
    public static final String PBU_EXT_1_SIGMA_ERR_PCT = "206Pb238U_Ext1SigmaErr_Pct";
    public static final String MIN_206PB238U_EXT_1SIGMA_ERR_PCT = "Min_206Pb238U_Ext1SigmaErr_Pct";
    public static final String MIN_208PB232TH_EXT_1SIGMA_ERR_PCT = "Min_208Pb232Th_Ext1SigmaErr_Pct";

    // ********************** OVER COUNTS **************************************
    public static final String OVER_COUNT_4_6_7 = "204Pb206Pb_OvCtCorFrom207Pb";
    public static final String OVER_COUNT_4_6_8 = "204Pb206Pb_OvCtCorFrom208Pb";
    public static final String OVER_COUNTS_PERSEC_4_8 = "204OvCts_From208Pb";
    public static final String OVER_COUNTS_PERSEC_4_7 = "204OvCts_From207Pb";

    public static final String BIWT_PRE = "BiWt_";

    public static final String BIWT_4COR_207_206_AGE = BIWT_PRE + PB4CORR + "207Pb206Pb_Age";

    public static final String CORR_7_PRIMARY_CALIB_CONST_DELTA_PCT = "7cor_PrimaryCalibConstDelta_Pct";
    public static final String BIWT_7COR_PRIMARY_CALIB_CONST_DELTA_PCT = BIWT_PRE + CORR_7_PRIMARY_CALIB_CONST_DELTA_PCT;
    public static final String BIWT_204_OVR_CTS_FROM_207 = BIWT_PRE + "204OvCts_From207Pb";

    public static final String CORR_8_PRIMARY_CALIB_CONST_DELTA_PCT = "8cor_PrimaryCalibConstDelta_Pct";
    public static final String BIWT_8COR_PRIMARY_CALIB_CONST_DELTA_PCT = BIWT_PRE + CORR_8_PRIMARY_CALIB_CONST_DELTA_PCT;
    public static final String BIWT_204_OVR_CTS_FROM_208 = BIWT_PRE + "204OvCts_From208Pb";

    // ********************** INTERNAL EXPRESSIONS *****************************
    // names for Squid2.5 Primary (-1) and Secondary (-2) are interchangeable based on U or Th in Primary
    public static final String UNCOR206PB238U_CALIB_CONST = "Uncor_206Pb238U_CalibConst";
    public static final String UNCOR206PB238U_CALIB_CONST_DEFAULT_EXPRESSION = "[\"206/238\"]/[\"254/238\"]^1.8";//Expo_Used";

    public static final String UNCOR208PB232TH_CALIB_CONST = "Uncor_208Pb232Th_CalibConst";
    public static final String UNCOR208PB232TH_CALIB_CONST_DEFAULT_EXPRESSION = "[\"208/248\"]";

    // Squid2.5 Th/U equation (-3)
    // TH_U_EXP is overwritten for perm2 and perm4
    public static final String TH_U_EXP_DEFAULT = "232Th238U_DEFAULT";
    public static final String TH_U_EXP_DEFAULT_EXPRESSION = "(0.03446*[\"254/238\"]+0.868)*[\"248/254\"]";
    public static final String TH_U_EXP_RM = "232Th238U_RM";
    public static final String TH_U_EXP = "232Th238U";

    // name for Squid2.5 Ppm parent eqn(-4) 
    public static final String PARENT_ELEMENT_CONC_CONST = "ParentElement_ConcenConst";
    public static final String PARENT_ELEMENT_CONC_CONST_DEFAULT_EXPRESSION = "[\"238/196\"]/[\"254/238\"]^0.66";

    public static final String AV_PARENT_ELEMENT_CONC_CONST = "Av_ParentElement_ConcenConst";
    public static final String AV_PARENT_ELEMENT_CONC_CONST_DEFAULT_EXPRESSION 
            = "CalculateMeanConcStd([\"" + PARENT_ELEMENT_CONC_CONST + "\"])";

    // name for Squid2.5 Ppm chosen based on U or Th in Primary; then other is calculated
    public static final String U_CONCEN_PPM_RM = "U_Concen_RM";
    public static final String U_CONCEN_PPM = "U_Concen";
    public static final String TH_CONCEN_PPM_RM = "Th_Concen_RM";
    public static final String TH_CONCEN_PPM = "Th_Concen";

    public static final String TOTAL_206_204 = "Total_206Pb204Pb";
    public static final String TOTAL_207_204 = "Total_207Pb204Pb";
    public static final String TOTAL_208_204 = "Total_208Pb204Pb";
    public static final String TOTAL_206_238 = "Total_206Pb238U";
    public static final String TOTAL_208_232 = "Total_208Pb232Th";
    public static final String TOTAL_207_206 = "Total_207Pb206Pb";
    public static final String TOTAL_238_206 = "Total_238U206Pb";

    public static final String DEFRAD_206PB204PB = "DefRad_206Pb204Pb";
    public static final String DEFRAD_207PB204PB = "DefRad_207Pb204Pb";
    public static final String DEFRAD_208PB204PB = "DefRad_208Pb204Pb";
    public static final String RAD_206PB204PB_FACTOR = "Rad_206Pb204Pb_Factor";
    public static final String RAD_208PB204PB_FACTOR = "Rad_208Pb204Pb_Factor";

    public static final List<String> COMPOSITION_EXPRESSION_NAMES = new ArrayList<>();
    static {
        // no uncertainties for ppm?
        COMPOSITION_EXPRESSION_NAMES.add(U_CONCEN_PPM);
        COMPOSITION_EXPRESSION_NAMES.add(TH_CONCEN_PPM);
        COMPOSITION_EXPRESSION_NAMES.add(TH_U_EXP);
    }
}
