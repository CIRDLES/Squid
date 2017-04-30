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

import java.io.IOException;
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
import javax.xml.bind.JAXBException;
import org.cirdles.calamari.prawn.PrawnFile.Run;
import org.cirdles.squid.gui.RunsViewModel.ShrimpFractionListCell;
import org.cirdles.squid.gui.RunsViewModel.SpotNameMatcher;
import org.xml.sax.SAXException;

/**
 * FXML Controller class
 *
 * @see
 * <a href="https://courses.bekwam.net/public_tutorials/bkcourse_filterlistapp.html" target="_blank">Bekwam.net</a>
 * @author James F. Bowring
 */
public class ProjectManagerController implements Initializable {

    @FXML
    private TextField orignalPrawnFileName;
    @FXML
    private ListView<Run> shrimpFractionList;
    @FXML
    private ListView<Run> shrimpRefMatList;
    @FXML
    private TextField selectedSpotNameText;
    @FXML
    private Label softwareVersionLabel;
    @FXML
    private Label headerLabel;
    @FXML
    private TextField filterSpotName;
    @FXML
    private Label spotsShownLabel;
    @FXML
    private Label headerLabelRefMat;
    private static final String spotListStyleSpecs = "-fx-font-size: 12px; -fx-font-weight: bold; -fx-font-family: 'Courier New';";
    @FXML
    private Button saveSpotNameButton;

    private ObservableList<Run> shrimpRuns;
    private ObservableList<Run> shrimpRunsRefMat;
    private final RunsViewModel runsModel = new RunsViewModel();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        orignalPrawnFileName.setEditable(false);
        setUpShrimpFractionListHeaders();
    }

    @FXML
    private void selectPrawnFileAction(ActionEvent event) {

        try {
            SquidUIController.squidProject.selectPrawnFile();

            orignalPrawnFileName.setText(SquidUIController.squidProject.getPrawnXMLFileName());

            softwareVersionLabel.setText(
                    "Software Version: "
                    + SquidUIController.squidProject.getPrawnFileShrimpSoftwareVersionName());

            shrimpRuns = SquidUIController.squidProject.getListOfPrawnFileRuns();
            shrimpRunsRefMat = FXCollections.observableArrayList();

            setUpShrimpFractionList();

        } catch (IOException | JAXBException | SAXException iOException) {
        }
    }

    private void setUpShrimpFractionListHeaders() {

        headerLabel.setStyle(spotListStyleSpecs);
        headerLabel.setText(
                String.format("%1$-" + 20 + "s", "Spot Name")
                + String.format("%1$-" + 12 + "s", "Date")
                + String.format("%1$-" + 12 + "s", "Time")
                + String.format("%1$-" + 6 + "s", "Peaks")
                + String.format("%1$-" + 6 + "s", "Scans"));

        headerLabelRefMat.setStyle(spotListStyleSpecs);
        headerLabelRefMat.setText(
                String.format("%1$-" + 20 + "s", "Ref Mat Name")
                + String.format("%1$-" + 12 + "s", "Date")
                + String.format("%1$-" + 12 + "s", "Time")
                + String.format("%1$-" + 6 + "s", "Peaks")
                + String.format("%1$-" + 6 + "s", "Scans"));
    }

    private void setUpShrimpFractionList() {

        shrimpFractionList.setStyle(spotListStyleSpecs);

        shrimpFractionList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Run>() {
            @Override
            public void changed(ObservableValue<? extends Run> observable, Run oldValue, Run newValue) {
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
                -> new ShrimpFractionListCell()
        );

        runsModel.addRunsList(shrimpRuns);
        spotsShownLabel.setText(runsModel.showFilteredOverAllCount());

        shrimpFractionList.itemsProperty().bind(runsModel.viewableShrimpRunsProperty());
        
        shrimpFractionList.setContextMenu( createContextMenu() );

        // display of selected reference materials
        shrimpRefMatList.setStyle(spotListStyleSpecs);

        shrimpRefMatList.setCellFactory(
                (lv)
                -> new ShrimpFractionListCell()
        );

    }

    private ContextMenu createContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Remove this spot.");
        menuItem.setOnAction((evt) -> {
            Run selectedRun = shrimpFractionList.getSelectionModel().getSelectedItem();
            if (selectedRun != null) {
                runsModel.remove(selectedRun);
                shrimpRunsRefMat.remove(selectedRun);
            }
        });
        contextMenu.getItems().add(menuItem);
        return contextMenu;
    }

    @FXML
    private void filterSpotNameKeyReleased(KeyEvent event) {
        String filterString = filterSpotName.getText().toUpperCase(Locale.US).trim();
        Predicate<Run> filter = new SpotNameMatcher(filterString);
        runsModel.filterProperty().set(filter);
        spotsShownLabel.setText(runsModel.showFilteredOverAllCount());
    }

    @FXML
    private void saveSpotNameAction(ActionEvent event) {
        if (saveSpotNameButton.getUserData() != null) {
            ((Run) saveSpotNameButton.getUserData()).getPar().get(0).setValue(selectedSpotNameText.getText().trim().toUpperCase(Locale.US));
            shrimpFractionList.refresh();
            shrimpRefMatList.refresh();
        }
    }

    @FXML
    private void setFilteredSpotsToRefMatAction(ActionEvent event) {
        shrimpRunsRefMat = runsModel.getViewableShrimpRuns();
        shrimpRefMatList.setItems(shrimpRunsRefMat);
    }
}
