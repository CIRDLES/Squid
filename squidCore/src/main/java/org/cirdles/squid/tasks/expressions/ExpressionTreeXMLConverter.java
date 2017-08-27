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
package org.cirdles.squid.tasks.expressions;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.operations.Operation;

/**
 * A <code>ExpressionTreeXMLConverter</code> is used to marshal and unmarshal
 * data between <code>ExpressionTree</code> and XML file.
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
public class ExpressionTreeXMLConverter implements Converter {

    /**
     * checks the argument <code>clazz</code> against
     * <code>ExpressionTree</code>'s <code>Class</code>. Used to ensure that the
     * object about to be marshalled/unmarshalled is of the correct type.
     *
     * @pre argument <code>clazz</code> is a valid <code>Class</code>
     * @post    <code>boolean</code> is returned comparing <code>clazz</code>
     * against <code>ExpressionTree.class</code>
     * @param clazz   <code>Class</code> of the <code>Object</code> you wish to
     * convert to/from XML
     * @return  <code>boolean</code> - <code>true</code> if <code>clazz</code>
     * matches <code>ExpressionTree</code>'s <code>Class</code>; else
     * <code>false</code>.
     */
    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(ExpressionTree.class);
    }

    /**
     * writes the argument <code>value</code> to the XML file specified through
     * <code>writer</code>
     *
     * @pre     <code>value</code> is a valid <code>ExpressionTree</code>, <code>
     *          writer</code> is a valid <code>HierarchicalStreamWriter</code>, and
     * <code>context</code> is a valid <code>MarshallingContext</code>
     * @post    <code>value</code> is written to the XML file specified via
     * <code>writer</code>
     * @param value   <code>ExpressionTree</code> that you wish to write to a file
     * @param writer stream to write through
     * @param context <code>MarshallingContext</code> used to store generic data
     */
    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer,
            MarshallingContext context) {

        ExpressionTree expressionTree = (ExpressionTree) value;

        writer.startNode("name");
        writer.setValue(expressionTree.getName());
        writer.endNode();

        writer.startNode("childrenET");
        context.convertAnother(expressionTree.getChildrenET());
        writer.endNode();

        writer.startNode("operation");
        context.convertAnother(expressionTree.getOperation());
        writer.endNode();

        writer.startNode("ratiosOfInterest");
        context.convertAnother(expressionTree.getRatiosOfInterest());
        writer.endNode();

        writer.startNode("squidSwitchSCSummaryCalculation");
        writer.setValue(String.valueOf(expressionTree.isSquidSwitchSCSummaryCalculation()));
        writer.endNode();

        writer.startNode("squidSwitchSTReferenceMaterialCalculation");
        writer.setValue(String.valueOf(expressionTree.isSquidSwitchSTReferenceMaterialCalculation()));
        writer.endNode();

        writer.startNode("squidSwitchSAUnknownCalculation");
        writer.setValue(String.valueOf(expressionTree.isSquidSwitchSAUnknownCalculation()));
        writer.endNode();

        writer.startNode("rootExpressionTree");
        writer.setValue(String.valueOf(expressionTree.isRootExpressionTree()));
        writer.endNode();

    }

    /**
     * reads a <code>ExpressionTree</code> from the XML file specified through
     * <code>reader</code>
     *
     * @pre     <code>reader</code> leads to a valid <code>ConstantNode</code>
     * @post the <code>ConstantNode</code> is read from the XML file and
     * returned
     * @param reader stream to read through
     * @param context <code>UnmarshallingContext</code> used to store generic
     * data
     * @return  <code>ExpressionTree</code> - <code>ExpressionTree</code> read
     * from file specified by <code>reader</code>
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {

        ExpressionTree expressionTree = new ExpressionTree();

        reader.moveDown();
        expressionTree.setName(reader.getValue());
        reader.moveUp();

        // leftET
        ExpressionTreeInterface leftET = null;
        reader.moveDown();
        reader.moveDown();
        String etType = reader.getNodeName();
        if (etType.compareToIgnoreCase("ExpressionTree") == 0) {
            leftET = new ExpressionTree();
            leftET = (ExpressionTreeInterface) context.convertAnother(leftET, ExpressionTree.class);
        } else if (etType.compareToIgnoreCase("ShrimpSpeciesNode") == 0) {
            leftET = new ShrimpSpeciesNode();
            leftET = (ExpressionTreeInterface) context.convertAnother(leftET, ShrimpSpeciesNode.class);
        } else if (etType.compareToIgnoreCase("ConstantNode") == 0) {
            leftET = new ConstantNode();
            leftET = (ExpressionTreeInterface) context.convertAnother(leftET, ConstantNode.class);
        }
        reader.moveUp();
        reader.moveUp();
        expressionTree.addChild(0, leftET);

        // rightET
        ExpressionTreeInterface rightET = null;
        reader.moveDown();
        if (reader.hasMoreChildren()) {
            reader.moveDown();
            etType = reader.getNodeName();
            if (etType.compareToIgnoreCase("ExpressionTree") == 0) {
                rightET = new ExpressionTree();
                rightET = (ExpressionTreeInterface) context.convertAnother(rightET, ExpressionTree.class);
            } else if (etType.compareToIgnoreCase("ShrimpSpeciesNode") == 0) {
                rightET = new ShrimpSpeciesNode();
                rightET = (ExpressionTreeInterface) context.convertAnother(rightET, ShrimpSpeciesNode.class);
            } else if (etType.compareToIgnoreCase("ConstantNode") == 0) {
                rightET = new ConstantNode();
                rightET = (ExpressionTreeInterface) context.convertAnother(rightET, ConstantNode.class);
            }
            reader.moveUp();
        }
        reader.moveUp();
        expressionTree.addChild(rightET);

        // operation
        reader.moveDown();
        reader.moveDown();
        OperationOrFunctionInterface operation = Operation.operationFactory(reader.getValue());
        expressionTree.setOperation(operation);
        reader.moveUp();
        reader.moveUp();

        // ratiosOfInterest
        reader.moveDown();
////        List<RawRatioNamesSHRIMP> ratiosOfInterest = new ArrayList<>();
        while (reader.hasMoreChildren()) {
            reader.moveDown(); // into ratio element
            reader.moveDown(); // into name element
////            ratiosOfInterest.add(RawRatioNamesSHRIMP.valueOf(reader.getValue()));
            reader.moveUp();
            reader.moveUp();
        }
//////        expressionTree.setRatiosOfInterest(ratiosOfInterest);
        reader.moveUp();

        return expressionTree;
    }

}
