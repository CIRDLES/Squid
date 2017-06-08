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
package org.cirdles.squid.shrimp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import static org.cirdles.ludwig.squid25.SquidConstants.SQUID_UPPER_LIMIT_1_SIGMA_PERCENT;


/**
 *
 * @author James F. Bowring
 */
public class IsotopeRatioModelSHRIMP implements Serializable{

    private RawRatioNamesSHRIMP rawRatioName;
    private IsotopeNames numerator;
    private IsotopeNames denominator;
    private List<Double> ratEqTime;
    private List<Double> ratEqVal;
    // one sigma absolute uncertainties for ratEqVal
    private List<Double> ratEqErr;
    private double ratioVal;
    // one sigma absolute uncertainty for ratioVal
    private double ratioFractErr;
    private int minIndex;
    private boolean active;

    /**
     *
     * @param rawRatioName the value of rawRatioName
     */
    public IsotopeRatioModelSHRIMP(RawRatioNamesSHRIMP rawRatioName) {
        this.rawRatioName = rawRatioName;
        this.numerator = rawRatioName.getNumerator();
        this.denominator = rawRatioName.getDenominator();
        this.ratEqTime = new ArrayList<>();
        this.ratEqVal = new ArrayList<>();
        this.ratEqErr = new ArrayList<>();
        this.ratioVal = 0;
        this.ratioFractErr = 0;
        this.minIndex = -2;
        this.active = false;
    }

    /**
     *
     * @return
     */
    public boolean numeratorAtomicRatioLessThanDenominator() {
        return (numerator.getAtomicMass() < denominator.getAtomicMass());
    }

    /**
     *
     * @return
     */
    public String prettyPrintSimpleName() {
        return Integer.toString(numerator.getAtomicMass()) + "/" + Integer.toString(denominator.getAtomicMass());
    }

    /**
     * @return the rawRatioName
     */
    public RawRatioNamesSHRIMP getRawRatioName() {
        return rawRatioName;
    }

    /**
     * @param rawRatioName the rawRatioName to set
     */
    public void setRawRatioName(RawRatioNamesSHRIMP rawRatioName) {
        this.rawRatioName = rawRatioName;
    }

    /**
     * @return the numerator
     */
    public IsotopeNames getNumerator() {
        return numerator;
    }

    /**
     * @param numerator the numerator to set
     */
    public void setNumerator(IsotopeNames numerator) {
        this.numerator = numerator;
    }

    /**
     * @return the denominator
     */
    public IsotopeNames getDenominator() {
        return denominator;
    }

    /**
     * @param denominator the denominator to set
     */
    public void setDenominator(IsotopeNames denominator) {
        this.denominator = denominator;
    }

    /**
     * @return the ratEqTime
     */
    public List<Double> getRatEqTime() {
        return ratEqTime;
    }

    /**
     * @param ratEqTime the ratEqTime to set
     */
    public void setRatEqTime(List<Double> ratEqTime) {
        this.ratEqTime = ratEqTime;
    }

    /**
     * @return the ratEqVal
     */
    public List<Double> getRatEqVal() {
        return ratEqVal;
    }

    /**
     * @param ratEqVal the ratEqVal to set
     */
    public void setRatEqVal(List<Double> ratEqVal) {
        this.ratEqVal = ratEqVal;
    }

    /**
     * @return the ratEqErr
     */
    public List<Double> getRatEqErr() {
        return ratEqErr;
    }

    /**
     * @param ratEqErr the ratEqErr to set
     */
    public void setRatEqErr(List<Double> ratEqErr) {
        this.ratEqErr = ratEqErr;
    }

    /**
     * @return the ratioVal
     */
    public double getRatioVal() {
        return ratioVal;
    }

    /**
     * @param ratioVal the ratioVal to set
     */
    public void setRatioVal(double ratioVal) {
        this.ratioVal = ratioVal;
    }

    /**
     * @return the ratioFractErr
     */
    public double getRatioFractErr() {
        return ratioFractErr;
    }

    /**
     * @return the ratioFractErr
     */
    public double getRatioFractErrAsOneSigmaPercent() {
        return ratioFractErr / ratioVal * 100.0;
    }

    /**
     * @param ratioFractErr the ratioFractErr to set
     */
    public void setRatioFractErr(double ratioFractErr) {
        // april 2017 introduce Squid2.5 upper limit
        // the value supplied is the 1 sigma percent uncertainty divided by 100
        // we choose to store the 1 sigma absolute as ratioFracErr

        // first determin if  above Squid25 limits
        double ratioFraErrFiltered = (ratioFractErr * 100.0 > SQUID_UPPER_LIMIT_1_SIGMA_PERCENT) ? SQUID_UPPER_LIMIT_1_SIGMA_PERCENT / 100.0 : ratioFractErr;

        this.ratioFractErr = ratioFraErrFiltered * ratioVal;
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

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

}
