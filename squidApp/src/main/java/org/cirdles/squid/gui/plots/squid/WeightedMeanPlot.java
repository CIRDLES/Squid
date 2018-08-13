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
package org.cirdles.squid.gui.plots.squid;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.cirdles.squid.gui.dataViews.AbstractDataView;
import org.cirdles.squid.gui.dataViews.TicGeneratorForAxes;
import org.cirdles.squid.gui.plots.PlotDisplayInterface;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class WeightedMeanPlot extends AbstractDataView implements PlotDisplayInterface {

    private String plotTitle = "NONE";
    private SpotSummaryDetails spotSummaryDetails;
    private List<ShrimpFractionExpressionInterface> shrimpFractions;
    private List<Double> ages;
    private List<Double> ageTwoSigma;
    private List<Double> hours;
    private double[] weightedMeanStats;
    private double[] onPeakTwoSigma;
    private boolean[] rejectedIndices;

    private double standardAge;

    private int indexOfSelectedSpot;

    public WeightedMeanPlot(Rectangle bounds, String plotTitle, SpotSummaryDetails spotSummaryDetails, String ageLookupString, double standardAge) {
        super(bounds, 0, 0);
        this.plotTitle = plotTitle;
        this.spotSummaryDetails = spotSummaryDetails;
        // extract needed values
        extractFractionDetails(ageLookupString);

        this.standardAge = standardAge;
        this.indexOfSelectedSpot = -1;
        setOpacity(1.0);

        this.setOnMouseClicked(new MouseClickEventHandler());
    }

    private void extractFractionDetails(String ageLookupString) {
        shrimpFractions = spotSummaryDetails.getSelectedSpots();
        rejectedIndices = spotSummaryDetails.getRejectedIndices();

        ages = new ArrayList<>();
        ageTwoSigma = new ArrayList<>();
        hours = new ArrayList<>();
        for (ShrimpFractionExpressionInterface sf : shrimpFractions) {
            ages.add(sf.getTaskExpressionsEvaluationsPerSpotByField(ageLookupString)[0][0]);
            ageTwoSigma.add(2.0 * sf.getTaskExpressionsEvaluationsPerSpotByField(ageLookupString)[0][1]);
            hours.add(sf.getHours());
        }

        weightedMeanStats = spotSummaryDetails.getValues()[0];
    }

    private class MouseClickEventHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent mouseEvent) {
            indexOfSelectedSpot = indexOfSpotFromMouseX(mouseEvent.getX());
            repaint();
        }
    }

    private int indexOfSpotFromMouseX(double x) {
        double convertedX = convertMouseXToValue(x);
        int index = -1;
        // look up index
        for (int i = 0; i < myOnPeakNormalizedAquireTimes.length; i++) {
            if ((Math.abs(convertedX - myOnPeakNormalizedAquireTimes[i]) < 0.5)) {
                index = i;
                break;
            }
        }

        return index;
    }

    /**
     *
     * @param g2d
     */
    @Override
    public void paint(GraphicsContext g2d) {
        super.paint(g2d);

        graphWidth = 700;
        graphHeight = 300;
        leftMargin = 150;
        topMargin = 200;

        g2d.setFont(Font.font("Lucida Sans", 15));

        g2d.setStroke(Paint.valueOf("BLACK"));
        g2d.setLineWidth(0.5);

        g2d.setFill(Paint.valueOf("Red"));

        g2d.fillText(plotTitle, 45, 45);

        g2d.setFill(Paint.valueOf("Blue"));

        int rightOfText = 400;
        Text text = new Text("Wtd Mean of Ref Mat Pb/U calibr.");
        text.setFont(Font.font("Lucida Sans", 15));

        int textWidth = (int) text.getLayoutBounds().getWidth();
        g2d.fillText(text.getText(), rightOfText - textWidth, 75);
        g2d.fillText(Double.toString(weightedMeanStats[0]), rightOfText + 10, 75);

        text.setText("1%\u03C3 error of mean");
        textWidth = (int) text.getLayoutBounds().getWidth();
        g2d.fillText(text.getText(), rightOfText - textWidth, 95);
        g2d.fillText(Double.toString(weightedMeanStats[2] / weightedMeanStats[0] * 100.0), rightOfText + 10, 95);

        text.setText("1\u03C3  external spot-to-spot error");
        textWidth = (int) text.getLayoutBounds().getWidth();
        g2d.fillText(text.getText(), rightOfText - textWidth, 115);
        g2d.fillText(Double.toString(weightedMeanStats[1] / weightedMeanStats[0] * 100.0), rightOfText + 10, 115);

        text.setText("MSWD");
        textWidth = (int) text.getLayoutBounds().getWidth();
        g2d.fillText(text.getText(), rightOfText - textWidth, 135);
        g2d.fillText(Double.toString(weightedMeanStats[4]), rightOfText + 10, 135);

        text.setText("Prob. of fit");
        textWidth = (int) text.getLayoutBounds().getWidth();
        g2d.fillText(text.getText(), rightOfText - textWidth, 155);
        g2d.fillText(Double.toString(weightedMeanStats[5]), rightOfText + 10, 155);

        g2d.setLineWidth(2.0);
        for (int i = 0; i < myOnPeakData.length; i++) {
            if (rejectedIndices[i]) {
                g2d.setStroke(Paint.valueOf("Blue"));
            } else {
                g2d.setStroke(Paint.valueOf("Red"));
            }
            g2d.strokeLine(
                    mapX(myOnPeakNormalizedAquireTimes[i]),
                    mapY(myOnPeakData[i] - onPeakTwoSigma[i]),
                    mapX(myOnPeakNormalizedAquireTimes[i]),
                    mapY(myOnPeakData[i] + onPeakTwoSigma[i]));
            // - 2 sigma tic
            g2d.strokeLine(
                    mapX(myOnPeakNormalizedAquireTimes[i]) - 1,
                    mapY(myOnPeakData[i] - onPeakTwoSigma[i]),
                    mapX(myOnPeakNormalizedAquireTimes[i]) + 1,
                    mapY(myOnPeakData[i] - onPeakTwoSigma[i]));
            // age tic
            g2d.strokeLine(
                    mapX(myOnPeakNormalizedAquireTimes[i]) - 1,
                    mapY(myOnPeakData[i]),
                    mapX(myOnPeakNormalizedAquireTimes[i]) + 1,
                    mapY(myOnPeakData[i]));
            // + 2 sigma tic
            g2d.strokeLine(
                    mapX(myOnPeakNormalizedAquireTimes[i]) - 1,
                    mapY(myOnPeakData[i] + onPeakTwoSigma[i]),
                    mapX(myOnPeakNormalizedAquireTimes[i]) + 1,
                    mapY(myOnPeakData[i] + onPeakTwoSigma[i]));
        }

        // standard age
        g2d.setLineWidth(1.0);
        g2d.setStroke(Paint.valueOf("Green"));
        g2d.strokeLine(
                mapX(minX), mapY(standardAge), mapX(maxX), mapY(standardAge));

        // border and fill
        g2d.setLineWidth(0.5);
        g2d.setStroke(Paint.valueOf("Black"));
        g2d.strokeRect(
                mapX(minX),
                mapY(ticsY[ticsY.length - 1].doubleValue()),
                graphWidth,
                Math.abs(mapY(ticsY[ticsY.length - 1].doubleValue()) - mapY(ticsY[0].doubleValue())));
        g2d.setFill(new Color(1, 1, 224 / 255, 0.1));
        g2d.fillRect(
                mapX(minX),
                mapY(ticsY[ticsY.length - 1].doubleValue()),
                graphWidth,
                Math.abs(mapY(ticsY[ticsY.length - 1].doubleValue()) - mapY(ticsY[0].doubleValue())));

        g2d.setFill(Paint.valueOf("Black"));
        text.setText("2\u03C3 error bars");
        textWidth = (int) text.getLayoutBounds().getWidth();
        g2d.fillText(text.getText(), leftMargin + graphWidth - textWidth, topMargin - 10);

        // ticsY         
        float verticalTextShift = 3.2f;
        g2d.setFont(Font.font("Lucida Sans", 10));
        if (ticsY != null) {
            for (int i = 0; i < ticsY.length; i++) {
                g2d.strokeLine(
                        mapX(minX), mapY(ticsY[i].doubleValue()), mapX(maxX), mapY(ticsY[i].doubleValue()));

                // left side
                g2d.fillText(ticsY[i].movePointLeft(6).toBigInteger().toString(),//
                        (float) mapX(minX) - 25f,
                        (float) mapY(ticsY[i].doubleValue()) + verticalTextShift);
                // right side
                g2d.fillText(ticsY[i].movePointLeft(6).toBigInteger().toString(),//
                        (float) mapX(maxX) + 5f,
                        (float) mapY(ticsY[i].doubleValue()) + verticalTextShift);
            }
        }

        // ticsX 
        if (ticsX != null) {
            for (int i = 0; i < ticsX.length - 1; i++) {
                try {
                    g2d.strokeLine(
                            mapX(ticsX[i].doubleValue()),
                            mapY(ticsY[0].doubleValue()),
                            mapX(ticsX[i].doubleValue()),
                            mapY(ticsY[0].doubleValue()) + 5);

                    // bottom
                    g2d.fillText(ticsX[i].toBigInteger().toString(),//
                            (float) mapX(ticsX[i].doubleValue()) - 5f,
                            (float) mapY(ticsY[0].doubleValue()) + 15);

                } catch (Exception e) {
                }
            }
        }

        g2d.setFont(Font.font("Lucida Sans", 15));

        // Y - label
        text.setText("Ref Mat Age (Ma)");
        g2d.rotate(-90);
        g2d.fillText(text.getText(), -400, 100);
        g2d.rotate(90);

        // X- label
        text.setText("Hours");
        textWidth = (int) text.getLayoutBounds().getWidth();
        g2d.fillText(text.getText(), leftMargin + (graphWidth - textWidth) / 2, topMargin + graphHeight + 35);

        // provide highlight and info about selected spot
        g2d.setFont(Font.font("Lucida Sans", 12));
        if (indexOfSelectedSpot >= 0) {
            // gray spot rectangle
            g2d.setFill(Color.rgb(0, 0, 0, 0.2));
            g2d.fillRect(
                    mapX(myOnPeakNormalizedAquireTimes[indexOfSelectedSpot]) - 6,
                    mapY(maxY) - 20,
                    12,
                    graphHeight + 40);
            g2d.setFill(Paint.valueOf("BLACK"));
            Text spotID = new Text(shrimpFractions.get(indexOfSelectedSpot).getFractionID());
            spotID.applyCss();
            g2d.fillText(
                    shrimpFractions.get(indexOfSelectedSpot).getFractionID()
                    + "  Age = " + new BigDecimal(myOnPeakData[indexOfSelectedSpot]).movePointLeft(6).setScale(2, RoundingMode.HALF_UP).toPlainString()
                    + " ±" + new BigDecimal(onPeakTwoSigma[indexOfSelectedSpot]).movePointLeft(6).setScale(2, RoundingMode.HALF_UP).toPlainString() + " Ma",
                    mapX(myOnPeakNormalizedAquireTimes[indexOfSelectedSpot]) - spotID.getLayoutBounds().getWidth(),
                    mapY(minY) + 35 + spotID.getLayoutBounds().getHeight());
        }

    }

    /**
     *
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void preparePanel() {

        myOnPeakData = ages.stream().mapToDouble(Double::doubleValue).toArray();
        myOnPeakNormalizedAquireTimes = hours.stream().mapToDouble(Double::doubleValue).toArray();
        onPeakTwoSigma = ageTwoSigma.stream().mapToDouble(Double::doubleValue).toArray();

        setDisplayOffsetY(0.0);

        setDisplayOffsetX(0.0);

        // X-axis is hours
        minX = myOnPeakNormalizedAquireTimes[0];
        maxX = myOnPeakNormalizedAquireTimes[myOnPeakNormalizedAquireTimes.length - 1];
        ticsX = TicGeneratorForAxes.generateTics(minX, maxX, (int) (graphWidth / 50.0));
        double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minX, maxX, 0.05);
        minX -= xMarginStretch;
        maxX += xMarginStretch;

        // Y-axis is ages
        minY = Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;

        for (int i = 0; i < myOnPeakData.length; i++) {
            minY = Math.min(minY, myOnPeakData[i] - onPeakTwoSigma[i]);
            maxY = Math.max(maxY, myOnPeakData[i] + onPeakTwoSigma[i]);
        }

        ticsY = TicGeneratorForAxes.generateTics(minY, maxY, (int) (graphHeight / 20.0));
        // adjust margins
        double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minY, maxY, 0.05);
        minY -= yMarginStretch;
        maxY += yMarginStretch;
    }

    @Override
    public List<Node> toolbarControlsFactory() {
        List<Node> controls = new ArrayList<>();

        return controls;
    }

    @Override
    public void setData(List<Map<String, Object>> data) {
        //plot.setData(data);
    }

    @Override
    public Node displayPlotAsNode() {
        preparePanel();
        this.repaint();
        return this;
    }

    @Override
    public void setProperty(String key, Object datum
    ) {
        getProperties().put(key, datum);
    }

    @Override
    public void setSelectedAllData(boolean selected
    ) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
