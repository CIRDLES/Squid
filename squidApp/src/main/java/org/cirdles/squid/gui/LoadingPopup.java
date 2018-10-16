package org.cirdles.squid.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LoadingPopup {
    private Stage primaryStage;
    private Stage window;

    public LoadingPopup(Stage primaryStage) {
        this.primaryStage = primaryStage;
        load();
    }

    public void load() {
        try {
            Parent popup = FXMLLoader.load(getClass().getResource("LoadingPopup.fxml"));
            Scene scene = new Scene(popup, 436, 320.8);
            Stage window = new Stage(StageStyle.UNDECORATED);
            this.window = window;
            window.setScene(scene);
            window.initModality(Modality.NONE);
        } catch (IOException iOException) {
        }
    }

    public void show() {
        window.setX(primaryStage.getX() + (primaryStage.getWidth() - window.getScene().getWidth()) / 2);
        window.setY(primaryStage.getY() + (primaryStage.getHeight() - window.getScene().getHeight()) / 2);
        window.initOwner(primaryStage.getScene().getWindow());
        window.show();
        window.requestFocus();
    }

    public void show(Stage stage) {
        window.setX(stage.getX() + (stage.getWidth() - window.getScene().getWidth()) / 2);
        window.setY(stage.getY() + (stage.getHeight() - window.getScene().getHeight()) / 2);
        window.initOwner(stage.getScene().getWindow());
        window.show();
        window.requestFocus();
    }

    public void hide() {
        primaryStage.hide();
    }
}
