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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_MEAN_PPM_PARENT_NAME;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.tasks.TaskInterface;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class TaskManagerController implements Initializable {

    private TaskInterface task;
    @FXML
    private AnchorPane taskManagerAnchorPane;
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
    private CheckBox calcMeanConcetrationCheckBox;
    @FXML
    private RadioButton pb204RadioButton;
    @FXML
    private ToggleGroup toggleGroupIsotope;
    @FXML
    private RadioButton pb207RadioButton;
    @FXML
    private RadioButton pb208RadioButton;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        taskManagerAnchorPane.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        taskManagerAnchorPane.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(PIXEL_OFFSET_FOR_MENU));

        if (squidProject.getTask() != null) {
            task = squidProject.getTask();
            populateTaskFields();
            setupListeners();
        } else {
            taskAuditTextArea.setText("No Task information available");
        }
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
        
        boolean hasZeroMeanConc = false;
        try {
            hasZeroMeanConc = (task.getTaskExpressionsEvaluationsPerSpotSet().get(SQUID_MEAN_PPM_PARENT_NAME).getValues()[0][0] == 0.0);
        } catch (Exception e) {
        }
        calcMeanConcetrationCheckBox.setDisable(hasZeroMeanConc);
        calcMeanConcetrationCheckBox.setSelected((!hasZeroMeanConc) && task.isUseCalculated_pdMeanParentEleA());

        taskAuditTextArea.setText(squidProject.getTask().printTaskAudit());
        
        // set Pb208 Isotope selector visible or not
        pb208RadioButton.setVisible(!task.isDirectAltPD()  && !task.getParentNuclide().contains("232"));
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
    private void calcMeanConcetrationCheckBoxAction(ActionEvent event) {
        task.setUseCalculated_pdMeanParentEleA(calcMeanConcetrationCheckBox.isSelected());
    }

    @FXML
    private void pb204RadioButtonAction(ActionEvent event) {
    }

    @FXML
    private void pb207RadioButtonAction(ActionEvent event) {
    }

    @FXML
    private void pb208RadioButtonAction(ActionEvent event) {
    }

}
