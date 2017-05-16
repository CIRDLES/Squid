/*
 * Copyright 2017 CIRDLES.org.
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

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javax.xml.bind.JAXBException;
import org.cirdles.calamari.prawn.PrawnFile.Run;
import org.cirdles.squid.gui.RunsViewModel.ShrimpFractionListCell;
import org.cirdles.squid.gui.RunsViewModel.SpotNameMatcher;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.utilities.SquidPrefixTree;
import org.xml.sax.SAXException;

/**
 * FXML Controller class
 *
 * @see
 * <a href="https://courses.bekwam.net/public_tutorials/bkcourse_filterlistapp.html" target="_blank">Bekwam.net</a>
 * @author James F. Bowring
 */
public class ProjectManagerController implements Initializable {

    private ObservableList<Run> shrimpRuns;
    private ObservableList<Run> shrimpRunsRefMat;
    private final RunsViewModel runsModel = new RunsViewModel();

    @FXML
    private TextField orignalPrawnFileName;
    @FXML
    private ListView<Run> shrimpFractionList;
    @FXML
    private ListView<Run> shrimpRefMatList;
    @FXML
    private TextField selectedSpotNameText;
    @FXML
    private Label softwareVersionLabel;
    @FXML
    private Label headerLabel;
    @FXML
    private TextField filterSpotName;
    @FXML
    private Label spotsShownLabel;
    @FXML
    private Label headerLabelRefMat;
    private static final String SPOT_LIST_CSS_STYLE_SPECS = "-fx-font-size: 12px; -fx-font-weight: bold; -fx-font-family: 'Courier New';";
    @FXML
    private Button saveSpotNameButton;
    @FXML
    private Button savePrawnFileButton;
    @FXML
    private Button setFilteredSpotsAsRefMatButton;
    @FXML
    private TreeView<String> prawnAuditTree;

    @FXML
    private TabPane prawnFileTabPane;
    @FXML
    private Label summaryStatsLabel;
    @FXML
    private Label totalAnalysisTimeLabel;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        orignalPrawnFileName.setEditable(false);
        setUpShrimpFractionListHeaders();
        savePrawnFileButton.setDisable(true);
        saveSpotNameButton.setDisable(true);
        setFilteredSpotsAsRefMatButton.setDisable(true);

