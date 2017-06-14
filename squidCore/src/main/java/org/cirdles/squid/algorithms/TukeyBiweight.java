/*
 * Copyright 2006-2017 CIRDLES.org.
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
package org.cirdles.squid.algorithms;

import java.math.BigDecimal;
import java.math.MathContext;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import static org.cirdles.squid.algorithms.BigDecimalCustomAlgorithms.bigDecimalSqrtBabylonian;
import static org.cirdles.squid.constants.SquidConstants.SQUID_TINY_VALUE;
import org.cirdles.squid.shrimp.ValueModel;

/**
 * From Ken Ludwig's Squid VBA code for use with Shrimp prawn files data
 * reduction. Note code extracted by Simon Bodorkos in emails to bowring
 * Feb.2016
 *
 * @author James F. Bowring
 */
public final class TukeyBiweight {

    /**
     *
     * @param name
     * @param tuningConstant
     * @param values
     * @return
     */
    public static ValueModel calculateTukeyBiweightMean(String name, double tuningConstant, double[] values) {
        // guarantee termination
        BigDecimal epsilon = BigDecimal.ONE.movePointLeft(10);
        int iterationMax = 100;
        int iterationCounter = 0;

        int n = values.length;
        // initial mean is median
        BigDecimal mean = new BigDecimal(calculateMedian(values));

        // initial sigma is median absolute deviation from mean = median (MAD)
        double deviations[] = new double[n];
        for (int i = 0; i < values.length; i++) {
            deviations[i] = Math.abs(values[i] - mean.doubleValue());
        }
        BigDecimal sigma = new BigDecimal(calculateMedian(deviations)).max( BigDecimal.valueOf(SQUID_TINY_VALUE));

        BigDecimal previousMean;
        BigDecimal previousSigma;

        do {
            iterationCounter++;
            previousMean = mean;
            previousSigma = sigma;

            // init to zeroes
            BigDecimal[] deltas = new BigDecimal[n];
            BigDecimal[] u = new BigDecimal[n];
            BigDecimal sa = BigDecimal.ZERO;
            BigDecimal sb = BigDecimal.ZERO;
            BigDecimal sc = BigDecimal.ZERO;

            BigDecimal tee = new BigDecimal(tuningConstant).multiply(sigma);

            for (int i = 0; i < n; i++) {
                deltas[i] = new BigDecimal(values[i]).subtract(mean);
                if (tee.compareTo(deltas[i].abs()) > 0) {
                    deltas[i] = new BigDecimal(values[i]).subtract(mean);
                    u[i] = deltas[i].divide(tee, MathContext.DECIMAL128);
                    BigDecimal uSquared = u[i].multiply(u[i]);
                    sa = sa.add(deltas[i].multiply(BigDecimal.ONE.subtract(uSquared).pow(2)).pow(2));
                    sb = sb.add(BigDecimal.ONE.subtract(uSquared).multiply(BigDecimal.ONE.subtract(new BigDecimal(5.0).multiply(uSquared))));
                    sc = sc.add(u[i].multiply(BigDecimal.ONE.subtract(uSquared).pow(2)));
                }
            }

            sigma = bigDecimalSqrtBabylonian(sa.multiply(new BigDecimal(n))).divide(sb.abs(), MathContext.DECIMAL128);
            sigma = sigma.max(BigDecimal.valueOf(SQUID_TINY_VALUE));
            mean = previousMean.add(tee.multiply(sc).divide(sb, MathContext.DECIMAL128));

        } // both tests against epsilon must pass OR iterations top out
        // april 2016 Simon B discovered we need 101 iterations possible, hence the "<=" below
        while (((sigma.subtract(previousSigma).abs().divide(sigma, MathContext.DECIMAL128).compareTo(epsilon) > 0)//
                || mean.subtract(previousMean).abs().divide(mean, MathContext.DECIMAL128).compareTo(epsilon) > 0)//
                && (iterationCounter <= iterationMax));

        return new ValueModel(name, mean, "ABS", sigma);
    }

    /**
     * Calculates arithmetic median of array of doubles.
     *
     * @pre values has one element
     * @param values
     * @return
     */
    public static double calculateMedian(double[] values) {
        double median;

        // enforce precondition
        if (values.length == 0) {
            median = 0.0;
        } else {
            DescriptiveStatistics stats = new DescriptiveStatistics();

            // Add the data from the array
            for (int i = 0; i < values.length; i++) {
                stats.addValue(values[i]);
            }
            median = stats.getPercentile(50);
        }

        return median;
    }
}
