/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui.parameters;

//import com.sun.javafx.scene.control.skin.TableHeaderRow;
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
import java.util.Map.Entry;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.control.Menu;
import javafx.scene.text.TextAlignment;
import org.cirdles.squid.dialogs.SquidMessageDialog;
import static org.cirdles.squid.gui.SquidUI.primaryStageWindow;
import static org.cirdles.squid.gui.SquidUIController.squidLabData;
import static org.cirdles.squid.gui.parameters.ParametersLauncher.squidLabDataStage;
import org.cirdles.squid.gui.utilities.fileUtilities.FileHandler;
import org.cirdles.squid.parameters.matrices.AbstractMatrixModel;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
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
    PhysicalConstantsModel physConstModel;
    PhysicalConstantsModel physConstHolder;

    ReferenceMaterial refMatModel;
    ReferenceMaterial refMatHolder;

    List<PhysicalConstantsModel> physConstModels;
    List<ReferenceMaterial> refMatModels;

    List<TextField> physConstReferences;
    List<TextField> molarMasses;

    private boolean isEditingCurrPhysConst;
    private boolean isEditingCurrRefMat;

    private DecimalFormat physConstDataNotation;
    private DecimalFormat physConstCorrNotation;
    private DecimalFormat physConstCovNotation;

    private DecimalFormat refMatDataNotation;
    private DecimalFormat refMatConcentrationsNotation;
    private DecimalFormat refMatCorrNotation;
    private DecimalFormat refMatCovNotation;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        physConstDataNotation = getScientificNotationFormat();
        physConstCorrNotation = getScientificNotationFormat();
        physConstCovNotation = getScientificNotationFormat();

        refMatDataNotation = getScientificNotationFormat();
        refMatConcentrationsNotation = getScientificNotationFormat();
        refMatCorrNotation = getScientificNotationFormat();
        refMatCovNotation = getScientificNotationFormat();

        physConstModels = squidLabData.getPhysicalConstantsModels();
        physConstModel = physConstModels.get(0);
        setUpPhysConstCB();

        refMatModels = squidLabData.getReferenceMaterials();
        refMatModel = refMatModels.get(0);
        setUpRefMatCB();

