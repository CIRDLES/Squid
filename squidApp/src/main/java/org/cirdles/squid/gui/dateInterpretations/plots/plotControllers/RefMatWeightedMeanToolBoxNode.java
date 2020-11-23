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
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.scene.paint.Color;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.VLineTo;
import static org.cirdles.squid.constants.Squid3Constants.IndexIsoptopesEnum.PB_204;
import static org.cirdles.squid.constants.Squid3Constants.IndexIsoptopesEnum.PB_207;
import static org.cirdles.squid.constants.Squid3Constants.IndexIsoptopesEnum.PB_208;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.gui.dateInterpretations.plots.plotControllers.PlotsController.correction;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.CALIB_CONST_206_238_ROOT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.CALIB_CONST_208_232_ROOT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8CORR;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.PlotRefreshInterface;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class RefMatWeightedMeanToolBoxNode extends HBox {

    private PlotRefreshInterface plotsController;

    public RefMatWeightedMeanToolBoxNode(PlotRefreshInterface plotsController) {
        super(5);
        this.plotsController = plotsController;

        initNode();
    }

    private void initNode() {
        correction = squidProject.getTask().getSelectedIndexIsotope().getIsotope().substring(2) + "cor_";
        
        Label corrChoiceLabel = new Label("Index Isotope:");
        formatNode(corrChoiceLabel, 85);

        ToggleGroup isotopeGroup = new ToggleGroup();

        RadioButton pb204RadioButton = new RadioButton("204Pb");
        pb204RadioButton.setToggleGroup(isotopeGroup);
        pb204RadioButton.setUserData(PB4CORR);
        pb204RadioButton.setSelected(correction.equals(PB4CORR));
        formatNode(pb204RadioButton, 60);

        RadioButton pb207RadioButton = new RadioButton("207Pb");
        pb207RadioButton.setToggleGroup(isotopeGroup);
        pb207RadioButton.setUserData(PB7CORR);
        pb207RadioButton.setSelected(correction.equals(PB7CORR));
        formatNode(pb207RadioButton, 60);

        RadioButton pb208RadioButton = new RadioButton("208Pb");
        pb208RadioButton.setToggleGroup(isotopeGroup);
        pb208RadioButton.setUserData(PB8CORR);
        pb208RadioButton.setSelected(correction.equals(PB8CORR));
        formatNode(pb208RadioButton, 60);

        // flavors
        Path separator1 = separator();

        Label typeChoiceLabel = new Label("System:");
        formatNode(typeChoiceLabel, 50);

        ToggleGroup typeGroup = new ToggleGroup();

        RadioButton type1RadioButton = new RadioButton();
        type1RadioButton.setToggleGroup(typeGroup);
        formatNode(type1RadioButton, 90);

        RadioButton type2RadioButton = new RadioButton();
        type2RadioButton.setToggleGroup(typeGroup);
        formatNode(type2RadioButton, 90);

        boolean isDirectAltPD = squidProject.getTask().isDirectAltPD();
        boolean has232 = squidProject.getTask().getParentNuclide().contains("232");

        type1RadioButton.setText(CALIB_CONST_206_238_ROOT);
        type2RadioButton.setText(CALIB_CONST_208_232_ROOT);
        type1RadioButton.setUserData(CALIB_CONST_206_238_ROOT);
        type2RadioButton.setUserData(CALIB_CONST_208_232_ROOT);

        pb208RadioButton.setDisable(true);
        if (!isDirectAltPD && !has232) { // perm1
            type1RadioButton.setSelected(true);
            PlotsController.calibrConstAgeBaseName = CALIB_CONST_206_238_ROOT;
            type2RadioButton.setDisable(true);
            pb208RadioButton.setDisable(false);
        } else if (!isDirectAltPD && has232) {// perm3
            type1RadioButton.setDisable(true);
            type2RadioButton.setSelected(true);
            PlotsController.calibrConstAgeBaseName = CALIB_CONST_208_232_ROOT;
        } else {
            type1RadioButton.setDisable(false);
            type1RadioButton.setSelected(PlotsController.calibrConstAgeBaseName.equals(CALIB_CONST_206_238_ROOT));
            type2RadioButton.setDisable(false);
            type2RadioButton.setSelected(PlotsController.calibrConstAgeBaseName.equals(CALIB_CONST_208_232_ROOT));
        }

        if ((correction.equals(PB8CORR)) && (pb208RadioButton.isDisabled())) {
            correction = PB4CORR;
            pb204RadioButton.setSelected(true);
        }

        getChildren().addAll(corrChoiceLabel, pb204RadioButton, pb207RadioButton, pb208RadioButton, separator1, typeChoiceLabel, type1RadioButton, type2RadioButton);

        // add listener after initial choice
        isotopeGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                correction = ((String) isotopeGroup.getSelectedToggle().getUserData());
                switch (correction.substring(0, 1)) {
                    case "4":
                        squidProject.getTask().setSelectedIndexIsotope(PB_204);
                        break;
                    case "7":
                        squidProject.getTask().setSelectedIndexIsotope(PB_207);
                        break;
                    default: // case 8
                        squidProject.getTask().setSelectedIndexIsotope(PB_208);
                }
                squidProject.getTask().setChanged(true);

                plotsController.showActivePlot();
            }
        });

        // add listener after initial choice
        typeGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                String flavor = ((String) typeGroup.getSelectedToggle().getUserData());
                PlotsController.calibrConstAgeBaseName = flavor;

                squidProject.getTask().setChanged(true);

                plotsController.showActivePlot();
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
