/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.squidReportTable;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTableInterface;

import java.net.URL;
import java.util.*;
import static org.cirdles.squid.gui.squidReportTable.TextArrayManager.textArrayManagerInitialize;

/**
 * FXML Controller class
 *
 * @author ryanb
 */
public class SquidReportTableController implements Initializable {

    @FXML
    private TableView<ObservableList<String>> reportsTable;
    @FXML
    private TableView<ObservableList<String>> boundCol;
    @FXML
    private AnchorPane root;
    @FXML
    private ScrollBar scrollBar;

    private String[][] reportTextArray;

    private boolean isSetUpScroller;

    public static SquidReportTableLauncher.ReportTableTab typeOfController;
    public static SquidReportTableInterface squidReportTable;
    public static String unknownSpot = "UNKNOWNS";

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isSetUpScroller = false;
        boundCol.setFixedCellSize(24);
        reportsTable.setFixedCellSize(24);

        reportTextArray = SquidReportTableHelperMethods.processReportTextArray(typeOfController, squidReportTable, unknownSpot);
        textArrayManagerInitialize(boundCol, reportsTable, reportTextArray, typeOfController);

        EventHandler<MouseEvent> scrollHandler = event -> {
            if (!isSetUpScroller) {
                setUpScroller();
                isSetUpScroller = true;
            }
        };
        reportsTable.setOnMouseEntered(scrollHandler);
        boundCol.setOnMouseEntered(scrollHandler);
        scrollBar.setOnMouseEntered(scrollHandler);
    }

    private void setUpScroller() {
        ScrollBar rTBar = (ScrollBar) reportsTable.lookup(".scroll-bar:vertical");
        ScrollBar bCBar = (ScrollBar) boundCol.lookup(".scroll-bar:vertical");
        rTBar.valueProperty().bindBidirectional(bCBar.valueProperty());

        scrollBar.setMax(rTBar.getMax());
        scrollBar.setMin(rTBar.getMin());
        scrollBar.unitIncrementProperty().bindBidirectional(rTBar.unitIncrementProperty());
        scrollBar.blockIncrementProperty().bindBidirectional(rTBar.blockIncrementProperty());
        scrollBar.visibleProperty().bindBidirectional(bCBar.visibleProperty());
        scrollBar.visibleAmountProperty().bindBidirectional(rTBar.visibleAmountProperty());
        scrollBar.setOrientation(Orientation.VERTICAL);
        scrollBar.valueProperty().bindBidirectional(rTBar.valueProperty());

        ScrollBar rtHbar = null;
        Set<Node> bars = reportsTable.lookupAll(".scroll-bar");
        Iterator<Node> iterator = bars.iterator();
        while (rtHbar == null && iterator.hasNext()) {
            ScrollBar curr = (ScrollBar) iterator.next();
            if (curr.getOrientation() == Orientation.HORIZONTAL) {
                rtHbar = curr;
            }
        }
        if (rtHbar != null) {
            rtHbar.setPrefHeight(24.0);
            rtHbar.visibleProperty().addListener((obVal, oldVal, newVal) -> {
                if (newVal) {
                    AnchorPane.setBottomAnchor(boundCol, 29.0);
                } else {
                    AnchorPane.setBottomAnchor(boundCol, 5.0);
                }
            });

            //surprisingly not redundant, above won't be triggered until the visible property changes
            if (rtHbar.isVisible()) {
                AnchorPane.setBottomAnchor(boundCol, 29.0);
            } else {
                AnchorPane.setBottomAnchor(boundCol, 5.0);
            }
        }
    }

}
