/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.parameters.util.XStreamETReduxConverters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cirdles.squid.parameters.ValueModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;

/**
 *
 * @author ryanb
 */
public class ETReduxPhysConstConverter implements Converter {

    @Override
    public boolean canConvert(Class type) {
        return type.equals(PhysicalConstantsModel.class);
    }

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext context) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        PhysicalConstantsModel model = new PhysicalConstantsModel();

        reader.moveDown();
        model.setModelName(reader.getValue());
        reader.moveUp();

        String version = "";
        reader.moveDown();
        version += reader.getValue() + ".";
        reader.moveUp();
        reader.moveDown();
        version += reader.getValue();
        reader.moveUp();
        model.setVersion(version);

        reader.moveDown();
        model.setLabName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        model.setDateCertified(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        model.setReferences(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        model.setComments(reader.getValue());
        reader.moveUp();

        List<ValueModel> values = new ArrayList<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            ValueModel valMod = new ValueModel();
            String currBigDec;

            reader.moveDown();

            reader.moveDown();
            valMod.setName(reader.getValue());
            reader.moveUp();

            reader.moveDown();
            currBigDec = reader.getValue();
            if (Double.parseDouble(currBigDec) == 0.0) {
                valMod.setValue(BigDecimal.ZERO);
            } else {
                valMod.setValue(new BigDecimal(currBigDec));
            }
            reader.moveUp();

            reader.moveDown();
            valMod.setUncertaintyType(reader.getValue());
            reader.moveUp();

            reader.moveDown();
            currBigDec = reader.getValue();
            if (Double.parseDouble(currBigDec) == 0.0) {
                valMod.setOneSigma(BigDecimal.ZERO);
            } else {
                valMod.setOneSigma(new BigDecimal(currBigDec));
            }
            reader.moveUp();

            reader.moveDown();
            valMod.setReference(reader.getValue());
            reader.moveUp();

            reader.moveUp();

            values.add(valMod);
        }
        reader.moveUp();
        ValueModel[] valuesArray = new ValueModel[values.size()];
        for (int i = 0; i < values.size(); i++) {
            valuesArray[i] = values.get(i);
        }
        model.setValues(valuesArray);

        Map<String, BigDecimal> rhos = new HashMap<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            reader.moveDown();

            reader.moveDown();
            String key = reader.getValue();
            reader.moveUp();

            reader.moveDown();
            String currBigDec = reader.getValue();
            BigDecimal value;
            if (Double.parseDouble(currBigDec) == 0.0) {
                value = BigDecimal.ZERO;
            } else {
                value = new BigDecimal(currBigDec);
            }
            reader.moveUp();

            reader.moveUp();

            rhos.put(key, value);
        }
        reader.moveUp();
        model.setRhos(rhos);

        Map<String, BigDecimal> molarMasses = new HashMap<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            reader.moveDown();

            reader.moveDown();
            String key = reader.getValue();
            reader.moveUp();

            reader.moveDown();
            String currBigDec = reader.getValue();
            BigDecimal value;
            if (Double.parseDouble(currBigDec) == 0.0) {
                value = BigDecimal.ZERO;
            } else {
                value = new BigDecimal(currBigDec);
            }
            reader.moveUp();

            reader.moveUp();

            molarMasses.put(key, value);
        }
        reader.moveUp();
        model.setMolarMasses(molarMasses);

        return model;
    }

}
