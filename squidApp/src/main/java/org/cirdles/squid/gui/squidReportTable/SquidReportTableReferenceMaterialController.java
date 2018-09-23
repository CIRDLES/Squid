/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.squidReportTable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.cirdles.squid.reports.reportSettings.ReportSettings;
import org.cirdles.squid.reports.reportSettings.ReportSettingsInterface;
import org.cirdles.squid.shrimp.ShrimpFraction;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;

import java.net.URL;
import java.util.*;

import static org.cirdles.squid.gui.SquidUIController.squidProject;

/**
 * FXML Controller class
 *
 * @author ryanb
 */
public class SquidReportTableReferenceMaterialController implements Initializable {

    @FXML
    private TableView<ObservableList<String>> reportsTable;
    @FXML
    private TableView<ObservableList<String>> boundCol;
    @FXML
    private AnchorPane root;

    private String[][] textArray;
    private TextArrayManager tableManager;
    private boolean isSetUpScroller;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isSetUpScroller = false;
        boundCol.setFixedCellSize(24);
        reportsTable.setFixedCellSize(24);

        ReportSettingsInterface reportSettings = new ReportSettings("TEST", true, squidProject.getTask());
        Map<String, List<ShrimpFractionExpressionInterface>> mapOfSpotsBySampleNames = squidProject.getTask().getMapOfUnknownsBySampleNames();
        List<ShrimpFractionExpressionInterface> spotsBySampleNames = new ArrayList<>();
        for (Map.Entry<String, List<ShrimpFractionExpressionInterface>> entry : mapOfSpotsBySampleNames.entrySet()) {
            ShrimpFractionExpressionInterface dummyForSample = new ShrimpFraction(entry.getKey(), new TreeSet<>());
            spotsBySampleNames.add(dummyForSample);
            spotsBySampleNames.addAll(entry.getValue());
        }

        textArray = reportSettings.reportFractionsByNumberStyle(spotsBySampleNames, true);

        tableManager = new TextArrayManager(boundCol, reportsTable, textArray);
        reportsTable.refresh();
        boundCol.refresh();

        EventHandler<MouseEvent> scrollHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!isSetUpScroller) {
                    setUpScroller();
                    isSetUpScroller = true;
                }
            }
        };
        reportsTable.setOnMouseEntered(scrollHandler);
        boundCol.setOnMouseEntered(scrollHandler);
    }

    private void setUpScroller() {
        ScrollBar rTBar = (ScrollBar) reportsTable.lookup(".scroll-bar:vertical");
        ScrollBar bCBar = (ScrollBar) boundCol.lookup(".scroll-bar:vertical");
        rTBar.valueProperty().bindBidirectional(bCBar.valueProperty());

        ScrollBar rtHbar = null;
        Set<Node> bars = reportsTable.lookupAll(".scroll-bar");
        Iterator<Node> iterator = bars.iterator();
        while (rtHbar == null && iterator.hasNext()) {
            ScrollBar curr = (ScrollBar) iterator.next();
            if (curr.getOrientation() == Orientation.HORIZONTAL) {
                rtHbar = curr;
            }
        }

        rtHbar.setPrefHeight(24.0);
        rtHbar.visibleProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(final ObservableValue<? extends Boolean> observableValue, final Boolean oldVal, final Boolean newVal) {
                if (newVal) {
                    AnchorPane.setBottomAnchor(boundCol, 196.0);
                } else {
                    AnchorPane.setBottomAnchor(boundCol, 172.0);
                }
            }
        });
        if (rtHbar.isVisible()) {
            AnchorPane.setBottomAnchor(boundCol, 196.0);
        } else {
            AnchorPane.setBottomAnchor(boundCol, 172.0);
        }
    }
}
