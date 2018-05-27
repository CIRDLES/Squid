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
import org.cirdles.squid.shrimp.SquidSpeciesModel;

/**
 *
 * @author ryanb
 */
public class SquidSpeciesModelXMLConverter implements Converter{

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {
        SquidSpeciesModel model = (SquidSpeciesModel) o;
        
        writer.startNode("massStationIndex");
        writer.setValue(Integer.toString(model.getMassStationIndex()));
        writer.endNode();
        
        writer.startNode("massStationSpeciesName");
        writer.setValue(model.getMassStationSpeciesName());
        writer.endNode();
        
        writer.startNode("isotopeName");
        writer.setValue(model.getIsotopeName());
        writer.endNode();
        
        writer.startNode("prawnFileIsotopeName");
        writer.setValue(model.getPrawnFileIsotopeName());
        writer.endNode();
        
        writer.startNode("elementName");
        writer.setValue(model.getElementName());
        writer.endNode();
        
        writer.startNode("isBackground");
        writer.setValue(Boolean.toString(model.getIsBackground()));
        writer.endNode();
        
        writer.startNode("uThBearingName");
        writer.setValue(model.getuThBearingName());
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {
        SquidSpeciesModel model = new SquidSpeciesModel();
        
        reader.moveDown();
        model.setMassStationIndex(Integer.parseInt(reader.getValue()));
        reader.moveUp();
        
        reader.moveDown();
        model.setMassStationSpeciesName(reader.getValue());
        reader.moveUp();
        
        reader.moveDown();
        model.setIsotopeName(reader.getValue());
        reader.moveUp();
        
        reader.moveDown();
        model.setPrawnFileIsotopeName(reader.getValue());
        reader.moveUp();
        
        reader.moveDown();
        model.setElementName(reader.getValue());
        reader.moveUp();
        
        reader.moveDown();
        model.setIsBackground(Boolean.parseBoolean(reader.getValue()));
        reader.moveUp();
        
        reader.moveDown();
        model.setuThBearingName(reader.getValue());
        reader.moveUp();
        
        return model;
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(SquidSpeciesModel.class);
    }
    
}
