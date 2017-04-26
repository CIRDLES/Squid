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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javax.xml.bind.JAXBException;
import org.cirdles.calamari.prawn.PrawnFile;
import org.cirdles.calamari.prawn.PrawnFile.Run;
import org.cirdles.calamari.shrimp.ShrimpFractionExpressionInterface;
import org.xml.sax.SAXException;

/**
 * FXML Controller class
 *
 * @author bowring
 */
public class ProjectManagerController implements Initializable {

    private List<ShrimpFractionExpressionInterface> myShrimpFractions;
    @FXML
    private TextField orignalPrawnFileName;
    @FXML
    private ListView<Run> shrimpFractionList;
    @FXML
    private TextField selectedFractionText;
    @FXML
    private Label softwareVersionLabel;

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

        File prawnXMLFile = SquidUIController.squidProject.selectPrawnFile();
        if (prawnXMLFile != null) {
            orignalPrawnFileName.setText(prawnXMLFile.getName());
            // proceed 
            try {
                PrawnFile prawnFile = SquidUIController.squidProject.deserializePrawnData();

                String shrimpSoftwareVersion = prawnFile.getSoftwareVersion();
                softwareVersionLabel.setText("Software Version: " + shrimpSoftwareVersion);

                int runsCount = prawnFile.getRuns();

                ObservableList<Run> shrimpRuns = FXCollections.observableArrayList(prawnFile.getRun());
                shrimpFractionList.setItems(shrimpRuns);
                shrimpFractionList.setCellFactory((ListView<Run> param) -> {
                    ListCell<Run> cell = new ListCell<Run>() {

                        @Override
                        protected void updateItem(Run run, boolean bln) {
                            super.updateItem(run, bln);
                            if (run != null) {
                                setText(
                                        String.format("%1$-" + 15 + "s", run.getPar().get(0).getValue()) // name
                                        + "\t  " + String.format("%1$-" + 12 + "s", run.getSet().getPar().get(0).getValue())//date
                                        + "\t  " + String.format("%1$-" + 12 + "s", run.getSet().getPar().get(1).getValue()) //time
                                        + "\t  " + run.getPar().get(2).getValue()
                                        + "\t  " + run.getPar().get(3).getValue());
                            }
                        }

                    };

                    return cell;
                });
                shrimpFractionList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Run>() {
                    @Override
                    public void changed(ObservableValue<? extends Run> observable, Run oldValue, Run newValue) {
                        selectedFractionText.setText(newValue.getPar().get(0).getValue() + "  " + newValue.getSet().getPar().get(0).getValue());
                    }
                });

            } catch (IOException | JAXBException | SAXException iOException) {
            }
        }

    }

}
