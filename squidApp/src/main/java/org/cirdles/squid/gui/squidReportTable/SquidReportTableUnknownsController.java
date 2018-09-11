/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.squidReportTable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import org.cirdles.squid.reports.reportSettings.ReportSettings;
import org.cirdles.squid.reports.reportSettings.ReportSettingsInterface;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import static org.cirdles.squid.gui.SquidUIController.squidProject;

/**
 * FXML Controller class
 *
 * @author ryanb
 */
public class SquidReportTableUnknownsController implements Initializable {

    @FXML
    private TableView<ObservableList<String>> reportsTable;
    @FXML
    private TableView<ObservableList<String>> boundCol;
    @FXML
    private TextArea footnoteText;
    @FXML
    private Button fractionsButtons;
    @FXML
    private AnchorPane root;

    private TextArrayManager tableManager;
    private ButtonTypes buttonState;
    private String[][] textArray;
    private boolean isSetUpScroller;

    private enum ButtonTypes {
        accepted, rejected
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isSetUpScroller = false;
        buttonState = ButtonTypes.accepted;
        boundCol.setFixedCellSize(24);
        reportsTable.setFixedCellSize(24);
        footnoteText.setEditable(false);
        ReportSettingsInterface reportSettings = new ReportSettings("TEST", false, squidProject.getTask());
        textArray = reportSettings.reportFractionsByNumberStyle(squidProject.getTask().getUnknownSpots(), true);
        tableManager = new TextArrayManager(boundCol, reportsTable, textArray);
        tableManager.setHeaders();
        tableManager.setTableItems();
        setTableItems();
        FootnoteManager.setUpFootnotes(footnoteText, textArray);
        reportsTable.refresh();
        boundCol.refresh();

        EventHandler<MouseEvent> scrollHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!isSetUpScroller) {
                    setUpScroller();
                    isSetUpScroller = true;
                }
            }
        };
        reportsTable.setOnMouseEntered(scrollHandler);
        boundCol.setOnMouseEntered(scrollHandler);
    }

    @FXML
    private void acceptedRejectedAction(ActionEvent event) {
        if (buttonState.equals(ButtonTypes.accepted)) {
            buttonState = ButtonTypes.rejected;
            fractionsButtons.setText("Rejected");
        } else {
            buttonState = ButtonTypes.accepted;
            fractionsButtons.setText("Accepted");
        }
        setTableItems();
    }

    private void setTableItems() {
        if (buttonState.equals(ButtonTypes.accepted)) {
            tableManager.setAccepted();
        } else {
            tableManager.setRejected();
        }
        reportsTable.refresh();
        boundCol.refresh();
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
        if(rtHbar.isVisible()) {
            AnchorPane.setBottomAnchor(boundCol, 196.0);
        } else {
            AnchorPane.setBottomAnchor(boundCol, 172.0);
        }
    }
}
