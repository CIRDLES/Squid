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
package org.cirdles.squid.gui.dateInterpretations.plots.plotControllers;

import org.cirdles.squid.gui.dateInterpretations.plots.topsoil.TopsoilPlotWetherill;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import static javafx.scene.paint.Color.RED;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;
import org.cirdles.squid.constants.Squid3Constants.IndexIsoptopesEnum;
import org.cirdles.squid.constants.Squid3Constants.SpotTypes;
import org.cirdles.squid.exceptions.SquidException;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.WeightedMeanPlot;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import org.cirdles.topsoil.Variable;
import org.controlsfx.control.CheckTreeView;
import static org.cirdles.squid.gui.SquidUI.SPOT_TREEVIEW_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.gui.dataViews.SampleNode;
import org.cirdles.squid.gui.dataViews.SampleTreeNodeInterface;
import org.cirdles.squid.gui.dateInterpretations.plots.PlotDisplayInterface;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.MessagePlot;
import org.cirdles.squid.gui.dateInterpretations.plots.topsoil.TopsoilPlotAnyTwo;
import org.cirdles.squid.gui.dateInterpretations.plots.topsoil.TopsoilPlotTeraWasserburg;
import org.cirdles.squid.gui.dateInterpretations.plots.topsoil.TopsoilDataFactory;
import static org.cirdles.squid.gui.dateInterpretations.plots.topsoil.TopsoilDataFactory.prepareWetherillDatum;
import static org.cirdles.squid.gui.utilities.stringUtilities.StringTester.stringIsSquidRatio;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4CORR;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.CALIB_CONST_206_238_ROOT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.WTDAV_PREFIX;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REFRAD_AGE_U_PB;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR206_238AGE;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PB4COR206_238AGE_RM;
import static org.cirdles.squid.utilities.conversionUtilities.RoundingUtilities.squid3RoundedToSize;
import static org.cirdles.topsoil.Variable.X;
import static org.cirdles.topsoil.Variable.Y;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.PlotRefreshInterface;
import org.cirdles.squid.gui.dateInterpretations.plots.topsoil.AbstractTopsoilPlot;
import static org.cirdles.topsoil.plot.PlotOption.MCLEAN_REGRESSION;
import static org.cirdles.topsoil.plot.PlotOption.SHOW_UNINCLUDED;
import static org.cirdles.squid.gui.dateInterpretations.plots.topsoil.TopsoilDataFactory.prepareTeraWasserburgDatum;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class PlotsController implements Initializable, PlotRefreshInterface {

    public static PlotDisplayInterface plot;
    private static Node topsoilPlotNode;
    public static TreeItem<SampleTreeNodeInterface> currentlyPlottedSampleTreeNode = null;

    public static String xAxisExpressionName;
    public static String yAxisExpressionName;

    private HBox anyTwoToolBox;

    @FXML
    private VBox vboxMaster;
    @FXML
    private ToolBar plotToolBar;

    public static TreeView<SampleTreeNodeInterface> spotsTreeViewCheckBox = new CheckTreeView<>();
    public static TreeView<SampleTreeNodeInterface> spotsTreeViewString = new TreeView<>();

    private static ObservableList<SampleTreeNodeInterface> fractionNodes;
    private static PlotDisplayInterface rootPlot;
    private static List<Map<String, Object>> rootData;
    private static Map<String, PlotDisplayInterface> mapOfPlotsOfSpotSets;
    private static CheckBoxTreeItem<SampleTreeNodeInterface> chosenSample;
    public static SpotSummaryDetails spotSummaryDetails;

    @FXML
    private VBox plotVBox;
    @FXML
    private AnchorPane plotAndConfigAnchorPane;
    @FXML
    private ScrollPane plotScrollPane;
    @FXML
    private ScrollPane spotListScrollPane;
    @FXML
    private AnchorPane spotListAnchorPane;

    @Override
    public void setXAxisExpressionName(String xAxisExpressionName) {
        PlotsController.xAxisExpressionName = xAxisExpressionName;
        ((Task) squidProject.getTask()).setxAxisExpressionName(xAxisExpressionName);
        showActivePlot();
    }

    @Override
    public void setYAxisExpressionName(String yAxisExpressionName) {
        PlotsController.yAxisExpressionName = yAxisExpressionName;
        ((Task) squidProject.getTask()).setyAxisExpressionName(yAxisExpressionName);
        showActivePlot();
    }

    @Override
    public void showExcludedSpots(boolean doShow) {
        plot.setProperty(SHOW_UNINCLUDED.getTitle(), doShow);
    }

    @Override
    public double getTaskParameterExtPErrU() {
        return ((Task) squidProject.getTask()).getExtPErrU();
    }

    
    public static enum PlotTypes {
        CONCORDIA("CONCORDIA"),
        TERA_WASSERBURG("TERA_WASSERBURG"),
        WEIGHTED_MEAN("WEIGHTED_MEAN"),
        WEIGHTED_MEAN_SAMPLE("WEIGHTED_MEAN_SAMPLE"),
        ANY_TWO("ANY_TWO");

        private String plotType;

        private PlotTypes(String plotType) {
            this.plotType = plotType;
        }
    }

    // default settings
    public static PlotTypes plotTypeSelected = PlotTypes.CONCORDIA;

    public static SpotTypes fractionTypeSelected = SpotTypes.REFERENCE_MATERIAL;

    public static String correction = PB4CORR;

    public static String calibrConstAgeBaseName = CALIB_CONST_206_238_ROOT;

    public static String topsoilPlotFlavor = "C";

    public static boolean doSynchIncludedSpotsBetweenConcordiaAndWM = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // update 
        squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(false);

        vboxMaster.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        vboxMaster.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(PIXEL_OFFSET_FOR_MENU));

        plotVBox.prefWidthProperty().bind(plotScrollPane.widthProperty());
        plotVBox.prefHeightProperty().bind(plotScrollPane.heightProperty());

        spotListAnchorPane.prefHeightProperty().bind(spotListScrollPane.heightProperty());
        spotListAnchorPane.prefWidthProperty().bind(spotListScrollPane.widthProperty());

        spotsTreeViewCheckBox.setStyle(SPOT_TREEVIEW_CSS_STYLE_SPECS);
        spotsTreeViewString.setStyle(SPOT_TREEVIEW_CSS_STYLE_SPECS);

        // default
        xAxisExpressionName = ((Task) squidProject.getTask()).getxAxisExpressionName();
        yAxisExpressionName = ((Task) squidProject.getTask()).getyAxisExpressionName();

        showActivePlot();
    }

    private PlotDisplayInterface generateConcordiaPlot(
            String plotType,
            List<ShrimpFractionExpressionInterface> shrimpFractions,
            ParametersModel physicalConstantsModel) {

        PlotDisplayInterface plot;
        // choose wetherill or tw
        if (topsoilPlotFlavor.equals("C")) {
            if (plotType.startsWith("R")) {
                // reference material
                if (squidProject.getTask().getSelectedIndexIsotope().equals(IndexIsoptopesEnum.PB_207)) {
                    plot = new MessagePlot(
                            "Concordia of Reference Material is not defined for Index Isotope "
                            + squidProject.getTask().getSelectedIndexIsotope().getName());
                } else {
                    plot = new TopsoilPlotWetherill(
                            "Wetherill Concordia from Index Isotope " + squidProject.getTask().getSelectedIndexIsotope().getName() + " for " + plotType,
                            shrimpFractions, physicalConstantsModel);
                }
            } else {
                plot = new TopsoilPlotWetherill(
                        "Wetherill Concordia of " + correction + " for " + plotType,
                        shrimpFractions, physicalConstantsModel);
            }
        } else {
            plot = new TopsoilPlotTeraWasserburg(
                    "Tera-Wasserburg Concordia of " + correction + " for " + plotType,
                    shrimpFractions, physicalConstantsModel);
        }

        return plot;
    }

    private PlotDisplayInterface generateAnyTwoPlot(
            List<ShrimpFractionExpressionInterface> shrimpFractions,
            ParametersModel physicalConstantsModel) {

        PlotDisplayInterface plot;
        plot = new TopsoilPlotAnyTwo(
                "Plot of " + yAxisExpressionName + " vs. " + xAxisExpressionName,
                shrimpFractions, physicalConstantsModel, xAxisExpressionName, yAxisExpressionName);

        return plot;
    }

    private void showConcordiaPlotsOfUnknownsOrRefMat() {
        // may 2020 new approach per Nicole              
        if (vboxMaster.getChildren().get(0) instanceof ToolBoxNodeInterface) {
            vboxMaster.getChildren().remove(0);
        }
        HBox toolBox = new ConcordiaControlNode(this);
        vboxMaster.getChildren().add(0, toolBox);

        spotsTreeViewCheckBox = new CheckTreeView<>();
        spotsTreeViewCheckBox.setStyle(SPOT_TREEVIEW_CSS_STYLE_SPECS);
        spotsTreeViewString.setStyle(SPOT_TREEVIEW_CSS_STYLE_SPECS);

        final List<ShrimpFractionExpressionInterface> allUnknownOrRefMatShrimpFractions;
        Map<String, List<ShrimpFractionExpressionInterface>> mapOfSpotsBySampleNames;
        if (fractionTypeSelected.compareTo(SpotTypes.UNKNOWN) == 0) {
            allUnknownOrRefMatShrimpFractions = squidProject.getTask().getUnknownSpots();
            mapOfSpotsBySampleNames = squidProject.getTask().getMapOfUnknownsBySampleNames();
            // case of sample names chosen
            if (mapOfSpotsBySampleNames.size() > 1) {
                mapOfSpotsBySampleNames.remove(SpotTypes.UNKNOWN.getSpotTypeName());
            }
        } else {
            // ref mat
            allUnknownOrRefMatShrimpFractions = squidProject.getTask().getReferenceMaterialSpots();
            mapOfSpotsBySampleNames = new TreeMap<>();
            mapOfSpotsBySampleNames.put("Ref Mat " + ((Task) squidProject.getTask()).getFilterForRefMatSpotNames(), squidProject.getTask().getReferenceMaterialSpots());
//            mapOfSpotsBySampleNames.put("Concentration Ref Mat", squidProject.getTask().getConcentrationReferenceMaterialSpots());

            // used to synchronize rejects between weighted mean and concordia
            spotSummaryDetails
                    = squidProject.getTask().getTaskExpressionsEvaluationsPerSpotSet().
                            get(WTDAV_PREFIX + correction + calibrConstAgeBaseName + "_CalibConst");
        }

        // need current physical contants for plotting of concordia etc.
        ParametersModel physicalConstantsModel = squidProject.getTask().getPhysicalConstantsModel();

        rootPlot = generateConcordiaPlot(
                fractionTypeSelected.getSpotTypeName(), allUnknownOrRefMatShrimpFractions, physicalConstantsModel);

        rootData = new ArrayList<>();

        List<SampleTreeNodeInterface> fractionNodeDetails = new ArrayList<>();

        // build out set of rootData for samples
        CheckBoxTreeItem<SampleTreeNodeInterface> rootItem
                = new CheckBoxTreeItem<>(new SampleNode(fractionTypeSelected.getSpotTypeName()));
        chosenSample = rootItem;
        rootItem.setExpanded(true);
        rootItem.setIndependent(true);
        rootItem.setSelected(true);

        rootItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                ((SampleNode) rootItem.getValue()).setSelectedProperty(new SimpleBooleanProperty(newValue));

                ObservableList<TreeItem<SampleTreeNodeInterface>> mySamples = rootItem.getChildren();
                Iterator<TreeItem<SampleTreeNodeInterface>> mySamplesIterator = mySamples.iterator();
                while (mySamplesIterator.hasNext()) {

                    CheckBoxTreeItem<SampleTreeNodeInterface> mySampleItem
                            = (CheckBoxTreeItem<SampleTreeNodeInterface>) mySamplesIterator.next();
                    mySampleItem.setSelected(newValue);
                }
                plot = rootPlot;
                plot.setData(rootData);
            }
        });

        spotsTreeViewCheckBox.setRoot(rootItem);
        spotsTreeViewCheckBox.setShowRoot((fractionTypeSelected.compareTo(SpotTypes.UNKNOWN) == 0));
        spotsTreeViewCheckBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<SampleTreeNodeInterface>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<SampleTreeNodeInterface>> observable, TreeItem<SampleTreeNodeInterface> oldValue, TreeItem<SampleTreeNodeInterface> newValue) {
                if (newValue == null) {
                    newValue = rootItem;
                }
                currentlyPlottedSampleTreeNode = newValue;
            }
        });

        // want plot choices sticky during execution
        if (mapOfPlotsOfSpotSets == null) {
            mapOfPlotsOfSpotSets = new TreeMap<>();
        }
        for (Map.Entry<String, List<ShrimpFractionExpressionInterface>> entry : mapOfSpotsBySampleNames.entrySet()) {
            CheckBoxTreeItem<SampleTreeNodeInterface> sampleItem
                    = new CheckBoxTreeItem<>(new SampleNode(entry.getKey()));
            sampleItem.setSelected(true);
            rootItem.getChildren().add(sampleItem);
            if (currentlyPlottedSampleTreeNode == null) {
                currentlyPlottedSampleTreeNode = sampleItem;
            }

            List<Map<String, Object>> myData = new ArrayList<>();

            PlotDisplayInterface myPlotTry = mapOfPlotsOfSpotSets.get(entry.getKey() + topsoilPlotFlavor);

            if (myPlotTry == null) {
                myPlotTry = generateConcordiaPlot(
                        entry.getKey(), entry.getValue(), physicalConstantsModel);
                mapOfPlotsOfSpotSets.put(entry.getKey() + topsoilPlotFlavor, myPlotTry);
            }
            // final for listener
            final PlotDisplayInterface myPlot = myPlotTry;

            for (ShrimpFractionExpressionInterface spot : entry.getValue()) {
                SampleTreeNodeInterface fractionNode
                        = new ConcordiaFractionNode(topsoilPlotFlavor, spot, correction);
                if (((ConcordiaFractionNode) fractionNode).isValid()) {

                    fractionNodeDetails.add(fractionNode);

                    // handles each spot
                    CheckBoxTreeItem<SampleTreeNodeInterface> checkBoxTreeSpotItem
                            = new CheckBoxTreeItem<>(fractionNode);
                    sampleItem.getChildren().add(checkBoxTreeSpotItem);

                    // for ref material synchronize rejects
                    if (doSynchIncludedSpotsBetweenConcordiaAndWM
                            && (fractionTypeSelected.compareTo(SpotTypes.REFERENCE_MATERIAL) == 0)) {
                        fractionNode.setSelectedProperty(
                                new SimpleBooleanProperty(
                                        !spotSummaryDetails.getRejectedIndices()[entry.getValue().indexOf(spot)]));
                    }

                    checkBoxTreeSpotItem.setIndependent(false);
                    checkBoxTreeSpotItem.setSelected(fractionNode.getSelectedProperty().getValue());

                    myData.add(((ConcordiaFractionNode) fractionNode).getDatum());
                    // this contains all samples at the tree top
                    rootData.add(((ConcordiaFractionNode) fractionNode).getDatum());

                    checkBoxTreeSpotItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            ((ConcordiaFractionNode) checkBoxTreeSpotItem.getValue()).setSelectedProperty(new SimpleBooleanProperty(newValue));
                            myPlot.setData(myData);
                        }
                    });

                }
            }

            myPlot.setData(myData);

            // this sample item contains all the spot item checkboxes         
            sampleItem.setIndependent(false);
            sampleItem.setExpanded(fractionTypeSelected.compareTo(SpotTypes.REFERENCE_MATERIAL) == 0);
            sampleItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    myPlot.setData(myData);
                    rootPlot.setData(rootData);
                }

            });

            if (currentlyPlottedSampleTreeNode == null) {
                currentlyPlottedSampleTreeNode = sampleItem;
            }

        }
        rootPlot.setData(rootData);

        fractionNodes = FXCollections.observableArrayList(fractionNodeDetails);

        ((TreeView<SampleTreeNodeInterface>) spotsTreeViewCheckBox).setCellFactory(cell -> new CheckBoxTreeCell<>(
                (TreeItem<SampleTreeNodeInterface> item) -> ((ConcordiaFractionNode) item.getValue()).getSelectedProperty(),
                new StringConverter<TreeItem<SampleTreeNodeInterface>>() {

            @Override
            public String toString(TreeItem<SampleTreeNodeInterface> object) {
                SampleTreeNodeInterface item = object.getValue();

                String nodeString = item.getNodeName();
                if ((object.getParent() != null) && !(item instanceof SampleNode)) {
                    double[][] expressionValues = item.getShrimpFraction()
                            .getTaskExpressionsEvaluationsPerSpotByField(
                                    (fractionTypeSelected.compareTo(SpotTypes.REFERENCE_MATERIAL) == 0)
                                    ? PB4COR206_238AGE_RM : PB4COR206_238AGE);

                    double uncertainty = 0.0;
                    if (expressionValues[0].length > 1) {
                        uncertainty = expressionValues[0][1];
                    }
                    String ageOrValueSource = WeightedMeanPlot.makeAgeString(expressionValues[0][0], uncertainty);

                    nodeString += "  " + ageOrValueSource;
                }
                return nodeString;
            }

            @Override
            public TreeItem<SampleTreeNodeInterface> fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }));

        spotListAnchorPane.getChildren().clear();
        spotListAnchorPane.getChildren().add(spotsTreeViewCheckBox);
        spotsTreeViewCheckBox.prefHeightProperty().bind(spotListAnchorPane.prefHeightProperty());
        spotsTreeViewCheckBox.prefWidthProperty().bind(spotListAnchorPane.prefWidthProperty());

        // dec 2018 improvement suggested by Nicole Rayner to use checkboxes to select members
        // thus selecting tree item displays it and checkbox (see above) for a sample will
        // allow toggling of all spots
        spotsTreeViewCheckBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<SampleTreeNodeInterface>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<SampleTreeNodeInterface>> observable,
                    TreeItem<SampleTreeNodeInterface> oldValue, TreeItem<SampleTreeNodeInterface> newValue) {
                rootPlot.setData(rootData);
                try {
                    if (newValue.getValue() instanceof SampleNode) {
                        if (newValue.getValue().getNodeName().equals(SpotTypes.UNKNOWN.getSpotTypeName())) {
                            plot = rootPlot;
                        } else if (chosenSample != newValue) {
                            plot = mapOfPlotsOfSpotSets.get(newValue.getValue().getNodeName() + topsoilPlotFlavor);
                        }
                        chosenSample = (CheckBoxTreeItem< SampleTreeNodeInterface>) newValue;
                        currentlyPlottedSampleTreeNode = chosenSample;
                    }
                } catch (Exception e) {
                }
                refreshPlot();
            }
        });

        refreshPlot();

        spotsTreeViewCheckBox.getSelectionModel().select(currentlyPlottedSampleTreeNode);
        currentlyPlottedSampleTreeNode.setExpanded(true);
    }

    private void showAnyTwoExpressions() {
        spotsTreeViewCheckBox = new CheckTreeView<>();
        spotsTreeViewCheckBox.setStyle(SPOT_TREEVIEW_CSS_STYLE_SPECS);

        final List<ShrimpFractionExpressionInterface> allRefMatShrimpFractions;
        Map<String, List<ShrimpFractionExpressionInterface>> mapOfSpotsBySampleNames;

        // ref mat
        allRefMatShrimpFractions = squidProject.getTask().getReferenceMaterialSpots();
        mapOfSpotsBySampleNames = new TreeMap<>();
        mapOfSpotsBySampleNames.put("Ref Mat " + ((Task) squidProject.getTask()).getFilterForRefMatSpotNames(), squidProject.getTask().getReferenceMaterialSpots());

        // need current physical constants for plotting of data 
        ParametersModel physicalConstantsModel = squidProject.getTask().getPhysicalConstantsModel();

        // want plot choices sticky during execution
        if (mapOfPlotsOfSpotSets == null) {
            mapOfPlotsOfSpotSets = new TreeMap<>();
        }
        rootPlot = mapOfPlotsOfSpotSets.get(
                "Ref Mat " + ((Task) squidProject.getTask()).getFilterForRefMatSpotNames() + xAxisExpressionName + yAxisExpressionName);

        if (rootPlot == null) {
            rootPlot = generateAnyTwoPlot(
                    allRefMatShrimpFractions, physicalConstantsModel);
            mapOfPlotsOfSpotSets.put(
                    "Ref Mat " + ((Task) squidProject.getTask()).getFilterForRefMatSpotNames() + xAxisExpressionName + yAxisExpressionName, rootPlot);
        }

        rootData = new ArrayList<>();
        plot = rootPlot;

        List<SampleTreeNodeInterface> fractionNodeDetails = new ArrayList<>();

        // build out set of rootData for samples
        CheckBoxTreeItem<SampleTreeNodeInterface> rootItem
                = new CheckBoxTreeItem<>(new SampleNode(fractionTypeSelected.getSpotTypeName()));
        chosenSample = rootItem;
        rootItem.setExpanded(true);
        rootItem.setIndependent(true);
        rootItem.setSelected(true);

        rootItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                ((SampleNode) rootItem.getValue()).setSelectedProperty(new SimpleBooleanProperty(newValue));

                ObservableList<TreeItem<SampleTreeNodeInterface>> mySamples = rootItem.getChildren();
                Iterator<TreeItem<SampleTreeNodeInterface>> mySamplesIterator = mySamples.iterator();
                while (mySamplesIterator.hasNext()) {

                    CheckBoxTreeItem<SampleTreeNodeInterface> mySampleItem
                            = (CheckBoxTreeItem<SampleTreeNodeInterface>) mySamplesIterator.next();
                    mySampleItem.setSelected(newValue);
                }
                provisionAnyTwoToolbox(newValue);
            }
        });

        rootItem.indeterminateProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                provisionAnyTwoToolbox(newValue || rootItem.isSelected());
            }
        });

        spotsTreeViewCheckBox.setRoot(rootItem);
        spotsTreeViewCheckBox.setShowRoot((fractionTypeSelected.compareTo(SpotTypes.UNKNOWN) == 0));
        spotsTreeViewCheckBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<SampleTreeNodeInterface>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<SampleTreeNodeInterface>> observable, TreeItem<SampleTreeNodeInterface> oldValue, TreeItem<SampleTreeNodeInterface> newValue) {
                if (newValue == null) {
                    newValue = rootItem;
                }
                currentlyPlottedSampleTreeNode = newValue;
            }
        });

        for (Map.Entry<String, List<ShrimpFractionExpressionInterface>> entry : mapOfSpotsBySampleNames.entrySet()) {
            CheckBoxTreeItem<SampleTreeNodeInterface> sampleItem
                    = new CheckBoxTreeItem<>(new SampleNode(entry.getKey()));
            sampleItem.setSelected(true);
            rootItem.getChildren().add(sampleItem);

            List<Map<String, Object>> myData = new ArrayList<>();

            PlotDisplayInterface myPlotTry
                    = mapOfPlotsOfSpotSets.get(sampleItem.getValue().getNodeName() + xAxisExpressionName + yAxisExpressionName);

            if (myPlotTry == null) {
                myPlotTry = generateAnyTwoPlot(
                        entry.getValue(), physicalConstantsModel);
                mapOfPlotsOfSpotSets.put(
                        sampleItem.getValue().getNodeName() + xAxisExpressionName + yAxisExpressionName, myPlotTry);
            }
            // final for listener
            final PlotDisplayInterface myPlot = myPlotTry;

            for (ShrimpFractionExpressionInterface spot : entry.getValue()) {
                SampleTreeNodeInterface fractionNode
                        = new PlotAnyTwoFractionNode(spot, xAxisExpressionName, yAxisExpressionName);
                if (((PlotAnyTwoFractionNode) fractionNode).isValid()) {

                    fractionNodeDetails.add(fractionNode);

                    // handles each spot
                    CheckBoxTreeItem<SampleTreeNodeInterface> checkBoxTreeSpotItem
                            = new CheckBoxTreeItem<>(fractionNode);
                    sampleItem.getChildren().add(checkBoxTreeSpotItem);

                    checkBoxTreeSpotItem.setIndependent(false);
                    checkBoxTreeSpotItem.setSelected(fractionNode.getSelectedProperty().getValue());

                    myData.add(((PlotAnyTwoFractionNode) fractionNode).getDatum());
                    // this contains all samples at the tree top
                    rootData.add(((PlotAnyTwoFractionNode) fractionNode).getDatum());

                    checkBoxTreeSpotItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            ((PlotAnyTwoFractionNode) checkBoxTreeSpotItem.getValue()).setSelectedProperty(new SimpleBooleanProperty(newValue));
                            myPlot.setData(myData);
                        }
                    });

                }
            }

            myPlot.setData(myData);

            // this sample item contains all the spot item checkboxes         
            sampleItem.setIndependent(false);
            sampleItem.setExpanded(fractionTypeSelected.compareTo(SpotTypes.REFERENCE_MATERIAL) == 0);
            sampleItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    myPlot.setData(myData);
                    rootPlot.setData(rootData);
                }
            });

            if (currentlyPlottedSampleTreeNode == null) {
                currentlyPlottedSampleTreeNode = sampleItem;
            }

        }
        rootPlot.setData(rootData);

        fractionNodes = FXCollections.observableArrayList(fractionNodeDetails);

        ((TreeView<SampleTreeNodeInterface>) spotsTreeViewCheckBox).setCellFactory(cell -> new CheckBoxTreeCell<>(
                (TreeItem<SampleTreeNodeInterface> item) -> ((PlotAnyTwoFractionNode) item.getValue()).getSelectedProperty(),
                new StringConverter<TreeItem<SampleTreeNodeInterface>>() {

            @Override
            public String toString(TreeItem<SampleTreeNodeInterface> object) {
                SampleTreeNodeInterface item = object.getValue();

                String nodeString = item.getNodeName();
                if ((object.getParent() != null) && !(item instanceof SampleNode)) {
                    double[][] expressionValues = item.getShrimpFraction()
                            .getTaskExpressionsEvaluationsPerSpotByField(
                                    (fractionTypeSelected.compareTo(SpotTypes.REFERENCE_MATERIAL) == 0)
                                    ? PB4COR206_238AGE_RM : PB4COR206_238AGE);

                    double uncertainty = 0.0;
                    if (expressionValues[0].length > 1) {
                        uncertainty = expressionValues[0][1];
                    }
                    String ageOrValueSource = WeightedMeanPlot.makeAgeString(expressionValues[0][0], uncertainty);

                    try {
                        nodeString += "  " + ageOrValueSource
                                + " (" + squid3RoundedToSize(((Double) item.getDatum().get(X.getTitle())), 5)
                                + ", " + squid3RoundedToSize(((Double) item.getDatum().get(Y.getTitle())), 5) + ")";
                    } catch (Exception e) {
                    }
                }
                return nodeString;
            }

            @Override
            public TreeItem<SampleTreeNodeInterface> fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }));

        spotListAnchorPane.getChildren().clear();
        spotListAnchorPane.getChildren().add(spotsTreeViewCheckBox);
        spotsTreeViewCheckBox.prefHeightProperty().bind(spotListAnchorPane.prefHeightProperty());
        spotsTreeViewCheckBox.prefWidthProperty().bind(spotListAnchorPane.prefWidthProperty());

        spotsTreeViewCheckBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<SampleTreeNodeInterface>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<SampleTreeNodeInterface>> observable,
                    TreeItem<SampleTreeNodeInterface> oldValue, TreeItem<SampleTreeNodeInterface> newValue) {
                rootPlot.setData(rootData);
                try {
                    if (newValue.getValue() instanceof SampleNode) {
                        if (newValue.getValue().getNodeName().equals(SpotTypes.UNKNOWN.getSpotTypeName())) {
                            plot = rootPlot;
                        } else if (chosenSample != newValue) {
                            plot = mapOfPlotsOfSpotSets.get(newValue.getValue().getNodeName() + xAxisExpressionName + yAxisExpressionName);
                        }
                        chosenSample = (CheckBoxTreeItem< SampleTreeNodeInterface>) newValue;
                        currentlyPlottedSampleTreeNode = chosenSample;
                    }
                } catch (Exception e) {
                }
                refreshPlot();
            }
        });

        spotsTreeViewCheckBox.getSelectionModel().select(currentlyPlottedSampleTreeNode);
        currentlyPlottedSampleTreeNode.setExpanded(true);

        provisionAnyTwoToolbox(true);

    }

    /**
     *
     * @param hasData the value of hasData
     */
    private void provisionAnyTwoToolbox(boolean hasData) {

        if (plot instanceof AbstractTopsoilPlot) {
            plot.setProperty(
                    MCLEAN_REGRESSION.getTitle(),
                    hasData && (Boolean) ((AbstractTopsoilPlot) plot).getPlotOptions().get(MCLEAN_REGRESSION));
        }

        if (vboxMaster.getChildren().get(0) instanceof ToolBoxNodeInterface) {
            vboxMaster.getChildren().remove(0);
        }
        anyTwoToolBox = new AnyTwoExpressionsControlNode(this, hasData);
        vboxMaster.getChildren().add(0, anyTwoToolBox);

        refreshPlot();
    }

    @Override
    public void refreshPlot() {
        if (plot != null) {
            try {
                if (plot instanceof WeightedMeanPlot) {
                    topsoilPlotNode = plot.displayPlotAsNode();
                    plotAndConfigAnchorPane.getChildren().setAll(((Canvas) plot));

                    AnchorPane.setLeftAnchor(((Canvas) plot), 0.0);
                    AnchorPane.setRightAnchor(((Canvas) plot), 0.0);
                    AnchorPane.setTopAnchor(((Canvas) plot), 0.0);
                    AnchorPane.setBottomAnchor(((Canvas) plot), 0.0);

                    VBox.setVgrow(((Canvas) plot), Priority.ALWAYS);

                    ((Canvas) plot).widthProperty().bind(plotScrollPane.widthProperty());

                    plotVBox.getChildren().remove(plotToolBar);

                    ((Canvas) plot).heightProperty().bind(plotScrollPane.heightProperty());
                    spotsTreeViewCheckBox.refresh();
                } else {
                    topsoilPlotNode = plot.displayPlotAsNode();
                    plotAndConfigAnchorPane.getChildren().setAll(topsoilPlotNode);
                    plotVBox.getChildren().remove(plotToolBar);
                    plotVBox.getChildren().add(plotToolBar);

                    AnchorPane.setLeftAnchor(topsoilPlotNode, 0.0);
                    AnchorPane.setRightAnchor(topsoilPlotNode, 0.0);
                    AnchorPane.setTopAnchor(topsoilPlotNode, 0.0);
                    AnchorPane.setBottomAnchor(topsoilPlotNode, 0.0);

                    VBox.setVgrow(topsoilPlotNode, Priority.ALWAYS);
                }

                VBox.setVgrow(plotAndConfigAnchorPane, Priority.ALWAYS);
                VBox.setVgrow(plotVBox, Priority.NEVER);//ALWAYS);

                plotToolBar.getItems().clear();
                plotToolBar.getItems().addAll(plot.toolbarControlsFactory());

            } catch (Exception e) {
            }
        } else {
            plotAndConfigAnchorPane.getChildren().clear();
            plotToolBar.getItems().clear();

            plotVBox.getChildren().remove(plotToolBar);
        }

    }

    @Override
    public void toggleSpotExclusionWM(int index) {
        if (currentlyPlottedSampleTreeNode != null) {
            ((CheckBoxTreeItem) currentlyPlottedSampleTreeNode.getChildren().get(index))
                    .setSelected(!((CheckBoxTreeItem) currentlyPlottedSampleTreeNode.getChildren().get(index)).isSelected());
        }
    }

    @Override
    public void calculateWeightedMean() {
        try {
            spotSummaryDetails.setValues(spotSummaryDetails.eval(squidProject.getTask()));
        } catch (SquidException squidException) {
        }
    }

    @Override
    public void showRefMatWeightedMeanPlot() {
        // may 2020 new approach per Nicole  
        if (vboxMaster.getChildren().get(0) instanceof ToolBoxNodeInterface) {
            vboxMaster.getChildren().remove(0);
        }
        HBox toolBox = new RefMatWeightedMeanControlNode(this);
        vboxMaster.getChildren().add(0, toolBox);

        // get details
        spotSummaryDetails
                = squidProject.getTask().getTaskExpressionsEvaluationsPerSpotSet().
                        get(WTDAV_PREFIX + correction + calibrConstAgeBaseName + "_CalibConst");
        // backwards compatible for priming sorting
        spotSummaryDetails.setSelectedExpressionName("SpotIndex");

        plot = new WeightedMeanPlot(
                new Rectangle(1000, 600),
                correction + calibrConstAgeBaseName + " calibr.const Weighted Mean of Reference Material "
                + ((Task) squidProject.getTask()).getFilterForRefMatSpotNames(),
                spotSummaryDetails,
                correction + calibrConstAgeBaseName + "_Age_RM", // TODO: FIX THIS HACK  correction + calibrConstAgeBaseName + "_CalibConst",//
                squidProject.getTask().getTaskExpressionsEvaluationsPerSpotSet().get(REFRAD_AGE_U_PB).getValues()[0][0],
                this);

        refreshPlot();

        List<ShrimpFractionExpressionInterface> shrimpFractionsDetails
                = spotSummaryDetails.getSelectedSpots();
        List<SampleTreeNodeInterface> fractionNodeDetailsWM = new ArrayList<>();
        rootData = new ArrayList<>();

        for (int i = 0; i < shrimpFractionsDetails.size(); i++) {
            WeightedMeanSpotNode fractionNodeWM
                    = new WeightedMeanSpotNode(shrimpFractionsDetails.get(i), i);
            fractionNodeDetailsWM.add(fractionNodeWM);
        }

        fractionNodes = FXCollections.observableArrayList(fractionNodeDetailsWM);

        try {
            if (spotSummaryDetails.isManualRejectionEnabled()) {
                TreeItem<SampleTreeNodeInterface> rootItemWM
                        = new CheckBoxTreeItem<>(new SampleNode(((Task) squidProject.getTask()).getFilterForRefMatSpotNames()));

                spotsTreeViewCheckBox.setCellFactory(p -> new CheckBoxTreeCell<>(
                        (TreeItem<SampleTreeNodeInterface> item) -> ((WeightedMeanSpotNode) item.getValue()).getSelectedProperty(),
                        new StringConverter<TreeItem<SampleTreeNodeInterface>>() {

                    @Override
                    public String toString(TreeItem<SampleTreeNodeInterface> object) {
                        SampleTreeNodeInterface item = object.getValue();

                        String displayVal = item.getNodeName();
                        try {
                            displayVal = displayVal
                                    + prettyPrintSortedWM(item.getShrimpFraction(), spotSummaryDetails.getSelectedExpressionName());
                        } catch (Exception e) {
                        }
                        return displayVal;
                    }

                    @Override
                    public TreeItem<SampleTreeNodeInterface> fromString(String string) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                }));
                spotsTreeViewCheckBox.setRoot(rootItemWM);
                rootItemWM.setExpanded(true);
                spotsTreeViewCheckBox.setShowRoot(true);
                currentlyPlottedSampleTreeNode = rootItemWM;

                for (int i = 0; i < fractionNodes.size(); i++) {
                    final CheckBoxTreeItem<SampleTreeNodeInterface> checkBoxTreeItemWM
                            = new CheckBoxTreeItem<>(fractionNodes.get(i));
                    rootItemWM.getChildren().add(checkBoxTreeItemWM);

                    checkBoxTreeItemWM.setSelected(!spotSummaryDetails.getRejectedIndices()[i]);
                    checkBoxTreeItemWM.selectedProperty().addListener((observable, oldValue, newValue) -> {
                        ((WeightedMeanSpotNode) checkBoxTreeItemWM.getValue()).setSelectedProperty(new SimpleBooleanProperty(newValue));
                        spotSummaryDetails.setIndexOfRejectedIndices(((WeightedMeanSpotNode) checkBoxTreeItemWM.getValue())
                                .getIndexOfSpot(), !newValue);
                        try {
                            spotSummaryDetails.setValues(spotSummaryDetails.eval(squidProject.getTask()));
                        } catch (SquidException squidException) {
                        }
                        refreshPlot();
                    });
                }

                spotListAnchorPane.getChildren().clear();
                spotListAnchorPane.getChildren().add(spotsTreeViewCheckBox);
                spotsTreeViewCheckBox.prefHeightProperty().bind(spotListAnchorPane.prefHeightProperty());
                spotsTreeViewCheckBox.prefWidthProperty().bind(spotListAnchorPane.prefWidthProperty());

            } else {
                TreeItem<SampleTreeNodeInterface> rootItemWM
                        = new TreeItem<>(new SampleNode(((Task) squidProject.getTask()).getFilterForRefMatSpotNames()));
                spotsTreeViewString.setRoot(rootItemWM);
                spotsTreeViewString.setCellFactory(param -> new TreeCell<SampleTreeNodeInterface>() {
                    @Override
                    public void updateItem(SampleTreeNodeInterface item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("");
                            setStyle(null);
                        } else {
                            if (item.getSelectedProperty().get()) {
                                setStyle(SPOT_TREEVIEW_CSS_STYLE_SPECS + "-fx-text-fill: red;");
                            } else {
                                setStyle(SPOT_TREEVIEW_CSS_STYLE_SPECS + "-fx-text-fill: blue;");
                            }

                            String displayVal = item.getNodeName();
                            try {
                                displayVal = displayVal
                                        + prettyPrintSortedWM(item.getShrimpFraction(), spotSummaryDetails.getSelectedExpressionName());
                            } catch (Exception e) {
                            }
                            setText(displayVal);
                        }
                    }

                });

                spotsTreeViewString.setRoot(rootItemWM);
                rootItemWM.setExpanded(true);
                spotsTreeViewString.setShowRoot(true);

                for (int i = 0; i < fractionNodes.size(); i++) {
                    boolean rejected = spotSummaryDetails.getRejectedIndices()[i];

                    fractionNodes.get(i).setSelectedProperty(new SimpleBooleanProperty(!rejected));
                    final TreeItem<SampleTreeNodeInterface> treeItemWM
                            = new TreeItem<>(fractionNodes.get(i));
                    rootItemWM.getChildren().add(treeItemWM);
                }

                spotListAnchorPane.getChildren().clear();
                spotListAnchorPane.getChildren().add(spotsTreeViewString);
                spotsTreeViewString.prefHeightProperty().bind(spotListAnchorPane.prefHeightProperty());
                spotsTreeViewString.prefWidthProperty().bind(spotListAnchorPane.prefWidthProperty());

            }
        } catch (Exception e) {
        }

        refreshPlot();
    }

    private void showSampleWeightedMeanPlot() {
        // dec 2019 new approach per Nicole
        if (vboxMaster.getChildren().get(0) instanceof ToolBoxNodeInterface) {
            vboxMaster.getChildren().remove(0);
        }
        HBox toolBox = new SamplesWeightedMeanToolBoxNode(this);
        vboxMaster.getChildren().add(0, toolBox);

        ((TreeView<SampleTreeNodeInterface>) spotsTreeViewCheckBox).setCellFactory(cell -> new CheckBoxTreeCell<>(
                (TreeItem<SampleTreeNodeInterface> item) -> ((SampleNode) item.getValue()).getSelectedProperty(),
                new StringConverter<TreeItem<SampleTreeNodeInterface>>() {

            @Override
            public String toString(TreeItem<SampleTreeNodeInterface> object) {
                SampleTreeNodeInterface item = object.getValue();
                // the goal is to show the nodename + weightedMean source + value of sorting choice if different
                String nodeStringWM = "";
                if (object.getParent() != null) {

                    String wmExpressionName
                            = ((SampleNode) object.getParent().getValue()).getSpotSummaryDetailsWM().getExpressionTree().getName().split("_WM_")[0];
                    double[][] wmExpressionValues;
                    if (stringIsSquidRatio(wmExpressionName)) {
                        // ratio case
                        wmExpressionValues
                                = Arrays.stream(item.getShrimpFraction()
                                        .getIsotopicRatioValuesByStringName(wmExpressionName)).toArray(double[][]::new);
                    } else {
                        wmExpressionValues = item.getShrimpFraction()
                                .getTaskExpressionsEvaluationsPerSpotByField(wmExpressionName);
                    }

                    String ageOrValueSourceOfWM;
                    double uncertainty = 0.0;
                    if (wmExpressionValues[0].length > 1) {
                        uncertainty = wmExpressionValues[0][1];
                    }
                    if (wmExpressionName.endsWith("Age")) {
                        ageOrValueSourceOfWM = WeightedMeanPlot.makeAgeString(wmExpressionValues[0][0], uncertainty);
                    } else {
                        ageOrValueSourceOfWM = WeightedMeanPlot.makeValueString(wmExpressionValues[0][0], uncertainty);
                    }
                    nodeStringWM = item.getShrimpFraction().getFractionID() + "  " + ageOrValueSourceOfWM;

                    String sortingExpression = ((SampleNode) object.getParent().getValue()).getSpotSummaryDetailsWM().getSelectedExpressionName();
                    // check to see if sorted by same field              
                    if ((item instanceof WeightedMeanSpotNode)
                            && (wmExpressionName.compareToIgnoreCase(sortingExpression) != 0)) {
                        nodeStringWM += prettyPrintSortedWM(item.getShrimpFraction(), sortingExpression);
                    }

                }
                return (object.getParent() == null) ? ((SampleNode) object.getValue()).getNodeName() : (item instanceof SampleNode) ? "" : nodeStringWM;
            }

            @Override
            public TreeItem<SampleTreeNodeInterface> fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

        }));

        spotListAnchorPane.getChildren().clear();
        spotListAnchorPane.getChildren().add(spotsTreeViewCheckBox);
        spotsTreeViewCheckBox.prefHeightProperty().bind(spotListAnchorPane.prefHeightProperty());
        spotsTreeViewCheckBox.prefWidthProperty().bind(spotListAnchorPane.prefWidthProperty());

        refreshPlot();

    }

    private String prettyPrintSortedWM(ShrimpFractionExpressionInterface shrimpFraction, String sortingExpression) {
        String nodeStringWM = "";

        if (sortingExpression.compareTo("Hours") == 0) {
            nodeStringWM += " ::" + shrimpFraction.getHours();
        } else if (sortingExpression.compareTo("SpotIndex") == 0) {
            nodeStringWM += " ::" + shrimpFraction.getSpotIndex();
        } else {
            double[][] expressionValues;
            if (stringIsSquidRatio(sortingExpression)) {
                // ratio case
                expressionValues
                        = Arrays.stream(shrimpFraction
                                .getIsotopicRatioValuesByStringName(sortingExpression)).toArray(double[][]::new);
            } else {
                expressionValues = shrimpFraction
                        .getTaskExpressionsEvaluationsPerSpotByField(sortingExpression);
            }

            if (sortingExpression.contains("Age")) {
                nodeStringWM += "::" + WeightedMeanPlot.makeSimpleAgeString(expressionValues[0][0]);
            } else {
                Formatter formatter = new Formatter();
                formatter.format("%4.4f", expressionValues[0][0]);
                nodeStringWM += "::" + formatter.toString();
            }
        }

        return nodeStringWM;

    }

    @Override
    public void showActivePlot() {
        switch (plotTypeSelected) {
            case CONCORDIA:
            case TERA_WASSERBURG:
                showConcordiaPlotsOfUnknownsOrRefMat();
                break;
            case WEIGHTED_MEAN:
                showRefMatWeightedMeanPlot();
                break;
            case WEIGHTED_MEAN_SAMPLE:
                showSampleWeightedMeanPlot();
                break;
            case ANY_TWO:
                showAnyTwoExpressions();
        }
    }

    private class ConcordiaFractionNode implements SampleTreeNodeInterface {

        private final ShrimpFractionExpressionInterface shrimpFraction;
        private Map<String, Object> datum;
        private SimpleBooleanProperty selectedProperty;

        public ConcordiaFractionNode(String flavor, ShrimpFractionExpressionInterface shrimpFraction, String correction) {
            this.shrimpFraction = shrimpFraction;
            this.selectedProperty = new SimpleBooleanProperty(shrimpFraction.isSelected());
            this.datum = null;
            switch (flavor) {
                case "C":
                    this.datum = prepareWetherillDatum(shrimpFraction, correction, !shrimpFraction.isReferenceMaterial());
                    break;
                case "TW":
                    this.datum = prepareTeraWasserburgDatum(shrimpFraction, correction, !shrimpFraction.isReferenceMaterial());
                    break;
            }

            if (datum != null) {
                this.datum.put(Variable.SELECTED.getTitle(), shrimpFraction.isSelected());
            }
        }

        public boolean isValid() {
            return datum != null;
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
            this.datum.put(Variable.SELECTED.getTitle(), selectedProperty.getValue());
        }

        /**
         *
         * @return the java.lang.String
         */
        @Override
        public String getNodeName() {
            return shrimpFraction.getFractionID();
        }
    }

    private class PlotAnyTwoFractionNode implements SampleTreeNodeInterface {

        private final ShrimpFractionExpressionInterface shrimpFraction;
        private Map<String, Object> datum;
        private SimpleBooleanProperty selectedProperty;

        public PlotAnyTwoFractionNode(
                ShrimpFractionExpressionInterface shrimpFraction, String xExpressionName, String yExpressionName) {
            this.shrimpFraction = shrimpFraction;
            this.selectedProperty = new SimpleBooleanProperty(shrimpFraction.isSelected());

            this.datum = TopsoilDataFactory.preparePlotAnyTwoDatum(shrimpFraction, xExpressionName, yExpressionName);
            if (datum != null) {
                this.datum.put(Variable.SELECTED.getTitle(), shrimpFraction.isSelected());
            }
        }

        public boolean isValid() {
            return datum != null;
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
            this.datum.put(Variable.SELECTED.getTitle(), selectedProperty.getValue());
        }

        /**
         *
         * @return the java.lang.String
         */
        @Override
        public String getNodeName() {
            return shrimpFraction.getFractionID();
        }
    }
}