//        disableTableColumReordering();
        setUpLaboratoryName();
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

    private void setUpPhysConstCov() {
        initializeTableWithObList(physConstCovTable,
                getObListFromMatrix(physConstModel.getCovModel()), physConstCovNotation, physConstModel);
    }

    private void setUpPhysConstCorr() {
        initializeTableWithObList(physConstCorrTable,
                getObListFromMatrix(physConstModel.getCorrModel()), physConstCorrNotation, physConstModel);
    }

    private void setUpRefMatCov() {
        initializeTableWithObList(refMatCovTable,
                getObListFromMatrix(refMatModel.getCovModel()), refMatCovNotation, refMatModel);
    }

    private void setUpRefMatCorr() {
        initializeTableWithObList(refMatCorrTable,
                getObListFromMatrix(refMatModel.getCorrModel()), refMatCorrNotation, refMatModel);
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
            Iterator<Entry<Integer, String>> rowIterator = matrix.getRows().entrySet().iterator();
            for (int i = 0; i < matrixArray.length; i++) {
                ObservableList<SimpleStringProperty> row = FXCollections.observableArrayList();
                row.add(new SimpleStringProperty(rowIterator.next().getValue()));
                for (int j = 0; j < matrixArray[0].length; j++) {
                    row.add(new SimpleStringProperty(Double.toString(matrixArray[i][j])));
                }
                obList.add(row);
            }
        }
        return obList;
    }

    private void initializeTableWithObList(TableView<ObservableList<SimpleStringProperty>> table,
            ObservableList<ObservableList<SimpleStringProperty>> obList, DecimalFormat format, ParametersModel model) {
        if (obList.size() > 0) {
            ObservableList<SimpleStringProperty> cols = obList.remove(0);
            table.getColumns().clear();
            TableColumn<ObservableList<SimpleStringProperty>, String> rowHead
                    = new TableColumn<>(getRatioVisibleName(cols.get(0).get()));
            final int rowHeadNum = 0;
            rowHead.setSortable(false);
            rowHead.setCellFactory(TextFieldTableCell.<ObservableList<SimpleStringProperty>>forTableColumn());
            rowHead.setCellValueFactory(param
                    -> new ReadOnlyObjectWrapper<String>(getRatioVisibleName(param.getValue().get(rowHeadNum).get())));
            table.getColumns().add(rowHead);
            for (int i = 1; i < cols.size(); i++) {
                TableColumn<ObservableList<SimpleStringProperty>, String> col
                        = new TableColumn<>(getRatioVisibleName(cols.get(i).get()));
                final int colNum = i;
                col.setSortable(false);
                col.setCellValueFactory(param
                        -> new ReadOnlyObjectWrapper<String>(format.format(new BigDecimal(param.getValue().get(colNum).get()))));

                if (table.equals(physConstCorrTable) || table.equals(refMatCorrTable)) {
                    col.setCellFactory(TextFieldTableCell.<ObservableList<SimpleStringProperty>>forTableColumn());
                    col.setOnEditCommit(value -> {
                        if (isNumeric(value.getNewValue()) && Double.parseDouble(value.getNewValue()) <= 1
                                && Double.parseDouble(value.getNewValue()) >= -1) {
                            int rowNum = value.getTablePosition().getRow();
                            BigDecimal newValue = new BigDecimal(value.getNewValue());
                            ObservableList<ObservableList<SimpleStringProperty>> items = value.getTableView().getItems();
                            items.get(rowNum).set(colNum, new SimpleStringProperty(format.format(newValue)));
                            items.get(colNum - 1).set(rowNum + 1, new SimpleStringProperty(format.format(newValue)));
                            String colRatio = getRatioHiddenName(value.getTableColumn().getText());
                            String rowRatio = getRatioHiddenName(items.get(rowNum).get(0).get());
                            String key = "rho" + colRatio.substring(0, 1).toUpperCase() + colRatio.substring(1) + "__" + rowRatio;
                            model.getRhos().put(key, newValue);
                            model.initializeCorrelations();
                            model.generateCovariancesFromCorrelations();
                            if (table.equals(physConstCorrTable)) {
                                setUpPhysConstCov();
                            }
                            if (table.equals(refMatCorrTable)) {
                                setUpRefMatCov();
                            }
                            table.refresh();
                        } else {
                            SquidMessageDialog.showWarningDialog("Value Out of Range or Invalid: Only values"
                                    + " in the range of [-1, 1] are allowed.", primaryStageWindow);
                            table.refresh();
                        }
                    });
                }
                table.getColumns().add(col);
            }
            table.setItems(obList);
            table.refresh();
        }
    }

    private void setUpPhysConstData() {
        physConstDataTable.getColumns().clear();
        List<TableColumn<DataModel, String>> columns = getDataModelColumns(physConstDataTable, physConstDataNotation);
        for (TableColumn<DataModel, String> col : columns) {
            physConstDataTable.getColumns().add(col);
        }
        physConstDataTable.setItems(getDataModelObList(physConstModel.getValues(), physConstDataNotation));
        physConstDataTable.refresh();
    }

    private void setUpRefMatData() {
        refMatDataTable.getColumns().clear();

        TableColumn<RefMatDataModel, String> nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(new PropertyValueFactory<RefMatDataModel, String>("name"));
        nameCol.setSortable(false);
        nameCol.setCellFactory(TextFieldTableCell.<RefMatDataModel>forTableColumn());
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
                mod.setValue(refMatDataNotation.format(valMod.getValue()));
                mod.setOneSigmaABS(refMatDataNotation.format(valMod.getOneSigmaABS()));
                mod.setOneSigmaPCT(refMatDataNotation.format(valMod.getOneSigmaPCT()));
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
                mod.setValue(refMatDataNotation.format(valMod.getValue()));
                mod.setOneSigmaABS(refMatDataNotation.format(valMod.getOneSigmaABS()));
                mod.setOneSigmaPCT(refMatDataNotation.format(valMod.getOneSigmaPCT()));
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
                mod.setValue(refMatDataNotation.format(valMod.getValue()));
                mod.setOneSigmaABS(refMatDataNotation.format(valMod.getOneSigmaABS()));
                mod.setOneSigmaPCT(refMatDataNotation.format(valMod.getOneSigmaPCT()));
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

        final ObservableList<RefMatDataModel> obList = FXCollections.observableArrayList();
        ValueModel[] values = refMatModel.getValues();
        for (int i = 0; i < values.length; i++) {
            ValueModel valMod = values[i];
            Boolean isMeasured = refMatModel.getDataMeasured()[i];
            String value = refMatDataNotation.format(valMod.getValue());
            String oneSigmaABS = refMatDataNotation.format(valMod.getOneSigmaABS());
            String oneSigmaPCT = refMatDataNotation.format(valMod.getOneSigmaPCT());
            RefMatDataModel mod = new RefMatDataModel(getRatioVisibleName(valMod.getName()), value,
                    oneSigmaABS, oneSigmaPCT, isMeasured);
            obList.add(mod);
        }
        refMatDataTable.setItems(obList);
        refMatDataTable.refresh();
    }

    private void setUpConcentrations() {
        refMatConcentrationsTable.getColumns().clear();
        List<TableColumn<DataModel, String>> columns = getDataModelColumns(refMatConcentrationsTable, refMatConcentrationsNotation);
        for (TableColumn<DataModel, String> col : columns) {
            refMatConcentrationsTable.getColumns().add(col);
        }
        refMatConcentrationsTable.setItems(getDataModelObList(refMatModel.getConcentrations(), refMatConcentrationsNotation));
        refMatConcentrationsTable.refresh();
    }

    private List<TableColumn<DataModel, String>> getDataModelColumns(TableView<DataModel> table, DecimalFormat format) {
        List<TableColumn<DataModel, String>> columns = new ArrayList<>();

        TableColumn<DataModel, String> nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(new PropertyValueFactory<DataModel, String>("name"));
        nameCol.setSortable(false);
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
                BigDecimal newValue = BigDecimal.ZERO;
                if (Double.parseDouble(value.getNewValue()) != 0) {
                    newValue = new BigDecimal(value.getNewValue());
                }
                valMod.setValue(newValue);
                mod.setValue(format.format(valMod.getValue()));
                mod.setOneSigmaABS(format.format(valMod.getOneSigmaABS()));
                mod.setOneSigmaPCT(format.format(valMod.getOneSigmaPCT()));
                value.getTableView().refresh();
                if (table.equals(physConstDataTable)) {
                    setUpPhysConstCovariancesAndCorrelations();
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
                BigDecimal newValue = BigDecimal.ZERO;
                if (Double.parseDouble(value.getNewValue()) != 0) {
                    newValue = new BigDecimal(value.getNewValue());
                }
                valMod.setOneSigma(newValue);
                valMod.setUncertaintyType("ABS");
                mod.setValue(format.format(valMod.getValue()));
                mod.setOneSigmaABS(format.format(valMod.getOneSigmaABS()));
                mod.setOneSigmaPCT(format.format(valMod.getOneSigmaPCT()));
                value.getTableView().refresh();
                if (table.equals(physConstDataTable)) {
                    setUpPhysConstCovariancesAndCorrelations();
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
                BigDecimal newValue = BigDecimal.ZERO;
                if (Double.parseDouble(value.getNewValue()) != 0) {
                    newValue = new BigDecimal(value.getNewValue());
                }
                valMod.setOneSigma(newValue);
                valMod.setUncertaintyType("PCT");
                mod.setValue(format.format(valMod.getValue()));
                mod.setOneSigmaABS(format.format(valMod.getOneSigmaABS()));
                mod.setOneSigmaPCT(format.format(valMod.getOneSigmaPCT()));
                value.getTableView().refresh();
                if (table.equals(physConstDataTable)) {
                    setUpPhysConstCovariancesAndCorrelations();
                }
            } else {
                SquidMessageDialog.showWarningDialog("Invalid Value Entered!", primaryStageWindow);
                value.getTableView().refresh();
            }
        });
        columns.add(pctCol);

        return columns;
    }

    private ObservableList<DataModel> getDataModelObList(ValueModel[] values, DecimalFormat numberFormat) {
        final ObservableList<DataModel> obList = FXCollections.observableArrayList();
        for (int i = 0; i < values.length; i++) {
            ValueModel valMod = values[i];
            String value = numberFormat.format(valMod.getValue());
            String oneSigmaABS = numberFormat.format(valMod.getOneSigmaABS());
            String oneSigmaPCT = numberFormat.format(valMod.getOneSigmaPCT());
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
            text.setPrefWidth(600);
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
            text.setPrefWidth(600);

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

        return retVal;
    }

    private static String getRatioHiddenName(String ratio) {
        String retVal = ratio.replaceAll("λ", "lambda");
        retVal = retVal.replaceAll("206-Pb/207-Pb", "r206_207r");
        retVal = retVal.replaceAll("206-Pb/208-Pb", "r206_208r");
        retVal = retVal.replaceAll("206-Pb/238-U", "r206_238r");
        retVal = retVal.replaceAll("208-Pb/232-Th", "r208_232r");
        retVal = retVal.replaceAll("238-U/235-U", "r238_235s");

        return retVal;
    }

//    private void disableTableColumReordering() {
//        disableColumnReordering(physConstDataTable);
//        disableColumnReordering(physConstCorrTable);
//        disableColumnReordering(physConstCovTable);
//
//        disableColumnReordering(refMatDataTable);
//        disableColumnReordering(refMatCorrTable);
//        disableColumnReordering(refMatCovTable);
//        disableColumnReordering(refMatConcentrationsTable);
//    }
//
//    private void disableColumnReordering(TableView table) {
//        table.skinProperty().addListener((obs, oldSkin, newSkin) -> {
//            final TableHeaderRow header = (TableHeaderRow) table.lookup("TableHeaderRow");
//            header.reorderingProperty().addListener(val -> {header.setReordering(false);});
//        });
//    }
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
    }

    @FXML
    private void physConstEditCopy(ActionEvent event) {
        physConstModel = physConstModel.clone();
        setUpPhysConst();
        physConstEditable(true);
        setUpPhysConstMenuItems(true, true);
    }

    @FXML
    private void physConstEditEmptyMod(ActionEvent event) {
        physConstModel = new PhysicalConstantsModel();
        setUpPhysConst();
        physConstEditable(true);
        setUpPhysConstMenuItems(true, true);
    }

    @FXML
    private void physConstCancelEdit(ActionEvent event) {
        if (isEditingCurrPhysConst) {
            physConstModel = physConstHolder;
        }
        if (physConstCB.getSelectionModel().getSelectedIndex() != 0) {
            physConstCB.getSelectionModel().selectFirst();
        } else {
            physConstModel = physConstModels.get(0);
            setUpPhysConst();
        }

        physConstEditable(false);
        setUpPhysConstMenuItems(false, physConstModel.isEditable());
    }

    @FXML
    private void physConstSaveAndRegisterEdit(ActionEvent event) {
        physConstModel.setIsEditable(true);
        physConstModel.setModelName(physConstModelName.getText());
        physConstModel.setVersion(physConstVersion.getText());
        physConstModel.setDateCertified(physConstDateCertified.getText());
        physConstModel.setLabName(physConstLabName.getText());

        ObservableList<DataModel> dataModels = physConstDataTable.getItems();
        ValueModel[] values = new ValueModel[dataModels.size()];
        for (int i = 0; i < values.length; i++) {
            DataModel mod = dataModels.get(i);
            ValueModel currVal = new ValueModel();

            currVal.setName(getRatioHiddenName(mod.getName()));

            String currBigDec = mod.getValue();
            if (Double.parseDouble(currBigDec) == 0) {
                currVal.setValue(BigDecimal.ZERO);
            } else {
                currVal.setValue(new BigDecimal(currBigDec));
            }

            currBigDec = mod.getOneSigmaABS();
            if (Double.parseDouble(currBigDec) == 0) {
                currVal.setOneSigma(BigDecimal.ZERO);
            } else {
                currVal.setOneSigma(new BigDecimal(currBigDec));
            }

            currVal.setReference(physConstReferences.get(i).getText());
            currVal.setUncertaintyType("ABS");
            values[i] = currVal;
        }
        physConstModel.setValues(values);

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
        physConstModel.setRhos(getRhosFromTable(physConstCorrTable));

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
    }

    @FXML
    private void refMatSaveAndRegisterEdit(ActionEvent event) {
        refMatModel.setIsEditable(true);
        refMatModel.setModelName(refMatModelName.getText());
        refMatModel.setLabName(refMatLabName.getText());
        refMatModel.setVersion(refMatVersion.getText());
        refMatModel.setDateCertified(refMatDateCertified.getText());

        ObservableList<RefMatDataModel> dataModels = refMatDataTable.getItems();
        ValueModel[] values = new ValueModel[dataModels.size()];
        boolean[] isMeasures = new boolean[dataModels.size()];
        for (int i = 0; i < values.length; i++) {
            RefMatDataModel mod = dataModels.get(i);
            ValueModel currVal = new ValueModel();

            currVal.setName(getRatioHiddenName(mod.getName()));

            String currBigDec = mod.getValue();
            if (Double.parseDouble(currBigDec) == 0) {
                currVal.setValue(BigDecimal.ZERO);
            } else {
                currVal.setValue(new BigDecimal(currBigDec));
            }

            currBigDec = mod.getOneSigmaABS();
            if (Double.parseDouble(currBigDec) == 0) {
                currVal.setOneSigma(BigDecimal.ZERO);
            } else {
                currVal.setOneSigma(new BigDecimal(currBigDec));
            }

            isMeasures[i] = mod.getIsMeasured().isSelected();

            currVal.setUncertaintyType("ABS");
            values[i] = currVal;
        }
        refMatModel.setValues(values);
        refMatModel.setDataMeasured(isMeasures);

        refMatModel.setRhos(getRhosFromTable(refMatCorrTable));

        ObservableList<DataModel> concentrationsData = refMatConcentrationsTable.getItems();
        ValueModel[] concentrations = new ValueModel[concentrationsData.size()];
        for (int i = 0; i < concentrations.length; i++) {
            DataModel mod = concentrationsData.get(i);
            ValueModel currVal = new ValueModel();

            currVal.setName(mod.getName());

            String currBigDec = mod.getValue();
            if (Double.parseDouble(currBigDec) == 0) {
                currVal.setValue(BigDecimal.ZERO);
            } else {
                currVal.setValue(new BigDecimal(currBigDec));
            }

            currBigDec = mod.getOneSigmaABS();
            if (Double.parseDouble(currBigDec) == 0) {
                currVal.setOneSigma(BigDecimal.ZERO);
            } else {
                currVal.setOneSigma(new BigDecimal(currBigDec));
            }

            currVal.setReference(physConstReferences.get(i).getText());
            currVal.setUncertaintyType("ABS");
            concentrations[i] = currVal;
        }
        refMatModel.setConcentrations(concentrations);

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
        }

        if (refMatCB.getSelectionModel().getSelectedIndex() != 0) {
            refMatCB.getSelectionModel().selectFirst();
        } else {
            refMatModel = refMatModels.get(0);
            setUpRefMat();
        }

        refMatEditable(false);
        setUpRefMatMenuItems(false, refMatModel.isEditable());
    }

    @FXML
    private void refMateEditEmptyMod(ActionEvent event) {
        refMatModel = new ReferenceMaterial();
        setUpRefMat();
        refMatEditable(true);
        setUpRefMatMenuItems(true, true);
    }

    @FXML
    private void refMatEditCopy(ActionEvent event) {
        refMatModel = refMatModel.clone();
        setUpRefMat();
        refMatEditable(true);
        setUpRefMatMenuItems(true, true);
    }

    @FXML
    private void refMatEditCurrMod(ActionEvent event) {
        refMatHolder = refMatModel.clone();
        refMatEditable(true);
        setUpRefMatMenuItems(true, true);
        isEditingCurrRefMat = true;
    }

    private Map<String, BigDecimal> getRhosFromTable(TableView<ObservableList<SimpleStringProperty>> table) {
        Map<String, BigDecimal> rhos = new HashMap<>();
        table.setEditable(false);

        if (table.getColumns().size() > 0) {
            ObservableList<TableColumn<ObservableList<SimpleStringProperty>, ?>> colHeaders = table.getColumns();
            ObservableList<ObservableList<SimpleStringProperty>> items = table.getItems();
            List<String> headerNames = new ArrayList<>();
            for (int i = 0; i < colHeaders.size(); i++) {
                headerNames.add(colHeaders.get(i).getText());
            }

            for (int i = 0; i < items.size(); i++) {
                ObservableList<SimpleStringProperty> currItem = items.get(i);
                for (int j = 1; j < currItem.size(); j++) {
                    if (i + 1 != j) {
                        String val = currItem.get(j).get();
                        if (Double.parseDouble(val) != 0) {
                            String colHeader = getRatioHiddenName(headerNames.get(j));
                            String rowHeader = getRatioHiddenName(currItem.get(0).get());
                            String reverseKey = "rho" + rowHeader.substring(0, 1).toUpperCase()
                                    + rowHeader.substring(1) + "__" + colHeader;
                            if (!rhos.containsKey(reverseKey)) {
                                String key = "rho" + colHeader.substring(0, 1).toUpperCase()
                                        + colHeader.substring(1) + "__" + rowHeader;
                                rhos.put(key, new BigDecimal(val));
                            }
                        }
                    }
                }
            }
        }

        return rhos;
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

            bigDec = new BigDecimal(mod.getValue());
            mod.setValue(physConstDataNotation.format(bigDec));

            bigDec = new BigDecimal(mod.getOneSigmaABS());
            mod.setOneSigmaABS(physConstDataNotation.format(bigDec));

            bigDec = new BigDecimal(mod.getOneSigmaPCT());
            mod.setOneSigmaPCT(physConstDataNotation.format(bigDec));
        }
        physConstDataTable.getColumns().clear();
        List<TableColumn<DataModel, String>> columns = getDataModelColumns(physConstDataTable, physConstDataNotation);
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

            bigDec = new BigDecimal(mod.getValue());
            mod.setValue(refMatDataNotation.format(bigDec));

            bigDec = new BigDecimal(mod.getOneSigmaABS());
            mod.setOneSigmaABS(refMatDataNotation.format(bigDec));

            bigDec = new BigDecimal(mod.getOneSigmaPCT());
            mod.setOneSigmaPCT(refMatDataNotation.format(bigDec));
        }
        refMatDataTable.getColumns().clear();

        TableColumn<RefMatDataModel, String> nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(new PropertyValueFactory<RefMatDataModel, String>("name"));
        nameCol.setSortable(false);
        nameCol.setCellFactory(TextFieldTableCell.<RefMatDataModel>forTableColumn());
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
                mod.setValue(refMatDataNotation.format(valMod.getValue()));
                mod.setOneSigmaABS(refMatDataNotation.format(valMod.getOneSigmaABS()));
                mod.setOneSigmaPCT(refMatDataNotation.format(valMod.getOneSigmaPCT()));
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
                mod.setValue(refMatDataNotation.format(valMod.getValue()));
                mod.setOneSigmaABS(refMatDataNotation.format(valMod.getOneSigmaABS()));
                mod.setOneSigmaPCT(refMatDataNotation.format(valMod.getOneSigmaPCT()));
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
                mod.setValue(refMatDataNotation.format(valMod.getValue()));
                mod.setOneSigmaABS(refMatDataNotation.format(valMod.getOneSigmaABS()));
                mod.setOneSigmaPCT(refMatDataNotation.format(valMod.getOneSigmaPCT()));
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

        refMatDataTable.refresh();
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

            bigDec = new BigDecimal(mod.getValue());
            mod.setValue(refMatConcentrationsNotation.format(bigDec));

            bigDec = new BigDecimal(mod.getOneSigmaABS());
            mod.setOneSigmaABS(refMatConcentrationsNotation.format(bigDec));

            bigDec = new BigDecimal(mod.getOneSigmaPCT());
            mod.setOneSigmaPCT(refMatConcentrationsNotation.format(bigDec));
        }
        refMatConcentrationsTable.getColumns().clear();
        List<TableColumn<DataModel, String>> columns = getDataModelColumns(refMatConcentrationsTable, refMatConcentrationsNotation);
        for (TableColumn<DataModel, String> col : columns) {
            refMatConcentrationsTable.getColumns().add(col);
        }

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
        swapCovCorrNotation(physConstCorrNotation, physConstCorrTable, physConstModel);
        physConstCorrTable.refresh();
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
        swapCovCorrNotation(physConstCovNotation, physConstCovTable, physConstModel);
        physConstCovTable.refresh();
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
        swapCovCorrNotation(refMatCorrNotation, refMatCorrTable, refMatModel);
        refMatCorrTable.refresh();
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
        swapCovCorrNotation(refMatCovNotation, refMatCovTable, refMatModel);
        refMatCovTable.refresh();
    }

    private void swapCovCorrNotation(DecimalFormat format, TableView<ObservableList<SimpleStringProperty>> table, ParametersModel model) {
        ObservableList<ObservableList<SimpleStringProperty>> items = table.getItems();
        ObservableList<SimpleStringProperty> cols = FXCollections.observableArrayList();
        for (int i = 0; i < table.getColumns().size(); i++) {
            cols.add(new SimpleStringProperty(table.getColumns().get(i).getText()));
        }

        items.add(0, cols);
        initializeTableWithObList(table, items, format, model);
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
