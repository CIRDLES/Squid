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
package org.cirdles.squid.gui.dateInterpretations.commonLeadAssignment;

import java.net.URL;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;
import org.cirdles.squid.constants.Squid3Constants;
import static org.cirdles.squid.constants.Squid3Constants.ABS_UNCERTAINTY_DIRECTIVE;
import org.cirdles.squid.exceptions.SquidException;
import static org.cirdles.squid.gui.SquidUI.EXPRESSION_TOOLTIP_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidLabData;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.StaceyKramerCommonLeadModel;
import org.cirdles.squid.projects.SquidProject;
import static org.cirdles.squid.shrimp.CommonLeadSpecsForSpot.METHOD_COMMON_LEAD_MODEL;
import static org.cirdles.squid.shrimp.CommonLeadSpecsForSpot.METHOD_STACEY_KRAMER;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.Task;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REF_238U235U_RM_MODEL_NAME;
import org.cirdles.squid.tasks.expressions.builtinExpressions.SampleAgeTypesEnum;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import static org.cirdles.squid.shrimp.CommonLeadSpecsForSpot.METHOD_STACEY_KRAMER_BY_GROUP;
import org.cirdles.squid.tasks.expressions.OperationOrFunctionInterface;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR206_238AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR207_206AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR208_232AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR206_238AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7COR208_232AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8COR206_238AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8COR207_206AGE;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import static org.cirdles.squid.utilities.conversionUtilities.CloningUtilities.clone2dArray;
import static org.cirdles.squid.utilities.conversionUtilities.RoundingUtilities.squid3RoundedToSize;

