/*
 * Copyright 2017 James F. Bowring and CIRDLES.org.
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
import java.util.Objects;

import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFAULT_BACKGROUND_MASS_LABEL;

/**
 * @author James F. Bowring
 */
public class MassStationDetail implements Comparable<MassStationDetail>, Serializable {

    private static final long serialVersionUID = -874679604916998001L;
    private final String massStationLabel;
    private final double centeringTimeSec;
    private final List<Double> measuredTrimMasses;
    private final List<Double> timesOfMeasuredTrimMasses;
    private final List<Integer> indicesOfScansAtMeasurementTimes;
    private int massStationIndex;
    private String elementLabel;
    private String isotopeLabel;
    private String isotopeAMU;
    private String taskIsotopeLabel;
    private boolean isBackground;
    private List<Integer> indicesOfRunsAtMeasurementTimes;

    private String uThBearingName;

    private boolean viewedAsGraph;

    // added July 2020 to accommodate Ratio mode
    private boolean numeratorRole;
    private boolean denominatorRole;

    public MassStationDetail(
            int massStationIndex,
            String massStationLabel,
            double centeringTimeSec,
            String isotopeLabel,
            String elementLabel,
            boolean isBackground,
            String uThBearingName) {

        this.massStationIndex = massStationIndex;
        this.massStationLabel = massStationLabel;
        this.isotopeLabel = isotopeLabel;
        this.isotopeAMU = isotopeLabel;
        this.taskIsotopeLabel = isotopeLabel;
        this.elementLabel = elementLabel;
        this.isBackground = isBackground;

        this.centeringTimeSec = centeringTimeSec;
        this.measuredTrimMasses = new ArrayList<>();
        this.timesOfMeasuredTrimMasses = new ArrayList<>();
        this.indicesOfScansAtMeasurementTimes = new ArrayList<>();
        this.indicesOfRunsAtMeasurementTimes = new ArrayList<>();

        this.uThBearingName = uThBearingName;

        // default value
        this.viewedAsGraph = false;
        this.numeratorRole = true;
        this.denominatorRole = true;
    }

    @Override
    public int compareTo(MassStationDetail massStationDetail) {
        int retVal = 0;
        if (this != massStationDetail) {
            retVal = Integer.compare(this.massStationIndex, massStationDetail.getMassStationIndex());
        }

        return retVal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MassStationDetail)) {
            return false;
        }
        MassStationDetail that = (MassStationDetail) o;
        return massStationIndex == that.massStationIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(massStationIndex, massStationLabel, elementLabel, isotopeLabel, taskIsotopeLabel, isBackground, centeringTimeSec, measuredTrimMasses, timesOfMeasuredTrimMasses, indicesOfScansAtMeasurementTimes, indicesOfRunsAtMeasurementTimes, uThBearingName, viewedAsGraph);
    }

    /**
     * @return the massStationIndex
     */
    public int getMassStationIndex() {
        return massStationIndex;
    }

    /**
     * @param massStationIndex the massStationIndex to set
     */
    public void setMassStationIndex(int massStationIndex) {
        this.massStationIndex = massStationIndex;
    }

    public boolean autoCentered() {
        return centeringTimeSec > 0.0;
    }

    /**
     * @return the massStationLabel
     */
    public String getMassStationLabel() {
        return massStationLabel;
    }

    /**
     * @return the centeringTimeSec
     */
    public double getCenteringTimeSec() {
        return centeringTimeSec;
    }

    /**
     * @return the isotopeLabel
     */
    public String getIsotopeLabel() {
        return isotopeLabel;
    }

    /**
     * @param label
     */
    public void setIsotopeLabel(String label) {
        isotopeLabel = label;
    }

    /**
     * @return the isotopeAMU
     */
    public String getIsotopeAMU() {
        // backwards compatitble July 2020
        if (isotopeAMU == null) {
            isotopeAMU = "n/a";
        }
        return isotopeAMU;
    }

    /**
     * @return the taskIsotopeLabel
     */
    public String getTaskIsotopeLabel() {
        return taskIsotopeLabel;
    }

    /**
     * @param taskIsotopeLabel the taskIsotopeLabel to set
     */
    public void setTaskIsotopeLabel(String label) {
        taskIsotopeLabel = label;
    }

    public void updateTaskIsotopeLabelForBackground(String nominalMass) {
        taskIsotopeLabel = DEFAULT_BACKGROUND_MASS_LABEL + " (" + nominalMass + ")";
    }

    /**
     * @return the elementLabel
     */
    public String getElementLabel() {
        return elementLabel;
    }

    /**
     * @param elementLabel
     */
    public void setElementLabel(String elementLabel) {
        this.elementLabel = elementLabel;
    }

    /**
     * @return the backgroundLabel
     */
    public boolean getIsBackground() {
        return isBackground;
    }

    /**
     * @param backgroundLabel the backgroundLabel to set
     */
    public void setIsBackground(boolean isBackground) {
        this.isBackground = isBackground;
    }

    /**
     * @return the measuredTrimMasses
     */
    public List<Double> getMeasuredTrimMasses() {
        return measuredTrimMasses;
    }

    /**
     * @return the timesOfMeasuredTrimMasses
     */
    public List<Double> getTimesOfMeasuredTrimMasses() {
        return timesOfMeasuredTrimMasses;
    }

    /**
     * @return the indicesOfScansAtMeasurementTimes
     */
    public List<Integer> getIndicesOfScansAtMeasurementTimes() {
        return indicesOfScansAtMeasurementTimes;
    }

    /**
     * @return the indicesOfRunsAtMeasurementTimes
     */
    public List<Integer> getIndicesOfRunsAtMeasurementTimes() {
        return indicesOfRunsAtMeasurementTimes;
    }

    /**
     * @param indicesOfRunsAtMeasurementTimes the
     *                                        indicesOfRunsAtMeasurementTimes to set
     */
    public void setIndicesOfRunsAtMeasurementTimes(List<Integer> indicesOfRunsAtMeasurementTimes) {
        this.indicesOfRunsAtMeasurementTimes = indicesOfRunsAtMeasurementTimes;
    }

    /**
     * @return the uThBearingName
     */
    public String getuThBearingName() {
        return uThBearingName;
    }

    /**
     * @param uThBearingName the uThBearingName to set
     */
    public void setuThBearingName(String uThBearingName) {
        this.uThBearingName = uThBearingName;
    }

    /**
     * @return the viewedAsGraph
     */
    public boolean isViewedAsGraph() {
        return viewedAsGraph;
    }

    /**
     * @param viewedAsGraph the viewedAsGraph to set
     */
    public void setViewedAsGraph(boolean viewedAsGraph) {
        this.viewedAsGraph = viewedAsGraph;
    }

    public String toPrettyString() {
        return String.format("%1$-" + 8 + "s", massStationLabel)
                + String.format("%1$-" + 7 + "s", isotopeLabel)
                + (autoCentered() ? "auto-centered" : "");
    }

    /**
     * @return the numeratorRole
     */
    public boolean isNumeratorRole() {
        return numeratorRole;
    }

    /**
     * @param numeratorRole the numeratorRole to set
     */
    public void setNumeratorRole(boolean numeratorRole) {
        this.numeratorRole = numeratorRole;
    }

    /**
     * @return the denominatorRole
     */
    public boolean isDenominatorRole() {
        return denominatorRole;
    }

    /**
     * @param denominatorRole the denominatorRole to set
     */
    public void setDenominatorRole(boolean denominatorRole) {
        this.denominatorRole = denominatorRole;
    }

}