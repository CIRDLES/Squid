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

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import org.cirdles.topsoil.plot.JavaScriptPlot;
import org.cirdles.topsoil.plot.Plot;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.CONCORDIA_LINE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.ELLIPSES;
import org.cirdles.topsoil.plot.internal.SVGSaver;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class TopsoilPlotController implements Initializable {

    @FXML
    private HBox plotAndConfig;
    @FXML
    private ToolBar plotToolBar;
    @FXML
    private VBox vboxMaster;

    private AnchorPane anchorPane;

    public static Plot plot;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (plot instanceof JavaScriptPlot) {

            Node node = plot.displayAsNode();
            anchorPane = new AnchorPane(node);
            vboxMaster.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
            vboxMaster.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(PIXEL_OFFSET_FOR_MENU));

            JavaScriptPlot javaScriptPlot = (JavaScriptPlot) plot;

            Button saveToSVG = new Button("Save as SVG");
            saveToSVG.setOnAction(mouseEvent -> {
                new SVGSaver().save(javaScriptPlot.displayAsSVGDocument());
            });

            Button recenter = new Button("Re-center");
            recenter.setOnAction(mouseEvent -> {
                javaScriptPlot.recenter();
            });

            CheckBox ellipses = new CheckBox("Ellipses");
            ellipses.setOnAction(mouseEvent -> {
                boolean value = (boolean) plot.getProperties().get(ELLIPSES);
                plot.setProperty(ELLIPSES, !value);
            });

            CheckBox concordiaLine = new CheckBox("Concordia");
            concordiaLine.setSelected(true);
            concordiaLine.setOnAction(mouseEvent -> {
                boolean value = (boolean) plot.getProperties().get(CONCORDIA_LINE);
                plot.setProperty(CONCORDIA_LINE, !value);
            });
            
            Text loadingIndicator = new Text("Loading...");

            javaScriptPlot.getLoadFuture().thenRunAsync(() -> {
                loadingIndicator.visibleProperty().bind(
                        javaScriptPlot.getWebEngine().getLoadWorker()
                                .stateProperty().isEqualTo(Worker.State.RUNNING));
            },
                    Platform::runLater
            );

            plotToolBar.getItems().addAll(saveToSVG, recenter, ellipses, concordiaLine, loadingIndicator);

            plotAndConfig.getChildren().setAll(anchorPane);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);

            HBox.setHgrow(anchorPane, Priority.ALWAYS);
            VBox.setVgrow(plotAndConfig, Priority.ALWAYS);
        }
    }
}
