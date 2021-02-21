/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.squidReportTable;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.cirdles.squid.dialogs.SquidMessageDialog;
import org.cirdles.squid.gui.SquidUI;
import org.cirdles.squid.gui.utilities.fileUtilities.FileHandler;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategory;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategoryInterface;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumn;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumnInterface;
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTable;
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTableInterface;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.functions.ShrimpSpeciesNodeFunction;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.operations.Value;
import org.cirdles.squid.tasks.expressions.spots.SpotFieldNode;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForSummary;
import org.cirdles.squid.utilities.IntuitiveStringComparator;
import org.cirdles.squid.utilities.fileUtilities.ProjectFileUtilities;
import org.cirdles.squid.utilities.fileUtilities.FileValidator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

import javafx.scene.input.DataFormat;

import static org.cirdles.squid.constants.Squid3Constants.ABS_UNCERTAINTY_DIRECTIVE;
import static org.cirdles.squid.constants.Squid3Constants.URL_STRING_FOR_SQUIDREPORTTABLE_XML_SCHEMA_LOCAL;
import org.cirdles.squid.constants.Squid3Constants.SpotTypes;
import static org.cirdles.squid.constants.Squid3Constants.XML_HEADER_FOR_SQUIDREPORTTABLE_FILES_USING_LOCAL_SCHEMA;
import org.cirdles.squid.exceptions.SquidException;
import static org.cirdles.squid.gui.SquidUI.*;
import static org.cirdles.squid.gui.SquidUIController.*;
import static org.cirdles.squid.squidReports.squidReportTables.SquidReportTable.NAME_OF_WEIGHTEDMEAN_PLOT_SORT_REPORT;
import static org.cirdles.squid.utilities.conversionUtilities.CloningUtilities.clone2dArray;
import static org.cirdles.squid.utilities.conversionUtilities.RoundingUtilities.squid3RoundedToSize;

/**
 * FXML Controller class
 *
 * @author ryanb
 */
public class SquidReportSettingsController implements Initializable {

    private static boolean showContextMenu = true;

    @FXML
    public ChoiceBox<SquidReportTableInterface> reportTableCB;
    @FXML
    public Button viewButton;
    @FXML
    public ListView<String> ratioExpressionsListView;
    @FXML
    public ListView<String> isotopesExpressionsListView;
    @FXML
    public ListView<String> spotMetaDataExpressionsListView;
    @FXML
    public ChoiceBox<String> spotsChoiceBox;
    @FXML
    public ToggleGroup refMatUnknownsToggleGroup;
    @FXML
    public RadioButton unknownsRadioButton;
    @FXML
    public RadioButton refMatRadioButton;
    @FXML
    public Button exportCSVButton;
    @FXML
    public HBox settingsAndSpotsCB;
    @FXML
    private SplitPane mainPane;
    @FXML
    private Accordion expressionsAccordion;
    @FXML
    private TitledPane customExpressionsTitledPane;
    @FXML
    private ListView<Expression> customExpressionsListView;
    @FXML
    private TitledPane nuSwitchedExpressionsTitledPane;
    @FXML
    private ListView<Expression> nuSwitchedExpressionsListView;
    @FXML
    private TitledPane builtInExpressionsTitledPane;
    @FXML
    private ListView<Expression> builtInExpressionsListView;
    @FXML
    private TextField categoryTextField;
    @FXML
    private ListView<SquidReportCategoryInterface> categoryListView;
    @FXML
    private ListView<SquidReportColumnInterface> columnListView;
    @FXML
    private TextArea columnDetailsTextArea;
    @FXML
    private Button newButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button copyButton;
    @FXML
    private Button renameButton;
    @FXML
    private Button restoreButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button exportButton;
    @FXML
    private Button importButton;
    @FXML
    private TitledPane colunmnsTitledPane;
    @FXML
    private HBox buttonHBox;

    private final ObjectProperty<Expression> selectedExpression = new SimpleObjectProperty<>();
    private final ObjectProperty<SquidReportCategoryInterface> selectedCategory = new SimpleObjectProperty<>();
    private final ObjectProperty<Boolean> selectedCategoryIsFixedCategory = new SimpleObjectProperty<>();
    private final ObjectProperty<SquidReportColumnInterface> selectedColumn = new SimpleObjectProperty<>();

    List<Expression> namedExpressions;
    public static Expression expressionToHighlightOnInit = null;
    private TaskInterface task;

    private ObjectProperty<Boolean> isEditing = new SimpleObjectProperty<>();
    private ObjectProperty<Boolean> isDefault = new SimpleObjectProperty<>();
    private ObjectProperty<Boolean> isDefaultLab = new SimpleObjectProperty<>();

    private boolean isRefMat;
    private SquidReportTableInterface selectedRefMatReportModel;
    private SquidReportTableInterface selectedUnknownReportModel;
    @FXML
    private Button makeDefaultButton;

    //INIT
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        task = squidProject.getTask();
        // update
        task.setupSquidSessionSpecsAndReduceAndReport(false);

        ((Task) task).initTaskDefaultSquidReportTables(false);

        isRefMat = false;
        spotsChoiceBox.setVisible(true);
        isEditing.set(false);
        isDefault.set(false);
        isDefaultLab.set(false);

        selectedRefMatReportModel = task.getSelectedRefMatReportModel();
        selectedUnknownReportModel = task.getSelectedUnknownReportModel();

        initEditing();
        initDefault();
        initDefaultLab();
        initCategoryTextField();

        initReportTableCB();
        initSpotChoiceBox();

        initSelectionActions();
        initListViews();

        if (expressionToHighlightOnInit != null) {
            selectInAllPanes(expressionToHighlightOnInit, true);
            expressionToHighlightOnInit = null;
        } else if (!customExpressionsListView.getItems().isEmpty()) {
            selectInAllPanes(customExpressionsListView.getItems().get(0), true);
        }

        // disable refmat if none
        refMatRadioButton.setDisable(task.getReferenceMaterialSpots().isEmpty());

