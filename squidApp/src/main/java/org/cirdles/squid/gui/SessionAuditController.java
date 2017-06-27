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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.utilities.SquidPrefixTree;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class SessionAuditController implements Initializable {

    @FXML
    private TreeView<String> prawnAuditTree;
    @FXML
    private AnchorPane sessionAnchorPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        sessionAnchorPane.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        sessionAnchorPane.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(PIXEL_OFFSET_FOR_MENU));
        
        prawnAuditTree.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        prawnAuditTree.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(PIXEL_OFFSET_FOR_MENU));
        
        setUpPrawnFileAuditTreeView();
    }

    private void setUpPrawnFileAuditTreeView() {
        prawnAuditTree.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);

        TreeItem<String> rootItem = new TreeItem<>("Spots", null);
        rootItem.setExpanded(true);
        prawnAuditTree.setRoot(rootItem);

        SquidPrefixTree spotPrefixTree = squidProject.getPrefixTree();

        String summaryStatsString = spotPrefixTree.buildSummaryDataString();
        rootItem.setValue("Spots by prefix:" + summaryStatsString);

        populatePrefixTreeView(rootItem, spotPrefixTree);
    }

    private void populatePrefixTreeView(TreeItem<String> parentItem, SquidPrefixTree squidPrefixTree) {

        List<SquidPrefixTree> children = squidPrefixTree.getChildren();

        for (int i = 0; i < children.size(); i++) {
            if (!children.get(i).isleaf()) {
                TreeItem<String> item
                        = new TreeItem<>(children.get(i).getStringValue()
                                + children.get(i).buildSummaryDataString()
                        );

                parentItem.getChildren().add(item);

                if (children.get(i).hasChildren()) {
                    populatePrefixTreeView(item, children.get(i));
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

}
