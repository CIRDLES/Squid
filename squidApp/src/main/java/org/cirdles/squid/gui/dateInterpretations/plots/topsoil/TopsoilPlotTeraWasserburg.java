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

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.util.Lambdas;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.utilities.stateUtilities.SquidLabData;

import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.data.Uncertainty;
import org.cirdles.topsoil.plot.PlotOptions;

import static org.cirdles.topsoil.plot.PlotOption.*;

/**
 *
 * @author James F. Bowring
 */
public class TopsoilPlotTeraWasserburg extends AbstractTopsoilPlot {

    public TopsoilPlotTeraWasserburg() {
        this("placeholder");
    }

    public TopsoilPlotTeraWasserburg(String title) {
        this(title,
                new ArrayList<ShrimpFractionExpressionInterface>(),
                SquidLabData.getExistingSquidLabData().getPhysConstDefault()
        );
    }

    public TopsoilPlotTeraWasserburg(
            String title,
            List<ShrimpFractionExpressionInterface> shrimpFractions,
            ParametersModel physicalConstantsModel) {
        super();
        setupPlot(title, physicalConstantsModel);
    }

    @Override
    protected void setupPlot(String title, ParametersModel physicalConstantsModel) {
        plotOptions = PlotOptions.defaultOptions();

        plotOptions.put(ISOTOPE_SYSTEM, IsotopeSystem.UPB);
        plotOptions.put(X_AXIS, "238U / 206Pb*");
        plotOptions.put(Y_AXIS, "207Pb* / 206Pb*");

        plotOptions.put(TITLE, title);
        plotOptions.put(CONCORDIA_LINE, true);
        plotOptions.put(CONCORDIA_TYPE, org.cirdles.topsoil.plot.feature.Concordia.TERA_WASSERBURG);
        plotOptions.put(POINTS, true);
        plotOptions.put(ELLIPSES, true);
        plotOptions.put(ELLIPSES_FILL, "red");

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

        CheckBox concordiaLineCheckBox = new CheckBox("Concordia");
        concordiaLineCheckBox.setSelected((Boolean) getPlotOptions().get(CONCORDIA_LINE));
        concordiaLineCheckBox.setOnAction(mouseEvent -> {
            setProperty(CONCORDIA_LINE, concordiaLineCheckBox.isSelected());
        });

        CheckBox ellipsesCheckBox = new CheckBox("Ellipses:");
        ellipsesCheckBox.setSelected((Boolean) getPlotOptions().get(ELLIPSES));
        ellipsesCheckBox.setOnAction(mouseEvent -> {
            setProperty(ELLIPSES, ellipsesCheckBox.isSelected());
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

        ColorPicker ellipsesColorPicker = new ColorPicker(Color.valueOf(((String) getPlotOptions().get(ELLIPSES_FILL)).replaceAll("#", "0x")));
        ellipsesColorPicker.setPrefWidth(100);
        ellipsesColorPicker.setOnAction(mouseEvent -> {
            // to satisfy D3
            setProperty(ELLIPSES_FILL, ellipsesColorPicker.getValue().toString().substring(0, 8).replaceAll("0x", "#"));
        });

        CheckBox dataPointsCheckBox = new CheckBox("DataPoints:");
        dataPointsCheckBox.setSelected(true);
        dataPointsCheckBox.setOnAction(mouseEvent -> {
            setProperty(POINTS, dataPointsCheckBox.isSelected());
        });

        ColorPicker dataPointsColorPicker = new ColorPicker(Color.valueOf(((String) getPlotOptions().get(POINTS_FILL)).replaceAll("#", "0x")));
        dataPointsColorPicker.setPrefWidth(100);
        dataPointsColorPicker.setOnAction(mouseEvent -> {
            // to satisfy D3
            setProperty(POINTS_FILL, dataPointsColorPicker.getValue().toString().substring(0, 8).replaceAll("0x", "#"));
        });
        controls.add(concordiaLineCheckBox);
        controls.add(ellipsesCheckBox);
        controls.add(uncertaintyChoiceBox);
        controls.add(ellipsesColorPicker);
        controls.add(dataPointsCheckBox);
        controls.add(dataPointsColorPicker);

        return controls;
    }

}
