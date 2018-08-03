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
package org.cirdles.squid.gui.plots;

import org.cirdles.squid.gui.plots.topsoil.TopsoilPlotWetherill;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.gui.plots.squid.WeightedMeanPlot;
import static org.cirdles.squid.gui.topsoil.TopsoilDataFactory.prepareWetherillDatum;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class PlotsController implements Initializable {

    public static PlotDisplayInterface plot;
    private static Node topsoilPlotNode;

    @FXML
    private VBox vboxMaster;
    @FXML
    private ToolBar plotToolBar;
    @FXML
    private TreeView<SampleTreeNodeInterface> fractionsTreeView1;

    private static ObservableList<SampleTreeNodeInterface> fractionNodes;
    private static List<Map<String, Object>> data;
    private static Map<String, List<Map<String, Object>>> dataSets;
    private static Map<String, PlotDisplayInterface> mapOfPlotsOfSpotSets;

    @FXML
    private VBox plotVBox;
    @FXML
    private RadioButton corr4_RadioButton;
    @FXML
    private ToggleGroup correctionToggleGroup;
    @FXML
    private RadioButton corr8_RadioButton;
    @FXML
    private RadioButton wetherillRadioButton;
    @FXML
    private ToggleGroup plotToggleGroup;
    @FXML
    private RadioButton terWasserburgRadioButton;
    @FXML
    private RadioButton weightedMeanRadioButton;
    @FXML
    private RadioButton corr7_RadioButton;
    @FXML
    private AnchorPane plotAndConfigAnchorPane;

    public static PlotTypes plotSelected = PlotTypes.CONCORDIA;

    public static enum PlotTypes {
        CONCORDIA("CONCORDIA"),
        TERA_WASSERBURG("TERA_WASSERBURG"),
        WEIGHTED_MEAN("WEIGHTED_MEAN");

        private String plotType;

        private PlotTypes(String plotType) {
            this.plotType = plotType;
        }
    }

    public static FractionTypes fractionTypeSelected = FractionTypes.REFERENCE_MATERIAL;

    public static enum FractionTypes {
        REFERENCE_MATERIAL("REFERENCE_MATERIAL"),
        UNKNOWN("UNKNOWN");

        private String plotType;

        private FractionTypes(String plotType) {
            this.plotType = plotType;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // update 
        squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport();

        vboxMaster.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        vboxMaster.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(PIXEL_OFFSET_FOR_MENU));

        corr4_RadioButton.setUserData("4-corr");
        corr7_RadioButton.setUserData("7-corr");
        corr8_RadioButton.setUserData("8-corr");

        showConcordiaPlotUnknowns();
    }

    private void showConcordiaPlot() {
        // default is reference materials
        List<ShrimpFractionExpressionInterface> shrimpFractionsDetails
                = squidProject.getTask().getReferenceMaterialSpots();
        boolean isUnknown = false;

        if (fractionTypeSelected.compareTo(FractionTypes.UNKNOWN) == 0) {
            shrimpFractionsDetails = squidProject.getTask().getUnknownSpots();
            isUnknown = true;
        }

        // get type of correctionList
        String correction = (String) correctionToggleGroup.getSelectedToggle().getUserData();

        plot = new TopsoilPlotWetherill(
                "Concordia of " + correction + " for " + ((Task) squidProject.getTask()).getFilterForRefMatSpotNames(),
                shrimpFractionsDetails);

        topsoilPlotNode = plot.displayPlotAsNode();

        plotAndConfigAnchorPane.getChildren().setAll(topsoilPlotNode);
        AnchorPane.setLeftAnchor(topsoilPlotNode, 0.0);
        AnchorPane.setRightAnchor(topsoilPlotNode, 0.0);
        AnchorPane.setTopAnchor(topsoilPlotNode, 0.0);
        AnchorPane.setBottomAnchor(topsoilPlotNode, 0.0);

        VBox.setVgrow(plotAndConfigAnchorPane, Priority.ALWAYS);
        VBox.setVgrow(topsoilPlotNode, Priority.ALWAYS);
        VBox.setVgrow(plotVBox, Priority.ALWAYS);

        plotToolBar.getItems().clear();
        plotToolBar.getItems().addAll(plot.toolbarControlsFactory());
        plotToolBar.setPadding(Insets.EMPTY);

        List<SampleTreeNodeInterface> fractionNodeDetails = new ArrayList<>();
        data = new ArrayList<>();

        for (int i = 0; i < shrimpFractionsDetails.size(); i++) {
            SampleTreeNodeInterface fractionNode
                    = new ConcordiaFractionNode(shrimpFractionsDetails.get(i), correction, isUnknown);
            fractionNodeDetails.add(fractionNode);
            data.add(((ConcordiaFractionNode) fractionNode).getDatum());
        }
        fractionNodes = FXCollections.observableArrayList(fractionNodeDetails);
        plot.setData(data);

        CheckBoxTreeItem<SampleTreeNodeInterface> rootItem
                = new CheckBoxTreeItem<>(new SampleNode(((Task) squidProject.getTask()).getFilterForRefMatSpotNames()));
        rootItem.setExpanded(true);

        fractionsTreeView1.setCellFactory(p -> new CheckBoxTreeCell<>(
                (TreeItem<SampleTreeNodeInterface> item) -> ((ConcordiaFractionNode) item.getValue()).getSelectedProperty(),
                new StringConverter<TreeItem<SampleTreeNodeInterface>>() {

            @Override
            public String toString(TreeItem<SampleTreeNodeInterface> object) {
                SampleTreeNodeInterface item = object.getValue();
                return item.getNodeName();
            }

            @Override
            public TreeItem<SampleTreeNodeInterface> fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }));

        for (int i = 0; i < fractionNodes.size(); i++) {
            final CheckBoxTreeItem<SampleTreeNodeInterface> checkBoxTreeItem
                    = new CheckBoxTreeItem<>(fractionNodes.get(i));
            rootItem.getChildren().add(checkBoxTreeItem);
            checkBoxTreeItem.setSelected(fractionNodes.get(i).getShrimpFraction().isSelected());
            checkBoxTreeItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
                ((ConcordiaFractionNode) checkBoxTreeItem.getValue()).setSelectedProperty(new SimpleBooleanProperty(newValue));
                plot.setData(data);
            });
        }
        fractionsTreeView1.setRoot(rootItem);
        fractionsTreeView1.setShowRoot(true);
    }

    private void showConcordiaPlotUnknowns() {
        List<ShrimpFractionExpressionInterface> unknownShrimpFractions
                = squidProject.getTask().getUnknownSpots();

        Map<String, List<ShrimpFractionExpressionInterface>> mapOfUnknownsBySampleNames
                = squidProject.getTask().getMapOfUnknownsBySampleNames();

        List<SampleTreeNodeInterface> fractionNodeDetails = new ArrayList<>();

        data = new ArrayList<>();

        // get type of correctionList
        String correction = (String) correctionToggleGroup.getSelectedToggle().getUserData();

        // build out set of data for samples
        CheckBoxTreeItem<SampleTreeNodeInterface> rootItem
                = new CheckBoxTreeItem<>(new SampleNode("UNKNOWNS"));
        rootItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            ((SampleNode) rootItem.getValue()).setSelectedProperty(new SimpleBooleanProperty(newValue));
            if (newValue) {
                //uncheck samples
                for (int i = 0; i < rootItem.getChildren().size(); i++) {
                    ((CheckBoxTreeItem<SampleTreeNodeInterface>) rootItem.getChildren().get(i)).setSelected(false);
                }
                // plot all unknowns
                plot = new TopsoilPlotWetherill(
                        "Concordia of " + correction + " for Uknowns",
                        unknownShrimpFractions);
                plot.setData(data);
                refreshPlot();
            }
        });

        rootItem.setExpanded(true);
        rootItem.setIndependent(true);
        rootItem.setSelected(true);
        fractionsTreeView1.setRoot(rootItem);
        fractionsTreeView1.setShowRoot(true);

        dataSets = new TreeMap<>();
        mapOfPlotsOfSpotSets = new TreeMap<>();
        for (Map.Entry<String, List<ShrimpFractionExpressionInterface>> entry : mapOfUnknownsBySampleNames.entrySet()) {
            CheckBoxTreeItem<SampleTreeNodeInterface> sampleItem
                    = new CheckBoxTreeItem<>(new SampleNode(entry.getKey()));
            rootItem.getChildren().add(sampleItem);

            List<Map<String, Object>> myData = new ArrayList<>();
            dataSets.put(entry.getKey(), myData);

            PlotDisplayInterface myPlot = new TopsoilPlotWetherill(
                    "Concordia of " + correction + " for " + entry.getKey(),
                    entry.getValue());
            mapOfPlotsOfSpotSets.put(sampleItem.getValue().getNodeName(), myPlot);
            myPlot.setData(myData);

            for (ShrimpFractionExpressionInterface spot : entry.getValue()) {
                SampleTreeNodeInterface fractionNode
                        = new ConcordiaFractionNode(spot, correction, true);
                fractionNodeDetails.add(fractionNode);
                CheckBoxTreeItem<SampleTreeNodeInterface> checkBoxTreeItem
                        = new CheckBoxTreeItem<>(fractionNode);
                checkBoxTreeItem.setSelected(true);
                sampleItem.getChildren().add(checkBoxTreeItem);

                checkBoxTreeItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    ((ConcordiaFractionNode) checkBoxTreeItem.getValue()).setSelectedProperty(new SimpleBooleanProperty(newValue));
                    myPlot.setData(myData);
                });

                myData.add(((ConcordiaFractionNode) fractionNode).getDatum());
                data.add(((ConcordiaFractionNode) fractionNode).getDatum());
                checkBoxTreeItem.setIndependent(true);
            }

            sampleItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
                ((SampleNode) sampleItem.getValue()).setSelectedProperty(new SimpleBooleanProperty(newValue));
                // remove plot in case none chosen
                plot = new TopsoilPlotWetherill("NO DATA SELECTED", new ArrayList<>());
                refreshPlot();

                if (newValue) {
                    // uncheck rootItem
                    ((CheckBoxTreeItem<SampleTreeNodeInterface>) rootItem).setSelected(false);
                    // uncheck others
                    for (int i = 0; i < rootItem.getChildren().size(); i++) {
                        if (rootItem.getChildren().get(i) != sampleItem) {
                            ((CheckBoxTreeItem<SampleTreeNodeInterface>) rootItem.getChildren().get(i)).setSelected(false);
                        }
                    }

                    plot = mapOfPlotsOfSpotSets.get(sampleItem.getValue().getNodeName());
                    refreshPlot();
                }
            });
            sampleItem.setIndependent(true);
        }

        fractionNodes = FXCollections.observableArrayList(fractionNodeDetails);

        fractionsTreeView1.setCellFactory(p -> new CheckBoxTreeCell<>(
                (TreeItem<SampleTreeNodeInterface> item) -> ((ConcordiaFractionNode) item.getValue()).getSelectedProperty(),
                new StringConverter<TreeItem<SampleTreeNodeInterface>>() {

            @Override
            public String toString(TreeItem<SampleTreeNodeInterface> object) {
                SampleTreeNodeInterface item = object.getValue();
                return item.getNodeName();
            }

            @Override
            public TreeItem<SampleTreeNodeInterface> fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }));
    }

    private void refreshPlot() {
        topsoilPlotNode = plot.displayPlotAsNode();
        plotAndConfigAnchorPane.getChildren().setAll(topsoilPlotNode);
        AnchorPane.setLeftAnchor(topsoilPlotNode, 0.0);
        AnchorPane.setRightAnchor(topsoilPlotNode, 0.0);
        AnchorPane.setTopAnchor(topsoilPlotNode, 0.0);
        AnchorPane.setBottomAnchor(topsoilPlotNode, 0.0);

        VBox.setVgrow(plotAndConfigAnchorPane, Priority.ALWAYS);
        VBox.setVgrow(topsoilPlotNode, Priority.ALWAYS);
        VBox.setVgrow(plotVBox, Priority.ALWAYS);

        plotToolBar.getItems().clear();
        plotToolBar.getItems().addAll(plot.toolbarControlsFactory());
        plotToolBar.setPadding(Insets.EMPTY);
    }

    private void showWeightedMeanPlot() {
        // get type of correction
        String correction = (String) correctionToggleGroup.getSelectedToggle().getUserData();
        // get details
        SpotSummaryDetails spotSummaryDetails = squidProject.getTask().getTaskExpressionsEvaluationsPerSpotSet().get(correction + "206Pb/238Ucalibr.const WM");
        plot = new WeightedMeanPlot(
                new Rectangle(1000, 600),
                "Weighted Mean of " + correction + " for " + ((Task) squidProject.getTask()).getFilterForRefMatSpotNames(),
                spotSummaryDetails,
                correction + "206Pb/238U Age",
                559.1 * 1e6);

        Node plotNode = plot.displayPlotAsNode();

        plotAndConfigAnchorPane.getChildren().setAll(plotNode);
        AnchorPane.setLeftAnchor(plotNode, 0.0);
        AnchorPane.setRightAnchor(plotNode, 0.0);
        AnchorPane.setTopAnchor(plotNode, 0.0);
        AnchorPane.setBottomAnchor(plotNode, 0.0);

        VBox.setVgrow(plotAndConfigAnchorPane, Priority.ALWAYS);
        VBox.setVgrow(plotNode, Priority.ALWAYS);
        VBox.setVgrow(plotVBox, Priority.ALWAYS);

        plotToolBar.getItems().clear();
        plotToolBar.getItems().addAll(plot.toolbarControlsFactory());
        plotToolBar.setPadding(Insets.EMPTY);

        List<ShrimpFractionExpressionInterface> shrimpFractionsDetails
                = spotSummaryDetails.getSelectedSpots();
        List<SampleTreeNodeInterface> fractionNodeDetails = new ArrayList<>();
        data = new ArrayList<>();

        for (int i = 0; i < shrimpFractionsDetails.size(); i++) {
            WeightedMeanFractionNode fractionNode
                    = new WeightedMeanFractionNode(shrimpFractionsDetails.get(i), spotSummaryDetails.getRejectedIndices());
            fractionNodeDetails.add(fractionNode);
        }

        fractionNodes = FXCollections.observableArrayList(fractionNodeDetails);

        CheckBoxTreeItem<SampleTreeNodeInterface> rootItem
                = new CheckBoxTreeItem<>(new SampleNode(((Task) squidProject.getTask()).getFilterForRefMatSpotNames()));
        rootItem.setExpanded(true);

        fractionsTreeView1.setCellFactory(p -> new CheckBoxTreeCell<>(
                (TreeItem<SampleTreeNodeInterface> item) -> ((WeightedMeanFractionNode) item.getValue()).getSelectedProperty(),
                new StringConverter<TreeItem<SampleTreeNodeInterface>>() {

            @Override
            public String toString(TreeItem<SampleTreeNodeInterface> object) {
                SampleTreeNodeInterface item = object.getValue();
                return item.getNodeName();
            }

            @Override
            public TreeItem<SampleTreeNodeInterface> fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }));

        for (int i = 0; i < fractionNodes.size(); i++) {
            final CheckBoxTreeItem<SampleTreeNodeInterface> checkBoxTreeItem
                    = new CheckBoxTreeItem<>(fractionNodes.get(i));
            rootItem.getChildren().add(checkBoxTreeItem);
            checkBoxTreeItem.setSelected(!((WeightedMeanFractionNode) fractionNodes.get(i)).getRejected(i));
            checkBoxTreeItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
                ((ConcordiaFractionNode) checkBoxTreeItem.getValue()).setSelectedProperty(new SimpleBooleanProperty(newValue));
                plot.setData(data);
            });
        }
        fractionsTreeView1.setRoot(rootItem);
        fractionsTreeView1.setShowRoot(true);
    }

    @FXML
    private void corr4_RadioButtonAction(ActionEvent event) {
        toggleToolBarButtonsDisabled(false, false, false, false, false);
        if (weightedMeanRadioButton.isSelected()) {
            showWeightedMeanPlot();
        }
        if (wetherillRadioButton.isSelected()) {
            showConcordiaPlot();
        }
    }

    @FXML
    private void corr7_RadioButtonAction(ActionEvent event) {
        toggleToolBarButtonsDisabled(false, false, true, true, false);
        weightedMeanRadioButton.setSelected(true);
        if (weightedMeanRadioButton.isSelected()) {
            showWeightedMeanPlot();
        }
        if (wetherillRadioButton.isSelected()) {
            showConcordiaPlot();
        }
    }

    @FXML
    private void corr8_RadioButtonAction(ActionEvent event) {
        toggleToolBarButtonsDisabled(false, false, false, false, false);
        if (weightedMeanRadioButton.isSelected()) {
            showWeightedMeanPlot();
        }
        if (wetherillRadioButton.isSelected()) {
            showConcordiaPlot();
        }
    }

    @FXML
    private void wetherillRadioButtonAction(ActionEvent event) {
        toggleToolBarButtonsDisabled(true, false, false, false, false);
        plotSelected = PlotTypes.CONCORDIA;
        showConcordiaPlot();
    }

    @FXML
    private void terWasserburgRadioButtonAction(ActionEvent event) {
        toggleToolBarButtonsDisabled(true, false, false, false, false);
    }

    @FXML
    private void weightedMeanRadioButtonAction(ActionEvent event) {
        toggleToolBarButtonsDisabled(false, false, false, false, false);
        plotSelected = PlotTypes.WEIGHTED_MEAN;
        showWeightedMeanPlot();
    }

    private void toggleToolBarButtonsDisabled(boolean corr7, boolean corr8, boolean concordia, boolean TW, boolean WM) {
        corr7_RadioButton.setDisable(corr7);
        corr8_RadioButton.setDisable(corr8);
        wetherillRadioButton.setDisable(concordia);
        terWasserburgRadioButton.setDisable(TW);
        weightedMeanRadioButton.setDisable(WM);

    }

    private interface SampleTreeNodeInterface {

        public String getNodeName();

        public ShrimpFractionExpressionInterface getShrimpFraction();

        /**
         * @return the selectedProperty
         */
        public SimpleBooleanProperty getSelectedProperty();

        /**
         * @param selectedProperty the selectedProperty to set
         */
        public void setSelectedProperty(SimpleBooleanProperty selectedProperty);
    }

    private class SampleNode implements SampleTreeNodeInterface {

        private String sampleName;
        private SimpleBooleanProperty selectedProperty;

        public SampleNode(String sampleName) {
            this.sampleName = sampleName;
            this.selectedProperty = new SimpleBooleanProperty(false);
        }

        @Override
        public String getNodeName() {
            return sampleName;
        }

        @Override
        public ShrimpFractionExpressionInterface getShrimpFraction() {
            return null;
        }

        @Override
        public SimpleBooleanProperty getSelectedProperty() {
            return selectedProperty;
        }

        @Override
        public void setSelectedProperty(SimpleBooleanProperty selectedProperty) {
            this.selectedProperty = selectedProperty;
        }
    }

    private class ConcordiaFractionNode implements SampleTreeNodeInterface {

        private ShrimpFractionExpressionInterface shrimpFraction;
        private Map<String, Object> datum;
        private SimpleBooleanProperty selectedProperty;

        public ConcordiaFractionNode(ShrimpFractionExpressionInterface shrimpFraction, String correction, boolean isUnknown) {
            this.shrimpFraction = shrimpFraction;
            this.selectedProperty = new SimpleBooleanProperty(shrimpFraction.isSelected());
            this.datum = prepareWetherillDatum(shrimpFraction, correction, isUnknown);
            this.datum.put("Selected", shrimpFraction.isSelected());
        }

        /**
         * @return the shrimpFraction
         */
        @Override
        public ShrimpFractionExpressionInterface getShrimpFraction() {
            return shrimpFraction;
        }

        /**
         * @return the datum
         */
        public Map<String, Object> getDatum() {
            return datum;
        }

        /**
         * @return the selectedProperty
         */
        public SimpleBooleanProperty getSelectedProperty() {
            return selectedProperty;
        }

        /**
         * @param selectedProperty the selectedProperty to set
         */
        public void setSelectedProperty(SimpleBooleanProperty selectedProperty) {
            this.selectedProperty = selectedProperty;
            this.shrimpFraction.setSelected(selectedProperty.getValue());
            this.datum.put("Selected", selectedProperty.getValue());
        }

        @Override
        public String getNodeName() {
            return shrimpFraction.getFractionID();
        }

    }

    private class WeightedMeanFractionNode implements SampleTreeNodeInterface {

        /**
         * @return the rejected
         */
        public boolean getRejected(int index) {
            return rejected[index];
        }

        private ShrimpFractionExpressionInterface shrimpFraction;
        private SimpleBooleanProperty selectedProperty;
        private boolean[] rejected;

        public WeightedMeanFractionNode(ShrimpFractionExpressionInterface shrimpFraction, boolean[] rejected) {
            this.shrimpFraction = shrimpFraction;
            this.selectedProperty = new SimpleBooleanProperty(shrimpFraction.isSelected());
            this.rejected = rejected;
        }

        /**
         * @return the shrimpFraction
         */
        public ShrimpFractionExpressionInterface getShrimpFraction() {
            return shrimpFraction;
        }

        /**
         * @return the selectedProperty
         */
        public SimpleBooleanProperty getSelectedProperty() {
            return selectedProperty;
        }

        /**
         * @param selectedProperty the selectedProperty to set
         */
        public void setSelectedProperty(SimpleBooleanProperty selectedProperty) {
            this.selectedProperty = selectedProperty;
            this.shrimpFraction.setSelected(selectedProperty.getValue());
        }

        @Override
        public String getNodeName() {
            return shrimpFraction.getFractionID();
        }
    }
}
