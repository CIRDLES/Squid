/*
 * Copyright 2017 CIRDLES.org.
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

import com.thoughtworks.xstream.XStream;
import java.util.List;
import java.util.Map;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.shrimp.MassStationDetail;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.shrimp.SquidSessionModel;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
public interface TaskInterface {

    public ExpressionTreeInterface findNamedExpression(String ratioName);

    public Expression generateExpressionFromRawExcelStyleText(String name, String originalExpressionText);

    public void buildSquidRatiosModelListFromMassStationDetails();

    public void createMapOfIndexToMassStationDetails();

    /**
     *
     * @param xstream
     */
    public void customizeXstream(XStream xstream);

    /**
     *
     * @param shrimpFractions
     */
    public void evaluateTaskExpressions(List<ShrimpFractionExpressionInterface> shrimpFractions);

    public void populateTableOfSelectedRatiosFromRatiosList();

    /**
     * @return the authorName
     */
    public String getAuthorName();

    /**
     * @return the dateRevised
     */
    public long getDateRevised();

    /**
     * @return the description
     */
    public String getDescription();

    /**
     * @return the labName
     */
    public String getLabName();

    /**
     * @return the mapOfIndexToMassStationDetails
     */
    public Map<Integer, MassStationDetail> getMapOfIndexToMassStationDetails();

    /**
     * @return the name
     */
    public String getName();

    /**
     * @return the provenance
     */
    public String getProvenance();

    /**
     * @return the ratioNames
     */
    public List<String> getRatioNames();

    /**
     * @return the prawnFile
     */
    public PrawnFile getPrawnFile();

    /**
     * @param prawnFile the prawnFile to set
     */
    public void setPrawnFile(PrawnFile prawnFile);
    
    public SquidSpeciesModel lookUpSpeciesByName(String isotopeName);

    /**
     * @return the squidRatiosModelList
     */
    List<SquidRatiosModel> getSquidRatiosModelList();

    /**
     * @return the squidSpeciesModelList
     */
    List<SquidSpeciesModel> getSquidSpeciesModelList();

    /**
     * @return the tableOfSelectedRatiosByMassStationIndex
     */
    boolean[][] getTableOfSelectedRatiosByMassStationIndex();

    /**
     * @return the taskExpressionsEvaluationsPerSpotSet
     */
    Map<String, SpotSummaryDetails> getTaskExpressionsEvaluationsPerSpotSet();

    /**
     * @return the taskExpressionsOrdered
     */
    List<ExpressionTree> getTaskExpressionTreesOrdered();

    /**
     * @return the type
     */
    String getType();

    boolean isEmptyTableOfSelectedRatiosByMassStationIndex();

    /**
     * @return the mapOfIndexToMassStationDetails
     */
    List<MassStationDetail> makeListOfMassStationDetails();

    String printSummaryData();

    void resetTableOfSelectedRatiosByMassStationIndex();

    public int selectBackgroundSpeciesReturnPreviousIndex(SquidSpeciesModel ssm);

    /**
     * @param authorName the authorName to set
     */
    public void setAuthorName(String authorName);

    /**
     * @param dateRevised the dateRevised to set
     */
    public void setDateRevised(long dateRevised);

    /**
     * @param description the description to set
     */
    public void setDescription(String description);

    /**
     * @param labName the labName to set
     */
    public void setLabName(String labName);

    /**
     * @param name the name to set
     */
    public void setName(String name);

    /**
     * @param provenance the provenance to set
     */
    public void setProvenance(String provenance);

    /**
     * @param ratioNames the ratioNames to set
     */
    public void setRatioNames(List<String> ratioNames);

    /**
     * @param taskExpressionsEvaluationsPerSpotSet the
     * taskExpressionsEvaluationsPerSpotSet to set
     */
    public void setTaskExpressionsEvaluationsPerSpotSet(Map<String, SpotSummaryDetails> taskExpressionsEvaluationsPerSpotSet);

    /**
     * @param taskExpressionsOrdered the taskExpressionsOrdered to set
     */
    public void setTaskExpressionTreesOrdered(List<ExpressionTree> taskExpressionsOrdered);

    /**
     * @param type the type to set
     */
    public void setType(String type);

    public void setupSquidSessionSpecs();

    public void updateTableOfSelectedRatiosByMassStationIndex(int row, int col, boolean selected);

    /**
     * @return the NAMED_EXPRESSIONS_MAP
     */
    public Map<String, ExpressionTreeInterface> getNamedExpressionsMap();

    /**
     * @return the squidSessionModel
     */
    public SquidSessionModel getSquidSessionModel();
    
     /**
     * @return the taskExpressionsOrdered
     */
    public List<Expression> getTaskExpressionsOrdered();

}
