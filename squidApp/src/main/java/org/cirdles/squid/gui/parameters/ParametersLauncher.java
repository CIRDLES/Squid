/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.parameters;

import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author ryanb
 */
public class ParametersLauncher {

    private Stage stage;
    private FXMLLoader loader;
    private TabPane tabs;

    public ParametersLauncher() {
        try {
            stage = new Stage();;

            stage.setMinHeight(700);
            stage.setMinWidth(800);

            loader = new FXMLLoader(getClass().getResource("SquidParametersManagerGUI.fxml"));           
            Scene scene = new Scene(loader.load());

            Map<String, Object> obMap = loader.getNamespace();
            tabs = (TabPane) obMap.get("rootTabPane");
            

            stage.setScene(scene);
            stage.setTitle("Squid Parameters Manager");

            stage.setOnCloseRequest((WindowEvent e) -> {
                stage.hide();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void launchParametersManager(boolean isRefMat) {
        stage.show();
        if (isRefMat) {
            tabs.getSelectionModel().select(1);
        } else {
            tabs.getSelectionModel().select(0);
        }
    }
}
