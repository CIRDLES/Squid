/*
 * Copyright 2017 James F. Bowring and CIRDLES.org.
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 *
 * @author James F. Bowring
 */
public final class SquidUI extends Application {
    public static final String EXPRESSION_TOOLTIP_CSS_STYLE_SPECS = "-fx-background-color:cornsilk;-fx-border-width: 1;-fx-border-color: black;-fx-border-radius: 0;-fx-text-fill: black;-fx-effect:none;-fx-font-size:11px;-fx-font-weight:bold;-fx-font-family:'Courier New';";
    public static final String SPOT_LIST_CSS_STYLE_SPECS = "-fx-font-size: 12px; -fx-font-weight: bold; -fx-font-family: 'Courier New';";
    public static final String EXPRESSION_LIST_CSS_STYLE_SPECS = "-fx-font-size: 12px; -fx-font-weight: bold; -fx-font-family: 'Courier New';-fx-fixed-cell-size: 20";
    public static final String OPERATOR_IN_EXPRESSION_LIST_CSS_STYLE_SPECS = "-fx-font-weight: bold; -fx-font-family: 'Courier New';";
    public static final String PRESENTATION_IN_EXPRESSION_LIST_CSS_STYLE_SPECS = "-fx-font-family: 'Courier New';";
    public static final String PEEK_LIST_CSS_STYLE_SPECS = "-fx-font-size: 11px; -fx-font-weight: bold; -fx-font-family: 'Courier New';";
    public static final String COLORPICKER_CSS_STYLE_SPECS = "-fx-font-size: 8px; -fx-font-family: 'Courier New';";
    public static final String SQUID_LOGO_SANS_TEXT_URL = "org/cirdles/squid/gui/images/SquidLogoSansBg.png";
            
    public static final String SPOT_TREEVIEW_CSS_STYLE_SPECS = "-fx-font-size: 10px; -fx-font-weight: bold; -fx-font-family: 'Courier New';";

    
    public static final int PIXEL_OFFSET_FOR_MENU = 38;
    public static Window primaryStageWindow;
    public static org.cirdles.squid.core.CalamariReportsEngine.CalamariReportFlavors calamariReportFlavor;
    
    protected static SquidAboutWindow squidAboutWindow;
    protected static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        this.primaryStage = primaryStage;
        Parent root = new AnchorPane();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(SQUID_LOGO_SANS_TEXT_URL));
        primaryStage.setTitle("Squid 3.0 pre-release");

        // this produces non-null window after .show()
        primaryStageWindow = primaryStage.getScene().getWindow();

        primaryStage.setOnCloseRequest((WindowEvent e) -> {
            Platform.exit();
            System.exit(0);
        });

        // postpone loading to allow for stage creation and use in controller
        scene.setRoot(FXMLLoader.load(getClass().getResource("SquidUIController.fxml")));
        primaryStage.show();
        primaryStage.setMinHeight(scene.getHeight() + 15);
        primaryStage.setMinWidth(scene.getWidth());

        squidAboutWindow = new SquidAboutWindow(primaryStage);
    }
    
    public static void updateStageTitle(String title){
        primaryStage.setTitle(title);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
