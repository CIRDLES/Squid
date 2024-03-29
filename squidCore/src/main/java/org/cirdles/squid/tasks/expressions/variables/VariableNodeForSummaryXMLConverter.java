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
package org.cirdles.squid.tasks.expressions.variables;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

/**
 * A <code>VariableNodeForSummaryXMLConverter</code> is used to marshal and unmarshal data
 * between <code>VariableNodeForSummary</code> and XML files.
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
public class VariableNodeForSummaryXMLConverter implements Converter {

    /**
     * checks the argument <code>clazz</code> against <code>VariableNodeForSummary</code>'s
     * <code>Class</code>. Used to ensure that the object about to be
     * marshalled/unmarshalled is of the correct type.
     *
     * @param clazz <code>Class</code> of the <code>Object</code> you wish to
     *              convert to/from XML
     * @return <code>boolean</code> - <code>true</code> if <code>clazz</code>
     * matches <code>VariableNodeForSummary</code>'s <code>Class</code>; else
     * <code>false</code>.
     * @pre argument <code>clazz</code> is a valid <code>Class</code>
     * @post <code>boolean</code> is returned comparing <code>clazz</code>
     * against <code>VariableNodeForSummary.class</code>
     */
    @Override
    public boolean canConvert(Class clazz) {
        return VariableNodeForSummary.class.isAssignableFrom(clazz);
    }

    /**
     * writes the argument <code>value</code> to the XML file specified through
     * <code>writer</code>
     *
     * @param value   <code>VariableNodeForSummary</code> that you wish to write to a file
     * @param writer  stream to write through
     * @param context <code>MarshallingContext</code> used to store generic data
     * @pre <code>value</code> is a valid <code>VariableNodeForSummary</code>, <code>
     * writer</code> is a valid <code>HierarchicalStreamWriter</code>, and
     * <code>context</code> is a valid <code>MarshallingContext</code>
     * @post <code>value</code> is written to the XML file specified via
     * <code>writer</code>
     */
    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer,
                        MarshallingContext context) {

        VariableNodeForSummary variable = (VariableNodeForSummary) value;

        writer.startNode("name");
        writer.setValue(variable.getName());
        writer.endNode();
    }

    /**
     * reads a <code>VariableNodeForSummary</code> from the XML file specified through
     * <code>reader</code>
     *
     * @param reader  stream to read through
     * @param context <code>UnmarshallingContext</code> used to store generic
     *                data
     * @return <code>VariableNodeForSummary</code> - <code>VariableNodeForSummary</code> read from file
     * specified by <code>reader</code>
     * @pre <code>reader</code> leads to a valid <code>Operation</code>
     * @post the <code>VariableNodeForSummary</code> is read from the XML file and returned
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
                            UnmarshallingContext context) {

        ExpressionTreeInterface variable = null;
        String variableType = reader.getNodeName();
        reader.moveDown();
        String variableName = reader.getValue();
        reader.moveUp();
        switch (variableType) {
            case "VariableNodeForSummary":
                variable = new VariableNodeForSummary(variableName);
                break;
            case "VariableNodeForPerSpotTaskExpressions":
                variable = new VariableNodeForPerSpotTaskExpressions(variableName);
                break;
            case "VariableNodeForIsotopicRatios":
                variable = new VariableNodeForIsotopicRatios(variableName);
                break;
        }

        return variable;
    }

}