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
package org.cirdles.squid.gui.dateInterpretations.plots.plotControllers;

import javafx.beans.property.SimpleBooleanProperty;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.gui.dataViews.SampleTreeNodeInterface;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.WeightedMeanPlot;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;

import java.util.Map;

import static org.cirdles.squid.gui.dateInterpretations.plots.plotControllers.PlotsController.plot;

/**
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class WeightedMeanSpotNode implements SampleTreeNodeInterface {

    private final ShrimpFractionExpressionInterface shrimpFraction;
    private final int indexOfSpot;
    private SimpleBooleanProperty selectedProperty;

    public WeightedMeanSpotNode(ShrimpFractionExpressionInterface shrimpFraction, int indexOfSpot) {
        this.shrimpFraction = shrimpFraction;
        this.selectedProperty = new SimpleBooleanProperty(shrimpFraction.isSelected());
        this.indexOfSpot = indexOfSpot;
    }

    /**
     * @return the shrimpFraction
     */
    @Override
    public ShrimpFractionExpressionInterface getShrimpFraction() {
        return shrimpFraction;
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
    @Override
    public void setSelectedProperty(SimpleBooleanProperty selectedProperty) {
        this.selectedProperty = selectedProperty;
        this.shrimpFraction.setSelected(selectedProperty.getValue());
    }

    /**
     * @return the fraction name with age data appended
     */
    @Override
    public String getNodeName() throws SquidException {
        String retVal = shrimpFraction.getFractionID();
        if (shrimpFraction.isReferenceMaterial()) {
            retVal += ((plot == null) ? "" : "  " + plot.makeAgeOrValueString(indexOfSpot));
        } else {
            double[] age = shrimpFraction.getTaskExpressionsEvaluationsPerSpotByField(shrimpFraction.getSelectedAgeExpressionName())[0];
            retVal += "  " + WeightedMeanPlot.makeAgeString(age[0], age[1]);
        }
        return retVal;
    }

    public int getIndexOfSpot() {
        return indexOfSpot;
    }

    @Override
    public Map<String, Object> getDatum() {
        return null;
    }
}