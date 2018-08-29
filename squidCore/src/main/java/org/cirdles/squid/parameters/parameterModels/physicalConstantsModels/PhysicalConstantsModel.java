/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.parameters.parameterModels.physicalConstantsModels;

import com.thoughtworks.xstream.XStream;
import org.cirdles.squid.parameters.matrices.CorrelationMatrixModel;
import org.cirdles.squid.parameters.matrices.CovarianceMatrixModel;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.util.DataDictionary;
import org.cirdles.squid.parameters.util.XStreamETReduxConverters.ETReduxPhysConstConverter;
import org.cirdles.squid.parameters.valueModels.ValueModel;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author ryanb
 */
public class PhysicalConstantsModel extends ParametersModel {

    private static long serialVersionUID = 6711139902976873456L;

    Map<String, BigDecimal> molarMasses;

    public PhysicalConstantsModel() {
        super();
        generateDefaultValueModels();
        molarMasses = new HashMap<>();
        setUpDefaultMolarMasses();
    }

    @Override
    public PhysicalConstantsModel clone() {
        PhysicalConstantsModel model = new PhysicalConstantsModel();
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
                    curr.getReference(), curr.getValue(), curr.getOneSigma());
        }

        model.setValues(vals);

        model.setCorrModel((CorrelationMatrixModel) corrModel.copy());
        model.setCovModel((CovarianceMatrixModel) covModel.copy());

        Map<String, BigDecimal> newRhos = new HashMap<>();
        model.setRhos(newRhos);
        Iterator<Entry<String, BigDecimal>> rhosIterator = rhos.entrySet().iterator();
        while (rhosIterator.hasNext()) {
            Entry<String, BigDecimal> entry = rhosIterator.next();
            newRhos.put(entry.getKey(), entry.getValue());
        }

        Map<String, BigDecimal> masses = model.getMolarMasses();
        Iterator<Entry<String, BigDecimal>> massesIterator = molarMasses.entrySet().iterator();
        while (massesIterator.hasNext()) {
            Entry<String, BigDecimal> entry = massesIterator.next();
            masses.put(entry.getKey(), entry.getValue());
        }
        model.setMolarMasses(masses);

        return model;
    }

    private void generateDefaultValueModels() {
        values = new ValueModel[7];
        values[0] = new ValueModel("lambda226");
        values[1] = new ValueModel("lambda230");
        values[2] = new ValueModel("lambda231");
        values[3] = new ValueModel("lambda232");
        values[4] = new ValueModel("lambda234");
        values[5] = new ValueModel("lambda235");
        values[6] = new ValueModel("lambda238");
    }

    public static List<PhysicalConstantsModel> getDefaultModels() {
        File folder = new File("SamplePhysicalConstantsModels/");
        File[] files = new File[0];
        List<PhysicalConstantsModel> models = new ArrayList<>();
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
                try {
                    models.add(PhysicalConstantsModel.getPhysicalConstantsModelFromETReduxXML(files[i]));
                } catch (Exception e) {
                    models.add((PhysicalConstantsModel) (new PhysicalConstantsModel()).readXMLObject(files[i].getAbsolutePath(), false));
                }
            }
        }

        return models;
    }

    public static PhysicalConstantsModel getDefaultModel(String modelName, String version) {
        PhysicalConstantsModel retVal = null;
        List<PhysicalConstantsModel> models = getDefaultModels();
        for (int i = 0; i < models.size() && retVal == null; i++) {
            if (models.get(i).getModelName().equals(modelName) && models.get(i).getVersion().equals(version)) {
                retVal = models.get(i);
            }
        }
        if (retVal == null) {
            retVal = new PhysicalConstantsModel();
        }
        return retVal;
    }

    public void setUpDefaultMolarMasses() {
        molarMasses.clear();
        String[][] masses = DataDictionary.AtomicMolarMasses;
        for (int i = 0; i < masses.length; i++) {
            molarMasses.put(masses[i][0], new BigDecimal(masses[i][1]));
        }
    }

    public Map<String, BigDecimal> getMolarMasses() {
        return molarMasses;
    }

    public void setMolarMasses(Map<String, BigDecimal> molarMasses) {
        this.molarMasses = molarMasses;
    }

    public static PhysicalConstantsModel getPhysicalConstantsModelFromETReduxXML(String input) {
        XStream xstream = getETReduxXStream();
        PhysicalConstantsModel model = (PhysicalConstantsModel) xstream.fromXML(input);
        return model;
    }

    public static PhysicalConstantsModel getPhysicalConstantsModelFromETReduxXML(File input) {
        XStream xstream = getETReduxXStream();
        PhysicalConstantsModel model = (PhysicalConstantsModel) xstream.fromXML(input);
        return model;
    }

    public static XStream getETReduxXStream() {
        XStream xstream = new XStream();
        xstream.registerConverter(new ETReduxPhysConstConverter());
        xstream.alias("PhysicalConstantsModel", PhysicalConstantsModel.class);
        return xstream;
    }

}
