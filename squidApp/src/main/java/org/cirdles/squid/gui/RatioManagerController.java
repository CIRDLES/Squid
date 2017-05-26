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
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
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
    private AnchorPane anchorPane;
    @FXML
    private AnchorPane scrolledAnchorPane;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        AbstractDataView canvas = new RawDataViewForShrimp(new Rectangle(25, 25, 5000, 150), squidProject);
        scrolledAnchorPane.setPrefWidth(5100);
        scrolledAnchorPane.getChildren().add(canvas);
        GraphicsContext gc1 = canvas.getGraphicsContext2D();
        canvas.preparePanel();
        canvas.paint(gc1);


//        AbstractDataView canvas2 = new RawDataViewForShrimp(new Rectangle(50, 100, 800, 100), squidProject);
//        anchorPane.getChildren().add(canvas2);
//        GraphicsContext gc2 = canvas2.getGraphicsContext2D();
//        canvas2.preparePanel();
//        canvas2.paint(gc2);

    }

 
}
