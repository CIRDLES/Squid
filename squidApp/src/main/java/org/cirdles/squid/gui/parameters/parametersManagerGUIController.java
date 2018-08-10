/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.parameters;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.text.TextAlignment;
import org.cirdles.squid.dialogs.SquidMessageDialog;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidLabData;
import org.cirdles.squid.gui.parameters.ParametersLauncher.ParametersTab;
import static org.cirdles.squid.gui.parameters.ParametersLauncher.squidLabDataStage;
import org.cirdles.squid.gui.utilities.fileUtilities.FileHandler;
import org.cirdles.squid.parameters.matrices.AbstractMatrixModel;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.parameterModels.pbBlankICModels.CommonPbModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterials.ReferenceMaterial;
import org.cirdles.squid.parameters.util.DataDictionary;
import org.cirdles.squid.parameters.valueModels.ValueModel;

/**
 * FXML Controller class
 *
 * @author ryanb
 */
public class parametersManagerGUIController implements Initializable {

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
    private TextField refMatDataSigFigs;
    @FXML
    private TextField physConstDataSigFigs;
    @FXML
    private TextField physConstCorrSigFigs;
    @FXML
    private TextField physConstCovSigFigs;
    @FXML
    private TextField refMatCorrSigFigs;
    @FXML
    private TextField refMatCovSigFigs;
    @FXML
    private TextField refMatConcSigFigs;
    @FXML
    private MenuItem saveAndRegCurrBlankICIsEditableMenuItem;
    @FXML
    private MenuItem cancelEditOfPbBlankIC;
    @FXML
    private MenuItem remCurrPbBlankIC;
    @FXML
    private MenuItem editCurrPbBlankIC;
    @FXML
    private MenuItem editCopyOfPbBlankIC;
    @FXML
    private MenuItem editNewEmptyPbBlankIC;
    @FXML
    private TableView<DataModel> pbBlankICDataTable;
    @FXML
    private Button pbBlankICDataNotationButton;
    @FXML
    private TextField pbBlankICDataSigFigs;
    @FXML
    private TableView<ObservableList<SimpleStringProperty>> pbBlankICCorrTable;
    @FXML
    private Button pbBlankICCorrNotationButton;
    @FXML
    private TableView<ObservableList<SimpleStringProperty>> pbBlankICCovTable;
    @FXML
    private Button pbBlankICCovNotationButton;
    @FXML
    private TextField pbBlankICCovSigFigs;
    @FXML
    private TextArea pbBlankICReferencesArea;
    @FXML
    private TextArea pbBlankICCommentsArea;
    @FXML
    private TextField pbBlankICModelName;
    @FXML
    private TextField pbBlankICVersion;
    @FXML
    private Label pbBlankICIsEditableLabel;
    @FXML
    private TextField pbBlankICLabName;
    @FXML
    private TextField pbBlankICDateCertified;
    @FXML
    private Menu pbBlankICFileMenu;
    @FXML
    private TextField pbBlankICCorrSigFigs;
    @FXML
    private ChoiceBox<String> pbBlankICCB;
    @FXML
    private Tab apparentDatesTab;

    PhysicalConstantsModel physConstModel;
    PhysicalConstantsModel physConstHolder;

    ReferenceMaterial refMatModel;
    ReferenceMaterial refMatHolder;

    CommonPbModel pbBlankICModel;
    CommonPbModel pbBlankICModelHolder;

    List<PhysicalConstantsModel> physConstModels;
    List<ReferenceMaterial> refMatModels;
    List<CommonPbModel> pbBlankICModels;

    List<TextField> physConstReferences;
    List<TextField> molarMasses;

    private boolean isEditingCurrPhysConst;
    private boolean isEditingCurrRefMat;
    private boolean isEditingCurrPbBlankICModel;

    private DecimalFormat physConstDataNotation;
    private DecimalFormat physConstCorrNotation;
    private DecimalFormat physConstCovNotation;

    private DecimalFormat refMatDataNotation;
    private DecimalFormat refMatConcentrationsNotation;
    private DecimalFormat refMatCorrNotation;
    private DecimalFormat refMatCovNotation;

    private DecimalFormat pbBlankICDataNotation;
    private DecimalFormat pbBlankICCorrNotation;
    private DecimalFormat pbBlankICCovNotation;

    public static boolean isEditingPhysConst;
    public static boolean isEditingRefMat;
    public static boolean isEditingPbBlankIC;

    public static ParametersTab chosenTab = ParametersTab.physConst;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isEditingCurrPbBlankICModel = false;
        isEditingCurrPhysConst = false;
        isEditingCurrRefMat = false;
        isEditingPhysConst = false;
        isEditingRefMat = false;
        isEditingPbBlankIC = false;

        physConstDataNotation = getScientificNotationFormat();
        physConstCorrNotation = getScientificNotationFormat();
        physConstCovNotation = getScientificNotationFormat();

        refMatDataNotation = getScientificNotationFormat();
        refMatConcentrationsNotation = getScientificNotationFormat();
        refMatCorrNotation = getScientificNotationFormat();
        refMatCovNotation = getScientificNotationFormat();

        pbBlankICDataNotation = getScientificNotationFormat();
        pbBlankICCorrNotation = getScientificNotationFormat();
        pbBlankICCovNotation = getScientificNotationFormat();

        physConstModels = squidLabData.getPhysicalConstantsModels();
        setUpPhysConstCB();

        refMatModels = squidLabData.getReferenceMaterials();
        setUpRefMatCB();

        pbBlankICModels = squidLabData.getPbBlankICModels();
        setUpPbBlankICCB();

