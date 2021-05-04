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

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.PlotRefreshInterface;
import org.cirdles.squid.gui.dateInterpretations.plots.topsoil.AbstractTopsoilPlot;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.spots.SpotFieldNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.topsoil.plot.PlotOption.MCLEAN_REGRESSION;
import static org.cirdles.topsoil.plot.PlotOption.MCLEAN_REGRESSION_ENVELOPE;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class SamplesAnyTwoExpressionsControlNode extends HBox implements ToolBoxNodeInterface {

    private Map<String, ExpressionTreeInterface> mapOfNamedExpressions;
    private final Map<String, List<ShrimpFractionExpressionInterface>> mapOfSpotsBySampleNames;
    private ComboBox<String> xAxisExpressionComboBox;
    private ComboBox<String> yAxisExpressionComboBox;
    public static String savedXAxisExpressionName = null;
    public static String savedYAxisExpressionName = null;
    public static String savedSampleName = null;
    private final ComboBox<String> sampleComboBox;
    private CheckBox regressionCheckBox;
    private CheckBox regressionUnctEnvelopeCheckBox;
    private final PlotRefreshInterface plotsController;
    private boolean plotExcluded = true;
    private boolean hasData = true;
    /**
     *
     * @param plotsController the value of plotsController
     */
    public SamplesAnyTwoExpressionsControlNode(PlotRefreshInterface plotsController) {
        super(4);

        this.mapOfSpotsBySampleNames  = squidProject.getTask().getMapOfUnknownsBySampleNames();
        this.xAxisExpressionComboBox = new ComboBox<>();
        this.yAxisExpressionComboBox = new ComboBox<>();
        this.sampleComboBox = new ComboBox<>();
        this.plotsController = plotsController;

        initNode();

    }

    private void initNode() {
        setStyle("-fx-padding: 1;" + "-fx-background-color: white;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 0 2 0 2;"
                + "-fx-border-radius: 4;" + "-fx-border-color: blue;-fx-effect: null;");

        setPrefHeight(23);
        setHeight(23);
        setFillHeight(true);
        setAlignment(Pos.CENTER);

        mapOfNamedExpressions = squidProject.getTask().getNamedExpressionsMap();
        List<String> sortedAvailableExpressions = new ArrayList<>();
        for (Entry<String, ExpressionTreeInterface> entry : mapOfNamedExpressions.entrySet()) {
            if (entry.getValue().amHealthy()
                    && entry.getValue().isSquidSwitchSAUnknownCalculation()
                    && !entry.getValue().isSquidSwitchSCSummaryCalculation()
                    && !(entry.getValue() instanceof ShrimpSpeciesNode)
                    && !(entry.getValue() instanceof ConstantNode)
                    && (!(entry.getValue() instanceof SpotFieldNode)
                    || ((entry.getValue() instanceof SpotFieldNode) && entry.getKey().equals("Hours")))) {
                sortedAvailableExpressions.add(entry.getKey());
            }
        }
        
        HBox samplesToolBox = samplesHBox();
        getChildren().addAll(samplesToolBox, separator(20.0F));

        CheckBox showExcludedSpotsCheckBox = new CheckBox("Plot Excluded");
        showExcludedSpotsCheckBox.setSelected(plotExcluded);
        formatNode(showExcludedSpotsCheckBox, 100);
        showExcludedSpotsCheckBox.setOnAction(mouseEvent -> {
            plotsController.showExcludedSpots(showExcludedSpotsCheckBox.isSelected());
            plotExcluded = showExcludedSpotsCheckBox.isSelected();
        });

        getChildren().addAll(showExcludedSpotsCheckBox, separator(20.0F));

        Label xAxisChooseLabel = new Label("Choose X-axis expression:");
        formatNode(xAxisChooseLabel, 160);

        formatNode(xAxisExpressionComboBox, 150);
        xAxisExpressionComboBox.setItems(FXCollections.observableArrayList(sortedAvailableExpressions));
        String selectedXAxisExpression = savedXAxisExpressionName;
        if (selectedXAxisExpression == null) {
            selectedXAxisExpression = sortedAvailableExpressions.get(0);
        }
        PlotsController.xAxisExpressionName = selectedXAxisExpression;
        xAxisExpressionComboBox.setValue(PlotsController.xAxisExpressionName);

        Label yAxisChooseLabel = new Label(" Choose Y-axis expression:");
        formatNode(yAxisChooseLabel, 160);

        formatNode(yAxisExpressionComboBox, 150);
        yAxisExpressionComboBox.setItems(FXCollections.observableArrayList(sortedAvailableExpressions));
        String selectedYAxisExpression = savedYAxisExpressionName;
        if (selectedYAxisExpression == null) {
            selectedYAxisExpression = sortedAvailableExpressions.get(0);
        }
        PlotsController.yAxisExpressionName = selectedYAxisExpression;
        yAxisExpressionComboBox.setValue(PlotsController.yAxisExpressionName);

        getChildren().addAll(yAxisChooseLabel, yAxisExpressionComboBox,
                xAxisChooseLabel, xAxisExpressionComboBox,
                separator(20.0F));

        regressionCheckBox = new CheckBox("2D Regression");
        regressionCheckBox.setSelected(false);
        formatNode(regressionCheckBox, 100);
        regressionCheckBox.disableProperty().bind(
                Bindings.not(new SimpleBooleanProperty(hasData))
        );

        getChildren().add(regressionCheckBox);

        regressionUnctEnvelopeCheckBox = new CheckBox("2D Regression Unct");
        regressionUnctEnvelopeCheckBox.setSelected(false);
        formatNode(regressionUnctEnvelopeCheckBox, 125);

        getChildren().add(regressionUnctEnvelopeCheckBox);

        Label announce = new Label(" * Means coming soon!");
        formatNode(yAxisChooseLabel, 160);
        getChildren().add(announce);
    }
    
        
    private HBox samplesHBox() {
        HBox sampleNameToolBox = new HBox(2);

        Label sampleInfoLabel = new Label("Samples: ");
        formatNode(sampleInfoLabel, 60);

        formatNode(sampleComboBox, 90);
        if (mapOfSpotsBySampleNames.size() > 1) {
            mapOfSpotsBySampleNames.remove(Squid3Constants.SpotTypes.UNKNOWN.getSpotTypeName());
        }
        sampleComboBox.setItems(FXCollections.observableArrayList(mapOfSpotsBySampleNames.keySet()));
        sampleNameToolBox.getChildren().addAll(sampleInfoLabel, sampleComboBox);

        return sampleNameToolBox;
    }


    public void configureCheckBoxesWithDisplayedPlot() {
        regressionCheckBox.setOnAction(mouseEvent -> {
            boolean isRegression = regressionCheckBox.isSelected();
            PlotsController.plot.setProperty(MCLEAN_REGRESSION.getTitle(), isRegression);
        });

        regressionUnctEnvelopeCheckBox.setOnAction(mouseEvent -> {
            PlotsController.plot.setProperty(
                    MCLEAN_REGRESSION_ENVELOPE.getTitle(), regressionUnctEnvelopeCheckBox.isSelected());
        });

        regressionUnctEnvelopeCheckBox.disableProperty().bind(
                Bindings.not(regressionCheckBox.selectedProperty()
                        .and(new SimpleBooleanProperty(((AbstractTopsoilPlot) PlotsController.plot).isHasUncertainties())))
        );
    }

    public ComboBox<String> getSampleComboBox() {
        return this.sampleComboBox;
    }

    public ComboBox<String> getXAxisExpressionComboBox() { return this.xAxisExpressionComboBox; }

    public ComboBox<String> getYAxisExpressionComboBox() { return this.yAxisExpressionComboBox; }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }

}
