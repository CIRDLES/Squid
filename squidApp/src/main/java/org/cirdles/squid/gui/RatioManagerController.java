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

import java.net.URL;
import java.util.List;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.gui.dataViews.AbstractDataView;
import org.cirdles.squid.gui.dataViews.RawDataViewForShrimp;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class RatioManagerController implements Initializable {

    @FXML
    private AnchorPane scrolledAnchorPane;
    @FXML
    private Pane ratioManagerPane;
    @FXML
    private ScrollPane ratioManagerScrollPane;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        ratioManagerPane.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        ratioManagerPane.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(29));
        
        ratioManagerScrollPane.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        ratioManagerScrollPane.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(29));

        scrolledAnchorPane.setPrefWidth(5100);
        scrolledAnchorPane.setPrefHeight(1700);
        int massCounter = 0;
        for (Entry<String, List<List<Double>>> entry : squidProject.getMassTimeSeries().entrySet()) {
            AbstractDataView canvas = new RawDataViewForShrimp(new Rectangle(25, (massCounter * 150) + 25, 5000, 150), entry.getKey(), entry.getValue());
            scrolledAnchorPane.getChildren().add(canvas);
            GraphicsContext gc1 = canvas.getGraphicsContext2D();
            canvas.preparePanel();
            canvas.paint(gc1);
            
            massCounter ++;
        }

    }

}
