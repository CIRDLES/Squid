/*
 * Copyright 2017 cirdles.org.
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
import javafx.scene.control.Label;
import org.cirdles.squid.gui.utilities.BrowserControl;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class AboutSquidController implements Initializable {

    @FXML
    private Label versionText;
    @FXML
    private Label buildDate;
    @FXML
    private Label aboutDetailsLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        versionText.setText(" Squid3 v " + SquidUI.VERSION);
        buildDate.setText("Release Date: " + SquidUI.RELEASE_DATE);
        aboutDetailsLabel.setText(SquidUI.aboutWindowContent.toString());
    }

    @FXML
    private void visitSquidOnCirdlesAction(ActionEvent event) {
        BrowserControl.showURI("http://cirdles.org/projects/squid/");
    }

    @FXML
    private void visitUsOnGithubAction(ActionEvent event) {
        BrowserControl.showURI("https://github.com/CIRDLES/Squid");
    }

}
