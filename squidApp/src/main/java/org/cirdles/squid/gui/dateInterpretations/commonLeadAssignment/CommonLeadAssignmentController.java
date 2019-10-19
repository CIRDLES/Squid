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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;
import org.cirdles.squid.constants.Squid3Constants;
import static org.cirdles.squid.constants.Squid3Constants.ABS_UNCERTAINTY_DIRECTIVE;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidLabData;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.StaceyKramerCommonLeadModel;
import org.cirdles.squid.projects.SquidProject;
import static org.cirdles.squid.shrimp.CommonLeadSpecsForSpot.METHOD_COMMON_LEAD_MODEL;
import static org.cirdles.squid.shrimp.CommonLeadSpecsForSpot.METHOD_CUSTOM_COMMON_LEAD;
import static org.cirdles.squid.shrimp.CommonLeadSpecsForSpot.METHOD_STACEY_KRAMER;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.Task;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB7CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB8CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REF_238U235U_RM_MODEL_NAME;
import org.cirdles.squid.tasks.expressions.builtinExpressions.SampleAgeTypesEnum;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

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
    private AnchorPane sampleTreeAnchorPane;

    private TreeView<CommonLeadSampleTreeInterface> spotsTreeViewCommonLeadTools = new TreeView<>();

    private ExpressionTreeInterface expPB4COR206_238AGE;
    private ExpressionTreeInterface expPB4COR208_232AGE;
    private ExpressionTreeInterface expPB4COR207_206AGE;
    private ExpressionTreeInterface expPB7COR206_238AGE;
    private ExpressionTreeInterface expPB7COR208_232AGE;
    private ExpressionTreeInterface expPB8COR206_238AGE;
    private ExpressionTreeInterface expPB8COR207_206AGE;

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

        ((Task) squidProject.getTask()).evaluateUnknownsWithChangedParameters(squidProject.getTask().getUnknownSpots());

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
                CommonLeadSampleTreeInterface treeItemSpotDisplay = new CommonLeadSpotDisplay(spot);
                TreeItem<CommonLeadSampleTreeInterface> treeItemSpotInfo = new TreeItem<>(treeItemSpotDisplay);
                toolBarSampleName.addSpotDependency(treeItemSpotDisplay);

                treeItemSampleInfo.getChildren().add(treeItemSpotInfo);
            }

            suppressChangeAction = true;
            // set radio button based on first spot
            switch (((CommonLeadSpotDisplay) treeItemSampleInfo.getChildren().get(0).getValue()).getSpot().getCommonLeadSpecsForSpot().getMethodSelected()) {
                case METHOD_COMMON_LEAD_MODEL:
                    treeItemSampleInfo.getValue().getChooseModelRB().setSelected(true);
                    break;
                case METHOD_STACEY_KRAMER:
                    treeItemSampleInfo.getValue().getChooseSKRB().setSelected(true);
                    break;
                case METHOD_CUSTOM_COMMON_LEAD:
                    break;
                default:
            }

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

    private interface CommonLeadSampleTreeInterface {

        /**
         * @return the chooseModelRB
         */
        public RadioButton getChooseModelRB();

        /**
         * @return the chooseModelRB
         */
        public RadioButton getChooseSKRB();

        public void addSpotDependency(CommonLeadSampleTreeInterface commonLeadToolBar);
    }

    private class CommonLeadSpotDisplay extends HBox implements CommonLeadSampleTreeInterface {

        private ShrimpFractionExpressionInterface spot;

        @FXML
        protected Label nodeName;

        public CommonLeadSpotDisplay(ShrimpFractionExpressionInterface spot) {
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

            addVboxFactory("206Pb/204Pb", spot.getCom_206Pb204Pb(), 0.0);
            addVboxFactory("207Pb/206Pb", spot.getCom_207Pb206Pb(), 0.0);
            addVboxFactory("208Pb/206Pb", spot.getCom_208Pb206Pb(), 0.0);

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
            VBox aVBox = new VBox(-10);

            boolean fontIsBold = false;
            Formatter formatter = new Formatter();
            if (unct > 0.0) {
                // we have an age
                formatter.format("%10.7f", value / 1e6);
                fontIsBold = spot.getSelectedAgeExpressionName().compareTo(label) == 0;
            } else {
                // we have a ratio
                formatter.format("%7.4f", value);
                fontIsBold = value > 0.0;
            }

            // selected age in bold
            Label alabel = new Label(label);
            alabel.getStyleClass().clear();
            alabel.setFont(Font.font("Monospaced", fontIsBold ? FontWeight.BOLD : FontWeight.MEDIUM, 8));
            alabel.setPrefWidth(100);
            alabel.setMinWidth(USE_PREF_SIZE);
            alabel.setPrefHeight(20);
            alabel.setMinHeight(USE_PREF_SIZE);

            Label aValue = new Label(formatter.toString());
            aValue.getStyleClass().clear();
            aValue.setFont(Font.font("Monospaced", fontIsBold ? FontWeight.BOLD : FontWeight.MEDIUM, 12));
            if ((value <= 0.0)|| Double.isNaN(value)) {
                aValue.setTextFill(Paint.valueOf("red"));
                aValue.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
            }
            aValue.setPrefWidth(100);
            aValue.setMinWidth(USE_PREF_SIZE);
            aValue.setPrefHeight(20);
            aValue.setMinHeight(USE_PREF_SIZE);

            formatter = new Formatter();
            Label bValue = new Label(unct > 0.0 ? ABS_UNCERTAINTY_DIRECTIVE + " " + formatter.format("%10.7f", unct / 1e6).toString() : "");
            bValue.getStyleClass().clear();
            bValue.setFont(Font.font("Monospaced", fontIsBold ? FontWeight.BOLD : FontWeight.MEDIUM, 12));
            bValue.setPrefWidth(102);
            bValue.setMinWidth(USE_PREF_SIZE);
            bValue.setPrefHeight(20);
            bValue.setMinHeight(USE_PREF_SIZE);

            aVBox.getChildren().addAll(alabel, aValue, bValue);

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
        public void addSpotDependency(CommonLeadSampleTreeInterface commonLeadToolBar) {
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
        ToggleGroup methodRB_ToggleGroup = new ToggleGroup();
        @FXML
        protected ToggleGroup ageRB_ToggleGroup = new ToggleGroup();

        @FXML
        protected RadioButton chooseModelRB;
        @FXML
        protected RadioButton chooseSKRB;
        @FXML
        protected RadioButton chooseCustomRB;

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
        protected VBox customModelRBVbox;
        @FXML
        protected VBox customModelButtonVbox;

        @FXML
        protected Button customButton;

        protected List<ShrimpFractionExpressionInterface> sampleGroup;

        protected List<CommonLeadSampleTreeInterface> commonLeadSpotToolBarTargets;

        public CommonLeadSampleToolBar(String sampleGroupName, List<ShrimpFractionExpressionInterface> mySampleGroup) {
            super(2);
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

                            for (CommonLeadSampleTreeInterface treeItem : commonLeadSpotToolBarTargets) {
                                ((CommonLeadSpotDisplay) treeItem).displayData();
                            }
                        }
                        SquidProject.setProjectChanged(true);
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
            commonLeadModelsVbox.getChildren().add(commonLeadModels);
            this.getChildren().add(commonLeadModelsVbox);

            // custom
//            customModelRBVbox = new VBox();
//            chooseCustomRB = new RadioButton();
//            chooseCustomRB.setId("model");
//            chooseCustomRB.setPrefWidth(27);
//            chooseCustomRB.setMinWidth(USE_PREF_SIZE);
//            chooseCustomRB.setPrefHeight(25);
//            chooseCustomRB.setMinHeight(USE_PREF_SIZE);
//            chooseCustomRB.setTranslateX(10);
//            chooseCustomRB.setToggleGroup(methodRB_ToggleGroup);
//            customModelRBVbox.getChildren().add(chooseCustomRB);
//            getChildren().add(customModelRBVbox);
//
//            customModelButtonVbox = new VBox();
//            customButton = new Button("Custom");
//            customButton.setStyle(customButton.getStyle() + "-fx-font-size: 12px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
//            customButton.setPrefWidth(55);
//            customButton.setMinWidth(USE_PREF_SIZE);
//            customButton.setPrefHeight(22);
//            customButton.setMinHeight(USE_PREF_SIZE);
//            customButton.setAlignment(Pos.CENTER);
//            //customButton.setTranslateY(-3);	
//            //customButton.setPadding(new Insets(5, -2, 5, -2));
//
//            customModelButtonVbox.getChildren().add(customButton);
//            getChildren().add(customModelButtonVbox);

            // stacey Kramer
            staceyKramerRBVbox = new VBox();
            chooseSKRB = new RadioButton("SK");
            chooseSKRB.setId("SK");
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

            HBox ageChoosersHBox = new HBox();
            ageChoosersHBox.setTranslateX(100);//15);

            ageChoosersHBox.getChildren().add(ageRadioButtonFactory(SampleAgeTypesEnum.PB4COR206_238AGE, PB4CORR));
            ageChoosersHBox.getChildren().add(ageRadioButtonFactory(SampleAgeTypesEnum.PB4COR208_232AGE, PB4CORR));
            ageChoosersHBox.getChildren().add(ageRadioButtonFactory(SampleAgeTypesEnum.PB4COR207_206AGE, PB4CORR));

            ageChoosersHBox.getChildren().add(ageRadioButtonFactory(SampleAgeTypesEnum.PB7COR206_238AGE, PB7CORR));
            ageChoosersHBox.getChildren().add(ageRadioButtonFactory(SampleAgeTypesEnum.PB7COR208_232AGE, PB7CORR));

            ageChoosersHBox.getChildren().add(ageRadioButtonFactory(SampleAgeTypesEnum.PB8COR206_238AGE, PB8CORR));
            ageChoosersHBox.getChildren().add(ageRadioButtonFactory(SampleAgeTypesEnum.PB8COR207_206AGE, PB8CORR));

            getChildren().add(ageChoosersHBox);
        }

        private RadioButton ageRadioButtonFactory(SampleAgeTypesEnum sampleAgeType, String corrString) {
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
                            ((Task) squidProject.getTask()).setUnknownGroupSelectedAge(sampleGroup, sampleAgeType);
                            ((Task) squidProject.getTask()).evaluateUnknownsWithChangedParameters(sampleGroup);

                            for (CommonLeadSampleTreeInterface treeItem : commonLeadSpotToolBarTargets) {
                                ((CommonLeadSpotDisplay) treeItem).displayData();
                            }
                        }
                        SquidProject.setProjectChanged(true);
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
         * @return the chooseModelRB
         */
        @Override
        public RadioButton getChooseSKRB() {
            return chooseSKRB;
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
