/*
 * RawCountsDataViewForShrimp.java
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import org.cirdles.ludwig.squid25.SquidMathUtils;

/**
 *
 * @author James F. Bowring
 */
public class PrimaryBeamAuditViewForShrimp extends AbstractDataView {

    private final List<Double> measuredTrimMasses;
    private final List<Double> timesOfMeasuredTrimMasses;
    private final List<Integer> indicesOfScansAtMeasurementTimes;
    private final List<Integer> indicesOfRunsAtMeasurementTimes;

    private int[] runIndices;

    /**
     *
     * @param bounds
     * @param massKey
     * @param measuredTrimMasses
     * @param timesOfMeasuredTrimMasses
     * @param indicesOfScansAtMeasurementTimes
     * @param indicesOfRunsAtMeasurementTimes
     */
    public PrimaryBeamAuditViewForShrimp(///
            Rectangle bounds,
            List<Double> measuredTrimMasses,
            List<Double> timesOfMeasuredTrimMasses,
            List<Integer> indicesOfScansAtMeasurementTimes,
            List<Integer> indicesOfRunsAtMeasurementTimes,
            boolean showTimeNormalized) {

        super(bounds, 250, 0);
        this.measuredTrimMasses = measuredTrimMasses;
        this.timesOfMeasuredTrimMasses = timesOfMeasuredTrimMasses;
        this.indicesOfScansAtMeasurementTimes = indicesOfScansAtMeasurementTimes;
        this.indicesOfRunsAtMeasurementTimes = indicesOfRunsAtMeasurementTimes;

        this.showTimeNormalized = showTimeNormalized;

        setOpacity(1.0);
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
        g2d.fillText("Primary Beam", 5, 15);

        g2d.beginPath();
        g2d.moveTo(mapX(myOnPeakNormalizedAquireTimes[0]) + 2.0f, mapY(myOnPeakData[0]));
        for (int i = 0; i < myOnPeakData.length; i++) {
            // line tracing through points
            g2d.lineTo(mapX(myOnPeakNormalizedAquireTimes[i]) + 2.0f, mapY(myOnPeakData[i]));

            // vertical lines just before scan # = 1
            if (i > 0) {
                g2d.setStroke(Paint.valueOf("Red"));
                g2d.setLineWidth(0.5);

                if (i < (myOnPeakData.length - 0)) {
                    double runX = mapX(myOnPeakNormalizedAquireTimes[i]) + 2.0f;
                    g2d.strokeLine(runX, 0, runX, height);
                }
            }

            g2d.setFont(Font.font("Lucida Sans", 8));
            g2d.fillText(String.valueOf(runIndices[i]), mapX(myOnPeakNormalizedAquireTimes[i + 1]) - 15f, height - 1.5);

            g2d.strokeOval(mapX(myOnPeakNormalizedAquireTimes[i]) + 2, mapY(myOnPeakData[i]) - 1, 2, 2);

        }

        g2d.stroke();

        // tics
        float verticalTextShift = 3.1f;
        g2d.setFont(Font.font("Lucida Sans", 10));
        if (tics != null) {
            for (int i = 0; i < tics.length; i++) {
                try {
                    g2d.strokeLine(
                            mapX(minX), mapY(tics[i].doubleValue()), mapX(maxX), mapY(tics[i].doubleValue()));

                    g2d.fillText(tics[i].toPlainString(),//
                            (float) mapX(minX) - 50f,
                            (float) mapY(tics[i].doubleValue()) + verticalTextShift);
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

        myOnPeakData = measuredTrimMasses.stream().mapToDouble(Double::doubleValue).toArray();

        myOnPeakNormalizedAquireTimes = timesOfMeasuredTrimMasses.stream().mapToDouble(Double::doubleValue).toArray();

        if (showTimeNormalized) {
            myOnPeakNormalizedAquireTimes = new double[myOnPeakNormalizedAquireTimes.length - 1];
            for (int i = 0; i < myOnPeakNormalizedAquireTimes.length; i++) {
                myOnPeakNormalizedAquireTimes[i] = i;
            }
        }

        runIndices = indicesOfRunsAtMeasurementTimes.stream().mapToInt(Integer::intValue).toArray();

        setDisplayOffsetY(0.0);

        setDisplayOffsetX(0.0);

        // X-axis lays out time evenly spaced
        minX = myOnPeakNormalizedAquireTimes[0];
        maxX = myOnPeakNormalizedAquireTimes[myOnPeakNormalizedAquireTimes.length - 1];
        double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minX, maxX, 0.005);
        minX -= xMarginStretch;
        maxX += xMarginStretch;

        // Y-axis is masses
        minY = Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;

        // on peak
        for (int i = 0; i < myOnPeakData.length; i++) {
            minY = Math.min(minY, myOnPeakData[i]);
            maxY = Math.max(maxY, myOnPeakData[i]);
        }

        // force plot max and min
        minY = minY - 0.0002;
        maxY = maxY + 0.0002;

        // adjust margins
        double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minY, maxY, 0.05);
        minY -= yMarginStretch;
        maxY += yMarginStretch;

        tics = TicGeneratorForAxes.generateTics(minY, maxY, (int) (graphHeight / 20.0));

    }

}
