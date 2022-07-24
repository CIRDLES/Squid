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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.gui.dialogs.SquidMessageDialog;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.StaceyKramerCommonLeadModel;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.taskDesign.TaskDesign;
import org.cirdles.squid.utilities.squidPrefixTree.SquidPrefixTree;

import java.net.URL;
import java.util.ResourceBundle;

import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidLabData;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.gui.constants.Squid3GuiConstants.STYLE_MANAGER_TITLE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REF_238U235U_RM_MODEL_NAME;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 * @see <a href="https://courses.bekwam.net/public_tutorials/bkcourse_filterlistapp.html" target="_blank">Bekwam.net</a>
 */
public class ProjectManagerController implements Initializable {

    @FXML
    private TextField orignalPrawnFileName;
    @FXML
    private Label softwareVersionLabel;
    @FXML
    private Label summaryStatsLabel;
    @FXML
    private Label totalAnalysisTimeLabel;
    @FXML
    private TextField projectNameText;
    @FXML
    private TextField analystNameText;
    @FXML
    private TextArea projectNotesText;
    @FXML
    private Label loginCommentLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private TextField squidFileNameText;
    @FXML
    private RadioButton pb207RadioButton;
    @FXML
    private RadioButton pb208RadioButton;
    @FXML
    private RadioButton pb204RadioButton;
    @FXML
    private RadioButton yesSBMRadioButton;
    @FXML
    private RadioButton noSBMRadioButton;
    @FXML
    private ToggleGroup toggleGroupSBM;
    @FXML
    private Label invalidSBMCounts;
    @FXML
    private Label invalidSBMCountsDescription;
    @FXML
    private RadioButton linearRegressionRatioCalcRadioButton;
    @FXML
    private RadioButton spotAverageRatioCalcRadioButton;
    @FXML
    private ToggleGroup toggleGroupRatioCalcMethod;
    @FXML
    private CheckBox autoExcludeSpotsCheckBox;
    @FXML
    private ComboBox<ParametersModel> commonPbModelComboBox;
    @FXML
    private ComboBox<ParametersModel> physConstModelComboBox;
    @FXML
    private Spinner<Double> assignedExternalErrUSpinner;
    @FXML
    private Spinner<Double> assignedExternalErrThSpinner;
    @FXML
    private Button parametersSetDefaultsButton;

    private TaskInterface task;
    private TaskDesign taskDesign;
    @FXML
    private ToggleGroup toggleGroupIsotope;
    @FXML
    private Label projectModeLabel;
    @FXML
    private Label specifyDefaultCommonPbLabel;
    @FXML
    private Label preferredIndexIsotopeLabel;
    @FXML
    private HBox isotopeHBox;
    @FXML
    private HBox weightedMeansHBox;
    @FXML
    private Label weightedMeanRefMatLabel;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        projectModeLabel.setText(squidProject.getProjectType().getProjectName() + " Mode");
        task = squidProject.getTask();
        taskDesign = SquidUIController.squidPersistentState.getTaskDesign();

        if (task != null) {
//            roundingSquid3.setSelected(task.isRoundingForSquid3());
            try {
                setUpParametersModelsComboBoxes();
            } catch (SquidException squidException) {
                SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
            }

            invalidSBMCounts.setText("" + ((Task) task).getCountOfShrimpFractionsWithInvalidSBMcounts());
            invalidSBMCounts.setVisible(((Task) task).getCountOfShrimpFractionsWithInvalidSBMcounts() > 0);
            invalidSBMCountsDescription.setVisible(((Task) task).getCountOfShrimpFractionsWithInvalidSBMcounts() > 0);
            if (squidProject.isUseSBM()) {
                yesSBMRadioButton.setSelected(true);
            } else {
                noSBMRadioButton.setSelected(true);
            }
            if (squidProject.isUserLinFits()) {
                linearRegressionRatioCalcRadioButton.setSelected(true);
            } else {
                spotAverageRatioCalcRadioButton.setSelected(true);
            }
        }

