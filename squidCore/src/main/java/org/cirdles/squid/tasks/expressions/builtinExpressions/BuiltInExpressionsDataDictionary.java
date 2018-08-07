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

    public static final String EXP_8CORR_238_206_STAR = "8-corr 238/206*";

    // names for Squid2.5 Primary (-1) and Secondary (-2) are interchangeable based on U or Th in Primary
    public static final String SQUID_PRIMARY_UTH_EQN_NAME_U = "UncorrPb/Uconst";
    public static final String SQUID_PRIMARY_UTH_EQN_NAME_TH = "UncorrPb/Thconst";
    // Squid2.5 Th/U equation (-3)
    public static final String SQUID_TH_U_EQN_NAME = "232Th/238U";
    public static final String SQUID_TH_U_EQN_NAME_S = "232Th/238US";
    // name for Squid2.5 Ppm parent eqn(-4) 
    public static final String SQUID_PPM_PARENT_EQN_NAME = "Ppm Parent Eqn";
    public static final String SQUID_MEAN_PPM_PARENT_NAME = "pdMeanParentEleA";
    // name for Squid2.5 Ppm chosen based on U or Th in Primary; then other is calculated
    public static final String SQUID_PPM_PARENT_EQN_NAME_U = "ppmU";
    public static final String SQUID_PPM_PARENT_EQN_NAME_TH = "ppmTh";
    public static final String SQUID_PPM_PARENT_EQN_NAME_TH_S = "ppmThS";

    public static final String SQUID_TOTAL_206_238_NAME = "Total 206Pb/238US";
    public static final String SQUID_TOTAL_208_232_NAME = "Total 208Pb/232ThS";

    public static final String SQUID_CALIB_CONST_AGE_206_238_BASENAME = "206Pb/238U";
    public static final String SQUID_CALIB_CONST_AGE_208_232_BASENAME = "208Pb/232Th";
    
    public static final String SQUID_ASSIGNED_PBU_EXTERNAL_ONE_SIGMA_PCT_ERR = "ExtPErr";
}
