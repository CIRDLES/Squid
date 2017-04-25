/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author bowring
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
