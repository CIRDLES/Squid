package org.cirdles.squid.algorithms;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Griffin Hiers on 1/30/2017.
 */
public class PoissonLimitsCountLessThanEqual100Test {

    /**
     * @throws Exception
     */
    @Test
    public void determineIndexOfValueWithLargestResidual() throws Exception {
        //test with randomly-generated values between 0 and 100
        double median = 51.1252907588;
        double[] measurements = {44.17, 12.53, 86.63, 19.94, 24.77, 15.07, 20.61, 24.12, 33.73, 25.46, 55.40, 54.49, 39.24, 91.29, 38.76, 51.89, 94.75, 16.96, 3.36, 8.42};
        //index of value that is furthest from the median & outside limits, 3.36 in this case
        int expectedResult = 18;
        int result = PoissonLimitsCountLessThanEqual100.determineIndexOfValueWithLargestResidual(median, measurements);
        assertEquals("failed on randomly generated values test", expectedResult, result, 0.0);

        //test with 15 positive zeroes
        median = 0;
        measurements = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        //index of value that is furthest from the median & outside limits, -1 in this case because nothing is outside the range
        expectedResult = -1;
        result = PoissonLimitsCountLessThanEqual100.determineIndexOfValueWithLargestResidual(median, measurements);
        assertEquals("failed on randomly generated values test", expectedResult, result, 0.0);

        //test with 15 negative zeroes
        median = -0d;
        measurements = new double[]{-0d, -0d, -0d, -0d, -0d, -0d, -0d, -0d, -0d, -0d, -0d, -0d, -0d, -0d, -0d};
        //index of value that is furthest from the median & outside limits, -1 in this case because nothing is outside the range
        expectedResult = -1;
        result = PoissonLimitsCountLessThanEqual100.determineIndexOfValueWithLargestResidual(median, measurements);
        assertEquals("failed on randomly generated values test", expectedResult, result, 0.0);
    }

}