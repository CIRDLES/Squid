/*
 * Copyright 2016 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.tasks.expressions.constants;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

/**
 * A <code>ConstantNodeXMLConverter</code> is used to marshal and unmarshal data
 * between <code>ConstantNode</code> and XML file.
 *
 * @author James F. Bowring, javaDocs by Stan Gasque
 * @imports <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/converters/Converter.html>
 * com.thoughtworks.xstream.converters.Converter</a>
 * @imports <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/converters/MarshallingContext.html>
 * com.thoughtworks.xstream.converters.MarhsallingContext</a>
 * @imports <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/converters/UnmarshallingContext.html>
 * com.thoughtworks.xstream.converters.UnmarshallingContext</a>
 * @imports <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/io/HierarchicalStreamReader.html>
 * com.thoughtworks.xstream.io.HierachicalSreamReader</a>
 * @imports <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/io/HierarchicalStreamWriter.html>
 * com.thoughtworks.xstream.io.HierarchicalStreamWriter</a>
 */
public class ConstantNodeXMLConverter implements Converter {

    /**
     * checks the argument <code>clazz</code> against
     * <code>ConstantNode</code>'s <code>Class</code>. Used to ensure that the
     * object about to be marshalled/unmarshalled is of the correct type.
     *
     * @param clazz <code>Class</code> of the <code>Object</code> you wish to
     *              convert to/from XML
     * @return <code>boolean</code> - <code>true</code> if <code>clazz</code>
     * matches <code>ConstantNode</code>'s <code>Class</code>; else
     * <code>false</code>.
     * @pre argument <code>clazz</code> is a valid <code>Class</code>
     * @post <code>boolean</code> is returned comparing <code>clazz</code>
     * against <code>ConstantNode.class</code>
     */
    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(ConstantNode.class);
    }

    /**
     * writes the argument <code>value</code> to the XML file specified through
     * <code>writer</code>
     *
     * @param value   <code>ConstantNode</code> that you wish to write to a file
     * @param writer  stream to write through
     * @param context <code>MarshallingContext</code> used to store generic data
     * @pre <code>value</code> is a valid <code>ConstantNode</code>, <code>
     * writer</code> is a valid <code>HierarchicalStreamWriter</code>, and
     * <code>context</code> is a valid <code>MarshallingContext</code>
     * @post <code>value</code> is written to the XML file specified via
     * <code>writer</code>
     */
    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer,
                        MarshallingContext context) {

        ExpressionTreeInterface constantNode = (ConstantNode) value;

        writer.startNode("name");
        writer.setValue(constantNode.getName());
        writer.endNode();

        writer.startNode("value");
        Object myValue = ((ConstantNode) constantNode).getValue();
        if (myValue instanceof Double) {
            writer.setValue(Double.toString((Double) ((ConstantNode) constantNode).getValue()));
        } else if (myValue instanceof Integer) {
            writer.setValue(Integer.toString((Integer) ((ConstantNode) constantNode).getValue()));
        } else if (myValue instanceof String) {
            writer.setValue((String) ((ConstantNode) constantNode).getValue());
        } else { // boolean
            writer.setValue(Boolean.toString((Boolean) ((ConstantNode) constantNode).getValue()));
        }
        writer.endNode();

    }

    /**
     * reads a <code>ConstantNode</code> from the XML file specified through
     * <code>reader</code>
     *
     * @param reader  stream to read through
     * @param context <code>UnmarshallingContext</code> used to store generic
     *                data
     * @return <code>ConstantNode</code> - <code>ConstantNode</code> read from
     * file specified by <code>reader</code>
     * @pre <code>reader</code> leads to a valid <code>ConstantNode</code>
     * @post the <code>ConstantNode</code> is read from the XML file and
     * returned
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
                            UnmarshallingContext context) {

        ExpressionTreeInterface constantNode = new ConstantNode();

        reader.moveDown();
        ((ConstantNode) constantNode).setName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        String constant = reader.getValue();
        if (constant.contains("e")) { // boolean
            ((ConstantNode) constantNode).setValue(Boolean.parseBoolean(reader.getValue()));
        } else if (constant.contains(".")) { // double
            ((ConstantNode) constantNode).setValue(Double.parseDouble(reader.getValue()));
        } else { // integer or string
            try {
                int myInt = Integer.parseInt(reader.getValue());
                ((ConstantNode) constantNode).setValue(myInt);
            } catch (NumberFormatException numberFormatException) {
                // String
                ((ConstantNode) constantNode).setValue(reader.getValue());
            }
        }

        reader.moveUp();

        return constantNode;
    }

}
