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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.cirdles.commons.util.ResourceExtractor;

/**
 *
 * @author James F. Bowring
 */
public class SquidUI extends Application {

    public static final String VERSION;
    public static final String RELEASE_DATE;

    public static Window primaryStageWindow;
    public static SquidAboutWindow squidAboutWindow;
    public static StringBuilder aboutWindowContent = new StringBuilder();

    public static ResourceExtractor squidResourceExtractor
            = new ResourceExtractor(SquidUI.class);

    static {
        String version = "version";
        String releaseDate = "date";

        // get version number and release date written by pom.xml
        Path resourcePath = squidResourceExtractor.extractResourceAsPath("version.txt");
        Charset charset = Charset.forName("US-ASCII");
        try (BufferedReader reader = Files.newBufferedReader(resourcePath, charset)) {
            String line = reader.readLine();
            if (line != null) {
                String[] versionText = line.split("=");
                version = versionText[1];
            }

            line = reader.readLine();
            if (line != null) {
                String[] versionDate = line.split("=");
                releaseDate = versionDate[1];
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        VERSION = version;
        RELEASE_DATE = releaseDate;

        // get content for about window
        resourcePath = squidResourceExtractor.extractResourceAsPath("/org/cirdles/squid/gui/content/aboutContent.txt");
        try (BufferedReader reader = Files.newBufferedReader(resourcePath, charset)) {
            aboutWindowContent = new StringBuilder();
            String thisLine;

            while ((thisLine = reader.readLine()) != null) {
                aboutWindowContent.append(thisLine);
            }

        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("SquidUIController.fxml"));

        Scene scene = new Scene(root);

        primaryStage.getIcons().add(new Image("org/cirdles/squid/gui/images/SquidLogoSansText.png"));

        primaryStage.setMinHeight(675.0);
        primaryStage.setMinWidth(935.0);
        primaryStage.setTitle("Squid 3.0");
        primaryStage.setScene(scene);
        primaryStage.show();
        // this produces non-null window after .show()
        this.primaryStageWindow = primaryStage.getScene().getWindow();

        primaryStage.setOnCloseRequest((WindowEvent e) -> {
            Platform.exit();
            System.exit(0);
        });

        squidAboutWindow = new SquidAboutWindow(primaryStage);
        squidAboutWindow.loadAboutWindow();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
