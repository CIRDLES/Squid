/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.squidReportTable;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import static org.cirdles.squid.gui.SquidUI.SQUID_LOGO_SANS_TEXT_URL;

/**
 * @author ryanb
 */
public class SquidReportTableLauncher {

    private Stage refMatStage;
    private Stage unknownsStage;
    private Stage primaryStage;

    public SquidReportTableLauncher(Stage primaryStage) {
        this.primaryStage = primaryStage;
        refMatStage = new Stage();
        unknownsStage = new Stage();
        refMatStage.getIcons().add(new Image(SQUID_LOGO_SANS_TEXT_URL));
        unknownsStage.getIcons().add(new Image(SQUID_LOGO_SANS_TEXT_URL));
        refMatStage.setTitle("Squid 3 Reference Materials");
        unknownsStage.setTitle("Squid 3 Unknowns");

        refMatStage.setMinWidth(500);
        refMatStage.setWidth(1000);
        refMatStage.setMinHeight(375);
        refMatStage.setHeight(600);
        unknownsStage.setMinWidth(500);
        unknownsStage.setWidth(1000);
        unknownsStage.setMinHeight(375);
        unknownsStage.setHeight(600);

        refMatStage.setOnCloseRequest(e -> {
            refMatStage.hide();
            e.consume();
        });
        unknownsStage.setOnCloseRequest(e -> {
            unknownsStage.hide();
            e.consume();
        });
    }

    public void launch(ReportTableTab tab) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("SquidReportTable.fxml"));
        try {
            switch (tab) {
                case refMat:
                    SquidReportTableController.typeOfController = ReportTableTab.refMat;
                    if (refMatStage.isShowing()) {
                        refMatStage.close();
                    }
                    Scene scene = new Scene(loader.load());
                    refMatStage.setScene(scene);
                    refMatStage.show();
                    refMatStage.setX(primaryStage.getX() + (primaryStage.getWidth() - refMatStage.getWidth()) / 2 + 10);
                    refMatStage.setY(primaryStage.getY() + (primaryStage.getHeight() - refMatStage.getHeight()) / 2 + 5);
                    refMatStage.requestFocus();
                    break;

                case refMatTest:
                    SquidReportTableController.typeOfController = ReportTableTab.refMatTest;
                    if (refMatStage.isShowing()) {
                        refMatStage.close();
                    }
                    scene = new Scene(loader.load());
                    refMatStage.setScene(scene);
                    refMatStage.show();
                    refMatStage.setX(primaryStage.getX() + (primaryStage.getWidth() - refMatStage.getWidth()) / 2 + 10);
                    refMatStage.setY(primaryStage.getY() + (primaryStage.getHeight() - refMatStage.getHeight()) / 2 + 5);
                    refMatStage.requestFocus();
                    break;

                case unknown:
                    SquidReportTableController.typeOfController = ReportTableTab.unknown;
                    if (unknownsStage.isShowing()) {
                        unknownsStage.close();
                    }
                    scene = new Scene(loader.load());
                    unknownsStage.setScene(scene);
                    unknownsStage.show();
                    unknownsStage.setX(primaryStage.getX() + (primaryStage.getWidth() - unknownsStage.getWidth()) / 2 - 10);
                    unknownsStage.setY(primaryStage.getY() + (primaryStage.getHeight() - unknownsStage.getHeight()) / 2 - 5);
                    unknownsStage.requestFocus();
                    break;

                default:
            }
        } catch (IOException iOException) {
        }
    }

    public enum ReportTableTab {
        refMat, unknown, refMatTest
    }
}
