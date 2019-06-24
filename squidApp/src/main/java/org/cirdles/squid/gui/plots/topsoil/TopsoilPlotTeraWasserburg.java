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
package org.cirdles.squid.gui.plots.topsoil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import static org.cirdles.squid.gui.SquidUI.COLORPICKER_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.plots.PlotDisplayInterface.SigmaPresentationModes.ONE_SIGMA_ABSOLUTE;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.util.Lambdas;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.utilities.stateUtilities.SquidLabData;

import org.cirdles.topsoil.IsotopeSystem;
import org.cirdles.topsoil.data.Uncertainty;
import org.cirdles.topsoil.javafx.PlotView;
import org.cirdles.topsoil.plot.PlotOption;
import org.cirdles.topsoil.plot.PlotOptions;
import org.cirdles.topsoil.plot.PlotType;

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
                SquidLabData.getExistingSquidLabData().getPhysConstDefault());
    }

    public TopsoilPlotTeraWasserburg(
            String title,
            List<ShrimpFractionExpressionInterface> shrimpFractions,
            ParametersModel physicalConstantsModel) {
        plot = new PlotView(PlotType.SCATTER);
        setupPlot(title, physicalConstantsModel);
    }

    private void setupPlot(String title, ParametersModel physicalConstantsModel) {
        PlotOptions options = PlotOptions.defaultOptions();

        options.put(ISOTOPE_SYSTEM, IsotopeSystem.UPB);
        options.put(X_AXIS, "238U / 206Pb* ");
        options.put(Y_AXIS, "207Pb* / 206Pb* ");

        options.put(TITLE, title);
        options.put(CONCORDIA_LINE, true);
//        options.put(CONCORDIA_ENVELOPE, true);
        options.put(CONCORDIA_TYPE, org.cirdles.topsoil.plot.feature.Concordia.TERA_WASSERBURG);
        options.put(POINTS, true);
        options.put(ELLIPSES, true);
        options.put(ELLIPSES_FILL, "red");

        options.put(MCLEAN_REGRESSION, false);
        options.put(MCLEAN_REGRESSION_ENVELOPE, false);

        options.put(UNCERTAINTY, Uncertainty.ONE_SIGMA_ABSOLUTE);

        options.put(LAMBDA_U234, physicalConstantsModel.getDatumByName(Lambdas.LAMBDA_234.getName()).getValue().doubleValue());
        options.put(LAMBDA_U235, physicalConstantsModel.getDatumByName(Lambdas.LAMBDA_235.getName()).getValue().doubleValue());
        options.put(LAMBDA_U238, physicalConstantsModel.getDatumByName(Lambdas.LAMBDA_238.getName()).getValue().doubleValue());

        plot.setOptions(options);
    }

    @Override
    public List<Node> toolbarControlsFactory() {
        List<Node> controls = super.toolbarControlsFactory();

        CheckBox ellipsesCheckBox = new CheckBox("Centers");
        ellipsesCheckBox.setSelected(true);
        ellipsesCheckBox.setOnAction(mouseEvent -> {
            plot.getOptions().put(POINTS, ellipsesCheckBox.isSelected());
        });

        ChoiceBox<SigmaPresentationModes> uncertaintyChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(SigmaPresentationModes.values()));
        uncertaintyChoiceBox.setValue(SigmaPresentationModes.ONE_SIGMA_ABSOLUTE);
        uncertaintyChoiceBox.setConverter(new StringConverter<SigmaPresentationModes>() {
            @Override
            public String toString(SigmaPresentationModes object) {
                return object.getDisplayName();
            }

            @Override
            public SigmaPresentationModes fromString(String string) {
                return null;
            }
        });
        uncertaintyChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SigmaPresentationModes>() {
            @Override
            public void changed(ObservableValue observable, SigmaPresentationModes oldValue, SigmaPresentationModes newValue) {
                plot.getOptions().put(UNCERTAINTY, Uncertainty.fromMultiplier(newValue.getSigmaMultiplier()));
            }
        });

        ColorPicker ellipsesColorPicker = new ColorPicker(Color.RED);
        ellipsesColorPicker.setStyle(COLORPICKER_CSS_STYLE_SPECS);
        ellipsesColorPicker.setPrefWidth(100);
        ellipsesColorPicker.setOnAction(mouseEvent -> {
            // to satisfy D3
            plot.getOptions().put(ELLIPSES_FILL, ellipsesColorPicker.getValue().toString().substring(0, 8).replaceAll("0x", "#"));
        });

        CheckBox concordiaLineCheckBox = new CheckBox("Concordia");
        concordiaLineCheckBox.setSelected(true);
        concordiaLineCheckBox.setOnAction(mouseEvent -> {
            plot.getOptions().put(CONCORDIA_LINE, concordiaLineCheckBox.isSelected());
        });

        CheckBox regressionUnctEnvelopeCheckBox = new CheckBox("2D Regression Unct");
        regressionUnctEnvelopeCheckBox.setSelected(false);
        regressionUnctEnvelopeCheckBox.setOnAction(mouseEvent -> {
            plot.getOptions().put(MCLEAN_REGRESSION_ENVELOPE, regressionUnctEnvelopeCheckBox.isSelected());
        });

        CheckBox regressionCheckBox = new CheckBox("2D Regression");
        regressionCheckBox.setSelected(false);
        regressionUnctEnvelopeCheckBox.setDisable(true);
        regressionCheckBox.setOnAction(mouseEvent -> {
            boolean isRegression = regressionCheckBox.isSelected();
            plot.getOptions().put(MCLEAN_REGRESSION, isRegression);
            regressionUnctEnvelopeCheckBox.setDisable(!isRegression);
        });

        controls.add(ellipsesCheckBox);
        controls.add(uncertaintyChoiceBox);
        controls.add(ellipsesColorPicker);
        controls.add(concordiaLineCheckBox);
        controls.add(regressionCheckBox);
        controls.add(regressionUnctEnvelopeCheckBox);

        return controls;
    }

    @Override
    public Node displayPlotAsNode() {
        return plot;
    }

    @Override
    public void setProperty(String key, Object datum) {
        plot.getOptions().put(PlotOption.forKey(key), datum);
    }

    @Override
    public String makeAgeString(int index) {
        return "";
    }
}
