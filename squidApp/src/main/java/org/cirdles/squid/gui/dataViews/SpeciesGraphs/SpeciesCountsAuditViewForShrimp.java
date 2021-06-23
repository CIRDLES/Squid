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
package org.cirdles.squid.gui.dataViews.SpeciesGraphs;

import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.cirdles.ludwig.squid25.SquidMathUtils;
import org.cirdles.squid.dialogs.SquidMessageDialog;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.gui.dataViews.AbstractDataView;
import org.cirdles.squid.gui.dataViews.MassAuditRefreshInterface;
import org.cirdles.squid.gui.dataViews.TicGeneratorForAxes;
import org.cirdles.squid.prawn.PrawnFile.Run;
import org.cirdles.squid.projects.SquidProject;

import java.util.ArrayList;
import java.util.List;

import static org.cirdles.squid.gui.MassesAuditController.LEGEND_WIDTH;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;

/**
 * @author James F. Bowring
 */
public class SpeciesCountsAuditViewForShrimp extends AbstractDataView implements SpeciesGraphInterface {

    private final MassAuditRefreshInterface massAuditRefreshInterface;
    private final ContextMenu spotContextMenu = new ContextMenu();
    private final int countsRadioButtonChoice;
    // -1, 0, 1
    private final int leadingZoomingTrailing;
    private List<Double> totalCounts;
    private List<Double> totalCountsSBM;
    private List<Double> timesOfMeasuredTrimMasses;
    private List<Integer> indicesOfScansAtMeasurementTimes = new ArrayList<>();
    private List<Integer> indicesOfRunsAtMeasurementTimes;
    private List<Run> prawnFileRuns;
    private int[] countOfScansCumulative;
    private double[] myOnPeakDataII;
    private double minYII;
    private double maxYII;
    private String plotTitle = "NONE";
    private int[] scanIndices;
    private int[] runIndices;
    //    private List<Run> selectedRuns = new ArrayList<>();
    private MenuItem spotContextMenuItem1;
    private Menu spotRestoreMenu;
    private Menu prawnFileSplitMenu;
    private MenuItem splitRunsOriginalMenuItem;
    private MenuItem splitRunsEditedMenuItem;
    private double controlMinY = 0;
    private double controlMaxY = 0;
    private double controlMinYII = 0;
    private double controlMaxYII = 0;

    /**
     * @param bounds
     * @param plotTitle
     * @param totalCounts
     * @param totalCountsSBM
     * @param countsRadioButtonChoice
     * @param timesOfMeasuredTrimMasses
     * @param indicesOfScansAtMeasurementTimes
     * @param indicesOfRunsAtMeasurementTimes
     * @param prawnFileRuns                    the value of prawnFileRuns
     * @param showTimeNormalized
     * @param showSpotLabels                   the value of showSpotLabels
     * @param massAuditRefreshInterface        the value of massAuditRefreshInterface
     * @param leadingZoomingTrailing
     * @param controlMinY
     * @param controlMaxY
     * @param controlMinYII
     * @param controlMaxYII
     */
    public SpeciesCountsAuditViewForShrimp(
            Rectangle bounds,
            String plotTitle,
            List<Double> totalCounts,
            List<Double> totalCountsSBM,
            int countsRadioButtonChoice,
            List<Double> timesOfMeasuredTrimMasses,
            List<Integer> indicesOfScansAtMeasurementTimes,
            List<Integer> indicesOfRunsAtMeasurementTimes,
            List<Run> prawnFileRuns,
            boolean showTimeNormalized,
            boolean showSpotLabels,
            MassAuditRefreshInterface massAuditRefreshInterface, int leadingZoomingTrailing,
            double controlMinY, double controlMaxY, double controlMinYII, double controlMaxYII) {

        super(bounds, 0, 0);
        this.plotTitle = plotTitle;
        this.totalCounts = totalCounts;
        this.totalCountsSBM = totalCountsSBM;
        this.countsRadioButtonChoice = countsRadioButtonChoice;
        this.timesOfMeasuredTrimMasses = timesOfMeasuredTrimMasses;
        // this list is modified in preparePanel
        this.indicesOfScansAtMeasurementTimes.addAll(indicesOfScansAtMeasurementTimes);
        this.indicesOfRunsAtMeasurementTimes = indicesOfRunsAtMeasurementTimes;
        this.prawnFileRuns = prawnFileRuns;
        this.showTimeNormalized = showTimeNormalized;
        this.showspotLabels = showSpotLabels;
        this.massAuditRefreshInterface = massAuditRefreshInterface;
        this.leadingZoomingTrailing = leadingZoomingTrailing;
        this.controlMinY = controlMinY;
        this.controlMaxY = controlMaxY;
        this.controlMinYII = controlMinYII;
        this.controlMaxYII = controlMaxYII;

        setOpacity(1.0);

        this.setOnMouseClicked(new MouseClickEventHandler());

        setupSpotContextMenu();
    }

