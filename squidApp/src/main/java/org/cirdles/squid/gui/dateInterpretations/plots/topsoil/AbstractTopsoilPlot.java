package org.cirdles.squid.gui.dateInterpretations.plots.topsoil;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import org.cirdles.squid.gui.SquidUI;
import org.cirdles.squid.gui.dateInterpretations.plots.PlotDisplayInterface;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.topsoil.javafx.PlotView;
import org.cirdles.topsoil.plot.DataEntry;
import org.cirdles.topsoil.plot.PlotFunction;
import org.cirdles.topsoil.plot.PlotOption;
import org.cirdles.topsoil.plot.PlotOptions;
import org.cirdles.topsoil.plot.PlotType;
import org.cirdles.topsoil.plot.internal.SVGSaver;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.cirdles.topsoil.Variable;
import static org.cirdles.topsoil.Variable.SIGMA_X;
import static org.cirdles.topsoil.Variable.SIGMA_Y;
import org.cirdles.topsoil.data.Uncertainty;

public abstract class AbstractTopsoilPlot implements PlotDisplayInterface {

    protected PlotView plot;
    protected PlotOptions plotOptions;
    protected boolean hasUncertainties;
    protected boolean hasData;

    protected final ReadOnlyListProperty<DataEntry> dataEntries = new SimpleListProperty<>(FXCollections.observableArrayList());

    public final ObservableList<DataEntry> getData() {
        return dataEntries.get();
    }

    @Override
    public final void setData(List<Map<String, Object>> data) {
        List<DataEntry> entries = new ArrayList<>();
        this.hasUncertainties = true;
        DataEntry newEntry;
        for (Map<String, Object> map : data) {
            newEntry = new DataEntry();
            for (Map.Entry<String, Object> pair : map.entrySet()) {
                String key = pair.getKey().toLowerCase();
                newEntry.put(Variable.variableForKey(key), pair.getValue());
            }
            entries.add(newEntry);
            this.hasUncertainties = hasUncertainties && ((Double) newEntry.get(SIGMA_X)) != 0.0;
            this.hasUncertainties = hasUncertainties && ((Double) newEntry.get(SIGMA_Y)) != 0.0;
        }

        dataEntries.setAll(entries);
    }

    public final void setDataEntries(List<DataEntry> dataEntries) {
        this.dataEntries.setAll(dataEntries);
    }

    protected final ReadOnlyMapProperty<PlotOption<?>, Object> options
            = new SimpleMapProperty<>(FXCollections.observableMap(PlotOptions.defaultOptions()));

    public final Map<PlotOption<?>, Object> getPlotOptions() {
        return options.get();
    }
   
    public final void setPlotOptions(PlotOptions options) {
        this.options.putAll(options);
    }

    public final void setPlotOptions(Map<String, Object> options) {
        for (Map.Entry<String, Object> entry : options.entrySet()) {
            setProperty(entry.getKey(), entry.getValue());
        }
    }

    public AbstractTopsoilPlot() {
        this.hasUncertainties = true;
        this.hasData = false;
    }

    protected abstract void setupPlot(String title, ParametersModel physicalConstantsModel);

    @Override
    public void setProperty(String key, Object datum) {
        PlotOption<?> option = PlotOption.forKey(key);
        if (option == null) {
            throw new IllegalArgumentException("String key \"" + key + "\" does not represent a valid PlotOption.");
        }

        options.put(PlotOption.forKey(key), datum);
    }

    public <T> void setProperty(PlotOption<T> option, T value) {
        options.put(option, value);
    }

    @Override
    public final Node displayPlotAsNode() {
        if (plot == null) {
            PlotOptions myPlotOptions = new PlotOptions(options.get());
            plot = new PlotView(PlotType.SCATTER, myPlotOptions, getData());
            dataEntries.addListener((ListChangeListener<DataEntry>) c -> {
                plot.setData(dataEntries);
            });
            options.addListener((MapChangeListener<PlotOption<?>, Object>) c -> {
                plot.setOptions(options);
            });
        }
        return plot;
    }

    @Override
    public List<Node> toolbarControlsFactory() {

        Button saveToSVGButton = new Button("Save as SVG");
        saveToSVGButton.setStyle("-fx-font-size: 12px;-fx-font-weight: bold; -fx-padding: 2 2 2 2;");
        saveToSVGButton.setOnAction(mouseEvent -> {
            if (plot == null) {
                // Plot has not been displayed
                return;
            }
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Export to SVG");
            chooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("SVG Image", "*.svg"));
            File file = chooser.showSaveDialog(SquidUI.primaryStageWindow);
            if (file != null) {
                new SVGSaver().save(plot.toSVGDocument(), file);
            }
        });

        Button recenterButton = new Button("Re-center");
        recenterButton.setStyle("-fx-font-size: 12px;-fx-font-weight: bold; -fx-padding: 2 2 2 2;");
        recenterButton.setOnAction(mouseEvent -> {
            recenterPlot();
        });

        List<Node> controls = new ArrayList<>(Arrays.asList(
                saveToSVGButton,
                recenterButton
        ));

        return controls;
    }

    public void recenterPlot() {
        if (plot != null) {
            plot.call(PlotFunction.Scatter.RECENTER);
        }
    }

    protected Uncertainty[] retrievePlottingUncertainties() {
        List<Uncertainty> plottingUncertList = new ArrayList<>();
        for (Uncertainty entry : Uncertainty.values()) {
            if (!entry.getName().contains("(%)")) {
                plottingUncertList.add(entry);
            }
        }

        Uncertainty[] plottingUncertArray = new Uncertainty[plottingUncertList.size()];
        plottingUncertArray = plottingUncertList.toArray(plottingUncertArray);
        return plottingUncertArray;
    }

    @Override
    public String makeAgeOrValueString(int index) {
        return "";
    }

    /**
     * @return the hasUncertainties
     */
    public boolean isHasUncertainties() {
        return hasUncertainties;
    }
}
