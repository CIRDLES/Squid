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
package org.cirdles.squid.gui;

import java.net.URL;
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
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
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

    private final int BUTTON_WIDTH = 50;
    private final int BUTTON_HEIGHT = 32;

    private List<SquidSpeciesModel> squidSpeciesList;
    private int indexOfBackgroundSpecies;

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

        prepareRatioGrid();
    }

    /**
     * Note that JavaFx GridPane uses (col, row) instead of (row, col)
     */
    private void prepareRatioGrid() {

        squidSpeciesList = squidProject.getTask().getSquidSpeciesModelList();

        indexOfBackgroundSpecies = -1;

        ratiosGridPane.getRowConstraints().clear();
        RowConstraints rowCon = new RowConstraints();
        rowCon.setPrefHeight(BUTTON_HEIGHT);
        ratiosGridPane.getRowConstraints().add(rowCon);

        ratiosGridPane.getColumnConstraints().clear();
        ColumnConstraints colcon = new ColumnConstraints(BUTTON_WIDTH);
        colcon.setPrefWidth(BUTTON_WIDTH);
        colcon.setHalignment(HPos.CENTER);
        ratiosGridPane.getColumnConstraints().add(colcon);

        for (int i = 0; i < squidSpeciesList.size(); i++) {
            if (squidSpeciesList.get(i).getIsBackground()) {
                indexOfBackgroundSpecies = squidSpeciesList.get(i).getMassStationIndex();
                squidProject.getTask().setIndexOfBackgroundSpecies(indexOfBackgroundSpecies);

                Text colText = new Text(squidSpeciesList.get(i).getIsotopeName());
                colText.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
                ratiosGridPane.add(colText, 0, i + 1);

                Text rowText = new Text(squidSpeciesList.get(i).getIsotopeName());
                rowText.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
                ratiosGridPane.add(rowText, i + 1, 0);
            } else {
                Button colButton = new SquidRowColButton(i, -1, squidSpeciesList.get(i).getIsotopeName());
                ratiosGridPane.add(colButton, 0, i + 1);

                Button rowButton = new SquidRowColButton(-1, i, squidSpeciesList.get(i).getIsotopeName());
                ratiosGridPane.add(rowButton, i + 1, 0);
            }

            Label corLabel = new Label("ROW /\n COL");
            corLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 2));
            corLabel.setStyle(
                    "    -fx-font-family: \"Courier New\", \"Lucida Sans\", \"Segoe UI\", Helvetica, Arial, sans-serif;\n"
                    + "    -fx-font-weight: bold;\n"
                    + "    -fx-font-size: 7pt;\n"
            );
            corLabel.setWrapText(true);

            ratiosGridPane.add(corLabel, 0, 0);

            ratiosGridPane.getRowConstraints().add(rowCon);
            ratiosGridPane.getColumnConstraints().add(colcon);
        }

        populateRatioGrid();

        // center in window
        double width = primaryStageWindow.getScene().getWidth();
        ratiosGridPane.setLayoutX((width - (squidSpeciesList.size() + 1) * BUTTON_WIDTH) / 2.0);
        ratiosGridPane.setLayoutY(15);

    }

    private void populateRatioGrid() {
        for (int i = 0; i < squidSpeciesList.size(); i++) {
            for (int j = 0; j < squidSpeciesList.size(); j++) {
                if ((i != j) && (i != indexOfBackgroundSpecies) && (j != indexOfBackgroundSpecies)) {
                    Button ratioButton = new SquidRatioButton(
                            i, j,
                            squidSpeciesList.get(i).getIsotopeName() + "/\n " + squidSpeciesList.get(j).getIsotopeName(),
                            SquidUIController.squidProject.getTask().getTableOfSelectedRatiosByMassStationIndex()[i][j]);
                    ratiosGridPane.add(ratioButton, j + 1, i + 1);
                }
            }
        }
    }

    class SquidRatioButton extends Button {

        public SquidRatioButton(int row, int col, String ratioName, boolean selected) {
            super(selected ? ratioName : "");

            Tooltip ratioToolTip = new Tooltip(ratioName);
            setTooltip(ratioToolTip);

            setOnAction(new SquidRatioButtonEventHandler(row, col, ratioName, selected));

            setPrefWidth(BUTTON_WIDTH - 2);
            setPrefHeight(BUTTON_HEIGHT - 2);
            setStyle("-fx-padding: 0 0 0 0;   \n"
                    + "    -fx-border-width: 1;\n"
                    + "    -fx-border-color: black;\n"
                    + "    -fx-background-radius: 0;\n"
                    + "    -fx-background-color: #00BFFF;\n"
                    + "    -fx-font-family: \"Courier New\", \"Lucida Sans\", \"Segoe UI\", Helvetica, Arial, sans-serif;\n"
                    + "    -fx-font-weight: bold;\n"
                    + "    -fx-font-size: 8pt;\n"
                    + "    -fx-text-fill: White;/*  #d8d8d8;*/\n"
            );
            setWrapText(true);
        }
    }

    class SquidRowColButton extends Button {

        private Tooltip ratioToolTip;

        public SquidRowColButton(int row, int col, String ratioName) {
            super(ratioName);

            ratioToolTip = new Tooltip("Click to select entire " + (String) (row == -1 ? "column" : "row"));
            setTooltip(ratioToolTip);

            setOnAction(new SquidRowColButtonEventHandler(row, col));

            setPrefWidth(BUTTON_WIDTH - 2);
            setPrefHeight(BUTTON_HEIGHT - 2);
            setStyle("-fx-padding: 0 0 0 0;   \n"
                    + "    -fx-border-width: 1;\n"
                    + "    -fx-border-color: black;\n"
                    + "    -fx-background-radius: 0;\n"
                    + "    -fx-background-color: #ffa06d;\n"
                    + "    -fx-font-family: \"Courier New\", \"Lucida Sans\", \"Segoe UI\", Helvetica, Arial, sans-serif;\n"
                    + "    -fx-font-weight: bold;\n"
                    + "    -fx-font-size: 9pt;\n"
                    + "    -fx-text-fill: Black;/*  #d8d8d8;*/\n"
            );
            setWrapText(true);
        }

        public void setToolTipText(String tip) {
            ratioToolTip.setText(tip);
        }
    }

    class SquidRatioButtonEventHandler implements EventHandler<ActionEvent> {

        private final int row;
        private final int col;
        private final String ratioName;
        private boolean selected;

        public SquidRatioButtonEventHandler(int row, int col, String ratioName, boolean selected) {
            this.row = row;
            this.col = col;
            this.ratioName = ratioName;
            this.selected = selected;
        }

        @Override
        public void handle(ActionEvent event) {
            selected = !selected;
            ((Button) event.getSource()).setText(selected ? ratioName : "");

            squidProject.getTask().updateTableOfSelectedRatiosByMassStationIndex(row, col, selected);
        }
    }

    class SquidRowColButtonEventHandler implements EventHandler<ActionEvent> {

        private final int row;
        private final int col;
        private boolean selected;

        public SquidRowColButtonEventHandler(int row, int col) {
            this.row = row;
            this.col = col;
            this.selected = false;
        }

        @Override
        public void handle(ActionEvent event) {
            selected = !selected;
            ((SquidRowColButton) event.getSource()).setToolTipText("Click to " + (selected ? "de-" : "") + "select entire " + (String) (row == -1 ? "column" : "row"));
            squidProject.getTask().updateTableOfSelectedRatiosByRowOrCol(row, col, selected);
            populateRatioGrid();
        }
    }
}
