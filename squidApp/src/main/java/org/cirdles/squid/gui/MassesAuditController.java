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

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;
import static org.cirdles.squid.gui.SquidUI.SQUID_LOGO_SANS_TEXT_URL;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.gui.dataViews.AbstractDataView;
import org.cirdles.squid.gui.dataViews.MassStationAuditViewForShrimp;
import org.cirdles.squid.gui.dataViews.PrimaryBeamAuditViewForShrimp;
import org.cirdles.squid.shrimp.MassStationDetail;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidSpeciesModel;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class MassesAuditController implements Initializable {

    private final int BUTTON_WIDTH = 60;
    private final int COMBO_WIDTH = 210;
    private final int ROW_HEIGHT = 30;

    private static final String STYLE_BUTTON_LABEL = "-fx-font-family: \"Courier New\", \"Lucida Sans\", \"Segoe UI\", Helvetica, Arial, sans-serif;\n"
            + "    -fx-font-weight: bold;\n"
            + "    -fx-font-size: 12pt;\n";

    @FXML
    private VBox scrolledBox;
    @FXML
    private TitledPane massChooserAccordion;
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

    private static ObservableList<MassStationDetail> allMassStations;
    private static ObservableList<MassStationDetail> availableMassStations;
    private static ObservableList<MassStationDetail> viewedAsGraphMassStations;
    private static final DataFormat MASS_DETAIL_FORMAT = new DataFormat("Mass station detail");

    private static List<MassStationDetail> massMinuends;
    private static List<MassStationDetail> massSubtrahends;

    private static boolean showTimeNormalized;
    private static boolean showPrimaryBeam;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        setupMassStationDetailsListViews();
        setupMassDeltas();

        showTimeNormalized = squidProject.getTask().isShowTimeNormalized();
        normalizeTimeAxisCheckBox.setSelected(showTimeNormalized);

        showPrimaryBeam = squidProject.getTask().isShowPrimaryBeam();
        showPrimaryBeamCheckBox.setSelected(showPrimaryBeam);

        displayMassStationsForReview();

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
        displayMassStationsForReview();
    }

    @FXML
    private void showPrimaryBeamCheckBoxAction(ActionEvent event) {
        showPrimaryBeam = showPrimaryBeamCheckBox.isSelected();
        squidProject.getTask().setShowPrimaryBeam(showPrimaryBeam);
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
    };

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
    };

    class onDragOverEventHandler implements EventHandler<DragEvent> {

        @Override
        public void handle(DragEvent event) {
            /* data is dragged over the target 
                * accept it only if it is not dragged from the same node*/
            if (event.getGestureSource() != ((ListView) event.getSource())
                    && event.getDragboard().hasContent(MASS_DETAIL_FORMAT)) {

                event.acceptTransferModes(TransferMode.MOVE);

            }
            event.consume();
        }
    };

    class onDragDroppedEventHandler implements EventHandler<DragEvent> {

        private ListView<MassStationDetail> myListView;

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
    };

    class onDragDoneEventHandler implements EventHandler<DragEvent> {

        private ListView<MassStationDetail> myListView;

        public onDragDoneEventHandler(ListView<MassStationDetail> myListView) {
            this.myListView = myListView;
        }

        @Override
        public void handle(DragEvent event) {
            /* the drag and drop gesture ended if the data was successfully moved, clear it */
            if (event.getTransferMode() == TransferMode.MOVE) {
                MassStationDetail massStationDetail = (MassStationDetail) event.getDragboard().getContent(MASS_DETAIL_FORMAT);
                myListView.getItems().remove(massStationDetail);
                massStationDetail.setViewedAsGraph(!myListView.idProperty().getValue().contains("viewedAsGraph"));

                SquidSpeciesModel ssm = squidProject.getTask().getSquidSpeciesModelList().get(massStationDetail.getMassStationIndex());
                ssm.setViewedAsGraph(massStationDetail.isViewedAsGraph());

                displayMassStationsForReview();
            }
            event.consume();
        }
    };

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

    class onMassDeltaComboSelectionAction implements EventHandler<ActionEvent> {

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

                displayMassStationsForReview();
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
                    if (massMinuendComboBox.getSelectionModel().getSelectedItem() != null) {
                        massMinuends.remove(row);
                    }
                    if (massSubtrahendComboBox.getSelectionModel().getSelectedItem() != null) {
                        massSubtrahends.remove(row);
                    }

                    // fix row index of all downstream
                    for (Node node : massDeltasGridPane.getChildren()) {
                        if (GridPane.getRowIndex(node) > row) {
                            GridPane.setRowIndex(node, GridPane.getRowIndex(node) - 1);
                        }
                    }
                }

                displayMassStationsForReview();
            }
        }

    }

    private void displayMassStationsForReview() {

        scrolledBox.getChildren().clear();
        scrolledBox.getChildren().add(massChooserAccordion);

        int heightOfMassPlot = 150;

        // plotting mass variations
        int widthOfView = squidProject.getPrawnFileRuns().size() * 25 + 350;

        int massCounter = 0;
        for (MassStationDetail entry : viewedAsGraphMassStations) {
            AbstractDataView canvas
                    = new MassStationAuditViewForShrimp(new Rectangle(25, (massCounter * heightOfMassPlot) + 25, widthOfView, heightOfMassPlot),
                            entry.getMassStationLabel() + "  " + entry.getIsotopeLabel(),
                            entry.getMeasuredTrimMasses(),
                            entry.getTimesOfMeasuredTrimMasses(),
                            entry.getIndicesOfScansAtMeasurementTimes(),
                            entry.getIndicesOfRunsAtMeasurementTimes(),
                            showTimeNormalized);

            scrolledBox.getChildren().add(canvas);
            GraphicsContext gc1 = canvas.getGraphicsContext2D();
            canvas.preparePanel();
            canvas.paint(gc1);

            massCounter++;
        }

        for (int i = 0; i < massMinuends.size(); i++) {
            MassStationDetail A = massMinuends.get(i);
            MassStationDetail B = massSubtrahends.get(i);
            List<Double> deltas = new ArrayList<>();
            for (int j = 0; j < A.getMeasuredTrimMasses().size(); j++) {
                BigDecimal aBD = new BigDecimal(A.getMeasuredTrimMasses().get(j));
                BigDecimal bBD = new BigDecimal(B.getMeasuredTrimMasses().get(j));
                deltas.add(aBD.subtract(bBD).doubleValue());
            }

            AbstractDataView canvas
                    = new MassStationAuditViewForShrimp(
                            new Rectangle(25, (massCounter * heightOfMassPlot) + 25, widthOfView, heightOfMassPlot),
                            A.getMassStationLabel() + " - " + B.getMassStationLabel(),
                            deltas,
                            A.getTimesOfMeasuredTrimMasses(),
                            A.getIndicesOfScansAtMeasurementTimes(),
                            A.getIndicesOfRunsAtMeasurementTimes(),
                            showTimeNormalized);

            scrolledBox.getChildren().add(canvas);
            GraphicsContext gc1 = canvas.getGraphicsContext2D();
            canvas.preparePanel();
            canvas.paint(gc1);

            massCounter++;
        }

        // primary beam
        // TODO: decide details of showing it - maybe use special class PrimaryBeamAuditViewForShrimp already made
        if (showPrimaryBeam) {
            List<ShrimpFractionExpressionInterface> spots = squidProject.getTask().getShrimpFractions();
            List<Double> primaryBeam = new ArrayList<>();
            for (int i = 0; i < spots.size(); i++) {
                for (int j = 0; j < 6; j++) {
                    primaryBeam.add(spots.get(i).getPrimaryBeam());
                }
            }

            AbstractDataView canvas
                    = new MassStationAuditViewForShrimp(
                            new Rectangle(25, (massCounter * heightOfMassPlot) + 25, widthOfView, heightOfMassPlot),
                            "Primary Beam",
                            primaryBeam,
                            allMassStations.get(0).getTimesOfMeasuredTrimMasses(),
                            allMassStations.get(0).getIndicesOfScansAtMeasurementTimes(),
                            allMassStations.get(0).getIndicesOfRunsAtMeasurementTimes(),
                            showTimeNormalized);

            scrolledBox.getChildren().add(canvas);
            GraphicsContext gc1 = canvas.getGraphicsContext2D();
            canvas.preparePanel();
            canvas.paint(gc1);

            massCounter++;
        }
    }
}
