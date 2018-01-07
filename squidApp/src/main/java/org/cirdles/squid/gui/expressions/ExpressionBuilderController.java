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
package org.cirdles.squid.gui.expressions;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedSet;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.cirdles.squid.gui.SquidUI;
import static org.cirdles.squid.gui.SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.SquidUI.OPERATOR_IN_EXPRESSION_LIST_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.SquidUI.SQUID_LOGO_SANS_TEXT_URL;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.functions.Function;
import static org.cirdles.squid.tasks.expressions.functions.Function.FUNCTIONS_MAP;
import static org.cirdles.squid.tasks.expressions.operations.Operation.OPERATIONS_MAP;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class ExpressionBuilderController implements Initializable {

    private final ObjectProperty<ListCell<Expression>> dragExpressionSource = new SimpleObjectProperty<>();
    private final ObjectProperty<SquidRatiosModel> dragSquidRatioModelSource = new SimpleObjectProperty<>();
    private final ObjectProperty<String> dragOperationOrFunctionSource = new SimpleObjectProperty<>();
    private final ObjectProperty<String> dragNumberSource = new SimpleObjectProperty<>();
    private final ObjectProperty<ExpressionTextNode> dragTextSource = new SimpleObjectProperty<>();

    private final String operationFlagDelimeter = " : ";
    private final String numberString = "NUMBER";
    private final String placeholder = " ";// \u2588 ";

    private List<List<ExpressionTextNode>> redoListForExpressionTextFlow = new ArrayList<>();

    @FXML
    private TextFlow expressionTextFlow;
    @FXML
    private ListView<Expression> nuSwitchedExpressionsListView;
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
    private ListView<String> constantsListView;
    @FXML
    private ListView<?> referenceMaterialsListView;
    @FXML
    private TitledPane builtInExpressionsTitledPane;
    @FXML
    private Accordion expressionsAccordian;
    @FXML
    private ToggleGroup InsertReplaceGroup;
    @FXML
    private RadioButton replaceRB;
    @FXML
    private TextArea expressionAuditTextArea;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initializeExpressionListViews();
        initializeRatiosListView();
        initializeOperationOrFunctionListViews();
        initializeNumberListViews();

        initializeExpressionTextFlow();

        builtInExpressionsTitledPane.setExpanded(true);
        expressionsAccordian.setExpandedPane(builtInExpressionsTitledPane);
    }

    private void initializeExpressionListViews() {
        nuSwitchedExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        nuSwitchedExpressionsListView.setCursor(Cursor.CLOSED_HAND);
        nuSwitchedExpressionsListView.setCellFactory(new expressionCellFactory());

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
        operationsListView.setCellFactory(new StringCellFactory(dragOperationOrFunctionSource));

        mathFunctionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        mathFunctionsListView.setCursor(Cursor.CLOSED_HAND);
        mathFunctionsListView.setCellFactory(new StringCellFactory(dragOperationOrFunctionSource));

        squidFunctionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        squidFunctionsListView.setCursor(Cursor.CLOSED_HAND);
        squidFunctionsListView.setCellFactory(new StringCellFactory(dragOperationOrFunctionSource));

        constantsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        constantsListView.setCursor(Cursor.CLOSED_HAND);
        constantsListView.setCellFactory(new StringCellFactory(dragOperationOrFunctionSource));

        populateOperationOrFunctionListViews();
    }

    private void initializeNumberListViews() {
        constantsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        constantsListView.setCursor(Cursor.CLOSED_HAND);
        constantsListView.setCellFactory(new StringCellFactory(dragNumberSource));

        populateNumberListViews();
    }

    private void resetDragSources() {
        dragExpressionSource.set(null);
        dragOperationOrFunctionSource.set(null);
        dragSquidRatioModelSource.set(null);
        dragNumberSource.set(null);
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
                    String content = placeholder + db.getString().split(operationFlagDelimeter)[0] + placeholder;
                    ExpressionTextNode expressionTextNode = new ExpressionTextNode(content);
                    if ((dragOperationOrFunctionSource.get() != null) && (!db.getString().contains(operationFlagDelimeter))) {
                        // case of function, make a series of objects
                        insertExpressionIntoExpressionTextFlow(content, expressionTextFlow.getChildren().size());
                    } else {
                        if ((dragOperationOrFunctionSource.get() != null) && (db.getString().contains(operationFlagDelimeter))) {
                            expressionTextNode = new OperationTextNode(content.trim());
                        } else if ((dragNumberSource.get() != null) && content.contains(numberString)) {
                            // special case of "NUMBER"
                            expressionTextNode = new NumberTextNode((placeholder + numberString + placeholder));
                        }

                        expressionTextNode.setOrdinalIndex(expressionTextFlow.getChildren().size());
                        expressionTextFlow.getChildren().add(expressionTextNode);
                    }

                    event.setDropCompleted(true);

                    success = true;
                }
                event.setDropCompleted(success);

                event.consume();
                resetDragSources();

                updateExpressionTextFlowChildren();
            }
        });

    }

    private void insertExpressionIntoExpressionTextFlow(String content, double ordinalOrder) {
        String[] funcCall = content.split(" ");
        for (int i = 0; i < funcCall.length; i++) {
            if (funcCall[i].compareTo("") != 0) {
                funcCall[i] = funcCall[i].replaceAll("[(]", " ( ").replaceAll("[)]", " )").replaceAll(",", " , ");
                ExpressionTextNode expressionTextNode = new ExpressionTextNode(funcCall[i]);
                // max 8 terms
                expressionTextNode.setOrdinalIndex(ordinalOrder - (9 - i) * 0.1);
                expressionTextFlow.getChildren().add(expressionTextNode);
            }
        }
    }

    @FXML
    private void clearExpressionAction(ActionEvent event) {
        expressionTextFlow.getChildren().clear();
        updateExpressionTextFlowChildren();
    }

    @FXML
    private void copyButtonAction(ActionEvent event) {
        String fullText = makeStringFromTextFlow();
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(fullText);
        clipboard.setContent(content);
    }

    @FXML
    private void undoButtonAction(ActionEvent event) {
        expressionTextFlow.getChildren().clear();
        List<ExpressionTextNode> lastList = null;
        try {
            lastList = redoListForExpressionTextFlow.get(redoListForExpressionTextFlow.size() - 1);
            redoListForExpressionTextFlow.remove(lastList);
            if (redoListForExpressionTextFlow.size() > 0) {
                expressionTextFlow.getChildren().addAll(redoListForExpressionTextFlow.get(redoListForExpressionTextFlow.size() - 1));
            }
        } catch (Exception e) {
        }
        
        makeAndAuditExpression();
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

    private class StringCellFactory implements Callback<ListView<String>, ListCell<String>> {

        private ObjectProperty<String> dragSource;

        public StringCellFactory(ObjectProperty<String> dragSource) {
            this.dragSource = dragSource;
        }

        @Override
        public ListCell<String> call(ListView<String> param) {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                public void updateItem(String operationOrFunction, boolean empty) {
                    super.updateItem(operationOrFunction, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(operationOrFunction);
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
                    dragSource.set(cell.getItem());
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

    // this node signals user can edit in context menu
    private class NumberTextNode extends ExpressionTextNode {

        public NumberTextNode(String text) {
            super(text);
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
                            && ((dragExpressionSource.get() != null) || (dragOperationOrFunctionSource.get() != null) || (dragSquidRatioModelSource.get() != null) || (dragNumberSource.get() != null))) {
                        // we have the case of dropping a SOURCE INTO the list
                        String content = placeholder + db.getString().split(operationFlagDelimeter)[0] + placeholder;
                        // general case
                        ExpressionTextNode expressionTextNode = new ExpressionTextNode(content);
                        // check for special cases
                        if ((dragOperationOrFunctionSource.get() != null) && (!db.getString().contains(operationFlagDelimeter))) {
                            // case of function, make and insert a series of objects - dragTextSource remains empty to avoid next logic
                            insertExpressionIntoExpressionTextFlow(content, ((ExpressionTextNode) event.getSource()).getOrdinalIndex());
                            if (replaceRB.isSelected()) {
                                expressionTextFlow.getChildren().remove((ExpressionTextNode) event.getSource());
                            }
                            expressionTextNode = null;
                        } else if ((dragOperationOrFunctionSource.get() != null) && (db.getString().contains(operationFlagDelimeter))) {
                            // operation
                            expressionTextNode = new OperationTextNode(content.trim());
                        } else if ((dragNumberSource.get() != null) && content.contains(numberString)) {
                            // number
                            expressionTextNode = new NumberTextNode(placeholder + numberString + placeholder);
                        }

                        if (expressionTextNode != null) {
                            // setup for logic below
                            expressionTextFlow.getChildren().add(expressionTextNode);
                        }
                        dragTextSource.set(expressionTextNode);
                    }

                    // within the list of text objects
                    if (db.hasString() && (dragTextSource.get() instanceof ExpressionTextNode)) {
                        // insert left
                        dragTextSource.get().setOrdinalIndex(((ExpressionTextNode) event.getSource()).getOrdinalIndex() - 0.5);
                        if (replaceRB.isSelected()) {
                            expressionTextFlow.getChildren().remove((ExpressionTextNode) event.getSource());
                        }

                        event.setDropCompleted(true);

                        success = true;
                    }
                    event.setDropCompleted(success);
                    event.consume();
                    resetDragSources();
                    updateExpressionTextFlowChildren();
                }
            });
        }
    }

    private void updateExpressionTextFlowChildren() {
        // extract and sort
        List<ExpressionTextNode> children = new ArrayList<>();
        for (Node etn : expressionTextFlow.getChildren()) {
            children.add((ExpressionTextNode) etn);
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

        redoListForExpressionTextFlow.add(children);
        
        makeAndAuditExpression();
    }

    public void makeAndAuditExpression() {
        // make and audit expression
        StringBuilder sb = new StringBuilder();
        for (Node node : expressionTextFlow.getChildren()) {
            if (node instanceof Text) {
                sb.append(((Text) node).getText());
            }
        }
        String fullText = makeStringFromTextFlow();

        if (fullText.trim().length() > 0) {
            Expression exp = squidProject.getTask().generateExpressionFromRawExcelStyleText(
                    "Editing Expression",
                    fullText.trim().replace("\n", ""),
                    false);

            expressionAuditTextArea.setText(exp.produceExpressionTreeAudit());
        } else {
            expressionAuditTextArea.setText("");
        }
    }

    private String makeStringFromTextFlow() {
        // make and audit expression
        StringBuilder sb = new StringBuilder();
        for (Node node : expressionTextFlow.getChildren()) {
            if (node instanceof Text) {
                sb.append(((Text) node).getText());
            }
        }
        return sb.toString();
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

        menuItem = new MenuItem("Wrap in parentheses.");
        menuItem.setOnAction((evt) -> {
            ExpressionTextNode leftP = new ExpressionTextNode(" ( ");
            leftP.setOrdinalIndex(etn.getOrdinalIndex() - 1.5);
            ExpressionTextNode rightP = new ExpressionTextNode(" ) ");
            rightP.setOrdinalIndex(etn.getOrdinalIndex() + 1.5);
            expressionTextFlow.getChildren().addAll(leftP, rightP);
            updateExpressionTextFlowChildren();
        });
        contextMenu.getItems().add(menuItem);

        // make editable node
        if (etn instanceof NumberTextNode) {
            TextField editText = new TextField(etn.getText());
            editText.setPrefWidth((editText.getText().trim().length() + 2) * editText.getFont().getSize());
            editText.textProperty().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
                    editText.setPrefWidth((editText.getText().trim().length() + 2) * editText.getFont().getSize());
                }
            });
            menuItem = new MenuItem("<< Edit value then click here to save.", editText);
            menuItem.setOnAction((evt) -> {
                //etn.setText(editText.getText());
                // this allows for redo of content editing
                NumberTextNode etn2 = new NumberTextNode(editText.getText());
                etn2.setOrdinalIndex(etn.getOrdinalIndex());
                expressionTextFlow.getChildren().remove(etn);
                expressionTextFlow.getChildren().add(etn2);
                updateExpressionTextFlowChildren();
            });
            contextMenu.getItems().add(menuItem);
        }

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

        List<Expression> sortedNUSwitchedExpressionsList = new ArrayList<>();
        List<Expression> sortedBuiltInExpressionsList = new ArrayList<>();
        List<Expression> sortedCustomExpressionsList = new ArrayList<>();

        for (Expression exp : namedExpressions) {
            if (exp.amHealthy() && exp.isSquidSwitchNU()) {
                sortedNUSwitchedExpressionsList.add(exp);
            } else if (exp.getExpressionTree().isSquidSpecialUPbThExpression() && exp.amHealthy() && !exp.isSquidSwitchNU()) {
                sortedBuiltInExpressionsList.add(exp);
            } else if (!exp.getExpressionTree().isSquidSpecialUPbThExpression() && exp.amHealthy() && !exp.isSquidSwitchNU()) {
                sortedCustomExpressionsList.add(exp);
            }
        }

        sortedNUSwitchedExpressionsList.sort(
                new Comparator<Expression>() {
            @Override
            public int compare(Expression exp1, Expression exp2) {
                int retVal = 0;
                retVal = exp1.getName().compareToIgnoreCase(exp2.getName());
                return retVal;
            }
        }
        );

        ObservableList<Expression> items = FXCollections.observableArrayList(sortedNUSwitchedExpressionsList);
        nuSwitchedExpressionsListView.setItems(items);

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

        items = FXCollections.observableArrayList(sortedBuiltInExpressionsList);
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
        // operations ==========================================================
        List<String> operationStrings = new ArrayList<>();
        for (Map.Entry<String, String> op : OPERATIONS_MAP.entrySet()) {
            operationStrings.add(op.getKey() + operationFlagDelimeter + op.getValue());
        }

        ObservableList<String> items = FXCollections.observableArrayList(operationStrings);
        operationsListView.setItems(items);

        // Math Functions ======================================================
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

    private void populateNumberListViews() {
        // constants and numbers ===============================================
        List<String> constantStrings = new ArrayList<>();
        constantStrings.add(numberString + operationFlagDelimeter + "placeholder for custom number");
        for (Map.Entry<String, ExpressionTreeInterface> constant : squidProject.getTask().getNamedConstantsMap().entrySet()) {
            constantStrings.add(constant.getKey() + operationFlagDelimeter + ((ConstantNode) constant.getValue()).getValue());
        }

        ObservableList<String> items = FXCollections.observableArrayList(constantStrings);
        constantsListView.setItems(items);
    }
}
