/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.parameters;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.StageStyle;
import org.cirdles.squid.dialogs.SquidMessageDialog;
import org.cirdles.squid.gui.parameters.ParametersLauncher.ParametersTab;
import org.cirdles.squid.gui.utilities.fileUtilities.FileHandler;
import org.cirdles.squid.parameters.ParametersModelComparator;
import org.cirdles.squid.parameters.matrices.AbstractMatrixModel;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.CommonPbModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
import org.cirdles.squid.parameters.util.DataDictionary;
import org.cirdles.squid.parameters.valueModels.ValueModel;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

import static org.cirdles.squid.gui.SquidUIController.squidLabData;
import static org.cirdles.squid.gui.parameters.ParametersLauncher.squidLabDataStage;
import static org.cirdles.squid.gui.parameters.ParametersLauncher.squidLabDataWindow;

/**
 * FXML Controller class
 *
 * @author ryanb
 */
public class ParametersManagerGUIController implements Initializable {

    @FXML
    public CheckBox refMatReferenceDatesCheckbox;
    @FXML
    public Tab refMatCorrTab;
    @FXML
    public Tab refMatDataTab;
    @FXML
    public Tab refMatCovTab;
    @FXML
    public Spinner<Integer> refDatesSigFigSpinner;
    @FXML
    public Button refDatesNotationButton;
    @FXML
    public Tab refMatRefDatesTab;
    @FXML
    public TableView<DataModel> refDatesTable;
    @FXML
    public TabPane refMatTabPane;
    @FXML
    public RadioButton refDatesARadioButton;
    @FXML
    public RadioButton refDatesMARadioButton;
    @FXML
    public RadioButton refDatesKARadioButton;
    @FXML
    public ToggleGroup refDatesUnitsToggleGroup;
    @FXML
    private MenuItem editCopyOfCurrPhysConst;
    @FXML
    private MenuItem editNewEmpPhysConst;
    @FXML
    private MenuItem cancelEditOfPhysConst;
    @FXML
    private MenuItem saveAndRegCurrPhysConst;
    @FXML
    private ChoiceBox<String> physConstCB;
    @FXML
    private TextField physConstModelName;
    @FXML
    private TextField physConstLabName;
    @FXML
    private TextField physConstVersion;
    @FXML
    private TextField physConstDateCertified;
    @FXML
    private Label physConstIsEditableLabel;
    @FXML
    private MenuItem saveAndRegCurrRefMat;
    @FXML
    private MenuItem remCurrRefMat;
    @FXML
    private MenuItem canEditOfRefMat;
    @FXML
    private MenuItem editNewEmptyRefMat;
    @FXML
    private MenuItem editCopyOfCurrRefMat;
    @FXML
    private MenuItem editCurrRefMat;
    @FXML
    private ChoiceBox<String> refMatCB;
    @FXML
    private TextField refMatModelName;
    @FXML
    private TextField refMatLabName;
    @FXML
    private TextField refMatVersion;
    @FXML
    private TextField refMatDateCertified;
    @FXML
    private TextField labNameTextField;
    @FXML
    private TextArea physConstReferencesArea;
    @FXML
    private TextArea physConstCommentsArea;
    @FXML
    private TextArea refMatReferencesArea;
    @FXML
    private TextArea refMatCommentsArea;
    @FXML
    private Label refMatIsEditableLabel;
    @FXML
    private TableView<DataModel> physConstDataTable;
    @FXML
    private TableView<RefMatDataModel> refMatDataTable;
    @FXML
    private AnchorPane referencesPane;
    @FXML
    private TableView<DataModel> refMatConcentrationsTable;
    @FXML
    private TableView<ObservableList<SimpleStringProperty>> physConstCorrTable;
    @FXML
    private TableView<ObservableList<SimpleStringProperty>> physConstCovTable;
    @FXML
    private TableView<ObservableList<SimpleStringProperty>> refMatCorrTable;
    @FXML
    private TableView<ObservableList<SimpleStringProperty>> refMatCovTable;
    @FXML
    private TabPane rootTabPane;
    @FXML
    private Menu physConstFileMenu;
    @FXML
    private Menu refMatFileMenu;
    @FXML
    private MenuItem editCurrPhysConst;
    @FXML
    private MenuItem remCurrPhysConst;
    @FXML
    private Button physConstDataNotationButton;
    @FXML
    private Button refMatDataNotationButton;
    @FXML
    private Button refMatConcentrationsNotationButton;
    @FXML
    private TextArea apparentDatesTextArea;
    @FXML
    private Button physConstCorrNotationButton;
    @FXML
    private Button physConstCovNotationButton;
    @FXML
    private Button refMatCorrNotationButton;
    @FXML
    private Button refMatCovNotationButton;
    @FXML
    private AnchorPane molarMassesPane;
    @FXML
    private Spinner<Integer> refMatDataSigFigs;
    @FXML
    private Spinner<Integer> physConstDataSigFigs;
    @FXML
    private Spinner<Integer> physConstCorrSigFigs;
    @FXML
    private Spinner<Integer> physConstCovSigFigs;
    @FXML
    private Spinner<Integer> refMatCorrSigFigs;
    @FXML
    private Spinner<Integer> refMatCovSigFigs;
    @FXML
    private Spinner<Integer> refMatConcSigFigs;
    @FXML
    private MenuItem saveAndRegCommonPbMenuItem;
    @FXML
    private MenuItem cancelEditOfCommonPb;
    @FXML
    private MenuItem remCurrCommonPb;
    @FXML
    private MenuItem editCurrCommonPb;
    @FXML
    private MenuItem editCopyOfCommonPb;
    @FXML
    private MenuItem editNewEmptyCommonPb;
    @FXML
    private TableView<DataModel> commonPbDataTable;
    @FXML
    private Button commonPbDataNotationButton;
    @FXML
    private Spinner<Integer> commonPbDataSigFigs;
    @FXML
    private TableView<ObservableList<SimpleStringProperty>> commonPbCorrTable;
    @FXML
    private Button commonPbCorrNotationButton;
    @FXML
    private TableView<ObservableList<SimpleStringProperty>> commonPbCovTable;
    @FXML
    private Button commonPbCovNotationButton;
    @FXML
    private Spinner<Integer> commonPbCovSigFigs;
    @FXML
    private TextArea commonPbReferencesArea;
    @FXML
    private TextArea commonPbCommentsArea;
    @FXML
    private TextField commonPbModelName;
    @FXML
    private TextField commonPbVersion;
    @FXML
    private Label commonPbIsEditableLabel;
    @FXML
    private TextField commonPbLabName;
    @FXML
    private TextField commonPbDateCertified;
    @FXML
    private Menu commonPbFileMenu;
    @FXML
    private Spinner<Integer> commonPbCorrSigFigs;
    @FXML
    private ChoiceBox<String> commonPbCB;
    @FXML
    private Tab apparentDatesTab;
    @FXML
    private Tab refMatTab;
    @FXML
    private Tab commonPbTab;
    @FXML
    private Tab physConstTab;

    private static final DecimalFormat scientificNotation = new DecimalFormat("0.0##############################E0#############");
    private static final DecimalFormat standardNotation = new DecimalFormat("########################0.0######################################");

    private ParametersModel physConstModel;
    private ParametersModel physConstHolder;

    private ParametersModel refMatModel;
    private ParametersModel refMatHolder;

    private ParametersModel commonPbModel;
    private ParametersModel commonPbModelHolder;

    private List<ParametersModel> physConstModels;
    private List<ParametersModel> refMatModels;
    private List<ParametersModel> commonPbModels;

    private List<TextField> physConstReferences;
    private List<TextField> molarMasses;

    private boolean isEditingCurrPhysConst;
    private boolean isEditingCurrRefMat;
    private boolean isEditingCurrCommonPbModel;

    private DecimalFormat physConstDataNotation;
    private DecimalFormat physConstCorrNotation;
    private DecimalFormat physConstCovNotation;

    private DecimalFormat refMatDataNotation;
    private DecimalFormat refMatConcentrationsNotation;
    private DecimalFormat refMatCorrNotation;
    private DecimalFormat refMatCovNotation;
    private DecimalFormat refDatesNotation;

    private DecimalFormat commonPbDataNotation;
    private DecimalFormat commonPbCorrNotation;
    private DecimalFormat commonPbCovNotation;

    public static boolean isEditingPhysConst;
    public static boolean isEditingRefMat;
    public static boolean isEditingCommonPb;

    public static ParametersTab chosenTab = ParametersTab.physConst;

    private Units refDatesUnits;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setUpSigFigSpinners();

        refDatesUnits = Units.a;

        isEditingCurrCommonPbModel = false;
        isEditingCurrPhysConst = false;
        isEditingCurrRefMat = false;
        isEditingPhysConst = false;
        isEditingRefMat = false;
        isEditingCommonPb = false;

        physConstDataNotation = scientificNotation;
        physConstCorrNotation = scientificNotation;
        physConstCovNotation = scientificNotation;

        refMatDataNotation = scientificNotation;
        refMatConcentrationsNotation = scientificNotation;
        refMatCorrNotation = scientificNotation;
        refMatCovNotation = scientificNotation;
        refDatesNotation = scientificNotation;

        commonPbDataNotation = scientificNotation;
        commonPbCorrNotation = scientificNotation;
        commonPbCovNotation = scientificNotation;

        physConstModels = squidLabData.getPhysicalConstantsModels();
        setUpPhysConstCB();

        refMatModels = squidLabData.getReferenceMaterials();
        setUpRefMatCB();

        commonPbModels = squidLabData.getCommonPbModels();
        setUpCommonPbCB();

