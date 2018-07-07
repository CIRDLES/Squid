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
package org.cirdles.squid.gui.topsoil;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.gui.topsoil.TopsoilDataFactory.prepareWetherillDatum;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.topsoil.plot.JavaScriptPlot;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class TopsoilPlotController implements Initializable {

    public static AbstractTopsoilPlot topsoilPlot;

    /**
     * Set this field with an AbstractTopsoilPlot instance in advance of loading
     * this class.
     *
     * @param aTopsoilPlot the aTopsoilPlot to set
     */
    public static void setTopsoilPlot(AbstractTopsoilPlot aTopsoilPlot) {
        topsoilPlot = aTopsoilPlot;
    }

    @FXML
    private VBox vboxMaster;
    @FXML
    private AnchorPane plotAndConfig;
    @FXML
    private ToolBar plotToolBar;
    @FXML
    private ListView<FractionNode> fractionsListView;

    private static ObservableList<FractionNode> fractionNodes;
    
    private static List<Map<String, Object>> data;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (topsoilPlot.getPlot() instanceof JavaScriptPlot) {
            Node topsoilPlotNode = topsoilPlot.getPlot().displayAsNode();

            plotAndConfig.getChildren().setAll(topsoilPlotNode);
            AnchorPane.setLeftAnchor(topsoilPlotNode, 0.0);
            AnchorPane.setRightAnchor(topsoilPlotNode, 0.0);
            AnchorPane.setTopAnchor(topsoilPlotNode, 0.0);
            AnchorPane.setBottomAnchor(topsoilPlotNode, 0.0);

            VBox.setVgrow(plotAndConfig, Priority.ALWAYS);

            plotToolBar.getItems().addAll(topsoilPlot.toolbarControlsFactory());
            plotToolBar.setPadding(Insets.EMPTY);

            List<ShrimpFractionExpressionInterface> shrimpFractionsDetails
                    = squidProject.getTask().getReferenceMaterialSpots();
            List<FractionNode> fractionNodeDetails = new ArrayList<>();
            data = new ArrayList<>();
            
            for (int i = 0; i < shrimpFractionsDetails.size(); i ++){
                FractionNode fractionNode = 
                        new FractionNode(shrimpFractionsDetails.get(i));
                fractionNodeDetails.add(fractionNode);
                data.add(fractionNode.getDatum());
            }
            fractionNodes = FXCollections.observableArrayList(fractionNodeDetails);
            topsoilPlot.getPlot().setData(data);
            
            for (FractionNode fraction : fractionNodes) {
                fraction.getSelectedProperty().addListener((obs, wasSelectedState, isSelectedState) -> {
                    fraction.getShrimpFraction().setSelected(isSelectedState);
                    fraction.getDatum().put("Selected", isSelectedState);
                    topsoilPlot.getPlot().setData(data);
                    System.out.println(fraction.getShrimpFraction().getFractionID() + " changed on state from " + wasSelectedState + " to " + isSelectedState);
                });
            }

            fractionsListView.setItems(fractionNodes);
            fractionsListView.setCellFactory(CheckBoxListCell.forListView(
                    item -> item.getSelectedProperty(),
                    new StringConverter<FractionNode>() {

                @Override
                public String toString(FractionNode fraction) {
                    return fraction.getShrimpFraction().getFractionID() + "jj";
                }

                @Override
                public FractionNode fromString(String string) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

            }));
        }
    }
    
    private class FractionNode{
        private ShrimpFractionExpressionInterface shrimpFraction;
        private Map<String, Object> datum;
        private SimpleBooleanProperty selectedProperty;

        public FractionNode(ShrimpFractionExpressionInterface shrimpFraction) {
            this.shrimpFraction = shrimpFraction;
            this.datum = prepareWetherillDatum(shrimpFraction);
            this.selectedProperty =  new SimpleBooleanProperty(shrimpFraction.isSelected());
        }

        /**
         * @return the shrimpFraction
         */
        public ShrimpFractionExpressionInterface getShrimpFraction() {
            return shrimpFraction;
        }

        /**
         * @return the datum
         */
        public Map<String, Object> getDatum() {
            return datum;
        }

        /**
         * @return the selectedProperty
         */
        public SimpleBooleanProperty getSelectedProperty() {
            return selectedProperty;
        }

        /**
         * @param selectedProperty the selectedProperty to set
         */
        public void setSelectedProperty(SimpleBooleanProperty selectedProperty) {
            this.selectedProperty = selectedProperty;
        }
        
    }
}
