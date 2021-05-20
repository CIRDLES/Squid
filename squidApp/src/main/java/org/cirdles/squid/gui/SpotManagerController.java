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
package org.cirdles.squid.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.cirdles.squid.dialogs.SquidMessageDialog;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.gui.parameters.ParametersLauncher;
import org.cirdles.squid.gui.parameters.ParametersManagerGUIController;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.prawn.PrawnFile.Run;
import org.cirdles.squid.projects.SquidProject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

import static javafx.scene.paint.Color.BLACK;
import static org.cirdles.squid.constants.Squid3Constants.REF_238U235U_DEFAULT;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.*;
import static org.cirdles.squid.gui.constants.Squid3GuiConstants.STYLE_MANAGER_TITLE;
import static org.cirdles.squid.parameters.util.RadDates.*;
import static org.cirdles.squid.parameters.util.ReferenceMaterialEnum.r238_235s;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class SpotManagerController implements Initializable {

    private static ObservableList<PrawnFile.Run> shrimpRuns;
    private static ObservableList<PrawnFile.Run> shrimpRunsRefMat;
    private static ObservableList<PrawnFile.Run> shrimpRunsConcRefMat;

    private final RunsViewModel runsModel = new RunsViewModel();

    @FXML
    private HBox spotManagerPane;
    @FXML
    private ListView<PrawnFile.Run> shrimpFractionList;
    @FXML
    private ListView<PrawnFile.Run> shrimpRefMatList;
    @FXML
    private ListView<PrawnFile.Run> shrimpConcentrationRefMatList;

    @FXML
    private TextField selectedSpotNameText;

    @FXML
    private Label headerLabel;
    @FXML
    private TextField filterSpotNameText;
    @FXML
    private Label spotsShownLabel;

    @FXML
    private Button setFilteredSpotsAsRefMatButton;
    @FXML
    private Label headerLabelRefMat;
    @FXML
    private Button saveSpotNameButton;

    @FXML
    private Label rmFilterLabel;
    @FXML
    private Label rmCountLabel;

    @FXML
    private Button setFilteredSpotsAsConcRefMatButton;
    @FXML
    private Label headerLabelConcRefMat;
    @FXML
    private Label concrmFilterLabel;
    @FXML
    private Label concrmCountLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private ComboBox<ParametersModel> refMatModelComboBox;
    @FXML
    private Label pbb206U238AgeLabel;
    @FXML
    private Label pb207Pb206AgeLabel;
    @FXML
    private Label pb208Th232AgeLabel;

    @FXML
    private ComboBox<ParametersModel> concRefMatModelComboBox;
    @FXML
    private Label uPpmLabel;
    @FXML
    private Label thPpmLabel;

    @FXML
    private ComboBox<String> sampleNameComboBox;

    private List<PrawnFile.Run> selectedRuns = new ArrayList<>();
    private MenuItem spotContextRemoveSpotsMenuItem;
    private Menu spotRestoreMenu;
    private Menu prawnFileSplitMenu;
    private MenuItem splitRunsOriginalMenuItem;
    private MenuItem splitRunsEditedMenuItem;
    @FXML
    private HBox refMatChooserHBox;
    @FXML
    private SplitPane refMatSplitPane;
    @FXML
    private Label u238u235NatAbunLabel;
    @FXML
    private Label defaultValueLabel;

    // save style from age labels
    private String savedAgeLabelStyle;
    private String savedAgeLabelStyleWithRed;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        savedAgeLabelStyle = pbb206U238AgeLabel.getStyle();
        savedAgeLabelStyleWithRed = savedAgeLabelStyle + " -fx-text-fill: red;";

        if (!squidProject.isTypeGeochron()) {
            refMatChooserHBox.setVisible(false);
        }
        setUpShrimpFractionListHeaders();
        saveSpotNameButton.setDisable(true);
        setFilteredSpotsAsRefMatButton.setDisable(true);
        setFilteredSpotsAsConcRefMatButton.setDisable(true);

        titleLabel.setStyle(STYLE_MANAGER_TITLE);

        setUpSampleNamesComboBox();
        setUpParametersModelsComboBoxes();

        try {
            setUpDataFile(false);
        } catch (SquidException squidException) {
        }

        alertForZeroNaturalUranium();
    }

    private void setUpSampleNamesComboBox() {
        String allSamples = "All Samples";
        String[] samples = (String[]) squidProject.getFiltersForUnknownNames().keySet().toArray(new String[0]);
        List<String> samplesList = new ArrayList<String>(Arrays.asList(samples));
        samplesList.add(0, allSamples);

        sampleNameComboBox.setItems(FXCollections.observableArrayList(samplesList));
        sampleNameComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                ListCell<String> cell = new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                    }
                };
                return cell;
            }
        });

        sampleNameComboBox.valueProperty()
                .addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        if ((newValue != null) && (newValue.length() > 0)) {
                            filterSpotNameText.setText(newValue.startsWith(allSamples) ? "" : newValue);
                            filterRuns(filterSpotNameText.getText().toUpperCase(Locale.ENGLISH).trim());
                        }
                    }
                });

        sampleNameComboBox.getSelectionModel().selectFirst();
    }

    private void setUpDataFile(boolean doReprocess) throws SquidException {

        if (doReprocess) {
            // May 2021 fixes issue #618
            squidProject.getTask().setChanged(true);
            squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(true);
        }

        shrimpRuns = FXCollections.observableArrayList(squidProject.getPrawnFileRuns());

        setUpShrimpFractionList();
        saveSpotNameButton.setDisable(false);
        setFilteredSpotsAsRefMatButton.setDisable(false);
        setFilteredSpotsAsConcRefMatButton.setDisable(false);

        // filter runs to populate ref mat list 
        filterRuns(squidProject.getFilterForRefMatSpotNames());
        updateReferenceMaterialsList(false);

        // filter runs to populate concentration ref mat list 
        filterRuns(squidProject.getFilterForConcRefMatSpotNames());
        updateConcReferenceMaterialsList(false);

        // restore spot list to full selected population
        filterRuns(sampleNameComboBox.getSelectionModel().getSelectedIndex() >= 1
                ? sampleNameComboBox.getSelectionModel().getSelectedItem() : "");
    }

    private void setUpShrimpFractionList()
            throws SquidException {

        shrimpFractionList.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);
        shrimpFractionList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        shrimpFractionList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PrawnFile.Run>() {
            @Override
            public void changed(ObservableValue<? extends PrawnFile.Run> observable, PrawnFile.Run oldValue, PrawnFile.Run newValue) {
                try {
                    selectedSpotNameText.setText(newValue.getPar().get(0).getValue());
                    saveSpotNameButton.setUserData(newValue);
                } catch (Exception e) {
                    selectedSpotNameText.setText("");
                }
            }
        });

        shrimpFractionList.setCellFactory(
                (lv)
                -> new RunsViewModel.ShrimpFractionListCell()
        );

        runsModel.addRunsList(shrimpRuns);
        spotsShownLabel.setText(runsModel.showFilteredOverAllCount());

        shrimpFractionList.itemsProperty().bind(runsModel.viewableShrimpRunsProperty());

        shrimpFractionList.setContextMenu(createAllSpotsViewContextMenu());
        shrimpFractionList.setOnMouseClicked(new MouseClickEventHandler());

        // display of selected reference materials
        shrimpRefMatList.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);

        shrimpRefMatList.setCellFactory(
                (lv)
                -> new RunsViewModel.ShrimpFractionAbbreviatedListCell()
        );

        shrimpRefMatList.setContextMenu(createRefMatSpotsViewContextMenu());

        // display of selected concentration reference materials
        shrimpConcentrationRefMatList.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);

        shrimpConcentrationRefMatList.setCellFactory(
                (lv)
                -> new RunsViewModel.ShrimpFractionAbbreviatedListCell()
        );

        shrimpConcentrationRefMatList.setContextMenu(createConcRefMatSpotsViewContextMenu());
    }

    private ContextMenu createAllSpotsViewContextMenu()
            throws SquidException {
        ContextMenu contextMenu = new ContextMenu();
        spotContextRemoveSpotsMenuItem = new MenuItem();
        spotContextRemoveSpotsMenuItem.setOnAction((evt) -> {
            squidProject.removeRunsFromPrawnFile(selectedRuns);

            try {
                setUpDataFile(true);
            } catch (SquidException squidException) {

            }
            squidProject.generatePrefixTreeFromSpotNames();
            SquidProject.setProjectChanged(true);
        });
        contextMenu.getItems().add(spotContextRemoveSpotsMenuItem);

        spotRestoreMenu = new Menu("Restore spots ...");

        prawnFileSplitMenu = new Menu("Split Prawn file ...");
        contextMenu.getItems().add(prawnFileSplitMenu);

        splitRunsOriginalMenuItem = new MenuItem("Split at this run, using original unedited, no duplicates noted.");
        splitRunsOriginalMenuItem.setOnAction((evt) -> {
            PrawnFile.Run selectedRun = shrimpFractionList.getSelectionModel().getSelectedItem();
            if (selectedRun != null) {
                try {
                    String[] splitNames = squidProject.splitPrawnFileAtRun(selectedRun, true);
                    SquidMessageDialog.showInfoDialog(
                            "Two Prawn XML files have been written:\n\n"
                            + "\t" + splitNames[0] + "\n"
                            + "\t" + splitNames[1] + "\n\n"
                            + "Create a new Squid3 Project with each of these Prawn XML files.",
                            primaryStageWindow
                    );
                } catch (SquidException squidException) {
                    String message = squidException.getMessage();
                    if (message == null) {
                        message = squidException.getCause().getMessage();
                    }

                    SquidMessageDialog.showWarningDialog(
                            "The Project's Prawn File cannot be found ... please use PrawnFile menu to save it:\n\n"
                            + message,
                            primaryStageWindow);
                }
            }
        });
        prawnFileSplitMenu.getItems().add(splitRunsOriginalMenuItem);

        splitRunsEditedMenuItem = new MenuItem("Split at this run, using this edited spots, duplicates noted.");
        splitRunsEditedMenuItem.setOnAction((evt) -> {
            PrawnFile.Run selectedRun = shrimpFractionList.getSelectionModel().getSelectedItem();
            if (selectedRun != null) {
                try {
                    String[] splitNames = squidProject.splitPrawnFileAtRun(selectedRun, false);
                    SquidMessageDialog.showInfoDialog(
                            "Two Prawn XML files have been written:\n\n"
                            + "\t" + splitNames[0] + "\n"
                            + "\t" + splitNames[1] + "\n\n"
                            + "Create a new Squid3 Project with each of these Prawn XML files.",
                            primaryStageWindow
                    );
                } catch (SquidException squidException) {
                    String message = squidException.getMessage();
                    if (message == null) {
                        message = squidException.getCause().getMessage();
                    }

                    SquidMessageDialog.showWarningDialog(
                            "The Project's Prawn File cannot be found ... please use PrawnFile menu to save it:\n\n"
                            + message,
                            primaryStageWindow);
                }
            }
        });
        prawnFileSplitMenu.getItems().add(splitRunsEditedMenuItem);

        return contextMenu;
    }

    private class MouseClickEventHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getButton().compareTo(MouseButton.SECONDARY) == 0) {
                selectedRuns = shrimpFractionList.getSelectionModel().getSelectedItems();

                if (selectedRuns.size() > 1) {
                    spotContextRemoveSpotsMenuItem.setText("Remove selected set of " + selectedRuns.size() + " spots.");
                } else {
                    spotContextRemoveSpotsMenuItem.setText("Remove selected spot.");
                }

                prawnFileSplitMenu.setDisable(selectedRuns.size() > 1);

                // customize spotContextMenu
                if (!squidProject.getRemovedRuns().isEmpty()) {
                    shrimpFractionList.getContextMenu().getItems().add(0, spotRestoreMenu);
                    spotRestoreMenu.getItems().clear();
                    MenuItem restoreAllSpotMenuItem = new MenuItem("Restore ALL");
                    spotRestoreMenu.getItems().add(restoreAllSpotMenuItem);
                    restoreAllSpotMenuItem.setOnAction((evt) -> {
                        squidProject.restoreAllRunsToPrawnFile();

                        try {
                            setUpDataFile(true);
                        } catch (SquidException squidException) {
                            //TODO: need message here
                        }
                        squidProject.generatePrefixTreeFromSpotNames();
                        SquidProject.setProjectChanged(true);
                    });
                    // list all removed runs
                    for (Run run : squidProject.getRemovedRuns()) {
                        MenuItem restoreSpotMenuItem = new MenuItem(run.getPar().get(0).getValue());
                        spotRestoreMenu.getItems().add(restoreSpotMenuItem);
                        restoreSpotMenuItem.setOnAction((evt) -> {
                            squidProject.restoreRunToPrawnFile(run);
                            try {
                                setUpDataFile(true);
                            } catch (SquidException squidException) {
                                //TODO: need message here
                            }
                            squidProject.generatePrefixTreeFromSpotNames();
                            SquidProject.setProjectChanged(true);
                        });
                    }
                } else {
                    shrimpFractionList.getContextMenu().getItems().remove(spotRestoreMenu);
                }
            }
        }
    }

    private ContextMenu createRefMatSpotsViewContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Clear list.");
        menuItem.setOnAction((evt) -> {
            squidProject.updateFilterForRefMatSpotNames("");
            squidProject.setReferenceMaterialModel(new ReferenceMaterialModel());
            updateReferenceMaterialsList(true);
        });
        contextMenu.getItems().add(menuItem);
        return contextMenu;
    }

    private ContextMenu createConcRefMatSpotsViewContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Clear list.");
        menuItem.setOnAction((evt) -> {
            squidProject.updateFilterForConcRefMatSpotNames("");
            squidProject.setConcentrationReferenceMaterialModel(new ReferenceMaterialModel());
            updateConcReferenceMaterialsList(true);
        });
        contextMenu.getItems().add(menuItem);
        return contextMenu;
    }

    private void setUpShrimpFractionListHeaders() {

        headerLabel.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);
        headerLabel.setText(
                String.format("%1$-" + 21 + "s", "Spot Name")
                + String.format("%1$-" + 12 + "s", "Date")
                + String.format("%1$-" + 12 + "s", "Time")
                + String.format("%1$-" + 6 + "s", "Peaks")
                + String.format("%1$-" + 6 + "s", "Scans"));

        headerLabelRefMat.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);
        headerLabelRefMat.setText(
                String.format("%1$-" + 21 + "s", "Ref Mat Name")
                + String.format("%1$-" + 12 + "s", "Date")
                + String.format("%1$-" + 12 + "s", "Time"));

        headerLabelConcRefMat.setStyle(SquidUI.SPOT_LIST_CSS_STYLE_SPECS);
        headerLabelConcRefMat.setText(
                String.format("%1$-" + 21 + "s", "Ref Mat Name")
                + String.format("%1$-" + 12 + "s", "Date")
                + String.format("%1$-" + 12 + "s", "Time"));
    }

    private void alertForZeroNaturalUranium() {
        // alert if zero
        if (((ReferenceMaterialModel) refMatModelComboBox.valueProperty().getValue()).getDatumByName(r238_235s.getName())
                .getValue().compareTo(BigDecimal.ZERO) == 0) {
            u238u235NatAbunLabel.setText(REF_238U235U_DEFAULT + "");
            defaultValueLabel.setVisible(true);
            u238u235NatAbunLabel.setStyle(u238u235NatAbunLabel.getStyle() + " -fx-text-fill: red;");
        } else {
            u238u235NatAbunLabel.setTextFill(BLACK);
            defaultValueLabel.setVisible(false);
        }
    }

    private void setUpParametersModelsComboBoxes() {
        // ReferenceMaterials
        refMatModelComboBox.setConverter(new ProjectManagerController.ParameterModelStringConverter());
        refMatModelComboBox.setItems(FXCollections.observableArrayList(squidLabData.getReferenceMaterialsWithNonZeroDate()));
        refMatModelComboBox.setConverter(new StringConverter<ParametersModel>() {
            @Override
            public String toString(ParametersModel model) {
                if (model == null) {
                    return null;
                } else {
                    return model.getModelNameWithVersion() + (model.isEditable() ? "" : " <Built-in>");
                }
            }

            @Override
            public ParametersModel fromString(String userId) {
                return null;
            }
        });

        updateViewRM();

        refMatModelComboBox.valueProperty()
                .addListener((ObservableValue<? extends ParametersModel> observable, ParametersModel oldValue, ParametersModel newValue) -> {
                    if ((oldValue != null) && (newValue != null) && (newValue.compareTo(oldValue) != 0)) {
                        squidProject.setReferenceMaterialModel(newValue);
                        squidProject.getTask().setChanged(true);
                        squidProject.getTask().refreshParametersFromModels(false, false, true);
                        
                        alertForZeroNaturalUranium();
                    }

                    ParametersModel curValue = (newValue != null) ? newValue : oldValue;

                    String[] audit = ((ReferenceMaterialModel) curValue).auditAndTempUpdateRefMatModel().split("Audit:");
                    String[] flags = audit[0].split(";");

                    if (flags[0].contains("F")) {
                        SquidMessageDialog.showInfoDialog(
                                "This reference material model is missing meaningful age data. \n"
                                + "Please choose another model.\n\n",
                                primaryStageWindow);
                    } else {
                        if (audit[0].contains("1")) {
                            SquidMessageDialog.showInfoDialog(
                                    "This reference material model is missing key age(s), so Squid3 is \n"
                                    + "temporarily substituting values (shown in red) and refreshing as follows:\n\n"
                                    + audit[1],
                                    primaryStageWindow);
                        }
                    }

                    BigDecimal age206_238rModel = ((ReferenceMaterialModel) curValue).getDateByName(age206_238r.getName()).getValue();
                    BigDecimal age207_206rModel = ((ReferenceMaterialModel) curValue).getDateByName(age207_206r.getName()).getValue();
                    BigDecimal age208_232rModel = ((ReferenceMaterialModel) curValue).getDateByName(age208_232r.getName()).getValue();

                    pbb206U238AgeLabel.setText(age206_238rModel.movePointLeft(6).setScale(3, RoundingMode.HALF_UP).toString());
                    pbb206U238AgeLabel.setStyle((flags[0].equals("1") ? savedAgeLabelStyleWithRed : savedAgeLabelStyle));

                    pb207Pb206AgeLabel.setText(age207_206rModel.movePointLeft(6).setScale(3, RoundingMode.HALF_UP).toString());
                    pb207Pb206AgeLabel.setStyle((flags[1].equals("1") ? savedAgeLabelStyleWithRed : savedAgeLabelStyle));

                    pb208Th232AgeLabel.setText(age208_232rModel.movePointLeft(6).setScale(3, RoundingMode.HALF_UP).toString());
                    pb208Th232AgeLabel.setStyle((flags[2].equals("1") ? savedAgeLabelStyleWithRed : savedAgeLabelStyle));

                    u238u235NatAbunLabel.setText(
                            ((ReferenceMaterialModel) curValue).getDatumByName(r238_235s.getName())
                                    .getValue().setScale(3, RoundingMode.HALF_UP).toString());

                    
                });

        // ConcentrationReferenceMaterials
        concRefMatModelComboBox.setConverter(new ProjectManagerController.ParameterModelStringConverter());
        concRefMatModelComboBox.setItems(FXCollections.observableArrayList(squidLabData.getReferenceMaterialsWithNonZeroConcentrations()));
        concRefMatModelComboBox.setConverter(new StringConverter<ParametersModel>() {
            @Override
            public String toString(ParametersModel model) {
                if (model == null) {
                    return null;
                } else {
                    return model.getModelNameWithVersion() + (model.isEditable() ? "" : " <Built-in>");
                }
            }

            @Override
            public ParametersModel fromString(String userId) {
                return null;
            }
        });
        updateViewCM();

        concRefMatModelComboBox.valueProperty()
                .addListener((ObservableValue<? extends ParametersModel> observable, ParametersModel oldValue, ParametersModel newValue) -> {
                    if ((oldValue != null) && (newValue != null) && (newValue.compareTo(oldValue) != 0)) {
                        squidProject.setConcentrationReferenceMaterialModel(newValue);
                        squidProject.getTask().setChanged(true);
                        squidProject.getTask().refreshParametersFromModels(false, false, true);
                    }

                    ParametersModel curValue = (newValue != null) ? newValue : oldValue;
                    uPpmLabel.setText(
                            ((ReferenceMaterialModel) curValue).getConcentrationByName("concU")
                                    .getValue().setScale(3, RoundingMode.HALF_UP).toString());
                    thPpmLabel.setText(
                            ((ReferenceMaterialModel) curValue).getConcentrationByName("concTh")
                                    .getValue().setScale(3, RoundingMode.HALF_UP).toString());
                });
    }

    @FXML
    private void filterSpotNameKeyReleased(KeyEvent event) {
        filterRuns(filterSpotNameText.getText().toUpperCase(Locale.ENGLISH).trim());
        if ((sampleNameComboBox.getSelectionModel().getSelectedItem() != null)
                && (!filterSpotNameText.getText().startsWith(sampleNameComboBox.getSelectionModel().getSelectedItem()))) {
            try {
                sampleNameComboBox.getSelectionModel().clearSelection();
            } catch (Exception e) {
            }
        }
    }

    private void filterRuns(String filterString) {
        Predicate<PrawnFile.Run> filter = new RunsViewModel.SpotNameMatcher(filterString);
        runsModel.filterProperty().set(filter);
        spotsShownLabel.setText(runsModel.showFilteredOverAllCount());
    }

    @FXML
    private void setFilteredSpotsToRefMatAction(ActionEvent event) {
        squidProject.updateFilterForRefMatSpotNames(
                filterSpotNameText.getText().toUpperCase(Locale.ENGLISH).trim());
        updateReferenceMaterialsList(true);
    }

    @FXML
    private void setFilteredSpotsToConcRefMatAction(ActionEvent event) {
        squidProject.updateFilterForConcRefMatSpotNames(
                filterSpotNameText.getText().toUpperCase(Locale.ENGLISH).trim());
        updateConcReferenceMaterialsList(true);
    }

    private void updateReferenceMaterialsList(boolean updateTaskStatus) {
        String filter = squidProject.getFilterForRefMatSpotNames();
        // initialize list
        shrimpRunsRefMat = runsModel.getViewableShrimpRuns();
        if (filter.length() == 0) {
            // prevent populating ref mat list with no filter
            shrimpRunsRefMat.clear();
        } else {
            shrimpRunsRefMat = runsModel.getViewableShrimpRuns();
        }
        shrimpRefMatList.setItems(shrimpRunsRefMat);
        rmFilterLabel.setText(
                squidProject.getFilterForRefMatSpotNames().length() > 0
                ? squidProject.getFilterForRefMatSpotNames()
                : "NO FILTER");
        rmCountLabel.setText(String.valueOf(shrimpRunsRefMat.size()));

        if (updateTaskStatus) {
            squidProject.getTask().setChanged(true);
            squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(false);
        }

        updateViewRM();

    }

    private void updateViewRM() {
        refMatModelComboBox.getSelectionModel().clearSelection();
        refMatModelComboBox.getSelectionModel().select(squidProject.getReferenceMaterialModel());
        refMatModelComboBox.setDisable(squidProject.getFilterForRefMatSpotNames().length() == 0);
    }

    private void updateViewCM() {
        concRefMatModelComboBox.getSelectionModel().clearSelection();
        concRefMatModelComboBox.getSelectionModel().select(squidProject.getConcentrationReferenceMaterialModel());
        concRefMatModelComboBox.setDisable(squidProject.getFilterForConcRefMatSpotNames().length() == 0);;
    }

    private void updateConcReferenceMaterialsList(boolean updateTaskStatus) {
        String filter = squidProject.getFilterForConcRefMatSpotNames();
        // initialize list
        shrimpRunsConcRefMat = runsModel.getViewableShrimpRuns();
        if (filter.length() == 0) {
            // prevent populating ref mat list with no filter
            shrimpRunsConcRefMat.clear();
        } else {
            shrimpRunsConcRefMat = runsModel.getViewableShrimpRuns();
        }
        shrimpConcentrationRefMatList.setItems(shrimpRunsConcRefMat);
        concrmFilterLabel.setText(
                squidProject.getFilterForConcRefMatSpotNames().length() > 0
                ? squidProject.getFilterForConcRefMatSpotNames()
                : "NO FILTER");
        concrmCountLabel.setText(String.valueOf(shrimpRunsConcRefMat.size()));

        if (updateTaskStatus) {
            squidProject.getTask().setChanged(true);
            squidProject.getTask().setupSquidSessionSpecsAndReduceAndReport(false);
        }

        updateViewCM();
    }

    @FXML
    private void saveSpotNameAction(ActionEvent event) {
        if (saveSpotNameButton.getUserData() != null) {
            ((PrawnFile.Run) saveSpotNameButton.getUserData()).getPar().get(0).setValue(selectedSpotNameText.getText().trim());
            squidProject.processPrawnSessionForDuplicateSpotNames();
            squidProject.generatePrefixTreeFromSpotNames();
            shrimpFractionList.refresh();
            shrimpRefMatList.refresh();
            shrimpConcentrationRefMatList.refresh();

            // refresh textbox in case "DUP" is removed or created
            selectedSpotNameText.setText(((PrawnFile.Run) saveSpotNameButton.getUserData()).getPar().get(0).getValue());

            squidProject.getTask().setChanged(true);
            squidProject.getTask().setPrawnChanged(true);
        }
    }

    @FXML
    private void viewRMmodelButton(ActionEvent event) {
        ParametersManagerGUIController.selectedReferenceMaterialModel = squidProject.getReferenceMaterialModel();
        parametersLauncher.launchParametersManager(ParametersLauncher.ParametersTab.refMat);
//        squidProject.getTask().refreshParametersFromModels(false, false, true);
//        updateViewRM();
    }

    @FXML
    private void viewCMmodelButton(ActionEvent event) {
        ParametersManagerGUIController.selectedReferenceMaterialModel = squidProject.getConcentrationReferenceMaterialModel();
        parametersLauncher.launchParametersManager(ParametersLauncher.ParametersTab.refMat);
//        squidProject.getTask().refreshParametersFromModels(false, false, true);
//        updateViewCM();
    }

    @FXML
    private void refreshRMmodelButton(ActionEvent event) {
        squidProject.getTask().refreshParametersFromModels(false, false, true);
//        updateViewRM();
//        updateViewCM();
    }
}