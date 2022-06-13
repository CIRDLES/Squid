/*
 * Copyright 2019 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.gui.dateInterpretations.plots.plotControllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.gui.dataViews.SampleNode;
import org.cirdles.squid.gui.dataViews.SampleTreeNodeInterface;
import org.cirdles.squid.gui.dateInterpretations.plots.PlotDisplayInterface;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.PlotRefreshInterface;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.WeightedMeanPlot;
import org.cirdles.squid.gui.dialogs.SquidMessageDialog;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategoryInterface;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumnInterface;
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTableInterface;
import org.cirdles.squid.squidReports.squidWeightedMeanReports.SquidWeightedMeanReportEngine;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import org.cirdles.squid.utilities.FileUtilities;
import org.cirdles.squid.utilities.OsCheck;
import org.cirdles.squid.utilities.stateUtilities.SquidLabData;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.gui.utilities.stringUtilities.StringTester.stringIsSquidRatio;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class RefMatWeightedMeanToolBoxNode extends HBox implements ToolBoxNodeInterface {

    private final ComboBox<String> sampleComboBox;
    private final PlotRefreshInterface plotsController;
    private final Slider probabilitySlider;
    private final TextField probTextField;
    private final ComboBox<SquidReportCategoryInterface> categoryComboBox;
    private final ComboBox<SquidReportColumnInterface> expressionComboBox;
    private final ComboBox<SquidReportCategoryInterface> categorySortComboBox;
    private final ComboBox<SquidReportColumnInterface> expressionSortComboBox;
    private Map<String, List<ShrimpFractionExpressionInterface>> mapOfSpotsBySampleNames;
    private SampleNode sampleNode;
    private PlotDisplayInterface sampleNodeSelectedAgeWMPlot;
    private CheckBoxTreeItem<SampleTreeNodeInterface> sampleItem;

    public RefMatWeightedMeanToolBoxNode(PlotRefreshInterface plotsController) throws SquidException {
        super(4);

        this.sampleComboBox = new ComboBox<>();
        this.plotsController = plotsController;
        this.probabilitySlider = new Slider(0, 1, 0);
        this.probTextField = new TextField("0.0");
        this.categoryComboBox = new ComboBox<>();
        this.expressionComboBox = new ComboBox<>();
        this.categorySortComboBox = new ComboBox<>();
        this.expressionSortComboBox = new ComboBox<>();

        initNode();

        sampleComboBox.getSelectionModel().selectFirst();

        SquidReportTableInterface squidWeightedMeansPlotSortTable = null;
        try {
            ((Task) squidProject.getTask()).initTaskDefaultSquidReportTables(false);
            squidWeightedMeansPlotSortTable = SquidLabData.getExistingSquidLabData().getSpecialRMWMSortingReportTable();
        } catch (SquidException squidException) {
            SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
        }

        categorySortComboBox.setItems(FXCollections.observableArrayList(squidWeightedMeansPlotSortTable.getReportCategories()));
        categorySortComboBox.getSelectionModel().selectFirst();
        expressionSortComboBox.setItems(FXCollections.observableArrayList(categorySortComboBox.getSelectionModel().getSelectedItem().getCategoryColumnsSorted()));

        categoryComboBox.setItems(FXCollections.observableArrayList(squidWeightedMeansPlotSortTable.getReportCategories()));
        //Category Housekeeping : No Time, Ages is first
        categoryComboBox.getItems().remove(0);
        categoryComboBox.getSelectionModel().selectFirst();
        expressionComboBox.setItems(FXCollections.observableArrayList(
                categoryComboBox.getSelectionModel().getSelectedItem().getCategoryColumnsSorted()));

    }

    private void initNode() throws SquidException {
        setStyle("-fx-padding: 1;" + "-fx-background-color: white;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 0 2 0 2;"
                + "-fx-border-radius: 4;" + "-fx-border-color: blue;-fx-effect: null;");

        VBox samplesToolBox = samplesVBox();
        VBox domainToolBox = expressionVBox();
        VBox filterToolBox = filterVBox();
        VBox sortingToolBox = sortedVBox();
        VBox saveAsToolBox = saveAsVBox();
        HBox publishExpressionToolBox = publishExpressionAndExportSVGVBox();

        Path separator1 = separator(45.0F);
        Path separator2 = separator(45.0F);
        Path separator3 = separator(45.0F);
        Path separator4 = separator(45.0F);
        Path separator5 = separator(45.0F);

        getChildren().addAll(
                samplesToolBox, separator1, domainToolBox, separator2,
                filterToolBox, separator3, sortingToolBox, separator4,
                saveAsToolBox, separator5, publishExpressionToolBox);

        setAlignment(Pos.CENTER);
    }

    private VBox samplesVBox() {
        VBox sampleNameToolBox = new VBox(0);

        Label sampleInfoLabel = new Label("Reference Material:");
        formatNode(sampleInfoLabel, 125);

        mapOfSpotsBySampleNames = ((Task) squidProject.getTask()).produceMapOfRefMatSpotsNames();

        formatNode(sampleComboBox, 100);
        sampleComboBox.setItems(FXCollections.observableArrayList(((Task) squidProject.getTask()).getFilterForRefMatSpotNames()));

        sampleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                displaySample(newValue);
            } catch (SquidException squidException) {
            }
        });

        sampleNameToolBox.getChildren().addAll(sampleInfoLabel, sampleComboBox);

        return sampleNameToolBox;
    }

    private void displaySample(String newValue) throws SquidException {
        sampleNode = new SampleNode(newValue);

        List<ShrimpFractionExpressionInterface> shrimpFractions = mapOfSpotsBySampleNames.get(newValue);

        // each fraction stores this info, so get from first one
        String selectedAge = shrimpFractions.get(0).getSelectedAgeExpressionName();
        ((Task) squidProject.getTask()).evaluateUnknownsWithChangedParameters(shrimpFractions);
        SpotSummaryDetails spotSummaryDetailsWM
                = ((Task) squidProject.getTask()).evaluateSelectedAgeWeightedMeanForUnknownGroup(newValue, shrimpFractions);
        spotSummaryDetailsWM.setManualRejectionEnabled(true);

        WeightedMeanPlot.switchRefMatViewToCalibConst = false;
        sampleNodeSelectedAgeWMPlot = new WeightedMeanPlot(
                new Rectangle(1000, 600),
                " Reference Material " + newValue,
                spotSummaryDetailsWM,
                selectedAge,
                0.0,
                plotsController);

        sampleNode.setSamplePlotWM(sampleNodeSelectedAgeWMPlot);
        sampleNode.setPlotsController(plotsController);

        PlotsController.plot = sampleNodeSelectedAgeWMPlot;
        PlotsController.spotSummaryDetails = spotSummaryDetailsWM;

        List<ShrimpFractionExpressionInterface> shrimpFractionsDetails = spotSummaryDetailsWM.getSelectedSpots();
        List<SampleTreeNodeInterface> fractionNodeDetailsWM = new ArrayList<>();

        for (int i = 0; i < shrimpFractionsDetails.size(); i++) {
            WeightedMeanSpotNode fractionNodeWM
                    = new WeightedMeanSpotNode(shrimpFractionsDetails.get(i), i);
            fractionNodeDetailsWM.add(fractionNodeWM);
        }

        // sort by spot index
        categorySortComboBox.getSelectionModel().selectFirst();

        ObservableList<SampleTreeNodeInterface> fractionNodesWM = FXCollections.observableArrayList(fractionNodeDetailsWM);

        // build spot tree for sample
        sampleItem = new CheckBoxTreeItem<>(sampleNode);
        sampleItem.setSelected(false);
        sampleItem.setExpanded(true);
        PlotsController.spotsTreeViewCheckBox.setRoot(sampleItem);
        PlotsController.spotsTreeViewCheckBox.setShowRoot(true);
        PlotsController.currentlyPlottedSampleTreeNode = sampleItem;

        for (int i = 0; i < fractionNodesWM.size(); i++) {
            final CheckBoxTreeItem<SampleTreeNodeInterface> checkBoxTreeItemWM
                    = new CheckBoxTreeItem<>(fractionNodesWM.get(i));
            sampleItem.getChildren().add(checkBoxTreeItemWM);

            checkBoxTreeItemWM.setSelected(!spotSummaryDetailsWM
                    .getRejectedIndices()[((WeightedMeanSpotNode) checkBoxTreeItemWM.getValue())
                    .getIndexOfSpot()]);

            checkBoxTreeItemWM.selectedProperty().addListener((observable, oldChoice, newChoice) -> {
                checkBoxTreeItemWM.getValue().setSelectedProperty(new SimpleBooleanProperty(newChoice));
                final SpotSummaryDetails spotSummaryDetailsCB = ((SampleNode) checkBoxTreeItemWM.getParent().getValue()).getSpotSummaryDetailsWM();
                spotSummaryDetailsCB.setIndexOfRejectedIndices(((WeightedMeanSpotNode) checkBoxTreeItemWM.getValue())
                        .getIndexOfSpot(), !newChoice);
                try {
                    spotSummaryDetailsCB.setValues(spotSummaryDetailsCB.eval(squidProject.getTask()));
                } catch (SquidException squidException) {
                }

                plotsController.refreshPlot();
            });
        }

        // sample always defaults to Ages Category because WM is of primary interest
        // need to force change detection to ages
        categoryComboBox.getSelectionModel().select(-1);
        categoryComboBox.getSelectionModel().selectFirst();

        probabilitySlider.valueProperty().setValue(spotSummaryDetailsWM.getMinProbabilityWM());
        probTextField.textProperty().setValue(String.format("%.2f", spotSummaryDetailsWM.getMinProbabilityWM()));
        probTextField.setAlignment(Pos.CENTER);

        DecimalFormat format = new DecimalFormat("0.00");
        probTextField.setTextFormatter(new TextFormatter<>(c
                -> {
            if (c.getControlNewText().isEmpty()) {
                return c;
            }
            ParsePosition parsePosition = new ParsePosition(0);
            Object object = format.parse(c.getControlNewText(), parsePosition);

            if (object == null || parsePosition.getIndex() < c.getControlNewText().length()) {
                return null;
            } else if (Double.parseDouble(c.getControlNewText()) > 1.0) {
                return null;
            } else {
                return c;
            }
        }));

        probTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (probTextField.getText().length() > 0) {
                    probabilitySlider.valueProperty().setValue(Double.parseDouble(probTextField.getText()));
                }
            }
        });

        plotsController.refreshPlot();
    }

    private VBox expressionVBox() {
        VBox expressionToolBox = new VBox(0);

        HBox expressionInfoHBox = new HBox(5);

        Label expressionInfoLabel = new Label("Calc Weighted Mean from:");
        formatNode(expressionInfoLabel, 155);

        expressionInfoHBox.getChildren().addAll(expressionInfoLabel);

        HBox categoryInfoHBox = new HBox(5);

        formatNode(categoryComboBox, 120);
        categoryComboBox.setPromptText("Category");

        categoryComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SquidReportCategoryInterface>() {
            @Override
            public void changed(ObservableValue<? extends SquidReportCategoryInterface> observable, SquidReportCategoryInterface oldValue, SquidReportCategoryInterface newValue) {
                if (newValue != null) {
                    // first get columns from category   
                    expressionComboBox.getSelectionModel().clearSelection();
                    expressionComboBox.setItems(FXCollections.observableArrayList(newValue.getCategoryColumnsSorted()));

                    // special case when Ages is picked, we look up stored WM for age name in sample
                    if (newValue.getDisplayName().compareToIgnoreCase("Ages") == 0) {
                        sampleNode.setSamplePlotWM(sampleNodeSelectedAgeWMPlot);
                        PlotsController.plot = sampleNodeSelectedAgeWMPlot;

                        String selectedAge = sampleNode.getSpotSummaryDetailsWM().getExpressionTree().getName().split("_WM_")[0];
                        expressionComboBox.getSelectionModel().select(newValue.findColumnByName(selectedAge));
                    } else {
                        // show the first
                        expressionComboBox.getSelectionModel().selectFirst();
                    }
                }
            }
        });

        formatNode(expressionComboBox, 195);
        expressionComboBox.setPromptText("Expression");
        expressionComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SquidReportColumnInterface>() {
            @Override
            public void changed(ObservableValue<? extends SquidReportColumnInterface> observable, SquidReportColumnInterface oldValue, SquidReportColumnInterface newValue) {
                if (newValue != null) {
                    PlotsController.spotsTreeViewCheckBox.setRoot(sampleItem);
                    String selectedExpression = newValue.getExpressionName();
                    if (categoryComboBox.getSelectionModel().getSelectedItem().getDisplayName().compareToIgnoreCase("AGES") == 0) {
                        try {
                            ((Task) squidProject.getTask()).setRefMatGroupSelectedAge(sampleNode.getSpotSummaryDetailsWM().getSelectedSpots(), newValue.getExpressionName());
                            ((Task) squidProject.getTask()).evaluateUnknownsWithChangedParameters(sampleNode.getSpotSummaryDetailsWM().getSelectedSpots());

                            SpotSummaryDetails spotSummaryDetailsWM
                                    = ((Task) squidProject.getTask())
                                    .evaluateSelectedAgeWeightedMeanForUnknownGroup(sampleNode.getNodeName(), sampleNode.getSpotSummaryDetailsWM().getSelectedSpots());
                            spotSummaryDetailsWM.setManualRejectionEnabled(true);

                            PlotDisplayInterface myPlot = sampleNode.getSamplePlotWM();
                            ((WeightedMeanPlot) myPlot).setSpotSummaryDetails(spotSummaryDetailsWM);
                            ((WeightedMeanPlot) myPlot).setAgeOrValueLookupString(selectedExpression);
                            refreshSampleCheckboxSelectionStatus(spotSummaryDetailsWM);
                            sortFractionCheckboxesByValue(spotSummaryDetailsWM);
                            PlotsController.plot = myPlot;
                        } catch (SquidException squidException) {
                            PlotsController.plot = null;
                            PlotsController.spotsTreeViewCheckBox.setRoot(null);
                        }

                    } else {
                        // non-AGE case for exploration
                        SpotSummaryDetails spotSummaryDetailsWM
                                = ((Task) squidProject.getTask())
                                .evaluateSelectedExpressionWeightedMeanForUnknownGroup(
                                        selectedExpression, sampleNode.getNodeName(), sampleNode.getSpotSummaryDetailsWM().getSelectedSpots());
                        spotSummaryDetailsWM.setManualRejectionEnabled(true);

                        spotSummaryDetailsWM.setSelectedExpressionName(selectedExpression);

                        WeightedMeanPlot.switchRefMatViewToCalibConst = false;
                        PlotDisplayInterface myPlot = new WeightedMeanPlot(
                                new Rectangle(1000, 600),
                                " Reference Material " + sampleNode.getNodeName(),
                                spotSummaryDetailsWM,
                                selectedExpression,
                                0.0,
                                plotsController);

                        refreshSampleCheckboxSelectionStatus(spotSummaryDetailsWM);
                        sortFractionCheckboxesByValue(spotSummaryDetailsWM);
                        PlotsController.plot = myPlot;
                        sampleNode.setSamplePlotWM(myPlot);
                    }

                    // sort by selected sort expression
                    if (sampleNode != null) {
                        String selectedSortExpression = expressionSortComboBox.getSelectionModel().getSelectedItem().getExpressionName();
                        sampleNode.getSpotSummaryDetailsWM().setSelectedExpressionName(selectedSortExpression);
                        refreshSampleCheckboxSelectionStatus(sampleNode.getSpotSummaryDetailsWM());
                        sortFractionCheckboxesByValue(sampleNode.getSpotSummaryDetailsWM());
                        plotsController.refreshPlot();
                    }
                }
            }
        });

        categoryInfoHBox.getChildren().addAll(categoryComboBox, expressionComboBox);

        expressionToolBox.getChildren().addAll(expressionInfoHBox, categoryInfoHBox);

        return expressionToolBox;
    }

    private VBox filterVBox() {
        VBox filterToolBox = new VBox(0);
        CheckBox showExcludedSpotsCheckBox = showExcludedSpotsCheckBox();

        filterToolBox.getChildren().addAll(showExcludedSpotsCheckBox);
        filterToolBox.setAlignment(Pos.CENTER);
        return filterToolBox;
    }

    private CheckBox showExcludedSpotsCheckBox() {
        CheckBox autoExcludeSpotsCheckBox = new CheckBox("Plot rejects");
        autoExcludeSpotsCheckBox.setSelected(WeightedMeanPlot.doPlotRejectedSpots);
        autoExcludeSpotsCheckBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                WeightedMeanPlot.doPlotRejectedSpots = !WeightedMeanPlot.doPlotRejectedSpots;
                plotsController.refreshPlot();
            }
        });
        formatNode(autoExcludeSpotsCheckBox, 85);
        return autoExcludeSpotsCheckBox;
    }

    private VBox sortedVBox() {
        VBox sortedToolBox = new VBox(0);

        HBox sortingHBoxA = new HBox(5);
        Label sortedByLabel = new Label("Sorted Ascending by:");
        formatNode(sortedByLabel, 125);

        sortingHBoxA.getChildren().addAll(sortedByLabel);

        HBox sortingHBoxB = new HBox(5);

        formatNode(categorySortComboBox, 120);
        categorySortComboBox.setPromptText("Category");

        categorySortComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SquidReportCategoryInterface>() {
            @Override
            public void changed(ObservableValue<? extends SquidReportCategoryInterface> observable, SquidReportCategoryInterface oldValue, SquidReportCategoryInterface newValue) {
                // first get columns from category   
                expressionSortComboBox.getSelectionModel().clearSelection();
                expressionSortComboBox.setItems(FXCollections.observableArrayList(newValue.getCategoryColumnsSorted()));

                // special case when Ages is picked, we look up stored WM for age name in sample
                if (newValue.getDisplayName().compareToIgnoreCase("Ages") == 0) {
                    try {
                        String selectedAge = sampleNode.getSpotSummaryDetailsWM().getSelectedSpots().get(0).getSelectedAgeExpressionName();
                        expressionSortComboBox.getSelectionModel().select(newValue.findColumnByName(selectedAge));
                    } catch (SquidException squidException) {
                    }
                } else {
                    // show the first
                    expressionSortComboBox.getSelectionModel().selectFirst();
                }
            }
        });

        formatNode(expressionSortComboBox, 195);
        expressionSortComboBox.setPromptText("Expression");
        expressionSortComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SquidReportColumnInterface>() {
            @Override
            public void changed(ObservableValue<? extends SquidReportColumnInterface> observable, SquidReportColumnInterface oldValue, SquidReportColumnInterface newValue) {
                if (newValue != null) {
                    if (sampleNode != null) {
                        String selectedExpression = newValue.getExpressionName();
                        sampleNode.getSpotSummaryDetailsWM().setSelectedExpressionName(
                                selectedExpression);

                        sortFractionCheckboxesByValue(sampleNode.getSpotSummaryDetailsWM());
                        plotsController.refreshPlot();
                    }
                }
            }
        });

        sortingHBoxB.getChildren().addAll(categorySortComboBox, expressionSortComboBox);

        sortedToolBox.getChildren().addAll(sortingHBoxA, sortingHBoxB);

        return sortedToolBox;
    }

    private VBox saveAsVBox() {
        VBox saveAsToolBox = new VBox(0);

        HBox saveDataHBox = new HBox(5);
        Label saveWMStatsLabel = new Label("Save WM stats to file:");
        saveWMStatsLabel.setAlignment(Pos.CENTER_LEFT);
        formatNode(saveWMStatsLabel, 125);

        Button saveToNewFileButton = new Button("New");
        formatNode(saveToNewFileButton, 50);
        saveToNewFileButton.setStyle("-fx-font-size: 12px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
        saveToNewFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    writeWeightedMeanReport(false);
                } catch (IOException ex) {
                    Logger.getLogger(RefMatWeightedMeanToolBoxNode.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Button appendToFileButton = new Button("Append");
        formatNode(appendToFileButton, 75);
        appendToFileButton.setStyle("-fx-font-size: 12px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
        appendToFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    writeWeightedMeanReport(true);
                } catch (IOException ex) {
                    Logger.getLogger(RefMatWeightedMeanToolBoxNode.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        saveDataHBox.getChildren().addAll(saveToNewFileButton, appendToFileButton);
        saveAsToolBox.getChildren().addAll(saveWMStatsLabel, saveDataHBox);

        return saveAsToolBox;
    }

    private HBox publishExpressionAndExportSVGVBox() throws SquidException {
        HBox publishExpressionVbox = new HBox(2);
        Button showInExpressionsButton = new Button("Show WM in Expressions");
        formatNode(showInExpressionsButton, 80);
        showInExpressionsButton.setPrefHeight(40);
        showInExpressionsButton.setMinHeight(USE_PREF_SIZE);
        showInExpressionsButton.setWrapText(true);
        showInExpressionsButton.setTextAlignment(TextAlignment.CENTER);
        showInExpressionsButton.setStyle("-fx-font-size: 12px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
        showInExpressionsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    ((Task) squidProject.getTask()).includeCustomWMExpressionByName(
                            sampleNode.getSpotSummaryDetailsWM().getExpressionTree().getName());
                } catch (SquidException squidException) {
                    SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
                }
            }
        });

        Button exportToSVGButton = new Button("To SVG");
        formatNode(exportToSVGButton, 80);
        exportToSVGButton.setPrefHeight(20);
        exportToSVGButton.setMinHeight(USE_PREF_SIZE);
        exportToSVGButton.setWrapText(true);
        exportToSVGButton.setTextAlignment(TextAlignment.CENTER);
        exportToSVGButton.setStyle("-fx-font-size: 12px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
        exportToSVGButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    try {
                        writeWeightedMeanSVG();
                    } catch (SquidException squidException) {
                    }
                } catch (IOException ex) {
                    SquidMessageDialog.showWarningDialog(ex.getMessage(), primaryStageWindow);
                    ex.printStackTrace();
                }
            }
        });

        publishExpressionVbox.getChildren().addAll(showInExpressionsButton, exportToSVGButton);
        publishExpressionVbox.setAlignment(Pos.CENTER);
        return publishExpressionVbox;
    }

    private void writeWeightedMeanReport(boolean doAppend) throws IOException {
        if (squidProject.hasReportsFolder()) {
            String report = SquidWeightedMeanReportEngine.makeWeightedMeanReportAsCSV(sampleNode.getSpotSummaryDetailsWM());
            String reportFileName = squidProject.getProjectName() + "_WMReport_RM_" + sampleNode.getNodeName() + ".csv";

            try {
                File reportFile = squidProject.getPrawnFileHandler().getReportsEngine().getWeightedMeansReportFile(reportFileName);
                if (reportFile != null) {
                    BooleanProperty writeReport = new SimpleBooleanProperty(true);
                    BooleanProperty doAppendProperty = new SimpleBooleanProperty(doAppend);
                    boolean confirmedExists = false;
                    OsCheck.OSType osType = OsCheck.getOperatingSystemType();
                    if (reportFile.exists()) {
                        switch (osType) {
                            case Windows:
                                if (!FileUtilities.isFileClosedWindows(reportFile)) {
                                    SquidMessageDialog.showWarningDialog("Please close the file in other applications and try again.", primaryStageWindow);
                                    writeReport.setValue(false);
                                }
                                break;
                            case MacOS:
                            case Linux:
                                if (!FileUtilities.isFileClosedUnix(reportFile)) {
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "The report file seems to be open in another application. Do you wish to continue?");
                                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                                    alert.showAndWait().ifPresent(action -> {
                                        if (action != ButtonType.OK) {
                                            writeReport.setValue(false);
                                        }
                                    });
                                    confirmedExists = true;
                                }
                                break;
                        }
                    }
                    if (writeReport.getValue()) {
                        if (reportFile.exists() && !doAppendProperty.getValue()) {
                            if (!confirmedExists) {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                                        "It appears that a weighted means report already exists. "
                                                + "Would you like to overwrite it?");
                                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                                alert.showAndWait().ifPresent(action -> {
                                    if (action.equals(ButtonType.CANCEL)) {
                                        writeReport.setValue(false);
                                    }
                                });
                            }
                        } else if (!reportFile.exists() && doAppendProperty.getValue()) {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "A weighted means report doesn't seem to exist. "
                                    + "Would you like to create a new report?");
                            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                            alert.showAndWait().ifPresent(action -> {
                                if (action.equals(ButtonType.OK)) {
                                    doAppendProperty.setValue(false);
                                }
                            });
                        }
                        if (writeReport.getValue()) {
                            squidProject.getPrawnFileHandler().getReportsEngine().writeSquidWeightedMeanReportToFile(report, reportFile, doAppendProperty.getValue());
                            SquidMessageDialog.showSavedAsDialog(reportFile, primaryStageWindow);
                        }
                    }
                }
            } catch (NoSuchFileException e) {
                SquidMessageDialog.showWarningDialog("The file doesn't seem to exist. Try hitting the new button.", primaryStageWindow);
            } catch (java.nio.file.FileSystemException e) {
                SquidMessageDialog.showWarningDialog("An error occurred. Try closing the file in other applications.", primaryStageWindow);
            } catch (IOException e) {
                SquidMessageDialog.showWarningDialog("An error occurred.\n" + e.getMessage(), primaryStageWindow);
            }
        } else {
            SquidMessageDialog.showWarningDialog("The Squid3 Project must be saved before reports can be written out.", primaryStageWindow);
        }
    }

    private void writeWeightedMeanSVG() throws IOException, SquidException {
        if (squidProject.hasReportsFolder()) {
            WeightedMeanPlot myPlot = (WeightedMeanPlot) sampleNode.getSamplePlotWM();
            String expressionName = myPlot.getAgeOrValueLookupString();
            String reportFileNameSVG = sampleNode.getSpotSummaryDetailsWM().getExpressionTree().getName().replace("/", "_") + ".svg";

            try {
                File reportFileSVG = squidProject.getPrawnFileHandler().getReportsEngine().getWeightedMeansReportFile(reportFileNameSVG);
                File reportFilePDF = new File(reportFileSVG.getCanonicalPath().replaceFirst("svg", "pdf"));
                if (reportFileSVG != null) {
                    BooleanProperty writeReport = new SimpleBooleanProperty(true);
                    boolean confirmedExists = false;
                    OsCheck.OSType osType = OsCheck.getOperatingSystemType();
                    if (reportFileSVG.exists()) {
                        switch (osType) {
                            case Windows:
                                if (!FileUtilities.isFileClosedWindows(reportFileSVG) &&
                                        !FileUtilities.isFileClosedWindows(reportFilePDF)) {
                                    SquidMessageDialog.showWarningDialog("Please close the file in other applications and try again.", primaryStageWindow);
                                    writeReport.setValue(false);
                                }
                                break;
                            case MacOS:
                            case Linux:
                                if (!FileUtilities.isFileClosedWindows(reportFileSVG) &&
                                        !FileUtilities.isFileClosedWindows(reportFilePDF)) {
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "The report file seems to be open in another application. Do you wish to continue?");
                                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                                    alert.showAndWait().ifPresent(action -> {
                                        if (action != ButtonType.OK) {
                                            writeReport.setValue(false);
                                        }
                                    });
                                    confirmedExists = true;
                                }
                                break;
                        }
                    }
                    if (writeReport.getValue()) {
                        if (reportFileSVG.exists()) {
                            if (!confirmedExists) {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                                        "It appears that a weighted means report already exists. "
                                                + "Would you like to overwrite it?");
                                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                                alert.showAndWait().ifPresent(action -> {
                                    if (action.equals(ButtonType.CANCEL)) {
                                        writeReport.setValue(false);
                                    }
                                });
                            }
                        }
                        if (writeReport.getValue()) {
                            myPlot.outputToSVG(reportFileSVG);
                            myPlot.outputToPDF(reportFileSVG);
                            SquidMessageDialog.showSavedAsDialog(reportFileSVG, primaryStageWindow);
                        }
                    }
                }
            } catch (NoSuchFileException e) {
                SquidMessageDialog.showWarningDialog("The file doesn't seem to exist. Try hitting the new button.", primaryStageWindow);
            } catch (java.nio.file.FileSystemException e) {
                SquidMessageDialog.showWarningDialog("An error occurred. Try closing the file in other applications.", primaryStageWindow);
            } catch (IOException e) {
                SquidMessageDialog.showWarningDialog("An error occurred.\n" + e.getMessage(), primaryStageWindow);
            }
        } else {
            SquidMessageDialog.showWarningDialog("The Squid3 Project must be saved before reports can be written out.", primaryStageWindow);
        }

    }

    /**
     * @param spotSummaryDetails the value of spotSummaryDetails
     */
    private void sortFractionCheckboxesByValue(SpotSummaryDetails spotSummaryDetails) {
        String selectedFieldName = spotSummaryDetails.getSelectedExpressionName();
        FXCollections.sort(sampleItem.getChildren(), (TreeItem node1, TreeItem node2) -> {
            double valueFromNode1 = 0.0;
            double valueFromNode2 = 0.0;
            if (stringIsSquidRatio(selectedFieldName)) {
                // Ratio case
                double[][] resultsFromNode1
                        = Arrays.stream(((SampleTreeNodeInterface) node1.getValue()).getShrimpFraction()
                        .getIsotopicRatioValuesByStringName(selectedFieldName)).toArray(double[][]::new);
                valueFromNode1 = resultsFromNode1[0][0];
                double[][] resultsFromNode2
                        = Arrays.stream(((SampleTreeNodeInterface) node2.getValue()).getShrimpFraction()
                        .getIsotopicRatioValuesByStringName(selectedFieldName)).toArray(double[][]::new);
                valueFromNode2 = resultsFromNode2[0][0];
            } else {
                valueFromNode1 = ((SampleTreeNodeInterface) node1.getValue()).getShrimpFraction()
                        .getTaskExpressionsEvaluationsPerSpotByField(selectedFieldName)[0][0];
                valueFromNode2 = ((SampleTreeNodeInterface) node2.getValue()).getShrimpFraction()
                        .getTaskExpressionsEvaluationsPerSpotByField(selectedFieldName)[0][0];
            }

            return Double.compare(valueFromNode1, valueFromNode2);
        });
    }

    private void refreshSampleCheckboxSelectionStatus(SpotSummaryDetails spotSummaryDetails) {
        if (PlotsController.plot != null) {
            try {
                for (TreeItem<SampleTreeNodeInterface> spotCheckBox : PlotsController.spotsTreeViewCheckBox.getRoot().getChildren()) {
                    int indexOfSpot = ((WeightedMeanSpotNode) spotCheckBox.getValue()).getIndexOfSpot();
                    ((CheckBoxTreeItem<SampleTreeNodeInterface>) spotCheckBox).setSelected(
                            !sampleNode.getSpotSummaryDetailsWM().getRejectedIndices()[indexOfSpot]);
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }
}