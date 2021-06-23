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
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.prawn.PrawnFile.Run;
import org.cirdles.squid.projects.SquidProject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.cirdles.squid.gui.MassesAuditController.LEGEND_WIDTH;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;

/**
 * @author James F. Bowring
 */
public class SpeciesAMUAuditViewForShrimp extends AbstractDataView implements SpeciesGraphInterface {

    private final MassAuditRefreshInterface massAuditRefreshInterface;
    private final ContextMenu spotContextMenu = new ContextMenu();
    // -1, 0, 1
    private final int leadingZoomingTrailing;
    private List<Double> measuredTrimMasses;
    private List<Double> timesOfMeasuredTrimMasses;
    private List<Integer> indicesOfRunsAtMeasurementTimes;
    private List<Run> prawnFileRuns;
    private List<Integer> indicesOfScansAtMeasurementTimes = new ArrayList<>();
    private int[] countOfScansCumulative;
    private String plotTitle = "NONE";
    private int[] scanIndices;
    private int[] runIndices;
    private double maxMassAMU;
    private double minMassAMU;
    private double[] peakTukeysMeanAndUnct;
    private MenuItem spotContextMenuItem1;
    private Menu spotRestoreMenu;
    private Menu prawnFileSplitMenu;
    private MenuItem splitRunsOriginalMenuItem;
    private MenuItem splitRunsEditedMenuItem;
    private double controlMinY = 0;
    private double controlMaxY = 0;
    private double controlMean = 0;
    private double plottedMean = 0;

    /**
     * @param bounds
     * @param plotTitle
     * @param measuredTrimMasses
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
     * @param controlMean
     */
    public SpeciesAMUAuditViewForShrimp(
            Rectangle bounds,
            String plotTitle,
            List<Double> measuredTrimMasses,
            List<Double> timesOfMeasuredTrimMasses,
            List<Integer> indicesOfScansAtMeasurementTimes,
            List<Integer> indicesOfRunsAtMeasurementTimes,
            List<Run> prawnFileRuns,
            boolean showTimeNormalized,
            boolean showSpotLabels,
            MassAuditRefreshInterface massAuditRefreshInterface, int leadingZoomingTrailing,
            double controlMinY, double controlMaxY, double controlMean) {

        super(bounds, 0, 0);
        this.plotTitle = plotTitle;
        this.measuredTrimMasses = measuredTrimMasses;
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
        this.controlMean = controlMean;

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
            PrawnFile.Run selectedRun = prawnFileRuns.get(indexOfSelectedSpot);
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
            PrawnFile.Run selectedRun = prawnFileRuns.get(indexOfSelectedSpot);
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
                    break;
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


            // stats
            g2d.setStroke(Paint.valueOf("BLUE"));
            g2d.setLineWidth(1.0);
            g2d.strokeLine(
                    mapX(minX) - 50f,
                    mapY(peakTukeysMeanAndUnct[0]),
                    mapX(minX),
                    mapY(peakTukeysMeanAndUnct[0]));

            g2d.setFill(Paint.valueOf("BLUE"));
            g2d.fillText("mean = " + (BigDecimal.valueOf(peakTukeysMeanAndUnct[0])).setScale(4, RoundingMode.HALF_UP).toPlainString(),
                    (float) mapX(minX) - 150f,
                    (float) mapY(peakTukeysMeanAndUnct[0]) + verticalTextShift);
            g2d.fillText("  95% = " + (BigDecimal.valueOf(peakTukeysMeanAndUnct[2])).setScale(6, RoundingMode.HALF_UP).toEngineeringString(),
                    (float) mapX(minX) - 150f,
                    (float) mapY(peakTukeysMeanAndUnct[0]) + 15f);
            g2d.fillText(" max = " + (BigDecimal.valueOf(maxMassAMU)).setScale(3, RoundingMode.HALF_UP).toPlainString(),
                    (float) mapX(minX) - 150f,
                    (float) mapY(maxMassAMU) + verticalTextShift);
            g2d.fillText(" min = " + (BigDecimal.valueOf(minMassAMU)).setScale(3, RoundingMode.HALF_UP).toPlainString(),
                    (float) mapX(minX) - 150f,
                    (float) mapY(minMassAMU) + verticalTextShift);

            // ticsY
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

            g2d.setFill(Paint.valueOf("Red"));
            g2d.beginPath();
            g2d.moveTo(mapX(myOnPeakNormalizedAquireTimes[0]), mapY(myOnPeakData[0]));
            for (int i = 0; i < myOnPeakData.length; i++) {
                // line tracing through points
                g2d.lineTo(mapX(myOnPeakNormalizedAquireTimes[i]), mapY(myOnPeakData[i]));

                // vertical lines just before scan # = 1
                if (scanIndices[i + 1] == scanIndices[0]) {
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
                    g2d.setLineWidth(0.5);
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

            // stats
            g2d.setStroke(Paint.valueOf("BLUE"));
            g2d.setLineWidth(1.0);
            g2d.strokeLine(
                    mapX(minX) - 50f,
                    mapY(plottedMean),
                    mapX(myOnPeakNormalizedAquireTimes[myOnPeakNormalizedAquireTimes.length - 1]),
                    mapY(plottedMean));

            // label spots
            if (showspotLabels && leadingZoomingTrailing == 0) {
                for (int spotIndex = 0; spotIndex < prawnFileRuns.size(); spotIndex++) {
                    showSpotLabelOnGraph(g2d, spotIndex);
                }
            }
        } // end of graph not legend


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

        myOnPeakData = measuredTrimMasses.stream().mapToDouble(Double::doubleValue).toArray();

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

        // Y-axis is masses
        minMassAMU = Double.MAX_VALUE;
        maxMassAMU = -Double.MAX_VALUE;

        // on peak
        for (int i = 0; i < myOnPeakData.length; i++) {
            minMassAMU = StrictMath.min(minMassAMU, myOnPeakData[i]);
            maxMassAMU = StrictMath.max(maxMassAMU, myOnPeakData[i]);
        }
        peakTukeysMeanAndUnct = SquidMathUtils.tukeysBiweight(myOnPeakData, 9.0);
        plottedMean = peakTukeysMeanAndUnct[0];

        // force plot max and min
        minY = StrictMath.min(minMassAMU, peakTukeysMeanAndUnct[0]) - 0.0002;
        maxY = StrictMath.max(maxMassAMU, peakTukeysMeanAndUnct[0]) + 0.0002;

        // adjust margins
        double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minY, maxY, 0.05);
        minY -= yMarginStretch;
        maxY += yMarginStretch;

        if (controlMinY * controlMaxY != 0) {
            minY = controlMinY;
            maxY = controlMaxY;
            plottedMean = controlMean;
        }

        ticsY = TicGeneratorForAxes.generateTics(minY, maxY, (int) (graphHeight / 20.0));
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
        return measuredTrimMasses;
    }

    public void setMeasuredTrimMasses(List<Double> measuredTrimMasses) {
        this.measuredTrimMasses = measuredTrimMasses;
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

    public double getPlottedMean() {
        return plottedMean;
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