/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.parameters.valueModels;

import org.cirdles.squid.parameters.*;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.math.BigDecimal;

/**
 *
 * @author ryanb
 */
public class ValueModelConverter implements Converter {

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {
        ValueModel model = (ValueModel) o;

        writer.startNode("name");
        writer.setValue(model.getName());
        writer.endNode();

        writer.startNode("uncertaintyType");
        writer.setValue(model.getUncertaintyType());
        writer.endNode();

        writer.startNode("reference");
        writer.setValue(model.getReference());
        writer.endNode();

        writer.startNode("value");
        writer.setValue(model.getValue().toPlainString());
        writer.endNode();

        writer.startNode("oneSigma");
        writer.setValue(model.getOneSigma().toPlainString());
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {
        ValueModel model = new ValueModel();

        reader.moveDown();
        model.setName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        model.setUncertaintyType(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        model.setReference(reader.getValue());
        reader.moveUp();

        String currBigDec;
        reader.moveDown();
        currBigDec = reader.getValue();
        if (Double.parseDouble(currBigDec) == 0.0) {
            model.setValue(BigDecimal.ZERO);
        } else {
            model.setValue(new BigDecimal(currBigDec));
        }
        reader.moveUp();

        reader.moveDown();
        currBigDec = reader.getValue();
        if (Double.parseDouble(currBigDec) == 0.0) {
            model.setOneSigma(BigDecimal.ZERO);
        } else {
            model.setOneSigma(new BigDecimal(currBigDec));
        }
        reader.moveUp();

        return model;
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(ValueModel.class);
    }

}