        prawnFileTabPane.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

    }

    @FXML
    private void selectPrawnFileAction(ActionEvent event) {

        try {
            if (squidProject.selectPrawnFile(primaryStageWindow)) {

                orignalPrawnFileName.setText(squidProject.getPrawnXMLFileName());

                softwareVersionLabel.setText(
                        "Software Version: "
                        + squidProject.getPrawnFileShrimpSoftwareVersionName());

                shrimpRuns = squidProject.getListOfPrawnFileRuns();
                shrimpRunsRefMat = FXCollections.observableArrayList();

                setUpShrimpFractionList();

                savePrawnFileButton.setDisable(false);
                saveSpotNameButton.setDisable(false);
                setFilteredSpotsAsRefMatButton.setDisable(false);

                setUpPrawnFileAuditTreeView();
            }

        } catch (IOException | JAXBException | SAXException iOException) {
        }
    }

    private void setUpPrawnFileAuditTreeView() {
        prawnAuditTree.setStyle(SPOT_LIST_CSS_STYLE_SPECS);

        TreeItem<String> rootItem = new TreeItem<>("Spots", null);
        rootItem.setExpanded(true);
        prawnAuditTree.setRoot(rootItem);

        SquidPrefixTree spotPrefixTree = squidProject.generatePrefixTreeFromSpotNames();

        String summaryStatsString = buildSummaryDataString(spotPrefixTree);
        rootItem.setValue("Spots by prefix:" + summaryStatsString);

        // format into rows for summary tab
        summaryStatsLabel.setText("Session summary:\n\t" + summaryStatsString.replaceAll(";", "\n\t"));

        totalAnalysisTimeLabel.setText("Total session time in hours = " + (int) squidProject.getSessionDurationHours());

        populateTreeView(rootItem, spotPrefixTree);
    }

    private void populateTreeView(TreeItem<String> parentItem, SquidPrefixTree squidPrefixTree) {

        List<SquidPrefixTree> children = squidPrefixTree.getChildren();

        for (int i = 0; i < children.size(); i++) {
            if (!children.get(i).isleaf()) {
                TreeItem<String> item
                        = new TreeItem<>(children.get(i).getStringValue()
                                + buildSummaryDataString(children.get(i))
                        );

                parentItem.getChildren().add(item);

                if (children.get(i).hasChildren()) {
                    populateTreeView(item, children.get(i));
                }

            } else {
                parentItem.setValue(children.get(i).getParent().getStringValue()
                        + " Dups=" + String.format("%1$ 2d", children.get(i).getParent().getCountOfDups())
                        + " Species=" + String.format("%1$ 2d", children.get(i).getCountOfSpecies())
                        + " Scans=" + String.format("%1$ 2d", children.get(i).getCountOfScans())
                        + ((String) (children.size() > 1 ? " ** see duplicates below **" : ""))
                );
            }
        }
    }

    private String buildSummaryDataString(SquidPrefixTree tree) {
        // build species and scans count string
        String speciesCounts = "";
        for (Integer count : tree.getMapOfSpeciesFrequencies().keySet()) {
            speciesCounts += "[" + String.format("%1$ 2d", count) + " in " + String.format("%1$ 3d", tree.getMapOfSpeciesFrequencies().get(count)) + "]";
        }

        String scansCounts = "";
        for (Integer count : tree.getMapOfScansFrequencies().keySet()) {
            scansCounts += "[" + String.format("%1$ 2d", count) + " in " + String.format("%1$ 3d", tree.getMapOfScansFrequencies().get(count)) + "]";
        }

        String summary = " Analyses=" + String.format("%1$ 3d", tree.getCountOfLeaves())
                + "; Dups=" + String.format("%1$ 3d", tree.getCountOfDups())
                + "; Species:" + speciesCounts
                + "; Scans:" + scansCounts;

        return summary;
    }

    private void setUpShrimpFractionListHeaders() {

        headerLabel.setStyle(SPOT_LIST_CSS_STYLE_SPECS);
        headerLabel.setText(
                String.format("%1$-" + 20 + "s", "Spot Name")
                + String.format("%1$-" + 12 + "s", "Date")
                + String.format("%1$-" + 12 + "s", "Time")
                + String.format("%1$-" + 6 + "s", "Peaks")
                + String.format("%1$-" + 6 + "s", "Scans"));

        headerLabelRefMat.setStyle(SPOT_LIST_CSS_STYLE_SPECS);
        headerLabelRefMat.setText(
                String.format("%1$-" + 20 + "s", "Ref Mat Name")
                + String.format("%1$-" + 12 + "s", "Date")
                + String.format("%1$-" + 12 + "s", "Time")
                + String.format("%1$-" + 6 + "s", "Peaks")
                + String.format("%1$-" + 6 + "s", "Scans"));
    }

    private void setUpShrimpFractionList() {

        shrimpFractionList.setStyle(SPOT_LIST_CSS_STYLE_SPECS);

        shrimpFractionList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Run>() {
            @Override
            public void changed(ObservableValue<? extends Run> observable, Run oldValue, Run newValue) {
                try {
                    selectedSpotNameText.setText(newValue.getPar().get(0).getValue());
                    saveSpotNameButton.setUserData(newValue);
                } catch (Exception e) {
                    selectedSpotNameText.setText("");
                }
            }
        });

        shrimpFractionList.setCellFactory(
                (lv)
                -> new ShrimpFractionListCell()
        );

        runsModel.addRunsList(shrimpRuns);
        spotsShownLabel.setText(runsModel.showFilteredOverAllCount());

        shrimpFractionList.itemsProperty().bind(runsModel.viewableShrimpRunsProperty());

        shrimpFractionList.setContextMenu(createAllSpotsViewContextMenu());

        // display of selected reference materials
        shrimpRefMatList.setStyle(SPOT_LIST_CSS_STYLE_SPECS);

        shrimpRefMatList.setCellFactory(
                (lv)
                -> new ShrimpFractionListCell()
        );

        shrimpRefMatList.setContextMenu(createRefMatSpotsViewContextMenu());

    }

    private ContextMenu createAllSpotsViewContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Remove this spot.");
        menuItem.setOnAction((evt) -> {
            Run selectedRun = shrimpFractionList.getSelectionModel().getSelectedItem();
            if (selectedRun != null) {
                runsModel.remove(selectedRun);
                shrimpRunsRefMat.remove(selectedRun);
                squidProject.removeRunFromPrawnFile(selectedRun);
                spotsShownLabel.setText(runsModel.showFilteredOverAllCount());
            }
        });
        contextMenu.getItems().add(menuItem);
        return contextMenu;
    }

    private ContextMenu createRefMatSpotsViewContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Clear list.");
        menuItem.setOnAction((evt) -> {
            shrimpRunsRefMat.clear();
            shrimpRefMatList.setItems(shrimpRunsRefMat);
        });
        contextMenu.getItems().add(menuItem);
        return contextMenu;
    }

    @FXML
    private void filterSpotNameKeyReleased(KeyEvent event) {
        String filterString = filterSpotName.getText().toUpperCase(Locale.US).trim();
        Predicate<Run> filter = new SpotNameMatcher(filterString);
        runsModel.filterProperty().set(filter);
        spotsShownLabel.setText(runsModel.showFilteredOverAllCount());

        squidProject.setFilterForRefMatSpotNames(filterString);
    }

    @FXML
    private void saveSpotNameAction(ActionEvent event) {
        if (saveSpotNameButton.getUserData() != null) {
            ((Run) saveSpotNameButton.getUserData()).getPar().get(0).setValue(selectedSpotNameText.getText().trim().toUpperCase(Locale.US));
            shrimpFractionList.refresh();
            shrimpRefMatList.refresh();
            setUpPrawnFileAuditTreeView();
        }
    }

    @FXML
    private void setFilteredSpotsToRefMatAction(ActionEvent event) {
        shrimpRunsRefMat = runsModel.getViewableShrimpRuns();
        shrimpRefMatList.setItems(shrimpRunsRefMat);
    }

    @FXML
    private void savePrawnFileAction(ActionEvent event) {
        try {
            squidProject.savePrawnFile(primaryStageWindow);
            orignalPrawnFileName.setText(SquidUIController.squidProject.getPrawnXMLFileName());
            shrimpFractionList.refresh();
            shrimpRefMatList.refresh();
            setUpPrawnFileAuditTreeView();
        } catch (IOException | JAXBException | SAXException iOException) {
        }
    }
}
