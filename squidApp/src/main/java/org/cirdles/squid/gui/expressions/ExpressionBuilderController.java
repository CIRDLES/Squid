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

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import org.cirdles.ludwig.squid25.Utilities;
import org.cirdles.squid.ExpressionsForSquid2Lexer;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.gui.SquidUI;
import static org.cirdles.squid.gui.SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.SquidUI.OPERATOR_IN_EXPRESSION_LIST_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.SquidUI.SQUID_LOGO_SANS_TEXT_URL;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.gui.utilities.BrowserControl;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeWriterMathML;
import org.cirdles.squid.tasks.expressions.functions.Function;
import static org.cirdles.squid.tasks.expressions.functions.Function.LOGIC_FUNCTIONS_MAP;
import static org.cirdles.squid.tasks.expressions.functions.Function.MATH_FUNCTIONS_MAP;
import static org.cirdles.squid.tasks.expressions.functions.Function.SQUID_FUNCTIONS_MAP;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import static org.cirdles.squid.tasks.expressions.operations.Operation.OPERATIONS_MAP;
import org.cirdles.squid.tasks.expressions.spots.SpotFieldNode;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForIsotopicRatios;



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
    @FXML
    private Button createExpressionBtn;
    @FXML
    private Button editExpressionBtn;
    @FXML
    private Button copyExpressionIntoCustomBtn;
    
    //TEXTS
    @FXML
    private TextFlow expressionTextFlow;
    @FXML
    private TextField expressionNameTextField;
    @FXML
    private TextArea auditTextArea;
    @FXML
    private TextArea unPeekTextArea;
    @FXML
    private TextArea rmPeekTextArea;
    @FXML
    private Label modeLabel;
    private final TextArea expressionAsTextArea = new TextArea();

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
    private CheckBox refMatSwitchCheckBox;
    @FXML
    private CheckBox unknownsSwitchCheckBox;
    @FXML
    private CheckBox concRefMatSwitchCheckBox;
    @FXML
    private CheckBox summaryCalculationSwitchCheckBox;
    @FXML
    private CheckBox showGraphCheckBox;
    @FXML
    private CheckBox graphBrowserCheckBox;
    
    //CHOICEBOXES
    @FXML
    private ChoiceBox orderChoiceBox;
    @FXML
    private ChoiceBox fromChoiceBox;   
    

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
    private ListView<String> logicFunctionsListView;
    @FXML
    private ListView<String> constantsListView;
    @FXML
    private ListView<?> referenceMaterialsListView;
    @FXML
    private ListView<Expression> globalListView;

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
    private TitledPane expressionPane;
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
    @FXML
    private VBox editorVBox;
    @FXML
    private HBox graphTitleHbox;
    
    //PEEK TABS
    @FXML
    private Tab unkTab;
    @FXML
    private Tab refMatTab;
    @FXML
    private TabPane spotTabPane;
    
    
    
    
    
    
    
    
    
    
    
    private final String operationFlagDelimeter = " : ";
    private final String numberString = "NUMBER";
    private final String placeholder = " ";// \u2588 ";
    
    
    
    private final ObjectProperty<ListCell<Expression>> dragExpressionSource = new SimpleObjectProperty<>();
    private final ObjectProperty<SquidRatiosModel> dragSquidRatioModelSource = new SimpleObjectProperty<>();
    private final ObjectProperty<String> dragOperationOrFunctionSource = new SimpleObjectProperty<>();
    private final ObjectProperty<String> dragNumberSource = new SimpleObjectProperty<>();
    private final ObjectProperty<ExpressionTextNode> dragTextSource = new SimpleObjectProperty<>();
    
    private final ListProperty<List<ExpressionTextNode>> undoListForExpressionTextFlow = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<List<ExpressionTextNode>> redoListForExpressionTextFlow = new SimpleListProperty<>(FXCollections.observableArrayList());
    
    private final ObjectProperty<Expression> selectedExpression = new SimpleObjectProperty<>();
    private final BooleanProperty selectedExpressionIsCustom = new SimpleBooleanProperty(false);
    //Boolean to save wether or not the expression has been save since the last modification
    private final BooleanProperty expressionIsSaved = new SimpleBooleanProperty(true);
    //Boolean to save wether the expression is currently edited as a textArea or with dra and drop
    private final BooleanProperty editAsText = new SimpleBooleanProperty(false);
    
    private final ObjectProperty<Mode> currentMode = new SimpleObjectProperty<>(Mode.EDIT);
    private enum Mode{
        
        EDIT("Edit"),
        CREATE("Create"),
        VIEW("View");
        
        private final String printString;
        
        private Mode(String printString){
            this.printString = printString;
        }
        
        @Override
        public String toString(){
            return printString;
        }
    }
    
    //List of operator used to detect if a string should be an operator node or not
    private final List<String> listOperators = new ArrayList<>();
    
    
    
    
    
    
    
    
    
    
    
    
    //INIT
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentMode.addListener((observable, oldValue, newValue) -> {
            System.out.println("Mode : "+newValue.name());
        });
        selectedExpression.addListener((observable, oldValue, newValue) -> {
            System.out.println("Selected : "+(newValue==null ? "null" : newValue.getName()));
        });
        selectedExpressionIsCustom.addListener((observable, oldValue, newValue) -> {
            System.out.println("Is custom : "+newValue);
        });
        expressionIsSaved.addListener((observable, oldValue, newValue) -> {
            System.out.println("Is saved : "+newValue);
        });
        
        currentMode.addListener((observable, oldValue, newValue) -> {
            modeLabel.setText(newValue.toString());
        });
        
        initPropertyBindings();
        initListViews();
        initRadios();
        initExpressionTextFlowAndTextArea();
        initGraph();
        initExpressionSelection();
        
        currentMode.set(Mode.VIEW);
        
        expressionsAccordion.setExpandedPane(builtInExpressionsTitledPane);
       
    }
    
    private void initPropertyBindings(){
        //Disable bindings
        editorVBox.disableProperty().bind(selectedExpression.isNull());
        expressionUndoBtn.disableProperty().bind(undoListForExpressionTextFlow.sizeProperty().lessThan(2).or(editAsText).or(currentMode.isEqualTo(Mode.VIEW)));
        expressionRedoBtn.disableProperty().bind(redoListForExpressionTextFlow.sizeProperty().lessThan(1).or(editAsText).or(currentMode.isEqualTo(Mode.VIEW)));
        editExpressionBtn.disableProperty().bind(currentMode.isNotEqualTo(Mode.VIEW).or(selectedExpressionIsCustom.not()));
        copyExpressionIntoCustomBtn.disableProperty().bind(currentMode.isNotEqualTo(Mode.VIEW).or(selectedExpression.isNull()));
        createExpressionBtn.disableProperty().bind(currentMode.isNotEqualTo(Mode.VIEW));
        expressionClearBtn.disableProperty().bind(currentMode.isEqualTo(Mode.VIEW));
        expressionPasteBtn.disableProperty().bind(currentMode.isEqualTo(Mode.VIEW));
        saveBtn.disableProperty().bind(currentMode.isEqualTo(Mode.VIEW));
        expressionAsTextBtn.disableProperty().bind(currentMode.isEqualTo(Mode.VIEW));
        refMatSwitchCheckBox.disableProperty().bind(currentMode.isEqualTo(Mode.VIEW));
        unknownsSwitchCheckBox.disableProperty().bind(currentMode.isEqualTo(Mode.VIEW));
        concRefMatSwitchCheckBox.disableProperty().bind(currentMode.isEqualTo(Mode.VIEW));
        expressionNameTextField.editableProperty().bind(currentMode.isNotEqualTo(Mode.VIEW));
        
        

        //Autoresize bindings
        graphPane.maxHeightProperty().bind(graphVBox.heightProperty().add(-3.0));
        auditPane.prefHeightProperty().bind(auditVBox.heightProperty().add(-3.0));
        peekPane.prefHeightProperty().bind(peekVBox.heightProperty().add(-3.0));
        graphPane.prefWidthProperty().bind(graphVBox.widthProperty().add(-3.0));
        auditPane.prefWidthProperty().bind(auditVBox.widthProperty().add(-3.0));
        peekPane.prefWidthProperty().bind(peekVBox.widthProperty().add(-3.0));
        expressionTextFlow.heightProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.doubleValue()+28.0>expressionPane.heightProperty().get()){
                bigSplitPane.setPrefHeight(bigSplitPane.getHeight()-(newValue.doubleValue()+28.0-expressionPane.heightProperty().get()));
            }
        });
        expressionAsTextArea.prefHeightProperty().bind(expressionTextFlow.heightProperty());
    }
    
    private void initRadios(){
        toggleGroup = new ToggleGroup();
        dragndropLeftRadio.setToggleGroup(toggleGroup);
        dragndropReplaceRadio.setToggleGroup(toggleGroup);
        dragndropRightRadio.setToggleGroup(toggleGroup);
        toggleGroup.selectToggle(dragndropRightRadio);
    }
    
    private void initListViews(){
        //EXPRESSIONS
        globalListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        globalListView.setCellFactory(new ExpressionCellFactory());
        nuSwitchedExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        nuSwitchedExpressionsListView.setCellFactory(new ExpressionCellFactory());
        builtInExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        builtInExpressionsListView.setCellFactory(new ExpressionCellFactory());
        customExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        customExpressionsListView.setCellFactory(new ExpressionCellFactory());
        populateExpressionListViews();
        
        //RATIOS
        ratioExpressionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        ratioExpressionsListView.setCellFactory(new expressionTreeCellFactory());
        populateRatiosListView();
        
        //OPERATIONS AND FUNCTIONS
        operationsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        operationsListView.setCellFactory(new StringCellFactory(dragOperationOrFunctionSource));
        mathFunctionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        mathFunctionsListView.setCellFactory(new StringCellFactory(dragOperationOrFunctionSource));
        squidFunctionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        squidFunctionsListView.setCellFactory(new StringCellFactory(dragOperationOrFunctionSource));
        logicFunctionsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        logicFunctionsListView.setCellFactory(new StringCellFactory(dragOperationOrFunctionSource));
        populateOperationOrFunctionListViews();
        
        //NUMBERS
        constantsListView.setStyle(SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS);
        constantsListView.setCellFactory(new StringCellFactory(dragNumberSource));
        populateNumberListViews();
    }
    
    private void initExpressionTextFlowAndTextArea(){
        
        //Init of the textarea
        expressionAsTextArea.setFont(Font.font("Courier New"));
        expressionAsTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            //Rebuild of the expression at each modification in order for the graph and audit to be updated
            buildTextFlowFromString(newValue);
        });
        
        //Init of the textflow following the mode
        currentMode.addListener((observable, oldValue, newValue) -> {
            switch(newValue){
                case VIEW :
                    disableExpressionTextFlowDragAndDrop();
                    break;
                case CREATE :
                case EDIT :
                    activeExpressionTextFlowDragAndDrop();
            }
        });
        
    }
    
    private void activeExpressionTextFlowDragAndDrop(){
        expressionTextFlow.setOnDragOver((DragEvent event) -> {
            if (event.getDragboard().hasString()) {
                if(event.getGestureSource() instanceof ExpressionTextNode){
                    //Move an other text node
                    event.acceptTransferModes(TransferMode.MOVE);
                }else{
                    //Or copy a new one from the lists
                    event.acceptTransferModes(TransferMode.COPY);
                }
            }
            event.consume();
        });
        
        expressionTextFlow.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            
            //By default, the node will be placed at the end of the textflow
            double ord = expressionTextFlow.getChildren().size();
            if(toggleGroup.getSelectedToggle() == dragndropLeftRadio){
                //But if the dragndropLeftRadio is selected, the node will be placed at the begining of the textflow
                ord = -1.0;
            }
            
            // if moving a node
            if(event.getGestureSource() instanceof ExpressionTextNode){
                //Just change the index and then update
                ((ExpressionTextNode)event.getGestureSource()).setOrdinalIndex(ord);
                updateExpressionTextFlowChildren();
                success = true;
            }
            // if copying a node from the lists, insert depending of the type of node
            else if (db.hasString()) {
                String content = placeholder + db.getString().split(operationFlagDelimeter)[0] + placeholder;
                if ((dragOperationOrFunctionSource.get() != null) && (!db.getString().contains(operationFlagDelimeter))) {
                    // case of function, make a series of objects
                    insertFunctionIntoExpressionTextFlow(content, ord);
                }else if ((dragOperationOrFunctionSource.get() != null) && (db.getString().contains(operationFlagDelimeter))) {
                    // case of operation
                    insertOperationIntoExpressionTextFlow(content, ord);
                } else if ((dragNumberSource.get() != null) && content.contains(numberString)) {
                    // case of "NUMBER"
                    insertNumberIntoExpressionTextFlow(numberString, ord);
                }else{
                    // case of expression
                    insertExpressionIntoExpressionTextFlow(content, ord);
                }
                
                success = true;
            }
            
            event.setDropCompleted(success);
            
            event.consume();
            resetDragSources();
        });
    }
    
    private void disableExpressionTextFlowDragAndDrop(){
        expressionTextFlow.setOnDragOver((DragEvent event) -> {
            //Nothing
        });
        
        expressionTextFlow.setOnDragDropped((DragEvent event) -> {
            //Nothing
        });
    }
    
    
    private void initExpressionSelection(){
        //Listener that updates the whole builder when the expression to edit is changed
        selectedExpression.addListener((observable, oldValue, newValue) -> {
            undoListForExpressionTextFlow.clear();
            redoListForExpressionTextFlow.clear();
            if(editAsText.get()){
                expressionAsTextAction(new ActionEvent());
            }
            if(newValue != null){
                if(!currentMode.get().equals(Mode.CREATE) && customExpressionsListView.getItems().contains(newValue)){
                    selectedExpressionIsCustom.set(true);
                }else{
                    selectedExpressionIsCustom.set(false);
                }
                expressionNameTextField.textProperty().set(newValue.getName());
                refMatSwitchCheckBox.setSelected(((ExpressionTree) newValue.getExpressionTree()).isSquidSwitchSTReferenceMaterialCalculation());
                unknownsSwitchCheckBox.setSelected(((ExpressionTree) newValue.getExpressionTree()).isSquidSwitchSAUnknownCalculation());
                concRefMatSwitchCheckBox.setSelected(((ExpressionTree) newValue.getExpressionTree()).isSquidSwitchConcentrationReferenceMaterialCalculation());
                summaryCalculationSwitchCheckBox.setSelected(((ExpressionTree) newValue.getExpressionTree()).isSquidSwitchSCSummaryCalculation());
                if(newValue.getExcelExpressionString().trim().length()>0){
                    buildTextFlowFromString(newValue.getExcelExpressionString());
                }
            }else{
                expressionNameTextField.clear();
                expressionTextFlow.getChildren().clear();
                refMatSwitchCheckBox.setSelected(false);
                unknownsSwitchCheckBox.setSelected(false);
                concRefMatSwitchCheckBox.setSelected(false);
                selectedExpressionIsCustom.set(false);
                updateAuditGraphAndPeek();
            }
        });
    }
    
    private void initGraph(){
        
        //Update graph when change in preferences
        showGraphCheckBox.setOnAction((event) -> {
            graphExpressionTree(makeExpression().getExpressionTree());
        });
        graphBrowserCheckBox.setOnAction((event) -> {
            graphExpressionTree(makeExpression().getExpressionTree());
        });
    }
    
    
    
    
    
    
    
    
    
    
    //CREATE EDIT SAVE CANCEL ACTIONS
    
    @FXML
    void newCustomExpressionAction(ActionEvent event) {
        if(currentMode.get().equals(Mode.VIEW)){
            selectedExpression.set(null);
            selectedExpression.set(new Expression("new_custom_expression", ""));
            currentMode.set(Mode.CREATE);
            expressionIsSaved.set(false);
            undoListForExpressionTextFlow.add(new ArrayList<>());
        }
    }
    
    @FXML
    void copyIntoCustomExpressionAction(ActionEvent event) {
        if(currentMode.get().equals(Mode.VIEW)){
            Expression exp = copySelectedExpression();
            selectedExpression.set(exp);
            currentMode.set(Mode.CREATE);
            expressionIsSaved.set(false);
            exp.setName("copy of "+exp.getName());
        }
    }
    
    @FXML
    void editCustomExpressionAction(ActionEvent event){
        if(selectedExpressionIsCustom.get() && currentMode.get().equals(Mode.VIEW)){
            currentMode.set(Mode.EDIT);
        }
    }
    
    @FXML
    void cancelAction(ActionEvent event) {
        selectedExpression.set(null);
        currentMode.set(Mode.VIEW);
    }

    @FXML
    void saveAction(ActionEvent event) {

        boolean nameExists = false;
        boolean nameExistsInCustom = false;
        
        for(Expression exLoop : builtInExpressionsListView.getItems()){
            if(exLoop.getName().equals(expressionNameTextField.getText())){
                nameExists = true;
            }
        }
        if(!nameExists){
            for(Expression exLoop : nuSwitchedExpressionsListView.getItems()){
                if(exLoop.getName().equals(expressionNameTextField.getText())){
                    nameExists = true;
                }
            }
        }
        if(!nameExists){
            for(Expression exLoop : customExpressionsListView.getItems()){
                if(exLoop.getName().equals(expressionNameTextField.getText())){
                    nameExistsInCustom = true;
                }
            }
        }
        
        if(nameExists){
            //Case name already exists not in custom -> impossible to save
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "An expression already exists with this name. Please change it before saving.",
                    ButtonType.OK
            );
            alert.show();
        }else if(nameExistsInCustom && currentMode.get().equals(Mode.EDIT) && selectedExpression.get().getName().equals(expressionNameTextField.getText())){
            //Case edition and name unchanged -> replace without asking
            save();
        }else if(nameExistsInCustom){
            //Case name already exists in custom -> ask for replacing
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "A custom expression already exists with this name. Do you want to replace it?",
                    ButtonType.YES,
                    ButtonType.NO
            );
            alert.showAndWait().ifPresent((t) -> {
                if(t.equals(ButtonType.YES)){
                    save();
                }
            });
        }
        else{
            //Case name doesnt exist -> save
            save();
        }
        
    }
    
    
    
    
    //EXPRESSION ACTIONS
    
    @FXML
    void expressionClearAction(ActionEvent event) {
        //Clear the textflow
        if(!currentMode.get().equals(Mode.VIEW)){
            expressionTextFlow.getChildren().clear();
        updateAuditGraphAndPeek();
        }
    }

    @FXML
    void expressionCopyAction(ActionEvent event) {
        //Copy in clipboard
        String fullText = makeStringFromTextFlow();
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(fullText);
        clipboard.setContent(content);
    }

    @FXML
    void expressionPasteAction(ActionEvent event) {
        //Build textflow from clipboard
        if(!currentMode.get().equals(Mode.VIEW)){
            Clipboard clipboard = Clipboard.getSystemClipboard();
            String content = clipboard.getString();
            if(editAsText.get()){
                expressionAsTextArea.setText(content);
            }else{
                buildTextFlowFromString(content);
            }
        }
    }

    @FXML
    void expressionUndoAction(ActionEvent event) {
        //Try to restore the last saved state
        try {
            expressionTextFlow.getChildren().clear();
            List<ExpressionTextNode> lastList = undoListForExpressionTextFlow.get(undoListForExpressionTextFlow.size() - 1);
            undoListForExpressionTextFlow.remove(lastList);
            redoListForExpressionTextFlow.add(lastList);
            if (undoListForExpressionTextFlow.size() > 0) {
                expressionTextFlow.getChildren().addAll(undoListForExpressionTextFlow.get(undoListForExpressionTextFlow.size() - 1));
            }
        } catch (Exception e) {
        }
        
        updateAuditGraphAndPeek();
    }

    @FXML
    void expressionRedoAction(ActionEvent event) {
        try {
            List<ExpressionTextNode> lastList = redoListForExpressionTextFlow.get(redoListForExpressionTextFlow.size() - 1);
            redoListForExpressionTextFlow.remove(lastList);
            undoListForExpressionTextFlow.add(lastList);
            expressionTextFlow.getChildren().clear();
            expressionTextFlow.getChildren().addAll(lastList);
        } catch (Exception e) {
        }
        
        updateAuditGraphAndPeek();
    }

    @FXML
    void expressionAsTextAction(ActionEvent event) {
        if(editAsText.get() == false){
            //Case was editing with drag and drop -> switch to textArea
            
            editAsText.set(true);
            
            expressionAsTextArea.setText(makeStringFromTextFlow());
            expressionPane.setContent(expressionAsTextArea);
            expressionAsTextBtn.setText("Edit with drag and drop");
                  
        }else{
            //Case was editing as textArea -> switch to drag and drop
            
            editAsText.set(true);
            
            expressionPane.setContent(expressionTextFlow);
            expressionAsTextBtn.setText("Edit as text");
            
            //Rebuild because of a CSS bug
            buildTextFlowFromString(makeStringFromTextFlow());
        }
    }
    
    
    
    
    //CHECKBOX ACTIONS
    @FXML
    void referenceMaterialCheckBoxAction(ActionEvent event) {
        updateAuditGraphAndPeek();
    }

    @FXML
    void unknownSamplesCheckBoxAction(ActionEvent event) {
        updateAuditGraphAndPeek();
    }

    @FXML
    void concRefMatCheckBoxAction(ActionEvent event) {
        updateAuditGraphAndPeek();
    }
    
    @FXML
    void summaryCalculationCheckBoxAction(ActionEvent event) {
        updateAuditGraphAndPeek();
    }
    
    
    
    @FXML
    void howToUseAction(ActionEvent event) {

    }
    
    
    
    
    //POPULATE LISTS
    
    private void populateExpressionListViews() {
        List<Expression> namedExpressions = squidProject.getTask().getTaskExpressionsOrdered();
        
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
        
        ObservableList<Expression> items = FXCollections.observableArrayList(namedExpressions);
        items = items.sorted((o1, o2) -> {
            return o1.getName().compareTo(o2.getName());
        });
        globalListView.setItems(items);
        
        items = FXCollections.observableArrayList(sortedNUSwitchedExpressionsList);
        items = items.sorted((Expression exp1, Expression exp2) -> {
            return exp1.getName().compareToIgnoreCase(exp2.getName());
        });
        nuSwitchedExpressionsListView.setItems(items);
        
        
        items = FXCollections.observableArrayList(sortedBuiltInExpressionsList);
        items = items.sorted((Expression exp1, Expression exp2) -> {
            return exp1.getName().compareToIgnoreCase(exp2.getName());
        });
        builtInExpressionsListView.setItems(items);
        
        
        items = FXCollections.observableArrayList(sortedCustomExpressionsList);
        items = items.sorted((Expression exp1, Expression exp2) -> {
            return exp1.getName().compareToIgnoreCase(exp2.getName());
        });
        customExpressionsListView.setItems(items);
    }
    
    private void populateRatiosListView() {
        List<SquidRatiosModel> ratiosList = squidProject.getTask().getSquidRatiosModelList();
        
        ObservableList<SquidRatiosModel> items = FXCollections.observableArrayList(ratiosList);
        items = items.sorted((ratio1, ratio2) -> {
            return ratio1.getRatioName().compareToIgnoreCase(ratio2.getRatioName());
        });
        ratioExpressionsListView.setItems(items);
    }
    
    private void populateOperationOrFunctionListViews() {
        // operations ==========================================================
        List<String> operationStrings = new ArrayList<>();
        for (Map.Entry<String, String> op : OPERATIONS_MAP.entrySet()) {
            operationStrings.add(op.getKey() + operationFlagDelimeter + op.getValue());
            listOperators.add(op.getKey());
        }
        
        ObservableList<String> items = FXCollections.observableArrayList(operationStrings);
        operationsListView.setItems(items);
        
        // Math Functions ======================================================
        List<String> mathFunctionStrings = new ArrayList<>();
        for (Map.Entry<String, String> op : MATH_FUNCTIONS_MAP.entrySet()) {
            int argumentCount = Function.operationFactory(op.getValue()).getArgumentCount();
            String args = op.getKey() + "(";
            for (int i = 0; i < argumentCount; i++) {
                args += "ARG" + i + (i < (argumentCount - 1) ? "," : ")");
            }
            
            mathFunctionStrings.add(args);
        }
        
        items = FXCollections.observableArrayList(mathFunctionStrings);
        items = items.sorted();
        mathFunctionsListView.setItems(items);
        
        // Squid Functions ======================================================
        List<String> squidFunctionStrings = new ArrayList<>();
        for (Map.Entry<String, String> op : SQUID_FUNCTIONS_MAP.entrySet()) {
            int argumentCount = Function.operationFactory(op.getValue()).getArgumentCount();
            String args = op.getKey() + "(";
            for (int i = 0; i < argumentCount; i++) {
                args += "ARG" + i + (i < (argumentCount - 1) ? "," : ")");
            }
            
            squidFunctionStrings.add(args);
        }
        
        items = FXCollections.observableArrayList(squidFunctionStrings);
        items = items.sorted();
        squidFunctionsListView.setItems(items);
        
        // Logic Functions ======================================================
        List<String> logicFunctionStrings = new ArrayList<>();
        for (Map.Entry<String, String> op : LOGIC_FUNCTIONS_MAP.entrySet()) {
            int argumentCount = Function.operationFactory(op.getValue()).getArgumentCount();
            String args = op.getKey() + "(";
            for (int i = 0; i < argumentCount; i++) {
                args += "ARG" + i + (i < (argumentCount - 1) ? "," : ")");
            }
            
            logicFunctionStrings.add(args);
        }
        
        items = FXCollections.observableArrayList(logicFunctionStrings);
        items = items.sorted();
        logicFunctionsListView.setItems(items);
    }
    
    private void populateNumberListViews() {
        // constants and numbers ===============================================
        List<String> constantStrings = new ArrayList<>();
        constantStrings.add(numberString + operationFlagDelimeter + "placeholder for number");
        
        for (Map.Entry<String, ExpressionTreeInterface> constant : squidProject.getTask().getNamedConstantsMap().entrySet()) {
            constantStrings.add(constant.getKey() + operationFlagDelimeter + ((ConstantNode) constant.getValue()).getValue());
        }
        
        for (Map.Entry<String, ExpressionTreeInterface> constant : squidProject.getTask().getNamedParametersMap().entrySet()) {
            constantStrings.add(constant.getKey() + operationFlagDelimeter + ((ConstantNode) constant.getValue()).getValue());
        }
        
        ObservableList<String> items = FXCollections.observableArrayList(constantStrings);
        constantsListView.setItems(items);
    }
    
    private void populatePeeks(Expression exp){
        SingleSelectionModel<Tab> selectionModel = spotTabPane.getSelectionModel();

        if ((exp == null) || (!exp.amHealthy())) {
            rmPeekTextArea.setText("No expression.");
            unPeekTextArea.setText("No expression.");
        }else if(!expressionIsSaved.get()){
            rmPeekTextArea.setText("You must save the expression to get a peek.");
            unPeekTextArea.setText("You must save the expression to get a peek.");
        }else {

            TaskInterface task = squidProject.getTask();

            List<ShrimpFractionExpressionInterface> refMatSpots = task.getReferenceMaterialSpots();
            List<ShrimpFractionExpressionInterface> unSpots = task.getUnknownSpots();
            List<ShrimpFractionExpressionInterface> concRefMatSpots = task.getConcentrationReferenceMaterialSpots();

            // choose peek tab
            if (refMatTab.isSelected() && !refMatSwitchCheckBox.isSelected() && !concRefMatSwitchCheckBox.isSelected()) {
                selectionModel.select(unkTab);
            } else if (unkTab.isSelected() && !unknownsSwitchCheckBox.isSelected()) {
                selectionModel.select(refMatTab);
            }

            if (exp.getExpressionTree() instanceof ConstantNode) {
                rmPeekTextArea.setText("Not used");
                unPeekTextArea.setText("Not used");
                if (exp.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()) {
                    try {
                        rmPeekTextArea.setText(exp.getName() + " = " + Utilities.roundedToSize((Double) ((ConstantNode) exp.getExpressionTree()).getValue(), 15));
                    } catch (Exception e) {
                    }
                }
                if (exp.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                    try {
                        unPeekTextArea.setText(exp.getName() + " = " + Utilities.roundedToSize((Double) ((ConstantNode) exp.getExpressionTree()).getValue(), 15));
                    } catch (Exception e) {
                    }
                }

            } else if (exp.getExpressionTree().isSquidSwitchSCSummaryCalculation()) {
                SpotSummaryDetails spotSummary = task.getTaskExpressionsEvaluationsPerSpotSet().get(exp.getExpressionTree().getName());

                rmPeekTextArea.setText("No Summary");
                unPeekTextArea.setText("No Summary");

                if (spotSummary != null) {
                    if (exp.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()) {
                        if (spotSummary.getSelectedSpots().size() > 0) {
                            rmPeekTextArea.setText(peekDetailsPerSummary(spotSummary));
                        } else {
                            rmPeekTextArea.setText("No Reference Materials");
                        }
                    }

                    if (exp.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation()) {
                        if (spotSummary.getSelectedSpots().size() > 0) {
                            rmPeekTextArea.setText(peekDetailsPerSummary(spotSummary));
                        } else {
                            rmPeekTextArea.setText("No Concentration Reference Materials");
                        }
                    }

                    if (exp.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                        if (spotSummary.getSelectedSpots().size() > 0) {
                            unPeekTextArea.setText(peekDetailsPerSummary(spotSummary));
                        } else {
                            unPeekTextArea.setText("No Unknowns");
                        }
                    }
                }

            } else {
                rmPeekTextArea.setText("Reference Materials not processed.");
                if (exp.getExpressionTree().isSquidSwitchSTReferenceMaterialCalculation()) {
                    if (refMatSpots.size() > 0) {
                        rmPeekTextArea.setText(peekDetailsPerSpot(refMatSpots, exp.getExpressionTree()));
                    } else {
                        rmPeekTextArea.setText("No Reference Materials");
                    }
                } else if (exp.getExpressionTree().isSquidSwitchConcentrationReferenceMaterialCalculation()) {
                    if (concRefMatSpots.size() > 0) {
                        rmPeekTextArea.setText(peekDetailsPerSpot(concRefMatSpots, exp.getExpressionTree()));
                    } else {
                        rmPeekTextArea.setText("No Concentration Reference Materials");
                    }
                }

                unPeekTextArea.setText("Unknowns not processed.");
                if (exp.getExpressionTree().isSquidSwitchSAUnknownCalculation()) {
                    if (unSpots.size() > 0) {
                        unPeekTextArea.setText(peekDetailsPerSpot(unSpots, exp.getExpressionTree()));
                    } else {
                        rmPeekTextArea.setText("No Unknowns");
                    }
                }
            }
        }
    }
    
    private String peekDetailsPerSummary(SpotSummaryDetails spotSummary) {
        String[][] labels = spotSummary.getOperation().getLabelsForOutputValues();
        StringBuilder sb = new StringBuilder();
        if (concRefMatSwitchCheckBox.isSelected()) {
            sb.append("Concentration Reference Materials Only\n\n");
        }
        for (int i = 0; i < labels[0].length; i++) {
            sb.append("\t");
            sb.append(String.format("%1$-" + 13 + "s", labels[0][i]));
            sb.append(": ");
            sb.append(Utilities.roundedToSize(spotSummary.getValues()[0][i], 15));
            sb.append("\n");
        }

        // handle special cases
        if (labels.length > 1) {
            sb.append("\t");
            sb.append(String.format("%1$-" + 13 + "s", labels[1][0]));
            sb.append(": ");
            // print list
            if (spotSummary.getValues()[1].length == 0) {
                sb.append("None");
            } else {
                for (int j = 0; j < spotSummary.getValues()[1].length; j++) {
                    sb.append((int) (spotSummary.getValues()[1][j]) + " ");
                }
            }
            sb.append("\n");
        }

        if (labels.length > 2) {
            sb.append("\t");
            sb.append(String.format("%1$-" + 13 + "s", labels[2][0]));
            sb.append(": ");
            // print list
            if (spotSummary.getValues()[2].length == 0) {
                sb.append("None");
            } else {
                for (int j = 0; j < spotSummary.getValues()[2].length; j++) {
                    sb.append((int) (spotSummary.getValues()[2][j])).append(" ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private String peekDetailsPerSpot(List<ShrimpFractionExpressionInterface> spots, ExpressionTreeInterface editedExp) {
        StringBuilder sb = new StringBuilder();

        if (editedExp instanceof ShrimpSpeciesNode) {
            sb.append("Please specify property of species such as totalCps.");
        } else if (editedExp instanceof VariableNodeForIsotopicRatios) {
            // special case where the expressionTree is a ratio
            sb.append(String.format("%1$-" + 15 + "s", "Spot name"));
            sb.append(String.format("%1$-" + 20 + "s", editedExp.getName()));
            sb.append(String.format("%1$-" + 20 + "s", "1-sigma ABS"));
            sb.append("\n");

            for (ShrimpFractionExpressionInterface spot : spots) {
                sb.append(String.format("%1$-" + 15 + "s", spot.getFractionID()));
                double[][] results
                        = spot.getIsotopicRatioValuesByStringName(editedExp.getName());
                for (int i = 0; i < results[0].length; i++) {
                    try {
                        sb.append(String.format("%1$-" + 20 + "s", Utilities.roundedToSize(results[0][i], 15)));
                    } catch (Exception e) {
                    }
                }
                sb.append("\n");
            }

        } else if (editedExp instanceof SpotFieldNode) {
            // special case where the expressionTree is a field in spot (non-ratio)
            sb.append(String.format("%1$-" + 15 + "s", "Spot name"));
            sb.append(String.format("%1$-" + 20 + "s", editedExp.getName()));
            sb.append("\n");

            for (ShrimpFractionExpressionInterface spot : spots) {
                sb.append(String.format("%1$-" + 15 + "s", spot.getFractionID()));
                List<ShrimpFractionExpressionInterface> singleSpot = new ArrayList<>();
                singleSpot.add(spot);

                try {
                    double[][] results = ExpressionTreeInterface.convertObjectArrayToDoubles(editedExp.eval(singleSpot, null));
                    for (int i = 0; i < results[0].length; i++) {
                        try {
                            sb.append(String.format("%1$-" + 20 + "s", Utilities.roundedToSize(results[0][i], 15)));
                        } catch (Exception e) {
                        }
                    }
                    sb.append("\n");
                } catch (SquidException squidException) {
                }

            }

        } else {
            if (concRefMatSwitchCheckBox.isSelected()) {
                sb.append("Concentration Reference Materials Only\n\n");
            }
            sb.append(String.format("%1$-" + 15 + "s", "Spot name"));
            if (((ExpressionTree) editedExp).getOperation() != null) {
                String[][] resultLabels = ((ExpressionTree) editedExp).getOperation().getLabelsForOutputValues();
                for (int i = 0; i < resultLabels[0].length; i++) {
                    try {
                        sb.append(String.format("%1$-" + 20 + "s", resultLabels[0][i]));
                    } catch (Exception e) {
                    }
                }
                if (((ExpressionTree) editedExp).hasRatiosOfInterest()) {
                    sb.append(String.format("%1$-" + 20 + "s", "1-sigma ABS"));
                }
            }
            sb.append("\n");

            for (ShrimpFractionExpressionInterface spot : spots) {
                if (spot.getTaskExpressionsEvaluationsPerSpot().get(editedExp) != null) {
                    sb.append(String.format("%1$-" + 15 + "s", spot.getFractionID()));
                    double[][] results
                            = spot.getTaskExpressionsEvaluationsPerSpot().get(editedExp);
                    for (int i = 0; i < results[0].length; i++) {
                        try {
                            sb.append(String.format("%1$-" + 20 + "s", Utilities.roundedToSize(results[0][i], 15)));
                        } catch (Exception e) {
                        }
                    }
                    sb.append("\n");
                }
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
            wrapInParentheses(etn.getOrdinalIndex(),etn.getOrdinalIndex());
        });
        contextMenu.getItems().add(menuItem);
        
        // For numbers -> make an editable node
        if (etn instanceof NumberTextNode) {
            TextField editText = new TextField(etn.getText());
            editText.setPrefWidth((editText.getText().trim().length() + 2) * editText.getFont().getSize());
            editText.textProperty().addListener((ObservableValue<? extends Object> observable, Object oldValue, Object newValue) -> {
                editText.setPrefWidth((editText.getText().trim().length() + 2) * editText.getFont().getSize());
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
        
        contextMenu.setOnHiding((WindowEvent event) -> {
            etn.popupShowing = false;
        });
        
        return contextMenu;
    }
    
    private void updateExpressionTextFlowChildren() {
        // extract and sort
        List<ExpressionTextNode> children = new ArrayList<>();
        for (Node etn : expressionTextFlow.getChildren()) {
            children.add((ExpressionTextNode) etn);
        }
        // sort
        children.sort((Node o1, Node o2) -> {
            int retVal = 0;
            if (o1 instanceof ExpressionTextNode && o2 instanceof ExpressionTextNode) {
                retVal = Double.compare(((ExpressionTextNode) o1).getOrdinalIndex(), ((ExpressionTextNode) o2).getOrdinalIndex());
            }
            return retVal;
        });
        
        // reset ordinals to integer values
        double ordIndex = 0;
        for (ExpressionTextNode etn : children) {
            etn.setOrdinalIndex(ordIndex);
            //etn.setFill(Color.BLACK);
            ordIndex++;
        }
        
        expressionTextFlow.getChildren().setAll(children);
        
        undoListForExpressionTextFlow.add(children);
        redoListForExpressionTextFlow.clear();
        
        updateAuditGraphAndPeek();
        
    }
    
    public void updateAuditGraphAndPeek() {
        
        //A modification has happend -> the expression is no longer saved
        if(currentMode.get().equals(Mode.VIEW)){
            expressionIsSaved.set(true);
        }else{
            expressionIsSaved.set(false);
        }
        
        if (selectedExpression.isNotNull().get()) {

            Expression exp = makeExpression();
            auditTextArea.setText(exp.produceExpressionTreeAudit());
            graphExpressionTree(exp.getExpressionTree());
            populatePeeks(exp);
            
        }else {
            
            graphExpressionTree(null);
            auditTextArea.setText("");
            rmPeekTextArea.setText("");
            unPeekTextArea.setText("");
            
        }
    }
    
    private Expression makeExpression(){
        //Creates a new expression from the modifications
        
        String fullText = makeStringFromTextFlow();
        
        Expression exp = squidProject.getTask().generateExpressionFromRawExcelStyleText(
                expressionNameTextField.getText(),
                fullText.trim().replace("\n", ""),
                false
        );
        
        ExpressionTreeInterface expTree = exp.getExpressionTree();
        
        expTree.setSquidSwitchSTReferenceMaterialCalculation(refMatSwitchCheckBox.isSelected());
        expTree.setSquidSwitchSAUnknownCalculation(unknownsSwitchCheckBox.isSelected());
        expTree.setSquidSwitchConcentrationReferenceMaterialCalculation(concRefMatSwitchCheckBox.isSelected());
        expTree.setSquidSwitchSCSummaryCalculation(summaryCalculationSwitchCheckBox.isSelected());
        
        return exp;
    }
    
    private Expression copySelectedExpression(){
        Expression exp = new Expression(selectedExpression.get().getName(), selectedExpression.get().getExcelExpressionString());
        
        ExpressionTree expTreeCopy = (ExpressionTree) exp.getExpressionTree();
        ExpressionTree expTree = (ExpressionTree) selectedExpression.get().getExpressionTree();
        
        copyTreeTags(expTree, expTreeCopy);
        
        return exp;
    }
    
    private void copyTreeTags(ExpressionTreeInterface source, ExpressionTreeInterface dest){
        dest.setSquidSwitchConcentrationReferenceMaterialCalculation(source.isSquidSwitchConcentrationReferenceMaterialCalculation());
        dest.setSquidSwitchSAUnknownCalculation(source.isSquidSwitchSAUnknownCalculation());
        dest.setSquidSwitchSCSummaryCalculation(source.isSquidSwitchSCSummaryCalculation());
        dest.setSquidSwitchSTReferenceMaterialCalculation(source.isSquidSwitchSTReferenceMaterialCalculation());
    }
    
    private void save(){
        //Saves the newly builded expression
        
        Expression exp = makeExpression();
        TaskInterface task = squidProject.getTask();
        //Remove if an expression already exists with the same name
        task.removeExpression(exp);
        task.addExpression(exp);
        //update lists
        populateExpressionListViews();
        //set the new expression as edited expression
        selectedExpression.set(null);
        selectedExpression.set(exp);
        currentMode.set(Mode.VIEW);
        expressionIsSaved.set(true);
        //Calculate peeks
        populatePeeks(exp);
    }
    
    private String makeStringFromTextFlow() {
        return makeStringFromTextList(expressionTextFlow.getChildren());
    }
    
    private String makeStringFromTextList(List<Node> list) {
        StringBuilder sb = new StringBuilder();
        for (Node node : list) {
            if (node instanceof Text) {
                sb.append(((Text) node).getText());
            }
        }
        return sb.toString();
    }
    
    private void buildTextFlowFromString(String string){
        
        String numberRegExp = "^\\d+(\\.\\d+)?$";
        
        List<ExpressionTextNode> children = new ArrayList<>();
        
        //The lexer separates the expression into tokens
        ExpressionsForSquid2Lexer lexer = new ExpressionsForSquid2Lexer(new ANTLRInputStream(string));
        List<? extends Token> tokens = lexer.getAllTokens();
        
        //Creates the notes from tokens
        for(int i = 0 ; i<tokens.size() ; i++){
            Token token = tokens.get(i);
            String nodeText = token.getText().trim();
            
            ExpressionTextNode etn;
            
            // Make a node of the corresponding type
            if(nodeText.matches(numberRegExp)){
                etn = new NumberTextNode(' '+nodeText+' ');
            }else if(listOperators.contains(nodeText)){
                etn = new OperationTextNode(' '+nodeText+' ');
            }else{
                etn = new ExpressionTextNode(' '+nodeText+' ');
            }
            
            etn.setOrdinalIndex(i);
            children.add(etn);
        }
        
        undoListForExpressionTextFlow.add(children);
        redoListForExpressionTextFlow.clear();
        
        expressionTextFlow.getChildren().setAll(children);
        
        updateAuditGraphAndPeek();
    }
    
    private void insertFunctionIntoExpressionTextFlow(String content, double ordinalIndex) {
        //Add spaces in order to split
        content = content.replaceAll("([(,)])", " $1 ");
        String[] funcCall = content.split(" ");
        
        for (int i = 0; i < funcCall.length; i++) {
            if (funcCall[i].compareTo("") != 0) {
                //Add spaces
                funcCall[i] = funcCall[i].replaceAll("([(,)])", " $1 ");
                ExpressionTextNode expressionTextNode = new ExpressionTextNode(funcCall[i]);
                expressionTextNode.setOrdinalIndex(ordinalIndex - (9 - i) * 0.01);
                expressionTextFlow.getChildren().add(expressionTextNode);
            }
        }
        updateExpressionTextFlowChildren();
    }
    
    private void insertOperationIntoExpressionTextFlow(String content, double ordinalIndex) {
        //Add spaces
        ExpressionTextNode exp = new OperationTextNode(' '+content.trim()+' ');
        exp.setOrdinalIndex(ordinalIndex);
        expressionTextFlow.getChildren().add(exp);
        updateExpressionTextFlowChildren();
    }
    
    private void insertNumberIntoExpressionTextFlow(String content, double ordinalIndex) {
        //Add spaces
        ExpressionTextNode exp = new NumberTextNode(' '+content.trim()+' ');
        exp.setOrdinalIndex(ordinalIndex);
        expressionTextFlow.getChildren().add(exp);
        updateExpressionTextFlowChildren();
    }
    
    private void insertExpressionIntoExpressionTextFlow(String content, double ordinalIndex) {
        //Add spaces
        ExpressionTextNode exp = new ExpressionTextNode(' '+content.trim()+' ');
        exp.setOrdinalIndex(ordinalIndex);
        expressionTextFlow.getChildren().add(exp);
        updateExpressionTextFlowChildren();
    }
    
    private void wrapInParentheses(double ordLeft, double ordRight){
        //Insert parentheses before ordLeft and after ordRight
        ExpressionTextNode leftP = new ExpressionTextNode(" ( ");
        leftP.setOrdinalIndex(ordLeft - 0.5);
        ExpressionTextNode rightP = new ExpressionTextNode(" ) ");
        rightP.setOrdinalIndex(ordRight + 0.5);
        expressionTextFlow.getChildren().addAll(leftP, rightP);
        updateExpressionTextFlowChildren();
    }
    
    private void resetDragSources() {
        dragExpressionSource.set(null);
        dragOperationOrFunctionSource.set(null);
        dragSquidRatioModelSource.set(null);
        dragNumberSource.set(null);
    }

    private void graphExpressionTree(ExpressionTreeInterface expTree) {
        
        String contentLocalGraphingOff = "<html>\n"
                    + "<h3> &nbsp; </h3>\n"
                    + "<h3 style=\"text-align:center;\">Local graphing is off.</h3>\n"
                    + "</html>";
        
        String contentNoExpression = "";
        
        String graphContents;
        //Decides the content to show
        if(expTree != null){
            graphContents = ExpressionTreeWriterMathML.toStringBuilderMathML(expTree).toString();
        }else{
            graphContents = contentNoExpression;
        }
        
        //Show in the software?
        if (showGraphCheckBox.isSelected()) {
            graphView.getEngine().loadContent(graphContents);
        } else {
            graphView.getEngine().loadContent(contentLocalGraphingOff);
        }
        
        //Show in the browser?
        if (graphBrowserCheckBox.isSelected()) {
            try {
                Files.write(Paths.get("EXPRESSION.HTML"), graphContents.getBytes());
                BrowserControl.showURI("EXPRESSION.HTML");
            } catch (IOException iOException) {
            }
        }

    }
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    private class ExpressionCellFactory implements Callback<ListView<Expression>, ListCell<Expression>> {
        
        private final List<ContextMenu> contextMenus = new ArrayList<>();
        
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
            
            updateCellMode(currentMode.get(), cell);
            
            currentMode.addListener((observable, oldValue, newValue) -> {
                updateCellMode(newValue, cell);
            });
            
            expressionsAccordion.expandedPaneProperty().addListener((observable, oldValue, newValue) -> {
                updateCellMode(currentMode.get(), cell);
            });
            
            return cell;
        }
        
        private void updateCellMode(Mode mode, ListCell<Expression> cell){
            switch(mode){
                case VIEW :
                    setCellModeView(cell);
                    break;
                case CREATE :
                case EDIT :
                    setCellModeEditCreate(cell);
            }
        }
        
        private void setCellModeView(ListCell<Expression> cell){
            cell.setOnDragDetected((event) -> {
                //Nothing
            });
            cell.setOnDragDone((event) -> {
                //Nothing
            });
            cell.setOnMouseClicked((event) -> {
                currentMode.set(Mode.VIEW);
                selectedExpression.set(cell.getItem());
            });
            cell.setOnMousePressed((event) -> {
                //Nothing
            });
            cell.setOnMouseReleased((event) -> {
                //Nothing
            });
            cell.setCursor(Cursor.HAND);
        }
        
        private void setCellModeEditCreate(ListCell<Expression> cell){
            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                    db.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString("[\"" + cell.getItem().getName() + "\"]");
                    db.setContent(cc);
                    dragExpressionSource.set(cell);
                    cell.setCursor(Cursor.CLOSED_HAND);
                }
            });
            
            cell.setOnDragDone((event) -> {
                cell.setCursor(Cursor.OPEN_HAND);
            });
            
            
            cell.setOnMouseClicked((MouseEvent event) -> {
                //Nothing
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
            
            updateCellMode(currentMode.get(), cell);
            
            currentMode.addListener((observable, oldValue, newValue) -> {
                updateCellMode(newValue, cell);
            });
            
            expressionsAccordion.expandedPaneProperty().addListener((observable, oldValue, newValue) -> {
                updateCellMode(currentMode.get(), cell);
            });
            
            return cell;
        }
        
        private void updateCellMode(Mode mode, ListCell<SquidRatiosModel> cell){
            switch(mode){
                case VIEW :
                    setCellModeView(cell);
                    break;
                case CREATE :
                case EDIT :
                    setCellModeEditCreate(cell);
            }
        }
        
        private void setCellModeView(ListCell<SquidRatiosModel> cell){
            cell.setOnDragDetected(event -> {
                //Nothing
            });
            
            cell.setOnDragDone((event) -> {
                //Nothing
            });
            
            cell.setOnMousePressed((event) -> {
                //Nothing
            });
            
            cell.setOnMouseReleased((event) -> {
                //Nothing
            });
            
            cell.setCursor(Cursor.DEFAULT);
        }
        
        private void setCellModeEditCreate(ListCell<SquidRatiosModel> cell){
            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                    db.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString("[\"" + cell.getItem().getRatioName() + "\"]");
                    db.setContent(cc);
                    dragSquidRatioModelSource.set(cell.getItem());
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
            
            updateCellMode(currentMode.get(), cell);
            
            currentMode.addListener((observable, oldValue, newValue) -> {
                updateCellMode(newValue, cell);
            });
            
            expressionsAccordion.expandedPaneProperty().addListener((observable, oldValue, newValue) -> {
                updateCellMode(currentMode.get(), cell);
            });
            
            return cell;
        }
        
        private void updateCellMode(Mode mode, ListCell<String> cell){
            switch(mode){
                case VIEW :
                    setCellModeView(cell);
                    break;
                case CREATE :
                case EDIT :
                    setCellModeEditCreate(cell);
            }
        }
        
        private void setCellModeView(ListCell<String> cell){
            cell.setOnDragDetected(event -> {
                //Nothing
            });
            
            cell.setOnDragDone((event) -> {
                //Nothing
            });
            
            cell.setOnMousePressed((event) -> {
                //Nothing
            });
            
            cell.setOnMouseReleased((event) -> {
                //Nothing
            });
            
            cell.setCursor(Cursor.DEFAULT);
        }
        
        private void setCellModeEditCreate(ListCell<String> cell){
            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                    db.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString(cell.getItem());
                    db.setContent(cc);
                    dragSource.set(cell.getItem());
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
        
        private final String text;
        private double ordinalIndex;
        private boolean popupShowing;
        
        public ExpressionTextNode(String text) {
            super(text);
            this.text = text;
            this.popupShowing = false;
            
            setStyle(EXPRESSION_LIST_CSS_STYLE_SPECS);
            
            updateMode(currentMode.get());
            
            currentMode.addListener((observable, oldValue, newValue) -> {
                updateMode(newValue);
            });
        }
        
        private void updateMode(Mode mode){
            switch(mode){
                case VIEW :
                    setNodeModeView();
                    break;
                case CREATE :
                case EDIT :
                    setNodeModeEditCreate();
            }
        }
        
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
        
        private void setNodeModeEditCreate(){
            setCursor(Cursor.OPEN_HAND);
            
            setOnMouseClicked((MouseEvent event) -> {
                if (event.getButton() == MouseButton.SECONDARY && event.getEventType() == MouseEvent.MOUSE_CLICKED && !popupShowing) {
                    createExpressionTextNodeContextMenu((ExpressionTextNode) event.getSource()).show((ExpressionTextNode) event.getSource(), event.getScreenX(), event.getScreenY());
                    popupShowing = true;
                }
            });
            
            setOnMousePressed((MouseEvent event) -> {
                setFill(Color.RED);
                setCursor(Cursor.CLOSED_HAND);
            });
            
            setOnMouseReleased((MouseEvent event) -> {
                setFill(Color.BLACK);
                setCursor(Cursor.OPEN_HAND);
            });
            
            setOnDragDetected((MouseEvent event) -> {
                setCursor(Cursor.CLOSED_HAND);
                Dragboard db = startDragAndDrop(TransferMode.MOVE);
                db.setDragView(new Image(SQUID_LOGO_SANS_TEXT_URL, 32, 32, true, true));
                ClipboardContent cc = new ClipboardContent();
                cc.putString(text);
                db.setContent(cc);
                dragTextSource.set((ExpressionTextNode) event.getSource());
            });
            
            setOnDragDone((event) -> {
                setCursor(Cursor.OPEN_HAND);
            });
            
            setOnDragOver((DragEvent event) -> {
                if (event.getGestureSource() != (ExpressionTextNode) event.getSource()) {
                    event.acceptTransferModes(TransferMode.COPY,TransferMode.MOVE);
                }
                event.consume();
            });
            
            setOnDragDropped((DragEvent event) -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                double ord = 0.0;
                
                //Chosing where to put the new node
                if(toggleGroup.getSelectedToggle() == dragndropLeftRadio){
                    ord = ordinalIndex - 0.5;
                }else if(toggleGroup.getSelectedToggle() == dragndropReplaceRadio){
                    ord = ordinalIndex;
                    expressionTextFlow.getChildren().remove(this);
                }else if(toggleGroup.getSelectedToggle() == dragndropRightRadio){
                    ord = ordinalIndex + 0.5;
                }
                
                //If moving an existing node
                if(event.getGestureSource() instanceof ExpressionTextNode){
                    //Just update the index
                    ((ExpressionTextNode)event.getGestureSource()).setOrdinalIndex(ord);
                    updateExpressionTextFlowChildren();
                    success = true;
                }
                //If copying a node from the lists
                else if(db.hasString()){
                    String content = placeholder + db.getString().split(operationFlagDelimeter)[0] + placeholder;
                    //Insert the right type of node
                    if ((dragOperationOrFunctionSource.get() != null) && (!db.getString().contains(operationFlagDelimeter))) {
                        // case of function, make a series of objects
                        insertFunctionIntoExpressionTextFlow(content, ord);
                    }else if ((dragOperationOrFunctionSource.get() != null) && (db.getString().contains(operationFlagDelimeter))) {
                        // case of operation
                        insertOperationIntoExpressionTextFlow(content, ord);
                    } else if ((dragNumberSource.get() != null) && content.contains(numberString)) {
                        // case of "NUMBER"
                        insertNumberIntoExpressionTextFlow(numberString, ord);
                    }else{
                        // case of expression
                        insertExpressionIntoExpressionTextFlow(content, ord);
                    }

                    success = true;
                }
                
                event.setDropCompleted(success);
                
                event.consume();
                resetDragSources();
            });
        }
        
        private void setNodeModeView(){
            setCursor(Cursor.DEFAULT);
            
            setOnMouseClicked((MouseEvent event) -> {
                //Nothing
            });
            
            setOnMousePressed((MouseEvent event) -> {
                //Nothing
            });
            
            setOnMouseReleased((MouseEvent event) -> {
                //Nothing
            });
            
            setOnDragDetected((MouseEvent event) -> {
                //Nothing
            });
            
            setOnDragDone((event) -> {
                //Nothing
            });
            
            setOnDragOver((DragEvent event) -> {
                //Nothing
            });
            
            setOnDragDropped((DragEvent event) -> {
                //Nothing
            });
        }
    }
    
}
