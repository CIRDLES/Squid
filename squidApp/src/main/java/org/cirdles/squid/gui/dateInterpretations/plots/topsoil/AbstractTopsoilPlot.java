package org.cirdles.squid.gui.dateInterpretations.plots.topsoil;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.cirdles.squid.gui.SquidUI;
import org.cirdles.squid.gui.dateInterpretations.plots.PlotDisplayInterface;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.utilities.stateUtilities.SquidLabData;
import org.cirdles.topsoil.Variable;
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
import java.util.concurrent.CompletableFuture;

public abstract class AbstractTopsoilPlot implements PlotDisplayInterface {

    private PlotView plot;

    private final ReadOnlyListProperty<DataEntry> dataEntries = new SimpleListProperty<>(FXCollections.observableArrayList());

    public final ObservableList<DataEntry> getData() {
        return dataEntries.get();
    }

    @Override
    public final void setData(List<Map<String, Object>> data) {
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
        dataEntries.setAll(entries);
    }

    public final void setDataEntries(List<DataEntry> dataEntries) {
        this.dataEntries.setAll(dataEntries);
    }

    private final ReadOnlyMapProperty<PlotOption<?>, Object> options
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

    private BooleanProperty isLoading = new SimpleBooleanProperty(false);

    public AbstractTopsoilPlot(String title) {
        this(
                title,
                new ArrayList<ShrimpFractionExpressionInterface>(),
                SquidLabData.getExistingSquidLabData().getPhysConstDefault()
        );
    }

    public AbstractTopsoilPlot(
            String title,
            List<ShrimpFractionExpressionInterface> shrimpFractions,
            ParametersModel physicalConstantsModel) {
        setupPlot(title, physicalConstantsModel);
    }

    protected abstract void setupPlot(String title, ParametersModel physicalConstantsModel);

    @Override
    public void setProperty(String key, Object datum) {
        PlotOption<?> option = PlotOption.forKey(key);
        if (option != null) {
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
            plot = new PlotView(PlotType.SCATTER, new PlotOptions(getPlotOptions()), getData());
            CompletableFuture<Void> loadFuture = plot.getLoadFuture();
            isLoading.set(!loadFuture.isDone());
            loadFuture.whenComplete(((aVoid, throwable) -> isLoading.set(false)));
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
        Text loadingIndicator = new Text("Loading...");
        loadingIndicator.visibleProperty().bind(isLoading);

        Button saveToSVGButton = new Button("Save as SVG");
        saveToSVGButton.setOnAction(mouseEvent -> {
            if (plot == null) {
                // Plot has not been displayed
                return;
            }
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("SVG", "svg"));
            File file = chooser.showSaveDialog(SquidUI.primaryStageWindow);
            if (file != null) {
                new SVGSaver().save(plot.toSVGDocument(), file);
            }
        });

        Button recenterButton = new Button("Re-center");
        recenterButton.setOnAction(mouseEvent -> {
            if (plot != null) {
                plot.call(PlotFunction.Scatter.RECENTER);
            }
        });

        List<Node> controls = new ArrayList<>(Arrays.asList(
                loadingIndicator,
                saveToSVGButton,
                recenterButton
        ));

        return controls;
    }

    @Override
    public String makeAgeOrValueString(int index) {
        return "";
    }

}
