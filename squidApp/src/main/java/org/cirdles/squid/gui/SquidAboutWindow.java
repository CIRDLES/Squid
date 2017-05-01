/*
 * Copyright 2017 CIRDLES.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.squid.gui;

import java.io.IOException;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author James F. Bowring
 */
public class SquidAboutWindow {

    private Stage primaryStage;
    private double xOffset = 0;
    private double yOffset = 0;

    private SquidAboutWindow() {
    }

    public SquidAboutWindow(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void loadAboutWindow() {
        try {
            Parent aboutPage = FXMLLoader.load(getClass().getResource("AboutSquid.fxml"));
            Scene aboutScene = new Scene(aboutPage, 450, 600);
            Stage aboutWindow = new Stage(StageStyle.UNDECORATED);
            aboutWindow.setResizable(false);
            aboutWindow.setScene(aboutScene);

            aboutWindow.requestFocus();
            aboutWindow.initOwner(primaryStage.getScene().getWindow());
            aboutWindow.initModality(Modality.NONE);

            aboutPage.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            aboutPage.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    aboutWindow.setX(event.getScreenX() - xOffset);
                    aboutWindow.setY(event.getScreenY() - yOffset);
                }
            });

            // Close window if main window gains focus.
            primaryStage.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    aboutWindow.close();
                }
            });

            aboutWindow.show();
        } catch (IOException iOException) {
        }
    }

}
