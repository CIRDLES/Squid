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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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
        squidProject.getTask().setupSquidSessionSpecs();//.buildSquidRatiosModelListFromMassStationDetails();

        // initialize expressions tab
        List<Expression> namedExpressions = squidProject.getTask().getTaskExpressionsOrdered();
        ObservableList<Expression> items
                = FXCollections.observableArrayList(namedExpressions);
        expressionsListView.setItems(items);

        expressionsListView.setCellFactory(param -> new ListCell<Expression>() {
            private ImageView imageView = new ImageView();

            @Override
            public void updateItem(Expression expression, boolean empty) {
                super.updateItem(expression, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (expression.getExpressionTree().amHealthy()) {
                        imageView.setImage(HEALTHY);
                    } else {
                        imageView.setImage(UNHEALTHY);
                    }

                    imageView.setFitHeight(12);
                    imageView.setFitWidth(12);
                    setText(expression.getName());
                    setGraphic(imageView);
                }
            }
        });
    }

}
