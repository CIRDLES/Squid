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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.VLineTo;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.PlotRefreshInterface;

import static org.cirdles.squid.constants.Squid3Constants.IndexIsoptopesEnum.*;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.gui.dateInterpretations.plots.plotControllers.PlotsController.correction;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8CORR;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class RefMatConcordiaToolBoxNode extends HBox {

    private PlotRefreshInterface plotsController;

    public RefMatConcordiaToolBoxNode(PlotRefreshInterface plotsController) {
        super(5);
        this.plotsController = plotsController;

        initNode();
    }

    private void initNode() {
        correction = squidProject.getTask().getSelectedIndexIsotope().getIsotope().substring(2) + "cor_";

        Button saveToNewFileButton = new Button("Synch Selected Spots with Weighted Mean");
        formatNode(saveToNewFileButton, 250);
        saveToNewFileButton.setStyle("-fx-font-size: 12px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
        saveToNewFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                PlotsController.doSynchIncludedSpotsBetweenConcordiaAndWM = true;
                try{plotsController.showActivePlot();}catch(SquidException squidException){}
                PlotsController.doSynchIncludedSpotsBetweenConcordiaAndWM = false;
            }
        });

        Label isotopeChoiceLabel = new Label("Index Isotope:");
        formatNode(isotopeChoiceLabel, 90);

        ToggleGroup isotopeGroup = new ToggleGroup();

        RadioButton pb204RadioButton = new RadioButton("204Pb");
        pb204RadioButton.setToggleGroup(isotopeGroup);
        pb204RadioButton.setUserData(PB4CORR);
        pb204RadioButton.setSelected(correction.equals(PB4CORR));
        formatNode(pb204RadioButton, 60);

//        RadioButton pb207RadioButton = new RadioButton("207Pb");
//        pb207RadioButton.setToggleGroup(isotopeGroup);
//        pb207RadioButton.setUserData(PB7CORR);
//        pb207RadioButton.setSelected(correction.equals(PB7CORR));
//        pb207RadioButton.setDisable(true);
//        formatNode(pb207RadioButton, 60);

        RadioButton pb208RadioButton = new RadioButton("208Pb");
        pb208RadioButton.setToggleGroup(isotopeGroup);
        pb208RadioButton.setUserData(PB8CORR);
        pb208RadioButton.setSelected(correction.equals(PB8CORR));
        formatNode(pb208RadioButton, 60);

        boolean isDirectAltPD = squidProject.getTask().isDirectAltPD();
        boolean has232 = squidProject.getTask().getParentNuclide().contains("232");

        // concordia for ref mat
        PlotsController.topsoilPlotFlavor = "C";

        // ref materials
        pb208RadioButton.setDisable(isDirectAltPD || has232);

        pb204RadioButton.setSelected(isDirectAltPD || has232 || pb204RadioButton.isSelected());

        getChildren().addAll(
                saveToNewFileButton, separator(), isotopeChoiceLabel, pb204RadioButton /*, pb207RadioButton*/, pb208RadioButton);

        // add listener after initial choice
        isotopeGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                correction = ((String) isotopeGroup.getSelectedToggle().getUserData());
                switch (correction.substring(0, 1)) {
                    case "4":
                        squidProject.setSelectedIndexIsotope(PB_204);
                        break;
                    case "7":
                        squidProject.setSelectedIndexIsotope(PB_207);
                        break;
                    default: // case 8
                        squidProject.setSelectedIndexIsotope(PB_208);
                }

                squidProject.getTask().setChanged(true);
                try{plotsController.showActivePlot();}catch(SquidException squidException){}
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