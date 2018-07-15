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
import java.util.HashMap;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;

/**
 *
 * @author ryanb
 */
public class PhysicalConstantsModelConverter implements Converter {

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext context) {
        PhysicalConstantsModel model = (PhysicalConstantsModel) o;

        context.convertAnother((ParametersModel) model);

        writer.startNode("molarMasses");
        context.convertAnother(model.getMolarMasses());
        writer.endNode();   
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        PhysicalConstantsModel model = new PhysicalConstantsModel();
        
        model = (PhysicalConstantsModel) context.convertAnother(model, ParametersModel.class);
        
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
