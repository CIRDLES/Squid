/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.parameters.parameterModels.physicalConstantsModels;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.valueModels.ValueModel;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ryanb
 */
public class PhysicalConstantsModelXMLConverter implements Converter {

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext context) {
        ParametersModel model = (PhysicalConstantsModel) o;

        writer.startNode("modelName");
        writer.setValue(model.getModelName());
        writer.endNode();

        writer.startNode("labName");
        writer.setValue(model.getLabName());
        writer.endNode();

        writer.startNode("version");
        writer.setValue(model.getVersion());
        writer.endNode();

        writer.startNode("dateCertified");
        writer.setValue(model.getDateCertified());
        writer.endNode();

        writer.startNode("comments");
        writer.setValue(model.getComments());
        writer.endNode();

        writer.startNode("references");
        writer.setValue(model.getReferences());
        writer.endNode();

        writer.startNode("values");
        context.convertAnother(model.getValues());
        writer.endNode();

        writer.startNode("rhos");
        context.convertAnother(model.getRhos());
        writer.endNode();

        writer.startNode("isEditable");
        writer.setValue(Boolean.toString(model.isEditable()));
        writer.endNode();

        writer.startNode("molarMasses");
        context.convertAnother(((PhysicalConstantsModel) model).getMolarMasses());
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        ParametersModel model = new PhysicalConstantsModel();

        reader.moveDown();
        model.setModelName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        model.setLabName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        model.setVersion(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        model.setDateCertified(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        model.setComments(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        model.setReferences(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        model.setValues(new ValueModel[0]);
        model.setValues((ValueModel[]) context.convertAnother(model.getValues(), ValueModel[].class));
        reader.moveUp();

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

        reader.moveDown();
        model.setIsEditable(Boolean.parseBoolean(reader.getValue()));
        reader.moveUp();

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
        ((PhysicalConstantsModel) model).setMolarMasses(molarMasses);

        return model;
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(PhysicalConstantsModel.class);
    }

}