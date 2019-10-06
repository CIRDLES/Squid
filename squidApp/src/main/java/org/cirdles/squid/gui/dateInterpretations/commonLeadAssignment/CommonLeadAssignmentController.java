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
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;
import org.cirdles.squid.constants.Squid3Constants;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidLabData;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.StaceyKramerCommonLeadModel;
import static org.cirdles.squid.shrimp.CommonLeadSpecsForSpot.METHOD_COMMON_LEAD_MODEL;
import static org.cirdles.squid.shrimp.CommonLeadSpecsForSpot.METHOD_STACEY_KRAMER;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.Task;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REF_238U235U_RM_MODEL_NAME;
import org.cirdles.squid.tasks.expressions.builtinExpressions.SampleAgeTypesEnum;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

/**
 * FXML Controller class
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class CommonLeadAssignmentController implements Initializable {

    @FXML
    private VBox vboxMaster;
    @FXML
    private HBox headerHBox;
    @FXML
    private AnchorPane sampleTreeAnchorPane;

    private TreeView<CommonLeadSampleTreeInterface> spotsTreeViewCommonLeadTools = new TreeView<>();

    private ExpressionTreeInterface expPB4COR206_238AGE;
    private ExpressionTreeInterface expPB4COR208_232AGE;
    private ExpressionTreeInterface expPB4COR207_206AGE;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // update 
        squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(false);

        spotsTreeViewCommonLeadTools.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        spotsTreeViewCommonLeadTools.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty()
                .subtract(PIXEL_OFFSET_FOR_MENU + headerHBox.getPrefHeight()));

        // prime StaceyKramer
        StaceyKramerCommonLeadModel.updatePhysicalConstants(squidProject.getTask().getPhysicalConstantsModel());
        StaceyKramerCommonLeadModel.updateU_Ratio(
                squidProject.getTask().getReferenceMaterialModel().getDatumByName(REF_238U235U_RM_MODEL_NAME).getValue().doubleValue());

        setupAgeTypes();

        showUnknownsWithOvercountCorrections();
    }

    private void setupAgeTypes() {
        // setup 7 age types
        expPB4COR206_238AGE = squidProject.getTask().getNamedExpressionsMap().
                get(SampleAgeTypesEnum.PB4COR206_238AGE.getExpressionName());
        expPB4COR208_232AGE = squidProject.getTask().getNamedExpressionsMap().
                get(SampleAgeTypesEnum.PB4COR208_232AGE.getExpressionName());
        expPB4COR207_206AGE = squidProject.getTask().getNamedExpressionsMap().
                get(SampleAgeTypesEnum.PB4COR207_206AGE.getExpressionName());
    }

    private void showUnknownsWithOvercountCorrections() {

        Map<String, List<ShrimpFractionExpressionInterface>> mapOfSpotsBySampleNames;
        mapOfSpotsBySampleNames = squidProject.getTask().getMapOfUnknownsBySampleNames();

        CommonLeadSampleTreeInterface toolBarSampleType
                = new CommonLeadSampleToolBar(
                        Squid3Constants.SpotTypes.UNKNOWN.getPlotType(),
                        mapOfSpotsBySampleNames.get(Squid3Constants.SpotTypes.UNKNOWN.getPlotType()));
        TreeItem<CommonLeadSampleTreeInterface> rootItemSamples = new TreeItem<>(toolBarSampleType);

        spotsTreeViewCommonLeadTools.setStyle("-fx-font-size: 12px;");
        spotsTreeViewCommonLeadTools.setRoot(rootItemSamples);

        rootItemSamples.setExpanded(true);
        spotsTreeViewCommonLeadTools.setShowRoot(true);

        // case of sample names chosen
        if (mapOfSpotsBySampleNames.size() > 1) {
            // task.getMapOfUnknownsBySampleNames restores this global set
            mapOfSpotsBySampleNames.remove(Squid3Constants.SpotTypes.UNKNOWN.getPlotType());
        }

        for (Map.Entry<String, List<ShrimpFractionExpressionInterface>> entry : mapOfSpotsBySampleNames.entrySet()) {
            CommonLeadSampleTreeInterface toolBarSampleName = new CommonLeadSampleToolBar(entry.getKey(), entry.getValue());
            TreeItem<CommonLeadSampleTreeInterface> treeItemSampleInfo = new TreeItem<>(toolBarSampleName);

            for (ShrimpFractionExpressionInterface spot : entry.getValue()) {
                CommonLeadSampleTreeInterface treeItemSpotDisplay = new commonLeadSpotDisplay(spot);
                TreeItem<CommonLeadSampleTreeInterface> treeItemSpotInfo = new TreeItem<>(treeItemSpotDisplay);
                toolBarSampleName.addCommonLeadModelRBDependency(treeItemSpotDisplay);

                treeItemSampleInfo.getChildren().add(treeItemSpotInfo);
            }

            toolBarSampleType.addCommonLeadModelRBDependency(toolBarSampleName);
            rootItemSamples.getChildren().add(treeItemSampleInfo);
        }

        sampleTreeAnchorPane.getChildren().clear();
        sampleTreeAnchorPane.getChildren().add(spotsTreeViewCommonLeadTools);
    }

    private interface CommonLeadSampleTreeInterface {

        /**
         * @return the chooseModelRB
         */
        public RadioButton getChooseModelRB();

        /**
         * @return the chooseModelRB
         */
        public RadioButton getChooseSKRB();

        public void addCommonLeadModelRBDependency(CommonLeadSampleTreeInterface commonLeadToolBar);
    }

    private class commonLeadSpotDisplay extends HBox implements CommonLeadSampleTreeInterface {

        private ShrimpFractionExpressionInterface spot;

        @FXML
        protected Label nodeName;

        public commonLeadSpotDisplay(ShrimpFractionExpressionInterface spot) {
            super(5);

            this.spot = spot;

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

            addVboxFactory("206Pb/204Pb", spot.getCom_206Pb204Pb());
            addVboxFactory("207Pb/206Pb", spot.getCom_207Pb206Pb());
            addVboxFactory("208Pb/206Pb", spot.getCom_208Pb206Pb());

            addVboxFactory("4cor_206Pb238U_Age", spot.getTaskExpressionsEvaluationsPerSpot().get(expPB4COR206_238AGE)[0][0]);
            addVboxFactory("4cor_208Pb232Th_Age", spot.getTaskExpressionsEvaluationsPerSpot().get(expPB4COR208_232AGE)[0][0]);
            addVboxFactory("4cor_207Pb206Pb_Age", spot.getTaskExpressionsEvaluationsPerSpot().get(expPB4COR207_206AGE)[0][0]);

            addVboxFactory("4cor_206Pb238U_Age", spot.getTaskExpressionsEvaluationsPerSpot().get(expPB4COR206_238AGE)[0][0]);
            addVboxFactory("4cor_206Pb238U_Age", spot.getTaskExpressionsEvaluationsPerSpot().get(expPB4COR206_238AGE)[0][0]);
            addVboxFactory("4cor_206Pb238U_Age", spot.getTaskExpressionsEvaluationsPerSpot().get(expPB4COR206_238AGE)[0][0]);
            addVboxFactory("4cor_206Pb238U_Age", spot.getTaskExpressionsEvaluationsPerSpot().get(expPB4COR206_238AGE)[0][0]);
        }

        private void addVboxFactory(String label, double value) {
            VBox aVBox = new VBox(-10);

            Label alabel = new Label(label);
            alabel.getStyleClass().clear();
            alabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 8));
            alabel.setPrefWidth(100);
            alabel.setMinWidth(USE_PREF_SIZE);
            alabel.setPrefHeight(20);
            alabel.setMinHeight(USE_PREF_SIZE);

            Label aValue = new Label(Double.toString(value));
            aValue.getStyleClass().clear();
            aValue.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
            aValue.setPrefWidth(100);
            aValue.setMinWidth(USE_PREF_SIZE);
            aValue.setPrefHeight(20);
            aValue.setMinHeight(USE_PREF_SIZE);

            aVBox.getChildren().addAll(alabel, aValue);

            getChildren().add(aVBox);
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
        public void addCommonLeadModelRBDependency(CommonLeadSampleTreeInterface commonLeadToolBar) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        /**
         * @return the spot
         */
        public ShrimpFractionExpressionInterface getSpot() {
            return spot;
        }

    }

    private class CommonLeadSampleToolBar extends HBox implements CommonLeadSampleTreeInterface {

        @FXML
        protected TextField nodeName;

        @FXML
        ToggleGroup radioButtonToggleGroup = new ToggleGroup();

        @FXML
        protected RadioButton chooseModelRB;
        @FXML
        protected RadioButton chooseSKRB;

        @FXML
        protected ComboBox<ParametersModel> commonLeadModels;

        @FXML
        protected VBox commonLeadModelsRBsVbox;
        @FXML
        protected VBox commonLeadModelsVbox;
        @FXML
        protected VBox staceyKramerRBsVbox;

        protected List<ShrimpFractionExpressionInterface> sampleGroup;

        protected List<CommonLeadSampleTreeInterface> commonLeadSpotToolBarTargets;

        public CommonLeadSampleToolBar(String sampleGroupName, List<ShrimpFractionExpressionInterface> mySampleGroup) {
            super(5);
            this.sampleGroup = mySampleGroup;
            commonLeadSpotToolBarTargets = new ArrayList<>();

            nodeName = new TextField(sampleGroupName);
            nodeName.getStyleClass().clear();
            nodeName.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
            nodeName.setPrefWidth(100);
            nodeName.setMinWidth(USE_PREF_SIZE);
            nodeName.setPrefHeight(20);
            nodeName.setMinHeight(USE_PREF_SIZE);
            getChildren().add(nodeName);

            commonLeadModelsRBsVbox = new VBox();
            chooseModelRB = new RadioButton();
            chooseModelRB.setPrefWidth(35);
            chooseModelRB.setMinWidth(USE_PREF_SIZE);
            chooseModelRB.setPrefHeight(25);
            chooseModelRB.setMinHeight(USE_PREF_SIZE);
            chooseModelRB.setTranslateX(10);
            chooseModelRB.setToggleGroup(radioButtonToggleGroup);
            chooseModelRB.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    for (CommonLeadSampleTreeInterface treeItem : commonLeadSpotToolBarTargets) {
                        if (treeItem instanceof CommonLeadSampleToolBar) {
                            treeItem.getChooseModelRB().setSelected(newValue);
                        } else {
                            // we have spot display item  
                            if (newValue) {
                                ShrimpFractionExpressionInterface itemSpot = ((commonLeadSpotDisplay) treeItem).getSpot();
                                itemSpot.getCommonLeadSpecsForSpot().setMethodSelected(METHOD_COMMON_LEAD_MODEL);
                                itemSpot.getCommonLeadSpecsForSpot().updateCommonLeadRatiosFromModel();
                            }
                        }
                    }
                    if (newValue) {
                        if (commonLeadSpotToolBarTargets.get(0) instanceof commonLeadSpotDisplay) {
                            // update
                            ((Task) squidProject.getTask()).evaluateExpressionsForSampleGroup(sampleGroup);

                            for (CommonLeadSampleTreeInterface treeItem : commonLeadSpotToolBarTargets) {
                                ((commonLeadSpotDisplay) treeItem).displayData();
                            }
                        }
                    }
                }
            });
            commonLeadModelsRBsVbox.getChildren().add(chooseModelRB);
            getChildren().add(commonLeadModelsRBsVbox);

            commonLeadModelsVbox = new VBox();
            commonLeadModels = new ComboBox<>();
            commonLeadModels.setStyle("-fx-font-size: 11px;");
            commonLeadModels.setPrefWidth(200);
            commonLeadModels.setMinWidth(USE_PREF_SIZE);
            commonLeadModels.setConverter(new ParameterModelStringConverter());
            commonLeadModels.setItems(FXCollections.observableArrayList(squidLabData.getCommonPbModels()));
            commonLeadModels.getSelectionModel().select(squidProject.getTask().getCommonPbModel());
            commonLeadModelsVbox.getChildren().add(commonLeadModels);
            this.getChildren().add(commonLeadModelsVbox);

            // stacey Kramer
            staceyKramerRBsVbox = new VBox();
            chooseSKRB = new RadioButton("Stacey Kramer");
            chooseSKRB.setPrefWidth(125);
            chooseSKRB.setMinWidth(USE_PREF_SIZE);
            chooseSKRB.setPrefHeight(25);
            chooseSKRB.setMinHeight(USE_PREF_SIZE);
            chooseSKRB.setTranslateX(10);
            chooseSKRB.setToggleGroup(radioButtonToggleGroup);
            chooseSKRB.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    // use first treeitem to establish groups expression for 
                    ExpressionTreeInterface selectedAgeExpression = null;
                    if (commonLeadSpotToolBarTargets.get(0) instanceof commonLeadSpotDisplay) {
                        selectedAgeExpression = squidProject.getTask().getNamedExpressionsMap().
                                get(((commonLeadSpotDisplay) commonLeadSpotToolBarTargets.get(0)).
                                        getSpot().getCommonLeadSpecsForSpot().getSampleAgeType().getExpressionName());
                    }

                    if (newValue) {
                        if (commonLeadSpotToolBarTargets.get(0) instanceof CommonLeadSampleToolBar) {
                            for (CommonLeadSampleTreeInterface treeItem : commonLeadSpotToolBarTargets) {
                                treeItem.getChooseSKRB().setSelected(newValue);
                            }
                        } else {
                            // run SK 5 times per Ludwig
                            for (int i = 0; i < 5; i++) {
                                for (CommonLeadSampleTreeInterface treeItem : commonLeadSpotToolBarTargets) {
                                    // we have spot display item  
                                    ShrimpFractionExpressionInterface itemSpot = ((commonLeadSpotDisplay) treeItem).getSpot();
                                    itemSpot.getCommonLeadSpecsForSpot().setMethodSelected(METHOD_STACEY_KRAMER);
                                    itemSpot.getCommonLeadSpecsForSpot().updateCommonLeadRatiosFromSK(
                                            itemSpot.getTaskExpressionsEvaluationsPerSpot().get(selectedAgeExpression)[0][0]);
                                }
                                // update
                                ((Task) squidProject.getTask()).evaluateExpressionsForSampleGroup(sampleGroup);
                            }

                            // display
                            for (CommonLeadSampleTreeInterface treeItem : commonLeadSpotToolBarTargets) {
                                ((commonLeadSpotDisplay) treeItem).displayData();
                            }
                        }
                    }
                }
            });
            staceyKramerRBsVbox.getChildren().add(chooseSKRB);
            getChildren().add(staceyKramerRBsVbox);
        }

        /**
         * @return the chooseModelRB
         */
        @Override
        public RadioButton getChooseModelRB() {
            return chooseModelRB;
        }

        /**
         * @return the chooseModelRB
         */
        @Override
        public RadioButton getChooseSKRB() {
            return chooseSKRB;
        }

        @Override
        public void addCommonLeadModelRBDependency(CommonLeadSampleTreeInterface commonLeadToolBar) {
            this.commonLeadSpotToolBarTargets.add(commonLeadToolBar);
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
