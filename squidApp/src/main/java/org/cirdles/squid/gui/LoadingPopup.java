package org.cirdles.squid.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LoadingPopup {
    private Stage primaryStage;
    private Stage popup;
    private ProgressIndicator progressIndicator;

    public LoadingPopup(Stage primaryStage) {
        progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        this.primaryStage = primaryStage;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoadingPopup.fxml"));
            Scene scene = new Scene(loader.load());
            popup = new Stage(StageStyle.UNDECORATED);
            popup.setScene(scene);
            popup.initModality(Modality.NONE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void show() {
        show(primaryStage);
    }

    public void show(Stage stage) {
        if (popup != null) {
            popup.show();
            popup.setX(stage.getX() + (stage.getWidth() - popup.getScene().getWidth()) / 2);
            popup.setY(stage.getY() + (stage.getHeight() - popup.getScene().getHeight()) / 2);
            popup.requestFocus();
        }
    }

    public void hide() {
        if (popup != null) {
            popup.hide();
        }
    }
}
