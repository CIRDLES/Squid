/* 
 * Copyright 2018 James F. Bowring and CIRDLES.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.squid.shrimp;

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
            writer.setValue(Double.toString(ratEqTimes.get(i)));
            writer.endNode();
        }
        writer.endNode();
        
        writer.startNode("ratEqVals");
        List<Double> ratEqVals = model.getRatEqVal();
        for(int i = 0; i < ratEqVals.size(); i++) {
            writer.startNode("ratEqVal");
            writer.setValue(Double.toString(ratEqVals.get(i)));
            writer.endNode();
        }
        writer.endNode();
        
        writer.startNode("ratEqErrs");
        List<Double> ratEqErrs = model.getRatEqErr();
        for(int i =0; i < ratEqErrs.size(); i++) {
            writer.startNode("ratEqErr");
            writer.setValue(Double.toString(ratEqErrs.get(i)));
            writer.endNode();
        }
        writer.endNode();
        
        writer.startNode("ratioVal");
        writer.setValue(Double.toString(model.getRatioVal()));
        writer.endNode();
        
        writer.startNode("ratioFractErr");
        writer.setValue(Double.toString(model.getRatioFractErr()));
        writer.endNode();
        
        writer.startNode("minIndex");
        writer.setValue(Integer.toString(model.getMinIndex()));
        writer.endNode();
        
        writer.startNode("active");
        writer.setValue(Boolean.toString(model.isActive()));
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
