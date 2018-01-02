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
package org.cirdles.squid.gui.topsoil;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import org.cirdles.squid.gui.topsoil.AbstractTopsoilPlot;
import org.cirdles.topsoil.plot.JavaScriptPlot;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class TopsoilPlotController implements Initializable {
    
    public static AbstractTopsoilPlot topsoilPlot;

    /**
     * Set this field with an AbstractTopsoilPlot instance in advance of loading
     * this class.
     *
     * @param aTopsoilPlot the aTopsoilPlot to set
     */
    public static void setTopsoilPlot(AbstractTopsoilPlot aTopsoilPlot) {
        topsoilPlot = aTopsoilPlot;
    }
    
    @FXML
    private VBox vboxMaster;
    @FXML
    private AnchorPane plotAndConfig;
    @FXML
    private ToolBar plotToolBar;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        if (topsoilPlot.getPlot() instanceof JavaScriptPlot) {
            
            vboxMaster.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
            vboxMaster.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(PIXEL_OFFSET_FOR_MENU));
            
            Node topsoilPlotNode = topsoilPlot.getPlot().displayAsNode();
            
            plotAndConfig.getChildren().setAll(topsoilPlotNode);
            AnchorPane.setLeftAnchor(topsoilPlotNode, 0.0);
            AnchorPane.setRightAnchor(topsoilPlotNode, 0.0);
            AnchorPane.setTopAnchor(topsoilPlotNode, 0.0);
            AnchorPane.setBottomAnchor(topsoilPlotNode, 0.0);
            
            VBox.setVgrow(plotAndConfig, Priority.ALWAYS);

            plotToolBar.getItems().addAll(topsoilPlot.toolbarControlsFactory());
            plotToolBar.setPadding(Insets.EMPTY);
            
        }
    }
}
