/*
 * ReportSpecificationsUPb.java
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.cirdles.squid.reports.reportSpecifications;

import java.util.HashMap;
import java.util.Map;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_PPM_PARENT_EQN_NAME_TH;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_PPM_PARENT_EQN_NAME_U;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_TH_U_EQN_NAME;

/**
 *
 * @author James F. Bowring
 */
public class ReportSpecificationsUPb extends ReportSpecificationsAbstract {

    // Report column order =
    //  displayName1, displayName2, displayName3, displayName4, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_CorrectionIndependentData = new String[][]{
        {"", "", "U", "(ppm)", "", "getTaskExpressionsEvaluationsPerSpotByField", SQUID_PPM_PARENT_EQN_NAME_U, "",
            "", "true", "false", "6", "", "concentration of U", "false", "false"
        },
        {"", "", "Th", "(ppm)", "", "getTaskExpressionsEvaluationsPerSpotByField", SQUID_PPM_PARENT_EQN_NAME_TH, "",
            "", "true", "false", "6", "", "concentration of Th", "true", "false"
        },
        {"", "", "232Th", "/238U", "", "getTaskExpressionsEvaluationsPerSpotByField", SQUID_TH_U_EQN_NAME, "",
            "", "true", "false", "3", "true", "232/38 ratio", "false", "false"
        },
        {"", "", "204Pb", "/206Pb", "", "getIsotopicRatioValuesByStringName", "204/206", "",
            "", "true", "false", "3", "true", "204/206 measured ratio", "false", "false"
        },};

    // Report column order =
    //  displayName1, displayName2, displayName3, displayName4, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_204PbCorrected = new String[][]{
        {"204cor", "Common", "206Pb", "(%)", "", "getTaskExpressionsEvaluationsPerSpotByField", "4-corr%com206", "",
            "", "true", "false", "6", "", "", "false", "false"
        },
        {"204cor", "206Pb", "/238U", "Age", "Ma", "getTaskExpressionsEvaluationsPerSpotByField", "4-corr206Pb/238U Age", "",
            "", "true", "false", "6", "", "", "false", "false"
        },};

    // Report column order =
    //  displayName1, displayName2, displayName3, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_PbcCorrIsotopicRatios = new String[][]{
        //        {"207Pb/", "206Pb", "PbcCorr", "", "getRadiogenicIsotopeRatioByName", RadRatiosPbcCorrected.r207_206_PbcCorr.getName(), "PCT",
        //            "", "true", "false", "2", "true", "", "true", "false"
        //        },
        //        {"206Pb/", "238U", "PbcCorr", "", "getRadiogenicIsotopeRatioByName", RadRatiosPbcCorrected.r206_238_PbcCorr.getName(), "PCT",
        //            "", "true", "false", "2", "true", "", "true", "false"
        //        },
        //        {"208Pb/", "232U", "PbcCorr", "", "getRadiogenicIsotopeRatioByName", RadRatiosPbcCorrected.r208_232_PbcCorr.getName(), "PCT",
        //            "", "true", "false", "2", "true", "", "true", "false"
        //        },
        //        {"207Pb/", "235U", "PbcCorr", "", "getRadiogenicIsotopeRatioByName", RadRatiosPbcCorrected.r207_235_PbcCorr.getName(), "PCT",
        //            "", "true", "false", "2", "true", "", "true", "false"
        //        },
        {"", "Corr.", "coef.", "", "getRadiogenicIsotopeRatioByName", "rhoR206_238PbcCorr__r207_235PbcCorr", "",
            "", "false", "true", "3", "", "Correlation coefficient", "true", "true"
        }
    };

    // Report column order =
    //  displayName1, displayName2, displayName3, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_Dates = new String[][]{ //        {"", "206Pb/", "238U", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age206_238r.getName(), "ABS",
    //            "FN-7", "true", "false", "2", "true", "", "true", "true"
    //        },
    //        {"206Pb/", "238U", "<Th>", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age206_238r_Th.getName(), "ABS",
    //            "FN-12", "false", "false", "2", "true", "206Pb/238U (Th\u2212corrected)", "true", "true"
    //        },
    //        {"", "207Pb/", "235U", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age207_235r.getName(), "ABS",
    //            "FN-7", "true", "false", "2", "true", "", "true", "true"
    //        },
    //        {"", "207Pb/", "206Pb", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age207_206r.getName(), "ABS",
    //            "FN-7", "true", "false", "2", "true", "", "true", "false"
    //        },
    //        {"", "Corr.", "coef.", "", "getRadiogenicIsotopeRatioByName", "rhoR206_238r__r207_235r", "",
    //            "", "true", "true", "3", "", "Correlation coefficient", "true", "true"
    //        },
    //        {"", "", "% disc", "", "getRadiogenicIsotopeDateByName", RadDates.percentDiscordance.getName(), "",
    //            "FN-8", "true", "true", "2", "", "percent discordance", "true", "true"
    //        },
    //        {"", "208Pb/", "232Th", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age208_232r.getName(), "ABS",
    //            "FN-9", "false", "false", "2", "true", "", "true", "true"
    //        },
    //        {"207Pb/", "206Pb", "<Th>", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age207_206r_Th.getName(), "ABS",
    //            "FN-12", "false", "false", "2", "true", "207Pb/206Pb (Th\u2212corrected)", "true", "true"
    //        },
    //        {"207Pb/", "235U", "<Pa>", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age207_235r_Pa.getName(), "ABS",
    //            "FN-13", "false", "false", "2", "true", "207Pb/235U (Pa\u2212corrected)", "true", "true"
    //        },
    //        {"207Pb/", "206Pb", "<Pa>", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age207_206r_Pa.getName(), "ABS",
    //            "FN-13", "false", "false", "2", "true", "207Pb/206Pb (Pa\u2212corrected)", "true", "false"
    //        },
    //        {"207Pb/", "206Pb", "<ThPa>", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age207_206r_ThPa.getName(), "ABS",
    //            "FN-12&FN-13", "false", "false", "2", "true", "207Pb/206Pb <Th\u2212 and Pa\u2212corrected>", "true", "true"
    //        },
    //        {"", "best", "date", "Ma", "getRadiogenicIsotopeDateByName", RadDates.bestAge.getName(), "ABS",
    //            "", "false", "false", "2", "true", "best date", "true", "true"
    };

    // Report column order =
    //  displayName1, displayName2, displayName3, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_PbcCorrDates = new String[][]{ //        {"", "U-Pb Date", "Pbc-corr", "Ma", "getRadiogenicIsotopeDateByName", RadDates.PbcCorr_UPb_Date.getName(), "ABS",
    //            "", "true", "false", "2", "true", "U-Pb PbcCorr Date", "true", "true"
    //        },
    //        {"206Pb/", "238U", "PbcCorr", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age206_238_PbcCorr.getName(), "ABS",
    //            "", "true", "false", "2", "true", "", "true", "true"
    //        },
    //        {"208Pb/", "232Th", "PbcCorr", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age208_232_PbcCorr.getName(), "ABS",
    //            "", "true", "false", "2", "true", "", "true", "true"
    //        },
    //        {"207Pb/", "235U", "PbcCorr", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age207_235_PbcCorr.getName(), "ABS",
    //            "", "true", "false", "2", "true", "", "true", "true"
    //        },
    //        {"", "Corr.", "coef.", "", "getRadiogenicIsotopeRatioByName", "rhoR206_238PbcCorr__r207_235PbcCorr", "",
    //            "", "true", "true", "3", "", "Correlation coefficient", "true", "true"
    //        },
    //        {"207Pb/", "206Pb", "PbcCorr", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age207_206_PbcCorr.getName(), "ABS",
    //            "", "true", "false", "2", "true", "", "true", "false"
    //        },
    //        {"", "% disc", "PbcCorr", "", "getRadiogenicIsotopeDateByName", RadDates.percentDiscordance_PbcCorr.getName(), "",
    //            "FN-8", "true", "true", "2", "", "% discordance PbcCorr", "true", "true"
    //        },
    //        {"best", "date", "PbcCorr", "Ma", "getRadiogenicIsotopeDateByName", RadDates.bestAge_PbcCorr.getName(), "ABS",
    //            "", "false", "false", "2", "true", "best date Pbc Corr", "true", "true"
    //        }
    };

    // Report column order =
    //  displayName1, displayName2, displayName3, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_CorrelationCoefficients = new String[][]{
        {"206Pb/238U", "-", "207Pb/235U", "", "getRadiogenicIsotopeRatioByName", "rhoR206_238r__r207_235r", "",
            "", "false", "false", "2", "", "", "true", "true"
        },
        {"206Pb/238U", "207Pb/235U", "PbcCorr", "", "getRadiogenicIsotopeRatioByName", "rhoR206_238PbcCorr__r207_235PbcCorr", "",
            "", "false", "false", "2", "", "", "true", "true"
        },
        {"206Pb/238U", "<Th>-", "207Pb/235U", "", "getRadiogenicIsotopeRatioByName", "rhoR206_238r_Th__r207_235r", "",
            "", "false", "false", "2", "", "", "true", "true"
        },
        {"206Pb/238U", "-207Pb/", "235U <Pa>", "", "getRadiogenicIsotopeRatioByName", "rhoR206_238r__r207_235r_Pa", "",
            "", "false", "false", "2", "", "", "true", "true"
        },
        {"206Pb/238U", "<Th>-207Pb/", "235U <Pa>", "", "getRadiogenicIsotopeRatioByName", "rhoR206_238r_Th__r207_235r_Pa", "",
            "", "false", "false", "2", "", "", "true", "true"
        },
        {"207Pb/206Pb", "-", "238U/206Pb", "", "getRadiogenicIsotopeRatioByName", "rhoR207_206r__r238_206r", "",
            "", "false", "false", "2", "", "", "true", "true"
        },
        {"207Pb/206Pb", "238U/206Pb", "PbcCorr", "", "getRadiogenicIsotopeRatioByName", "rhoR207_206PbcCorr__r238_206PbcCorr", "",
            "", "false", "false", "2", "", "", "true", "true"
        },
        {"207Pb/206Pb", "<Pa>-", "238U/206Pb", "", "getRadiogenicIsotopeRatioByName", "rhoR207_206r_Pa__r238_206r", "",
            "", "false", "false", "2", "", "", "true", "true"
        },
        {"207Pb/206Pb", "<Th>-238U/", "207Pb <Th>", "", "getRadiogenicIsotopeRatioByName", "rhoR207_206r_Th__r238_206r_Th", "",
            "", "false", "false", "2", "", "", "true", "true"
        },
        {"207Pb/206Pb", "<ThPa>-238U/", "206Pb <Th>", "", "getRadiogenicIsotopeRatioByName", "rhoR207_206r_ThPa__r238_206r_Th", "",
            "", "false", "false", "2", "", "", "true", "true"
        }
    };

    /**
     *
     */
    public final static Map<String, String> reportTableFootnotes = new HashMap<String, String>();

    static {

        reportTableFootnotes.put(//
                "FN-1", //
                "Th contents calculated from radiogenic 208Pb and 230Th-corrected 206Pb/238U "// corrected text 25 April 2014 per McLean
                + "date of the sample, assuming concordance between U-Pb Th-Pb systems.");
        reportTableFootnotes.put(//
                "FN-2", //
                "Ratio of radiogenic Pb (including 208Pb) to common Pb.");
        reportTableFootnotes.put(//
                "FN-3", //
                "Total mass of radiogenic Pb.");
        reportTableFootnotes.put(//
                "FN-4", //
                "Total mass of common Pb.");
        reportTableFootnotes.put(//
                "FN-5", //
                "<zirconPopulationChoice>");
        reportTableFootnotes.put(//
                "FN-5noZircon", //
                "Measured ratios corrected for fractionation, tracer, blank and initial common Pb.");
        reportTableFootnotes.put(//
                "FN-5zircon", //
                "Measured ratios corrected for fractionation, tracer and blank.");
        reportTableFootnotes.put(//
                "FN-5mixed", //
                "Measured ratios corrected for fractionation, tracer, blank and, where applicable, initial common Pb.");
        reportTableFootnotes.put(//
                "FN-6", //
                "Measured ratio corrected for fractionation and spike contribution only.");
        reportTableFootnotes.put(//
                "FN-7", //
                "Isotopic dates calculated using <lambda238> and <lambda235>.");
        reportTableFootnotes.put(//
                "FN-8", //
                "% discordance = 100 - (100 * (206Pb/238U date) / (207Pb/206Pb date))");
        reportTableFootnotes.put(//
                "FN-9", //
                "Isotopic date calculated using <lambda232>");
        reportTableFootnotes.put(//
                "FN-10", //
                "Initial [231Pa]/[235U] activity ratio of mineral.");
        reportTableFootnotes.put(//
                "FN-11", //
                "Th/U ratio of magma from which mineral crystallized.");
        reportTableFootnotes.put(//
                "FN-12", //
                "Corrected for initial Th/U disequilibrium using radiogenic 208Pb "
                + "and Th/U[magma] <rTh_Umagma>.");
        reportTableFootnotes.put(//
                "FN-13", //
                "Corrected for initial Pa/U disequilibrium using "
                + "initial fraction activity ratio [231Pa]/[235U] <ar231_235sample>.");
        reportTableFootnotes.put(//
                "FN-14", //
                "Lower-intercept concordia date calculated by assuming that the "
                + "207Pb/206Pb ratio of the common Pb analyzed is <r207_206c>.");
        reportTableFootnotes.put(//
                "FN-15", //
                "Lower-intercept concordia date calculated by assuming the 207Pb/206Pb ratio of common Pb  "
                + "shares a Stacey-Kramers (1975) model date with the intercept date.");
        reportTableFootnotes.put(//
                "FN-16", //
                "Best Date threshold between 206Pb/238U and 206Pb/207Pb is <bestDateDivider> MA.");
        reportTableFootnotes.put(//
                "FN-17", //
                "Activity Ratios calculated using <lambda238>.");
        reportTableFootnotes.put(//
                "FN-18", //
                "Activity Ratios calculated using <lambda230>.");
        reportTableFootnotes.put(//
                "FN-19", //
                "Activity Ratios calculated using <lambda232>.");
        reportTableFootnotes.put(//
                "FN-20", //
                "Activity Ratios calculated using <lambda234>.");

    }
}
