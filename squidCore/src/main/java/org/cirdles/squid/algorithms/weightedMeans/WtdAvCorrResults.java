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
public final class WtdAvCorrResults {

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