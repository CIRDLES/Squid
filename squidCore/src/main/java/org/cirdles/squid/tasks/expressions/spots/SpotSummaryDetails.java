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
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.expressions.OperationOrFunctionInterface;

/**
 *
 * @author James F. Bowring
 */
public class SpotSummaryDetails implements Serializable {

    private static final long serialVersionUID = 761897612163829455L;

    private double[][] values;
    private List<ShrimpFractionExpressionInterface> selectedSpots;
    private OperationOrFunctionInterface operation;
    private boolean[] rejectedIndices;

    private SpotSummaryDetails() {
        this(null);
    }
    
    public SpotSummaryDetails(OperationOrFunctionInterface operation) {
        this(operation, new double[0][0], new ArrayList<ShrimpFractionExpressionInterface>());
    }

    /**
     *
     * @param operation
     * @param values
     * @param selectedSpots
     */
    public SpotSummaryDetails(OperationOrFunctionInterface operation, double[][] values, List<ShrimpFractionExpressionInterface> selectedSpots) {
        this.operation = operation;
        this.values = values.clone();
        this.selectedSpots = selectedSpots;
        this.rejectedIndices = new boolean[selectedSpots.size()];
    }
    
    public List<ShrimpFractionExpressionInterface> retrieveActiveSpots(){
        List<ShrimpFractionExpressionInterface> activeSpots = new ArrayList<>();
        
        if (rejectedIndices == null) {
            rejectedIndices = new boolean[selectedSpots.size()];
        }
        
//        rejectedIndices[0] = true;
//        rejectedIndices[7] = true;
//        rejectedIndices[8] = true;
//        rejectedIndices[11] = true;
//        rejectedIndices[12] = true;
//        rejectedIndices[13] = true;
        
        for (int i = 0; i < selectedSpots.size(); i ++){
            if (! rejectedIndices[i]){
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
        this.values = values;
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
     * @return the operation
     */
    public OperationOrFunctionInterface getOperation() {
        return operation;
    }

    /**
     *
     * @param operation
     */
    public void setOperation(OperationOrFunctionInterface operation) {
        this.operation = operation;
    }

    /**
     * @param rejectedIndices the rejectedIndices to set
     */
    public void setRejectedIndices(boolean[] rejectedIndices) {
        this.rejectedIndices = rejectedIndices;
    }

}
