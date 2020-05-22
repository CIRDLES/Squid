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
import static org.cirdles.squid.constants.Squid3Constants.IndexIsoptopesEnum.PB_204;
import static org.cirdles.squid.constants.Squid3Constants.IndexIsoptopesEnum.PB_207;
import static org.cirdles.squid.constants.Squid3Constants.IndexIsoptopesEnum.PB_208;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.gui.dateInterpretations.plots.plotControllers.PlotsController.correction;
import static org.cirdles.squid.gui.dateInterpretations.plots.plotControllers.PlotsController.selectedIndexIsotope;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.WeightedMeanRefreshInterface;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8CORR;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class CorrectionsControlToolBoxNode extends HBox{

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
        if (PlotsController.plotTypeSelected.name().startsWith("WEIGHT")) {
            getChildren().addAll(corrChoiceLabel, corr4_RadioButton, corr7_RadioButton, corr8_RadioButton);
        } else {
            getChildren().addAll(corrChoiceLabel, corr4_RadioButton, corr8_RadioButton);
            if (correction == PB7CORR){
                correction = PB4CORR;
            }
            corr4_RadioButton.setSelected(correction == PB4CORR);
        }

    }

    private void formatNode(Control control, int width) {
        control.setStyle(control.getStyle() + "-font-family: San Serif;-fx-font-size: 12px;-fx-font-weight: bold;");
        control.setPrefWidth(width);
        control.setMinWidth(USE_PREF_SIZE);
        control.setPrefHeight(23);
        control.setMinHeight(USE_PREF_SIZE);
    }
}
