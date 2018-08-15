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
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
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
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.prawn.PrawnFile.Run;

/**
 *
 * @author James F. Bowring
 */
public class MassStationAuditViewForShrimp extends AbstractDataView {

    private final List<Double> measuredTrimMasses;
    private final List<Double> timesOfMeasuredTrimMasses;
    private final List<Integer> indicesOfScansAtMeasurementTimes;
    private final List<Integer> indicesOfRunsAtMeasurementTimes;
    private final List<Run> prawnFileRuns;

    private String plotTitle = "NONE";

    private int[] scanIndices;
    private int[] runIndices;
    private double maxMassAMU;
    private double minMassAMU;
    private double[] peakTukeysMeanAndUnct;

    private int indexOfSelectedSpot;
    private int countOfScans;

    private final MassAuditRefreshInterface massAuditRefreshInterface;

    private ContextMenu spotContextMenu = new ContextMenu();

    /**
     *
     * @param bounds
     * @param plotTitle
     * @param measuredTrimMasses
     * @param timesOfMeasuredTrimMasses
     * @param indicesOfScansAtMeasurementTimes
     * @param indicesOfRunsAtMeasurementTimes
     * @param prawnFileRuns the value of prawnFileRuns
     * @param showTimeNormalized
     * @param massAuditRefreshInterface the value of massAuditRefreshInterface
     */
    public MassStationAuditViewForShrimp(
            Rectangle bounds,
            String plotTitle,
            List<Double> measuredTrimMasses,
            List<Double> timesOfMeasuredTrimMasses,
            List<Integer> indicesOfScansAtMeasurementTimes,
            List<Integer> indicesOfRunsAtMeasurementTimes,
            List<Run> prawnFileRuns,
            boolean showTimeNormalized,
            MassAuditRefreshInterface massAuditRefreshInterface) {

        super(bounds, 265, 0);
        this.plotTitle = plotTitle;
        this.measuredTrimMasses = measuredTrimMasses;
        this.timesOfMeasuredTrimMasses = timesOfMeasuredTrimMasses;
        this.indicesOfScansAtMeasurementTimes = indicesOfScansAtMeasurementTimes;
        this.indicesOfRunsAtMeasurementTimes = indicesOfRunsAtMeasurementTimes;
        this.prawnFileRuns = prawnFileRuns;
        this.showTimeNormalized = showTimeNormalized;
        this.massAuditRefreshInterface = massAuditRefreshInterface;

        this.indexOfSelectedSpot = -1;

        setOpacity(1.0);

        this.setOnMouseClicked(new MouseClickEventHandler());

        setupSpotContextMenu();

        countOfScans = Integer.parseInt(prawnFileRuns.get(0).getPar().get(3).getValue());
    }