        orignalPrawnFileName.setEditable(false);

        preferredIndexIsotopeLabel.setVisible(squidProject.isTypeGeochron());
        isotopeHBox.setVisible(squidProject.isTypeGeochron());
        weightedMeansHBox.setVisible(squidProject.isTypeGeochron());
        weightedMeanRefMatLabel.setVisible(squidProject.isTypeGeochron());

        setupListeners();

        // detect if project opened from menu by deserialization
        if (squidProject.prawnFileExists()) {
            setUpPrawnFile();
        }

        titleLabel.setStyle(STYLE_MANAGER_TITLE);
    }

    private void setUpParametersModelsComboBoxes() throws SquidException {

        // PhysicalConstantsModels
        physConstModelComboBox.setConverter(new ParameterModelStringConverter());
        physConstModelComboBox.setItems(FXCollections.observableArrayList(squidLabData.getPhysicalConstantsModels()));
        physConstModelComboBox.getSelectionModel().select(squidProject.getPhysicalConstantsModel());

        physConstModelComboBox.valueProperty()
                .addListener((ObservableValue<? extends ParametersModel> observable, ParametersModel oldValue, ParametersModel newValue) -> {
                    squidProject.setPhysicalConstantsModel(newValue);
                    SquidProject.setProjectChanged(true);
                    task.setChanged(true);
                    if (task.getReferenceMaterialSpots().size() > 0) {
                        try {
                            task.setupSquidSessionSpecsAndReduceAndReport(false);
                        } catch (SquidException squidException) {
                            SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
                        }
                    }
                });

        if (squidProject.isTypeGeochron()) {
            // CommonPbModels
            commonPbModelComboBox.setConverter(new ParameterModelStringConverter());
            commonPbModelComboBox.setItems(FXCollections.observableArrayList(squidLabData.getCommonPbModels()));
            commonPbModelComboBox.getSelectionModel().select(squidProject.getCommonPbModel());

            commonPbModelComboBox.valueProperty()
                    .addListener((ObservableValue<? extends ParametersModel> observable, ParametersModel oldValue, ParametersModel newValue) -> {
                        squidProject.setCommonPbModel(newValue);
                        SquidProject.setProjectChanged(true);
                        task.setChanged(true);
                        if (task.getReferenceMaterialSpots().size() > 0) {
                            try {
                                task.setupSquidSessionSpecsAndReduceAndReport(false);

                                // issue #714 prime the models
                                StaceyKramerCommonLeadModel.updatePhysicalConstants(squidProject.getTask().getPhysicalConstantsModel());
                                StaceyKramerCommonLeadModel.updateU_Ratio(
                                        squidProject.getTask().getReferenceMaterialModel().getDatumByName(REF_238U235U_RM_MODEL_NAME).getValue().doubleValue());
                                ((Task) task).evaluateUnknownsWithChangedParameters(task.getUnknownSpots());

                            } catch (SquidException squidException) {
                                SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
                            }
                        }
                    });
        } else {
            specifyDefaultCommonPbLabel.setVisible(false);
            commonPbModelComboBox.setVisible(false);
        }
    }

    private void setupListeners() {
        projectNameText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                squidProject.setProjectName(newValue);
            }
        });

        analystNameText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                squidProject.setAnalystName(newValue);
            }
        });

        projectNotesText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                squidProject.setProjectNotes(newValue);
            }
        });

        pb208RadioButton.setVisible(!task.isDirectAltPD() && !task.getParentNuclide().contains("232"));
        if (!pb208RadioButton.isVisible() && task.getSelectedIndexIsotope().compareTo(Squid3Constants.IndexIsoptopesEnum.PB_208) == 0) {
            pb204RadioButton.setSelected(true);
        }

        switch (squidProject.getSelectedIndexIsotope()) {
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

        autoExcludeSpotsCheckBox.setSelected(squidProject.isSquidAllowsAutoExclusionOfSpots());

        SpinnerValueFactory<Double> valueFactoryU
                = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.00, 1.00, task.getExtPErrU(), 0.05);
        assignedExternalErrUSpinner.setValueFactory(valueFactoryU);
        assignedExternalErrUSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                assignedExternalErrUSpinner.increment(0); // won't change value, but will commit editor
            }
        });
        assignedExternalErrUSpinner.valueProperty().addListener((ObservableValue<? extends Double> observable,
                                                                 Double oldValue, Double newValue) -> {
            squidProject.setExtPErrU(newValue);
            SquidProject.setProjectChanged(true);
            task.setExtPErrU(newValue);
            task.setChanged(true);
        });

        SpinnerValueFactory<Double> valueFactoryTh
                = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.00, 1.00, task.getExtPErrTh(), 0.05);
        assignedExternalErrThSpinner.setValueFactory(valueFactoryTh);
        assignedExternalErrThSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                assignedExternalErrThSpinner.increment(0); // won't change value, but will commit editor
            }
        });
        assignedExternalErrThSpinner.valueProperty().addListener((ObservableValue<? extends Double> observable, //
                                                                  Double oldValue, Double newValue) -> {
            squidProject.setExtPErrTh(newValue);
            SquidProject.setProjectChanged(true);
            task.setExtPErrTh(newValue);
            task.setChanged(true);
        });
    }

    private void setUpPrawnFile() {
        // first prepare the session
        squidProject.preProcessPrawnSession();

        projectNameText.setText(squidProject.getProjectName());
        analystNameText.setText(squidProject.getAnalystName());
        projectNotesText.setText(squidProject.getProjectNotes());

        orignalPrawnFileName.setText(squidProject.getPrawnFileHandler().getCurrentPrawnSourceFileLocation());

        squidFileNameText.setText(SquidUIController.projectFileName);

        softwareVersionLabel.setText(
                "Version: "
                        + squidProject.getPrawnFileShrimpSoftwareVersionName());

        loginCommentLabel.setText(
                "Login Comment: "
                        + squidProject.getPrawnFileLoginComment());

        extractSummaryStatsFromPrawnFile();
    }

    private void extractSummaryStatsFromPrawnFile() {

        SquidPrefixTree spotPrefixTree = squidProject.generatePrefixTreeFromSpotNames();

        String summaryStatsString = spotPrefixTree.buildSummaryDataString();

        // format into rows for summary tab
        summaryStatsLabel.setText("Summary:\n\t" + summaryStatsString.replaceAll(";", "\n\t"));

        totalAnalysisTimeLabel.setText("Total time in hours = " + (int) squidProject.getSessionDurationHours());
    }

    @FXML
    private void yesSBMRadioButtonAction(ActionEvent event) {
        squidProject.setUseSBM(true);
        SquidProject.setProjectChanged(true);
        task.setUseSBM(true);
        task.setChanged(true);
        try {
            task.setupSquidSessionSpecsAndReduceAndReport(true);
        } catch (SquidException squidException) {
            boolean chooseNoSBM = SquidMessageDialog.showChoiceDialog(squidException.getMessage(), primaryStageWindow);
            if (chooseNoSBM) {
                noSBMRadioButton.setSelected(true);
            }
        }
    }

    @FXML
    private void noSBMRadioButtonActions(ActionEvent event) {
        squidProject.setUseSBM(false);
        SquidProject.setProjectChanged(true);
        task.setUseSBM(false);
        task.setChanged(true);
        try {
            task.setupSquidSessionSpecsAndReduceAndReport(true);
        } catch (SquidException squidException) {
            SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
        }
    }

    @FXML
    private void linearRegressionRatioCalcRadioButtonAction(ActionEvent event) {
        squidProject.setUserLinFits(true);
        SquidProject.setProjectChanged(true);
        task.setUserLinFits(true);
        task.setChanged(true);
        try {
            task.setupSquidSessionSpecsAndReduceAndReport(true);
        } catch (SquidException squidException) {
            SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
        }
    }

    @FXML
    private void spotAverageRatioCalcRadioButtonAction(ActionEvent event) {
        squidProject.setUserLinFits(false);
        SquidProject.setProjectChanged(true);
        task.setUserLinFits(false);
        task.setChanged(true);
        try {
            task.setupSquidSessionSpecsAndReduceAndReport(true);
        } catch (SquidException squidException) {
            SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
        }
    }

    @FXML
    private void pb204RadioButtonAction(ActionEvent event) throws SquidException {
        squidProject.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_204);
        SquidProject.setProjectChanged(true);
        task.setChanged(true);
    }

    @FXML
    private void pb207RadioButtonAction(ActionEvent event) throws SquidException {
        squidProject.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_207);
        SquidProject.setProjectChanged(true);
        task.setChanged(true);
    }

    @FXML
    private void pb208RadioButtonAction(ActionEvent event) throws SquidException {
        squidProject.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_208);
        SquidProject.setProjectChanged(true);
        task.setChanged(true);
    }

    @FXML
    private void autoExcludeSpotsCheckBoxAction(ActionEvent event) {
        // this will cause weighted mean expressions to be changed with boolean flag
        squidProject.setSquidAllowsAutoExclusionOfSpots(autoExcludeSpotsCheckBox.isSelected());
        SquidProject.setProjectChanged(true);
        try {
            task.updateRefMatCalibConstWMeanExpressions(autoExcludeSpotsCheckBox.isSelected());
        } catch (SquidException squidException) {
            SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
        }
    }

    @FXML
    private void parametersSetDefaultsOnAction(ActionEvent actionEvent) {
        taskDesign.setUseSBM(yesSBMRadioButton.isSelected());

        taskDesign.setUserLinFits(linearRegressionRatioCalcRadioButton.isSelected());

        if (pb204RadioButton.isSelected()) {
            taskDesign.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_204);
        } else if (pb207RadioButton.isSelected()) {
            taskDesign.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_207);
        } else {
            taskDesign.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_208);
        }

        taskDesign.setSquidAllowsAutoExclusionOfSpots(autoExcludeSpotsCheckBox.isSelected());

        taskDesign.setExtPErrTh(assignedExternalErrThSpinner.getValue());
        taskDesign.setExtPErrU(assignedExternalErrUSpinner.getValue());

        taskDesign.setPhysicalConstantsModel(physConstModelComboBox.getValue());
        squidLabData.setPhysConstDefault(physConstModelComboBox.getValue());
        taskDesign.setCommonPbModel(commonPbModelComboBox.getValue());
        squidLabData.setCommonPbDefault(commonPbModelComboBox.getValue());

        taskDesign.setAnalystName(analystNameText.getText());

        SquidUIController.squidPersistentState.updateSquidPersistentState();
    }

    @FXML
    private void refreshModelsAction(ActionEvent event) {
        try {
            task.refreshParametersFromModels(squidProject.isTypeGeochron(), true, false);
            physConstModelComboBox.setItems(FXCollections.observableArrayList(squidLabData.getPhysicalConstantsModels()));
            commonPbModelComboBox.setItems(FXCollections.observableArrayList(squidLabData.getCommonPbModels()));
        } catch (SquidException squidException) {
            SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
        }
    }

    static class ParameterModelStringConverter extends StringConverter<ParametersModel> {

        @Override
        public String toString(ParametersModel model) {
            if (model == null) {
                return null;
            } else {
                return model.getModelNameWithVersion() + (model.isEditable() ? "" : " <Built-in>");
            }
        }

        @Override
        public ParametersModel fromString(String string) {
            return null;
        }
    }
}