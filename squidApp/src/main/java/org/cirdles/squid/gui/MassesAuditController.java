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
package org.cirdles.squid.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.gui.dataViews.AbstractDataView;
import org.cirdles.squid.gui.dataViews.MassAuditRefreshInterface;
import org.cirdles.squid.gui.dataViews.SpeciesGraphs.SpeciesAMUAuditViewForShrimp;
import org.cirdles.squid.gui.dataViews.SpeciesGraphs.SpeciesCountsAuditViewForShrimp;
import org.cirdles.squid.gui.dataViews.SpeciesGraphs.SpeciesGraphInterface;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.prawn.PrawnFile.Run;
import org.cirdles.squid.shrimp.MassStationDetail;
import org.cirdles.squid.shrimp.ShrimpFraction;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.tasks.Task;

import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

import static org.cirdles.squid.gui.SquidUI.SQUID_LOGO_SANS_TEXT_URL;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.gui.constants.Squid3GuiConstants.MASS_DETAIL_FORMAT;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class MassesAuditController implements Initializable, MassAuditRefreshInterface {

    public static final int LEGEND_WIDTH = 259;
    private static final String STYLE_BUTTON_LABEL
            = "-fx-font-family: 'Monospaced', 'SansSerif';\n"
            + "    -fx-font-weight: bold;\n"
            + "    -fx-font-size: 12pt;\n";
    private static ObservableList<MassStationDetail> allMassStations;
    private static ObservableList<MassStationDetail> viewedAsGraphMassStations;
    private static List<MassStationDetail> massMinuends;
    private static List<MassStationDetail> massSubtrahends;
    private static boolean showTimeNormalized;
    private static boolean showPrimaryBeam;
    private static boolean showQt1y;
    private static boolean showQt1z;
    private static boolean showSpotLabels;
    private static boolean synchedScrolls;
    // binary: 00= masses, 01 = SBM, 10 = Counts; 11 = both SBM and Counts
    private static int countsRadioButtonChoice;
    private final int BUTTON_WIDTH = 60;
    private final int COMBO_WIDTH = 210;
    private final int ROW_HEIGHT = 30;
    private final int heightOfMassPlot = 150;
    private final int plotWidthOfSpots = 7;
    private final int plotWidthOfZoomedSpots = 20;

    private int[] countOfScansCumulative;
    private List<AbstractDataView> legendGraphs;
    private List<AbstractDataView> leadingGraphs;
    private List<AbstractDataView> zoomingGraphs;
    private List<AbstractDataView> trailingGraphs;

    // 1-based counting of spots
    private int zoomedStart;
    private int zoomedWidth;
    private int zoomedEnd;

    @FXML
    private Accordion massesAccordian;
    @FXML
    private TitledPane massesTitledPane;
    @FXML
    private VBox scrolledBox;
    @FXML
    private VBox scrolledBoxLeft;
    @FXML
    private ListView<MassStationDetail> availableMassesListView;
    @FXML
    private ListView<MassStationDetail> viewedAsGraphMassesListView;
    @FXML
    private CheckBox normalizeTimeAxisCheckBox;
    @FXML
    private GridPane massDeltasGridPane;
    @FXML
    private CheckBox showPrimaryBeamCheckBox;
    @FXML
    private CheckBox showQt1yCheckBox;
    @FXML
    private CheckBox showQt1zCheckBox;
    @FXML
    private ScrollPane leftScrollPane;
    @FXML
    private ScrollPane rightScrollPane;
    @FXML
    private CheckBox displaySpotLabelsCheckBox;
    @FXML
    private ScrollBar zoomScrollBar;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        countsRadioButtonChoice = 0B00;

        try {
            setupMassStationDetailsListViews();
        } catch (SquidException squidException) {
        }
        setupMassDeltas();

        showTimeNormalized = squidProject.getTask().isShowTimeNormalized();
        normalizeTimeAxisCheckBox.setSelected(showTimeNormalized);

        showPrimaryBeam = squidProject.getTask().isShowPrimaryBeam();
        showPrimaryBeamCheckBox.setSelected(showPrimaryBeam);

        showQt1y = squidProject.getTask().isShowQt1y();
        showQt1yCheckBox.setSelected(showQt1y);

        showQt1z = squidProject.getTask().isShowQt1z();
        showQt1zCheckBox.setSelected(showQt1z);

        showSpotLabels = squidProject.getTask().isShowSpotLabels();
        displaySpotLabelsCheckBox.setSelected(showSpotLabels);

        legendGraphs = new ArrayList<>();
        leadingGraphs = new ArrayList<>();
        zoomingGraphs = new ArrayList<>();
        trailingGraphs = new ArrayList<>();

        calculateCountOfScansCumulative();

        zoomedWidth = Math.min(40, squidProject.getTask().getShrimpFractions().size() - 0);
        zoomedStart = zoomedWidth == 40 ? 5 : 1;
        zoomedEnd = zoomedStart + zoomedWidth - 1;

        zoomScrollBar.setVisible(zoomedWidth == 40);

        zoomScrollBar.setValue(zoomedStart);
        zoomScrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                int savedZoomedStart = zoomedStart;
                int delta = (new_val.intValue() - old_val.intValue());
                if (delta < 0) {
                    zoomedStart = ((zoomedStart + delta) > 1) ? (zoomedStart + delta) : 1;
                    zoomedEnd = zoomedStart + zoomedWidth - 1;
                } else {
                    zoomedEnd = ((zoomedEnd + delta) <= (zoomScrollBar.getMax())) ? (zoomedEnd + delta) : (int) zoomScrollBar.getMax();
                    zoomedStart = Math.max(1, zoomedEnd - zoomedWidth + 1);
                }
                try {
                    updateAllMassesCanvases(savedZoomedStart);
                } catch (SquidException squidException) {
                }
            }
        });

        try {
            displayMassStationsForReview();
        } catch (SquidException squidException) {
        }
        zoomScrollBar.prefWidthProperty().bind(rightScrollPane.widthProperty());

        massesAccordian.setExpandedPane(massesTitledPane);

        synchedScrolls = false;
    }

    private void setupScrollBarSynch() {
        if (!synchedScrolls) {
            ScrollBar rightBar = (ScrollBar) leftScrollPane.lookup(".scroll-bar:vertical");
            ScrollBar leftBar = (ScrollBar) rightScrollPane.lookup(".scroll-bar:vertical");
            rightBar.valueProperty().bindBidirectional(leftBar.valueProperty());
            synchedScrolls = true;
        }
    }

    private void setupMassStationDetailsListViews() throws SquidException {
        //https://docs.oracle.com/javafx/2/drag_drop/jfxpub-drag_drop.htm
        List<MassStationDetail> availableMassStationDetails = new ArrayList<>();
        List<MassStationDetail> allMassStationDetails = new ArrayList<>();
        List<MassStationDetail> viewedAsGraphMassStationDetails = new ArrayList<>();

        Map<Integer, MassStationDetail> mapOfIndexToMassStationDetails = squidProject.getTask().getMapOfIndexToMassStationDetails();
        for (Map.Entry<Integer, MassStationDetail> entry : mapOfIndexToMassStationDetails.entrySet()) {
            allMassStationDetails.add(entry.getValue());
            if (entry.getValue().isViewedAsGraph()) {
                viewedAsGraphMassStationDetails.add(entry.getValue());
            } else {
                availableMassStationDetails.add(entry.getValue());
            }
        }

        ObservableList<MassStationDetail> availableMassStations = FXCollections.observableArrayList(availableMassStationDetails);
        availableMassesListView.setItems(availableMassStations);
        availableMassesListView.setCellFactory(
                (parameter)
                        -> new MassStationDetailListCell()
        );

        availableMassesListView.setOnDragDetected(new onDragDetectedEventHandler());
        availableMassesListView.setOnDragOver(new onDragOverEventHandler());
        availableMassesListView.setOnDragDropped(new onDragDroppedEventHandler(availableMassesListView));
        availableMassesListView.setOnDragDone(new onDragDoneEventHandler(availableMassesListView));
        availableMassesListView.setId("");

        availableMassesListView.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);

        viewedAsGraphMassStations = FXCollections.observableArrayList(viewedAsGraphMassStationDetails);
        viewedAsGraphMassesListView.setItems(viewedAsGraphMassStations);
        viewedAsGraphMassesListView.setCellFactory(
                (parameter)
                        -> new MassStationDetailListCell()
        );

        viewedAsGraphMassesListView.setOnDragDetected(new onDragDetectedEventHandler());
        viewedAsGraphMassesListView.setOnDragOver(new onDragOverEventHandler());
        viewedAsGraphMassesListView.setOnDragDropped(new onDragDroppedEventHandler(viewedAsGraphMassesListView));
        viewedAsGraphMassesListView.setOnDragDone(new onDragDoneEventHandler(viewedAsGraphMassesListView));
        viewedAsGraphMassesListView.setId("viewedAsGraph");

        viewedAsGraphMassesListView.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);

        // used in paired differences
        allMassStations = FXCollections.observableArrayList(allMassStationDetails);
    }

    @FXML
    private void normalizeTimeAxisCheckBoxAction(ActionEvent event) {
        showTimeNormalized = normalizeTimeAxisCheckBox.isSelected();
        squidProject.getTask().setShowTimeNormalized(showTimeNormalized);
        try {
            displayMassStationsForReview();
        } catch (SquidException squidException) {
        }
    }

    @FXML
    private void showPrimaryBeamCheckBoxAction(ActionEvent event) {
        showPrimaryBeam = showPrimaryBeamCheckBox.isSelected();
        squidProject.getTask().setShowPrimaryBeam(showPrimaryBeam);
        try {
            displayMassStationsForReview();
        } catch (SquidException squidException) {
        }
    }

    @FXML
    private void showQt1yCheckBoxAction(ActionEvent event) {
        showQt1y = showQt1yCheckBox.isSelected();
        squidProject.getTask().setShowQt1y(showQt1y);
        try {
            displayMassStationsForReview();
        } catch (SquidException squidException) {
        }
    }

    @FXML
    private void showQt1zCheckBoxAction(ActionEvent event) {
        showQt1z = showQt1zCheckBox.isSelected();
        squidProject.getTask().setShowQt1z(showQt1z);
        try {
            displayMassStationsForReview();
        } catch (SquidException squidException) {
        }
    }

    @FXML
    private void scrollBoxMouseEntered(MouseEvent event) {
        setupScrollBarSynch();
    }

    private void calculateCountOfScansCumulative() {
        // stores accumulated scans at each index
        List<Run> prawnFileRuns = squidProject.getPrawnFileRuns();
        countOfScansCumulative = new int[prawnFileRuns.size() + 1];
        for (int i = 0; i < prawnFileRuns.size(); i++) {
            int countOfScans = Integer.parseInt(prawnFileRuns.get(i).getPar().get(3).getValue());
            countOfScansCumulative[i + 1] = countOfScansCumulative[i] + countOfScans;
        }
    }

    public int[] getCountOfScansCumulative(List<Run> prawnFileRuns) {
        return countOfScansCumulative;
    }

    @FXML
    private void displaySpotLabelsCheckBoxAction(ActionEvent event) {
        showSpotLabels = displaySpotLabelsCheckBox.isSelected();
        squidProject.getTask().setShowSpotLabels(showSpotLabels);
        try {
            displayMassStationsForReview();
        } catch (SquidException squidException) {
        }
    }

    private void setupMassDeltas() {
        massMinuends = squidProject.getTask().getMassMinuends();
        massSubtrahends = squidProject.getTask().getMassSubtrahends();

        massDeltasGridPane.getRowConstraints().clear();
        RowConstraints rowCon = new RowConstraints();
        rowCon.setPrefHeight(ROW_HEIGHT + 2);
        rowCon.setMinHeight(ROW_HEIGHT + 2);
        rowCon.setMaxHeight(ROW_HEIGHT + 2);
        rowCon.setValignment(VPos.CENTER);
        massDeltasGridPane.getRowConstraints().add(rowCon);

        massDeltasGridPane.getColumnConstraints().clear();
        ColumnConstraints colconButtons = new ColumnConstraints();
        colconButtons.setPrefWidth(BUTTON_WIDTH + 2);
        colconButtons.setMinWidth(BUTTON_WIDTH + 2);
        colconButtons.setMaxWidth(BUTTON_WIDTH + 2);
        colconButtons.setHalignment(HPos.CENTER);
        massDeltasGridPane.getColumnConstraints().add(0, colconButtons);

        ColumnConstraints colconCombo = new ColumnConstraints();
        colconCombo.setPrefWidth(COMBO_WIDTH + 2);
        colconCombo.setMinWidth(COMBO_WIDTH + 2);
        colconCombo.setMaxWidth(COMBO_WIDTH + 2);
        colconCombo.setHalignment(HPos.CENTER);
        massDeltasGridPane.getColumnConstraints().add(1, colconCombo);
        massDeltasGridPane.getColumnConstraints().add(2, colconCombo);

        // prepopulate with saved values
        for (int row = 0; row < massMinuends.size(); row++) {
            massDeltasGridPane.add(addRemoveButtonFactory("-"), 0, row);

            ComboBox<MassStationDetail> massMinuendComboBox = addMassComboFactory(allMassStations);
            massDeltasGridPane.add(massMinuendComboBox, 1, row);
            massMinuendComboBox.getSelectionModel().select(massMinuends.get(row));

            ComboBox<MassStationDetail> massSubtrahendComboBox = addMassComboFactory(allMassStations);
            massDeltasGridPane.add(massSubtrahendComboBox, 2, row);
            massSubtrahendComboBox.getSelectionModel().select(massSubtrahends.get(row));
        }

        // setup first add button
        massDeltasGridPane.add(addRemoveButtonFactory("+"), 0, massMinuends.size());

    }

    private Button addRemoveButtonFactory(String labelText) {
        Button addRemoveButton = new Button(labelText);
        addRemoveButton.setMaxWidth(BUTTON_WIDTH);
        addRemoveButton.setMinWidth(BUTTON_WIDTH);
        addRemoveButton.setPrefWidth(BUTTON_WIDTH);
        addRemoveButton.setMaxHeight(ROW_HEIGHT);
        addRemoveButton.setMinHeight(ROW_HEIGHT);
        addRemoveButton.setPrefHeight(ROW_HEIGHT);
        addRemoveButton.setStyle(STYLE_BUTTON_LABEL);
        addRemoveButton.setOnAction(new onAddRemoveButtonAction());

        return addRemoveButton;
    }

    private ComboBox<MassStationDetail> addMassComboFactory(ObservableList<MassStationDetail> massStatonDetails) {
        ComboBox<MassStationDetail> massComboBox = new ComboBox<>(massStatonDetails);

        massComboBox.setMaxWidth(COMBO_WIDTH);
        massComboBox.setMinWidth(COMBO_WIDTH);
        massComboBox.setPrefWidth(COMBO_WIDTH);
        massComboBox.setCellFactory(
                (parameter)
                        -> new MassStationDetailListCell()
        );
        massComboBox.setConverter(new MassStationDetailStringConverter());
        massComboBox.setOnAction(new onMassDeltaComboSelectionAction());
        massComboBox.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);

        return massComboBox;
    }

    @FXML
    private void displayMassesAction(ActionEvent actionEvent) {
        countsRadioButtonChoice = 0b00;
        try {
            displayMassStationsForReview();
        } catch (SquidException squidException) {
        }
    }

    @FXML
    private void displayTotalCountsAction(ActionEvent actionEvent) {
        countsRadioButtonChoice = 0b10;
        try {
            displayMassStationsForReview();
        } catch (SquidException squidException) {
        }
    }

    @FXML
    private void displayTotalSBMAction(ActionEvent actionEvent) {
        countsRadioButtonChoice = 0b01;
        try {
            displayMassStationsForReview();
        } catch (SquidException squidException) {
        }
    }

    @FXML
    private void displayBothCountsAction(ActionEvent actionEvent) {
        countsRadioButtonChoice = 0b11;
        try {
            displayMassStationsForReview();
        } catch (SquidException squidException) {
        }
    }

    private void produceMassModeGraphOnScrolledPane(int massCounter, String title, List<Double> data, MassStationDetail entry) throws SquidException {
        // plot legend
        SpeciesAMUAuditViewForShrimp legendCanvas
                = new SpeciesAMUAuditViewForShrimp(new Rectangle(0, (massCounter * heightOfMassPlot) + 25, LEGEND_WIDTH, heightOfMassPlot),
                title,
                data,
                entry.getTimesOfMeasuredTrimMasses(),
                entry.getIndicesOfScansAtMeasurementTimes(),
                entry.getIndicesOfRunsAtMeasurementTimes(),
                squidProject.getPrawnFileRuns(),
                showTimeNormalized,
                showSpotLabels,
                this, -1,
                0, 0, 0);

        scrolledBoxLeft.getChildren().add(legendCanvas);
        legendGraphs.add(legendCanvas);
        GraphicsContext gc0 = legendCanvas.getGraphicsContext2D();
        legendCanvas.preparePanel();
        legendCanvas.paint(gc0);

        // prepare for three plots (canvas) = leadingSpots, zoomedSpots, trailingSpots
        HBox graphsBox = new HBox();
        scrolledBox.getChildren().add(graphsBox);

        // determine indices of details
        int endLeadingIndex = 0;
        int endZoomedIndex = 0;
        for (int i = 0; i < entry.getIndicesOfRunsAtMeasurementTimes().size(); i++) {
            if (entry.getIndicesOfRunsAtMeasurementTimes().get(i) < zoomedStart) {
                endLeadingIndex = i;
            } else if (entry.getIndicesOfRunsAtMeasurementTimes().get(i) <= zoomedEnd) {
                endZoomedIndex = i;
            } else {
                break;
            }
        }

        int widthOfView = plotWidthOfSpots * (zoomedStart - 1);
        if (endLeadingIndex > 0) {
            // leading canvas
            // sublist excludes right end of range
            endLeadingIndex += 1;
            AbstractDataView leadingCanvas
                    = new SpeciesAMUAuditViewForShrimp(new Rectangle(0, (massCounter * heightOfMassPlot) + 25, widthOfView, heightOfMassPlot),
                    title,
                    data.subList(0, endLeadingIndex),
                    entry.getTimesOfMeasuredTrimMasses().subList(0, endLeadingIndex),
                    entry.getIndicesOfScansAtMeasurementTimes().subList(0, endLeadingIndex),
                    entry.getIndicesOfRunsAtMeasurementTimes().subList(0, endLeadingIndex),
                    squidProject.getPrawnFileRuns().subList(0, zoomedStart - 1),
                    showTimeNormalized,
                    showSpotLabels,
                    this,
                    -1,
                    legendCanvas.getMinY_Display(), legendCanvas.getMaxY_Display(), legendCanvas.getPlottedMean());

            graphsBox.getChildren().add(leadingCanvas);
            leadingGraphs.add(leadingCanvas);
            GraphicsContext gc1 = leadingCanvas.getGraphicsContext2D();
            leadingCanvas.preparePanel();
            leadingCanvas.paint(gc1);
        }

        // zoomed canvas
        // sublist excludes right end of range
        endZoomedIndex += 1;
        widthOfView = plotWidthOfZoomedSpots * (zoomedWidth);
        AbstractDataView zoomedCanvas
                = new SpeciesAMUAuditViewForShrimp(new Rectangle(0, (massCounter * heightOfMassPlot) + 25, widthOfView, heightOfMassPlot),
                title,
                data.subList(endLeadingIndex, endZoomedIndex),
                entry.getTimesOfMeasuredTrimMasses().subList(endLeadingIndex, endZoomedIndex),
                entry.getIndicesOfScansAtMeasurementTimes().subList(endLeadingIndex, endZoomedIndex),
                entry.getIndicesOfRunsAtMeasurementTimes().subList(endLeadingIndex, endZoomedIndex),
                squidProject.getPrawnFileRuns().subList(zoomedStart - 1, zoomedEnd),
                showTimeNormalized,
                showSpotLabels,
                this,
                0,
                legendCanvas.getMinY_Display(), legendCanvas.getMaxY_Display(), legendCanvas.getPlottedMean());

        graphsBox.getChildren().add(zoomedCanvas);
        zoomingGraphs.add(zoomedCanvas);
        GraphicsContext gc2 = zoomedCanvas.getGraphicsContext2D();
        zoomedCanvas.preparePanel();
        zoomedCanvas.paint(gc2);

        // trailing canvas
        int endTrailingIndex = data.size();
        widthOfView = plotWidthOfSpots * (squidProject.getPrawnFileRuns().size() - zoomedEnd);

        if (widthOfView > 0) {
            AbstractDataView trailingCanvas
                    = new SpeciesAMUAuditViewForShrimp(new Rectangle(0, (massCounter * heightOfMassPlot) + 25, widthOfView, heightOfMassPlot),
                    title,
                    data.subList(endZoomedIndex, endTrailingIndex),
                    entry.getTimesOfMeasuredTrimMasses().subList(endZoomedIndex, endTrailingIndex),
                    entry.getIndicesOfScansAtMeasurementTimes().subList(endZoomedIndex, endTrailingIndex),
                    entry.getIndicesOfRunsAtMeasurementTimes().subList(endZoomedIndex, endTrailingIndex),
                    squidProject.getPrawnFileRuns().subList(zoomedEnd, squidProject.getPrawnFileRuns().size()),
                    showTimeNormalized,
                    showSpotLabels,
                    this,
                    1,
                    legendCanvas.getMinY_Display(), legendCanvas.getMaxY_Display(), legendCanvas.getPlottedMean());

            graphsBox.getChildren().add(trailingCanvas);
            trailingGraphs.add(trailingCanvas);
            GraphicsContext gc3 = trailingCanvas.getGraphicsContext2D();
            trailingCanvas.preparePanel();
            trailingCanvas.paint(gc3);
        }
    }

    private void updateSpeciesAMUAuditCanvas(
            SpeciesGraphInterface spotsCanvas, List<Double> data, List<Double> dataII, int index, int startIndex, int endIndex, int startSpotIndex, int endSpotIndex) throws SquidException {

        if (spotsCanvas instanceof SpeciesCountsAuditViewForShrimp) {
            ((SpeciesCountsAuditViewForShrimp) spotsCanvas).setTotalCounts(data.subList(startIndex, endIndex));
            if (dataII != null) {
                ((SpeciesCountsAuditViewForShrimp) spotsCanvas).setTotalCountsSBM(dataII.subList(startIndex, endIndex));
            }
        } else {
            spotsCanvas.setMeasuredTrimMasses(data.subList(startIndex, endIndex));
        }

        spotsCanvas.setTimesOfMeasuredTrimMasses(((SpeciesGraphInterface) legendGraphs.get(index)).getTimesOfMeasuredTrimMasses().subList(startIndex, endIndex));

        // protect against internal changes
        List<Integer> indicesOfScansAtMeasurementTimes =
                new ArrayList<>(((SpeciesGraphInterface) legendGraphs.get(index)).getIndicesOfScansAtMeasurementTimes().subList(startIndex, endIndex));
        spotsCanvas.setIndicesOfScansAtMeasurementTimes(indicesOfScansAtMeasurementTimes);

        spotsCanvas.setIndicesOfRunsAtMeasurementTimes(((SpeciesGraphInterface) legendGraphs.get(index)).getIndicesOfRunsAtMeasurementTimes().subList(startIndex, endIndex));
        spotsCanvas.setPrawnFileRuns(squidProject.getPrawnFileRuns().subList(startSpotIndex, endSpotIndex));
        ((AbstractDataView) spotsCanvas).preparePanel();
        ((AbstractDataView) spotsCanvas).repaint();
    }

    private void updateAllMassesCanvases(int savedZoomedStart) throws SquidException {
        int endLeadingIndex = 0;
        int endZoomedIndex = 0;
        List<Integer> entry0 = ((SpeciesGraphInterface) legendGraphs.get(0)).getIndicesOfRunsAtMeasurementTimes();
        for (int i = 0; i < entry0.size(); i++) {
            if (entry0.get(i) < zoomedStart) {
                endLeadingIndex = i;
            } else if (entry0.get(i) <= zoomedEnd) {
                endZoomedIndex = i;
            } else {
                break;
            }
        }

        int widthOfView;
        // leading graphs
        if (endLeadingIndex > 0) {
            // leading canvas
            // sublist excludes right end of range
            endLeadingIndex += 1;

            widthOfView = plotWidthOfSpots * (zoomedStart - 1);
            int index = 0;
            for (AbstractDataView spotsCanvas : leadingGraphs) {
                spotsCanvas.setMyWidth(widthOfView);
                spotsCanvas.setGraphWidth(widthOfView);
                updateSpeciesAMUAuditCanvas((SpeciesGraphInterface) spotsCanvas,
                        ((SpeciesGraphInterface) legendGraphs.get(index)).getMeasuredTrimMasses(),
                        (spotsCanvas instanceof SpeciesCountsAuditViewForShrimp) ? ((SpeciesCountsAuditViewForShrimp) legendGraphs.get(index)).getTotalCountsSBM() : null,
                        index,
                        0, endLeadingIndex, 0, zoomedStart - 1);
                index += 1;
            }
        } else {
            for (AbstractDataView spotsCanvas : leadingGraphs) {
                spotsCanvas.setMyWidth(0);
                spotsCanvas.setGraphWidth(0);
                spotsCanvas.repaint();
            }
        }
        // zoomed canvas
        // sublist excludes right end of range
        endZoomedIndex += 1;
        widthOfView = plotWidthOfZoomedSpots * (zoomedWidth);
        int index = 0;
        for (AbstractDataView spotsCanvas : zoomingGraphs) {
            spotsCanvas.setMyWidth(widthOfView);
            spotsCanvas.setGraphWidth(widthOfView);
            updateSpeciesAMUAuditCanvas((SpeciesGraphInterface) spotsCanvas,
                    ((SpeciesGraphInterface) legendGraphs.get(index)).getMeasuredTrimMasses(),
                    (spotsCanvas instanceof SpeciesCountsAuditViewForShrimp) ? ((SpeciesCountsAuditViewForShrimp) legendGraphs.get(index)).getTotalCountsSBM() : null,
                    index,
                    endLeadingIndex, endZoomedIndex, zoomedStart - 1, zoomedEnd);
            index += 1;
        }
        // adjust selections
        List<Integer> shiftedIndices = new ArrayList<>();
        List<PrawnFile.Run> shiftedRuns = new ArrayList<>();
        if (!AbstractDataView.getListOfSelectedIndices().isEmpty()) {
            int shift = zoomedStart - savedZoomedStart;
            Integer[] targetArray = AbstractDataView.getListOfSelectedIndices().toArray(new Integer[0]);

            for (int i = 0; i < targetArray.length; i++) {
                targetArray[i] -= shift;
                if ((targetArray[i] >= 0) && (targetArray[i] < zoomedWidth)) {
                    shiftedIndices.add(targetArray[i]);
                    shiftedRuns.add(((SpeciesGraphInterface) zoomingGraphs.get(0)).getPrawnFileRuns().get(targetArray[i]));
                }
            }
        }
        updateGraphsWithSelectedIndices(shiftedIndices, shiftedRuns, 0);

        // trailing canvas
        int endTrailingIndex = ((SpeciesGraphInterface) legendGraphs.get(0)).getMeasuredTrimMasses().size();
        widthOfView = plotWidthOfSpots * (squidProject.getPrawnFileRuns().size() - zoomedEnd);

        if (widthOfView > 0) {
            index = 0;
            for (AbstractDataView spotsCanvas : trailingGraphs) {
                spotsCanvas.setMyWidth(widthOfView);
                spotsCanvas.setGraphWidth(widthOfView);
                updateSpeciesAMUAuditCanvas((SpeciesGraphInterface) spotsCanvas,
                        ((SpeciesGraphInterface) legendGraphs.get(index)).getMeasuredTrimMasses(),
                        (spotsCanvas instanceof SpeciesCountsAuditViewForShrimp) ? ((SpeciesCountsAuditViewForShrimp) legendGraphs.get(index)).getTotalCountsSBM() : null,
                        index,
                        endZoomedIndex, endTrailingIndex, zoomedEnd, squidProject.getPrawnFileRuns().size());
                index += 1;
            }
        } else {
            for (AbstractDataView spotsCanvas : trailingGraphs) {
                spotsCanvas.setMyWidth(0);
                spotsCanvas.setGraphWidth(0);
                spotsCanvas.repaint();
            }
        }
    }

    private void produceCountsModeGraphOnScrolledPane(int massCounter, String title, List<Double> countsData, List<Double> countsSBMData, MassStationDetail entry) throws SquidException {
        // plot legend
        AbstractDataView legendCanvas
                = new SpeciesCountsAuditViewForShrimp(new Rectangle(0, (massCounter * heightOfMassPlot) + 25, LEGEND_WIDTH, heightOfMassPlot),
                title,
                countsData,
                countsSBMData,
                countsRadioButtonChoice,
                entry.getTimesOfMeasuredTrimMasses(),
                entry.getIndicesOfScansAtMeasurementTimes(),
                entry.getIndicesOfRunsAtMeasurementTimes(),
                squidProject.getPrawnFileRuns(),
                showTimeNormalized,
                showSpotLabels,
                this, -1, 0, 0, 0, 0);

        scrolledBoxLeft.getChildren().add(legendCanvas);
        legendGraphs.add(legendCanvas);
        GraphicsContext gc0 = legendCanvas.getGraphicsContext2D();
        legendCanvas.preparePanel();
        legendCanvas.paint(gc0);

        // prepare for three plots (canvas) = leadingSpots, zoomedSpots, trailingSpots
        HBox graphsBox = new HBox();
        scrolledBox.getChildren().add(graphsBox);

        // determine indices of details
        int endLeadingIndex = 0;
        int endZoomedIndex = 0;
        for (int i = 0; i < entry.getIndicesOfRunsAtMeasurementTimes().size(); i++) {
            if (entry.getIndicesOfRunsAtMeasurementTimes().get(i) < zoomedStart) {
                endLeadingIndex = i;
            } else if (entry.getIndicesOfRunsAtMeasurementTimes().get(i) <= zoomedEnd) {
                endZoomedIndex = i;
            } else {
                break;
            }
        }

        int widthOfView = plotWidthOfSpots * (zoomedStart - 1);
        if (endLeadingIndex > 0) {
            // leading canvas
            // sublist excludes right end of range
            endLeadingIndex += 1;
            AbstractDataView leadingCanvas
                    = new SpeciesCountsAuditViewForShrimp(new Rectangle(0, (massCounter * heightOfMassPlot) + 25, widthOfView, heightOfMassPlot),
                    title,
                    countsData.subList(0, endLeadingIndex),
                    countsSBMData.subList(0, endLeadingIndex),
                    countsRadioButtonChoice,
                    entry.getTimesOfMeasuredTrimMasses().subList(0, endLeadingIndex),
                    entry.getIndicesOfScansAtMeasurementTimes().subList(0, endLeadingIndex),
                    entry.getIndicesOfRunsAtMeasurementTimes().subList(0, endLeadingIndex),
                    squidProject.getPrawnFileRuns().subList(0, zoomedStart - 1),
                    showTimeNormalized,
                    showSpotLabels,
                    this,
                    -1,
                    legendCanvas.getMinY_Display(), legendCanvas.getMaxY_Display(),
                    ((SpeciesCountsAuditViewForShrimp) legendCanvas).getMinYII(), ((SpeciesCountsAuditViewForShrimp) legendCanvas).getMaxYII());


            graphsBox.getChildren().add(leadingCanvas);
            leadingGraphs.add(leadingCanvas);
            GraphicsContext gc1 = leadingCanvas.getGraphicsContext2D();
            leadingCanvas.preparePanel();
            leadingCanvas.paint(gc1);
        }

        // zoomed canvas
        // sublist excludes right end of range
        endZoomedIndex += 1;
        widthOfView = plotWidthOfZoomedSpots * (zoomedWidth);
        AbstractDataView zoomedCanvas
                = new SpeciesCountsAuditViewForShrimp(new Rectangle(0, (massCounter * heightOfMassPlot) + 25, widthOfView, heightOfMassPlot),
                title,
                countsData.subList(endLeadingIndex, endZoomedIndex),
                countsSBMData.subList(endLeadingIndex, endZoomedIndex),
                countsRadioButtonChoice,
                entry.getTimesOfMeasuredTrimMasses().subList(endLeadingIndex, endZoomedIndex),
                entry.getIndicesOfScansAtMeasurementTimes().subList(endLeadingIndex, endZoomedIndex),
                entry.getIndicesOfRunsAtMeasurementTimes().subList(endLeadingIndex, endZoomedIndex),
                squidProject.getPrawnFileRuns().subList(zoomedStart - 1, zoomedEnd),
                showTimeNormalized,
                showSpotLabels,
                this,
                0, legendCanvas.getMinY_Display(), legendCanvas.getMaxY_Display(),
                ((SpeciesCountsAuditViewForShrimp) legendCanvas).getMinYII(), ((SpeciesCountsAuditViewForShrimp) legendCanvas).getMaxYII());

        graphsBox.getChildren().add(zoomedCanvas);
        zoomingGraphs.add(zoomedCanvas);
        GraphicsContext gc2 = zoomedCanvas.getGraphicsContext2D();
        zoomedCanvas.preparePanel();
        zoomedCanvas.paint(gc2);

        // trailing canvas
        int endTrailingIndex = countsData.size();
        widthOfView = plotWidthOfSpots * (squidProject.getPrawnFileRuns().size() - zoomedEnd);

        if (widthOfView > 0) {
            AbstractDataView trailingCanvas
                    = new SpeciesCountsAuditViewForShrimp(new Rectangle(0, (massCounter * heightOfMassPlot) + 25, widthOfView, heightOfMassPlot),
                    title,
                    countsData.subList(endZoomedIndex, endTrailingIndex),
                    countsSBMData.subList(endZoomedIndex, endTrailingIndex),
                    countsRadioButtonChoice,
                    entry.getTimesOfMeasuredTrimMasses().subList(endZoomedIndex, endTrailingIndex),
                    entry.getIndicesOfScansAtMeasurementTimes().subList(endZoomedIndex, endTrailingIndex),
                    entry.getIndicesOfRunsAtMeasurementTimes().subList(endZoomedIndex, endTrailingIndex),
                    squidProject.getPrawnFileRuns().subList(zoomedEnd, squidProject.getPrawnFileRuns().size()),
                    showTimeNormalized,
                    showSpotLabels,
                    this,
                    1, legendCanvas.getMinY_Display(), legendCanvas.getMaxY_Display(),
                    ((SpeciesCountsAuditViewForShrimp) legendCanvas).getMinYII(), ((SpeciesCountsAuditViewForShrimp) legendCanvas).getMaxYII());

            graphsBox.getChildren().add(trailingCanvas);
            trailingGraphs.add(trailingCanvas);
            GraphicsContext gc3 = trailingCanvas.getGraphicsContext2D();
            trailingCanvas.preparePanel();
            trailingCanvas.paint(gc3);
        }
    }

    private void displayMassStationsForReview() throws SquidException {

        scrolledBox.getChildren().clear();
        scrolledBoxLeft.getChildren().clear();
        legendGraphs.clear();
        leadingGraphs.clear();
        zoomingGraphs.clear();
        trailingGraphs.clear();

        zoomScrollBar.setMin(1);
        zoomScrollBar.setMax(squidProject.getPrawnFileRuns().size());
        zoomScrollBar.setUnitIncrement(2);
        zoomScrollBar.setBlockIncrement(4);
        zoomScrollBar.setVisibleAmount(20);

        int massCounter = 0;
        for (MassStationDetail entry : viewedAsGraphMassStations) {
            if (countsRadioButtonChoice == 0) {
                produceMassModeGraphOnScrolledPane(
                        massCounter,
                        entry.getMassStationLabel() + " " + entry.getIsotopeLabel(),
                        entry.getMeasuredTrimMasses(),
                        entry);
            } else {
                // prep for shrimp fractions to extract total counts and totalSBM
                List<Double> totalCounts = new ArrayList<>();
                List<Double> totalCountsSBM = new ArrayList<>();
                List<ShrimpFractionExpressionInterface> shrimpFractions = squidProject.getTask().getShrimpFractions();
                for (ShrimpFractionExpressionInterface shrimpFraction : shrimpFractions) {
                    // note totalCounts = new double[nScans][nSpecies];
                    double[][] myTotalCounts = shrimpFraction.getTotalCounts();
                    double[][] myTotalCountsSBM = ((ShrimpFraction) shrimpFraction).getTotalCountsSBM();
                    for (int i = 0; i < myTotalCounts.length; i++) {
                        totalCounts.add(myTotalCounts[i][entry.getMassStationIndex()]);
                        totalCountsSBM.add(myTotalCountsSBM[i][entry.getMassStationIndex()]);
                    }
                }

                produceCountsModeGraphOnScrolledPane(
                        massCounter,
                        entry.getMassStationLabel() + " " + entry.getIsotopeLabel(),
                        totalCounts,
                        totalCountsSBM,
                        entry);
            }
            massCounter++;
        }

        for (int i = 0; i < massMinuends.size(); i++) {
            // Feb 2020 need to confirm A nd B are up to date
            MassStationDetail A = getCurrentVersionOfMassStationDetail(massMinuends.get(i));
            MassStationDetail B = getCurrentVersionOfMassStationDetail(massSubtrahends.get(i));
            List<Double> deltas = new ArrayList<>();
            for (int j = 0; j < A.getMeasuredTrimMasses().size(); j++) {
                BigDecimal aBD = new BigDecimal(A.getMeasuredTrimMasses().get(j));
                BigDecimal bBD = new BigDecimal(B.getMeasuredTrimMasses().get(j));
                deltas.add(aBD.subtract(bBD).doubleValue());
            }

            produceMassModeGraphOnScrolledPane(
                    massCounter,
                    A.getMassStationLabel() + " " + A.getIsotopeLabel()
                            + " - "
                            + B.getMassStationLabel() + " " + B.getIsotopeLabel(),
                    deltas,
                    A
            );

            massCounter++;
        }

        // primary beam
        // TODO: decide details of showing it
        if (showPrimaryBeam) {
            List<ShrimpFractionExpressionInterface> spots = squidProject.getTask().getShrimpFractions();
            List<Double> primaryBeam = new ArrayList<>();
            for (int i = 0; i < squidProject.getPrawnFileRuns().size(); i++) {
                int countOfScans = Integer.parseInt(squidProject.getPrawnFileRuns().get(i).getPar().get(3).getValue());
                for (int j = 0; j < countOfScans; j++) {
                    primaryBeam.add(spots.get(i).getPrimaryBeam());
                }
            }

            produceMassModeGraphOnScrolledPane(
                    massCounter,
                    "Primary Beam",
                    primaryBeam,
                    allMassStations.get(0)
            );

            massCounter++;
        }

        // qt1y
        // TODO: decide details of showing it
        if (showQt1y) {
            List<ShrimpFractionExpressionInterface> spots = squidProject.getTask().getShrimpFractions();
            List<Double> qt1y = new ArrayList<>();
            for (int i = 0; i < squidProject.getPrawnFileRuns().size(); i++) {
                int countOfScans = Integer.parseInt(squidProject.getPrawnFileRuns().get(i).getPar().get(3).getValue());
                for (int j = 0; j < countOfScans; j++) {
                    qt1y.add((double) spots.get(i).getQt1Y());
                }
            }
            produceMassModeGraphOnScrolledPane(
                    massCounter,
                    "qt1y",
                    qt1y,
                    allMassStations.get(0)
            );

            massCounter++;
        }

        // qt1z
        // TODO: decide details of showing it
        if (showQt1z) {
            List<ShrimpFractionExpressionInterface> spots = squidProject.getTask().getShrimpFractions();
            List<Double> qt1z = new ArrayList<>();
            for (int i = 0; i < squidProject.getPrawnFileRuns().size(); i++) {
                int countOfScans = Integer.parseInt(squidProject.getPrawnFileRuns().get(i).getPar().get(3).getValue());
                for (int j = 0; j < countOfScans; j++) {
                    qt1z.add((double) spots.get(i).getQt1Z());
                }
            }
            produceMassModeGraphOnScrolledPane(
                    massCounter,
                    "qt1z",
                    qt1z,
                    allMassStations.get(0)
            );

            massCounter++;
        }

    }

    private MassStationDetail getCurrentVersionOfMassStationDetail(MassStationDetail massStationDetail) {
        // MassStationDetails are solely equal based on mass station number
        MassStationDetail retVal = allMassStations.get(allMassStations.indexOf(massStationDetail));

        return retVal;
    }

    @Override
    public void updateGraphsWithSelectedIndex(int index, int leadingZoomingTrailing) throws SquidException {
        List<AbstractDataView> activeGraphs = new ArrayList<>();
        switch (leadingZoomingTrailing) {
            case -1:
                activeGraphs.addAll(leadingGraphs);
                break;
            case 0:
                activeGraphs.addAll(zoomingGraphs);
                break;
            case 1:
                activeGraphs.addAll(trailingGraphs);
                break;
        }

        for (int i = 0; i < activeGraphs.size(); i++) {
            ((SpeciesGraphInterface) activeGraphs.get(i)).setIndexOfSelectedSpot(index);
            activeGraphs.get(i).repaint();
        }
    }

    @Override
    public void updateGraphsWithSelectedIndices(List<Integer> listOfSelectedSpotsIndices, List<PrawnFile.Run> selectedRuns, int leadingZoomingTrailing) throws SquidException {
        List<AbstractDataView> activeGraphs = new ArrayList<>();
        switch (leadingZoomingTrailing) {
            case -1:
                // activeGraphs.addAll(leadingGraphs);
                break;
            case 0:
                activeGraphs.addAll(zoomingGraphs);
                break;
            case 1:
                //  activeGraphs.addAll(trailingGraphs);
                break;
        }

        for (int i = 0; i < activeGraphs.size(); i++) {
            AbstractDataView.setListOfSelectedIndices(listOfSelectedSpotsIndices);
            AbstractDataView.setSelectedRuns(selectedRuns);
            activeGraphs.get(i).repaint();
        }
    }

    /**
     *
     */
    @Override
    public void updateSpotsInGraphs() throws SquidException {
        // May 2021 fixes issue #618
        squidProject.getTask().setChanged(true);

        squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(true);

        calculateCountOfScansCumulative();
        setupMassStationDetailsListViews();
        displayMassStationsForReview();
    }

    @FXML
    private void resetMassesAuditGraphs(ActionEvent actionEvent) throws SquidException {
        ((Task) squidProject.getTask()).resetMassStationGraphViews();
        SquidUIController primaryStageController = (SquidUIController) primaryStageWindow.getScene().getUserData();
        primaryStageController.launchMassesAudit();
    }

    static class MassStationDetailListCell extends ListCell<MassStationDetail> {

        @Override
        protected void updateItem(MassStationDetail msd, boolean empty) {
            super.updateItem(msd, empty);
            if (msd == null || empty) {
                setText(null);
            } else {
                setText(msd.toPrettyString());
            }
        }
    }

    static class MassStationDetailStringConverter extends StringConverter<MassStationDetail> {

        @Override
        public String toString(MassStationDetail msd) {
            if (msd == null) {
                return null;
            } else {
                return msd.toPrettyString();
            }
        }

        @Override
        public MassStationDetail fromString(String massStationDetailString) {
            return null; // No conversion fromString needed.
        }
    }

    class onDragDetectedEventHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            /* drag was detected, start a drag-and-drop gesture */
            Dragboard db = ((ListView) event.getSource()).startDragAndDrop(TransferMode.COPY_OR_MOVE);
            ClipboardContent content = new ClipboardContent();
            content.put(MASS_DETAIL_FORMAT, ((ListView) event.getSource()).getSelectionModel().getSelectedItem());
            db.setContent(content);
            db.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));
            event.consume();
        }
    }

    class onDragOverEventHandler implements EventHandler<DragEvent> {

        @Override
        public void handle(DragEvent event) {
            /* data is dragged over the target
             * accept it only if it is not dragged from the same node*/
            if (event.getGestureSource() != event.getSource()
                    && event.getDragboard().hasContent(MASS_DETAIL_FORMAT)) {

                event.acceptTransferModes(TransferMode.MOVE);

            }
            event.consume();
        }
    }

    class onDragDroppedEventHandler implements EventHandler<DragEvent> {

        private final ListView<MassStationDetail> myListView;

        public onDragDroppedEventHandler(ListView<MassStationDetail> myListView) {
            this.myListView = myListView;
        }

        @Override
        public void handle(DragEvent event) {
            /* data dropped */
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasContent(MASS_DETAIL_FORMAT)) {
                myListView.getItems().add((MassStationDetail) db.getContent(MASS_DETAIL_FORMAT));
                Collections.sort(myListView.getItems());
                myListView.refresh();

                success = true;
            }
            /* let the source know whether the MassStationDetail was successfully transferred and used */
            event.setDropCompleted(success);

            event.consume();
        }
    }

    class onDragDoneEventHandler implements EventHandler<DragEvent> {

        private final ListView<MassStationDetail> myListView;

        public onDragDoneEventHandler(ListView<MassStationDetail> myListView) {
            this.myListView = myListView;
        }

        @Override
        public void handle(DragEvent event) {
            /* the drag and drop gesture ended if the data was successfully moved, clear it */
            if (event.getTransferMode().equals(TransferMode.MOVE)) {
                MassStationDetail massStationDetail = (MassStationDetail) event.getDragboard().getContent(MASS_DETAIL_FORMAT);
                myListView.getItems().remove(massStationDetail);
                massStationDetail.setViewedAsGraph(!myListView.idProperty().getValue().contains("viewedAsGraph"));

                SquidSpeciesModel ssm = squidProject.getTask().getSquidSpeciesModelList().get(massStationDetail.getMassStationIndex());
                ssm.setViewedAsGraph(massStationDetail.isViewedAsGraph());

                try {
                    displayMassStationsForReview();
                } catch (SquidException squidException) {
                }
            }
            event.consume();
        }
    }

    class onMassDeltaComboSelectionAction implements EventHandler<ActionEvent> {

        @SuppressWarnings("unchecked")
        @Override
        public void handle(ActionEvent event) {
            // reprocess all diffs
            massMinuends.clear();
            massSubtrahends.clear();
            for (int i = 0; i < massDeltasGridPane.getChildren().size(); i += 3) {
                // check for combo boxes in row
                if (massDeltasGridPane.getChildren().size() >= (i + 2)) {
                    ComboBox<MassStationDetail> massMinuendComboBox
                            = (ComboBox<MassStationDetail>) massDeltasGridPane.getChildren().get(i + 1);
                    ComboBox<MassStationDetail> massSubtrahendComboBox
                            = (ComboBox<MassStationDetail>) massDeltasGridPane.getChildren().get(i + 2);
                    MassStationDetail massMinuend = massMinuendComboBox.getSelectionModel().getSelectedItem();
                    MassStationDetail massSubtrahend = massSubtrahendComboBox.getSelectionModel().getSelectedItem();

                    if ((massMinuend != null) && (massSubtrahend != null)) {
                        try {
                            massMinuends.remove(i / 3);
                        } catch (Exception e) {
                        }
                        massMinuends.add(i / 3, massMinuend);
                        try {
                            massSubtrahends.remove(i / 3);
                        } catch (Exception e) {
                        }
                        massSubtrahends.add(i / 3, massSubtrahend);
                    }
                }

                try {
                    displayMassStationsForReview();
                } catch (SquidException squidException) {
                }
            }
        }

    }

    class onAddRemoveButtonAction implements EventHandler<ActionEvent> {

        private ComboBox<MassStationDetail> massMinuendComboBox;
        private ComboBox<MassStationDetail> massSubtrahendComboBox;

        @Override
        public void handle(ActionEvent e) {
            Button addRemoveButton = (Button) e.getSource();
            int row = GridPane.getRowIndex(addRemoveButton);

            if (addRemoveButton.getText().compareTo("+") == 0) {
                addRemoveButton.setText("-");

                massMinuendComboBox = addMassComboFactory(allMassStations);
                massDeltasGridPane.add(massMinuendComboBox, 1, row);

                massSubtrahendComboBox = addMassComboFactory(allMassStations);
                massDeltasGridPane.add(massSubtrahendComboBox, 2, row);

                // setup next button
                massDeltasGridPane.add(addRemoveButtonFactory("+"), 0, row + 1);

            } else {
                // case remove
                massDeltasGridPane.getChildren().remove(addRemoveButton);

                massDeltasGridPane.getChildren().remove(massMinuendComboBox);

                massDeltasGridPane.getChildren().remove(massSubtrahendComboBox);
                // test to reset grid
                if (massDeltasGridPane.getChildren().isEmpty()) {
                    // setup first button
                    massDeltasGridPane.add(addRemoveButtonFactory("+"), 0, 0);
                    massMinuends.clear();
                    massSubtrahends.clear();
                } else {

                    try {
                        massMinuends.remove(row);
                    } catch (Exception exception) {
//                        exception.printStackTrace();
                    }
                    try {
                        massSubtrahends.remove(row);
                    } catch (Exception exception) {
//                        exception.printStackTrace();
                    }

                    for (Node node : massDeltasGridPane.getChildren()) {
                        if (GridPane.getRowIndex(node) > row) {
                            GridPane.setRowIndex(node, GridPane.getRowIndex(node) - 1);
                        }
                    }
                }
                if (massMinuends.isEmpty()) {
                    massDeltasGridPane.getChildren().clear();
                    // setup first add button
                    massDeltasGridPane.add(addRemoveButtonFactory("+"), 0, 0);
                }
                try {
                    displayMassStationsForReview();
                } catch (SquidException squidException) {
                }
            }
        }

    }

}