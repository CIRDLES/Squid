/*
 * Copyright 2016 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.tasks.expressions.spots;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToDoubles;
import static org.cirdles.squid.utilities.conversionUtilities.CloningUtilities.clone2dArray;

/**
 *
 * @author James F. Bowring
 */
public class SpotSummaryDetails implements Serializable {

    private static final long serialVersionUID = 761897612163829455L;

    private double[][] values;
    private List<ShrimpFractionExpressionInterface> selectedSpots;
    private ExpressionTreeInterface expressionTree;
    private boolean[] rejectedIndices;
    private boolean manualRejectionEnabled;
    private double minProbabilityWM;
    private String selectedExpressionName;

    private SpotSummaryDetails() {
        this(null);
    }

    public SpotSummaryDetails(ExpressionTreeInterface expressionTree) {
        this(expressionTree, new double[0][0], new ArrayList<ShrimpFractionExpressionInterface>());
    }

    /**
     *
     * @param expressionTree
     * @param values
     * @param selectedSpots
     */
    public SpotSummaryDetails(ExpressionTreeInterface expressionTree, double[][] values, List<ShrimpFractionExpressionInterface> selectedSpots) {
        this.expressionTree = expressionTree;
        this.values = values.clone();
        this.selectedSpots = selectedSpots;
        this.rejectedIndices = new boolean[selectedSpots.size()];
        this.manualRejectionEnabled = false;
        this.minProbabilityWM = 0.0;
        this.selectedExpressionName = "204/206";
    }

    public double[][] eval(TaskInterface task) throws SquidException {
        return convertObjectArrayToDoubles(expressionTree.eval(retrieveActiveSpots(), task));
    }

    public List<ShrimpFractionExpressionInterface> retrieveActiveSpots() {
        List<ShrimpFractionExpressionInterface> activeSpots = new ArrayList<>();

        if (rejectedIndices == null) {
            rejectedIndices = new boolean[selectedSpots.size()];
        }

        for (int i = 0; i < selectedSpots.size(); i++) {
            if (!rejectedIndices[i]) {
                activeSpots.add(selectedSpots.get(i));
            }
        }
        return activeSpots;
    }

    /**
     * @return the values
     */
    public double[][] getValues() {
        return values.clone();
    }

    /**
     *
     * @param values
     */
    public void setValues(double[][] values) {
        this.values = clone2dArray(values);
    }

    /**
     * @return the selectedSpots
     */
    public List<ShrimpFractionExpressionInterface> getSelectedSpots() {
        return selectedSpots;
    }

    /**
     *
     * @param selectedSpots
     */
    public void setSelectedSpots(List<ShrimpFractionExpressionInterface> selectedSpots) {
        this.selectedSpots = selectedSpots;
    }

    /**
     *
     * @param expressionTree
     */
    public void setExpressionTree(ExpressionTreeInterface expressionTree) {
        this.expressionTree = expressionTree;
    }

    /**
     *
     * @return
     */
    public ExpressionTreeInterface getExpressionTree() {
        return expressionTree;
    }

    /**
     * @param rejectedIndices the rejectedIndices to set
     */
    public void setRejectedIndices(boolean[] rejectedIndices) {
        this.rejectedIndices = rejectedIndices.clone();
    }

    public void rejectNone() {
        rejectedIndices = new boolean[selectedSpots.size()];
    }

    public void rejectAll() {
        rejectedIndices = new boolean[selectedSpots.size()];
        for (int i = 0; i < rejectedIndices.length; i++) {
            rejectedIndices[i] = true;
        }
    }

    public boolean[] getRejectedIndices() {
        return this.rejectedIndices.clone();
    }

    public void setIndexOfRejectedIndices(int index, boolean value) {
        rejectedIndices[index] = value;
    }

    /**
     * @return the manualRejectionEnabled
     */
    public boolean isManualRejectionEnabled() {
        return manualRejectionEnabled;
    }

    /**
     * @param manualRejectionEnabled the manualRejectionEnabled to set
     */
    public void setManualRejectionEnabled(boolean manualRejectionEnabled) {
        this.manualRejectionEnabled = manualRejectionEnabled;
    }

    /**
     * @return the minProbabilityWM
     */
    public double getMinProbabilityWM() {
        return minProbabilityWM;
    }

    /**
     * @param minProbabilityWM the minProbabilityWM to set
     */
    public void setMinProbabilityWM(double minProbabilityWM) {
        this.minProbabilityWM = minProbabilityWM;
    }

    /**
     * @return the selectedExpressionName
     */
    public String getSelectedExpressionName() {
        return selectedExpressionName;
    }

    /**
     * @param selectedExpressionName the selectedExpressionName to set
     */
    public void setSelectedExpressionName(String selectedExpressionName) {
        this.selectedExpressionName = selectedExpressionName;
    }
}
