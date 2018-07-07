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
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
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
import javafx.util.Callback;
import javafx.util.StringConverter;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
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
    private ListView<ShrimpFractionExpressionInterface> fractionsListView;

    private static ObservableList<ShrimpFractionExpressionInterface> shrimpFractions;

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
            shrimpFractions = FXCollections.observableArrayList(shrimpFractionsDetails);

            for (ShrimpFractionExpressionInterface fraction : shrimpFractions) {
                // observe item's on property and display message if it changes:
                fraction.selectedProperty().addListener((obs, wasSelectedState, isSelectedState) -> {
                    fraction.setSelected(isSelectedState);
                    
                    List<Map<String, Object>> data = topsoilPlot.getPlot().getData();
                    data.get(0).put("Selected", fraction.isSelected());
                    topsoilPlot.getPlot().setData(data);
                    System.out.println(fraction.getFractionID() + " changed on state from " + wasSelectedState + " to " + isSelectedState);
                });
            }

            fractionsListView.setItems(shrimpFractions);
            fractionsListView.setCellFactory(CheckBoxListCell.forListView(
                    item -> item.selectedProperty(),
                    new StringConverter<ShrimpFractionExpressionInterface>() {

                @Override
                public String toString(ShrimpFractionExpressionInterface fraction) {
                    return fraction.getFractionID() + "jj";
                }

                @Override
                public ShrimpFractionExpressionInterface fromString(String string) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

            }));
        }
    }
    
    private class FractionNode{
        private ShrimpFractionExpressionInterface shrimpFraction;
        private Map<String, Object> datum;
        private SimpleBooleanProperty selectedProperty;

        public FractionNode(ShrimpFractionExpressionInterface shrimpFraction, Map<String, Object> datum, SimpleBooleanProperty selectedProperty) {
            this.shrimpFraction = shrimpFraction;
            this.datum = datum;
            this.selectedProperty = selectedProperty;
        }

        /**
         * @return the shrimpFraction
         */
        public ShrimpFractionExpressionInterface getShrimpFraction() {
            return shrimpFraction;
        }

        /**
         * @param shrimpFraction the shrimpFraction to set
         */
        public void setShrimpFraction(ShrimpFractionExpressionInterface shrimpFraction) {
            this.shrimpFraction = shrimpFraction;
        }

        /**
         * @return the datum
         */
        public Map<String, Object> getDatum() {
            return datum;
        }

        /**
         * @param datum the datum to set
         */
        public void setDatum(Map<String, Object> datum) {
            this.datum = datum;
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
