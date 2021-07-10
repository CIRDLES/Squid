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
 * @author James F. Bowring
 */
public final class WeightedLinearCorrResults {

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