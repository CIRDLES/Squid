/*
 * Copyright 2018 James F. Bowring and CIRDLES.org.
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

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.SortedSet;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.cirdles.squid.tasks.expressions.Expression;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import static org.cirdles.squid.gui.SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import static org.cirdles.squid.gui.SquidUI.SQUID_LOGO_SANS_TEXT_URL;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import static org.cirdles.squid.tasks.expressions.operations.Operation.OPERATIONS_MAP;
import java.util.Map;
import javafx.scene.control.Accordion;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import static org.cirdles.squid.gui.SquidUI.OPERATOR_IN_EXPRESSION_LIST_CSS_STYLE_SPECS;
import org.cirdles.squid.tasks.expressions.functions.Function;
import static org.cirdles.squid.tasks.expressions.functions.Function.FUNCTIONS_MAP;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class ExpressionBuilderController implements Initializable {

    private final ObjectProperty<ListCell<Expression>> dragExpressionSource = new SimpleObjectProperty<>();
    private final ObjectProperty<SquidRatiosModel> dragSquidRatioModelSource = new SimpleObjectProperty<>();
    private final ObjectProperty<String> dragOperationOrFunctionSource = new SimpleObjectProperty<>();
    private final ObjectProperty<ExpressionTextNode> dragTextSource = new SimpleObjectProperty<>();

    private final String operationFlagDelimeter = " : ";
    private final String placeholder = " ";// \u2588 ";

    @FXML
    private TextFlow expressionTextFlow;
    @FXML
    private ListView<Expression> builtInExpressionsListView;
    @FXML
    private ListView<Expression> customExpressionsListView;
    @FXML
    private ListView<SquidRatiosModel> ratioExpressionsListView;
    @FXML
    private ListView<String> operationsListView;
    @FXML
    private ListView<String> mathFunctionsListView;
    @FXML
    private ListView<String> squidFunctionsListView;
    @FXML
    private TitledPane builtInExpressionsTitledPane;
    @FXML
    private Accordion expressionsAccordian;
    @FXML
    private ListView<?> constantsListView;
    @FXML
    private ListView<?> referenceMaterialsListView;
    @FXML
    private RadioButton insertLeftRB;
    @FXML
    private ToggleGroup InsertReplaceGroup;
    @FXML
    private RadioButton replaceRB;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initializeExpressionListViews();
        initializeRatiosListView();
        initializeOperationOrFunctionListViews();

        initializeExpressionTextFlow();

        builtInExpressionsTitledPane.setExpanded(true);
        expressionsAccordian.setExpandedPane(builtInExpressionsTitledPane);
    }

    private void initializeExpressionListViews() {
        builtInExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        builtInExpressionsListView.setCursor(Cursor.CLOSED_HAND);
        builtInExpressionsListView.setCellFactory(new expressionCellFactory());

        customExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        customExpressionsListView.setCursor(Cursor.CLOSED_HAND);
        customExpressionsListView.setCellFactory(new expressionCellFactory());

        populateExpressionListViews();
    }

    private void initializeRatiosListView() {
        ratioExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        ratioExpressionsListView.setCursor(Cursor.CLOSED_HAND);
        ratioExpressionsListView.setCellFactory(new expressionTreeCellFactory());

        populateRatiosListView();
    }

    private void initializeOperationOrFunctionListViews() {
        operationsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        operationsListView.setCursor(Cursor.CLOSED_HAND);
        operationsListView.setCellFactory(new operationOrFunctionCellFactory());

        mathFunctionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        mathFunctionsListView.setCursor(Cursor.CLOSED_HAND);
        mathFunctionsListView.setCellFactory(new operationOrFunctionCellFactory());

        squidFunctionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        squidFunctionsListView.setCursor(Cursor.CLOSED_HAND);
        squidFunctionsListView.setCellFactory(new operationOrFunctionCellFactory());

        populateOperationOrFunctionListViews();
    }

    private void initializeExpressionTextFlow() {
        expressionTextFlow.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != expressionTextFlow
                        && !(event.getGestureSource() instanceof ExpressionTextNode)
                        && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.COPY);
                }

                event.consume();
            }
        });

        expressionTextFlow.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    String content = placeholder + db.getString() + placeholder;
                    if ((dragOperationOrFunctionSource.get() != null) && (!db.getString().contains(operationFlagDelimeter))) {
                        // case of function, make a series of objects
                        insertExpressionIntoExpressionTextFlow(content);
                    } else {
                        ExpressionTextNode expressionTextNode = new ExpressionTextNode(content);
                        if (content.contains(operationFlagDelimeter)) {
                            // case of operator, strip out description
                            //content = placeholder + db.getString().split(operationFlagDelimeter)[0] + placeholder;
                            content = db.getString().split(operationFlagDelimeter)[0];
                            expressionTextNode = new OperationTextNode(content);
                        }
                        expressionTextNode.setOrdinalIndex(expressionTextFlow.getChildren().size());
                        expressionTextFlow.getChildren().add(expressionTextNode);
                    }

                    event.setDropCompleted(true);

                    success = true;
                }
                event.setDropCompleted(success);

                event.consume();
                dragExpressionSource.set(null);
                dragOperationOrFunctionSource.set(null);
                dragSquidRatioModelSource.set(null);

                updateExpressionTextFlowChildren();
            }
        });
    }

    private void insertExpressionIntoExpressionTextFlow(String content) {
        String[] funcCall = content.split(" ");
        for (int i = 0; i < funcCall.length; i++) {
            if (funcCall[i].compareTo("") != 0) {
                if ((funcCall[i].compareTo("\u2588") == 0) || (funcCall[i].compareTo(" \u2588") == 0) || (funcCall[i].compareTo("\u2588 ") == 0)) {
                    funcCall[i] = placeholder;
                }
                funcCall[i] = funcCall[i].replaceAll("[(]", " ( ").replaceAll("[)]", " )").replaceAll(",", " , ");
                ExpressionTextNode expressionTextNode = new ExpressionTextNode(funcCall[i]);
                expressionTextNode.setOrdinalIndex(expressionTextFlow.getChildren().size());
                expressionTextFlow.getChildren().add(expressionTextNode);
            }
        }
    }

    private class expressionCellFactory implements Callback<ListView<Expression>, ListCell<Expression>> {

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
                        setText(expression.getName());
                    }
                }
            };

            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                    db.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString("[\"" + cell.getItem().getName() + "\"]");
                    db.setContent(cc);
                    dragExpressionSource.set(cell);
                }
            });

            cell.setCursor(Cursor.CLOSED_HAND);
            return cell;
        }

    }

    private class expressionTreeCellFactory implements Callback<ListView<SquidRatiosModel>, ListCell<SquidRatiosModel>> {

        @Override
        public ListCell<SquidRatiosModel> call(ListView<SquidRatiosModel> param) {
            ListCell<SquidRatiosModel> cell = new ListCell<SquidRatiosModel>() {
                @Override
                public void updateItem(SquidRatiosModel expression, boolean empty) {
                    super.updateItem(expression, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(expression.getRatioName());
                    }
                }
            };

            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                    db.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString("[\"" + cell.getItem().getRatioName() + "\"]");
                    db.setContent(cc);
                    dragSquidRatioModelSource.set(cell.getItem());
                }
            });

            cell.setCursor(Cursor.CLOSED_HAND);
            return cell;
        }

    }

    private class operationOrFunctionCellFactory implements Callback<ListView<String>, ListCell<String>> {

        @Override
        public ListCell<String> call(ListView<String> param) {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                public void updateItem(String expression, boolean empty) {
                    super.updateItem(expression, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(expression);
                    }
                }
            };

            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                    db.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString(cell.getItem());
                    db.setContent(cc);
                    dragOperationOrFunctionSource.set(cell.getItem());
                }
            });

            cell.setCursor(Cursor.CLOSED_HAND);
            return cell;
        }
    }

    private class OperationTextNode extends ExpressionTextNode {

        public OperationTextNode(String text) {
            super(text);
            setStyle(OPERATOR_IN_EXPRESSION_LIST_CSS_STYLE_SPECS);
        }

    }

    private class ExpressionTextNode extends Text {

        /**
         * @return the ordinalIndex
         */
        public double getOrdinalIndex() {
            return ordinalIndex;
        }

        /**
         * @param ordinalIndex the ordinalIndex to set
         */
        public void setOrdinalIndex(double ordinalIndex) {
            this.ordinalIndex = ordinalIndex;
        }

        private String text;
        private double ordinalIndex;
        private boolean popupShowing;

        public ExpressionTextNode(String text) {
            super(text);
            this.text = text;
            this.popupShowing = false;

            setStyle(EXPRESSION_LIST_CSS_STYLE_SPECS);
            setCursor(Cursor.CLOSED_HAND);

            setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getButton() == MouseButton.SECONDARY && event.getEventType() == MouseEvent.MOUSE_CLICKED && !popupShowing) {
                        createExpressionTextNodeContextMenu((ExpressionTextNode) event.getSource()).show((ExpressionTextNode) event.getSource(), event.getScreenX(), event.getScreenY());
                        popupShowing = true;
                    }
                }
            });

            setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    setFill(Color.RED);
                }
            });

            setOnMouseReleased(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    setFill(Color.BLACK);
                }
            });

            setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Dragboard db = startDragAndDrop(TransferMode.COPY);
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString(text);
                    db.setContent(cc);
                    dragTextSource.set((ExpressionTextNode) event.getSource());
                }
            });

            setOnDragOver(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent event) {
                    if (event.getGestureSource() != (ExpressionTextNode) event.getSource()) {
                        event.acceptTransferModes(TransferMode.COPY);
                    }
                    event.consume();
                }
            });

            setOnDragDropped(new EventHandler<DragEvent>() {
                public void handle(DragEvent event) {
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasString()
                            && ((dragExpressionSource.get() != null) || (dragOperationOrFunctionSource.get() != null) || (dragSquidRatioModelSource.get() != null))) {
                        // we have the case of dropping a SOURCE INTO the list
                        ExpressionTextNode expressionTextNode = new ExpressionTextNode(db.getString());
                        if (dragOperationOrFunctionSource.get() != null) {
                            expressionTextNode = new OperationTextNode(db.getString().split(operationFlagDelimeter)[0]);
                        }
                        // setup for logic below
                        expressionTextFlow.getChildren().add(expressionTextNode);
                        dragTextSource.set(expressionTextNode);
                    }
                    // within the list
                    if (db.hasString() && (dragTextSource.get() instanceof ExpressionTextNode)) {
                        // insert left
                        dragTextSource.get().setOrdinalIndex(((ExpressionTextNode) event.getSource()).getOrdinalIndex() - 0.5);
                        if (replaceRB.isSelected()) {
                            // replace
                            expressionTextFlow.getChildren().remove((ExpressionTextNode) event.getSource());
                        }

                        event.setDropCompleted(true);

                        success = true;

                    }
                    event.setDropCompleted(success);
                    event.consume();
                    dragExpressionSource.set(null);
                    dragOperationOrFunctionSource.set(null);
                    dragSquidRatioModelSource.set(null);
                    updateExpressionTextFlowChildren();
                }
            });
        }
    }

    private void updateExpressionTextFlowChildren() {

        // extract and sort
        List<ExpressionTextNode> children = new ArrayList<>();
        boolean fillerExistsFlag = false;
        boolean fillerExistsFlagSaved = false;
        for (Node etn : expressionTextFlow.getChildren()) {
            fillerExistsFlag = ((ExpressionTextNode) etn).getText().compareTo(placeholder) == 0;
            if (!(fillerExistsFlag && fillerExistsFlagSaved)) {
                children.add((ExpressionTextNode) etn);
            }
            fillerExistsFlagSaved = fillerExistsFlag;
        }
        // sort
        children.sort(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                int retVal = 0;
                if (o1 instanceof ExpressionTextNode && o2 instanceof ExpressionTextNode) {
                    retVal = Double.compare(((ExpressionTextNode) o1).getOrdinalIndex(), ((ExpressionTextNode) o2).getOrdinalIndex());
                }
                return retVal;
            }
        });

        // reset ordinals to integer values
        double ordIndex = 0;
        for (ExpressionTextNode etn : children) {
            etn.setOrdinalIndex(ordIndex);
            etn.setFill(Color.BLACK);
            ordIndex++;
        }

        expressionTextFlow.getChildren().clear();
        expressionTextFlow.getChildren().addAll(children);
    }

    private ContextMenu createExpressionTextNodeContextMenu(ExpressionTextNode etn) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem menuItem = new MenuItem("Remove expression.");
        menuItem.setOnAction((evt) -> {
            expressionTextFlow.getChildren().remove(etn);
            updateExpressionTextFlowChildren();
        });
        contextMenu.getItems().add(menuItem);

        menuItem = new MenuItem("Move left.");
        menuItem.setOnAction((evt) -> {
            etn.setOrdinalIndex(etn.getOrdinalIndex() - 1.5);
            updateExpressionTextFlowChildren();
        });
        contextMenu.getItems().add(menuItem);

        menuItem = new MenuItem("Move right.");
        menuItem.setOnAction((evt) -> {
            etn.setOrdinalIndex(etn.getOrdinalIndex() + 1.5);
            updateExpressionTextFlowChildren();
        });
        contextMenu.getItems().add(menuItem);

        contextMenu.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                etn.popupShowing = false;
            }
        });

        return contextMenu;
    }

    private void populateExpressionListViews() {
        SortedSet<Expression> namedExpressions = squidProject.getTask().getTaskExpressionsOrdered();

        List<Expression> sortedBuiltInExpressionsList = new ArrayList<>();
        List<Expression> sortedCustomExpressionsList = new ArrayList<>();

        for (Expression exp : namedExpressions) {
            if (exp.getExpressionTree().isSquidSpecialUPbThExpression() && exp.amHealthy()) {
                sortedBuiltInExpressionsList.add(exp);
            } else if (!exp.getExpressionTree().isSquidSpecialUPbThExpression() && exp.amHealthy()) {
                sortedCustomExpressionsList.add(exp);
            }
        }

        sortedBuiltInExpressionsList.sort(
                new Comparator<Expression>() {
            @Override
            public int compare(Expression exp1, Expression exp2) {
                int retVal = 0;
                retVal = exp1.getName().compareToIgnoreCase(exp2.getName());
                return retVal;
            }
        }
        );

        ObservableList<Expression> items = FXCollections.observableArrayList(sortedBuiltInExpressionsList);
        builtInExpressionsListView.setItems(items);

        sortedCustomExpressionsList.sort(
                new Comparator<Expression>() {
            @Override
            public int compare(Expression exp1, Expression exp2) {
                int retVal = 0;
                retVal = exp1.getName().compareToIgnoreCase(exp2.getName());
                return retVal;
            }
        }
        );

        items = FXCollections.observableArrayList(sortedCustomExpressionsList);
        customExpressionsListView.setItems(items);
    }

    private void populateRatiosListView() {
        List<SquidRatiosModel> ratiosList = squidProject.getTask().getSquidRatiosModelList();

        ObservableList<SquidRatiosModel> items = FXCollections.observableArrayList(ratiosList);
        ratioExpressionsListView.setItems(items);
    }

    private void populateOperationOrFunctionListViews() {
        List<String> operationStrings = new ArrayList<>();
        for (Map.Entry<String, String> op : OPERATIONS_MAP.entrySet()) {
            operationStrings.add(op.getKey() + operationFlagDelimeter + op.getValue());
        }

        ObservableList<String> items = FXCollections.observableArrayList(operationStrings);
        operationsListView.setItems(items);

        // Math Functions
        List<String> functionStrings = new ArrayList<>();
        for (Map.Entry<String, String> op : FUNCTIONS_MAP.entrySet()) {
            int argumentCount = Function.operationFactory(op.getValue()).getArgumentCount();
            // space-delimited
            String args = op.getKey() + " ( ";
            for (int i = 0; i < argumentCount; i++) {
                args += "ARG-" + i + (i < (argumentCount - 1) ? " , " : "");
            }
            args += " )";

            functionStrings.add(args);
        }

        items = FXCollections.observableArrayList(functionStrings);
        mathFunctionsListView.setItems(items);
    }

}
