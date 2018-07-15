/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.parameters.parameterModels.referenceMaterials;

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import org.cirdles.squid.parameters.ValueModel;
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
        concentrations = new ValueModel[0];
        dataMeasured = new boolean[0];
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
