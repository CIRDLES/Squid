/*
 * Copyright 2019 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.gui.dataViews;

import java.util.Map;
import javafx.beans.property.SimpleBooleanProperty;
import org.cirdles.squid.gui.dateInterpretations.plots.PlotDisplayInterface;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.WeightedMeanPlot;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.PlotRefreshInterface;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class SampleNode implements SampleTreeNodeInterface {

    private final String sampleName;
    private SimpleBooleanProperty selectedProperty;
    private PlotDisplayInterface samplePlot;    
    private PlotRefreshInterface plotsController;

    public SampleNode(String sampleName) {
        this.sampleName = sampleName;
        this.selectedProperty = new SimpleBooleanProperty(false);
        this.samplePlot = null;
        this.plotsController = null;
    }

    /**
     *
     * @return the java.lang.String
     */
    @Override
    public String getNodeName() {
        return sampleName;
    }

    @Override
    public ShrimpFractionExpressionInterface getShrimpFraction() {
        return null;
    }

    @Override
    public SimpleBooleanProperty getSelectedProperty() {
        return selectedProperty;
    }

    @Override
    public void setSelectedProperty(SimpleBooleanProperty selectedProperty) {
        this.selectedProperty = selectedProperty;
    }

    /**
     * @return the spotSummaryDetailsWM
     */
    public SpotSummaryDetails getSpotSummaryDetailsWM() {
        return (samplePlot == null) ? null : ((WeightedMeanPlot)samplePlot).getSpotSummaryDetails();
    }

    /**
     * @return the samplePlotWM
     */
    public PlotDisplayInterface getSamplePlotWM() {
        return samplePlot;
    }

    /**
     * @param samplePlotWM the samplePlotWM to set
     */
    public void setSamplePlotWM(PlotDisplayInterface samplePlotWM) {
        this.samplePlot = samplePlotWM;
    }

    /**
     * @return the plotsController
     */
    public PlotRefreshInterface getPlotsController() {
        return plotsController;
    }

    /**
     * @param plotsController the plotsController to set
     */
    public void setPlotsController(PlotRefreshInterface plotsController) {
        this.plotsController = plotsController;
    }

    @Override
    public Map<String, Object> getDatum() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
