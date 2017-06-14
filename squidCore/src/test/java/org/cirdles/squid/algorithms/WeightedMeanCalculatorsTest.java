package org.cirdles.squid.algorithms;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Griffin on 2/7/2017.
 */
public class WeightedMeanCalculatorsTest {
    
    /**
     *
     * @throws Exception
     */
    @Test
    public void wtdLinCorr() throws Exception {
        double[] x, y;
        double[][] sigRho;
        boolean expectedBad;
        double expectedSlope, expectedIntercept, expectedSigmaSlope, expectedSigmaIntercept, expectedCovSlopeInter,
                expectedMswd, expectedProbFit;
        int expectedMinIndex;
        y = new double[]{1.837969504110633, 1.8259442264825132, 1.8124896193468751, 1.8094547710489035, 1.8051014270405017};
        x = new double[]{248.5, 430.5, 612.5, 794.5, 976.5};
        sigRho = new double[][]{{0.0038538615162367618, 0.0, 0.0, 0.0, 0.0},
                {0.0, 0.003831224800286577, 0.25, 0.0, 0.0},
                {0.0, 0.25, 0.003803284943932538, 0.25, 0.0},
                {0.0, 0.0, 0.25, 0.003803284943932538, 0.25},
                {0.0, 0.0, 0.0, 0.25, 0.003803284943932538}};
        expectedBad = false;
        expectedSlope = 0.0;
        expectedIntercept = 1.8464967500150462;
        expectedSigmaSlope = 0.0;
        expectedSigmaIntercept = 0.004522048443740375;
        expectedCovSlopeInter = 0.0;
        expectedMswd = 1.2623509241318576;
        expectedProbFit = 0.2853957071615587;
        WeightedMeanCalculators.WtdLinCorrResults results = WeightedMeanCalculators.wtdLinCorr(y, sigRho, x);


        assertEquals(expectedBad, results.isBad());
        assertEquals(expectedSlope, results.getSlope(), 0.0);
        assertEquals(expectedIntercept, results.getIntercept(), 0.0);
        assertEquals(expectedSigmaSlope, results.getSigmaSlope(), 0.0);
        assertEquals(expectedSigmaIntercept, results.getSigmaIntercept(), 0.0);
        assertEquals(expectedCovSlopeInter, results.getCovSlopeInter(), 0.0);
        assertEquals(expectedMswd, results.getMswd(), 0.0);
        assertEquals(expectedProbFit, results.getProbFit(), 0.0);
    }

    /**
     * The values for this test were retrieved from the example prawn file 100142_G6147_10111109.43.xml. This file was
     * read in by squid and the first time weightedLinearCorr was called, the parameters and result were
     * recorded.
     * @throws Exception
     */
    @Test
    public void weightedLinearCorr() throws Exception {
        double[] x, y;
        double[][] sigmaRhoY;
        y = new double[]{1.837969504110633, 1.8259442264825132, 1.8124896193468751, 1.8094547710489035, 1.8051014270405017};
        x = new double[]{248.5, 430.5, 612.5, 794.5, 976.5};
        sigmaRhoY = new double[][]{{0.0038538615162367618, 0.0, 0.0, 0.0, 0.0},
                {0.0, 0.003831224800286577, 0.25, 0.0, 0.0},
                {0.0, 0.25, 0.003803284943932538, 0.25, 0.0},
                {0.0, 0.0, 0.25, 0.003803284943932538, 0.25},
                {0.0, 0.0, 0.0, 0.25, 0.003803284943932538}};

        boolean expectedBad = false;
        double expectedSlope = -4.5494878620412566E-5;
        double expectedIntercept = 1.8464967500150462;
        double expectedSlopeSig = 6.924027363443903E-6;
        double expectedInterceptSig = 0.004522048443740375;
        double expectedSlopeInterceptCov = -2.8336583202677104E-8;
        double expectedMswd = 1.2623509241318576;
        double expectedProb = 0.2853957071615587;
        WeightedMeanCalculators.WeightedLinearCorrResults results = WeightedMeanCalculators.weightedLinearCorr(y, x, sigmaRhoY);

        assertEquals(expectedBad, results.isBad());
        assertEquals(expectedSlope, results.getSlope(), 0.0);
        assertEquals(expectedIntercept, results.getIntercept(), 0.0);
        assertEquals(expectedSlopeSig, results.getSlopeSig(), 0.0);
        assertEquals(expectedInterceptSig, results.getInterceptSig(), 0.0);
        assertEquals(expectedSlopeInterceptCov, results.getSlopeInterceptCov(), 0.0);
        assertEquals(expectedMswd, results.getMswd(), 0.0);
        assertEquals(expectedProb, results.getProb(), 0.0);
    }

