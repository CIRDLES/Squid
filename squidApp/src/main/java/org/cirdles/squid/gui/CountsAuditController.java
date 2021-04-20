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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;
import org.cirdles.squid.gui.dataViews.AbstractDataView;
import org.cirdles.squid.gui.dataViews.MassAuditRefreshInterface;
import org.cirdles.squid.gui.dataViews.SpeciesCountsAuditViewForShrimp;
import org.cirdles.squid.prawn.PrawnFile.Run;
import org.cirdles.squid.shrimp.MassStationDetail;
import org.cirdles.squid.shrimp.ShrimpFraction;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidSpeciesModel;

import java.net.URL;
import java.util.*;

import static org.cirdles.squid.constants.Squid3Constants.MASS_DETAIL_FORMAT;
import static org.cirdles.squid.gui.SquidUI.SQUID_LOGO_SANS_TEXT_URL;
import static org.cirdles.squid.gui.SquidUIController.squidProject;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class CountsAuditController implements Initializable, MassAuditRefreshInterface {

    private static final String STYLE_BUTTON_LABEL
            = "-fx-font-family: 'Monospaced', 'SansSerif';\n"
            + "    -fx-font-weight: bold;\n"
            + "    -fx-font-size: 12pt;\n";
    // binary: 01 = SBM, 10 = Counts; 11 = both
    private static int countsRadioButtonChoice = 0B11;
    private static ObservableList<MassStationDetail> allMassStations;
    private static ObservableList<MassStationDetail> availableMassStations;
    private static ObservableList<MassStationDetail> viewedAsGraphMassStations;
    private static boolean showTimeNormalized;
    private static boolean showSpotLabels;
    private static boolean synchedScrolls;
    private final int BUTTON_WIDTH = 60;
    private final int COMBO_WIDTH = 210;
    private final int ROW_HEIGHT = 30;
    @FXML
    private RadioButton displayTotalCountsRB;
    @FXML
    private RadioButton displayTotalSBMRB;
    @FXML
    private RadioButton displayBothCountsRB;
    private int[] countOfScansCumulative;
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
    private ScrollPane leftScrollPane;
    @FXML
    private ScrollPane rightScrollPane;
    private List<AbstractDataView> graphs;
    @FXML
    private CheckBox displaySpotLabelsCheckBox;

    public CountsAuditController() {
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        setCountChoiceRatioButton();

        setupMassStationDetailsListViews();

        showTimeNormalized = squidProject.getTask().isShowTimeNormalized();
        normalizeTimeAxisCheckBox.setSelected(showTimeNormalized);

        showSpotLabels = squidProject.getTask().isShowSpotLabels();
        displaySpotLabelsCheckBox.setSelected(showSpotLabels);

        graphs = new ArrayList<>();

        calculateCountOfScansCumulative();

        displayMassStationsForReview();

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

    private void setupMassStationDetailsListViews() {
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

        availableMassStations = FXCollections.observableArrayList(availableMassStationDetails);
        availableMassesListView.setItems(availableMassStations);
        availableMassesListView.setCellFactory(
                (parameter)
                        -> new CountsAuditController.MassStationDetailListCell()
        );

        availableMassesListView.setOnDragDetected(new CountsAuditController.onDragDetectedEventHandler());
        availableMassesListView.setOnDragOver(new CountsAuditController.onDragOverEventHandler());
        availableMassesListView.setOnDragDropped(new CountsAuditController.onDragDroppedEventHandler(availableMassesListView));
        availableMassesListView.setOnDragDone(new CountsAuditController.onDragDoneEventHandler(availableMassesListView));
        availableMassesListView.setId("");

        availableMassesListView.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);

        viewedAsGraphMassStations = FXCollections.observableArrayList(viewedAsGraphMassStationDetails);
        viewedAsGraphMassesListView.setItems(viewedAsGraphMassStations);
        viewedAsGraphMassesListView.setCellFactory(
                (parameter)
                        -> new CountsAuditController.MassStationDetailListCell()
        );

        viewedAsGraphMassesListView.setOnDragDetected(new CountsAuditController.onDragDetectedEventHandler());
        viewedAsGraphMassesListView.setOnDragOver(new CountsAuditController.onDragOverEventHandler());
        viewedAsGraphMassesListView.setOnDragDropped(new CountsAuditController.onDragDroppedEventHandler(viewedAsGraphMassesListView));
        viewedAsGraphMassesListView.setOnDragDone(new CountsAuditController.onDragDoneEventHandler(viewedAsGraphMassesListView));
        viewedAsGraphMassesListView.setId("viewedAsGraph");

        viewedAsGraphMassesListView.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);

        // used in paired differences
        allMassStations = FXCollections.observableArrayList(allMassStationDetails);
    }

    @FXML
    private void normalizeTimeAxisCheckBoxAction(ActionEvent event) {
        showTimeNormalized = normalizeTimeAxisCheckBox.isSelected();
        squidProject.getTask().setShowTimeNormalized(showTimeNormalized);
        displayMassStationsForReview();
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

    public int[] getCountOfScansCumulative() {
        return countOfScansCumulative;
    }

    @FXML
    private void displaySpotLabelsCheckBoxAction(ActionEvent event) {
        showSpotLabels = displaySpotLabelsCheckBox.isSelected();
        squidProject.getTask().setShowSpotLabels(showSpotLabels);
        displayMassStationsForReview();
    }


    private void setCountChoiceRatioButton() {
        switch (countsRadioButtonChoice) {
            case 0b01:
                displayTotalSBMRB.setSelected(true);
                break;
            case 0b10:
                displayTotalCountsRB.setSelected(true);
                break;
            default:
                displayBothCountsRB.setSelected(true);
        }
    }

    @FXML
    private void displayTotalCountsAction(ActionEvent actionEvent) {
        countsRadioButtonChoice = 0b10;
        displayMassStationsForReview();
    }

    @FXML
    private void displayTotalSBMAction(ActionEvent actionEvent) {
        countsRadioButtonChoice = 0b01;
        displayMassStationsForReview();
    }

    @FXML
    private void displayBothCountsAction(ActionEvent actionEvent) {
        countsRadioButtonChoice = 0b11;
        displayMassStationsForReview();
    }

    private void produceGraphOnScrolledPane(int massCounter, String title, List<Double> countsData, List<Double> countsSBMData, MassStationDetail entry, VBox scrolledBox) {

        int heightOfMassPlot = 150;
        // aug 2018 to manage large spot counts and pixels used
        int customSpotWidth = (1000 - squidProject.getPrawnFileRuns().size()) / 30;//was just 25, then 15
        int widthOfView = squidProject.getPrawnFileRuns().size() * customSpotWidth + 350;

        AbstractDataView canvas
                = new SpeciesCountsAuditViewForShrimp(
                        new Rectangle(25, (massCounter * heightOfMassPlot) + 25, widthOfView, heightOfMassPlot),
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
                this);

        scrolledBox.getChildren().add(canvas);
        graphs.add(canvas);
        GraphicsContext gc1 = canvas.getGraphicsContext2D();
        canvas.preparePanel();
        canvas.paint(gc1);
    }

    private void displayMassStationsForReview() {

        scrolledBox.getChildren().clear();
        scrolledBoxLeft.getChildren().clear();
        graphs.clear();

        int massCounter = 0;
        for (MassStationDetail entry : viewedAsGraphMassStations) {
            // prep for shrimp fractions
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

            produceGraphOnScrolledPane(
                    massCounter,
                    entry.getMassStationLabel() + " " + entry.getIsotopeLabel(),
                    totalCounts,
                    totalCountsSBM,
                    entry,
                    scrolledBox);
            produceGraphOnScrolledPane(
                    massCounter,
                    entry.getMassStationLabel() + " " + entry.getIsotopeLabel(),
                    totalCounts,
                    totalCountsSBM,
                    entry,
                    scrolledBoxLeft);
            massCounter++;
        }
    }

    private MassStationDetail getCurrentVersionOfMassStationDetail(MassStationDetail massStationDetail) {
        // MassStationDetails are solely equal based on mass station number
        MassStationDetail retVal = allMassStations.get(allMassStations.indexOf(massStationDetail));

        return retVal;
    }

    @Override
    public void updateGraphsWithSelectedIndex(int index) {
        for (int i = 0; i < graphs.size(); i++) {
            ((SpeciesCountsAuditViewForShrimp) graphs.get(i)).setIndexOfSelectedSpot(index);
            graphs.get(i).repaint();
        }
    }

    @Override
    public void updateGraphsWithSecondSelectedIndex(int index) {
        for (int i = 0; i < graphs.size(); i++) {
            ((SpeciesCountsAuditViewForShrimp) graphs.get(i)).setIndexOfSecondSelectedSpotForMultiSelect(index);
            graphs.get(i).repaint();
        }
    }

    /**
     *
     */
    @Override
    public void updateSpotsInGraphs() {
        calculateCountOfScansCumulative();
        setupMassStationDetailsListViews();
        displayMassStationsForReview();
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

                displayMassStationsForReview();
            }
            event.consume();
        }
    }

}