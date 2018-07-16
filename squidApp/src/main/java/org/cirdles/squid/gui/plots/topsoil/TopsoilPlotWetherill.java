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
import org.cirdles.squid.gui.topsoil.TopsoilDataFactory;
import static org.cirdles.squid.gui.SquidUI.COLORPICKER_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.plots.PlotDisplayInterface.SigmaPresentationModes.TWO_SIGMA_ABSOLUTE;
import static org.cirdles.squid.gui.topsoil.TopsoilDataFactory.EXAMPLE_CM2_DATASET;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.topsoil.app.isotope.IsotopeType;
import org.cirdles.topsoil.plot.base.BasePlotDefaultProperties;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.CONCORDIA_LINE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.ELLIPSES;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.ELLIPSE_FILL_COLOR;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.ISOTOPE_TYPE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.REGRESSION_ENVELOPE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.REGRESSION_LINE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.TITLE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.UNCERTAINTY;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.X_AXIS;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.Y_AXIS;

/**
 *
 * @author James F. Bowring
 */
public class TopsoilPlotWetherill extends AbstractTopsoilPlot {

    public TopsoilPlotWetherill(String title) {
        plot = IsotopeType.UPb.getPlots()[0].getPlot();
        plot.setData(TopsoilDataFactory.prepareWetherillData(EXAMPLE_CM2_DATASET));
        setupPlot(title);
    }

    public TopsoilPlotWetherill(String title, List<ShrimpFractionExpressionInterface> shrimpFractions) {
        plot = IsotopeType.UPb.getPlots()[0].getPlot();
        setupPlot(title);
    }

    private void setupPlot(String title) {     
        plot.setProperties(new BasePlotDefaultProperties());

        plot.setProperty(ISOTOPE_TYPE, IsotopeType.UPb.getName());
        plot.setProperty(X_AXIS, IsotopeType.UPb.getHeaders()[0]);
        plot.setProperty(Y_AXIS, IsotopeType.UPb.getHeaders()[1]);

        plot.setProperty(TITLE, title);
        plot.setProperty(CONCORDIA_LINE, true);
        plot.setProperty(ELLIPSES, true);
        plot.setProperty(ELLIPSE_FILL_COLOR, "red");

        plot.setProperty(REGRESSION_LINE, false);
        plot.setProperty(REGRESSION_ENVELOPE, false);
        
        plot.setProperty(UNCERTAINTY, TWO_SIGMA_ABSOLUTE.getSigmaMultiplier());
    }

    @Override
    public List<Node> toolbarControlsFactory() {
        List<Node> controls = super.toolbarControlsFactory();

        CheckBox ellipsesCheckBox = new CheckBox("Ellipses");
        ellipsesCheckBox.setSelected(true);
        ellipsesCheckBox.setOnAction(mouseEvent -> {
            plot.setProperty(ELLIPSES, ellipsesCheckBox.isSelected());
        });

        ChoiceBox<SigmaPresentationModes> uncertaintyChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(SigmaPresentationModes.values()));
        uncertaintyChoiceBox.setValue(SigmaPresentationModes.TWO_SIGMA_ABSOLUTE);
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
                plot.setProperty(UNCERTAINTY, newValue.getSigmaMultiplier());
            }
        });

        ColorPicker ellipsesColorPicker = new ColorPicker(Color.RED);
        ellipsesColorPicker.setStyle(COLORPICKER_CSS_STYLE_SPECS);
        ellipsesColorPicker.setPrefWidth(100);
        ellipsesColorPicker.setOnAction(mouseEvent -> {
            // to satisfy D3
            plot.setProperty(ELLIPSE_FILL_COLOR, ellipsesColorPicker.getValue().toString().substring(0, 8).replaceAll("0x", "#"));
        });

        CheckBox concordiaLineCheckBox = new CheckBox("Concordia");
        concordiaLineCheckBox.setSelected(true);
        concordiaLineCheckBox.setOnAction(mouseEvent -> {
            plot.setProperty(CONCORDIA_LINE, concordiaLineCheckBox.isSelected());
        });

        CheckBox regressionUnctEnvelopeCheckBox = new CheckBox("2D Regression Unct");
        regressionUnctEnvelopeCheckBox.setSelected(false);
        regressionUnctEnvelopeCheckBox.setOnAction(mouseEvent -> {
            plot.setProperty(REGRESSION_ENVELOPE, regressionUnctEnvelopeCheckBox.isSelected());
        });

        CheckBox regressionCheckBox = new CheckBox("2D Regression");
        regressionCheckBox.setSelected(false);
        regressionUnctEnvelopeCheckBox.setDisable(true);
        regressionCheckBox.setOnAction(mouseEvent -> {
            boolean isRegression = regressionCheckBox.isSelected();
            plot.setProperty(REGRESSION_LINE, isRegression);
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
    public void setData(List<Map<String, Object>> data) {
        plot.setData(data);
    }

    @Override
    public Node displayPlotAsNode() {
        return plot.displayAsNode();
    }

    @Override
    public void setProperty(String key, Object datum) {
        plot.getProperties().put(key, datum);
    }
}
