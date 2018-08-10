/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.parameters.parameterModels.pbBlankICModels;

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.ObjectStreamClass;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.cirdles.squid.parameters.matrices.CorrelationMatrixModel;
import org.cirdles.squid.parameters.matrices.CovarianceMatrixModel;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.util.XStreamETReduxConverters.ETReduxPbBlankICConverter;
import org.cirdles.squid.parameters.valueModels.ValueModel;

/**
 *
 * @author ryanb
 */
public class CommonPbModel extends ParametersModel {

    private static long serialVersionUID = -5054855251006560667L;

    public static CommonPbModel defaultPbBlankICModel = getDefaultModel("EARTHTIME Example Pb Blank IC", "3.0");

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

    public static List<CommonPbModel> getDefaultModels() {
        File folder = new File("SamplePbBlankICModels/");
        List<CommonPbModel> models = new ArrayList<>();
        File[] files = new File[0];
        if (folder.exists()) {
            files = folder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    boolean retVal;
                    if (name.toLowerCase().endsWith(".xml")) {
                        retVal = true;
                    } else {
                        retVal = false;
                    }
                    return retVal;
                }
            });
            for (int i = 0; i < files.length; i++) {
                models.add(CommonPbModel.getPbBlankICModelFromETReduxXML(files[i]));
            }
        }

        return models;
    }

    public static CommonPbModel getDefaultModel(String modelName, String version) {
        CommonPbModel retVal = null;
        List<CommonPbModel> models = getDefaultModels();
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

    public static CommonPbModel getPbBlankICModelFromETReduxXML(String input) {
        XStream xstream = getETReduxXStream();
        CommonPbModel model = (CommonPbModel) xstream.fromXML(input);
        return model;
    }

    public static CommonPbModel getPbBlankICModelFromETReduxXML(File input) {
        XStream xstream = getETReduxXStream();
        CommonPbModel model = (CommonPbModel) xstream.fromXML(input);
        return model;
    }

    public static XStream getETReduxXStream() {
        XStream xstream = new XStream();
        xstream.registerConverter(new ETReduxPbBlankICConverter());
        xstream.alias("PbBlankICModel", CommonPbModel.class);
        return xstream;
    }

    public static void main(String[] args) {
        ObjectStreamClass stream = ObjectStreamClass.lookup(CommonPbModel.class);
        System.out.println("serialVersionUID: " + stream.getSerialVersionUID());
    }

}
