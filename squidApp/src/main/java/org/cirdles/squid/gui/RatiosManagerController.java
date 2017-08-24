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
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import org.cirdles.squid.projects.SquidProject;
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

    private final int BUTTON_WIDTH = 70;
    private final int BUTTON_HEIGHT = 30;

    private List<SquidRatiosModel> squidRatiosModelList;
    private List<SquidSpeciesModel> squidSpeciesList;
    private int indexOfBackgroundSpecies;

    @FXML
    private ToolBar toolbar;
    @FXML
    private VBox manageRatiosVBox;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        manageRatiosVBox.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        manageRatiosVBox.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(PIXEL_OFFSET_FOR_MENU));
        manageRatiosAnchorPane.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        manageRatiosAnchorPane.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(PIXEL_OFFSET_FOR_MENU - 40));

        squidRatiosModelList = new ArrayList<>();
        prepareRatioGrid();
    }

    private void prepareRatioGrid() {

        squidSpeciesList = SquidUIController.squidProject.getSquidSpeciesModelList();

        indexOfBackgroundSpecies = -1;

        for (int i = 0; i < squidSpeciesList.size(); i++) {
            ratiosGridPane.add(new Label(squidSpeciesList.get(i).getIsotopeName()), 0, i + 1);
            ratiosGridPane.add(new Label(squidSpeciesList.get(i).getIsotopeName()), i + 1, 0);

            if (squidSpeciesList.get(i).getIsBackground()) {
                indexOfBackgroundSpecies = squidSpeciesList.get(i).getMassStationIndex();
            }

            RowConstraints con = new RowConstraints();
            con.setPrefHeight(BUTTON_HEIGHT);
            ratiosGridPane.getRowConstraints().add(con);

            ColumnConstraints colcon = new ColumnConstraints();
            colcon.setPrefWidth(BUTTON_WIDTH);
            colcon.setHalignment(HPos.CENTER);
            ratiosGridPane.getColumnConstraints().add(colcon);
        }

        populateRatioGrid();

        ratiosGridPane.setLayoutX(25);

    }

    private void populateRatioGrid() {
        for (int i = 0; i < squidSpeciesList.size(); i++) {
            for (int j = 0; j < squidSpeciesList.size(); j++) {
                if ((i != j) && (i != indexOfBackgroundSpecies) && (j != indexOfBackgroundSpecies)) {
                    Button ratioButton = new SquidRatioButton(
                            i, j,
                            squidSpeciesList.get(i).getIsotopeName() + "/" + squidSpeciesList.get(j).getIsotopeName(),
                            SquidUIController.squidProject.getTableOfSelectedRatiosByMassStationIndex()[i][j]);

                    ratioButton.setPrefWidth(BUTTON_WIDTH - 2);
                    ratioButton.setPrefHeight(BUTTON_HEIGHT - 2);
                    ratioButton.setStyle("-fx-padding: 0 0 0 0;   \n"
                            + "    -fx-border-width: 1;\n"
                            + "    -fx-border-color: black;\n"
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

    }

    @FXML
    private void useTaskRatiosButtonAction(ActionEvent event) {
        SquidUIController.squidProject.extractTask25Ratios();
        populateRatioGrid();
    }

    @FXML
    private void clearRatiosButtonAction(ActionEvent event) {
        SquidUIController.squidProject.resetTableOfSelectedRatiosByMassStationIndex();
        populateRatioGrid();
    }

    class SquidRatioButton extends Button {

        public SquidRatioButton(int row, int col, String ratioName, boolean selected) {
            super(selected ? ratioName : "");

            setHeight(25);

            setOnAction(new SquidButtonEventHandler(row, col, ratioName, selected));
        }
    }

    class SquidButtonEventHandler implements EventHandler {

        private int row;
        private int col;
        private String ratioName;
        private boolean selected;

        public SquidButtonEventHandler(int row, int col, String ratioName, boolean selected) {
            this.row = row;
            this.col = col;
            this.ratioName = ratioName;
            this.selected = selected;
        }

        @Override
        public void handle(Event event) {
            selected = !selected;
            ((Button) event.getSource()).setText(selected ? ratioName : "");

            SquidUIController.squidProject.getTableOfSelectedRatiosByMassStationIndex()[row][col] = selected;

        }
    }

}
