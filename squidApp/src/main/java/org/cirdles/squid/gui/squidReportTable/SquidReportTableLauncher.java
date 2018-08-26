/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.squidReportTable;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.IOException;

/**
 *
 * @author ryanb
 */
public class SquidReportTableLauncher {

    private static Stage primaryStage;

    public SquidReportTableLauncher(Stage masterStage) throws IOException {
        primaryStage = new Stage();

        Parent root = new AnchorPane();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Squid Report Table");

        primaryStage.setOnCloseRequest((WindowEvent e) -> {
            Platform.exit();
            System.exit(0);
        });
        
        scene.setRoot(FXMLLoader.load(getClass().getResource("SquidReportTableGUI.fxml")));

        primaryStage.setMinHeight(scene.getHeight());
        primaryStage.setMinWidth(scene.getWidth());
        primaryStage.setHeight(scene.getHeight() + 200);
        primaryStage.setWidth(scene.getWidth() + 400);
        primaryStage.show();
    }

    public void launch(ReportTableTab tab) {

    }

    public enum ReportTableTab {
        refMat, unknown
    }

}
