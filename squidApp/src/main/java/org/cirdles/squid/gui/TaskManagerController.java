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
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST_DEFAULT_EXPRESSION;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_DEFAULT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_DEFAULT_EXPRESSION;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR206PB238U_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR208PB232TH_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR206PB238U_CALIB_CONST_DEFAULT_EXPRESSION;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR208PB232TH_CALIB_CONST_DEFAULT_EXPRESSION;
import static org.cirdles.squid.gui.SquidUI.HEALTHY_EXPRESSION_STYLE;
import static org.cirdles.squid.gui.SquidUI.UNHEALTHY_EXPRESSION_STYLE;
import static org.cirdles.squid.tasks.expressions.Expression.makeExpressionForAudit;

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
    private ComboBox<ParametersModel> commonPbModelComboBox;
    @FXML
    private ComboBox<ParametersModel> physConstModelComboBox;
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
    @FXML
    private Spinner<Double> assignedExternalErrUSpinner;
    @FXML
    private Spinner<Double> assignedExternalErrThSpinner;
    @FXML
    private ComboBox<String> delimiterComboBox;

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
            setUpParametersModelsComboBoxes();
        } else {
            taskAuditTextArea.setText("No Task information available");
        }

        titleLabel.setStyle(STYLE_MANAGER_TITLE);
    }

    private void populateTaskFields() {

        taskNameTextField.setText(task.getName());
        taskDescriptionTextField.setText(task.getDescription());
        authorsNameTextField.setText(task.getAuthorName());
        labNameTextField.setText(task.getLabName());
        provenanceTextField.setText(task.getProvenance());

        if (task.getTaskType().compareTo(Squid3Constants.TaskTypeEnum.GEOCHRON) != 0) {
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

        SpinnerValueFactory<Double> valueFactoryU
                = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.50, 1.00, task.getExtPErrU(), 0.05);
        assignedExternalErrUSpinner.setValueFactory(valueFactoryU);
        assignedExternalErrUSpinner.valueProperty().addListener((ObservableValue<? extends Double> observable, //
                Double oldValue, Double newValue) -> {
            task.setExtPErrU(newValue);
        });

        SpinnerValueFactory<Double> valueFactoryTh
                = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.50, 1.00, task.getExtPErrTh(), 0.05);
        assignedExternalErrThSpinner.setValueFactory(valueFactoryTh);
        assignedExternalErrThSpinner.valueProperty().addListener((ObservableValue<? extends Double> observable, //
                Double oldValue, Double newValue) -> {
            task.setExtPErrTh(newValue);
        });

        // samples
        ObservableList<String> delimitersList = FXCollections.observableArrayList(Squid3Constants.SampleNameDelimitersEnum.names());
        delimiterComboBox.setItems(delimitersList);
        // set value before adding listener
        delimiterComboBox.getSelectionModel().select(task.getDelimiterForUnknownNames());
        delimiterComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> ov,
                    final String oldValue, final String newValue) {
                task.setDelimiterForUnknownNames(newValue);
            }
        });

        populateDirectives();
    }

    class MyConverter extends StringConverter<Double> {

        @Override
        public String toString(Double object) {
            return object + "";
        }

        @Override
        public Double fromString(String string) {
            return Double.parseDouble(string);
        }

    }

    /**
     *
     * @param expressionName
     * @param expressionString
     * @return
     */
    private Expression makeExpression(String expressionName, final String expressionString) {
        return makeExpressionForAudit(expressionName, expressionString, task.getNamedExpressionsMap());
    }

    private void populateDirectives() {
        // Directives fields
        ((RadioButton) taskManagerGridPane.lookup("#" + task.getParentNuclide())).setSelected(true);
        ((RadioButton) taskManagerGridPane.lookup("#" + (String) (task.isDirectAltPD() ? "direct" : "indirect"))).setSelected(true);

        boolean uPicked = ((RadioButton) taskManagerGridPane.lookup("#238")).isSelected();
        boolean directPicked = ((RadioButton) taskManagerGridPane.lookup("#direct")).isSelected();

        pb208RadioButton.setVisible(uPicked && !directPicked);
        if (!pb208RadioButton.isVisible() && task.getSelectedIndexIsotope().compareTo(Squid3Constants.IndexIsoptopesEnum.PB_208) == 0) {
            pb204RadioButton.setSelected(true);
        }
        boolean perm1 = uPicked && !directPicked;
        boolean perm2 = uPicked && directPicked;
        boolean perm3 = !uPicked && !directPicked;
        boolean perm4 = !uPicked && directPicked;

        uncorrConstPbUlabel.setText(UNCOR206PB238U_CALIB_CONST + ":");
        Expression UTh_U = task.getExpressionByName(UNCOR206PB238U_CALIB_CONST);
        String UTh_U_ExpressionString = (UTh_U == null) ? UNCOR206PB238U_CALIB_CONST_DEFAULT_EXPRESSION : UTh_U.getExcelExpressionString();
        uncorrConstPbUExpressionLabel.setText((perm1 || perm2 || perm4) ? UTh_U_ExpressionString : "Not Used");

        uncorrConstPbUExpressionLabel.setStyle(uncorrConstPbUExpressionLabel.getStyle()
                + (makeExpression(UNCOR206PB238U_CALIB_CONST, UTh_U_ExpressionString).amHealthy() ? HEALTHY_EXPRESSION_STYLE : UNHEALTHY_EXPRESSION_STYLE));

        uncorrConstPbThlabel.setText(UNCOR208PB232TH_CALIB_CONST + ":");
        Expression UTh_Th = task.getExpressionByName(UNCOR208PB232TH_CALIB_CONST);
        String UTh_Th_ExpressionString = (UTh_Th == null) ? UNCOR208PB232TH_CALIB_CONST_DEFAULT_EXPRESSION : UTh_Th.getExcelExpressionString();
        uncorrConstPbThExpressionLabel.setText((perm2 || perm3 || perm4) ? UTh_Th_ExpressionString : "Not Used");

        uncorrConstPbThExpressionLabel.setStyle(uncorrConstPbThExpressionLabel.getStyle()
                + (makeExpression(UNCOR208PB232TH_CALIB_CONST, UTh_Th_ExpressionString).amHealthy() ? HEALTHY_EXPRESSION_STYLE : UNHEALTHY_EXPRESSION_STYLE));

        th232U238Label.setText(TH_U_EXP_RM + ":");
        Expression thU = task.getExpressionByName(TH_U_EXP_DEFAULT);
        String thU_ExpressionString = (thU == null) ? TH_U_EXP_DEFAULT_EXPRESSION : thU.getExcelExpressionString();
        th232U238ExpressionLabel.setText((perm1 || perm3) ? thU_ExpressionString : "Not Used");

        th232U238ExpressionLabel.setStyle(th232U238ExpressionLabel.getStyle()
                + (makeExpression(TH_U_EXP_DEFAULT, thU_ExpressionString).amHealthy() ? HEALTHY_EXPRESSION_STYLE : UNHEALTHY_EXPRESSION_STYLE));

        parentConcLabel.setText(PARENT_ELEMENT_CONC_CONST + ":");
        Expression parentPPM = task.getExpressionByName(PARENT_ELEMENT_CONC_CONST);
        String parentPPM_ExpressionString = (parentPPM == null) ? PARENT_ELEMENT_CONC_CONST_DEFAULT_EXPRESSION : parentPPM.getExcelExpressionString();
        parentConcExpressionLabel.setText(parentPPM_ExpressionString);
        parentConcExpressionLabel.setStyle(parentConcExpressionLabel.getStyle()
                + (makeExpression(PARENT_ELEMENT_CONC_CONST, thU_ExpressionString).amHealthy() ? HEALTHY_EXPRESSION_STYLE : UNHEALTHY_EXPRESSION_STYLE));
        
        updateDirectiveButtons();
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
        task.setParentNuclide(((RadioButton) event.getSource()).getId());
        task.applyDirectives();
        populateDirectives();
        taskAuditTextArea.setText(task.printTaskAudit());
    }

    @FXML
    private void toggleDirectAltAction(ActionEvent event) {
        task.setDirectAltPD(((RadioButton) event.getSource()).getId().compareToIgnoreCase("DIRECT") == 0);
        task.applyDirectives();
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
        task.setTaskType(TaskTypeEnum.valueOf(geochronTaskTypeRadioButton.getId()));
        task.setChanged(true);
    }

    @FXML
    private void generalTaskTypeRadioButtonAction(ActionEvent event) {
        task.setTaskType(TaskTypeEnum.valueOf(generalTaskTypeRadioButton.getId()));
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
        updateDirectiveButtons();
        task.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_204);
        task.setChanged(true);
    }

    @FXML
    private void pb207RadioButtonAction(ActionEvent event) {
        updateDirectiveButtons();
        task.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_207);
        task.setChanged(true);
    }

    @FXML
    private void pb208RadioButtonAction(ActionEvent event) {
        updateDirectiveButtons();
        task.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_208);
        task.setChanged(true);
    }

    private void updateDirectiveButtons() {
        ((RadioButton) taskManagerGridPane.lookup("#232")).setDisable(pb208RadioButton.isSelected());
        ((RadioButton) taskManagerGridPane.lookup("#direct")).setDisable(pb208RadioButton.isSelected());
    }

    @FXML
    private void autoExcludeSpotsCheckBoxAction(ActionEvent event) {
        // this will cause weighted mean expressions to be changed with boolean flag
        task.updateRefMatCalibConstWMeanExpressions(autoExcludeSpotsCheckBox.isSelected());
    }

    @FXML
    private void refreshModelsAction(ActionEvent event) {
        task.refreshParametersFromModels();
    }

}
