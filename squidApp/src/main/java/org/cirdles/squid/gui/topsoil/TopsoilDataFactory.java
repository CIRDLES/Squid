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
package org.cirdles.squid.gui.topsoil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;

import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.ERR_CORREL;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.ERR_CORREL_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R206PB_238U;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R206PB_238U_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207PB_206PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207PB_206PB_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207PB_235U;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R207PB_235U_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.R238U_206PB;

import static org.cirdles.topsoil.Variable.*;

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
            errCorr = ERR_CORREL;
        }

        Map<String, Object> datum = prepareDatum(shrimpFraction, correction, ratioBase86, ratioBase76, errCorr);

        return datum;
    }

    private static Map<String, Object> prepareDatum(
            ShrimpFractionExpressionInterface shrimpFraction, String correction, String xAxisRatio, String yAxisRatio, String rho) {

        Map<String, Object> datum = new HashMap<>();
        boolean badData = true;

        try {
            Method method = ShrimpFractionExpressionInterface.class.getMethod(//
                    "getTaskExpressionsEvaluationsPerSpotByField",
                    new Class[]{String.class});

            double[] xAxisValueAndUnct = ((double[][]) method.invoke(shrimpFraction, new Object[]{correction + xAxisRatio}))[0].clone();
            badData = badData && Double.isNaN(xAxisValueAndUnct[0]);
            datum.put(X.getTitle(), xAxisValueAndUnct[0]);
            datum.put(SIGMA_X.getTitle(), 1.0 * xAxisValueAndUnct[1]);

            double[] yAxisValueAndUnct = ((double[][]) method.invoke(shrimpFraction, new Object[]{correction + yAxisRatio}))[0].clone();
            badData = badData && Double.isNaN(yAxisValueAndUnct[0]);
            datum.put(Y.getTitle(), yAxisValueAndUnct[0]);
            datum.put(SIGMA_Y.getTitle(), 1.0 * yAxisValueAndUnct[1]);

            double plotRho = ((double[][]) method.invoke(shrimpFraction, new Object[]{correction + rho}))[0].clone()[0];
            datum.put(RHO.getTitle(), plotRho);

            datum.put(VISIBLE.getTitle(), true);
            datum.put(SELECTED.getTitle(), true);

//            datum.put("AGE", 0.0);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException noSuchMethodException) {
        }

        return badData ? null : datum;
    }
}
