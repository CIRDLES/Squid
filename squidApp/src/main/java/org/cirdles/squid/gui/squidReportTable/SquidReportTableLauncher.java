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
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTableInterface;

import static org.cirdles.squid.gui.SquidUI.SQUID_LOGO_SANS_TEXT_URL;
import static org.cirdles.squid.gui.squidReportTable.SquidReportTableLauncher.ReportTableTab.unknownCustom;

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
            refMatStage.setScene(null);
            e.consume();
        });
        unknownsStage.setOnCloseRequest(e -> {
            unknownsStage.hide();
            unknownsStage.setScene(null);
            e.consume();
        });
    }

    public void launch(ReportTableTab tab) {
        launch(tab, null);
    }

    public void launch(ReportTableTab tab, SquidReportTableInterface table) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SquidReportTable.fxml"));
        SquidReportTableController.typeOfController = tab;
        SquidReportTableController.squidReportTable = table;

        Stage activeStage = refMatStage;
        if (tab.equals(unknownCustom)) {
            activeStage = unknownsStage;
            unknownsStage.setTitle("Squid 3 Report for " + SquidReportTableController.unknownSpot);
        }

        if (activeStage.isShowing()) {
            activeStage.close();
        }
        
        try {
            Scene scene = new Scene(loader.load());
            activeStage.setScene(scene);
            activeStage.show();
            activeStage.setX(primaryStage.getX() + (primaryStage.getWidth() - refMatStage.getWidth()) / 2 + 10);
            activeStage.setY(primaryStage.getY() + (primaryStage.getHeight() - refMatStage.getHeight()) / 2 + 5);
            activeStage.requestFocus();
        } catch (IOException iOException) {
            //TODO: add warning
        }
    }

    public enum ReportTableTab {
        refMatCustom, unknownCustom
    }
}
