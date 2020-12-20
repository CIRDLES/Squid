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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Path;
import org.cirdles.squid.dialogs.SquidMessageDialog;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategoryInterface;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumnInterface;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.gui.dataViews.SampleTreeNodeInterface;
import static org.cirdles.squid.gui.dateInterpretations.plots.plotControllers.PlotsController.spotSummaryDetails;
import static org.cirdles.squid.gui.dateInterpretations.plots.plotControllers.PlotsController.spotsTreeViewCheckBox;
import static org.cirdles.squid.gui.dateInterpretations.plots.plotControllers.PlotsController.spotsTreeViewString;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.WeightedMeanPlot;
import static org.cirdles.squid.gui.utilities.stringUtilities.StringTester.stringIsSquidRatio;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategory;
import static org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategory.defaultRefMatWMSortingCategories;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumn;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.PlotRefreshInterface;
import org.cirdles.squid.gui.utilities.fileUtilities.FileHandler;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class RefMatWeightedMeanControlNode extends HBox implements ToolBoxNodeInterface {

    private PlotRefreshInterface plotsController;

    private final ComboBox<SquidReportCategoryInterface> categorySortComboBox;
    private final ComboBox<SquidReportColumnInterface> expressionSortComboBox;

    public RefMatWeightedMeanControlNode(PlotRefreshInterface plotsController) {
        super(4);

        this.plotsController = plotsController;

        this.categorySortComboBox = new ComboBox<>();
        this.expressionSortComboBox = new ComboBox<>();

        initNode();

        // set up custom sorting for ref mat wm
        List<SquidReportCategory> refMatWMSortingCategories = defaultRefMatWMSortingCategories;
        // handle special case where raw ratios is populated on the fly per task
        SquidReportCategoryInterface rawRatiosCategory = refMatWMSortingCategories.get(1);
        LinkedList<SquidReportColumnInterface> categoryColumns = new LinkedList<>();
        for (String ratioName : squidProject.getTask().getRatioNames()) {
            SquidReportColumnInterface column = SquidReportColumn.createSquidReportColumn(ratioName);
            categoryColumns.add(column);
        }
        rawRatiosCategory.setCategoryColumns(categoryColumns);

        categorySortComboBox.setItems(FXCollections.observableArrayList(defaultRefMatWMSortingCategories));
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
        HBox corrChoiceHBox = new RefMatWeightedMeanToolBoxNode(plotsController);
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
        autoExcludeSpotsCheckBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                squidProject.getTask().setSquidAllowsAutoExclusionOfSpots(autoExcludeSpotsCheckBox.isSelected());
                // this will cause weighted mean expressions to be changed with boolean flag
                squidProject.getTask().updateRefMatCalibConstWMeanExpressions(autoExcludeSpotsCheckBox.isSelected());
                plotsController.showActivePlot();
            }
        });
        formatNode(autoExcludeSpotsCheckBox, 110);
        return autoExcludeSpotsCheckBox;
    }

    private CheckBox showExcludedSpotsCheckBox() {
        CheckBox autoExcludeSpotsCheckBox = new CheckBox("Plot rejects");
        autoExcludeSpotsCheckBox.setSelected(WeightedMeanPlot.doPlotRejectedSpots);
        autoExcludeSpotsCheckBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                WeightedMeanPlot.doPlotRejectedSpots = !WeightedMeanPlot.doPlotRejectedSpots;
                plotsController.showRefMatWeightedMeanPlot();
            }
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
                plotsController.showRefMatWeightedMeanPlot();
            }
        });

        plotChoiceHBox.getChildren().addAll(plotChoiceLabel, ageRB, ccRB);

        return plotChoiceHBox;
    }

    private HBox sortedHBox() {

        HBox sortingHBox = new HBox(5);

        Label sortedByLabel = new Label("Sorted Ascending by:");
        formatNode(sortedByLabel, 125);

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

        formatNode(expressionSortComboBox, 115);
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
        
        Button saveToNewFileButton = new Button("Export to SVG");
        formatNode(saveToNewFileButton, 150);
        saveToNewFileButton.setStyle("-fx-font-size: 12px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
        saveToNewFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    WeightedMeanPlot SVGPlot = (WeightedMeanPlot) PlotsController.plot;
                    File file = FileHandler.saveWeightedMeanSVGFile(SVGPlot, primaryStageWindow);
                    SVGPlot.outputToSVG(file);
                }
                catch(IOException ex) {
                    SquidMessageDialog.showWarningDialog(ex.getMessage(), primaryStageWindow);
                    ex.printStackTrace();
                }  
            }
        });
        
        exportHBox.getChildren().addAll(saveToNewFileButton);
        
        return exportHBox;
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