        setUpTabs();
        setUpApparentDatesTabSelection();
        setUpLaboratoryName();
        setUpSigFigTextFields();
    }

    private void setUpTabs() {
        squidLabDataStage.focusedProperty().addListener(listener -> {
            if (squidLabDataStage.isFocused()) {
                if (chosenTab.equals(ParametersTab.physConst)) {
                    rootTabPane.getSelectionModel().select(0);
                } else if (chosenTab.equals(ParametersTab.refMat)) {
                    rootTabPane.getSelectionModel().select(1);
                } else if (chosenTab.equals(ParametersTab.pbBlankIC)) {
                    rootTabPane.getSelectionModel().select(2);
                }
                String selected = "";
                if (!isEditingPhysConst) {
                    selected = physConstCB.getSelectionModel().getSelectedItem();
                    setUpPhysConstCBItems();
                    physConstCB.getSelectionModel().select(selected);
                }
                if (!isEditingRefMat) {
                    selected = refMatCB.getSelectionModel().getSelectedItem();
                    setUpRefMatCBItems();
                    refMatCB.getSelectionModel().select(selected);
                }
                if (!isEditingPbBlankIC) {
                    selected = pbBlankICCB.getSelectionModel().getSelectedItem();
                    setUpPbBlankICCBItems();
                    pbBlankICCB.getSelectionModel().select(selected);
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
        setUpApparentDates();
        setUpRefMatEditableLabel();
    }

    private void setUpPbBlankIC() {
        setUpPbBlankICTextFields();
        setUpPbBlankICData();
        setUpPbBlankICCovariancesAndCorrelations();
        setUpPbBlankICEditableLabel();
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

    private void setUpPbBlankICCovariancesAndCorrelations() {
        pbBlankICModel.initializeCorrelations();
        pbBlankICModel.generateCovariancesFromCorrelations();
        setUpPbBlankICCov();
        setUpPbBlankICCorr();
    }

    private void setUpPhysConstCB() {
        setUpPhysConstCBItems();
        physConstCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue val, Number ov, Number nv) {
                setPhysConstModel(nv.intValue());
            }
        });
        physConstCB.getSelectionModel().selectFirst();
    }

    private void setUpPhysConstCBItems() {
        final ObservableList<String> cbList = FXCollections.observableArrayList();
        for (PhysicalConstantsModel mod : physConstModels) {
            cbList.add(getModVersionName(mod));
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
        refMatCB.getSelectionModel().selectFirst();
    }

    private void setUpRefMatCBItems() {
        final ObservableList<String> cbList = FXCollections.observableArrayList();
        for (ReferenceMaterial mod : refMatModels) {
            cbList.add(getModVersionName(mod));
        }
        refMatCB.setItems(cbList);
    }

    private void setRefMatModel(int num) {
        if (num > -1 && num < refMatModels.size()) {
            refMatModel = refMatModels.get(num);
            setUpRefMat();
            setUpRefMatMenuItems(false, refMatModel.isEditable());
            refMatEditable(false);
        }
    }

    private void setUpPbBlankICCB() {
        setUpPbBlankICCBItems();
        pbBlankICCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue val, Number ov, Number nv) {
                setPbBlankICModel(nv.intValue());
            }
        });
        pbBlankICCB.getSelectionModel().selectFirst();
    }

    private void setPbBlankICModel(int num) {
        if (num > -1 && num < pbBlankICModels.size()) {
            pbBlankICModel = pbBlankICModels.get(num);
            setUpPbBlankIC();
            setUpPbBlankICMenuItems(false, pbBlankICModel.isEditable());
            pbBlankICModelEditable(false);
        }
    }

    private void setUpPbBlankICCBItems() {
        final ObservableList<String> cbList = FXCollections.observableArrayList();
        for (CommonPbModel mod : pbBlankICModels) {
            cbList.add(getModVersionName(mod));
        }
        pbBlankICCB.setItems(cbList);
    }

    private void setUpPhysConstCov() {
        initializeTableWithObList(physConstCovTable, getObListFromMatrix(physConstModel.getCovModel()),
                physConstCovNotation, physConstModel, getPrecisionValue(physConstCovSigFigs.getText()));
    }

    private void setUpPhysConstCorr() {
        initializeTableWithObList(physConstCorrTable, getObListFromMatrix(physConstModel.getCorrModel()),
                physConstCorrNotation, physConstModel, getPrecisionValue(physConstCorrSigFigs.getText()));
    }

    private void setUpRefMatCov() {
        initializeTableWithObList(refMatCovTable, getObListFromMatrix(refMatModel.getCovModel()),
                refMatCovNotation, refMatModel, getPrecisionValue(refMatCovSigFigs.getText()));
    }

    private void setUpRefMatCorr() {
        initializeTableWithObList(refMatCorrTable, getObListFromMatrix(refMatModel.getCorrModel()),
                refMatCorrNotation, refMatModel, getPrecisionValue(refMatCorrSigFigs.getText()));
    }

    private void setUpPbBlankICCov() {
        initializeTableWithObList(pbBlankICCovTable, getObListFromMatrix(pbBlankICModel.getCovModel()),
                pbBlankICCovNotation, pbBlankICModel, getPrecisionValue(pbBlankICCovSigFigs.getText()));
    }

    private void setUpPbBlankICCorr() {
        initializeTableWithObList(pbBlankICCorrTable, getObListFromMatrix(pbBlankICModel.getCorrModel()),
                pbBlankICCorrNotation, pbBlankICModel, getPrecisionValue(pbBlankICCorrSigFigs.getText()));
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
            ObservableList<ObservableList<SimpleStringProperty>> obList, DecimalFormat format, ParametersModel model, int precision) {
        if (obList.size() > 0) {
            List<TableColumn<ObservableList<SimpleStringProperty>, String>> columns = new ArrayList<>();
            ObservableList<SimpleStringProperty> cols = obList.remove(0);
            table.getColumns().clear();
            TableColumn<ObservableList<SimpleStringProperty>, String> rowHead
                    = new TableColumn<>(getRatioVisibleName(cols.get(0).get()));
            final int rowHeadNum = 0;
            rowHead.setSortable(false);
            rowHead.setEditable(false);
            rowHead.setCellFactory(TextFieldTableCell.<ObservableList<SimpleStringProperty>>forTableColumn());
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

                if (table.equals(physConstCorrTable) || table.equals(refMatCorrTable) || table.equals(pbBlankICCorrTable)) {
                    col.setCellFactory(TextFieldTableCell.<ObservableList<SimpleStringProperty>>forTableColumn());
                    col.setOnEditCommit(value -> {
                        if (isNumeric(value.getNewValue()) && Double.parseDouble(value.getNewValue()) <= 1
                                && Double.parseDouble(value.getNewValue()) >= -1) {
                            int rowNum = value.getTablePosition().getRow();
                            String colRatio = getRatioHiddenName(value.getTableColumn().getText());
                            String rowRatio = getRatioHiddenName(value.getRowValue().get(0).get());
                            String key = "rho" + colRatio.substring(0, 1).toUpperCase() + colRatio.substring(1) + "__" + rowRatio;
                            model.getRhos().remove(key);
                            if (Double.parseDouble(value.getNewValue()) != 0) {
                                model.getRhos().put(key, new BigDecimal(value.getNewValue()));
                            }
                            model.initializeCorrelations();
                            model.generateCovariancesFromCorrelations();
                            if (table.equals(physConstCorrTable)) {
                                setUpPhysConstCorr();
                                setUpPhysConstCov();
                            } else if (table.equals(refMatCorrTable)) {
                                setUpRefMatCorr();
                                setUpRefMatCov();
                            } else if (table.equals(pbBlankICCorrTable)) {
                                setUpPbBlankICCorr();
                                setUpPbBlankICCov();
                            }
                            table.refresh();
                        } else {
                            SquidMessageDialog.showWarningDialog("Value Out of Range or Invalid: Only values"
                                    + " in the range of [-1, 1] are allowed.", primaryStageWindow);
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
        int precision = getPrecisionValue(physConstDataSigFigs.getText());
        physConstDataTable.getColumns().setAll(getDataModelColumns(physConstDataTable, physConstDataNotation, precision));
        physConstDataTable.setItems(getDataModelObList(physConstModel.getValues(), physConstDataNotation, precision));
        physConstDataTable.refresh();
    }

    private void setUpPbBlankICData() {
        int precision = getPrecisionValue(pbBlankICDataSigFigs.getText());
        pbBlankICDataTable.getColumns().setAll(getDataModelColumns(pbBlankICDataTable, pbBlankICDataNotation, precision));
        pbBlankICDataTable.setItems(getDataModelObList(pbBlankICModel.getValues(), pbBlankICDataNotation, precision));
        pbBlankICDataTable.refresh();
    }

    private void setUpRefMatData() {
        setUpRefMatDataModelColumns();
        int precision = getPrecisionValue(refMatDataSigFigs.getText());
        final ObservableList<RefMatDataModel> obList = FXCollections.observableArrayList();
        ValueModel[] values = refMatModel.getValues();
        for (int i = 0; i < values.length; i++) {
            ValueModel valMod = values[i];
            Boolean isMeasured = refMatModel.getDataMeasured()[i];
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
        int precision = getPrecisionValue(refMatConcSigFigs.getText());
        refMatConcentrationsTable.getColumns().setAll(getDataModelColumns(refMatConcentrationsTable, refMatConcentrationsNotation, precision));
        refMatConcentrationsTable.setItems(getDataModelObList(refMatModel.getConcentrations(), refMatConcentrationsNotation, precision));
        refMatConcentrationsTable.refresh();
    }

    private List<TableColumn<DataModel, String>> getDataModelColumns(TableView<DataModel> table, DecimalFormat format, int precision) {
        List<TableColumn<DataModel, String>> columns = new ArrayList<>();

        TableColumn<DataModel, String> nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(new PropertyValueFactory<DataModel, String>("name"));
        nameCol.setSortable(false);
        nameCol.setEditable(false);
        nameCol.setCellFactory(TextFieldTableCell.<DataModel>forTableColumn());
        columns.add(nameCol);

        TableColumn<DataModel, String> valCol = new TableColumn<>("value");
        valCol.setCellValueFactory(new PropertyValueFactory<DataModel, String>("value"));
        valCol.setSortable(false);
        valCol.setCellFactory(TextFieldTableCell.<DataModel>forTableColumn());
        valCol.setOnEditCommit(value -> {
            if (isNumeric(value.getNewValue())) {
                ObservableList<DataModel> items = value.getTableView().getItems();
                DataModel mod = items.get(value.getTablePosition().getRow());
                String ratioName = getRatioHiddenName(mod.getName());
                ValueModel valMod = new ValueModel(ratioName);
                if (table.equals(physConstDataTable)) {
                    valMod = physConstModel.getDatumByName(ratioName);
                }
                if (table.equals(refMatConcentrationsTable)) {
                    valMod = refMatModel.getConcentrationByName(ratioName);
                }
                if (table.equals(pbBlankICDataTable)) {
                    valMod = pbBlankICModel.getDatumByName(ratioName);
                }
                BigDecimal newValue = BigDecimal.ZERO;
                if (Double.parseDouble(value.getNewValue()) != 0) {
                    newValue = new BigDecimal(value.getNewValue());
                }
                valMod.setValue(newValue);
                mod.setValue(format.format(round(valMod.getValue(), precision)));
                mod.setOneSigmaABS(format.format(round(valMod.getOneSigmaABS(), precision)));
                mod.setOneSigmaPCT(format.format(round(valMod.getOneSigmaPCT(), precision)));
                value.getTableView().refresh();
                if (table.equals(physConstDataTable)) {
                    setUpPhysConstCovariancesAndCorrelations();
                }
                if (table.equals(pbBlankICDataTable)) {
                    setUpPbBlankICCovariancesAndCorrelations();
                }
            } else {
                SquidMessageDialog.showWarningDialog("Invalid Value Entered!", primaryStageWindow);
                value.getTableView().refresh();
            }
        });
        columns.add(valCol);

        TableColumn<DataModel, String> absCol = new TableColumn<>("1σ ABS");
        absCol.setCellValueFactory(new PropertyValueFactory<DataModel, String>("oneSigmaABS"));
        absCol.setSortable(false);
        absCol.setCellFactory(TextFieldTableCell.<DataModel>forTableColumn());
        absCol.setOnEditCommit(value -> {
            if (isNumeric(value.getNewValue())) {
                ObservableList<DataModel> items = value.getTableView().getItems();
                DataModel mod = items.get(value.getTablePosition().getRow());
                String ratioName = getRatioHiddenName(mod.getName());
                ValueModel valMod = new ValueModel(ratioName);
                if (table.equals(physConstDataTable)) {
                    valMod = physConstModel.getDatumByName(ratioName);
                }
                if (table.equals(refMatConcentrationsTable)) {
                    valMod = refMatModel.getConcentrationByName(ratioName);
                }
                if (table.equals(pbBlankICDataTable)) {
                    valMod = pbBlankICModel.getDatumByName(ratioName);
                }
                BigDecimal newValue = BigDecimal.ZERO;
                if (Double.parseDouble(value.getNewValue()) != 0) {
                    newValue = new BigDecimal(value.getNewValue());
                }
                valMod.setOneSigma(newValue);
                valMod.setUncertaintyType("ABS");
                mod.setValue(format.format(round(valMod.getValue(), precision)));
                mod.setOneSigmaABS(format.format(round(valMod.getOneSigmaABS(), precision)));
                mod.setOneSigmaPCT(format.format(round(valMod.getOneSigmaPCT(), precision)));
                value.getTableView().refresh();
                if (table.equals(physConstDataTable)) {
                    setUpPhysConstCovariancesAndCorrelations();
                }
                if (table.equals(pbBlankICDataTable)) {
                    setUpPbBlankICCovariancesAndCorrelations();
                }
            } else {
                SquidMessageDialog.showWarningDialog("Invalid Value Entered!", primaryStageWindow);
                value.getTableView().refresh();
            }
        });
        columns.add(absCol);

        TableColumn<DataModel, String> pctCol = new TableColumn<>("1σ PCT");
        pctCol.setCellValueFactory(new PropertyValueFactory<DataModel, String>("oneSigmaPCT"));
        pctCol.setSortable(false);
        pctCol.setCellFactory(TextFieldTableCell.<DataModel>forTableColumn());
        pctCol.setOnEditCommit(value -> {
            if (isNumeric(value.getNewValue())) {
                ObservableList<DataModel> items = value.getTableView().getItems();
                DataModel mod = items.get(value.getTablePosition().getRow());
                String ratioName = getRatioHiddenName(mod.getName());
                ValueModel valMod = new ValueModel(ratioName);
                if (table.equals(physConstDataTable)) {
                    valMod = physConstModel.getDatumByName(ratioName);
                }
                if (table.equals(refMatConcentrationsTable)) {
                    valMod = refMatModel.getConcentrationByName(ratioName);
                }
                if (table.equals(pbBlankICDataTable)) {
                    valMod = pbBlankICModel.getDatumByName(ratioName);
                }
                BigDecimal newValue = BigDecimal.ZERO;
                if (Double.parseDouble(value.getNewValue()) != 0) {
                    newValue = new BigDecimal(value.getNewValue());
                }
                valMod.setOneSigma(newValue);
                valMod.setUncertaintyType("PCT");
                mod.setValue(format.format(round(valMod.getValue(), precision)));
                mod.setOneSigmaABS(format.format(round(valMod.getOneSigmaABS(), precision)));
                mod.setOneSigmaPCT(format.format(round(valMod.getOneSigmaPCT(), precision)));
                value.getTableView().refresh();
                if (table.equals(physConstDataTable)) {
                    setUpPhysConstCovariancesAndCorrelations();
                }
                if (table.equals(pbBlankICDataTable)) {
                    setUpPbBlankICCovariancesAndCorrelations();
                }
            } else {
                SquidMessageDialog.showWarningDialog("Invalid Value Entered!", primaryStageWindow);
                value.getTableView().refresh();
            }
        });
        columns.add(pctCol);

        return columns;
    }

    private void setUpRefMatDataModelColumns() {
        refMatDataTable.getColumns().clear();
        int precision = getPrecisionValue(refMatDataSigFigs.getText());

        TableColumn<RefMatDataModel, String> nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(new PropertyValueFactory<RefMatDataModel, String>("name"));
        nameCol.setSortable(false);
        nameCol.setCellFactory(TextFieldTableCell.<RefMatDataModel>forTableColumn());
        nameCol.setEditable(false);
        refMatDataTable.getColumns().add(nameCol);

        TableColumn<RefMatDataModel, String> valCol = new TableColumn<>("value");
        valCol.setCellValueFactory(new PropertyValueFactory<RefMatDataModel, String>("value"));
        valCol.setSortable(false);
        valCol.setCellFactory(TextFieldTableCell.<RefMatDataModel>forTableColumn());
        valCol.setOnEditCommit(value -> {
            if (isNumeric(value.getNewValue())) {
                ObservableList<RefMatDataModel> items = value.getTableView().getItems();
                DataModel mod = items.get(value.getTablePosition().getRow());
                String ratioName = getRatioHiddenName(mod.getName());
                ValueModel valMod = refMatModel.getDatumByName(ratioName);
                BigDecimal newValue = BigDecimal.ZERO;
                if (Double.parseDouble(value.getNewValue()) != 0) {
                    newValue = new BigDecimal(value.getNewValue());
                }
                valMod.setValue(newValue);
                mod.setValue(refMatDataNotation.format(round(valMod.getValue(), precision)));
                mod.setOneSigmaABS(refMatDataNotation.format(round(valMod.getOneSigmaABS(), precision)));
                mod.setOneSigmaPCT(refMatDataNotation.format(round(valMod.getOneSigmaPCT(), precision)));
                value.getTableView().refresh();
                setUpRefMatCovariancesAndCorrelations();
            } else {
                SquidMessageDialog.showWarningDialog("Invalid Value Entered!", primaryStageWindow);
                value.getTableView().refresh();
            }
        });
        refMatDataTable.getColumns().add(valCol);

        TableColumn<RefMatDataModel, String> absCol = new TableColumn<>("1σ ABS");
        absCol.setCellValueFactory(new PropertyValueFactory<RefMatDataModel, String>("oneSigmaABS"));
        absCol.setSortable(false);
        absCol.setCellFactory(TextFieldTableCell.<RefMatDataModel>forTableColumn());
        absCol.setOnEditCommit(value -> {
            if (isNumeric(value.getNewValue())) {
                ObservableList<RefMatDataModel> items = value.getTableView().getItems();
                DataModel mod = items.get(value.getTablePosition().getRow());
                String ratioName = getRatioHiddenName(mod.getName());
                ValueModel valMod = refMatModel.getDatumByName(ratioName);
                BigDecimal newValue = BigDecimal.ZERO;
                if (Double.parseDouble(value.getNewValue()) != 0) {
                    newValue = new BigDecimal(value.getNewValue());
                }
                valMod.setOneSigma(newValue);
                valMod.setUncertaintyType("ABS");
                mod.setValue(refMatDataNotation.format(round(valMod.getValue(), precision)));
                mod.setOneSigmaABS(refMatDataNotation.format(round(valMod.getOneSigmaABS(), precision)));
                mod.setOneSigmaPCT(refMatDataNotation.format(round(valMod.getOneSigmaPCT(), precision)));
                value.getTableView().refresh();
                setUpRefMatCovariancesAndCorrelations();
            } else {
                SquidMessageDialog.showWarningDialog("Invalid Value Entered!", primaryStageWindow);
                value.getTableView().refresh();
            }
        });
        refMatDataTable.getColumns().add(absCol);

        TableColumn<RefMatDataModel, String> pctCol = new TableColumn<>("1σ PCT");
        pctCol.setCellValueFactory(new PropertyValueFactory<RefMatDataModel, String>("oneSigmaPCT"));
        pctCol.setSortable(false);
        pctCol.setCellFactory(TextFieldTableCell.<RefMatDataModel>forTableColumn());
        pctCol.setOnEditCommit(value -> {
            if (isNumeric(value.getNewValue())) {
                ObservableList<RefMatDataModel> items = value.getTableView().getItems();
                DataModel mod = items.get(value.getTablePosition().getRow());
                String ratioName = getRatioHiddenName(mod.getName());
                ValueModel valMod = refMatModel.getDatumByName(ratioName);
                BigDecimal newValue = BigDecimal.ZERO;
                if (Double.parseDouble(value.getNewValue()) != 0) {
                    newValue = new BigDecimal(value.getNewValue());
                }
                valMod.setOneSigma(newValue);
                valMod.setUncertaintyType("PCT");
                mod.setValue(refMatDataNotation.format(round(valMod.getValue(), precision)));
                mod.setOneSigmaABS(refMatDataNotation.format(round(valMod.getOneSigmaABS(), precision)));
                mod.setOneSigmaPCT(refMatDataNotation.format(round(valMod.getOneSigmaPCT(), precision)));
                value.getTableView().refresh();
                setUpRefMatCovariancesAndCorrelations();
            } else {
                SquidMessageDialog.showWarningDialog("Invalid Value Entered!", primaryStageWindow);
                value.getTableView().refresh();
            }
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

    private void setUpMolarMasses() {
        molarMassesPane.getChildren().clear();
        molarMasses = new ArrayList<>();
        Map<String, BigDecimal> masses = physConstModel.getMolarMasses();
        String[][] defaultMasses = DataDictionary.AtomicMolarMasses;
        int currY = 0;
        for (String[] mass : defaultMasses) {
            Label lab = new Label(mass[0] + ":");
            molarMassesPane.getChildren().add(lab);
            lab.setLayoutY(currY + 5);
            lab.setLayoutX(10);
            lab.setTextAlignment(TextAlignment.RIGHT);
            lab.setPrefWidth(Region.USE_COMPUTED_SIZE);

            TextField text = new TextField(masses.get(mass[0]).toPlainString());
            molarMassesPane.getChildren().add(text);
            text.setLayoutY(currY);
            text.setLayoutX(80 + lab.getLayoutX());
            text.setPrefWidth(300);
            text.focusedProperty().addListener((obV, ov, nv) -> {
                if (!nv && !isNumeric(text.getText())) {
                    SquidMessageDialog.showWarningDialog("Invalid Molar Mass: must be numeric", primaryStageWindow);
                    text.setText(masses.get(mass[0]).toPlainString());
                }
            });

            molarMasses.add(text);
            currY += 40;
        }
    }

    private void setUpApparentDates() {
        refMatModel.calculateApparentDates();
        apparentDatesTextArea.setText(refMatModel.listFormattedApparentDates());
    }

    private void setUpReferences() {
        referencesPane.getChildren().clear();
        ValueModel[] models = physConstModel.getValues();
        physConstReferences = new ArrayList<>();
        AnchorPane.setRightAnchor(referencesPane, 0.0);
        AnchorPane.setBottomAnchor(referencesPane, 0.0);
        int currHeight = 0;
        for (int i = 0; i < models.length; i++) {
            ValueModel mod = models[i];

            Label lab = new Label(getRatioVisibleName(mod.getName()) + ":");
            referencesPane.getChildren().add(lab);
            lab.setLayoutY(currHeight + 5);
            lab.setLayoutX(10);
            lab.setTextAlignment(TextAlignment.RIGHT);
            lab.setPrefWidth(Region.USE_COMPUTED_SIZE);

            TextField text = new TextField(mod.getReference());
            referencesPane.getChildren().add(text);
            text.setLayoutY(currHeight);
            text.setLayoutX(lab.getLayoutX() + 40);
            text.setPrefWidth(300);

            physConstReferences.add(text);
            currHeight += 40;
        }
    }

    private void setUpPhysConstEditableLabel() {
        if (physConstModel.isEditable()) {
            physConstIsEditableLabel.setText("editable");
        } else {
            physConstIsEditableLabel.setText("not editable");
        }
    }

    private void setUpRefMatEditableLabel() {
        if (refMatModel.isEditable()) {
            refMatIsEditableLabel.setText("editable");
        } else {
            refMatIsEditableLabel.setText("not editable");
        }
    }

    private void setUpPbBlankICEditableLabel() {
        if (pbBlankICModel.isEditable()) {
            pbBlankICIsEditableLabel.setText("editable");
        } else {
            pbBlankICIsEditableLabel.setText("not editable");
        }
    }

    private void setUpPhysConstTextFields() {
        physConstModelName.setText(physConstModel.getModelName());
        physConstLabName.setText(physConstModel.getLabName());
        physConstVersion.setText(physConstModel.getVersion());
        physConstDateCertified.setText(physConstModel.getDateCertified());
    }

    private void setUpRefMatTextFields() {
        refMatModelName.setText(refMatModel.getModelName());
        refMatLabName.setText(refMatModel.getLabName());
        refMatVersion.setText(refMatModel.getVersion());
        refMatDateCertified.setText(refMatModel.getDateCertified());
    }

    private void setUpPbBlankICTextFields() {
        pbBlankICModelName.setText(pbBlankICModel.getModelName());
        pbBlankICLabName.setText(pbBlankICModel.getLabName());
        pbBlankICVersion.setText(pbBlankICModel.getVersion());
        pbBlankICDateCertified.setText(pbBlankICModel.getDateCertified());
        pbBlankICCommentsArea.setText(pbBlankICModel.getComments());
        pbBlankICReferencesArea.setText(pbBlankICModel.getReferences());
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

    public static String getModVersionName(ParametersModel mod) {
        return mod.getModelName() + " v." + mod.getVersion();
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
            file = FileHandler.parametersManagerSelectPhysicalConstantsXMLFile(primaryStageWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), primaryStageWindow);
        }
        if (file != null) {
            PhysicalConstantsModel importedMod = (PhysicalConstantsModel) physConstModel.readXMLObject(file.getAbsolutePath(), false);
            physConstModels.add(importedMod);
            physConstCB.getItems().add(getModVersionName(importedMod));
            physConstCB.getSelectionModel().selectLast();
            physConstModel = importedMod;
            setUpPhysConst();
            squidLabData.storeState();
        }
        squidLabDataStage.requestFocus();
    }

    @FXML
    private void physConstExpXMLAction(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.parametersManagerSavePhysicalConstantsXMLFile(physConstModel, primaryStageWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), primaryStageWindow);
        }
        if (file != null) {
            physConstModel.serializeXMLObject(file.getAbsolutePath());
        }
        squidLabDataStage.requestFocus();
    }

    @FXML
    private void refMatExpXMLAction(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.parametersManagerSaveReferenceMaterialXMLFile(refMatModel, primaryStageWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), primaryStageWindow);
        }
        if (file != null) {
            refMatModel.serializeXMLObject(file.getAbsolutePath());
        }
        squidLabDataStage.requestFocus();
    }

    @FXML
    private void refMatImpXMLAction(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.parametersManagerSelectReferenceMaterialXMLFile(primaryStageWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), primaryStageWindow);
        }
        if (file != null) {
            ReferenceMaterial importedMod = (ReferenceMaterial) refMatModel.readXMLObject(file.getAbsolutePath(), false);
            refMatModels.add(importedMod);
            refMatCB.getItems().add(getModVersionName(importedMod));
            refMatCB.getSelectionModel().selectLast();
            refMatModel = importedMod;
            squidLabData.storeState();
        }
        squidLabDataStage.requestFocus();
    }

    @FXML
    private void importETReduxPhysicalConstantsModel(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.parametersManagerSelectPhysicalConstantsXMLFile(primaryStageWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), primaryStageWindow);
        }
        if (file != null) {
            PhysicalConstantsModel importedMod = PhysicalConstantsModel.getPhysicalConstantsModelFromETReduxXML(file);
            importedMod.setIsEditable(true);
            physConstModels.add(importedMod);
            physConstCB.getItems().add(getModVersionName(importedMod));
            physConstCB.getSelectionModel().selectLast();
            physConstModel = importedMod;
            setUpPhysConst();
            squidLabData.storeState();
        }
        squidLabDataStage.requestFocus();
    }

    @FXML
    private void importETReduxReferenceMaterial(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.parametersManagerSelectReferenceMaterialXMLFile(primaryStageWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), primaryStageWindow);
        }
        if (file != null) {
            ReferenceMaterial importedMod = ReferenceMaterial.getReferenceMaterialFromETReduxXML(file);
            importedMod.setIsEditable(true);
            refMatModels.add(importedMod);
            refMatCB.getItems().add(getModVersionName(importedMod));
            refMatCB.getSelectionModel().selectLast();
            refMatModel = importedMod;
            squidLabData.storeState();
        }
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
        }

        refMatModelName.setEditable(isEditable);
        refMatLabName.setEditable(isEditable);
        refMatVersion.setEditable(isEditable);
        refMatDateCertified.setEditable(isEditable);

        refMatDataTable.setEditable(isEditable);
        refMatDataTable.getColumns().get(0).setEditable(false);
        ObservableList<RefMatDataModel> refMatData = refMatDataTable.getItems();
        for (RefMatDataModel mod : refMatData) {
            if (!isEditable) {
                mod.getIsMeasured().setDisable(true);
                mod.getIsMeasured().setStyle("-fx-opacity: 1");
            } else {
                mod.getIsMeasured().setDisable(false);
            }
        }

        refMatConcentrationsTable.setEditable(isEditable);
        refMatConcentrationsTable.getColumns().get(0).setEditable(false);

        refMatCorrTable.setEditable(isEditable);
        refMatCorrTable.getColumns().get(0).setEditable(false);

        refMatCommentsArea.setEditable(isEditable);
        refMatReferencesArea.setEditable(isEditable);
    }

    private void pbBlankICModelEditable(boolean isEditable) {
        if (isEditable) {
            pbBlankICIsEditableLabel.setText("editing");
        }

        pbBlankICModelName.setEditable(isEditable);
        pbBlankICLabName.setEditable(isEditable);
        pbBlankICVersion.setEditable(isEditable);
        pbBlankICDateCertified.setEditable(isEditable);

        pbBlankICDataTable.setEditable(isEditable);
        pbBlankICDataTable.getColumns().get(0).setEditable(false);

        pbBlankICCorrTable.setEditable(isEditable);
        pbBlankICCorrTable.getColumns().get(0).setEditable(false);

        pbBlankICCommentsArea.setEditable(isEditable);
        pbBlankICReferencesArea.setEditable(isEditable);
    }

    private void setUpRefMatMenuItems(boolean isEditing, boolean isEditable) {
        refMatFileMenu.setDisable(isEditing);
        saveAndRegCurrRefMat.setDisable(!isEditing);
        remCurrRefMat.setDisable(!isEditable || isEditing);
        canEditOfRefMat.setDisable(!isEditing);
        editNewEmptyRefMat.setDisable(isEditing);
        editCopyOfCurrRefMat.setDisable(isEditing);
        editCurrRefMat.setDisable(!isEditable || isEditing);
        refMatCB.setDisable(isEditing);
    }

    private void setUpPhysConstMenuItems(boolean isEditing, boolean isEditable) {
        physConstFileMenu.setDisable(isEditing);
        saveAndRegCurrPhysConst.setDisable(!isEditing);
        remCurrPhysConst.setDisable(!isEditable || isEditing);
        cancelEditOfPhysConst.setDisable(!isEditing);
        editNewEmpPhysConst.setDisable(isEditing);
        editCopyOfCurrPhysConst.setDisable(isEditing);
        editCurrPhysConst.setDisable(!isEditable || isEditing);
        physConstCB.setDisable(isEditing);
    }

    private void setUpPbBlankICMenuItems(boolean isEditing, boolean isEditable) {
        pbBlankICFileMenu.setDisable(isEditing);
        saveAndRegCurrBlankICIsEditableMenuItem.setDisable(!isEditing);
        remCurrPbBlankIC.setDisable(!isEditable || isEditing);
        cancelEditOfPbBlankIC.setDisable(!isEditing);
        editNewEmptyPbBlankIC.setDisable(isEditing);
        editCopyOfPbBlankIC.setDisable(isEditing);
        editCurrPbBlankIC.setDisable(!isEditable || isEditing);
        pbBlankICCB.setDisable(isEditing);
    }

    @FXML
    private void physConstRemoveCurrMod(ActionEvent event) {
        physConstModels.remove(physConstModel);
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
        String selected = physConstCB.getSelectionModel().getSelectedItem();
        setUpPhysConstCBItems();
        physConstCB.getSelectionModel().select(selected);
        physConstEditable(false);
        setUpPhysConstMenuItems(false, physConstModel.isEditable());
        isEditingPhysConst = false;
    }

    @FXML
    private void physConstSaveAndRegisterEdit(ActionEvent event) {
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
        setUpPhysConstCBItems();
        physConstCB.getSelectionModel().select(getModVersionName(physConstModel));
        physConstEditable(false);
        setUpPhysConstMenuItems(false, physConstModel.isEditable());
        squidLabData.storeState();
        isEditingPhysConst = false;
    }

    @FXML
    private void refMatSaveAndRegisterEdit(ActionEvent event) {
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
        refMatModel.setDataMeasured(isMeasures);

        refMatModel.setReferences(refMatReferencesArea.getText());
        refMatModel.setComments(refMatCommentsArea.getText());

        if (!isEditingCurrRefMat) {
            refMatModels.add(refMatModel);
        } else {
            isEditingCurrRefMat = false;
            refMatHolder = null;
        }
        setUpRefMatCBItems();
        refMatCB.getSelectionModel().select(getModVersionName(refMatModel));
        refMatEditable(false);
        setUpRefMatMenuItems(false, refMatModel.isEditable());
        squidLabData.storeState();
        isEditingRefMat = false;
    }

    @FXML
    private void refMatRemoveCurrMod(ActionEvent event) {
        refMatModels.remove(refMatModel);
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
        String selected = refMatCB.getSelectionModel().getSelectedItem();
        setUpRefMatCBItems();
        refMatCB.getSelectionModel().select(selected);
        refMatEditable(false);
        setUpRefMatMenuItems(false, refMatModel.isEditable());
        isEditingRefMat = false;
    }

    @FXML
    private void refMateEditEmptyMod(ActionEvent event) {
        refMatModel = new ReferenceMaterial();
        setUpRefMat();
        refMatEditable(true);
        setUpRefMatMenuItems(true, true);
        isEditingRefMat = true;
    }

    @FXML
    private void refMatEditCopy(ActionEvent event) {
        refMatModel = refMatModel.clone();
        refMatModel.setModelName(refMatModel.getModelName() + " - copy");
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

    public static DecimalFormat getScientificNotationFormat() {
        return new DecimalFormat("0.0##############################E0#############");
    }

    public static DecimalFormat getStandardNotationFormat() {
        return new DecimalFormat("########################0.0######################################");
    }

    @FXML
    private void physConstDataNotationOnAction(ActionEvent event) {
        if (physConstDataNotation.equals(getScientificNotationFormat())) {
            physConstDataNotation = getStandardNotationFormat();
            physConstDataNotationButton.setText("Standard Notation");
        } else {
            physConstDataNotation = getScientificNotationFormat();
            physConstDataNotationButton.setText("Scientific Notation");
        }
        ObservableList<DataModel> models = physConstDataTable.getItems();
        for (int i = 0; i < models.size(); i++) {
            DataModel mod = models.get(i);
            BigDecimal bigDec;
            int precision = getPrecisionValue(physConstDataSigFigs.getText());

            bigDec = new BigDecimal(mod.getValue());
            mod.setValue(physConstDataNotation.format(round(bigDec, precision)));

            bigDec = new BigDecimal(mod.getOneSigmaABS());
            mod.setOneSigmaABS(physConstDataNotation.format(round(bigDec, precision)));

            bigDec = new BigDecimal(mod.getOneSigmaPCT());
            mod.setOneSigmaPCT(physConstDataNotation.format(round(bigDec, precision)));
        }
        physConstDataTable.getColumns().clear();
        List<TableColumn<DataModel, String>> columns = getDataModelColumns(physConstDataTable, physConstDataNotation,
                getPrecisionValue(physConstDataSigFigs.getText()));
        for (TableColumn<DataModel, String> col : columns) {
            physConstDataTable.getColumns().add(col);
        }
        physConstDataTable.refresh();
    }

    @FXML
    private void refMatDataNotationOnAction(ActionEvent event) {
        if (refMatDataNotation.equals(getScientificNotationFormat())) {
            refMatDataNotation = getStandardNotationFormat();
            refMatDataNotationButton.setText("Standard Notation");
        } else {
            refMatDataNotation = getScientificNotationFormat();
            refMatDataNotationButton.setText("Scientific Notation");
        }
        ObservableList<RefMatDataModel> models = refMatDataTable.getItems();
        for (int i = 0; i < models.size(); i++) {
            DataModel mod = models.get(i);
            BigDecimal bigDec;
            int precision = getPrecisionValue(refMatDataSigFigs.getText());

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
        if (refMatConcentrationsNotation.equals(getScientificNotationFormat())) {
            refMatConcentrationsNotation = getStandardNotationFormat();
            refMatConcentrationsNotationButton.setText("Standard Notation");
        } else {
            refMatConcentrationsNotation = getScientificNotationFormat();
            refMatConcentrationsNotationButton.setText("Scientific Notation");
        }
        ObservableList<DataModel> models = refMatConcentrationsTable.getItems();
        for (int i = 0; i < models.size(); i++) {
            DataModel mod = models.get(i);
            BigDecimal bigDec;
            int precision = getPrecisionValue(refMatConcSigFigs.getText());

            bigDec = new BigDecimal(mod.getValue());
            mod.setValue(refMatConcentrationsNotation.format(round(bigDec, precision)));

            bigDec = new BigDecimal(mod.getOneSigmaABS());
            mod.setOneSigmaABS(refMatConcentrationsNotation.format(round(bigDec, precision)));

            bigDec = new BigDecimal(mod.getOneSigmaPCT());
            mod.setOneSigmaPCT(refMatConcentrationsNotation.format(round(bigDec, precision)));
        }
        refMatConcentrationsTable.getColumns().setAll(getDataModelColumns(refMatConcentrationsTable, refMatConcentrationsNotation,
                getPrecisionValue(refMatConcSigFigs.getText())));

        refMatConcentrationsTable.refresh();
    }

    @FXML
    private void physConstCorrNotationOnAction(ActionEvent event) {
        if (physConstCorrNotation.equals(getScientificNotationFormat())) {
            physConstCorrNotation = getStandardNotationFormat();
            physConstCorrNotationButton.setText("Standard Notation");
        } else {
            physConstCorrNotation = getScientificNotationFormat();
            physConstCorrNotationButton.setText("Scientific Notation");
        }
        int precision = getPrecisionValue(physConstCorrSigFigs.getText());
        corrCovPrecisionOrNotationAction(physConstModel, physConstCorrTable, precision, physConstCorrNotation);
    }

    @FXML
    private void physConstCovNotationOnAction(ActionEvent event) {
        if (physConstCovNotation.equals(getScientificNotationFormat())) {
            physConstCovNotation = getStandardNotationFormat();
            physConstCovNotationButton.setText("Standard Notation");
        } else {
            physConstCovNotation = getScientificNotationFormat();
            physConstCovNotationButton.setText("Scientific Notation");
        }
        int precision = getPrecisionValue(physConstCovSigFigs.getText());
        corrCovPrecisionOrNotationAction(physConstModel, physConstCovTable, precision, physConstCovNotation);
    }

    @FXML
    private void refMatCorrNotationOnAction(ActionEvent event) {
        if (refMatCorrNotation.equals(getScientificNotationFormat())) {
            refMatCorrNotation = getStandardNotationFormat();
            refMatCorrNotationButton.setText("Standard Notation");
        } else {
            refMatCorrNotation = getScientificNotationFormat();
            refMatCorrNotationButton.setText("Scientific Notation");
        }
        int precision = getPrecisionValue(refMatCorrSigFigs.getText());
        corrCovPrecisionOrNotationAction(refMatModel, refMatCorrTable, precision, refMatCorrNotation);
    }

    @FXML
    private void refMatCovNotationOnAction(ActionEvent event) {
        if (refMatCovNotation.equals(getScientificNotationFormat())) {
            refMatCovNotation = getStandardNotationFormat();
            refMatCovNotationButton.setText("Standard Notation");
        } else {
            refMatCovNotation = getScientificNotationFormat();
            refMatCovNotationButton.setText("Scientific Notation");
        }
        int precision = getPrecisionValue(refMatCovSigFigs.getText());
        corrCovPrecisionOrNotationAction(refMatModel, refMatCovTable, precision, refMatCovNotation);
    }

    private void setUpSigFigTextFields() {
        refMatDataSigFigs.setOnKeyReleased(value -> {
            ObservableList<RefMatDataModel> items = refMatDataTable.getItems();
            String text = refMatDataSigFigs.getText();
            if (!text.trim().isEmpty() && isNumeric(text)) {
                int precision = getPrecisionValue(text);
                for (RefMatDataModel mod : items) {
                    ValueModel valMod = refMatModel.getDatumByName(getRatioHiddenName(mod.getName()));
                    mod.setOneSigmaABS(refMatDataNotation.format(round(valMod.getOneSigmaABS(), precision)));
                    mod.setOneSigmaPCT(refMatDataNotation.format(round(valMod.getOneSigmaPCT(), precision)));
                    mod.setValue(refMatDataNotation.format(round(valMod.getValue(), precision)));
                }
                setUpRefMatDataModelColumns();
                refMatDataTable.refresh();
            }
        });

        refMatConcSigFigs.setOnKeyReleased(value -> {
            ObservableList<DataModel> items = refMatConcentrationsTable.getItems();
            String text = refMatConcSigFigs.getText();
            if (!text.trim().isEmpty() && isNumeric(text)) {
                int precision = getPrecisionValue(text);
                for (DataModel mod : items) {
                    ValueModel valMod = refMatModel.getConcentrationByName(getRatioHiddenName(mod.getName()));
                    mod.setOneSigmaABS(refMatConcentrationsNotation.format(round(valMod.getOneSigmaABS(), precision)));
                    mod.setOneSigmaPCT(refMatConcentrationsNotation.format(round(valMod.getOneSigmaPCT(), precision)));
                    mod.setValue(refMatConcentrationsNotation.format(round(valMod.getValue(), precision)));
                }
                refMatConcentrationsTable.getColumns().setAll(getDataModelColumns(refMatConcentrationsTable, refMatConcentrationsNotation, precision));
                refMatConcentrationsTable.refresh();
            }
        });

        physConstDataSigFigs.setOnKeyReleased(value -> {
            ObservableList<DataModel> items = physConstDataTable.getItems();
            String text = physConstDataSigFigs.getText();
            if (!text.trim().isEmpty() && isNumeric(text)) {
                int precision = getPrecisionValue(text);
                for (DataModel mod : items) {
                    ValueModel valMod = physConstModel.getDatumByName(getRatioHiddenName(mod.getName()));
                    mod.setOneSigmaABS(physConstDataNotation.format(round(valMod.getOneSigmaABS(), precision)));
                    mod.setOneSigmaPCT(physConstDataNotation.format(round(valMod.getOneSigmaPCT(), precision)));
                    mod.setValue(physConstDataNotation.format(round(valMod.getValue(), precision)));
                }
                physConstDataTable.getColumns().setAll(getDataModelColumns(physConstDataTable, physConstDataNotation, precision));
                physConstDataTable.refresh();
            }
        });

        physConstCorrSigFigs.setOnKeyReleased(value -> {
            String text = physConstCorrSigFigs.getText();
            if (!text.trim().isEmpty() && isNumeric(text)) {
                int precision = getPrecisionValue(text);
                corrCovPrecisionOrNotationAction(physConstModel, physConstCorrTable, precision, physConstCorrNotation);
            }
        });

        physConstCovSigFigs.setOnKeyReleased(value -> {
            String text = physConstCovSigFigs.getText();
            if (!text.trim().isEmpty() && isNumeric(text)) {
                int precision = getPrecisionValue(text);
                corrCovPrecisionOrNotationAction(physConstModel, physConstCovTable, precision, physConstCovNotation);
            }
        });

        refMatCorrSigFigs.setOnKeyReleased(value -> {
            String text = refMatCorrSigFigs.getText();
            if (!text.trim().isEmpty() && isNumeric(text)) {
                int precision = getPrecisionValue(text);
                corrCovPrecisionOrNotationAction(refMatModel, refMatCorrTable, precision, refMatCorrNotation);
            }
        });

        refMatCovSigFigs.setOnKeyReleased(value -> {
            String text = refMatCovSigFigs.getText();
            if (!text.trim().isEmpty() && isNumeric(text)) {
                int precision = getPrecisionValue(text);
                corrCovPrecisionOrNotationAction(refMatModel, refMatCovTable, precision, refMatCovNotation);
            }
        });

        pbBlankICDataSigFigs.setOnKeyReleased(value -> {
            ObservableList<DataModel> items = pbBlankICDataTable.getItems();
            String text = pbBlankICDataSigFigs.getText();
            if (!text.trim().isEmpty() && isNumeric(text)) {
                int precision = getPrecisionValue(text);
                for (DataModel mod : items) {
                    ValueModel valMod = pbBlankICModel.getDatumByName(getRatioHiddenName(mod.getName()));
                    mod.setOneSigmaABS(pbBlankICDataNotation.format(round(valMod.getOneSigmaABS(), precision)));
                    mod.setOneSigmaPCT(pbBlankICDataNotation.format(round(valMod.getOneSigmaPCT(), precision)));
                    mod.setValue(pbBlankICDataNotation.format(round(valMod.getValue(), precision)));
                }
                pbBlankICDataTable.getColumns().setAll(getDataModelColumns(pbBlankICDataTable, pbBlankICDataNotation, precision));
                pbBlankICDataTable.refresh();
            }
        });

        pbBlankICCorrSigFigs.setOnKeyReleased(value -> {
            String text = refMatCovSigFigs.getText();
            if (!text.trim().isEmpty() && isNumeric(text)) {
                int precision = getPrecisionValue(text);
                corrCovPrecisionOrNotationAction(pbBlankICModel, pbBlankICCorrTable, precision, pbBlankICCorrNotation);
            }
        });

        pbBlankICCovSigFigs.setOnKeyReleased(value -> {
            String text = refMatCovSigFigs.getText();
            if (!text.trim().isEmpty() && isNumeric(text)) {
                int precision = getPrecisionValue(text);
                corrCovPrecisionOrNotationAction(pbBlankICModel, pbBlankICCovTable, precision, pbBlankICCovNotation);
            }
        });
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

    private int getPrecisionValue(String text) {
        int retVal = 6;
        if (!text.trim().isEmpty() && isNumeric(text)) {
            retVal = Integer.parseInt(text);
        }
        return retVal;
    }

    @FXML
    private void pbBlankICImpXMLAction(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.parametersManagerSelectPbBlankICModelXMLFile(primaryStageWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), primaryStageWindow);
        }
        if (file != null) {
            CommonPbModel importedMod = (CommonPbModel) pbBlankICModel.readXMLObject(file.getAbsolutePath(), false);
            importedMod.setIsEditable(true);
            pbBlankICModels.add(importedMod);
            pbBlankICCB.getItems().add(getModVersionName(importedMod));
            pbBlankICCB.getSelectionModel().selectLast();
            pbBlankICModel = importedMod;
            setUpPbBlankIC();
            squidLabData.storeState();
        }
        squidLabDataStage.requestFocus();
    }

    @FXML
    private void pbBlankICExpXMLAction(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.parametersManagerSavePbBlankICModelXMLFile(pbBlankICModel, primaryStageWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), primaryStageWindow);
        }
        if (file != null) {
            pbBlankICModel.serializeXMLObject(file.getAbsolutePath());
        }
        squidLabDataStage.requestFocus();
    }

    @FXML
    private void pbBlankICSaveAndRegisterEdit(ActionEvent event) {
        pbBlankICModel.setIsEditable(true);
        pbBlankICModel.setModelName(pbBlankICModelName.getText());
        pbBlankICModel.setLabName(pbBlankICLabName.getText());
        pbBlankICModel.setVersion(pbBlankICVersion.getText());
        pbBlankICModel.setDateCertified(pbBlankICDateCertified.getText());

        pbBlankICModel.setReferences(pbBlankICReferencesArea.getText());
        pbBlankICModel.setComments(pbBlankICCommentsArea.getText());

        if (!isEditingCurrPbBlankICModel) {
            pbBlankICModels.add(pbBlankICModel);
        } else {
            isEditingCurrPbBlankICModel = false;
            pbBlankICModelHolder = null;
        }
        isEditingPbBlankIC = false;
        setUpPbBlankICCBItems();
        pbBlankICCB.getSelectionModel().select(getModVersionName(pbBlankICModel));
        pbBlankICModelEditable(false);
        setUpPbBlankICMenuItems(false, pbBlankICModel.isEditable());
        squidLabData.storeState();
    }

    @FXML
    private void pbBlankICCancelEdit(ActionEvent event) {
        if (isEditingCurrPbBlankICModel) {
            isEditingCurrPbBlankICModel = false;
            pbBlankICModel = pbBlankICModelHolder;
            pbBlankICModelHolder = null;
        }
        isEditingPbBlankIC = false;
        String selected = pbBlankICCB.getSelectionModel().getSelectedItem();
        setUpPbBlankICCBItems();
        pbBlankICCB.getSelectionModel().select(selected);
        pbBlankICModelEditable(false);
        setUpPbBlankICMenuItems(false, pbBlankICModel.isEditable());
    }

    @FXML
    private void pbBlankICRemoveCurrMod(ActionEvent event) {
        pbBlankICModels.remove(pbBlankICModel);
        setUpPbBlankICCBItems();
        pbBlankICCB.getSelectionModel().selectFirst();
        pbBlankICModelEditable(false);
        setUpPbBlankICMenuItems(false, pbBlankICModel.isEditable());
        squidLabData.storeState();
    }

    @FXML
    private void pbBlankICEditCurrMod(ActionEvent event) {
        isEditingPbBlankIC = true;
        pbBlankICModelHolder = pbBlankICModel.clone();
        pbBlankICModelEditable(true);
        setUpPbBlankICMenuItems(true, true);
        isEditingCurrPbBlankICModel = true;
    }

    @FXML
    private void pbBlankICEditCopy(ActionEvent event) {
        isEditingPbBlankIC = true;
        pbBlankICModel = pbBlankICModel.clone();
        pbBlankICModel.setModelName(pbBlankICModel.getModelName() + " - copy");
        setUpPbBlankIC();
        pbBlankICModelEditable(true);
        setUpPbBlankICMenuItems(true, true);
    }

    @FXML
    private void pbBlankICEditEmptyMod(ActionEvent event) {
        isEditingPbBlankIC = true;
        pbBlankICModel = new CommonPbModel();
        setUpPbBlankIC();
        pbBlankICModelEditable(true);
        setUpPbBlankICMenuItems(true, true);
    }

    @FXML
    private void pbBlankICDataNotationOnAction(ActionEvent event) {
        if (pbBlankICDataNotation.equals(getScientificNotationFormat())) {
            pbBlankICDataNotation = getStandardNotationFormat();
            pbBlankICDataNotationButton.setText("Standard Notation");
        } else {
            pbBlankICDataNotation = getScientificNotationFormat();
            pbBlankICDataNotationButton.setText("Scientific Notation");
        }
        ObservableList<DataModel> models = pbBlankICDataTable.getItems();
        for (int i = 0; i < models.size(); i++) {
            DataModel mod = models.get(i);
            BigDecimal bigDec;
            int precision = getPrecisionValue(pbBlankICDataSigFigs.getText());

            bigDec = new BigDecimal(mod.getValue());
            mod.setValue(pbBlankICDataNotation.format(round(bigDec, precision)));

            bigDec = new BigDecimal(mod.getOneSigmaABS());
            mod.setOneSigmaABS(pbBlankICDataNotation.format(round(bigDec, precision)));

            bigDec = new BigDecimal(mod.getOneSigmaPCT());
            mod.setOneSigmaPCT(pbBlankICDataNotation.format(round(bigDec, precision)));
        }
        pbBlankICDataTable.getColumns().setAll(getDataModelColumns(pbBlankICDataTable, pbBlankICDataNotation,
                getPrecisionValue(pbBlankICDataSigFigs.getText())));

        pbBlankICDataTable.refresh();
    }

    @FXML
    private void pbBlankICCorrNotationOnAction(ActionEvent event) {
        if (pbBlankICCorrNotation.equals(getScientificNotationFormat())) {
            pbBlankICCorrNotation = getStandardNotationFormat();
            pbBlankICCorrNotationButton.setText("Standard Notation");
        } else {
            pbBlankICCorrNotation = getScientificNotationFormat();
            pbBlankICCorrNotationButton.setText("Scientific Notation");
        }
        int precision = getPrecisionValue(pbBlankICCorrSigFigs.getText());
        corrCovPrecisionOrNotationAction(pbBlankICModel, pbBlankICCorrTable, precision, pbBlankICCorrNotation);
    }

    @FXML
    private void pbBlankICCovNotationOnAction(ActionEvent event) {
        if (pbBlankICCovNotation.equals(getScientificNotationFormat())) {
            pbBlankICCovNotation = getStandardNotationFormat();
            pbBlankICCovNotationButton.setText("Standard Notation");
        } else {
            pbBlankICCovNotation = getScientificNotationFormat();
            pbBlankICCovNotationButton.setText("Scientific Notation");
        }
        int precision = getPrecisionValue(pbBlankICCovSigFigs.getText());
        corrCovPrecisionOrNotationAction(pbBlankICModel, pbBlankICCovTable, precision, pbBlankICCovNotation);
    }

    @FXML
    private void pbBlankICImpETReduxXMLAction(ActionEvent event) {
        File file = null;
        try {
            file = FileHandler.parametersManagerSelectPbBlankICModelXMLFile(primaryStageWindow);
        } catch (IOException e) {
            SquidMessageDialog.showWarningDialog(e.getMessage(), primaryStageWindow);
        }
        if (file != null) {
            CommonPbModel importedMod = CommonPbModel.getPbBlankICModelFromETReduxXML(file);
            importedMod.setIsEditable(true);
            pbBlankICModels.add(importedMod);
            pbBlankICCB.getItems().add(getModVersionName(importedMod));
            pbBlankICCB.getSelectionModel().selectLast();
            pbBlankICModel = importedMod;
            setUpPbBlankIC();
            squidLabData.storeState();
        }
        squidLabDataStage.requestFocus();
    }

    private void setUpApparentDatesTabSelection() {
        apparentDatesTab.setOnSelectionChanged(value -> {
            if (apparentDatesTab.isSelected() && isEditingRefMat) {
                setUpApparentDates();
            }
        });
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
