/*
 * Copyright 2019 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.gui.dateInterpretations.countCorrections;

import java.net.URL;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.cirdles.squid.constants.Squid3Constants;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.SPOT_TREEVIEW_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;

/**
 * FXML Controller class
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class CountCorrectionsController implements Initializable {

    @FXML
    private VBox vboxMaster;
    @FXML
    private AnchorPane sampleTreeAnchorPane;

    private TreeView<String> spotsTreeViewString = new TreeView<>();
    @FXML
    private HBox headerHBox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // update 
        squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(false);

        spotsTreeViewString.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        spotsTreeViewString.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty()
                .subtract(PIXEL_OFFSET_FOR_MENU + headerHBox.getPrefHeight()));

        showUnknowns();

    }

    private void showUnknowns() {

        spotsTreeViewString.setStyle(SPOT_TREEVIEW_CSS_STYLE_SPECS);

        Map<String, List<ShrimpFractionExpressionInterface>> mapOfSpotsBySampleNames;
        mapOfSpotsBySampleNames = squidProject.getTask().getMapOfUnknownsBySampleNames();
        // case of sample names chosen
        if (mapOfSpotsBySampleNames.size() > 1) {
            mapOfSpotsBySampleNames.remove(Squid3Constants.SpotTypes.UNKNOWN.getPlotType());
        }

        TreeItem<String> rootItemWM
                = new TreeItem<>(Squid3Constants.SpotTypes.UNKNOWN.getPlotType());
        spotsTreeViewString.setRoot(rootItemWM);

        spotsTreeViewString.setRoot(rootItemWM);

        rootItemWM.setExpanded(true);
        spotsTreeViewString.setShowRoot(true);

        for (Map.Entry<String, List<ShrimpFractionExpressionInterface>> entry : mapOfSpotsBySampleNames.entrySet()) {
            TreeItem<String> treeItemSample = new TreeItem<>(entry.getKey());
            for (ShrimpFractionExpressionInterface spot : entry.getValue()) {

                StringBuilder spotDataString = new StringBuilder();
                double[][] r204_206 = spot.getIsotopicRatioValuesByStringName("204/206");
                double[][] r204_206_207 = spot.getTaskExpressionsEvaluationsPerSpot()
                        .get(squidProject.getTask().getNamedExpressionsMap().get("CountCorrectionExpression204From207"));
                double[][] r204_206_208 = spot.getTaskExpressionsEvaluationsPerSpot()
                        .get(squidProject.getTask().getNamedExpressionsMap().get("CountCorrectionExpression204From208"));

                spotDataString.append(String.format("%1$-" + 24 + "s", spot.getFractionID()));
                spotDataString.append(String.format("%1$-" + 24 + "s", String.valueOf(r204_206[0][0])));
                spotDataString.append(String.format("%1$-" + 35 + "s", String.valueOf(r204_206[0][1])));
                spotDataString.append(String.format("%1$-" + 24 + "s", String.valueOf(r204_206_207[0][0])));
                spotDataString.append(String.format("%1$-" + 35 + "s", String.valueOf(r204_206_207[0][1])));
                spotDataString.append(String.format("%1$-" + 24 + "s", String.valueOf(r204_206_208[0][0])));
                spotDataString.append(String.format("%1$-" + 24 + "s", String.valueOf(r204_206_208[0][1])));

                Formatter fmt = new Formatter();
                fmt.format("% .15g", r204_206_208[0][1]);
                spotDataString.append(fmt);

                TreeItem<String> treeItemSpot = new TreeItem<>(spotDataString.toString());
                treeItemSample.getChildren().add(treeItemSpot);
            }
            rootItemWM.getChildren().add(treeItemSample);
        }

        sampleTreeAnchorPane.getChildren().clear();
        sampleTreeAnchorPane.getChildren().add(spotsTreeViewString);
    }
}
