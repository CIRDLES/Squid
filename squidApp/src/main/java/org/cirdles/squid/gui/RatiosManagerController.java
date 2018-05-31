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
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.shrimp.SquidSpeciesModel;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class RatiosManagerController implements Initializable {

    private static final String STYLE_RATIO = "-fx-padding: 0 0 0 0;   \n"
            + "    -fx-border-width: 1;\n"
            + "    -fx-border-color: black;\n"
            + "    -fx-background-radius: 0;\n"
            + "    -fx-font-family: \"Courier New\", \"Lucida Sans\", \"Segoe UI\", Helvetica, Arial, sans-serif;\n"
            + "    -fx-font-weight: bold;\n"
            + "    -fx-font-size: 8pt;\n"
            + "    -fx-text-fill: White;/*  #d8d8d8;*/\n";

    private static final String STYLE_RATIO_SELECTED = STYLE_RATIO
            + "    -fx-background-color: #3c77c9;\n";

    private static final String STYLE_RATIO_UNSELECTED = STYLE_RATIO
            + "    -fx-background-color: #00BFFF;\n";

    private static final String STYLE_RATIO_HEADER = "-fx-padding: 0 0 0 0;   \n"
            + "    -fx-border-width: 1;\n"
            + "    -fx-border-color: black;\n"
            + "    -fx-background-radius: 0;\n"
            + "    -fx-background-color: #ffa06d;\n"
            + "    -fx-font-family: \"Courier New\", \"Lucida Sans\", \"Segoe UI\", Helvetica, Arial, sans-serif;\n"
            + "    -fx-font-weight: bold;\n"
            + "    -fx-font-size: 9pt;\n"
            + "    -fx-text-fill: Black;/*  #d8d8d8;*/\n";

    private static final String STYLE_RATIO_LABEL = "-fx-font-family: \"Courier New\", \"Lucida Sans\", \"Segoe UI\", Helvetica, Arial, sans-serif;\n"
            + "    -fx-font-weight: bold;\n"
            + "    -fx-font-size: 7pt;\n";

    @FXML
    private GridPane ratiosGridPane;

    private final int BUTTON_WIDTH = 40;
    private final int BUTTON_HEIGHT = 30;

    private List<SquidSpeciesModel> squidSpeciesList;
    private int indexOfBackgroundSpecies;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        rowCon.setPrefHeight(BUTTON_HEIGHT + 2);
        rowCon.setMinHeight(BUTTON_HEIGHT + 2);
        rowCon.setMaxHeight(BUTTON_HEIGHT + 2);
        rowCon.setValignment(VPos.CENTER);
        ratiosGridPane.getRowConstraints().add(rowCon);

        ratiosGridPane.getColumnConstraints().clear();
        ColumnConstraints colcon = new ColumnConstraints();
        colcon.setPrefWidth(BUTTON_WIDTH + 2);
        colcon.setMinWidth(BUTTON_WIDTH + 2);
        colcon.setMaxWidth(BUTTON_WIDTH + 2);
        colcon.setHalignment(HPos.CENTER);
        ratiosGridPane.getColumnConstraints().add(colcon);

        for (int i = 0; i < squidSpeciesList.size(); i++) {
            if (squidSpeciesList.get(i).getIsBackground()) {
                indexOfBackgroundSpecies = squidSpeciesList.get(i).getMassStationIndex();
                squidProject.getTask().setIndexOfBackgroundSpecies(indexOfBackgroundSpecies);

                Label rowLabel = new SquidLabel(0, i + 1, squidSpeciesList.get(i).getIsotopeName());
                Label colLabel = new SquidLabel(0, i + 1, squidSpeciesList.get(i).getIsotopeName());
                ratiosGridPane.add(rowLabel, 0, i + 1);
                ratiosGridPane.add(colLabel, i + 1, 0);
            } else {
                Button colButton = new SquidRowColButton(i, -1, squidSpeciesList.get(i).getIsotopeName());
                ratiosGridPane.add(colButton, 0, i + 1);

                Button rowButton = new SquidRowColButton(-1, i, squidSpeciesList.get(i).getIsotopeName());
                ratiosGridPane.add(rowButton, i + 1, 0);
            }

            Label corLabel = new SquidLabel(0, 0, "ROW/\nCOL");
            ratiosGridPane.add(corLabel, 0, 0);

            ratiosGridPane.getRowConstraints().add(rowCon);
            ratiosGridPane.getColumnConstraints().add(colcon);
        }

        populateRatioGrid();
    }

    private void populateRatioGrid() {
        for (int i = 0; i < squidSpeciesList.size(); i++) {
            for (int j = 0; j < squidSpeciesList.size(); j++) {
                if ((i != j) && (i != indexOfBackgroundSpecies) && (j != indexOfBackgroundSpecies)) {
                    Button ratioButton = new SquidRatioButton(
                            i, j,
                            squidSpeciesList.get(i).getIsotopeName() + "/\n" + squidSpeciesList.get(j).getIsotopeName(),
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

            setOnMousePressed(new SquidRatioButtonEventHandler(row, col, ratioName, selected, this));

            setPrefWidth(BUTTON_WIDTH);
            setPrefHeight(BUTTON_HEIGHT);
            setMinWidth(BUTTON_WIDTH);
            setMinHeight(BUTTON_HEIGHT);

            if (selected) {
                setStyle(STYLE_RATIO_SELECTED);
            } else {
                setStyle(STYLE_RATIO_UNSELECTED);
            }

            setWrapText(true);
        }
    }

    class SquidRowColButton extends Button {

        private final Tooltip ratioToolTip;

        public SquidRowColButton(int row, int col, String ratioName) {
            super(ratioName);

            ratioToolTip = new Tooltip("Click to select entire " + (String) (row == -1 ? "column" : "row"));
            setTooltip(ratioToolTip);

            setOnMousePressed(new SquidRowColButtonEventHandler(row, col));

            setPrefWidth(BUTTON_WIDTH);
            setPrefHeight(BUTTON_HEIGHT);
            setMinWidth(BUTTON_WIDTH);
            setMinHeight(BUTTON_HEIGHT);

            setStyle(STYLE_RATIO_HEADER);

            setWrapText(true);
        }

        public void setToolTipText(String tip) {
            ratioToolTip.setText(tip);
        }
    }

    class SquidLabel extends Label {

        public SquidLabel(int row, int col, String text) {
            super(text);

            setStyle(STYLE_RATIO_LABEL);
        }

    }

    class SquidRatioButtonEventHandler implements EventHandler<Event> {

        private final int row;
        private final int col;
        private final String ratioName;
        private boolean selected;
        private final Button btn;

        public SquidRatioButtonEventHandler(int row, int col, String ratioName, boolean selected, Button btn) {
            this.row = row;
            this.col = col;
            this.ratioName = ratioName;
            this.selected = selected;
            this.btn = btn;
        }

        @Override
        public void handle(Event event) {
            selected = !selected;
            ((Button) event.getSource()).setText(selected ? ratioName : "");

            if (selected) {
                btn.setStyle(STYLE_RATIO_SELECTED);
            } else {
                btn.setStyle(STYLE_RATIO_UNSELECTED);
            }

            squidProject.getTask().updateTableOfSelectedRatiosByMassStationIndex(row, col, selected);
        }
    }

    class SquidRowColButtonEventHandler implements EventHandler<Event> {

        private final int row;
        private final int col;
        private boolean selected;

        public SquidRowColButtonEventHandler(int row, int col) {
            this.row = row;
            this.col = col;
            this.selected = false;
        }

        @Override
        public void handle(Event event) {
            selected = !selected;
            ((SquidRowColButton) event.getSource()).setToolTipText("Click to " + (selected ? "de-" : "") + "select entire " + (String) (row == -1 ? "column" : "row"));
            squidProject.getTask().updateTableOfSelectedRatiosByRowOrCol(row, col, selected);
            populateRatioGrid();
        }
    }
}
