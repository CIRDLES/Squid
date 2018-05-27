/* 
 * Copyright 2018 James F. Bowring and CIRDLES.org.
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

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 *
 * @author ryanb
 */
public class MassStationDetailXMLConverter implements Converter {

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {
        MassStationDetail station = (MassStationDetail) o;

        writer.startNode("massStationIndex");
        writer.setValue(Integer.toString(station.getMassStationIndex()));
        writer.endNode();

        writer.startNode("massStationLabel");
        writer.setValue(station.getMassStationLabel());
        writer.endNode();

        writer.startNode("elementLabel");
        writer.setValue(station.getElementLabel());
        writer.endNode();

        writer.startNode("isotopeLabel");
        writer.setValue(station.getIsotopeLabel());
        writer.endNode();

        writer.startNode("taskIsotopeLabel");
        writer.setValue(station.getTaskIsotopeLabel());
        writer.endNode();

        writer.startNode("isBackground");
        writer.setValue(String.valueOf(station.getIsBackground()));
        writer.endNode();

        writer.startNode("centeringTimeSec");
        writer.setValue(Double.toString(station.getCenteringTimeSec()));
        writer.endNode();


        writer.startNode("uThBearingName");
        writer.setValue(station.getuThBearingName());
        writer.endNode();
        
//        writer.startNode("measuredTrimMasses");
//        List<Double> measuredTrimMasses = station.getMeasuredTrimMasses();
//        for (int i = 0; i < measuredTrimMasses.size(); i++) {
//            writer.startNode("measuredTrimMass");
//            writer.setValue(String.valueOf(measuredTrimMasses.get(i)));
//            writer.endNode();
//        }
//        writer.endNode();
//
//        writer.startNode("timesOfMeasuredTrimMasses");
//        List<Double> timesOfMeasuredTrimMasses = station.getTimesOfMeasuredTrimMasses();
//        for (int i = 0; i < timesOfMeasuredTrimMasses.size(); i++) {
//            writer.startNode("timeOfMeasuredTrimMass");
//            writer.setValue(Double.toString(timesOfMeasuredTrimMasses.get(i)));
//            writer.endNode();
//        }
//        writer.endNode();
//
//        writer.startNode("indicesOfScansAtMeasurementTimes");
//        List<Integer> indicesOfScansAtMeasurementTimes = station.getIndicesOfScansAtMeasurementTimes();
//        for (int i = 0; i < indicesOfScansAtMeasurementTimes.size(); i++) {
//            writer.startNode("indexOfScanAtMeasurementTime");
//            writer.setValue(Integer.toString(indicesOfScansAtMeasurementTimes.get(i)));
//            writer.endNode();
//        }
//        writer.endNode();
//
//        writer.startNode("indicesOfRunsAtMeasurementTimes");
//        List<Integer> indicesOfRunsAtMeasurementTimes = station.getIndicesOfRunsAtMeasurementTimes();
//        for (int i = 0; i < indicesOfRunsAtMeasurementTimes.size(); i++) {
//            writer.startNode("indexOfRunAtMeasurementTime");
//            writer.setValue(Integer.toString(indicesOfRunsAtMeasurementTimes.get(i)));
//            writer.endNode();
//        }
//        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {
        int massStationIndex;
        String massStationLabel;
        double centeringTimeSec;
        String isotopeLabel;
        String elementLabel;
        boolean isBackground;
        String uThBearingName;
        String taskIsotopeLabel;

        reader.moveDown();
        massStationIndex = Integer.valueOf(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        massStationLabel = reader.getValue();
        reader.moveUp();

        reader.moveDown();
        elementLabel = reader.getValue();
        reader.moveUp();

        reader.moveDown();
        isotopeLabel = reader.getValue();
        reader.moveUp();

        reader.moveDown();
        taskIsotopeLabel = reader.getValue();
        reader.moveUp();

        reader.moveDown();
        isBackground = Boolean.valueOf(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        centeringTimeSec = Double.valueOf(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        uThBearingName = reader.getValue();
        reader.moveUp();

        MassStationDetail station = new MassStationDetail(massStationIndex,
                massStationLabel, centeringTimeSec, isotopeLabel, elementLabel,
                isBackground, uThBearingName);
        station.setTaskIsotopeLabel(taskIsotopeLabel);

//        List<Double> measuredTrimMasses = station.getMeasuredTrimMasses();
//        reader.moveDown();
//        while (reader.hasMoreChildren()) {
//            reader.moveDown();
//            measuredTrimMasses.add(Double.parseDouble(reader.getValue()));
//            reader.moveUp();
//        }
//        reader.moveUp();
//
//        List<Double> timesOfMeasuredTrimMasses = station.getTimesOfMeasuredTrimMasses();
//        reader.moveDown();
//        while (reader.hasMoreChildren()) {
//            reader.moveDown();
//            timesOfMeasuredTrimMasses.add(Double.parseDouble(reader.getValue()));
//            reader.moveUp();
//        }
//        reader.moveUp();
//
//        List<Integer> indicesOfScansAtMeasurementTimes = station.getIndicesOfScansAtMeasurementTimes();
//        reader.moveDown();
//        while (reader.hasMoreChildren()) {
//            reader.moveDown();
//            indicesOfScansAtMeasurementTimes.add(Integer.parseInt(reader.getValue()));
//            reader.moveUp();
//        }
//        reader.moveUp();
//
//        List<Integer> indicesOfRunsAtMeasurementTimes = station.getIndicesOfRunsAtMeasurementTimes();
//        reader.moveUp();
//        while (reader.hasMoreChildren()) {
//            reader.moveDown();
//            indicesOfRunsAtMeasurementTimes.add(Integer.parseInt(reader.getValue()));
//            reader.moveUp();
//        }
//        reader.moveUp();
        
        return station;
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(MassStationDetail.class);
    }
}