/**
 * FXML Controller class
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class CommonLeadAssignmentController implements Initializable {

    private static boolean suppressChangeAction = false;

    @FXML
    private VBox vboxMaster;
    @FXML
    private HBox headerHBox;
    @FXML
    private HBox footerHBox;
    @FXML
    private AnchorPane sampleTreeAnchorPane;

    private TreeView<CommonLeadSampleTreeInterface> spotsTreeViewCommonLeadTools = new TreeView<>();

    private ExpressionTreeInterface expPB4COR206_238AGE;
    private ExpressionTreeInterface expPB4COR208_232AGE;
    private ExpressionTreeInterface expPB4COR207_206AGE;
    private ExpressionTreeInterface expPB7COR206_238AGE;
    private ExpressionTreeInterface expPB7COR208_232AGE;
    private ExpressionTreeInterface expPB8COR206_238AGE;
    private ExpressionTreeInterface expPB8COR207_206AGE;

    private Map<String, List<ShrimpFractionExpressionInterface>> mapOfSpotsBySampleNames;
    private Map<String, SpotSummaryDetails> mapOfWeightedMeansBySampleNames;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // update 
        squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(false);

        spotsTreeViewCommonLeadTools.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        spotsTreeViewCommonLeadTools.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty()
                .subtract(PIXEL_OFFSET_FOR_MENU + headerHBox.getPrefHeight() + footerHBox.getPrefHeight()));

        // prime StaceyKramer
        StaceyKramerCommonLeadModel.updatePhysicalConstants(squidProject.getTask().getPhysicalConstantsModel());
        StaceyKramerCommonLeadModel.updateU_Ratio(
                squidProject.getTask().getReferenceMaterialModel().getDatumByName(REF_238U235U_RM_MODEL_NAME).getValue().doubleValue());

        setupAgeTypes();

        // set up groups and refresh calculations       
        mapOfSpotsBySampleNames = squidProject.getTask().getMapOfUnknownsBySampleNames();
        // case of sample names chosen
        if (mapOfSpotsBySampleNames.size() > 1) {
            // task.getMapOfUnknownsBySampleNames restores this global set
            mapOfSpotsBySampleNames.remove(Squid3Constants.SpotTypes.UNKNOWN.getPlotType());
        }

        mapOfWeightedMeansBySampleNames = new TreeMap<>();

        for (Map.Entry<String, List<ShrimpFractionExpressionInterface>> entry : mapOfSpotsBySampleNames.entrySet()) {
            ((Task) squidProject.getTask()).evaluateUnknownsWithChangedParameters(entry.getValue());
            SpotSummaryDetails spotSummaryDetails
                    = ((Task) squidProject.getTask()).evaluateSelectedAgeWeightedMeanForUnknownGroup(entry.getKey(), entry.getValue());
            mapOfWeightedMeansBySampleNames.put(entry.getKey(), spotSummaryDetails);
        }

        showUnknownsWithOvercountCorrections();

        setUpFooter();

    }

    private void setupAgeTypes() {
        // setup 7 age types
        expPB4COR206_238AGE = squidProject.getTask().getNamedExpressionsMap().
                get(SampleAgeTypesEnum.PB4COR206_238AGE.getExpressionName());
        expPB4COR208_232AGE = squidProject.getTask().getNamedExpressionsMap().
                get(SampleAgeTypesEnum.PB4COR208_232AGE.getExpressionName());
        expPB4COR207_206AGE = squidProject.getTask().getNamedExpressionsMap().
                get(SampleAgeTypesEnum.PB4COR207_206AGE.getExpressionName());

        expPB7COR206_238AGE = squidProject.getTask().getNamedExpressionsMap().
                get(SampleAgeTypesEnum.PB7COR206_238AGE.getExpressionName());
        expPB7COR208_232AGE = squidProject.getTask().getNamedExpressionsMap().
                get(SampleAgeTypesEnum.PB7COR208_232AGE.getExpressionName());
        expPB8COR206_238AGE = squidProject.getTask().getNamedExpressionsMap().
                get(SampleAgeTypesEnum.PB8COR206_238AGE.getExpressionName());
        expPB8COR207_206AGE = squidProject.getTask().getNamedExpressionsMap().
                get(SampleAgeTypesEnum.PB8COR207_206AGE.getExpressionName());
    }

    private void showUnknownsWithOvercountCorrections() {

        CommonLeadSampleTreeInterface toolBarSampleType
                = new CommonLeadSampleToolBar(
                        Squid3Constants.SpotTypes.UNKNOWN.getPlotType(),
                        mapOfSpotsBySampleNames.get(Squid3Constants.SpotTypes.UNKNOWN.getPlotType()));
        toolBarSampleType.getCommonLeadModels().disableProperty().setValue(true);
        TreeItem<CommonLeadSampleTreeInterface> rootItemSamples = new TreeItem<>(toolBarSampleType);

        spotsTreeViewCommonLeadTools.setStyle("-fx-font-size: 12px;");
        spotsTreeViewCommonLeadTools.setRoot(rootItemSamples);

        rootItemSamples.setExpanded(true);
        spotsTreeViewCommonLeadTools.setShowRoot(true);

        for (Map.Entry<String, List<ShrimpFractionExpressionInterface>> entry : mapOfSpotsBySampleNames.entrySet()) {
            CommonLeadSampleTreeInterface toolBarSampleName = new CommonLeadSampleToolBar(entry.getKey(), entry.getValue());
            TreeItem<CommonLeadSampleTreeInterface> treeItemSampleInfo = new TreeItem<>(toolBarSampleName);

            for (ShrimpFractionExpressionInterface spot : entry.getValue()) {
                int indexOfSpot = entry.getValue().indexOf(spot);
                CommonLeadSampleTreeInterface treeItemSpotDisplay = new CommonLeadSpotDisplay(spot, entry.getKey(), indexOfSpot);
                TreeItem<CommonLeadSampleTreeInterface> treeItemSpotInfo = new TreeItem<>(treeItemSpotDisplay);
                toolBarSampleName.addSpotDependency(treeItemSpotDisplay);

                treeItemSampleInfo.getChildren().add(treeItemSpotInfo);
            }

            suppressChangeAction = true;

            // set radio button based on first spot
            switch (((CommonLeadSpotDisplay) treeItemSampleInfo.getChildren().get(0).getValue()).getSpot().getCommonLeadSpecsForSpot().getMethodSelected()) {
                case METHOD_COMMON_LEAD_MODEL:
                    treeItemSampleInfo.getValue().getChooseModelRB().setSelected(true);
                    treeItemSampleInfo.getValue().getCommonLeadModels().disableProperty().setValue(false);
                    break;
                case METHOD_STACEY_KRAMER:
                    treeItemSampleInfo.getValue().getChooseSKRB().setSelected(true);
                    treeItemSampleInfo.getValue().getCommonLeadModels().disableProperty().setValue(true);
                    break;
                case METHOD_STACEY_KRAMER_BY_GROUP:
                    treeItemSampleInfo.getValue().getChooseSKStarRB().setSelected(true);
                    treeItemSampleInfo.getValue().getCommonLeadModels().disableProperty().setValue(true);
                    break;
                default:
            }

            // update combobox
            treeItemSampleInfo.getValue().getCommonLeadModels().getSelectionModel().select(
                    ((CommonLeadSpotDisplay) treeItemSampleInfo.getChildren().get(0).getValue()).getSpot().getCommonLeadModel());

            // update text for SK age
            treeItemSampleInfo.getValue().getSkStarValueTextField().setText(
                    Double.toString(((CommonLeadSpotDisplay) treeItemSampleInfo.getChildren().get(0).getValue())
                            .getSpot().getCommonLeadSpecsForSpot().getSampleAgeSK() / 1.0e6));

            // set selected age radio button based on first spot
            String selectedAge = (((CommonLeadSpotDisplay) treeItemSampleInfo.getChildren().get(0).getValue()).getSpot().getSelectedAgeExpressionName());
            ((RadioButton) ((CommonLeadSampleToolBar) treeItemSampleInfo.getValue()).lookup("#" + selectedAge)).setSelected(true);

            suppressChangeAction = false;

            toolBarSampleType.addSpotDependency(toolBarSampleName);
            rootItemSamples.getChildren().add(treeItemSampleInfo);
        }

        sampleTreeAnchorPane.getChildren().clear();
        sampleTreeAnchorPane.getChildren().add(spotsTreeViewCommonLeadTools);
    }

    private void setUpFooter() {
        footerHBox.getChildren().addAll(
                makeLabel("", 135, true, 10),
                makeLabel("206Pb/204Pb", 90, true, 10),
                makeLabel("207Pb/204Pb", 90, true, 10),
                makeLabel("208Pb/204Pb", 90, true, 10),
                makeLabel(PB4CORR + "\n" + PB4COR206_238AGE.replace(PB4CORR, ""), 90, true, 10),
                makeLabel(PB4CORR + "\n" + PB4COR208_232AGE.replace(PB4CORR, ""), 90, true, 10),
                makeLabel(PB4CORR + "\n" + PB4COR207_206AGE.replace(PB4CORR, ""), 90, true, 10),
                makeLabel(PB7CORR + "\n" + PB7COR206_238AGE.replace(PB7CORR, ""), 90, true, 10),
                makeLabel(PB7CORR + "\n" + PB7COR208_232AGE.replace(PB7CORR, ""), 90, true, 10),
                makeLabel(PB8CORR + "\n" + PB8COR206_238AGE.replace(PB8CORR, ""), 90, true, 10),
                makeLabel(PB8CORR + "\n" + PB8COR207_206AGE.replace(PB8CORR, ""), 90, true, 10));
    }

    private Label makeLabel(String label, int width, boolean fontIsBold, int fontSize) {
        return makeLabel(label, width, fontIsBold, fontSize, -3.0);
    }

    private Label makeRedLabel(String label, int width, boolean fontIsBold, int fontSize, double verticalTranslate) {
        Label myLabel = makeLabel(label, width, fontIsBold, fontSize, verticalTranslate);
        myLabel.setTextFill(Color.RED);

        return myLabel;
    }

    private Label makeLabel(String label, int width, boolean fontIsBold, int fontSize, double verticalTranslate) {
        Label aLabel = new Label(label);
        aLabel.getStyleClass().clear();
        aLabel.setFont(Font.font("Monospaced", fontIsBold ? FontWeight.BOLD : FontWeight.MEDIUM, fontSize));
        aLabel.setPrefWidth(width);
        aLabel.setMinWidth(USE_PREF_SIZE);
        aLabel.setPrefHeight(25);
        aLabel.setMinHeight(USE_PREF_SIZE);
        aLabel.setTranslateY(verticalTranslate);

        return aLabel;
    }

    private interface CommonLeadSampleTreeInterface {

        /**
         * @return the chooseModelRB
         */
        public RadioButton getChooseModelRB();

        /**
         * @return the commonLeadModels
         */
        public ComboBox<ParametersModel> getCommonLeadModels();

        /**
         * @return the chooseModelRB
         */
        public RadioButton getChooseSKRB();

        /**
         * @return the chooseSKStarRB
         */
        public RadioButton getChooseSKStarRB();

        /**
         * @return the skStarValueTextField
         */
        public TextField getSkStarValueTextField();

        public void addSpotDependency(CommonLeadSampleTreeInterface commonLeadToolBar);
    }

    private class CommonLeadSpotDisplay extends HBox implements CommonLeadSampleTreeInterface {

        private ShrimpFractionExpressionInterface spot;
        private String sampleName;
        private int indexOfSpot;

        @FXML
        protected Label nodeName;

        public CommonLeadSpotDisplay(ShrimpFractionExpressionInterface spot, String sampleName, int indexOfSpot) {
            super(5);

            this.spot = spot;
            this.sampleName = sampleName;
            this.indexOfSpot = indexOfSpot;

            nodeName = new Label(spot.getFractionID());
            nodeName.getStyleClass().clear();
            nodeName.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
            nodeName.setPrefWidth(100);
            nodeName.setMinWidth(USE_PREF_SIZE);
            nodeName.setPrefHeight(20);
            nodeName.setMinHeight(USE_PREF_SIZE);

            displayData();
        }

        public void displayData() {
            getChildren().clear();
            getChildren().add(nodeName);

            addVboxFactory("206Pb/204Pb", spot.getCom_206Pb204Pb(), 0.0);
            addVboxFactory("207Pb/204Pb", spot.getCom_207Pb204Pb(), 0.0);
            addVboxFactory("208Pb/204Pb", spot.getCom_208Pb204Pb(), 0.0);

            addVboxFactory(expPB4COR206_238AGE.getName(),
                    spot.getTaskExpressionsEvaluationsPerSpot().get(expPB4COR206_238AGE)[0][0],
                    spot.getTaskExpressionsEvaluationsPerSpot().get(expPB4COR206_238AGE)[0][1]);
            addVboxFactory(expPB4COR208_232AGE.getName(),
                    spot.getTaskExpressionsEvaluationsPerSpot().get(expPB4COR208_232AGE)[0][0],
                    spot.getTaskExpressionsEvaluationsPerSpot().get(expPB4COR208_232AGE)[0][1]);
            addVboxFactory(expPB4COR207_206AGE.getName(),
                    spot.getTaskExpressionsEvaluationsPerSpot().get(expPB4COR207_206AGE)[0][0],
                    spot.getTaskExpressionsEvaluationsPerSpot().get(expPB4COR207_206AGE)[0][1]);

            addVboxFactory(expPB7COR206_238AGE.getName(),
                    spot.getTaskExpressionsEvaluationsPerSpot().get(expPB7COR206_238AGE)[0][0],
                    spot.getTaskExpressionsEvaluationsPerSpot().get(expPB7COR206_238AGE)[0][1]);
            addVboxFactory(expPB7COR208_232AGE.getName(),
                    spot.getTaskExpressionsEvaluationsPerSpot().get(expPB7COR208_232AGE)[0][0],
                    spot.getTaskExpressionsEvaluationsPerSpot().get(expPB7COR208_232AGE)[0][1]);
            addVboxFactory(expPB8COR206_238AGE.getName(),
                    spot.getTaskExpressionsEvaluationsPerSpot().get(expPB8COR206_238AGE)[0][0],
                    spot.getTaskExpressionsEvaluationsPerSpot().get(expPB8COR206_238AGE)[0][1]);
            addVboxFactory(expPB8COR207_206AGE.getName(),
                    spot.getTaskExpressionsEvaluationsPerSpot().get(expPB8COR207_206AGE)[0][0],
                    spot.getTaskExpressionsEvaluationsPerSpot().get(expPB8COR207_206AGE)[0][1]);

        }

        /**
         *
         * @param label the value of label
         * @param value the value of value
         * @param unct the value of unct
         */
        private void addVboxFactory(String label, double value, double unct) {
            boolean fontIsBold = false;
            Formatter formatter = new Formatter();
            if (unct > 0.0) {
                // we have an age
                formatter.format("%5.1f", value / 1e6);
                formatter.format(" " + ABS_UNCERTAINTY_DIRECTIVE + "%2.1f", unct / 1e6).toString();
                boolean spotIsRejected = mapOfWeightedMeansBySampleNames.get(sampleName).getRejectedIndices()[indexOfSpot];
                fontIsBold = (spot.getSelectedAgeExpressionName().compareTo(label) == 0) && !spotIsRejected;
            } else {
                // we have a ratio
                formatter.format("%7.4f", value);
                fontIsBold = value > 0.0;
            }

            // selected age in bold
            Label aValue = new Label(formatter.toString());
            aValue.getStyleClass().clear();
            aValue.setFont(Font.font("Monospaced", fontIsBold ? FontWeight.BOLD : FontWeight.MEDIUM, 12));
            if ((value <= 0.0) || Double.isNaN(value)) {
                aValue.setTextFill(Paint.valueOf("red"));
                aValue.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
            }
            aValue.setPrefWidth(102);
            aValue.setMinWidth(USE_PREF_SIZE);
            aValue.setPrefHeight(20);
            aValue.setMinHeight(USE_PREF_SIZE);

            getChildren().add(aValue);
        }

        /**
         * @return the chooseModelRB
         */
        @Override
        public RadioButton getChooseModelRB() {
            return null;
        }

        @Override
        public RadioButton getChooseSKRB() {
            return null;
        }

        @Override
        public void addSpotDependency(CommonLeadSampleTreeInterface commonLeadToolBar) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        /**
         * @return the spot
         */
        public ShrimpFractionExpressionInterface getSpot() {
            return spot;
        }

        @Override
        public RadioButton getChooseSKStarRB() {
            return null;
        }

        @Override
        public TextField getSkStarValueTextField() {
            return null;
        }

        @Override
        public ComboBox<ParametersModel> getCommonLeadModels() {
            return null;
        }

    }

    private class CommonLeadSampleToolBar extends HBox implements CommonLeadSampleTreeInterface {

        @FXML
        protected TextField nodeName;

        @FXML
        ToggleGroup methodRB_ToggleGroup = new ToggleGroup();
        @FXML
        protected ToggleGroup ageRB_ToggleGroup = new ToggleGroup();

        @FXML
        protected RadioButton chooseModelRB;
        @FXML
        protected RadioButton chooseSKRB;
        @FXML
        protected RadioButton chooseSKStarRB;

        @FXML
        protected ComboBox<ParametersModel> commonLeadModels;

        @FXML
        protected VBox nodNameVbox;
        @FXML
        protected VBox commonLeadModelsRBVbox;
        @FXML
        protected VBox commonLeadModelsVbox;
        @FXML
        protected VBox staceyKramerRBVbox;
        @FXML
        protected VBox skStarVBox;
        @FXML
        protected VBox customModelButtonVbox;

        @FXML
        protected TextField skStarValueTextField;

        protected List<ShrimpFractionExpressionInterface> sampleGroup;

        protected List<CommonLeadSampleTreeInterface> commonLeadSpotToolBarTargets;

        protected HBox weightedMeansHBox;

        public CommonLeadSampleToolBar(String sampleGroupName, List<ShrimpFractionExpressionInterface> mySampleGroup) {
            super(2);

            this.addEventFilter(EventType.ROOT, new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    spotsTreeViewCommonLeadTools.getSelectionModel().clearSelection();
                }
            });

            this.sampleGroup = mySampleGroup;
            commonLeadSpotToolBarTargets = new ArrayList<>();

            nodNameVbox = new VBox();
            nodeName = new TextField(sampleGroupName);
            nodeName.getStyleClass().clear();
            nodeName.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
            nodeName.setPrefWidth(65);
            nodeName.setMinWidth(USE_PREF_SIZE);
            nodeName.setPrefHeight(20);
            nodeName.setMinHeight(USE_PREF_SIZE);
            nodNameVbox.getChildren().add(nodeName);
            getChildren().add(nodNameVbox);

            commonLeadModelsRBVbox = new VBox();
            chooseModelRB = new RadioButton();
            chooseModelRB.setId("model");
            chooseModelRB.setPrefWidth(27);
            chooseModelRB.setMinWidth(USE_PREF_SIZE);
            chooseModelRB.setPrefHeight(25);
            chooseModelRB.setMinHeight(USE_PREF_SIZE);
            chooseModelRB.setTranslateX(10);
            chooseModelRB.setToggleGroup(methodRB_ToggleGroup);
            chooseModelRB.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue && !suppressChangeAction) {
                        if (commonLeadSpotToolBarTargets.get(0) instanceof CommonLeadSampleToolBar) {
                            for (CommonLeadSampleTreeInterface treeItem : commonLeadSpotToolBarTargets) {
                                treeItem.getChooseModelRB().setSelected(newValue);
                            }
                        } else {
                            ((Task) squidProject.getTask()).setUnknownGroupCommonLeadMethod(sampleGroup, METHOD_COMMON_LEAD_MODEL);
                            ((Task) squidProject.getTask()).evaluateUnknownsWithChangedParameters(sampleGroup);
                            SpotSummaryDetails spotSummaryDetails
                                    = ((Task) squidProject.getTask()).evaluateSelectedAgeWeightedMeanForUnknownGroup(sampleGroupName, sampleGroup);
                            mapOfWeightedMeansBySampleNames.put(sampleGroupName, spotSummaryDetails);

                            try {
                                updateWeightedMeanLabel(
                                        ((Label) weightedMeansHBox.lookup("#" + spotSummaryDetails.getExpressionTree().getName().split("_WM_")[0])),
                                        sampleGroupName);
                            } catch (Exception e) {
                            }

                            for (CommonLeadSampleTreeInterface treeItem : commonLeadSpotToolBarTargets) {
                                ((CommonLeadSpotDisplay) treeItem).displayData();
                            }
                        }
                        SquidProject.setProjectChanged(true);
                        getCommonLeadModels().setDisable(false);
                    } else {
                        getCommonLeadModels().setDisable(true && !suppressChangeAction);
                    }
                }
            });
            commonLeadModelsRBVbox.getChildren().add(chooseModelRB);
            getChildren().add(commonLeadModelsRBVbox);

            commonLeadModelsVbox = new VBox();
            commonLeadModels = new ComboBox<>();
            commonLeadModels.setStyle("-fx-font-size: 11px;");
            commonLeadModels.setPrefWidth(180);
            commonLeadModels.setMinWidth(USE_PREF_SIZE);
            commonLeadModels.setConverter(new ParameterModelStringConverter());
            commonLeadModels.setItems(FXCollections.observableArrayList(squidLabData.getCommonPbModels()));
            commonLeadModels.getSelectionModel().select(squidProject.getTask().getCommonPbModel());
            commonLeadModels.valueProperty().addListener((ObservableValue<? extends ParametersModel> observableModel, ParametersModel oldModel, ParametersModel newModel) -> {
                if (chooseModelRB.isSelected()) {
                    if ((!newModel.equals(oldModel)) && !suppressChangeAction) {
                        if (commonLeadSpotToolBarTargets.get(0) instanceof CommonLeadSampleToolBar) {
                            for (CommonLeadSampleTreeInterface treeItem : commonLeadSpotToolBarTargets) {
                                treeItem.getCommonLeadModels().getSelectionModel().select(newModel);
                            }
                        } else {

                            ((Task) squidProject.getTask()).setUnknownGroupCommonLeadModel(sampleGroup, newModel);
                            ((Task) squidProject.getTask()).evaluateUnknownsWithChangedParameters(sampleGroup);
                            SpotSummaryDetails spotSummaryDetails
                                    = ((Task) squidProject.getTask()).evaluateSelectedAgeWeightedMeanForUnknownGroup(sampleGroupName, sampleGroup);
                            mapOfWeightedMeansBySampleNames.put(sampleGroupName, spotSummaryDetails);

                            try {
                                updateWeightedMeanLabel(
                                        ((Label) weightedMeansHBox.lookup("#" + spotSummaryDetails.getExpressionTree().getName().split("_WM_")[0])),
                                        sampleGroupName);
                            } catch (Exception e) {
                            }
                            for (CommonLeadSampleTreeInterface treeItem : commonLeadSpotToolBarTargets) {
                                ((CommonLeadSpotDisplay) treeItem).displayData();
                            }
                        }
                        SquidProject.setProjectChanged(true);
                    }
                }
            });

            commonLeadModelsVbox.getChildren().add(commonLeadModels);
            this.getChildren().add(commonLeadModelsVbox);

            // SK* = 1 age per group
            skStarVBox = new VBox();
            chooseSKStarRB = new RadioButton("SK*");
            chooseSKStarRB.setId("SK*");
            chooseSKStarRB.setFont(Font.font("Monospaced", FontWeight.BOLD, 11));
            chooseSKStarRB.setPrefWidth(38);
            chooseSKStarRB.setMinWidth(USE_PREF_SIZE);
            chooseSKStarRB.setPrefHeight(25);
            chooseSKStarRB.setMinHeight(USE_PREF_SIZE);
            chooseSKStarRB.setTranslateX(10);
            chooseSKStarRB.setToggleGroup(methodRB_ToggleGroup);
            chooseSKStarRB.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue && !suppressChangeAction) {
                        if (commonLeadSpotToolBarTargets.get(0) instanceof CommonLeadSampleToolBar) {
                            for (CommonLeadSampleTreeInterface treeItem : commonLeadSpotToolBarTargets) {
                                treeItem.getChooseSKStarRB().setSelected(newValue);
                            }
                        } else {
                            ((Task) squidProject.getTask()).setUnknownGroupCommonLeadMethod(sampleGroup, METHOD_STACEY_KRAMER_BY_GROUP);
                            ((Task) squidProject.getTask()).evaluateUnknownsWithChangedParameters(sampleGroup);
                            SpotSummaryDetails spotSummaryDetails
                                    = ((Task) squidProject.getTask()).evaluateSelectedAgeWeightedMeanForUnknownGroup(sampleGroupName, sampleGroup);
                            mapOfWeightedMeansBySampleNames.put(sampleGroupName, spotSummaryDetails);

                            try {
                                updateWeightedMeanLabel(
                                        ((Label) weightedMeansHBox.lookup("#" + spotSummaryDetails.getExpressionTree().getName().split("_WM_")[0])),
                                        sampleGroupName);
                            } catch (Exception e) {
                            }

                            for (CommonLeadSampleTreeInterface treeItem : commonLeadSpotToolBarTargets) {
                                ((CommonLeadSpotDisplay) treeItem).displayData();
                            }
                        }
                        SquidProject.setProjectChanged(true);
                    }
                }
            });
            skStarVBox.getChildren().add(chooseSKStarRB);
            getChildren().add(skStarVBox);

            customModelButtonVbox = new VBox();
            skStarValueTextField = new TextField();
            skStarValueTextField.setStyle("-fx-font-size: 11px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
            skStarValueTextField.setPromptText("Age Ma");
            skStarValueTextField.setTranslateX(10);
            skStarValueTextField.setPrefWidth(45);
            skStarValueTextField.setMinWidth(USE_PREF_SIZE);
            skStarValueTextField.setPrefHeight(25);
            skStarValueTextField.setMinHeight(USE_PREF_SIZE);
            skStarValueTextField.setAlignment(Pos.CENTER);
            skStarValueTextField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if ((newValue.compareToIgnoreCase(oldValue) != 0) && !suppressChangeAction) {
                        if (commonLeadSpotToolBarTargets.get(0) instanceof CommonLeadSampleToolBar) {
                            for (CommonLeadSampleTreeInterface treeItem : commonLeadSpotToolBarTargets) {
                                treeItem.getSkStarValueTextField().setText(newValue);
                            }
                        } else {
                            try {
                                // set to annum
                                ((Task) squidProject.getTask()).setUnknownGroupAgeSK(sampleGroup, Double.parseDouble(newValue.trim()) * 1.0e6);
                            } catch (NumberFormatException numberFormatException) {
                            }

                            if (chooseSKStarRB.isSelected()) {
                                ((Task) squidProject.getTask()).evaluateUnknownsWithChangedParameters(sampleGroup);
                                SpotSummaryDetails spotSummaryDetails
                                        = ((Task) squidProject.getTask()).evaluateSelectedAgeWeightedMeanForUnknownGroup(sampleGroupName, sampleGroup);
                                mapOfWeightedMeansBySampleNames.put(sampleGroupName, spotSummaryDetails);

                                try {
                                    updateWeightedMeanLabel(
                                            ((Label) weightedMeansHBox.lookup("#" + spotSummaryDetails.getExpressionTree().getName().split("_WM_")[0])),
                                            sampleGroupName);
                                } catch (Exception e) {
                                }

                                for (CommonLeadSampleTreeInterface treeItem : commonLeadSpotToolBarTargets) {
                                    ((CommonLeadSpotDisplay) treeItem).displayData();
                                }
                            }
                        }
                        SquidProject.setProjectChanged(true);
                    }
                }
            });

            customModelButtonVbox.getChildren().add(skStarValueTextField);
            getChildren().add(customModelButtonVbox);

            // stacey Kramer
            staceyKramerRBVbox = new VBox();
            chooseSKRB = new RadioButton("SK");
            chooseSKRB.setId("SK");
            chooseSKRB.setFont(Font.font("Monospaced", FontWeight.BOLD, 11));
            chooseSKRB.setPrefWidth(35);
            chooseSKRB.setMinWidth(USE_PREF_SIZE);
            chooseSKRB.setPrefHeight(25);
            chooseSKRB.setMinHeight(USE_PREF_SIZE);
            chooseSKRB.setTranslateX(10);
            chooseSKRB.setToggleGroup(methodRB_ToggleGroup);
            chooseSKRB.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue && !suppressChangeAction) {
                        if (commonLeadSpotToolBarTargets.get(0) instanceof CommonLeadSampleToolBar) {
                            for (CommonLeadSampleTreeInterface treeItem : commonLeadSpotToolBarTargets) {
                                treeItem.getChooseSKRB().setSelected(newValue);
                            }
                        } else {
                            ((Task) squidProject.getTask()).setUnknownGroupCommonLeadMethod(sampleGroup, METHOD_STACEY_KRAMER);
                            ((Task) squidProject.getTask()).evaluateUnknownsWithChangedParameters(sampleGroup);
                            SpotSummaryDetails spotSummaryDetails
                                    = ((Task) squidProject.getTask()).evaluateSelectedAgeWeightedMeanForUnknownGroup(sampleGroupName, sampleGroup);
                            mapOfWeightedMeansBySampleNames.put(sampleGroupName, spotSummaryDetails);

                            try {
                                updateWeightedMeanLabel(
                                        ((Label) weightedMeansHBox.lookup("#" + spotSummaryDetails.getExpressionTree().getName().split("_WM_")[0])),
                                        sampleGroupName);
                            } catch (Exception e) {
                            }

                            for (CommonLeadSampleTreeInterface treeItem : commonLeadSpotToolBarTargets) {
                                ((CommonLeadSpotDisplay) treeItem).displayData();
                            }
                        }
                        SquidProject.setProjectChanged(true);
                    }
                }
            });
            staceyKramerRBVbox.getChildren().add(chooseSKRB);
            getChildren().add(staceyKramerRBVbox);

            VBox groupDetailsVBox = new VBox(-10);
            HBox ageChoosersHBox = new HBox();
            ageChoosersHBox.setTranslateX(15);

            ageChoosersHBox.getChildren().add(ageRadioButtonFactory(sampleGroupName, SampleAgeTypesEnum.PB4COR206_238AGE, PB4CORR));
            ageChoosersHBox.getChildren().add(ageRadioButtonFactory(sampleGroupName, SampleAgeTypesEnum.PB4COR208_232AGE, PB4CORR));
            ageChoosersHBox.getChildren().add(ageRadioButtonFactory(sampleGroupName, SampleAgeTypesEnum.PB4COR207_206AGE, PB4CORR));

            ageChoosersHBox.getChildren().add(ageRadioButtonFactory(sampleGroupName, SampleAgeTypesEnum.PB7COR206_238AGE, PB7CORR));
            ageChoosersHBox.getChildren().add(ageRadioButtonFactory(sampleGroupName, SampleAgeTypesEnum.PB7COR208_232AGE, PB7CORR));

            ageChoosersHBox.getChildren().add(ageRadioButtonFactory(sampleGroupName, SampleAgeTypesEnum.PB8COR206_238AGE, PB8CORR));
            ageChoosersHBox.getChildren().add(ageRadioButtonFactory(sampleGroupName, SampleAgeTypesEnum.PB8COR207_206AGE, PB8CORR));

            groupDetailsVBox.getChildren().add(ageChoosersHBox);

            // be sure we are on sample group toolbar
            if (sampleGroup != null) {
                weightedMeansHBox = new HBox();
                weightedMeansHBox.setTranslateX(15);

                int wmWidth = 107;
                Label weightedMeanLabel = makeRedLabel("", wmWidth, true, 11, 4.0);
                weightedMeanLabel.setId(SampleAgeTypesEnum.PB4COR206_238AGE.getExpressionName());
                weightedMeansHBox.getChildren().add(weightedMeanLabel);

                weightedMeanLabel = makeRedLabel("", wmWidth, true, 11, 4.0);
                weightedMeanLabel.setId(SampleAgeTypesEnum.PB4COR208_232AGE.getExpressionName());
                weightedMeansHBox.getChildren().add(weightedMeanLabel);

                weightedMeanLabel = makeRedLabel("", wmWidth, true, 11, 4.0);
                weightedMeanLabel.setId(SampleAgeTypesEnum.PB4COR207_206AGE.getExpressionName());
                weightedMeansHBox.getChildren().add(weightedMeanLabel);

                weightedMeanLabel = makeRedLabel("", wmWidth, true, 11, 4.0);
                weightedMeanLabel.setId(SampleAgeTypesEnum.PB7COR206_238AGE.getExpressionName());
                weightedMeansHBox.getChildren().add(weightedMeanLabel);

                weightedMeanLabel = makeRedLabel("", wmWidth, true, 11, 4.0);
                weightedMeanLabel.setId(SampleAgeTypesEnum.PB7COR208_232AGE.getExpressionName());
                weightedMeansHBox.getChildren().add(weightedMeanLabel);

                weightedMeanLabel = makeRedLabel("", wmWidth, true, 11, 4.0);
                weightedMeanLabel.setId(SampleAgeTypesEnum.PB8COR206_238AGE.getExpressionName());
                weightedMeansHBox.getChildren().add(weightedMeanLabel);

                weightedMeanLabel = makeRedLabel("", wmWidth, true, 11, 4.0);
                weightedMeanLabel.setId(SampleAgeTypesEnum.PB8COR207_206AGE.getExpressionName());
                weightedMeansHBox.getChildren().add(weightedMeanLabel);

                weightedMeansHBox.addEventFilter(EventType.ROOT, new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        spotsTreeViewCommonLeadTools.getSelectionModel().clearSelection();
                    }
                });
                groupDetailsVBox.getChildren().add(weightedMeansHBox);
            }
            getChildren().add(groupDetailsVBox);

        }

        private void updateWeightedMeanLabel(Label wmLabel, String sampleGroupName) {
            SpotSummaryDetails spotSummaryDetails = mapOfWeightedMeansBySampleNames.get(sampleGroupName);
            Formatter formatter = new Formatter();
            formatter.format("%5.1f", spotSummaryDetails.getValues()[0][0] / 1e6);
            formatter.format(" " + ABS_UNCERTAINTY_DIRECTIVE + "%2.1f", spotSummaryDetails.getValues()[0][1] / 1e6).toString();
            wmLabel.setText("WM: " + formatter.toString());

            // tool tip
            OperationOrFunctionInterface op = ((ExpressionTree) spotSummaryDetails.getExpressionTree()).getOperation();
            String[][] labels = clone2dArray(op.getLabelsForOutputValues());

            StringBuilder sb = new StringBuilder();
            sb.append("Weighted Mean for Sample ").append(sampleGroupName).append("\n");
            for (int i = 0; i < labels[0].length - 1; i++) {
                sb.append("\t");
                if (spotSummaryDetails.getValues().length > 0) {
                    // show array index in Squid3
                    sb.append("[").append(i).append("] ");
                    sb.append(String.format("%1$-" + 16 + "s", labels[0][i]));
                    sb.append(": ");
                    sb.append(squid3RoundedToSize(spotSummaryDetails.getValues()[0][i] / (i < 4 ? 1.0e6 : 1.0), 5));
                } else {
                    sb.append("Undefined Expression or Function");
                }
                sb.append("\n");
            }

            Tooltip toolTip = new Tooltip(sb.toString());
            toolTip.setStyle(EXPRESSION_TOOLTIP_CSS_STYLE_SPECS);
            wmLabel.setTooltip(toolTip);

            // https://coderanch.com/t/622070/java/control-Tooltip-visible-time-duration
            wmLabel.setOnMouseEntered(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    Point2D p = wmLabel.localToScreen(wmLabel.getLayoutBounds().getMaxX(), wmLabel.getLayoutBounds().getMaxY());
                    toolTip.show(wmLabel, p.getX(), p.getY());
                }
            });
            wmLabel.setOnMouseExited(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    toolTip.hide();
                }
            });

        }

        /**
         *
         * @param sampleGroupName the value of sampleGroupName
         * @param sampleAgeType the value of sampleAgeType
         * @param corrString the value of corrString
         */
        private RadioButton ageRadioButtonFactory(String sampleGroupName, SampleAgeTypesEnum sampleAgeType, String corrString) {
            RadioButton ageRB = new RadioButton(corrString + "\n" + sampleAgeType.getExpressionName().replace(corrString, ""));
            ageRB.setId(sampleAgeType.getExpressionName());
            ageRB.setToggleGroup(ageRB_ToggleGroup);
            ageRB.setFont(Font.font("Monospaced", FontWeight.BOLD, 10));
            ageRB.setPrefWidth(107);
            ageRB.setMinWidth(USE_PREF_SIZE);
            ageRB.setPrefHeight(25);
            ageRB.setMinHeight(USE_PREF_SIZE);

            ageRB.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue && !suppressChangeAction) {
                        if (commonLeadSpotToolBarTargets.get(0) instanceof CommonLeadSampleToolBar) {
                            for (CommonLeadSampleTreeInterface treeItem : commonLeadSpotToolBarTargets) {
                                ((RadioButton) ((CommonLeadSampleToolBar) treeItem).lookup("#" + sampleAgeType.getExpressionName())).setSelected(true);
                            }
                        } else {
                            // get some settings from current wm
                            SpotSummaryDetails currentSpotSummaryDetails = mapOfWeightedMeansBySampleNames.get(sampleGroupName);

                            ((Task) squidProject.getTask()).setUnknownGroupSelectedAge(sampleGroup, sampleAgeType);
                            ((Task) squidProject.getTask()).evaluateUnknownsWithChangedParameters(sampleGroup);
                            SpotSummaryDetails spotSummaryDetailsWM
                                    = ((Task) squidProject.getTask()).evaluateSelectedAgeWeightedMeanForUnknownGroup(sampleGroupName, sampleGroup);
                            spotSummaryDetailsWM.setManualRejectionEnabled(true);
                            spotSummaryDetailsWM.setRejectedIndices(currentSpotSummaryDetails.getRejectedIndices().clone());
                            spotSummaryDetailsWM.setMinProbabilityWM(currentSpotSummaryDetails.getMinProbabilityWM());
                            try {
                                spotSummaryDetailsWM.setValues(spotSummaryDetailsWM.eval(squidProject.getTask()));
                            } catch (SquidException squidException) {
                            }
                            mapOfWeightedMeansBySampleNames.put(sampleGroupName, spotSummaryDetailsWM);

                            for (CommonLeadSampleTreeInterface treeItem : commonLeadSpotToolBarTargets) {
                                ((CommonLeadSpotDisplay) treeItem).displayData();
                            }
                        }
                        SquidProject.setProjectChanged(true);
                    }

                    if (newValue) {
                        try {
                            updateWeightedMeanLabel(
                                    ((Label) weightedMeansHBox.lookup("#" + sampleAgeType.getExpressionName())),
                                    sampleGroupName);
                        } catch (Exception e) {
                        }
                    } else if (!newValue) {
                        try {
                            Label label = ((Label) weightedMeansHBox.lookup("#" + sampleAgeType.getExpressionName()));
                            label.setText("");
                            label.getTooltip().setText("not calculated");
                        } catch (Exception e) {
                        }
                    }
                }
            });

            return ageRB;
        }

        /**
         * @return the chooseModelRB
         */
        @Override

        public RadioButton getChooseModelRB() {
            return chooseModelRB;
        }

        /**
         * @return the commonLeadModels
         */
        @Override
        public ComboBox<ParametersModel> getCommonLeadModels() {
            return commonLeadModels;
        }

        /**
         * @return the chooseModelRB
         */
        @Override
        public RadioButton getChooseSKRB() {
            return chooseSKRB;
        }

        /**
         * @return the chooseSKStarRB
         */
        @Override
        public RadioButton getChooseSKStarRB() {
            return chooseSKStarRB;
        }

        /**
         * @return the skStarValueTextField
         */
        @Override
        public TextField getSkStarValueTextField() {
            return skStarValueTextField;
        }

        @Override
        public void addSpotDependency(CommonLeadSampleTreeInterface commonLeadToolBar) {
            this.commonLeadSpotToolBarTargets.add(commonLeadToolBar);
        }

        /**
         * @return the ageRB_ToggleGroup
         */
        public ToggleGroup getAgeRB_ToggleGroup() {
            return ageRB_ToggleGroup;
        }

        /**
         * @return the commonLeadSpotToolBarTargets
         */
        public List<CommonLeadSampleTreeInterface> getCommonLeadSpotToolBarTargets() {
            return commonLeadSpotToolBarTargets;
        }
    }

    static class ParameterModelStringConverter extends StringConverter<ParametersModel> {

        @Override
        public String toString(ParametersModel model) {
            return model.getModelNameWithVersion();
        }

        @Override
        public ParametersModel fromString(String string) {
            return null;
        }
    };
}
