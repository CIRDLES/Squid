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
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum;

import static org.cirdles.squid.gui.SquidUIController.squidLabData;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.gui.constants.Squid3GuiConstants.STYLE_MANAGER_TITLE;

import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_MEAN_PPM_PARENT_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_PPM_PARENT_EQN_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_PRIMARY_UTH_EQN_NAME_TH;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_PRIMARY_UTH_EQN_NAME_U;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_TH_U_EQN_NAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_TH_U_EQN_NAME_S;
import org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsFactory;

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
    private ComboBox<ParametersModel> refMatModelComboBox;
    @FXML
    private ComboBox<ParametersModel> commonPbModelComboBox;
    @FXML
    private ComboBox<ParametersModel> physConstModelComboBox;
    @FXML
    private ComboBox<ParametersModel> concRefMatModelComboBox;
    @FXML
    private ToggleGroup primaryAgeToggleGroup;
    @FXML
    private ToggleGroup dirctALTtoggleGroup;
    @FXML
    private Label uncorrConstPbUlabel;
    @FXML
    private Label uncorrConstPbThlabel;
    @FXML
    private Label uncorrConstPbUExpressionLabel;
    @FXML
    private Label uncorrConstPbThExpressionLabel;
    @FXML
    private Label th232U238Label;
    @FXML
    private Label th232U238ExpressionLabel;
    @FXML
    private Label parentConcExpressionLabel;
    @FXML
    private Label parentConcLabel;

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
            task.setupSquidSessionSpecsAndReduceAndReport();
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

        if (task.getType().compareTo(Squid3Constants.TaskTypeEnum.GEOCHRON) != 0) {
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

        taskAuditTextArea.setText(task.printTaskAudit());

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
                taskAuditTextArea.setText(task.printTaskAudit());
            }
        });

        populateDirectives();
    }

    private void populateDirectives() {
        // Directives fields
        ((RadioButton) taskManagerGridPane.lookup("#" + task.getParentNuclide())).setSelected(true);
        ((RadioButton) taskManagerGridPane.lookup("#" + (String) (task.isDirectAltPD() ? "direct" : "indirect"))).setSelected(true);

        uncorrConstPbUlabel.setText(SQUID_PRIMARY_UTH_EQN_NAME_U + ":");
        Expression UTh_U = task.getExpressionByName(SQUID_PRIMARY_UTH_EQN_NAME_U);
        uncorrConstPbUExpressionLabel.setText((UTh_U == null) ? "Not Used" : UTh_U.getExcelExpressionString());

        uncorrConstPbThlabel.setText(SQUID_PRIMARY_UTH_EQN_NAME_TH + ":");
        Expression UTh_Th = task.getExpressionByName(SQUID_PRIMARY_UTH_EQN_NAME_TH);
        uncorrConstPbThExpressionLabel.setText((UTh_Th == null) ? "Not Used" : UTh_Th.getExcelExpressionString());

        th232U238Label.setText(SQUID_TH_U_EQN_NAME + ":");
        Expression thU = task.getExpressionByName(SQUID_TH_U_EQN_NAME);
        th232U238ExpressionLabel.setText((task.isDirectAltPD()) ? "Not Used" : thU.getExcelExpressionString());

        parentConcLabel.setText(SQUID_PPM_PARENT_EQN_NAME + ":");
        Expression parentPPM = task.getExpressionByName(SQUID_PPM_PARENT_EQN_NAME);
        parentConcExpressionLabel.setText((parentPPM == null) ? "Not Used" : parentPPM.getExcelExpressionString());
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

    @FXML
    private void toggleParentNuclideAction(ActionEvent event) {
        task.toggleParentNuclide();
        populateDirectives();
        taskAuditTextArea.setText(task.printTaskAudit());
    }

    static class ParameterModelStringConverter extends StringConverter<ParametersModel> {

        @Override
        public String toString(ParametersModel model) {
            return model.getModelNameWithVersion();
        }

        @Override
        public ParametersModel fromString(String string) {
            return null;
        }
    };

    private void setUpParametersModelsComboBoxes() {
        // ReferenceMaterials
        refMatModelComboBox.setConverter(new ParameterModelStringConverter());
        refMatModelComboBox.setItems(FXCollections.observableArrayList(squidLabData.getReferenceMaterials()));
        refMatModelComboBox.getSelectionModel().select(task.getReferenceMaterialModel());

        refMatModelComboBox.valueProperty()
                .addListener((ObservableValue<? extends ParametersModel> observable, ParametersModel oldValue, ParametersModel newValue) -> {
                    task.setReferenceMaterial(newValue);
                    task.setChanged(true);
                    task.setupSquidSessionSpecsAndReduceAndReport();
                });

        // ConcentrationReferenceMaterials
        concRefMatModelComboBox.setConverter(new ParameterModelStringConverter());
        concRefMatModelComboBox.setItems(FXCollections.observableArrayList(squidLabData.getReferenceMaterials()));
        concRefMatModelComboBox.getSelectionModel().select(task.getConcentrationReferenceMaterialModel());

        concRefMatModelComboBox.valueProperty()
                .addListener((ObservableValue<? extends ParametersModel> observable, ParametersModel oldValue, ParametersModel newValue) -> {
                    task.setConcentrationReferenceMaterial(newValue);
                    task.setChanged(true);
                    task.setupSquidSessionSpecsAndReduceAndReport();
                });

        // PhysicalConstantsModels
        physConstModelComboBox.setConverter(new ParameterModelStringConverter());
        physConstModelComboBox.setItems(FXCollections.observableArrayList(squidLabData.getPhysicalConstantsModels()));
        physConstModelComboBox.getSelectionModel().select(task.getPhysicalConstantsModel());

        physConstModelComboBox.valueProperty()
                .addListener((ObservableValue<? extends ParametersModel> observable, ParametersModel oldValue, ParametersModel newValue) -> {
                    task.setPhysicalConstantsModel(newValue);
                    task.setChanged(true);
                    task.setupSquidSessionSpecsAndReduceAndReport();
                });

        // CommonPbModels
        commonPbModelComboBox.setConverter(new ParameterModelStringConverter());
        commonPbModelComboBox.setItems(FXCollections.observableArrayList(squidLabData.getCommonPbModels()));
        commonPbModelComboBox.getSelectionModel().select(task.getCommonPbModel());

        commonPbModelComboBox.valueProperty()
                .addListener((ObservableValue<? extends ParametersModel> observable, ParametersModel oldValue, ParametersModel newValue) -> {
                    task.setCommonPbModel(newValue);
                    task.setChanged(true);
                    task.setupSquidSessionSpecsAndReduceAndReport();
                });
    }

    @FXML
    private void geochronTaskTypeRadioButtonAction(ActionEvent event) {
        task.setType(TaskTypeEnum.valueOf(geochronTaskTypeRadioButton.getId()));
        task.setChanged(true);
    }

    @FXML
    private void generalTaskTypeRadioButtonAction(ActionEvent event) {
        task.setType(TaskTypeEnum.valueOf(generalTaskTypeRadioButton.getId()));
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
