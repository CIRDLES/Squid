/*
 * Copyright 2020 James F. Bowring and CIRDLES.org.
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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.VLineTo;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.PlotRefreshInterface;

import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.gui.dateInterpretations.plots.plotControllers.PlotsController.correction;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.*;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class SamplesConcordiaToolBoxNode extends HBox {

    private final PlotRefreshInterface plotsController;

    public SamplesConcordiaToolBoxNode(PlotRefreshInterface plotsController) {
        super(5);
        this.plotsController = plotsController;

        initNode();
    }

    private void initNode() {

        Label corrChoiceLabel = new Label("Corr:");
        formatNode(corrChoiceLabel, 35);

        ToggleGroup corrGroup = new ToggleGroup();

        RadioButton corr4_RadioButton = new RadioButton("4-corr");
        corr4_RadioButton.setToggleGroup(corrGroup);
        corr4_RadioButton.setUserData(PB4CORR);
        corr4_RadioButton.setSelected(correction.equals(PB4CORR));
        formatNode(corr4_RadioButton, 60);

        RadioButton corr7_RadioButton = new RadioButton("7-corr");
        corr7_RadioButton.setToggleGroup(corrGroup);
        corr7_RadioButton.setUserData(PB7CORR);
        corr7_RadioButton.setSelected(correction.equals(PB7CORR));
        formatNode(corr7_RadioButton, 60);

        RadioButton corr8_RadioButton = new RadioButton("8-corr");
        corr8_RadioButton.setToggleGroup(corrGroup);
        corr8_RadioButton.setUserData(PB8CORR);
        corr8_RadioButton.setSelected(correction.equals(PB8CORR));
        formatNode(corr8_RadioButton, 60);

        // flavors
        Path separator1 = separator();

        Label typeChoiceLabel = new Label("System:");
        formatNode(typeChoiceLabel, 55);

        ToggleGroup typeGroup = new ToggleGroup();

        RadioButton type1RadioButton = new RadioButton();
        type1RadioButton.setToggleGroup(typeGroup);
        formatNode(type1RadioButton, 90);

        RadioButton type2RadioButton = new RadioButton();
        type2RadioButton.setToggleGroup(typeGroup);
        formatNode(type2RadioButton, 90);

        if (correction.equals(PB7CORR)) {
            correction = PB4CORR;
            corr4_RadioButton.setSelected(true);
        }

        type1RadioButton.setText("Wetherill");
        type1RadioButton.setUserData("C");
        type1RadioButton.setSelected(PlotsController.topsoilPlotFlavor.equals("C"));
        formatNode(type1RadioButton, 80);

        type2RadioButton.setText("Tera-Wasserburg");
        type2RadioButton.setUserData("TW");
        type2RadioButton.setSelected(PlotsController.topsoilPlotFlavor.equals("TW"));
        formatNode(type2RadioButton, 120);

        getChildren().addAll(
                corrChoiceLabel, corr4_RadioButton, corr8_RadioButton, separator1, typeChoiceLabel, type1RadioButton, type2RadioButton);

        // add listener after initial choice
        corrGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                correction = ((String) corrGroup.getSelectedToggle().getUserData());
                squidProject.getTask().setChanged(true);
                try {
                    plotsController.showActivePlot();
                } catch (SquidException squidException) {
                }
            }
        });

        // add listener after initial choice
        typeGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                String flavor = ((String) typeGroup.getSelectedToggle().getUserData());
                PlotsController.topsoilPlotFlavor = flavor;
                squidProject.getTask().setChanged(true);
                try {
                    plotsController.showActivePlot();
                } catch (SquidException squidException) {
                }
            }
        });

    }

    private Path separator() {
        Path separator = new Path();
        separator.getElements().add(new MoveTo(2.0f, 0.0f));
        separator.getElements().add(new VLineTo(20.0f));
        separator.setStroke(new Color(251 / 255, 109 / 255, 66 / 255, 1));
        separator.setStrokeWidth(2);

        return separator;
    }

    private void formatNode(Control control, int width) {
        control.setStyle(control.getStyle() + "-font-family: 'Monospaced', 'SansSerif';-fx-font-size: 12px;-fx-font-weight: bold;");
        control.setPrefWidth(width);
        control.setMinWidth(USE_PREF_SIZE);
        control.setPrefHeight(23);
        control.setMinHeight(USE_PREF_SIZE);
    }
}