        setUpTabs();
        setUpApparentDatesTabSelection();
        setUpLaboratoryName();
        setUpDatesCheckboxVisibilityListener();
    }

    private void setUpTabs() {
        refMatTab.setOnSelectionChanged(val -> {
            if (refMatTab.isSelected()) {
                chosenTab = ParametersTab.refMat;
            }
        });
        commonPbTab.setOnSelectionChanged(val -> {
            if (commonPbTab.isSelected()) {
                chosenTab = ParametersTab.commonPb;
            }
        });
        physConstTab.setOnSelectionChanged(val -> {
            if (physConstTab.isSelected()) {
                chosenTab = ParametersTab.physConst;
            }
        });

        squidLabDataStage.focusedProperty().addListener(listener -> {
            if (squidLabDataStage.isFocused()) {
                if (chosenTab.equals(ParametersTab.physConst)) {
                    rootTabPane.getSelectionModel().select(physConstTab);
                } else if (chosenTab.equals(ParametersTab.refMat)) {
                    rootTabPane.getSelectionModel().select(refMatTab);
                } else if (chosenTab.equals(ParametersTab.commonPb)) {
                    rootTabPane.getSelectionModel().select(commonPbTab);
                }

                int selectedIndex;
                if (!isEditingPhysConst) {
                    selectedIndex = physConstCB.getSelectionModel().getSelectedIndex();
                    setUpPhysConstCBItems();
                    physConstCB.getSelectionModel().select(selectedIndex);
                }
                if (!isEditingRefMat) {
                    selectedIndex = refMatCB.getSelectionModel().getSelectedIndex();
                    setUpRefMatCBItems();
                    refMatCB.getSelectionModel().select(selectedIndex);
                }
                if (!isEditingCommonPb) {
                    selectedIndex = commonPbCB.getSelectionModel().getSelectedIndex();
                    setUpCommonPbCBItems();
                    commonPbCB.getSelectionModel().select(selectedIndex);
                }
            }
        });
    }

    private void setUpPhysConst() {
        setUpPhysConstTextFields();
        setUpPhysConstData();
        setUpMolarMasses();
        setUpReferences();
        setUpPhysConstCovariancesAndCorrelations();
        setUpPhysConstEditableLabel();
    }

    private void setUpRefMat() {
        setUpRefMatTextFields();
        setUpRefMatData();
        setUpConcentrations();
        setUpRefMatCovariancesAndCorrelations();
        setUpRefMatDatesSelection();
        setUpRefMatEditableLabel();
    }

    private void setUpDatesCheckBoxVisibility() {
        boolean hasNonZero = false;
        ValueModel[] models = refMatModel.getValues();
        if (models != null) {
            for (int i = 0; !hasNonZero && i < models.length; i++) {
                hasNonZero = !(models[i].getValue().doubleValue() == 0.0);
            }
        }
        refMatReferenceDatesCheckbox.setVisible(!hasNonZero);
    }

    private void setUpDatesCheckboxVisibilityListener() {
        refMatIsEditableLabel.textProperty().addListener((val) -> {
            if (refMatIsEditableLabel.getText().equals("editing")) {
                setUpDatesCheckBoxVisibility();
            } else {
                refMatReferenceDatesCheckbox.setVisible(false);
            }
        });
    }

    private void setUpCommonPb() {
        setUpCommonPbTextFields();
        setUpCommonPbData();
        setUpCommonPbCovariancesAndCorrelations();
        setUpCommonPbEditableLabel();
    }

    private void setUpPhysConstCovariancesAndCorrelations() {
        physConstModel.initializeCorrelations();
        physConstModel.generateCovariancesFromCorrelations();
        setUpPhysConstCov();
        setUpPhysConstCorr();
    }

    private void setUpRefMatCovariancesAndCorrelations() {
        refMatModel.initializeCorrelations();
        refMatModel.generateCovariancesFromCorrelations();
        setUpRefMatCov();
        setUpRefMatCorr();
    }

    private void setUpCommonPbCovariancesAndCorrelations() {
        commonPbModel.initializeCorrelations();
        commonPbModel.generateCovariancesFromCorrelations();
        setUpCommonPbCov();
        setUpCommonPbCorr();
    }

    private void setUpPhysConstCB() {
        setUpPhysConstCBItems();
        physConstCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue val, Number ov, Number nv) {
                setPhysConstModel(nv.intValue());
            }
        });
        physConstCB.getSelectionModel().select(physConstModels.indexOf(squidLabData.getPhysConstDefault()));
    }

    private void setUpPhysConstCBItems() {
        final ObservableList<String> cbList = FXCollections.observableArrayList();
        for (ParametersModel mod : physConstModels) {
            if (mod.equals(squidLabData.getPhysConstDefault())) {
                cbList.add(mod.getModelNameWithVersion() + " - default");
            } else {
                cbList.add(mod.getModelNameWithVersion());
            }
        }
        physConstCB.setItems(cbList);
    }

    private void setPhysConstModel(int num) {
        if (num > -1 && num < physConstModels.size()) {
            physConstModel = physConstModels.get(num);
            setUpPhysConst();
            setUpPhysConstMenuItems(false, physConstModel.isEditable());
            physConstEditable(false);
        }
    }

    private void setUpRefMatCB() {
        setUpRefMatCBItems();
        refMatCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue val, Number ov, Number nv) {
                setRefMatModel(nv.intValue());
            }
        });
        refMatCB.getSelectionModel().select(refMatModels.indexOf(squidLabData.getRefMatDefault()));
    }

    private void setUpRefMatCBItems() {
        final ObservableList<String> cbList = FXCollections.observableArrayList();
        for (ParametersModel mod : refMatModels) {
            if (mod.equals(squidLabData.getRefMatDefault())) {
                cbList.add(mod.getModelNameWithVersion() + " - default");
            } else {
                cbList.add(mod.getModelNameWithVersion());
            }
        }
        refMatCB.setItems(cbList);
    }

    private void setRefMatModel(int num) {
        if (num > -1 && num < refMatModels.size()) {
            refMatModel = refMatModels.get(num);
            refMatReferenceDatesCheckbox.setSelected(((ReferenceMaterialModel) refMatModel).isReferenceDates());
            setUpRefMat();
            setUpRefMatMenuItems(false, refMatModel.isEditable());
            refMatEditable(false);
        }
    }

    private void setUpCommonPbCB() {
        setUpCommonPbCBItems();
        commonPbCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue val, Number ov, Number nv) {
                setCommonPbModel(nv.intValue());
            }
        });
        commonPbCB.getSelectionModel().select(commonPbModels.indexOf(squidLabData.getCommonPbDefault()));
    }

    private void setCommonPbModel(int num) {
        if (num > -1 && num < commonPbModels.size()) {
            commonPbModel = commonPbModels.get(num);
            setUpCommonPb();
            setUpCommonPbMenuItems(false, commonPbModel.isEditable());
            commonPbModelEditable(false);
        }
    }

    private void setUpCommonPbCBItems() {
        final ObservableList<String> cbList = FXCollections.observableArrayList();
        for (ParametersModel mod : commonPbModels) {
            if (mod.equals(squidLabData.getCommonPbDefault())) {
                cbList.add(mod.getModelNameWithVersion() + " - default");
            } else {
                cbList.add(mod.getModelNameWithVersion());
            }
        }
        commonPbCB.setItems(cbList);
    }

    private void setUpPhysConstCov() {
        initializeTableWithObList(physConstCovTable, getObListFromMatrix(physConstModel.getCovModel()),
                physConstCovNotation, physConstModel, physConstCovSigFigs.getValue());
    }

    private void setUpPhysConstCorr() {
        initializeTableWithObList(physConstCorrTable, getObListFromMatrix(physConstModel.getCorrModel()),
                physConstCorrNotation, physConstModel, physConstCorrSigFigs.getValue());
    }

    private void setUpRefMatCov() {
        initializeTableWithObList(refMatCovTable, getObListFromMatrix(refMatModel.getCovModel()),
                refMatCovNotation, refMatModel, refMatCovSigFigs.getValue());
    }

    private void setUpRefMatCorr() {
        initializeTableWithObList(refMatCorrTable, getObListFromMatrix(refMatModel.getCorrModel()),
                refMatCorrNotation, refMatModel, refMatCorrSigFigs.getValue());
    }

    private void setUpCommonPbCov() {
        initializeTableWithObList(commonPbCovTable, getObListFromMatrix(commonPbModel.getCovModel()),
                commonPbCovNotation, commonPbModel, commonPbCovSigFigs.getValue());
    }

    private void setUpCommonPbCorr() {
        initializeTableWithObList(commonPbCorrTable, getObListFromMatrix(commonPbModel.getCorrModel()),
                commonPbCorrNotation, commonPbModel, commonPbCorrSigFigs.getValue());
    }

    private static ObservableList<ObservableList<SimpleStringProperty>> getObListFromMatrix(AbstractMatrixModel matrix) {
        ObservableList<ObservableList<SimpleStringProperty>> obList = FXCollections.observableArrayList();
        if (matrix != null && matrix.getMatrix() != null) {
            Iterator<String> colIterator = matrix.getRows().values().iterator();
            ObservableList<SimpleStringProperty> colList = FXCollections.observableArrayList();
            colList.add(new SimpleStringProperty("names ↓→"));
            while (colIterator.hasNext()) {
                colList.add(new SimpleStringProperty(colIterator.next()));
            }
            obList.add(colList);

            double[][] matrixArray = matrix.getMatrix().getArray();
            Iterator<String> rowIterator = matrix.getRows().values().iterator();
            for (int i = 0; i < matrixArray.length; i++) {
                ObservableList<SimpleStringProperty> row = FXCollections.observableArrayList();
                row.add(new SimpleStringProperty(rowIterator.next()));
                for (int j = 0; j < matrixArray[0].length; j++) {
                    row.add(new SimpleStringProperty(Double.toString(matrixArray[i][j])));
                }
                obList.add(row);
            }
        }
        return obList;
    }

    private void initializeTableWithObList(TableView<ObservableList<SimpleStringProperty>> table,
                                           ObservableList<ObservableList<SimpleStringProperty>> obList, DecimalFormat format,
                                           ParametersModel model, int precision) {
        if (obList.size() > 0) {
            List<TableColumn<ObservableList<SimpleStringProperty>, String>> columns = new ArrayList<>();
            ObservableList<SimpleStringProperty> cols = obList.remove(0);
            table.getColumns().clear();
            TableColumn<ObservableList<SimpleStringProperty>, String> rowHead
                    = new TableColumn<>(getRatioVisibleName(cols.get(0).get()));
            final int rowHeadNum = 0;
            rowHead.setSortable(false);
            rowHead.setEditable(false);
            rowHead.setCellFactory(column -> EditCell.createStringEditCell());
            rowHead.setCellValueFactory(param
                    -> new ReadOnlyObjectWrapper<String>(getRatioVisibleName(param.getValue().get(rowHeadNum).get())));
            columns.add(rowHead);
            for (int i = 1; i < cols.size(); i++) {
                TableColumn<ObservableList<SimpleStringProperty>, String> col
                        = new TableColumn<>(getRatioVisibleName(cols.get(i).get()));
                final int colNum = i;
                col.setSortable(false);
                col.setCellValueFactory(param
                        -> new ReadOnlyObjectWrapper<String>(format.format(round(new BigDecimal(param.getValue().get(colNum).get()), precision))));

                if (table.equals(physConstCorrTable) || table.equals(refMatCorrTable) || table.equals(commonPbCorrTable)) {
                    col.setCellFactory(column -> EditCell.createStringEditCell());
                    col.setOnEditCommit(value -> {
                        String newValue = correctColumnCommittedValue(value.getNewValue());
                        if (Double.parseDouble(newValue) <= 1
                                && Double.parseDouble(newValue) >= -1
                                && value.getTablePosition().getColumn() != value.getTablePosition().getRow() + 1) {
                            String colRatio = getRatioHiddenName(value.getTableColumn().getText());
                            String rowRatio = getRatioHiddenName(value.getRowValue().get(0).get());
                            String key = "rho" + colRatio.substring(0, 1).toUpperCase() + colRatio.substring(1) + "__" + rowRatio;
                            String reverseKey = "rho" + rowRatio.substring(0, 1).toUpperCase() + rowRatio.substring(1) + "__" + colRatio;
                            model.getRhos().remove(key);
                            model.getRhos().remove(reverseKey);
                            if (Double.parseDouble(newValue) != 0) {
                                model.getRhos().put(key, new BigDecimal(newValue));
                            }
                            model.initializeCorrelations();
                            model.generateCovariancesFromCorrelations();
                            if (table.equals(physConstCorrTable)) {
                                setUpPhysConstCorr();
                                setUpPhysConstCov();
                            } else if (table.equals(refMatCorrTable)) {
                                setUpRefMatCorr();
                                setUpRefMatCov();
                            } else if (table.equals(commonPbCorrTable)) {
                                setUpCommonPbCorr();
                                setUpCommonPbCov();
                            }
                            table.refresh();
                        } else {
                            table.refresh();
                        }
                    });
                }
                columns.add(col);
            }
            table.getColumns().addAll(columns);
            table.setItems(obList);
            table.refresh();
        }
    }

    private void setUpPhysConstData() {
        int precision = physConstDataSigFigs.getValue();
        physConstDataTable.getColumns().setAll(getDataModelColumns(physConstDataTable, physConstDataNotation, precision));
        physConstDataTable.setItems(getDataModelObList(physConstModel.getValues(), physConstDataNotation, precision));
        physConstDataTable.refresh();
    }

    private void setUpCommonPbData() {
        int precision = commonPbDataSigFigs.getValue();
        commonPbDataTable.getColumns().setAll(getDataModelColumns(commonPbDataTable, commonPbDataNotation, precision));
        commonPbDataTable.setItems(getDataModelObList(commonPbModel.getValues(), commonPbDataNotation, precision));
        commonPbDataTable.refresh();
    }

    private void setUpRefDates() {
        int precision = refDatesSigFigSpinner.getValue();
        refDatesTable.getColumns().setAll(getDataModelColumns(refDatesTable, refDatesNotation, precision));
        refDatesTable.setItems(getDataRefDatesModelObList());
    }

    private void setUpRefMatData() {
        setUpRefMatDataModelColumns();
        int precision = refMatDataSigFigs.getValue();
        final ObservableList<RefMatDataModel> obList = FXCollections.observableArrayList();
        ValueModel[] values = refMatModel.getValues();
        for (int i = 0; i < values.length; i++) {
            ValueModel valMod = values[i];
            Boolean isMeasured = ((ReferenceMaterialModel) refMatModel).getDataMeasured()[i];
            String value = refMatDataNotation.format(round(valMod.getValue(), precision));
            String oneSigmaABS = refMatDataNotation.format(round(valMod.getOneSigmaABS(), precision));
            String oneSigmaPCT = refMatDataNotation.format(round(valMod.getOneSigmaPCT(), precision));
            RefMatDataModel mod = new RefMatDataModel(getRatioVisibleName(valMod.getName()), value,
                    oneSigmaABS, oneSigmaPCT, isMeasured);
            obList.add(mod);
        }
        refMatDataTable.setItems(obList);
        refMatDataTable.refresh();
    }

    private void setUpConcentrations() {
        int precision = refMatConcSigFigs.getValue();
        refMatConcentrationsTable.getColumns().setAll(getDataModelColumns(refMatConcentrationsTable, refMatConcentrationsNotation, precision));
        refMatConcentrationsTable.setItems(getDataModelObList(((ReferenceMaterialModel) refMatModel).getConcentrations(), refMatConcentrationsNotation, precision));
        refMatConcentrationsTable.refresh();
    }

    private List<TableColumn<DataModel, String>> getDataModelColumns(TableView<DataModel> table, DecimalFormat format, int precision) {
        List<TableColumn<DataModel, String>> columns = new ArrayList<>();

        TableColumn<DataModel, String> nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(new PropertyValueFactory<DataModel, String>("name"));
        nameCol.setSortable(false);
        nameCol.setEditable(false);
        nameCol.setCellFactory(column -> EditCell.createStringEditCell());
        columns.add(nameCol);

        TableColumn<DataModel, String> valCol = new TableColumn<>("value");
        valCol.setCellValueFactory(new PropertyValueFactory<DataModel, String>("value"));
        valCol.setSortable(false);
        valCol.setCellFactory(column -> EditCell.createStringEditCell());
        valCol.setOnEditCommit(value -> {
            String newValue = correctColumnCommittedValue(value.getNewValue());
            ObservableList<DataModel> items = value.getTableView().getItems();
            DataModel mod = items.get(value.getTablePosition().getRow());
            String ratioName = getRatioHiddenName(mod.getName());
            ValueModel valMod = new ValueModel(ratioName);
            if (table.equals(physConstDataTable)) {
                valMod = physConstModel.getDatumByName(ratioName);
            } else if (table.equals(refMatConcentrationsTable)) {
                valMod = ((ReferenceMaterialModel) refMatModel).getConcentrationByName(ratioName);
            } else if (table.equals(refDatesTable)) {
                valMod = ((ReferenceMaterialModel) refMatModel).getDateByName(ratioName);
            } else if (table.equals(commonPbDataTable)) {
                valMod = commonPbModel.getDatumByName(ratioName);
            }
            BigDecimal newBigDec = BigDecimal.ZERO;
            if (Double.parseDouble(newValue) != 0) {
                newBigDec = new BigDecimal(newValue);
            }
            if (table.equals(refDatesTable)) {
                BigDecimal operand = new BigDecimal(getRefDatesDivisorOrMultiplier());
                valMod.setValue(newBigDec.multiply(operand));
                mod.setValue(format.format(round(valMod.getValue(), precision).divide(operand)));
                mod.setOneSigmaABS(format.format(round(valMod.getOneSigmaABS(), precision).divide(operand)));
                mod.setOneSigmaPCT(format.format(round(valMod.getOneSigmaPCT(), precision).divide(operand)));
                value.getTableView().refresh();
            } else {
                valMod.setValue(newBigDec);
                mod.setValue(format.format(round(valMod.getValue(), precision)));
                mod.setOneSigmaABS(format.format(round(valMod.getOneSigmaABS(), precision)));
                mod.setOneSigmaPCT(format.format(round(valMod.getOneSigmaPCT(), precision)));
                value.getTableView().refresh();

                if (table.equals(physConstDataTable)) {
                    setUpPhysConstCovariancesAndCorrelations();
                } else if (table.equals(commonPbDataTable)) {
                    setUpCommonPbCovariancesAndCorrelations();
                }
            }

            table.getColumns().setAll(getDataModelColumns(table, format, precision));
        });
        columns.add(valCol);

        TableColumn<DataModel, String> absCol = new TableColumn<>("1σ ABS");
        absCol.setCellValueFactory(new PropertyValueFactory<DataModel, String>("oneSigmaABS"));
        absCol.setSortable(false);
        absCol.setCellFactory(column -> EditCell.createStringEditCell());
        absCol.setOnEditCommit(value -> {
            String newValue = correctColumnCommittedValue(value.getNewValue());
            ObservableList<DataModel> items = value.getTableView().getItems();
            DataModel mod = items.get(value.getTablePosition().getRow());
            String ratioName = getRatioHiddenName(mod.getName());
            ValueModel valMod = new ValueModel(ratioName);
            if (table.equals(physConstDataTable)) {
                valMod = physConstModel.getDatumByName(ratioName);
            } else if (table.equals(refMatConcentrationsTable)) {
                valMod = ((ReferenceMaterialModel) refMatModel).getConcentrationByName(ratioName);
            } else if (table.equals(refDatesTable)) {
                valMod = ((ReferenceMaterialModel) refMatModel).getDateByName(ratioName);
            } else if (table.equals(commonPbDataTable)) {
                valMod = commonPbModel.getDatumByName(ratioName);
            }
            BigDecimal newBigDec = BigDecimal.ZERO;
            if (Double.parseDouble(newValue) != 0) {
                newBigDec = new BigDecimal(newValue);
            }
            if (table.equals(refDatesTable)) {
                BigDecimal operand = new BigDecimal(getRefDatesDivisorOrMultiplier());
                valMod.setOneSigma(newBigDec.multiply(operand));
                valMod.setUncertaintyType("ABS");
                mod.setValue(format.format(round(valMod.getValue(), precision).divide(operand)));
                mod.setOneSigmaABS(format.format(round(valMod.getOneSigmaABS(), precision).divide(operand)));
                mod.setOneSigmaPCT(format.format(round(valMod.getOneSigmaPCT(), precision).divide(operand)));
                value.getTableView().refresh();
            } else {
                valMod.setOneSigma(newBigDec);
                valMod.setUncertaintyType("ABS");
                mod.setValue(format.format(round(valMod.getValue(), precision)));
                mod.setOneSigmaABS(format.format(round(valMod.getOneSigmaABS(), precision)));
                mod.setOneSigmaPCT(format.format(round(valMod.getOneSigmaPCT(), precision)));
                value.getTableView().refresh();
                if (table.equals(physConstDataTable)) {
                    setUpPhysConstCovariancesAndCorrelations();
                } else if (table.equals(commonPbDataTable)) {
                    setUpCommonPbCovariancesAndCorrelations();
                }
            }
            table.getColumns().setAll(getDataModelColumns(table, format, precision));
        });
        columns.add(absCol);

        TableColumn<DataModel, String> pctCol = new TableColumn<>("1σ PCT");
        pctCol.setCellValueFactory(new PropertyValueFactory<DataModel, String>("oneSigmaPCT"));
        pctCol.setSortable(false);
        pctCol.setCellFactory(column -> EditCell.createStringEditCell());
        pctCol.setOnEditCommit(value -> {
            String newValue = correctColumnCommittedValue(value.getNewValue());
            ObservableList<DataModel> items = value.getTableView().getItems();
            DataModel mod = items.get(value.getTablePosition().getRow());
            String ratioName = getRatioHiddenName(mod.getName());
            ValueModel valMod = new ValueModel(ratioName);
            if (table.equals(physConstDataTable)) {
                valMod = physConstModel.getDatumByName(ratioName);
            } else if (table.equals(refMatConcentrationsTable)) {
                valMod = ((ReferenceMaterialModel) refMatModel).getConcentrationByName(ratioName);
            } else if (table.equals(refDatesTable)) {
                valMod = ((ReferenceMaterialModel) refMatModel).getDateByName(ratioName);
            } else if (table.equals(commonPbDataTable)) {
                valMod = commonPbModel.getDatumByName(ratioName);
            }
            BigDecimal newBigDec = BigDecimal.ZERO;
            if (Double.parseDouble(newValue) != 0) {
                newBigDec = new BigDecimal(newValue);
            }
            if (table.equals(refDatesTable)) {
                BigDecimal operand = new BigDecimal(getRefDatesDivisorOrMultiplier());
                valMod.setOneSigma(newBigDec.multiply(operand));
                valMod.setUncertaintyType("PCT");
                mod.setValue(format.format(round(valMod.getValue(), precision).divide(operand)));
                mod.setOneSigmaABS(format.format(round(valMod.getOneSigmaABS(), precision).divide(operand)));
                mod.setOneSigmaPCT(format.format(round(valMod.getOneSigmaPCT(), precision).divide(operand)));
                value.getTableView().refresh();
            } else {
                valMod.setOneSigma(newBigDec);
                valMod.setUncertaintyType("PCT");
                mod.setValue(format.format(round(valMod.getValue(), precision)));
                mod.setOneSigmaABS(format.format(round(valMod.getOneSigmaABS(), precision)));
                mod.setOneSigmaPCT(format.format(round(valMod.getOneSigmaPCT(), precision)));
                value.getTableView().refresh();
                if (table.equals(physConstDataTable)) {
                    setUpPhysConstCovariancesAndCorrelations();
                } else if (table.equals(commonPbDataTable)) {
                    setUpCommonPbCovariancesAndCorrelations();
                }
            }

            table.getColumns().setAll(getDataModelColumns(table, format, precision));
        });
        columns.add(pctCol);

        return columns;
    }

    private String correctColumnCommittedValue(String value) {
        String newValue = value.replaceAll("e", "E");
        if (newValue.endsWith("E") || newValue.isEmpty()) {
            newValue = newValue + "0";
        }
        if (newValue.startsWith("E") || newValue.startsWith(".")) {
            newValue = "0" + newValue;
        }
        if (newValue.startsWith("-")) {
            newValue = "-0" + newValue.substring(1);
        }
        if (newValue.endsWith("-")) {
            newValue = newValue + "0";
        }
        return newValue;
    }

    private void setUpRefMatDataModelColumns() {
        refMatDataTable.getColumns().clear();
        int precision = refMatDataSigFigs.getValue();

        TableColumn<RefMatDataModel, String> nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(new PropertyValueFactory<RefMatDataModel, String>("name"));
        nameCol.setSortable(false);
        nameCol.setCellFactory(column -> EditCell.createStringEditCell());
        nameCol.setEditable(false);
        refMatDataTable.getColumns().add(nameCol);

        TableColumn<RefMatDataModel, String> valCol = new TableColumn<>("value");
        valCol.setCellValueFactory(new PropertyValueFactory<RefMatDataModel, String>("value"));
        valCol.setSortable(false);
        valCol.setCellFactory(column -> EditCell.createStringEditCell());
        valCol.setOnEditCommit(value -> {
            String newValue = correctColumnCommittedValue(value.getNewValue());
            ObservableList<RefMatDataModel> items = value.getTableView().getItems();
            DataModel mod = items.get(value.getTablePosition().getRow());
            String ratioName = getRatioHiddenName(mod.getName());
            ValueModel valMod = refMatModel.getDatumByName(ratioName);
            BigDecimal newBigDec = BigDecimal.ZERO;
            if (Double.parseDouble(newValue) != 0) {
                newBigDec = new BigDecimal(newValue);
            }
            valMod.setValue(newBigDec);
            mod.setValue(refMatDataNotation.format(round(valMod.getValue(), precision)));
            mod.setOneSigmaABS(refMatDataNotation.format(round(valMod.getOneSigmaABS(), precision)));
            mod.setOneSigmaPCT(refMatDataNotation.format(round(valMod.getOneSigmaPCT(), precision)));
            value.getTableView().refresh();
            setUpRefMatCovariancesAndCorrelations();
            setUpDatesCheckBoxVisibility();

            setUpRefMatDataModelColumns();
        });
        refMatDataTable.getColumns().add(valCol);

        TableColumn<RefMatDataModel, String> absCol = new TableColumn<>("1σ ABS");
        absCol.setCellValueFactory(new PropertyValueFactory<RefMatDataModel, String>("oneSigmaABS"));
        absCol.setSortable(false);
        absCol.setCellFactory(column -> EditCell.createStringEditCell());
        absCol.setOnEditCommit(value -> {
            String newValue = correctColumnCommittedValue(value.getNewValue());
            ObservableList<RefMatDataModel> items = value.getTableView().getItems();
            DataModel mod = items.get(value.getTablePosition().getRow());
            String ratioName = getRatioHiddenName(mod.getName());
            ValueModel valMod = refMatModel.getDatumByName(ratioName);
            BigDecimal newBigDec = BigDecimal.ZERO;
            if (Double.parseDouble(newValue) != 0) {
                newBigDec = new BigDecimal(newValue);
            }
            valMod.setOneSigma(newBigDec);
            valMod.setUncertaintyType("ABS");
            mod.setValue(refMatDataNotation.format(round(valMod.getValue(), precision)));
            mod.setOneSigmaABS(refMatDataNotation.format(round(valMod.getOneSigmaABS(), precision)));
            mod.setOneSigmaPCT(refMatDataNotation.format(round(valMod.getOneSigmaPCT(), precision)));
            value.getTableView().refresh();
            setUpRefMatCovariancesAndCorrelations();

            setUpRefMatDataModelColumns();
        });
        refMatDataTable.getColumns().add(absCol);

        TableColumn<RefMatDataModel, String> pctCol = new TableColumn<>("1σ PCT");
        pctCol.setCellValueFactory(new PropertyValueFactory<RefMatDataModel, String>("oneSigmaPCT"));
        pctCol.setSortable(false);
        pctCol.setCellFactory(column -> EditCell.createStringEditCell());
        pctCol.setOnEditCommit(value -> {
            String newValue = correctColumnCommittedValue(value.getNewValue());
            ObservableList<RefMatDataModel> items = value.getTableView().getItems();
            DataModel mod = items.get(value.getTablePosition().getRow());
            String ratioName = getRatioHiddenName(mod.getName());
            ValueModel valMod = refMatModel.getDatumByName(ratioName);
            BigDecimal newBigDec = BigDecimal.ZERO;
            if (Double.parseDouble(newValue) != 0) {
                newBigDec = new BigDecimal(newValue);
            }
            valMod.setOneSigma(newBigDec);
            valMod.setUncertaintyType("PCT");
            mod.setValue(refMatDataNotation.format(round(valMod.getValue(), precision)));
            mod.setOneSigmaABS(refMatDataNotation.format(round(valMod.getOneSigmaABS(), precision)));
            mod.setOneSigmaPCT(refMatDataNotation.format(round(valMod.getOneSigmaPCT(), precision)));
            value.getTableView().refresh();
            setUpRefMatCovariancesAndCorrelations();

            setUpRefMatDataModelColumns();
        });
        refMatDataTable.getColumns().add(pctCol);

        TableColumn<RefMatDataModel, ChoiceBox> measuredCol = new TableColumn<>("measured");
        measuredCol.setCellValueFactory(new PropertyValueFactory<RefMatDataModel, ChoiceBox>("isMeasured"));
        measuredCol.setSortable(false);
        refMatDataTable.getColumns().add(measuredCol);
    }

    private ObservableList<DataModel> getDataModelObList(ValueModel[] values, DecimalFormat numberFormat, int precision) {
        final ObservableList<DataModel> obList = FXCollections.observableArrayList();
        for (int i = 0; i < values.length; i++) {
            ValueModel valMod = values[i];
            String value = numberFormat.format(round(valMod.getValue(), precision));
            String oneSigmaABS = numberFormat.format(round(valMod.getOneSigmaABS(), precision));
            String oneSigmaPCT = numberFormat.format(round(valMod.getOneSigmaPCT(), precision));
            DataModel mod = new DataModel(getRatioVisibleName(valMod.getName()), value,
                    oneSigmaABS, oneSigmaPCT);
            obList.add(mod);
        }
        return obList;
    }

    private ObservableList<DataModel> getDataRefDatesModelObList() {
        ValueModel[] values = ((ReferenceMaterialModel) refMatModel).getDates();
        int precision = refDatesSigFigSpinner.getValue();
        BigDecimal divisor = new BigDecimal(getRefDatesDivisorOrMultiplier());
        final ObservableList<DataModel> obList = FXCollections.observableArrayList();
        for (int i = 0; i < values.length; i++) {
            ValueModel valMod = values[i];
            String value = refDatesNotation.format(round(valMod.getValue(), precision).divide(divisor));
            String oneSigmaABS = refDatesNotation.format(round(valMod.getOneSigmaABS(), precision).divide(divisor));
            String oneSigmaPCT = refDatesNotation.format(round(valMod.getOneSigmaPCT(), precision).divide(divisor));
            DataModel mod = new DataModel(getRatioVisibleName(valMod.getName()), value,
                    oneSigmaABS, oneSigmaPCT);
            obList.add(mod);
        }
        return obList;
    }

    private void setUpMolarMasses() {
        molarMassesPane.getChildren().clear();
        molarMasses = new ArrayList<>();
        Map<String, BigDecimal> masses = ((PhysicalConstantsModel) physConstModel).getMolarMasses();
        String[][] defaultMasses = DataDictionary.AtomicMolarMasses;
        int currY = 10;
        for (String[] mass : defaultMasses) {
            Label lab = new Label(mass[0] + ":");

            TextField text = new TextField(masses.get(mass[0]).toPlainString());
            molarMasses.add(text);
            text.setPrefWidth(300);
            text.focusedProperty().addListener((obV, ov, nv) -> {
                if (!nv && !isNumeric(text.getText())) {
                    SquidMessageDialog.showWarningDialog("Invalid Molar Mass: must be numeric", squidLabDataWindow);
                    text.setText(masses.get(mass[0]).toPlainString());
                }
            });

            HBox hbox = new HBox();
            hbox.setSpacing(5);
            AnchorPane.setLeftAnchor(hbox, 0.0);
            AnchorPane.setRightAnchor(hbox, 0.0);
            hbox.setAlignment(Pos.CENTER);
            hbox.getChildren().add(lab);
            hbox.getChildren().add(text);
            hbox.setLayoutY(currY);
            molarMassesPane.getChildren().add(hbox);

            currY += 30;
        }
    }

    private void setUpDates() {
        if (refMatReferenceDatesCheckbox.isSelected()) {
            setUpRefDates();
        } else {
            setUpApparentDates();
        }
    }

    private void setUpApparentDates() {
        apparentDatesTextArea.setText(((ReferenceMaterialModel) refMatModel).listFormattedApparentDates());
    }

    private void setUpReferences() {
        referencesPane.getChildren().clear();
        ValueModel[] models = physConstModel.getValues();
        physConstReferences = new ArrayList<>();
        AnchorPane.setRightAnchor(referencesPane, 0.0);
        AnchorPane.setBottomAnchor(referencesPane, 0.0);
        int currHeight = 10;
        for (int i = 0; i < models.length; i++) {
            ValueModel mod = models[i];

            Label lab = new Label(getRatioVisibleName(mod.getName()) + ":");
            TextField text = new TextField(mod.getReference());
            physConstReferences.add(text);
            text.setPrefWidth(400);

            HBox hbox = new HBox();
            hbox.setSpacing(5);
            AnchorPane.setLeftAnchor(hbox, 0.0);
            AnchorPane.setRightAnchor(hbox, 0.0);
            hbox.setAlignment(Pos.CENTER);
            hbox.getChildren().add(lab);
            hbox.getChildren().add(text);
            hbox.setLayoutY(currHeight);

            referencesPane.getChildren().add(hbox);
            currHeight += 30;
        }
    }

    private void setUpPhysConstEditableLabel() {
        setUpIsEditableLabel(physConstIsEditableLabel, physConstModel.isEditable());
    }

    private void setUpRefMatEditableLabel() {
        setUpIsEditableLabel(refMatIsEditableLabel, refMatModel.isEditable());
    }

    private void setUpCommonPbEditableLabel() {
        setUpIsEditableLabel(commonPbIsEditableLabel, commonPbModel.isEditable());
    }

    private void setUpIsEditableLabel(Label lab, boolean isEditable) {
        if (isEditable) {
            lab.setText("editable");
        } else {
            lab.setText("not editable");
        }
    }

    private void setUpPhysConstTextFields() {
        setUpTextFields(physConstModelName, physConstModel.getModelName(), physConstLabName, physConstModel.getLabName(),
                physConstVersion, physConstModel.getVersion(), physConstDateCertified, physConstModel.getDateCertified(),
                physConstCommentsArea, physConstModel.getComments(), physConstReferencesArea, physConstModel.getReferences());
    }

    private void setUpRefMatTextFields() {
        setUpTextFields(refMatModelName, refMatModel.getModelName(), refMatLabName, refMatModel.getLabName(), refMatVersion,
                refMatModel.getVersion(), refMatDateCertified, refMatModel.getDateCertified(), refMatCommentsArea,
                refMatModel.getComments(), refMatReferencesArea, refMatModel.getReferences());
    }

    private void setUpCommonPbTextFields() {
        setUpTextFields(commonPbModelName, commonPbModel.getModelName(), commonPbLabName, commonPbModel.getLabName(),
                commonPbVersion, commonPbModel.getVersion(), commonPbDateCertified, commonPbModel.getDateCertified(),
                commonPbCommentsArea, commonPbModel.getComments(), commonPbReferencesArea, commonPbModel.getReferences());
    }

    private void setUpTextFields(TextField model, String modelName, TextField lab, String labName, TextField ver, String version,
                                 TextField date, String dateCertified, TextArea com, String comments, TextArea ref, String references) {
        model.setText(modelName);
        lab.setText(labName);
        ver.setText(version);
        date.setText(dateCertified);
        com.setText(comments);
        ref.setText(references);
    }

    private void setUpLaboratoryName() {
        labNameTextField.setText(squidLabData.getLaboratoryName());
        labNameTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue && !squidLabData.getLaboratoryName().equals(labNameTextField.getText())) {
                    squidLabData.setLaboratoryName(labNameTextField.getText());
                    squidLabData.storeState();
                }
            }
        });
    }

    private static String getRatioVisibleName(String ratio) {
        String retVal = ratio.replaceAll("lambda", "λ");

        retVal = retVal.replaceAll("r206_207r", "206-Pb/207-Pb");
        retVal = retVal.replaceAll("r206_208r", "206-Pb/208-Pb");
        retVal = retVal.replaceAll("r206_238r", "206-Pb/238-U");
        retVal = retVal.replaceAll("r208_232r", "208-Pb/232-Th");
        retVal = retVal.replaceAll("r238_235s", "238-U/235-U");

        retVal = retVal.replaceAll("r206_204b", "206-Pb/204-Pb");
        retVal = retVal.replaceAll("r207_204b", "207-Pb/204-Pb");
        retVal = retVal.replaceAll("r207_206b", "207-Pb/206-Pb");
        retVal = retVal.replaceAll("r208_204b", "208-Pb/204-Pb");
        retVal = retVal.replaceAll("r208_206b", "208-Pb/206-Pb");

        return retVal;
    }

    private static String getRatioHiddenName(String ratio) {
        String retVal = ratio.replaceAll("λ", "lambda");

        retVal = retVal.replaceAll("206-Pb/207-Pb", "r206_207r");
        retVal = retVal.replaceAll("206-Pb/208-Pb", "r206_208r");
        retVal = retVal.replaceAll("206-Pb/238-U", "r206_238r");
        retVal = retVal.replaceAll("208-Pb/232-Th", "r208_232r");
        retVal = retVal.replaceAll("238-U/235-U", "r238_235s");

        retVal = retVal.replaceAll("206-Pb/204-Pb", "r206_204b");
        retVal = retVal.replaceAll("207-Pb/204-Pb", "r207_204b");
        retVal = retVal.replaceAll("207-Pb/206-Pb", "r207_206b");
        retVal = retVal.replaceAll("208-Pb/204-Pb", "r208_204b");
        retVal = retVal.replaceAll("208-Pb/206-Pb", "r208_206b");

        return retVal;
    }

    @FXML
    private void physConstImpXMLAction(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.parametersManagerSelectPhysicalConstantsXMLFile(squidLabDataWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), squidLabDataWindow);
        }
        if (file != null) {
            try {
                PhysicalConstantsModel importedMod = (PhysicalConstantsModel) physConstModel.readXMLObject(file.getAbsolutePath(), false);
                if (physConstModels.contains(importedMod)) {

                    ButtonType renameButton = new ButtonType("Rename");
                    ButtonType changeVersionButton = new ButtonType("Change Version");
                    ButtonType cancelButton = new ButtonType("Cancel");
                    ButtonType overwriteButton = new ButtonType("Overwrite");
                    Alert alert;
                    if (physConstModels.get(physConstModels.indexOf(importedMod)).isEditable()) {
                        alert = new Alert(Alert.AlertType.WARNING, "A Physical Constants Model with the same name and version exists."
                                + "What would you like to do?", overwriteButton, renameButton, changeVersionButton, cancelButton);
                    } else {
                        alert = new Alert(Alert.AlertType.WARNING, "A Physical Constants Model with the same name and version exists."
                                + "What would you like to do?", renameButton, changeVersionButton, cancelButton);
                    }
                    alert.initStyle(StageStyle.UNDECORATED);
                    alert.initOwner(squidLabDataWindow);
                    alert.setX(squidLabDataStage.getX() + (squidLabDataStage.getWidth() - alert.getWidth()) / 2);
                    alert.setY(squidLabDataStage.getY() + (squidLabDataStage.getHeight() - alert.getHeight()) / 2);
                    alert.showAndWait().ifPresent(p -> {
                        if (p.equals(renameButton)) {
                            TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle("Rename");
                            dialog.setHeaderText("Rename " + importedMod.getModelName());
                            dialog.setContentText("Enter the new name:");
                            Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
                            TextField newName = null;
                            for (Node n : dialog.getDialogPane().getChildren()) {
                                if (n instanceof TextField) {
                                    newName = (TextField) n;
                                }
                            }
                            if (okBtn != null && newName != null) {
                                newName.textProperty().addListener((observable, oldValue, newValue) -> {
                                    importedMod.setModelName(newValue);
                                    okBtn.setDisable(physConstModels.contains(importedMod) || newValue.isEmpty());
                                });
                            }
                            dialog.initStyle(StageStyle.UNDECORATED);
                            dialog.initOwner(squidLabDataStage.getScene().getWindow());
                            dialog.setX(squidLabDataStage.getX() + (squidLabDataStage.getWidth() - 200) / 2);
                            dialog.setY(squidLabDataStage.getY() + (squidLabDataStage.getHeight() - 150) / 2);
                            dialog.showAndWait().ifPresent(d -> {
                                importedMod.setModelName(dialog.getResult());
                                if (!physConstModels.contains(importedMod)) {
                                    importedMod.setIsEditable(true);
                                    physConstModels.add(importedMod);
                                    physConstCB.getItems().add(importedMod.getModelNameWithVersion());
                                    physConstCB.getSelectionModel().selectLast();
                                    physConstModel = importedMod;
                                    setUpPhysConst();
                                    squidLabData.storeState();
                                } else {
                                    SquidMessageDialog.showWarningDialog("Invalid new name, model not imported", squidLabDataStage);
                                }
                            });
                        } else if (p.equals(changeVersionButton)) {
                            TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle("Change Version");
                            dialog.setHeaderText("Change Version " + importedMod.getModelName());
                            dialog.setContentText("Enter the new version:");
                            Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
                            TextField newName = null;
                            for (Node n : dialog.getDialogPane().getChildren()) {
                                if (n instanceof TextField) {
                                    newName = (TextField) n;
                                }
                            }
                            if (okBtn != null && newName != null) {
                                newName.textProperty().addListener((observable, oldValue, newValue) -> {
                                    importedMod.setModelName(newValue);
                                    okBtn.setDisable(physConstModels.contains(importedMod) || newValue.isEmpty());
                                });
                            }
                            dialog.initStyle(StageStyle.UNDECORATED);
                            dialog.initOwner(squidLabDataStage.getScene().getWindow());
                            dialog.setX(squidLabDataStage.getX() + (squidLabDataStage.getWidth() - 200) / 2);
                            dialog.setY(squidLabDataStage.getY() + (squidLabDataStage.getHeight() - 150) / 2);
                            dialog.showAndWait().ifPresent(d -> {
                                importedMod.setVersion(dialog.getResult());
                                if (!physConstModels.contains(importedMod)) {
                                    importedMod.setIsEditable(true);
                                    physConstModels.add(importedMod);
                                    physConstCB.getItems().add(importedMod.getModelNameWithVersion());
                                    physConstCB.getSelectionModel().selectLast();
                                    physConstModel = importedMod;
                                    setUpPhysConst();
                                    squidLabData.storeState();
                                } else {
                                    SquidMessageDialog.showWarningDialog("Invalid new version, model not imported", squidLabDataStage);
                                }
                            });
                        } else if (p.equals(overwriteButton)) {
                            physConstModels.remove(importedMod);
                            importedMod.setIsEditable(true);
                            physConstModels.add(importedMod);
                            physConstCB.getItems().add(importedMod.getModelNameWithVersion());
                            physConstCB.getSelectionModel().selectLast();
                            physConstModel = importedMod;
                            setUpPhysConst();
                            squidLabData.storeState();
                        }
                    });

                } else {
                    importedMod.setIsEditable(true);
                    physConstModels.add(importedMod);
                    physConstCB.getItems().add(importedMod.getModelNameWithVersion());
                    physConstCB.getSelectionModel().selectLast();
                    physConstModel = importedMod;
                    setUpPhysConst();
                    squidLabData.storeState();
                }
            } catch (Exception e) {
                SquidMessageDialog.showWarningDialog("An error occurred: \n", squidLabDataWindow);
            }
        }
        chosenTab = ParametersTab.physConst;
        squidLabDataStage.requestFocus();
    }

    @FXML
    private void physConstExpXMLAction(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.parametersManagerSavePhysicalConstantsXMLFile(physConstModel, squidLabDataWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), squidLabDataWindow);
        }
        if (file != null) {
            physConstModel.serializeXMLObject(file.getAbsolutePath());
        }
        chosenTab = ParametersTab.physConst;
        squidLabDataStage.requestFocus();
    }

    @FXML
    private void refMatExpXMLAction(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.parametersManagerSaveReferenceMaterialXMLFile(refMatModel, squidLabDataWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), squidLabDataWindow);
        }
        if (file != null) {
            refMatModel.serializeXMLObject(file.getAbsolutePath());
        }
        chosenTab = ParametersTab.refMat;
        squidLabDataStage.requestFocus();
    }

    @FXML
    private void refMatImpXMLAction(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.parametersManagerSelectReferenceMaterialXMLFile(squidLabDataWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), squidLabDataWindow);
        }
        if (file != null) {
            try {
                ReferenceMaterialModel importedMod = (ReferenceMaterialModel) refMatModel.readXMLObject(file.getAbsolutePath(), false);
                if (refMatModels.contains(importedMod)) {

                    ButtonType renameButton = new ButtonType("Rename");
                    ButtonType changeVersionButton = new ButtonType("Change Version");
                    ButtonType cancelButton = new ButtonType("Cancel");
                    ButtonType overwriteButton = new ButtonType("Overwrite");
                    Alert alert;
                    if (refMatModels.get(refMatModels.indexOf(importedMod)).isEditable()) {
                        alert = new Alert(Alert.AlertType.WARNING, "A Reference Material Model with the same name and version exists."
                                + "What would you like to do?", overwriteButton, renameButton, changeVersionButton, cancelButton);
                    } else {
                        alert = new Alert(Alert.AlertType.WARNING, "A Reference Material Model with the same name and version exists."
                                + "What would you like to do?", renameButton, changeVersionButton, cancelButton);
                    }
                    alert.initStyle(StageStyle.UNDECORATED);
                    alert.initOwner(squidLabDataStage.getScene().getWindow());
                    alert.setX(squidLabDataStage.getX() + (squidLabDataStage.getWidth() - alert.getWidth()) / 2);
                    alert.setY(squidLabDataStage.getY() + (squidLabDataStage.getHeight() - alert.getHeight()) / 2);
                    alert.showAndWait().ifPresent(p -> {
                        if (p.equals(renameButton)) {
                            TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle("Rename");
                            dialog.setHeaderText("Rename " + importedMod.getModelName());
                            dialog.setContentText("Enter the new name:");
                            Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
                            TextField newName = null;
                            for (Node n : dialog.getDialogPane().getChildren()) {
                                if (n instanceof TextField) {
                                    newName = (TextField) n;
                                }
                            }
                            if (okBtn != null && newName != null) {
                                newName.textProperty().addListener((observable, oldValue, newValue) -> {
                                    importedMod.setModelName(newValue);
                                    okBtn.setDisable(refMatModels.contains(importedMod) || newValue.isEmpty());
                                });
                            }
                            dialog.initStyle(StageStyle.UNDECORATED);
                            dialog.initOwner(squidLabDataStage.getScene().getWindow());
                            dialog.setX(squidLabDataStage.getX() + (squidLabDataStage.getWidth() - 200) / 2);
                            dialog.setY(squidLabDataStage.getY() + (squidLabDataStage.getHeight() - 150) / 2);
                            dialog.showAndWait().ifPresent(d -> {
                                importedMod.setModelName(dialog.getResult());
                                if (!refMatModels.contains(importedMod)) {
                                    importedMod.setIsEditable(true);
                                    refMatModels.add(importedMod);
                                    refMatCB.getItems().add(importedMod.getModelNameWithVersion());
                                    refMatCB.getSelectionModel().selectLast();
                                    refMatModel = importedMod;
                                    setUpRefMat();
                                    squidLabData.storeState();
                                } else {
                                    SquidMessageDialog.showWarningDialog("Invalid new name, model not imported", squidLabDataStage);
                                }
                            });
                        } else if (p.equals(changeVersionButton)) {
                            TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle("Change Version");
                            dialog.setHeaderText("Change Version " + importedMod.getModelName());
                            dialog.setContentText("Enter the new version:");
                            Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
                            TextField newName = null;
                            for (Node n : dialog.getDialogPane().getChildren()) {
                                if (n instanceof TextField) {
                                    newName = (TextField) n;
                                }
                            }
                            if (okBtn != null && newName != null) {
                                newName.textProperty().addListener((observable, oldValue, newValue) -> {
                                    importedMod.setModelName(newValue);
                                    okBtn.setDisable(refMatModels.contains(importedMod) || newValue.isEmpty());
                                });
                            }
                            dialog.initStyle(StageStyle.UNDECORATED);
                            dialog.initOwner(squidLabDataStage.getScene().getWindow());
                            dialog.setX(squidLabDataStage.getX() + (squidLabDataStage.getWidth() - 200) / 2);
                            dialog.setY(squidLabDataStage.getY() + (squidLabDataStage.getHeight() - 150) / 2);
                            dialog.showAndWait().ifPresent(d -> {
                                importedMod.setVersion(dialog.getResult());
                                if (!refMatModels.contains(importedMod)) {
                                    importedMod.setIsEditable(true);
                                    refMatModels.add(importedMod);
                                    refMatCB.getItems().add(importedMod.getModelNameWithVersion());
                                    refMatCB.getSelectionModel().selectLast();
                                    refMatModel = importedMod;
                                    setUpRefMat();
                                    squidLabData.storeState();
                                } else {
                                    SquidMessageDialog.showWarningDialog("Invalid new version, model not imported", squidLabDataStage);
                                }
                            });
                        } else if (p.equals(overwriteButton)) {
                            refMatModels.remove(importedMod);
                            importedMod.setIsEditable(true);
                            refMatModels.add(importedMod);
                            refMatCB.getItems().add(importedMod.getModelNameWithVersion());
                            refMatCB.getSelectionModel().selectLast();
                            refMatModel = importedMod;
                            setUpRefMat();
                            squidLabData.storeState();
                        }
                    });

                } else {
                    importedMod.setIsEditable(true);
                    refMatModels.add(importedMod);
                    refMatCB.getItems().add(importedMod.getModelNameWithVersion());
                    refMatCB.getSelectionModel().selectLast();
                    refMatModel = importedMod;
                    setUpRefMat();
                    squidLabData.storeState();
                }
            } catch (Exception e) {
                SquidMessageDialog.showWarningDialog("An error occurred: \n" + e.getMessage(), squidLabDataWindow);
            }
        }
        chosenTab = ParametersTab.refMat;
        squidLabDataStage.requestFocus();
    }

    @FXML
    private void importETReduxPhysicalConstantsModel(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.parametersManagerSelectPhysicalConstantsXMLFile(squidLabDataWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), squidLabDataWindow);
        }
        if (file != null) {
            try {
                final PhysicalConstantsModel importedMod = PhysicalConstantsModel.getPhysicalConstantsModelFromETReduxXML(file);
                if (physConstModels.contains(importedMod)) {

                    ButtonType renameButton = new ButtonType("Rename");
                    ButtonType changeVersionButton = new ButtonType("Change Version");
                    ButtonType cancelButton = new ButtonType("Cancel");
                    ButtonType overwriteButton = new ButtonType("Overwrite");
                    Alert alert;
                    if (physConstModels.get(physConstModels.indexOf(importedMod)).isEditable()) {
                        alert = new Alert(Alert.AlertType.WARNING, "A Physical Constants Model with the same name and version exists."
                                + "What would you like to do?", overwriteButton, renameButton, changeVersionButton, cancelButton);
                    } else {
                        alert = new Alert(Alert.AlertType.WARNING, "A Physical Constants Model with the same name and version exists."
                                + "What would you like to do?", renameButton, changeVersionButton, cancelButton);
                    }
                    alert.initStyle(StageStyle.UNDECORATED);
                    alert.initOwner(squidLabDataWindow);
                    alert.setX(squidLabDataStage.getX() + (squidLabDataStage.getWidth() - alert.getWidth()) / 2);
                    alert.setY(squidLabDataStage.getY() + (squidLabDataStage.getHeight() - alert.getHeight()) / 2);
                    alert.showAndWait().ifPresent(p -> {
                        if (p.equals(renameButton)) {
                            TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle("Rename");
                            dialog.setHeaderText("Rename " + importedMod.getModelName());
                            dialog.setContentText("Enter the new name:");
                            Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
                            TextField newName = null;
                            for (Node n : dialog.getDialogPane().getChildren()) {
                                if (n instanceof TextField) {
                                    newName = (TextField) n;
                                }
                            }
                            if (okBtn != null && newName != null) {
                                newName.textProperty().addListener((observable, oldValue, newValue) -> {
                                    importedMod.setModelName(newValue);
                                    okBtn.setDisable(physConstModels.contains(importedMod) || newValue.isEmpty());
                                });
                            }
                            dialog.initStyle(StageStyle.UNDECORATED);
                            dialog.initOwner(squidLabDataStage.getScene().getWindow());
                            dialog.setX(squidLabDataStage.getX() + (squidLabDataStage.getWidth() - 200) / 2);
                            dialog.setY(squidLabDataStage.getY() + (squidLabDataStage.getHeight() - 150) / 2);
                            dialog.showAndWait().ifPresent(d -> {
                                importedMod.setModelName(dialog.getResult());
                                if (!physConstModels.contains(importedMod)) {
                                    importedMod.setIsEditable(true);
                                    physConstModels.add(importedMod);
                                    physConstCB.getItems().add(importedMod.getModelNameWithVersion());
                                    physConstCB.getSelectionModel().selectLast();
                                    physConstModel = importedMod;
                                    setUpPhysConst();
                                    squidLabData.storeState();
                                } else {
                                    SquidMessageDialog.showWarningDialog("Invalid new name, model not imported", squidLabDataStage);
                                }
                            });
                        } else if (p.equals(changeVersionButton)) {
                            TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle("Change Version");
                            dialog.setHeaderText("Change Version " + importedMod.getModelName());
                            dialog.setContentText("Enter the new version:");
                            Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
                            TextField newName = null;
                            for (Node n : dialog.getDialogPane().getChildren()) {
                                if (n instanceof TextField) {
                                    newName = (TextField) n;
                                }
                            }
                            if (okBtn != null && newName != null) {
                                newName.textProperty().addListener((observable, oldValue, newValue) -> {
                                    importedMod.setModelName(newValue);
                                    okBtn.setDisable(physConstModels.contains(importedMod) || newValue.isEmpty());
                                });
                            }
                            dialog.initStyle(StageStyle.UNDECORATED);
                            dialog.initOwner(squidLabDataStage.getScene().getWindow());
                            dialog.setX(squidLabDataStage.getX() + (squidLabDataStage.getWidth() - 200) / 2);
                            dialog.setY(squidLabDataStage.getY() + (squidLabDataStage.getHeight() - 150) / 2);
                            dialog.showAndWait().ifPresent(d -> {
                                importedMod.setVersion(dialog.getResult());
                                if (!physConstModels.contains(importedMod)) {
                                    importedMod.setIsEditable(true);
                                    physConstModels.add(importedMod);
                                    physConstCB.getItems().add(importedMod.getModelNameWithVersion());
                                    physConstCB.getSelectionModel().selectLast();
                                    physConstModel = importedMod;
                                    setUpPhysConst();
                                    squidLabData.storeState();
                                } else {
                                    SquidMessageDialog.showWarningDialog("Invalid new version, model not imported", squidLabDataStage);
                                }
                            });
                        } else if (p.equals(overwriteButton)) {
                            commonPbModels.remove(importedMod);
                            importedMod.setIsEditable(true);
                            physConstModels.add(importedMod);
                            physConstCB.getItems().add(importedMod.getModelNameWithVersion());
                            physConstCB.getSelectionModel().selectLast();
                            physConstModel = importedMod;
                            setUpPhysConst();
                            squidLabData.storeState();
                        }
                    });

                } else {
                    importedMod.setIsEditable(true);
                    physConstModels.add(importedMod);
                    physConstCB.getItems().add(importedMod.getModelNameWithVersion());
                    physConstCB.getSelectionModel().selectLast();
                    physConstModel = importedMod;
                    setUpPhysConst();
                    squidLabData.storeState();
                }
            } catch (Exception e) {
                SquidMessageDialog.showWarningDialog("An error occurred: \n" + e.getMessage(), squidLabDataWindow);
            }
        }
        chosenTab = ParametersTab.physConst;
        squidLabDataStage.requestFocus();
    }

    @FXML
    private void importETReduxReferenceMaterial(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.parametersManagerSelectReferenceMaterialXMLFile(squidLabDataWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), squidLabDataWindow);
        }
        if (file != null) {
            try {
                final ReferenceMaterialModel importedMod = ReferenceMaterialModel.getReferenceMaterialFromETReduxXML(file);
                if (refMatModels.contains(importedMod)) {

                    ButtonType renameButton = new ButtonType("Rename");
                    ButtonType changeVersionButton = new ButtonType("Change Version");
                    ButtonType cancelButton = new ButtonType("Cancel");
                    ButtonType overwriteButton = new ButtonType("Overwrite");
                    Alert alert;
                    if (refMatModels.get(refMatModels.indexOf(importedMod)).isEditable()) {
                        alert = new Alert(Alert.AlertType.WARNING, "A Reference Material Model with the same name and version exists."
                                + "What would you like to do?", overwriteButton, renameButton, changeVersionButton, cancelButton);
                    } else {
                        alert = new Alert(Alert.AlertType.WARNING, "A Reference Material Model with the same name and version exists."
                                + "What would you like to do?", renameButton, changeVersionButton, cancelButton);
                    }
                    alert.initStyle(StageStyle.UNDECORATED);
                    alert.initOwner(squidLabDataWindow);
                    alert.setX(squidLabDataStage.getX() + (squidLabDataStage.getWidth() - alert.getWidth()) / 2);
                    alert.setY(squidLabDataStage.getY() + (squidLabDataStage.getHeight() - alert.getHeight()) / 2);
                    alert.showAndWait().ifPresent(p -> {
                        if (p.equals(renameButton)) {
                            TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle("Rename");
                            dialog.setHeaderText("Rename " + importedMod.getModelName());
                            dialog.setContentText("Enter the new name:");
                            Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
                            TextField newName = null;
                            for (Node n : dialog.getDialogPane().getChildren()) {
                                if (n instanceof TextField) {
                                    newName = (TextField) n;
                                }
                            }
                            if (okBtn != null && newName != null) {
                                newName.textProperty().addListener((observable, oldValue, newValue) -> {
                                    importedMod.setModelName(newValue);
                                    okBtn.setDisable(refMatModels.contains(importedMod) || newValue.isEmpty());
                                });
                            }
                            dialog.initStyle(StageStyle.UNDECORATED);
                            dialog.initOwner(squidLabDataStage.getScene().getWindow());
                            dialog.setX(squidLabDataStage.getX() + (squidLabDataStage.getWidth() - 200) / 2);
                            dialog.setY(squidLabDataStage.getY() + (squidLabDataStage.getHeight() - 150) / 2);
                            dialog.showAndWait().ifPresent(d -> {
                                importedMod.setModelName(dialog.getResult());
                                if (!refMatModels.contains(importedMod)) {
                                    importedMod.setIsEditable(true);
                                    refMatModels.add(importedMod);
                                    refMatCB.getItems().add(importedMod.getModelNameWithVersion());
                                    refMatCB.getSelectionModel().selectLast();
                                    refMatModel = importedMod;
                                    setUpRefMat();
                                    squidLabData.storeState();
                                } else {
                                    SquidMessageDialog.showWarningDialog("Invalid new name, model not imported", squidLabDataStage);
                                }
                            });
                        } else if (p.equals(changeVersionButton)) {
                            TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle("Change Version");
                            dialog.setHeaderText("Change Version " + importedMod.getModelName());
                            dialog.setContentText("Enter the new version:");
                            Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
                            TextField newName = null;
                            for (Node n : dialog.getDialogPane().getChildren()) {
                                if (n instanceof TextField) {
                                    newName = (TextField) n;
                                }
                            }
                            if (okBtn != null && newName != null) {
                                newName.textProperty().addListener((observable, oldValue, newValue) -> {
                                    importedMod.setModelName(newValue);
                                    okBtn.setDisable(refMatModels.contains(importedMod) || newValue.isEmpty());
                                });
                            }
                            dialog.initStyle(StageStyle.UNDECORATED);
                            dialog.initOwner(squidLabDataStage.getScene().getWindow());
                            dialog.setX(squidLabDataStage.getX() + (squidLabDataStage.getWidth() - 200) / 2);
                            dialog.setY(squidLabDataStage.getY() + (squidLabDataStage.getHeight() - 150) / 2);
                            dialog.showAndWait().ifPresent(d -> {
                                importedMod.setVersion(dialog.getResult());
                                if (!refMatModels.contains(importedMod)) {
                                    importedMod.setIsEditable(true);
                                    refMatModels.add(importedMod);
                                    refMatCB.getItems().add(importedMod.getModelNameWithVersion());
                                    refMatCB.getSelectionModel().selectLast();
                                    refMatModel = importedMod;
                                    setUpRefMat();
                                    squidLabData.storeState();
                                } else {
                                    SquidMessageDialog.showWarningDialog("Invalid new version, model not imported", squidLabDataStage);
                                }
                            });
                        } else if (p.equals(overwriteButton)) {
                            refMatModels.remove(importedMod);
                            importedMod.setIsEditable(true);
                            refMatModels.add(importedMod);
                            refMatCB.getItems().add(importedMod.getModelNameWithVersion());
                            refMatCB.getSelectionModel().selectLast();
                            refMatModel = importedMod;
                            setUpRefMat();
                            squidLabData.storeState();
                        }
                    });

                } else {
                    importedMod.setIsEditable(true);
                    refMatModels.add(importedMod);
                    refMatCB.getItems().add(importedMod.getModelNameWithVersion());
                    refMatCB.getSelectionModel().selectLast();
                    refMatModel = importedMod;
                    setUpRefMat();
                    squidLabData.storeState();
                }
            } catch (Exception e) {
                SquidMessageDialog.showWarningDialog("An error occurred: \n" + e.getMessage(), squidLabDataWindow);
            }
        }
        chosenTab = ParametersTab.refMat;
        squidLabDataStage.requestFocus();
    }

    private void physConstEditable(boolean isEditable) {
        if (isEditable) {
            physConstIsEditableLabel.setText("editing");
        }

        physConstModelName.setEditable(isEditable);
        physConstLabName.setEditable(isEditable);
        physConstVersion.setEditable(isEditable);
        physConstDateCertified.setEditable(isEditable);

        physConstDataTable.setEditable(isEditable);
        physConstDataTable.getColumns().get(0).setEditable(false);

        physConstCorrTable.setEditable(isEditable);
        physConstCorrTable.getColumns().get(0).setEditable(false);

        for (TextField text : molarMasses) {
            text.setEditable(isEditable);
        }

        for (int i = 0; i < physConstReferences.size(); i++) {
            physConstReferences.get(i).setEditable(isEditable);
        }

        physConstCommentsArea.setEditable(isEditable);
        physConstReferencesArea.setEditable(isEditable);
    }

    private void refMatEditable(boolean isEditable) {
        if (isEditable) {
            refMatIsEditableLabel.setText("editing");
            refMatReferenceDatesCheckbox.setDisable(false);
        } else {
            refMatReferenceDatesCheckbox.setDisable(true);
            refMatReferenceDatesCheckbox.setStyle("-fx-opacity: 1");
        }
        refMatModelName.setEditable(isEditable);
        refMatLabName.setEditable(isEditable);
        refMatVersion.setEditable(isEditable);
        refMatDateCertified.setEditable(isEditable);

        refMatDataTable.setEditable(isEditable);
        refMatDataTable.getColumns().get(0).setEditable(false);
        ObservableList<RefMatDataModel> refMatData = refMatDataTable.getItems();
        for (RefMatDataModel mod : refMatData) {
            if (isEditable) {
                mod.getIsMeasured().setDisable(false);
            } else {
                mod.getIsMeasured().setDisable(true);
                mod.getIsMeasured().setStyle("-fx-opacity: 1");
            }
        }

        refMatConcentrationsTable.setEditable(isEditable);
        refMatConcentrationsTable.getColumns().get(0).setEditable(false);

        refMatCorrTable.setEditable(isEditable);
        refMatCorrTable.getColumns().get(0).setEditable(false);

        refDatesTable.setEditable(isEditable);
        if (refDatesTable.getColumns() != null && refDatesTable.getColumns().size() > 0) {
            refDatesTable.getColumns().get(0).setEditable(false);
        }

        refMatCommentsArea.setEditable(isEditable);
        refMatReferencesArea.setEditable(isEditable);
    }

    private void commonPbModelEditable(boolean isEditable) {
        if (isEditable) {
            commonPbIsEditableLabel.setText("editing");
        }

        commonPbModelName.setEditable(isEditable);
        commonPbLabName.setEditable(isEditable);
        commonPbVersion.setEditable(isEditable);
        commonPbDateCertified.setEditable(isEditable);

        commonPbDataTable.setEditable(isEditable);
        commonPbDataTable.getColumns().get(0).setEditable(false);

        commonPbCorrTable.setEditable(isEditable);
        commonPbCorrTable.getColumns().get(0).setEditable(false);

        commonPbCommentsArea.setEditable(isEditable);
        commonPbReferencesArea.setEditable(isEditable);
    }

    private void setUpRefMatMenuItems(boolean isEditing, boolean isEditable) {
        setUpMenuItems(isEditing, isEditable, refMatFileMenu, saveAndRegCurrRefMat, remCurrRefMat, canEditOfRefMat, editNewEmptyRefMat,
                editCopyOfCurrRefMat, editCurrRefMat, refMatCB);
    }

    private void setUpPhysConstMenuItems(boolean isEditing, boolean isEditable) {
        setUpMenuItems(isEditing, isEditable, physConstFileMenu, saveAndRegCurrPhysConst, remCurrPhysConst, cancelEditOfPhysConst,
                editNewEmpPhysConst, editCopyOfCurrPhysConst, editCurrPhysConst, physConstCB);
    }

    private void setUpCommonPbMenuItems(boolean isEditing, boolean isEditable) {
        setUpMenuItems(isEditing, isEditable, commonPbFileMenu, saveAndRegCommonPbMenuItem, remCurrCommonPb, cancelEditOfCommonPb,
                editNewEmptyCommonPb, editCopyOfCommonPb, editCurrCommonPb, commonPbCB);
    }

    private void setUpMenuItems(boolean isEditing, boolean isEditable, Menu fileMenu, MenuItem saveAndReg,
                                MenuItem remCurr, MenuItem cancelEdit, MenuItem editNewEmp,
                                MenuItem editCopy, MenuItem editCurr, ChoiceBox<String> cB) {
        fileMenu.setDisable(isEditing);
        saveAndReg.setDisable(!isEditing);
        remCurr.setDisable(!isEditable || isEditing);
        cancelEdit.setDisable(!isEditing);
        editNewEmp.setDisable(isEditing);
        editCopy.setDisable(isEditing);
        editCurr.setDisable(!isEditable || isEditing);
        cB.setDisable(isEditing);
    }

    @FXML
    private void physConstRemoveCurrMod(ActionEvent event) {
        physConstModels.remove(physConstModel);
        physConstModels.sort(new ParametersModelComparator());
        setUpPhysConstCBItems();
        physConstCB.getSelectionModel().selectFirst();
        physConstEditable(false);
        setUpPhysConstMenuItems(false, physConstModel.isEditable());
        squidLabData.storeState();
    }

    @FXML
    private void physConstEditCurrMod(ActionEvent event) {
        physConstHolder = physConstModel.clone();
        physConstEditable(true);
        setUpPhysConstMenuItems(true, physConstModel.isEditable());
        isEditingCurrPhysConst = true;
        isEditingPhysConst = true;
    }

    @FXML
    private void physConstEditCopy(ActionEvent event) {
        physConstModel = physConstModel.clone();
        physConstModel.setModelName(physConstModel.getModelName() + " - copy");
        physConstModel.setIsEditable(true);
        setUpPhysConst();
        physConstEditable(true);
        setUpPhysConstMenuItems(true, true);
        isEditingPhysConst = true;
    }

    @FXML
    private void physConstEditEmptyMod(ActionEvent event) {
        physConstModel = new PhysicalConstantsModel();
        setUpPhysConst();
        physConstEditable(true);
        setUpPhysConstMenuItems(true, true);
        isEditingPhysConst = true;
    }

    @FXML
    private void physConstCancelEdit(ActionEvent event) {
        if (isEditingCurrPhysConst) {
            isEditingCurrPhysConst = false;
            physConstModel = physConstHolder;
            physConstHolder = null;
        }
        setUpPhysConstCBItems();
        physConstCB.getSelectionModel().select(physConstModels.indexOf(squidLabData.getPhysConstDefault()));
        physConstEditable(false);
        setUpPhysConstMenuItems(false, physConstModel.isEditable());
        isEditingPhysConst = false;
    }

    @FXML
    private void physConstSaveAndRegisterEdit(ActionEvent event) {
        boolean hasModelWithSameNameAndVersion = false;
        String name = physConstModelName.getText();
        String version = physConstVersion.getText();
        for (int i = 0; i < physConstModels.size() && !hasModelWithSameNameAndVersion; i++) {
            hasModelWithSameNameAndVersion = name.equals(physConstModels.get(i).getModelName())
                    && version.equals(physConstModels.get(i).getVersion());
        }
        if (!hasModelWithSameNameAndVersion || isEditingCurrPhysConst) {

            physConstModel.setIsEditable(true);
            physConstModel.setModelName(physConstModelName.getText());
            physConstModel.setVersion(physConstVersion.getText());
            physConstModel.setDateCertified(physConstDateCertified.getText());
            physConstModel.setLabName(physConstLabName.getText());

            Map<String, BigDecimal> masses = new HashMap<>();
            String[][] defaultMasses = DataDictionary.AtomicMolarMasses;
            for (int i = 0; i < defaultMasses.length; i++) {
                String[] defaultMass = defaultMasses[i];
                try {
                    if (Double.parseDouble(defaultMass[1]) != 0) {
                        masses.put(defaultMass[0], new BigDecimal(molarMasses.get(i).getText()));
                    }
                } catch (Exception e) {
                    masses.put(defaultMass[0], new BigDecimal(defaultMass[1]));
                }
            }

            physConstModel.setReferences(physConstReferencesArea.getText());
            physConstModel.setComments(physConstCommentsArea.getText());

            if (!isEditingCurrPhysConst) {
                physConstModels.add(physConstModel);
            } else {
                isEditingCurrPhysConst = false;
                physConstHolder = null;
            }
            physConstModels.sort(new ParametersModelComparator());
            setUpPhysConstCBItems();
            physConstCB.getSelectionModel().select(physConstModels.indexOf(physConstModel));
            physConstEditable(false);
            setUpPhysConstMenuItems(false, physConstModel.isEditable());
            isEditingPhysConst = false;

            squidLabData.storeState();
        } else {
            SquidMessageDialog.showWarningDialog("A Physical Constants Model with the same name and version exists.\n"
                    + "Please change the name and/or version", squidLabDataWindow);
        }
    }

    @FXML
    private void refMatSaveAndRegisterEdit(ActionEvent event) {
        boolean hasModelWithSameNameAndVersion = false;
        String name = refMatModelName.getText();
        String version = refMatVersion.getText();
        for (int i = 0; i < refMatModels.size() && !hasModelWithSameNameAndVersion; i++) {
            hasModelWithSameNameAndVersion = name.equals(refMatModels.get(i).getModelName())
                    && version.equals(refMatModels.get(i).getVersion());
        }
        if (!hasModelWithSameNameAndVersion || isEditingCurrRefMat) {

            refMatModel.setIsEditable(true);
            refMatModel.setModelName(refMatModelName.getText());
            refMatModel.setLabName(refMatLabName.getText());
            refMatModel.setVersion(refMatVersion.getText());
            refMatModel.setDateCertified(refMatDateCertified.getText());

            ObservableList<RefMatDataModel> dataModels = refMatDataTable.getItems();
            boolean[] isMeasures = new boolean[dataModels.size()];
            for (int i = 0; i < isMeasures.length; i++) {
                RefMatDataModel mod = dataModels.get(i);
                isMeasures[i] = mod.getIsMeasured().isSelected();
            }
            ((ReferenceMaterialModel) refMatModel).setDataMeasured(isMeasures);

            refMatModel.setReferences(refMatReferencesArea.getText());
            refMatModel.setComments(refMatCommentsArea.getText());

            if (!isEditingCurrRefMat) {
                refMatModels.add(refMatModel);
            } else {
                isEditingCurrRefMat = false;
                refMatHolder = null;
            }
            refMatModels.sort(new ParametersModelComparator());
            setUpRefMatCBItems();
            refMatCB.getSelectionModel().select(refMatModels.indexOf(refMatModel));
            refMatEditable(false);
            setUpRefMatMenuItems(false, refMatModel.isEditable());
            isEditingRefMat = false;

            squidLabData.storeState();
        } else {
            SquidMessageDialog.showWarningDialog("A Reference Material with the same name and version exists.\n"
                    + "Please change the name and/or version", squidLabDataWindow);
        }
    }

    @FXML
    private void refMatRemoveCurrMod(ActionEvent event) {
        refMatModels.remove(refMatModel);
        refMatModels.sort(new ParametersModelComparator());
        setUpRefMatCBItems();
        refMatCB.getSelectionModel().selectFirst();
        refMatEditable(false);
        setUpRefMatMenuItems(false, refMatModel.isEditable());
        squidLabData.storeState();
    }

    @FXML
    private void refMatCancelEdit(ActionEvent event) {
        if (isEditingCurrRefMat) {
            isEditingCurrRefMat = false;
            refMatModel = refMatHolder;
            refMatHolder = null;
        }
        setUpRefMatCBItems();
        refMatCB.getSelectionModel().select(refMatModels.indexOf(squidLabData.getRefMatDefault()));
        refMatEditable(false);
        setUpRefMatMenuItems(false, refMatModel.isEditable());
        isEditingRefMat = false;
    }

    @FXML
    private void refMateEditEmptyMod(ActionEvent event) {
        refMatModel = new ReferenceMaterialModel();
        ((ReferenceMaterialModel) refMatModel).generateBaseDates();
        refMatReferenceDatesCheckbox.setSelected(false);
        setUpRefMat();
        refMatEditable(true);
        setUpRefMatMenuItems(true, true);
        isEditingRefMat = true;
    }

    @FXML
    private void refMatEditCopy(ActionEvent event) {
        refMatModel = refMatModel.clone();
        refMatModel.setModelName(refMatModel.getModelName() + " - copy");
        refMatModel.setIsEditable(true);
        setUpRefMat();
        refMatEditable(true);
        setUpRefMatMenuItems(true, true);
        isEditingRefMat = true;
    }

    @FXML
    private void refMatEditCurrMod(ActionEvent event) {
        refMatHolder = refMatModel.clone();
        refMatEditable(true);
        setUpRefMatMenuItems(true, true);
        isEditingCurrRefMat = true;
        isEditingRefMat = true;
    }

    public static String trimTrailingZeroes(String s) {
        String retVal;
        if (s.contains("E")) {
            retVal = s;
        } else {
            retVal = s.indexOf(".") < 0 ? s : s.replaceAll("0*$", "").replaceAll("\\.$", "");
        }
        return retVal;
    }

    public static boolean isNumeric(String s) {
        boolean retVal;
        try {
            Double.parseDouble(s);
            retVal = true;
        } catch (Exception e) {
            retVal = false;
        }
        return retVal;
    }

    @FXML
    private void physConstDataNotationOnAction(ActionEvent event) {
        if (physConstDataNotation.equals(scientificNotation)) {
            physConstDataNotation = standardNotation;
            physConstDataNotationButton.setText("Use Scientific Notation");
        } else {
            physConstDataNotation = scientificNotation;
            physConstDataNotationButton.setText("Use Standard Notation");
        }
        ObservableList<DataModel> models = physConstDataTable.getItems();
        for (int i = 0; i < models.size(); i++) {
            DataModel mod = models.get(i);
            BigDecimal bigDec;
            int precision = physConstDataSigFigs.getValue();

            bigDec = new BigDecimal(mod.getValue());
            mod.setValue(physConstDataNotation.format(round(bigDec, precision)));

            bigDec = new BigDecimal(mod.getOneSigmaABS());
            mod.setOneSigmaABS(physConstDataNotation.format(round(bigDec, precision)));

            bigDec = new BigDecimal(mod.getOneSigmaPCT());
            mod.setOneSigmaPCT(physConstDataNotation.format(round(bigDec, precision)));
        }
        physConstDataTable.getColumns().clear();
        List<TableColumn<DataModel, String>> columns = getDataModelColumns(physConstDataTable, physConstDataNotation,
                physConstDataSigFigs.getValue());
        for (TableColumn<DataModel, String> col : columns) {
            physConstDataTable.getColumns().add(col);
        }
        physConstDataTable.refresh();
    }

    @FXML
    private void refMatDataNotationOnAction(ActionEvent event) {
        if (refMatDataNotation.equals(scientificNotation)) {
            refMatDataNotation = standardNotation;
            refMatDataNotationButton.setText("Use Scientific Notation");
        } else {
            refMatDataNotation = scientificNotation;
            refMatDataNotationButton.setText("Use Standard Notation");
        }
        ObservableList<RefMatDataModel> models = refMatDataTable.getItems();
        for (int i = 0; i < models.size(); i++) {
            DataModel mod = models.get(i);
            BigDecimal bigDec;
            int precision = refMatDataSigFigs.getValue();

            bigDec = new BigDecimal(mod.getValue());
            mod.setValue(refMatDataNotation.format(round(bigDec, precision)));

            bigDec = new BigDecimal(mod.getOneSigmaABS());
            mod.setOneSigmaABS(refMatDataNotation.format(round(bigDec, precision)));

            bigDec = new BigDecimal(mod.getOneSigmaPCT());
            mod.setOneSigmaPCT(refMatDataNotation.format(round(bigDec, precision)));
        }
        setUpRefMatDataModelColumns();

        refMatDataTable.refresh();
    }

    public static BigDecimal round(BigDecimal val, int precision) {
        return new BigDecimal("" + org.cirdles.ludwig.squid25.Utilities.roundedToSize(val.doubleValue(), precision));
    }

    public static BigDecimal round(String val, int precision) {
        return round(new BigDecimal(val), precision);
    }

    @FXML
    private void refMatConcentrationsNotationOnAction(ActionEvent event) {
        if (refMatConcentrationsNotation.equals(scientificNotation)) {
            refMatConcentrationsNotation = standardNotation;
            refMatConcentrationsNotationButton.setText("Use Scientific Notation");
        } else {
            refMatConcentrationsNotation = scientificNotation;
            refMatConcentrationsNotationButton.setText("Use Standard Notation");
        }
        ObservableList<DataModel> models = refMatConcentrationsTable.getItems();
        for (int i = 0; i < models.size(); i++) {
            DataModel mod = models.get(i);
            BigDecimal bigDec;
            int precision = refMatConcSigFigs.getValue();

            bigDec = new BigDecimal(mod.getValue());
            mod.setValue(refMatConcentrationsNotation.format(round(bigDec, precision)));

            bigDec = new BigDecimal(mod.getOneSigmaABS());
            mod.setOneSigmaABS(refMatConcentrationsNotation.format(round(bigDec, precision)));

            bigDec = new BigDecimal(mod.getOneSigmaPCT());
            mod.setOneSigmaPCT(refMatConcentrationsNotation.format(round(bigDec, precision)));
        }
        refMatConcentrationsTable.getColumns().setAll(getDataModelColumns(refMatConcentrationsTable, refMatConcentrationsNotation,
                refMatConcSigFigs.getValue()));

        refMatConcentrationsTable.refresh();
    }

    @FXML
    private void physConstCorrNotationOnAction(ActionEvent event) {
        if (physConstCorrNotation.equals(scientificNotation)) {
            physConstCorrNotation = standardNotation;
            physConstCorrNotationButton.setText("Use Scientific Notation");
        } else {
            physConstCorrNotation = scientificNotation;
            physConstCorrNotationButton.setText("Use Standard Notation");
        }
        int precision = physConstCorrSigFigs.getValue();
        corrCovPrecisionOrNotationAction(physConstModel, physConstCorrTable, precision, physConstCorrNotation);
    }

    @FXML
    private void physConstCovNotationOnAction(ActionEvent event) {
        if (physConstCovNotation.equals(scientificNotation)) {
            physConstCovNotation = standardNotation;
            physConstCovNotationButton.setText("Use Scientific Notation");
        } else {
            physConstCovNotation = scientificNotation;
            physConstCovNotationButton.setText("Use Standard Notation");
        }
        int precision = physConstCovSigFigs.getValue();
        corrCovPrecisionOrNotationAction(physConstModel, physConstCovTable, precision, physConstCovNotation);
    }

    @FXML
    private void refMatCorrNotationOnAction(ActionEvent event) {
        if (refMatCorrNotation.equals(scientificNotation)) {
            refMatCorrNotation = standardNotation;
            refMatCorrNotationButton.setText("Use Scientific Notation");
        } else {
            refMatCorrNotation = scientificNotation;
            refMatCorrNotationButton.setText("Use Standard Notation");
        }
        int precision = refMatCorrSigFigs.getValue();
        corrCovPrecisionOrNotationAction(refMatModel, refMatCorrTable, precision, refMatCorrNotation);
    }

    @FXML
    private void refMatCovNotationOnAction(ActionEvent event) {
        if (refMatCovNotation.equals(scientificNotation)) {
            refMatCovNotation = standardNotation;
            refMatCovNotationButton.setText("Use Scientific Notation");
        } else {
            refMatCovNotation = scientificNotation;
            refMatCovNotationButton.setText("Use Standard Notation");
        }
        int precision = refMatCovSigFigs.getValue();
        corrCovPrecisionOrNotationAction(refMatModel, refMatCovTable, precision, refMatCovNotation);
    }

    private void setUpSigFigSpinners() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 6);

        refMatDataSigFigs.setValueFactory(valueFactory);
        refMatDataSigFigs.valueProperty().addListener(value -> {
            int precision = refMatDataSigFigs.getValue();
            ObservableList<RefMatDataModel> items = refMatDataTable.getItems();
            for (RefMatDataModel mod : items) {
                ValueModel valMod = refMatModel.getDatumByName(getRatioHiddenName(mod.getName()));
                mod.setOneSigmaABS(refMatDataNotation.format(round(valMod.getOneSigmaABS(), precision)));
                mod.setOneSigmaPCT(refMatDataNotation.format(round(valMod.getOneSigmaPCT(), precision)));
                mod.setValue(refMatDataNotation.format(round(valMod.getValue(), precision)));
            }
            setUpRefMatDataModelColumns();
            refMatDataTable.refresh();
        });

        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 6);
        refMatConcSigFigs.setValueFactory(valueFactory);
        refMatConcSigFigs.valueProperty().addListener(value -> {
            int precision = refMatConcSigFigs.getValue();
            ObservableList<DataModel> items = refMatConcentrationsTable.getItems();
            for (DataModel mod : items) {
                ValueModel valMod = ((ReferenceMaterialModel) refMatModel).getConcentrationByName(getRatioHiddenName(mod.getName()));
                mod.setOneSigmaABS(refMatConcentrationsNotation.format(round(valMod.getOneSigmaABS(), precision)));
                mod.setOneSigmaPCT(refMatConcentrationsNotation.format(round(valMod.getOneSigmaPCT(), precision)));
                mod.setValue(refMatConcentrationsNotation.format(round(valMod.getValue(), precision)));
            }
            refMatConcentrationsTable.getColumns().setAll(getDataModelColumns(refMatConcentrationsTable, refMatConcentrationsNotation, precision));
            refMatConcentrationsTable.refresh();
        });

        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 6);
        physConstDataSigFigs.setValueFactory(valueFactory);
        physConstDataSigFigs.valueProperty().addListener(value -> {
            int precision = physConstDataSigFigs.getValue();
            ObservableList<DataModel> items = physConstDataTable.getItems();
            for (DataModel mod : items) {
                ValueModel valMod = physConstModel.getDatumByName(getRatioHiddenName(mod.getName()));
                mod.setOneSigmaABS(physConstDataNotation.format(round(valMod.getOneSigmaABS(), precision)));
                mod.setOneSigmaPCT(physConstDataNotation.format(round(valMod.getOneSigmaPCT(), precision)));
                mod.setValue(physConstDataNotation.format(round(valMod.getValue(), precision)));
            }
            physConstDataTable.getColumns().setAll(getDataModelColumns(physConstDataTable, physConstDataNotation, precision));
            physConstDataTable.refresh();
        });

        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 6);
        physConstCorrSigFigs.setValueFactory(valueFactory);
        physConstCorrSigFigs.valueProperty().addListener(value -> {
            int precision = physConstCorrSigFigs.getValue();
            corrCovPrecisionOrNotationAction(physConstModel, physConstCorrTable, precision, physConstCorrNotation);
        });

        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 6);
        physConstCovSigFigs.setValueFactory(valueFactory);
        physConstCovSigFigs.valueProperty().addListener(value -> {
            int precision = physConstCovSigFigs.getValue();
            corrCovPrecisionOrNotationAction(physConstModel, physConstCovTable, precision, physConstCovNotation);
        });

        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 6);
        refMatCorrSigFigs.setValueFactory(valueFactory);
        refMatCorrSigFigs.valueProperty().addListener(value -> {
            int precision = refMatCorrSigFigs.getValue();
            corrCovPrecisionOrNotationAction(refMatModel, refMatCorrTable, precision, refMatCorrNotation);
        });

        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 6);
        refMatCovSigFigs.setValueFactory(valueFactory);
        refMatCovSigFigs.valueProperty().addListener(value -> {
            int precision = refMatCovSigFigs.getValue();
            corrCovPrecisionOrNotationAction(refMatModel, refMatCovTable, precision, refMatCovNotation);
        });

        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 6);
        refDatesSigFigSpinner.setValueFactory(valueFactory);
        refDatesSigFigSpinner.valueProperty().addListener(value -> {
            int precision = refDatesSigFigSpinner.getValue();
            ObservableList<DataModel> items = refDatesTable.getItems();
            for (DataModel mod : items) {
                boolean found = false;
                BigDecimal divisor = new BigDecimal(getRefDatesDivisorOrMultiplier());
                for (int i = 0; !found && i < ((ReferenceMaterialModel) refMatModel).getDates().length; i++) {
                    ValueModel valMod = ((ReferenceMaterialModel) refMatModel).getDateByName(mod.getName());
                    mod.setOneSigmaABS(refDatesNotation.format(round(valMod.getOneSigmaABS(), precision).divide(divisor)));
                    mod.setOneSigmaPCT(refDatesNotation.format(round(valMod.getOneSigmaPCT(), precision).divide(divisor)));
                    mod.setValue(refDatesNotation.format(round(valMod.getValue(), precision).divide(divisor)));
                }
            }
            refDatesTable.getColumns().setAll(getDataModelColumns(refDatesTable, refDatesNotation, precision));
            refDatesTable.refresh();
        });

        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 6);
        commonPbDataSigFigs.setValueFactory(valueFactory);
        commonPbDataSigFigs.valueProperty().addListener(value -> {
            int precision = commonPbDataSigFigs.getValue();
            ObservableList<DataModel> items = commonPbDataTable.getItems();
            for (DataModel mod : items) {
                ValueModel valMod = commonPbModel.getDatumByName(getRatioHiddenName(mod.getName()));
                mod.setOneSigmaABS(commonPbDataNotation.format(round(valMod.getOneSigmaABS(), precision)));
                mod.setOneSigmaPCT(commonPbDataNotation.format(round(valMod.getOneSigmaPCT(), precision)));
                mod.setValue(commonPbDataNotation.format(round(valMod.getValue(), precision)));
            }
            commonPbDataTable.getColumns().setAll(getDataModelColumns(commonPbDataTable, commonPbDataNotation, precision));
            commonPbDataTable.refresh();
        });

        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 6);
        commonPbCorrSigFigs.setValueFactory(valueFactory);
        commonPbCorrSigFigs.valueProperty().addListener(value -> {
            int precision = commonPbCorrSigFigs.getValue();
            corrCovPrecisionOrNotationAction(commonPbModel, commonPbCorrTable, precision, commonPbCorrNotation);
        });

        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 6);
        commonPbCovSigFigs.setValueFactory(valueFactory);
        commonPbCovSigFigs.valueProperty().addListener(value -> {
            int precision = commonPbCovSigFigs.getValue();
            corrCovPrecisionOrNotationAction(commonPbModel, commonPbCovTable, precision, commonPbCovNotation);
        });
    }

    private int getRefDatesDivisorOrMultiplier() {
        int divisor;
        if (refDatesUnits == Units.a) {
            divisor = 1;
        } else if (refDatesUnits == Units.ka) {
            divisor = 1000;
        } else {
            divisor = 1000000;
        }
        return divisor;
    }

    private void corrCovPrecisionOrNotationAction(ParametersModel model,
                                                  TableView<ObservableList<SimpleStringProperty>> table, int precision, DecimalFormat format) {

        ObservableList<ObservableList<SimpleStringProperty>> items = table.getItems();

        ObservableList<SimpleStringProperty> colHeaders = FXCollections.observableArrayList();
        List<TableColumn<ObservableList<SimpleStringProperty>, ?>> columns = table.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            colHeaders.add(new SimpleStringProperty(columns.get(i).getText()));
        }
        items.add(0, colHeaders);
        initializeTableWithObList(table, items, format, model, precision);
    }

    @FXML
    private void commonPbImpXMLAction(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.parametersManagerSelectCommonPbModelXMLFile(squidLabDataWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), squidLabDataWindow);
        }
        if (file != null) {
            try {
                CommonPbModel importedMod = (CommonPbModel) commonPbModel.readXMLObject(file.getAbsolutePath(), false);

                if (commonPbModels.contains(importedMod)) {

                    ButtonType renameButton = new ButtonType("Rename");
                    ButtonType changeVersionButton = new ButtonType("Change Version");
                    ButtonType cancelButton = new ButtonType("Cancel");
                    ButtonType overwriteButton = new ButtonType("Overwrite");
                    Alert alert;
                    if (commonPbModels.get(commonPbModels.indexOf(importedMod)).isEditable()) {
                        alert = new Alert(Alert.AlertType.WARNING, "A Common Pb Model with the same name and version exists."
                                + "What would you like to do?", overwriteButton, renameButton, changeVersionButton, cancelButton);
                    } else {
                        alert = new Alert(Alert.AlertType.WARNING, "A Common Pb Model with the same name and version exists."
                                + "What would you like to do?", renameButton, changeVersionButton, cancelButton);
                    }
                    alert.initStyle(StageStyle.UNDECORATED);
                    alert.initOwner(squidLabDataWindow);
                    alert.setX(squidLabDataStage.getX() + (squidLabDataStage.getWidth() - alert.getWidth()) / 2);
                    alert.setY(squidLabDataStage.getY() + (squidLabDataStage.getHeight() - alert.getHeight()) / 2);
                    alert.showAndWait().ifPresent(p -> {
                        if (p.equals(renameButton)) {
                            TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle("Rename");
                            dialog.setHeaderText("Rename " + importedMod.getModelName());
                            dialog.setContentText("Enter the new name:");
                            Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
                            TextField newName = null;
                            for (Node n : dialog.getDialogPane().getChildren()) {
                                if (n instanceof TextField) {
                                    newName = (TextField) n;
                                }
                            }
                            if (okBtn != null && newName != null) {
                                newName.textProperty().addListener((observable, oldValue, newValue) -> {
                                    importedMod.setModelName(newValue);
                                    okBtn.setDisable(commonPbModels.contains(importedMod) || newValue.isEmpty());
                                });
                            }
                            dialog.initStyle(StageStyle.UNDECORATED);
                            dialog.initOwner(squidLabDataStage.getScene().getWindow());
                            dialog.setX(squidLabDataStage.getX() + (squidLabDataStage.getWidth() - 200) / 2);
                            dialog.setY(squidLabDataStage.getY() + (squidLabDataStage.getHeight() - 150) / 2);
                            dialog.showAndWait().ifPresent(d -> {
                                importedMod.setModelName(dialog.getResult());
                                if (!commonPbModels.contains(importedMod)) {
                                    importedMod.setIsEditable(true);
                                    commonPbModels.add(importedMod);
                                    commonPbCB.getItems().add(importedMod.getModelNameWithVersion());
                                    commonPbCB.getSelectionModel().selectLast();
                                    commonPbModel = importedMod;
                                    setUpCommonPb();
                                    squidLabData.storeState();
                                } else {
                                    SquidMessageDialog.showWarningDialog("Invalid new name, model not imported", squidLabDataStage);
                                }
                            });
                        } else if (p.equals(changeVersionButton)) {
                            TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle("Change Version");
                            dialog.setHeaderText("Change Version " + importedMod.getModelName());
                            dialog.setContentText("Enter the new version:");
                            Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
                            TextField newName = null;
                            for (Node n : dialog.getDialogPane().getChildren()) {
                                if (n instanceof TextField) {
                                    newName = (TextField) n;
                                }
                            }
                            if (okBtn != null && newName != null) {
                                newName.textProperty().addListener((observable, oldValue, newValue) -> {
                                    importedMod.setModelName(newValue);
                                    okBtn.setDisable(commonPbModels.contains(importedMod) || newValue.isEmpty());
                                });
                            }
                            dialog.initStyle(StageStyle.UNDECORATED);
                            dialog.initOwner(squidLabDataStage.getScene().getWindow());
                            dialog.setX(squidLabDataStage.getX() + (squidLabDataStage.getWidth() - 200) / 2);
                            dialog.setY(squidLabDataStage.getY() + (squidLabDataStage.getHeight() - 150) / 2);
                            dialog.showAndWait().ifPresent(d -> {
                                importedMod.setVersion(dialog.getResult());
                                if (!commonPbModels.contains(importedMod)) {
                                    importedMod.setIsEditable(true);
                                    commonPbModels.add(importedMod);
                                    commonPbCB.getItems().add(importedMod.getModelNameWithVersion());
                                    commonPbCB.getSelectionModel().selectLast();
                                    commonPbModel = importedMod;
                                    setUpCommonPb();
                                    squidLabData.storeState();
                                } else {
                                    SquidMessageDialog.showWarningDialog("Invalid new version, model not imported", squidLabDataStage);
                                }
                            });
                        } else if (p.equals(overwriteButton)) {
                            commonPbModels.remove(importedMod);
                            importedMod.setIsEditable(true);
                            commonPbModels.add(importedMod);
                            commonPbCB.getItems().add(importedMod.getModelNameWithVersion());
                            commonPbCB.getSelectionModel().selectLast();
                            commonPbModel = importedMod;
                            setUpCommonPb();
                            squidLabData.storeState();
                        }
                    });

                } else {

                    importedMod.setIsEditable(true);
                    commonPbModels.add(importedMod);
                    commonPbCB.getItems().add(importedMod.getModelNameWithVersion());
                    commonPbCB.getSelectionModel().selectLast();
                    commonPbModel = importedMod;
                    setUpCommonPb();
                    squidLabData.storeState();

                }
            } catch (Exception e) {
                SquidMessageDialog.showWarningDialog("An error occurred: \n" + e.getMessage(), squidLabDataWindow);
            }
        }

        chosenTab = ParametersTab.commonPb;
        squidLabDataStage.requestFocus();
    }

    @FXML
    private void commonPbExpXMLAction(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.parametersManagerSaveCommonPbModelXMLFile(commonPbModel, squidLabDataWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), squidLabDataWindow);
        }
        if (file != null) {
            commonPbModel.serializeXMLObject(file.getAbsolutePath());
        }
        chosenTab = ParametersTab.commonPb;
        squidLabDataStage.requestFocus();
    }

    @FXML
    private void commonPbSaveAndRegisterEdit(ActionEvent event) {
        boolean hasModelWithSameNameAndVersion = false;
        String name = commonPbModelName.getText();
        String version = commonPbVersion.getText();
        for (int i = 0; i < commonPbModels.size() && !hasModelWithSameNameAndVersion; i++) {
            hasModelWithSameNameAndVersion = name.equals(commonPbModels.get(i).getModelName())
                    && version.equals(commonPbModels.get(i).getVersion());
        }
        if (!hasModelWithSameNameAndVersion || isEditingCurrCommonPbModel) {
            commonPbModel.setIsEditable(true);
            commonPbModel.setModelName(commonPbModelName.getText());
            commonPbModel.setLabName(commonPbLabName.getText());
            commonPbModel.setVersion(commonPbVersion.getText());
            commonPbModel.setDateCertified(commonPbDateCertified.getText());

            commonPbModel.setReferences(commonPbReferencesArea.getText());
            commonPbModel.setComments(commonPbCommentsArea.getText());

            if (!isEditingCurrCommonPbModel) {
                commonPbModels.add(commonPbModel);
            } else {
                isEditingCurrCommonPbModel = false;
                commonPbModelHolder = null;
            }
            commonPbModels.sort(new ParametersModelComparator());
            isEditingCommonPb = false;
            setUpCommonPbCBItems();
            commonPbCB.getSelectionModel().select(commonPbModels.indexOf(commonPbModel));
            commonPbModelEditable(false);
            setUpCommonPbMenuItems(false, commonPbModel.isEditable());

            squidLabData.storeState();
        } else {
            SquidMessageDialog.showWarningDialog("A Common Lead Model with the same name and version exists.\n"
                    + "Please change the name and/or version", squidLabDataWindow);
        }
    }

    @FXML
    private void commonPbCancelEdit(ActionEvent event) {
        if (isEditingCurrCommonPbModel) {
            isEditingCurrCommonPbModel = false;
            commonPbModel = commonPbModelHolder;
            commonPbModelHolder = null;
        }
        isEditingCommonPb = false;
        setUpCommonPbCBItems();
        commonPbCB.getSelectionModel().select(commonPbModels.indexOf(squidLabData.getCommonPbDefault()));
        commonPbModelEditable(false);
        setUpCommonPbMenuItems(false, commonPbModel.isEditable());
    }

    @FXML
    private void commonPbRemoveCurrMod(ActionEvent event) {
        commonPbModels.remove(commonPbModel);
        commonPbModels.sort(new ParametersModelComparator());
        setUpCommonPbCBItems();
        commonPbCB.getSelectionModel().selectFirst();
        commonPbModelEditable(false);
        setUpCommonPbMenuItems(false, commonPbModel.isEditable());
        squidLabData.storeState();
    }

    @FXML
    private void commonPbEditCurrMod(ActionEvent event) {
        isEditingCommonPb = true;
        commonPbModelHolder = commonPbModel.clone();
        commonPbModelEditable(true);
        setUpCommonPbMenuItems(true, true);
        isEditingCurrCommonPbModel = true;
    }

    @FXML
    private void commonPbEditCopy(ActionEvent event) {
        isEditingCommonPb = true;
        commonPbModel = commonPbModel.clone();
        commonPbModel.setIsEditable(true);
        commonPbModel.setModelName(commonPbModel.getModelName() + " - copy");
        setUpCommonPb();
        commonPbModelEditable(true);
        setUpCommonPbMenuItems(true, true);
    }

    @FXML
    private void commonPbEditEmptyMod(ActionEvent event) {
        isEditingCommonPb = true;
        commonPbModel = new CommonPbModel();
        setUpCommonPb();
        commonPbModelEditable(true);
        setUpCommonPbMenuItems(true, true);
    }

    @FXML
    private void commonPbDataNotationOnAction(ActionEvent event) {
        if (commonPbDataNotation.equals(scientificNotation)) {
            commonPbDataNotation = standardNotation;
            commonPbDataNotationButton.setText("Use Scientific Notation");
        } else {
            commonPbDataNotation = scientificNotation;
            commonPbDataNotationButton.setText("Use Standard Notation");
        }
        ObservableList<DataModel> models = commonPbDataTable.getItems();
        for (int i = 0; i < models.size(); i++) {
            DataModel mod = models.get(i);
            BigDecimal bigDec;
            int precision = commonPbDataSigFigs.getValue();
            bigDec = new BigDecimal(mod.getValue());
            mod.setValue(commonPbDataNotation.format(round(bigDec, precision)));

            bigDec = new BigDecimal(mod.getOneSigmaABS());
            mod.setOneSigmaABS(commonPbDataNotation.format(round(bigDec, precision)));

            bigDec = new BigDecimal(mod.getOneSigmaPCT());
            mod.setOneSigmaPCT(commonPbDataNotation.format(round(bigDec, precision)));
        }
        commonPbDataTable.getColumns().setAll(getDataModelColumns(commonPbDataTable, commonPbDataNotation,
                commonPbDataSigFigs.getValue()));

        commonPbDataTable.refresh();
    }

    @FXML
    private void commonPbCorrNotationOnAction(ActionEvent event) {
        if (commonPbCorrNotation.equals(scientificNotation)) {
            commonPbCorrNotation = standardNotation;
            commonPbCorrNotationButton.setText("Use Scientific Notation");
        } else {
            commonPbCorrNotation = scientificNotation;
            commonPbCorrNotationButton.setText("Use Standard Notation");
        }
        int precision = commonPbCorrSigFigs.getValue();
        corrCovPrecisionOrNotationAction(commonPbModel, commonPbCorrTable, precision, commonPbCorrNotation);
    }

    @FXML
    private void commonPbCovNotationOnAction(ActionEvent event) {
        if (commonPbCovNotation.equals(scientificNotation)) {
            commonPbCovNotation = standardNotation;
            commonPbCovNotationButton.setText("Use Scientific Notation");
        } else {
            commonPbCovNotation = scientificNotation;
            commonPbCovNotationButton.setText("Use Standard Notation");
        }
        int precision = commonPbCovSigFigs.getValue();
        corrCovPrecisionOrNotationAction(commonPbModel, commonPbCovTable, precision, commonPbCovNotation);
    }

    private void setUpApparentDatesTabSelection() {
        apparentDatesTab.setOnSelectionChanged(value -> {
            if (apparentDatesTab.isSelected() && isEditingRefMat) {
                setUpApparentDates();
            }
        });
    }

    @FXML
    public void refMatReferenceDatesCheckBoxOnAction(ActionEvent actionEvent) {
        ((ReferenceMaterialModel) refMatModel).setReferenceDates(refMatReferenceDatesCheckbox.isSelected());
        setUpRefMatDatesSelection();
    }

    private void setUpRefMatDatesSelection() {
        if (((ReferenceMaterialModel) refMatModel).isReferenceDates()) {
            refMatDataTab.setDisable(true);
            refMatCorrTab.setDisable(true);
            refMatCovTab.setDisable(true);
            apparentDatesTab.setDisable(true);

            refMatRefDatesTab.setDisable(false);

            if (((ReferenceMaterialModel) refMatModel).getDates().length == 0) {
                ((ReferenceMaterialModel) refMatModel).generateBaseDates();
            }

            Tab selectedTab = refMatTabPane.getSelectionModel().getSelectedItem();
            if (selectedTab == refMatDataTab || selectedTab == refMatCorrTab || selectedTab == refMatCovTab || selectedTab == apparentDatesTab) {
                refMatTabPane.getSelectionModel().select(refMatRefDatesTab);
            }
        } else {
            refMatDataTab.setDisable(false);
            refMatCorrTab.setDisable(false);
            refMatCovTab.setDisable(false);
            apparentDatesTab.setDisable(false);

            refMatRefDatesTab.setDisable(true);

            if (refMatTabPane.getSelectionModel().getSelectedItem() == refMatRefDatesTab) {
                refMatTabPane.getSelectionModel().select(refMatDataTab);
            }
        }
        setUpDates();
    }

    @FXML
    public void refDatesNotationAction(ActionEvent actionEvent) {
        if (refDatesNotation.equals(standardNotation)) {
            refDatesNotationButton.setText("Use Standard Notation");
            refDatesNotation = scientificNotation;
        } else {
            refDatesNotationButton.setText("Use Scientific Notation");
            refDatesNotation = standardNotation;
        }
        ObservableList<DataModel> dataModels = refDatesTable.getItems();
        int precision = refDatesSigFigSpinner.getValue();
        for (DataModel model : dataModels) {
            BigDecimal bigDec;

            bigDec = new BigDecimal(model.getValue());
            model.setValue(refDatesNotation.format(round(bigDec, precision)));

            bigDec = new BigDecimal(model.getOneSigmaABS());
            model.setOneSigmaABS(refDatesNotation.format(round(bigDec, precision)));

            bigDec = new BigDecimal(model.getOneSigmaPCT());
            model.setOneSigmaPCT(refDatesNotation.format(round(bigDec, precision)));
        }
        refDatesTable.getColumns().setAll(getDataModelColumns(refDatesTable, refDatesNotation, precision));
        refDatesTable.refresh();
    }

    @FXML
    public void refDatesKARadioButtonOnAction(ActionEvent actionEvent) {
        refDatesUnits = Units.ka;
        setUpRefDates();
    }

    @FXML
    public void refDatesMARadioButtonOnAction(ActionEvent actionEvent) {
        refDatesUnits = Units.ma;
        setUpRefDates();
    }

    @FXML
    public void refDatesARadioButtonOnAction(ActionEvent actionEvent) {
        refDatesUnits = Units.a;
        setUpRefDates();
    }

    public enum Units {
        a, ka, ma
    }

    public class DataModel {

        private SimpleStringProperty name;
        private SimpleStringProperty value;
        private SimpleStringProperty oneSigmaABS;
        private SimpleStringProperty oneSigmaPCT;

        public DataModel(String name, String value,
                         String oneSigmaABS, String oneSigmaPCT) {
            this.name = new SimpleStringProperty(name);
            this.value = new SimpleStringProperty(trimTrailingZeroes(value));
            this.oneSigmaABS = new SimpleStringProperty(trimTrailingZeroes(oneSigmaABS));
            this.oneSigmaPCT = new SimpleStringProperty(trimTrailingZeroes(oneSigmaPCT));
        }

        public String getName() {
            return name.get();
        }

        public String getValue() {
            return value.get();
        }

        public String getOneSigmaABS() {
            return oneSigmaABS.get();
        }

        public String getOneSigmaPCT() {
            return oneSigmaPCT.get();
        }

        public void setName(String name) {
            this.name = new SimpleStringProperty(name);
        }

        public void setValue(String value) {
            this.value = new SimpleStringProperty(value);
        }

        public void setOneSigmaABS(String oneSigmaABS) {
            this.oneSigmaABS = new SimpleStringProperty(oneSigmaABS);
        }

        public void setOneSigmaPCT(String oneSigmaPCT) {
            this.oneSigmaPCT = new SimpleStringProperty(oneSigmaPCT);
        }

    }

    public class RefMatDataModel extends DataModel {

        private CheckBox isMeasured;

        public RefMatDataModel(String name, String value,
                               String oneSigmaABS, String oneSigmaPCT,
                               boolean isMeasured) {
            super(name, value, oneSigmaABS, oneSigmaPCT);
            this.isMeasured = new CheckBox();
            this.isMeasured.setSelected(isMeasured);
        }

        public CheckBox getIsMeasured() {
            return isMeasured;
        }

        public void setIsMeasured(CheckBox isMeasured) {
            this.isMeasured = isMeasured;
        }

    }

}