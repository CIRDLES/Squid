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
package org.cirdles.squid.gui.plots.topsoil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.cirdles.squid.gui.SquidUI;
import org.cirdles.squid.gui.plots.PlotDisplayInterface;
import org.cirdles.topsoil.Variable;
import org.cirdles.topsoil.javafx.PlotView;
import org.cirdles.topsoil.plot.DataEntry;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.PlotFunction;
import org.cirdles.topsoil.plot.internal.SVGSaver;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractTopsoilPlot implements PlotDisplayInterface {

    protected PlotView plot;

    protected AbstractTopsoilPlot() {
    }

    @Override
    public void setData(List<Map<String, Object>> data) {
        List<DataEntry> entries = new ArrayList<>();
        DataEntry newEntry;
        for (Map<String, Object> map : data) {
            newEntry = new DataEntry();
            for (Map.Entry<String, Object> pair : map.entrySet()) {
                String key = pair.getKey().toLowerCase();
                newEntry.put(Variable.variableForKey(key), pair.getValue());
            }
            entries.add(newEntry);
        }
        plot.setData(entries);
    }

    @Override
    public abstract Node displayPlotAsNode();

    public void reloadEngine() {
        plot.reloadEngine().thenRun(plot::update);
    }

    public void recenter() {
        plot.call(PlotFunction.Scatter.RECENTER);
    }

    @Override
    public abstract void setProperty(String key, Object datum);

    @Override
    public List<Node> toolbarControlsFactory() {
        List<Node> controls = new ArrayList<>();

//        Text loadingIndicator = new Text("Loading...");
//        loadingIndicator.setVisible(true);
//        plot.getLoadFuture().whenComplete(((aVoid, throwable) -> loadingIndicator.setVisible(false)));

        Button saveToSVGButton = new Button("Save as SVG");
        saveToSVGButton.setOnAction(mouseEvent -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("SVG", "svg"));
            File file = chooser.showSaveDialog(SquidUI.primaryStageWindow);
            if (file != null) {
                new SVGSaver().save(plot.toSVGDocument(), file);
            }
        });

        Button recenterButton = new Button("Re-center");
        recenterButton.setOnAction(mouseEvent -> {
            plot.call(PlotFunction.Scatter.RECENTER);
        });

//        controls.add(loadingIndicator);
        controls.add(saveToSVGButton);
        controls.add(recenterButton);

        return controls;
    }

}
