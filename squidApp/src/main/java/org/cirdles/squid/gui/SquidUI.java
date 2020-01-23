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

import java.io.OutputStream;
import java.io.PrintStream;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.cirdles.squid.core.CalamariReportsEngine.CalamariReportFlavors;
import org.cirdles.squid.utilities.fileUtilities.CalamariFileUtilities;

import static org.cirdles.squid.gui.SquidUIController.squidProject;

/**
 * @author James F. Bowring
 */
public final class SquidUI extends Application {

    public static final String EXPRESSION_TOOLTIP_CSS_STYLE_SPECS
            = "-fx-background-color:cornsilk;-fx-border-width: 1;-fx-border-color: black;"
            + "-fx-border-radius: 0;-fx-text-fill: black;-fx-effect:none;-fx-font-size:11px;"
            + "-fx-font-weight:bold;-fx-font-family:'Monospaced';";
    public static final String SPOT_LIST_CSS_STYLE_SPECS
            = "-fx-font-size: 12px; -fx-font-weight: bold; -fx-font-family: 'Monospaced';";
    public static final String EXPRESSION_LIST_CSS_STYLE_SPECS
            = "-fx-font-size: 12px; -fx-font-weight: 600;-fx-font-family: 'SansSerif';-fx-fixed-cell-size: 20;";
    public static final String OPERATOR_IN_EXPRESSION_LIST_CSS_STYLE_SPECS
            = "-fx-font-weight: bold; -fx-font-family: 'Monospaced';";
    public static final String PRESENTATION_IN_EXPRESSION_LIST_CSS_STYLE_SPECS
            = "-fx-font-family: 'Monospaced';";
    public static final String PEEK_LIST_CSS_STYLE_SPECS
            = "-fx-font-size: 11px; -fx-font-weight: bold; -fx-font-family: 'Monospaced';";
    public static final String COLORPICKER_CSS_STYLE_SPECS
            = "-fx-font-size: 12px; -fx-font-family: 'Monospaced';";
    public static final String SPOT_TREEVIEW_CSS_STYLE_SPECS
            = "-fx-font-size: 12px; -fx-font-weight: bold; -fx-font-family: 'Monospaced';";

    public static final String SQUID_LOGO_SANS_TEXT_URL
            = "org/cirdles/squid/gui/images/SquidLogoSansBg.png";

    
    public static final String HEALTHY_URL = "org/cirdles/squid/gui/images/icon_checkmark.png";
    public static final Image HEALTHY = new Image(HEALTHY_URL);
    public static final String UNHEALTHY_URL = "org/cirdles/squid/gui/images/wrongx_icon.png";
    public static final Image UNHEALTHY = new Image(UNHEALTHY_URL);
    
    
    public static  final String HEALTHY_EXPRESSION_STYLE
            = "-fx-background-image:url('\"" + HEALTHY_URL
            + "\"');-fx-background-repeat: no-repeat;-fx-background-position: left center;"
            + " -fx-background-size: 16px 16px;";
    public static  final String UNHEALTHY_EXPRESSION_STYLE
            = "-fx-background-image:url('\"" + UNHEALTHY_URL
            + "\"');-fx-background-repeat: no-repeat;-fx-background-position: left center;"
            + " -fx-background-size: 16px 16px;";

    public static final int PIXEL_OFFSET_FOR_MENU = 38;
    public static Window primaryStageWindow;
    public static CalamariReportFlavors calamariReportFlavor;

    protected static SquidAboutWindow squidAboutWindow;
    protected static Stage primaryStage;
    protected static LoadingPopup loadingPopup;

    @Override
    public void start(Stage primaryStage) throws Exception {
        loadingPopup = new LoadingPopup(primaryStage);

        SquidUI.primaryStage = primaryStage;
        Parent root = new AnchorPane();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(SQUID_LOGO_SANS_TEXT_URL));
        updateStageTitle("");

        // this produces non-null window after .show()
        primaryStageWindow = primaryStage.getScene().getWindow();

        primaryStage.setOnCloseRequest((WindowEvent e) -> {
            Platform.exit();
            System.exit(0);
        });

        CalamariFileUtilities.initSampleParametersModels();

        // postpone loading to allow for stage creation and use in controller
        scene.setRoot(FXMLLoader.load(getClass().getResource("SquidUIController.fxml")));
        primaryStage.show();
        primaryStage.setMinHeight(scene.getHeight() + 15);
        primaryStage.setMinWidth(scene.getWidth());

        squidAboutWindow = new SquidAboutWindow(primaryStage);
    }

    public static void updateStageTitle(String fileName) {
        String fileSpec = "[Project File: NONE]";
        fileSpec = fileName.length() > 0 ? fileSpec.replace("NONE", fileName) : fileSpec;
        primaryStage.setTitle("Squid 3  " + fileSpec);
        SquidUIController.projectFileName = fileName;
    }

    public static void loadSpecsAndReduceReports() {
        loadingPopup.show();
        squidProject.getTask().setChanged(true);
        squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(false);
        loadingPopup.hide();
    }

    public static void loadSpecsAndReduceReports(Stage stage) {
        loadingPopup.show(stage);
        squidProject.getTask().setChanged(true);
        squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(false);
        loadingPopup.hide();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // arg[0] : -v[erbose]
        boolean verbose = false;
        if (args.length > 0) {
            verbose = args[0].startsWith("-v");
        }

        // http://patorjk.com/software/taag/#p=display&c=c%2B%2B&f=Varsity&t=Squid
        // Varsity font with defaults
        //    ______                      _        __    ______   
        //  .' ____ \                    (_)      |  ]  / ____ `. 
        //  | (___ \_|  .--. _  __   _   __   .--.| |   `'  __) | 
        //   _.____`. / /'`\' ][  | | | [  |/ /'`\' |   _  |__ '. 
        //  | \____) || \__/ |  | \_/ |, | || \__/  |  | \____) | 
        //   \______.' \__.; |  '.__.'_/[___]'.__.;__]  \______.' 
        //                 |__]                
        StringBuilder logo = new StringBuilder();
        logo.append("         ______                      _        __    ______   \n");
        logo.append("       .' ____ \\                    (_)      |  ]  / ____ `.\n");
        logo.append("       | (___ \\_|  .--. _  __   _   __   .--.| |   `'  __) |\n");
        logo.append("        _.____`. / /'`\\' ][  | | | [  |/ /'`\\' |   _  |__ '.\n");
        logo.append("       | \\____) || \\__/ |  | \\_/ |, | || \\__/  |  | \\____) |\n");
        logo.append("        \\______.' \\__.; |  '.__.'_/[___]'.__.;__]  \\______.'\n");
        logo.append("                      |__]                        \n");
        System.out.println((logo));

        // detect if running from jar file
        if (!verbose && (ClassLoader.getSystemResource("org/cirdles/squid/gui/SquidUI.class").toExternalForm().startsWith("jar"))) {
            System.out.println(
                    "Running Squid from Jar file ... suppressing terminal output.\n"
                    + "\t use '-verbose' argument after jar file name to enable terminal output.");
            System.setOut(new PrintStream(new OutputStream() {
                public void write(int b) {
                    // NO-OP
                }
            }));
            System.setErr(new PrintStream(new OutputStream() {
                public void write(int b) {
                    // NO-OP
                }
            }));

        }

        launch(args);
    }
}
