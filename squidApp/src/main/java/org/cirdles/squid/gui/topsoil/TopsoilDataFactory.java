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

import static org.cirdles.topsoil.variable.Variables.*;

/**
 *
 * @author James F. Bowring
 */
public class TopsoilDataFactory {

    public static Map<String, Object> prepareWetherillDatum(
            ShrimpFractionExpressionInterface shrimpFraction, String correction, boolean isUnknown) {

        // default is for reference materials
        String ratioBase75 = " 207*/235";
        String ratioBase68 = " 206*/238";
        String errCorr = " errcorr";
        if (isUnknown) {
            ratioBase75 = " 207*/235S";
            ratioBase68 = " 206*/238S";
            errCorr = " errcorrS";
        }

        Map<String, Object> datum = prepareDatum(shrimpFraction, correction, ratioBase75, ratioBase68, errCorr);

        return datum;
    }

    public static Map<String, Object> prepareTeraWasserburgDatum(
            ShrimpFractionExpressionInterface shrimpFraction, String correction, boolean isUnknown) {
        // jan 2019 - there is a naming problem we are working on
        // see for example : (unknowns): '4-corr 238/236' vs '8-corr 238/206*'
        // default is for reference materials
        String ratioBase86 = " 238/206*";
        String ratioBase76 = " 207*/206*";
        String errCorr = " errcorr";
        if (isUnknown) {
            ratioBase86 = " 238/206";
            ratioBase76 = " 207*/206*";
            errCorr = " errcorrS";
        }

        Map<String, Object> datum = prepareDatum(shrimpFraction, correction, ratioBase86, ratioBase76, errCorr);

        return datum;
    }

    private static Map<String, Object> prepareDatum(
            ShrimpFractionExpressionInterface shrimpFraction, String correction, String xAxisRatio, String yAxisRatio, String rho) {

        Map<String, Object> datum = new HashMap<>();

        try {
            Method method = ShrimpFractionExpressionInterface.class.getMethod(//
                    "getTaskExpressionsEvaluationsPerSpotByField",
                    new Class[]{String.class});

            double[] xAxisValueAndUnct = ((double[][]) method.invoke(shrimpFraction, new Object[]{correction + xAxisRatio}))[0].clone();
            datum.put(X.getName(), xAxisValueAndUnct[0]);
            datum.put(SIGMA_X.getName(), 1.0 * xAxisValueAndUnct[1]);

            double[] yAxisValueAndUnct = ((double[][]) method.invoke(shrimpFraction, new Object[]{correction + yAxisRatio}))[0].clone();
            datum.put(Y.getName(), yAxisValueAndUnct[0]);
            datum.put(SIGMA_Y.getName(), 1.0 * yAxisValueAndUnct[1]);

            double plotRho = ((double[][]) method.invoke(shrimpFraction, new Object[]{correction + rho}))[0].clone()[0];
            datum.put(RHO.getName(), plotRho);

            datum.put("Selected", true);

            datum.put("AGE", 0.0);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException noSuchMethodException) {
        }

        return datum;
    }
}
