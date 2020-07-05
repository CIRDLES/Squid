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
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.taskDesign.TaskDesign;
import org.cirdles.squid.utilities.squidPrefixTree.SquidPrefixTree;

import java.net.URL;
import java.util.ResourceBundle;

import static org.cirdles.squid.gui.SquidUIController.squidLabData;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.gui.constants.Squid3GuiConstants.STYLE_MANAGER_TITLE;
import org.cirdles.squid.projects.SquidProject;
import static org.cirdles.squid.utilities.conversionUtilities.RoundingUtilities.USE_SIG_FIG_15;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 * @see
 * <a href="https://courses.bekwam.net/public_tutorials/bkcourse_filterlistapp.html" target="_blank">Bekwam.net</a>
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
    private GridPane projectManagerGridPane;
    @FXML
    private Label softwareVersionLabel1;
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
    private RadioButton roundingSquid25;
    @FXML
    private RadioButton roundingSquid3;
    @FXML
    private ToggleGroup roundingToggleGroup;
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

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        task = squidProject.getTask();
        taskDesign = SquidUIController.squidPersistentState.getTaskDesign();

        if (task != null) {
            roundingSquid3.setSelected(task.isRoundingForSquid3());
            setUpParametersModelsComboBoxes();
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

        setupListeners();

        // detect if project opened from menu by deserialization
        if (squidProject.prawnFileExists()) {
            setUpPrawnFile();
        }

        titleLabel.setStyle(STYLE_MANAGER_TITLE);
    }

    private void setUpParametersModelsComboBoxes() {

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
                        task.setupSquidSessionSpecsAndReduceAndReport(false);
                    }
                });

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
                        task.setupSquidSessionSpecsAndReduceAndReport(false);
                    }
                });
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
        assignedExternalErrUSpinner.valueProperty().addListener((ObservableValue<? extends Double> observable,
                Double oldValue, Double newValue) -> {
            squidProject.setExtPErrU(newValue);
            SquidProject.setProjectChanged(true);
            task.setExtPErrU(newValue);
        });

        SpinnerValueFactory<Double> valueFactoryTh
                = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.00, 1.00, task.getExtPErrTh(), 0.05);
        assignedExternalErrThSpinner.setValueFactory(valueFactoryTh);
        assignedExternalErrThSpinner.valueProperty().addListener((ObservableValue<? extends Double> observable, //
                Double oldValue, Double newValue) -> {
            squidProject.setExtPErrTh(newValue);
            SquidProject.setProjectChanged(true);
            task.setExtPErrTh(newValue);
        });
    }

    private void setUpPrawnFile() {
        // first prepare the session
        squidProject.preProcessPrawnSession();

        projectNameText.setText(squidProject.getProjectName());
        analystNameText.setText(squidProject.getAnalystName());
        projectNotesText.setText(squidProject.getProjectNotes());

        orignalPrawnFileName.setText(squidProject.getPrawnSourceFilePath());

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
        task.setupSquidSessionSpecsAndReduceAndReport(true);
    }

    @FXML
    private void noSBMRadioButtonActions(ActionEvent event) {
        squidProject.setUseSBM(false);
        SquidProject.setProjectChanged(true);
        task.setUseSBM(false);
        task.setChanged(true);
        task.setupSquidSessionSpecsAndReduceAndReport(true);
    }

    @FXML
    private void linearRegressionRatioCalcRadioButtonAction(ActionEvent event) {
        squidProject.setUserLinFits(true);
        SquidProject.setProjectChanged(true);
        task.setUserLinFits(true);
        task.setChanged(true);
        task.setupSquidSessionSpecsAndReduceAndReport(true);
    }

    @FXML
    private void spotAverageRatioCalcRadioButtonAction(ActionEvent event) {
        squidProject.setUserLinFits(false);
        SquidProject.setProjectChanged(true);
        task.setUserLinFits(false);
        task.setChanged(true);
        task.setupSquidSessionSpecsAndReduceAndReport(true);
    }

    @FXML
    private void pb204RadioButtonAction(ActionEvent event) {
        squidProject.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_204);
        SquidProject.setProjectChanged(true);
        task.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_204);
        task.setChanged(true);
    }

    @FXML
    private void pb207RadioButtonAction(ActionEvent event) {
        squidProject.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_207);
        SquidProject.setProjectChanged(true);
        task.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_207);
        task.setChanged(true);
    }

    @FXML
    private void pb208RadioButtonAction(ActionEvent event) {
        squidProject.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_208);
        SquidProject.setProjectChanged(true);
        task.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.PB_208);
        task.setChanged(true);
    }

    @FXML
    private void roundingSquid25Action(ActionEvent event) {
        USE_SIG_FIG_15 = false;
        task.setRoundingForSquid3(false);
        task.setChanged(true);
        task.setupSquidSessionSpecsAndReduceAndReport(true);
    }

    @FXML
    private void roundingSquid3Action(ActionEvent event) {
        USE_SIG_FIG_15 = true;
        task.setRoundingForSquid3(true);
        task.setChanged(true);
        task.setupSquidSessionSpecsAndReduceAndReport(true);
    }

    @FXML
    private void autoExcludeSpotsCheckBoxAction(ActionEvent event) {
        // this will cause weighted mean expressions to be changed with boolean flag
        squidProject.setSquidAllowsAutoExclusionOfSpots(autoExcludeSpotsCheckBox.isSelected());
        SquidProject.setProjectChanged(true);
        task.updateRefMatCalibConstWMeanExpressions(autoExcludeSpotsCheckBox.isSelected());
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
        taskDesign.setCommonPbModel(commonPbModelComboBox.getValue());
        
        taskDesign.setAnalystName(analystNameText.getText());

        SquidUIController.squidPersistentState.updateSquidPersistentState();
    }

    @FXML
    private void refreshModelsAction(ActionEvent event) {
        task.refreshParametersFromModels(true, true, false);
    }

}
