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
package org.cirdles.squid.projects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author James F. Bowring
 */
public class MassStationDetail implements Serializable {

    private static final long serialVersionUID = -874679604916998001L;
//    private void readObject(
//            ObjectInputStream stream)
//            throws IOException, ClassNotFoundException {
//        stream.defaultReadObject();
//        ObjectStreamClass myObject = ObjectStreamClass.lookup(Class.forName(MassStationDetail.class.getCanonicalName()));
//        long theSUID = myObject.getSerialVersionUID();
//        System.out.println("Customized De-serialization of MassStationDetail " + theSUID);
//    }

    private String massStationLabel;
    private double centeringTimeSec;
    private String isotopeLabel;
    private List<Double> measuredTrimMasses;
    private List<Double> timesOfMeasuredTrimMasses;

    private MassStationDetail() {
    }

    public MassStationDetail(String massStationLabel, double centeringTimeSec, String isotopeLabel) {
        this.massStationLabel = massStationLabel;
        this.centeringTimeSec = centeringTimeSec;
        this.isotopeLabel = isotopeLabel;

        measuredTrimMasses = new ArrayList<>();
        timesOfMeasuredTrimMasses = new ArrayList<>();
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

}
