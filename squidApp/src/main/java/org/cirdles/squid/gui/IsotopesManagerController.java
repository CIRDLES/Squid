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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.shrimp.MassStationDetail;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import static org.cirdles.squid.shrimp.SquidSpeciesModel.SQUID_DEFAULT_BACKGROUND_ISOTOPE_LABEL;
import org.cirdles.squid.tasks.TaskInterface;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class IsotopesManagerController implements Initializable {

    private TaskInterface task;
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
    @FXML
    private TableColumn<MassStationDetail, String> taskIsotopeLabelColumn;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isotopesManagerAnchorPane.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        isotopesManagerAnchorPane.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(PIXEL_OFFSET_FOR_MENU));

        task = squidProject.getTask();
        
        setupIsotopeTable();
    }

    private void setupIsotopeTable() {
        ObservableList<MassStationDetail> massStationsData
                = FXCollections.observableArrayList(task.makeListOfMassStationDetails());

        massLabelColumn.setCellValueFactory(new PropertyValueFactory<>("massStationLabel"));
        elementLabelColumn.setCellValueFactory(new PropertyValueFactory<>("elementLabel"));
        taskIsotopeLabelColumn.setCellValueFactory(new PropertyValueFactory<>("taskIsotopeLabel"));

        Callback<TableColumn<MassStationDetail, String>, TableCell<MassStationDetail, String>> cellFactory
                = new Callback<TableColumn<MassStationDetail, String>, TableCell<MassStationDetail, String>>() {
            @Override
            public TableCell<MassStationDetail, String> call(TableColumn param) {
                return new EditingCell();
            }
        };

        isotopeLabelColumn.setCellValueFactory(new PropertyValueFactory<>("isotopeLabel"));
        isotopeLabelColumn.setCellFactory(cellFactory);
        isotopeLabelColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<MassStationDetail, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<MassStationDetail, String> editEvent) {
                String editIsotopeName = editEvent.getNewValue();
                if (editIsotopeName.compareToIgnoreCase(SQUID_DEFAULT_BACKGROUND_ISOTOPE_LABEL) != 0) {
                    ((MassStationDetail) editEvent.getTableView().getItems().get(editEvent.getTablePosition().getRow()))
                            .setIsotopeLabel(editIsotopeName);
                    SquidSpeciesModel ssm
                            = task.getSquidSpeciesModelList()
                                    .get(editEvent.getTablePosition().getRow());
                    task.setChanged(true);
                    ssm.setIsotopeName(editIsotopeName);
                } else {
                    ((MassStationDetail) editEvent.getTableView().getItems().get(editEvent.getTablePosition().getRow()))
                            .setIsotopeLabel(editEvent.getOldValue());
                    isotopesTableView.refresh();
                }
            }
        });

        isotopesTableView.setRowFactory(new Callback<TableView<MassStationDetail>, TableRow<MassStationDetail>>() {
            @Override
            public TableRow<MassStationDetail> call(TableView<MassStationDetail> tableView) {
                final TableRow<MassStationDetail> row = new TableRow<>();
                final ContextMenu contextMenu = new ContextMenu();
                final MenuItem selectAsBackgroundMenuItem = new MenuItem("Select as Background");
                selectAsBackgroundMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        row.getItem().setIsotopeLabel(SquidSpeciesModel.SQUID_DEFAULT_BACKGROUND_ISOTOPE_LABEL);
                        SquidSpeciesModel ssm
                                = task.getSquidSpeciesModelList()
                                        .get(row.getItem().getMassStationIndex());
                        int previousIndex = task.selectBackgroundSpeciesReturnPreviousIndex(ssm);
                        if (previousIndex >= 0) {
                            massStationsData.get(previousIndex).setIsotopeLabel(
                                    task.getSquidSpeciesModelList().get(previousIndex).getIsotopeName());
                        }
                        task.setChanged(true);
                        isotopesTableView.refresh();
                    }
                });
                contextMenu.getItems().add(selectAsBackgroundMenuItem);

                final MenuItem deSelectAsBackgroundMenuItem = new MenuItem("De-select as Background");
                deSelectAsBackgroundMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        SquidSpeciesModel ssm
                                = task.getSquidSpeciesModelList()
                                        .get(row.getItem().getMassStationIndex());
                        task.setChanged(true);
                        ssm.setIsBackground(false);
                        row.getItem().setIsotopeLabel(ssm.getIsotopeName());
                        isotopesTableView.refresh();
                    }
                });
                contextMenu.getItems().add(deSelectAsBackgroundMenuItem);

                // Set context menu on row, but use a binding to make it only show for non-empty rows:  
                row.contextMenuProperty().bind(
                        Bindings.when(row.emptyProperty())
                                .then((ContextMenu) null)
                                .otherwise(contextMenu)
                );
                return row;
            }
        });

        isotopesTableView.setItems(massStationsData);
        isotopesTableView.setFixedCellSize(25);
        isotopesTableView.prefHeightProperty().bind(Bindings.size(isotopesTableView.getItems()).multiply(isotopesTableView.getFixedCellSize()).add(30));
    }

    @FXML
    private void applyTaskIsotopeLabelsAction(ActionEvent event) {
        task.applyTaskIsotopeLabels();
        task.setChanged(true);
        isotopesTableView.refresh();
        task.setupSquidSessionSpecsAndReduceAndReport();
        task.updateAllExpressions(2);
    }

    class EditingCell extends TableCell<MassStationDetail, String> {

        private TextField textField;

        public EditingCell() {
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText((String) getItem());
            setGraphic(null);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0,
                        Boolean arg1, Boolean arg2) {
                    if (!arg2) {
                        commitEdit(textField.getText());
                    }
                }
            });

            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent ke) {
                    KeyCode kc = ke.getCode();
                    if ((kc.equals(KeyCode.ENTER) || kc.equals(KeyCode.UP) || kc.equals(KeyCode.DOWN) || kc.equals(KeyCode.LEFT) || kc.equals(KeyCode.RIGHT))) {
                        commitEdit(textField.getText());
                    }
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem();
        }
    }

}
