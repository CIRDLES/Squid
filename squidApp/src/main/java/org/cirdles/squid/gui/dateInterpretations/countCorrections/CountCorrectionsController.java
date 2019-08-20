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
package org.cirdles.squid.gui.dateInterpretations.countCorrections;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class CountCorrectionsController implements Initializable {

    @FXML
    private VBox vboxMaster;
    @FXML
    private VBox vboxTreeHolder;
    @FXML
    private VBox plotVBox;
    @FXML
    private AnchorPane plotAndConfigAnchorPane;
    @FXML
    private ToolBar plotToolBar;
    @FXML
    private RadioButton corr4_RadioButton;
    @FXML
    private ToggleGroup correctionToggleGroup;
    @FXML
    private RadioButton corr7_RadioButton;
    @FXML
    private RadioButton corr8_RadioButton;
    @FXML
    private RadioButton plotFlavorOneRadioButton;
    @FXML
    private ToggleGroup plotFlavorToggleGroup;
    @FXML
    private RadioButton plotFlavorTwoRadioButton;
    @FXML
    private CheckBox autoExcludeSpotsCheckBox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void selectedIsotopeIndexAction(ActionEvent event) {
    }

    @FXML
    private void plotChooserAction(ActionEvent event) {
    }

    @FXML
    private void autoExcludeSpotsCheckBoxAction(ActionEvent event) {
    }
    
}
