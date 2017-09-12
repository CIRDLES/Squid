/*
 * Copyright 2017 CIRDLES.org.
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
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import static org.cirdles.squid.gui.SquidUI.PIXEL_OFFSET_FOR_MENU;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.tasks.expressions.Expression;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class ExpressionManagerController implements Initializable {

    @FXML
    private AnchorPane scrolledAnchorPane;
    @FXML
    private AnchorPane expressionsAnchorPane;
    @FXML
    private ListView<Expression> expressionsListView;
    @FXML
    private Pane expressionDetailsPane;

    private final Image HEALTHY = new Image("org/cirdles/squid/gui/images/icon_checkmark.png");
    private final Image UNHEALTHY = new Image("org/cirdles/squid/gui/images/wrongx_icon.png");
    @FXML
    private Label expressionListHeaderLabel;
    @FXML
    private TextField expressionNameTextField;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        expressionsAnchorPane.prefWidthProperty().bind(primaryStageWindow.getScene().widthProperty());
        expressionsAnchorPane.prefHeightProperty().bind(primaryStageWindow.getScene().heightProperty().subtract(PIXEL_OFFSET_FOR_MENU));

        // update expressions
        squidProject.getTask().setupSquidSessionSpecs();

        initializeExpressionsListView();
    }

    private void initializeExpressionsListView() {
        expressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        expressionListHeaderLabel.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        expressionListHeaderLabel.setText(
                String.format("%1$-" + 5 + "s", " ")
                + String.format("%1$-" + 3 + "s", "RI")
                + String.format("%1$-" + 3 + "s", "SC")
                + String.format("%1$-" + 3 + "s", "RM")
                + String.format("%1$-" + 3 + "s", "UN")
                + String.format("%1$-" + 3 + "s", "SQ"));
        Tooltip tooltip = new Tooltip();
        tooltip.setText("RI = Ratios of Interest; SC = Summary; RM = Reference Materials; UN = Unknowns; SQ = Special Squid UPbTh");
        expressionListHeaderLabel.setTooltip(tooltip);

        populateExpressionsListView();

        expressionsListView.setCellFactory(param -> new ListCell<Expression>() {
            private ImageView imageView = new ImageView();

            @Override
            public void updateItem(Expression expression, boolean empty) {
                super.updateItem(expression, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (expression.amHealthy()) {
                        imageView.setImage(HEALTHY);
                    } else {
                        imageView.setImage(UNHEALTHY);
                    }

                    imageView.setFitHeight(12);
                    imageView.setFitWidth(12);
                    setText(expression.buildSignatureString());
                    setGraphic(imageView);
                }
            }

        });

        expressionsListView.setContextMenu(createExpressionsListViewContextMenu());

        expressionsListView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Expression>() {
            public void changed(ObservableValue<? extends Expression> ov,
                    Expression old_val, Expression new_val) {
                if (new_val != null) {
                    populateExpressionDetails(new_val);
                } else {
                    vacateExpressionDetails();
                }
            }
        });
    }

    private void populateExpressionDetails(Expression expression) {
        expressionNameTextField.setText(expression.getName());
    }

    private void vacateExpressionDetails() {
        expressionNameTextField.setText("");
    }

    private void populateExpressionsListView() {
        List<Expression> namedExpressions = squidProject.getTask().getTaskExpressionsOrdered();
        ObservableList<Expression> items
                = FXCollections.observableArrayList(namedExpressions);
        expressionsListView.setItems(items);
        expressionsListView.getSelectionModel().clearSelection();
    }

    private ContextMenu createExpressionsListViewContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem menuItem = new MenuItem("Remove expression.");
        menuItem.setOnAction((evt) -> {
            Expression selectedExpression = expressionsListView.getSelectionModel().getSelectedItem();
            if (selectedExpression != null) {
                squidProject.getTask().removeExpression(selectedExpression);
                populateExpressionsListView();
            }
        });
        contextMenu.getItems().add(menuItem);

        menuItem = new MenuItem("Restore removed expressions.");
        menuItem.setOnAction((evt) -> {
            squidProject.getTask().restoreRemovedExpressions();
            populateExpressionsListView();
        });
        contextMenu.getItems().add(menuItem);

        return contextMenu;
    }

}
