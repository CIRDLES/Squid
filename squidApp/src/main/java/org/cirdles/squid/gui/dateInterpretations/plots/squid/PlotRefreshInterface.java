/*
 * Copyright 2018 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.gui.dateInterpretations.plots.squid;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public interface PlotRefreshInterface {  
    
    public void toggleSpotExclusionWM(int index);
    public void calculateWeightedMean();
    public void refreshPlot();
    public void showRefMatWeightedMeanPlot();
    public void showActivePlot();
    public void setXAxisExpressionName(String xAxisExpressionName);
    public void setYAxisExpressionName(String yAxisExpressionName);
    public void showExcludedSpots(boolean doShow);
}
