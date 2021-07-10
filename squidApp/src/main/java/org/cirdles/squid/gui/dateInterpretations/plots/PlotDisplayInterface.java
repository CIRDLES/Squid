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

import javafx.scene.Node;

import java.util.List;
import java.util.Map;

/**
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

    public String makeAgeOrValueString(int index);


}