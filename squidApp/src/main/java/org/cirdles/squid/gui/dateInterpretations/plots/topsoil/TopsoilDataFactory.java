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
package org.cirdles.squid.gui.dateInterpretations.plots.topsoil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static org.cirdles.squid.gui.utilities.stringUtilities.StringTester.stringIsSquidRatio;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;

import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.ERR_CORREL;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.ERR_CORREL_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.ERR_CORREL_TERA_WASSERBURG;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R206PB_238U;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R206PB_238U_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207PB_206PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207PB_235U;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207PB_235U_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R238U_206PB;
import static org.cirdles.topsoil.Variable.RHO;
import static org.cirdles.topsoil.Variable.SELECTED;
import static org.cirdles.topsoil.Variable.SIGMA_X;
import static org.cirdles.topsoil.Variable.SIGMA_Y;
import static org.cirdles.topsoil.Variable.VISIBLE;
import static org.cirdles.topsoil.Variable.X;
import static org.cirdles.topsoil.Variable.Y;

/**
 *
 * @author James F. Bowring
 */
public class TopsoilDataFactory {

    public static Map<String, Object> prepareWetherillDatum(
            ShrimpFractionExpressionInterface shrimpFraction, String correction, boolean isUnknown) {

        // default is for reference materials
        String ratioBase75 = R207PB_235U_RM;
        String ratioBase68 = R206PB_238U_RM;
        String errCorr = ERR_CORREL_RM;
        if (isUnknown) {
            ratioBase75 = R207PB_235U;
            ratioBase68 = R206PB_238U;
            errCorr = ERR_CORREL;
        }

        Map<String, Object> datum = prepareDatum(shrimpFraction, correction, ratioBase75, ratioBase68, errCorr);

        return datum;
    }

    public static Map<String, Object> prepareTeraWasserburgDatum(
            ShrimpFractionExpressionInterface shrimpFraction, String correction, boolean isUnknown) {
        // jan 2019 - there is a naming problem we are working on
        // TW is not defined for reference materials
        String ratioBase86 = "";
        String ratioBase76 = "";
        String errCorr = "";
        if (isUnknown) {
            ratioBase86 = R238U_206PB;
            ratioBase76 = R207PB_206PB;
            errCorr = ERR_CORREL_TERA_WASSERBURG;
        }

        Map<String, Object> datum = prepareDatum(shrimpFraction, correction, ratioBase86, ratioBase76, errCorr);

        return datum;
    }

    public static Map<String, Object> preparePlotAnyTwoDatum(
            ShrimpFractionExpressionInterface shrimpFraction, String xAxisExpressionName, String yAxisExpressionName) {

        Map<String, Object> datum = prepareDatum(shrimpFraction, "", xAxisExpressionName, yAxisExpressionName, "");

        return datum;
    }

    private static Map<String, Object> prepareDatum(
            ShrimpFractionExpressionInterface shrimpFraction, String correction, String xAxisRatio, String yAxisRatio, String rho) {

        Map<String, Object> datum = new HashMap<>();
        boolean badData = true;

        // xAxis
        double[] xAxisValueAndUnct;
        if (stringIsSquidRatio(correction + xAxisRatio)) {
            // case of raw ratios
            xAxisValueAndUnct
                    = Arrays.stream(shrimpFraction
                            .getIsotopicRatioValuesByStringName(correction + xAxisRatio)).toArray(double[][]::new)[0];
        } else {
            // all other expressions
            xAxisValueAndUnct = shrimpFraction
                    .getTaskExpressionsEvaluationsPerSpotByField(correction + xAxisRatio)[0];
        }
        badData = badData && !Double.isFinite(xAxisValueAndUnct[0]);
        datum.put(X.getTitle(), xAxisValueAndUnct[0]);
        datum.put(SIGMA_X.getTitle(), 0.0);
        if (xAxisValueAndUnct.length > 1) {
            datum.put(SIGMA_X.getTitle(), 1.0 * xAxisValueAndUnct[1]);
        }

        // yAxis
        double[] yAxisValueAndUnct;
        if (stringIsSquidRatio(correction + yAxisRatio)) {
            // case of raw ratios
            yAxisValueAndUnct
                    = Arrays.stream(shrimpFraction
                            .getIsotopicRatioValuesByStringName(correction + yAxisRatio)).toArray(double[][]::new)[0];
        } else {
            // all other expressions
            yAxisValueAndUnct = shrimpFraction
                    .getTaskExpressionsEvaluationsPerSpotByField(correction + yAxisRatio)[0];
        }
        badData = badData && !Double.isFinite(yAxisValueAndUnct[0]);
        datum.put(Y.getTitle(), yAxisValueAndUnct[0]);
        datum.put(SIGMA_Y.getTitle(), 0.0);
        if (yAxisValueAndUnct.length > 1) {
            datum.put(SIGMA_Y.getTitle(), 1.0 * yAxisValueAndUnct[1]);
        }

        if (rho.compareToIgnoreCase(ERR_CORREL_TERA_WASSERBURG) == 0) {
            // Nov 2020 per Simon B , TW RHO = 0; see: https://github.com/CIRDLES/Squid/issues/531
            datum.put(RHO.getTitle(), 0.0);
        } else {
            double[] plotRho;
            try {
                plotRho = shrimpFraction
                        .getTaskExpressionsEvaluationsPerSpotByField(correction + rho)[0];
                datum.put(RHO.getTitle(), plotRho[0]);
            } catch (Exception e) {
            }
        }

        datum.put(VISIBLE.getTitle(), true);
        datum.put(SELECTED.getTitle(), true);

        return badData ? null : datum;
    }
}
