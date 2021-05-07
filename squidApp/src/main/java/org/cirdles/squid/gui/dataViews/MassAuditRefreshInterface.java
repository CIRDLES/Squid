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
package org.cirdles.squid.gui.dataViews;

import org.cirdles.squid.prawn.PrawnFile;

import java.util.List;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public interface MassAuditRefreshInterface {
    public void updateGraphsWithSelectedIndex(int index, int leadingZoomingTrailing);
    
    public void updateGraphsWithSecondSelectedIndex(int index, int leadingZoomingTrailing);
    
    /**
     *
     */
    public void updateSpotsInGraphs();
    
    public int[] getCountOfScansCumulative(List<PrawnFile.Run> prawnFileRuns);
}