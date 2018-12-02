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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;

import static org.cirdles.topsoil.variable.Variables.*;

/**
 *
 * @author James F. Bowring
 */
public class TopsoilDataFactory {

    // this test data set is CM2 from ET_Redux work
    public static final double[][] EXAMPLE_CM2_DATASET = new double[][]{
        {0.071813669, 0.011006957, 0.00010654762, 0.0000029902690, 0.525021016},
        {0.072151433, 0.011005053, 0.00007607823, 0.0000027925747, 0.576825349},
        {0.071944887, 0.011003275, 0.00005774879, 0.0000026419337, 0.565467772},
        {0.071935928, 0.011006019, 0.00007001780, 0.0000027907138, 0.593632132},
        {0.071881029, 0.011006746, 0.00011879759, 0.0000029932998, 0.547212036},
        {0.072008073, 0.011000075, 0.00012637628, 0.0000030924856, 0.491113441},
        {0.071909459, 0.011005301, 0.00014366566, 0.0000034129203, 0.53576221},
        {0.072023966, 0.011007749, 0.00006526067, 0.0000027068856, 0.588051902},
        {0.07204976, 0.011005122, 0.00006776661, 0.0000027686638, 0.663026105},
        {0.072067922, 0.011007277, 0.00007005962, 0.0000027064135, 0.547645084},
        {0.072012531, 0.011005595, 0.00007278283, 0.0000027963019, 0.485116139},
        {0.071951025, 0.011001109, 0.00006243122, 0.0000027089483, 0.587402112},
        {0.071984195, 0.011002318, 0.00005824101, 0.0000026286515, 0.547568329},
        {0.072026796, 0.011001577, 0.00009552567, 0.0000028683014, 0.580131768}

    };

    public static List<Map<String, Object>> prepareWetherillData(double[][] data) {
        List<Map<String, Object>> datumList = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            Map<String, Object> datum = new HashMap<>();
            datumList.add(datum);
            datum.put(X.getName(), data[i][0]);
            datum.put(SIGMA_X.getName(), data[i][2]);
            datum.put(Y.getName(), data[i][1]);
            datum.put(SIGMA_Y.getName(), data[i][3]);
            datum.put(RHO.getName(), data[i][4]);
            datum.put("Selected", true);
        }

        return datumList;
    }

    public static Map<String, Object> prepareWetherillDatum(
            ShrimpFractionExpressionInterface shrimpFraction, String correction, boolean isUnknown) {

        Map<String, Object> datum = new HashMap<>();
        // default is for reference materials
        String ratioBase75 = " 207*/235";
        String ratioBase68 = " 206*/238";
        String errCorr = " errcorr";
        if (isUnknown) {
            ratioBase75 = " 207*/235S";
            ratioBase68 = " 206*/238S";
            errCorr = " errcorrS";
        }

        try {
            Method method = ShrimpFractionExpressionInterface.class.getMethod(//
                    "getTaskExpressionsEvaluationsPerSpotByField",
                    new Class[]{String.class});

            double[] r207_235 = ((double[][]) method.invoke(shrimpFraction, new Object[]{correction + ratioBase75}))[0].clone();
            datum.put(X.getName(), r207_235[0]);
            datum.put(SIGMA_X.getName(), 1.0 * r207_235[1]);

            double[] r206_235 = ((double[][]) method.invoke(shrimpFraction, new Object[]{correction + ratioBase68}))[0].clone();
            datum.put(Y.getName(), r206_235[0]);
            datum.put(SIGMA_Y.getName(), 1.0 * r206_235[1]);

            double rho = ((double[][]) method.invoke(shrimpFraction, new Object[]{correction + errCorr}))[0].clone()[0];
            datum.put(RHO.getName(), rho);
            datum.put("Selected", true);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException noSuchMethodException) {
        }

        return datum;
    }
}
