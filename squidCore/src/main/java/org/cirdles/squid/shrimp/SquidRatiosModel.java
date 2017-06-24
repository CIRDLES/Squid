/*
 * Copyright 2017 CIRDLES.org.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import static org.cirdles.ludwig.squid25.SquidConstants.SQUID_UPPER_LIMIT_1_SIGMA_PERCENT;
import org.cirdles.squid.tasks.expressions.ExpressionTree;
import org.cirdles.squid.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.ExpressionTreeWithRatiosInterface;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.operations.Operation;

/**
 *
 * @author James F. Bowring
 */
public class SquidRatiosModel implements Serializable, Comparable<SquidRatiosModel> {

    private static long serialVersionUID = -2944080263487487243L;

    private String ratioName;
    private SquidSpeciesModel numerator;
    private SquidSpeciesModel denominator;
    private int reportingOrderIndex;

    private List<Double> ratEqTime;
    private List<Double> ratEqVal;
    // one sigma absolute uncertainties for ratEqVal
    private List<Double> ratEqErr;
    private double ratioVal;
    // one sigma absolute uncertainty for ratioVal
    private double ratioFractErr;
    private int minIndex;
    private boolean active;
    
    public static Map<String, SquidRatiosModel> knownSquidRatiosModels = new HashMap<>();

    public SquidRatiosModel(SquidSpeciesModel numerator, SquidSpeciesModel denominator, int reportingOrderIndex) {
        this.numerator = numerator;
        this.denominator = denominator;
        this.reportingOrderIndex = reportingOrderIndex;

        this.ratioName = numerator.getIsotopeName() + "/" + denominator.getIsotopeName();

        this.ratEqTime = new ArrayList<>();
        this.ratEqVal = new ArrayList<>();
        this.ratEqErr = new ArrayList<>();
        this.ratioVal = 0;
        this.ratioFractErr = 0;
        this.minIndex = -2;
        this.active = false;
        
        knownSquidRatiosModels.put(ratioName, this);
    }

    @Override
    public int compareTo(SquidRatiosModel squidRatiosModel) {
        return Integer.compare(reportingOrderIndex, squidRatiosModel.getReportingOrderIndex());
    }

    @Override
    public boolean equals(Object squidRatiosModel) {
        boolean retVal = false;

        if (squidRatiosModel instanceof SquidRatiosModel) {
            retVal = reportingOrderIndex == ((SquidRatiosModel) squidRatiosModel).getReportingOrderIndex();
        }

        return retVal;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public SquidRatiosModel copy() {
        SquidRatiosModel copy = new SquidRatiosModel(numerator, denominator, reportingOrderIndex);
        return copy;
    }

    public static ExpressionTreeInterface buildRatioExpression(String ratioName) {
        // format of ratioName is "nnn/mmm"
        ExpressionTreeInterface ratioExpression
                = new ExpressionTree(
                        ratioName,
                        new ShrimpSpeciesNode(findNumerator(ratioName), "getPkInterpScanArray"),
                        new ShrimpSpeciesNode(findDenominator(ratioName), "getPkInterpScanArray"),
                        Operation.divide());

        ((ExpressionTreeWithRatiosInterface) ratioExpression).getRatiosOfInterest().add(ratioName);
        return ratioExpression;
    }

    public static SquidSpeciesModel findNumerator(String ratioName) {
        String[] parts = ratioName.split("/");
        return SquidSpeciesModel.knownSquidSpeciesModels.get(parts[0]);
    }

    public static SquidSpeciesModel findDenominator(String ratioName) {
        String[] parts = ratioName.split("/");
        return SquidSpeciesModel.knownSquidSpeciesModels.get(parts[1]);
    }

    public static SquidRatiosModel findSquidRatiosModelByName(SortedSet<SquidRatiosModel> isotopicRatios, String ratioName) {
        SquidRatiosModel retVal = null;
        Iterator<SquidRatiosModel> iterator = isotopicRatios.iterator();
        while (iterator.hasNext()) {
            SquidRatiosModel model = iterator.next();
            if (model.getRatioName().equals(ratioName)) {
                retVal = model;
                break;
            }
        }

        return retVal;
    }

    /**
     * @return the ratioName
     */
    public String getRatioName() {
        return ratioName;
    }

    /**
     * @param ratioName the ratioName to set
     */
    public void setRatioName(String ratioName) {
        this.ratioName = ratioName;
    }

    /**
     *
     * @return
     */
    public String getDisplayNameNoSpaces() {
        return numerator.getIsotopeName() + "/" + denominator.getIsotopeName();
    }

    /**
     * @return the numerator
     */
    public SquidSpeciesModel getNumerator() {
        return numerator;
    }

    /**
     * @param numerator the numerator to set
     */
    public void setNumerator(SquidSpeciesModel numerator) {
        this.numerator = numerator;
    }

    /**
     * @return the denominator
     */
    public SquidSpeciesModel getDenominator() {
        return denominator;
    }

    /**
     * @param denominator the denominator to set
     */
    public void setDenominator(SquidSpeciesModel denominator) {
        this.denominator = denominator;
    }

    /**
     * @return the reportingOrderIndex
     */
    public int getReportingOrderIndex() {
        return reportingOrderIndex;
    }

    /**
     * @param reportingOrderIndex the reportingOrderIndex to set
     */
    public void setReportingOrderIndex(int reportingOrderIndex) {
        this.reportingOrderIndex = reportingOrderIndex;
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

        // first determine if  above Squid25 limits
        double ratioFraErrFiltered = ((ratioFractErr * 100.0) > SQUID_UPPER_LIMIT_1_SIGMA_PERCENT)
                ? (SQUID_UPPER_LIMIT_1_SIGMA_PERCENT / 100.0) : ratioFractErr;

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
