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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import org.cirdles.topsoil.app.plot.TopsoilPlotType;
import org.cirdles.topsoil.plot.JavaScriptPlot;
import org.cirdles.topsoil.plot.Plot;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.ELLIPSES;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.TITLE;
import org.cirdles.topsoil.plot.internal.SVGSaver;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class TopsoilPlot implements Initializable {

    private AnchorPane anchorPane;
    @FXML
    private ToolBar plotToolBar;

    private static final String X = "x";
    private static final String SIGMA_X = "sigma_x";
    private static final String Y = "y";
    private static final String SIGMA_Y = "sigma_y";
    private static final String RHO = "rho";

    // x, y, sigma x, sigma y, rho
    private static final double[][] DATA = new double[][]{
        {0.0722539075, 0.0110295656, 0.0002049758, 0.0000063126, 0.5365532874},
        {0.0721971452, 0.0110309854, 0.0001783027, 0.0000056173, 0.5325448483},
        {0.0721480905, 0.0110333887, 0.0001262722, 0.0000053814, 0.5693849155},
        {0.0720208987, 0.0110278685, 0.0001041118, 0.0000051695, 0.6034598793},
        {0.0722006985, 0.0110287224, 0.0001150679, 0.0000053550, 0.6488140173},
        {0.0721043666, 0.0110269651, 0.0001536438, 0.0000055438, 0.4514464090},
        {0.0721563039, 0.0110282194, 0.0001241486, 0.0000054189, 0.5407720667},
        {0.0721973299, 0.0110274879, 0.0001224165, 0.0000055660, 0.5557499444},
        {0.0721451656, 0.0110281849, 0.0001461117, 0.0000054048, 0.5309378161},
        {0.0720654237, 0.0110247729, 0.0001547497, 0.0000053235, 0.2337854029},
        {0.0721799174, 0.0110318201, 0.0001485404, 0.0000056511, 0.5177944463},
        {0.0721826355, 0.0110283902, 0.0001377158, 0.0000056126, 0.5953348385},
        {0.0720275042, 0.0110278402, 0.0001875497, 0.0000058909, 0.5274591815},
        {0.0721360819, 0.0110276418, 0.0001252055, 0.0000054561, 0.5760966585}
    };
    @FXML
    private HBox plotAndConfig;
    @FXML
    private VBox vboxMaster;
    private Plot plot;
    private String plotTitle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        List<Map<String, Object>> myData = new ArrayList<>();
        for (int i = 0; i < DATA.length; i++) {
            Map<String, Object> datum = new HashMap<>();
            myData.add(datum);
            datum.put(X, DATA[i][0]);
            datum.put(SIGMA_X, DATA[i][2]);
            datum.put(Y, DATA[i][1]);
            datum.put(SIGMA_Y, DATA[i][3]);
            datum.put(RHO, DATA[i][4]);
        }

        plot = TopsoilPlotType.BASE_PLOT.getPlot();
        plot.setData(myData);
        plot.getProperties().put(TITLE, "Squid Test Plot");

        Node node = plot.displayAsNode();
        anchorPane = new AnchorPane(node);
        vboxMaster.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        vboxMaster.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(PIXEL_OFFSET_FOR_MENU));

        if (plot instanceof JavaScriptPlot) {
            JavaScriptPlot javaScriptPlot = (JavaScriptPlot) plot;

            Button saveToSVG = new Button("Save as SVG");
            saveToSVG.setOnAction(mouseEvent -> {
                new SVGSaver().save(javaScriptPlot.displayAsSVGDocument());
            });

            Button recenter = new Button("Re-center");
            recenter.setOnAction(mouseEvent -> {
                javaScriptPlot.recenter();
            });

            Button ellipses = new Button("Ellipses");
            ellipses.setOnAction(mouseEvent -> {
                boolean value = (boolean)plot.getProperties().get(ELLIPSES);
                plot.setProperty(ELLIPSES, !value);
            });

            Text loadingIndicator = new Text("Loading...");

            javaScriptPlot.getLoadFuture().thenRunAsync(() -> {
                loadingIndicator.visibleProperty().bind(
                        javaScriptPlot.getWebEngine().getLoadWorker()
                                .stateProperty().isEqualTo(Worker.State.RUNNING));
            },
                    Platform::runLater
            );

            plotToolBar.getItems().addAll(saveToSVG, recenter, ellipses,  loadingIndicator);
        }

        plotAndConfig.getChildren().setAll(anchorPane);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);

        HBox.setHgrow(anchorPane, Priority.ALWAYS);
        VBox.setVgrow(plotAndConfig, Priority.ALWAYS);
    }

    /**
     * @param plotTitle the plotTitle to set
     */
    public void setPlotTitle(String plotTitle) {
        this.plotTitle = plotTitle;
    }

}
