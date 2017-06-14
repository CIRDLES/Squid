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

import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.cirdles.ludwig.squid25.Utilities;

/**
 *
 * @author James F. Bowring
 * Based on Simon Bodorkos' interpreation of Ludwig's code:
 * https://github.com/CIRDLES/ET_Redux/wiki/SHRIMP:-Step-4
 */
public final class WeightedMeanCalculators {

    /**
     * Adapted from Simon Bodorkos interpretation of Ludwig:
     * https://github.com/CIRDLES/ET_Redux/wiki/SHRIMP:-Sub-wtdLinCorr. Note the
     * logic is simplified and output values are stored in object of type
     * WtdLinCorrResults. Indexing in Java is 0-based, hence the use of i-1 and
     * minIndex - 1 in the calls to deletePoint.
     *
     * @param y
     * @param sigRho
     * @param x
     * @return
     */
    public static WtdLinCorrResults wtdLinCorr(double[] y, double[][] sigRho, double[] x) {

        WtdLinCorrResults wtdLinCorrResults = new WtdLinCorrResults();

        boolean linReg = (y.length == x.length);

        int avg1LinRegr2 = linReg ? 2 : 1;
        int n = y.length;
        double[] mswdRatList = new double[]{0.0, 0.1, 0.15, 0.2, 0.2, 0.25};

        double mswdRatToler = (n > 7) ? 0.3 : mswdRatList[n - avg1LinRegr2 - 1];
//        int maxRej = (int) Math.ceil((n - avg1LinRegr2) / 8.0);
        // incorrect statement found by Griffin Hiers Feb 2017
        int maxRej = 1 + (int) Math.floor((n - avg1LinRegr2) / 8.0);
//        boolean[] rej = new boolean[n]; // not used

        double minProb = 0.1;
//        double wLrej = 0;
        int pass = 0;
        int minIndex = -1;
        double minMSWD = 0.0;
        double maxProb = 0.0;

        double[] y1 = y.clone();
        double[] y2 = y.clone();
        double[] x1 = x.clone();
        double[] x2 = x.clone();
        double[][] sigRho1 = sigRho.clone();
        double[][] sigRho2 = sigRho.clone();

        double[] sigmaY = new double[n];
        for (int i = 0; i < n; i++) {
            sigmaY[i] = sigRho[i][i];
        }

        double f = Math.max(Utilities.median(sigmaY), 1e-10);
        for (int i = 0; i < n; i++) {
            sigRho1[i][i] = Math.max(sigRho1[i][i], f);
            sigRho2[i][i] = sigRho1[i][i];
        }

        boolean doContinue = true;
        int nw = n;
        DeletePointResults deletePointResults;
        double[] probW = new double[n + 1];
        double[] mswdW = new double[n + 1];
        double[] sigmaInterW = new double[n + 1];
        double[] interW = new double[n + 1];
        double[] slopeW = new double[n + 1];
        double[] sigmaSlopeW = new double[n + 1];
        double[] covSlopeInterW = new double[n + 1];

        do {
            for (int i = 0; i < (n + 1); i++) {
                if (i > 0) {
                    deletePointResults = deletePoint(i - 1, y1, sigRho1, x1);

                    y2 = deletePointResults.getY2();
                    sigRho2 = deletePointResults.getSigRho2();
                    x2 = deletePointResults.getX2();
                    nw = n - 1;
                }

                if ((nw == 1) && !linReg) {
                    probW[i] = 1.0;
                    mswdW[i] = 0.0;
                    sigmaInterW[i] = 1.0;
                    interW[i] = 1.0;
                } else if (linReg) {
                    WeightedLinearCorrResults weightedLinearCorrResults = weightedLinearCorr(y2, x2, sigRho2);

                    slopeW[i] = weightedLinearCorrResults.getSlope();
                    interW[i] = weightedLinearCorrResults.getIntercept();
                    mswdW[i] = weightedLinearCorrResults.getMswd();
                    probW[i] = weightedLinearCorrResults.getProb();
                    sigmaSlopeW[i] = weightedLinearCorrResults.getSlopeSig();
                    sigmaInterW[i] = weightedLinearCorrResults.getInterceptSig();
                    covSlopeInterW[i] = weightedLinearCorrResults.getSlopeInterceptCov();
                    // bad is never used

                } else {
                    WtdAvCorrResults wtdAvCorrResults = wtdAvCorr(y2, convertCorrelationsToCovariances(sigRho2));

                    interW[i] = wtdAvCorrResults.getMeanVal();
                    sigmaInterW[i] = wtdAvCorrResults.getSigmaMeanVal();
                    mswdW[i] = wtdAvCorrResults.getMswd();
                    probW[i] = wtdAvCorrResults.getProb();
                }

                if (i == 0) {
                    if (probW[0] > 0.1) {
                        minIndex = 0;
//                        minMSWD = mswdW[0]; // assignment never used
                        // exit for loop of i
                        break;
                    }

                    maxProb = probW[0];
                }
            } // for loop of i

            if (minIndex == 0) {
                doContinue = false;
            } else {
                minIndex = 0;
                minMSWD = mswdW[0];

                for (int i = 1; i < (n + 1); i++) {
                    double mswdRat = mswdW[i] / Math.max(1e-32, mswdW[0]);
                    if ((mswdRat < mswdRatToler) && (mswdW[i] < minMSWD) && (probW[i] > minProb)) {
//                        rej[i] = true; not used
//                        wLrej++; not used
                        minIndex = i;
                        maxProb = probW[i];
                        minMSWD = mswdW[i];
                    }
                }

                pass++;

                // note check for pass > 0 in original code is redundant
                if ((minIndex == 0) || (pass == maxRej) || (maxProb > 0.1)) {
                    doContinue = false;
                } else {
                    deletePointResults = deletePoint(minIndex - 1, y1, sigRho1, x1);

                    y2 = deletePointResults.getY2();
                    sigRho2 = deletePointResults.getSigRho2();
                    x2 = deletePointResults.getX2();
                    n -= 1;

                    y1 = new double[n];
                    if (linReg) {
                        x1 = new double[n];
                    }

                    sigRho1 = new double[n][n];

                    for (int i = 0; i < n; i++) {
                        y1[i] = y2[i];
                        if (linReg) {
                            x1[i] = x2[i];
                        }
                        System.arraycopy(sigRho2[i], 0, sigRho1[i], 0, n);
                    }
                }
            }
        } while (doContinue);

        double intercept = interW[minIndex];
        double sigmaIntercept = sigmaInterW[minIndex];
        double mswd = mswdW[minIndex];
        double probfit = probW[minIndex];

        if (linReg && (minIndex > 0)) {
            wtdLinCorrResults.setSlope(slopeW[minIndex]);
            wtdLinCorrResults.setSigmaSlope(sigmaSlopeW[minIndex]);
            wtdLinCorrResults.setCovSlopeInter(covSlopeInterW[minIndex]);
        }

        if (probfit < 0.05) {
            sigmaIntercept *= Math.sqrt(mswd);

            if (linReg) {
                wtdLinCorrResults.setSigmaSlope(wtdLinCorrResults.getSigmaSlope() * Math.sqrt(mswd));
            }
        }

        wtdLinCorrResults.setBad(false);
        wtdLinCorrResults.setIntercept(intercept);
        wtdLinCorrResults.setSigmaIntercept(sigmaIntercept);
        wtdLinCorrResults.setMswd(mswd);
        wtdLinCorrResults.setProbFit(probfit);
        wtdLinCorrResults.setMinIndex(minIndex);

        return wtdLinCorrResults;
    }