        columnDetailsTextArea.setStyle(SquidUI.PEEK_LIST_CSS_STYLE_SPECS);
    }

    private void initCategoryTextField() {
        categoryTextField.setOnKeyPressed(val -> {
            if (val.getCode() == KeyCode.ENTER) {
                createCategory();
            }
        });
    }

    private void processButtons() {
        if (isEditing.getValue()) {
            Arrays.asList(makeDefaultButton, newButton, copyButton, renameButton, deleteButton, exportButton, importButton, unknownsRadioButton, refMatRadioButton).
                    parallelStream().forEach(button -> button.setDisable(true));
            Arrays.asList(saveButton, restoreButton).forEach(button -> button.setDisable(false));
        } else if (isDefault.getValue()) {
            Arrays.asList(makeDefaultButton, saveButton, restoreButton, renameButton, deleteButton).
                    parallelStream().forEach(button -> button.setDisable(true));
            Arrays.asList(unknownsRadioButton, refMatRadioButton, newButton, copyButton, exportButton, importButton).
                    parallelStream().forEach(button -> button.setDisable(false));
        } else if (isDefaultLab.getValue()) {
            Arrays.asList(makeDefaultButton, saveButton, restoreButton, deleteButton).
                    parallelStream().forEach(button -> button.setDisable(true));
            Arrays.asList(unknownsRadioButton, refMatRadioButton, newButton, copyButton, exportButton, importButton, renameButton).
                    parallelStream().forEach(button -> button.setDisable(false));
        } else {
            Arrays.asList(restoreButton, saveButton).forEach(button -> button.setDisable(true));
            Arrays.asList(makeDefaultButton, newButton, copyButton, renameButton, exportButton, importButton, refMatRadioButton, unknownsRadioButton).
                    parallelStream().forEach(button -> button.setDisable(false));

            if (!isRefMat
                    && reportTableCB.getSelectionModel().getSelectedItem().getReportTableName().matches(NAME_OF_WEIGHTEDMEAN_PLOT_SORT_REPORT)) {
                Arrays.asList(makeDefaultButton, deleteButton, renameButton).forEach(button -> button.setDisable(true));
            } else {
                Arrays.asList(makeDefaultButton, deleteButton, renameButton).forEach(button -> button.setDisable(false));
            }
        }
    }

    private void processReportTableChoiceBoxes() {
        if (isEditing.getValue()) {
            reportTableCB.setDisable(true);
        } else {
            reportTableCB.setDisable(false);
        }
    }

    private void initEditing() {
        isEditing.setValue(false);
        isEditing.addListener(ob -> {
            processButtons();
            processReportTableChoiceBoxes();
        });
    }

    private void initDefault() {
        isDefault.setValue(false);
        isDefault.addListener(ob -> processButtons());
    }

    private void initDefaultLab() {
        isDefaultLab.setValue(false);
        try {
            isDefaultLab.addListener(ob -> processButtons());
        } catch (Exception e) {
        }
    }

    private void initReportTableCB() {
        reportTableCB.setConverter(new StringConverter<SquidReportTableInterface>() {
            @Override
            public String toString(SquidReportTableInterface object) {
                return (object.isIsLabDataDefault() ? (object.getReportTableName() + " <Lab Default>") : object.getReportTableName());
            }

            @Override
            public SquidReportTableInterface fromString(String string) {
                return null;
            }
        });
        reportTableCB.getSelectionModel().selectedItemProperty().addListener(param -> {
            if (reportTableCB.getSelectionModel().getSelectedItem() != null) {
                isDefault.setValue(reportTableCB.getSelectionModel().getSelectedItem().isDefault());
                isDefaultLab.setValue(reportTableCB.getSelectionModel().getSelectedItem().isIsLabDataDefault());

                populateCategoryListView();
                if (isRefMat) {
                    selectedRefMatReportModel = reportTableCB.getSelectionModel().getSelectedItem();
                    task.setSelectedRefMatReportModel(selectedRefMatReportModel);
                    //makeDefaultButton.setDisable(isDefaultLab.get());//    selectedRefMatReportModel.isDefault() || selectedRefMatReportModel.amWeightedMeanPlotAndSortReport() || !saveButton.isDisabled());
                } else {
                    selectedUnknownReportModel = reportTableCB.getSelectionModel().getSelectedItem();
                    task.setSelectedUnknownReportModel(selectedUnknownReportModel);
                    // makeDefaultButton.setDisable(isDefaultLab.get());//  selectedUnknownReportModel.isDefault() || selectedUnknownReportModel.amWeightedMeanPlotAndSortReport() || !saveButton.isDisabled());
                }
                processButtons();

            }
        });
        populateSquidReportTableChoiceBox();
        selectSquidReportTableByPriors();
    }

    private void selectSquidReportTableByPriors() {
        SquidReportTableInterface selectedReportModel = (isRefMat) ? selectedRefMatReportModel : selectedUnknownReportModel;
        SquidReportTableInterface defaultReportModel = (isRefMat) ? Task.squidLabData.getDefaultReportTableRM() : Task.squidLabData.getDefaultReportTable();

        if (selectedReportModel != null) {
            reportTableCB.getSelectionModel().select(selectedReportModel);
        } else if (defaultReportModel != null) {
            reportTableCB.getSelectionModel().select(defaultReportModel);
        } else {
            reportTableCB.getSelectionModel().selectFirst();
        }

        if (reportTableCB.getSelectionModel().getSelectedIndex() == -1) {
            reportTableCB.getSelectionModel().selectFirst();
        }
        processButtons();
    }

    private void populateSquidReportTableChoiceBox() {
        List<SquidReportTableInterface> tables = getTables();
        reportTableCB.getItems().setAll(FXCollections.observableArrayList(tables));
    }

    private void initListViews() {
        nuSwitchedExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        nuSwitchedExpressionsListView.setCellFactory(new ExpressionCellFactory());
        nuSwitchedExpressionsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //Same listener for each category
        nuSwitchedExpressionsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Expression>() {
            @Override
            public void changed(ObservableValue<? extends Expression> observable, Expression oldValue, Expression newValue) {
                if (newValue != null) {

                    selectedExpression.set(newValue);

                    selectInAllPanes(newValue, false);
                }
            }
        });

        builtInExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        builtInExpressionsListView.setCellFactory(new ExpressionCellFactory());
        builtInExpressionsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        builtInExpressionsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Expression>() {
            @Override
            public void changed(ObservableValue<? extends Expression> observable, Expression oldValue, Expression newValue) {
                if (newValue != null) {

                    selectedExpression.set(newValue);

                    selectInAllPanes(newValue, false);
                }
            }
        });

        customExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        customExpressionsListView.setCellFactory(new ExpressionCellFactory());
        customExpressionsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        customExpressionsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Expression>() {
            @Override
            public void changed(ObservableValue<? extends Expression> observable, Expression oldValue, Expression newValue) {
                if (newValue != null) {

                    selectedExpression.set(newValue);

                    selectInAllPanes(newValue, false);
                }
            }
        });

        populateExpressionListViews();

        //RATIOS
        ratioExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        ratioExpressionsListView.setCellFactory(new StringCellFactory());
        ratioExpressionsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ratioExpressionsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    Expression exp = new Expression(
                            task.getNamedExpressionsMap().get(newValue), "[\"" + newValue + "\"]",
                            false, false, false);
                    exp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                    exp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
                    exp.getExpressionTree().setSquidSwitchSAUnknownCalculation(true);
                    selectedExpression.set(exp);

                    selectInAllPanes(exp, false);
                }
            }
        });

        populateRatiosListView();

        //ISOTOPES
        isotopesExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        isotopesExpressionsListView.setCellFactory(new StringCellFactory());
        isotopesExpressionsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        isotopesExpressionsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    Expression exp = new Expression(
                            task.getNamedExpressionsMap().get(newValue), "TotalCPS([\"" + newValue + "\"])",
                            false, false, false);
                    exp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                    exp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
                    exp.getExpressionTree().setSquidSwitchSAUnknownCalculation(true);
                    selectInAllPanes(exp, false);
                }
            }
        });

        populateIsotopesListView();

        //Spot Meta Data
        spotMetaDataExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        spotMetaDataExpressionsListView.setCellFactory(new StringCellFactory());
        spotMetaDataExpressionsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        spotMetaDataExpressionsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    Expression exp = new Expression(task.getNamedExpressionsMap().get(newValue),
                            newValue, false, false, false);
                    exp.getExpressionTree().setSquidSpecialUPbThExpression(true);
                    exp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(true);
                    exp.getExpressionTree().setSquidSwitchSAUnknownCalculation(true);
                    selectedExpression.set(exp);
                    selectInAllPanes(exp, false);
                }
            }
        });

        populateSpotMetaDataListView();

        //Squid Report Categories
        categoryListView.setCellFactory(new SquidReportCategoryInterfaceCellFactory());
        categoryListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        categoryListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        categoryListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedCategory.setValue(newValue);
        });
        categoryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectedCategory.setValue(categoryListView.getSelectionModel().getSelectedItem());
        selectedCategory.addListener((ob, ov, nv) -> {
            selectedCategoryIsFixedCategory.setValue(nv != null
                    && reportTableCB.getSelectionModel().getSelectedItem().amWeightedMeanPlotAndSortReport()
                    && (nv.getDisplayName().compareTo("Time") == 0
                    || nv.getDisplayName().compareTo("Ages") == 0
                    || nv.getDisplayName().compareTo("Raw Ratios") == 0
                    || nv.getDisplayName().compareTo("Corr. Ratios") == 0));
        });

        populateCategoryListView();

        //Squid Report Columns
        columnListView.setCellFactory(new SquidReportColumnInterfaceCellFactory());
        columnListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        columnListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        columnListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            selectedColumn.setValue(newValue);
        }));
        selectedColumn.setValue(columnListView.getSelectionModel().getSelectedItem());
        columnListView.setOnDragOver(event -> {
            if (selectedCategory.getValue() != null
                    && !selectedCategoryIsFixedCategory.getValue()
                    && event.getDragboard() != null && (event.getDragboard().hasString() || event.getDragboard().hasContent(STRING_LIST))) {
                event.acceptTransferModes(TransferMode.COPY);
            }
        });
        columnListView.setOnDragDropped(event -> {
            boolean success = false;
            if (event.getTransferMode().equals(TransferMode.COPY) && !selectedCategoryIsFixedCategory.getValue()) {
                SquidReportColumnInterface col = null;
                @SuppressWarnings("unchecked")
                List<String> listOfExp
                        = ((List<String>) event.getDragboard().getContent(STRING_LIST));
                for (String colName : listOfExp) {
                    col = SquidReportColumn.createSquidReportColumn(colName);
                    columnListView.getItems().add(col);
                }

                columnListView.getSelectionModel().select(col);
                success = true;
                isEditing.setValue(true);
            }
            event.setDropCompleted(success);
            event.consume();
        });
        columnListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void initSelectionActions() {
        selectedCategory.addListener(observable -> {
            if (selectedCategory.getValue() != null) {
                populateColumnListView();
                columnListView.getSelectionModel().selectFirst();
            } else {
                columnDetailsTextArea.setText("No Column Selected");
            }
        });
        selectedColumn.addListener(observable -> {
            populateColumnDetails();
        });
    }

    private void initSpotChoiceBox() {
        ObservableList<String> spots = FXCollections.observableArrayList();
        task.getMapOfUnknownsBySampleNames().keySet().forEach(val -> spots.add(val));
        spots.remove(SpotTypes.UNKNOWN.getSpotTypeName());
        spots.add(0, SpotTypes.UNKNOWN.getSpotTypeName());
        spotsChoiceBox.setItems(spots);
        spotsChoiceBox.getSelectionModel().select(SpotTypes.UNKNOWN.getSpotTypeName());
        spotsChoiceBox.getSelectionModel().selectedItemProperty().addListener(val -> {
            Platform.runLater(() -> populateColumnDetails());
        });
    }

    private void populateRatiosListView() {
        final ObservableList<String> obList = FXCollections.observableArrayList();
        task.getSquidRatiosModelList().forEach(item -> obList.add(item.getRatioName()));
        obList.sort(new IntuitiveStringComparator<>());
        ratioExpressionsListView.setItems(obList);
    }

    private void populateIsotopesListView() {
        final ObservableList<String> obList = FXCollections.observableArrayList();
        task.getSquidSpeciesModelList().forEach(item -> obList.add(item.getIsotopeName()));
        obList.sort(new IntuitiveStringComparator<>());
        isotopesExpressionsListView.setItems(obList);
    }

    private void populateSpotMetaDataListView() {
        final ObservableList<String> obList = FXCollections.observableArrayList();
        task.getNamedSpotLookupFieldsMap().forEach((key, value) -> obList.add(value.getName()));
        task.getNamedSpotMetaDataFieldsMap().forEach((key, value) -> obList.add(value.getName()));
        obList.sort(new IntuitiveStringComparator<>());
        spotMetaDataExpressionsListView.setItems(obList);
    }

    private void populateCategoryListView() {
        LinkedList<SquidReportCategoryInterface> cats = new LinkedList<>();
        for (SquidReportCategoryInterface cat : reportTableCB.getSelectionModel().getSelectedItem().getReportCategories()) {
            cats.add(cat.clone());
        }
        ObservableList<SquidReportCategoryInterface> obList = FXCollections.observableArrayList(cats);
        categoryListView.setItems(obList);
        if (categoryListView.getItems().isEmpty()) {
            columnListView.setItems(FXCollections.observableArrayList());
        } else {
            categoryListView.getSelectionModel().selectFirst();
        }
    }

    private void populateColumnListView() {
        ObservableList<SquidReportColumnInterface> obList = FXCollections.observableList(selectedCategory.getValue().getCategoryColumns());
        columnListView.setItems(obList);
    }

    private void populateColumnDetails() {
        String result;
        if (selectedColumn.getValue() != null) {
            String expName = selectedColumn.getValue().getExpressionName();
            ExpressionTreeInterface exp = task.findNamedExpression(expName);
            if (exp != null) {
                if (isRefMat) {
                    List<ShrimpFractionExpressionInterface> refMatSpots = task.getReferenceMaterialSpots();
                    List<ShrimpFractionExpressionInterface> concRefMatSpots = task.getConcentrationReferenceMaterialSpots();
                    if (exp instanceof ConstantNode) {
                        result = "Not used";
                        if (exp.isSquidSwitchSTReferenceMaterialCalculation()) {
                            try {
                                result = exp.getName() + " = " + squid3RoundedToSize((Double) ((ConstantNode) exp).getValue(), 15);
                            } catch (Exception e) {
                            }
                        }
                    } else {
                        result = "Reference Materials not processed.";
                        if (exp.isSquidSwitchSTReferenceMaterialCalculation()) {
                            if (refMatSpots.size() > 0) {
                                result = peekDetailsPerSpot(refMatSpots, exp);
                            } else {
                                result = "No Reference Materials";
                            }
                        } else if (exp.isSquidSwitchConcentrationReferenceMaterialCalculation()) {
                            if (concRefMatSpots.size() > 0) {
                                result = peekDetailsPerSpot(concRefMatSpots, exp);
                            } else {
                                result = "No Concentration Reference Materials";
                            }
                        }
                    }
                } else {
                    List<ShrimpFractionExpressionInterface> unSpots
                            = task.getMapOfUnknownsBySampleNames().get(exp.getUnknownsGroupSampleName());
                    String spot = spotsChoiceBox.getValue();
                    if (spot.compareToIgnoreCase(SpotTypes.UNKNOWN.getSpotTypeName()) != 0) {
                        List<ShrimpFractionExpressionInterface> spotsToBeUsed = new ArrayList<>();
                        unSpots.forEach(unSpot -> {
                            if (unSpot.getFractionID().startsWith(spot)) {
                                spotsToBeUsed.add(unSpot);
                            }
                        });
                        unSpots = spotsToBeUsed;
                    }
                    if (exp instanceof ConstantNode) {
                        result = "Not used";
                        if (exp.isSquidSwitchSAUnknownCalculation()) {
                            try {
                                result = expName + " = " + squid3RoundedToSize((Double) ((ConstantNode) exp).getValue(), 15);
                            } catch (Exception e) {
                            }
                        }
                    } else {
                        result = "Unknowns not processed.";
                        if (exp.isSquidSwitchSAUnknownCalculation()) {
                            if (unSpots.size() > 0) {
                                result = peekDetailsPerSpot(unSpots, exp);
                            } else {
                                result = "No Unknowns";
                            }
                        }
                    }
                }
            } else {
                result = "Could not locate expression tree";
            }
        } else {
            result = "No column selected";
        }
        columnDetailsTextArea.setText(result);
    }

    private String peekDetailsPerSpot(List<ShrimpFractionExpressionInterface> spots, ExpressionTreeInterface expTree) {

        StringBuilder sb = new StringBuilder();
        int sigDigits = 15;

        // context-sensitivity - we use Ma in Squid for display
        boolean isAge = expTree.getName().toUpperCase(Locale.ENGLISH).contains("AGE");
        String contextAgeFieldName = (isAge ? "Age(Ma)" : "Value");
        String contextAge1SigmaAbsName = (isAge ? "1\u03C3Abs(Ma)" : "1\u03C3Abs");
        // or it may be concentration ppm
        boolean isConcen = expTree.getName().toUpperCase(Locale.ENGLISH).contains("CONCEN");
        contextAgeFieldName = (isConcen ? "ppm" : contextAgeFieldName);

        if (expTree.isSquidSwitchConcentrationReferenceMaterialCalculation()) {
            sb.append("Concentration Reference Materials Only\n\n");
        }
        sb.append(String.format("%1$-" + 18 + "s", "SpotName"));
        String[][] resultLabels;
        if (((ExpressionTree) expTree).getOperation() != null) {
            if ((((ExpressionTree) expTree).getOperation().getName().compareToIgnoreCase("Value") == 0)) {
                if (((ExpressionTree) expTree).getChildrenET().get(0) instanceof VariableNodeForSummary) {
                    String uncertaintyDirective
                            = ((VariableNodeForSummary) ((ExpressionTree) expTree).getChildrenET().get(0)).getUncertaintyDirective();
                    if (uncertaintyDirective.length() > 0) {
                        if (uncertaintyDirective.compareTo(ABS_UNCERTAINTY_DIRECTIVE) == 0) {
                            resultLabels = new String[][]{{contextAge1SigmaAbsName}, {}};
                        } else {
                            resultLabels = new String[][]{{"1\u03C3" + uncertaintyDirective}, {}};
                        }
                    } else {
                        resultLabels = new String[][]{{contextAgeFieldName}, {}};
                    }
                } else if (((ExpressionTree) expTree).getChildrenET().get(0) instanceof ConstantNode) {
                    resultLabels = new String[][]{{"Constant"}, {}};
                } else if (((ExpressionTree) expTree).getChildrenET().get(0) instanceof SpotFieldNode) {
                    resultLabels = new String[][]{{((ExpressionTree) expTree).getChildrenET().get(0).getName()}, {}};
                } else {
                    // ShrimpSpeciesNode
                    resultLabels = new String[][]{{"TotalCPS"}, {}};
                }
            } else if (((ExpressionTree) expTree).getLeftET() instanceof ShrimpSpeciesNode) {
                // Check for functions of species
                if (((ExpressionTree) expTree).getOperation() instanceof ShrimpSpeciesNodeFunction) {
                    resultLabels = new String[][]{{((ShrimpSpeciesNodeFunction) ((ExpressionTree) expTree).getOperation()).getName()}, {}};
                } else {
                    // case of ratio
                    resultLabels = new String[][]{{expTree.getName(), "1\u03C3Abs", "1\u03C3%"}, {}};
                }

            } else if (((ExpressionTree) expTree).hasRatiosOfInterest()) {
                // case of NU switch
                String uncertaintyDirective
                        = ((ExpressionTree) expTree).getUncertaintyDirective();
                if (uncertaintyDirective.length() > 0) {
                    resultLabels = new String[][]{{"1\u03C3" + uncertaintyDirective}, {}};
                } else {
                    resultLabels = new String[][]{{contextAgeFieldName, "1\u03C3Abs", "1\u03C3%"}, {}};
                }
            } else {
                // some smarts
                String[][] resultLabelsFirst = clone2dArray(((ExpressionTree) expTree).getOperation().getLabelsForOutputValues());
                resultLabels = new String[1][resultLabelsFirst[0].length == 1 ? 1 : 3];
                resultLabels[0][0] = contextAgeFieldName;
                if (resultLabelsFirst[0].length > 1) {
                    resultLabels[0][1] = contextAge1SigmaAbsName;
                    resultLabels[0][2] = "1\u03C3%";
                }
            }

            for (int i = 0; i < resultLabels[0].length; i++) {
                try {
                    sb.append(String.format("%1$-" + 25 + "s", resultLabels[0][i]));
                } catch (Exception e) {
                }
            }

            sb.append("\n");

            // produce values
            if (((ExpressionTree) expTree).getLeftET() instanceof ShrimpSpeciesNode) {
                // Check for functions of species
                if (((ExpressionTree) expTree).getOperation() instanceof ShrimpSpeciesNodeFunction) {
                    for (ShrimpFractionExpressionInterface spot : spots) {
                        sb.append(String.format("%1$-" + 18 + "s", spot.getFractionID()));

                        double[][] results
                                = Arrays.stream(spot.getTaskExpressionsEvaluationsPerSpot().get(expTree)).toArray(double[][]::new);

                        if (!Double.isFinite(results[0][0])) {
                            sb.append("NaN");
                        } else {
                            Formatter formatter = new Formatter();
                            for (int i = 0; i < resultLabels[0].length; i++) {
                                formatter.format("% 20.14" + (String) ((i == 2) ? "f   " : "E   "), squid3RoundedToSize(results[0][i], sigDigits));
                            }
                            sb.append(formatter.toString());
                        }
//
//
//                        try {
//                            double[][] results
//                                    = Arrays.stream(spot.getTaskExpressionsEvaluationsPerSpot().get(expTree)).toArray(double[][]::new);
//                            for (int i = 0; i < resultLabels[0].length; i++) {
//                                try {
//                                    sb.append(String.format("%1$-" + 20 + "s", squid3RoundedToSize(results[0][i], sigDigits)));
//                                } catch (Exception e) {
//                                }
//                            }
//                        } catch (Exception e) {
//                        }
                        sb.append("\n");
                    }
                } else if (((ExpressionTree) expTree).getOperation() instanceof Value) {
                    // case of isotope
                    for (ShrimpFractionExpressionInterface spot : spots) {
                        sb.append(String.format("%1$-" + 18 + "s", spot.getFractionID()));
                        double[][] results
                                = Arrays.stream(spot.getTaskExpressionsEvaluationsPerSpot().get(expTree)).toArray(double[][]::new);

                        if (!Double.isFinite(results[0][0])) {
                            sb.append("NaN");
                        } else {
                            Formatter formatter = new Formatter();
                            for (int i = 0; i < results[0].length; i++) {
                                formatter.format("% 20.14" + (String) ((i == 2) ? "f   " : "E   "), squid3RoundedToSize(results[0][i], sigDigits));
                            }
                            sb.append(formatter.toString());
                        }
                        sb.append("\n");
                    }
                } else {
                    // case of ratio
                    for (ShrimpFractionExpressionInterface spot : spots) {
                        sb.append(String.format("%1$-" + 18 + "s", spot.getFractionID()));
                        double[][] results
                                = Arrays.stream(spot.getIsotopicRatioValuesByStringName(expTree.getName())).toArray(double[][]::new);

                        if (!Double.isFinite(results[0][0])) {
                            sb.append("NaN");
                        } else {
                            Formatter formatter = new Formatter();
                            formatter.format("% 20.14E   % 20.14E   % 20.14f   ", results[0][0], results[0][1], calcPercentUnct(results[0]));
                            sb.append(formatter.toString());
                        }

                        sb.append("\n");
                    }
                }
            } else {
                for (ShrimpFractionExpressionInterface spot : spots) {
                    if (spot.getTaskExpressionsEvaluationsPerSpot().get(expTree) != null) {
                        sb.append(String.format("%1$-" + 18 + "s", spot.getFractionID()));
                        double[][] results
                                = Arrays.stream(spot.getTaskExpressionsEvaluationsPerSpot().get(expTree)).toArray(double[][]::new);

                        if (!Double.isFinite(results[0][0])) {
                            sb.append("NaN");
                        } else {
                            double[] resultsWithPct = new double[0];
                            if ((resultLabels[0].length == 1) && (results[0].length >= 1)) {
                                resultsWithPct = new double[1];
                                resultsWithPct[0] = squid3RoundedToSize(results[0][0] / (isAge ? 1.0e6 : 1.0), sigDigits);
                            } else if (results[0].length > 1) {
                                resultsWithPct = new double[3];
                                resultsWithPct[0] = squid3RoundedToSize(results[0][0] / (isAge ? 1.0e6 : 1.0), sigDigits);
                                resultsWithPct[1] = squid3RoundedToSize(results[0][1] / (isAge ? 1.0e6 : 1.0), sigDigits);
                                resultsWithPct[2] = calcPercentUnct(results[0]);
                            }

                            Formatter formatter = new Formatter();
                            if (!Double.isFinite(results[0][0])) {
                                sb.append("NaN");
                            } else {
                                for (int i = 0; i < resultsWithPct.length; i++) {
                                    formatter.format("% 20.14" + (String) ((i == 2) ? "f   " : "E   "), squid3RoundedToSize(resultsWithPct[i], sigDigits));
                                }
                                sb.append(formatter.toString());
                            }
                        }
                        sb.append("\n");
                    }
                }
            }
        } else {
            // null operation ==> SquidSpeciesNode or SpotFieldNode
            if (expTree instanceof ShrimpSpeciesNode) {
                sb.append(String.format("%1$-25s", "TotalCPS"));
                sb.append("\n");
                for (ShrimpFractionExpressionInterface spot : spots) {
                    sb.append(String.format("%1$-" + 18 + "s", spot.getFractionID()));
                    // force evaluation with default view of species nodes
                    ((ShrimpSpeciesNode) expTree).setMethodNameForShrimpFraction("getTotalCps");
                    ((Task) task).evaluateTaskExpression(expTree);

                    double[][] results
                            = Arrays.stream(spot.getTaskExpressionsEvaluationsPerSpot().get(expTree)).toArray(double[][]::new);
                    if (!Double.isFinite(results[0][0])) {
                        sb.append("NaN");
                    } else {
                        Formatter formatter = new Formatter();

                        formatter.format("%1$-20s   ", squid3RoundedToSize(results[0][0], sigDigits));
                        sb.append(formatter.toString());
                    }

//                    try {
//                        double[][] results
//                                = Arrays.stream(spot.getTaskExpressionsEvaluationsPerSpot().get(expTree)).toArray(double[][]::new);
//                        for (int i = 0; i < results[0].length; i++) {
//                            try {
//                                sb.append(String.format("%1$-" + 20 + "s", squid3RoundedToSize(results[0][i], sigDigits)));
//                            } catch (Exception e) {
//                            }
//                        }
//                    } catch (Exception e) {
//                    }
                    sb.append("\n");
                }
            } else {
                // Spot metadata fields
                sb.append(String.format("%1$-23s", expTree.getName()));
                sb.append("\n");

                // force evaluation
                ((Task) task).evaluateTaskExpression(expTree);

                for (ShrimpFractionExpressionInterface spot : spots) {
                    sb.append(String.format("%1$-" + 18 + "s", spot.getFractionID()));
//                    // force evaluation
//                    ((Task) task).evaluateTaskExpression(expTree);

                    // check for special text metadata
                    if (spot.getTaskExpressionsMetaDataPerSpot().get(expTree) != null) {
                        String metaData = spot.getTaskExpressionsMetaDataPerSpot().get(expTree);
                        sb.append(String.format("%1$-" + 20 + "s", metaData));
                        sb.append("\n");
                    } else {
                        try {
                            double[][] results
                                    = Arrays.stream(spot.getTaskExpressionsEvaluationsPerSpot().get(expTree)).toArray(double[][]::new);

                            if (!Double.isFinite(results[0][0])) {
                                sb.append("NaN");
                            } else {
                                Formatter formatter = new Formatter();
                                formatter.format("%1$-" + 20 + "s", squid3RoundedToSize(results[0][0], sigDigits));
                                sb.append(formatter.toString());
                            }

                        } catch (Exception e) {
                        }
                        sb.append("\n");
                    }
                }
            }
        }

        return sb.toString();
    }

    private double calcPercentUnct(double[] valueModel) {
        return StrictMath.abs(valueModel[1] / valueModel[0] * 100.0);
    }

    private void selectInAllPanes(Expression exp, boolean scrollIfAlreadySelected) {
        if (nuSwitchedExpressionsListView.getSelectionModel().getSelectedItem() == null
                || !nuSwitchedExpressionsListView.getSelectionModel().getSelectedItem().equals(exp)) {
            nuSwitchedExpressionsListView.getSelectionModel().clearSelection();
            if (nuSwitchedExpressionsListView.getItems().contains(exp)) {
                nuSwitchedExpressionsListView.getSelectionModel().select(exp);
                nuSwitchedExpressionsListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(nuSwitchedExpressionsTitledPane);
            }
        } else {
            if (scrollIfAlreadySelected) {
                nuSwitchedExpressionsListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(nuSwitchedExpressionsTitledPane);
            }
        }

        if (builtInExpressionsListView.getSelectionModel().getSelectedItem() == null
                || !builtInExpressionsListView.getSelectionModel().getSelectedItem().equals(exp)) {
            builtInExpressionsListView.getSelectionModel().clearSelection();
            if (builtInExpressionsListView.getItems().contains(exp)) {
                builtInExpressionsListView.getSelectionModel().select(exp);
                builtInExpressionsListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(builtInExpressionsTitledPane);
            }
        } else {
            if (scrollIfAlreadySelected) {
                builtInExpressionsListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(builtInExpressionsTitledPane);
            }
        }

        if (customExpressionsListView.getSelectionModel().getSelectedItem() == null
                || !customExpressionsListView.getSelectionModel().getSelectedItem().equals(exp)) {
            customExpressionsListView.getSelectionModel().clearSelection();
            if (customExpressionsListView.getItems().contains(exp)) {
                customExpressionsListView.getSelectionModel().select(exp);
                customExpressionsListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(customExpressionsTitledPane);
            }
        } else {
            if (scrollIfAlreadySelected) {
                customExpressionsListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(customExpressionsTitledPane);
            }
        }
    }

    //POPULATE LISTS
    private void populateExpressionListViews() {
        namedExpressions = new ArrayList<>();
        task.getTaskExpressionsOrdered().forEach(exp -> {
            if (!exp.getExpressionTree().isSquidSwitchSCSummaryCalculation()
                    && ((exp.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation() && isRefMat)
                    || (exp.getExpressionTree().isSquidSwitchSAUnknownCalculation() && !isRefMat))) {
                namedExpressions.add(exp);
            }
        });

        Collections.sort(namedExpressions, new Comparator<Expression>() {
            @Override
            public int compare(Expression exp1, Expression exp2) {
                IntuitiveStringComparator<String> intuitiveStringComparator = new IntuitiveStringComparator<>();
                return intuitiveStringComparator.compare(exp1.getName().toLowerCase(), exp2.getName().toLowerCase());
            }
        });

        List<Expression> sortedNUSwitchedExpressionsList = new ArrayList<>();
        List<Expression> sortedBuiltInExpressionsList = new ArrayList<>();
        List<Expression> sortedCustomExpressionsList = new ArrayList<>();

        for (Expression exp : namedExpressions) {
            if (exp.amHealthy() && exp.isSquidSwitchNU()
                    && !exp.aliasedExpression()) {
                sortedNUSwitchedExpressionsList.add(exp);
            } else if (exp.getExpressionTree().isSquidSpecialUPbThExpression()
                    && exp.amHealthy()
                    && !exp.isSquidSwitchNU()
                    && !exp.aliasedExpression()) {
                sortedBuiltInExpressionsList.add(exp);
            } else if (exp.isCustom() && exp.amHealthy()) {
                sortedCustomExpressionsList.add(exp);
            }
        }

        ObservableList<Expression> items = FXCollections.observableArrayList(sortedNUSwitchedExpressionsList);
        nuSwitchedExpressionsListView.setItems(null);
        nuSwitchedExpressionsListView.setItems(items);

        items = FXCollections.observableArrayList(sortedBuiltInExpressionsList);
        builtInExpressionsListView.setItems(null);
        builtInExpressionsListView.setItems(items);

        items = FXCollections.observableArrayList(sortedCustomExpressionsList);
        customExpressionsListView.setItems(null);
        customExpressionsListView.setItems(items);
    }

    private List<String> getNamesOfTables() {
        List<SquidReportTableInterface> tables = getTables();
        List<String> namesOfTables = new ArrayList<>(tables.size());
        tables.forEach(table -> namesOfTables.add(table.getReportTableName()));
        return namesOfTables;
    }

    private List<SquidReportTableInterface> getTables() {

        List<SquidReportTableInterface> tables
                = isRefMat
                        ? task.getSquidReportTablesRefMat()
                        : task.getSquidReportTablesUnknown();

        SquidReportTableInterface defaultLabDataRT;
        if (isRefMat) {
            defaultLabDataRT = Task.squidLabData.getDefaultReportTableRM();
        } else {
            defaultLabDataRT = Task.squidLabData.getDefaultReportTable();
            // backwards fix
            if (defaultLabDataRT != null) {
                if (defaultLabDataRT.getReportTableName().toUpperCase().startsWith("BUILTIN")) {
                    defaultLabDataRT = null;
                    Task.squidLabData.setDefaultReportTable(null);
                }
            }
        }

        for (SquidReportTableInterface table : tables) {
            if (table.isIsLabDataDefault()) {
                table.setIsBuiltInSquidDefault(false);
            }
            table.setIsLabDataDefault(false);
        }

        if (defaultLabDataRT != null) {
            defaultLabDataRT.setIsLabDataDefault(true);
            tables.remove(defaultLabDataRT);
            tables.add(defaultLabDataRT);
        }

        // clean tables
        List<SquidReportTableInterface> listWithoutDuplicates = new ArrayList<>(
                new HashSet<>(tables));

        tables.clear();
        tables.addAll(listWithoutDuplicates);
        return tables;
    }

    private SquidReportTableInterface createCopyOfUpdatedSquidReportTable() {
        SquidReportTableInterface table = reportTableCB.getSelectionModel().getSelectedItem().copy();
        LinkedList<SquidReportCategoryInterface> cats = new LinkedList<>();
        categoryListView.getItems().forEach(cat -> cats.add(cat.clone()));
        if (isRefMat) {
            table.setReportSpotTarget(SpotTypes.REFERENCE_MATERIAL);
        } else {
            table.setReportSpotTarget(SpotTypes.UNKNOWN);
        }
        table.setReportCategories(cats);
        table.setIsBuiltInSquidDefault(false);
        table.setIsLabDataDefault(false);

        return table;
    }

    @FXML
    public void viewOnAction(ActionEvent actionEvent) {
        SquidReportTableInterface table = createCopyOfUpdatedSquidReportTable();
        SquidReportTableLauncher.ReportTableTab tab;
        if (isRefMat) {
            tab = SquidReportTableLauncher.ReportTableTab.refMatCustom;
        } else {
            tab = SquidReportTableLauncher.ReportTableTab.unknownCustom;
            SquidReportTableController.unknownSpot = spotsChoiceBox.getValue();
        }
        squidReportTableLauncher.launch(tab, table);
    }

    @FXML
    private void newOnAction(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("name goes here");
        dialog.setTitle("Squid3 Report Model Name");
        dialog.setHeaderText("Enter a name for the Report Model");
        dialog.setContentText("Name:");

        dialog.showAndWait().ifPresent(name -> {

            if (getNamesOfTables().contains(name)) {
                SquidMessageDialog.showWarningDialog("A Squid3 Report Model with the name you entered already exists. Please try again.", primaryStageWindow);
            } else {
                SquidReportTableInterface table = SquidReportTable.createEmptySquidReportTable(name);
                if (isRefMat) {
                    table.setReportSpotTarget(SpotTypes.REFERENCE_MATERIAL);
                }
                else {
                    table.setReportSpotTarget(SpotTypes.UNKNOWN);
                }
                table.setIsLabDataDefault(false);
                getTables().add(table);
                populateSquidReportTableChoiceBox();
                reportTableCB.getSelectionModel().select(table);
                isEditing.setValue(true);

                // set up default as example
                SquidReportCategoryInterface cat = SquidReportCategory.createReportCategory("Default Category");
                categoryListView.getItems().add(cat);
                categoryListView.getSelectionModel().selectFirst();
            }
        });
    }

    @FXML
    private void copyOnAction(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("name goes here");
        dialog.setTitle("Squid3 Report Model Name");
        dialog.setHeaderText("Enter a name for the Report Model Copy");
        dialog.setContentText("Name:");
        dialog.showAndWait().ifPresent(name -> {
            if (getNamesOfTables().contains(name)) {
                SquidMessageDialog.showWarningDialog("A Squid3 Report Model with the name you entered already exists. Aborting.", primaryStageWindow);
            } else {
                SquidReportTableInterface copy = createCopyOfUpdatedSquidReportTable();
                copy.setReportTableName(name);
                copy.setIsLabDataDefault(false);
                copy.setIsBuiltInSquidDefault(false);
                getTables().add(copy);
                populateSquidReportTableChoiceBox();
                reportTableCB.getSelectionModel().select(copy);
                isEditing.setValue(true);
            }
        });
    }

    @FXML
    private void renameOnAction(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("new name goes here");
        dialog.setTitle("Squid3 Report Model Name");
        dialog.setHeaderText("Enter a new name for the Report Model");
        dialog.setContentText("Name:");
        dialog.showAndWait().ifPresent(name -> {
            if (getNamesOfTables().contains(name)) {
                SquidMessageDialog.showWarningDialog("A Squid3 Report Model with the name you entered already exists. Aborting.", primaryStageWindow);
            } else {
                int selectedIndex = reportTableCB.getSelectionModel().getSelectedIndex();
                reportTableCB.getSelectionModel().getSelectedItem().setReportTableName(name);
                populateSquidReportTableChoiceBox();
                reportTableCB.getSelectionModel().select(selectedIndex);
            }
        });
    }

    @FXML
    private void restoreOnAction(ActionEvent event) {
        populateCategoryListView();
        isEditing.setValue(false);
    }

    @FXML
    private void deleteOnAction(ActionEvent event) {
        getTables().remove(reportTableCB.getSelectionModel().getSelectedItem());
        populateSquidReportTableChoiceBox();
        reportTableCB.getSelectionModel().selectFirst();
    }

    @FXML
    private void exportOnAction(ActionEvent event) {
        try {
            SquidReportTable table = (SquidReportTable) createCopyOfUpdatedSquidReportTable();
            File file = FileHandler.saveSquidReportModelXMLFile(table, primaryStageWindow);
            if (file != null) {
                table.serializeXMLObject(file.getAbsolutePath());
            }
        } catch (Exception e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), primaryStageWindow);
            e.printStackTrace();
        }
    }

    @FXML
    private void importOnAction(ActionEvent event) {
        File file = null;
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        boolean isValidTableXML = false;
        try {
            file = FileHandler.selectSquidReportModelXMLFile(primaryStageWindow);
            final Schema schema = sf.newSchema(new File(URL_STRING_FOR_SQUIDREPORTTABLE_XML_SCHEMA_LOCAL));
            isValidTableXML = FileValidator.validateXML(file, schema, XML_HEADER_FOR_SQUIDREPORTTABLE_FILES_USING_LOCAL_SCHEMA);
        } catch (SAXException | IOException | ArrayIndexOutOfBoundsException e) {
            SquidMessageDialog.showWarningDialog("Unable to import. Could not be validated against XML schema.", primaryStageWindow);
            // Another message here to describe why failure occured?
        }
        if (file != null && isValidTableXML) {
            SquidReportTableInterface temp = SquidReportTable.createEmptySquidReportTable("");
            final SquidReportTableInterface IMPORTED_TABLE = (SquidReportTableInterface) ((SquidReportTable) temp).readXMLObject(file.getAbsolutePath(), false);
            if (IMPORTED_TABLE != null) {
                IMPORTED_TABLE.setIsLabDataDefault(false); // Not serialized, so initialize to false and let user decide if table should be lab data default
                // Switch radio button to spot target of imported report table if necessary
                boolean switchButton = false;
                if (isRefMat && !IMPORTED_TABLE.getReportSpotTarget().equals(SpotTypes.REFERENCE_MATERIAL)) {
                    isRefMat = false;
                    unknownsRadioButton.fire();
                    switchButton = true;
                }   
                else if (!isRefMat && IMPORTED_TABLE.getReportSpotTarget().equals(SpotTypes.REFERENCE_MATERIAL)) {
                    isRefMat = true;
                    refMatRadioButton.fire();
                    switchButton = true;
                }
                
                if (switchButton){
                    populateSquidReportTableChoiceBox();
                    selectSquidReportTableByPriors();
                    populateExpressionListViews();
                    populateIsotopesListView();
                    populateRatiosListView();
                    populateSpotMetaDataListView();
                } // End switch button
                
                final List<SquidReportTableInterface> tables = getTables();
                int indexOfSameNameTable = tables.indexOf(IMPORTED_TABLE);
                if (indexOfSameNameTable >= 0) {
                    SquidReportTableInterface sameNameTable = tables.get(indexOfSameNameTable);

                    ButtonType replace = new ButtonType("Replace");
                    ButtonType rename = new ButtonType("Rename");
                    Alert alert;
                    if (sameNameTable.isDefault()) {
                        alert = new Alert(Alert.AlertType.WARNING,
                                "A Squid3 Report Model already exists with this name. What do you want to do?",
                                rename,
                                ButtonType.CANCEL
                        );
                    } else {
                        alert = new Alert(Alert.AlertType.WARNING,
                                "A Squid3 Report Model already exists with this name. What do you want to do?",
                                replace,
                                rename,
                                ButtonType.CANCEL
                        );
                    }
                    alert.setX(SquidUI.primaryStageWindow.getX() + (SquidUI.primaryStageWindow.getWidth() - 200) / 2);
                    alert.setY(SquidUI.primaryStageWindow.getY() + (SquidUI.primaryStageWindow.getHeight() - 150) / 2);
                    alert.showAndWait().ifPresent((t) -> {
                        if (t.equals(replace)) {
                            tables.set(indexOfSameNameTable, IMPORTED_TABLE);
                            populateSquidReportTableChoiceBox();
                            reportTableCB.getSelectionModel().select(IMPORTED_TABLE);
                        } else if (t.equals(rename)) {
                            TextInputDialog dialog = new TextInputDialog(IMPORTED_TABLE.getReportTableName());
                            dialog.setTitle("Rename");
                            dialog.setHeaderText("Rename " + IMPORTED_TABLE.getReportTableName());
                            dialog.setContentText("Enter the new Squid3 Report Model's name:");
                            Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
                            TextField newName = null;
                            for (Node n : dialog.getDialogPane().getChildren()) {
                                if (n instanceof TextField) {
                                    newName = (TextField) n;
                                }
                            }
                            if (okBtn != null && newName != null) {
                                newName.textProperty().addListener((observable, oldValue, newValue) -> {
                                    IMPORTED_TABLE.setReportTableName(newValue);
                                    okBtn.setDisable(tables.contains(IMPORTED_TABLE) || newValue.isEmpty());
                                });
                            }
                            dialog.setX(SquidUI.primaryStageWindow.getX() + (SquidUI.primaryStageWindow.getWidth() - 200) / 2);
                            dialog.setY(SquidUI.primaryStageWindow.getY() + (SquidUI.primaryStageWindow.getHeight() - 150) / 2);
                            Optional<String> result = dialog.showAndWait();
                            if (result.isPresent()) {
                                IMPORTED_TABLE.setReportTableName(result.get());
                                if (tables.contains(IMPORTED_TABLE)) {
                                    SquidMessageDialog.showWarningDialog("A Squid3 Report Model already exists with this name.", primaryStageWindow);
                                } else {
                                    tables.add(IMPORTED_TABLE);
                                    populateSquidReportTableChoiceBox();
                                    reportTableCB.getSelectionModel().select(IMPORTED_TABLE);
                                }
                            }
                        }
                    });
                } else {
                    tables.add(IMPORTED_TABLE);
                    populateSquidReportTableChoiceBox();
                    reportTableCB.getSelectionModel().select(IMPORTED_TABLE);
                }
            }
        }
    }

    @FXML
    private void saveOnAction(ActionEvent event) {
        SquidReportTableInterface currTable = reportTableCB.getSelectionModel().getSelectedItem();

        if (currTable.isDefault()) {
            TextInputDialog dialog = new TextInputDialog("name goes here");
            dialog.setTitle("Squid3 Report Model Name");
            dialog.setHeaderText("Enter a name for the new Report Model");
            dialog.setContentText("Name:");
            dialog.showAndWait().ifPresent(name -> {
                if (getNamesOfTables().contains(name)) {
                    SquidMessageDialog.showWarningDialog("A Squid3 Report Model with the name you entered already exists. Aborting.", primaryStageWindow);
                } else {
                    SquidReportTableInterface table = createCopyOfUpdatedSquidReportTable();
                    table.setReportTableName(name);
                    getTables().add(table);
                    populateSquidReportTableChoiceBox();
                    reportTableCB.getSelectionModel().select(table);
                    isEditing.setValue(false);
                }
            });
        } else {
            SquidReportTableInterface table = createCopyOfUpdatedSquidReportTable();
            if (table.amWeightedMeanPlotAndSortReport()) {
                table.formatWeightedMeanPlotAndSortReport();
                Task.squidLabData.setSpecialWMSortingReportTable(table);
            }
            List<SquidReportTableInterface> tables = getTables();
            int location = tables.indexOf(table);
            if (location >= 0) {
                tables.set(location, table);
            } else {
                tables.add(table);
            }

            if (currTable.isIsLabDataDefault()){
                table.setIsLabDataDefault(true);
                currTable.setIsLabDataDefault(false);
            }
            if (table.isIsLabDataDefault()) {
                if (isRefMat) {
                    Task.squidLabData.setDefaultReportTableRM(table);
                } else {
                    Task.squidLabData.setDefaultReportTable(table);
                }
            }

            populateSquidReportTableChoiceBox();
            reportTableCB.getSelectionModel().select(table);
            isEditing.setValue(false);
        }

        if (squidProject != null) {
            try {
                ProjectFileUtilities.serializeSquidProject(squidProject, squidPersistentState.getMRUProjectFile().getCanonicalPath());
            } catch (IOException | SquidException ex) {
                SquidMessageDialog.showWarningDialog(ex.getMessage(), null);
            }
        }
    }

    @FXML
    public void toggleRefMatUnknownsAction(ActionEvent actionEvent) {

        isRefMat = !unknownsRadioButton.isSelected();
        spotsChoiceBox.setVisible(!isRefMat);

        populateSquidReportTableChoiceBox();
        selectSquidReportTableByPriors();
        populateExpressionListViews();
        populateIsotopesListView();
        populateRatiosListView();
        populateSpotMetaDataListView();
    }

    @FXML
    public void exportCSVOnAction(ActionEvent actionEvent) throws IOException {

        if (isEditing.getValue()) {
            SquidMessageDialog.showInfoDialog(
                    "Please save the report first.\n",
                    primaryStageWindow);
        } else {
            File reportTableFile = null;
            if (isRefMat) {
                reportTableFile = squidProject.produceSelectedReferenceMaterialReportCSV();
            } else {
                reportTableFile = squidProject.produceTargetedSelectedUnknownsReportCSV(spotsChoiceBox.getValue());
            }

            if (reportTableFile != null) {
                SquidMessageDialog.showSavedAsDialog(reportTableFile, primaryStageWindow);
            } else {
                SquidMessageDialog.showInfoDialog(
                        "An Error Occurred.\n",
                        primaryStageWindow);
            }
        }
    }

    @FXML
    private void makeDefaultAction(ActionEvent event) {
        SquidReportTableInterface defaultReportTableSpec = reportTableCB.getSelectionModel().getSelectedItem();
        if (!defaultReportTableSpec.isIsLabDataDefault()
                && !defaultReportTableSpec.isDefault()) {

            SquidReportTableInterface defaultReport;
            if (isRefMat) {
                defaultReport = Task.squidLabData.getDefaultReportTableRM();
            } else {
                defaultReport = Task.squidLabData.getDefaultReportTable();
            }

            if ((defaultReport != null) && (!defaultReport.equals(defaultReportTableSpec))) {
                defaultReport.setIsLabDataDefault(false);
            }
            defaultReportTableSpec.setIsLabDataDefault(true);
            if (isRefMat) {
                Task.squidLabData.setDefaultReportTableRM(defaultReportTableSpec);
            } else {
                Task.squidLabData.setDefaultReportTable(defaultReportTableSpec);
            }

            // show new notation for default
            populateSquidReportTableChoiceBox();
            selectSquidReportTableByPriors();
        }
    }

    private class SquidReportCategoryInterfaceCellFactory implements Callback<ListView<SquidReportCategoryInterface>, ListCell<SquidReportCategoryInterface>> {

        public SquidReportCategoryInterfaceCellFactory() {
        }

        @Override
        public ListCell<SquidReportCategoryInterface> call(ListView<SquidReportCategoryInterface> param) {
            ObjectProperty<Boolean> isFixedCategory = new SimpleObjectProperty<>();

            ListCell<SquidReportCategoryInterface> cell = new ListCell<SquidReportCategoryInterface>() {
                @Override
                public void updateItem(SquidReportCategoryInterface category, boolean empty) {
                    super.updateItem(category, empty);
                    if (!empty) {
                        setText(category.getDisplayName());
                        boolean hasBadExp = false;
                        for (SquidReportColumnInterface col : category.getCategoryColumns()) {
                            ExpressionTreeInterface exp = task.findNamedExpression(col.getExpressionName());
                            if (!(exp != null && !exp.isSquidSwitchSCSummaryCalculation()
                                    && ((exp.isSquidSwitchSTReferenceMaterialCalculation() && isRefMat)
                                    || (exp.isSquidSwitchSAUnknownCalculation() && !isRefMat)))) {
                                hasBadExp = true;
                                break;
                            }
                        }
                        if (hasBadExp) {
                            ImageView graphic = new ImageView(WARNING);
                            graphic.setPreserveRatio(true);
                            graphic.setFitHeight(this.getHeight());
                            setGraphic(graphic);
                        } else {
                            setGraphic(new ImageView(HEALTHY));
                        }
                    } else {
                        setText(null);
                        setGraphic(null);
                    }
                    isFixedCategory.setValue(!this.isEmpty()
                            && reportTableCB.getSelectionModel().getSelectedItem().amWeightedMeanPlotAndSortReport()
                            && (getText().compareTo("Time") == 0
                            || getText().compareTo("Ages") == 0
                            || getText().compareTo("Raw Ratios") == 0
                            || getText().compareTo("Corr. Ratios") == 0));
                }
            };

            cell.setOnDragOver(event -> {
                if (!isFixedCategory.getValue() && event.getDragboard().hasString() && event.getGestureSource() != cell) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            cell.setOnDragEntered(event -> {
                if (event.getGestureSource() != cell
                        && event.getDragboard().hasString()) {
                    cell.setOpacity(0.3);
                }
            });

            cell.setOnDragExited(event -> {
                if (event.getGestureSource() != cell
                        && event.getDragboard().hasString()) {
                    cell.setOpacity(1);
                }
            });

            cell.setOnDragDropped(event -> {
                boolean success = false;

                ObservableList<SquidReportCategoryInterface> items = categoryListView.getItems();
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getDisplayName().equals(event.getDragboard().getString())) {
                        SquidReportCategoryInterface cat = items.get(i);
                        items.remove(i);
                        if (cell.isEmpty()) {
                            items.add(cat);
                        } else {
                            items.add(cell.getIndex(), cat);
                        }
                        categoryListView.getSelectionModel().select(cat);
                        success = true;
                        event.consume();
                        isEditing.setValue(true);
                        break;
                    }
                }

                event.setDropCompleted(success);
            });

            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    selectedCategory.setValue(cell.getItem());
                    if (!isFixedCategory.getValue()) {
                        Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                        db.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));
                        ClipboardContent cc = new ClipboardContent();
                        cc.putString(cell.getItem().getDisplayName());
                        db.setContent(cc);
                        cell.setCursor(Cursor.CLOSED_HAND);
                    } else {
                        SquidMessageDialog.showWarningDialog("Cannot drag fixed categories.", primaryStageWindow);
                    }
                }
            });

            cell.setOnDragDone((event) -> {
                cell.setCursor(Cursor.OPEN_HAND);
            });

            cell.setOnMousePressed((event) -> {
                if (cell.isEmpty()) {
                } else if (event.isSecondaryButtonDown()) {
                    ContextMenu contextMenu = new ContextMenu();

                    // special case prevent showing if one of three base categories in Weighted Mean Plot Sort Table
                    showContextMenu = true;
                    if (reportTableCB.getSelectionModel().getSelectedItem().amWeightedMeanPlotAndSortReport()) {
                        if ((cell.getItem().getDisplayName().compareTo("Time") == 0)
                                || (cell.getItem().getDisplayName().compareTo("Ages") == 0)
                                || (cell.getItem().getDisplayName().compareTo("Raw Ratios") == 0)
                                || (cell.getItem().getDisplayName().compareTo("Corr. Ratios") == 0)) {
                            showContextMenu = false;
                        }
                    }
                    if (!showContextMenu) {
                        contextMenu.getItems().addAll(new MenuItem("Fixed Category"));
                    } else {
                        MenuItem deleteItem = new MenuItem("Delete");
                        deleteItem.setOnAction(action -> {
                            Integer[] selectedIndices = categoryListView.getSelectionModel().getSelectedIndices().toArray(new Integer[0]);
                            Arrays.sort(selectedIndices);

                            boolean hasFixedCats = false;
                            int numDeletedCats = 0;
                            String name;
                            for (int i = 0; i < selectedIndices.length; i++) {
                                SquidReportCategoryInterface cat = categoryListView.getItems().get(selectedIndices[i] - numDeletedCats);
                                name = cat.getDisplayName();
                                if (reportTableCB.getSelectionModel().getSelectedItem().amWeightedMeanPlotAndSortReport()
                                        && (name.compareTo("Time") == 0
                                        || name.compareTo("Age") == 0
                                        || name.compareTo("Raw Ratios") == 0
                                        || name.compareTo("Corr. Ratios") == 0)) {
                                    hasFixedCats = true;
                                } else {
                                    categoryListView.getItems().remove(selectedIndices[i] - numDeletedCats++);
                                }
                            }

                            if (hasFixedCats) {
                                SquidMessageDialog.showWarningDialog("Cannot delete fixed categories.", primaryStageWindow);
                            }
                            isEditing.setValue(true);
                        });
                        contextMenu.getItems().add(deleteItem);
                        if (categoryListView.getSelectionModel().getSelectedItems().size() == 1) {
                            MenuItem renameItem = new MenuItem(("Rename"));
                            renameItem.setOnAction(action -> {
                                TextInputDialog dialog = new TextInputDialog(cell.getText());
                                dialog.setTitle("Rename");
                                dialog.setHeaderText("Rename " + cell.getText());
                                dialog.setContentText("Enter the new category name:");

                                List<String> catNames = new ArrayList<>(categoryListView.getItems().size());
                                categoryListView.getItems().forEach(cat -> catNames.add(cat.getDisplayName()));

                                Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
                                TextField newName = null;
                                for (Node n : dialog.getDialogPane().getChildren()) {
                                    if (n instanceof TextField) {
                                        newName = (TextField) n;
                                    }
                                }
                                if (okBtn != null && newName != null) {
                                    newName.textProperty().addListener((observable, oldValue, newValue) -> {
                                        okBtn.setDisable(catNames.contains(newValue) || newValue.isEmpty());
                                    });
                                }

                                dialog.setX(SquidUI.primaryStageWindow.getX() + (SquidUI.primaryStageWindow.getWidth() - 200) / 2);
                                dialog.setY(SquidUI.primaryStageWindow.getY() + (SquidUI.primaryStageWindow.getHeight() - 150) / 2);
                                Optional<String> result = dialog.showAndWait();
                                if (result.isPresent()) {
                                    if (catNames.contains(result.get())) {
                                        SquidMessageDialog.showWarningDialog("A category already exists with this name.", primaryStageWindow);
                                    } else {
                                        cell.getItem().setDisplayName(result.get());
                                        isEditing.setValue(true);
                                        categoryListView.refresh();
                                    }
                                }
                            });
                            contextMenu.getItems().add(renameItem);
                        }
                    }
                    contextMenu.show(cell, event.getScreenX(), event.getScreenY());

                } else {
                    cell.setCursor(Cursor.CLOSED_HAND);
                }
            });

            cell.setOnMouseReleased((event)
                    -> {
                cell.setCursor(Cursor.OPEN_HAND);
            });

            cell.setCursor(Cursor.OPEN_HAND);

            cell.setOnMouseClicked((event)
                    -> {
                //Nothing
            });
            return cell;
        }
    }

    private class SquidReportColumnInterfaceCellFactory implements Callback<ListView<SquidReportColumnInterface>, ListCell<SquidReportColumnInterface>> {

        public SquidReportColumnInterfaceCellFactory() {
        }

        @Override
        public ListCell<SquidReportColumnInterface> call(ListView<SquidReportColumnInterface> param) {
            ListCell<SquidReportColumnInterface> cell = new ListCell<SquidReportColumnInterface>() {
                @Override
                public void updateItem(SquidReportColumnInterface column, boolean empty) {
                    super.updateItem(column, empty);
                    if (!empty) {
                        setText(column.getExpressionName());
                        ExpressionTreeInterface exp = task.findNamedExpression(column.getExpressionName());
                        if (exp != null && !exp.isSquidSwitchSCSummaryCalculation()
                                && ((exp.isSquidSwitchSTReferenceMaterialCalculation() && isRefMat)
                                || (exp.isSquidSwitchSAUnknownCalculation() && !isRefMat))) {
                            setGraphic(new ImageView(HEALTHY));
                        } else {
                            ImageView graphic = new ImageView(WARNING);
                            graphic.setPreserveRatio(true);
                            graphic.setFitHeight(this.getHeight());
                            setGraphic(graphic);
                        }
                    } else {
                        setText(null);
                        setGraphic(null);
                    }
                }
            };
            cell.setOnDragOver(event -> {
                if ((event.getDragboard().hasString() || event.getDragboard().hasContent(STRING_LIST)) && event.getGestureSource() != cell && !selectedCategoryIsFixedCategory.getValue()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            });

            cell.setOnDragEntered(event -> {
                if (event.getGestureSource() != cell
                        && (event.getDragboard().hasString() || event.getDragboard().hasContent(STRING_LIST))) {
                    cell.setOpacity(0.3);
                }
            });

            cell.setOnDragExited(event -> {
                if (event.getGestureSource() != cell
                        && (event.getDragboard().hasString() || event.getDragboard().hasContent(STRING_LIST))) {
                    cell.setOpacity(1);
                }
            });

            cell.setOnDragDropped(event -> {
                boolean success = false;
                if (!selectedCategoryIsFixedCategory.getValue()) {
                    ObservableList<SquidReportColumnInterface> items = columnListView.getItems();
                    if (event.getTransferMode().equals(TransferMode.COPY)) {
                        SquidReportColumnInterface col = null;
                        @SuppressWarnings("unchecked")
                        List<String> listOfExp
                                = ((List<String>) event.getDragboard().getContent(STRING_LIST));
                        for (String colName : listOfExp) {
                            col = SquidReportColumn.createSquidReportColumn(colName);
                            if (cell.getItem() != null) {
                                items.add(items.indexOf(cell.getItem()), col);
                            } else {
                                items.add(col);
                            }
                        }
                        columnListView.getSelectionModel().select(col);
                        success = true;
                        event.consume();
                        isEditing.setValue(true);
                    } else {
                        // MOVE
                        for (int i = 0; i < items.size(); i++) {
                            if (items.get(i).getExpressionName().equals(event.getDragboard().getString())) {
                                SquidReportColumnInterface col = items.get(i);
                                items.remove(i);
                                if (cell.isEmpty()) {
                                    items.add(col);
                                } else {
                                    items.add(cell.getIndex(), col);
                                }
                                columnListView.getSelectionModel().select(col);
                                success = true;
                                event.consume();
                                isEditing.setValue(true);
                                break;
                            }
                        }
                    }
                }
                event.setDropCompleted(success);
            });

            cell.setOnDragDetected(event -> {
                selectedColumn.setValue(cell.getItem());
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString(cell.getItem().getExpressionName());
                    db.setContent(cc);
                    cell.setCursor(Cursor.CLOSED_HAND);
                }
            });

            cell.setOnDragDone((event) -> {
                cell.setCursor(Cursor.OPEN_HAND);
            });

            cell.setOnMousePressed((event) -> {
                if (cell.isEmpty()) {
                } else if (event.isSecondaryButtonDown()) {
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem deleteItem = new MenuItem("Delete");
                    deleteItem.setOnAction(action -> {
                        Integer[] selectedIndices = columnListView.getSelectionModel().getSelectedIndices().toArray(new Integer[0]);
                        Arrays.sort(selectedIndices);
                        for (int i = 0; i < selectedIndices.length; i++) {
                            columnListView.getItems().remove(selectedIndices[i] - i);
                        }
                        isEditing.setValue(true);
                    });

                    // special case prevent showing if one of three base categories in Weighted Mean Plot Sort Table
                    showContextMenu = true;
                    if (reportTableCB.getSelectionModel().getSelectedItem().amWeightedMeanPlotAndSortReport()) {
                        if ((categoryListView.getSelectionModel().getSelectedItem().getDisplayName().compareTo("Time") == 0)
                                || (categoryListView.getSelectionModel().getSelectedItem().getDisplayName().compareTo("Ages") == 0)
                                || (categoryListView.getSelectionModel().getSelectedItem().getDisplayName().compareTo("Raw Ratios") == 0)
                                || (categoryListView.getSelectionModel().getSelectedItem().getDisplayName().compareTo("Corr. Ratios") == 0)) {
                            showContextMenu = false;
                        }
                    }
                    if (showContextMenu) {
                        contextMenu.getItems().addAll(deleteItem);
                    } else {
                        contextMenu.getItems().addAll(new MenuItem("Fixed Column"));
                    }
                    contextMenu.show(cell, event.getScreenX(), event.getScreenY());
                } else {
                    cell.setCursor(Cursor.CLOSED_HAND);
                }
            });

            cell.setOnMouseReleased((event) -> {
                cell.setCursor(Cursor.OPEN_HAND);
            });

            cell.setCursor(Cursor.OPEN_HAND);

            cell.setOnMouseClicked((event) -> {
                //Nothing
            });

            return cell;
        }
    }

    private class StringCellFactory implements Callback<ListView<String>, ListCell<String>> {

        public StringCellFactory() {
        }

        @Override
        public ListCell<String> call(ListView<String> param) {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                public void updateItem(String string, boolean empty) {
                    super.updateItem(string, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(string);
                        setGraphic(null);
                    }

                }

            };
            cell.setOnDragDetected(event -> {

                // Make sure at least one item is selected
                int selectedCount = param.getSelectionModel().getSelectedIndices().size();

                if (selectedCount == 0) {
                    event.consume();
                    return;
                }

                // Initiate a drag-and-drop gesture
                Dragboard dragboard = cell.startDragAndDrop(TransferMode.COPY);
                dragboard.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));

                // Put the the selected items to the dragboard
                List<String> selectedItems = getSelectedExpressionStrings(param);

                ClipboardContent content = new ClipboardContent();
                content.putString(null);
                content.put(STRING_LIST, selectedItems);

                dragboard.setContent(content);

                cell.setCursor(Cursor.CLOSED_HAND);

                event.consume();
            });

            cell.setOnDragDone((event) -> {
                cell.setCursor(Cursor.OPEN_HAND);
            });

            cell.setOnMousePressed((event) -> {
                if (!cell.isEmpty()) {
                    cell.setCursor(Cursor.CLOSED_HAND);
                }
            });

            cell.setOnMouseReleased((event) -> {
                cell.setCursor(Cursor.OPEN_HAND);
            });

            cell.setCursor(Cursor.OPEN_HAND);

            cell.setOnMouseClicked((event) -> {
                //Nothing
            });
            return cell;
        }
    }

    private class ExpressionCellFactory implements Callback<ListView<Expression>, ListCell<Expression>> {

        private final boolean showImage;

        public ExpressionCellFactory() {
            showImage = false;
        }

        public ExpressionCellFactory(boolean showImage) {
            this.showImage = showImage;
        }

        @Override
        public ListCell<Expression> call(ListView<Expression> param) {
            ListCell<Expression> cell = new ListCell<Expression>() {

                @Override
                public void updateItem(Expression expression, boolean empty) {
                    super.updateItem(expression, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        String mainText = expression.buildShortSignatureString();
                        String postPend = (expression.isParameterValue() || (expression.isReferenceMaterialValue())) ? " (see Notes)" : "";
                        setText(mainText + postPend);
                        if (showImage) {
                            ImageView imageView;
                            if (expression.amHealthy()) {
                                imageView = new ImageView(HEALTHY);

                            } else {
                                imageView = new ImageView(UNHEALTHY);
                            }
                            imageView.setFitHeight(12);
                            imageView.setFitWidth(12);
                            setGraphic(imageView);
                        }
                    }
                }
            };
            cell.setOnDragDetected(event -> {
                // Make sure at least one item is selected
                int selectedCount = param.getSelectionModel().getSelectedIndices().size();

                if (selectedCount == 0) {
                    event.consume();
                    return;
                }

                // Initiate a drag-and-drop gesture
                Dragboard dragboard = cell.startDragAndDrop(TransferMode.COPY);
                dragboard.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));

                // Put the the selected items to the dragboard
                List<Expression> selectedItems = getSelectedExpressions(param);
                List<String> selectedItemNames = new ArrayList<>();
                for (Expression exp : selectedItems) {
                    selectedItemNames.add(exp.getName());
                }

                ClipboardContent content = new ClipboardContent();
                content.putString(null);
                content.put(STRING_LIST, selectedItemNames);

                dragboard.setContent(content);

                cell.setCursor(Cursor.CLOSED_HAND);

                event.consume();
            });

            cell.setOnDragDone((event) -> {
                cell.setCursor(Cursor.OPEN_HAND);
            });

            cell.setOnMousePressed((event) -> {
                if (!cell.isEmpty()) {
                    cell.setCursor(Cursor.CLOSED_HAND);
                }
            });

            cell.setOnMouseReleased((event) -> {
                cell.setCursor(Cursor.OPEN_HAND);
            });

            cell.setCursor(Cursor.OPEN_HAND);

            cell.setOnMouseClicked((event) -> {
                //Nothing
            });
            return cell;
        }

    }

    static final DataFormat STRING_LIST = new DataFormat("StringList");

    private List<String> getSelectedExpressionStrings(ListView<String> listView) {
        // Return the list of selected expressions in ArrayList, so it is
        // serializable and can be stored in a Dragboard.
        List<String> list = new ArrayList<>(listView.getSelectionModel().getSelectedItems());

        return list;
    }

    private List<Expression> getSelectedExpressions(ListView<Expression> listView) {
        // Return the list of selected expressions in ArrayList, so it is
        // serializable and can be stored in a Dragboard.
        List<Expression> list = new ArrayList<>(listView.getSelectionModel().getSelectedItems());

        return list;
    }

    @FXML
    private void createCategoryOnAction(ActionEvent event) {
        createCategory();
    }

    private void createCategory() {
        String catName = categoryTextField.getText();

        if (catName.isEmpty()) {
            SquidMessageDialog.showWarningDialog("Please enter a non-empty name.", primaryStageWindow);
        } else {
            List<String> catNames = new ArrayList<>(categoryListView.getItems().size());
            categoryListView.getItems().forEach(cat -> catNames.add(cat.getDisplayName()));
            if (catNames.contains(catName)) {
                SquidMessageDialog.showWarningDialog("A category exists with the specified name.", primaryStageWindow);
            } else {
                SquidReportCategoryInterface cat = SquidReportCategory.createReportCategory(catName);
                categoryListView.getItems().add(cat);
                int catIndex = categoryListView.getItems().indexOf(cat);
                categoryListView.getSelectionModel().select(catIndex);
                categoryListView.scrollTo(catIndex);
                categoryListView.getFocusModel().focus(catIndex);
                isEditing.setValue(true);
                categoryTextField.setText("");
            }
        }
    }

}
