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
package org.cirdles.squid.gui.dateInterpretations.plots;

import java.util.List;
import java.util.Map;
import javafx.scene.Node;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public interface PlotDisplayInterface {

    /**
     * @return the javafx.scene.Node
     */
    Node displayPlotAsNode();

    void setData(List<Map<String, Object>> data);
    
    void setProperty(String key, Object datum);

    List<Node> toolbarControlsFactory();
    
    public String makeAgeString(int index);

    static enum SigmaPresentationModes {
        ONE_SIGMA_ABSOLUTE("1σ (abs)", 1.0),
        TWO_SIGMA_ABSOLUTE("2σ (abs)", 2.0),
        NINETY_FIVE_PERCENT_CONFIDENCE("95% Conf.", 2.4477);

        private final String displayName;
        private final double sigmaMultiplier;

        private SigmaPresentationModes(String displayName, double sigmaMultiplier) {
            this.displayName = displayName;
            this.sigmaMultiplier = sigmaMultiplier;
        }

        /**
         * @return the displayName
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * @return the sigmaMultiplier
         */
        public double getSigmaMultiplier() {
            return sigmaMultiplier;
        }
    }
    

}
