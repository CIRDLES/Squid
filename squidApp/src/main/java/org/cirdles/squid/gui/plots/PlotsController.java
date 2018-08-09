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
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_CALIB_CONST_AGE_206_238_BASENAME;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.SQUID_CALIB_CONST_AGE_208_232_BASENAME;
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
    private TreeView<SampleTreeNodeInterface> spotsTreeView;

    private static ObservableList<SampleTreeNodeInterface> fractionNodes;
    private static PlotDisplayInterface rootPlot;
    private static List<Map<String, Object>> rootData;
    private static Map<String, List<Map<String, Object>>> dataSets;
    private static Map<String, PlotDisplayInterface> mapOfPlotsOfSpotSets;
    private static CheckBoxTreeItem<SampleTreeNodeInterface> chosenSample;

    @FXML
    private VBox plotVBox;
    @FXML
    private RadioButton corr4_RadioButton;
    @FXML
    private ToggleGroup correctionToggleGroup;
    @FXML
    private RadioButton corr8_RadioButton;
    @FXML
    private RadioButton corr7_RadioButton;
    @FXML
    private AnchorPane plotAndConfigAnchorPane;

    @FXML
    private RadioButton plotFlavorOneRadioButton;
    @FXML
    private RadioButton plotFlavorTwoRadioButton;
    @FXML
    private ToggleGroup plotFlavorToggleGroup;

    public static enum PlotTypes {
        CONCORDIA("CONCORDIA"),
        TERA_WASSERBURG("TERA_WASSERBURG"),
        WEIGHTED_MEAN("WEIGHTED_MEAN");

        private String plotType;

        private PlotTypes(String plotType) {
            this.plotType = plotType;
        }
    }
    public static PlotTypes plotTypeSelected = PlotTypes.CONCORDIA;

    public static enum SpotTypes {
        REFERENCE_MATERIAL("REFERENCE MATERIALS"),
        UNKNOWN("UNKNOWNS");

        private String plotType;

        private SpotTypes(String plotType) {
            this.plotType = plotType;
        }

        public String getPlotType() {
            return plotType;
        }
    }
    public static SpotTypes fractionTypeSelected = SpotTypes.REFERENCE_MATERIAL;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // update 
        squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport();

        vboxMaster.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        vboxMaster.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(PIXEL_OFFSET_FOR_MENU));

        corr4_RadioButton.setUserData("4-corr");
        corr7_RadioButton.setUserData("7-corr");
        corr8_RadioButton.setUserData("8-corr");

        customizePlotChooserToolbarAndInvokePlotter();
    }

    private void showConcordiaPlotsOfUnknownsOrRefMat() {
        final List<ShrimpFractionExpressionInterface> allUnknownOrRefMatShrimpFractions;
        Map<String, List<ShrimpFractionExpressionInterface>> mapOfSpotsBySampleNames;
        if (fractionTypeSelected.compareTo(SpotTypes.UNKNOWN) == 0) {
            allUnknownOrRefMatShrimpFractions = squidProject.getTask().getUnknownSpots();
            mapOfSpotsBySampleNames = squidProject.getTask().getMapOfUnknownsBySampleNames();
            // case of no sample names chosen
            if (mapOfSpotsBySampleNames.size() == 0){
                mapOfSpotsBySampleNames.put("Super Sample", allUnknownOrRefMatShrimpFractions);
            }
        } else {
            allUnknownOrRefMatShrimpFractions = squidProject.getTask().getReferenceMaterialSpots();
            mapOfSpotsBySampleNames = new TreeMap<>();
            mapOfSpotsBySampleNames.put("Reference Mat", squidProject.getTask().getReferenceMaterialSpots());
            mapOfSpotsBySampleNames.put("Concentration Ref Mat", squidProject.getTask().getConcentrationReferenceMaterialSpots());
        }
        // get type of correctionList
        String correction = (String) correctionToggleGroup.getSelectedToggle().getUserData();

        rootPlot = new TopsoilPlotWetherill(
                "Concordia of " + correction + " for " + fractionTypeSelected.getPlotType(),
                allUnknownOrRefMatShrimpFractions);
        rootData = new ArrayList<>();

        List<SampleTreeNodeInterface> fractionNodeDetails = new ArrayList<>();

        // build out set of rootData for samples
        CheckBoxTreeItem<SampleTreeNodeInterface> rootItem
                = new CheckBoxTreeItem<>(new SampleNode(fractionTypeSelected.getPlotType()));
        chosenSample = rootItem;
        rootItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            ((SampleNode) rootItem.getValue()).setSelectedProperty(new SimpleBooleanProperty(newValue));
            if (newValue) {
                if (chosenSample != rootItem) {
                    chosenSample.setSelected(false);
                    chosenSample = rootItem;
                }
                // plot all samples
                plot = rootPlot;
                plot.setData(rootData);
                refreshConcordiaPlot();
            }
            refreshConcordiaPlot();
        });

        rootItem.setExpanded(true);
        rootItem.setIndependent(true);
        rootItem.setSelected(true);
        spotsTreeView.setRoot(rootItem);
        spotsTreeView.setShowRoot(true);

        dataSets = new TreeMap<>();
        mapOfPlotsOfSpotSets = new TreeMap<>();
        for (Map.Entry<String, List<ShrimpFractionExpressionInterface>> entry : mapOfSpotsBySampleNames.entrySet()) {
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
                        = new ConcordiaFractionNode(spot, correction);
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
                // this is for overall of all at the tree top
                rootData.add(((ConcordiaFractionNode) fractionNode).getDatum());
                checkBoxTreeItem.setIndependent(true);
            }

            sampleItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
                ((SampleNode) sampleItem.getValue()).setSelectedProperty(new SimpleBooleanProperty(newValue));
                if (newValue) {
                    if (chosenSample != sampleItem) {
                        chosenSample.setSelected(false);
                        chosenSample = sampleItem;
                    }
                    plot = mapOfPlotsOfSpotSets.get(sampleItem.getValue().getNodeName());
                    refreshConcordiaPlot();
                }
                refreshConcordiaPlot();
            });
            sampleItem.setIndependent(true);
        }

        fractionNodes = FXCollections.observableArrayList(fractionNodeDetails);

        spotsTreeView.setCellFactory(p -> new CheckBoxTreeCell<>(
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

    private void refreshConcordiaPlot() {

        if (chosenSample.isSelected()) {
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
        } else {
            plotAndConfigAnchorPane.getChildren().clear();
        }
    }

    private void showWeightedMeanPlot() {
        // get type of correction
        String correction = (String) correctionToggleGroup.getSelectedToggle().getUserData();
        // flavor of plot
        String calibrConstAgeBaseName = (String) plotFlavorToggleGroup.getSelectedToggle().getUserData();
        // get details
        SpotSummaryDetails spotSummaryDetails
                = squidProject.getTask().getTaskExpressionsEvaluationsPerSpotSet().get(correction
                        + " " + calibrConstAgeBaseName + "calibr.const WM");
        plot = new WeightedMeanPlot(
                new Rectangle(1000, 600),
                correction + " " + calibrConstAgeBaseName + " calibr.const Weighted Mean of Reference Material "
                + ((Task) squidProject.getTask()).getFilterForRefMatSpotNames(),
                spotSummaryDetails,
                correction + " " + calibrConstAgeBaseName + " Age",
                squidProject.getTask().getTaskExpressionsEvaluationsPerSpotSet().get("StdAgeUPb").getValues()[0][0]);//559.1 * 1e6);

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
        rootData = new ArrayList<>();

        for (int i = 0; i < shrimpFractionsDetails.size(); i++) {
            WeightedMeanFractionNode fractionNode
                    = new WeightedMeanFractionNode(shrimpFractionsDetails.get(i), spotSummaryDetails.getRejectedIndices());
            fractionNodeDetails.add(fractionNode);
        }

        fractionNodes = FXCollections.observableArrayList(fractionNodeDetails);

        CheckBoxTreeItem<SampleTreeNodeInterface> rootItem
                = new CheckBoxTreeItem<>(new SampleNode(((Task) squidProject.getTask()).getFilterForRefMatSpotNames()));
        rootItem.setExpanded(true);

        spotsTreeView.setCellFactory(p -> new CheckBoxTreeCell<>(
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
                plot.setData(rootData);
            });
        }
        spotsTreeView.setRoot(rootItem);
        spotsTreeView.setShowRoot(true);
    }

    @FXML
    private void plotChooserAction(ActionEvent event) {
        customizePlotChooserToolbarAndInvokePlotter();
    }

    private void customizePlotChooserToolbarAndInvokePlotter() {

        switch (plotTypeSelected) {
            case CONCORDIA:
            case TERA_WASSERBURG:
                plotFlavorOneRadioButton.setText("Wetherill Concordia");
                plotFlavorTwoRadioButton.setText("Tera-Wasserburg");
                plotFlavorOneRadioButton.setDisable(false);
                plotFlavorTwoRadioButton.setDisable(false);

                corr7_RadioButton.setVisible(false);
                corr8_RadioButton.setVisible(true);

                showConcordiaPlotsOfUnknownsOrRefMat();
                break;
            case WEIGHTED_MEAN:
                plotFlavorOneRadioButton.setText(SQUID_CALIB_CONST_AGE_206_238_BASENAME + "calibr.const WM");
                plotFlavorTwoRadioButton.setText(SQUID_CALIB_CONST_AGE_208_232_BASENAME + "calibr.const WM");
                plotFlavorOneRadioButton.setUserData(SQUID_CALIB_CONST_AGE_206_238_BASENAME);
                plotFlavorTwoRadioButton.setUserData(SQUID_CALIB_CONST_AGE_208_232_BASENAME);

                boolean isDirectAltPD = squidProject.getTask().isDirectAltPD();
                boolean has232 = squidProject.getTask().getParentNuclide().contains("232");

                corr8_RadioButton.setVisible(false);
                if (!isDirectAltPD && !has232) { // perm1
                    plotFlavorOneRadioButton.setSelected(true);
                    plotFlavorTwoRadioButton.setDisable(true);
                    corr8_RadioButton.setVisible(true);
                } else if (!isDirectAltPD && has232) {// perm3
                    plotFlavorOneRadioButton.setDisable(true);
                    plotFlavorTwoRadioButton.setSelected(true);
                } else {
                    plotFlavorOneRadioButton.setDisable(false);
                    plotFlavorTwoRadioButton.setDisable(false);
                }

                corr7_RadioButton.setVisible(true);

                showWeightedMeanPlot();
        }
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

        private final String sampleName;
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

        private final ShrimpFractionExpressionInterface shrimpFraction;
        private final Map<String, Object> datum;
        private SimpleBooleanProperty selectedProperty;

        public ConcordiaFractionNode(ShrimpFractionExpressionInterface shrimpFraction, String correction) {
            this.shrimpFraction = shrimpFraction;
            this.selectedProperty = new SimpleBooleanProperty(shrimpFraction.isSelected());
            this.datum = prepareWetherillDatum(shrimpFraction, correction, !shrimpFraction.isReferenceMaterial());
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
        @Override
        public SimpleBooleanProperty getSelectedProperty() {
            return selectedProperty;
        }

        /**
         * @param selectedProperty the selectedProperty to set
         */
        @Override
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
