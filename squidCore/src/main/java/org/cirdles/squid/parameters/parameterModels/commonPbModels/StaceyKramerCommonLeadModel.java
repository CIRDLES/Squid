/*
 * Copyright 2019 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.parameters.parameterModels.commonPbModels;

import static org.cirdles.squid.constants.Squid3Constants.REF_238U235U_DEFAULT;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.util.Lambdas;

/**
 * @see https://github.com/CIRDLES/ET_Redux/wiki/Function-SingleStagePbR
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class StaceyKramerCommonLeadModel {

    private static final double SK_ALPHA0 = 11.152;
    private static final double SK_BETA0 = 12.998;
    private static final double SK_GAMMA0 = 31.23;
    private static final double SK_MU = 9.74;
    private static final double SK_KAPPA_MU = 3.78;
    private static final double SK_START_AGE = 3.7E9;
    private static double U_RATIO = 137.88;

    private static double lambda238 = 0.0;
    private static double lambda235 = 0.0;
    private static double lambd232 = 0.0;

    public static void updatePhysicalConstants(ParametersModel physicalConstantsModel) {
        lambda238 = physicalConstantsModel.getDatumByName(Lambdas.LAMBDA_238.getName()).getValue().doubleValue();
        lambda235 = physicalConstantsModel.getDatumByName(Lambdas.LAMBDA_235.getName()).getValue().doubleValue();
        lambd232 = physicalConstantsModel.getDatumByName(Lambdas.LAMBDA_232.getName()).getValue().doubleValue();
    }

    public static void updateU_Ratio(double u_ratio){
        U_RATIO = u_ratio;
        if (u_ratio == 0.0) {
            U_RATIO = REF_238U235U_DEFAULT;
        }
    }

    /**
     * Output is a 3-element vector of model [206Pb/204Pb, 207Pb/204Pb,
     * 208Pb/204Pb] corresponding to TargetAge, as per Stacey & Kramers (1975).
     *
     * @param targetAge the value of targetAge
     * @return the double[]
     */
    public static double[] staceyKramerSingleStagePbR(double targetAge) {

        double[] PbLambda = new double[]{lambda238, lambda235, lambd232};
        double[] PbR0 = new double[]{SK_ALPHA0, SK_BETA0, SK_GAMMA0};
        double[] MuIsh = new double[]{
            SK_MU,
            (SK_MU / U_RATIO),
            (SK_MU * SK_KAPPA_MU)};
        double[] PbExp = new double[]{
            Math.exp(SK_START_AGE * lambda238),
            Math.exp(SK_START_AGE * lambda235),
            Math.exp(SK_START_AGE * lambd232)};
        double[] eTerm = new double[3];
        double[] singleStagePbR = new double[3];

        for (int i = 0; i < 3; i++) {
            eTerm[i] = targetAge * PbLambda[i];
            singleStagePbR[i] = PbR0[i] + MuIsh[i] * (PbExp[i] - Math.exp(eTerm[i]));
        }

        return singleStagePbR;
    }
}
