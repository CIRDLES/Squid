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
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.cirdles.squid.dialogs.SquidMessageDialog;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.prawn.PrawnFile;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class SpotManagerController implements Initializable {

    private static ObservableList<PrawnFile.Run> shrimpRuns;
    private static ObservableList<PrawnFile.Run> shrimpRunsRefMat;
    private static ObservableList<PrawnFile.Run> shrimpRunsConcRefMat;

    private final RunsViewModel runsModel = new RunsViewModel();

    @FXML
    private AnchorPane spotManagerPane;
    @FXML
    private ListView<PrawnFile.Run> shrimpFractionList;
    @FXML
    private ListView<PrawnFile.Run> shrimpRefMatList;
    @FXML
    private ListView<PrawnFile.Run> shrimpConcentrationRefMatList;

    @FXML
    private TextField selectedSpotNameText;

    @FXML
    private Label headerLabel;
    @FXML
    private TextField filterSpotNameText;
    @FXML
    private Label spotsShownLabel;

    @FXML
    private Button setFilteredSpotsAsRefMatButton;
    @FXML
    private Label headerLabelRefMat;
    @FXML
    private Button saveSpotNameButton;

    @FXML
    private Label rmFilterLabel;
    @FXML
    private Label rmCountLabel;

    @FXML
    private Button setFilteredSpotsAsConcRefMatButton;
    @FXML
    private Label headerLabelConcRefMat;
    @FXML
    private Label concrmFilterLabel;
    @FXML
    private Label concrmCountLabel;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        spotManagerPane.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        spotManagerPane.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(PIXEL_OFFSET_FOR_MENU));

        setUpShrimpFractionListHeaders();
        saveSpotNameButton.setDisable(true);
        setFilteredSpotsAsRefMatButton.setDisable(true);
        setFilteredSpotsAsConcRefMatButton.setDisable(true);

        setUpPrawnFile();
    }

    private void setUpPrawnFile() {

        shrimpRuns = FXCollections.observableArrayList(squidProject.getPrawnFileRuns());

        setUpShrimpFractionList();
        saveSpotNameButton.setDisable(false);
        setFilteredSpotsAsRefMatButton.setDisable(false);
        setFilteredSpotsAsConcRefMatButton.setDisable(false);

        // filter runs to populate ref mat list 
        filterRuns(squidProject.getFilterForRefMatSpotNames());
        updateReferenceMaterialsList(false);

        // filter runs to populate concentration ref mat list 
        filterRuns(squidProject.getFilterForConcRefMatSpotNames());
        updateConcReferenceMaterialsList(false);

        // restore spot list to full population
        filterRuns("");
    }

    private void setUpShrimpFractionList() {

        shrimpFractionList.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);

        shrimpFractionList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PrawnFile.Run>() {
            @Override
            public void changed(ObservableValue<? extends PrawnFile.Run> observable, PrawnFile.Run oldValue, PrawnFile.Run newValue) {
                try {
                    selectedSpotNameText.setText(newValue.getPar().get(0).getValue());
                    saveSpotNameButton.setUserData(newValue);
                } catch (Exception e) {
                    selectedSpotNameText.setText("");
                }
            }
        });

        shrimpFractionList.setCellFactory(
                (lv)
                -> new RunsViewModel.ShrimpFractionListCell()
        );

        runsModel.addRunsList(shrimpRuns);
        spotsShownLabel.setText(runsModel.showFilteredOverAllCount());

        shrimpFractionList.itemsProperty().bind(runsModel.viewableShrimpRunsProperty());

        shrimpFractionList.setContextMenu(createAllSpotsViewContextMenu());

        // display of selected reference materials
        shrimpRefMatList.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);

        shrimpRefMatList.setCellFactory(
                (lv)
                -> new RunsViewModel.ShrimpFractionListCell()
        );

        shrimpRefMatList.setContextMenu(createRefMatSpotsViewContextMenu());

        // display of selected concentration reference materials
        shrimpConcentrationRefMatList.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);

        shrimpConcentrationRefMatList.setCellFactory(
                (lv)
                -> new RunsViewModel.ShrimpFractionListCell()
        );

        shrimpConcentrationRefMatList.setContextMenu(createConcRefMatSpotsViewContextMenu());
    }

    private ContextMenu createAllSpotsViewContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Remove this spot.");
        menuItem.setOnAction((evt) -> {
            PrawnFile.Run selectedRun = shrimpFractionList.getSelectionModel().getSelectedItem();
            if (selectedRun != null) {
                runsModel.remove(selectedRun);
                shrimpRunsRefMat.remove(selectedRun);
                shrimpRunsConcRefMat.remove(selectedRun);
                squidProject.removeRunFromPrawnFile(selectedRun);
                spotsShownLabel.setText(runsModel.showFilteredOverAllCount());

                squidProject.generatePrefixTreeFromSpotNames();
                rmCountLabel.setText(String.valueOf(shrimpRunsRefMat.size()));
                concrmCountLabel.setText(String.valueOf(shrimpRunsConcRefMat.size()));
                squidProject.getTask().setChanged(true);
            }
        });
        contextMenu.getItems().add(menuItem);

        menuItem = new MenuItem("Split Prawn file starting with this run, using original unedited and without duplicates noted.");
        menuItem.setOnAction((evt) -> {
            PrawnFile.Run selectedRun = shrimpFractionList.getSelectionModel().getSelectedItem();
            if (selectedRun != null) {
                String[] splitNames = squidProject.splitPrawnFileAtRun(selectedRun, true);
                SquidMessageDialog.showInfoDialog(
                        "Two Prawn XML files have been written:\n\n"
                        + "\t" + splitNames[0] + "\n"
                        + "\t" + splitNames[1] + "\n\n"
                        + "Create a new Squid Proejct with each of these Prawn XML files.",
                        primaryStageWindow
                );
            }
        });
        contextMenu.getItems().add(menuItem);

        menuItem = new MenuItem("Split Prawn file starting with this run, using this edited list with duplicates noted.");
        menuItem.setOnAction((evt) -> {
            PrawnFile.Run selectedRun = shrimpFractionList.getSelectionModel().getSelectedItem();
            if (selectedRun != null) {
                String[] splitNames = squidProject.splitPrawnFileAtRun(selectedRun, false);
                SquidMessageDialog.showInfoDialog(
                        "Two Prawn XML files have been written:\n\n"
                        + "\t" + splitNames[0] + "\n"
                        + "\t" + splitNames[1] + "\n\n"
                        + "Create a new Squid Proejct with each of these Prawn XML files.",
                        primaryStageWindow
                );
            }
        });
        contextMenu.getItems().add(menuItem);

        return contextMenu;
    }

    private ContextMenu createRefMatSpotsViewContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Clear list.");
        menuItem.setOnAction((evt) -> {
            squidProject.updateFilterForRefMatSpotNames("");
            updateReferenceMaterialsList(true);
        });
        contextMenu.getItems().add(menuItem);
        return contextMenu;
    }

    private ContextMenu createConcRefMatSpotsViewContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Clear list.");
        menuItem.setOnAction((evt) -> {
            squidProject.updateFilterForConcRefMatSpotNames("");
            updateConcReferenceMaterialsList(true);
        });
        contextMenu.getItems().add(menuItem);
        return contextMenu;
    }

    private void setUpShrimpFractionListHeaders() {

        headerLabel.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);
        headerLabel.setText(
                String.format("%1$-" + 20 + "s", "Spot Name")
                + String.format("%1$-" + 12 + "s", "Date")
                + String.format("%1$-" + 12 + "s", "Time")
                + String.format("%1$-" + 6 + "s", "Peaks")
                + String.format("%1$-" + 6 + "s", "Scans"));

        headerLabelRefMat.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);
        headerLabelRefMat.setText(
                String.format("%1$-" + 20 + "s", "Ref Mat Name")
                + String.format("%1$-" + 12 + "s", "Date")
                + String.format("%1$-" + 12 + "s", "Time")
                + String.format("%1$-" + 6 + "s", "Peaks")
                + String.format("%1$-" + 6 + "s", "Scans"));
        
        headerLabelConcRefMat.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);
        headerLabelConcRefMat.setText(
                String.format("%1$-" + 20 + "s", "Ref Mat Name")
                + String.format("%1$-" + 12 + "s", "Date")
                + String.format("%1$-" + 12 + "s", "Time")
                + String.format("%1$-" + 6 + "s", "Peaks")
                + String.format("%1$-" + 6 + "s", "Scans"));
    }

    @FXML
    private void filterSpotNameKeyReleased(KeyEvent event) {
        filterRuns(filterSpotNameText.getText().toUpperCase(Locale.US).trim());
    }

    private void filterRuns(String filterString) {
        Predicate<PrawnFile.Run> filter = new RunsViewModel.SpotNameMatcher(filterString);
        runsModel.filterProperty().set(filter);
        spotsShownLabel.setText(runsModel.showFilteredOverAllCount());
    }

    @FXML
    private void setFilteredSpotsToRefMatAction(ActionEvent event) {
        squidProject.updateFilterForRefMatSpotNames(
                filterSpotNameText.getText().toUpperCase(Locale.US).trim());
        updateReferenceMaterialsList(true);
    }
    
    @FXML
    private void setFilteredSpotsToConcRefMatAction(ActionEvent event) {
        squidProject.updateFilterForConcRefMatSpotNames(
                filterSpotNameText.getText().toUpperCase(Locale.US).trim());
        updateConcReferenceMaterialsList(true);
    }

    private void updateReferenceMaterialsList(boolean updateTaskStatus) {
        String filter = squidProject.getFilterForRefMatSpotNames();
        // initialize list
        shrimpRunsRefMat = runsModel.getViewableShrimpRuns();
        if (filter.length() == 0) {
            // prevent populating ref mat list with no filter
            shrimpRunsRefMat.clear();
        } else {
            shrimpRunsRefMat = runsModel.getViewableShrimpRuns();
        }
        shrimpRefMatList.setItems(shrimpRunsRefMat);
        rmFilterLabel.setText(
                squidProject.getFilterForRefMatSpotNames().length() > 0
                ? squidProject.getFilterForRefMatSpotNames()
                : "NO FILTER");
        rmCountLabel.setText(String.valueOf(shrimpRunsRefMat.size()));

        if (updateTaskStatus) {
            squidProject.getTask().setChanged(true);
            squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport();
        }
    }

    private void updateConcReferenceMaterialsList(boolean updateTaskStatus) {
        String filter = squidProject.getFilterForConcRefMatSpotNames();
        // initialize list
        shrimpRunsConcRefMat = runsModel.getViewableShrimpRuns();
        if (filter.length() == 0) {
            // prevent populating ref mat list with no filter
            shrimpRunsConcRefMat.clear();
        } else {
            shrimpRunsConcRefMat = runsModel.getViewableShrimpRuns();
        }
        shrimpConcentrationRefMatList.setItems(shrimpRunsConcRefMat);
        concrmFilterLabel.setText(
                squidProject.getFilterForConcRefMatSpotNames().length() > 0
                ? squidProject.getFilterForConcRefMatSpotNames()
                : "NO FILTER");
        concrmCountLabel.setText(String.valueOf(shrimpRunsConcRefMat.size()));

        if (updateTaskStatus) {
            squidProject.getTask().setChanged(true);
            squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport();
        }

    }

    @FXML
    private void saveSpotNameAction(ActionEvent event) {
        if (saveSpotNameButton.getUserData() != null) {
            ((PrawnFile.Run) saveSpotNameButton.getUserData()).getPar().get(0).setValue(selectedSpotNameText.getText().trim());
            squidProject.processPrawnSessionForDuplicateSpotNames();
            squidProject.generatePrefixTreeFromSpotNames();
            shrimpFractionList.refresh();
            shrimpRefMatList.refresh();
            shrimpConcentrationRefMatList.refresh();

            // refresh textbox in case "DUP" is removed or created
            selectedSpotNameText.setText(((PrawnFile.Run) saveSpotNameButton.getUserData()).getPar().get(0).getValue());
        }
    }  
}
