package org.cirdles.squid.gui.squidReportTable;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.cirdles.squid.dialogs.SquidMessageDialog;

import static org.cirdles.squid.gui.SquidUI.SQUID_LOGO_SANS_TEXT_URL;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;

public class SquidReportSettingsLauncher {
    private Stage primaryStage;
    private Stage stage;
    public SquidReportSettingsLauncher(Stage primaryStage) {
        this.primaryStage = primaryStage;
        stage = new Stage();
        stage.setMinHeight(600);
        stage.setMinWidth(900);
        stage.getIcons().add(new Image(SQUID_LOGO_SANS_TEXT_URL));
        stage.setTitle("");

        stage.setOnCloseRequest(event -> stage.hide());
    }

    public void launch() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SquidReportSettings.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
            stage.setX(primaryStage.getX() + (primaryStage.getWidth() - stage.getWidth()) / 2);
            stage.setY(primaryStage.getY() + (primaryStage.getHeight() - stage.getHeight()) / 2);
        } catch(Exception e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), primaryStageWindow);
        }

    }
}