    /**
     *
     */
    public static class WtdLinCorrResults {

        private boolean bad;
        private double intercept;
        private double sigmaIntercept;
        private double mswd;
        private double probFit;
        // used for linReg = true fitting case
        private double slope = 0.0;
        private double sigmaSlope = 0.0;
        private double covSlopeInter = 0.0;
        private int minIndex;

        /**
         *
         */
        public WtdLinCorrResults() {
            bad = true;
            intercept = 0.0;
            sigmaIntercept = 0.0;
            mswd = 0.0;
            probFit = 0.0;
            slope = 0.0;
            sigmaSlope = 0.0;
            covSlopeInter = 0.0;
            minIndex = -1;
        }

        /**
         * @return the bad
         */
        public boolean isBad() {
            return bad;
        }

        /**
         * @param bad the bad to set
         */
        public void setBad(boolean bad) {
            this.bad = bad;
        }

        /**
         * @return the intercept
         */
        public double getIntercept() {
            return intercept;
        }

        /**
         * @param intercept the intercept to set
         */
        public void setIntercept(double intercept) {
            this.intercept = intercept;
        }

        /**
         * @return the sigmaIntercept
         */
        public double getSigmaIntercept() {
            return sigmaIntercept;
        }

        /**
         * @param sigmaIntercept the sigmaIntercept to set
         */
        public void setSigmaIntercept(double sigmaIntercept) {
            this.sigmaIntercept = sigmaIntercept;
        }

