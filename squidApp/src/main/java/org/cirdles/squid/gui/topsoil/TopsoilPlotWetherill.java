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
package org.cirdles.squid.gui.topsoil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import static org.cirdles.squid.gui.SquidUI.COLORPICKER_CSS_STYLE_SPECS;
import org.cirdles.squid.gui.TopsoilPlotController;
import static org.cirdles.squid.gui.topsoil.TopsoilDataFactory.EXAMPLE_CM2_DATASET;
import org.cirdles.topsoil.app.isotope.IsotopeType;
import static org.cirdles.topsoil.app.plot.variable.Variables.RHO;
import static org.cirdles.topsoil.app.plot.variable.Variables.SIGMA_X;
import static org.cirdles.topsoil.app.plot.variable.Variables.SIGMA_Y;
import static org.cirdles.topsoil.app.plot.variable.Variables.X;
import static org.cirdles.topsoil.app.plot.variable.Variables.Y;
import org.cirdles.topsoil.plot.base.BasePlotDefaultProperties;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.CONCORDIA_LINE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.ELLIPSES;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.ELLIPSE_FILL_COLOR;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.ISOTOPE_TYPE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.REGRESSION_ENVELOPE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.REGRESSION_LINE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.TITLE;
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
    }

    @Override
    public Pane initializePlotPane() {
        TopsoilPlotController.setTopsoilPlot(this);
        Pane topsoilPlotUI = null;
        try {
            topsoilPlotUI = FXMLLoader.load(getClass().getResource("../TopsoilPlot.fxml"));
        } catch (IOException iOException) {
        }

        return topsoilPlotUI;
    }

    @Override
    public List<Node> toolbarControlsFactory() {
        List<Node> controls = super.toolbarControlsFactory();

        CheckBox ellipsesCheckBox = new CheckBox("Ellipses");
        ellipsesCheckBox.setSelected(true);
        ellipsesCheckBox.setOnAction(mouseEvent -> {
            plot.setProperty(ELLIPSES, ellipsesCheckBox.isSelected());
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

        CheckBox allSelectedCheckBox = new CheckBox("Select All");
        allSelectedCheckBox.setSelected(true);
        allSelectedCheckBox.setOnAction(mouseEvent -> {
            setSelectedAllData(allSelectedCheckBox.isSelected());
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
        controls.add(ellipsesColorPicker);
        controls.add(allSelectedCheckBox);
        controls.add(concordiaLineCheckBox);
        controls.add(regressionCheckBox);
        controls.add(regressionUnctEnvelopeCheckBox);

        return controls;
    }
}
