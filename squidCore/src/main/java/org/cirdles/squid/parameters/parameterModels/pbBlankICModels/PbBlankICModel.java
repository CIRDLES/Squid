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
public class PbBlankICModel extends ParametersModel {

    private static long serialVersionUID = -5054855251006560667L;

    public static PbBlankICModel defaultPbBlankICModel = getDefaultModel("EARTHTIME Example Pb Blank IC", "3.0");

    public PbBlankICModel() {
        super();
        generateDefaultValueModels();
    }

    private void generateDefaultValueModels() {
        values = new ValueModel[3];
        values[0] = new ValueModel("r206_204b");
        values[1] = new ValueModel("r207_204b");
        values[2] = new ValueModel("r208_204b");
    }

    public PbBlankICModel clone() {
        PbBlankICModel model = new PbBlankICModel();

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

    public static List<PbBlankICModel> getDefaultModels() {
        File folder = new File("SamplePbBlankICModels/");
        File[] files = folder.listFiles(new FilenameFilter() {
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
        List<PbBlankICModel> models = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            models.add(PbBlankICModel.getPbBlankICModelFromETReduxXML(files[i]));
        }

        return models;
    }

    public static PbBlankICModel getDefaultModel(String modelName, String version) {
        PbBlankICModel retVal = null;
        List<PbBlankICModel> models = getDefaultModels();
        for (int i = 0; i < models.size() && retVal == null; i++) {
            if (models.get(i).getModelName().equals(modelName) && models.get(i).getVersion().equals(version)) {
                retVal = models.get(i);
            }
        }
        if (retVal == null) {
            retVal = new PbBlankICModel();
        }
        return retVal;
    }

    public static PbBlankICModel getPbBlankICModelFromETReduxXML(String input) {
        XStream xstream = getETReduxXStream();
        PbBlankICModel model = (PbBlankICModel) xstream.fromXML(input);
        return model;
    }

    public static PbBlankICModel getPbBlankICModelFromETReduxXML(File input) {
        XStream xstream = getETReduxXStream();
        PbBlankICModel model = (PbBlankICModel) xstream.fromXML(input);
        return model;
    }

    public static XStream getETReduxXStream() {
        XStream xstream = new XStream();
        xstream.registerConverter(new ETReduxPbBlankICConverter());
        xstream.alias("PbBlankICModel", PbBlankICModel.class);
        return xstream;
    }

    public static void main(String[] args) {
        ObjectStreamClass stream = ObjectStreamClass.lookup(PbBlankICModel.class);
        System.out.println("serialVersionUID: " + stream.getSerialVersionUID());
    }

}
