/*
 * Copyright 2017 CIRDLES.org.
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
package org.cirdles.squid.gui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.shrimp.SquidSpeciesModel;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class RatiosManagerController implements Initializable {

    @FXML
    private AnchorPane manageRatiosAnchorPane;
    @FXML
    private GridPane ratiosGridPane;

    private List<SquidRatiosModel> squidRatiosModelList;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        manageRatiosAnchorPane.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        manageRatiosAnchorPane.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(30));

        squidRatiosModelList = new ArrayList<>();
        prepareRatioGrid();
    }

    private void prepareRatioGrid() {
        List<SquidSpeciesModel> squidSpeciesList = SquidUIController.squidProject.getSquidSpeciesModelList();

        int indexOfBackgroundSpeicies = -1;

        int rows = 0;
        try {
            Method method = ratiosGridPane.getClass().getDeclaredMethod("getNumberOfRows");
            method.setAccessible(true);
            rows = (Integer) method.invoke(ratiosGridPane);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException noSuchMethodException) {
        }

        for (int i = 0; i < squidSpeciesList.size(); i++) {
            ratiosGridPane.add(new Label(squidSpeciesList.get(i).getIsotopeName()), 0, i + 1);
            ratiosGridPane.add(new Label(squidSpeciesList.get(i).getIsotopeName()), i + 1, 0);

            if (squidSpeciesList.get(i).getIsBackground()) {
                indexOfBackgroundSpeicies = squidSpeciesList.get(i).getMassStationIndex();
            }

            RowConstraints con = new RowConstraints();
            con.setPrefHeight(25);
            ratiosGridPane.getRowConstraints().add(con);

            ColumnConstraints colcon = new ColumnConstraints();
            colcon.setPrefWidth(70);
            colcon.setHalignment(HPos.CENTER);
            ratiosGridPane.getColumnConstraints().add(colcon);

        }

        for (int i = 0; i < squidSpeciesList.size(); i++) {
            for (int j = 0; j < squidSpeciesList.size(); j++) {
                if ((i != j) && (i != indexOfBackgroundSpeicies) && (j != indexOfBackgroundSpeicies)) {
                    Button ratioButton = new SquidRatioButton(squidSpeciesList.get(i), squidSpeciesList.get(j), false);
                    ratioButton.setPrefWidth(70);
                    ratioButton.setStyle("-fx-padding: 0 0 0 0;   \n"
                            + "    -fx-border-width: 1;\n"
                            + "    -fx-border-color: #e2e2e2;\n"
                            + "    -fx-background-radius: 0;\n"
                            + "    -fx-background-color: #43ABC9;/*#72b1bf;/*#43ABC9;/*#ff8f89; /*#ff6961; /*#1d1d1d;*/\n"
                            + "    -fx-font-family: \"Lucida Sans Bold\", \"Segoe UI\", Helvetica, Arial, sans-serif;\n"
                            + "    -fx-font-weight: bold;\n"
                            + "    -fx-font-size: 10pt;\n"
                            + "    -fx-text-fill: whitesmoke;/*  #d8d8d8;*/\n"
                    );

                    ratiosGridPane.add(ratioButton, j + 1, i + 1);
                }
            }
        }

        ratiosGridPane.setLayoutX(25);

    }

    class SquidRatioButton extends Button {

        private SquidRatiosModel ratioModel;

        public SquidRatioButton(SquidSpeciesModel numerator, SquidSpeciesModel denominator, boolean selected) {
            super();
            
            ratioModel = new SquidRatiosModel(numerator, denominator, 0);
            if (selected) {
                squidRatiosModelList.add(ratioModel);
            }

            setText(selected ? ratioModel.getRatioName() : "");

            setOnAction(new SquidButtonEventHandler(ratioModel, selected));
        }
    }

    class SquidButtonEventHandler implements EventHandler {

        private SquidRatiosModel ratioModel;
        private boolean selected;

        public SquidButtonEventHandler(SquidRatiosModel ratioModel, boolean selected) {
            this.ratioModel = ratioModel;
            this.selected = selected;
        }

        @Override
        public void handle(Event event) {
            selected = !selected;
            ((Button) event.getSource()).setText(selected ? ratioModel.getRatioName() : "");
            if (selected){
                squidRatiosModelList.add(ratioModel);
            } else {
                squidRatiosModelList.remove(ratioModel);
            }
        }
    }

}
