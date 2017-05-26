/*
 * Copyright 2017 cirdles.org.
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

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 *
 * @author James F. Bowring
 */
public final class SquidUI extends Application {

    protected static Window primaryStageWindow;
    protected static SquidAboutWindow squidAboutWindow;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("SquidUIController.fxml"));

        Scene scene = new Scene(root);

        primaryStage.getIcons().add(new Image("org/cirdles/squid/gui/images/SquidLogoSansText.png"));

        primaryStage.setMinHeight(650.0);
        primaryStage.setMinWidth(925.0);
        primaryStage.setTitle("Squid 3.0");
        primaryStage.setScene(scene);
        primaryStage.show();
        // this produces non-null window after .show()
        primaryStageWindow = primaryStage.getScene().getWindow();

        primaryStage.setOnCloseRequest((WindowEvent e) -> {
            Platform.exit();
            System.exit(0);
        });

        squidAboutWindow = new SquidAboutWindow(primaryStage);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
