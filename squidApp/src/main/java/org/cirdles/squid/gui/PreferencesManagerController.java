/*
 * Copyright 2019 James F. Bowring and CIRDLES.org.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.constants.Squid3Constants.IndexIsoptopesEnum;
import static org.cirdles.squid.constants.Squid3Constants.SpotTypes.UNKNOWN;
import org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum;
import static org.cirdles.squid.gui.SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.SquidUI.EXPRESSION_TOOLTIP_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.SquidUI.HEALTHY_URL;
import static org.cirdles.squid.gui.SquidUI.UNHEALTHY_URL;
import static org.cirdles.squid.gui.SquidUIController.squidLabData;
import static org.cirdles.squid.gui.constants.Squid3GuiConstants.STYLE_MANAGER_TITLE;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.tasks.expressions.Expression;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_DEFAULT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR206PB238U_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR208PB232TH_CALIB_CONST;
import org.cirdles.squid.tasks.expressions.expressionTrees.BuiltInExpressionInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.utilities.stateUtilities.SquidPersistentState;
import org.cirdles.squid.utilities.stateUtilities.SquidUserPreferences;

/**
 * FXML Controller class
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class PreferencesManagerController implements Initializable {

    private SquidUserPreferences squidUserPreferences;

    @FXML
    private GridPane taskManagerGridPane;
    @FXML
    private RadioButton geochronTaskTypeRadioButton;
    @FXML
    private ToggleGroup taskTypeToggleGroup;
    @FXML
    private RadioButton generalTaskTypeRadioButton;
    @FXML
    private TextField authorsNameTextField;
    @FXML
    private TextField labNameTextField;
    @FXML
    private ComboBox<ParametersModel> refMatModelComboBox;
    @FXML
    private ComboBox<ParametersModel> concRefMatModelComboBox;
    @FXML
    private RadioButton pb204RadioButton;
    @FXML
    private ToggleGroup toggleGroupIsotope;
    @FXML
    private RadioButton pb207RadioButton;
    @FXML
    private RadioButton pb208RadioButton;
    @FXML
    private RadioButton yesSBMRadioButton;
    @FXML
    private ToggleGroup toggleGroupSMB;
    @FXML
    private RadioButton noSBMRadioButton;
    @FXML
    private RadioButton linearRegressionRatioCalcRadioButton;
    @FXML
    private ToggleGroup toggleGroupRatioCalcMethod;
    @FXML
    private RadioButton spotAverageRatioCalcRadioButton;
    @FXML
    private CheckBox autoExcludeSpotsCheckBox;
    @FXML
    private Spinner<Double> assignedExternalErrSpinner;
    @FXML
    private ComboBox<ParametersModel> commonPbModelComboBox;
    @FXML
    private ComboBox<ParametersModel> physConstModelComboBox;
    @FXML
    private Label titleLabel;
    @FXML
    private ToggleGroup primaryAgeToggleGroup;
    @FXML
    private ToggleGroup dirctALTtoggleGroup;
    @FXML
    private Label uncorrConstPbUlabel;
    @FXML
    private Label uncorrConstPbThlabel;
    @FXML
    private Label th232U238Label;
    @FXML
    private Label parentConcLabel;
    @FXML
    private ComboBox<String> delimeterComboBox;
    @FXML
    private TextField uncorrConstPbUExpressionText;
    @FXML
    private TextField uncorrConstPbThExpressionText;
    @FXML
    private TextField parentConcExpressionText;
    @FXML
    private TextField pb208Th232ExpressionText;

    private Map<String, ExpressionTreeInterface> namedExpressionsMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private Map<String, Tooltip> tooltipsMap = new HashMap<>();

    private final String healthyStyle
            = "-fx-background-image:url('\"" + HEALTHY_URL
            + "\"');-fx-background-repeat: no-repeat;-fx-background-position: left center;"
            + " -fx-background-size: 16px 16px;";
    private final String unHealthyStyle
            = "-fx-background-image:url('\"" + UNHEALTHY_URL
            + "\"');-fx-background-repeat: no-repeat;-fx-background-position: left center;"
            + " -fx-background-size: 16px 16px;";

    private Map<KeyCode, Boolean> keyMap = new HashMap<>();
    @FXML
    private TextFlow defaultMassesListTextFlow;
    @FXML
    private TextFlow defaultRatiosListTextFlow;
    @FXML
    private Button undoRatioButton;
    
    private List<VBox> undoList = new ArrayList<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titleLabel.setStyle(STYLE_MANAGER_TITLE);
        squidUserPreferences = SquidPersistentState.getExistingPersistentState().getSquidUserPreferences();

        ((RadioButton) taskManagerGridPane.lookup("#" + squidUserPreferences.getTaskType().getName())).setSelected(true);

        authorsNameTextField.setText(squidUserPreferences.getAuthorName());
        labNameTextField.setText(squidUserPreferences.getLabName());

        if (squidUserPreferences.isUseSBM()) {
            yesSBMRadioButton.setSelected(true);
        } else {
            noSBMRadioButton.setSelected(true);
        }

        if (squidUserPreferences.isUserLinFits()) {
            linearRegressionRatioCalcRadioButton.setSelected(true);
        } else {
            spotAverageRatioCalcRadioButton.setSelected(true);
        }

        ((RadioButton) taskManagerGridPane.lookup("#" + squidUserPreferences.getSelectedIndexIsotope().getName())).setSelected(true);

        autoExcludeSpotsCheckBox.setSelected(squidUserPreferences.isSquidAllowsAutoExclusionOfSpots());

        SpinnerValueFactory<Double> valueFactory
                = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.50, 1.00, squidUserPreferences.getExtPErr(), 0.05);
        assignedExternalErrSpinner.setValueFactory(valueFactory);
        assignedExternalErrSpinner.valueProperty().addListener(new ChangeListener<Double>() {

            @Override
            public void changed(ObservableValue<? extends Double> observable,//
                    Double oldValue, Double newValue) {
                squidUserPreferences.setExtPErr(assignedExternalErrSpinner.getValue());
            }
        });

        setupListeners();
        setUpParametersModelsComboBoxes();

        // samples
        ObservableList<String> delimetersList = FXCollections.observableArrayList(Squid3Constants.SampleNameDelimetersEnum.names());
        delimeterComboBox.setItems(delimetersList);
        // set value before adding listener
        delimeterComboBox.getSelectionModel().select(squidUserPreferences.getDelimiterForUnknownNames());
        delimeterComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> ov,
                    final String oldvalue, final String newvalue) {
                squidUserPreferences.setDelimiterForUnknownNames(newvalue);
            }
        });

        populateMasses();
        populateRatios();

        namedExpressionsMap = squidUserPreferences.buildNamedExpressionsMap();

        populateDirectives();
        showPermutationPlayers();
        
        undoRatioButton.setVisible(false);
    }

    private void populateMasses() {
        defaultMassesListTextFlow.setPadding(new Insets(0, 0, 0, 10));
        List<String> nominalMasses = squidUserPreferences.getNominalMasses();
        int count = 1;
        for (String nominalMass : nominalMasses) {
            Text massText = new Text(nominalMass);
            massText.setFont(new Font("Monospaced Bold", 14));
            massText.setTranslateY(5);
            massText.setTranslateX(15 * count++);
            defaultMassesListTextFlow.getChildren().add(massText);
        }
    }

    private void populateRatios() {
        List<String> ratioNamesR = squidUserPreferences.getRequiredRatioNames();
        int count = 1;
        for (String ratioName : ratioNamesR) {
            VBox ratio = makeRatioVBox(ratioName);
            ratio.setStyle(ratio.getStyle() + "-fx-border-color: black;-fx-background-color: pink;");

            ratio.setTranslateX(1 * count++);
            defaultRatiosListTextFlow.getChildren().add(ratio);
        }

        List<String> ratioNames = squidUserPreferences.getRatioNames();
        for (String ratioName : ratioNames) {
            VBox ratio = makeRatioVBox(ratioName);
            ratio.setStyle(ratio.getStyle() + "-fx-border-color: black;");
            ratio.setTranslateX(1 * count++);
            ratio.setOnMouseClicked((MouseEvent event) -> {
                if (event.getButton() == MouseButton.SECONDARY && event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    namedExpressionsMap.remove(ratioName);
                    defaultRatiosListTextFlow.getChildren().remove(ratio);
                    undoList.add(ratio);
                    undoRatioButton.setVisible(true);
                    squidUserPreferences.removeRatioName(ratioName);
                    refreshExpressionAudits();
                }
            });

            defaultRatiosListTextFlow.getChildren().add(ratio);
        } 
    }

    private VBox makeRatioVBox(String ratioName) {
        String[] numDen = ratioName.split("/");
        Text num = new Text(numDen[0]);
        num.setFont(new Font("Monospaced Bold", 12));
        Text den = new Text(numDen[1]);
        den.setFont(new Font("Monospaced Bold", 12));
        Shape line = new Line(0, 0, 35, 0);

        VBox ratio = new VBox(num, line, den);
        ratio.setAlignment(Pos.CENTER);
        ratio.setPadding(new Insets(1, 3, 1, 3));

        return ratio;
    }

    private void populateDirectives() {
        ((RadioButton) taskManagerGridPane.lookup("#" + squidUserPreferences.getParentNuclide())).setSelected(true);
        ((RadioButton) taskManagerGridPane.lookup("#" + (String) (squidUserPreferences.isDirectAltPD() ? "direct" : "indirect"))).setSelected(true);

        pb208RadioButton.setVisible(
                ((RadioButton) taskManagerGridPane.lookup("#238")).isSelected()
                && ((RadioButton) taskManagerGridPane.lookup("#indirect")).isSelected());

        uncorrConstPbUlabel.setText(UNCOR206PB238U_CALIB_CONST + ":");
        String UTh_U = squidUserPreferences.getSpecialSquidFourExpressionsMap().get(UNCOR206PB238U_CALIB_CONST);
        uncorrConstPbUExpressionText.setText(UTh_U);
        uncorrConstPbUExpressionText.setUserData(UNCOR206PB238U_CALIB_CONST);
        uncorrConstPbUExpressionText.setStyle(
                makeExpression(UNCOR206PB238U_CALIB_CONST, UTh_U).amHealthy() ? healthyStyle : unHealthyStyle);

        uncorrConstPbThlabel.setText(UNCOR208PB232TH_CALIB_CONST + ":");
        String UTh_Th = squidUserPreferences.getSpecialSquidFourExpressionsMap().get(UNCOR208PB232TH_CALIB_CONST);
        uncorrConstPbThExpressionText.setText(UTh_Th);
        uncorrConstPbThExpressionText.setUserData(UNCOR208PB232TH_CALIB_CONST);
        uncorrConstPbThExpressionText.setStyle(
                makeExpression(UNCOR208PB232TH_CALIB_CONST, UTh_Th).amHealthy() ? healthyStyle : unHealthyStyle);

        th232U238Label.setText(TH_U_EXP_RM + ":");
        String thU = squidUserPreferences.getSpecialSquidFourExpressionsMap().get(TH_U_EXP_DEFAULT);
        pb208Th232ExpressionText.setText(thU);
        pb208Th232ExpressionText.setUserData(TH_U_EXP_DEFAULT);
        pb208Th232ExpressionText.setStyle(
                makeExpression(TH_U_EXP_DEFAULT, thU).amHealthy() ? healthyStyle : unHealthyStyle);

        parentConcLabel.setText(PARENT_ELEMENT_CONC_CONST + ":");
        String parentPPM = squidUserPreferences.getSpecialSquidFourExpressionsMap().get(PARENT_ELEMENT_CONC_CONST);
        parentConcExpressionText.setText(parentPPM);
        parentConcExpressionText.setUserData(PARENT_ELEMENT_CONC_CONST);
        parentConcExpressionText.setStyle(
                makeExpression(PARENT_ELEMENT_CONC_CONST, parentPPM).amHealthy() ? healthyStyle : unHealthyStyle);

    }

    @FXML
    private void geochronTaskTypeRadioButtonAction(ActionEvent event) {
        squidUserPreferences.setTaskType(TaskTypeEnum.valueOf(geochronTaskTypeRadioButton.getId()));
    }

    @FXML
    private void generalTaskTypeRadioButtonAction(ActionEvent event) {
        squidUserPreferences.setTaskType(TaskTypeEnum.valueOf(generalTaskTypeRadioButton.getId()));
    }

    private void setupListeners() {

        authorsNameTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            squidUserPreferences.setAuthorName(newValue);
        });

        labNameTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            squidUserPreferences.setLabName(newValue);
        });

        final StringProperty uncorrConstPbUExpressionString = new SimpleStringProperty();
        uncorrConstPbUExpressionText.textProperty().bindBidirectional(uncorrConstPbUExpressionString);
        uncorrConstPbUExpressionString.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Expression exp = makeExpression(UNCOR206PB238U_CALIB_CONST, uncorrConstPbUExpressionString.get());
                squidUserPreferences.getSpecialSquidFourExpressionsMap()
                        .put(UNCOR206PB238U_CALIB_CONST, uncorrConstPbUExpressionString.get());
                tooltipMapPut(exp, UNCOR206PB238U_CALIB_CONST);
                updateExpressionHealthFlag(uncorrConstPbUExpressionText, exp.amHealthy());
            }
        });
        uncorrConstPbUExpressionText.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEnteredExpressionEventHandler);
        uncorrConstPbUExpressionText.setOnMouseExited(mouseExitedEventHandler);

        final StringProperty uncorrConstPbThExpressionString = new SimpleStringProperty();
        uncorrConstPbThExpressionText.textProperty().bindBidirectional(uncorrConstPbThExpressionString);
        uncorrConstPbThExpressionString.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Expression exp = makeExpression(UNCOR208PB232TH_CALIB_CONST, uncorrConstPbThExpressionString.get());
                squidUserPreferences.getSpecialSquidFourExpressionsMap()
                        .put(UNCOR208PB232TH_CALIB_CONST, uncorrConstPbThExpressionString.get());
                tooltipMapPut(exp, UNCOR208PB232TH_CALIB_CONST);
                updateExpressionHealthFlag(uncorrConstPbThExpressionText, exp.amHealthy());
            }
        });
        uncorrConstPbThExpressionText.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEnteredExpressionEventHandler);
        uncorrConstPbThExpressionText.setOnMouseExited(mouseExitedEventHandler);

        final StringProperty pb208Th232ExpressionString = new SimpleStringProperty();
        pb208Th232ExpressionText.textProperty().bindBidirectional(pb208Th232ExpressionString);
        pb208Th232ExpressionString.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Expression exp = makeExpression(TH_U_EXP_DEFAULT, pb208Th232ExpressionString.get());
                squidUserPreferences.getSpecialSquidFourExpressionsMap()
                        .put(TH_U_EXP_DEFAULT, pb208Th232ExpressionString.get());
                tooltipMapPut(exp, TH_U_EXP_DEFAULT);
                updateExpressionHealthFlag(pb208Th232ExpressionText, exp.amHealthy());
            }
        });
        pb208Th232ExpressionText.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEnteredExpressionEventHandler);
        pb208Th232ExpressionText.setOnMouseExited(mouseExitedEventHandler);

        final StringProperty parentConcExpressionString = new SimpleStringProperty();
        parentConcExpressionText.textProperty().bindBidirectional(parentConcExpressionString);
        parentConcExpressionString.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Expression exp = makeExpression(PARENT_ELEMENT_CONC_CONST, parentConcExpressionString.get());
                squidUserPreferences.getSpecialSquidFourExpressionsMap()
                        .put(PARENT_ELEMENT_CONC_CONST, parentConcExpressionString.get());
                tooltipMapPut(exp, PARENT_ELEMENT_CONC_CONST);

                updateExpressionHealthFlag(parentConcExpressionText, exp.amHealthy());
            }
        });
        parentConcExpressionText.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEnteredExpressionEventHandler);
        parentConcExpressionText.setOnMouseExited(mouseExitedEventHandler);
    }

    private void refreshExpressionAudits() {
        Expression exp = makeExpression(UNCOR206PB238U_CALIB_CONST, uncorrConstPbUExpressionText.getText());
        tooltipMapPut(exp, UNCOR206PB238U_CALIB_CONST);
        updateExpressionHealthFlag(uncorrConstPbUExpressionText, exp.amHealthy());

        exp = makeExpression(UNCOR208PB232TH_CALIB_CONST, uncorrConstPbThExpressionText.getText());
        tooltipMapPut(exp, UNCOR208PB232TH_CALIB_CONST);
        updateExpressionHealthFlag(uncorrConstPbThExpressionText, exp.amHealthy());

        exp = makeExpression(TH_U_EXP_DEFAULT, pb208Th232ExpressionText.getText());
        tooltipMapPut(exp, TH_U_EXP_DEFAULT);
        updateExpressionHealthFlag(pb208Th232ExpressionText, exp.amHealthy());

        exp = makeExpression(PARENT_ELEMENT_CONC_CONST, parentConcExpressionText.getText());
        tooltipMapPut(exp, PARENT_ELEMENT_CONC_CONST);
        updateExpressionHealthFlag(parentConcExpressionText, exp.amHealthy());

    }

    private void tooltipMapPut(Expression exp, String expressionText) {
        Tooltip audit = new Tooltip(exp.produceExpressionTreeAudit());
        audit.setStyle(EXPRESSION_TOOLTIP_CSS_STYLE_SPECS);
        Tooltip result = tooltipsMap.put(expressionText, audit);
        if (result != null) {
            result.hide();
        }
    }

    EventHandler<MouseEvent> mouseEnteredExpressionEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            showToolTip(event, ((TextField) event.getSource()), tooltipsMap.get((String) ((TextField) event.getSource()).getUserData()));
        }
    };

    EventHandler<MouseEvent> mouseExitedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            tooltipsMap.get((String) ((TextField) event.getSource()).getUserData()).hide();
        }
    };

    private void showToolTip(MouseEvent event, TextField textField, Tooltip tooltip) {
        if (tooltip != null) {
            tooltip.hide();
            if (event.isShiftDown()) {
                tooltip.show(textField, 225, 225);
            }
        }
    }

    private Expression makeExpression(String expressionName, final String expressionString) {
        //Creates a new expression from the modifications

        String fullText = expressionString;
        Expression exp = new Expression(expressionName, fullText);

        exp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);

        ExpressionTreeInterface expTree = exp.getExpressionTree();

        expTree.setSquidSwitchSTReferenceMaterialCalculation(true);
        expTree.setSquidSwitchSAUnknownCalculation(true);
        expTree.setSquidSwitchConcentrationReferenceMaterialCalculation(false);
        expTree.setSquidSwitchSCSummaryCalculation(false);
        expTree.setSquidSpecialUPbThExpression(true);
        expTree.setUnknownsGroupSampleName(UNKNOWN.getPlotType());

        // to detect ratios of interest
        if (expTree instanceof BuiltInExpressionInterface) {
            ((BuiltInExpressionInterface) expTree).buildExpression();
        }

        return exp;
    }

    private void setUpParametersModelsComboBoxes() {
        // ReferenceMaterials
        refMatModelComboBox.setConverter(new TaskManagerController.ParameterModelStringConverter());
        refMatModelComboBox.setItems(FXCollections.observableArrayList(squidLabData.getReferenceMaterials()));
        refMatModelComboBox.getSelectionModel().select(squidLabData.getRefMatDefault());

        refMatModelComboBox.valueProperty()
                .addListener((ObservableValue<? extends ParametersModel> observable, ParametersModel oldValue, ParametersModel newValue) -> {
                    squidLabData.setRefMatDefault(newValue);
                    squidLabData.storeState();
                });

        // ConcentrationReferenceMaterials
        concRefMatModelComboBox.setConverter(new TaskManagerController.ParameterModelStringConverter());
        concRefMatModelComboBox.setItems(FXCollections.observableArrayList(squidLabData.getReferenceMaterials()));
        concRefMatModelComboBox.getSelectionModel().select(squidLabData.getRefMatConcDefault());

        concRefMatModelComboBox.valueProperty()
                .addListener((ObservableValue<? extends ParametersModel> observable, ParametersModel oldValue, ParametersModel newValue) -> {
                    squidLabData.setRefMatConcDefault(newValue);
                    squidLabData.storeState();
                });

        // PhysicalConstantsModels
        physConstModelComboBox.setConverter(new TaskManagerController.ParameterModelStringConverter());
        physConstModelComboBox.setItems(FXCollections.observableArrayList(squidLabData.getPhysicalConstantsModels()));
        physConstModelComboBox.getSelectionModel().select(squidLabData.getPhysConstDefault());

        physConstModelComboBox.valueProperty()
                .addListener((ObservableValue<? extends ParametersModel> observable, ParametersModel oldValue, ParametersModel newValue) -> {
                    squidLabData.setPhysConstDefault(newValue);
                    squidLabData.storeState();
                });

        // CommonPbModels
        commonPbModelComboBox.setConverter(new TaskManagerController.ParameterModelStringConverter());
        commonPbModelComboBox.setItems(FXCollections.observableArrayList(squidLabData.getCommonPbModels()));
        commonPbModelComboBox.getSelectionModel().select(squidLabData.getCommonPbDefault());

        commonPbModelComboBox.valueProperty()
                .addListener((ObservableValue<? extends ParametersModel> observable, ParametersModel oldValue, ParametersModel newValue) -> {
                    squidLabData.setCommonPbDefault(newValue);
                    squidLabData.storeState();
                });
    }

    @FXML
    private void pb204RadioButtonAction(ActionEvent event) {
        squidUserPreferences.setSelectedIndexIsotope(IndexIsoptopesEnum.valueOf(pb204RadioButton.getId()));
    }

    @FXML
    private void pb207RadioButtonAction(ActionEvent event) {
        squidUserPreferences.setSelectedIndexIsotope(IndexIsoptopesEnum.valueOf(pb207RadioButton.getId()));
    }

    @FXML
    private void pb208RadioButtonAction(ActionEvent event) {
        squidUserPreferences.setSelectedIndexIsotope(IndexIsoptopesEnum.valueOf(pb208RadioButton.getId()));
    }

    @FXML
    private void yesSBMRadioButtonAction(ActionEvent event) {
        squidUserPreferences.setUseSBM(true);
    }

    @FXML
    private void noSBMRadioButtonActions(ActionEvent event) {
        squidUserPreferences.setUseSBM(false);
    }

    @FXML
    private void linearRegressionRatioCalcRadioButtonAction(ActionEvent event) {
        squidUserPreferences.setUserLinFits(true);
    }

    @FXML
    private void spotAverageRatioCalcRadioButtonAction(ActionEvent event) {
        squidUserPreferences.setUserLinFits(false);
    }

    @FXML
    private void autoExcludeSpotsCheckBoxAction(ActionEvent event) {
        squidUserPreferences.setSquidAllowsAutoExclusionOfSpots(autoExcludeSpotsCheckBox.isSelected());
    }

    @FXML
    private void toggleParentNuclideAction(ActionEvent event) {
        squidUserPreferences.setParentNuclide(((RadioButton) event.getSource()).getId());
        showPermutationPlayers();
    }

    @FXML
    private void toggleDirectAltAction(ActionEvent event) {
        squidUserPreferences.setDirectAltPD(((RadioButton) event.getSource()).getId().compareToIgnoreCase("DIRECT") == 0);
        showPermutationPlayers();
    }

    @FXML
    private void defaultMassesAction(ActionEvent event) {
    }

    @FXML
    private void defaultRatiosAction(ActionEvent event) {
    }

    private void showPermutationPlayers() {
        boolean uPicked = ((RadioButton) taskManagerGridPane.lookup("#238")).isSelected();
        boolean directPicked = ((RadioButton) taskManagerGridPane.lookup("#direct")).isSelected();

        boolean perm1 = uPicked && !directPicked;
        boolean perm2 = uPicked && directPicked;
        boolean perm3 = !uPicked && !directPicked;
        boolean perm4 = !uPicked && directPicked;

        updateExpressionStyle(uncorrConstPbUExpressionText, (perm1 || perm2 || perm4));
        updateExpressionStyle(uncorrConstPbThExpressionText, (perm2 || perm3 || perm4));
        updateExpressionStyle(pb208Th232ExpressionText, (perm1 || perm3));
        updateExpressionStyle(parentConcExpressionText, (perm1 || perm2 || perm3 || perm4));
    }

    private void updateExpressionStyle(TextField expressionText, boolean player) {
        String playerStyle = EXPRESSION_LIST_CSS_STYLE_SPECS
                + "-fx-background-color: white;-fx-border-color: red;-fx-border-width: 2;";
        String notPlayerStyle = EXPRESSION_LIST_CSS_STYLE_SPECS
                + "-fx-background-color: white;-fx-border-color: black;";

        boolean unhealthy = expressionText.getStyle().contains("wrongx_icon");

        expressionText.setStyle((player ? playerStyle : notPlayerStyle) + (unhealthy ? unHealthyStyle : healthyStyle));
    }

    private void updateExpressionHealthFlag(TextField expressionText, boolean healthy) {
        if (healthy) {
            expressionText.setStyle(expressionText.getStyle().replace("wrongx_icon", "icon_checkmark"));
        } else {
            expressionText.setStyle(expressionText.getStyle().replace("icon_checkmark", "wrongx_icon"));
        }
    }

    @FXML
    private void undoRatioButtonAction(ActionEvent event) {
    }
}
