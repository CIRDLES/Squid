/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.squidReportTable;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import static org.cirdles.squid.gui.SquidUI.SQUID_LOGO_SANS_TEXT_URL;

/**
 * @author ryanb
 */
public class SquidReportTableLauncher {

    public String[][] referenceMaterialsReports;
    public String[][] unknownsReports;
    private Stage refMatStage;
    private Stage unknownsStage;
    private Stage primaryStage;

    public SquidReportTableLauncher(Stage primaryStage) {
        this.primaryStage = primaryStage;
        refMatStage = new Stage();
        unknownsStage = new Stage();
        refMatStage.getIcons().add(new Image(SQUID_LOGO_SANS_TEXT_URL));
        unknownsStage.getIcons().add(new Image(SQUID_LOGO_SANS_TEXT_URL));
        refMatStage.setTitle("Reference Materials");
        unknownsStage.setTitle("Unknowns");

        refMatStage.setMinWidth(900);
        refMatStage.setMinHeight(600);
        unknownsStage.setMinWidth(900);
        unknownsStage.setMinHeight(600);

        refMatStage.show();
        refMatStage.hide();
        unknownsStage.show();
        unknownsStage.hide();

        refMatStage.setOnCloseRequest(e -> {
            refMatStage.hide();
            e.consume();
        });
        unknownsStage.setOnCloseRequest(e -> {
            unknownsStage.hide();
            e.consume();
        });
    }

    public void load() {
        try {
            FXMLLoader refLoader = new FXMLLoader(getClass().getResource("SquidReportTableReferenceMaterials.fxml"));
            Scene refScene = new Scene(refLoader.load());
            refMatStage.setScene(refScene);
            FXMLLoader unLoader = new FXMLLoader(getClass().getResource("SquidReportTableUnknowns.fxml"));
            Scene unScene = new Scene(unLoader.load());
            unknownsStage.setScene(unScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void launch(ReportTableTab tab) {
        if (tab == ReportTableTab.refMat) {
            if (!refMatStage.isShowing()) {
                refMatStage.setX(primaryStage.getX() + (primaryStage.getWidth() - refMatStage.getWidth()) / 2);
                refMatStage.setY(primaryStage.getY() + (primaryStage.getHeight() - refMatStage.getHeight()) / 2);
                refMatStage.show();
            }
            refMatStage.requestFocus();
        } else {
            if (!unknownsStage.isShowing()) {
                unknownsStage.setX(primaryStage.getX() + (primaryStage.getWidth() - unknownsStage.getWidth()) / 2);
                unknownsStage.setY(primaryStage.getY() + (primaryStage.getHeight() - unknownsStage.getHeight()) / 2);
                unknownsStage.show();
            }
            unknownsStage.requestFocus();
        }
    }

    public enum ReportTableTab {
        refMat, unknown
    }

}
