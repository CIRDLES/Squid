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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.cirdles.squid.constants.Squid3Constants.SampleNameDelimitersEnum;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.gui.constants.Squid3GuiConstants.STYLE_MANAGER_TITLE;
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
    private VBox sessionVBox;
    @FXML
    private CheckBox checkbox;

    private static ObservableList<SampleNameTreeNodeInterface> nameNodes;
    @FXML
    private TreeView<SampleNameTreeNodeInterface> prawnAuditTreeCheckBox;

    private static Map<String, Integer> workingListOfSelectedNames = new HashMap<>();
    @FXML
    private Label summaryLabel;

    private boolean hasDuplicates;

    @FXML
    private Label titleLabel;
    @FXML
    private ComboBox<String> delimiterComboBox;

    public SessionAuditController() {
    }
    private String sampleNameDelimiter;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sampleNameDelimiter = squidProject.getDelimiterForUnknownNames();

        prawnAuditTreeCheckBox.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        prawnAuditTreeCheckBox.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(PIXEL_OFFSET_FOR_MENU));
        setUpPrawnAuditTreeView(false);

        ObservableList<String> delimitersList = FXCollections.observableArrayList(SampleNameDelimitersEnum.names());
        delimiterComboBox.setItems(delimitersList);
        // set value before adding listener
        delimiterComboBox.getSelectionModel().select(sampleNameDelimiter);

        delimiterComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> ov,
                    final String oldvalue, final String newvalue) {
                sampleNameDelimiter = newvalue.trim();
                squidProject.updateFiltersForUnknownNames(new HashMap<>());
                squidProject.setDelimiterForUnknownNames(newvalue);
                squidProject.getTask().setChanged(true);
                setUpPrawnAuditTreeView(false);
                refreshView();
            }
        });

        titleLabel.setStyle(STYLE_MANAGER_TITLE);

    }

    /**
     * Prepares the TreeView to be displayed
     *
     * @param showDupesOnly determines whether the duplicate CheckBox has been
     * checked
     */
    private void setUpPrawnAuditTreeView(boolean showDupesOnly) {
        prawnAuditTreeCheckBox.setRoot(null);
        prawnAuditTreeCheckBox.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);

        SquidPrefixTree spotPrefixTree = squidProject.getPrefixTree();
        String summaryStatsString = spotPrefixTree.buildSummaryDataString();

        hasDuplicates = spotPrefixTree.getCountOfDups() > 0;
        checkbox.setVisible(hasDuplicates);

        CheckBoxTreeItem<SampleNameTreeNodeInterface> rootItem
                = customizeRootItem(spotPrefixTree.getCountOfLeaves(), summaryStatsString, hasDuplicates);
        prawnAuditTreeCheckBox.setRoot(rootItem);

        prawnAuditTreeCheckBox.setCellFactory(p -> new CheckBoxTreeCell<SampleNameTreeNodeInterface>(
                (TreeItem<SampleNameTreeNodeInterface> item) -> ((SampleNameNode) item.getValue()).getSelectedProperty(),
                new StringConverter<TreeItem<SampleNameTreeNodeInterface>>() {

            @Override
            public String toString(TreeItem<SampleNameTreeNodeInterface> sampleNameTreeNode) {
                SampleNameTreeNodeInterface item = sampleNameTreeNode.getValue();
                String retVal = "";
                String referenceMaterialFilter = squidProject.getFilterForRefMatSpotNames();
                String concentrationReferenceMaterialFilter = squidProject.getFilterForConcRefMatSpotNames();
                if (item.getSampleName().compareTo(referenceMaterialFilter) == 0) {
                    ((CheckBoxTreeItem<SampleNameTreeNodeInterface>) sampleNameTreeNode)
                            .setSelected(!sampleNameTreeNode.getValue().isLockedForRefMat());
                    lockParentsForRefMat((CheckBoxTreeItem<SampleNameTreeNodeInterface>) sampleNameTreeNode);
                    retVal = item.getSampleContents()
                            + "    REFERENCE MATERIAL Name Selected = " + item.prettyPrintInfo();
                } else if (item.getSampleName().compareTo(concentrationReferenceMaterialFilter) == 0) {
                    ((CheckBoxTreeItem<SampleNameTreeNodeInterface>) sampleNameTreeNode)
                            .setSelected(!sampleNameTreeNode.getValue().isLockedForRefMat());
                    lockParentsForRefMat((CheckBoxTreeItem<SampleNameTreeNodeInterface>) sampleNameTreeNode);
                    retVal = item.getSampleContents()
                            + "    CONC REFERENCE MATERIAL Name Selected = " + item.prettyPrintInfo();
                } else {
                    retVal = item.getSampleContents()
                            + ((String) ((item.getSelectedProperty().get() && !item.isLockedForRefMat())
                            ? "    Sample Name Selected = " + item.prettyPrintInfo()
                            : ""));
                }
                return retVal;
            }

            @Override
            public TreeItem<SampleNameTreeNodeInterface> fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }));

        List<SampleNameTreeNodeInterface> sampleNameNodes = new ArrayList<>();
        workingListOfSelectedNames = squidProject.getFiltersForUnknownNames();

        populatePrefixTreeView(sampleNameNodes, rootItem, spotPrefixTree, showDupesOnly, 0, true);

        squidProject.updateFiltersForUnknownNames(workingListOfSelectedNames);
        nameNodes = FXCollections.observableArrayList(sampleNameNodes);
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
    private CheckBoxTreeItem<SampleNameTreeNodeInterface> customizeRootItem(
            int countOfLeaves, String summaryStatsString, boolean hasDuplicates) {
        CheckBoxTreeItem<SampleNameTreeNodeInterface> rootItem
                = new CheckBoxTreeItem<>();

        rootItem.setValue(new SampleNameNode("Spots by prefix:" + summaryStatsString, "ALL", countOfLeaves));
        rootItem.setExpanded(true);
        rootItem.setIndependent(true);
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
    private void populatePrefixTreeView(
            List<SampleNameTreeNodeInterface> sampleNameNodes,
            CheckBoxTreeItem<SampleNameTreeNodeInterface> parentItem,
            SquidPrefixTree squidPrefixTree,
            boolean showDupesOnly,
            int depth,
            boolean doExpand) {

        List<SquidPrefixTree> children = squidPrefixTree.getChildren();

        boolean continueExpansion = false;
        if (children.size() > 0) {
            if (squidProject.getFiltersForUnknownNames().isEmpty()) {
                int countOfLetters = 0;
                if (sampleNameDelimiter.matches("\\d")) {
                    countOfLetters = Integer.parseInt(sampleNameDelimiter);
                    continueExpansion = (children.get(0).getStringValue().length() <= countOfLetters);
                } else {
                    continueExpansion = (children.get(0).getNode().getValue().compareTo(sampleNameDelimiter) != 0);
                }
                for (int i = 0; i < children.size(); i++) {
                    if (squidProject.getFilterForRefMatSpotNames().startsWith(children.get(i).getStringValue())) {
                        continueExpansion = true;
                    }
                }
            } else {
                continueExpansion = !workingListOfSelectedNames.containsKey(squidPrefixTree.getStringValue());
            }
        }

        parentItem.setExpanded(continueExpansion && doExpand);

        boolean selectedAsSampleName = false;
        try {
            selectedAsSampleName = !(continueExpansion && doExpand) && parentItem.getParent().isExpanded();
        } catch (Exception e) {
        }

        SampleNameTreeNodeInterface parentNode = new SampleNameNode(
                parentItem.getValue().getSampleContents(),
                squidPrefixTree.getStringValue(),
                squidPrefixTree.getCountOfLeaves());

        sampleNameNodes.add(parentNode);
        parentItem.setValue(parentNode);
        parentItem.setSelected(selectedAsSampleName);

        for (int i = 0; i < children.size(); i++) {
            if (!children.get(i).isleaf()) {
                SampleNameTreeNodeInterface childNode = new SampleNameNode(
                        children.get(i).getStringValue()
                        + children.get(i).buildSummaryDataString(),
                        children.get(i).getStringValue(),
                        children.get(i).getCountOfLeaves());

                CheckBoxTreeItem<SampleNameTreeNodeInterface> childItem
                        = new CheckBoxTreeItem<>(childNode);
                childItem.setIndependent(true);
                childItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    ((SampleNameNode) childItem.getValue()).setSelectedProperty(new SimpleBooleanProperty(newValue));
                    workingListOfSelectedNames.remove(childItem.getValue().getSampleName());
                    if (newValue) {
                        workingListOfSelectedNames.put(childItem.getValue().getSampleName(), childNode.getCountOfIncludedSpots());
                        clearAllChildrenOf(childItem);
                        clearAllParentsOf(childItem);
                    }
                    refreshView();
                });

                sampleNameNodes.add(childNode);
                if (showDupesOnly) {
                    if (children.get(i).getCountOfDups() > 0) {
                        parentItem.getChildren().add(childItem);
                    }
                } else {
                    parentItem.getChildren().add(childItem);
                }

                if (children.get(i).hasChildren()) {
                    populatePrefixTreeView(sampleNameNodes, childItem, children.get(i), showDupesOnly, depth + 1, continueExpansion && doExpand);
                }

            } else {
                parentNode = new SampleNameNode(children.get(i).getParent().getStringValue()
                        + " Dups=" + String.format("%1$ 2d", children.get(i).getParent().getCountOfDups())
                        + " Species=" + String.format("%1$ 2d", children.get(i).getCountOfSpecies())
                        + " Scans=" + String.format("%1$ 2d", children.get(i).getCountOfScans())
                        + ((String) (children.size() > 1 ? " ** see duplicates below **" : "")),
                        children.get(i).getParent().getStringValue(),
                        children.get(i).getCountOfLeaves()
                );
                sampleNameNodes.add(parentNode);
                parentItem.setValue(parentNode);
            }
        }
    }

    private void clearAllChildrenOf(CheckBoxTreeItem<SampleNameTreeNodeInterface> childItem) {
        ObservableList<TreeItem<SampleNameTreeNodeInterface>> childChildren = childItem.getChildren();
        for (int j = 0; j < childChildren.size(); j++) {
            ((CheckBoxTreeItem<SampleNameTreeNodeInterface>) childChildren.get(j)).setSelected(false);
            workingListOfSelectedNames.remove(childChildren.get(j).getValue().getSampleName());
            clearAllChildrenOf(((CheckBoxTreeItem<SampleNameTreeNodeInterface>) childChildren.get(j)));
        }
    }

    private void clearAllParentsOf(CheckBoxTreeItem<SampleNameTreeNodeInterface> childItem) {
        CheckBoxTreeItem<SampleNameTreeNodeInterface> parent
                = ((CheckBoxTreeItem<SampleNameTreeNodeInterface>) ((CheckBoxTreeItem<SampleNameTreeNodeInterface>) childItem).getParent());
        if (parent != null) {
            parent.setSelected(false);
            workingListOfSelectedNames.remove(parent.getValue().getSampleName());
            clearAllParentsOf(parent);
        }
    }

    private void lockParentsForRefMat(CheckBoxTreeItem<SampleNameTreeNodeInterface> childItem) {
        CheckBoxTreeItem<SampleNameTreeNodeInterface> parent
                = ((CheckBoxTreeItem<SampleNameTreeNodeInterface>) ((CheckBoxTreeItem<SampleNameTreeNodeInterface>) childItem).getParent());
        if (parent != null) {
            parent.getValue().setLockedForRefMat(true);
            lockParentsForRefMat(parent);
        }
    }

    @FXML
    /**
     * Calls method displaying appropriate tree dependent on the state of the
     * CheckBox
     */
    private void duplicatesChecked(ActionEvent event) {
        //setUpPrawnFileAuditTreeView(checkbox.isSelected());
        setUpPrawnAuditTreeView(checkbox.isSelected());
    }

    private void refreshView() {
        prawnAuditTreeCheckBox.refresh();
        int totalCount = 0;
        for (String name : workingListOfSelectedNames.keySet()) {
            totalCount += workingListOfSelectedNames.get(name);
        }
        summaryLabel.setText(
                "  Sample count = "
                + workingListOfSelectedNames.size()
                + "  Covering "
                + totalCount
                + " of "
                + prawnAuditTreeCheckBox.getRoot().getValue().getCountOfIncludedSpots()
                + " spots."
                + (hasDuplicates ? "  Please remove duplicate spot names." : ""));

        squidProject.getTask().setChanged(true);
    }

    // classes to support tree with checkboxes
    private interface SampleNameTreeNodeInterface {

        public String getSampleContents();

        public int getContentsLength();

        public String getSampleName();

        public String prettyPrintInfo();

        public SimpleBooleanProperty getSelectedProperty();

        public boolean isLockedForRefMat();

        /**
         * @param lockedForRefMat the lockedForRefMat to set
         */
        public void setLockedForRefMat(boolean lockedForRefMat);

        /**
         * @return the countOfIncludedSpots
         */
        public int getCountOfIncludedSpots();

    }

    private class SampleNameNode implements SampleNameTreeNodeInterface {

        private final String sampleContents;
        private String sampleName;
        private SimpleBooleanProperty selectedProperty;
        private boolean lockedForRefMat;
        private int countOfIncludedSpots;

        public SampleNameNode(String sampleContents, String sampleName, int countOfIncludedSpots) {
            this.sampleContents = sampleContents;
            this.sampleName = sampleName;
            this.countOfIncludedSpots = countOfIncludedSpots;

            this.selectedProperty = new SimpleBooleanProperty(false);
            this.lockedForRefMat = false;
        }

        @Override
        public String prettyPrintInfo() {
            return "\"" + sampleName + "\" with " + countOfIncludedSpots + " spots";
        }

        @Override
        public String getSampleContents() {
            return sampleContents;
        }

        @Override
        public String getSampleName() {
            return sampleName;
        }

        @Override
        public int getContentsLength() {
            return sampleContents.length();
        }

        @Override
        public String toString() {
            return sampleContents;
        }

        /**
         * @return the selectedProperty
         */
        @Override
        public SimpleBooleanProperty getSelectedProperty() {
            return selectedProperty;
        }

        /**
         * @param selectedProperty the selectedProperty to set
         */
        public void setSelectedProperty(SimpleBooleanProperty selectedProperty) {
            this.selectedProperty = selectedProperty;
        }

        /**
         * @return the lockedForRefMat
         */
        @Override
        public boolean isLockedForRefMat() {
            return lockedForRefMat;
        }

        /**
         * @param lockedForRefMat the lockedForRefMat to set
         */
        @Override
        public void setLockedForRefMat(boolean lockedForRefMat) {
            this.lockedForRefMat = lockedForRefMat;
        }

        /**
         * @return the countOfIncludedSpots
         */
        @Override
        public int getCountOfIncludedSpots() {
            return countOfIncludedSpots;
        }

    }

}
