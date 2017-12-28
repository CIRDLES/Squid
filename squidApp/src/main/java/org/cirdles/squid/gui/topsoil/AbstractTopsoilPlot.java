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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import org.cirdles.squid.gui.TopsoilPlotController;
import org.cirdles.topsoil.app.isotope.IsotopeType;
import org.cirdles.topsoil.app.plot.TopsoilPlotType;
import static org.cirdles.topsoil.app.plot.variable.Variables.RHO;
import static org.cirdles.topsoil.app.plot.variable.Variables.SIGMA_X;
import static org.cirdles.topsoil.app.plot.variable.Variables.SIGMA_Y;
import static org.cirdles.topsoil.app.plot.variable.Variables.X;
import static org.cirdles.topsoil.app.plot.variable.Variables.Y;
import org.cirdles.topsoil.plot.Plot;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.CONCORDIA_LINE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.ISOTOPE_TYPE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.TITLE;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public abstract class AbstractTopsoilPlot {

    protected Plot plot;

    protected AbstractTopsoilPlot() {
    }

    public abstract Pane initializePlotPane();

    /**
     * @return the plot
     */
    public Plot getPlot() {
        return plot;
    }

}
