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
package org.cirdles.squid.gui.dateInterpretations.plots.squid;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import static org.cirdles.squid.constants.Squid3Constants.ABS_UNCERTAINTY_DIRECTIVE;
import org.cirdles.squid.gui.dataViews.AbstractDataView;
import org.cirdles.squid.gui.dataViews.TicGeneratorForAxes;
import org.cirdles.squid.gui.dateInterpretations.plots.PlotDisplayInterface;
import org.cirdles.squid.gui.dateInterpretations.plots.plotControllers.PlotsController;
import static org.cirdles.squid.gui.utilities.stringUtilities.StringTester.stringIsSquidRatio;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import static org.cirdles.squid.utilities.conversionUtilities.RoundingUtilities.squid3RoundedToSize;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class WeightedMeanPlot extends AbstractDataView implements PlotDisplayInterface {

    private String plotTitle = "NONE";
    private SpotSummaryDetails spotSummaryDetails;
    private List<ShrimpFractionExpressionInterface> shrimpFractions;
    private List<ShrimpFractionExpressionInterface> storedShrimpFractions;
    private List<Double> agesOrValues;
    private List<Double> agesOrValuesTwoSigma;
    private List<Double> hours;
    private double[] weightedMeanStats;
    private double[] onPeakTwoSigma;
    private boolean[] rejectedIndices;
    private String ageOrValueLookupString;
    private boolean adaptToAgeInMA;
    private int countOfIncluded;

    private final double referenceMaterialAge;

    private int indexOfSelectedSpot;
    private final WeightedMeanRefreshInterface weightedMeanRefreshInterface;
    private final ContextMenu spotContextMenu = new ContextMenu();

    private boolean doPlotRejectedSpots;

    public WeightedMeanPlot(
            Rectangle bounds,
            String plotTitle,
            SpotSummaryDetails spotSummaryDetails,
            String ageOrValueLookupString,
            double referenceMaterialAge,
            WeightedMeanRefreshInterface weightedMeanRefreshInterface) {

        super(bounds, 0, 0);

        leftMargin = 75;
        topMargin = 200;

        this.plotTitle = plotTitle;
        this.spotSummaryDetails = spotSummaryDetails;
        // extract needed values
        this.ageOrValueLookupString = ageOrValueLookupString;
        this.adaptToAgeInMA = ageOrValueLookupString.contains("Age");
        extractSpotDetails();

        this.referenceMaterialAge = referenceMaterialAge;
        this.weightedMeanRefreshInterface = weightedMeanRefreshInterface;

        this.indexOfSelectedSpot = -1;
        this.doPlotRejectedSpots = true;

        setOpacity(1.0);

        setupSpotInWMContextMenu();

        this.setOnMouseClicked(new MouseClickEventHandler());
        this.setOnMouseMoved(new MouseMovedHandler());

        widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > 100) {
                    bounds.setWidth(newValue.intValue());
                    width = (int) bounds.getWidth();
                    graphWidth = (int) width - 2 * leftMargin;
                    displayPlotAsNode();
                }
            }
        });

        heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > 100) {
                    bounds.setHeight(newValue.intValue());
                    height = (int) bounds.getHeight();
                    graphHeight = (int) height - topMargin - topMargin / 2;
                    displayPlotAsNode();
                }
            }
        });
    }

    // https://dlsc.com/2014/04/10/javafx-tip-1-resizable-canvas/
    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefHeight(double width) {
        return this.getHeight();
    }

    @Override
    public double prefWidth(double height) {
        return this.getWidth();
    }

    private boolean extractSpotDetails() {
        storedShrimpFractions = spotSummaryDetails.getSelectedSpots();
        shrimpFractions = new ArrayList<>();

        boolean[] storedRejectedIndices = spotSummaryDetails.getRejectedIndices();
        rejectedIndices = new boolean[storedRejectedIndices.length];

        boolean retVal = storedShrimpFractions.size() > 0;
        if (retVal) {
            for (ShrimpFractionExpressionInterface sf : storedShrimpFractions) {
                shrimpFractions.add(sf);
            }

            if (PlotsController.plotTypeSelected.compareTo(PlotsController.PlotTypes.WEIGHTED_MEAN_SAMPLE) == 0) {
                Collections.sort(shrimpFractions, (ShrimpFractionExpressionInterface fraction1, ShrimpFractionExpressionInterface fraction2) -> {
                    double valueFromNode1 = 0.0;
                    double valueFromNode2 = 0.0;

                    if (stringIsSquidRatio(spotSummaryDetails.getSelectedExpressionName())) {
                        // case of raw ratios
                        double[][] resultsFromNode1
                                = Arrays.stream(fraction1
                                        .getIsotopicRatioValuesByStringName(spotSummaryDetails.getSelectedExpressionName())).toArray(double[][]::new);
                        valueFromNode1 = resultsFromNode1[0][0];
                        double[][] resultsFromNode2
                                = Arrays.stream(fraction2
                                        .getIsotopicRatioValuesByStringName(spotSummaryDetails.getSelectedExpressionName())).toArray(double[][]::new);
                        valueFromNode2 = resultsFromNode2[0][0];

                    } else {
                        // all other expressions
                        valueFromNode1 = fraction1
                                .getTaskExpressionsEvaluationsPerSpotByField(spotSummaryDetails.getSelectedExpressionName())[0][0];
                        valueFromNode2 = fraction2
                                .getTaskExpressionsEvaluationsPerSpotByField(spotSummaryDetails.getSelectedExpressionName())[0][0];

                    }

                    return Double.compare(valueFromNode1, valueFromNode2);
                });
            }

            countOfIncluded = 0;
            for (int i = 0; i < shrimpFractions.size(); i++) {
                boolean rejected = storedRejectedIndices[storedShrimpFractions.indexOf(shrimpFractions.get(i))];
                rejectedIndices[i] = rejected;
                countOfIncluded = countOfIncluded + (rejected ? 0 : 1);
            }

            agesOrValues = new ArrayList<>();
            agesOrValuesTwoSigma = new ArrayList<>();
            hours = new ArrayList<>();

            double index = 0;
            for (ShrimpFractionExpressionInterface spot : shrimpFractions) {
                double[][] results;
                if (stringIsSquidRatio(ageOrValueLookupString)) {
                    // ratio case
                    results = Arrays.stream(spot.getIsotopicRatioValuesByStringName(ageOrValueLookupString)).toArray(double[][]::new);
                } else {
                    results = spot.getTaskExpressionsEvaluationsPerSpotByField(ageOrValueLookupString);
                }

                agesOrValues.add(results[0][0]);
                // handle no uncertainty case
                if (results[0].length < 2) {
                    agesOrValuesTwoSigma.add(0.0);
                } else {
                    agesOrValuesTwoSigma.add(2.0 * results[0][1]);
                }

                if (spotSummaryDetails.getSelectedExpressionName().compareToIgnoreCase("Hours") == 0) {
                    hours.add(spot.getHours());
                } else {
                    hours.add(index++);
                }
            }

            weightedMeanStats = spotSummaryDetails.getValues()[0];
        }

        return retVal;
    }

    private class MouseMovedHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            if (mouseInHouse(event)) {
                ((Canvas) event.getSource()).getParent().getScene().setCursor(Cursor.CROSSHAIR);
            } else {
                ((Canvas) event.getSource()).getParent().getScene().setCursor(Cursor.DEFAULT);
            }
        }
    }

    private class MouseClickEventHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent mouseEvent) {
            if (mouseInHouse(mouseEvent)) {
                indexOfSelectedSpot = indexOfSpotFromMouseX(mouseEvent.getX());

                spotContextMenu.hide();
                if (getSpotSummaryDetails().isManualRejectionEnabled() && (mouseEvent.getButton().compareTo(MouseButton.SECONDARY) == 0)) {
                    try {
                        spotContextMenu.show((Node) mouseEvent.getSource(), Side.LEFT,
                                mapX(myOnPeakNormalizedAquireTimes[indexOfSelectedSpot]), mouseEvent.getY());
                    } catch (Exception e) {
                    }
                }
            } else {
                indexOfSelectedSpot = -1;
            }

            repaint();
        }
    }

    private int indexOfSpotFromMouseX(double x) {
        double convertedX = convertMouseXToValue(x);
        int index = -1;
        // look up index
        for (int i = 0; i < myOnPeakNormalizedAquireTimes.length - 1; i++) {
            if ((convertedX >= myOnPeakNormalizedAquireTimes[i])
                    && (convertedX < myOnPeakNormalizedAquireTimes[i + 1])) {
                index = i;
                break;
            }

            // handle case of last age
            if (index == -1) {
                if ((Math.abs(convertedX - myOnPeakNormalizedAquireTimes[myOnPeakNormalizedAquireTimes.length - 1]) < 0.25)) {
                    index = myOnPeakNormalizedAquireTimes.length - 1;
                }
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

        g2d.setFont(Font.font("SansSerif", FontWeight.SEMI_BOLD, 15));

        g2d.setStroke(Paint.valueOf("BLACK"));
        g2d.setLineWidth(0.5);

        g2d.setFill(Paint.valueOf("RED"));

        g2d.fillText(plotTitle, 45, 45);

        g2d.setFill(Paint.valueOf("RED"));

        Text text = new Text();
        text.setFont(Font.font("SansSerif", 15));
        int rightOfText = 450;
        int textWidth = 0;
        int widthOffset = 10;
        int currentTextHeightPixels = 75;
        int heightOffset = 18;

        if (PlotsController.plotTypeSelected.compareTo(PlotsController.PlotTypes.WEIGHTED_MEAN_SAMPLE) == 0) {
            // section for sample wms
            text.setText("Wtd Mean of " + ageOrValueLookupString);
            textWidth = (int) text.getLayoutBounds().getWidth();
            g2d.fillText(text.getText(), rightOfText - textWidth, currentTextHeightPixels);
            if (adaptToAgeInMA) {
                g2d.fillText(squid3RoundedToSize(weightedMeanStats[0] / 1e6, 5) + " Ma", rightOfText + widthOffset, currentTextHeightPixels);
            } else {
                g2d.fillText(squid3RoundedToSize(weightedMeanStats[0], 5) + "", rightOfText + widthOffset, currentTextHeightPixels);
            }

            text.setText("1-sigmaAbs");
            textWidth = (int) text.getLayoutBounds().getWidth();
            g2d.fillText(text.getText(), rightOfText - textWidth, currentTextHeightPixels += heightOffset);
            if (adaptToAgeInMA) {
                g2d.fillText(squid3RoundedToSize(weightedMeanStats[1] / 1e6, 5) + " Ma", rightOfText + widthOffset, currentTextHeightPixels);
            } else {
                g2d.fillText(squid3RoundedToSize(weightedMeanStats[1], 5) + "", rightOfText + widthOffset, currentTextHeightPixels);
            }

            text.setText("err 68");
            textWidth = (int) text.getLayoutBounds().getWidth();
            g2d.fillText(text.getText(), rightOfText - textWidth, currentTextHeightPixels += heightOffset);
            g2d.fillText(squid3RoundedToSize(weightedMeanStats[2] / (adaptToAgeInMA ? 1e6 : 1.0), 5) + "", rightOfText + widthOffset, currentTextHeightPixels);

            text.setText("err 95");
            textWidth = (int) text.getLayoutBounds().getWidth();
            g2d.fillText(text.getText(), rightOfText - textWidth, currentTextHeightPixels += heightOffset);
            g2d.fillText(squid3RoundedToSize(weightedMeanStats[3] / (adaptToAgeInMA ? 1e6 : 1.0), 5) + "", rightOfText + widthOffset, currentTextHeightPixels);

            text.setText("MSWD");
            textWidth = (int) text.getLayoutBounds().getWidth();
            g2d.fillText(text.getText(), rightOfText - textWidth, currentTextHeightPixels += heightOffset);
            g2d.fillText(squid3RoundedToSize(weightedMeanStats[4], 5) + "", rightOfText + widthOffset, currentTextHeightPixels);

            text.setText("Prob. of fit");
            textWidth = (int) text.getLayoutBounds().getWidth();
            g2d.fillText(text.getText(), rightOfText - textWidth, currentTextHeightPixels += heightOffset);
            g2d.fillText(squid3RoundedToSize(weightedMeanStats[5], 5) + "", rightOfText + widthOffset, currentTextHeightPixels);

            text.setText("n");
            textWidth = (int) text.getLayoutBounds().getWidth();
            g2d.fillText(text.getText(), rightOfText - textWidth, currentTextHeightPixels += heightOffset);
            g2d.fillText(String.valueOf(countOfIncluded) + " of " + String.valueOf(shrimpFractions.size()), rightOfText + widthOffset, currentTextHeightPixels);

        } else {

            text.setText("Wtd Mean of Ref Mat Pb/" + ((String) (ageOrValueLookupString.contains("Th") ? "Th" : "U")) + " calibr.");
            textWidth = (int) text.getLayoutBounds().getWidth();
            g2d.fillText(text.getText(), rightOfText - textWidth, currentTextHeightPixels);
            g2d.fillText(Double.toString(weightedMeanStats[0]), rightOfText + widthOffset, currentTextHeightPixels);

            text.setText("1%\u03C3 error of mean");
            textWidth = (int) text.getLayoutBounds().getWidth();
            g2d.fillText(text.getText(), rightOfText - textWidth, currentTextHeightPixels += heightOffset);
            g2d.fillText(Double.toString(weightedMeanStats[2] / weightedMeanStats[0] * 100.0), rightOfText + widthOffset, currentTextHeightPixels);

            text.setText("1\u03C3  external spot-to-spot error");
            textWidth = (int) text.getLayoutBounds().getWidth();
            g2d.fillText(text.getText(), rightOfText - textWidth, currentTextHeightPixels += heightOffset);
            g2d.fillText(Double.toString(weightedMeanStats[1] / weightedMeanStats[0] * 100.0), rightOfText + widthOffset, currentTextHeightPixels);

            text.setText("MSWD");
            textWidth = (int) text.getLayoutBounds().getWidth();
            g2d.fillText(text.getText(), rightOfText - textWidth, currentTextHeightPixels += heightOffset);
            g2d.fillText(Double.toString(weightedMeanStats[4]), rightOfText + widthOffset, currentTextHeightPixels);

            text.setText("Prob. of fit");
            textWidth = (int) text.getLayoutBounds().getWidth();
            g2d.fillText(text.getText(), rightOfText - textWidth, currentTextHeightPixels += heightOffset);
            g2d.fillText(Double.toString(weightedMeanStats[5]), rightOfText + widthOffset, currentTextHeightPixels);

        }

        g2d.setLineWidth(2.0);
        for (int i = 0; i < myOnPeakData.length; i++) {
            if (rejectedIndices[i]) {
                g2d.setStroke(Paint.valueOf("BLUE"));
            } else {
                g2d.setStroke(Paint.valueOf("RED"));
            }
            if (doPlotRejectedSpots || !rejectedIndices[i]) {
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
            } else {
                // leave a marker on bottom axis
                g2d.strokeLine(
                        mapX(myOnPeakNormalizedAquireTimes[i]),
                        mapY(ticsY[0].doubleValue()) - 1,
                        mapX(myOnPeakNormalizedAquireTimes[i]),
                        mapY(ticsY[0].doubleValue()) + 1);
            }
        }

        // plot either the reference material age or the weighted mean
        // standard age
        g2d.setLineWidth(1.0);
        g2d.setStroke(Paint.valueOf("GREEN"));
        if (PlotsController.plotTypeSelected.compareTo(PlotsController.PlotTypes.WEIGHTED_MEAN_SAMPLE) == 0) {
            g2d.strokeLine(
                    mapX(minX), mapY(weightedMeanStats[0]), mapX(maxX), mapY(weightedMeanStats[0]));
            // show plus minus 2 sigma
            g2d.setFill(new Color(153 / 255, 1, 204 / 255, 0.2));
            g2d.fillRect(
                    mapX(minX),
                    mapY(weightedMeanStats[0] + 2.0 * weightedMeanStats[1]),
                    graphWidth,
                    Math.abs(mapY(weightedMeanStats[0] + 2.0 * weightedMeanStats[1])
                            - mapY(weightedMeanStats[0] - 2.0 * weightedMeanStats[1])));
        } else {
            g2d.strokeLine(mapX(minX), mapY(referenceMaterialAge), mapX(maxX), mapY(referenceMaterialAge));
        }

        g2d.setFill(Paint.valueOf("BLACK"));
        g2d.setFont(Font.font("Monospaced", FontWeight.BOLD, 14));
        text.setText("2\u03C3 error bars");
        textWidth = (int) text.getLayoutBounds().getWidth();
        g2d.fillText(text.getText(), leftMargin + graphWidth - textWidth, topMargin);

        if (ticsY.length > 1) {
            // border and fill
            g2d.setLineWidth(0.5);
            g2d.setStroke(Paint.valueOf("BLACK"));
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
            g2d.setFill(Paint.valueOf("BLACK"));

            // ticsY         
            float verticalTextShift = 3.2f;
            g2d.setFont(Font.font("SansSerif", 10));
            if (ticsY != null) {
                for (int i = 0; i < ticsY.length; i++) {
                    g2d.strokeLine(
                            mapX(minX), mapY(ticsY[i].doubleValue()), mapX(maxX), mapY(ticsY[i].doubleValue()));

                    // left side
                    if (adaptToAgeInMA) {
                        g2d.fillText(ticsY[i].movePointLeft(6).toBigInteger().toString(),//
                                (float) mapX(minX) - 25f,
                                (float) mapY(ticsY[i].doubleValue()) + verticalTextShift);
                    } else {
                        g2d.fillText(ticsY[i].toString(),//
                                (float) mapX(minX) - 25f,
                                (float) mapY(ticsY[i].doubleValue()) + verticalTextShift);
                    }

                    // right side
                    if (adaptToAgeInMA) {
                        g2d.fillText(ticsY[i].movePointLeft(6).toBigInteger().toString(),//
                                (float) mapX(maxX) + 5f,
                                (float) mapY(ticsY[i].doubleValue()) + verticalTextShift);
                    } else {
                        g2d.fillText(ticsY[i].toString(),//
                                (float) mapX(maxX) + 5f,
                                (float) mapY(ticsY[i].doubleValue()) + verticalTextShift);
                    }

                }
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

        g2d.setFont(Font.font("SansSerif", 15));

        // Y - label
        if (PlotsController.plotTypeSelected.compareTo(PlotsController.PlotTypes.WEIGHTED_MEAN_SAMPLE) == 0) {
            text.setText((adaptToAgeInMA ? "Age (Ma)" : ageOrValueLookupString));
        } else {
            text.setText("Ref Mat Age (Ma)");
        }

        g2d.rotate(-90);
        g2d.fillText(text.getText(), -400, leftMargin - 50);
        g2d.rotate(90);

        // X- label
        StringBuilder description = new StringBuilder();
        description.append(ageOrValueLookupString).append(" ");
        description.append("ascending by ").append(spotSummaryDetails.getSelectedExpressionName());
        text.setText(description.toString());

        textWidth = (int) text.getLayoutBounds().getWidth();
        g2d.fillText(text.getText(), leftMargin + (graphWidth - textWidth) / 2, topMargin + graphHeight + 45);

        // legend
        text.setText("Legend:");
        g2d.fillText(text.getText(), leftMargin + 225, topMargin + graphHeight + 80);

        g2d.setFill(Paint.valueOf("RED"));
        text.setText("Included");
        g2d.fillText(text.getText(), leftMargin + 325, topMargin + graphHeight + 80);

        g2d.setFill(Paint.valueOf("BLUE"));
        text.setText("Excluded");
        g2d.fillText(text.getText(), leftMargin + 425, topMargin + graphHeight + 80);

        g2d.setFill(Paint.valueOf("BLACK"));
        g2d.setFont(Font.font("SansSerif", 10));
        g2d.fillText("Mouse:", leftMargin + 0, topMargin + graphHeight + 60);
        g2d.fillText(" left = spot details", leftMargin + 0, topMargin + graphHeight + 70);
        if (spotSummaryDetails.isManualRejectionEnabled()) {
            g2d.fillText(" right = spot menu", leftMargin + 0, topMargin + graphHeight + 80);
        }

        // provide highlight and info about selected spot
        g2d.setFont(Font.font("SansSerif", 11));
        if (indexOfSelectedSpot >= 0) {
            // gray spot rectangle
            g2d.setFill(Color.rgb(0, 0, 0, 0.2));
            g2d.fillRect(
                    mapX(myOnPeakNormalizedAquireTimes[indexOfSelectedSpot]) - 3,
                    mapY(ticsY[ticsY.length - 1].doubleValue()),
                    6,
                    Math.abs(mapY(ticsY[ticsY.length - 1].doubleValue()) - mapY(ticsY[0].doubleValue())));
            if (rejectedIndices[indexOfSelectedSpot]) {
                g2d.setFill(Paint.valueOf("BLUE"));
            } else {
                g2d.setFill(Paint.valueOf("RED"));
            }

            Text spotID = new Text(shrimpFractions.get(indexOfSelectedSpot).getFractionID());
            spotID.applyCss();
            g2d.fillText(
                    shrimpFractions.get(indexOfSelectedSpot).getFractionID()
                    + "  Age = " + makeAgeOrValueString(indexOfSelectedSpot),
                    mapX(myOnPeakNormalizedAquireTimes[indexOfSelectedSpot]) - spotID.getLayoutBounds().getWidth() - 25,
                    mapY(minY) + 0 + spotID.getLayoutBounds().getHeight());
        }

    }

    @Override
    public String makeAgeOrValueString(int index) {
        double[][] values;
        if (adaptToAgeInMA) {
            values
                    = shrimpFractions.get(index).getTaskExpressionsEvaluationsPerSpotByField(ageOrValueLookupString);
        } else {
            // for display when another expression is subject of weighted mean
            values
                    = shrimpFractions.get(index).getTaskExpressionsEvaluationsPerSpotByField(
                            shrimpFractions.get(index).getSelectedAgeExpressionName());
        }

        String retVal = makeAgeString(values[0][0], values[0][1]);
        if (!adaptToAgeInMA) {
            retVal += "\n\t\t" + ageOrValueLookupString + " = " + (new BigDecimal(myOnPeakData[index]).setScale(4, RoundingMode.HALF_UP).toEngineeringString());
        }

        return retVal;
    }

    public static String makeAgeString(double age, double twoSigmaUncert) {
        String retVal = "No Age calculated.";
        try {
            retVal = new BigDecimal(age)
                    .movePointLeft(6).setScale(2, RoundingMode.HALF_UP).toEngineeringString()
                    + " ±" + new BigDecimal(twoSigmaUncert)
                            .movePointLeft(6).setScale(2, RoundingMode.HALF_UP).toEngineeringString() + "Ma ";
        } catch (Exception e) {
        }
        return retVal;
    }

    public static String makeValueString(double value, double twoSigmaUncert) {
//        String retVal = "No Value calculated.";
//        try {
//            retVal = new BigDecimal(value)
//                    .setScale(3, RoundingMode.HALF_UP).toEngineeringString()
//                    + " ±" + new BigDecimal(twoSigmaUncert)
//                            .setScale(3, RoundingMode.HALF_UP).toEngineeringString() + " ";
//        } catch (Exception e) {
//        }

        Formatter formatter = new Formatter();
        formatter.format("%3.2E", value);
        if (twoSigmaUncert > 0.0) {
            formatter.format(" " + ABS_UNCERTAINTY_DIRECTIVE + "%2.2E", twoSigmaUncert).toString();
        }
        return formatter.toString() + " ";
    }

    public static String makeSimpleAgeString(double age) {
        String retVal = "No Age calculated.";
        try {
            retVal = new BigDecimal(age)
                    .movePointLeft(6).setScale(2, RoundingMode.HALF_UP).toEngineeringString()
                    + "Ma";
        } catch (Exception e) {
        }
        return retVal;
    }

    /**
     *
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void preparePanel() {

        myOnPeakData = agesOrValues.stream().mapToDouble(Double::doubleValue).toArray();
        myOnPeakNormalizedAquireTimes = hours.stream().mapToDouble(Double::doubleValue).toArray();
        onPeakTwoSigma = agesOrValuesTwoSigma.stream().mapToDouble(Double::doubleValue).toArray();

        minY = Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;
        minX = Double.MAX_VALUE;
        maxX = -Double.MAX_VALUE;

        setDisplayOffsetY(0.0);
        setDisplayOffsetX(0.0);

        // X-axis is hours or counts depending on sorting order
        minX = myOnPeakNormalizedAquireTimes[0];
        maxX = myOnPeakNormalizedAquireTimes[myOnPeakNormalizedAquireTimes.length - 1];
        ticsX = TicGeneratorForAxes.generateTics(minX, maxX, (int) (graphWidth / 50.0));
        double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minX, maxX, 0.05);
        minX -= xMarginStretch;
        maxX += xMarginStretch;

        // Y-axis is agesOrValues
        minY = Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;

        for (int i = 0; i < myOnPeakData.length; i++) {
            if (doPlotRejectedSpots || !rejectedIndices[i]) {
                minY = Math.min(minY, myOnPeakData[i] - onPeakTwoSigma[i]);
                maxY = Math.max(maxY, myOnPeakData[i] + onPeakTwoSigma[i]);
            }
        }

        ticsY = TicGeneratorForAxes.generateTics(minY, maxY, (int) (graphHeight / 20.0));

        // check for no data
        if ((ticsY != null) && (ticsY.length > 1)) {
            // force y to tics
            minY = ticsY[0].doubleValue();
            maxY = ticsY[ticsY.length - 1].doubleValue();
            // adjust margins
            double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minY, maxY, 0.05);
            minY -= yMarginStretch;
            maxY += yMarginStretch;
        }
    }

    private void setupSpotInWMContextMenu() {
        MenuItem menuItem1 = new MenuItem("Toggle Exclusion of this spot.");
        menuItem1.setOnAction((evt) -> {
            if (indexOfSelectedSpot > -1) {
                weightedMeanRefreshInterface.toggleSpotExclusionWM(indexOfSelectedSpot);
                weightedMeanRefreshInterface.calculateWeightedMean();
                weightedMeanRefreshInterface.refreshPlot();
            }
        });
        spotContextMenu.getItems().addAll(menuItem1);

        MenuItem menuItem2 = new MenuItem("Toggle Show Excluded Spots");
        menuItem2.setOnAction((evt) -> {
            doPlotRejectedSpots = !doPlotRejectedSpots;
            refreshPanel(false, false);
        });
        spotContextMenu.getItems().addAll(menuItem2);
    }

    @Override
    public List<Node> toolbarControlsFactory() {
        List<Node> controls = new ArrayList<>();

        return controls;
    }

    @Override
    public void setData(List<Map<String, Object>> data) {
        // do nothing
    }

    @Override
    public Node displayPlotAsNode() {
        if (extractSpotDetails()) {
            preparePanel();
            this.repaint();
        }
        return this;
    }

//    private SVGGraphics2D svgGenerator = null;
//
//    public void outputToSVG(File file) {
//
////        File file2 = new File("TEST.SVG");
////        //Bounds dim = this.getBoundsInLocal();
////        Document doc = SVGDOMImplementation.getDOMImplementation()
////                .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
////
////        try {
////            Writer writer = new BufferedWriter(new FileWriter(file2));
////            TranscoderOutput output = new TranscoderOutput(writer);
////            Bounds bounds = this.getBoundsInLocal();
////            Rectangle2D rec = new Rectangle2D(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
////            
////            svgGenerator = new SVGGraphics2D(doc);
////            JFXConverter converter = new JFXConverter();
////            converter.convert(svgGenerator, this);
////            
////            finishTranscoding(rec, output);
////            writer.flush();
////        } catch (IOException | TranscoderException iOException) {
////        }
//        // Get a DOMImplementation.
//        DOMImplementation domImpl
//                = GenericDOMImplementation.getDOMImplementation();
//
//        // Create an instance of org.w3c.dom.Document.
//        String svgNS = "http://www.w3.org/2000/svg";
//        Document document = domImpl.createDocument(svgNS, "svg", null);
//
//        // Create an instance of the SVG Generator.
//        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
//
//        // Ask the test to render into the SVG Graphics2D implementation.
//        GraphicsContext gc = this.getGraphicsContext2D();
//        paint(gc);
//
//        File file2 = new File("TEST.PNG");
//        WritableImage wim = new WritableImage((int) this.getWidth(), (int) this.getHeight());
//        this.snapshot(null, wim);
//        try {
//            ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", file2);
//
//        } catch (IOException iOException) {
//        }
//
////        // Finally, stream out SVG to the standard output using
////        // UTF-8 encoding.
////        boolean useCSS = true; // we want to use CSS style attributes
////           
////        Writer out = null;
////        try {
////            out = new OutputStreamWriter(new FileOutputStream(file2), "UTF-8");
////        } catch (FileNotFoundException | UnsupportedEncodingException fileNotFoundException) {
////        }
////        try {
////            svgGenerator.stream(out, useCSS);
////        } catch (SVGGraphics2DIOException sVGGraphics2DIOException) {
////            System.out.println(sVGGraphics2DIOException.getMessage());
////        }
////
////        // aug 2013
////        // read svg file back in to add clip size to comments
////        
////
////
////        // Get a DOMImplementation.
////        DOMImplementation domImpl
////                = GenericDOMImplementation.getDOMImplementation();
////
////        // Create an instance of org.w3c.dom.Document.
////        String svgNS = "http://www.w3.org/2000/svg";
////        Document document = domImpl.createDocument(svgNS, "svg", null);
////
////        // Create an instance of the SVG Generator.
////        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
////
////        JFXConverter converter = new JFXConverter();
////        converter.convert(svgGenerator, this);
////
////        // Ask the test to render into the SVG Graphics2D implementation.
//////        paint(converter);
////        // Finally, stream out SVG to the standard output using
////        // UTF-8 encoding.
////        boolean useCSS = true; // we want to use CSS style attributes
//////            File file2 = new File("TEST.SVG");
////        Writer out = null;
////        try {
////            out = new OutputStreamWriter(new FileOutputStream(file2), "UTF-8");
////        } catch (FileNotFoundException | UnsupportedEncodingException fileNotFoundException) {
////        }
////        try {
////            svgGenerator.stream(out, useCSS);
////        } catch (SVGGraphics2DIOException sVGGraphics2DIOException) {
////            System.out.println(sVGGraphics2DIOException.getMessage());
////        }
////
////        // aug 2013
////        // read svg file back in to add clip size to comments
//    }
//
//    private void finishTranscoding(Rectangle2D rec, TranscoderOutput output)
//            throws TranscoderException {
//
//        // get the root element and add size
//        String minX1 = Double.toString(rec.getMinX());
//        String minY1 = Double.toString(rec.getMinY());
//        String width1 = Double.toString(rec.getWidth());
//        String height1 = Double.toString(rec.getHeight());
//        String size = minX1 + " " + minY1 + " " + width1 + " " + height1;
//
//        Element svgRoot = svgGenerator.getRoot();
//        svgRoot.setAttributeNS(null, "viewBox", size);
//
//        //testOutput(doc, new File(System.getProperty("user.dir"),"test.xml"));
//        // Now, write the SVG content to the output
//        writeSVGToOutput(svgRoot, output);
//    }
//
//    protected void writeSVGToOutput(Element svgRoot, TranscoderOutput output) throws TranscoderException {
//        try {
//            // Writer
//            Writer wr = output.getWriter();
//            if (wr != null) {
//                svgGenerator.stream(svgRoot, wr);
//                return;
//            }
//        } catch (IOException e) {
//            throw new TranscoderException(e);
//        }
//    }
//
//    /**
//     *
//     * @param file
//     */
//    public void outputToPDF(File file) {
//        SVGConverter myConv = new SVGConverter();
//        myConv.setDestinationType(org.apache.batik.apps.rasterizer.DestinationType.PDF);
//        try {
//            myConv.setSources(new String[]{file.getCanonicalPath()});
//
//        } catch (IOException iOException) {
//        }
//        myConv.setWidth((float) getWidth() + 2);
//        myConv.setHeight((float) getHeight() + 2);
//
//        try {
//            myConv.execute();
//
//        } catch (SVGConverterException sVGConverterException) {
//            System.out.println("Error in pdf conversion: " + sVGConverterException.getMessage());
//        }
//    }

    @Override
    public void setProperty(String key, Object datum) {
        getProperties().put(key, datum);
    }

    /**
     * @return the spotSummaryDetails
     */
    public SpotSummaryDetails getSpotSummaryDetails() {
        return spotSummaryDetails;
    }

    /**
     * @param spotSummaryDetails the spotSummaryDetails to set
     */
    public void setSpotSummaryDetails(SpotSummaryDetails spotSummaryDetails) {
        this.spotSummaryDetails = spotSummaryDetails;
    }

    /**
     * @param ageOrValueLookupString the ageOrValueLookupString to set
     */
    public void setAgeOrValueLookupString(String ageOrValueLookupString) {
        this.ageOrValueLookupString = ageOrValueLookupString;
    }

    /**
     * @return the ageOrValueLookupString
     */
    public String getAgeOrValueLookupString() {
        return ageOrValueLookupString;
    }

    /**
     * @return the rejectedIndices
     */
    public boolean[] getRejectedIndices() {
        return rejectedIndices;
    }

}
