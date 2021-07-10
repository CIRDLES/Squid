/*
 * Copyright 2017 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.gui.dateInterpretations.plots.topsoil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.util.Lambdas;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.utilities.stateUtilities.SquidLabData;
import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.data.Uncertainty;
import org.cirdles.topsoil.plot.PlotOptions;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.layout.Region.USE_PREF_SIZE;
import static org.cirdles.topsoil.plot.PlotOption.*;

/**
 * @author James F. Bowring
 */
public class TopsoilPlotAnyTwo extends AbstractTopsoilPlot {

    private String xAxisExpressionName;
    private String yAxisExpressionName;

    public TopsoilPlotAnyTwo() {
        this("placeholder");
    }

    public TopsoilPlotAnyTwo(String title) {
        this(title,
                new ArrayList<ShrimpFractionExpressionInterface>(),
                SquidLabData.getExistingSquidLabData().getPhysConstDefault(),
                "X",
                "Y"
        );
    }

    public TopsoilPlotAnyTwo(
            String title,
            List<ShrimpFractionExpressionInterface> shrimpFractions,
            ParametersModel physicalConstantsModel,
            String xAxisExpressionName,
            String yAxisExpressionName) {
        super();

        this.xAxisExpressionName = xAxisExpressionName;
        this.yAxisExpressionName = yAxisExpressionName;

        setupPlot(title, physicalConstantsModel);
    }

    @Override
    protected void setupPlot(String title, ParametersModel physicalConstantsModel) {
        plotOptions = PlotOptions.defaultOptions();

        plotOptions.put(ISOTOPE_SYSTEM, IsotopeSystem.GENERIC);
        plotOptions.put(X_AXIS, xAxisExpressionName);
        plotOptions.put(Y_AXIS, yAxisExpressionName);

        plotOptions.put(TITLE, title);
        plotOptions.put(CONCORDIA_LINE, false);
        plotOptions.put(POINTS, true);
        plotOptions.put(ELLIPSES, false);
        plotOptions.put(UNCTBARS, true);
        plotOptions.put(UNCTBARS_FILL, "red");

        plotOptions.put(MCLEAN_REGRESSION, false);
        plotOptions.put(MCLEAN_REGRESSION_ENVELOPE, false);

        plotOptions.put(UNCERTAINTY, Uncertainty.TWO_SIGMA_ABSOLUTE);

        plotOptions.put(LAMBDA_U234, physicalConstantsModel.getDatumByName(Lambdas.LAMBDA_234.getName()).getValue().doubleValue());
        plotOptions.put(LAMBDA_U235, physicalConstantsModel.getDatumByName(Lambdas.LAMBDA_235.getName()).getValue().doubleValue());
        plotOptions.put(LAMBDA_U238, physicalConstantsModel.getDatumByName(Lambdas.LAMBDA_238.getName()).getValue().doubleValue());

        setPlotOptions(plotOptions);
    }

    @Override
    public List<Node> toolbarControlsFactory() {
        List<Node> controls = super.toolbarControlsFactory();

        if (hasUncertainties) {
            CheckBox unctbarsCheckBox = new CheckBox("Unct Bars:");
            formatNode(unctbarsCheckBox, 80);
            unctbarsCheckBox.setSelected((Boolean) getPlotOptions().get(UNCTBARS));
            unctbarsCheckBox.setOnAction(mouseEvent -> {
                setProperty(UNCTBARS, unctbarsCheckBox.isSelected());
            });

            ChoiceBox<Uncertainty> uncertaintyChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(retrievePlottingUncertainties()));
            uncertaintyChoiceBox.setValue((Uncertainty) getPlotOptions().get(UNCERTAINTY));
            uncertaintyChoiceBox.setConverter(new StringConverter<Uncertainty>() {
                @Override
                public String toString(Uncertainty object) {
                    return object.getName();
                }

                @Override
                public Uncertainty fromString(String string) {
                    return null;
                }
            });
            uncertaintyChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Uncertainty>() {
                @Override
                public void changed(ObservableValue observable, Uncertainty oldValue, Uncertainty newValue) {
                    setProperty(UNCERTAINTY, newValue);
                }
            });

            ColorPicker unctbarsColorPicker = new ColorPicker(Color.valueOf(((String) getPlotOptions().get(UNCTBARS_FILL)).replaceAll("#", "0x")));
            unctbarsColorPicker.setPrefWidth(100);
            unctbarsColorPicker.setOnAction(mouseEvent -> {
                // to satisfy D3
                setProperty(UNCTBARS_FILL, unctbarsColorPicker.getValue().toString().substring(0, 8).replaceAll("0x", "#"));
            });

            controls.add(unctbarsCheckBox);
            controls.add(uncertaintyChoiceBox);
            controls.add(unctbarsColorPicker);
        }

        Label dataPointsLabel = new Label(" DataPoints:");
        formatNode(dataPointsLabel, 75);

        ColorPicker dataPointsColorPicker = new ColorPicker(Color.valueOf(((String) getPlotOptions().get(POINTS_FILL)).replaceAll("#", "0x")));
        dataPointsColorPicker.setPrefWidth(100);
        dataPointsColorPicker.setOnAction(mouseEvent -> {
            // to satisfy D3
            setProperty(POINTS_FILL, dataPointsColorPicker.getValue().toString().substring(0, 8).replaceAll("0x", "#"));
        });

        controls.add(dataPointsLabel);
        controls.add(dataPointsColorPicker);

        return controls;
    }

    private void formatNode(Control control, int width) {
        control.setStyle(control.getStyle() + "-font-family: 'Monospaced', 'SansSerif';-fx-font-size: 12px;-fx-font-weight: bold;");
        control.setPrefWidth(width);
        control.setMinWidth(USE_PREF_SIZE);
        control.setPrefHeight(25);
        control.setMinHeight(USE_PREF_SIZE);
    }

}