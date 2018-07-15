/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.parameters.parameterModels.physicalConstantsModels;

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.cirdles.squid.parameters.ValueModel;
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
        molarMasses = new HashMap<>();
        setUpDefaultMolarMasses();
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
