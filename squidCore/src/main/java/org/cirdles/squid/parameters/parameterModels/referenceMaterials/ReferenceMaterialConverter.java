/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.parameters.parameterModels.referenceMaterials;

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
public class ReferenceMaterialConverter implements Converter {

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext context) {
        ReferenceMaterial model = (ReferenceMaterial) o;

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
        
        writer.startNode("concentrations");
        context.convertAnother(model.getConcentrations());
        writer.endNode();

        writer.startNode("dataMeasured");
        context.convertAnother(model.getDataMeasured());
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        ReferenceMaterial model = new ReferenceMaterial();

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
        model.setConcentrations(new ValueModel[0]);
        model.setConcentrations((ValueModel[]) context.convertAnother(model.getConcentrations(), ValueModel[].class));
        reader.moveUp();

        reader.moveDown();
        model.setDataMeasured(new boolean[0]);
        model.setDataMeasured((boolean[]) context.convertAnother(model.getDataMeasured(), boolean[].class));
        reader.moveUp();

        return model;
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(ReferenceMaterial.class);
    }
}
