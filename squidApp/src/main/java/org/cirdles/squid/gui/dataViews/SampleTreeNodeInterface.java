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
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public interface SampleTreeNodeInterface {

    /**
     *
     * @return the java.lang.String
     */
    public String getNodeName();

    public ShrimpFractionExpressionInterface getShrimpFraction();

    /**
     * @return the selectedProperty
     */
    public SimpleBooleanProperty getSelectedProperty();

    /**
     * @param selectedProperty the selectedProperty to set
     */
    public void setSelectedProperty(SimpleBooleanProperty selectedProperty);

    /**
     * @return the datum
     */
    public Map<String, Object> getDatum();
}
