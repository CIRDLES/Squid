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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.cirdles.calamari.shrimp.ShrimpFractionExpressionInterface;

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
    private ListView<ShrimpFractionExpressionInterface> shrimpFractionList;
    @FXML
    private TextField selectedFractionText;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        orignalPrawnFileName.setEditable(false);
    }

    @FXML
    private void selectPrawnFileAction(ActionEvent event) {

        File prawnFile = SquidUIController.squidProject.selectPrawnFile();
        if (prawnFile != null) {
            orignalPrawnFileName.setText(prawnFile.getName());
            // proceed 
            myShrimpFractions = SquidUIController.squidProject.extractShrimpFractionsFromPrawnFileData();

            // prepare to list fractions
            ObservableList<ShrimpFractionExpressionInterface> shrimpFractions = FXCollections.observableArrayList(myShrimpFractions);

            shrimpFractionList.setItems(shrimpFractions);
            shrimpFractionList.setCellFactory((ListView<ShrimpFractionExpressionInterface> param) -> {
                ListCell<ShrimpFractionExpressionInterface> cell = new ListCell<ShrimpFractionExpressionInterface>() {

                    @Override
                    protected void updateItem(ShrimpFractionExpressionInterface t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.getFractionID());
                        }
                    }

                };

                return cell;
            });
            shrimpFractionList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ShrimpFractionExpressionInterface>() {
                @Override
                public void changed(ObservableValue<? extends ShrimpFractionExpressionInterface> observable, ShrimpFractionExpressionInterface oldValue, ShrimpFractionExpressionInterface newValue) {
                    selectedFractionText.setText(newValue.getFractionID() + "  " + ((newValue.isReferenceMaterial() ? "ref mat" : "unknown")));
                }
            });
        }

    }

}
