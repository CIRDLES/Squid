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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum;
import org.cirdles.squid.dialogs.SquidMessageDialog;
import static org.cirdles.squid.gui.SquidUI.EXPRESSION_LIST_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.SquidUI.EXPRESSION_TOOLTIP_CSS_STYLE_SPECS;
import static org.cirdles.squid.gui.constants.Squid3GuiConstants.STYLE_MANAGER_TITLE;
import org.cirdles.squid.tasks.expressions.Expression;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REQUIRED_RATIO_NAMES;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_DEFAULT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_RM;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR206PB238U_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR208PB232TH_CALIB_CONST;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.utilities.stateUtilities.SquidPersistentState;
import org.cirdles.squid.tasks.taskDesign.TaskDesign;
import org.cirdles.squid.tasks.taskDesign.TaskDesign10Mass;
import org.cirdles.squid.tasks.taskDesign.TaskDesign11Mass;
import org.cirdles.squid.tasks.taskDesign.TaskDesign9Mass;
import org.cirdles.squid.tasks.taskDesign.TaskDesignBlank;
import static org.cirdles.squid.gui.SquidUI.HEALTHY_EXPRESSION_STYLE;
import static org.cirdles.squid.gui.SquidUI.UNHEALTHY_EXPRESSION_STYLE;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidProject;
import org.cirdles.squid.gui.utilities.fileUtilities.FileHandler;
import org.cirdles.squid.tasks.Task;
import org.cirdles.squid.tasks.TaskInterface;
import static org.cirdles.squid.tasks.expressions.Expression.makeExpressionForAudit;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.REQUIRED_NOMINAL_MASSES;
import org.cirdles.squid.utilities.IntuitiveStringComparator;

