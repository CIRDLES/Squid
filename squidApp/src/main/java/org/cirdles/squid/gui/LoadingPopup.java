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
        alert.showingProperty().addListener((ob, oldVal, newVal) ->{
            if(newVal) {
                System.out.println("Loading Popup: is showing");
            } else {
                System.out.println("Loading Popup: isn't showing");
            }
        });
        alert.setOnShown(val -> System.out.println("Loading Popup: shown"));
        alert.setOnHidden(val -> System.out.println("Loading Popup: hidden"));
    }

    public void show(Stage stage) {
        setUpAlert();
        alert.initOwner(stage);
        alert.show();
    }

    public void hide() {
        alert.hide();
    }
}
