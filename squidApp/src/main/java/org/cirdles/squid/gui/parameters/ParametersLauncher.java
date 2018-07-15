/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.parameters;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.cirdles.squid.gui.SquidUI;

/**
 *
 * @author ryanb
 */
public class ParametersLauncher {

    public ParametersLauncher() {
        
    };

    public void launchParametersManager() {
        try {
            Stage stage = SquidUI.parametersStage;

            stage.setMinHeight(700);
            stage.setMinWidth(800);

            Parent root = new AnchorPane();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("SquidParametersManagerGUI.fxml")));

            stage.setScene(scene);
            stage.setTitle("Squid Parameters Manager");

            SquidUI.parametersWindow = stage.getScene().getWindow();

            stage.setOnCloseRequest((WindowEvent e) -> {
                Platform.exit();
                System.exit(0);
            });

            stage.show();
            
            SquidUI.parametersWindow.requestFocus();
        } catch (Exception e) {

        }
    }
}
