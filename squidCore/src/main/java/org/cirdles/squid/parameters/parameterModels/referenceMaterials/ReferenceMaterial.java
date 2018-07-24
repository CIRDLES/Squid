/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.parameters.parameterModels.referenceMaterials;

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.cirdles.squid.parameters.ValueModel;
import org.cirdles.squid.parameters.matrices.CorrelationMatrixModel;
import org.cirdles.squid.parameters.matrices.CovarianceMatrixModel;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.util.ReferenceMaterialEnum;
import org.cirdles.squid.parameters.util.XStreamETReduxConverters.ETReduxRefMatConverter;

/**
 *
 * @author ryanb
 */
public class ReferenceMaterial extends ParametersModel {

    ValueModel[] concentrations;
    boolean[] dataMeasured;

    public ReferenceMaterial() {
        super();
        generateDefaultValueModels();
        concentrations = new ValueModel[0];
    }

    public ReferenceMaterial clone() {
        ReferenceMaterial model = new ReferenceMaterial();

        model.setModelName(modelName + " - copy");
        model.setLabName(labName);
        model.setVersion(version);
        model.setDateCertified(dateCertified);
        model.setComments(comments);
        model.setReferences(references);
        model.setValues(values.clone());
        model.setCorrModel((CorrelationMatrixModel) corrModel.copy());
        model.setCovModel((CovarianceMatrixModel) covModel.copy());
        model.setConcentrations(concentrations.clone());
        model.setDataMeasured(dataMeasured.clone());

        return model;
    }

    private void generateDefaultValueModels() {
        values = new ValueModel[5];
        values[0] = new ValueModel("r206_207r", "ABS", "", BigDecimal.ZERO, BigDecimal.ZERO);
        values[1] = new ValueModel("r206_208r", "ABS", "", BigDecimal.ZERO, BigDecimal.ZERO);
        values[2] = new ValueModel("r206_238r", "ABS", "", BigDecimal.ZERO, BigDecimal.ZERO);
        values[3] = new ValueModel("r208_232r", "ABS", "", BigDecimal.ZERO, BigDecimal.ZERO);
        values[4] = new ValueModel("r238_235s", "ABS", "", BigDecimal.ZERO, BigDecimal.ZERO);
        dataMeasured = new boolean[5];
        for (int i = 0; i < dataMeasured.length; i++) {
            dataMeasured[i] = false;
        }
    }

    public final void initializeNewRatiosAndRhos() {
        ArrayList<ValueModel> holdRatios = new ArrayList<>();
        for (ReferenceMaterialEnum value : ReferenceMaterialEnum.values()) {
            holdRatios.add( //
                    new ValueModel(value.getName(),
                            "ABS", BigDecimal.ZERO,
                            BigDecimal.ZERO));
        }

        values = holdRatios.toArray(new ValueModel[holdRatios.size()]);

        Arrays.sort(values, new DataValueModelNameComparator());

        buildRhosMap();
    }

    public static List<ReferenceMaterial> getDefaultModels() {
        File folder = new File("SampleReferenceMaterials/");
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
        List<ReferenceMaterial> models = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            models.add(ReferenceMaterial.getReferenceMaterialFromETReduxXML(files[i]));
        }
        return models;
    }

    public ValueModel[] getConcentrations() {
        return concentrations;
    }

    public void setConcentrations(ValueModel[] concentrations) {
        this.concentrations = concentrations;
    }

    public boolean[] getDataMeasured() {
        return dataMeasured;
    }

    public void setDataMeasured(boolean[] dataMeasured) {
        this.dataMeasured = dataMeasured;
    }

    public static XStream getETReduxXStream() {
        XStream xstream = new XStream();
        xstream.registerConverter(new ETReduxRefMatConverter());
        xstream.alias("ReferenceMaterial", ReferenceMaterial.class);
        xstream.alias("MineralStandardUPbModel", ReferenceMaterial.class);
        return xstream;
    }

    public static ReferenceMaterial getReferenceMaterialFromETReduxXML(String input) {
        XStream xstream = getETReduxXStream();
        ReferenceMaterial model = (ReferenceMaterial) xstream.fromXML(input);
        return model;
    }

    public static ReferenceMaterial getReferenceMaterialFromETReduxXML(File input) {
        XStream xstream = getETReduxXStream();
        ReferenceMaterial model = (ReferenceMaterial) xstream.fromXML(input);
        return model;
    }
}
