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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.constants.Squid3Constants.IndexIsoptopesEnum;
import org.cirdles.squid.shrimp.MassStationDetail;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.shrimp.SquidSessionModel;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.tasks.expressions.Expression;

/**
 * A <code>TaskXMLConverter</code> is used to marshal and unmarshal data between
 * <code>Task</code> and XML files.
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
public class TaskXMLConverter implements Converter {

    /**
     * checks the argument <code>clazz</code> against <code>Task</code>'s
     * <code>Class</code>. Used to ensure that the object about to be
     * marshalled/unmarshalled is of the correct type.
     *
     * @pre argument <code>clazz</code> is a valid <code>Class</code>
     * @post    <code>boolean</code> is returned comparing <code>clazz</code>
     * against <code>Task.class</code>
     * @param clazz   <code>Class</code> of the <code>Object</code> you wish to
     * convert to/from XML
     * @return  <code>boolean</code> - <code>true</code> if <code>clazz</code>
     * matches <code>Task</code>'s <code>Class</code>; else <code>false</code>.
     */
    @Override
    public boolean canConvert(Class clazz) {
        return Task.class.isAssignableFrom(clazz);
    }

    /**
     * writes the argument <code>value</code> to the XML file specified through
     * <code>writer</code>
     *
     * @pre     <code>value</code> is a valid <code>Task</code>, <code>
     *          writer</code> is a valid <code>HierarchicalStreamWriter</code>, and
     * <code>context</code> is a valid <code>MarshallingContext</code>
     * @post    <code>value</code> is written to the XML file specified via
     * <code>writer</code>
     * @param value   <code>Task</code> that you wish to write to a file
     * @param writer stream to write through
     * @param context <code>MarshallingContext</code> used to store generic data
     */
    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer,
            MarshallingContext context) {
        
        TaskInterface task = (Task) value;
        
        writer.startNode("name");
        writer.setValue(task.getName());
        writer.endNode();
        
        writer.startNode("type");
        writer.setValue(task.getType());
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

//        writer.startNode("primaryParentElement");
//        writer.setValue(task.getPrimaryParentElement());
//        writer.endNode();
        writer.startNode("filterForRefMatSpotNames");
        writer.setValue(task.getFilterForRefMatSpotNames());
        writer.endNode();
        
        writer.startNode("filterForConcRefMatSpotNames");
        writer.setValue(task.getFilterForConcRefMatSpotNames());
        writer.endNode();
        
        writer.startNode("nominalMasses");
        List<String> nominalMasses = task.getNominalMasses();
        for (int i = 0; i < nominalMasses.size(); i++) {
            writer.startNode("nominalMass");
            writer.setValue(nominalMasses.get(i));
            writer.endNode();
        }
        writer.endNode();
        
        writer.startNode("ratioNames");
        List<String> ratioNames = task.getRatioNames();
        for (int i = 0; i < ratioNames.size(); i++) {
            writer.startNode("ratioName");
            writer.setValue(ratioNames.get(i));
            writer.endNode();
        }
        writer.endNode();
        
        writer.startNode("mapOfIndexToMassStationDetails");
        context.convertAnother(task.getMapOfIndexToMassStationDetails());
        writer.endNode();
        
        writer.startNode("SquidSessionModel");
        context.convertAnother(task.getSquidSessionModel());
        writer.endNode();
        
        writer.startNode("SquidSpeciesModelList");
        context.convertAnother(task.getSquidSpeciesModelList());
        writer.endNode();
        
        writer.startNode("SquidRatiosModelList");
        context.convertAnother(task.getSquidRatiosModelList());
        writer.endNode();
        
        boolean[][] tableOfSelectedRatiosByMassStationIndex = task.getTableOfSelectedRatiosByMassStationIndex();
        writer.startNode("tableOfSelectedRatiosByMassStationIndex");
        for (int i = 0; i < tableOfSelectedRatiosByMassStationIndex.length; i++) {
            writer.startNode("tableOfSelectedRatiosByMassStationIndexRow");
            for (int j = 0; j < tableOfSelectedRatiosByMassStationIndex[i].length; j++) {
                writer.startNode("ratioByMassStationIndex");
                writer.setValue(Boolean.toString(tableOfSelectedRatiosByMassStationIndex[i][j]));
                writer.endNode();
            }
            writer.endNode();
        }
        writer.endNode();
        
        writer.startNode("taskExpressionsOrdered");
//        List<Expression> taskExpressionsOrdered = task.getTaskExpressionsOrdered();
//        for (int i = 0; i < taskExpressionsOrdered.size(); i++) {
//            writer.startNode("Expression");
//            context.convertAnother(taskExpressionsOrdered.get(i));
//            writer.endNode();
//        }
        context.convertAnother(task.getTaskExpressionsOrdered());
        writer.endNode();
//
//        Map<String, ExpressionTreeInterface> namedExpressionsMap = task.getNamedExpressionsMap();
//        writer.startNode("namedExpressionsMap");
//        context.convertAnother(namedExpressionsMap);
//        writer.endNode();
//
//        Map<String, ExpressionTreeInterface> namedOvercountExpressionsMap = task.getNamedOvercountExpressionsMap();
//        writer.startNode("namedOvercountExpressionsMap");
//        context.convertAnother(namedOvercountExpressionsMap);
//        writer.endNode();
//
//        Map<String, ExpressionTreeInterface> namedConstantsMap = task.getNamedConstantsMap();
//        writer.startNode("namedConstantsMap");
//        context.convertAnother(namedConstantsMap);
//        writer.endNode();
//
//        Map<String, ExpressionTreeInterface> namedParametersMap = task.getNamedParametersMap();
//        writer.startNode("namedParametersMap");
//        context.convertAnother(namedParametersMap);
//        writer.endNode();
//        
//        writer.startNode("shrimpFractions");
//        context.convertAnother(task.getShrimpFractions());
//        writer.endNode();
//        
//        writer.startNode("referenceMaterialSpots");
//        context.convertAnother(task.getReferenceMaterialSpots());
//        writer.endNode();
//        
//        writer.startNode("concenctrationReferenceMaterialSpots");
//       context.convertAnother(task.getConcentrationReferenceMaterialSpots());
//       writer.endNode();
//       
//       writer.startNode("unknownSpots");
//       context.convertAnother(task.getUnknownSpots());
//       writer.endNode();
        writer.startNode("selectedIndexIsotope");
        context.convertAnother(task.getSelectedIndexIsotope());
        writer.endNode();
    }

    /**
     * reads a <code>Task</code> from the XML file specified through
     * <code>reader</code>
     *
     * @pre     <code>reader</code> leads to a valid <code>Task</code>
     * @post the <code>Task</code> is read from the XML file and returned
     * @param reader stream to read through
     * @param context <code>UnmarshallingContext</code> used to store generic
     * data
     * @return  <code>Task</code> - <code>Task</code> read from file specified by
     * <code>reader</code>
     */
    @Override
    
    public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {
        
        TaskInterface task = new Task();
        
        reader.moveDown();
        task.setName(reader.getValue());
        reader.moveUp();
        
        reader.moveDown();
        task.setType(reader.getValue());
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
        task.setDateRevised(Long.parseLong(reader.getValue()));
        reader.moveUp();
        
        reader.moveDown();
        task.setUseSBM(Boolean.parseBoolean(reader.getValue()));
        reader.moveUp();
        
        reader.moveDown();
        task.setUserLinFits(Boolean.parseBoolean(reader.getValue()));
        reader.moveUp();
        
        reader.moveDown();
        task.setIndexOfBackgroundSpecies(Integer.parseInt(reader.getValue()));
        reader.moveUp();
        
        reader.moveDown();
        task.setIndexOfTaskBackgroundMass(Integer.parseInt(reader.getValue()));
        reader.moveUp();
        
        reader.moveDown();
        task.setParentNuclide(reader.getValue());
        reader.moveUp();
        
        reader.moveDown();
        task.setDirectAltPD(Boolean.parseBoolean(reader.getValue()));
        reader.moveUp();

//        reader.moveDown();
//        task.setPrimaryParentElement(reader.getValue());
//        reader.moveUp();
        reader.moveDown();
        task.setFilterForRefMatSpotNames(reader.getValue());
        reader.moveUp();
        
        reader.moveDown();
        task.setFilterForConcRefMatSpotNames(reader.getValue());
        reader.moveUp();
        
        List<String> nominalMasses = new ArrayList<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            nominalMasses.add(reader.getValue());
            reader.moveUp();
        }
        reader.moveUp();
        task.setNominalMasses(nominalMasses);
        
        List<String> ratioNames = new ArrayList<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            ratioNames.add(reader.getValue());
            reader.moveUp();
        }
        reader.moveUp();
        task.setRatioNames(ratioNames);
        
        reader.moveDown();
        Map<Integer, MassStationDetail> mapOfIndexToMassStationDetails = new TreeMap<>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            int mapInt;
            reader.moveDown();
            mapInt = Integer.parseInt(reader.getValue());
            reader.moveUp();
            reader.moveDown();
            Object massStation = new Object();
            massStation = (MassStationDetail) context.convertAnother(massStation, MassStationDetail.class);
            reader.moveUp();
            reader.moveUp();
            mapOfIndexToMassStationDetails.put(mapInt, (MassStationDetail) massStation);
        }
        task.setMapOfIndexToMassStationDetails(mapOfIndexToMassStationDetails);
        reader.moveUp();
        
        SquidSessionModel squidSessionModel = task.getSquidSessionModel();
        reader.moveDown();
        squidSessionModel = (SquidSessionModel) context.convertAnother(squidSessionModel, SquidSessionModel.class);
        reader.moveUp();
        task.setSquidSessionModel(squidSessionModel);
        
        List<SquidSpeciesModel> squidSpeciesModelList = task.getSquidSpeciesModelList();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            SquidSpeciesModel squidSpeciesModel = new SquidSpeciesModel();
            reader.moveDown();
            squidSpeciesModel = (SquidSpeciesModel) context.convertAnother(squidSpeciesModel, SquidSpeciesModel.class);
            reader.moveUp();
            squidSpeciesModelList.add(squidSpeciesModel);
        }
        reader.moveUp();
        
        List<SquidRatiosModel> squidRatiosModelList = task.getSquidRatiosModelList();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            Object squidRatiosModel = new Object();
            reader.moveDown();
            squidRatiosModel = (SquidRatiosModel) context.convertAnother(squidRatiosModel, SquidRatiosModel.class);
            reader.moveUp();
            squidRatiosModelList.add((SquidRatiosModel) squidRatiosModel);
        }
        reader.moveUp();
        
        List<List<Boolean>> listOfSelectedRatiosByMassStationIndex = new LinkedList<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            List<Boolean> selectedRatioList = new LinkedList<>();
            reader.moveDown();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                selectedRatioList.add(Boolean.parseBoolean(reader.getValue()));
                reader.moveUp();
            }
            reader.moveUp();
            listOfSelectedRatiosByMassStationIndex.add(selectedRatioList);
        }
        reader.moveUp();
        if (listOfSelectedRatiosByMassStationIndex.size() > 0 && listOfSelectedRatiosByMassStationIndex.get(0).size() > 0) {
            boolean[][] tableOfSelectedRatiosByMassStationIndex = new boolean[listOfSelectedRatiosByMassStationIndex.size()][listOfSelectedRatiosByMassStationIndex.get(0).size()];
            for (int i = 0; i < tableOfSelectedRatiosByMassStationIndex.length; i++) {
                for (int j = 0; j < tableOfSelectedRatiosByMassStationIndex[0].length; j++) {
                    tableOfSelectedRatiosByMassStationIndex[i][j] = listOfSelectedRatiosByMassStationIndex.get(i).get(j);
                }
            }
            task.setTableOfSelectedRatiosByMassStationIndex(tableOfSelectedRatiosByMassStationIndex);
        }
        
        List<Expression> taskExpressions = new ArrayList<>();
        reader.moveDown();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            Expression exp = new Expression();
            exp = (Expression) context.convertAnother(exp, Expression.class);
            taskExpressions.add((Expression) exp);
            reader.moveUp();
        }
        task.setTaskExpressionsOrdered(taskExpressions);
        reader.moveUp();
