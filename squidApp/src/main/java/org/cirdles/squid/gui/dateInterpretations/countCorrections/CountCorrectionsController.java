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

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.gui.SquidUIController;
import org.cirdles.squid.gui.dialogs.SquidMessageDialog;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import org.cirdles.squid.tasks.taskUtilities.OvercountCorrection;

import java.net.URL;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static org.cirdles.squid.constants.Squid3Constants.ABS_UNCERTAINTY_DIRECTIVE;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.*;

/**
 * FXML Controller class
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class CountCorrectionsController implements Initializable {

    private static final int fontSize = 12;
    private final TreeView<TextFlow> spotsTreeViewTextFlow = new TreeView<>();
    public Label customSwapLabel;
    public RadioButton customSWAPRB;
    @FXML
    private VBox vboxMaster;
    @FXML
    private AnchorPane sampleTreeAnchorPane;
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
    @FXML
    private Label biweight207Label;
    @FXML
    private Label biweight208Label;
    @FXML
    private Button returnToCommonLeadButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (squidProject.getTask().getOvercountCorrectionType().equals(Squid3Constants.OvercountCorrectionTypes.FR_Custom)) {
            try {
                OvercountCorrection.correctionCustom(squidProject.getTask());
            } catch (SquidException e) {
//                    throw new RuntimeException(e);
            }
        }
        // update 
        try {
            squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(false);
        } catch (SquidException squidException) {
            SquidMessageDialog.showWarningDialog(squidException.getMessage(), primaryStageWindow);
        }

        spotsTreeViewTextFlow.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        spotsTreeViewTextFlow.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty()
                .subtract(PIXEL_OFFSET_FOR_MENU + headerHBox.getPrefHeight()));

        ExpressionTreeInterface customExpression = squidProject.getTask().getNamedExpressionsMap().get(SWAP_CUSTOM_CORRECTION_204);
        if ((customExpression == null) || !customExpression.isValueModel()) {
            customSWAPRB.setDisable(true);
            squidProject.getTask().setOvercountCorrectionType(Squid3Constants.OvercountCorrectionTypes.NONE);
        }

        switch (squidProject.getTask().getOvercountCorrectionType()) {
            case NONE:
                correctionNoneRB.setSelected(true);
                break;
            case FR_207:
                correction207RB.setSelected(true);
                break;
            case FR_208:
                correction208RB.setSelected(true);
                break;
            case FR_Custom:
                customSWAPRB.setSelected(true);
        }

        setUpHeader();
        showUnknownsWithOvercountCorrections();
    }

    public void setUpHeader() {
        SpotSummaryDetails spotSummaryDetails
                = squidProject.getTask().getTaskExpressionsEvaluationsPerSpotSet().get(BIWT_204_OVR_CTS_FROM_207);
        double biWeight = spotSummaryDetails.getValues()[0][0];
        double conf95 = spotSummaryDetails.getValues()[0][2];

        Formatter formatter = new Formatter();
        formatter.format("%5.5f", biWeight);
        formatter.format(" " + ABS_UNCERTAINTY_DIRECTIVE + "%2.5f", conf95).toString();

        biweight207Label.setText("biWgt 204 ovrCnts:  " + formatter);

        spotSummaryDetails
                = squidProject.getTask().getTaskExpressionsEvaluationsPerSpotSet().get(BIWT_204_OVR_CTS_FROM_208);
        biWeight = spotSummaryDetails.getValues()[0][0];
        conf95 = spotSummaryDetails.getValues()[0][2];

        formatter = new Formatter();
        formatter.format("%5.5f", biWeight);
        formatter.format(" " + ABS_UNCERTAINTY_DIRECTIVE + "%2.5f", conf95).toString();

        biweight208Label.setText("biWgt 204 ovrCnts:  " + formatter);

        returnToCommonLeadButton.setStyle("-fx-font-size: 12px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
    }

    private void showUnknownsWithOvercountCorrections() {

        Map<String, List<ShrimpFractionExpressionInterface>> mapOfSpotsBySampleNames;
        mapOfSpotsBySampleNames = squidProject.getTask().getMapOfUnknownsBySampleNames();
        // case of sample names chosen
        if (mapOfSpotsBySampleNames.size() > 1) {
            mapOfSpotsBySampleNames.remove(Squid3Constants.SpotTypes.UNKNOWN.getSpotTypeName());
        }

        TextFlow textFlowSampleType = new TextFlow();
        Text textUnknown = new Text(Squid3Constants.SpotTypes.UNKNOWN.getSpotTypeName());
        textUnknown.setFont(Font.font("Monospaced", FontWeight.BOLD, fontSize));
        textFlowSampleType.getChildren().add(textUnknown);
        TreeItem<TextFlow> rootItemSamples = new TreeItem<>(textFlowSampleType);

        spotsTreeViewTextFlow.setStyle("-fx-font-size: 12px;");
        spotsTreeViewTextFlow.setRoot(rootItemSamples);

        rootItemSamples.setExpanded(true);
        spotsTreeViewTextFlow.setShowRoot(true);

        for (Map.Entry<String, List<ShrimpFractionExpressionInterface>> entry : mapOfSpotsBySampleNames.entrySet()) {
            TextFlow textFlowSampleInfo = new TextFlow();
            Text textSample = new Text(entry.getKey());
            textSample.setFont(Font.font("Monospaced", FontWeight.BOLD, fontSize));
            textFlowSampleInfo.getChildren().add(textSample);
            TreeItem<TextFlow> treeItemSampleInfo = new TreeItem<>(textFlowSampleInfo);
            for (ShrimpFractionExpressionInterface spot : entry.getValue()) {

                double[][] r204_206 = spot.getOriginalIsotopicRatioValuesByStringName("204/206");
                double[][] r204_206_207 = spot.getTaskExpressionsEvaluationsPerSpot()
                        .get(squidProject.getTask().getNamedExpressionsMap().get("SWAPCountCorrectionExpression204From207"));
                double[][] r204_206_208 = spot.getTaskExpressionsEvaluationsPerSpot()
                        .get(squidProject.getTask().getNamedExpressionsMap().get("SWAPCountCorrectionExpression204From208"));
                double[][] custom204 = new double[r204_206_208.length][r204_206_208[0].length];
                ExpressionTreeInterface customExp = squidProject.getTask().getNamedExpressionsMap().get(SWAP_CUSTOM_CORRECTION_204);
                if ((null != customExp) && customExp.isValueModel()) {
                    custom204 = spot.getTaskExpressionsEvaluationsPerSpot()
                            .get(squidProject.getTask().getNamedExpressionsMap().get(SWAP_CUSTOM_CORRECTION_204));
                }

                TextFlow textFlowI = new TextFlow();

                Text textSampleName = new Text(String.format("%1$-" + 23 + "s", spot.getFractionID()));
                textSampleName.setFont(Font.font("Monospaced", FontWeight.BOLD, fontSize));
                textFlowI.getChildren().add(textSampleName);

                // no correction
                Formatter formatter = new Formatter();
                formatter.format("% 12.6E   % 12.3f         ", r204_206[0][0], StrictMath.abs(r204_206[0][1] / r204_206[0][0] * 100.0));
                Text textNone = new Text(formatter.toString());
                textNone.setFont(Font.font("Monospaced", correctionNoneRB.isSelected() ? FontWeight.BOLD : FontWeight.THIN, fontSize));
                textFlowI.getChildren().add(textNone);

                // 207 correction
                formatter = new Formatter();
                formatter.format("% 12.6E   % 12.3f         ", r204_206_207[0][0], StrictMath.abs(r204_206_207[0][1] / r204_206_207[0][0] * 100.0));
                Text text207 = new Text(formatter.toString());
                text207.setFont(Font.font("Monospaced", correction207RB.isSelected() ? FontWeight.BOLD : FontWeight.THIN, fontSize));
                textFlowI.getChildren().add(text207);

                // 208 correction
                formatter = new Formatter();
                formatter.format("% 12.6E   % 12.3f         ", r204_206_208[0][0], StrictMath.abs(r204_206_208[0][1] / r204_206_208[0][0] * 100.0));
                Text text208 = new Text(formatter.toString());
                text208.setFont(Font.font("Monospaced", correction208RB.isSelected() ? FontWeight.BOLD : FontWeight.THIN, fontSize));
                textFlowI.getChildren().add(text208);

                // custom correction
                formatter = new Formatter();
                formatter.format("% 12.6E   % 12.3f         ", custom204[0][0], StrictMath.abs(custom204[0][1] / custom204[0][0] * 100.0));
                Text textCustom = new Text(formatter.toString());
                textCustom.setFont(Font.font("Monospaced", customSWAPRB.isSelected() ? FontWeight.BOLD : FontWeight.THIN, fontSize));
                textFlowI.getChildren().add(textCustom);

                TreeItem<TextFlow> treeItemSampleI = new TreeItem<>(textFlowI);
                treeItemSampleInfo.getChildren().add(treeItemSampleI);
            }
            rootItemSamples.getChildren().add(treeItemSampleInfo);
        }

        sampleTreeAnchorPane.getChildren().clear();
        sampleTreeAnchorPane.getChildren().add(spotsTreeViewTextFlow);
    }

    private void updateColumnBold(boolean isOrig, boolean is207, boolean is208, boolean isCustom) {
        TreeItem<TextFlow> rootItemSamples = spotsTreeViewTextFlow.getRoot();
        for (TreeItem<TextFlow> treeItemSampleI : rootItemSamples.getChildren()) {
            List<TreeItem<TextFlow>> treeSampleItems = treeItemSampleI.getChildren();
            for (TreeItem<TextFlow> treeSpotItem : treeSampleItems) {
                TextFlow textFlow = treeSpotItem.getValue();
                Text text = (Text) textFlow.getChildren().get(1);
                text.setFont(Font.font("Monospaced", isOrig ? FontWeight.BOLD : FontWeight.THIN, fontSize));
                text = (Text) textFlow.getChildren().get(2);
                text.setFont(Font.font("Monospaced", is207 ? FontWeight.BOLD : FontWeight.THIN, fontSize));
                text = (Text) textFlow.getChildren().get(3);
                text.setFont(Font.font("Monospaced", is208 ? FontWeight.BOLD : FontWeight.THIN, fontSize));
                text = (Text) textFlow.getChildren().get(4);
                text.setFont(Font.font("Monospaced", isCustom ? FontWeight.BOLD : FontWeight.THIN, fontSize));
            }
        }
    }

    @FXML
    private void correctionNoneAction() throws SquidException {
        OvercountCorrection.correctionNone(squidProject.getTask());
        updateColumnBold(true, false, false, false);
    }

    @FXML
    private void correction207Action() throws SquidException {
        OvercountCorrection.correction207(squidProject.getTask());
        updateColumnBold(false, true, false, false);
    }

    @FXML
    private void correction208Action() throws SquidException {
        OvercountCorrection.correction208(squidProject.getTask());
        updateColumnBold(false, false, true, false);
    }

    @FXML
    private void customCorrectionAction() throws SquidException {
        OvercountCorrection.correctionCustom(squidProject.getTask());
        updateColumnBold(false, false, false, true);
    }

    @FXML
    private void returnOnAction() throws SquidException {
        SquidUIController primaryStageController = (SquidUIController) primaryStageWindow.getScene().getUserData();
        primaryStageController.launchCommonLeadAssignment();
    }

}