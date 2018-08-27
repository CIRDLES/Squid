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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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
    private static Pane referenceMaterials;
    private static Pane unknowns;

    public SquidReportTableLauncher(Stage masterStage) throws IOException {
        primaryStage = new Stage();

        Parent root = new Pane();
        Scene scene = new Scene(root);

        scene.setRoot(root);
        TabPane tabPane = new TabPane();



        primaryStage.setScene(scene);
        primaryStage.setTitle("Squid Report Table");

        primaryStage.setOnCloseRequest((WindowEvent e) -> {
            Platform.exit();
            System.exit(0);
        });
        
        referenceMaterials = FXMLLoader.load(getClass().getResource("SquidReportTableGUI.fxml"));
        unknowns = FXMLLoader.load(getClass().getResource("SquidReportTableGUI.fxml"));

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
