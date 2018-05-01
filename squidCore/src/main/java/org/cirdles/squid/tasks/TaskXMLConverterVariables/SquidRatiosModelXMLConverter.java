/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.tasks.TaskXMLConverterVariables;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.List;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.shrimp.SquidSpeciesModel;

/**
 *
 * @author ryanb
 */
public class SquidRatiosModelXMLConverter implements Converter{

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext context) {
        SquidRatiosModel model = (SquidRatiosModel) o;
        
        writer.startNode("numerator");
        context.convertAnother(model.getNumerator());
        writer.endNode();
        
        writer.startNode("denominator");
        context.convertAnother(model.getDenominator());
        writer.endNode();
        
        writer.startNode("reportingOrderIndex");
        writer.setValue(Integer.toString(model.getReportingOrderIndex()));
        writer.endNode();
        
        writer.startNode("ratEqTimes");
        List<Double> ratEqTimes = model.getRatEqTime();
        for(int i = 0; i < ratEqTimes.size(); i++) {
            writer.startNode("ratEqTime");
            writer.setValue(String.valueOf(ratEqTimes.get(i)));
            writer.endNode();
        }
        writer.endNode();
        
        writer.startNode("ratEqVals");
        List<Double> ratEqVals = model.getRatEqVal();
        for(int i = 0; i < ratEqVals.size(); i++) {
            writer.startNode("ratEqVal");
            writer.setValue(String.valueOf(ratEqVals.get(i)));
            writer.endNode();
        }
        writer.endNode();
        
        writer.startNode("ratEqErrs");
        List<Double> ratEqErrs = model.getRatEqErr();
        for(int i =0; i < ratEqErrs.size(); i++) {
            writer.startNode("ratEqErr");
            writer.setValue(String.valueOf(ratEqErrs.get(i)));
            writer.endNode();
        }
        writer.endNode();
        
        writer.startNode("ratioVal");
        writer.setValue(String.valueOf(model.getRatioVal()));
        writer.endNode();
        
        writer.startNode("ratioFractErr");
        writer.setValue(String.valueOf(model.getRatioFractErr()));
        writer.endNode();
        
        writer.startNode("minIndex");
        writer.setValue(Integer.toString(model.getMinIndex()));
        writer.endNode();
        
        writer.startNode("active");
        writer.setValue(String.valueOf(model.isActive()));
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        reader.moveDown();
        SquidSpeciesModel numerator = new SquidSpeciesModel();
        numerator = (SquidSpeciesModel) context.convertAnother(numerator, SquidSpeciesModel.class);
        reader.moveUp();
        
        reader.moveDown();
        SquidSpeciesModel denominator = new SquidSpeciesModel();
        denominator = (SquidSpeciesModel) context.convertAnother(denominator, SquidSpeciesModel.class);
        reader.moveUp();
        
      reader.moveDown();
      int reportingOrderIndex = Integer.parseInt(reader.getValue());
      reader.moveUp();
      
      SquidRatiosModel model = new SquidRatiosModel(numerator, denominator, reportingOrderIndex);
      
      reader.moveDown();
      List<Double> ratEqTimes = model.getRatEqTime();
      while(reader.hasMoreChildren()) {
          reader.moveDown();
          ratEqTimes.add(Double.parseDouble(reader.getValue()));
          reader.moveUp();
      }
      reader.moveUp();
      
      reader.moveDown();
      List<Double> ratEqVals = model.getRatEqVal();
      while(reader.hasMoreChildren()) {
          reader.moveDown();
          ratEqVals.add(Double.parseDouble(reader.getValue()));
          reader.moveUp();
      }
      reader.moveUp();
      
      reader.moveDown();
      List<Double> ratEqErrs = model.getRatEqErr();
      while(reader.hasMoreChildren()) {
          reader.moveDown();
          ratEqErrs.add(Double.parseDouble(reader.getValue()));
          reader.moveUp();
      }
      reader.moveUp();
      
      reader.moveDown();
      model.setRatioVal(Double.parseDouble(reader.getValue()));
      reader.moveUp();
      
      reader.moveDown();
      model.setRatioFractErr(Double.parseDouble(reader.getValue()));
      reader.moveUp();
      
      reader.moveDown();
      model.setMinIndex(Integer.parseInt(reader.getValue()));
      reader.moveUp();
      
      reader.moveDown();
      model.setActive(Boolean.parseBoolean(reader.getValue()));
      reader.moveUp();
      
      return model;
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(SquidRatiosModel.class);
    }
    
}
