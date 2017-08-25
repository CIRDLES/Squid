/* 
 * Copyright 2006-2017 CIRDLES.org.
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

import org.cirdles.squid.tasks.expressions.isotopes.*;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * A <code>ShrimpSpeciesNodeXMLConverter</code> is used to marshal and unmarshal
 * data between <code>ShrimpSpeciesNode</code> and XML files.
 *
 * @imports
 * <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/converters/Converter.html>
 * com.thoughtworks.xstream.converters.Converter</a>
 * @imports
 * <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/converters/MarshallingContext.html>
 * com.thoughtworks.xstream.converters.MarhsallingContext</a>
 * @imports
 * <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/converters/UnmarshallingContext.html>
 * com.thoughtworks.xstream.converters.UnmarshallingContext</a>
 * @imports
 * <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/io/HierarchicalStreamReader.html>
 * com.thoughtworks.xstream.io.HierachicalSreamReader</a>
 * @imports
 * <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/io/HierarchicalStreamWriter.html>
 * com.thoughtworks.xstream.io.HierarchicalStreamWriter</a>
 * @author James F. Bowring, javaDocs by Stan Gasque
 */
public class SquidSpeciesModelXMLConverter implements Converter {

    /**
     * checks the argument <code>clazz</code> against
     * <code>ShrimpSpeciesNode</code>'s <code>Class</code>. Used to ensure that
     * the object about to be marshalled/unmarshalled is of the correct type.
     *
     * @pre argument <code>clazz</code> is a valid <code>Class</code>
     * @post    <code>boolean</code> is returned comparing <code>clazz</code>
     * against <code>ShrimpSpeciesNode.class</code>
     * @param clazz   <code>Class</code> of the <code>Object</code> you wish to
     * convert to/from XML
     * @return  <code>boolean</code> - <code>true</code> if <code>clazz</code>
     * matches <code>ShrimpSpeciesNode</code>'s <code>Class</code>; else
     * <code>false</code>.
     */
    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(SquidSpeciesModel.class);
    }

    /**
     * writes the argument <code>value</code> to the XML file specified through
     * <code>writer</code>
     *
     * @pre     <code>value</code> is a valid <code>ShrimpSpeciesNode</code>, <code>
     *          writer</code> is a valid <code>HierarchicalStreamWriter</code>, and
     * <code>context</code> is a valid <code>MarshallingContext</code>
     * @post    <code>value</code> is written to the XML file specified via
     * <code>writer</code>
     * @param value   <code>ShrimpSpeciesNode</code> that you wish to write to a
     * file
     * @param writer stream to write through
     * @param context <code>MarshallingContext</code> used to store generic data
     */
    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer,
            MarshallingContext context) {

        SquidSpeciesModel squidSpeciesModel = (SquidSpeciesModel) value;

        writer.startNode("massStationIndex");
        writer.setValue(String.valueOf(squidSpeciesModel.getMassStationIndex()));
        writer.endNode();

        writer.startNode("massStationSpeciesName");
        writer.setValue(squidSpeciesModel.getMassStationSpeciesName());
        writer.endNode();

        writer.startNode("isotopeName");
        writer.setValue(squidSpeciesModel.getIsotopeName());
        writer.endNode();

        writer.startNode("elementName");
        writer.setValue(squidSpeciesModel.getElementName());
        writer.endNode();
        
        writer.startNode("isBackground");
        writer.setValue(String.valueOf(squidSpeciesModel.getIsBackground()));
        writer.endNode();

    }

    /**
     * reads a <code>shrimpSpeciesNode</code> from the XML file specified
     * through <code>reader</code>
     *
     * @pre     <code>reader</code> leads to a valid <code>ShrimpSpeciesNode</code>
     * @post the <code>ShrimpSpeciesNode</code> is read from the XML file and
     * returned
     * @param reader stream to read through
     * @param context <code>UnmarshallingContext</code> used to store generic
     * data
     * @return  <code>ShrimpSpeciesNode</code> - <code>ShrimpSpeciesNode</code>
     * read from file specified by <code>reader</code>
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {

        SquidSpeciesModel squidSpeciesModel = new SquidSpeciesModel();

        reader.moveDown();

        reader.moveUp();

        return squidSpeciesModel;
    }

}
