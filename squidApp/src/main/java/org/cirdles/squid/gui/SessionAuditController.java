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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.gui.plots.PlotsController;
import org.cirdles.squid.utilities.squidPrefixTree.SquidPrefixTree;

/**
 * FXML Controller class
 *
 * This class displays the spot tree with statistics. If there are duplicate
 * spot names the user has the option to display only those trees containing
 * duplicates.
 *
 * @author James F. Bowring
 */
public class SessionAuditController implements Initializable {

    @FXML
    private TreeView<SampleNamePrefixInterface> prawnAuditTree;
    @FXML
    private VBox sessionVBox;
    @FXML
    private CheckBox checkbox;

    private static char[] spaces = new char[100];

    static {
        for (int i = 0; i < 100; i++) {
            spaces[i] = ' ';
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        prawnAuditTree.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        prawnAuditTree.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(PIXEL_OFFSET_FOR_MENU));

        setUpPrawnFileAuditTreeView(false);
    }

    /**
     * Prepares the TreeView to be displayed
     *
     * @param showDupesOnly determines whether the duplicate CheckBox has been
     * checked
     */
    private void setUpPrawnFileAuditTreeView(boolean showDupesOnly) {
        prawnAuditTree.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);

//        prawnAuditTree.setCellFactory(new Callback<TreeView<SampleNamePrefixInterface>, TreeCell<SampleNamePrefixInterface>>() {
//            @Override
//            public TreeCell<SampleNamePrefixInterface> call(TreeView<SampleNamePrefixInterface> p) {
//                return new SampleNamePrefixNode("XXX" + p.toString());
//            }
//        });
//
//        prawnAuditTree.setCellFactory(p -> new TreeCell<>(
//                (TreeItem<SampleNamePrefixInterface> item) -> ((PlotsController.ConcordiaFractionNode) item.getValue()).getSelectedProperty(),
//                new StringConverter<TreeItem<PlotsController.SampleNamePrefixInterface>>() {
//
//            @Override
//            public String toString(TreeItem<SampleNamePrefixInterface> object) {
//                PlotsController.SampleNamePrefixInterface item = object.getValue();
//                return item.getNodeName();
//            }
//
//            @Override
//            public TreeItem<PlotsController.SampleNamePrefixInterface> fromString(String string) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//        }));

        SquidPrefixTree spotPrefixTree = squidProject.getPrefixTree();
        String summaryStatsString = spotPrefixTree.buildSummaryDataString();

        boolean hasDuplicates = spotPrefixTree.getCountOfDups() > 0;
        checkbox.setVisible(hasDuplicates);
        TreeItem<SampleNamePrefixInterface> rootItem = customizeRootItem(summaryStatsString, hasDuplicates);
        rootItem.setExpanded(true);
        prawnAuditTree.setRoot(rootItem);

        populatePrefixTreeView(rootItem, spotPrefixTree, showDupesOnly, 0, true);
    }

    /**
     * Initializes root item of prawnAuditTree TreeView based on whether the
     * SquidPrefixTree has duplicates
     *
     * @param hasDuplicates the SquidPrefixTree has duplicates
     * @param summaryStatsString summary of statistics from SquidPrefixTree to
     * display
     * @return the root item depending on whether there are duplicates
     */
    private TreeItem<SampleNamePrefixInterface> customizeRootItem(String summaryStatsString, boolean hasDuplicates) {
        TreeItem<SampleNamePrefixInterface> rootItem = new TreeItem<>(new SampleNamePrefixNode("Spots"), null);
        if (hasDuplicates) {
            rootItem.setValue(
                    new SampleNamePrefixNode("***This file has duplicate names. Change names of duplicates in PrawnFile > Manage Spots & "
                            + "Choose Reference Materials***"
                            + "\n\nSpots by prefix: " + summaryStatsString));
        } else {
            rootItem.setValue(new SampleNamePrefixNode("Spots by prefix:" + summaryStatsString));
        }
        return rootItem;
    }

    /**
     * Displays all nodes in the tree starting from the root. Displays only
     * duplicate spot tree if showDupesOnly is true. Displays entire spot tree
     * if hasBeenChecked is false.
     *
     * @param parentItem the root item for this tree
     * @param squidPrefixTree the current instance of SquidPrefixTree
     * @param showDupesOnly implies both that SquidPrefixTree has duplicates,
     * and that the CheckBox has been checked
     * @param depth the value of depth
     * @param doExpand the value of doExpand
     */
    private void populatePrefixTreeView(TreeItem<SampleNamePrefixInterface> parentItem, SquidPrefixTree squidPrefixTree, boolean showDupesOnly, int depth, boolean doExpand) {

        List<SquidPrefixTree> children = squidPrefixTree.getChildren();

        boolean endExpansion = false;
        if (children.size() > 0) {
            endExpansion = children.get(0).getNode().getValue().compareTo("-") != 0;
        }

        parentItem.setExpanded(endExpansion && doExpand);
        parentItem.setValue(new SampleNamePrefixNode(
                parentItem.getValue().getContents()
                + String.copyValueOf(spaces, 0, 100 - parentItem.getValue().lengthOfContents() - depth)
                + ((String) (!(endExpansion && doExpand) && parentItem.getParent().isExpanded() ? "SAMPLE" : ""))));

        for (int i = 0; i < children.size(); i++) {
            if (!children.get(i).isleaf()) {
                TreeItem<SampleNamePrefixInterface> childItem
                        = new TreeItem<>(new SampleNamePrefixNode(
                                children.get(i).getStringValue()
                                + children.get(i).buildSummaryDataString()));
                if (showDupesOnly) {
                    if (children.get(i).getCountOfDups() > 0) {
                        parentItem.getChildren().add(childItem);
                    }
                } else {
                    parentItem.getChildren().add(childItem);
                }

                if (children.get(i).hasChildren()) {
                    populatePrefixTreeView(childItem, children.get(i), showDupesOnly, depth + 1, endExpansion && doExpand);
                }

            } else {
                parentItem.setValue(new SampleNamePrefixNode(children.get(i).getParent().getStringValue()
                        + " Dups=" + String.format("%1$ 2d", children.get(i).getParent().getCountOfDups())
                        + " Species=" + String.format("%1$ 2d", children.get(i).getCountOfSpecies())
                        + " Scans=" + String.format("%1$ 2d", children.get(i).getCountOfScans())
                        + ((String) (children.size() > 1 ? " ** see duplicates below **" : ""))
                ));
            }
        }
    }

    @FXML
    /**
     * Calls method displaying appropriate tree dependent on the state of the
     * CheckBox
     */
    private void duplicatesChecked(ActionEvent event) {
        setUpPrawnFileAuditTreeView(checkbox.isSelected());
    }

    private interface SampleNamePrefixInterface {

        public int lengthOfContents();

        public String getContents();
    }

    private class SampleNamePrefixNode extends TreeCell<SampleNamePrefixInterface> implements SampleNamePrefixInterface {

        private String contents;

        public SampleNamePrefixNode(String contents) {
            this.contents = contents;
        }

        @Override
        public String getContents() {
            return contents;
        }

        @Override
        public int lengthOfContents() {
            return contents.length();
        }

    }
}
