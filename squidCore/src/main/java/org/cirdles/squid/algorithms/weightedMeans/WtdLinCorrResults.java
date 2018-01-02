/*
 * Copyright 2016 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.algorithms.weightedMeans;

/**
 *
 * @author James F. Bowring
 */
public final class WtdLinCorrResults {

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
