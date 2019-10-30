/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.squidReportTable;

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
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategory;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategoryInterface;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumn;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumnInterface;
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTable;
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTableInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.utilities.IntuitiveStringComparator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.cirdles.squid.gui.SquidUI.*;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.gui.SquidUIController.squidReportTableLauncher;

/**
 * FXML Controller class
 *
 * @author ryanb
 */
public class SquidReportSettingsController implements Initializable {

    @FXML
    public ChoiceBox<SquidReportTableInterface> reportTableCB;
    @FXML
    public Button refMatUnknownToggleButton;
    @FXML
    public Button viewButton;
    @FXML
    private SplitPane mainPane;
    @FXML
    private ToggleGroup expressionsSortToggleGroup;
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
    private TitledPane referenceMaterialsTitledPane;
    @FXML
    private ListView<Expression> referenceMaterialsListView;
    @FXML
    private TitledPane parametersTitledPane;
    @FXML
    private ListView<Expression> parametersListView;
    @FXML
    private TitledPane brokenExpressionsTitledPane;
    @FXML
    private ListView<Expression> brokenExpressionsListView;
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
    private final ObjectProperty<SquidReportColumnInterface> selectedColumn = new SimpleObjectProperty<>();

    ObservableList<Expression> namedExpressions;
    public static Expression expressionToHighlightOnInit = null;
    private TaskInterface task;

    private boolean isRefMat;
    private ObjectProperty<Boolean> isEditing = new SimpleObjectProperty<>();
    private ObjectProperty<Boolean> isDefault = new SimpleObjectProperty<>();

    //INIT
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        task = squidProject.getTask();
        initTask();

        isRefMat = true;

        initReportTableCB();

        initEditing();
        initDefault();
        initCategoryTextField();

        //squidReportTable = task.getSquidReportTable();
        // update
        task.setupSquidSessionSpecsAndReduceAndReport(false);

        initSelectionActions();
        initListViews();

