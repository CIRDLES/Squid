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
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.gui.dataViews.AbstractDataView;
import org.cirdles.squid.gui.dataViews.MassStationAuditViewForShrimp;
import org.cirdles.squid.shrimp.MassStationDetail;
import org.cirdles.squid.utilities.fileUtilities.PrawnFileUtilities;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class MassesAuditController implements Initializable {

    @FXML
    private ScrollPane massesAuditScrollPane;
    @FXML
    private VBox scrolledBox;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        displayMassStationsForReview();

    }

    private void displayMassStationsForReview() {
  
        int heightOfMassPlot = 150;

        // plotting mass variations
        // only show those with auto-centering on with count_time_sec > 0 in the run table at the mas station
        int widthOfView = squidProject.getPrawnFileRuns().size() * 25 + 350;

        int massCounter = 0;
        Map<Integer, MassStationDetail> mapOfIndexToMassStationDetails = PrawnFileUtilities.createMapOfIndexToMassStationDetails(squidProject.getPrawnFileRuns());
        for (Map.Entry<Integer, MassStationDetail> entry : mapOfIndexToMassStationDetails.entrySet()) {
            if (entry.getValue().autoCentered()) {
                AbstractDataView canvas
                        = new MassStationAuditViewForShrimp(new Rectangle(25, (massCounter * heightOfMassPlot) + 25, widthOfView, heightOfMassPlot),
                                entry.getValue().getMassStationLabel(),
                                entry.getValue().getMeasuredTrimMasses(),
                                entry.getValue().getTimesOfMeasuredTrimMasses(),
                                entry.getValue().getIndicesOfScansAtMeasurementTimes(),
                                entry.getValue().getIndicesOfRunsAtMeasurementTimes());

                scrolledBox.getChildren().add(canvas);
                GraphicsContext gc1 = canvas.getGraphicsContext2D();
                canvas.preparePanel();
                canvas.paint(gc1);

                massCounter++;

            }
        }
    }

}
