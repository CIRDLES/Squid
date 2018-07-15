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
        
        context.convertAnother((ParametersModel) model);
        
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
        
        model = (ReferenceMaterial) context.convertAnother(model, ParametersModel.class);
        
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
