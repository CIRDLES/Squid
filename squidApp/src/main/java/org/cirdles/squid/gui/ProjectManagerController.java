/*
 * Copyright 2017 cirdles.org.
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javax.xml.bind.JAXBException;
import org.cirdles.calamari.prawn.PrawnFile.Run;
import org.xml.sax.SAXException;

/**
 * FXML Controller class
 *
 * @author bowring
 */
public class ProjectManagerController implements Initializable {

    @FXML
    private TextField orignalPrawnFileName;
    @FXML
    private ListView<Run> shrimpFractionList;

    @FXML
    private TextField selectedFractionText;
    @FXML
    private Label softwareVersionLabel;

    private ObservableList<Run> shrimpRuns;
    @FXML
    private Label headerLabel;
    @FXML
    private TextField filterSpotName;

    private final RunsModel runsModel = new RunsModel();
    @FXML
    private Label spotsShownLabel;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        orignalPrawnFileName.setEditable(false);
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

            setUpShrimpFractionList();

        } catch (IOException | JAXBException | SAXException iOException) {
        }
    }

    private void setUpShrimpFractionList() {

        headerLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-font-family: 'Courier New';");
        headerLabel.setText(
                String.format("%1$-" + 20 + "s", "Name")
                + String.format("%1$-" + 12 + "s", "Date")
                + String.format("%1$-" + 12 + "s", "Time")
                + String.format("%1$-" + 6 + "s", "Peaks")
                + String.format("%1$-" + 6 + "s", "Scans"));

        shrimpFractionList.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-font-family: 'Courier New';");

        shrimpFractionList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Run>() {
            @Override
            public void changed(ObservableValue<? extends Run> observable, Run oldValue, Run newValue) {
                selectedFractionText.setText(newValue.getPar().get(0).getValue() + "  " + newValue.getSet().getPar().get(0).getValue());
            }
        });

        shrimpFractionList.setCellFactory(
                (lv)
                -> new ShrimpFractionListCell()
        );

        runsModel.addRunsList(shrimpRuns);
        spotsShownLabel.setText(runsModel.showFilteredOverAllCount());

        shrimpFractionList.itemsProperty().bind(runsModel.viewableShrimpRunsProperty());
    }

    @FXML
    private void filterSpotNameKeyReleased(KeyEvent event) {
        String filterString = filterSpotName.getText().toUpperCase(Locale.US).trim();
        Predicate<Run> filter = new SpotNameMatcher(filterString);
        runsModel.filterProperty().set(filter);
        spotsShownLabel.setText(runsModel.showFilteredOverAllCount());
    }

    public class RunsModel {

        public RunsModel() {
        }

        private final ObservableList<Run> shrimpRuns
                = FXCollections.observableArrayList();

        public ReadOnlyObjectProperty<ObservableList<Run>> shrimpRunsProperty() {
            return new SimpleObjectProperty<>(shrimpRuns);
        }

        private final FilteredList<Run> viewableShrimpRuns = new FilteredList<>(shrimpRuns);

        public ReadOnlyObjectProperty<ObservableList<Run>> viewableShrimpRunsProperty() {
            return new SimpleObjectProperty<>(viewableShrimpRuns);
        }

        public ObjectProperty<Predicate<? super Run>> filterProperty() {
            return viewableShrimpRuns.predicateProperty();
        }

        public void addRunsList(List<Run> myShrimpRuns) {
            shrimpRuns.clear();
            viewableShrimpRuns.clear();
            this.shrimpRuns.addAll(myShrimpRuns);
        }
        
        public String showFilteredOverAllCount(){
            return viewableShrimpRuns.size() + " / " + shrimpRuns.size() + " shown";
        }
    }

    static class SpotNameMatcher implements Predicate<Run> {

        private final String spotName;

        public SpotNameMatcher(String spotName) {
            this.spotName = spotName;
        }

        @Override
        public boolean test(Run run) {
            return run.getPar().get(0).getValue().startsWith(spotName);
        }
    }

    static class ShrimpFractionListCell extends ListCell<Run> {

        @Override
        protected void updateItem(Run run, boolean empty) {
            super.updateItem(run, empty);
            if (run == null || empty) {
                setText(null);
            } else {
                setText(
                        String.format("%1$-" + 20 + "s", run.getPar().get(0).getValue()) // name
                        + String.format("%1$-" + 12 + "s", run.getSet().getPar().get(0).getValue())//date
                        + String.format("%1$-" + 12 + "s", run.getSet().getPar().get(1).getValue()) //time
                        + String.format("%1$-" + 6 + "s", run.getPar().get(2).getValue()) //peaks
                        + String.format("%1$-" + 6 + "s", run.getPar().get(3).getValue())); //scans
            }
        }
    };
}
