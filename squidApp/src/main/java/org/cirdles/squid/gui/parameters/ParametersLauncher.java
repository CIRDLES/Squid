/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.parameters;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author ryanb
 */
public class ParametersLauncher {

    public ParametersLauncher() {

    }

    ;

    public void launchParametersManager() {
        try{
        Stage stage = new Stage();;
        
        stage.setMinHeight(700);
        stage.setMinWidth(800);
        
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("SquidParametersManagerGUI.fxml")));
        
        stage.setScene(scene);
        stage.setTitle("Squid Parameters Manager");

        stage.setOnCloseRequest((WindowEvent e) -> {
            Platform.exit();
            System.exit(0);
        });
        
        stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
