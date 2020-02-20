package org.cirdles.squid.gui;

import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoadingPopup {
    private Stage primaryStage;
    Alert alert;

    public LoadingPopup(Stage primaryStage) {
        this.primaryStage = primaryStage;
        setUpAlert();
    }

    public void show() {
        show(primaryStage);
    }

    private void setUpAlert() {
        alert = new Alert(Alert.AlertType.INFORMATION, "We're working on it");
        alert.setTitle("Loading");
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initStyle(StageStyle.DECORATED);
    }

    public void show(Stage stage) {
        try {
            Thread thread = new Thread(() -> {
            });
            thread.start();
            
            setUpAlert();
            alert.initOwner(stage);
            alert.show();

            thread.join();
        } catch (InterruptedException e) {

        }
    }

    public void hide() {
        alert.hide();
    }
}
