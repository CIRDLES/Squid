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
import javafx.scene.canvas.GraphicsContext;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import static org.cirdles.squid.gui.SquidUI.SQUID_LOGO_SANS_TEXT_URL;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.gui.dataViews.AbstractDataView;
import org.cirdles.squid.gui.dataViews.MassStationAuditViewForShrimp;
import org.cirdles.squid.shrimp.MassStationDetail;
import org.cirdles.squid.shrimp.SquidSpeciesModel;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class MassesAuditController implements Initializable {

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

    private static ObservableList<MassStationDetail> allMassStations;
    private static ObservableList<MassStationDetail> availableMassStations;
    private static ObservableList<MassStationDetail> viewedAsGraphMassStations;
    private static DataFormat massDetailFormat = new DataFormat("Mass station detail");

    private static boolean showTimeNormalized;
    @FXML
    private ComboBox<MassStationDetail> massMinuendComboBox;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        setupMassStationDetailsListViews();
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

        allMassStations = FXCollections.observableArrayList(allMassStationDetails);
        massMinuendComboBox.setItems(allMassStations);
        massMinuendComboBox.setCellFactory(
                (parameter)
                -> new MassStationDetailListCell()
        );
        massMinuendComboBox.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);


    }

    @FXML
    private void normalizeTimeAxisCheckBoxAction(ActionEvent event) {
        displayMassStationsForReview();
    }

    static class MassStationDetailListCell extends ListCell<MassStationDetail> {

        @Override
        protected void updateItem(MassStationDetail msd, boolean empty) {
            super.updateItem(msd, empty);
            if (msd == null || empty) {
                setText(null);
            } else {
                setText(
                        String.format("%1$-" + 8 + "s", msd.getMassStationLabel())
                        + String.format("%1$-" + 7 + "s", msd.getIsotopeLabel())
                        + String.format("%1$-" + 13 + "s", (msd.autoCentered() ? "auto-centered" : "")));
            }
        }
    };

    class onDragDetectedEventHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            /* drag was detected, start a drag-and-drop gesture */
            Dragboard db = ((ListView) event.getSource()).startDragAndDrop(TransferMode.COPY_OR_MOVE);
            ClipboardContent content = new ClipboardContent();
            content.put(massDetailFormat, ((ListView) event.getSource()).getSelectionModel().getSelectedItem());
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
                    && event.getDragboard().hasContent(massDetailFormat)) {

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
            if (db.hasContent(massDetailFormat)) {
                myListView.getItems().add((MassStationDetail) db.getContent(massDetailFormat));
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
                MassStationDetail massStationDetail = (MassStationDetail) event.getDragboard().getContent(massDetailFormat);
                myListView.getItems().remove(massStationDetail);
                massStationDetail.setViewedAsGraph(!myListView.idProperty().getValue().contains("viewedAsGraph"));

                SquidSpeciesModel ssm = squidProject.getTask().getSquidSpeciesModelList().get(massStationDetail.getMassStationIndex());
                ssm.setViewedAsGraph(massStationDetail.isViewedAsGraph());

                displayMassStationsForReview();
            }
            event.consume();
        }
    };

    private void displayMassStationsForReview() {

        showTimeNormalized = normalizeTimeAxisCheckBox.isSelected();

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
        MassStationDetail A = viewedAsGraphMassStations.get(0);
        MassStationDetail B = viewedAsGraphMassStations.get(1);
        List<Double> deltas = new ArrayList<>();
        for (int i = 0; i < A.getMeasuredTrimMasses().size(); i++) {
            BigDecimal aBD = new BigDecimal(A.getMeasuredTrimMasses().get(i));
            BigDecimal bBD = new BigDecimal(B.getMeasuredTrimMasses().get(i));
            deltas.add(aBD.subtract(bBD).doubleValue());
        }

        AbstractDataView canvas
                = new MassStationAuditViewForShrimp(new Rectangle(25, (massCounter * heightOfMassPlot) + 25, widthOfView, heightOfMassPlot),
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
}
