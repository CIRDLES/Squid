/*
 * RawCountsDataViewForShrimp.java
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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

import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.cirdles.squid.projects.SquidProject;

/**
 *
 * @author James F. Bowring
 */
public class RawDataViewForShrimp extends AbstractDataView {

    private SquidProject squidProject;
    private String massKey = "NONE";

    /**
     *
     * @param sampleSessionDataView
     * @param tripoliFraction
     * @param rawIsotopeDataModel
     * @param bounds
     * @param invokeMouseListener
     */
    public RawDataViewForShrimp(///
            Rectangle bounds, SquidProject squidProject) {
        super(bounds);
        this.squidProject = squidProject;
    }

    /**
     *
     * @param g2d
     */
    @Override
    public void paint(GraphicsContext g2d) {
        super.paint(g2d);

        g2d.setStroke(Paint.valueOf("BLACK"));
        g2d.setLineWidth(1.5);

        g2d.strokeText(massKey, 10, 15);

        g2d.beginPath();
        g2d.moveTo(mapX(myOnPeakNormalizedAquireTimes[0]), mapY(myOnPeakData[0]));
        for (int i = 0; i < myOnPeakData.length; i++) {
            // line tracing through points
            g2d.lineTo(mapX(myOnPeakNormalizedAquireTimes[i]), mapY(myOnPeakData[i]));

            // every 6 scans
            if (((i + 1) % 6 == 0) && (i < (myOnPeakData.length - 1))) {
                double runX = mapX((myOnPeakNormalizedAquireTimes[i] + myOnPeakNormalizedAquireTimes[i + 1]) / 2.0);
                g2d.setStroke(Paint.valueOf("Red"));
                g2d.setLineWidth(1.0);
                g2d.strokeLine(runX, 0, runX, height);
                g2d.setStroke(Paint.valueOf("BLACK"));
                g2d.setLineWidth(1.5);
            }

//            g2d.setStroke(new BasicStroke(0.5f));
//            g2d.setPaint(determineDataColor(i, Color.GRAY));
//            g2d.draw(pointTrace);
//
//            Shape intensity = new java.awt.geom.Ellipse2D.Double( //
//                    mapX(myOnPeakNormalizedAquireTimes[i]) - 1, mapY(myOnPeakData[i]) - 1, 2, 2);
//            g2d.setStroke(new BasicStroke(1.5f));
//            g2d.setPaint(determineDataColor(i, Color.black));
//            g2d.draw(intensity);
            g2d.strokeOval(mapX(myOnPeakNormalizedAquireTimes[i]) - 2, mapY(myOnPeakData[i]) - 2, 4, 4);
//
//            // uncertainty
//            Shape plusMinusOneSigma = new Line2D.Double(//
//                    mapX(myOnPeakNormalizedAquireTimes[i]),// 
//                    mapY(myOnPeakData[i] - myOnPeakOneSigmas[i]),//
//                    mapX(myOnPeakNormalizedAquireTimes[i]),// 
//                    mapY(myOnPeakData[i] + myOnPeakOneSigmas[i]));
//            g2d.setStroke(new BasicStroke(1.0f));
//            g2d.draw(plusMinusOneSigma);
//
//            // tips of uncertainty
//            Shape plusOneSigmaTip = new Line2D.Double(//
//                    mapX(myOnPeakNormalizedAquireTimes[i]) - 1,// 
//                    mapY(myOnPeakData[i] + myOnPeakOneSigmas[i]),//
//                    mapX(myOnPeakNormalizedAquireTimes[i]) + 1,// 
//                    mapY(myOnPeakData[i] + myOnPeakOneSigmas[i]));
//
//            Shape minusOneSigmaTip = new Line2D.Double(//
//                    mapX(myOnPeakNormalizedAquireTimes[i]) - 1,// 
//                    mapY(myOnPeakData[i] - myOnPeakOneSigmas[i]),//
//                    mapX(myOnPeakNormalizedAquireTimes[i]) + 1,// 
//                    mapY(myOnPeakData[i] - myOnPeakOneSigmas[i]));
//
//            g2d.setStroke(new BasicStroke(1.0f));
//            g2d.draw(plusOneSigmaTip);
//            g2d.draw(minusOneSigmaTip);
        }

        g2d.stroke();
    }

    /**
     *
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void preparePanel() {
        //196Zr2O => [[195.765627, 195.765511, 195.763746, 195.76466, 195.764639, 195.764332], [1.289488671E12, 1.289488854E12, 1.289489035E12, 1.289489217E12, 1.289489399E12, 1.289489581E12]]

        myOnPeakData = new double[]{195.765627, 195.765511, 195.763746, 195.76466, 195.764639, 195.764332};

        myOnPeakNormalizedAquireTimes = new double[]{1.289488671E12, 1.289488854E12, 1.289489035E12, 1.289489217E12, 1.289489399E12, 1.289489581E12};

        massKey = (String) squidProject.getMassTimeSeries().keySet().toArray()[5];

        List<Double> peaksList = squidProject.getMassTimeSeries().get(massKey).get(0);
        myOnPeakData = peaksList.stream().mapToDouble(Double::doubleValue).toArray();

        List<Double> timesList = squidProject.getMassTimeSeries().get(massKey).get(1);
        myOnPeakNormalizedAquireTimes = timesList.stream().mapToDouble(Double::doubleValue).toArray();

        setDisplayOffsetY(0.0);

        setDisplayOffsetX(0.0);

        // X-axis lays out time evenly spaced
        minX = myOnPeakNormalizedAquireTimes[0];
        maxX = myOnPeakNormalizedAquireTimes[myOnPeakNormalizedAquireTimes.length - 1];
        double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minX, maxX, 0.005);
        minX -= xMarginStretch;
        maxX += xMarginStretch;

        // Y-axis is intensities as voltages plus or minus
        minY = Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;

        // on peak
        for (int i = 0; i < myOnPeakData.length; i++) {
            minY = Math.min(minY, myOnPeakData[i]);
            maxY = Math.max(maxY, myOnPeakData[i]);
        }

        // adjust margins
        double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minY, maxY, 0.05);
        minY -= yMarginStretch;
        maxY += yMarginStretch;

    }

}
