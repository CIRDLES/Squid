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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.cirdles.squid.gui.squidReportTable.utilities.CSVExtractor;
import org.cirdles.squid.gui.squidReportTable.utilities.FileHandler;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * FXML Controller class
 *
 * @author ryanb
 */
public class SquidReportTableGUIController implements Initializable {

    @FXML
    private Button selectCSVButton;
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
    @FXML
    private Label label;

    private String[][] textArray;
    private TextArrayManager tableManager;
    private ButtonTypes buttonState;

    private enum ButtonTypes {
        accepted, rejected
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        buttonState = ButtonTypes.accepted;
        boundCol.setFixedCellSize(24);
        reportsTable.setFixedCellSize(24);
        footnoteText.setEditable(false);
        setStyles();
    }

    @FXML
    private void selectCSVButton(ActionEvent event) {
        File fileName = FileHandler.getFile();
        if (fileName != null) {
            textArray = CSVExtractor.extractCSVFile(fileName);
            tableManager = new TextArrayManager(boundCol, reportsTable, textArray);
            tableManager.setHeaders();
            tableManager.setTableItems();
            setTableItems();
            FootnoteManager.setUpFootnotes(footnoteText, textArray);
            setUpColFootnote();
            setUpScroller();
            setUpColFootnote();
            reportsTable.refresh();
            boundCol.refresh();
        }
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

        label.setPrefHeight(24);                    
        label.setPrefWidth(160);
        rtHbar.setPrefHeight(24.0);
        rtHbar.visibleProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(final ObservableValue<? extends Boolean> observableValue, final Boolean oldVal, final Boolean newVal) {
                if (newVal) {
                    AnchorPane.setBottomAnchor(boundCol, 276.0);
                    AnchorPane.clearConstraints(label);
                    AnchorPane.setBottomAnchor(label, 252.0);
                    AnchorPane.setLeftAnchor(label, 14.0);

                } else {
                    AnchorPane.setBottomAnchor(boundCol, 252.0);
                    AnchorPane.setBottomAnchor(label, 225.0);
                    AnchorPane.setRightAnchor(label, 14.0);
                    AnchorPane.setLeftAnchor(label, 14.0);
                }
            }
        });
    }

    private void setStyles() {
        String tableStyle = ".table-view {"
                + "-fx-font-family: \"Courier New\"; -fx-font-size: 15;}";
        reportsTable.setStyle(tableStyle);
        boundCol.setStyle(tableStyle);
        fractionsButtons.setStyle("-fx-background-color: orange;"
                + "-fx-font-family: \"Times New Roman\";"
                + "-fx-font-size: 18;");
        selectCSVButton.setStyle("-fx-background-color: orange;"
                + "-fx-font-family: \"Times New Roman\";"
                + "-fx-font-size: 18;");
        root.setStyle("-fx-background-color: cadetblue");
    }

    private void setUpColFootnote() {
        int spot = Integer.parseInt(textArray[0][0]);
        label.setText(textArray[spot][1].trim());
        label.setStyle("-fx-font-size:17;-fx-background-color:orange;");
    }
}
