/*
 * Copyright 2016 James F. Bowring and CIRDLES.org.
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

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.cirdles.squid.shrimp.MassStationDetail;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.shrimp.UThBearingEnum;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.TaskInterface;

import java.net.URL;
import java.util.ResourceBundle;

import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.DEFAULT_BACKGROUND_MASS_LABEL;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class IsotopesManagerController implements Initializable {

    private TaskInterface task;
    @FXML
    private VBox isotopesManagerPane;
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
    @FXML
    private TableColumn<MassStationDetail, UThBearingEnum> uOrThBearingColumn;
    @FXML
    private Label backgroundStatusLabel;
    @FXML
    private TableColumn<MassStationDetail, String> amuColumn;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        task = squidProject.getTask();

        setupIsotopeTable();
        updateBackgroundStatusLabel();
    }

    private void updateBackgroundStatusLabel() {
        backgroundStatusLabel.setText(
                "Background Status: "
                        + ((task.getIndexOfBackgroundSpecies() == -1) ? "NOT " : "") + "Selected");
    }

    private void setupIsotopeTable() {
        // ref: http://o7planning.org/en/11079/javafx-tableview-tutorial

        ObservableList<MassStationDetail> massStationsData
                = FXCollections.observableArrayList(task.makeListOfMassStationDetails());

        // ==== massLabelColumn  ==
        massLabelColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MassStationDetail, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MassStationDetail, String> param) {
                MassStationDetail massStationDetail = param.getValue();
                String massStationLabel = massStationDetail.getMassStationLabel();
                return new SimpleObjectProperty<>(massStationLabel);
            }
        });

        // ==== elementLabelColumn  ==
        elementLabelColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MassStationDetail, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MassStationDetail, String> param) {
                MassStationDetail massStationDetail = param.getValue();
                String elementLabel = massStationDetail.getElementLabel();
                return new SimpleObjectProperty<>(elementLabel);
            }
        });

        elementLabelColumn.setCellFactory(new Callback<TableColumn<MassStationDetail, String>, TableCell<MassStationDetail, String>>() {
            @Override
            public TableCell<MassStationDetail, String> call(TableColumn param) {
                return new EditingCell();
            }
        });

        elementLabelColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<MassStationDetail, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<MassStationDetail, String> editEvent) {
                String editElementName = editEvent.getNewValue();
                ((MassStationDetail) editEvent.getTableView().getItems().get(editEvent.getTablePosition().getRow()))
                        .setElementLabel(editElementName);
                SquidSpeciesModel ssm
                        = task.getSquidSpeciesModelList()
                        .get(editEvent.getTablePosition().getRow());
                ssm.setElementName(editElementName);

                task.setChanged(true);
                isotopesTableView.refresh();
            }
        });

        // ==== uOrThBearingColumn  ==
        uOrThBearingColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MassStationDetail, UThBearingEnum>, ObservableValue<UThBearingEnum>>() {
            @Override
            public ObservableValue<UThBearingEnum> call(TableColumn.CellDataFeatures<MassStationDetail, UThBearingEnum> param) {
                MassStationDetail massStationDetail = param.getValue();
                String uThBearingName = massStationDetail.getuThBearingName();
                UThBearingEnum uThBearing = UThBearingEnum.getByName(uThBearingName);
                return new SimpleObjectProperty<>(uThBearing);
            }
        });

        ObservableList<UThBearingEnum> uThBearingList = FXCollections.observableArrayList(UThBearingEnum.values());
        uOrThBearingColumn.setCellFactory(ComboBoxTableCell.forTableColumn(uThBearingList));
        uOrThBearingColumn.setOnEditCommit((CellEditEvent<MassStationDetail, UThBearingEnum> event) -> {
            TablePosition<MassStationDetail, UThBearingEnum> pos = event.getTablePosition();
            int row = pos.getRow();
            MassStationDetail massStationDetail = event.getTableView().getItems().get(row);
            UThBearingEnum newUThBearingName = event.getNewValue();
            massStationDetail.setuThBearingName(newUThBearingName.getName());
            SquidSpeciesModel ssm = task.getSquidSpeciesModelList().get(row);
            ssm.setuThBearingName(newUThBearingName.getName());
        });

        // ==== amuColumn  ==
        amuColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MassStationDetail, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MassStationDetail, String> param) {
                MassStationDetail massStationDetail = param.getValue();
                String isotopeLabel = massStationDetail.getIsotopeAMU();
                return new SimpleObjectProperty<>(isotopeLabel);
            }
        });

        // ==== isotopeLabelColumn  ==
        isotopeLabelColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MassStationDetail, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MassStationDetail, String> param) {
                MassStationDetail massStationDetail = param.getValue();
                String isotopeLabel = massStationDetail.getIsotopeLabel();
                return new SimpleObjectProperty<>(isotopeLabel);
            }
        });

        isotopeLabelColumn.setCellFactory(new Callback<TableColumn<MassStationDetail, String>, TableCell<MassStationDetail, String>>() {
            @Override
            public TableCell<MassStationDetail, String> call(TableColumn param) {
                return new EditingCell();
            }
        });
        isotopeLabelColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<MassStationDetail, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<MassStationDetail, String> editEvent) {
                String editIsotopeName = editEvent.getNewValue();
                if (editIsotopeName.compareToIgnoreCase(DEFAULT_BACKGROUND_MASS_LABEL) != 0) {
                    ((MassStationDetail) editEvent.getTableView().getItems().get(editEvent.getTablePosition().getRow()))
                            .setIsotopeLabel(editIsotopeName);
                    SquidSpeciesModel ssm
                            = task.getSquidSpeciesModelList()
                            .get(editEvent.getTablePosition().getRow());
                    ssm.setIsotopeName(editIsotopeName);
                } else {
                    ((MassStationDetail) editEvent.getTableView().getItems().get(editEvent.getTablePosition().getRow()))
                            .setIsotopeLabel(editEvent.getOldValue());
                }
                task.setChanged(true);
                isotopesTableView.refresh();
            }
        });

        // ==== taskIsotopeLabelColumn  ==
        taskIsotopeLabelColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MassStationDetail, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MassStationDetail, String> param) {
                MassStationDetail massStationDetail = param.getValue();
                String taskIsotopeLabel = massStationDetail.getTaskIsotopeLabel();
                return new SimpleObjectProperty<>(taskIsotopeLabel);
            }
        });

        isotopesTableView.setRowFactory(new Callback<TableView<MassStationDetail>, TableRow<MassStationDetail>>() {
            @Override
            public TableRow<MassStationDetail> call(TableView<MassStationDetail> tableView) {
                final TableRow<MassStationDetail> row = new TableRow<>();
                final ContextMenu contextMenu = new ContextMenu();
                final MenuItem selectAsBackgroundMenuItem = new MenuItem("Select as Background Isotope");
                selectAsBackgroundMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (!row.getItem().getIsBackground()) {
                            task.setIndexOfBackgroundSpecies(row.getItem().getMassStationIndex());
                            task.setIndexOfTaskBackgroundMass(row.getItem().getMassStationIndex());

                            row.getItem().setIsBackground(true);
                            row.getItem().setIsotopeLabel(DEFAULT_BACKGROUND_MASS_LABEL);
                            row.getItem().updateTaskIsotopeLabelForBackground(((Task) task).findNominalMassOfTaskBackgroundMass());
                            row.getItem().setNumeratorRole(false);
                            row.getItem().setDenominatorRole(false);

                            SquidSpeciesModel ssm
                                    = task.getSquidSpeciesModelList()
                                    .get(row.getItem().getMassStationIndex());
                            int previousIndex = task.selectBackgroundSpeciesReturnPreviousIndex(ssm);
                            if (previousIndex >= 0) {
                                MassStationDetail previousMassStationDetail = massStationsData.get(previousIndex);
                                previousMassStationDetail.setIsotopeLabel(
                                        task.getSquidSpeciesModelList().get(previousIndex).getIsotopeName());
                                previousMassStationDetail.setTaskIsotopeLabel(task.getNominalMasses().get(previousIndex));
                                previousMassStationDetail.setNumeratorRole(false);
                                previousMassStationDetail.setDenominatorRole(false);

                                SquidSpeciesModel previousSsm = task.getSquidSpeciesModelList().get(previousIndex);
                                previousSsm.setIsBackground(false);
                                //previousSsm.setNumeratorRole(false);
                                //previousSsm.setDenominatorRole(false);
                            }
                            task.setChanged(true);
                            isotopesTableView.refresh();
                            updateBackgroundStatusLabel();
                        }
                    }
                });
                contextMenu.getItems().add(selectAsBackgroundMenuItem);

                final MenuItem deSelectAsBackgroundMenuItem = new MenuItem("De-select as Background Isotope");
                deSelectAsBackgroundMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (row.getItem().getIsBackground()) {
                            SquidSpeciesModel ssm
                                    = task.getSquidSpeciesModelList()
                                    .get(row.getItem().getMassStationIndex());
                            task.setIndexOfBackgroundSpecies(-1);
                            task.setIndexOfTaskBackgroundMass(-1);
                            task.setChanged(true);
                            ssm.setIsBackground(false);
                            row.getItem().setIsBackground(false);
                            row.getItem().setIsotopeLabel(ssm.getIsotopeName());
                            row.getItem().setTaskIsotopeLabel(task.getNominalMasses().get(row.getItem().getMassStationIndex()));

                            isotopesTableView.refresh();
                            updateBackgroundStatusLabel();
                        }
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
        task.applyTaskIsotopeLabelsToMassStationsAndUpdateTask();
        postApplicationOfLabelsUpdate();
    }

    @FXML
    private void applyMassStationIsotopeLabelsAction(ActionEvent event) {
        task.applyMassStationLabelsToTask();
        postApplicationOfLabelsUpdate();
    }

    private void postApplicationOfLabelsUpdate() {
        ObservableList<MassStationDetail> massStationsData
                = FXCollections.observableArrayList(task.makeListOfMassStationDetails());
        isotopesTableView.setItems(massStationsData);
        setupIsotopeTable();
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