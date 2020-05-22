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
package org.cirdles.squid.tasks;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTable;
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTableInterface;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

/**
 * A <code>TaskXMLConverter</code> is used to marshal and unmarshal data between
 * <code>Task</code> and XML files.
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
public class TaskXMLConverter implements Converter {

    /**
     * checks the argument <code>clazz</code> against <code>Task</code>'s
     * <code>Class</code>. Used to ensure that the object about to be
     * marshalled/unmarshalled is of the correct taskType.
     *
     * @param clazz <code>Class</code> of the <code>Object</code> you wish to
     *              convert to/from XML
     * @return <code>boolean</code> - <code>true</code> if <code>clazz</code>
     * matches <code>Task</code>'s <code>Class</code>; else <code>false</code>.
     * @pre argument <code>clazz</code> is a valid <code>Class</code>
     * @post <code>boolean</code> is returned comparing <code>clazz</code>
     * against <code>Task.class</code>
     */
    @Override
    public boolean canConvert(Class clazz) {
        return Task.class.isAssignableFrom(clazz);
    }

    /**
     * writes the argument <code>value</code> to the XML file specified through
     * <code>writer</code>
     *
     * @param value   <code>Task</code> that you wish to write to a file
     * @param writer  stream to write through
     * @param context <code>MarshallingContext</code> used to store generic data
     * @pre <code>value</code> is a valid <code>Task</code>, <code>
     * writer</code> is a valid <code>HierarchicalStreamWriter</code>, and
     * <code>context</code> is a valid <code>MarshallingContext</code>
     * @post <code>value</code> is written to the XML file specified via
     * <code>writer</code>
     */
    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer,
                        MarshallingContext context) {

        TaskInterface task = (Task) value;

        writer.startNode("name");
        writer.setValue(task.getName());
        writer.endNode();

        writer.startNode("taskSquidVersion");
        writer.setValue(task.getTaskSquidVersion());
        writer.endNode();

        writer.startNode("type");
        context.convertAnother(task.getTaskType());
        writer.endNode();

        writer.startNode("description");
        writer.setValue(task.getDescription());
        writer.endNode();

        writer.startNode("authorName");
        writer.setValue(task.getAuthorName());
        writer.endNode();

        writer.startNode("labName");
        writer.setValue(task.getLabName());
        writer.endNode();

        writer.startNode("provenance");
        writer.setValue(task.getProvenance());
        writer.endNode();

        writer.startNode("dateRevised");
        writer.setValue(Long.toString(task.getDateRevised()));
        writer.endNode();

        writer.startNode("useSBM");
        writer.setValue(Boolean.toString(task.isUseSBM()));
        writer.endNode();

        writer.startNode("userLinFits");
        writer.setValue(Boolean.toString(task.isUserLinFits()));
        writer.endNode();

        writer.startNode("indexOfBackgroundSpecies");
        writer.setValue(Integer.toString(task.getIndexOfBackgroundSpecies()));
        writer.endNode();

        writer.startNode("indexOfTaskBackgroundMass");
        writer.setValue(Integer.toString(task.getIndexOfTaskBackgroundMass()));
        writer.endNode();

        writer.startNode("parentNuclide");
        writer.setValue(task.getParentNuclide());
        writer.endNode();

        writer.startNode("directAltPD");
        writer.setValue(Boolean.toString(task.isDirectAltPD()));
        writer.endNode();

        writer.startNode("filterForRefMatSpotNames");
        writer.setValue(task.getFilterForRefMatSpotNames());
        writer.endNode();

        writer.startNode("filterForConcRefMatSpotNames");
        writer.setValue(task.getFilterForConcRefMatSpotNames());
        writer.endNode();

        writer.startNode("nominalMasses");
        context.convertAnother(task.getNominalMasses());
        writer.endNode();

        writer.startNode("ratioNames");
        context.convertAnother(task.getRatioNames());
        writer.endNode();

        writer.startNode("taskExpressionsOrdered");
        context.convertAnother(task.getTaskExpressionsOrdered());
        writer.endNode();

        writer.startNode("taskExpressionsRemoved");
        context.convertAnother(task.getTaskExpressionsRemoved());
        writer.endNode();

        writer.startNode("namedExpressionsMap");
        writeOutExpressionTreeHashMap(writer, context, task.getNamedExpressionsMap());
        writer.endNode();

        writer.startNode("namedOvercountExpressionsMap");
        writeOutExpressionTreeHashMap(writer, context, task.getNamedOvercountExpressionsMap());
        writer.endNode();

        writer.startNode("namedConstantsMap");
        writeOutExpressionTreeHashMap(writer, context, task.getNamedConstantsMap());
        writer.endNode();

        writer.startNode("namedParametersMap");
        writeOutExpressionTreeHashMap(writer, context, task.getNamedParametersMap());
        writer.endNode();

        writer.startNode("namedSpotLookupFieldsMap");
        writeOutExpressionTreeHashMap(writer, context, task.getNamedSpotLookupFieldsMap());
        writer.endNode();

        writer.startNode("useCalculatedAv_ParentElement_ConcenConst");
        writer.setValue(Boolean.toString(task.isUseCalculatedAv_ParentElement_ConcenConst()));
        writer.endNode();

        writer.startNode("selectedIndexIsotope");
        context.convertAnother(task.getSelectedIndexIsotope());
        writer.endNode();

        writer.startNode("squidAllowsAutoExclusionOfSpots");
        writer.setValue(Boolean.toString(task.isSquidAllowsAutoExclusionOfSpots()));
        writer.endNode();

        writer.startNode("specialSquidFourExpressionsMap");
        task.getSpecialSquidFourExpressionsMap().forEach((key, val) -> {
            writer.startNode("entry");

            writer.startNode("key");
            writer.setValue(key);
            writer.endNode();

            writer.startNode("value");
            writer.setValue(val);
            writer.endNode();

            writer.endNode();
        });
        writer.endNode();

        writer.startNode("squidReportTablesRefMat");
        context.convertAnother(task.getSquidReportTablesRefMat());
        writer.endNode();

        writer.startNode("squidReportTablesUnknown");
        context.convertAnother(task.getSquidReportTablesUnknown());
        writer.endNode();
    }

    /**
     * reads a <code>Task</code> from the XML file specified through
     * <code>reader</code>
     *
     * @param reader  stream to read through
     * @param context <code>UnmarshallingContext</code> used to store generic
     *                data
     * @return <code>Task</code> - <code>Task</code> read from file specified by
     * <code>reader</code>
     * @pre <code>reader</code> leads to a valid <code>Task</code>
     * @post the <code>Task</code> is read from the XML file and returned
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
                            UnmarshallingContext context) {

        TaskInterface task = new Task();

        reader.moveDown();
        task.setName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        task.setTaskSquidVersion(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        task.setTaskType(Squid3Constants.TaskTypeEnum.valueOf(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        task.setDescription(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        task.setAuthorName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        task.setLabName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        task.setProvenance(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        task.setDateRevised(Long.valueOf(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        task.setUseSBM(Boolean.valueOf(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        task.setUserLinFits(Boolean.valueOf(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        task.setIndexOfBackgroundSpecies(Integer.valueOf(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        task.setIndexOfTaskBackgroundMass(Integer.valueOf(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        task.setParentNuclide(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        task.setDirectAltPD(Boolean.valueOf(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        task.setFilterForRefMatSpotNames(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        task.setFilterForConcRefMatSpotNames(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        task.setNominalMasses((List) context.convertAnother(task.getNominalMasses(), List.class));
        reader.moveUp();

        reader.moveDown();
        task.setRatioNames((List) context.convertAnother(task.getRatioNames(), List.class));
        reader.moveUp();

        reader.moveDown();
        task.setTaskExpressionsOrdered((List) context.convertAnother(task.getTaskExpressionsOrdered(), List.class));
        reader.moveUp();

        reader.moveDown();
        task.setTaskExpressionsRemoved((SortedSet) context.convertAnother(task.getTaskExpressionsRemoved(), SortedSet.class));
        reader.moveUp();

        reader.moveDown();
        readInExpressionTreeHashMap(reader, context, task.getNamedExpressionsMap());
        reader.moveUp();

        reader.moveDown();
        readInExpressionTreeHashMap(reader, context, task.getNamedOvercountExpressionsMap());
        reader.moveUp();

        reader.moveDown();
        readInExpressionTreeHashMap(reader, context, task.getNamedConstantsMap());
        reader.moveUp();

        reader.moveDown();
        readInExpressionTreeHashMap(reader, context, task.getNamedParametersMap());
        reader.moveUp();

        reader.moveDown();
        readInExpressionTreeHashMap(reader, context, task.getNamedSpotLookupFieldsMap());
        reader.moveUp();

        reader.moveDown();
        task.setUseCalculatedAv_ParentElement_ConcenConst(Boolean.valueOf(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        task.setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum.valueOf(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        task.setSquidAllowsAutoExclusionOfSpots(Boolean.valueOf(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        Map<String, String> specialSquidFourExpressionsMap = task.getSpecialSquidFourExpressionsMap();
        while (reader.hasMoreChildren()) {
            String key;
            String val;

            reader.moveDown();

            reader.moveDown();
            key = reader.getValue();
            reader.moveUp();

            reader.moveDown();
            val = reader.getValue();
            reader.moveUp();

            reader.moveUp();

            specialSquidFourExpressionsMap.put(key, val);
        }
        reader.moveUp();

        reader.moveDown();
        task.setSquidReportTablesRefMat((List) context.convertAnother(task.getSquidReportTablesRefMat(), List.class));
        reader.moveUp();

        reader.moveDown();
        task.setSquidReportTablesUnknown((List) context.convertAnother(task.getSquidReportTablesUnknown(), List.class));
        reader.moveUp();

        return task;
    }

    private static final void writeOutExpressionTreeHashMap(HierarchicalStreamWriter writer, MarshallingContext context, Map<String, ExpressionTreeInterface> map) {
        map.forEach((key, val) -> {
            if(val.amHealthy()) {
                writer.startNode("entry");

                writer.startNode("key");
                writer.setValue(key);
                writer.endNode();

                writer.startNode(val.getClass().getName());
                context.convertAnother(val);
                writer.endNode();

                writer.endNode();
            }
        });
    }

    private static final void readInExpressionTreeHashMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map<String, ExpressionTreeInterface> map) {
        while (reader.hasMoreChildren()) {
            String key;
            ExpressionTreeInterface expTree = new ExpressionTree();

            reader.moveDown();

            reader.moveDown();
            key = reader.getValue();
            reader.moveUp();

            reader.moveDown();
            try {
                expTree = (ExpressionTree) context.convertAnother(expTree, Class.forName(reader.getNodeName()));
            } catch (ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
            reader.moveUp();

            reader.moveUp();

            map.put(key, expTree);
        }
    }

}
