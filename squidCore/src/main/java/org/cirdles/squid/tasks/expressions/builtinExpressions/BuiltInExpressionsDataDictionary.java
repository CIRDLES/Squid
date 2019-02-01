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
    public static final String SCOMM_64_NAME = "sComm_64";
    public static final String SCOMM_74_NAME = "sComm_74";
    public static final String SCOMM_84_NAME = "sComm_84";
    public static final String SCOMM_76_NAME = "sComm_76";
    public static final String SCOMM_86_NAME = "sComm_86";
    public static final String SCOMM_68_NAME = "sComm_68";

    public static final String STD_U_CONC_PPM = "StdUConcPpm"; // confirmed unique
    public static final String STD_TH_CONC_PPM = "StdThConcPpm";
    public static final String STD_AGE_U_PB = "StdAgeUPb";
    public static final String STD_AGE_TH_PB = "StdAgeThPb";
    public static final String STD_AGE_PB_PB = "StdAgePbPb";
    public static final String STD_U_PB_RATIO = "StdUPbRatio";
    public static final String STD_TH_PB_RATIO = "StdThPbRatio";
    public static final String STD_7_6 = "Std_76";
    public static final String STD_RAD_8_6_FACT = "StdRad86fact";

    // **********************  LAMBDAs *************************************
    public static final String LAMBDA230 = "lambda230";
    public static final String LAMBDA232 = "lambda232";
    public static final String LAMBDA234 = "lambda234";
    public static final String LAMBDA235 = "lambda235";
    public static final String LAMBDA238 = "lambda238";

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
    public static final String SQUID_PRIMARY_UTH_EQN_NAME_U = "Uncorr 206Pb/238U Calib Const";
    public static final String SQUID_PRIMARY_UTH_EQN_NAME_TH = "UncorrPb/Thconst";
    // Squid2.5 Th/U equation (-3)
    public static final String SQUID_TH_U_EQN_NAME = "232Th/238U";
    public static final String SQUID_TH_U_EQN_NAME_S = "232Th/238US";
    // name for Squid2.5 Ppm parent eqn(-4) 
    public static final String SQUID_PPM_PARENT_EQN_NAME = "Ppm Parent Eqn";
    public static final String AV_PARENT_ELEMENT_CONC_CONST = "Av_ParentElement_ConcenConst";
    // name for Squid2.5 Ppm chosen based on U or Th in Primary; then other is calculated
    public static final String SQUID_PPM_PARENT_EQN_NAME_U = "ppmU";
    public static final String SQUID_PPM_PARENT_EQN_NAME_TH = "ppmTh";
    public static final String SQUID_PPM_PARENT_EQN_NAME_TH_S = "ppmThS";

    public static final String SQUID_TOTAL_206_238_NAME = "Total 206Pb/238U";
    public static final String SQUID_TOTAL_208_232_NAME = "Total 208Pb/232Th";

    public static final String SQUID_TOTAL_206_238_NAME_S = "Total 206Pb/238US";
    public static final String SQUID_TOTAL_208_232_NAME_S = "Total 208Pb/232ThS";

    public static final String SQUID_CALIB_CONST_AGE_206_238_BASENAME = "206Pb/238U";
    public static final String SQUID_CALIB_CONST_AGE_208_232_BASENAME = "208Pb/232Th";

    public static final String SQUID_ASSIGNED_PBU_EXTERNAL_ONE_SIGMA_PCT_ERR = "ExtPErr";

    public static final String OVER_COUNT_4_6_8 = "204/206 (fr. 208)";
    public static final String OVER_COUNTS_PERSEC_4_8 = "204 overcts/sec (fr. 208)";
    public static final String CORR_8_PRIMARY_CALIB_CONST_PCT_DELTA = "8-corr Primary calib const. delta%";
    public static final String EXP_8CORR_238_206_STAR = "8-corr 238/206*";

}
