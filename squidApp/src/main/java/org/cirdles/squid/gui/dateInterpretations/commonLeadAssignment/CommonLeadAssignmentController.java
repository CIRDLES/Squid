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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
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
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;

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

    private TreeView<CommonLeadToolBar> spotsTreeViewCommonLeadTools = new TreeView<>();

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

        showUnknownsWithOvercountCorrections();
    }

    private void showUnknownsWithOvercountCorrections() {

        Map<String, List<ShrimpFractionExpressionInterface>> mapOfSpotsBySampleNames;
        mapOfSpotsBySampleNames = squidProject.getTask().getMapOfUnknownsBySampleNames();
        // case of sample names chosen
        if (mapOfSpotsBySampleNames.size() > 1) {
            mapOfSpotsBySampleNames.remove(Squid3Constants.SpotTypes.UNKNOWN.getPlotType());
        }

        CommonLeadToolBar toolBarSampleType = new CommonLeadSampleToolBar(Squid3Constants.SpotTypes.UNKNOWN.getPlotType());
        TreeItem<CommonLeadToolBar> rootItemSamples = new TreeItem<>(toolBarSampleType);

        spotsTreeViewCommonLeadTools.setStyle("-fx-font-size: 12px;");
        spotsTreeViewCommonLeadTools.setRoot(rootItemSamples);

        rootItemSamples.setExpanded(true);
        spotsTreeViewCommonLeadTools.setShowRoot(true);

        for (Map.Entry<String, List<ShrimpFractionExpressionInterface>> entry : mapOfSpotsBySampleNames.entrySet()) {
            CommonLeadToolBar toolBarSampleName = new CommonLeadToolBar(entry.getKey());
            TreeItem<CommonLeadToolBar> treeItemSampleInfo = new TreeItem<>(toolBarSampleName);

            for (ShrimpFractionExpressionInterface spot : entry.getValue()) {
                CommonLeadToolBar toolBarSpotName = new CommonLeadToolBar(spot.getFractionID());
                TreeItem<CommonLeadToolBar> treeItemSpotInfo = new TreeItem<>(toolBarSpotName);
                toolBarSampleName.addCommonLeadModelRBDependency(toolBarSpotName);

                treeItemSampleInfo.getChildren().add(treeItemSpotInfo);
            }

            rootItemSamples.getChildren().add(treeItemSampleInfo);
        }

        sampleTreeAnchorPane.getChildren().clear();
        sampleTreeAnchorPane.getChildren().add(spotsTreeViewCommonLeadTools);
    }

    private class CommonLeadToolBar extends HBox {

        @FXML
        protected TextField nodeName;

        @FXML
        ToggleGroup radioButtonToggleGroup = new ToggleGroup(); 
        
        @FXML
        protected RadioButton chooseModelRB;
        protected List<RadioButton> chooseModelRBTargets;

        @FXML
        protected ComboBox<ParametersModel> commonLeadModels;

        @FXML
        protected VBox commonLeadModelsRBsVbox;
        @FXML
        protected VBox commonLeadModelsVbox;

        public CommonLeadToolBar(String spotName) {
            super(5);

            nodeName = new TextField(spotName);
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
            commonLeadModelsRBsVbox.getChildren().add(chooseModelRB);
            getChildren().add(commonLeadModelsRBsVbox);

            radioButtonToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                public void changed(ObservableValue<? extends Toggle> ob,
                        Toggle o, Toggle n) {

                    RadioButton rb = (RadioButton) radioButtonToggleGroup.getSelectedToggle();

                    if (rb != null) {
                        for (RadioButton radioButton : chooseModelRBTargets){
                            radioButton.setSelected(rb.isSelected());
                        }
                    }
                }
            });

            chooseModelRBTargets = new ArrayList<>();

            commonLeadModelsVbox = new VBox();
            commonLeadModels = new ComboBox<>();
            commonLeadModels.setStyle("-fx-font-size: 11px;");
            commonLeadModels.setPrefWidth(200);
            commonLeadModels.setMinWidth(USE_PREF_SIZE);
            commonLeadModels.setConverter(new ParameterModelStringConverter());
            commonLeadModels.setItems(FXCollections.observableArrayList(squidLabData.getCommonPbModels()));
            commonLeadModels.getSelectionModel().select(squidProject.getTask().getCommonPbModel());
            commonLeadModelsVbox.getChildren().add(commonLeadModels);
            getChildren().add(commonLeadModelsVbox);

        }

        protected void addCommonLeadModelRBDependency(CommonLeadToolBar commonLeadToolBar) {
            this.chooseModelRBTargets.add(commonLeadToolBar.getChooseModelRB());
        }

        /**
         * @return the chooseModelRB
         */
        public RadioButton getChooseModelRB() {
            return chooseModelRB;
        }
    }

    private class CommonLeadSampleToolBar extends CommonLeadToolBar {

        @FXML
        protected Button fillCommonLeadModelsRB;
        @FXML
        protected Button fillCommonLeadModelsComboButton;

        public CommonLeadSampleToolBar(String spotName) {
            super(spotName + "?");

            fillCommonLeadModelsRB = new Button("↓↓↓");
            fillCommonLeadModelsRB.setStyle(fillCommonLeadModelsRB.getStyle() + "-fx-font-size: 14px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
            fillCommonLeadModelsRB.setPrefWidth(35);
            fillCommonLeadModelsRB.setMinWidth(USE_PREF_SIZE);
            fillCommonLeadModelsRB.setPrefHeight(25);
            fillCommonLeadModelsRB.setMinHeight(USE_PREF_SIZE);
            fillCommonLeadModelsRB.setAlignment(Pos.CENTER);
            fillCommonLeadModelsRB.setTranslateY(-3);
            fillCommonLeadModelsRB.setPadding(new Insets(5, -2, 5, -2));
            commonLeadModelsRBsVbox.getChildren().add(fillCommonLeadModelsRB);

            fillCommonLeadModelsComboButton = new Button("↓↓↓ Fill");
            fillCommonLeadModelsComboButton.setStyle(fillCommonLeadModelsComboButton.getStyle() + "-fx-font-size: 14px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
            fillCommonLeadModelsComboButton.setPrefWidth(200);
            fillCommonLeadModelsComboButton.setMinWidth(USE_PREF_SIZE);
            fillCommonLeadModelsComboButton.setPrefHeight(25);
            fillCommonLeadModelsComboButton.setMinHeight(USE_PREF_SIZE);
            fillCommonLeadModelsComboButton.setAlignment(Pos.CENTER);
            commonLeadModelsVbox.getChildren().add(fillCommonLeadModelsComboButton);
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
