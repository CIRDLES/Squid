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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.VLineTo;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.exceptions.SquidException;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.gui.dataViews.SampleNode;
import org.cirdles.squid.gui.dataViews.SampleTreeNodeInterface;
import org.cirdles.squid.gui.dateInterpretations.plots.PlotDisplayInterface;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.WeightedMeanPlot;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.WeightedMeanRefreshInterface;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategory;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategoryInterface;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumn;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumnInterface;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import org.cirdles.squid.tasks.taskUtilities.SpotGroupProcessor;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class SamplesPlottingNode extends HBox {

    private Map<String, List<ShrimpFractionExpressionInterface>> mapOfSpotsBySampleNames;
    private final ComboBox<String> sampleComboBox;
    private final WeightedMeanRefreshInterface plotsController;
    private SampleNode sampleNode;
    private PlotDisplayInterface sampleNodeSelectedAgeWMPlot;
    private CheckBoxTreeItem<SampleTreeNodeInterface> sampleItem;
    private final Slider probabilitySlider;
    private final Label probLabel;

    private final ComboBox<SquidReportCategoryInterface> categoryComboBox;
    private final ComboBox<SquidReportColumnInterface> expressionComboBox;

    public SamplesPlottingNode(WeightedMeanRefreshInterface plotsController) {
        super(4);

        this.sampleComboBox = new ComboBox<>();
        this.plotsController = plotsController;
        this.probabilitySlider = new Slider(0, 1, 0);
        this.probLabel = new Label("0.0");
        this.categoryComboBox = new ComboBox<>();
        this.expressionComboBox = new ComboBox<>();

        initNode();

        // handle special case where raw ratios is populated on the fly per task
        SquidReportCategory rawRatiosCategory = SquidReportCategory.defaultSquidReportCategories.get(1);
        LinkedList<SquidReportColumnInterface> categoryColumns = new LinkedList<>();
        for (String ratioName : squidProject.getTask().getRatioNames()) {
            SquidReportColumnInterface column = SquidReportColumn.createSquidReportColumn(ratioName);
            categoryColumns.add(column);
        }
        rawRatiosCategory.setCategoryColumns(categoryColumns);

        sampleComboBox.getSelectionModel().selectFirst();

        categoryComboBox.setItems(FXCollections.observableArrayList(SquidReportCategory.defaultSquidReportCategories));
        categoryComboBox.getSelectionModel().selectFirst();

        expressionComboBox.setItems(FXCollections.observableArrayList(categoryComboBox.getSelectionModel().getSelectedItem().getCategoryColumns()));

    }

    private void initNode() {

        setStyle("-fx-padding: 1;" + "-fx-background-color: white;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 0 2 0 2;"
                + "-fx-border-radius: 4;" + "-fx-border-color: blue;-fx-effect: null;");

        VBox samplesToolBox = samplesVBox();
        VBox domainToolBox = expressionVBox();
        VBox filterToolBox = filterVBox();
        VBox sortingToolBox = sortedVBox();
        VBox saveAsToolBox = saveAsVBox();

        Path separator1 = separator();
        Path separator2 = separator();
        Path separator3 = separator();
        Path separator4 = separator();

        getChildren().addAll(samplesToolBox, separator1, domainToolBox, separator2, filterToolBox, separator3, sortingToolBox, separator4, saveAsToolBox);

        setAlignment(Pos.CENTER);

    }

    private Path separator() {
        Path separator = new Path();
        separator.getElements().add(new MoveTo(2.0f, 0.0f));
        separator.getElements().add(new VLineTo(60.0f));
        separator.setStroke(new Color(251 / 255, 109 / 255, 66 / 255, 1));
        separator.setStrokeWidth(2);

        return separator;
    }

    private VBox samplesVBox() {
        VBox sampleNameToolBox = new VBox(2);

        Label sampleInfoLabel = new Label("Samples:");
        formatNode(sampleInfoLabel, 100);

        mapOfSpotsBySampleNames = squidProject.getTask().getMapOfUnknownsBySampleNames();
        // case of sample names chosen remove the redundant superset
        if (mapOfSpotsBySampleNames.size() > 1) {
            mapOfSpotsBySampleNames.remove(Squid3Constants.SpotTypes.UNKNOWN.getPlotType());
        }

        formatNode(sampleComboBox, 100);
        sampleComboBox.setItems(FXCollections.observableArrayList(mapOfSpotsBySampleNames.keySet()));

        sampleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            displaySample(newValue);
        });

        sampleNameToolBox.getChildren().addAll(sampleInfoLabel, sampleComboBox);

        return sampleNameToolBox;
    }

    private void displaySample(String newValue) {
        sampleNode = new SampleNode(newValue);
        List<ShrimpFractionExpressionInterface> shrimpFractions = mapOfSpotsBySampleNames.get(newValue);

        String selectedAge = shrimpFractions.get(0).getSelectedAgeExpressionName();
        ((Task) squidProject.getTask()).evaluateUnknownsWithChangedParameters(shrimpFractions);
        SpotSummaryDetails spotSummaryDetailsWM
                = ((Task) squidProject.getTask()).evaluateSelectedAgeWeightedMeanForUnknownGroup(newValue, shrimpFractions);
        spotSummaryDetailsWM.setManualRejectionEnabled(true);

        sampleNodeSelectedAgeWMPlot = new WeightedMeanPlot(
                new Rectangle(1000, 600),
                " Sample " + newValue,
                spotSummaryDetailsWM,
                selectedAge,
                0.0,
                plotsController);

        sampleNode.setSamplePlotWM(sampleNodeSelectedAgeWMPlot);
        sampleNode.setPlotsController(plotsController);

        probabilitySlider.valueProperty().setValue(spotSummaryDetailsWM.getMinProbabilityWM());
        probLabel.textProperty().setValue(String.format("%.2f", spotSummaryDetailsWM.getMinProbabilityWM()));

        PlotsController.plot = sampleNodeSelectedAgeWMPlot;
        PlotsController.spotSummaryDetails = spotSummaryDetailsWM;

        List<ShrimpFractionExpressionInterface> shrimpFractionsDetails = spotSummaryDetailsWM.getSelectedSpots();
        List<SampleTreeNodeInterface> fractionNodeDetailsWM = new ArrayList<>();

        for (int i = 0; i < shrimpFractionsDetails.size(); i++) {
            WeightedMeanSpotNode fractionNodeWM
                    = new WeightedMeanSpotNode(shrimpFractionsDetails.get(i), i);
            fractionNodeDetailsWM.add(fractionNodeWM);
        }

        // sort the fractions based on age
        Collections.sort(fractionNodeDetailsWM, (SampleTreeNodeInterface fraction1, SampleTreeNodeInterface fraction2) -> {
            double age1 = fraction1.getShrimpFraction().getTaskExpressionsEvaluationsPerSpotByField(selectedAge)[0][0];
            double age2 = fraction2.getShrimpFraction().getTaskExpressionsEvaluationsPerSpotByField(selectedAge)[0][0];

            // original aquire time order
            int retVal = 0;
            if (spotSummaryDetailsWM.getPreferredViewSortOrder() == -1) {
                retVal = Double.compare(age2, age1);
            }
            if (spotSummaryDetailsWM.getPreferredViewSortOrder() == 1) {
                retVal = Double.compare(age1, age2);
            }
            return retVal;
        });

        ObservableList<SampleTreeNodeInterface> fractionNodesWM = FXCollections.observableArrayList(fractionNodeDetailsWM);

        // build spot tree for sample
        sampleItem = new CheckBoxTreeItem<>(sampleNode);
        sampleItem.setSelected(false);
        PlotsController.spotsTreeViewCheckBox.setRoot(sampleItem);
        PlotsController.spotsTreeViewCheckBox.setShowRoot(false);
        PlotsController.currentlyPlottedSampleTreeNode = sampleItem;

        for (int i = 0; i < fractionNodesWM.size(); i++) {
            final CheckBoxTreeItem<SampleTreeNodeInterface> checkBoxTreeItemWM
                    = new CheckBoxTreeItem<>(fractionNodesWM.get(i));
            sampleItem.getChildren().add(checkBoxTreeItemWM);

            checkBoxTreeItemWM.setSelected(!spotSummaryDetailsWM
                    .getRejectedIndices()[((WeightedMeanSpotNode) checkBoxTreeItemWM.getValue())
                            .getIndexOfSpot()]);

            checkBoxTreeItemWM.selectedProperty().addListener((observable, oldChoice, newChoice) -> {
                ((WeightedMeanSpotNode) checkBoxTreeItemWM.getValue()).setSelectedProperty(new SimpleBooleanProperty(newChoice));
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
        categoryComboBox.getSelectionModel().selectFirst();

        plotsController.refreshPlot();
    }

    private VBox expressionVBox() {
        VBox expressionToolBox = new VBox(2);

        HBox expressionInfoHBox = new HBox(5);

        Label expressionInfoLabel = new Label("Calc Weighted Mean from");
        formatNode(expressionInfoLabel, 155);

        CheckBox expressionFromReportCheckBox = new CheckBox("Include Report Categories");
        formatNode(expressionFromReportCheckBox, 175);
        expressionFromReportCheckBox.setDisable(true);

        expressionInfoHBox.getChildren().addAll(expressionInfoLabel, expressionFromReportCheckBox);

        HBox categoryInfoHBox = new HBox(5);

        formatNode(categoryComboBox, 120);
        categoryComboBox.setPromptText("Category");

        categoryComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SquidReportCategoryInterface>() {
            @Override
            public void changed(ObservableValue<? extends SquidReportCategoryInterface> observable, SquidReportCategoryInterface oldValue, SquidReportCategoryInterface newValue) {
                // first get columns from category   
                expressionComboBox.getSelectionModel().clearSelection();
                expressionComboBox.setItems(FXCollections.observableArrayList(newValue.getCategoryColumns()));

                // special case when Ages is picked, we look up stored WM for age name in sample
                if (newValue.getDisplayName().compareToIgnoreCase("Ages") == 0) {
                    // restore age wm
                    sampleNode.setSamplePlotWM(sampleNodeSelectedAgeWMPlot);
                    PlotsController.plot = sampleNodeSelectedAgeWMPlot;

                    String selectedAge = sampleNode.getSpotSummaryDetailsWM().getExpressionTree().getName().split("_WM_")[0];
                    expressionComboBox.getSelectionModel().select(newValue.findColumnByName(selectedAge));
                } else {
                    // show the first
                    expressionComboBox.getSelectionModel().selectFirst();
                }
            }
        });

        formatNode(expressionComboBox, 200);
        expressionComboBox.setPromptText("Expression");
        expressionComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SquidReportColumnInterface>() {
            @Override
            public void changed(ObservableValue<? extends SquidReportColumnInterface> observable, SquidReportColumnInterface oldValue, SquidReportColumnInterface newValue) {
                if (newValue != null) {
                    String selectedExpression = newValue.getExpressionName();
                    if (categoryComboBox.getSelectionModel().getSelectedItem().getDisplayName().compareToIgnoreCase("AGES") == 0) {
                        // TODO: TEST FOR AGES vs others

                        ((Task) squidProject.getTask()).setUnknownGroupSelectedAge(sampleNode.getSpotSummaryDetailsWM().getSelectedSpots(), newValue.getExpressionName());
                        ((Task) squidProject.getTask()).evaluateUnknownsWithChangedParameters(sampleNode.getSpotSummaryDetailsWM().getSelectedSpots());

                        boolean[] savedRejectedIndices = sampleNode.getSpotSummaryDetailsWM().getRejectedIndices();
                        int savedPreferredViewSortOrder = sampleNode.getSpotSummaryDetailsWM().getPreferredViewSortOrder();
                        double savedMinProbabilityWM = sampleNode.getSpotSummaryDetailsWM().getMinProbabilityWM();

                        SpotSummaryDetails spotSummaryDetailsWM
                                = ((Task) squidProject.getTask())
                                        .evaluateSelectedAgeWeightedMeanForUnknownGroup(sampleNode.getNodeName(), sampleNode.getSpotSummaryDetailsWM().getSelectedSpots());
                        spotSummaryDetailsWM.setManualRejectionEnabled(true);
                        spotSummaryDetailsWM.setRejectedIndices(savedRejectedIndices);
                        spotSummaryDetailsWM.setPreferredViewSortOrder(savedPreferredViewSortOrder);
                        spotSummaryDetailsWM.setMinProbabilityWM(savedMinProbabilityWM);

                        PlotDisplayInterface myPlot = ((SampleNode) sampleNode).getSamplePlotWM();
                        ((WeightedMeanPlot) myPlot).setSpotSummaryDetails(spotSummaryDetailsWM);
                        ((WeightedMeanPlot) myPlot).setAgeOrValueLookupString(selectedExpression);
                        sampleNode.getSpotSummaryDetailsWM().setSortFlavor("AGE");
                        sortFractionCheckboxesByValue("AGE", selectedExpression, savedPreferredViewSortOrder);
                        PlotsController.plot = myPlot;
                        ((SampleNode) sampleNode).getPlotsController().refreshPlot();
                    } else {
                        // non- WM case for exploration
                        SpotSummaryDetails spotSummaryDetailsWM
                                = ((Task) squidProject.getTask())
                                        .evaluateSelectedExpressionWeightedMeanForUnknownGroup(
                                                selectedExpression, sampleNode.getNodeName(), sampleNode.getSpotSummaryDetailsWM().getSelectedSpots());
                        spotSummaryDetailsWM.setManualRejectionEnabled(true);
                        spotSummaryDetailsWM.setRejectedIndices(((WeightedMeanPlot) PlotsController.plot).getSpotSummaryDetails().getRejectedIndices());
                        spotSummaryDetailsWM.setPreferredViewSortOrder(((WeightedMeanPlot) PlotsController.plot).getSpotSummaryDetails().getPreferredViewSortOrder());
                        spotSummaryDetailsWM.setMinProbabilityWM(((WeightedMeanPlot) PlotsController.plot).getSpotSummaryDetails().getMinProbabilityWM());

                        PlotDisplayInterface myPlot = new WeightedMeanPlot(
                                new Rectangle(1000, 600),
                                " Sample " + sampleNode.getNodeName(),
                                spotSummaryDetailsWM,
                                selectedExpression,
                                0.0,
                                plotsController);

                        PlotsController.plot = myPlot;
                        sampleNode.setSamplePlotWM(myPlot);
                        ((SampleNode) sampleNode).getPlotsController().refreshPlot();
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

        HBox filterInfoHBox = new HBox();

        Label filterInfoLabel = new Label("Filter by min. Prob of Fit:");
        formatNode(filterInfoLabel, 150);

        formatNode(probLabel, 30);
        probLabel.setStyle(probLabel.getStyle() + "-fx-text-fill: red;");

        filterInfoHBox.getChildren().addAll(filterInfoLabel, probLabel);

        probabilitySlider.setShowTickLabels(true);
        probabilitySlider.setShowTickMarks(true);
        probabilitySlider.setMajorTickUnit(0.25);
        probabilitySlider.setMinorTickCount(5);
        probabilitySlider.setBlockIncrement(0.05);
        probabilitySlider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean wasChanging, Boolean changing) {

                if (wasChanging) {
                    updateSampleFromSlider(probabilitySlider.getValue());
                }
            }
        });
        probabilitySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {

                updateSampleFromSlider(newValue.doubleValue());
            }
        });
        probabilitySlider.onMouseClickedProperty().addListener(new ChangeListener<EventHandler<? super MouseEvent>>() {
            @Override
            public void changed(ObservableValue<? extends EventHandler<? super MouseEvent>> observable, EventHandler<? super MouseEvent> oldValue, EventHandler<? super MouseEvent> newValue) {
                updateSampleFromSlider(probabilitySlider.getValue());
            }
        });

        filterToolBox.getChildren()
                .addAll(filterInfoHBox, probabilitySlider);

        return filterToolBox;
    }

    private void updateSampleFromSlider(double newValue) {
        probLabel.textProperty().setValue(
                String.format("%.2f", newValue));
        sampleNode.getSpotSummaryDetailsWM().setMinProbabilityWM(newValue);
        SpotSummaryDetails spotSummaryDetailsWM = sampleNode.getSpotSummaryDetailsWM();
        SpotGroupProcessor.findCoherentGroupOfSpotsForWeightedMean(
                squidProject.getTask(), spotSummaryDetailsWM, newValue);

        for (TreeItem<SampleTreeNodeInterface> spotCheckBox : PlotsController.spotsTreeViewCheckBox.getRoot().getChildren()) {
            int indexOfSpot = ((WeightedMeanSpotNode) spotCheckBox.getValue()).getIndexOfSpot();
            ((CheckBoxTreeItem<SampleTreeNodeInterface>) spotCheckBox).setSelected(
                    !sampleNode.getSpotSummaryDetailsWM().getRejectedIndices()[indexOfSpot]);
        }

        plotsController.refreshPlot();
    }

    private VBox sortedVBox() {
        VBox sortedToolBox = new VBox(2);

        CheckBox sortedByCheckbox = new CheckBox("Spots Sorted by Domain Ascending:");
        formatNode(sortedByCheckbox, 225);

        HBox sortingHBox = new HBox(5);
        ComboBox<String> categorySortComboBox = new ComboBox<>();
        formatNode(categorySortComboBox, 120);
        categorySortComboBox.setPromptText("Category");

        ComboBox<String> expressionSortComboBox = new ComboBox<>();
        formatNode(expressionSortComboBox, 200);
        expressionSortComboBox.setPromptText("Expression");

        sortingHBox.getChildren().addAll(categorySortComboBox, expressionSortComboBox);

        sortedToolBox.getChildren().addAll(sortedByCheckbox, sortingHBox);

        return sortedToolBox;
    }

    private VBox saveAsVBox() {
        VBox saveAsToolBox = new VBox(2);

        HBox saveDataHBox = new HBox(5);
        Label saveWMStatsLabel = new Label("Save WM stats to file:");
        saveWMStatsLabel.setAlignment(Pos.CENTER_RIGHT);
        formatNode(saveWMStatsLabel, 125);

        ToggleGroup saveAsToggleGroup = new ToggleGroup();

        RadioButton saveToFileRadioButton = new RadioButton("New");
        saveToFileRadioButton.setSelected(true);
        formatNode(saveToFileRadioButton, 50);
        saveToFileRadioButton.setToggleGroup(saveAsToggleGroup);

        RadioButton appendToFileRadioButton = new RadioButton("Append");
        formatNode(appendToFileRadioButton, 65);
        appendToFileRadioButton.setToggleGroup(saveAsToggleGroup);

        saveDataHBox.getChildren().addAll(saveWMStatsLabel, saveToFileRadioButton, appendToFileRadioButton);

        HBox saveImageHBox = new HBox(5);
        Label saveImageLabel = new Label("Save WM Image as:");
        saveImageLabel.setAlignment(Pos.CENTER_RIGHT);
        formatNode(saveImageLabel, 125);

        ToggleGroup saveImageAsToggleGroup = new ToggleGroup();

        RadioButton saveAsSVGRadioButton = new RadioButton("SVG");
        saveAsSVGRadioButton.setSelected(true);
        formatNode(saveAsSVGRadioButton, 50);
        saveAsSVGRadioButton.setToggleGroup(saveImageAsToggleGroup);

        RadioButton saveAsPDFRadioButton = new RadioButton("PDF");
        formatNode(saveAsPDFRadioButton, 50);
        saveAsPDFRadioButton.setToggleGroup(saveImageAsToggleGroup);

        saveImageHBox.getChildren().addAll(saveImageLabel, saveAsSVGRadioButton, saveAsPDFRadioButton);

        saveAsToolBox.getChildren().addAll(saveDataHBox, saveImageHBox);

        return saveAsToolBox;
    }

    private void formatNode(Control control, int width) {
        control.setStyle(control.getStyle() + "-font-family: San Serif;-fx-font-size: 12px;-fx-font-weight: bold;");
        control.setPrefWidth(width);
        control.setMinWidth(USE_PREF_SIZE);
        control.setPrefHeight(23);
        control.setMinHeight(USE_PREF_SIZE);
    }

    private void sortFractionCheckboxesByValue(String flavor, String selectedFieldName, int savedPreferredViewSortOrder) {
        FXCollections.sort(sampleItem.getChildren(), (TreeItem node1, TreeItem node2) -> {
            // original aquire time order  
            int retVal = 0;
            if (savedPreferredViewSortOrder == 0) {
                long acquireTime1 = ((SampleTreeNodeInterface) node1.getValue()).getShrimpFraction().getDateTimeMilliseconds();
                long acquireTime2 = ((SampleTreeNodeInterface) node2.getValue()).getShrimpFraction().getDateTimeMilliseconds();
                retVal = Long.compare(acquireTime1, acquireTime2);
            } else {
                double valueFromNode1 = 0.0;
                double valueFromNode2 = 0.0;
                switch (flavor) {
                    case "AGE":
                        valueFromNode1 = ((SampleTreeNodeInterface) node1.getValue()).getShrimpFraction()
                                .getTaskExpressionsEvaluationsPerSpotByField(selectedFieldName)[0][0];
                        valueFromNode2 = ((SampleTreeNodeInterface) node2.getValue()).getShrimpFraction()
                                .getTaskExpressionsEvaluationsPerSpotByField(selectedFieldName)[0][0];
                        break;
                    case "RATIO":
                        double[][] resultsFromNode1
                                = Arrays.stream(((SampleTreeNodeInterface) node1.getValue()).getShrimpFraction()
                                        .getIsotopicRatioValuesByStringName(selectedFieldName)).toArray(double[][]::new);
                        valueFromNode1 = resultsFromNode1[0][0];
                        double[][] resultsFromNode2
                                = Arrays.stream(((SampleTreeNodeInterface) node2.getValue()).getShrimpFraction()
                                        .getIsotopicRatioValuesByStringName(selectedFieldName)).toArray(double[][]::new);
                        valueFromNode2 = resultsFromNode2[0][0];
                        break;
                }

                if (savedPreferredViewSortOrder == 1) {
                    retVal = Double.compare(valueFromNode1, valueFromNode2);
                }
                if (savedPreferredViewSortOrder == -1) {
                    retVal = Double.compare(valueFromNode2, valueFromNode1);
                }
            }
            return retVal;
        });
    }

}
