
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
package org.cirdles.squid.gui.dateInterpretations.plots.plotControllers;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.HBox;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import javafx.util.StringConverter;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.gui.dataViews.SampleNode;
import org.cirdles.squid.gui.dataViews.SampleTreeNodeInterface;
import org.cirdles.squid.gui.dateInterpretations.plots.PlotDisplayInterface;
import org.cirdles.squid.gui.dateInterpretations.plots.squid.WeightedMeanPlot;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.expressions.builtinExpressions.SampleAgeTypesEnum;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import org.cirdles.squid.tasks.taskUtilities.SpotGroupProcessor;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class CustomWeightedMeanCheckBoxTreeCell<T> extends CheckBoxTreeCell<T> {

    /**
     * *************************************************************************
     *                                                                         *
     * Static cell factories * *
     * ************************************************************************
     */
    /**
     * Creates a cell factory for use in a TreeView control, although there is a
     * major assumption when used in a TreeView: this cell factory assumes that
     * the TreeView root, and <b>all</b> children are instances of
     * {@link CheckBoxTreeItem}, rather than the default {@link TreeItem} class
     * that is used normally.
     *
     * <p>
     * When used in a TreeView, the CheckBoxCell is rendered with a CheckBox to
     * the right of the 'disclosure node' (i.e. the arrow). The item stored in
     * {@link CheckBoxTreeItem#getValue()} will then have the StringConverter
     * called on it, and this text will take all remaining horizontal space.
     * Additionally, by using {@link CheckBoxTreeItem}, the TreeView will
     * automatically handle situations such as:
     *
     * <ul>
     * <li>Clicking on the {@link CheckBox} beside an item that has children
     * will result in all children also becoming selected/unselected.</li>
     * <li>Clicking on the {@link CheckBox} beside an item that has a parent
     * will possibly toggle the state of the parent. For example, if you select
     * a single child, the parent will become indeterminate (indicating partial
     * selection of children). If you proceed to select all children, the parent
     * will then show that it too is selected. This is recursive, with all
     * parent nodes updating as expected.</li>
     * </ul>
     *
     * <p>
     * Unfortunately, due to limitations in Java, it is necessary to provide an
     * explicit cast when using this method. For example:
     *
     * <pre>
     * {@code
     * final TreeView<String> treeView = new TreeView<String>();
     * treeView.setCellFactory(CheckBoxCell.<String>forTreeView());}</pre>
     *
     * @param <T> The type of the elements contained within the
     * {@link CheckBoxTreeItem} instances.
     * @return A {@link Callback} that will return a TreeCell that is able to
     * work on the type of element contained within the TreeView root, and all
     * of its children (recursively).
     */
    public static <T> Callback<TreeView<T>, TreeCell<T>> forTreeView() {
        return CheckBoxTreeCell.forTreeView();
    }

    /**
     * Creates a cell factory for use in a TreeView control. Unlike
     * {@link #forTreeView()}, this method does not assume that all TreeItem
     * instances in the TreeView are {@link CheckBoxTreeItem} instances.
     *
     * <p>
     * When used in a TreeView, the CheckBoxCell is rendered with a CheckBox to
     * the right of the 'disclosure node' (i.e. the arrow). The item stored in
     * {@link CheckBoxTreeItem#getValue()} will then have the StringConverter
     * called on it, and this text will take all remaining horizontal space.
     *
     * <p>
     * Unlike {@link #forTreeView()}, this cell factory does not handle updating
     * the state of parent or children TreeItems - it simply toggles the
     * {@code ObservableValue<Boolean>} that is provided, and no more. Of
     * course, this functionality can then be implemented externally by adding
     * observers to the {@code ObservableValue<Boolean>}, and toggling the state
     * of other properties as necessary.
     *
     * @param <T> The type of the elements contained within the {@link TreeItem}
     * instances.
     * @param getSelectedProperty A {@link Callback} that, given an object of
     * type TreeItem<T>, will return an {@code ObservableValue<Boolean>} that
     * represents whether the given item is selected or not. This
     * {@code ObservableValue<Boolean>} will be bound bidirectionally (meaning
     * that the CheckBox in the cell will set/unset this property based on user
     * interactions, and the CheckBox will reflect the state of the
     * {@code ObservableValue<Boolean>}, if it changes externally).
     * @return A {@link Callback} that will return a TreeCell that is able to
     * work on the type of element contained within the TreeView root, and all
     * of its children (recursively).
     */
    public static <T> Callback<TreeView<T>, TreeCell<T>> forTreeView(
            final Callback<TreeItem<T>, ObservableValue<Boolean>> getSelectedProperty) {
        return CheckBoxTreeCell.forTreeView(getSelectedProperty);
    }

    /**
     * Creates a cell factory for use in a TreeView control. Unlike
     * {@link #forTreeView()}, this method does not assume that all TreeItem
     * instances in the TreeView are {@link CheckBoxTreeItem}.
     *
     * <p>
     * When used in a TreeView, the CheckBoxCell is rendered with a CheckBox to
     * the right of the 'disclosure node' (i.e. the arrow). The item stored in
     * {@link TreeItem#getValue()} will then have the the StringConverter called
     * on it, and this text will take all remaining horizontal space.
     *
     * <p>
     * Unlike {@link #forTreeView()}, this cell factory does not handle updating
     * the state of parent or children TreeItems - it simply toggles the
     * {@code ObservableValue<Boolean>} that is provided, and no more. Of
     * course, this functionality can then be implemented externally by adding
     * observers to the {@code ObservableValue<Boolean>}, and toggling the state
     * of other properties as necessary.
     *
     * @param <T> The type of the elements contained within the {@link TreeItem}
     * instances.
     * @param getSelectedProperty A Callback that, given an object of type
     * TreeItem<T>, will return an {@code ObservableValue<Boolean>} that
     * represents whether the given item is selected or not. This
     * {@code ObservableValue<Boolean>} will be bound bidirectionally (meaning
     * that the CheckBox in the cell will set/unset this property based on user
     * interactions, and the CheckBox will reflect the state of the
     * {@code ObservableValue<Boolean>}, if it changes externally).
     * @param converter A StringConverter that, give an object of type
     * TreeItem<T>, will return a String that can be used to represent the
     * object visually. The default implementation in
     * {@link #forTreeView(Callback)} is to simply call .toString() on all
     * non-null items (and to just return an empty string in cases where the
     * given item is null).
     * @return A {@link Callback} that will return a TreeCell that is able to
     * work on the type of element contained within the TreeView root, and all
     * of its children (recursively).
     */
    public static <T> Callback<TreeView<T>, TreeCell<T>> forTreeView(
            final Callback<TreeItem<T>, ObservableValue<Boolean>> getSelectedProperty,
            final StringConverter<TreeItem<T>> converter) {
        return tree -> new CheckBoxTreeCell<T>(getSelectedProperty, converter);
    }

    private final static Map<Integer, String> SORT_NEXT_LABELS;

    static {
        SORT_NEXT_LABELS = new TreeMap<>();
        SORT_NEXT_LABELS.put(-1, "by Time");
        SORT_NEXT_LABELS.put(0, "ascending");
        SORT_NEXT_LABELS.put(1, "descending");
    }

    /**
     * *************************************************************************
     *                                                                         *
     * Constructors * *
     * ************************************************************************
     */
    /**
     * Creates a default {@link CheckBoxTreeCell} that assumes the TreeView is
     * constructed with {@link CheckBoxTreeItem} instances, rather than the
     * default {@link TreeItem}. By using {@link CheckBoxTreeItem}, it will
     * internally manage the selected and indeterminate state of each item in
     * the tree.
     */
    public CustomWeightedMeanCheckBoxTreeCell() {
        // getSelectedProperty as anonymous inner class to deal with situation
        // where the user is using CheckBoxTreeItem instances in their tree
        this(item -> {
            if (item instanceof CheckBoxTreeItem<?>) {
                return ((CheckBoxTreeItem<?>) item).selectedProperty();
            }
            return null;
        });
    }

    /**
     * Creates a {@link CheckBoxTreeCell} for use in a TreeView control via a
     * cell factory. Unlike {@link CheckBoxTreeCell#CheckBoxTreeCell()}, this
     * method does not assume that all TreeItem instances in the TreeView are
     * {@link CheckBoxTreeItem}.
     *
     * <p>
     * To call this method, it is necessary to provide a {@link Callback} that,
     * given an object of type TreeItem<T>, will return an
     * {@code ObservableValue<Boolean>} that represents whether the given item
     * is selected or not. This {@code ObservableValue<Boolean>} will be bound
     * bidirectionally (meaning that the CheckBox in the cell will set/unset
     * this property based on user interactions, and the CheckBox will reflect
     * the state of the {@code ObservableValue<Boolean>}, if it changes
     * externally).
     *
     * <p>
     * If the items are not {@link CheckBoxTreeItem} instances, it becomes the
     * developers responsibility to handle updating the state of parent and
     * children TreeItems. This means that, given a TreeItem, this class will
     * simply toggles the {@code ObservableValue<Boolean>} that is provided, and
     * no more. Of course, this functionality can then be implemented externally
     * by adding observers to the {@code ObservableValue<Boolean>}, and toggling
     * the state of other properties as necessary.
     *
     * @param getSelectedProperty A {@link Callback} that will return an
     * {@code ObservableValue<Boolean>} that represents whether the given item
     * is selected or not.
     */
    public CustomWeightedMeanCheckBoxTreeCell(
            final Callback<TreeItem<T>, ObservableValue<Boolean>> getSelectedProperty) {
        super(getSelectedProperty);
    }

    /**
     * Creates a {@link CheckBoxTreeCell} for use in a TreeView control via a
     * cell factory. Unlike {@link CheckBoxTreeCell#CheckBoxTreeCell()}, this
     * method does not assume that all TreeItem instances in the TreeView are
     * {@link CheckBoxTreeItem}.
     *
     * <p>
     * To call this method, it is necessary to provide a {@link Callback} that,
     * given an object of type TreeItem<T>, will return an
     * {@code ObservableValue<Boolean>} that represents whether the given item
     * is selected or not. This {@code ObservableValue<Boolean>} will be bound
     * bidirectionally (meaning that the CheckBox in the cell will set/unset
     * this property based on user interactions, and the CheckBox will reflect
     * the state of the {@code ObservableValue<Boolean>}, if it changes
     * externally).
     *
     * <p>
     * If the items are not {@link CheckBoxTreeItem} instances, it becomes the
     * developers responsibility to handle updating the state of parent and
     * children TreeItems. This means that, given a TreeItem, this class will
     * simply toggles the {@code ObservableValue<Boolean>} that is provided, and
     * no more. Of course, this functionality can then be implemented externally
     * by adding observers to the {@code ObservableValue<Boolean>}, and toggling
     * the state of other properties as necessary.
     *
     * @param getSelectedProperty A {@link Callback} that will return an
     * {@code ObservableValue<Boolean>} that represents whether the given item
     * is selected or not.
     * @param converter A StringConverter that, give an object of type
     * TreeItem<T>, will return a String that can be used to represent the
     * object visually.
     */
    public CustomWeightedMeanCheckBoxTreeCell(
            final Callback<TreeItem<T>, ObservableValue<Boolean>> getSelectedProperty,
            final StringConverter<TreeItem<T>> converter) {
        this(getSelectedProperty, converter, null);
    }

    private CustomWeightedMeanCheckBoxTreeCell(
            final Callback<TreeItem<T>, ObservableValue<Boolean>> getSelectedProperty,
            final StringConverter<TreeItem<T>> converter,
            final Callback<TreeItem<T>, ObservableValue<Boolean>> getIndeterminateProperty) {
        super(getSelectedProperty, converter);

    }

    /**
     * The purpose is to hide checkbox
     *
     * @param item
     * @param empty
     */
    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (item instanceof SampleNode) {
            if (((SampleNode) item).getSamplePlotWM() != null) {
                // this is single sample
                setGraphic(sampleWeightedMeanToolbox((SampleNode) item, this));
            } else {
                // this is all UNKNOWNS
                setGraphic(null);
                setText("All Samples");
            }
        }
    }

    private HBox sampleWeightedMeanToolbox(SampleNode sample, CustomWeightedMeanCheckBoxTreeCell<T> sampleCheckBox) {
        HBox toolBox = new HBox(10);
        toolBox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: blue;");

        VBox sampleNameTool = new VBox(0);
        Label sampleNameNode = new Label(sample.getNodeName());
        sampleNameNode.getStyleClass().clear();
        sampleNameNode.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
        sampleNameNode.setPrefWidth(50);
        sampleNameNode.setMinWidth(USE_PREF_SIZE);
        sampleNameNode.setPrefHeight(20);
        sampleNameNode.setMinHeight(USE_PREF_SIZE);

        Label dummyLabel = new Label("");
        dummyLabel.setStyle("-fx-font-size: 12px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
        dummyLabel.setPrefWidth(50);
        dummyLabel.setMinWidth(USE_PREF_SIZE);
        dummyLabel.setPrefHeight(20);
        dummyLabel.setMinHeight(USE_PREF_SIZE);

        Spinner<Double> minProbabilitySpinner = new Spinner<>();
        minProbabilitySpinner.setStyle("-fx-font-size: 11px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
        minProbabilitySpinner.setPrefWidth(60);
        minProbabilitySpinner.setMinWidth(USE_PREF_SIZE);
        minProbabilitySpinner.setPrefHeight(23);
        minProbabilitySpinner.setMinHeight(USE_PREF_SIZE);
        SpinnerValueFactory<Double> valueFactory
                = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.10, 1.00, sample.getSpotSummaryDetailsWM().getMinProbabilityWM(), 0.05);
        minProbabilitySpinner.setValueFactory(valueFactory);
        minProbabilitySpinner.valueProperty().addListener((ObservableValue<? extends Double> observable,
                Double oldValue, Double newValue) -> {
            sample.getSpotSummaryDetailsWM().setMinProbabilityWM(newValue);
            minProbabilitySpinner.getValueFactory().setValue(newValue);
        });

        Label minProbLabel = new Label("min. prob: ");
        minProbLabel.setStyle("-fx-font-size: 9px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
        minProbLabel.setPrefWidth(50);
        minProbLabel.setMinWidth(USE_PREF_SIZE);
        minProbLabel.setPrefHeight(20);
        minProbLabel.setMinHeight(USE_PREF_SIZE);

        HBox minProbHBox = new HBox(1);
        minProbHBox.getChildren().addAll(minProbLabel, minProbabilitySpinner);

        sampleNameTool.getChildren().addAll(sampleNameNode, dummyLabel, minProbHBox);

        // buttons for priming selection of spots
        VBox selectionTool = new VBox(0);
        Button chooseAllButton = new Button("All");
        chooseAllButton.setStyle("-fx-font-size: 12px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
        chooseAllButton.setPrefWidth(50);
        chooseAllButton.setMinWidth(USE_PREF_SIZE);
        chooseAllButton.setPrefHeight(20);
        chooseAllButton.setMinHeight(USE_PREF_SIZE);
        chooseAllButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                sample.getSpotSummaryDetailsWM().rejectNone();
                synchronizeCheckBoxesWithWM(sample);
            }
        });

        Button chooseNoneButton = new Button("None");
        chooseNoneButton.setStyle("-fx-font-size: 12px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
        chooseNoneButton.setPrefWidth(50);
        chooseNoneButton.setMinWidth(USE_PREF_SIZE);
        chooseNoneButton.setPrefHeight(20);
        chooseNoneButton.setMinHeight(USE_PREF_SIZE);
        chooseNoneButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                sample.getSpotSummaryDetailsWM().rejectAll();
                synchronizeCheckBoxesWithWM(sample);
            }
        });

        Button chooseSquidButton = new Button("Squid");
        chooseSquidButton.setStyle("-fx-font-size: 12px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
        chooseSquidButton.setPrefWidth(50);
        chooseSquidButton.setMinWidth(USE_PREF_SIZE);
        chooseSquidButton.setPrefHeight(23);
        chooseSquidButton.setMinHeight(USE_PREF_SIZE);
        chooseSquidButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                SpotSummaryDetails spotSummaryDetailsWM = sample.getSpotSummaryDetailsWM();
                SpotGroupProcessor.findCoherentGroupOfSpotsForWeightedMean(
                        squidProject.getTask(), spotSummaryDetailsWM, minProbabilitySpinner.getValue());
                synchronizeCheckBoxesWithWM(sample);
            }
        });

        selectionTool.getChildren().addAll(chooseAllButton, chooseNoneButton, chooseSquidButton);

        VBox ageTool = new VBox(0);
        int currentPreferredViewSortOrder = sample.getSpotSummaryDetailsWM().getPreferredViewSortOrder();
        Button sortAgeButton = new Button("Sort " + SORT_NEXT_LABELS.get(currentPreferredViewSortOrder));
        ComboBox<SampleAgeTypesEnum> ageComboBox = new ComboBox<>();
        sortAgeButton.setStyle("-fx-font-size: 12px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
        sortAgeButton.setPrefWidth(150);
        sortAgeButton.setMinWidth(USE_PREF_SIZE);
        sortAgeButton.setPrefHeight(20);
        sortAgeButton.setMinHeight(USE_PREF_SIZE);
        sortAgeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String selectedAge = ageComboBox.getSelectionModel().getSelectedItem().getExpressionName();
                int savedPreferredViewSortOrder = sample.getSpotSummaryDetailsWM().getPreferredViewSortOrder();
                int preferredViewSortOrder = 1;
                if (savedPreferredViewSortOrder == -1) {
                    preferredViewSortOrder = 0;
                } else if (savedPreferredViewSortOrder == 1) {
                    preferredViewSortOrder = -1;
                }
                sample.getSpotSummaryDetailsWM().setPreferredViewSortOrder(preferredViewSortOrder);
                ((Button) e.getSource()).setText("Sort " + SORT_NEXT_LABELS.get(preferredViewSortOrder));
                sortFractionCheckboxesByValue("AGE", selectedAge, preferredViewSortOrder);
                ((SampleNode) sample).getPlotsController().refreshPlot();
            }
        });

        ageComboBox.setStyle("-fx-font-size: 10px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
        ageComboBox.setPrefWidth(150);
        ageComboBox.setMinWidth(USE_PREF_SIZE);
        ageComboBox.setPrefHeight(20);
        ageComboBox.setMinHeight(USE_PREF_SIZE);
        ageComboBox.setItems(FXCollections.observableArrayList(SampleAgeTypesEnum.values()));
        SampleAgeTypesEnum chosenAge = ((SampleNode) sample).getSpotSummaryDetailsWM().getSelectedSpots().get(0).getCommonLeadSpecsForSpot().getSampleAgeType();
        ageComboBox.getSelectionModel().select(chosenAge);

        ageComboBox.valueProperty()
                .addListener((ObservableValue<? extends SampleAgeTypesEnum> observable, SampleAgeTypesEnum oldValue, SampleAgeTypesEnum newValue) -> {
                    String selectedAge = newValue.getExpressionName();

                    ((Task) squidProject.getTask()).setUnknownGroupSelectedAge(sample.getSpotSummaryDetailsWM().getSelectedSpots(), newValue);
                    ((Task) squidProject.getTask()).evaluateUnknownsWithChangedParameters(sample.getSpotSummaryDetailsWM().getSelectedSpots());

                    boolean[] savedRejectedIndices = sample.getSpotSummaryDetailsWM().getRejectedIndices();
                    int savedPreferredViewSortOrder = sample.getSpotSummaryDetailsWM().getPreferredViewSortOrder();
                    double savedMinProbabilityWM = sample.getSpotSummaryDetailsWM().getMinProbabilityWM();

                    SpotSummaryDetails spotSummaryDetailsWM
                            = ((Task) squidProject.getTask())
                                    .evaluateSelectedAgeWeightedMeanForUnknownGroup(sample.getNodeName(), sample.getSpotSummaryDetailsWM().getSelectedSpots());
                    spotSummaryDetailsWM.setManualRejectionEnabled(true);
                    spotSummaryDetailsWM.setRejectedIndices(savedRejectedIndices);
                    spotSummaryDetailsWM.setPreferredViewSortOrder(savedPreferredViewSortOrder);
                    spotSummaryDetailsWM.setMinProbabilityWM(savedMinProbabilityWM);

                    PlotDisplayInterface myPlot = ((SampleNode) sample).getSamplePlotWM();
                    ((WeightedMeanPlot) myPlot).setSpotSummaryDetails(spotSummaryDetailsWM);
                    ((WeightedMeanPlot) myPlot).setAgeOrValueLookupString(selectedAge);
                    sortFractionCheckboxesByValue("AGE", selectedAge, savedPreferredViewSortOrder);
                    ((SampleNode) sample).getPlotsController().refreshPlot();
                });

        Button chooseAgeButton = new Button("Choose");
        chooseAgeButton.setStyle("-fx-font-size: 12px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
        chooseAgeButton.setPrefWidth(150);
        chooseAgeButton.setMinWidth(USE_PREF_SIZE);
        chooseAgeButton.setPrefHeight(23);
        chooseAgeButton.setMinHeight(USE_PREF_SIZE);
        chooseAgeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                sortFractionCheckboxesByValue(
                        "AGE",
                        ageComboBox.getSelectionModel().getSelectedItem().getExpressionName(),
                        sample.getSpotSummaryDetailsWM().getPreferredViewSortOrder());
                ((SampleNode) sample).getPlotsController().refreshPlot();
            }
        });

        ageTool.getChildren().addAll(ageComboBox, sortAgeButton, chooseAgeButton);

        // ***************************************************************************** RATIOS
        VBox ratioTool = new VBox(0);
        currentPreferredViewSortOrder = sample.getSpotSummaryDetailsWM().getPreferredViewSortOrder();
        Button sortRatioButton = new Button("Sort " + SORT_NEXT_LABELS.get(currentPreferredViewSortOrder));
        ComboBox<String> ratioComboBox = new ComboBox<>();
        sortRatioButton.setStyle("-fx-font-size: 12px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
        sortRatioButton.setPrefWidth(100);
        sortRatioButton.setMinWidth(USE_PREF_SIZE);
        sortRatioButton.setPrefHeight(20);
        sortRatioButton.setMinHeight(USE_PREF_SIZE);
        sortRatioButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String selectedRatioName = ratioComboBox.getSelectionModel().getSelectedItem();
                int savedPreferredViewSortOrder = sample.getSpotSummaryDetailsWM().getPreferredViewSortOrder();
                int preferredViewSortOrder = 1;
                if (savedPreferredViewSortOrder == -1) {
                    preferredViewSortOrder = 0;
                } else if (savedPreferredViewSortOrder == 1) {
                    preferredViewSortOrder = -1;
                }
                sample.getSpotSummaryDetailsWM().setPreferredViewSortOrder(preferredViewSortOrder);
                ((Button) e.getSource()).setText("Sort " + SORT_NEXT_LABELS.get(preferredViewSortOrder));
                sortFractionCheckboxesByValue("RATIO", selectedRatioName, preferredViewSortOrder);
                ((SampleNode) sample).getPlotsController().refreshPlot();
            }
        });

        ratioComboBox.setStyle("-fx-font-size: 10px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
        ratioComboBox.setPrefWidth(100);
        ratioComboBox.setMinWidth(USE_PREF_SIZE);
        ratioComboBox.setPrefHeight(20);
        ratioComboBox.setMinHeight(USE_PREF_SIZE);
        ratioComboBox.setItems(FXCollections.observableArrayList(squidProject.getTask().getRatioNames()));
        String chosenRatioName = ((SampleNode) sample).getSpotSummaryDetailsWM().getSelectedExpressionName();
        ratioComboBox.getSelectionModel().select(chosenRatioName);
        ratioComboBox.valueProperty()
                .addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    String selectedRatio = newValue;
                    sample.getSpotSummaryDetailsWM().setSelectedExpressionName(selectedRatio);
                    sortFractionCheckboxesByValue("RATIO", selectedRatio, sample.getSpotSummaryDetailsWM().getPreferredViewSortOrder());
                    ((SampleNode) sample).getPlotsController().refreshPlot();
                });

        Button chooseRatioButton = new Button("Choose");
        chooseRatioButton.setStyle("-fx-font-size: 12px;-fx-font-weight: bold; -fx-padding: 0 0 0 0;");
        chooseRatioButton.setPrefWidth(100);
        chooseRatioButton.setMinWidth(USE_PREF_SIZE);
        chooseRatioButton.setPrefHeight(23);
        chooseRatioButton.setMinHeight(USE_PREF_SIZE);
        chooseRatioButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                sortFractionCheckboxesByValue(
                        "RATIO",
                        ratioComboBox.getSelectionModel().getSelectedItem(),
                        sample.getSpotSummaryDetailsWM().getPreferredViewSortOrder());
                ((SampleNode) sample).getPlotsController().refreshPlot();
            }
        });

        ratioTool.getChildren().addAll(ratioComboBox, sortRatioButton, chooseRatioButton);

        toolBox.getChildren().addAll(sampleNameTool, selectionTool, ageTool, ratioTool);

        return toolBox;
    }

    private void sortFractionCheckboxesByValue(String flavor, String selectedFieldName, int savedPreferredViewSortOrder) {
        FXCollections.sort(this.getTreeItem().getChildren(), (TreeItem node1, TreeItem node2) -> {
            // original aquire time order  
            int retVal = 0;
            if (savedPreferredViewSortOrder == 0) {
                long acquireTime1 = ((SampleTreeNodeInterface) node1.getValue()).getShrimpFraction().getDateTimeMilliseconds();
                long acquireTime2 = ((SampleTreeNodeInterface) node2.getValue()).getShrimpFraction().getDateTimeMilliseconds();
                retVal = Long.compare(acquireTime1, acquireTime2);
            } else {
                double valueFromNode1 = 0.0;
                double valueFromNode2 = 0.0;
                switch (flavor) {
                    case "AGE":
                        valueFromNode1 = ((SampleTreeNodeInterface) node1.getValue()).getShrimpFraction()
                                .getTaskExpressionsEvaluationsPerSpotByField(selectedFieldName)[0][0];
                        valueFromNode2 = ((SampleTreeNodeInterface) node2.getValue()).getShrimpFraction()
                                .getTaskExpressionsEvaluationsPerSpotByField(selectedFieldName)[0][0];
                        break;
                    case "RATIO":
                        double[][] resultsFromNode1
                                = Arrays.stream(((SampleTreeNodeInterface) node1.getValue()).getShrimpFraction()
                                        .getIsotopicRatioValuesByStringName(selectedFieldName)).toArray(double[][]::new);
                        valueFromNode1 = resultsFromNode1[0][0];
                        double[][] resultsFromNode2
                                = Arrays.stream(((SampleTreeNodeInterface) node2.getValue()).getShrimpFraction()
                                        .getIsotopicRatioValuesByStringName(selectedFieldName)).toArray(double[][]::new);
                        valueFromNode2 = resultsFromNode2[0][0];
                        break;
                }

                if (savedPreferredViewSortOrder == 1) {
                    retVal = Double.compare(valueFromNode1, valueFromNode2);
                }
                if (savedPreferredViewSortOrder == -1) {
                    retVal = Double.compare(valueFromNode2, valueFromNode1);
                }
            }
            return retVal;
        });
    }

    private void synchronizeCheckBoxesWithWM(SampleNode sample) {
        for (TreeItem<T> spotCheckBox : this.getTreeItem().getChildren()) {
            int indexOfSpot = ((WeightedMeanSpotNode) spotCheckBox.getValue()).getIndexOfSpot();
            ((CheckBoxTreeItem<T>) spotCheckBox).setSelected(
                    !sample.getSpotSummaryDetailsWM().getRejectedIndices()[indexOfSpot]);
        }

        sample.getPlotsController().refreshPlot();
    }
}
