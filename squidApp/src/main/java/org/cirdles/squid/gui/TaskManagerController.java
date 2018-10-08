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
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.cirdles.squid.constants.Squid3Constants;

import static org.cirdles.squid.gui.SquidUIController.squidLabData;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.gui.constants.Squid3GuiConstants.STYLE_MANAGER_TITLE;

import org.cirdles.squid.gui.parameters.ParametersManagerGUIController;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.CommonPbModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterials.ReferenceMaterial;
import org.cirdles.squid.tasks.TaskInterface;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class TaskManagerController implements Initializable {

    private TaskInterface task;
    @FXML
    private GridPane taskManagerGridPane;
    @FXML
    private TextField taskNameTextField;
    @FXML
    private RadioButton geochronTaskTypeRadioButton;
    @FXML
    private ToggleGroup taskTypeToggleGroup;
    @FXML
    private RadioButton generalTaskTypeRadioButton;
    @FXML
    private ToggleGroup toggleGroupSMB;
    @FXML
    private ToggleGroup toggleGroupRatioCalcMethod;
    @FXML
    private TextField authorsNameTextField;
    @FXML
    private TextField labNameTextField;
    @FXML
    private TextField taskDescriptionTextField;
    @FXML
    private TextField provenanceTextField;
    @FXML
    private RadioButton yesSBMRadioButton;
    @FXML
    private RadioButton noSBMRadioButton;
    @FXML
    private RadioButton linearRegressionRatioCalcRadioButton;
    @FXML
    private RadioButton spotAverageRatioCalcRadioButton;
    @FXML
    private TextArea taskAuditTextArea;
    @FXML
    private RadioButton pb204RadioButton;
    @FXML
    private ToggleGroup toggleGroupIsotope;
    @FXML
    private RadioButton pb207RadioButton;
    @FXML
    private RadioButton pb208RadioButton;
    @FXML
    private Label titleLabel;
    @FXML
    private CheckBox autoExcludeSpotsCheckBox;
    @FXML
    private Spinner<Double> assignedExternalErrSpinner;
    @FXML
    private ComboBox<String> refMatModelComboBox;
    @FXML
    private ComboBox<String> commonPbModelComboBox;
    @FXML
    private ComboBox<String> physConstModelComboBox;
    @FXML
    private ComboBox<String> concRefMatModelComboBox;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (squidProject.getTask() != null) {
            task = squidProject.getTask();
            SquidUI.loadSpecsAndReduceReports();
            populateTaskFields();
            setupListeners();
        } else {
            taskAuditTextArea.setText("No Task information available");
        }

        setUpParametersModelsComboBoxes();
        titleLabel.setStyle(STYLE_MANAGER_TITLE);
    }

    private void populateTaskFields() {

        taskNameTextField.setText(task.getName());
        taskDescriptionTextField.setText(task.getDescription());
        authorsNameTextField.setText(task.getAuthorName());
        labNameTextField.setText(task.getLabName());
        provenanceTextField.setText(task.getProvenance());

        if (task.getType().compareToIgnoreCase("GEOCHRON") != 0) {
            generalTaskTypeRadioButton.setSelected(true);
        } else {
            geochronTaskTypeRadioButton.setSelected(true);
        }

        if (task.isUseSBM()) {
            yesSBMRadioButton.setSelected(true);
        } else {
            noSBMRadioButton.setSelected(true);
        }

        if (task.isUserLinFits()) {
            linearRegressionRatioCalcRadioButton.setSelected(true);
        } else {
            spotAverageRatioCalcRadioButton.setSelected(true);
        }

        taskAuditTextArea.setText(squidProject.getTask().printTaskAudit());

        // set Pb208 Isotope selector visible or not
        pb208RadioButton.setVisible(!task.isDirectAltPD() && !task.getParentNuclide().contains("232"));

        switch (task.getSelectedIndexIsotope()) {
            case PB_204:
                pb204RadioButton.setSelected(true);
                break;
            case PB_207:
                pb207RadioButton.setSelected(true);
                break;
            case PB_208:
                pb208RadioButton.setSelected(true);
                break;
        }

        autoExcludeSpotsCheckBox.setSelected(task.isSquidAllowsAutoExclusionOfSpots());

        SpinnerValueFactory<Double> valueFactory
                = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.50, 1.00, task.getExtPErr(), 0.05);
        assignedExternalErrSpinner.setValueFactory(valueFactory);
        assignedExternalErrSpinner.valueProperty().addListener(new ChangeListener<Double>() {

            @Override
            public void changed(ObservableValue<? extends Double> observable,//
                    Double oldValue, Double newValue) {
                    task.setExtPErr(assignedExternalErrSpinner.getValue());
                    taskAuditTextArea.setText(squidProject.getTask().printTaskAudit());
            }
        });
    }

    private void setupListeners() {
        taskNameTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                task.setName(newValue);
            }
        });

        taskDescriptionTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                task.setDescription(newValue);
            }
        });

        authorsNameTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                task.setAuthorName(newValue);
            }
        });

        labNameTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                task.setLabName(newValue);
            }
        });

        provenanceTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                task.setProvenance(newValue);
            }
        });

    }

    private void setUpParametersModelsComboBoxes() {
        ObservableList<String> list = FXCollections.observableArrayList();
        for(ReferenceMaterial model : squidLabData.getReferenceMaterials()) {
            list.add(ParametersManagerGUIController.getModVersionName(model));
        }
        refMatModelComboBox.setItems(list);
        concRefMatModelComboBox.setItems(list);
        if(task != null) {
            if(task.getReferenceMaterial() != null) {
                refMatModelComboBox.getSelectionModel().select(ParametersManagerGUIController.getModVersionName(task.getReferenceMaterial()));
            }
            if(task.getConcentrationReferenceMaterial() != null) {
                concRefMatModelComboBox.getSelectionModel().select(ParametersManagerGUIController.getModVersionName(task.getConcentrationReferenceMaterial()));
            }
        }
        refMatModelComboBox.addEventFilter(MouseEvent.MOUSE_CLICKED, val ->{
            ObservableList<String> refList = FXCollections.observableArrayList();
            for(ReferenceMaterial model : squidLabData.getReferenceMaterials()) {
                refList.add(ParametersManagerGUIController.getModVersionName(model));
            }
            refMatModelComboBox.setItems(refList);
            if(task != null && task.getReferenceMaterial() != null) {
                refMatModelComboBox.getSelectionModel().select(ParametersManagerGUIController.getModVersionName(task.getReferenceMaterial()));
            }
        });
        refMatModelComboBox.setOnAction(action -> {
            int selectedSpot = refMatModelComboBox.getSelectionModel().getSelectedIndex();
            if(selectedSpot > -1 && selectedSpot < squidLabData.getReferenceMaterials().size() && task != null) {
                task.setReferenceMaterial(squidLabData.getReferenceMaterial(selectedSpot));
                SquidUI.loadSpecsAndReduceReports();
            }
        });
        concRefMatModelComboBox.addEventFilter(MouseEvent.MOUSE_CLICKED, val ->{
            ObservableList<String> refList = FXCollections.observableArrayList();
            for(ReferenceMaterial model : squidLabData.getReferenceMaterials()) {
                refList.add(ParametersManagerGUIController.getModVersionName(model));
            }
            concRefMatModelComboBox.setItems(refList);
            if(task != null && task.getConcentrationReferenceMaterial() != null) {
                concRefMatModelComboBox.getSelectionModel().select(ParametersManagerGUIController.getModVersionName(task.getReferenceMaterial()));
            }
        });
        concRefMatModelComboBox.setOnAction(action -> {
            int selectedSpot = concRefMatModelComboBox.getSelectionModel().getSelectedIndex();
            if(selectedSpot > -1 && selectedSpot < squidLabData.getReferenceMaterials().size() && task != null) {
                task.setConcentrationReferenceMaterial(squidLabData.getReferenceMaterial(selectedSpot));
                SquidUI.loadSpecsAndReduceReports();
            }
        });

        list = FXCollections.observableArrayList();
        for(PhysicalConstantsModel model : squidLabData.getPhysicalConstantsModels()) {
            list.add(ParametersManagerGUIController.getModVersionName(model));
        }
        physConstModelComboBox.setItems(list);
        if(task != null && task.getPhysicalConstantsModel() != null) {
            physConstModelComboBox.getSelectionModel().select(ParametersManagerGUIController.getModVersionName(task.getPhysicalConstantsModel()));
        }
        physConstModelComboBox.addEventFilter(MouseEvent.MOUSE_CLICKED, val -> {
            ObservableList<String> physList = FXCollections.observableArrayList();
            for(PhysicalConstantsModel model : squidLabData.getPhysicalConstantsModels()) {
                physList.add(ParametersManagerGUIController.getModVersionName(model));
            }
            physConstModelComboBox.setItems(physList);
            if(task != null && task.getPhysicalConstantsModel() != null) {
                physConstModelComboBox.getSelectionModel().select(ParametersManagerGUIController.getModVersionName(task.getPhysicalConstantsModel()));
            }
        });
        physConstModelComboBox.setOnAction(action -> {
            int selectedSpot = physConstModelComboBox.getSelectionModel().getSelectedIndex();
            if(selectedSpot > -1 && selectedSpot < squidLabData.getPhysicalConstantsModels().size() && task != null) {
                task.setPhysicalConstantsModel(squidLabData.getPhysicalConstantsModel(selectedSpot));
                SquidUI.loadSpecsAndReduceReports();
            }
        });

        list = FXCollections.observableArrayList();
        for(CommonPbModel model : squidLabData.getcommonPbModels()) {
            list.add(ParametersManagerGUIController.getModVersionName(model));
        }
        commonPbModelComboBox.setItems(list);
        if(task != null && task.getCommonPbModel() != null) {
            commonPbModelComboBox.getSelectionModel().select(ParametersManagerGUIController.getModVersionName(task.getCommonPbModel()));
        }
        commonPbModelComboBox.addEventFilter(MouseEvent.MOUSE_CLICKED, val -> {
            ObservableList<String> pbList = FXCollections.observableArrayList();
            for(CommonPbModel model : squidLabData.getcommonPbModels()) {
                pbList.add(ParametersManagerGUIController.getModVersionName(model));
            }
            commonPbModelComboBox.setItems(pbList);
            if(task != null && task.getCommonPbModel() != null) {
                commonPbModelComboBox.getSelectionModel().select(ParametersManagerGUIController.getModVersionName(task.getCommonPbModel()));
            }
        });
        commonPbModelComboBox.setOnAction(action -> {
            int selectedSpot = commonPbModelComboBox.getSelectionModel().getSelectedIndex();
            if(selectedSpot > -1 && selectedSpot < squidLabData.getcommonPbModels().size() && task != null) {
                task.setCommonPbModel(squidLabData.getcommonPbModel(selectedSpot));
                SquidUI.loadSpecsAndReduceReports();
            }
        });
    }

    @FXML
    private void geochronTaskTypeRadioButtonAction(ActionEvent event) {
        task.setType("geochron");
        task.setChanged(true);
    }

    @FXML
    private void generalTaskTypeRadioButtonAction(ActionEvent event) {
        task.setType("general");
        task.setChanged(true);
    }

    @FXML
    private void yesSBMRadioButtonAction(ActionEvent event) {
        task.setUseSBM(true);
        task.setChanged(true);
    }

    @FXML
    private void noSBMRadioButtonActions(ActionEvent event) {
        task.setUseSBM(false);
        task.setChanged(true);
    }

    @FXML
    private void linearRegressionRatioCalcRadioButtonAction(ActionEvent event) {
        task.setUserLinFits(true);
        task.setChanged(true);
    }

    @FXML
    private void spotAverageRatioCalcRadioButtonAction(ActionEvent event) {
        task.setUserLinFits(false);
        task.setChanged(true);
    }

    @FXML
    private void pb204RadioButtonAction(ActionEvent event) {
        task.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_204);
        task.setChanged(true);
    }

    @FXML
    private void pb207RadioButtonAction(ActionEvent event) {
        task.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_207);
        task.setChanged(true);
    }

    @FXML
    private void pb208RadioButtonAction(ActionEvent event) {
        task.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_208);
        task.setChanged(true);
    }

    @FXML
    private void autoExcludeSpotsCheckBoxAction(ActionEvent event) {
        // this will cause weighted mean expressions to be changed with boolean flag
        task.updateRefMatCalibConstWMeanExpressions(autoExcludeSpotsCheckBox.isSelected());
    }

}
