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
import org.cirdles.squid.constants.Squid3Constants.SpotTypes;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.gui.dateInterpretations.plots.plotControllers.PlotsController.correction;
import static org.cirdles.squid.gui.dateInterpretations.plots.plotControllers.PlotsController.fractionTypeSelected;
import static org.cirdles.squid.gui.dateInterpretations.plots.plotControllers.PlotsController.plotTypeSelected;
import static org.cirdles.squid.gui.dateInterpretations.plots.plotControllers.PlotsController.selectedIndexIsotope;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.WeightedMeanRefreshInterface;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.CALIB_CONST_206_238_ROOT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.CALIB_CONST_208_232_ROOT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8CORR;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class CorrectionsControlToolBoxNode extends HBox {

    private WeightedMeanRefreshInterface plotsController;

    public CorrectionsControlToolBoxNode(WeightedMeanRefreshInterface plotsController) {
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
        corr4_RadioButton.setSelected(correction == PB4CORR);
        formatNode(corr4_RadioButton, 60);

        RadioButton corr7_RadioButton = new RadioButton("7-corr");
        corr7_RadioButton.setToggleGroup(corrGroup);
        corr7_RadioButton.setUserData(PB7CORR);
        corr7_RadioButton.setSelected(correction == PB7CORR);
        formatNode(corr7_RadioButton, 60);

        RadioButton corr8_RadioButton = new RadioButton("8-corr");
        corr8_RadioButton.setToggleGroup(corrGroup);
        corr8_RadioButton.setUserData(PB8CORR);
        corr8_RadioButton.setSelected(correction == PB8CORR);
        formatNode(corr8_RadioButton, 60);

        // flavors
        Path separator1 = separator();

        Label typeChoiceLabel = new Label("Type:");
        formatNode(typeChoiceLabel, 35);

        ToggleGroup typeGroup = new ToggleGroup();

        RadioButton type1RadioButton = new RadioButton();
        type1RadioButton.setToggleGroup(typeGroup);
        formatNode(type1RadioButton, 90);

        RadioButton type2RadioButton = new RadioButton();
        type2RadioButton.setToggleGroup(typeGroup);
        formatNode(type2RadioButton, 90);

        boolean isDirectAltPD = squidProject.getTask().isDirectAltPD();
        boolean has232 = squidProject.getTask().getParentNuclide().contains("232");
        // weighted mean
        if (PlotsController.plotTypeSelected.name().startsWith("WEIGHT")) {
            type1RadioButton.setText(CALIB_CONST_206_238_ROOT);
            type2RadioButton.setText(CALIB_CONST_208_232_ROOT);
            type1RadioButton.setUserData(CALIB_CONST_206_238_ROOT);
            type2RadioButton.setUserData(CALIB_CONST_208_232_ROOT);

            corr8_RadioButton.setDisable(true);
//            PlotsController.calibrConstAgeBaseName = CALIB_CONST_206_238_ROOT;
            if (!isDirectAltPD && !has232) { // perm1
                type1RadioButton.setSelected(true);
                PlotsController.calibrConstAgeBaseName = CALIB_CONST_206_238_ROOT;
                type2RadioButton.setDisable(true);
                corr8_RadioButton.setDisable(false);
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

            if ((correction.equals(PB8CORR)) && (corr8_RadioButton.isDisabled())) {
                correction = PB4CORR;
                corr4_RadioButton.setSelected(true);
            }

            getChildren().addAll(
                    corrChoiceLabel, corr4_RadioButton, corr7_RadioButton, corr8_RadioButton, separator1, typeChoiceLabel, type1RadioButton, type2RadioButton);
        } else {
            // concordia
            if (correction.equals(PB7CORR)) {
                correction = PB4CORR;
                corr4_RadioButton.setSelected(true);
            }

            if (fractionTypeSelected.compareTo(SpotTypes.REFERENCE_MATERIAL) == 0) {
                PlotsController.concordiaFlavor = "C";
            }

            type1RadioButton.setText("Wetherill");
            type1RadioButton.setUserData("C");
            type1RadioButton.setSelected(PlotsController.concordiaFlavor.equals("C"));
            formatNode(type1RadioButton, 80);

            type2RadioButton.setText("Tera-Wasserburg");
            type2RadioButton.setUserData("TW");
            type2RadioButton.setSelected(PlotsController.concordiaFlavor.equals("TW"));
            formatNode(type2RadioButton, 120);

            if (fractionTypeSelected.compareTo(SpotTypes.REFERENCE_MATERIAL) == 0) {
                // ref materials
                corr8_RadioButton.setDisable(isDirectAltPD || has232);
                if (isDirectAltPD || has232) {
                    correction = PB4CORR;
                }
                //corr4_RadioButton.setDisable(!isDirectAltPD && has232);
                corr4_RadioButton.setSelected(isDirectAltPD || has232 || corr4_RadioButton.isSelected());
                getChildren().addAll(
                        corrChoiceLabel, corr4_RadioButton, corr8_RadioButton);
            } else {
                // unknowns
                getChildren().addAll(
                        corrChoiceLabel, corr4_RadioButton, corr8_RadioButton, separator1, typeChoiceLabel, type1RadioButton, type2RadioButton);
            }
        }

        // add listener after initial choice
        corrGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                correction = ((String) corrGroup.getSelectedToggle().getUserData());
                switch (correction.substring(0, 1)) {
                    case "4":
                        squidProject.getTask().setSelectedIndexIsotope(PB_204);
                        selectedIndexIsotope = PB_204;
                        break;
                    case "7":
                        squidProject.getTask().setSelectedIndexIsotope(PB_207);
                        selectedIndexIsotope = PB_207;
                        break;
                    default: // case 8
                        squidProject.getTask().setSelectedIndexIsotope(PB_208);
                        selectedIndexIsotope = PB_208;
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
                switch (plotTypeSelected) {
                    case CONCORDIA:
                    case TERA_WASSERBURG:
                        PlotsController.concordiaFlavor = flavor;
                        break;
                    case WEIGHTED_MEAN:
                    case WEIGHTED_MEAN_SAMPLE:
                        PlotsController.calibrConstAgeBaseName = flavor;
                }

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
        control.setStyle(control.getStyle() + "-font-family: San Serif;-fx-font-size: 12px;-fx-font-weight: bold;");
        control.setPrefWidth(width);
        control.setMinWidth(USE_PREF_SIZE);
        control.setPrefHeight(23);
        control.setMinHeight(USE_PREF_SIZE);
    }
}