        /**
         * @return the mswd
         */
        public double getMswd() {
            return mswd;
        }

        /**
         * @param mswd the mswd to set
         */
        public void setMswd(double mswd) {
            this.mswd = mswd;
        }

        /**
         * @return the probFit
         */
        public double getProbFit() {
            return probFit;
        }

        /**
         * @param probFit the probFit to set
         */
        public void setProbFit(double probFit) {
            this.probFit = probFit;
        }

        /**
         * @return the slope
         */
        public double getSlope() {
            return slope;
        }

        /**
         * @param slope the slope to set
         */
        public void setSlope(double slope) {
            this.slope = slope;
        }

        /**
         * @return the sigmaSlope
         */
        public double getSigmaSlope() {
            return sigmaSlope;
        }

        /**
         * @param sigmaSlope the sigmaSlope to set
         */
        public void setSigmaSlope(double sigmaSlope) {
            this.sigmaSlope = sigmaSlope;
        }

        /**
         * @return the covSlopeInter
         */
        public double getCovSlopeInter() {
            return covSlopeInter;
        }

        /**
         * @param covSlopeInter the covSlopeInter to set
         */
        public void setCovSlopeInter(double covSlopeInter) {
            this.covSlopeInter = covSlopeInter;
        }

        /**
         * @return the minIndex
         */
        public int getMinIndex() {
            return minIndex;
        }

        /**
         * @param minIndex the minIndex to set
         */
        public void setMinIndex(int minIndex) {
            this.minIndex = minIndex;
        }

    }

