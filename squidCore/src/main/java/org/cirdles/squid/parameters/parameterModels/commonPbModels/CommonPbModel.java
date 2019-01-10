/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.parameters.parameterModels.commonPbModels;

import org.cirdles.squid.parameters.matrices.CorrelationMatrixModel;
import org.cirdles.squid.parameters.matrices.CovarianceMatrixModel;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.valueModels.ValueModel;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author ryanb
 */
public class CommonPbModel extends ParametersModel {

    private static final long serialVersionUID = 8844997267638498894L;

    public CommonPbModel() {
        super();
        generateDefaultValueModels();
    }

    private void generateDefaultValueModels() {
        values = new ValueModel[5];
        values[0] = new ValueModel("r206_204b");
        values[1] = new ValueModel("r207_204b");
        values[2] = new ValueModel("r207_206b");
        values[3] = new ValueModel("r208_204b");
        values[4] = new ValueModel("r208_206b");
    }

    public CommonPbModel clone() {
        CommonPbModel model = new CommonPbModel();

        model.setModelName(modelName);
        model.setIsEditable(isEditable);
        model.setLabName(labName);
        model.setVersion(version);
        model.setDateCertified(dateCertified);
        model.setComments(comments);
        model.setReferences(references);

        ValueModel[] vals = new ValueModel[values.length];
        for (int i = 0; i < vals.length; i++) {
            ValueModel curr = values[i];
            vals[i] = new ValueModel(curr.getName(), curr.getUncertaintyType(),
                    curr.getValue(), curr.getOneSigma());
        }
        model.setValues(vals);

        model.setCorrModel((CorrelationMatrixModel) corrModel.copy());
        model.setCovModel((CovarianceMatrixModel) covModel.copy());

        Map<String, BigDecimal> newRhos = new HashMap<>();
        model.setRhos(newRhos);
        Iterator<Map.Entry<String, BigDecimal>> rhosIterator = rhos.entrySet().iterator();
        while (rhosIterator.hasNext()) {
            Map.Entry<String, BigDecimal> entry = rhosIterator.next();
            newRhos.put(entry.getKey(), entry.getValue());
        }

        return model;
    }

    public static List<ParametersModel> getDefaultModels() {
        File folder = new File("SampleCommonPbModels");
        List<ParametersModel> models = new ArrayList<>();
        if (folder.exists()) {
            File[] files = folder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".xml");
                }
            });
            for (int i = 0; i < files.length; i++) {
                models.add((ParametersModel) (new CommonPbModel()).readXMLObject(files[i].getAbsolutePath(), false));
            }
        }

        return models;
    }

    public static ParametersModel getDefaultModel(String modelName, String version) {
        ParametersModel retVal = null;
        List<ParametersModel> models = getDefaultModels();
        for (int i = 0; i < models.size() && retVal == null; i++) {
            if (models.get(i).getModelName().equals(modelName) && models.get(i).getVersion().equals(version)) {
                retVal = models.get(i);
            }
        }
        if (retVal == null) {
            retVal = new CommonPbModel();
        }
        return retVal;
    }

}
