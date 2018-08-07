/*
 * ReportSpecificationsUPbSamples.java
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
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_PPM_PARENT_EQN_NAME_TH;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_PPM_PARENT_EQN_NAME_U;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_TH_U_EQN_NAME;

/**
 * Modified from ET_Redux July 2018
 *
 * @author James F. Bowring
 */
public class ReportSpecificationsUPbReferenceMaterials extends ReportSpecificationsAbstract {


    // Report column order =
    //  displayName1, displayName2, displayName3, displayName4, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_CustomExpressions = new String[][]{
        {"", "", "", "", "", "getTaskExpressionsEvaluationsPerSpotByField", "<RM_EXPRESSIONS_ARRAY>", "",
            "", "", "", "", "", "", "", ""
        }
    };

    // Report column order =
    //  displayName1, displayName2, displayName3, displayName4, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_CorrectionIndependent = new String[][]{
        {"", "", "U", "(ppm)", "", "getTaskExpressionsEvaluationsPerSpotByField", SQUID_PPM_PARENT_EQN_NAME_U, "",
            "", "true", "false", "6", "", "concentration of U", "false", "false"
        },
        {"", "", "Th", "(ppm)", "", "getTaskExpressionsEvaluationsPerSpotByField", SQUID_PPM_PARENT_EQN_NAME_TH, "",
            "", "true", "false", "6", "", "concentration of Th", "false", "false"
        },
        {"", "", "232Th", "/238U", "", "getTaskExpressionsEvaluationsPerSpotByField", SQUID_TH_U_EQN_NAME, "PCT",
            "", "true", "false", "3", "true", "232/238 ratio", "false", "false"
        },
        {"204", "overcts", "/sec", "(fr. 207)", "", "getTaskExpressionsEvaluationsPerSpotByField", "204 overcts/sec (fr. 207)", "",
            "", "true", "false", "3", "", "", "false", "false"
        },
        {"204", "overcts", "/sec", "(fr. 208)", "", "getTaskExpressionsEvaluationsPerSpotByField", "204 overcts/sec (fr. 208)", "",
            "", "true", "false", "3", "", "", "false", "false"
        },
        {"", "204", "/206", "(fr. 207)", "", "getTaskExpressionsEvaluationsPerSpotByField", "204/206 (fr. 207)", "PCT",
            "", "true", "true", "6", "true", "", "false", "false"
        },
//        {"", "", "%", "err", "", "getTaskExpressionsEvaluationsPerSpotByField", "204/206 (fr. 207) %err", "",
//            "", "true", "true", "0", "true", "", "false", "false"
//        },
        {"", "204", "/206", "(fr. 208)", "", "getTaskExpressionsEvaluationsPerSpotByField", "204/206 (fr. 208)", "",
            "", "true", "true", "6", "true", "", "false", "false"
        },
        {"7-corr", "206Pb", "/238U", "const delta%", "", "getTaskExpressionsEvaluationsPerSpotByField", "7-corrPrimary calib const. delta%", "",
            "", "true", "true", "3", "true", "", "false", "false"
        },
        {"8-corr", "206Pb", "/238U", "const delta%", "", "getTaskExpressionsEvaluationsPerSpotByField", "8-corrPrimary calib const. delta%", "",
            "", "true", "true", "3", "true", "", "false", "false"
        },
        {"", "Uncorr", "Pb/U", "const", "", "getTaskExpressionsEvaluationsPerSpotByField", "UncorrPb/Uconst", "PCT",
            "", "true", "false", "3", "true", "232/238 ratio", "false", "false"
        }

    };
    // Report column order =
    //  displayName1, displayName2, displayName3, displayName4, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_204PbCorrected = new String[][]{
        {"4-corr", "206Pb", "/238U", "calCon", "", "getTaskExpressionsEvaluationsPerSpotByField", "4-corr206Pb/238Ucalibr.const", "PCT",
            "", "true", "false", "4", "true", "", "false", "false"
        },
        {"4-corr", "208Pb", "/232Th", "calCon", "", "getTaskExpressionsEvaluationsPerSpotByField", "4-corr208Pb/232Thcalibr.const", "PCT",
            "", "true", "false", "4", "true", "", "false", "false"
        },
        {"", "", "Age", "", "Ma", "getTaskExpressionsEvaluationsPerSpotByField", "4-corr206Pb/238U Age", "",
            "", "true", "true", "0", "", "", "false", "false"
        },
        {"", "", "\u00B11\u03C3", "", "Ma", "getTaskExpressionsEvaluationsPerSpotByField", "4-corr206Pb/238U Age 1sigma", "",
            "", "true", "true", "0", "", "", "false", "false"
        },
        {"", "4-corr", "%com", "206", "", "getTaskExpressionsEvaluationsPerSpotByField", "4-corr %com206", "",
            "", "true", "false", "3", "", "", "false", "false"
        },
        {"", "4-corr", "208Pb*", "/206Pb*", "", "getTaskExpressionsEvaluationsPerSpotByField", "4-corr208Pb*/206Pb*", "",
            "", "true", "false", "4", "", "", "false", "false"
        },
        {"", "", "%", "err", "", "getTaskExpressionsEvaluationsPerSpotByField", "4-corr208Pb*/206Pb* %err", "",
            "", "true", "false", "3", "", "", "false", "false"
        },       
        {"4-corr", "207Pb", "206Pb", "age","Ma",  "getTaskExpressionsEvaluationsPerSpotByField", "4-corr207Pb/206Pbage", "ABS",
            "", "true", "false", "3", "true", "", "false", "false"
        },
        {"", "4-corr", "207Pb", "/206Pb", "", "getTaskExpressionsEvaluationsPerSpotByField", "4-corr207Pb/206Pb", "",
            "", "true", "false", "4", "", "", "false", "false"
        },
        {"", "", "%", "err", "", "getTaskExpressionsEvaluationsPerSpotByField", "4-corr207Pb/206Pb%err", "",
            "", "true", "false", "3", "", "", "false", "false"
        },
        {"", "4-corr", "207Pb", "/235U", "", "getTaskExpressionsEvaluationsPerSpotByField", "4-corr 207Pb/235U", "PCT",
            "", "true", "false", "4", "true", "", "false", "false"
        },
