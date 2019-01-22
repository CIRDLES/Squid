/*
 * Copyright 2019 James F. Bowring and CIRDLES.org.
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.cirdles.squid.constants.Squid3Constants;
import static org.cirdles.squid.gui.constants.Squid3GuiConstants.STYLE_MANAGER_TITLE;
import org.cirdles.squid.utilities.stateUtilities.SquidPersistentState;
import org.cirdles.squid.utilities.stateUtilities.SquidUserPreferences;

/**
 * FXML Controller class
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class PreferencesManagerController implements Initializable {

    private SquidUserPreferences squidUserPreferences;
    
    @FXML
    private GridPane taskManagerGridPane;
    @FXML
    private RadioButton geochronTaskTypeRadioButton;
    @FXML
    private ToggleGroup taskTypeToggleGroup;
    @FXML
    private RadioButton generalTaskTypeRadioButton;
    @FXML
    private TextField authorsNameTextField;
    @FXML
    private TextField labNameTextField;
    @FXML
    private ComboBox<?> refMatModelComboBox;
    @FXML
    private ComboBox<?> concRefMatModelComboBox;
    @FXML
    private RadioButton pb204RadioButton;
    @FXML
    private ToggleGroup toggleGroupIsotope;
    @FXML
    private RadioButton pb207RadioButton;
    @FXML
    private RadioButton pb208RadioButton;
    @FXML
    private RadioButton yesSBMRadioButton;
    @FXML
    private ToggleGroup toggleGroupSMB;
    @FXML
    private RadioButton noSBMRadioButton;
    @FXML
    private RadioButton linearRegressionRatioCalcRadioButton;
    @FXML
    private ToggleGroup toggleGroupRatioCalcMethod;
    @FXML
    private RadioButton spotAverageRatioCalcRadioButton;
    @FXML
    private CheckBox autoExcludeSpotsCheckBox;
    @FXML
    private Spinner<?> assignedExternalErrSpinner;
    @FXML
    private ComboBox<?> commonPbModelComboBox;
    @FXML
    private ComboBox<?> physConstModelComboBox;
    @FXML
    private Label titleLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titleLabel.setStyle(STYLE_MANAGER_TITLE);       
        squidUserPreferences = SquidPersistentState.getExistingPersistentState().getSquidUserPreferences();
        ((RadioButton)taskManagerGridPane.lookup("#" + squidUserPreferences.getTaskType().getName())).setSelected(true);
    }    

    @FXML
    private void geochronTaskTypeRadioButtonAction(ActionEvent event) {
        squidUserPreferences.setTaskType(Squid3Constants.TaskTypeEnum.valueOf(geochronTaskTypeRadioButton.getId()));
        SquidPersistentState.getExistingPersistentState().updateUserPreferences();
    }

    @FXML
    private void generalTaskTypeRadioButtonAction(ActionEvent event) {
        squidUserPreferences.setTaskType(Squid3Constants.TaskTypeEnum.valueOf(generalTaskTypeRadioButton.getId()));
        SquidPersistentState.getExistingPersistentState().updateUserPreferences();
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

    @FXML
    private void yesSBMRadioButtonAction(ActionEvent event) {
    }

    @FXML
    private void noSBMRadioButtonActions(ActionEvent event) {
    }

    @FXML
    private void linearRegressionRatioCalcRadioButtonAction(ActionEvent event) {
    }

    @FXML
    private void spotAverageRatioCalcRadioButtonAction(ActionEvent event) {
    }

    @FXML
    private void autoExcludeSpotsCheckBoxAction(ActionEvent event) {
    }
    
}