    /**
     * The values for this test were retrieved from the example prawn file 100142_G6147_10111109.43.xml. This file was
     * read in by squid and the first time convertCorrelationsToCovariances was called, the parameters and result were
     * recorded.
     * @throws Exception
     */
    @Test
    public void convertCorrelationsToCovariances() throws Exception {

        double[][] correlations, expectedResult, result;

        correlations = new double[][]{{7.030865164278388E-4, 0.48, 0.0, 0.0, 0.0},
                {0.48, 7.030865164278388E-4, 0.48, 0.0, 0.0},
                {0.0, 0.48, 7.030865164278388E-4, 0.48, 0.0},
                {0.0, 0.0, 0.48, 7.118276142802683E-4, 0.48},
                {0.0, 0.0, 0.0, 0.48, 7.156630505487066E-4}};

        expectedResult = new double[][]{{4.943306495826336E-7, 2.372787117996641E-7, 0.0, 0.0, 0.0},
                {2.372787117996641E-7, 4.943306495826336E-7, 2.372787117996641E-7, 0.0, 0.0},
                {0.0, 2.372787117996641E-7, 4.943306495826336E-7, 2.402286708582975E-7, 0.0},
                {0.0, 0.0, 2.402286708582975E-7, 5.066985524519384E-7, 2.445257865122999E-7},
                {0.0, 0.0, 0.0, 2.445257865122999E-7, 5.121736019206806E-7}};

        result = WeightedMeanCalculators.convertCorrelationsToCovariances(correlations);
        for (int i = 0; i < result.length; ++i)
        {
            assertTrue(Arrays.equals(expectedResult[i], result[i]));
        }
    }

    /**
     * The values for this test were retrieved from the example prawn file 100142_G6147_10111109.43.xml. This file was
     * read in by squid and the first time wtdAvCorr() was called, the parameters and result were recorded.
     * @throws Exception
     */
    @Test
    public void wtdAvCorr() throws Exception {

        boolean expectedBad;
        double expectedMeanVal, expectedSigmaMeanVal, expectedMswd, expectedProb, values[], varCov[][];

        values = new double[]{0.05894591763320877, 0.0584223008458865, 0.05895273157365964, 0.059756958328105375, 0.05990723246652871};
        varCov = new double[][]{{4.943306495826336E-7, 2.372787117996641E-7, 0.0, 0.0, 0.0},
                            {2.372787117996641E-7, 4.943306495826336E-7, 2.372787117996641E-7, 0.0, 0.0},
                            {0.0, 2.372787117996641E-7, 4.943306495826336E-7, 2.402286708582975E-7, 0.0},
                            {0.0, 0.0, 2.402286708582975E-7, 5.066985524519384E-7, 2.445257865122999E-7},
                            {0.0, 0.0, 0.0, 2.445257865122999E-7, 5.121736019206806E-7}};

        WeightedMeanCalculators.WtdAvCorrResults results = WeightedMeanCalculators.wtdAvCorr(values, varCov);
        expectedBad = false;
        expectedMeanVal = 0.05925183517876606;
        expectedSigmaMeanVal = 4.076591732406797E-4;
        expectedMswd = 0.5870446008183258;
        expectedProb = 0.6720117976694331;
        assertEquals(expectedBad, results.isBad());
        assertEquals(expectedMeanVal, results.getMeanVal(), 0);
        assertEquals(expectedSigmaMeanVal, results.getSigmaMeanVal(), 0);
        assertEquals(expectedMswd, results.getMswd(), 0);
        assertEquals(expectedProb, results.getProb(), 0);
    }

    /**
     * This test was created with randomly generated numbers
     * @throws Exception
     */
    @Test
    public void deletePoint() throws Exception {

        int rejPoint;
        WeightedMeanCalculators.DeletePointResults results;
        double[] y1, y2ExpResult, y2Result, x1, x2ExpResult, x2Result;
        double[][] sigRho1, sigRho2ExpResult, sigRho2Result;

        //test with random y1 (between 0 and 100) and incremental x1
        rejPoint = 5;
        y1 = new double[]{7.21, 47.90, 91.56, 4.06, 40.62, 39.74, 97.81, 61.35, 27.42, 3.53, 52.66, 32.71, 22.79, 25.69,
                80.57, 78.25, 86.56, 38.76, 62.94, 31.81};
        x1 = new double[]{0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0,
                17.0, 18.0, 19.0};
        sigRho1 = new double[][]{
                {8.52, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 4.25, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 47.84, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 35.63, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 17.47, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 28.09, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 47.05, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 7.89, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 14.60, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 46.67, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 17.61, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 38.47, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 4.15, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 3.37, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 11.50, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 23.99, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 37.37, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 13.30, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 35.41, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 15.99}};

        y2ExpResult = new double[]{7.21, 47.90, 91.56, 4.06, 40.62, 97.81, 61.35, 27.42, 3.53, 52.66, 32.71, 22.79, 25.69,
                80.57, 78.25, 86.56, 38.76, 62.94, 31.81};
        x2ExpResult = new double[]{0.0, 1.0, 2.0, 3.0, 4.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0,
                17.0, 18.0, 19.0};
        sigRho2ExpResult = new double[][]{
                {8.52, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 4.25, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 47.84, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 35.63, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 17.47, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 47.05, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 7.89, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 14.60, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 46.67, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 17.61, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 38.47, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 4.15, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 3.37, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 11.50, 0.00, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 23.99, 0.00, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 37.37, 0.00, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 13.30, 0.00, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 35.41, 0.00},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 15.99}};

        results = WeightedMeanCalculators.deletePoint(rejPoint, y1, sigRho1, x1);
        y2Result = results.getY2();
        x2Result = results.getX2();
        sigRho2Result = results.getSigRho2();
        assertTrue(Arrays.equals(y2ExpResult, y2Result));
        assertTrue(Arrays.equals(x2ExpResult, x2Result));
        assertEquals(sigRho2ExpResult.length, sigRho2Result.length);
        for (int i = 0; i < sigRho2Result.length; ++i)
        {
            assertTrue(Arrays.equals(sigRho2ExpResult[i], sigRho2Result[i]));
        }
    }
}