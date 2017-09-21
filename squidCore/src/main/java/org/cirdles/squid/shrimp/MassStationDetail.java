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
import java.util.List;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author James F. Bowring
 */
public class MassStationDetail implements Serializable {

    private static final long serialVersionUID = -874679604916998001L;

    private int massStationIndex;

    private final SimpleStringProperty massStationLabel;
    private final SimpleStringProperty isotopeLabel;
    private final SimpleStringProperty elementLabel;

    private boolean isBackground;

    private final double centeringTimeSec;
    private final List<Double> measuredTrimMasses;
    private final List<Double> timesOfMeasuredTrimMasses;
    private final List<Integer> indicesOfScansAtMeasurementTimes;
    private List<Integer> indicesOfRunsAtMeasurementTimes;

    public MassStationDetail(int massStationIndex, String massStationLabel, double centeringTimeSec, String isotopeLabel, String elementLabel, boolean isBackground) {
        this.massStationIndex = massStationIndex;
        this.massStationLabel = new SimpleStringProperty(massStationLabel);
        this.isotopeLabel = new SimpleStringProperty(isotopeLabel);
        this.elementLabel = new SimpleStringProperty(elementLabel);
        this.isBackground = isBackground;

        this.centeringTimeSec = centeringTimeSec;
        this.measuredTrimMasses = new ArrayList<>();
        this.timesOfMeasuredTrimMasses = new ArrayList<>();
        this.indicesOfScansAtMeasurementTimes = new ArrayList<>();
        this.indicesOfRunsAtMeasurementTimes = new ArrayList<>();
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
        return massStationLabel.get();
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
        return isotopeLabel.get();
    }

    /**
     *
     * @param label
     */
    public void setIsotopeLabel(String label) {
        isotopeLabel.set(label);
    }

    /**
     * @return the elementLabel
     */
    public String getElementLabel() {
        return elementLabel.get();
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

}
