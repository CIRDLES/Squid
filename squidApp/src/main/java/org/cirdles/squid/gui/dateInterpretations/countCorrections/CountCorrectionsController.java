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
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.gui.SquidUI;
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

//    private TreeView<String> spotsTreeViewString = new TreeView<>();
    private TreeView<TextFlow> spotsTreeViewTextFlow = new TreeView<>();
    @FXML
    private HBox headerHBox;
    @FXML
    private RadioButton correctionNoneRB;
    @FXML
    private ToggleGroup correctionsToggleGroup;
    @FXML
    private RadioButton correction207RB;
    @FXML
    private RadioButton correction208RB;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // update 
        squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(false);

        spotsTreeViewTextFlow.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        spotsTreeViewTextFlow.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty()
                .subtract(PIXEL_OFFSET_FOR_MENU + headerHBox.getPrefHeight()));

        switch (squidProject.getTask().getOvercountCorrectionType()) {
            case NONE:
                correctionNoneRB.setSelected(true);
                break;
            case FR_207:
                correction207RB.setSelected(true);
                break;
            case FR_208:
                correction208RB.setSelected(true);

        }
        showUnknowns();

    }

    private void showUnknowns() {

        Map<String, List<ShrimpFractionExpressionInterface>> mapOfSpotsBySampleNames;
        mapOfSpotsBySampleNames = squidProject.getTask().getMapOfUnknownsBySampleNames();
        // case of sample names chosen
        if (mapOfSpotsBySampleNames.size() > 1) {
            mapOfSpotsBySampleNames.remove(Squid3Constants.SpotTypes.UNKNOWN.getPlotType());
        }

        TextFlow textFlowSampleType = new TextFlow();
        Text textUnknown = new Text(Squid3Constants.SpotTypes.UNKNOWN.getPlotType());
        textUnknown.setFont(Font.font("Monospaced", FontWeight.BOLD, 10));
        textFlowSampleType.getChildren().add(textUnknown);
        TreeItem<TextFlow> rootItemSamples = new TreeItem<>(textFlowSampleType);

        spotsTreeViewTextFlow.setStyle("-fx-font-size: 10px;");
        spotsTreeViewTextFlow.setRoot(rootItemSamples);

        rootItemSamples.setExpanded(true);
        spotsTreeViewTextFlow.setShowRoot(true);

        for (Map.Entry<String, List<ShrimpFractionExpressionInterface>> entry : mapOfSpotsBySampleNames.entrySet()) {
//                // experimental formatting
////                Formatter fmt = new Formatter();
////                fmt.format("% .15g", r204_206_208[0][1]);
////                spotDataString.append(fmt);

            TextFlow textFlowSampleInfo = new TextFlow();
            Text textSample = new Text(entry.getKey());
            textSample.setFont(Font.font("Monospaced", FontWeight.BOLD, 10));
            textFlowSampleInfo.getChildren().add(textSample);
            TreeItem<TextFlow> treeItemSampleInfo = new TreeItem<>(textFlowSampleInfo);
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

                TextFlow textFlowI = new TextFlow();

                Text textSampleName = new Text(String.format("%1$-" + 24 + "s", spot.getFractionID()));
                textSampleName.setFont(Font.font("Monospaced", FontWeight.BOLD, 10));
                textFlowI.getChildren().add(textSampleName);

                Text textNoneValue = new Text(String.format("%1$-" + 24 + "s", String.valueOf(r204_206[0][0])));
                textNoneValue.setFont(Font.font("Monospaced", correctionNoneRB.isSelected() ? FontWeight.BOLD : FontWeight.THIN, 10));
                textFlowI.getChildren().add(textNoneValue);

                Text textNoneUnct = new Text(String.format("%1$-" + 35 + "s", String.valueOf(r204_206[0][1])));
                textNoneUnct.setFont(Font.font("Monospaced", correctionNoneRB.isSelected() ? FontWeight.BOLD : FontWeight.THIN, 10));
                textFlowI.getChildren().add(textNoneUnct);

                Text text207Value = new Text(String.format("%1$-" + 24 + "s", String.valueOf(r204_206_207[0][0])));
                text207Value.setFont(Font.font("Monospaced", correction207RB.isSelected() ? FontWeight.BOLD : FontWeight.THIN, 10));
                textFlowI.getChildren().add(text207Value);

                Text text207Unct = new Text(String.format("%1$-" + 35 + "s", String.valueOf(r204_206_207[0][1])));
                text207Unct.setFont(Font.font("Monospaced", correction207RB.isSelected() ? FontWeight.BOLD : FontWeight.THIN, 10));
                textFlowI.getChildren().add(text207Unct);
                
                Text text208Value = new Text(String.format("%1$-" + 24 + "s", String.valueOf(r204_206_207[0][0])));
                text208Value.setFont(Font.font("Monospaced", correction208RB.isSelected() ? FontWeight.BOLD : FontWeight.THIN, 10));
                textFlowI.getChildren().add(text208Value);

                Text text208Unct = new Text(String.format("%1$-" + 35 + "s", String.valueOf(r204_206_207[0][1])));
                text208Unct.setFont(Font.font("Monospaced", correction208RB.isSelected() ? FontWeight.BOLD : FontWeight.THIN, 10));
                textFlowI.getChildren().add(text208Unct);


                TreeItem<TextFlow> treeItemSampleI = new TreeItem<>(textFlowI);
                treeItemSampleInfo.getChildren().add(treeItemSampleI);
            }
            rootItemSamples.getChildren().add(treeItemSampleInfo);
        }

        sampleTreeAnchorPane.getChildren().clear();
        sampleTreeAnchorPane.getChildren().add(spotsTreeViewTextFlow);
    }

    @FXML
    private void correctionNoneAction(ActionEvent event) {
        squidProject.getTask().setOvercountCorrectionType(Squid3Constants.OvercountCorrectionTypes.NONE);
        showUnknowns();
    }

    @FXML
    private void correction207Action(ActionEvent event) {
        squidProject.getTask().setOvercountCorrectionType(Squid3Constants.OvercountCorrectionTypes.FR_207);
        showUnknowns();
    }

    @FXML
    private void correction208Action(ActionEvent event) {
        squidProject.getTask().setOvercountCorrectionType(Squid3Constants.OvercountCorrectionTypes.FR_208);
        showUnknowns();
    }
}
