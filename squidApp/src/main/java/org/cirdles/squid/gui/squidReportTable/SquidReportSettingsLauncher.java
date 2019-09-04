package org.cirdles.squid.gui.squidReportTable;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.cirdles.squid.dialogs.SquidMessageDialog;

import static org.cirdles.squid.gui.SquidUI.SQUID_LOGO_SANS_TEXT_URL;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;

public class SquidReportSettingsLauncher {
    private Stage primaryStage;
    public static Stage squidReportSettingsStage;
    public static Window squidReportSettingsWindow;

    public SquidReportSettingsLauncher(Stage primaryStage) {
        this.primaryStage = primaryStage;
        squidReportSettingsStage = new Stage();
        squidReportSettingsStage.setMinHeight(600);
        squidReportSettingsStage.setMinWidth(900);
        squidReportSettingsStage.getIcons().add(new Image(SQUID_LOGO_SANS_TEXT_URL));
        squidReportSettingsStage.setTitle("Squid Report Settings");

        squidReportSettingsStage.setOnCloseRequest(event -> {
            squidReportSettingsStage.hide();
            event.consume();
        });
    }

    public void launch() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SquidReportSettings.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            squidReportSettingsStage.setScene(scene);
            squidReportSettingsStage.show();
            squidReportSettingsStage.setX(primaryStage.getX() + (primaryStage.getWidth() - squidReportSettingsStage.getWidth()) / 2);
            squidReportSettingsStage.setY(primaryStage.getY() + (primaryStage.getHeight() - squidReportSettingsStage.getHeight()) / 2);
            squidReportSettingsWindow = squidReportSettingsStage.getScene().getWindow();
        } catch (Exception e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), primaryStageWindow);
        }

    }
}
