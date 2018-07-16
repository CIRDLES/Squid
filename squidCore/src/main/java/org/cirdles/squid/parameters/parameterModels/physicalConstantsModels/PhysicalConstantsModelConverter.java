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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.cirdles.squid.parameters.ValueModel;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;

/**
 *
 * @author ryanb
 */
public class PhysicalConstantsModelConverter implements Converter {

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext context) {
        PhysicalConstantsModel model = (PhysicalConstantsModel) o;

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

        writer.startNode("molarMasses");
        context.convertAnother(model.getMolarMasses());
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        PhysicalConstantsModel model = new PhysicalConstantsModel();

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

        reader.moveDown();
        Map<String, BigDecimal> rhos = new HashMap<>();
        rhos = (HashMap) context.convertAnother(rhos, Map.class);
        reader.moveUp();
        model.setRhos(rhos);
        
        reader.moveDown();
        model.setMolarMasses(new HashMap<>());
        model.setMolarMasses((HashMap) context.convertAnother(model.getMolarMasses(), HashMap.class));
        reader.moveUp();

        return model;
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(PhysicalConstantsModel.class);
    }

}
