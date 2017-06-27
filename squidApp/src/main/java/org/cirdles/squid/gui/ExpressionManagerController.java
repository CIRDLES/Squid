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
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.tasks.expressions.ExpressionTree;
import org.cirdles.squid.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.ExpressionWriterMathML;
import org.cirdles.squid.tasks.expressions.builtinExpressions.SquidExpressionMinus1;
import org.cirdles.squid.tasks.expressions.builtinExpressions.SquidExpressionMinus3;
import org.cirdles.squid.tasks.expressions.builtinExpressions.SquidExpressionMinus4;
import org.cirdles.squid.tasks.expressions.customExpressions.CustomExpression_LnPbR_U;
import org.cirdles.squid.tasks.expressions.customExpressions.CustomExpression_LnUO_U;
import org.cirdles.squid.tasks.expressions.customExpressions.CustomExpression_Net204BiWt;
import org.cirdles.squid.tasks.expressions.customExpressions.CustomExpression_Net204cts_sec;
import org.cirdles.squid.tasks.expressions.parsing.ExpressionParser;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class ExpressionManagerController implements Initializable {

    private WebEngine webEngine;

    @FXML
    private ListView<ExpressionTreeInterface> expressionListView;
    @FXML
    private TextField expressionText;
    @FXML
    private WebView browser;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ExpressionTree.squidProject = SquidUIController.squidProject;
        
        // initialize expressions tab
        ObservableList<ExpressionTreeInterface> items = FXCollections.observableArrayList(
                CustomExpression_LnPbR_U.EXPRESSION,
                CustomExpression_LnUO_U.EXPRESSION,
                CustomExpression_Net204cts_sec.EXPRESSION,
                CustomExpression_Net204BiWt.EXPRESSION,
                SquidExpressionMinus1.EXPRESSION,
                SquidExpressionMinus3.EXPRESSION,
                SquidExpressionMinus4.EXPRESSION);

        Iterator<String> ratioNameIterator = SquidRatiosModel.knownSquidRatiosModels.keySet().iterator();
        while (ratioNameIterator.hasNext()){
            String ratioName = ratioNameIterator.next();
            items.add(SquidUIController.squidProject.buildRatioExpression(ratioName));
        }

        expressionListView.setItems(items);

        webEngine = browser.getEngine();

        expressionListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends ExpressionTreeInterface> ov, ExpressionTreeInterface old_val, ExpressionTreeInterface new_val) -> {
            webEngine.loadContent(
                    ExpressionWriterMathML.toStringBuilderMathML(new_val).toString());
        });
    }

    @FXML
    private void handleParseButtonAction(ActionEvent event) {
        
        ExpressionParser expressionParser = new ExpressionParser(SquidUIController.squidProject);
        ExpressionTreeInterface result = expressionParser.parseExpression(expressionText.getText());

        webEngine.loadContent(
                ExpressionWriterMathML.toStringBuilderMathML(result).toString());
    }

}
