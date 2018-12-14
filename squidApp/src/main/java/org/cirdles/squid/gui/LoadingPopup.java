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
        window = null;
    }

    public void load() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoadingPopup.fxml"));
            Scene scene = new Scene(loader.load());
            Stage window = new Stage(StageStyle.UNDECORATED);
            this.window = window;
            window.setScene(scene);
            window.initModality(Modality.NONE);
        } catch (IOException iOException) {
        }
    }

    public void show() {
        load();
        window.initOwner(primaryStage.getScene().getWindow());
        window.show();
        window.setX(primaryStage.getX() + (primaryStage.getWidth() - window.getScene().getWidth()) / 2);
        window.setY(primaryStage.getY() + (primaryStage.getHeight() - window.getScene().getHeight()) / 2);
        window.requestFocus();
    }

    public void show(Stage stage) {
        load();
        window.initOwner(stage.getScene().getWindow());
        window.show();
        window.setX(stage.getX() + (stage.getWidth() - window.getScene().getWidth()) / 2);
        window.setY(stage.getY() + (stage.getHeight() - window.getScene().getHeight()) / 2);
        window.requestFocus();
    }

    public void hide() {
        if(window != null) {
            window.hide();
        }
    }
}
