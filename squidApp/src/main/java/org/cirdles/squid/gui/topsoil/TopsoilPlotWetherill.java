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
import static org.cirdles.topsoil.app.plot.variable.Variables.RHO;
import static org.cirdles.topsoil.app.plot.variable.Variables.SIGMA_X;
import static org.cirdles.topsoil.app.plot.variable.Variables.SIGMA_Y;
import static org.cirdles.topsoil.app.plot.variable.Variables.X;
import static org.cirdles.topsoil.app.plot.variable.Variables.Y;
import org.cirdles.topsoil.plot.base.BasePlotDefaultProperties;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.CONCORDIA_LINE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.ELLIPSE_FILL_COLOR;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.ISOTOPE_TYPE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.TITLE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.X_AXIS;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.Y_AXIS;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class TopsoilPlotWetherill extends AbstractTopsoilPlot {

    // x, y, sigma x, sigma y, rho
    private static final double[][] DATA = new double[][]{
        {0.0722539075, 0.0110295656, 0.0002049758, 0.0000063126, 0.5365532874},
        {0.0721971452, 0.0110309854, 0.0001783027, 0.0000056173, 0.5325448483},
        {0.0721480905, 0.0110333887, 0.0001262722, 0.0000053814, 0.5693849155},
        {0.0720208987, 0.0110278685, 0.0001041118, 0.0000051695, 0.6034598793},
        {0.0722006985, 0.0110287224, 0.0001150679, 0.0000053550, 0.6488140173},
        {0.0721043666, 0.0110269651, 0.0001536438, 0.0000055438, 0.4514464090},
        {0.0721563039, 0.0110282194, 0.0001241486, 0.0000054189, 0.5407720667},
        {0.0721973299, 0.0110274879, 0.0001224165, 0.0000055660, 0.5557499444},
        {0.0721451656, 0.0110281849, 0.0001461117, 0.0000054048, 0.5309378161},
        {0.0720654237, 0.0110247729, 0.0001547497, 0.0000053235, 0.2337854029},
        {0.0721799174, 0.0110318201, 0.0001485404, 0.0000056511, 0.5177944463},
        {0.0721826355, 0.0110283902, 0.0001377158, 0.0000056126, 0.5953348385},
        {0.0720275042, 0.0110278402, 0.0001875497, 0.0000058909, 0.5274591815},
        {0.0721360819, 0.0110276418, 0.0001252055, 0.0000054561, 0.5760966585}
    };

    // this test data set is CM2 from ET_Redux work
    private static final double[][] DATA2 = new double[][]{
        {0.071813669, 0.011006957, 0.00010654762, 0.0000029902690, 0.525021016},
        {0.072151433, 0.011005053, 0.00007607823, 0.0000027925747, 0.576825349},
        {0.071944887, 0.011003275, 0.00005774879, 0.0000026419337, 0.565467772},
        {0.071935928, 0.011006019, 0.00007001780, 0.0000027907138, 0.593632132},
        {0.071881029, 0.011006746, 0.00011879759, 0.0000029932998, 0.547212036},
        {0.072008073, 0.011000075, 0.00012637628, 0.0000030924856, 0.491113441},
        {0.071909459, 0.011005301, 0.00014366566, 0.0000034129203, 0.53576221},
        {0.072023966, 0.011007749, 0.00006526067, 0.0000027068856, 0.588051902},
        {0.07204976, 0.011005122, 0.00006776661, 0.0000027686638, 0.663026105},
        {0.072067922, 0.011007277, 0.00007005962, 0.0000027064135, 0.547645084},
        {0.072012531, 0.011005595, 0.00007278283, 0.0000027963019, 0.485116139},
        {0.071951025, 0.011001109, 0.00006243122, 0.0000027089483, 0.587402112},
        {0.071984195, 0.011002318, 0.00005824101, 0.0000026286515, 0.547568329},
        {0.072026796, 0.011001577, 0.00009552567, 0.0000028683014, 0.580131768}

    };

    public TopsoilPlotWetherill(String title) {
        List<Map<String, Object>> myData = new ArrayList<>();
        for (int i = 0; i < DATA2.length; i++) {
            Map<String, Object> datum = new HashMap<>();
            myData.add(datum);
            datum.put(X.getName(), DATA2[i][0]);
            datum.put(SIGMA_X.getName(), DATA2[i][2]);
            datum.put(Y.getName(), DATA2[i][1]);
            datum.put(SIGMA_Y.getName(), DATA2[i][3]);
            datum.put(RHO.getName(), DATA2[i][4]);
        }

        plot = IsotopeType.UPb.getPlots()[0].getPlot();
        plot.setProperties(new BasePlotDefaultProperties());
        
        plot.setProperty(ISOTOPE_TYPE, IsotopeType.UPb.getName());
        plot.setProperty(X_AXIS, IsotopeType.UPb.getHeaders()[0]);
        plot.setProperty(Y_AXIS, IsotopeType.UPb.getHeaders()[1]);
        
        plot.setProperty(ELLIPSE_FILL_COLOR, "red");

        plot.setProperty(TITLE, title);
        plot.setProperty(CONCORDIA_LINE, true);

        plot.setData(myData);
    }

    @Override
    public Pane initializePlotPane() {
        TopsoilPlotController.plot = getPlot();
        Pane topsoilPlotUI = null;
        try {
            topsoilPlotUI = FXMLLoader.load(getClass().getResource("../TopsoilPlot.fxml"));
        } catch (IOException iOException) {
        }

        return topsoilPlotUI;
    }

}
