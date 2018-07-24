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
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;

/**
 *
 * @author ryanb
 */
public class ParametersLauncher {

    private Stage squidLabDataStage;
    private TabPane tabs;

    public ParametersLauncher() {
        try {
            squidLabDataStage = new Stage();;

            squidLabDataStage.setMinHeight(720);
            squidLabDataStage.setMinWidth(800);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SquidParametersManagerGUI.fxml"));
            Scene scene = new Scene(loader.load());

            Map<String, Object> obMap = loader.getNamespace();
            tabs = (TabPane) obMap.get("rootTabPane");

            squidLabDataStage.setScene(scene);
            squidLabDataStage.setTitle("Squid Parameters Manager");

            squidLabDataStage.setOnCloseRequest((WindowEvent e) -> {
                squidLabDataStage.hide();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void launchParametersManager(boolean isRefMat) {
        if (isRefMat) {
            tabs.getSelectionModel().select(1);
            squidLabDataStage.centerOnScreen();
            squidLabDataStage.requestFocus();
        } else {
            tabs.getSelectionModel().select(0);
            squidLabDataStage.centerOnScreen();
            squidLabDataStage.requestFocus();
        }
        squidLabDataStage.show();
    }
}
