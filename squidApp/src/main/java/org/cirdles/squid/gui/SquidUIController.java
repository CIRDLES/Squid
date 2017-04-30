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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.cirdles.squid.fileManagement.CalamariFileManager;
import org.cirdles.squid.projects.SquidProject;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class SquidUIController implements Initializable {

    public static SquidProject squidProject;

    @FXML
    private Pane centerPane;
    @FXML
    private Menu manageExpressionsMenu;
    @FXML
    private Menu manageTasksMenu;
    @FXML
    private Menu manageAnalysisMenu;
    @FXML
    private Pane mainPane;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            Pane splashScreen = FXMLLoader.load(getClass().getResource("AboutSquid.fxml"));
            splashScreen.setId("AboutSquid");
            VBox.setVgrow(splashScreen, Priority.ALWAYS);
            HBox.setHgrow(splashScreen, Priority.ALWAYS);
            centerPane.getChildren().set(0, splashScreen);
            splashScreen.setVisible(true);
        } catch (IOException iOException) {
        }

        manageExpressionsMenu.setDisable(true);
        manageTasksMenu.setDisable(true);
        manageAnalysisMenu.setDisable(true);

    }

    @FXML
    private void newSquidProjectAction(ActionEvent event) {
        squidProject = new SquidProject();
        CalamariFileManager.initCalamariFiles(squidProject.getPrawnFileHandler(), "1.4.0");

        try {
            Pane projectManagerUI = FXMLLoader.load(getClass().getResource("ProjectManager.fxml"));
            projectManagerUI.setId("ProjectManager");
            VBox.setVgrow(projectManagerUI, Priority.ALWAYS);
            HBox.setHgrow(projectManagerUI, Priority.ALWAYS);
            mainPane.getChildren().set(0, projectManagerUI);
            projectManagerUI.setVisible(true);
        } catch (IOException iOException) {
        }

    }

}
