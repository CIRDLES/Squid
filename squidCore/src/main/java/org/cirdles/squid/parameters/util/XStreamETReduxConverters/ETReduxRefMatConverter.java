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
import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
import org.cirdles.squid.parameters.valueModels.ValueModel;

/**
 *
 * @author ryanb
 */
public class ETReduxRefMatConverter implements Converter {

    @Override
    public boolean canConvert(Class type) {
        return type.equals(ReferenceMaterialModel.class);
    }

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext context) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        ReferenceMaterialModel model = new ReferenceMaterialModel();

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
        
        reader.moveDown();
        reader.moveUp();
        reader.moveDown();
        reader.moveUp();

        List<ValueModel> values = new ArrayList<>();
        List<Boolean> measures = new ArrayList<>();
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
            measures.add(Boolean.parseBoolean(reader.getValue()));
            reader.moveUp();

            reader.moveUp();

            values.add(valMod);
        }
        reader.moveUp();
        ValueModel[] valuesArray = new ValueModel[values.size()];
        boolean[] measuredArray = new boolean[values.size()];
        for (int i = 0; i < values.size(); i++) {
            valuesArray[i] = values.get(i);
            measuredArray[i] = measures.get(i);
        }
        model.setValues(valuesArray);
        model.setDataMeasured(measuredArray);

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

        List<ValueModel> concentrationsList = new ArrayList<>();
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

            reader.moveUp();

            concentrationsList.add(valMod);
        }
        reader.moveUp();
        ValueModel[] concentrations = new ValueModel[concentrationsList.size()];
        for (int i = 0; i < concentrationsList.size(); i++) {
            concentrations[i] = concentrationsList.get(i);
        }
        model.setConcentrations(concentrations);

        return model;
    }
}
