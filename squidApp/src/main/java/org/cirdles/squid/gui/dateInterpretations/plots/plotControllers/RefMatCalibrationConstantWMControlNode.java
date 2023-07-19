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
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Path;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.gui.dataViews.SampleTreeNodeInterface;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.PlotRefreshInterface;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.WeightedMeanPlot;
import org.cirdles.squid.gui.dialogs.SquidMessageDialog;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategory;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategoryInterface;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumn;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumnInterface;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import org.cirdles.squid.utilities.FileUtilities;
import org.cirdles.squid.utilities.OsCheck;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.gui.dateInterpretations.plots.plotControllers.PlotsController.*;
import static org.cirdles.squid.gui.utilities.stringUtilities.StringTester.stringIsSquidRatio;
import static org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategory.defaultRefMatCalibrationConstantSortingCategories;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class RefMatCalibrationConstantWMControlNode extends HBox implements ToolBoxNodeInterface {

    private final ComboBox<SquidReportCategoryInterface> categorySortComboBox;
    private final ComboBox<SquidReportColumnInterface> expressionSortComboBox;
    private final PlotRefreshInterface plotsController;

    public RefMatCalibrationConstantWMControlNode(PlotRefreshInterface plotsController) {
        super(4);

        this.plotsController = plotsController;

        this.categorySortComboBox = new ComboBox<>();
        this.expressionSortComboBox = new ComboBox<>();

        initNode();

        // set up custom sorting for ref mat calibration constant wm
        List<SquidReportCategory> refMatCalibrationConstantWMSortingCategories = defaultRefMatCalibrationConstantSortingCategories;
        // handle special case where raw ratios is populated on the fly per task
        SquidReportCategoryInterface rawRatiosCategory = refMatCalibrationConstantWMSortingCategories.get(1);
        LinkedList<SquidReportColumnInterface> categoryColumns = new LinkedList<>();
        for (String ratioName : squidProject.getTask().getRatioNames()) {
            SquidReportColumnInterface column = SquidReportColumn.createSquidReportColumn(ratioName);
            categoryColumns.add(column);
        }
        rawRatiosCategory.setCategoryColumns(categoryColumns);

        categorySortComboBox.setItems(FXCollections.observableArrayList(defaultRefMatCalibrationConstantSortingCategories));
        categorySortComboBox.getSelectionModel().selectFirst();
        expressionSortComboBox.setItems(FXCollections.observableArrayList(categorySortComboBox.getSelectionModel().getSelectedItem().getCategoryColumns()));

    }

    private void initNode() {
        setStyle("-fx-padding: 1;" + "-fx-background-color: white;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 0 2 0 2;"
                + "-fx-border-radius: 4;" + "-fx-border-color: blue;-fx-effect: null;");

        setPrefHeight(23);
        setHeight(23);
        setFillHeight(true);
        setAlignment(Pos.CENTER);

        CheckBox autoExcludeSpotsCheckBox = autoExcludeSpotsCheckBox();
        CheckBox showExcludedSpotsCheckBox = showExcludedSpotsCheckBox();
        HBox plotChoiceHBox = plotChoiceHBox();
        HBox corrChoiceHBox = new RefMatCalibrationConstantWMToolBoxNode(plotsController);
        HBox sortingToolBox = sortedHBox();
        HBox exportToSVGHBox = exportButtonHBox();

        Path separator1 = separator(20.0F);
        Path separator2 = separator(20.0F);
        Path separator3 = separator(20.0F);
        Path separator4 = separator(20.0F);

        getChildren().addAll(
                autoExcludeSpotsCheckBox, showExcludedSpotsCheckBox,
                separator1, plotChoiceHBox,
                separator2, corrChoiceHBox,
                separator3, sortingToolBox,
                separator4, exportToSVGHBox);
    }

    private CheckBox autoExcludeSpotsCheckBox() {
        CheckBox autoExcludeSpotsCheckBox = new CheckBox("Auto-reject spots");
        autoExcludeSpotsCheckBox.setSelected(squidProject.getTask().isSquidAllowsAutoExclusionOfSpots());
        autoExcludeSpotsCheckBox.setOnAction(event -> {
            squidProject.getTask().setSquidAllowsAutoExclusionOfSpots(autoExcludeSpotsCheckBox.isSelected());
            // this will cause weighted mean expressions to be changed with boolean flag
            try {
                squidProject.getTask().updateRefMatCalibConstWMeanExpressions(autoExcludeSpotsCheckBox.isSelected());
            } catch (SquidException squidException) {
                SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
            }
            try {
                plotsController.showActivePlot();
            } catch (SquidException squidException) {
            }
        });
        formatNode(autoExcludeSpotsCheckBox, 110);
        return autoExcludeSpotsCheckBox;
    }

    private CheckBox showExcludedSpotsCheckBox() {
        CheckBox autoExcludeSpotsCheckBox = new CheckBox("Plot rejects");
        autoExcludeSpotsCheckBox.setSelected(WeightedMeanPlot.doPlotRejectedSpots);
        autoExcludeSpotsCheckBox.setOnAction(event -> {
            WeightedMeanPlot.doPlotRejectedSpots = !WeightedMeanPlot.doPlotRejectedSpots;
            plotsController.showRefMatCalibrationConstantPlot();
        });
        formatNode(autoExcludeSpotsCheckBox, 80);
        return autoExcludeSpotsCheckBox;
    }

    private HBox plotChoiceHBox() {

        HBox plotChoiceHBox = new HBox(5);

        Label plotChoiceLabel = new Label("Plot:");
        formatNode(plotChoiceLabel, 28);

        ToggleGroup plotGroup = new ToggleGroup();

        RadioButton ageRB = new RadioButton("Age");
        ageRB.setToggleGroup(plotGroup);
        ageRB.setUserData(false);
        ageRB.setSelected(!WeightedMeanPlot.switchRefMatViewToCalibConst);
        formatNode(ageRB, 45);

        RadioButton ccRB = new RadioButton("CC");
        ccRB.setToggleGroup(plotGroup);
        ccRB.setUserData(true);
        ccRB.setSelected(WeightedMeanPlot.switchRefMatViewToCalibConst);
        formatNode(ccRB, 40);

        // add listener after initial choice
        plotGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                WeightedMeanPlot.switchRefMatViewToCalibConst = (boolean) plotGroup.getSelectedToggle().getUserData();
                plotsController.showRefMatCalibrationConstantPlot();
            }
        });

        plotChoiceHBox.getChildren().addAll(plotChoiceLabel, ageRB, ccRB);

        return plotChoiceHBox;
    }

    private HBox sortedHBox() {

        HBox sortingHBox = new HBox(5);

        Label sortedByLabel = new Label("Sort ASC by:");
        formatNode(sortedByLabel, 75);

        formatNode(categorySortComboBox, 100);
        categorySortComboBox.setPromptText("Category");

        categorySortComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SquidReportCategoryInterface>() {
            @Override
            public void changed(ObservableValue<? extends SquidReportCategoryInterface> observable, SquidReportCategoryInterface oldValue, SquidReportCategoryInterface newValue) {
                // first get columns from category   
                expressionSortComboBox.getSelectionModel().clearSelection();
                expressionSortComboBox.setItems(FXCollections.observableArrayList(newValue.getCategoryColumns()));

                expressionSortComboBox.getSelectionModel().selectFirst();

            }
        });

        formatNode(expressionSortComboBox, 110);
        expressionSortComboBox.setPromptText("Expression");
        expressionSortComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SquidReportColumnInterface>() {
            @Override
            public void changed(ObservableValue<? extends SquidReportColumnInterface> observable, SquidReportColumnInterface oldValue, SquidReportColumnInterface newValue) {
                if ((spotSummaryDetails != null) && (newValue != null)) {
                    String selectedExpression = newValue.getExpressionName();
                    spotSummaryDetails.setSelectedExpressionName(
                            selectedExpression);
                    sortFractionCheckboxesByValue(spotSummaryDetails);
                    plotsController.refreshPlot();
                }
            }
        });

        sortingHBox.getChildren().addAll(sortedByLabel, categorySortComboBox, expressionSortComboBox);

        return sortingHBox;
    }

    private HBox exportButtonHBox() {
        HBox exportHBox = new HBox(2);

        Button saveToNewFileButton = new Button("To SVG");
        formatNode(saveToNewFileButton, 50);
        saveToNewFileButton.setStyle("-fx-font-size: 11px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
        saveToNewFileButton.setOnAction(e -> {
            try {
                try {
                    writeWeightedMeanSVG();
                } catch (SquidException squidException) {
                }
            } catch (IOException ex) {
                SquidMessageDialog.showWarningDialog(ex.getMessage(), primaryStageWindow);
                ex.printStackTrace();
            }
        });

        exportHBox.getChildren().addAll(saveToNewFileButton);

        return exportHBox;
    }

    private void writeWeightedMeanSVG() throws IOException, SquidException {
        if (squidProject.hasReportsFolder()) {
            WeightedMeanPlot myPlot = (WeightedMeanPlot) plot;
            String expressionName = myPlot.getAgeOrValueLookupString().split(" ")[0];
            TreeView<SampleTreeNodeInterface> activeTreeView = spotsTreeViewString;
            if (!autoExcludeSpotsCheckBox().isSelected()) {
                activeTreeView = spotsTreeViewCheckBox;
            }
            String reportFileNameSVG = expressionName.replace("/", "_") + ".svg";
            String reportFileNamePDF = expressionName.replace("/", "_") + ".pdf";

            try {
                File reportFileSVG = squidProject.getPrawnFileHandler().getReportsEngine().getWeightedMeansReportFile(reportFileNameSVG);
                File reportFilePDF = new File(reportFileSVG.getCanonicalPath().replaceFirst("svg", "pdf"));

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

        TreeView<SampleTreeNodeInterface> activeTreeView = spotsTreeViewString;
        if (!autoExcludeSpotsCheckBox().isSelected()) {
            activeTreeView = spotsTreeViewCheckBox;
        }

        if (activeTreeView.getRoot() != null) {
            try {
                FXCollections.sort(activeTreeView.getRoot().getChildren(), (TreeItem node1, TreeItem node2) -> {
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
            } catch (Exception e) {
                // sorting optional
            }
        }
    }
}