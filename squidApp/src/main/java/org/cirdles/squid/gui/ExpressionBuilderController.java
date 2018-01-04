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
import javafx.scene.layout.AnchorPane;
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
import static org.cirdles.squid.gui.SquidUIController.squidProject;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class ExpressionBuilderController implements Initializable {

    @FXML
    private AnchorPane expressionBuilderAnchorPane;

    @FXML
    private TextFlow expressionTextFlow;

    private final ObjectProperty<ListCell<Expression>> dragExpressionSource = new SimpleObjectProperty<>();
    private final ObjectProperty<ExpressionTextNode> dragTextSource = new SimpleObjectProperty<>();
    @FXML
    private ListView<Expression> builtInExpressionsListView;
    @FXML
    private ListView<Expression> customExpressionsListView;
    @FXML
    private ListView<Expression> ratioExpressionsListView;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initializeExpressionListViews();
        initializeExpressionTextFlow();
    }

    private void initializeExpressionListViews() {
        builtInExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        customExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);

        builtInExpressionsListView.setCellFactory(new expressionCellFactory());
        customExpressionsListView.setCellFactory(new expressionCellFactory());

        populateExpressionListViews();
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
                    ExpressionTextNode expressionTextNode = new ExpressionTextNode(" [\"" + event.getDragboard().getString() + "\"] ");
                    expressionTextNode.setOrdinalIndex(expressionTextFlow.getChildren().size());
                    expressionTextFlow.getChildren().add(expressionTextNode);
                    event.setDropCompleted(true);
                    dragExpressionSource.set(null);
                    success = true;
                }
                event.setDropCompleted(success);

                event.consume();
            }
        });
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
                    db.setDragView(new Image("org/cirdles/squid/gui/images/SquidLogoSansText.png", 32, 32, true, true));
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString(cell.getItem().getName());
                    db.setContent(cc);
                    dragExpressionSource.set(cell);
                }
            });

            cell.setCursor(Cursor.CLOSED_HAND);
            return cell;
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
                    if (db.hasString() && (dragExpressionSource.get() != null)) {
                        // we have the case of dropping an expression into the list
                        ExpressionTextNode expressionTextNode = new ExpressionTextNode(" [\"" + db.getString() + "\"] ");
                        // inserts after then swapped and reset below
                        expressionTextNode.setOrdinalIndex(((ExpressionTextNode) event.getSource()).getOrdinalIndex() + 0.5);
                        expressionTextFlow.getChildren().add(expressionTextNode);
                        dragExpressionSource.set(null);
                        dragTextSource.set(expressionTextNode);
                    }
                    if (db.hasString() && (dragTextSource.get() instanceof ExpressionTextNode)) {
                        // swap locations
                        double ordinalIndexOfSource = dragTextSource.get().getOrdinalIndex();
                        double ordinalIndexOfTarget = ((ExpressionTextNode) event.getSource()).getOrdinalIndex();
                        dragTextSource.get().setOrdinalIndex(ordinalIndexOfTarget);
                        ((ExpressionTextNode) event.getSource()).setOrdinalIndex(ordinalIndexOfSource);

                        event.setDropCompleted(true);
                        dragTextSource.set(null);
                        success = true;
                        updateExpressionTextFlowChildren();
                    }
                    event.setDropCompleted(success);
                    event.consume();
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
    }

    private ContextMenu createExpressionTextNodeContextMenu(ExpressionTextNode etn) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem menuItem = new MenuItem("Remove expression.");
        menuItem.setOnAction((evt) -> {
            expressionTextFlow.getChildren().remove(etn);
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

}
