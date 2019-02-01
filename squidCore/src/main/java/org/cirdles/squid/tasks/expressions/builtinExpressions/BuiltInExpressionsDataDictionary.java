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

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class BuiltInExpressionsDataDictionary {

    // **********************  SPECIAL CONSTANTS ******************************
    public static final String PRESENT_R238_235S_NAME = "r238_235s";

    // **********************  COMMON LEAD *************************************
    public static final String DEFCOM_64 = "DefCom_206Pb204Pb";
    public static final String DEFCOM_68 = "DefCom_206Pb208Pb";
    public static final String DEFCOM_74 = "DefCom_207Pb204Pb";
    public static final String DEFCOM_76 = "DefCom_207Pb206Pb";
    public static final String DEFCOM_84 = "DefCom_208Pb204Pb";   
    public static final String DEFCOM_86 = "DefCom_208Pb206Pb";
    

    public static final String REF_U_CONC_PPM = "Ref_U_Concen";
    public static final String REF_TH_CONC_PPM = "Ref_Th_Concen";
    public static final String REF_AGE_U_PB = "RefRad_206Pb238U_Age";
    public static final String REF_AGE_TH_PB = "RefRad_208Pb232Th_Age";
    public static final String REF_AGE_PB_PB = "RefRad_207Pb206Pb_Age";
    public static final String REF_U_PB_RATIO = "RefRad_206Pb238U";
    public static final String REF_TH_PB_RATIO = "RefRad_208Pb232Th";
    public static final String REF_7_6 = "RefRad_207Pb206Pb";
    public static final String REF_RAD_8_6_FACT = "RefRad_208Pb206Pb_Factor";

    // **********************  LAMBDAs *************************************
    public static final String LAMBDA230 = "Lambda230";
    public static final String LAMBDA232 = "Lambda232";
    public static final String LAMBDA234 = "Lambda234";
    public static final String LAMBDA235 = "Lambda235";
    public static final String LAMBDA238 = "Lambda238";

    // **********************  MISC *************************************
    public static final String L859 = "L859";
    public static final String L1033 = "L1033";

    public static final String R206_204B = "r206_204b";
    public static final String R207_204B = "r207_204b";
    public static final String R207_206B = "r207_206b";
    public static final String R208_204B = "r208_204b";
    public static final String R208_206B = "r208_206b";

    // ********************** COR PREFIX *********************************************
    public static final String COR_ = "cor_";
    public static final String PB4CORR = "4" + COR_;
    public static final String PB7CORR = "7" + COR_;
    public static final String PB8CORR = "8" + COR_;
    
    // ********************** COMMON LEAD *********************************************
    public static final String COM206PB_PCT = "Com206Pb_Pct";
    public static final String COM206PB_PCT_RM = "Com206Pb_Pct_RM";
    public static final String COM208PB_PCT = "Com208Pb_Pct";
    public static final String COM208PB_PCT_RM = "Com208Pb_Pct_RM";
    public static final String R208PB206PB = "208Pb206Pb";
    public static final String R208PB206PB_RM = "208Pb206Pb_RM";

    // ********************** AGES *********************************************
    public static final String PB4COR206_238AGE = PB4CORR + "206Pb/238U_Age";
    public static final String PB4COR208_232AGE = PB4CORR + "208Pb/232Th_Age";

    // names for Squid2.5 Primary (-1) and Secondary (-2) are interchangeable based on U or Th in Primary
    public static final String UNCOR206PB238U_CALIB_CONST = "Uncor_206Pb238U_CalibConst";
    public static final String UNCOR208PB232TH_CALIB_CONST = "Uncor_208Pb232Th_CalibConst";
    // Squid2.5 Th/U equation (-3)
    public static final String SQUID_TH_U_EQN_NAME = "232Th/238U";
    public static final String SQUID_TH_U_EQN_NAME_S = "232Th/238US";
    // name for Squid2.5 Ppm parent eqn(-4) 
    public static final String PARENT_ELEMENT_CONC_CONST = "ParentElement_ConcenConst";
    public static final String AV_PARENT_ELEMENT_CONC_CONST = "Av_ParentElement_ConcenConst";
    // name for Squid2.5 Ppm chosen based on U or Th in Primary; then other is calculated
    public static final String U_CONCEN_PPM = "U_Concen";
    public static final String TH_CONCEN_PPM_RM = "Th_Concen_RM";
    public static final String TH_CONCEN_PPM = "Th_Concen";

    public static final String SQUID_TOTAL_206_238_NAME = "Total 206Pb/238U";
    public static final String SQUID_TOTAL_208_232_NAME = "Total 208Pb/232Th";

    public static final String SQUID_TOTAL_206_238_NAME_S = "Total 206Pb/238US";
    public static final String SQUID_TOTAL_208_232_NAME_S = "Total 208Pb/232ThS";

    public static final String SQUID_CALIB_CONST_AGE_206_238_BASENAME = "206Pb/238U";
    public static final String SQUID_CALIB_CONST_AGE_208_232_BASENAME = "208Pb/232Th";

    public static final String SQUID_ASSIGNED_PBU_EXTERNAL_ONE_SIGMA_PCT_ERR = "ExtPErr";

    // ********************** OVER COUNTS **************************************
    public static final String OVER_COUNT_4_6_8 = "204/206 (fr. 208)";
    public static final String OVER_COUNTS_PERSEC_4_8 = "204OvCts_From208Pb";
    public static final String OVER_COUNTS_PERSEC_4_7 = "204OvCts_From207Pb";
    
    public static final String CORR_8_PRIMARY_CALIB_CONST_PCT_DELTA = "8-corr Primary calib const. delta%";
    public static final String EXP_8CORR_238_206_STAR = "8-corr 238/206*";
    

}