    /**
     *
     * @param y
     * @param x
     * @param sigmaRhoY
     * @return
     */
    public static WeightedLinearCorrResults weightedLinearCorr(double[] y, double[] x, double[][] sigmaRhoY) {
        WeightedLinearCorrResults weightedLinearCorrResults = new WeightedLinearCorrResults();

        RealMatrix omega = new BlockRealMatrix(convertCorrelationsToCovariances(sigmaRhoY));
        RealMatrix invOmega = MatrixUtils.inverse(omega);
        int n = y.length;

        double mX = 0;
        double pX = 0;
        double pY = 0;
        double pXY = 0;
        double w = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double invOm = invOmega.getEntry(i, j);
                w += invOm;
                pX += (invOm * (x[i] + x[j]));
                pY += (invOm * (y[i] + y[j]));
                pXY += (invOm * (((x[i] * y[j]) + (x[j] * y[i]))));
                mX += (invOm * x[i] * x[j]);
            }
        }       
        double slope = ((2 * pXY * w) - (pX * pY)) / ((4 * mX * w) - (pX * pX));
        double intercept = (pY - (slope * pX)) / (2 * w);

        RealMatrix fischer = new BlockRealMatrix(new double[][]{{mX, pX / 2.0}, {pX / 2.0, w}});
        RealMatrix fischerInv = MatrixUtils.inverse(fischer);

        double slopeSig = Math.sqrt(fischerInv.getEntry(0, 0));
        double interceptSig = Math.sqrt(fischerInv.getEntry(1, 1));
        double slopeInterceptCov = fischerInv.getEntry(0, 1);
        
        RealMatrix resid = new BlockRealMatrix(n, 1);
        for (int i = 0; i < n; i++) {
            resid.setEntry(i, 0, y[i] - (slope * x[i]) - intercept);
        }

        RealMatrix residT = resid.transpose();
        RealMatrix mM = residT.multiply(invOmega).multiply(resid);

        double sumSqWtdResids = mM.getEntry(0, 0);
        double mswd = sumSqWtdResids / (n - 2);

        // http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/distribution/FDistribution.html
        FDistribution fdist = new org.apache.commons.math3.distribution.FDistribution((n - 2), 1E9);
        double prob = 1.0 - fdist.cumulativeProbability(mswd);
        
        weightedLinearCorrResults.setBad(false);
        weightedLinearCorrResults.setSlope(slope);
        weightedLinearCorrResults.setIntercept(intercept);
        weightedLinearCorrResults.setSlopeSig(slopeSig);
        weightedLinearCorrResults.setInterceptSig(interceptSig);
        weightedLinearCorrResults.setSlopeInterceptCov(slopeInterceptCov);
        weightedLinearCorrResults.setMswd(mswd);
        weightedLinearCorrResults.setProb(prob);

        return weightedLinearCorrResults;
    }

    /**
     *
     */
    public static class WeightedLinearCorrResults {

        private boolean bad;
        private double slope;
        private double intercept;
        private double slopeSig;
        private double interceptSig;
        private double slopeInterceptCov;
        private double mswd;
        private double prob;

        /**
         *
         */
        public WeightedLinearCorrResults() {
            bad = true;
            slope = 0.0;
            intercept = 0.0;
            slopeSig = 0.0;
            interceptSig = 0.0;
            slopeInterceptCov = 0.0;
            mswd = 0.0;
            prob = 0.0;
        }

        /**
         * @return the bad
         */
        public boolean isBad() {
            return bad;
        }

        /**
         * @param bad the bad to set
         */
        public void setBad(boolean bad) {
            this.bad = bad;
        }

        /**
         * @return the slope
         */
        public double getSlope() {
            return slope;
        }

        /**
         * @param slope the slope to set
         */
        public void setSlope(double slope) {
            this.slope = slope;
        }

        /**
         * @return the intercept
         */
        public double getIntercept() {
            return intercept;
        }

        /**
         * @param intercept the intercept to set
         */
        public void setIntercept(double intercept) {
            this.intercept = intercept;
        }

        /**
         * @return the slopeSig
         */
        public double getSlopeSig() {
            return slopeSig;
        }

        /**
         * @param slopeSig the slopeSig to set
         */
        public void setSlopeSig(double slopeSig) {
            this.slopeSig = slopeSig;
        }

        /**
         * @return the interceptSig
         */
        public double getInterceptSig() {
            return interceptSig;
        }

        /**
         * @param interceptSig the interceptSig to set
         */
        public void setInterceptSig(double interceptSig) {
            this.interceptSig = interceptSig;
        }

        /**
         * @return the slopeInterceptCov
         */
        public double getSlopeInterceptCov() {
            return slopeInterceptCov;
        }

        /**
         * @param slopeInterceptCov the slopeInterceptCov to set
         */
        public void setSlopeInterceptCov(double slopeInterceptCov) {
            this.slopeInterceptCov = slopeInterceptCov;
        }

        /**
         * @return the mswd
         */
        public double getMswd() {
            return mswd;
        }

        /**
         * @param mswd the mswd to set
         */
        public void setMswd(double mswd) {
            this.mswd = mswd;
        }

        /**
         * @return the prob
         */
        public double getProb() {
            return prob;
        }

        /**
         * @param prob the prob to set
         */
        public void setProb(double prob) {
            this.prob = prob;
        }

    }

    /**
     *
     * @param rejPoint
     * @param y1
     * @param sigRho1
     * @param x1
     * @return
     */
    public static DeletePointResults deletePoint(int rejPoint, double[] y1, double[][] sigRho1, double[] x1) {

        DeletePointResults results = new DeletePointResults();

        int n = y1.length;
        boolean linReg = (x1.length == n);

        double[] y2 = new double[n - 1];
        double[][] sigRho2 = new double[n - 1][n - 1];
        double[] x2 = new double[0];

        if (linReg) {
            x2 = new double[n - 1];
        }

        for (int j = 0; j < n; j++) {
            int m = j + 1;
            int p = j + 2;

            if (j < rejPoint) {
                sigRho2[j][j] = sigRho1[j][j];
                y2[j] = y1[j];
                if (linReg) {
                    x2[j] = x1[j];
                }
            } else if (j < (n - 1)) {
                sigRho2[j][j] = sigRho1[m][m];
                y2[j] = y1[m];
                if (linReg) {
                    x2[j] = x1[m];
                }
            }

            if (j < (rejPoint - 1)) {
                sigRho2[j][m] = sigRho1[j][m];
                sigRho2[m][j] = sigRho1[m][j];
            } else if ((j == (rejPoint - 1)) && (m < (n - 1))) {
                sigRho2[j][m] = 0.0;
                sigRho2[m][j] = 0.0;
            } else if (j < (n - 2)) {
                sigRho2[j][m] = sigRho1[m][p];
                sigRho2[m][j] = sigRho1[p][m];
            }
        }

        results.setY2(y2);
        results.setSigRho2(sigRho2);
        results.setX2(x2);

        return results;
    }

    /**
     *
     */
    public static class DeletePointResults {

        private double[] y2;
        private double[][] sigRho2;
        private double[] x2;

        /**
         *
         */
        public DeletePointResults() {
            y2 = new double[0];
            sigRho2 = new double[0][0];
            x2 = new double[0];
        }

        /**
         * @return the y2
         */
        public double[] getY2() {
            return y2.clone();
        }

        /**
         * @param y2 the y2 to set
         */
        public void setY2(double[] y2) {
            this.y2 = y2.clone();
        }

        /**
         * @return the sigRho2
         */
        public double[][] getSigRho2() {
            return sigRho2.clone();
        }

        /**
         * @param sigRho2 the sigRho2 to set
         */
        public void setSigRho2(double[][] sigRho2) {
            this.sigRho2 = sigRho2.clone();
        }

        /**
         * @return the x2
         */
        public double[] getX2() {
            return x2.clone();
        }

        /**
         * @param x2 the x2 to set
         */
        public void setX2(double[] x2) {
            this.x2 = x2.clone();
        }

    }

    /**
     *
     * @param values
     * @param varCov
     * @return
     */
    public static WtdAvCorrResults wtdAvCorr(double[] values, double[][] varCov) {
        // assume varCov is variance-covariance matrix (i.e. SigRho = false)

        WtdAvCorrResults results = new WtdAvCorrResults();

        int n = varCov.length;
        RealMatrix omegaInv = new BlockRealMatrix(varCov);
        RealMatrix omega = MatrixUtils.inverse(omegaInv);

        double numer = 0.0;
        double denom = 0.0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                numer += (values[i] + values[j]) * omega.getEntry(i, j);
                denom += omega.getEntry(i, j);
            }
        }

        // test denom
        if (denom > 0.0) {
            double meanVal = numer / denom / 2.0;
            double meanValSigma = Math.sqrt(1.0 / denom);

            double[][] unwtdResidsArray = new double[n][1];
            for (int i = 0; i < n; i++) {
                unwtdResidsArray[i][0] = values[i] - meanVal;
            }

            RealMatrix unwtdResids = new BlockRealMatrix(unwtdResidsArray);
            RealMatrix transUnwtdResids = unwtdResids.transpose();
            RealMatrix product = transUnwtdResids.multiply(omega);
            RealMatrix sumWtdResids = product.multiply(unwtdResids);

            double mswd = 0.0;
            double prob = 0.0;
            if (n > 1) {
                mswd = sumWtdResids.getEntry(0, 0) / (n - 1);

                // http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/distribution/FDistribution.html
                FDistribution fdist = new org.apache.commons.math3.distribution.FDistribution((n - 1), 1E9);
                prob = 1.0 - fdist.cumulativeProbability(mswd);
            }

            results.setBad(false);
            results.setMeanVal(meanVal);
            results.setSigmaMeanVal(meanValSigma);
            results.setMswd(mswd);
            results.setProb(prob);
        }

        return results;

    }

    /**
     *
     */
    public static class WtdAvCorrResults {

        private boolean bad;
        private double meanVal;
        private double sigmaMeanVal;
        private double mswd;
        private double prob;

        /**
         *
         */
        public WtdAvCorrResults() {
            bad = true;
            meanVal = 0.0;
            sigmaMeanVal = 0.0;
            mswd = 0.0;
            prob = 0.0;
        }

        /**
         * @return the bad
         */
        public boolean isBad() {
            return bad;
        }

        /**
         * @param bad the bad to set
         */
        public void setBad(boolean bad) {
            this.bad = bad;
        }

        /**
         * @return the meanVal
         */
        public double getMeanVal() {
            return meanVal;
        }

        /**
         * @param meanVal the meanVal to set
         */
        public void setMeanVal(double meanVal) {
            this.meanVal = meanVal;
        }

        /**
         * @return the sigmaMeanVal
         */
        public double getSigmaMeanVal() {
            return sigmaMeanVal;
        }

        /**
         * @param sigmaMeanVal the sigmaMeanVal to set
         */
        public void setSigmaMeanVal(double sigmaMeanVal) {
            this.sigmaMeanVal = sigmaMeanVal;
        }

        /**
         * @return the mswd
         */
        public double getMswd() {
            return mswd;
        }

        /**
         * @param mswd the mswd to set
         */
        public void setMswd(double mswd) {
            this.mswd = mswd;
        }

        /**
         * @return the prob
         */
        public double getProb() {
            return prob;
        }

        /**
         * @param prob the prob to set
         */
        public void setProb(double prob) {
            this.prob = prob;
        }
    }

    /**
     *
     * @param correlations
     * @return
     */
    public static double[][] convertCorrelationsToCovariances(double[][] correlations) {
        // precondition: square matrix correlations
        int n = correlations.length;

        double[][] covariances = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    covariances[i][j] = correlations[i][i] * correlations[i][i];
                } else {
                    covariances[i][j] = correlations[i][j] * correlations[i][i] * correlations[j][j];
                }
            }
        }

        return covariances;
    }
}
