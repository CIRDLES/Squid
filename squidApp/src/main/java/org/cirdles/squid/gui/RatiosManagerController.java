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
import java.util.ArrayList;
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

    private static final String STYLE_RATIO
            = "-fx-padding: 0 0 0 0;   \n"
            + "    -fx-border-width: 1;\n"
            + "    -fx-border-color: black;\n"
            + "    -fx-background-radius: 0;\n"
            + "    -fx-font-family: 'Monospaced', 'SansSerif';\n"
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
            + "    -fx-font-family: 'Monospaced', 'SansSerif';\n"
            + "    -fx-font-weight: bold;\n"
            + "    -fx-font-size: 9pt;\n"
            + "    -fx-text-fill: Black;/*  #d8d8d8;*/\n";

    private static final String STYLE_RATIO_LABEL = "-fx-font-family: 'SansSerif';\n"
            + "    -fx-font-weight: bold;\n"
            + "    -fx-font-size: 7pt;\n";

    @FXML
    private GridPane ratiosGridPane;

    private final int BUTTON_WIDTH = 45;
    private final int BUTTON_HEIGHT = 30;

    private List<SquidSpeciesModel> squidSpeciesList;
    private int indexOfBackgroundSpecies;

    List<Button> rowButtons = new ArrayList<>();
    List<Button> colButtons = new ArrayList<>();

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

        int rowCounter = 0;
        int colCounter = 0;
        for (int i = 0; i < squidSpeciesList.size(); i++) {
            if (squidSpeciesList.get(i).getIsBackground()) {
//                indexOfBackgroundSpecies = squidSpeciesList.get(i).getMassStationIndex();
//                squidProject.getTask().setIndexOfBackgroundSpecies(indexOfBackgroundSpecies);

                if (squidSpeciesList.get(i).isNumeratorRole()) {
                    Label rowBackgroundLabel = new SquidLabel(rowCounter + 1, 0, squidSpeciesList.get(i).getIsotopeName());
                    ratiosGridPane.add(rowBackgroundLabel, 0, rowCounter + 1);
                    rowButtons.add(null);
                    rowCounter++;
                }

                if (squidSpeciesList.get(i).isDenominatorRole()) {
                    Label colBackgroundLabel = new SquidLabel(0, colCounter + 1, squidSpeciesList.get(i).getIsotopeName());
                    ratiosGridPane.add(colBackgroundLabel, colCounter + 1, 0);
                    colButtons.add(null);
                    colCounter++;
                }

            } else {
                if (squidSpeciesList.get(i).isNumeratorRole()) {
                    Button rowNumeratorButton = new SquidRowColButton(i, -1, squidSpeciesList.get(i).getIsotopeName());
                    ratiosGridPane.add(rowNumeratorButton, 0, rowCounter + 1);
                    rowButtons.add(rowNumeratorButton);
                    rowCounter++;
                }

                if (squidSpeciesList.get(i).isDenominatorRole()) {
                    Button colDenominatorButton = new SquidRowColButton(-1, i, squidSpeciesList.get(i).getIsotopeName());
                    ratiosGridPane.add(colDenominatorButton, colCounter + 1, 0);
                    colButtons.add(colDenominatorButton);
                    colCounter++;
                }
            }

            Label cornerLabel = new SquidLabel(0, 0, "Num/\nDen");
            ratiosGridPane.add(cornerLabel, 0, 0);
        }

        populateRatioGrid();
    }

    private void populateRatioGrid() {
        int rowCounter = 0;
        for (int i = 0; i < squidSpeciesList.size(); i++) {
            if ((i != indexOfBackgroundSpecies)
                    && squidSpeciesList.get(i).isNumeratorRole()) {
                int colCounter = 0;
                for (int j = 0; j < squidSpeciesList.size(); j++) {
                    if ((i != j) && (j != indexOfBackgroundSpecies)
                            && squidSpeciesList.get(j).isDenominatorRole()) {
                        boolean selected = SquidUIController.squidProject.getTask().getTableOfSelectedRatiosByMassStationIndex()[i][j];
                        Button ratioButton = new SquidRatioButton(
                                i, j,
                                squidSpeciesList.get(i).getIsotopeName() + "/\n" + squidSpeciesList.get(j).getIsotopeName(),
                                selected);
                        ratiosGridPane.add(ratioButton, colCounter + 1, rowCounter + 1);
                        if (!selected) {
                            ((SquidRowColButton)rowButtons.get(rowCounter)).setSelected(false);
                            ((SquidRowColButton)colButtons.get(colCounter)).setSelected(false);
                        }
                    }
                    colCounter = colCounter + (squidSpeciesList.get(j).isDenominatorRole() ? 1 : 0);
                }
            }
            rowCounter = rowCounter + (squidSpeciesList.get(i).isNumeratorRole() ? 1 : 0);
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
        private boolean selected;

        public SquidRowColButton(int row, int col, String ratioName) {
            super(ratioName);

            ratioToolTip = new Tooltip("Click to select entire " + (String) (row == -1 ? "column" : "row"));
            setTooltip(ratioToolTip);
            
            selected = false;

            setOnMousePressed(new SquidRowColButtonEventHandler(row, col, selected));

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

        /**
         * @param selected the selected to set
         */
        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    class SquidLabel extends Label {

        public SquidLabel(int row, int col, String text) {
            super(text);
            setWidth(BUTTON_WIDTH);
            setHeight(BUTTON_HEIGHT);

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

        /**
         * @param selected the selected to set
         */
        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        private final int row;
        private final int col;
        private boolean selected;

        public SquidRowColButtonEventHandler(int row, int col, boolean selected) {
            this.row = row;
            this.col = col;
            this.selected = selected;
        }

        @Override
        public void handle(Event event) {
            selected = !selected;
            ((SquidRowColButton) event.getSource()).setToolTipText("Click to " + (selected ? "de-" : "") + "select entire " + (String) (row == -1 ? "column" : "row"));
            ((SquidRowColButton) event.getSource()).setSelected(selected);
            squidProject.getTask().updateTableOfSelectedRatiosByRowOrCol(row, col, selected);
            populateRatioGrid();
        }
    }
}
