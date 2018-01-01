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
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author James F. Bowring
 */
public class MassStationDetail implements Serializable {

    private static final long serialVersionUID = -874679604916998001L;

    private int massStationIndex;

    private final String massStationLabel;
    private final String elementLabel;
    private String isotopeLabel;
    private String taskIsotopeLabel;

    private boolean isBackground;

    private final double centeringTimeSec;
    private final List<Double> measuredTrimMasses;
    private final List<Double> timesOfMeasuredTrimMasses;
    private final List<Integer> indicesOfScansAtMeasurementTimes;
    private List<Integer> indicesOfRunsAtMeasurementTimes;

    private String uThBearingName;

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
        this.taskIsotopeLabel = isotopeLabel;
        this.elementLabel = elementLabel;
        this.isBackground = isBackground;

        this.centeringTimeSec = centeringTimeSec;
        this.measuredTrimMasses = new ArrayList<>();
        this.timesOfMeasuredTrimMasses = new ArrayList<>();
        this.indicesOfScansAtMeasurementTimes = new ArrayList<>();
        this.indicesOfRunsAtMeasurementTimes = new ArrayList<>();

        this.uThBearingName = uThBearingName;
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
     *
     * @param label
     */
    public void setIsotopeLabel(String label) {
        isotopeLabel = label;
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

    /**
     * @return the elementLabel
     */
    public String getElementLabel() {
        return elementLabel;
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
     * indicesOfRunsAtMeasurementTimes to set
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

}
