/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.squidReportTable;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

/**
 * @author ryanb
 */
public class SquidReportTableLauncher {

    private static Stage primaryStage;
    private static Pane referenceMaterials;
    private static Pane unknowns;
    public String[][] referenceMaterialsReports;
    public String[][] unknownsReports;
    TabPane tabs;
    private Tab refMatTab;
    private Tab unknownsTab;

    public SquidReportTableLauncher(Stage masterStage) throws IOException {
        primaryStage = new Stage();

        Parent root = new Pane();
        Scene scene = new Scene(root);

        scene.setRoot(root);
        tabs = new TabPane();
        refMatTab = new Tab("Reference Materials");
        unknownsTab = new Tab("Unknowns");
        tabs.getTabs().add(refMatTab);
        tabs.getTabs().add(unknownsTab);
        refMatTab.setContent(referenceMaterials);
        unknownsTab.setContent(unknowns);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Squid Report Table");

        primaryStage.setOnCloseRequest((WindowEvent e) -> {
            Platform.exit();
            System.exit(0);
        });

        referenceMaterials = FXMLLoader.load(getClass().getResource("SquidReportTableReferenceMaterials.fxml"));
        unknowns = FXMLLoader.load(getClass().getResource("SquidReportTableUnknowns.fxml"));
    }

    public void launch(ReportTableTab tab) {
        try {
            referenceMaterials = FXMLLoader.load(getClass().getResource("SquidReportTableReferenceMaterials.fxml"));
            unknowns = FXMLLoader.load(getClass().getResource("SquidReportTableUnknowns.fxml"));
            if (tab == ReportTableTab.refMat) {
                tabs.getSelectionModel().select(refMatTab);
            } else {
                tabs.getSelectionModel().select(unknownsTab);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum ReportTableTab {
        refMat, unknown
    }

}
