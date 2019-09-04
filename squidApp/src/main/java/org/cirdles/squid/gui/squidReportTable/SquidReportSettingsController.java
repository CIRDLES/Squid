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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import org.cirdles.squid.dialogs.SquidMessageDialog;
import org.cirdles.squid.gui.SquidUI;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategory;
import org.cirdles.squid.squidReports.squidReportCategories.SquidReportCategoryInterface;
import org.cirdles.squid.squidReports.squidReportColumns.SquidReportColumnInterface;
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTable;
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTableInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.utilities.IntuitiveStringComparator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.cirdles.squid.gui.SquidUI.*;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import static org.cirdles.squid.gui.squidReportTable.SquidReportSettingsLauncher.squidReportSettingsWindow;

/**
 * FXML Controller class
 *
 * @author ryanb
 */
public class SquidReportSettingsController implements Initializable {

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


    private final ObjectProperty<Expression> selectedExpression = new SimpleObjectProperty<>();
    private final ObjectProperty<SquidReportCategoryInterface> selectedCategoroy = new SimpleObjectProperty<>();
    private final ObjectProperty<SquidReportColumnInterface> selectedColumn = new SimpleObjectProperty<>();

    ObservableList<Expression> namedExpressions;
    public static Expression expressionToHighlightOnInit = null;

    private TaskInterface task;
    private SquidReportTableInterface squidReportTable;

    //INIT
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        task = squidProject.getTask();
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
            selectedCategoroy.setValue(newValue);
        });
        populateCategoryListView();

        //Squid Report Columns
        columnListView.setCellFactory(new SquidReportColumnInterfaceCellFactory());
        columnListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        columnListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            selectedColumn.setValue(newValue);
        }));
    }

    private void initSelectionActions() {
        selectedCategoroy.addListener(observable -> {
            populateColumnListView();
        });
        selectedColumn.addListener(observable -> {
            populateColumnDetails();
        });
    }

    private void populateCategoryListView() {
        ObservableList<SquidReportCategoryInterface> obList = FXCollections.observableList(SquidReportTable.createDefaultReportCategoriesUnknown(task));
        categoryListView.setItems(obList);
        categoryListView.getSelectionModel().selectFirst();
    }

    private void populateColumnListView() {
        if (selectedCategoroy.getValue() != null) {
            ObservableList<SquidReportColumnInterface> obList = FXCollections.observableList(selectedCategoroy.getValue().getCategoryColumns());
            columnListView.setItems(obList);
            columnListView.getSelectionModel().selectFirst();
        } else {
            columnListView.setItems(null);
        }
    }

    private void populateColumnDetails() {
        if (selectedColumn.getValue() != null) {
            String expName = selectedColumn.getValue().getExpressionName();
            Expression exp = task.getExpressionByName(expName);
            if (exp != null) {
                columnDetailsTextArea.setText(exp.getNotes());
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

    private class SquidReportCategoryInterfaceCellFactory implements Callback<ListView<SquidReportCategoryInterface>, ListCell<SquidReportCategoryInterface>> {

        public SquidReportCategoryInterfaceCellFactory() {
        }

        ;

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

            return cell;
        }
    }

    private class SquidReportColumnInterfaceCellFactory implements Callback<ListView<SquidReportColumnInterface>, ListCell<SquidReportColumnInterface>> {

        public SquidReportColumnInterfaceCellFactory() {
        }

        ;

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
            addDraggableFunctionality(cell);
            return cell;
        }


    }

    private void addDraggableFunctionality(ListCell<Expression> cell) {
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
    }

    @FXML
    private void createCategoryOnAction(ActionEvent event) {
        SquidReportCategoryInterface cat = SquidReportCategory.createReportCategory(categoryTextField.getText());

        if(!categoryListView.getItems().contains(cat)) {
            categoryListView.getItems().add(cat);
            int catIndex = categoryListView.getItems().indexOf(cat);
            categoryListView.getSelectionModel().select(catIndex);
            categoryListView.scrollTo(catIndex);
            categoryListView.getFocusModel().focus(catIndex);
        } else {
            SquidMessageDialog.showWarningDialog("A category exists with the specified name.", squidReportSettingsWindow);
        }

    }

}