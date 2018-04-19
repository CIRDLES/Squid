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
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.tasks.expressions.Expression;



/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class ExpressionBuilderController implements Initializable {

    
    
    
    //BUTTONS
    @FXML
    private Button expressionCopyBtn;
    @FXML
    private Button expressionPasteBtn;
    @FXML
    private Button expressionAsTextBtn;
    @FXML
    private Button expressionClearBtn;
    @FXML
    private Button expressionUndoBtn;
    @FXML
    private Button expressionRedoBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;
    
    //TEXTS
    @FXML
    private TextFlow expressionTextFlow;
    @FXML
    private TextField textExpressionName;
    @FXML
    private TextArea textAudit;
    @FXML
    private TextArea textPeekUnk;
    @FXML
    private TextArea textPeekRM;

    //RADIOS
    ToggleGroup toggleGroup;
    @FXML
    private RadioButton dragndropRightRadio;
    @FXML
    private RadioButton dragndropReplaceRadio;
    @FXML
    private RadioButton dragndropLeftRadio;

    //CHECKBOXES
    @FXML
    private CheckBox referenceMaterialCheckBox;
    @FXML
    private CheckBox unknownSamplesCheckBox;
    @FXML
    private CheckBox concRefMatCheckBox;

    //LISTVIEWS
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

    //MISC
    @FXML
    private TitledPane builtInExpressionsTitledPane;
    @FXML
    private TitledPane graphPane;
    @FXML
    private TitledPane auditPane;
    @FXML
    private TitledPane peekPane;
    @FXML
    private Accordion expressionsAccordion;
    @FXML
    private WebView graphView;
    @FXML
    private SplitPane bigSplitPane;
    @FXML
    private SplitPane smallSplitPane;
    @FXML
    private VBox graphVBox;
    @FXML
    private VBox auditVBox;
    @FXML
    private VBox peekVBox;
    
    
    
    
    
    
    
    
    
    
    
    //INIT
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        expressionsAccordion.setExpandedPane(builtInExpressionsTitledPane);
        initPanes();
        initRadios();
    }

    private void initPanes(){
        graphPane.prefHeightProperty().bind(graphVBox.heightProperty().add(-3.0));
        auditPane.prefHeightProperty().bind(auditVBox.heightProperty().add(-3.0));
        peekPane.prefHeightProperty().bind(peekVBox.heightProperty().add(-3.0));
        graphPane.prefWidthProperty().bind(graphVBox.widthProperty().add(-3.0));
        auditPane.prefWidthProperty().bind(auditVBox.widthProperty().add(-3.0));
        peekPane.prefWidthProperty().bind(peekVBox.widthProperty().add(-3.0));
    }
    
    private void initRadios(){
        toggleGroup = new ToggleGroup();
        dragndropLeftRadio.setToggleGroup(toggleGroup);
        dragndropReplaceRadio.setToggleGroup(toggleGroup);
        dragndropRightRadio.setToggleGroup(toggleGroup);
        toggleGroup.selectToggle(dragndropLeftRadio);
    }
    
    
    
    //CREATE SAVE CANCEL ACTIONS
    
    @FXML
    void newCustomExpressionAction(ActionEvent event) {

    }
    
    @FXML
    void cancelAction(ActionEvent event) {

    }

    @FXML
    void saveAction(ActionEvent event) {

    }
    
    
    
    
    //EXPRESSION ACTIONS
    
     @FXML
    void expressionClearAction(ActionEvent event) {

    }

    @FXML
    void expressionCopyAction(ActionEvent event) {

    }

    @FXML
    void expressionPasteAction(ActionEvent event) {

    }

    @FXML
    void expressionUndoAction(ActionEvent event) {

    }

    @FXML
    void expressionRedoAction(ActionEvent event) {

    }

    @FXML
    void expressionAsTextAction(ActionEvent event) {

    }
    
    
    
    
    //CHECKBOX ACTIONS
    @FXML
    void referenceMaterialCheckBoxAction(ActionEvent event) {

    }

    @FXML
    void unknownSamplesCheckBoxAction(ActionEvent event) {

    }

    @FXML
    void concRefMatCheckBoxAction(ActionEvent event) {

    }
    
    
    
    
    @FXML
    void howToUseAction(ActionEvent event) {

    }

}
