/*
 * Copyright 2017 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.gui.topsoil;

import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author James F. Bowring
 */
public final class TopsoilWindow {

    private AbstractTopsoilPlot topsoilPlot;

    private Stage topsoilPlotWindow;

    private TopsoilWindow() {
    }

    public TopsoilWindow(AbstractTopsoilPlot topsoilPlot) {
        this.topsoilPlot = topsoilPlot;
    }

    public void loadTopsoilWindow(double x, double y) {

        SplitPane topsoilPlotUI = topsoilPlot.initializePlotPane();

        Scene topsoilPlotScene = new Scene(topsoilPlotUI, 600, 600);
        topsoilPlotWindow = new Stage(StageStyle.DECORATED);
        // center on Squid
        topsoilPlotWindow.setX(x);
        topsoilPlotWindow.setY(y);
        topsoilPlotWindow.setResizable(true);
        topsoilPlotWindow.setScene(topsoilPlotScene);
        topsoilPlotWindow.setTitle("Topsoil Plot");

        topsoilPlotWindow.requestFocus();
        topsoilPlotWindow.initOwner(null);
        topsoilPlotWindow.initModality(Modality.NONE);

        topsoilPlotWindow.show();

    }

    public void close() {
        topsoilPlotWindow.close();
    }

    /**
     * @return the topsoilPlot
     */
    public AbstractTopsoilPlot getTopsoilPlot() {
        return topsoilPlot;
    }

}
