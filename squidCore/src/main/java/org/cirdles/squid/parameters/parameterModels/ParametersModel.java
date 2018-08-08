/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.parameters.parameterModels;

import com.thoughtworks.xstream.XStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.cirdles.squid.parameters.matrices.CorrelationMatrixModel;
import org.cirdles.squid.parameters.matrices.CovarianceMatrixModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModelConverter;
import org.cirdles.squid.parameters.parameterModels.referenceMaterials.ReferenceMaterial;
import org.cirdles.squid.parameters.parameterModels.referenceMaterials.ReferenceMaterialConverter;
import org.cirdles.squid.parameters.util.DateHelper;
import org.cirdles.squid.parameters.valueModels.ValueModel;
import org.cirdles.squid.parameters.valueModels.ValueModelConverter;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;

/**
 *
 * @author ryanb
 */
public class ParametersModel implements
        Comparable<ParametersModel>,
        Serializable, XMLSerializerInterface {

    protected String modelName;
    protected String labName;
    protected String version;
    protected String dateCertified;
    protected String comments;
    protected String references;
    protected ValueModel[] values;
    protected CorrelationMatrixModel corrModel;
    protected CovarianceMatrixModel covModel;
    protected Map<String, BigDecimal> rhos;
    protected boolean isEditable;

    public ParametersModel() {
        modelName = "new model";
        labName = "unknown lab";
        version = "1.0";
        dateCertified = DateHelper.getCurrentDate();
        comments = "";
        references = "";
        values = new ValueModel[0];
        corrModel = new CorrelationMatrixModel();
        covModel = new CovarianceMatrixModel();
        this.rhos = new HashMap<>();
        isEditable = false;
    }

    public ParametersModel(String modelName) {
        this.modelName = modelName;
        this.labName = labName;
        this.version = version;
        this.dateCertified = dateCertified;
        comments = "";
        references = "";
        values = new ValueModel[0];
        corrModel = new CorrelationMatrixModel();
        covModel = new CovarianceMatrixModel();
        this.rhos = new HashMap<>();
        isEditable = false;
    }

    public ParametersModel(String modelName, String labName,
            String version, String dateCertified) {
        this.modelName = modelName;
        this.labName = labName;
        this.version = version;
        this.dateCertified = dateCertified;
        comments = "";
        references = "";
        values = new ValueModel[0];
        corrModel = new CorrelationMatrixModel();
        covModel = new CovarianceMatrixModel();
        this.rhos = new HashMap<>();
        isEditable = false;
    }

    public ParametersModel(String modelName, String labName, String version,
            String dateCertified, String comments, String references) {
        this.modelName = modelName;
        this.labName = labName;
        this.version = version;
        this.dateCertified = dateCertified;
        this.comments = comments;
        this.references = references;
        values = new ValueModel[0];
        corrModel = new CorrelationMatrixModel();
        covModel = new CovarianceMatrixModel();
        this.rhos = new HashMap<>();
        isEditable = false;
    }

    public ParametersModel(String modelName, String labName, String version,
            String dateCertified, String comments, String references,
            ValueModel[] values) {
        this.modelName = modelName;
        this.labName = labName;
        this.version = version;
        this.dateCertified = dateCertified;
        this.comments = comments;
        this.references = references;
        this.values = values;
        corrModel = new CorrelationMatrixModel();
        covModel = new CovarianceMatrixModel();
        this.rhos = new HashMap<>();
        isEditable = false;
    }

    public ParametersModel(String modelName, String labName, String version,
            String dateCertified, String comments, String references, ValueModel[] values,
            CorrelationMatrixModel corrModel, CovarianceMatrixModel covModel,
            Map<String, BigDecimal> rhos, boolean isEditable) {
        this.modelName = modelName;
        this.labName = labName;
        this.version = version;
        this.dateCertified = dateCertified;
        this.comments = comments;
        this.references = references;
        this.values = values;
        this.corrModel = corrModel;
        this.covModel = covModel;
        this.rhos = rhos;
        this.isEditable = isEditable;
    }

    @Override
    public int compareTo(ParametersModel o) {
        return (modelName + version).compareTo(o.getModelName() + o.getVersion());
    }

    public boolean equals(Object o) {
        boolean retVal = o instanceof ParametersModel;
        if (retVal && ((ParametersModel) o).
                getModelName().compareTo(modelName) != 0) {
            retVal = false;
        }
        return retVal;
    }

    public ValueModel getDatumByName(String datumName) {
        ValueModel retVal = new ValueModel(datumName);
        boolean found = false;
        for (int i = 0; !found && i < values.length; i++) {
            if (values[i].getName().equals(datumName)) {
                retVal = values[i];
                found = true;
            }
        }

        return retVal;
    }
    
    public ParametersModel clone() {
        ParametersModel model = new ParametersModel();
        
        model.setModelName(modelName);
        model.setIsEditable(isEditable);
        model.setLabName(labName);
        model.setVersion(version);
        model.setDateCertified(dateCertified);
        model.setComments(comments);
        model.setReferences(references);
        
        ValueModel[] vals = new ValueModel[values.length];
        for(int i = 0; i < vals.length; i++) {
            ValueModel curr = values[i];
            vals[i] = new ValueModel(curr.getName(), curr.getUncertaintyType(),
            curr.getValue(), curr.getOneSigma());
        }
        
        model.setValues(vals);
        
        model.setCorrModel((CorrelationMatrixModel) corrModel.copy());
        model.setCovModel((CovarianceMatrixModel) covModel.copy());
        
        return model;
    }

    public void generateCorrelationsFromCovariances() {
        Iterator<String> colNames;
        try {
            corrModel.copyValuesFrom(covModel);
            // divide each cell by (1-sigma for x * 1-sigma for y)
            colNames = corrModel.getCols().keySet().iterator();
            while (colNames.hasNext()) {
                String colName = colNames.next();
                ValueModel colData = getDatumByName(colName);
                int col = corrModel.getCols().get(colName);
                //calculate values for this column
                int rowColDimension = corrModel.getMatrix().getColumnDimension();
                for (int row = 0; row < rowColDimension; row++) {
                    String rowName = corrModel.getRows().get(row);
                    ValueModel rowData = getDatumByName(rowName);
                    double correlation
                            = //
                            covModel.getMatrix().get(row, col)//
                            / rowData.getOneSigmaABS().doubleValue() //
                            / colData.getOneSigmaABS().doubleValue();
                    corrModel.setValueAt(row, col, correlation);
                }
            }
        } catch (Exception e) {
        }
    }

    public void generateCovariancesFromCorrelations() {
        Iterator<String> colNames;
        try {
            covModel.copyValuesFrom(corrModel);
            // divide each cell by (1-sigma for x * 1-sigma for y)
            colNames = covModel.getCols().keySet().iterator();
            while (colNames.hasNext()) {
                String colName = colNames.next();
                ValueModel colData = getDatumByName(colName);
                int col = covModel.getCols().get(colName);
                //calculate values for this column
                int rowColDimension = covModel.getMatrix().getColumnDimension();
                for (int row = 0; row < rowColDimension; row++) {
                    String rowName = covModel.getRows().get(row);
                    ValueModel rowData = getDatumByName(rowName);
                    double covariance
                            = //
                            corrModel.getMatrix().get(row, col)//
                            * rowData.getOneSigmaABS().doubleValue() //
                            * colData.getOneSigmaABS().doubleValue();
                    covModel.setValueAt(row, col, covariance);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeCorrelations() {
        Map<Integer, String> dataNamesList = new HashMap<>();

        // only build matrices for values with positive uncertainties
        int valueCount = 0;
        for (ValueModel value : values) {
            if (value.isPositive()) {
                dataNamesList.put(valueCount, value.getName());
                valueCount++;
            }
        }
        corrModel.setRows(dataNamesList);
        corrModel.setCols(dataNamesList);

        corrModel.initializeMatrix();

        ((CorrelationMatrixModel) corrModel).initializeCorrelations(rhos);
    }

    protected void buildRhosMap() {

        rhos = new HashMap<>();

        for (int i = 0; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {
                String key = "rho" + values[i].getName().substring(0, 1).toUpperCase()
                        + values[i].getName().substring(1) + "__" + values[j].getName();
                rhos.put(key, BigDecimal.ZERO);
            }
        }
    }

    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new ValueModelConverter());
        xstream.alias("ValueModel", ValueModel.class);

        xstream.registerConverter(new ReferenceMaterialConverter());
        xstream.alias("ReferenceMaterial", ReferenceMaterial.class);

        xstream.registerConverter(new PhysicalConstantsModelConverter());
        xstream.alias("PhysicalConstantsModel", PhysicalConstantsModel.class);
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDateCertified() {
        return dateCertified;
    }

    public void setDateCertified(String dateCertified) {
        this.dateCertified = dateCertified;
    }

    public ValueModel[] getValues() {
        return values;
    }

    public void setValues(ValueModel[] values) {
        this.values = values;
    }

    public CorrelationMatrixModel getCorrModel() {
        return corrModel;
    }

    public void setCorrModel(CorrelationMatrixModel corrModel) {
        this.corrModel = corrModel;
    }

    public CovarianceMatrixModel getCovModel() {
        return covModel;
    }

    public void setCovModel(CovarianceMatrixModel covModel) {
        this.covModel = covModel;
    }

    protected class DataValueModelNameComparator implements Comparator<ValueModel> {

        public DataValueModelNameComparator() {
        }

        @Override
        public int compare(ValueModel vm1, ValueModel vm2) {
            if (vm1.getName().substring(0, 1).equalsIgnoreCase(vm2.getName().substring(0, 1))) {
                return vm1.compareTo(vm2);
            } else {
                return vm2.compareTo(vm1);
            }
        }
    }

    public Map<String, BigDecimal> getRhos() {
        return rhos;
    }

    public void setRhos(Map<String, BigDecimal> rhos) {
        this.rhos = rhos;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setIsEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }
}
