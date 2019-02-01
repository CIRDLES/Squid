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
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.COM206PB_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.COM208PB_PCT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.EXP_8CORR_238_206_STAR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R206PB_238U;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R208PB206PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.U_CONCEN_PPM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_CONCEN_PPM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR206PB238U_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR208PB232TH_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TOTAL_206_238;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TOTAL_208_232;

/**
 * Modified from ET_Redux July 2018
 *
 * @author James F. Bowring
 */
public class ReportSpecificationsUPbSamples extends ReportSpecificationsAbstract {

    // Report column order =
    //  displayName1, displayName2, displayName3, displayName4, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_CorrectionIndependentData = new String[][]{
        {"", "", "UncorrPb", "/Uconst", "", "getTaskExpressionsEvaluationsPerSpotByField", UNCOR206PB238U_CALIB_CONST, "PCT",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"", "", "UncorrPb", "/Thconst", "", "getTaskExpressionsEvaluationsPerSpotByField", UNCOR208PB232TH_CALIB_CONST, "PCT",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"", "", "U", "(ppm)", "", "getTaskExpressionsEvaluationsPerSpotByField", U_CONCEN_PPM, "",
            "", "true", "false", "6", "", "concentration of U", "false", "false"
        },
        {"", "", "Th", "(ppm)", "", "getTaskExpressionsEvaluationsPerSpotByField", TH_CONCEN_PPM, "",
            "", "true", "false", "6", "", "concentration of Th", "true", "false"
        },
        {"", "", "232Th", "/238U", "", "getTaskExpressionsEvaluationsPerSpotByField", TH_U_EXP, "PCT",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"", "Total", "206Pb", "/238U", "", "getTaskExpressionsEvaluationsPerSpotByField", TOTAL_206_238, "PCT",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"", "Total", "208Pb", "/232Th", "", "getTaskExpressionsEvaluationsPerSpotByField", TOTAL_208_232, "PCT",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"", "Total", "238U", "/206Pb", "", "getTaskExpressionsEvaluationsPerSpotByField", "Total 238U/206PbS", "PCT",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"", "Total", "207Pb", "/206Pb", "", "getTaskExpressionsEvaluationsPerSpotByField", "Total 207Pb/206PbS", "PCT",
            "", "true", "false", "6", "true", "", "false", "false"
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
        {"", "4-corr", "%com", "206", "", "getTaskExpressionsEvaluationsPerSpotByField", PB4CORR + COM206PB_PCT, "",
            "", "true", "false", "6", "", "", "false", "false"
        },
        {"", "4-corr", "%com", "208", "", "getTaskExpressionsEvaluationsPerSpotByField", PB4CORR + COM208PB_PCT, "",
            "", "true", "false", "6", "", "", "false", "false"
        },
        {"", "4-corr", "208Pb*", "/206Pb*", "", "getTaskExpressionsEvaluationsPerSpotByField", PB4CORR + R208PB206PB, "PCT",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"", "4-corr", "ppm", "206*", "", "getTaskExpressionsEvaluationsPerSpotByField", PB4CORR + "ppm 206*", "",
            "", "true", "false", "6", "", "", "false", "false"
        },
        {"", "4-corr", "ppm", "208*", "", "getTaskExpressionsEvaluationsPerSpotByField", PB4CORR + "ppm 208*", "",
            "", "true", "false", "6", "", "", "false", "false"
        },
        {"204corr", "206Pb", "/238U", "Age", "Ma", "getTaskExpressionsEvaluationsPerSpotByField", "204cor_206Pb/238U Age", "ABS",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"204corr", "207Pb", "/206Pb", "Age", "Ma", "getTaskExpressionsEvaluationsPerSpotByField", "204corr 207Pb/206Pb Age", "ABS",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"204corr", "208Pb", "/232Th", "Age", "Ma", "getTaskExpressionsEvaluationsPerSpotByField", "204corr 208Pb/232Th Age", "ABS",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"%", "Dis-", "cor-", "dant", "", "getTaskExpressionsEvaluationsPerSpotByField", "204corr Discordance", "",
            "", "true", "false", "6", "", "", "false", "false"
        },
        {"", "4-corr", "208*", "/232", "", "getTaskExpressionsEvaluationsPerSpotByField", PB4CORR + "208*/232", "PCT",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"", "4-corr", "238", "/206*", "", "getTaskExpressionsEvaluationsPerSpotByField", PB4CORR + "238/206", "PCT",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"", "4-corr", "207*", "/206*", "", "getTaskExpressionsEvaluationsPerSpotByField", PB4CORR + "207*/206*", "PCT",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"", "4-corr", "207*", "/235", "", "getTaskExpressionsEvaluationsPerSpotByField", PB4CORR + "207*/235S", "PCT",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"", "4-corr", "206*", "/238", "", "getTaskExpressionsEvaluationsPerSpotByField", PB4CORR + R206PB_238U, "PCT",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"", "", "err", "corr", "", "getTaskExpressionsEvaluationsPerSpotByField", PB4CORR + "errcorrS", "",
            "", "true", "false", "6", "", "", "false", "false"
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
        {"", "7-corr", "204Pb", "/206Pb", "", "getTaskExpressionsEvaluationsPerSpotByField", PB7CORR + "204Pb/206Pb", "",
            "", "true", "false", "6", "", "", "false", "false"
        },
        {"", "7-corr", "%com", "206", "", "getTaskExpressionsEvaluationsPerSpotByField", PB7CORR + COM206PB_PCT, "",
            "", "true", "false", "6", "", "", "false", "false"
        },
        {"", "7-corr", "%com", "208", "", "getTaskExpressionsEvaluationsPerSpotByField", PB7CORR + COM208PB_PCT, "",
            "", "true", "false", "6", "", "", "false", "false"
        },
        {"", "7-corr", "208Pb*", "/206Pb*", "", "getTaskExpressionsEvaluationsPerSpotByField", PB7CORR + R208PB206PB, "PCT",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"", "7-corr", "ppm", "206*", "", "getTaskExpressionsEvaluationsPerSpotByField", PB7CORR + "ppm 206*", "",
            "", "true", "false", "6", "", "", "false", "false"
        },
        {"", "7-corr", "ppm", "208*", "", "getTaskExpressionsEvaluationsPerSpotByField", PB7CORR + "ppm 208*", "",
            "", "true", "false", "6", "", "", "false", "false"
        },
        {"207corr", "206Pb", "/238U", "Age", "Ma", "getTaskExpressionsEvaluationsPerSpotByField", "207cor_206Pb/238U Age", "ABS",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"207corr", "208Pb", "/232Th", "Age", "Ma", "getTaskExpressionsEvaluationsPerSpotByField", PB7CORR + "208Pb/232Th Age", "ABS",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"", "7-corr", "206*", "/238", "", "getTaskExpressionsEvaluationsPerSpotByField", PB7CORR + R206PB_238U, "PCT",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"", "7-corr", "208*", "/232", "", "getTaskExpressionsEvaluationsPerSpotByField", PB7CORR + "208*/232S", "PCT",
            "", "true", "false", "6", "true", "", "false", "false"
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
        {"", "8-corr", "204Pb", "/206Pb", "", "getTaskExpressionsEvaluationsPerSpotByField", PB8CORR + "204Pb/206Pb", "",
            "", "true", "false", "6", "", "", "false", "false"
        },
        {"", "8-corr", "%com", "206", "", "getTaskExpressionsEvaluationsPerSpotByField", PB8CORR + COM206PB_PCT, "",
            "", "true", "false", "6", "", "", "false", "false"
        },
        {"", "8-corr", "ppm", "206", "", "getTaskExpressionsEvaluationsPerSpotByField", PB8CORR + "ppm 206*", "",
            "", "true", "false", "6", "", "", "false", "false"
        },
        {"208corr", "206Pb", "/238U", "Age", "Ma", "getTaskExpressionsEvaluationsPerSpotByField", "208cor_206Pb/238U Age", "ABS",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"208corr", "207Pb", "/206Pb", "Age", "Ma", "getTaskExpressionsEvaluationsPerSpotByField", "208corr 207Pb/206PbS Age", "ABS",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"", "8-corr", "238", "/206*", "", "getTaskExpressionsEvaluationsPerSpotByField", EXP_8CORR_238_206_STAR, "PCT",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"", "8-corr", "207*", "/206*", "", "getTaskExpressionsEvaluationsPerSpotByField", PB8CORR + "207*/206*S", "PCT",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"", "8-corr", "207*", "/235", "", "getTaskExpressionsEvaluationsPerSpotByField", PB8CORR + "207*/235S", "PCT",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"", "8-corr", "206*", "/238", "", "getTaskExpressionsEvaluationsPerSpotByField", PB8CORR + R206PB_238U, "PCT",
            "", "true", "false", "6", "true", "", "false", "false"
        },
        {"", "", "err", "corr", "", "getTaskExpressionsEvaluationsPerSpotByField", PB8CORR + "errcorrS", "",
            "", "true", "false", "6", "", "", "false", "false"
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