        if (expressionToHighlightOnInit != null) {
            selectInAllPanes(expressionToHighlightOnInit, true);
            expressionToHighlightOnInit = null;
        } else if (!customExpressionsListView.getItems().isEmpty()) {
            selectInAllPanes(customExpressionsListView.getItems().get(0), true);
        }
        buttonHBox.getChildren().removeAll(saveButton, restoreButton);
    }

    private void initCategoryTextField() {
        categoryTextField.setOnKeyPressed(val -> {
            if (val.getCode() == KeyCode.ENTER)
                createCategory();
        });
    }

    private void processButtons() {
        if (isEditing.getValue()) {
            buttonHBox.getChildren().setAll(viewButton, saveButton, restoreButton);
        } else if (isDefault.getValue()) {
            buttonHBox.getChildren().setAll(refMatUnknownToggleButton, reportTableCB, viewButton, newButton, copyButton,
                    exportButton, importButton);
        } else {
            buttonHBox.getChildren().setAll(refMatUnknownToggleButton, reportTableCB, viewButton, newButton, copyButton,
                    renameButton, deleteButton, exportButton, importButton);
        }
    }

    private void initEditing() {
        isEditing.setValue(false);
        isEditing.addListener(ob -> processButtons());
    }

    private void initDefault() {
        isDefault.setValue(false);
        isDefault.addListener(ob -> processButtons());
    }

    private void initTask() {
        List<SquidReportTableInterface> refMatTables = task.getSquidReportTablesRefMat();
        if (refMatTables.isEmpty()) {
            refMatTables.add(SquidReportTable.createDefaultSquidReportTableRefMat(task));
        }
        List<SquidReportTableInterface> unknownTables = task.getSquidReportTablesUnknown();
        if (unknownTables.isEmpty()) {
            unknownTables.add(SquidReportTable.createDefaultSquidReportTableUnknown(task));
        }
    }

    private void initReportTableCB() {
        reportTableCB.setConverter(new StringConverter<SquidReportTableInterface>() {
            @Override
            public String toString(SquidReportTableInterface object) {
                return object.getReportTableName();
            }

            @Override
            public SquidReportTableInterface fromString(String string) {
                return null;
            }
        });
        reportTableCB.getSelectionModel().selectedItemProperty().addListener(param -> {
            populateCategoryListView();
            categoryListView.getSelectionModel().selectFirst();
        });
        populateSquidReportTableChoiceBox();
        reportTableCB.getSelectionModel().selectFirst();
    }

    private void populateSquidReportTableChoiceBox() {
        reportTableCB.getItems().setAll(FXCollections.observableArrayList(isRefMat ? task.getSquidReportTablesRefMat()
                : task.getSquidReportTablesUnknown()));
    }

    @FXML
    private void expressionSortToggleAction(ActionEvent event) {
        String flag = ((RadioButton) event.getSource()).getId();
        orderExpressionListsByFlag(flag);
    }

    private void orderExpressionListsByFlag(String flag) {
        orderListViewByFlag(customExpressionsListView, flag);
        orderListViewByFlag(nuSwitchedExpressionsListView, flag);
        orderListViewByFlag(builtInExpressionsListView, flag);
        orderListViewByFlag(brokenExpressionsListView, flag);

        // special cases
        orderListViewByFlag(referenceMaterialsListView, "NAME");
        orderListViewByFlag(parametersListView, "NAME");
    }

    private void orderListViewByFlag(ListView<Expression> listView, String flag) {
        ObservableList<Expression> items = listView.getItems();
        IntuitiveStringComparator<String> intuitiveStringComparator = new IntuitiveStringComparator<>();

        switch (flag) {
            case "EXEC":
                listView.setItems(items.sorted((exp1, exp2) -> {
                    if ((exp1.amHealthy() && exp2.amHealthy()) || (!exp1.amHealthy() && !exp2.amHealthy())) {
                        return namedExpressions.indexOf(exp1) - namedExpressions.indexOf(exp2);
                    } else if (!exp1.amHealthy() && exp2.amHealthy()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }));
                break;

            case "TARGET":
                // order by ConcRefMat then RU then R then U
                listView.setItems(items.sorted((exp1, exp2) -> {
                    // ConcRefMat
                    if (exp1.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation()
                            && !exp2.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation()) {
                        return -1;
                        // ConcRefMat
                    } else if (!exp1.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation()
                            && exp2.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation()) {
                        return 1;
                        //RU
                    } else if (exp1.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                            && exp1.getExpressionTree().isSquidSwitchSAUnknownCalculation()
                            && exp2.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                            && exp2.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                        return intuitiveStringComparator.compare(exp1.getName(), exp2.getName());
                        // RU
                    } else if (exp1.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                            && exp1.getExpressionTree().isSquidSwitchSAUnknownCalculation()
                            && (!exp2.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                            || !exp2.getExpressionTree().isSquidSwitchSAUnknownCalculation())) {
                        return -1;
                        // R
                    } else if (exp1.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                            && !exp1.getExpressionTree().isSquidSwitchSAUnknownCalculation()
                            && exp2.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                            && !exp2.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                        return intuitiveStringComparator.compare(exp1.getName(), exp2.getName());
                        // R
                    } else if (exp1.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                            && !exp1.getExpressionTree().isSquidSwitchSAUnknownCalculation()
                            && !exp2.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                            && exp2.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                        return -1;
                        // U
                    } else if (!exp1.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                            && exp1.getExpressionTree().isSquidSwitchSAUnknownCalculation()
                            && !exp2.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()
                            && exp2.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                        return intuitiveStringComparator.compare(exp1.getName(), exp2.getName());
                    } else {
                        return 1;
                    }

                }));
                break;

            default://"NAME":
                listView.setItems(items.sorted((exp1, exp2) -> {
                    return exp1.getName().toLowerCase().compareTo(exp2.getName().toLowerCase());
                }));
                break;
        }
    }

    private void customizeBrokenExpressionsTitledPane() {
        if ((brokenExpressionsListView.getItems() == null) || (brokenExpressionsListView.getItems().isEmpty())) {
            brokenExpressionsTitledPane.setStyle("-fx-font-size: 12; -fx-text-fill: black; -fx-font-family: SansSerif;");
        } else {
            brokenExpressionsTitledPane.setStyle("-fx-font-size: 12; -fx-text-fill: red; -fx-font-family: SansSerif;");
        }
    }

    private void initListViews() {
        //EXPRESSIONS
        brokenExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        brokenExpressionsListView.setCellFactory(new ExpressionCellFactory(true));
        brokenExpressionsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //Listener to update the filter tab when a new value is selected in the broken expression category
        brokenExpressionsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Expression>() {
            @Override
            public void changed(ObservableValue<? extends Expression> observable, Expression oldValue, Expression newValue) {
                if (newValue != null) {

                    selectedExpression.set(newValue);

                    selectInAllPanes(newValue, false);
                }
                customizeBrokenExpressionsTitledPane();
            }
        });
        customizeBrokenExpressionsTitledPane();

        nuSwitchedExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        nuSwitchedExpressionsListView.setCellFactory(new ExpressionCellFactory());
        nuSwitchedExpressionsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
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
        builtInExpressionsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
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
        customExpressionsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        customExpressionsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Expression>() {
            @Override
            public void changed(ObservableValue<? extends Expression> observable, Expression oldValue, Expression newValue) {
                if (newValue != null) {

                    selectedExpression.set(newValue);


                    selectInAllPanes(newValue, false);
                }
            }
        });

        // REFERERENCE MATERIAL VALUES
        referenceMaterialsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        referenceMaterialsListView.setCellFactory(new ExpressionCellFactory());
        referenceMaterialsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        referenceMaterialsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Expression>() {
            @Override
            public void changed(ObservableValue<? extends Expression> observable, Expression oldValue, Expression newValue) {
                if (newValue != null) {

                    selectedExpression.set(newValue);

                    selectInAllPanes(newValue, false);
                }
            }
        });
        // PARAMETER VALUES
        parametersListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        parametersListView.setCellFactory(new ExpressionCellFactory());
        parametersListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        parametersListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Expression>() {
            @Override
            public void changed(ObservableValue<? extends Expression> observable, Expression oldValue, Expression newValue) {
                if (newValue != null) {

                    selectedExpression.set(newValue);

                    selectInAllPanes(newValue, false);
                }
            }
        });

        populateExpressionListViews();

        //Squid Report Categories
        categoryListView.setCellFactory(new SquidReportCategoryInterfaceCellFactory());
        categoryListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        categoryListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedCategory.setValue(newValue);
        });
        populateCategoryListView();
        categoryListView.getSelectionModel().selectFirst();

        //Squid Report Columns
        columnListView.setCellFactory(new SquidReportColumnInterfaceCellFactory());
        columnListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        columnListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            selectedColumn.setValue(newValue);
        }));
        columnListView.setOnDragOver(event -> {
            if (selectedCategory.getValue() != null && event.getDragboard() != null && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
        });
        columnListView.setOnDragDropped(event -> {
            boolean success = false;
            ObservableList<SquidReportColumnInterface> items = columnListView.getItems();
            if (event.getTransferMode().equals(TransferMode.COPY)) {
                SquidReportColumnInterface col = SquidReportColumn.createSquidReportColumn(event.getDragboard().getString());
                items.add(col);
                columnListView.getSelectionModel().select(col);
                success = true;
                isEditing.setValue(true);
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void initSelectionActions() {
        selectedCategory.addListener(observable -> {
            if (selectedCategory.getValue() != null) {
                populateColumnListView();
                columnListView.getSelectionModel().selectFirst();
            }
        });
        selectedColumn.addListener(observable -> {
            if (selectedColumn.getValue() != null) {
                populateColumnDetails();
            }
        });
    }

    private void populateCategoryListView() {
        ObservableList<SquidReportCategoryInterface> obList = FXCollections.observableArrayList(reportTableCB.getSelectionModel().getSelectedItem().getReportCategories());
        categoryListView.setItems(obList);
    }

    private void populateColumnListView() {
        ObservableList<SquidReportColumnInterface> obList = FXCollections.observableList(selectedCategory.getValue().getCategoryColumns());
        columnListView.setItems(obList);
    }

    private void populateColumnDetails() {
        if (selectedColumn.getValue() != null) {
            String expName = selectedColumn.getValue().getExpressionName();
            ExpressionTreeInterface exp = task.findNamedExpression(expName);
            if (exp != null) {
                columnDetailsTextArea.setText("");
            } else {
                columnDetailsTextArea.setText("Could not locate expression tree");
            }
        } else {
            columnDetailsTextArea.setText("");
        }
    }

    private void selectInAllPanes(Expression exp, boolean scrollIfAlreadySelected) {
        //If nothing is selected or the selected value is not the new one
        if (brokenExpressionsListView.getSelectionModel().getSelectedItem() == null
                || !brokenExpressionsListView.getSelectionModel().getSelectedItem().equals(exp)) {
            //Clear selection
            brokenExpressionsListView.getSelectionModel().clearSelection();
            //If the new value is on this pane then select it
            if (brokenExpressionsListView.getItems().contains(exp)) {

                brokenExpressionsListView.getSelectionModel().select(exp);
                brokenExpressionsListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(brokenExpressionsTitledPane);
            }
        } else {
            if (scrollIfAlreadySelected) {
                brokenExpressionsListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(brokenExpressionsTitledPane);
            }
        }

        //Same thing for the other panes
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

        if (referenceMaterialsListView.getSelectionModel().getSelectedItem() == null
                || !referenceMaterialsListView.getSelectionModel().getSelectedItem().equals(exp)) {
            referenceMaterialsListView.getSelectionModel().clearSelection();
            if (referenceMaterialsListView.getItems().contains(exp)) {
                referenceMaterialsListView.getSelectionModel().select(exp);
                referenceMaterialsListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(referenceMaterialsTitledPane);
            }
        } else {
            if (scrollIfAlreadySelected) {
                referenceMaterialsListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(referenceMaterialsTitledPane);
            }
        }

        if (parametersListView.getSelectionModel().getSelectedItem() == null
                || !parametersListView.getSelectionModel().getSelectedItem().equals(exp)) {
            parametersListView.getSelectionModel().clearSelection();
            if (parametersListView.getItems().contains(exp)) {
                parametersListView.getSelectionModel().select(exp);
                parametersListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(parametersTitledPane);
            }
        } else {
            if (scrollIfAlreadySelected) {
                parametersListView.scrollTo(exp);
                expressionsAccordion.setExpandedPane(parametersTitledPane);
            }
        }
    }

    //POPULATE LISTS
    private void populateExpressionListViews() {
        namedExpressions = FXCollections.observableArrayList(task.getTaskExpressionsOrdered());

        List<Expression> sortedNUSwitchedExpressionsList = new ArrayList<>();
        List<Expression> sortedBuiltInExpressionsList = new ArrayList<>();
        List<Expression> sortedCustomExpressionsList = new ArrayList<>();
        List<Expression> sortedBrokenExpressionsList = new ArrayList<>();
        List<Expression> sortedReferenceMaterialValuesList = new ArrayList<>();
        List<Expression> sortedParameterValuesList = new ArrayList<>();

        for (Expression exp : namedExpressions) {
            if (!exp.getExpressionTree().isSquidSwitchSCSummaryCalculation() && (exp.getExpressionTree().isSquidSwitchSAUnknownCalculation() != isRefMat ||
                    exp.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation() == isRefMat)) {
                if (exp.amHealthy() && exp.isSquidSwitchNU()
                        && !exp.aliasedExpression()) {
                    sortedNUSwitchedExpressionsList.add(exp);
                } else if (exp.isReferenceMaterialValue() && exp.amHealthy()) {
                    sortedReferenceMaterialValuesList.add(exp);
                } else if (exp.isParameterValue() && exp.amHealthy()) {
                    sortedParameterValuesList.add(exp);
                } else if (exp.getExpressionTree().isSquidSpecialUPbThExpression()
                        && exp.amHealthy()
                        && !exp.isSquidSwitchNU()
                        && !exp.aliasedExpression()) {
                    sortedBuiltInExpressionsList.add(exp);
                } else if (exp.isCustom() && exp.amHealthy()) {
                    sortedCustomExpressionsList.add(exp);
                } else if (!exp.amHealthy()) {
                    sortedBrokenExpressionsList.add(exp);
                }
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

        items = FXCollections.observableArrayList(sortedBrokenExpressionsList);
        brokenExpressionsListView.setItems(null);
        brokenExpressionsListView.setItems(items);
        customizeBrokenExpressionsTitledPane();

        items = FXCollections.observableArrayList(sortedReferenceMaterialValuesList);
        referenceMaterialsListView.setItems(null);
        referenceMaterialsListView.setItems(items);

        items = FXCollections.observableArrayList(sortedParameterValuesList);
        parametersListView.setItems(null);
        parametersListView.setItems(items);

        // sort everyone
        String flag = ((RadioButton) expressionsSortToggleGroup.getSelectedToggle()).getId();
        orderExpressionListsByFlag(flag);

    }

    private List<String> getNamesOfTables() {
        List<SquidReportTableInterface> tables = isRefMat ? task.getSquidReportTablesRefMat() : task.getSquidReportTablesUnknown();
        List<String> namesOfTables = new ArrayList<>(tables.size());
        for (SquidReportTableInterface table : tables) {
            namesOfTables.add(table.getReportTableName());
        }
        return namesOfTables;
    }

    private List<SquidReportTableInterface> getTables() {
        return isRefMat ? task.getSquidReportTablesRefMat()
                : task.getSquidReportTablesUnknown();
    }

    private SquidReportTableInterface createSquidReportTable() {
        SquidReportTableInterface table = SquidReportTable.createDefaultSquidReportTableRefMat(task);
        table.setReportCategories(new LinkedList<>(categoryListView.getItems()));
        table.setReportTableName(reportTableCB.getSelectionModel().getSelectedItem().getReportTableName());
        return table;
    }

    @FXML
    public void viewOnAction(ActionEvent actionEvent) {
        SquidReportTableInterface table = createSquidReportTable();
        SquidReportTableLauncher.ReportTableTab tab = (isRefMat) ? SquidReportTableLauncher.ReportTableTab.refMatCustom
                : SquidReportTableLauncher.ReportTableTab.unknownCustom;
        squidReportTableLauncher.launch(tab, table);
    }

    @FXML
    public void refMatUnknownToggleButton(ActionEvent actionEvent) {
        if (isRefMat) {
            isRefMat = false;
            refMatUnknownToggleButton.setText("RefMat");
        } else {
            isRefMat = true;
            refMatUnknownToggleButton.setText("Unknown");
        }
        populateSquidReportTableChoiceBox();
        reportTableCB.getSelectionModel().selectFirst();
        populateExpressionListViews();
    }

    @FXML
    private void newOnAction(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("name goes here");
        dialog.setTitle("Squid Report Setting Name");
        dialog.setHeaderText("Enter a name for the Report Setting");
        dialog.setContentText("Name:");

        dialog.showAndWait().ifPresent(name -> {

            if (getNamesOfTables().contains(name)) {
                SquidMessageDialog.showWarningDialog("A Squid Report Setting with the name you entered already exists. Aborting.", primaryStageWindow);
            } else {
                SquidReportTableInterface table = SquidReportTable.createEmptySquidReportTable(name);
                reportTableCB.getItems().add(table);
                reportTableCB.getSelectionModel().select(table);
                isEditing.setValue(true);
            }
        });
    }

    @FXML
    private void copyOnAction(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("name goes here");
        dialog.setTitle("Squid Report Setting Name");
        dialog.setHeaderText("Enter a name for the Report Setting Copy");
        dialog.setContentText("Name:");
        dialog.showAndWait().ifPresent(name -> {

            if (getNamesOfTables().contains(name)) {
                SquidMessageDialog.showWarningDialog("A Squid Report Setting with the name you entered already exists. Aborting.", primaryStageWindow);
            } else {
                SquidReportTableInterface copy = createSquidReportTable();
                reportTableCB.getItems().add(copy);
                reportTableCB.getSelectionModel().select(copy);
                isEditing.setValue(true);
            }
        });
    }

    @FXML
    private void renameOnAction(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("new name goes here");
        dialog.setTitle("Squid Report Setting Name");
        dialog.setHeaderText("Enter a new name for the Report Setting");
        dialog.setContentText("Name:");
        dialog.showAndWait().ifPresent(name -> {
            if (getNamesOfTables().contains(name)) {
                SquidMessageDialog.showWarningDialog("A Squid Report Setting with the name you entered already exists. Aborting.", primaryStageWindow);
            } else {
                reportTableCB.getSelectionModel().getSelectedItem().setReportTableName(name);
            }
        });
    }


    @FXML
    private void restoreOnAction(ActionEvent event) {
        populateCategoryListView();
        categoryListView.getSelectionModel().selectFirst();
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
            File file = FileHandler.selectSquidReportSettingsXMLFile(primaryStageWindow);
            if (file != null) {
                reportTableCB.getSelectionModel().getSelectedItem().serializeXMLObject(file.getAbsolutePath());
            }
        } catch (Exception e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), primaryStageWindow);
        }
    }

    @FXML
    private void importOnAction(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.selectSquidReportSettingsXMLFile(primaryStageWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), primaryStageWindow);
        }
        if (file != null) {
            SquidReportTableInterface temp = SquidReportTable.createEmptySquidReportTable("");
            final SquidReportTableInterface table = (SquidReportTableInterface) ((SquidReportTable) temp).readXMLObject(file.getAbsolutePath(), false);
            if (table != null) {
                final List<SquidReportTableInterface> tables = getTables();
                if (tables.contains(table)) {
                    ButtonType replace = new ButtonType("Replace");
                    ButtonType rename = new ButtonType("Rename");
                    Alert alert = new Alert(Alert.AlertType.WARNING,
                            "A Squid Report Setting already exists with this name. What do you want to do?",
                            replace,
                            rename,
                            ButtonType.CANCEL
                    );
                    alert.setX(SquidUI.primaryStageWindow.getX() + (SquidUI.primaryStageWindow.getWidth() - 200) / 2);
                    alert.setY(SquidUI.primaryStageWindow.getY() + (SquidUI.primaryStageWindow.getHeight() - 150) / 2);
                    alert.showAndWait().ifPresent((t) -> {
                        if (t.equals(replace)) {

                        } else if (t.equals(rename)) {
                            TextInputDialog dialog = new TextInputDialog(table.getReportTableName());
                            dialog.setTitle("Rename");
                            dialog.setHeaderText("Rename " + table.getReportTableName());
                            dialog.setContentText("Enter the new squid report settings name:");
                            Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
                            TextField newName = null;
                            for (Node n : dialog.getDialogPane().getChildren()) {
                                if (n instanceof TextField) {
                                    newName = (TextField) n;
                                }
                            }
                            if (okBtn != null && newName != null) {
                                newName.textProperty().addListener((observable, oldValue, newValue) -> {
                                    table.setReportTableName(newValue);
                                    okBtn.setDisable(tables.contains(table) || newValue.isEmpty());
                                });
                            }
                            dialog.setX(SquidUI.primaryStageWindow.getX() + (SquidUI.primaryStageWindow.getWidth() - 200) / 2);
                            dialog.setY(SquidUI.primaryStageWindow.getY() + (SquidUI.primaryStageWindow.getHeight() - 150) / 2);
                            Optional<String> result = dialog.showAndWait();
                            if (result.isPresent()) {
                                table.setReportTableName(result.get());
                                tables.add(table);
                                populateSquidReportTableChoiceBox();
                                reportTableCB.getSelectionModel().select(table);
                            }
                        }
                    });
                } else {
                    tables.add(table);
                    populateSquidReportTableChoiceBox();
                    reportTableCB.getSelectionModel().select(table);
                }
            }
        }
    }

    @FXML
    private void saveOnAction(ActionEvent event) {
        SquidReportTableInterface table = createSquidReportTable();
        List<SquidReportTableInterface> list;
        if (isRefMat) {
            list = task.getSquidReportTablesRefMat();
        } else {
            list = task.getSquidReportTablesUnknown();
        }
        int location = list.indexOf(table);
        if (location > 0) {
            list.set(location, table);
        } else {
            list.add(table);
        }
        isEditing.setValue(false);
    }

    private class SquidReportCategoryInterfaceCellFactory implements Callback<ListView<SquidReportCategoryInterface>, ListCell<SquidReportCategoryInterface>> {

        public SquidReportCategoryInterfaceCellFactory() {
        }

        @Override
        public ListCell<SquidReportCategoryInterface> call(ListView<SquidReportCategoryInterface> param) {
            ListCell<SquidReportCategoryInterface> cell = new ListCell<SquidReportCategoryInterface>() {
                @Override
                public void updateItem(SquidReportCategoryInterface category, boolean empty) {
                    super.updateItem(category, empty);
                    if (!empty) {
                        setText(category.getDisplayName());
                    } else {
                        setText(null);
                    }
                }
            };
            cell.setOnDragOver(event -> {
                if (event.getDragboard().hasString() && event.getGestureSource() != cell) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            cell.setOnDragEntered(event -> {
                if (event.getGestureSource() != cell &&
                        event.getDragboard().hasString()) {
                    cell.setOpacity(0.3);
                }
            });

            cell.setOnDragExited(event -> {
                if (event.getGestureSource() != cell &&
                        event.getDragboard().hasString()) {
                    cell.setOpacity(1);
                }
            });

            cell.setOnDragDropped(event -> {
                boolean success = false;

                ObservableList<SquidReportCategoryInterface> items = categoryListView.getItems();
                SquidReportCategoryInterface cat = null;
                for (int i = 0; cat == null && i < items.size(); i++) {
                    if (items.get(i).getDisplayName().equals(event.getDragboard().getString())) {
                        cat = items.get(i);
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
                    }
                }

                event.setDropCompleted(success);
            });

            cell.setOnDragDetected(event -> {
                selectedCategory.setValue(cell.getItem());
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString(cell.getItem().getDisplayName());
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
                        categoryListView.getItems().remove(cell.getItem());
                    });
                    contextMenu.getItems().addAll(deleteItem);

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
                    } else {
                        setText(null);
                    }
                }
            };
            cell.setOnDragOver(event -> {
                if (event.getDragboard().hasString() && event.getGestureSource() != cell) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            });

            cell.setOnDragEntered(event -> {
                if (event.getGestureSource() != cell &&
                        event.getDragboard().hasString()) {
                    cell.setOpacity(0.3);
                }
            });

            cell.setOnDragExited(event -> {
                if (event.getGestureSource() != cell &&
                        event.getDragboard().hasString()) {
                    cell.setOpacity(1);
                }
            });

            cell.setOnDragDropped(event -> {
                boolean success = false;
                ObservableList<SquidReportColumnInterface> items = columnListView.getItems();
                if (event.getTransferMode().equals(TransferMode.COPY)) {
                    SquidReportColumnInterface col = SquidReportColumn.createSquidReportColumn(event.getDragboard().getString());
                    if (cell.getItem() != null) {
                        items.add(items.indexOf(cell.getItem()), col);
                    } else {
                        items.add(col);
                    }
                    columnListView.getSelectionModel().select(col);
                    success = true;
                    event.consume();
                    isEditing.setValue(true);
                } else {
                    SquidReportColumnInterface col = null;
                    for (int i = 0; col == null && i < items.size(); i++) {
                        if (items.get(i).getExpressionName().equals(event.getDragboard().getString())) {
                            col = items.get(i);
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
                        columnListView.getItems().remove(cell.getItem());
                    });
                    contextMenu.getItems().addAll(deleteItem);

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
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                    db.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString(cell.getItem().getName());
                    db.setContent(cc);
                    cell.setCursor(Cursor.CLOSED_HAND);
                }
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

    @FXML
    private void createCategoryOnAction(ActionEvent event) {
        createCategory();
    }

    private void createCategory() {
        SquidReportCategoryInterface cat = SquidReportCategory.createReportCategory(categoryTextField.getText());

        if (!categoryListView.getItems().contains(cat)) {
            categoryListView.getItems().add(cat);
            int catIndex = categoryListView.getItems().indexOf(cat);
            categoryListView.getSelectionModel().select(catIndex);
            categoryListView.scrollTo(catIndex);
            categoryListView.getFocusModel().focus(catIndex);
            isEditing.setValue(true);
            categoryTextField.setText("");
        } else {
            SquidMessageDialog.showWarningDialog("A category exists with the specified name.", primaryStageWindow);
        }
    }

}