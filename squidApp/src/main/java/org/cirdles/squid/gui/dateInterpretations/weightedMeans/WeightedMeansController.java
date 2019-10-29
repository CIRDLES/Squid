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
package org.cirdles.squid.gui.dateInterpretations.weightedMeans;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;

/**
 * FXML Controller class
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class WeightedMeansController implements Initializable {
    
    @FXML
    private VBox vboxMaster;
    @FXML
    private HBox headerHBox;
    @FXML
    private AnchorPane sampleTreeAnchorPane;

    private TreeView<WeightedMeanSampleTreeInterface> groupsTreeViewWeighteMeans = new TreeView<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // update 
        squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(false);

        groupsTreeViewWeighteMeans.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        groupsTreeViewWeighteMeans.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty()
                .subtract(PIXEL_OFFSET_FOR_MENU + headerHBox.getPrefHeight()));
    }

    private void showUnknownsWithOvercountCorrections() {

//        Map<String, List<ShrimpFractionExpressionInterface>> mapOfSpotsBySampleNames;
//        mapOfSpotsBySampleNames = squidProject.getTask().getMapOfUnknownsBySampleNames();
//
//        WeightedMeanSampleTreeInterface toolBarSampleType
//                = new CommonLeadSampleToolBar(
//                        Squid3Constants.SpotTypes.UNKNOWN.getPlotType(),
//                        mapOfSpotsBySampleNames.get(Squid3Constants.SpotTypes.UNKNOWN.getPlotType()));
//        TreeItem<CommonLeadSampleTreeInterface> rootItemSamples = new TreeItem<>(toolBarSampleType);
//
//        spotsTreeViewCommonLeadTools.setStyle("-fx-font-size: 12px;");
//        spotsTreeViewCommonLeadTools.setRoot(rootItemSamples);
//
//        rootItemSamples.setExpanded(true);
//        spotsTreeViewCommonLeadTools.setShowRoot(true);
//
//        // case of sample names chosen
//        if (mapOfSpotsBySampleNames.size() > 1) {
//            // task.getMapOfUnknownsBySampleNames restores this global set
//            mapOfSpotsBySampleNames.remove(Squid3Constants.SpotTypes.UNKNOWN.getPlotType());
//        }
//
//        for (Map.Entry<String, List<ShrimpFractionExpressionInterface>> entry : mapOfSpotsBySampleNames.entrySet()) {
//            CommonLeadSampleTreeInterface toolBarSampleName = new CommonLeadSampleToolBar(entry.getKey(), entry.getValue());
//            TreeItem<CommonLeadSampleTreeInterface> treeItemSampleInfo = new TreeItem<>(toolBarSampleName);
//
//            for (ShrimpFractionExpressionInterface spot : entry.getValue()) {
//                CommonLeadSampleTreeInterface treeItemSpotDisplay = new CommonLeadSpotDisplay(spot);
//                TreeItem<CommonLeadSampleTreeInterface> treeItemSpotInfo = new TreeItem<>(treeItemSpotDisplay);
//                toolBarSampleName.addSpotDependency(treeItemSpotDisplay);
//
//                treeItemSampleInfo.getChildren().add(treeItemSpotInfo);
//            }
//
//            toolBarSampleType.addSpotDependency(toolBarSampleName);
//            rootItemSamples.getChildren().add(treeItemSampleInfo);
//        }
//
//        sampleTreeAnchorPane.getChildren().clear();
//        sampleTreeAnchorPane.getChildren().add(spotsTreeViewCommonLeadTools);
    }

    private interface WeightedMeanSampleTreeInterface {

    }
    
    private class WeightedMeanSampleToolBar extends HBox implements WeightedMeanSampleTreeInterface {
        
    }
}