    /**
     * TODO: Adapted from SpotManagerController - should refactor to one
     * implementation
     */
    private void setupSpotContextMenu() {
        spotContextMenuItem1 = new MenuItem();
        spotContextMenuItem1.setOnAction((evt) -> {

            if (indexOfSelectedSpot >= 0) {
                squidProject.removeRunsFromPrawnFile(selectedRuns);

                squidProject.generatePrefixTreeFromSpotNames();
                SquidProject.setProjectChanged(true);
                massAuditRefreshInterface.updateSpotsInGraphs();
            }

        });

        spotRestoreMenu = new Menu("Restore spots ...");

        prawnFileSplitMenu = new Menu("Split Prawn file ...");

        spotContextMenu.getItems().addAll(spotContextMenuItem1, prawnFileSplitMenu);

        splitRunsOriginalMenuItem = new MenuItem("Split at this run, using original unedited.");
        splitRunsOriginalMenuItem.setOnAction((evt) -> {
            Run selectedRun = prawnFileRuns.get(indexOfSelectedSpot);
            if (selectedRun != null) {
                try {
                    String[] splitNames = squidProject.splitPrawnFileAtRun(selectedRun, true);
                    SquidMessageDialog.showInfoDialog(
                            "Two Prawn XML files have been written:\n\n"
                                    + "\t" + splitNames[0] + "\n"
                                    + "\t" + splitNames[1] + "\n\n"
                                    + "Create a new Squid3 Project with each of these Prawn XML files.",
                            primaryStageWindow
                    );
                } catch (SquidException squidException) {
                    String message = squidException.getMessage();
                    if (message == null) {
                        message = squidException.getCause().getMessage();
                    }

                    SquidMessageDialog.showWarningDialog(
                            "The Project's Prawn File cannot be found ... please use PrawnFile menu to save it:\n\n"
                                    + message,
                            primaryStageWindow);
                }
            }
        });

        splitRunsEditedMenuItem = new MenuItem("Split at this run, using edits.");
        splitRunsEditedMenuItem.setOnAction((evt) -> {
            Run selectedRun = prawnFileRuns.get(indexOfSelectedSpot);
            if (selectedRun != null) {
                try {
                    String[] splitNames = squidProject.splitPrawnFileAtRun(selectedRun, false);
                    SquidMessageDialog.showInfoDialog(
                            "Two Prawn XML files have been written:\n\n"
                                    + "\t" + splitNames[0] + "\n"
                                    + "\t" + splitNames[1] + "\n\n"
                                    + "Create a new Squid3 Project with each of these Prawn XML files.",
                            primaryStageWindow
                    );
                } catch (SquidException squidException) {
                    String message = squidException.getMessage();
                    if (message == null) {
                        message = squidException.getCause().getMessage();
                    }

                    SquidMessageDialog.showWarningDialog(
                            "The Project's Prawn File cannot be found ... please use PrawnFile menu to save it:\n\n"
                                    + message,
                            primaryStageWindow);
                }
            }
        });

        prawnFileSplitMenu.getItems().addAll(splitRunsOriginalMenuItem, splitRunsEditedMenuItem);

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
                if ((StrictMath.abs(convertedX - myOnPeakNormalizedAquireTimes[myOnPeakNormalizedAquireTimes.length - 1]) < 0.25)) {
                    index = myOnPeakNormalizedAquireTimes.length - 1;
                }
            }
        }

        // determine spot number
        int spotIndex = -1;
        for (int i = 0; i < countOfScansCumulative.length; i++) {
            if (index < countOfScansCumulative[i]) {
                spotIndex = i - 1;
                break;
            }
        }

        return spotIndex;
    }

    /**
     * @param g2d
     */
    @Override
    public void paint(GraphicsContext g2d) {
        super.paint(g2d);

        boolean legendOnly = (width - LEGEND_WIDTH == 0);
        float verticalTextShift = 3.1f;

        g2d.setFont(Font.font("SansSerif", 12));

        g2d.setStroke(Paint.valueOf("BLACK"));
        g2d.setLineWidth(0.5);

        if (legendOnly) {
            leftMargin = LEGEND_WIDTH;
            g2d.setFill(Paint.valueOf("Red"));
            if (plotTitle.contains("-")) {
                String[] titleArray = plotTitle.split("-");
                g2d.fillText(titleArray[0], 5, 15);
                g2d.fillText("- " + titleArray[1], 8, 30);
            } else {
                g2d.fillText(plotTitle, 5, 15);
            }

            // legend background
            g2d.setFill(Color.rgb(255, 239, 213, 0.75));
            g2d.fillRect(3, 35, 105, 47);
            // legend
            g2d.setFill(Paint.valueOf("BLACK"));
            g2d.setFont(Font.font("SansSerif", 10));
            g2d.fillText("Mouse:", 5, 45);
            g2d.fillText(" left = select spot", 5, 55);
            g2d.fillText(" shift + left = select 2nd", 5, 65);
            g2d.fillText(" right = spot menu", 5, 75);

            // supplemental legend

            g2d.setFont(Font.font("SansSerif", 12));

            // ticsY for counts
            if ((countsRadioButtonChoice & 0b10) > 0) {
                g2d.setFill(Paint.valueOf("BLACK"));
                g2d.fillText("totalCounts", 15, 110);
                g2d.setFont(Font.font("SansSerif", 10));
                if (ticsY != null) {
                    for (int i = 0; i < ticsY.length; i++) {
                        try {
                            g2d.fillText(ticsY[i].toPlainString(),//
                                    (float) mapX(minX) - 50f,
                                    (float) mapY(ticsY[i].doubleValue()) + verticalTextShift);
                        } catch (Exception e) {
                        }
                    }
                }
            }

            // ticsYII
            if ((countsRadioButtonChoice & 0b01) > 0) {
                g2d.setFill(Paint.valueOf("BLUE"));
                g2d.fillText("totalSBM", 15, 125);
                g2d.setFont(Font.font("SansSerif", 10));
                if (ticsYII != null) {
                    double saveMinY = minY;
                    double saveMaxY = maxY;
                    minY = minYII;
                    maxY = maxYII;
                    for (int i = 0; i < ticsYII.length; i++) {
                        try {
                            g2d.fillText(ticsYII[i].toPlainString(),//
                                    (float) mapX(minX) - 100f,
                                    (float) mapY(ticsYII[i].doubleValue()) + verticalTextShift);
                        } catch (Exception e) {
                        }
                    }
                    minY = saveMinY;
                    maxY = saveMaxY;
                }
            }

        } else {
            leftMargin = 0;
            // selection rectangle and labels
            g2d.setFill(Paint.valueOf("BLACK"));
            g2d.setFont(Font.font("SansSerif", 12));

            if (indexOfSelectedSpot >= 0 && leadingZoomingTrailing == 0) {
                for (int i = 0; i < listOfSelectedIndices.size(); i++) {
                    int index = listOfSelectedIndices.get(i);
                    // gray spot(s) rectangle
                    g2d.setFill(Color.rgb(0, 0, 0, 0.1));
                    g2d.fillRect(
                            mapX(myOnPeakNormalizedAquireTimes[countOfScansCumulative[index]]) - 2f,
                            0,
                            StrictMath.abs(mapX(myOnPeakNormalizedAquireTimes[countOfScansCumulative[index + 1] - 1])
                                    - mapX(myOnPeakNormalizedAquireTimes[countOfScansCumulative[index]])) + 4f,
                            height);
                    showSpotLabelOnGraph(g2d, index);
                }
            }

            // show totalCounts
            if ((countsRadioButtonChoice & 0b10) > 0) {
                g2d.setFill(Paint.valueOf("Red"));
                g2d.beginPath();
                g2d.moveTo(mapX(myOnPeakNormalizedAquireTimes[0]), mapY(myOnPeakData[0]));
                for (int i = 0; i < myOnPeakData.length; i++) {
                    // line tracing through points
                    g2d.lineTo(mapX(myOnPeakNormalizedAquireTimes[i]), mapY(myOnPeakData[i]));

                    // vertical lines just before scan # = 1
                    if (scanIndices[i + 1] == 1) {
                        g2d.setStroke(Paint.valueOf("Red"));
                        g2d.setLineWidth(0.5);

                        if (i < (myOnPeakData.length - 1)) {
                            double runX = mapX(myOnPeakNormalizedAquireTimes[i]) + 2.0f;
                            g2d.strokeLine(runX, 0, runX, height);
                        }

                        g2d.setFont(Font.font("SansSerif", 7));
                        Text text = new Text(String.valueOf(runIndices[i]));
                        text.setFont(Font.font("SansSerif", 7));
                        g2d.setFill(Paint.valueOf("BLACK"));
                        g2d.fillText(String.valueOf(runIndices[i]), mapX(myOnPeakNormalizedAquireTimes[i]) - text.getLayoutBounds().getWidth() + 2.0f, height - 1.5);
                        g2d.setStroke(Paint.valueOf("BLACK"));
                        g2d.setLineWidth(1.0);
                    }

                    g2d.strokeOval(mapX(myOnPeakNormalizedAquireTimes[i]) - 1, mapY(myOnPeakData[i]) - 1, 2, 2);

                }

                g2d.stroke();

                // ticsY
                g2d.setFont(Font.font("SansSerif", 10));
                if (ticsY != null) {
                    for (int i = 0; i < ticsY.length; i++) {
                        try {
                            g2d.strokeLine(
                                    mapX(minX), mapY(ticsY[i].doubleValue()), mapX(maxX), mapY(ticsY[i].doubleValue()));
                        } catch (Exception e) {
                        }
                    }
                }
            }

            //April 2021 add totalSBM
            if ((countsRadioButtonChoice & 0b01) > 0) {
                double saveMinY = minY;
                double saveMaxY = maxY;
                minY = minYII;
                maxY = maxYII;
                g2d.beginPath();
                g2d.moveTo(mapX(myOnPeakNormalizedAquireTimes[0]), mapY(myOnPeakDataII[0]));
                for (int i = 0; i < myOnPeakDataII.length; i++) {
                    // line tracing through points
                    g2d.lineTo(mapX(myOnPeakNormalizedAquireTimes[i]), mapY(myOnPeakDataII[i]));

                    // vertical lines just before scan # = 1
                    if (scanIndices[i + 1] == 1) {
                        g2d.setStroke(Paint.valueOf("Red"));
                        g2d.setLineWidth(0.5);

                        if (i < (myOnPeakDataII.length - 1)) {
                            double runX = mapX(myOnPeakNormalizedAquireTimes[i]) + 2.0f;
                            g2d.strokeLine(runX, 0, runX, height);
                        }

                        g2d.setFont(Font.font("SansSerif", 7));
                        Text text = new Text(String.valueOf(runIndices[i]));
                        text.setFont(Font.font("SansSerif", 7));
                        g2d.setFill(Paint.valueOf("BLUE"));
                        g2d.fillText(String.valueOf(runIndices[i]), mapX(myOnPeakNormalizedAquireTimes[i]) - text.getLayoutBounds().getWidth() + 2.0f, height - 1.5);
                        g2d.setStroke(Paint.valueOf("BLUE"));
                        g2d.setLineWidth(1.0);
                    }

                    g2d.strokeOval(mapX(myOnPeakNormalizedAquireTimes[i]) - 1, mapY(myOnPeakDataII[i]) - 1, 2, 2);
                }

                g2d.stroke();

                // ticsY
                g2d.setFont(Font.font("SansSerif", 10));
                if (ticsYII != null) {
                    for (int i = 0; i < ticsYII.length; i++) {
                        try {
                            g2d.strokeLine(
                                    mapX(minX), mapY(ticsYII[i].doubleValue()), mapX(maxX), mapY(ticsYII[i].doubleValue()));
                        } catch (Exception e) {
                        }
                    }
                }

                minY = saveMinY;
                maxY = saveMaxY;
            }
            // label spots
            if (showspotLabels && leadingZoomingTrailing == 0) {
                for (int spotIndex = 0; spotIndex < prawnFileRuns.size(); spotIndex++) {
                    showSpotLabelOnGraph(g2d, spotIndex);
                }
            }
        }


    }

    private void showSpotLabelOnGraph(GraphicsContext g2d, int spotIndex) {
        if (countOfScansCumulative.length > spotIndex) {
            g2d.setFont(Font.font("SansSerif", 11));
            g2d.setFill(Paint.valueOf("BLUE"));
            Text text = new Text(prawnFileRuns.get(spotIndex).getPar().get(0).getValue());
            text.applyCss();
            g2d.rotate(-90);

            int onPeakAcquireTimesIndex = countOfScansCumulative[spotIndex] + 4;
            if (onPeakAcquireTimesIndex >= myOnPeakNormalizedAquireTimes.length) {
                onPeakAcquireTimesIndex = myOnPeakNormalizedAquireTimes.length - 1;
            }
            g2d.fillText(
                    prawnFileRuns.get(spotIndex).getPar().get(0).getValue(),
                    -70,//-text.getLayoutBounds().getWidth() - 5,
                    mapX(myOnPeakNormalizedAquireTimes[onPeakAcquireTimesIndex])
            );
            g2d.rotate(90);
        }
    }

    /**
     *
     */
    @Override
    public void preparePanel() {

        this.countOfScansCumulative = new int[prawnFileRuns.size() + 1];
        for (int i = 0; i < prawnFileRuns.size(); i++) {
            int countOfScans = Integer.parseInt(prawnFileRuns.get(i).getPar().get(3).getValue());
            countOfScansCumulative[i + 1] = countOfScansCumulative[i] + countOfScans;
        }

        myOnPeakData = totalCounts.stream().mapToDouble(Double::doubleValue).toArray();
        myOnPeakDataII = totalCountsSBM.stream().mapToDouble(Double::doubleValue).toArray();

        myOnPeakNormalizedAquireTimes = timesOfMeasuredTrimMasses.stream().mapToDouble(Double::doubleValue).toArray();

        double normalizer = myOnPeakNormalizedAquireTimes[0];
        for (int i = 0; i < myOnPeakNormalizedAquireTimes.length; i++) {
            myOnPeakNormalizedAquireTimes[i] -= normalizer;
        }

        if (showTimeNormalized) {
            for (int i = 0; i < myOnPeakNormalizedAquireTimes.length; i++) {
                myOnPeakNormalizedAquireTimes[i] = i;
            }
        }

        // add dummy placeholder
        indicesOfScansAtMeasurementTimes.add(1);
        scanIndices = indicesOfScansAtMeasurementTimes.stream().mapToInt(Integer::intValue).toArray();

        runIndices = indicesOfRunsAtMeasurementTimes.stream().mapToInt(Integer::intValue).toArray();

        setDisplayOffsetY(0.0);

        setDisplayOffsetX(0.0);

        // X-axis lays out time evenly spaced
        minX = myOnPeakNormalizedAquireTimes[0];
        maxX = myOnPeakNormalizedAquireTimes[myOnPeakNormalizedAquireTimes.length - 1];
        double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minX, maxX, 0.005);
        minX -= xMarginStretch;
        maxX += xMarginStretch;

        // Y-axis is counts
        double minCount = Double.MAX_VALUE;
        double maxCount = -Double.MAX_VALUE;
        double minCountII = Double.MAX_VALUE;
        double maxCountII = -Double.MAX_VALUE;

        // on peak
        for (int i = 0; i < myOnPeakData.length; i++) {
            minCount = StrictMath.min(minCount, myOnPeakData[i]);
            maxCount = StrictMath.max(maxCount, myOnPeakData[i]);
            minCountII = StrictMath.min(minCountII, myOnPeakDataII[i]);
            maxCountII = StrictMath.max(maxCountII, myOnPeakDataII[i]);
        }
        double[] peakTukeysMeanAndUnct = SquidMathUtils.tukeysBiweight(myOnPeakData, 9.0);

        // force plot max and min
        minY = minCount;
        maxY = maxCount;
        minYII = minCountII;
        maxYII = maxCountII;

        // adjust margins
        double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minY, maxY, 0.05);
        minY -= yMarginStretch;
        maxY += yMarginStretch;

        yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minYII, maxYII, 0.05);
        minYII -= yMarginStretch;
        maxYII += yMarginStretch;

        if (controlMinY * controlMaxY != 0) {
            minY = controlMinY;
            maxY = controlMaxY;
            minYII = controlMinYII;
            maxYII = controlMaxYII;
        }

        ticsY = TicGeneratorForAxes.generateTics(minY, maxY, (int) (graphHeight / 20.0));
        ticsYII = TicGeneratorForAxes.generateTics(minYII, maxYII, (int) (graphHeight / 20.0));

    }

    /**
     * @param aIndexOfSelectedSpot the indexOfSelectedSpot to set
     */
    public void setIndexOfSelectedSpot(int aIndexOfSelectedSpot) {
        indexOfSelectedSpot = aIndexOfSelectedSpot;
    }

    /**
     * @param indexOfSecondSelectedSpotForMultiSelect the
     *                                                indexOfSecondSelectedSpotForMultiSelect to set
     */
    public void setIndexOfSecondSelectedSpotForMultiSelect(int indexOfSecondSelectedSpotForMultiSelect) {
        AbstractDataView.indexOfSecondSelectedSpotForMultiSelect = indexOfSecondSelectedSpotForMultiSelect;
    }

    /**
     * @return the measuredTrimMasses
     */
    public List<Double> getMeasuredTrimMasses() {
        return totalCounts;
    }

    public void setMeasuredTrimMasses(List<Double> measuredTrimMasses) {
        this.totalCounts = measuredTrimMasses;
    }

    /**
     * @return the timesOfMeasuredTrimMasses
     */
    public List<Double> getTimesOfMeasuredTrimMasses() {
        return timesOfMeasuredTrimMasses;
    }

    public void setTimesOfMeasuredTrimMasses(List<Double> timesOfMeasuredTrimMasses) {
        this.timesOfMeasuredTrimMasses = timesOfMeasuredTrimMasses;
    }

    /**
     * @return the indicesOfScansAtMeasurementTimes
     */
    public List<Integer> getIndicesOfScansAtMeasurementTimes() {
        return indicesOfScansAtMeasurementTimes;
    }

    public void setIndicesOfScansAtMeasurementTimes(List<Integer> indicesOfScansAtMeasurementTimes) {
        this.indicesOfScansAtMeasurementTimes = indicesOfScansAtMeasurementTimes;
    }

    /**
     * @return the indicesOfRunsAtMeasurementTimes
     */
    public List<Integer> getIndicesOfRunsAtMeasurementTimes() {
        return indicesOfRunsAtMeasurementTimes;
    }

    public void setIndicesOfRunsAtMeasurementTimes(List<Integer> indicesOfRunsAtMeasurementTimes) {
        this.indicesOfRunsAtMeasurementTimes = indicesOfRunsAtMeasurementTimes;
    }

    /**
     * @return the prawnFileRuns
     */
    public List<Run> getPrawnFileRuns() {
        return prawnFileRuns;
    }

    public void setPrawnFileRuns(List<Run> prawnFileRuns) {
        this.prawnFileRuns = prawnFileRuns;
    }

    public List<Double> getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(List<Double> totalCounts) {
        this.totalCounts = totalCounts;
    }

    public List<Double> getTotalCountsSBM() {
        return totalCountsSBM;
    }

    public void setTotalCountsSBM(List<Double> totalCountsSBM) {
        this.totalCountsSBM = totalCountsSBM;
    }

    public double getMinYII() {
        return minYII;
    }

    public double getMaxYII() {
        return maxYII;
    }

    private class MouseClickEventHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent mouseEvent) {
            spotContextMenu.hide();

            // new logic may 2021 to allow for multiple selections +++++++++++++++++++++++++++++++++++++++++++++++++++++
            // determine if left click or with cmd or with shift
            boolean isShift = mouseEvent.isShiftDown();
            boolean isControl = mouseEvent.isControlDown() || mouseEvent.isMetaDown();
            boolean isPrimary = mouseEvent.getButton().compareTo(MouseButton.PRIMARY) == 0;

            // shift wipes out singletons
            int currentSelection = indexOfSpotFromMouseX(mouseEvent.getX());
            if (isPrimary) {
                if (!isShift && !isControl) {
                    indexOfSelectedSpot = currentSelection;
                    listOfSelectedIndices.clear();
                    listOfSelectedIndices.add(currentSelection);
                    selectedRuns.clear();
                    selectedRuns.add(prawnFileRuns.get(currentSelection));
                } else if (isControl) {
                    indexOfSelectedSpot = currentSelection;
                    if (listOfSelectedIndices.contains(currentSelection)) {
                        listOfSelectedIndices.remove((Integer) currentSelection);
                        selectedRuns.remove(prawnFileRuns.get(currentSelection));
                    } else {
                        listOfSelectedIndices.add(currentSelection);
                        selectedRuns.add(prawnFileRuns.get(currentSelection));
                    }
                } else { // isShift to nearest neighbor incl
                    selectedRuns = new ArrayList<>();
                    listOfSelectedIndices.clear();
                    if (indexOfSelectedSpot >= 0) {
                        boolean increasing = (currentSelection >= indexOfSelectedSpot);
                        for (int index = (increasing ? indexOfSelectedSpot : currentSelection);
                             index <= (increasing ? currentSelection : indexOfSelectedSpot);
                             index++) {
                            listOfSelectedIndices.add(index);
                            selectedRuns.add(prawnFileRuns.get(index));
                        }
                    }
                }
            }
            massAuditRefreshInterface.updateGraphsWithSelectedIndices(listOfSelectedIndices, selectedRuns, leadingZoomingTrailing);

            if (selectedRuns.size() > 1) {
                spotContextMenuItem1.setText("Remove selected set of " + selectedRuns.size() + " spots.");
            } else {
                spotContextMenuItem1.setText("Remove selected spot.");
            }

            prawnFileSplitMenu.setDisable(selectedRuns.size() > 1);

            if ((indexOfSelectedSpot > -1) && (mouseEvent.getButton().compareTo(MouseButton.SECONDARY) == 0)) {
                // customize spotContextMenu
                if (!squidProject.getRemovedRuns().isEmpty()) {
                    spotContextMenu.getItems().add(0, spotRestoreMenu);
                    spotRestoreMenu.getItems().clear();
                    MenuItem restoreAllSpotMenuItem = new MenuItem("Restore ALL");
                    spotRestoreMenu.getItems().add(restoreAllSpotMenuItem);
                    restoreAllSpotMenuItem.setOnAction((evt) -> {
                        squidProject.restoreAllRunsToPrawnFile();
                        squidProject.generatePrefixTreeFromSpotNames();
                        SquidProject.setProjectChanged(true);
                        massAuditRefreshInterface.updateSpotsInGraphs();
                    });
                    // list all removed runs
                    for (Run run : squidProject.getRemovedRuns()) {
                        MenuItem restoreSpotMenuItem = new MenuItem(run.getPar().get(0).getValue());
                        spotRestoreMenu.getItems().add(restoreSpotMenuItem);
                        restoreSpotMenuItem.setOnAction((evt) -> {
                            squidProject.restoreRunToPrawnFile(run);
                            squidProject.generatePrefixTreeFromSpotNames();
                            SquidProject.setProjectChanged(true);
                            massAuditRefreshInterface.updateSpotsInGraphs();
                        });
                    }
                } else {
                    spotContextMenu.getItems().remove(spotRestoreMenu);
                }

                spotContextMenu.show((Node) mouseEvent.getSource(), Side.LEFT,
                        mapX(myOnPeakNormalizedAquireTimes[countOfScansCumulative[indexOfSelectedSpot]]), 25);
            }
        }
    }

}