    /**
     * TODO: Adapted from SpotManagerCOntroller - should refactor to one
     * implementation
     */
    private void setupSpotContextMenu() {
        MenuItem menuItem1 = new MenuItem("Remove this spot.");
        menuItem1.setOnAction((evt) -> {
            PrawnFile.Run selectedRun = prawnFileRuns.get(indexOfSelectedSpot);
            if (selectedRun != null) {
                squidProject.removeRunFromPrawnFile(selectedRun);
                squidProject.generatePrefixTreeFromSpotNames();
                squidProject.getTask().setChanged(true);
                squidProject.getTask().buildSquidSpeciesModelList();
                massAuditRefreshInterface.removeSpotFromGraphs(indexOfSelectedSpot);
            }
        });

        MenuItem menuItem2 = new MenuItem("Split Prawn file starting with this run, using original unedited.");
        menuItem2.setOnAction((evt) -> {
            PrawnFile.Run selectedRun = prawnFileRuns.get(indexOfSelectedSpot);
            if (selectedRun != null) {
                try {
                    String[] splitNames = squidProject.splitPrawnFileAtRun(selectedRun, true);
                    SquidMessageDialog.showInfoDialog(
                            "Two Prawn XML files have been written:\n\n"
                            + "\t" + splitNames[0] + "\n"
                            + "\t" + splitNames[1] + "\n\n"
                            + "Create a new Squid Project with each of these Prawn XML files.",
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

        MenuItem menuItem3 = new MenuItem("Split Prawn file starting with this run, using edits.");
        menuItem3.setOnAction((evt) -> {
            PrawnFile.Run selectedRun = prawnFileRuns.get(indexOfSelectedSpot);
            if (selectedRun != null) {
                try {
                    String[] splitNames = squidProject.splitPrawnFileAtRun(selectedRun, false);
                    SquidMessageDialog.showInfoDialog(
                            "Two Prawn XML files have been written:\n\n"
                            + "\t" + splitNames[0] + "\n"
                            + "\t" + splitNames[1] + "\n\n"
                            + "Create a new Squid Project with each of these Prawn XML files.",
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
        spotContextMenu.getItems().addAll(menuItem1, menuItem2, menuItem3);

    }

    private class MouseClickEventHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent mouseEvent) {
            spotContextMenu.hide();
            indexOfSelectedSpot = indexOfSpotFromMouseX(mouseEvent.getX());
            if (indexOfSelectedSpot > -1) {
                massAuditRefreshInterface.updateGraphsWithSelectedIndex(indexOfSelectedSpot);
                if (mouseEvent.getButton().compareTo(MouseButton.SECONDARY) == 0) {
                    spotContextMenu.show((Node) mouseEvent.getSource(), Side.LEFT,
                            mapX(myOnPeakNormalizedAquireTimes[indexOfSelectedSpot * countOfScans]), 25);
                }
            }
        }
    }

    private int indexOfSpotFromMouseX(double x) {
        double convertedX = convertMouseXToValue(x);
        int index = -1;
        // look up index
        for (int i = 0; i < myOnPeakNormalizedAquireTimes.length - 1; i++) {
            if ((convertedX < myOnPeakNormalizedAquireTimes[i])) {
                index = i;
                break;
            }
        }

        return (int) Math.floor(index / countOfScans);
    }

    /**
     *
     * @param g2d
     */
    @Override
    public void paint(GraphicsContext g2d) {
        super.paint(g2d);

        g2d.setFont(Font.font("Lucida Sans", 12));

        g2d.setStroke(Paint.valueOf("BLACK"));
        g2d.setLineWidth(0.5);

        g2d.setFill(Paint.valueOf("Red"));
        if (plotTitle.contains("-")) {
            String[] titleArray = plotTitle.split("-");
            g2d.fillText(titleArray[0], 5, 15);
            g2d.fillText("- " + titleArray[1], 8, 30);
        } else {
            g2d.fillText(plotTitle, 5, 15);
        }

        g2d.setFill(Paint.valueOf("BLACK"));
        g2d.setFont(Font.font("Lucida Sans", 8));
        g2d.fillText("Mouse:", 5, 45);
        g2d.fillText(" left = spot name", 5, 55);
        g2d.fillText(" right = spot menu", 5, 65);

        g2d.setFont(Font.font("Lucida Sans", 12));
        if (indexOfSelectedSpot >= 0) {
            // gray spot rectangle
            g2d.setFill(Color.rgb(0, 0, 0, 0.2));
            g2d.fillRect(
                    mapX(myOnPeakNormalizedAquireTimes[indexOfSelectedSpot * countOfScans]),
                    0,
                    Math.abs(mapX(myOnPeakNormalizedAquireTimes[indexOfSelectedSpot * countOfScans + 4]) - mapX(myOnPeakNormalizedAquireTimes[indexOfSelectedSpot * countOfScans])),
                    height);
            g2d.setFill(Paint.valueOf("BLACK"));
            Text text = new Text(prawnFileRuns.get(indexOfSelectedSpot).getPar().get(0).getValue());
            text.applyCss();
            g2d.fillText(
                    prawnFileRuns.get(indexOfSelectedSpot).getPar().get(0).getValue(),
                    mapX(myOnPeakNormalizedAquireTimes[indexOfSelectedSpot * countOfScans + 4]) - text.getLayoutBounds().getWidth(),
                    20);
        }

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

                g2d.setFont(Font.font("Lucida Sans", 7));
                Text text = new Text(String.valueOf(runIndices[i]));
                text.setFont(Font.font("Lucida Sans", 7)); //       applyCss();
                g2d.setFill(Paint.valueOf("BLACK"));
                g2d.fillText(String.valueOf(runIndices[i]), mapX(myOnPeakNormalizedAquireTimes[i]) - text.getLayoutBounds().getWidth() + 2.0f, height - 1.5);
                g2d.setStroke(Paint.valueOf("BLACK"));
                g2d.setLineWidth(0.5);
            }

            g2d.strokeOval(mapX(myOnPeakNormalizedAquireTimes[i]) - 1, mapY(myOnPeakData[i]) - 1, 2, 2);

        }

        g2d.stroke();

        // ticsY
        float verticalTextShift = 3.1f;
        g2d.setFont(Font.font("Lucida Sans", 10));
        if (ticsY != null) {
            for (int i = 0; i < ticsY.length; i++) {
                try {
                    g2d.strokeLine(
                            mapX(minX), mapY(ticsY[i].doubleValue()), mapX(maxX), mapY(ticsY[i].doubleValue()));

                    g2d.fillText(ticsY[i].toPlainString(),//
                            (float) mapX(minX) - 50f,
                            (float) mapY(ticsY[i].doubleValue()) + verticalTextShift);
                } catch (Exception e) {
                }
            }
        }

        // stats
        g2d.setStroke(Paint.valueOf("BLUE"));
        g2d.setLineWidth(1.0);
        g2d.strokeLine(
                mapX(minX) - 50f,
                mapY(peakTukeysMeanAndUnct[0]),
                mapX(myOnPeakNormalizedAquireTimes[myOnPeakNormalizedAquireTimes.length - 1]),
                mapY(peakTukeysMeanAndUnct[0]));

        g2d.setFill(Paint.valueOf("BLUE"));
        g2d.fillText("mean = " + (new BigDecimal(peakTukeysMeanAndUnct[0])).setScale(4, RoundingMode.HALF_UP).toPlainString(),
                (float) mapX(minX) - 150f,
                (float) mapY(peakTukeysMeanAndUnct[0]) + verticalTextShift);
        g2d.fillText("  95% = " + (new BigDecimal(peakTukeysMeanAndUnct[2])).setScale(6, RoundingMode.HALF_UP).toEngineeringString(),
                (float) mapX(minX) - 150f,
                (float) mapY(peakTukeysMeanAndUnct[0]) + 15f);
        g2d.fillText(" max = " + (new BigDecimal(maxMassAMU)).setScale(3, RoundingMode.HALF_UP).toPlainString(),
                (float) mapX(minX) - 150f,
                (float) mapY(maxMassAMU) + verticalTextShift);
        g2d.fillText(" min = " + (new BigDecimal(minMassAMU)).setScale(3, RoundingMode.HALF_UP).toPlainString(),
                (float) mapX(minX) - 150f,
                (float) mapY(minMassAMU) + verticalTextShift);

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
            minMassAMU = Math.min(minMassAMU, myOnPeakData[i]);
            maxMassAMU = Math.max(maxMassAMU, myOnPeakData[i]);
        }
        peakTukeysMeanAndUnct = SquidMathUtils.tukeysBiweight(myOnPeakData, 9.0);

        // force plot max and min
        minY = Math.min(minMassAMU, peakTukeysMeanAndUnct[0]) - 0.0002;
        maxY = Math.max(maxMassAMU, peakTukeysMeanAndUnct[0]) + 0.0002;

        // adjust margins
        double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minY, maxY, 0.05);
        minY -= yMarginStretch;
        maxY += yMarginStretch;

        ticsY = TicGeneratorForAxes.generateTics(minY, maxY, (int) (graphHeight / 20.0));

    }

    /**
     * @param aIndexOfSelectedSpot the indexOfSelectedSpot to set
     */
    public void setIndexOfSelectedSpot(int aIndexOfSelectedSpot) {
        indexOfSelectedSpot = aIndexOfSelectedSpot;
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
     * @return the prawnFileRuns
     */
    public List<Run> getPrawnFileRuns() {
        return prawnFileRuns;
    }

}