//
//        Map<String, ExpressionTreeInterface> namedExpressionsMap = new TreeMap<>();
//        reader.moveDown();
//        namedExpressionsMap = (TreeMap) context.convertAnother(namedExpressionsMap, TreeMap.class);
//        reader.moveUp();
//        task.setNamedExpressionsMap(namedExpressionsMap);
//
//        Map<String, ExpressionTreeInterface> namedOvercountExpressionsMap = new TreeMap<>();
//        reader.moveDown();
//        namedOvercountExpressionsMap = (TreeMap) context.convertAnother(namedOvercountExpressionsMap, TreeMap.class);
//        reader.moveUp();
//        task.setNamedOvercountExpressionsMap(namedOvercountExpressionsMap);
//
//        Map<String, ExpressionTreeInterface> namedConstantsMap = new TreeMap<>();
//        reader.moveDown();
//        namedConstantsMap = (TreeMap) context.convertAnother(namedConstantsMap, TreeMap.class);
//        reader.moveUp();
//        task.setNamedConstantsMap(namedConstantsMap);
//
//        Map<String, ExpressionTreeInterface> namedParametersMap = new  TreeMap<>();
//        reader.moveDown();
//        namedParametersMap = (TreeMap) context.convertAnother(namedParametersMap, TreeMap.class);
//        reader.moveUp();
//        task.setNamedParametersMap(namedParametersMap);
//        
//        List<ShrimpFractionExpressionInterface> shrimpFractions = task.getShrimpFractions();
//        reader.moveDown();
//        while(reader.hasMoreChildren()) {
//            reader.moveDown();
//            ShrimpFractionExpressionInterface fraction = new ShrimpFraction();
//            fraction = (ShrimpFractionExpressionInterface) context.convertAnother(fraction, ShrimpFraction.class);
//            shrimpFractions.add(fraction);
//            reader.moveUp();
//        }
//        reader.moveUp();
//        
//        List<ShrimpFractionExpressionInterface> referenceMaterialSpots = task.getReferenceMaterialSpots();
//        reader.moveDown();
//        while(reader.hasMoreChildren()) {
//            reader.moveDown();
//            ShrimpFractionExpressionInterface fraction = new ShrimpFraction();
//            fraction = (ShrimpFractionExpressionInterface) context.convertAnother(fraction, ShrimpFraction.class);
//            referenceMaterialSpots.add(fraction);
//            reader.moveUp();
//        }
//        reader.moveUp();
//        
//        List<ShrimpFractionExpressionInterface> concentrationReferenceMaterialSpots = task.getConcentrationReferenceMaterialSpots();
//        reader.moveDown();
//        while(reader.hasMoreChildren()) {
//            reader.moveDown();
//            ShrimpFractionExpressionInterface fraction = new ShrimpFraction();
//            fraction = (ShrimpFractionExpressionInterface) context.convertAnother(fraction, ShrimpFraction.class);
//            concentrationReferenceMaterialSpots.add(fraction);
//            reader.moveUp();
//        }
//        reader.moveUp();
//        
//        List<ShrimpFractionExpressionInterface> unknownSpots = task.getUnknownSpots();
//        reader.moveDown();
//        while(reader.hasMoreChildren()) {
//            reader.moveDown();
//            ShrimpFractionExpressionInterface fraction = new ShrimpFraction();
//            fraction = (ShrimpFractionExpressionInterface) context.convertAnother(fraction, ShrimpFraction.class);
//            unknownSpots.add(fraction);
//            reader.moveUp();
//        }
//        reader.moveUp();
        reader.moveDown();
        task.setSelectedIndexIsotope(IndexIsoptopesEnum.PB_204);
        Object selectedIndexIsotope = context.convertAnother(task.getSelectedIndexIsotope(), Squid3Constants.IndexIsoptopesEnum.class);
        task.setSelectedIndexIsotope((Squid3Constants.IndexIsoptopesEnum) selectedIndexIsotope);
        reader.moveUp();
        
        return task;
    }
    
}
