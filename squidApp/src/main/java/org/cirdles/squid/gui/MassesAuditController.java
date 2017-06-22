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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.gui.dataViews.AbstractDataView;
import org.cirdles.squid.gui.dataViews.MassStationAuditViewForShrimp;
import org.cirdles.squid.gui.dataViews.SummaryRawDataViewForShrimp;
import org.cirdles.squid.shrimp.MassStationDetail;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class MassesAuditController implements Initializable {

    @FXML
    private AnchorPane massesAnchorPane;
    @FXML
    private ScrollPane massesAuditScrollPane;
    @FXML
    private AnchorPane scrolledAnchorPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        massesAnchorPane.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        massesAnchorPane.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(30));

        massesAuditScrollPane.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        massesAuditScrollPane.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(30));

        displayMassStationsForReview();

    }

    private void displayMassStationsForReview() {
        // assume user has got the file healthy - same number of mass stations throughout - check later
        // let's present the stations from the first run table
        
        int heightOfMassPlot = 150;

        // original code for plotting mass variations
        // note must change to only show those with auto-centering on with count_time_sec > 0 in the run table at the mas station
        int widthOfView = squidProject.getPrawnFileRuns().size() * 25 + 350;
        scrolledAnchorPane.setPrefWidth(widthOfView + 150);

//        scrolledAnchorPane.setPrefHeight(3700);// needs to be based on number of mass stations        
//        AbstractDataView canvas = new SummaryRawDataViewForShrimp(
//                new Rectangle(25, 25, widthOfView, 3500), 
//                squidProject.getMapOfIndexToMassStationDetails());
//        scrolledAnchorPane.getChildren().add(canvas);
//                GraphicsContext gc1 = canvas.getGraphicsContext2D();
//                canvas.preparePanel();
//                canvas.paint(gc1);
        int massCounter = 0;
        for (Map.Entry<Integer, MassStationDetail> entry : squidProject.getMapOfIndexToMassStationDetails().entrySet()) {
            if (entry.getValue().autoCentered()) {
                AbstractDataView canvas
                        = new MassStationAuditViewForShrimp(new Rectangle(25, (massCounter * heightOfMassPlot) + 25, widthOfView, heightOfMassPlot),
                                entry.getValue().getMassStationLabel(),
                                entry.getValue().getMeasuredTrimMasses(),
                                entry.getValue().getTimesOfMeasuredTrimMasses(),
                                entry.getValue().getIndicesOfScansAtMeasurementTimes(),
                                entry.getValue().getIndicesOfRunsAtMeasurementTimes());

                scrolledAnchorPane.getChildren().add(canvas);
                GraphicsContext gc1 = canvas.getGraphicsContext2D();
                canvas.preparePanel();
                canvas.paint(gc1);

                massCounter++;

            }
//            else {
//                List<Double> empty = new ArrayList<>();
//                empty.add(0.0);
//                AbstractDataView canvas
//                        = new MassStationAuditViewForShrimp(new Rectangle(25, (massCounter * 150) + 25, widthOfView, 150),
//                                entry.getValue().getMassStationLabel() + "\n > " + entry.getValue().getIsotopeLabel(),
//                                empty,
//                                empty);
//                scrolledAnchorPane.getChildren().add(canvas);
//                GraphicsContext gc1 = canvas.getGraphicsContext2D();
//                canvas.preparePanel();
//                canvas.paint(gc1);
//            }

//            massCounter++;
        }

        scrolledAnchorPane.setPrefHeight((massCounter * heightOfMassPlot) + 50);

    }

}
