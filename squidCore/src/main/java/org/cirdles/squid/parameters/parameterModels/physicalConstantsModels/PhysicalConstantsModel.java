/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.parameters.parameterModels.physicalConstantsModels;

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.cirdles.squid.parameters.ValueModel;
import org.cirdles.squid.parameters.matrices.CorrelationMatrixModel;
import org.cirdles.squid.parameters.matrices.CovarianceMatrixModel;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.util.XStreamETReduxConverters.ETReduxPhysConstConverter;
import org.cirdles.squid.parameters.util.DataDictionary;

/**
 *
 * @author ryanb
 */
public class PhysicalConstantsModel extends ParametersModel {

    Map<String, BigDecimal> molarMasses;

    public PhysicalConstantsModel() {
        super();
        generateDefaultValueModels();
        molarMasses = new HashMap<>();
        setUpDefaultMolarMasses();
    }

    public PhysicalConstantsModel clone() {
        PhysicalConstantsModel model = new PhysicalConstantsModel();
        model.setModelName(modelName + " - copy");
        model.setLabName(labName);
        model.setVersion(version);
        model.setDateCertified(dateCertified);
        model.setComments(comments);
        model.setReferences(references);
        model.setValues(values.clone());
        model.setCorrModel((CorrelationMatrixModel) corrModel.copy());
        model.setCovModel((CovarianceMatrixModel) covModel.copy());

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
        values[0] = new ValueModel("lambda226", "ABS", "", BigDecimal.ZERO, BigDecimal.ZERO);
        values[1] = new ValueModel("lambda230", "ABS", "", BigDecimal.ZERO, BigDecimal.ZERO);
        values[2] = new ValueModel("lambda231", "ABS", "", BigDecimal.ZERO, BigDecimal.ZERO);
        values[3] = new ValueModel("lambda232", "ABS", "", BigDecimal.ZERO, BigDecimal.ZERO);
        values[4] = new ValueModel("lambda234", "ABS", "", BigDecimal.ZERO, BigDecimal.ZERO);
        values[5] = new ValueModel("lambda235", "ABS", "", BigDecimal.ZERO, BigDecimal.ZERO);
        values[6] = new ValueModel("lambda238", "ABS", "", BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public static List<PhysicalConstantsModel> getDefaultModels() {
        File folder = new File("SamplePhysicalConstantsModels/");
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
        List<PhysicalConstantsModel> models = new ArrayList<>();
        for(int i = 0; i < files.length; i++) {
            models.add(PhysicalConstantsModel.getPhysicalConstantsModelFromETReduxXML(files[i]));
        }
        return models;
    }

    public void setUpDefaultMolarMasses() {
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

    public final void initializeNewRatiosAndRhos() {

        this.values = new ValueModel[DataDictionary.MeasuredConstants.length];
        for (int i = 0; i < DataDictionary.MeasuredConstants.length; i++) {
            this.values[i]
                    = new ValueModel(
                            DataDictionary.MeasuredConstants[i][0],
                            "PCT", "", BigDecimal.ZERO, BigDecimal.ZERO);
        }

        Arrays.sort(values, new DataValueModelNameComparator());

        buildRhosMap();

    }

}