/**
 * FXML Controller class
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class TaskEditorController implements Initializable {

    private TaskDesign taskDesigner;

    public static TaskInterface existingTaskToEdit = null;
    // handle for closing stage when Squid closes
    public static final Stage ADD_RATIOS_STAGE = new Stage();

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
    private TextField uncorrConstPbUExpressionText;
    @FXML
    private TextField uncorrConstPbThExpressionText;
    @FXML
    private TextField parentConcExpressionText;
    @FXML
    private TextField pb208Th232ExpressionText;
    @FXML
    private TextFlow defaultMassesListTextFlow;
    @FXML
    private TextFlow defaultRatiosListTextFlow;
    @FXML
    private Button chooseMassesButton;
    @FXML
    private Button chooseRatiosButton;
    @FXML
    private TextField taskNameTextField;
    @FXML
    private TextField taskDescriptionTextField;
    @FXML
    private TextField provenanceTextField;

    private Map<String, ExpressionTreeInterface> namedExpressionsMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private Map<String, Tooltip> tooltipsMap = new HashMap<>();

    private final List<StackPane> undoMassesList = new ArrayList<>();
    private final List<VBox> undoRatiosList = new ArrayList<>();
    private ContextMenu massesCM;
    private ContextMenu ratiosCM;

    private final VBox addNumeratorVBox = new VBox();
    private ToggleGroup numTG = new ToggleGroup();
    private final VBox addDenominatorVBox = new VBox();
    private ToggleGroup denTG = new ToggleGroup();
    private final HBox numDemHBox = new HBox(addNumeratorVBox, addDenominatorVBox);
    private final Label instructions = new Label("   Choose numerator and denominator");
    private final Button addBtn = new Button("Add ratio");
    private final HBox addBtnHBox = new HBox(addBtn);
    private final Label numLabel = new Label("");
    private final Label divLabel = new Label("/");
    private final Label denLabel = new Label("");
    private final HBox infoLabelHBox = new HBox(numLabel, divLabel, denLabel);
    private final VBox addInfo = new VBox(infoLabelHBox, addBtnHBox);
    private final VBox menuVBox = new VBox(instructions, numDemHBox, addInfo);

    public static Squid3Constants.TaskEditTypeEnum editType = Squid3Constants.TaskEditTypeEnum.EDIT_CURRENT;
    @FXML
    private TextArea customExpressionTextArea;
    @FXML
    private Label directivesLabel;
    @FXML
    private Label primaryDPLabel;
    @FXML
    private Label secondaryDPLabel;

    private boolean amGeochronMode;
    @FXML
    private Text hintLabel;

    {
        addBtnHBox.setAlignment(Pos.CENTER);
        addBtn.setStyle(
                "-fx-padding: 5 22 5 22;\n"
                + "    -fx-border-color: #e2e2e2;"
                + "    -fx-border-width: 2;"
                + "    -fx-background-radius: 0;"
                + "    -fx-background-color: #FB6D42;"
                + "    -fx-font-family: SansSerif;"
                + "    -fx-font-size: 11pt;"
                + "    -fx-text-fill: whitesmoke;"
                + "    -fx-background-insets: 0 0 0 0, 0, 1, 2;");

        addBtn.setOnMouseClicked((event) -> {
            taskDesigner.addRatioName(numLabel.getText() + "/" + denLabel.getText());
            namedExpressionsMap = taskDesigner.buildNamedExpressionsMap();
            populateRatios();
            refreshExpressionAudits();
            updateAddButton();
        });

        updateAddButton();

        numLabel.setStyle("-fx-font-family: SansSerif bold;-fx-font-size: 18");
        divLabel.setStyle("-fx-font-family: SansSerif bold;-fx-font-size: 18");
        denLabel.setStyle("-fx-font-family: SansSerif bold;-fx-font-size: 18");

        infoLabelHBox.setAlignment(Pos.CENTER);
        AnchorPane pane = new AnchorPane(menuVBox);
        AnchorPane.setBottomAnchor(menuVBox, 0.0);
        AnchorPane.setTopAnchor(menuVBox, 0.0);
        AnchorPane.setRightAnchor(menuVBox, 0.0);
        AnchorPane.setLeftAnchor(menuVBox, 0.0);

        numTG.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) {
                RadioButton rb = (RadioButton) numTG.getSelectedToggle();
                if (rb != null) {
                    numLabel.setText(rb.getText());
                    updateAddButton();
                }
            }
        });
        denTG.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) {
                RadioButton rb = (RadioButton) denTG.getSelectedToggle();
                if (rb != null) {
                    denLabel.setText(rb.getText());
                    updateAddButton();
                }
            }
        });
        ADD_RATIOS_STAGE.setScene(new Scene(pane, 250, 500));
        ADD_RATIOS_STAGE.setAlwaysOnTop(true);

    }

    private void updateAddButton() {
        String num = numLabel.getText();
        String den = denLabel.getText();
        boolean valid = (num.compareTo(den) != 0)
                && !taskDesigner.getRatioNames().contains(num + "/" + den)
                && !REQUIRED_RATIO_NAMES.contains(num + "/" + den)
                && num.length() > 0 && den.length() > 0;
        addBtn.setDisable(!valid);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titleLabel.setStyle(STYLE_MANAGER_TITLE);

        taskDesigner = SquidPersistentState.getExistingPersistentState().getTaskDesign();
        switch (editType) {
            case EDIT_CURRENT:
                squidProject.getTask().updateTaskDesignFromTask(taskDesigner, true);
                break;
            case EDIT_COPY_CURRENT:
                SquidUIController.squidProject.getTask().updateTaskDesignFromTask(taskDesigner, true);
                taskDesigner.setName("COPY_OF_" + taskDesigner.getName());
                break;
            case EDIT_COPY_CURRENT_NO_EXP:
                SquidUIController.squidProject.getTask().updateTaskDesignFromTask(taskDesigner, false);
                taskDesigner.setName("COPY_OF_" + taskDesigner.getName());
                break;
            case EDIT_EMPTY:
                SquidPersistentState.getExistingPersistentState().setTaskDesign(new TaskDesignBlank());
                break;
            case EDIT_DEFAULT_9_MASS:
                SquidPersistentState.getExistingPersistentState().setTaskDesign(new TaskDesign9Mass());
                break;
            case EDIT_DEFAULT_10_MASS:
                SquidPersistentState.getExistingPersistentState().setTaskDesign(new TaskDesign10Mass());
                break;
            case EDIT_DEFAULT_11_MASS:
                SquidPersistentState.getExistingPersistentState().setTaskDesign(new TaskDesign11Mass());
                break;
            case EDIT_EXISTING_TASK:
                existingTaskToEdit.updateTaskDesignFromTask(taskDesigner, true);
                break;
        }
        amGeochronMode = taskDesigner.getTaskType().compareTo(TaskTypeEnum.GEOCHRON) == 0;

        initTaskDesign();
    }

    private void initTaskDesign() {
        taskDesigner = SquidPersistentState.getExistingPersistentState().getTaskDesign();
        // update to project having parameters spring 2020
        taskDesigner.setPhysicalConstantsModel(squidProject.getPhysicalConstantsModel());
        taskDesigner.setCommonPbModel(squidProject.getCommonPbModel());
        taskDesigner.setUseSBM(squidProject.isUseSBM());
        taskDesigner.setUserLinFits(squidProject.isUserLinFits());
        taskDesigner.setSelectedIndexIsotope(squidProject.getSelectedIndexIsotope());
        taskDesigner.setSquidAllowsAutoExclusionOfSpots(squidProject.isSquidAllowsAutoExclusionOfSpots());
        taskDesigner.setExtPErrU(squidProject.getExtPErrU());
        taskDesigner.setExtPErrTh(squidProject.getExtPErrTh());

        ((RadioButton) taskManagerGridPane.lookup("#" + taskDesigner.getTaskType().getName())).setSelected(true);

        taskNameTextField.setText(taskDesigner.getName());
        taskDescriptionTextField.setText(taskDesigner.getDescription());
        provenanceTextField.setText(taskDesigner.getDescription());
        authorsNameTextField.setText(taskDesigner.getAuthorName());
        labNameTextField.setText(taskDesigner.getLabName());

        setupListeners();

        populateMasses();
        populateRatios();

        if (amGeochronMode) {
            namedExpressionsMap = taskDesigner.buildNamedExpressionsMap();
            populateDirectives();
            showPermutationPlayers();
        } else {
            uncorrConstPbUlabel.setVisible(false);
            uncorrConstPbThlabel.setVisible(false);
            th232U238Label.setVisible(false);
            parentConcLabel.setVisible(false);
            uncorrConstPbUExpressionText.setVisible(false);
            uncorrConstPbThExpressionText.setVisible(false);
            parentConcExpressionText.setVisible(false);
            pb208Th232ExpressionText.setVisible(false);
            ((RadioButton) taskManagerGridPane.lookup("#232")).setVisible(false);
            ((RadioButton) taskManagerGridPane.lookup("#238")).setVisible(false);
            ((RadioButton) taskManagerGridPane.lookup("#direct")).setVisible(false);
            ((RadioButton) taskManagerGridPane.lookup("#indirect")).setVisible(false);
            directivesLabel.setVisible(false);
            primaryDPLabel.setVisible(false);
            secondaryDPLabel.setVisible(false);
            hintLabel.setVisible(false);

        }

        populateCustomExpressionsText();

        chooseMassesButton.setOnMouseClicked((event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                massesCM = createChooseMassesContextMenu();
                massesCM.show(chooseMassesButton, Side.TOP, 0, -50);
            }
        });

        chooseRatiosButton.setOnMouseClicked((event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                ratiosCM = createChooseRatiosContextMenu();
                ratiosCM.show(chooseRatiosButton, Side.TOP, 0, -50);
            }
        });
    }

    private void populateCustomExpressionsText() {
        StringBuilder customExp = new StringBuilder();
        customExp.append("List of Custom Expression Names: \n");
        int counter = 0;
        for (Expression exp : taskDesigner.getCustomTaskExpressions()) {
            counter++;
            customExp.append("\t").append(exp.getName()).append(genSpaces(exp.getName()));
            if (counter % 3 == 0) {
                customExp.append("\n");
            }
        }

        customExpressionTextArea.setText(customExp.toString());
    }

    private String genSpaces(String name) {
        String retVal = "";
        for (int i = name.length(); i < 25; i++) {
            retVal += " ";
        }
        return retVal;
    }

    private void populateMasses() {
        defaultMassesListTextFlow.getChildren().clear();
        defaultMassesListTextFlow.setMaxHeight(30);
        List<String> allMasses = new ArrayList<>();
        if (amGeochronMode) {
            allMasses.addAll(REQUIRED_NOMINAL_MASSES);
        }
        allMasses.addAll(taskDesigner.getNominalMasses());

        Collections.sort(allMasses, new IntuitiveStringComparator<>());

//        allMasses.remove(DEFAULT_BACKGROUND_MASS_LABEL);
//        if (taskDesigner.getIndexOfBackgroundSpecies() >= 0) {
//            allMasses.add((allMasses.size() > taskDesigner.getIndexOfBackgroundSpecies()
//                    ? taskDesigner.getIndexOfBackgroundSpecies() : allMasses.size()),
//                    DEFAULT_BACKGROUND_MASS_LABEL);
//        }
        int count = 1;
        for (String mass : allMasses) {
            StackPane massText;
            if (count == taskDesigner.getIndexOfBackgroundSpecies() + 1) {
                massText = makeMassStackPane(mass, "Aquamarine");
            } else if (REQUIRED_NOMINAL_MASSES.contains(mass)) {
                massText = makeMassStackPane(mass, "pink");
            } else {
                massText = makeMassStackPane(mass, "white");
                massText.setOnMouseClicked((MouseEvent event) -> {
                    if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
                        defaultMassesListTextFlow.getChildren().remove(massText);
                        undoMassesList.add(0, massText);
                        taskDesigner.removeNominalMass(mass);
                        namedExpressionsMap = taskDesigner.buildNamedExpressionsMap();
                        populateMasses();
                        populateRatios();
                        refreshExpressionAudits();
                    }
                });
            }
            massText.setTranslateX(1 * count++);
            defaultMassesListTextFlow.getChildren().add(massText);
        }
    }

    private void populateRatios() {
        defaultRatiosListTextFlow.getChildren().clear();
        defaultRatiosListTextFlow.setMaxHeight(30);
        List<String> ratioNamesR = REQUIRED_RATIO_NAMES;
        int count = 1;

        if (amGeochronMode) {
            for (String ratioName : ratioNamesR) {
                VBox ratio = makeRatioVBox(ratioName);
                ratio.setStyle(ratio.getStyle() + "-fx-border-color: black;-fx-background-color: pink;");
                ratio.setTranslateX(1 * count++);
                defaultRatiosListTextFlow.getChildren().add(ratio);
            }
        }

        List<String> ratioNames = taskDesigner.getRatioNames();
        for (String ratioName : ratioNames) {
            VBox ratio = makeRatioVBox(ratioName);
            ratio.setStyle(ratio.getStyle() + "-fx-border-color: black;");
            ratio.setTranslateX(1 * count++);
            ratio.setOnMouseClicked((MouseEvent event) -> {
                if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
                    namedExpressionsMap.remove(ratioName);
                    defaultRatiosListTextFlow.getChildren().remove(ratio);
                    undoRatiosList.add(0, ratio);
                    taskDesigner.removeRatioName(ratioName);
                    refreshExpressionAudits();
                }
            });

            defaultRatiosListTextFlow.getChildren().add(ratio);
        }
    }

    public static StackPane makeMassStackPane(String massName, String color) {
        Text massText = new Text(massName);
        massText.setFont(new Font("Monospaced Bold", 12));
//        if (massName.compareTo(DEFAULT_BACKGROUND_MASS_LABEL) == 0) {
//            massText.setFill(Paint.valueOf("red"));
//        }

        Shape circle = new Ellipse(15, 15, 20, 14);
        circle.setFill(Paint.valueOf(color));
        circle.setStroke(Paint.valueOf("black"));
        circle.setStrokeWidth(1);

        StackPane mass = new StackPane(circle, massText);
        mass.setUserData(massName);
        mass.setAlignment(Pos.CENTER);

        return mass;
    }

    public static VBox makeRatioVBox(String ratioName) {
        String[] numDen = ratioName.split("/");
        Text num = new Text(numDen[0]);
        num.setFont(new Font("Monospaced Bold", 12));
        Text den = new Text(numDen[1]);
        den.setFont(new Font("Monospaced Bold", 12));
        Shape line = new Line(0, 0, 35, 0);

        VBox ratio = new VBox(num, line, den);
        ratio.setUserData(ratioName);
        ratio.setAlignment(Pos.CENTER);
        ratio.setPadding(new Insets(1, 3, 1, 3));

        return ratio;
    }

    private void updateDirectiveButtons() {
        ((RadioButton) taskManagerGridPane.lookup("#232")).setDisable(squidProject.getTask().getSelectedIndexIsotope().compareTo(Squid3Constants.IndexIsoptopesEnum.PB_208) == 0);
        ((RadioButton) taskManagerGridPane.lookup("#direct")).setDisable(squidProject.getTask().getSelectedIndexIsotope().compareTo(Squid3Constants.IndexIsoptopesEnum.PB_208) == 0);
    }

    private void populateDirectives() {
        updateDirectiveButtons();

        ((RadioButton) taskManagerGridPane.lookup("#" + taskDesigner.getParentNuclide())).setSelected(true);
        ((RadioButton) taskManagerGridPane.lookup("#" + (String) (taskDesigner.isDirectAltPD() ? "direct" : "indirect"))).setSelected(true);

        uncorrConstPbUlabel.setText(UNCOR206PB238U_CALIB_CONST + ":");
        String UTh_U = taskDesigner.getSpecialSquidFourExpressionsMap().get(UNCOR206PB238U_CALIB_CONST);
        uncorrConstPbUExpressionText.setText(UTh_U);
        uncorrConstPbUExpressionText.setUserData(UNCOR206PB238U_CALIB_CONST);
        uncorrConstPbUExpressionText.setStyle(makeExpression(UNCOR206PB238U_CALIB_CONST, UTh_U).amHealthy() ? HEALTHY_EXPRESSION_STYLE : UNHEALTHY_EXPRESSION_STYLE);

        uncorrConstPbThlabel.setText(UNCOR208PB232TH_CALIB_CONST + ":");
        String UTh_Th = taskDesigner.getSpecialSquidFourExpressionsMap().get(UNCOR208PB232TH_CALIB_CONST);
        uncorrConstPbThExpressionText.setText(UTh_Th);
        uncorrConstPbThExpressionText.setUserData(UNCOR208PB232TH_CALIB_CONST);
        uncorrConstPbThExpressionText.setStyle(makeExpression(UNCOR208PB232TH_CALIB_CONST, UTh_Th).amHealthy() ? HEALTHY_EXPRESSION_STYLE : UNHEALTHY_EXPRESSION_STYLE);

        th232U238Label.setText(TH_U_EXP_RM + ":");
        String thU = taskDesigner.getSpecialSquidFourExpressionsMap().get(TH_U_EXP_DEFAULT);
        pb208Th232ExpressionText.setText(thU);
        pb208Th232ExpressionText.setUserData(TH_U_EXP_DEFAULT);
        pb208Th232ExpressionText.setStyle(makeExpression(TH_U_EXP_DEFAULT, thU).amHealthy() ? HEALTHY_EXPRESSION_STYLE : UNHEALTHY_EXPRESSION_STYLE);

        parentConcLabel.setText(PARENT_ELEMENT_CONC_CONST + ":");
        String parentPPM = taskDesigner.getSpecialSquidFourExpressionsMap().get(PARENT_ELEMENT_CONC_CONST);
        parentConcExpressionText.setText(parentPPM);
        parentConcExpressionText.setUserData(PARENT_ELEMENT_CONC_CONST);
        parentConcExpressionText.setStyle(makeExpression(PARENT_ELEMENT_CONC_CONST, parentPPM).amHealthy() ? HEALTHY_EXPRESSION_STYLE : UNHEALTHY_EXPRESSION_STYLE);

    }

    @FXML
    private void geochronTaskTypeRadioButtonAction(ActionEvent event) {
        taskDesigner.setTaskType(TaskTypeEnum.valueOf(geochronTaskTypeRadioButton.getId()));
    }

    @FXML
    private void generalTaskTypeRadioButtonAction(ActionEvent event) {
        taskDesigner.setTaskType(TaskTypeEnum.valueOf(generalTaskTypeRadioButton.getId()));
    }

    private void setupListeners() {
        taskNameTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            taskDesigner.setName(newValue);
        });

        taskDescriptionTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            taskDesigner.setDescription(newValue);
        });

        provenanceTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            taskDesigner.setProvenance(newValue);
        });

        authorsNameTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            taskDesigner.setAuthorName(newValue);
        });

        labNameTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            taskDesigner.setLabName(newValue);
        });

        final StringProperty uncorrConstPbUExpressionString = new SimpleStringProperty();
        uncorrConstPbUExpressionText.textProperty().bindBidirectional(uncorrConstPbUExpressionString);
        uncorrConstPbUExpressionString.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Expression exp = makeExpression(UNCOR206PB238U_CALIB_CONST, uncorrConstPbUExpressionString.get());
                taskDesigner.getSpecialSquidFourExpressionsMap()
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
                taskDesigner.getSpecialSquidFourExpressionsMap()
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
                taskDesigner.getSpecialSquidFourExpressionsMap()
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
                taskDesigner.getSpecialSquidFourExpressionsMap()
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
            try {
                tooltipsMap.get((String) ((TextField) event.getSource()).getUserData()).hide();
            } catch (Exception e) {
            }
        }
    };

    private void showToolTip(MouseEvent event, TextField textField, Tooltip tooltip) {
        if (tooltip != null) {
            tooltip.hide();
            if (event.isShiftDown()) {
                // force calculation of tolltip extents so it locates correctly
                tooltip.show(textField, event.getScreenX() - 50, event.getScreenY() - tooltip.getHeight() - 50);
                tooltip.hide();
                tooltip.show(textField, event.getScreenX() - 50, event.getScreenY() - tooltip.getHeight() - 50);
            }
        }
    }

    /**
     *
     * @param expressionName
     * @param expressionString
     * @return
     */
    private Expression makeExpression(String expressionName, final String expressionString) {
        return makeExpressionForAudit(expressionName, expressionString, namedExpressionsMap);
    }

    @FXML
    private void toggleParentNuclideAction(ActionEvent event) {
        taskDesigner.setParentNuclide(((RadioButton) event.getSource()).getId());
        showPermutationPlayers();
    }

    @FXML
    private void toggleDirectAltAction(ActionEvent event) {
        taskDesigner.setDirectAltPD(((RadioButton) event.getSource()).getId().compareToIgnoreCase("DIRECT") == 0);
        showPermutationPlayers();
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

        expressionText.setStyle((player ? playerStyle : notPlayerStyle) + (unhealthy ? UNHEALTHY_EXPRESSION_STYLE : HEALTHY_EXPRESSION_STYLE));
    }

    private void updateExpressionHealthFlag(TextField expressionText, boolean healthy) {
        if (healthy) {
            expressionText.setStyle(expressionText.getStyle().replace("wrongx_icon", "icon_checkmark"));
        } else {
            expressionText.setStyle(expressionText.getStyle().replace("icon_checkmark", "wrongx_icon"));
        }
    }

    @FXML
    private void chooseMassesAction(ActionEvent event) {
    }

    @FXML
    private void chooseRatiosAction(ActionEvent event) {
    }

    private RadioButton makeMassRadioButton(String mass, ToggleGroup tg) {
        RadioButton nrb = new RadioButton(mass);
        nrb.setToggleGroup(tg);
        nrb.setStyle("-fx-font-family: SanSerif Bold;-fx-font-size:14");
        nrb.setPrefSize(100, 30);
        nrb.setPadding(new Insets(0, 0, 0, 40));
        return nrb;
    }

    private ContextMenu createChooseRatiosContextMenu() {
        List<MenuItem> itemsForThisNode = new ArrayList<>();

        MenuItem menuItem = new MenuItem("Add ratio");
        if (!ADD_RATIOS_STAGE.isShowing()) {
            menuItem.setOnAction((evt) -> {
                numLabel.setText("");
                denLabel.setText("");
                addBtn.setDisable(true);
                List<String> masses = new ArrayList<>();
                masses.addAll(REQUIRED_NOMINAL_MASSES);
                masses.addAll(taskDesigner.getNominalMasses());
                //                 DEFAULT_BACKGROUND_MASS_LABEL);
                Collections.sort(masses, new IntuitiveStringComparator<>());
                masses.remove(taskDesigner.getIndexOfBackgroundSpecies());
                addNumeratorVBox.getChildren().clear();
                addDenominatorVBox.getChildren().clear();
                for (String mass : masses) {
                    addNumeratorVBox.getChildren().add(makeMassRadioButton(mass, numTG));
                    addDenominatorVBox.getChildren().add(makeMassRadioButton(mass, denTG));
                }
                ADD_RATIOS_STAGE.setX(SquidUI.primaryStageWindow.getX() + 800);//(SquidUI.primaryStageWindow.getWidth() - 600) / 2);
                ADD_RATIOS_STAGE.setY(SquidUI.primaryStageWindow.getY() + 100);//(SquidUI.primaryStageWindow.getHeight() - 250) / 2);
                ADD_RATIOS_STAGE.show();//AndWait();
            });
        }
        itemsForThisNode.add(menuItem);

        menuItem = new MenuItem("Restore ratio");
        menuItem.setDisable(undoRatiosList.isEmpty());
        menuItem.setOnAction((evt) -> {
            if (undoRatiosList.size() > 0) {
                VBox last = undoRatiosList.get(0);
                undoRatiosList.remove(last);
                taskDesigner.addRatioName((String) last.getUserData());
                namedExpressionsMap = taskDesigner.buildNamedExpressionsMap();
                populateRatios();
                refreshExpressionAudits();
            }
        });
        itemsForThisNode.add(menuItem);

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().setAll(itemsForThisNode);
        return contextMenu;
    }

    private ContextMenu createChooseMassesContextMenu() {
        List<MenuItem> itemsForThisNode = new ArrayList<>();

        TextField massName = new TextField();
        massName.setPrefWidth(40);
        massName.textProperty().addListener((ObservableValue<? extends Object> observable, Object oldValue, Object newValue) -> {
            massName.setPrefWidth((massName.getText().trim().length() + 2) * massName.getFont().getSize());
        });
        MenuItem menuItem = new MenuItem("Add masses separated by commas", massName);
        menuItem.setOnAction((evt) -> {
            String mass = massName.getText();
            if (mass.length() > 0) {
                // split on commas
                String [] massesAdded = massName.getText().split(",");
                for (int i = 0; i < massesAdded.length; i ++){
                    taskDesigner.addNominalMass(massesAdded[i].trim());
                }
            }
            populateMasses();
        });
        itemsForThisNode.add(menuItem);

        TextField bkgIndexString = new TextField();
        bkgIndexString.setPrefWidth(40);
        bkgIndexString.textProperty().addListener((ObservableValue<? extends Object> observable, Object oldValue, Object newValue) -> {
            bkgIndexString.setPrefWidth((bkgIndexString.getText().trim().length() + 2) * bkgIndexString.getFont().getSize());
        });
        menuItem = new MenuItem("Place BKG label at position 1, 2, 3, ...", bkgIndexString);
        menuItem.setOnAction((evt) -> {
            String index = bkgIndexString.getText();
            try {
                int bkgIndex = StrictMath.abs(Integer.parseInt(index));
                taskDesigner.setIndexOfBackgroundSpecies(bkgIndex - 1);
                populateMasses();
            } catch (NumberFormatException numberFormatException) {
                // do nothing
            }
        });
        itemsForThisNode.add(menuItem);

        menuItem = new MenuItem("Restore mass");
        menuItem.setDisable(undoMassesList.isEmpty());
        menuItem.setOnAction((evt) -> {
            if (undoMassesList.size() > 0) {
                StackPane last = undoMassesList.get(0);
                undoMassesList.remove(last);
                taskDesigner.addNominalMass((String) last.getUserData());
                populateMasses();
            }
        });
        itemsForThisNode.add(menuItem);

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().setAll(itemsForThisNode);
        return contextMenu;
    }

    @FXML
    private void updateCurrentTaskWithThisTaskAction(ActionEvent event) {
        if (squidProject.getTask().getTaskType().equals(SquidPersistentState.getExistingPersistentState().getTaskDesign().getTaskType())) {
            // check the mass count
            boolean valid = (squidProject.getTask().getSquidSpeciesModelList().size()
                    == (SquidPersistentState.getExistingPersistentState().getTaskDesign().getNominalMasses().size()
                    + (amGeochronMode ? REQUIRED_NOMINAL_MASSES.size() : 0)));
            if (valid) {
                squidProject.createNewTask();
                squidProject.getTask().updateTaskFromTaskDesign(SquidPersistentState.getExistingPersistentState().getTaskDesign(), false);
                MenuItem menuItemTaskManager = ((MenuBar) SquidUI.primaryStage.getScene()
                        .getRoot().getChildrenUnmodifiable().get(0)).getMenus().get(2).getItems().get(0);
                menuItemTaskManager.fire();

            } else {
                SquidMessageDialog.showInfoDialog(
                        "The data file has " + squidProject.getTask().getSquidSpeciesModelList().size()
                        + " masses, but the Task Designer specifies "
                        + ((amGeochronMode ? REQUIRED_NOMINAL_MASSES.size() : 0) + SquidPersistentState.getExistingPersistentState().getTaskDesign().getNominalMasses().size())
                        + ".",
                        primaryStageWindow);
            }
        } else {
            SquidMessageDialog.showInfoDialog(
                    "The data file is type  " + squidProject.getTask().getTaskType()
                    + ", but the Task Designer specifies type "
                    + SquidPersistentState.getExistingPersistentState().getTaskDesign().getTaskType()
                    + ".",
                    primaryStageWindow);
        }
    }

    @FXML
    private void saveThisTaskAsXMLFileAction(ActionEvent event) {
        TaskInterface task = new Task();
        task.updateTaskFromTaskDesign(SquidPersistentState.getExistingPersistentState().getTaskDesign(), true);
        try {
            FileHandler.saveTaskFileXML(task, SquidUI.primaryStageWindow);
            taskNameTextField.setText(task.getName());
        } catch (IOException iOException) {
        }
    }

    @FXML
    private void viewCurrentTaskAction(ActionEvent event) {
        MenuItem menuItemTaskManager = ((MenuBar) SquidUI.primaryStage.getScene()
                .getRoot().getChildrenUnmodifiable().get(0)).getMenus().get(2).getItems().get(0);
        menuItemTaskManager.fire();
    }

}
