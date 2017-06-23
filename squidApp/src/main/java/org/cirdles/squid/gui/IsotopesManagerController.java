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

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import org.cirdles.squid.shrimp.MassStationDetail;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class IsotopesManagerController implements Initializable {

    @FXML
    private AnchorPane isotopesManagerAnchorPane;
    @FXML
    private TableView<MassStationDetail> isotopesTableView;
    @FXML
    private TableColumn<MassStationDetail, String> massLabelColumn;
    @FXML
    private TableColumn<MassStationDetail, String> elementLabelColumn;
    @FXML
    private TableColumn<MassStationDetail, String> isotopeLabelColumn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isotopesManagerAnchorPane.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        isotopesManagerAnchorPane.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(30));

        setupIsotopeTable();
    }

    private void setupIsotopeTable() {
        ObservableList<MassStationDetail> massStationsData
                = FXCollections.observableArrayList(SquidUIController.squidProject.makeListOfMassStationDetails());

        massLabelColumn.setCellValueFactory(new PropertyValueFactory<>("massStationLabel"));
        elementLabelColumn.setCellValueFactory(new PropertyValueFactory<>("elementLabel"));
        isotopeLabelColumn.setCellValueFactory(new PropertyValueFactory<>("isotopeLabel"));

        isotopesTableView.setItems(massStationsData);
        isotopesTableView.setFixedCellSize(25);
        isotopesTableView.prefHeightProperty().bind(Bindings.size(isotopesTableView.getItems()).multiply(isotopesTableView.getFixedCellSize()).add(30));
    }

}
