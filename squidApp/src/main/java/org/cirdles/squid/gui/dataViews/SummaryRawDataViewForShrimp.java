/*
 * SummaryRawDataViewForShrimp.java
 *
 * Copyright 2006 James F. Bowring and Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.cirdles.squid.gui.dataViews;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.cirdles.squid.shrimp.MassStationDetail;

/**
 *
 * @author James F. Bowring
 */
public class SummaryRawDataViewForShrimp extends AbstractDataView {

    private Map<Integer, MassStationDetail> massStationDetails;

    private List<double[]> allMeasuredTrimMasses;
    private List<double[]> allMeasuredTrimMassesTimes;

    /**
     *
     * @param bounds
     * @param massKey
     * @param measuredTrimMasses
     * @param timesOfMeasuredTrimMasses
     * @param massData
     */
    public SummaryRawDataViewForShrimp(///
            Rectangle bounds, Map<Integer, MassStationDetail> massStationDetails) {
        super(bounds, 100, 0);
        this.massStationDetails = massStationDetails;
    }

    /**
     *
     * @param g2d
     */
    @Override
    public void paint(GraphicsContext g2d) {
        super.paint(g2d);

        g2d.setStroke(Paint.valueOf("BLACK"));
        g2d.setLineWidth(0.5);

        g2d.setFill(Paint.valueOf("Red"));
//        g2d.fillText(massKey, 10, 15);

        int index = 0;
        for (double[] myOnPeakData : allMeasuredTrimMasses) {
            double[] myOnPeakNormalizedAquireTimes = allMeasuredTrimMassesTimes.get(index);

            g2d.beginPath();
            g2d.moveTo(mapX(myOnPeakNormalizedAquireTimes[0]), mapY(myOnPeakData[0]));
            for (int i = 0; i < myOnPeakData.length; i++) {
                // line tracing through points
                g2d.lineTo(mapX(myOnPeakNormalizedAquireTimes[i]), mapY(myOnPeakData[i]));

//                // every 6 scans
//                if ((i + 1) % 6 == 0) {
//                    g2d.setStroke(Paint.valueOf("Red"));
//                    g2d.setLineWidth(0.5);
//
//                    if (i < (myOnPeakData.length - 1)) {
//                        double runX = mapX((myOnPeakNormalizedAquireTimes[i] + myOnPeakNormalizedAquireTimes[i + 1]) / 2.0);
//                        g2d.strokeLine(runX, 0, runX, height);
//                    }
//
//                    g2d.setFont(Font.font("Lucida Sans", 8));
//                    g2d.fillText(String.valueOf((int) ((i + 1) / 6)), mapX(myOnPeakNormalizedAquireTimes[i - 4]), height - 2);
//                    g2d.setStroke(Paint.valueOf("BLACK"));
//                    g2d.setLineWidth(0.5);
//                }
                g2d.strokeOval(mapX(myOnPeakNormalizedAquireTimes[i]) - 1, mapY(myOnPeakData[i]) - 1, 2, 2);

            }

            g2d.stroke();
        }

        // tics
        if (tics != null) {
            for (int i = 0; i < tics.length; i++) {
                try {
                    g2d.strokeLine( //
                            mapX(minX), mapY(tics[i].doubleValue()), mapX(maxX), mapY(tics[i].doubleValue()));

                    g2d.fillText(tics[i].toPlainString(),//
                            (float) mapX(minX) - 35f,
                            (float) mapY(tics[i].doubleValue()) + 2.9f);
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     *
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void preparePanel() {

        setDisplayOffsetY(0.0);
        setDisplayOffsetX(100.0);

        // Y-axis is mass values
        minY = Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;

        // X-axis is time
        minX = Double.MAX_VALUE;
        maxX = -Double.MAX_VALUE;
        
        allMeasuredTrimMasses = new ArrayList<>();
        allMeasuredTrimMassesTimes = new ArrayList<>();

        // extract autoCentered masses for inclusion
        for (Map.Entry<Integer, MassStationDetail> entry : massStationDetails.entrySet()) {
            if (entry.getValue().autoCentered()) {
                double[] onPeakData = entry.getValue().getMeasuredTrimMasses().stream().mapToDouble(Double::doubleValue).toArray();
                allMeasuredTrimMasses.add(onPeakData);
                for (int i = 0; i < onPeakData.length; i++) {
                    minY = Math.min(minY, onPeakData[i]);
                    maxY = Math.max(maxY, onPeakData[i]);
                }

                double[] onPeakDataTimes = entry.getValue().getTimesOfMeasuredTrimMasses().stream().mapToDouble(Double::doubleValue).toArray();
                allMeasuredTrimMassesTimes.add(entry.getValue().getTimesOfMeasuredTrimMasses().stream().mapToDouble(Double::doubleValue).toArray());
                minX = Math.min(minX, onPeakDataTimes[0]);
                maxX = Math.max(maxX, onPeakDataTimes[onPeakDataTimes.length - 1]);

            }

            // adjust margins
            double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minY, maxY, 0.05);
            minY -= yMarginStretch;
            maxY += yMarginStretch;

            double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minX, maxX, 0.005);
            minX -= xMarginStretch;
            maxX += xMarginStretch;

            tics = TicGeneratorForAxes.generateTics(minY, maxY, (int) (graphHeight / 20.0));

        }

    }
}
