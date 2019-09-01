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

import javafx.beans.property.SimpleBooleanProperty;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class SampleNode implements SampleTreeNodeInterface {
        private final String sampleName;
        private SimpleBooleanProperty selectedProperty;

        public SampleNode(String sampleName) {
            this.sampleName = sampleName;
            this.selectedProperty = new SimpleBooleanProperty(false);
        }

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
}