//        {"", "", "%", "err", "", "getTaskExpressionsEvaluationsPerSpotByField", "4-corr207Pb/235U %err", "",
//            "", "true", "false", "3", "", "", "false", "false"
//        },
        {"", "4-corr", "206Pb", "/238U", "", "getTaskExpressionsEvaluationsPerSpotByField", "4-corr 206Pb/238U", "PCT",
            "", "true", "false", "4", "true", "", "false", "false"
        },
//        {"", "", "%", "err", "", "getTaskExpressionsEvaluationsPerSpotByField", "4-corr206Pb/238U %err", "",
//            "", "true", "false", "3", "", "", "false", "false"
//        },
        {"", "4-corr", "err", "corr", "", "getTaskExpressionsEvaluationsPerSpotByField", "4-corr-errcorr", "",
            "", "true", "false", "3", "", "", "false", "false"
        }

    };
    // Report column order =
    //  displayName1, displayName2, displayName3, displayName4, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_207PbCorrected = new String[][]{
        {"7-corr", "206Pb", "/238U", "calCon", "", "getTaskExpressionsEvaluationsPerSpotByField", "7-corr206Pb/238Ucalibr.const", "PCT",
            "", "true", "false", "4", "true", "", "false", "false"
        },
        {"7-corr", "208Pb", "/232Th", "calCon", "", "getTaskExpressionsEvaluationsPerSpotByField", "7-corr208Pb/232Thcalibr.const", "PCT",
            "", "true", "false", "4", "true", "", "false", "false"
        },        {"", "", "Age", "", "Ma", "getTaskExpressionsEvaluationsPerSpotByField", "7-corr206Pb/238U Age", "",
            "", "true", "true", "0", "", "", "false", "false"
        },
        {"", "", "\u00B11\u03C3", "", "Ma", "getTaskExpressionsEvaluationsPerSpotByField", "7-corr206Pb/238U Age 1sigma", "",
            "", "true", "true", "0", "", "", "false", "false"
        },
        {"", "7-corr", "%com", "206", "", "getTaskExpressionsEvaluationsPerSpotByField", "7-corr %com206", "",
            "", "true", "false", "3", "", "", "false", "false"
        },
        {"", "7-corr", "208Pb*", "/206Pb*", "", "getTaskExpressionsEvaluationsPerSpotByField", "7-corr208Pb*/206Pb*", "",
            "", "true", "false", "4", "", "", "false", "false"
        },
        {"", "", "%", "err", "", "getTaskExpressionsEvaluationsPerSpotByField", "7-corr208Pb*/206Pb* %err", "",
            "", "true", "false", "3", "", "", "false", "false"
        }

    };

    // Report column order =
    //  displayName1, displayName2, displayName3, displayName4, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_208PbCorrected = new String[][]{
        {"8-corr", "206Pb", "/238U", "calCon", "", "getTaskExpressionsEvaluationsPerSpotByField", "8-corr206Pb/238Ucalibr.const", "",
            "", "true", "false", "4", "", "", "false", "false"
        },
        {"", "", "%", "err", "", "getTaskExpressionsEvaluationsPerSpotByField", "8-corr206Pb/238Ucalibr.const %err", "",
            "", "true", "false", "2", "", "", "false", "false"
        },
        {"", "", "Age", "", "Ma", "getTaskExpressionsEvaluationsPerSpotByField", "8-corr206Pb/238U Age", "",
            "", "true", "true", "0", "", "", "false", "false"
        },
        {"", "", "\u00B11\u03C3", "", "Ma", "getTaskExpressionsEvaluationsPerSpotByField", "8-corr206Pb/238U Age 1sigma", "",
            "", "true", "true", "0", "", "", "false", "false"
        },
        {"", "8-corr", "%com", "206", "", "getTaskExpressionsEvaluationsPerSpotByField", "8-corr %com206", "",
            "", "true", "false", "3", "", "", "false", "false"
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
