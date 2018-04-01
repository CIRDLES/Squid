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
package org.cirdles.squid.tasks.expressions.expressionTrees;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.ArrayList;
import java.util.List;
import org.cirdles.squid.tasks.expressions.OperationOrFunctionInterface;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import org.cirdles.squid.tasks.expressions.functions.Function;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.operations.Operation;
import org.cirdles.squid.tasks.expressions.spots.SpotFieldNode;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForIsotopicRatios;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForPerSpotTaskExpressions;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForSummary;

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

        writer.startNode("squidSpecialUPbThExpression");
        writer.setValue(String.valueOf(expressionTree.isSquidSpecialUPbThExpression()));
        writer.endNode();

        writer.startNode("rootExpressionTree");
        writer.setValue(String.valueOf(expressionTree.isRootExpressionTree()));
        writer.endNode();
        
        writer.startNode("squidSwitchConcentrationReferenceMaterialCalculation");
        writer.setValue(String.valueOf(expressionTree.isSquidSwitchConcentrationReferenceMaterialCalculation()));
        writer.endNode();
        
        writer.startNode("uncertaintyDirective");
        writer.setValue(expressionTree.getUncertaintyDirective());
        writer.endNode();
        
        writer.startNode("index");
        writer.setValue(String.valueOf(expressionTree.getIndex()));
        writer.endNode();
    }

    /**
     * reads a <code>ExpressionTree</code> from the XML file specified through
     * <code>reader</code>
     *
     * @pre     <code>reader</code> leads to a valid <code>ExpressionTree</code>
     * @post the <code>ExpressionTree</code> is read from the XML file and
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
////TODO CONFIRM FIELDS
        ExpressionTree expressionTree = new ExpressionTree();

        reader.moveDown();
        expressionTree.setName(reader.getValue());
        reader.moveUp();

        // now we have list of childrenET
        reader.moveDown();
        List<ExpressionTreeInterface> childrenET = new ArrayList<>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            ExpressionTreeInterface childExpressionTree = null;
            String expressionType = reader.getNodeName();
            switch (expressionType) {
                case "ExpressionTree":
                    childExpressionTree = new ExpressionTree();
                    childExpressionTree = (ExpressionTreeInterface) context.convertAnother(childExpressionTree, ExpressionTree.class);
                    break;
                case "ShrimpSpeciesNode":
                    childExpressionTree = ShrimpSpeciesNode.buildEmptyShrimpSpeciesNode();
                    childExpressionTree = (ExpressionTreeInterface) context.convertAnother(childExpressionTree, ShrimpSpeciesNode.class);
                    break;
                case "ConstantNode":
                    childExpressionTree = new ConstantNode();
                    childExpressionTree = (ExpressionTreeInterface) context.convertAnother(childExpressionTree, ConstantNode.class);
                    break;
                case "SpotFieldNode":
                    childExpressionTree = new SpotFieldNode();
                    childExpressionTree = (ExpressionTreeInterface) context.convertAnother(childExpressionTree, SpotFieldNode.class);
                    break;
                case "VariableNodeForSummary":
                    childExpressionTree = new VariableNodeForSummary();
                    childExpressionTree = (ExpressionTreeInterface) context.convertAnother(childExpressionTree, VariableNodeForSummary.class);
                    break;
                case "VariableNodeForPerSpotTaskExpressions":
                    childExpressionTree = new VariableNodeForPerSpotTaskExpressions();
                    childExpressionTree = (ExpressionTreeInterface) context.convertAnother(childExpressionTree, VariableNodeForPerSpotTaskExpressions.class);
                    break;
                case "VariableNodeForIsotopicRatios":
                    childExpressionTree = new VariableNodeForIsotopicRatios();
                    childExpressionTree = (ExpressionTreeInterface) context.convertAnother(childExpressionTree, VariableNodeForIsotopicRatios.class);
                    break;
            }
            childrenET.add(childExpressionTree);
            reader.moveUp();
        }
        expressionTree.setChildrenET(childrenET);
        reader.moveUp();

        // operation
        reader.moveDown();
        reader.moveDown();
        OperationOrFunctionInterface operation = Operation.operationFactory(reader.getValue());
        if (operation == null) {
            operation = Function.operationFactory(reader.getValue());
        }
        if (operation ==null){
            System.out.println("NULL OP  "+ expressionTree.getName() + "    " + reader.getValue());
        }
        expressionTree.setOperation(operation);
        reader.moveUp();
        reader.moveUp();

        // ratiosOfInterest
        reader.moveDown();
        List<String> ratiosOfInterest = new ArrayList<>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            ratiosOfInterest.add(reader.getValue());
            reader.moveUp();
        }
        expressionTree.setRatiosOfInterest(ratiosOfInterest);
        reader.moveUp();

        reader.moveDown();
        expressionTree.setSquidSwitchSCSummaryCalculation(Boolean.parseBoolean(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        expressionTree.setSquidSwitchSTReferenceMaterialCalculation(Boolean.parseBoolean(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        expressionTree.setSquidSwitchSAUnknownCalculation(Boolean.parseBoolean(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        expressionTree.setSquidSpecialUPbThExpression(Boolean.parseBoolean(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        expressionTree.setRootExpressionTree(Boolean.parseBoolean(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        expressionTree.setSquidSwitchConcentrationReferenceMaterialCalculation(Boolean.parseBoolean(reader.getValue()));
        reader.moveUp();
        
        reader.moveDown();
        expressionTree.setUncertaintyDirective(reader.getValue());
        reader.moveUp();
        
        reader.moveDown();
        expressionTree.setIndex(Integer.parseInt(reader.getValue()));
        reader.moveUp();
        
        return expressionTree;
    }

}
