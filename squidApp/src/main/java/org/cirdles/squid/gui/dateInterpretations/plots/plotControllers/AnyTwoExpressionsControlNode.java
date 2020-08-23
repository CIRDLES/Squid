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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.PlotRefreshInterface;
import org.cirdles.squid.gui.dateInterpretations.plots.topsoil.AbstractTopsoilPlot;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.spots.SpotFieldNode;
import static org.cirdles.topsoil.plot.PlotOption.MCLEAN_REGRESSION;
import static org.cirdles.topsoil.plot.PlotOption.MCLEAN_REGRESSION_ENVELOPE;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class AnyTwoExpressionsControlNode extends HBox implements ToolBoxNodeInterface {

    private Map<String, ExpressionTreeInterface> mapOfNamedExpressions;
    private final ComboBox<String> xAxisExpressionComboBox;
    private final ComboBox<String> yAxisExpressionComboBox;
    private PlotRefreshInterface plotsController;
    protected boolean hasData;
    private CheckBox regressionCheckBox;
    private static boolean plotExcluded = true;

    /**
     *
     * @param plotsController the value of plotsController
     * @param hasData the value of hasData
     */
    public AnyTwoExpressionsControlNode(PlotRefreshInterface plotsController, boolean hasData) {
        super(4);

        this.xAxisExpressionComboBox = new ComboBox<>();
        this.yAxisExpressionComboBox = new ComboBox<>();
        this.plotsController = plotsController;
        this.hasData = hasData;

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
                    && entry.getValue().isSquidSwitchSTReferenceMaterialCalculation()
                    && !entry.getValue().isSquidSwitchSCSummaryCalculation()
                    && !(entry.getValue() instanceof ShrimpSpeciesNode)
                    && !(entry.getValue() instanceof ConstantNode)
                    && (!(entry.getValue() instanceof SpotFieldNode)
                    || ((entry.getValue() instanceof SpotFieldNode) && entry.getKey().equals("Hours")))) {
                sortedAvailableExpressions.add(entry.getKey());
            }
        }

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
        xAxisExpressionComboBox.setValue(PlotsController.xAxisExpressionName);

        xAxisExpressionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            plotsController.setXAxisExpressionName(newValue);
        });

        Label yAxisChooseLabel = new Label(" Choose Y-axis expression:");
        formatNode(yAxisChooseLabel, 160);

        formatNode(yAxisExpressionComboBox, 150);
        yAxisExpressionComboBox.setItems(FXCollections.observableArrayList(sortedAvailableExpressions));
        yAxisExpressionComboBox.setValue(PlotsController.yAxisExpressionName);

        yAxisExpressionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            plotsController.setYAxisExpressionName(newValue);
        });

        getChildren().addAll(yAxisChooseLabel, yAxisExpressionComboBox,
                xAxisChooseLabel, xAxisExpressionComboBox,
                separator(20.0F));

        regressionCheckBox = new CheckBox("2D Regression");
        regressionCheckBox.setSelected(false);
        formatNode(regressionCheckBox, 100);
        regressionCheckBox.setOnAction(mouseEvent -> {
            boolean isRegression = regressionCheckBox.isSelected();
            PlotsController.plot.setProperty(MCLEAN_REGRESSION.getTitle(), isRegression);
        });
        regressionCheckBox.disableProperty().bind(
                Bindings.not(new SimpleBooleanProperty(hasData))
        );

        getChildren().add(regressionCheckBox);

        CheckBox regressionUnctEnvelopeCheckBox = new CheckBox("2D Regression Unct");
        regressionUnctEnvelopeCheckBox.setSelected(false);
        formatNode(regressionUnctEnvelopeCheckBox, 125);
        regressionUnctEnvelopeCheckBox.setOnAction(mouseEvent -> {
            PlotsController.plot.setProperty(
                    MCLEAN_REGRESSION_ENVELOPE.getTitle(), regressionUnctEnvelopeCheckBox.isSelected());
        });
        regressionUnctEnvelopeCheckBox.disableProperty().bind(
                Bindings.not(regressionCheckBox.selectedProperty()
                        .and(new SimpleBooleanProperty(((AbstractTopsoilPlot) PlotsController.plot).isHasUncertainties())))
        );
        getChildren().add(regressionUnctEnvelopeCheckBox);

        Label announce = new Label(" * Means coming soon!");
        formatNode(yAxisChooseLabel, 160);
        getChildren().add(announce);
    }

    /**
     * @param hasData the hasData to set
     */
    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }

}
