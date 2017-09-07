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
import java.util.Set;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.shrimp.MassStationDetail;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring
 */
public interface TaskInterface {

    public ExpressionTreeInterface buildRatioExpression(String ratioName);

    public Expression generateExpressionFromRawExcelStyleText(String name, String originalExpressionText);

    void buildSquidRatiosModelListFromMassStationDetails();

    void createMapOfIndexToMassStationDetails();

    /**
     *
     * @param xstream
     */
    void customizeXstream(XStream xstream);

    /**
     *
     * @param shrimpFractions
     */
    void evaluateTaskExpressions(List<ShrimpFractionExpressionInterface> shrimpFractions);

    void populateTableOfSelectedRatiosFromRatiosList();

    /**
     *
     * @param ratiosOfInterest
     * @return
     */
    Set<SquidSpeciesModel> extractUniqueSpeciesNumbers(List<String> ratiosOfInterest);

    SquidSpeciesModel findDenominator(String ratioName);

    SquidSpeciesModel findNumerator(String ratioName);

    /**
     * @return the authorName
     */
    String getAuthorName();

    /**
     * @return the dateRevised
     */
    long getDateRevised();

    /**
     * @return the description
     */
    String getDescription();

    /**
     * @return the labName
     */
    String getLabName();

    /**
     * @return the mapOfIndexToMassStationDetails
     */
    Map<Integer, MassStationDetail> getMapOfIndexToMassStationDetails();

    /**
     * @return the name
     */
    String getName();

    /**
     * @return the provenance
     */
    String getProvenance();

    /**
     * @return the ratioNames
     */
    List<String> getRatioNames();

    /**
     * @return the prawnFile
     */
    public PrawnFile getPrawnFile();

    /**
     * @param prawnFile the prawnFile to set
     */
    public void setPrawnFile(PrawnFile prawnFile);

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
    List<ExpressionTreeInterface> getTaskExpressionsOrdered();

    /**
     * @return the type
     */
    String getType();

    boolean isEmptyTableOfSelectedRatiosByMassStationIndex();

    SquidSpeciesModel lookUpSpeciesByName(String isotopeName);

    /**
     * @return the mapOfIndexToMassStationDetails
     */
    List<MassStationDetail> makeListOfMassStationDetails();

    String printSummaryData();

    void resetTableOfSelectedRatiosByMassStationIndex();

    int selectBackgroundSpeciesReturnPreviousIndex(SquidSpeciesModel ssm);

    /**
     * @param authorName the authorName to set
     */
    void setAuthorName(String authorName);

    /**
     * @param dateRevised the dateRevised to set
     */
    void setDateRevised(long dateRevised);

    /**
     * @param description the description to set
     */
    void setDescription(String description);

    /**
     * @param labName the labName to set
     */
    void setLabName(String labName);

    /**
     * @param name the name to set
     */
    void setName(String name);

    /**
     * @param provenance the provenance to set
     */
    void setProvenance(String provenance);

    /**
     * @param ratioNames the ratioNames to set
     */
    void setRatioNames(List<String> ratioNames);

    /**
     * @param taskExpressionsEvaluationsPerSpotSet the
     * taskExpressionsEvaluationsPerSpotSet to set
     */
    void setTaskExpressionsEvaluationsPerSpotSet(Map<String, SpotSummaryDetails> taskExpressionsEvaluationsPerSpotSet);

    /**
     * @param taskExpressionsOrdered the taskExpressionsOrdered to set
     */
    void setTaskExpressionsOrdered(List<ExpressionTreeInterface> taskExpressionsOrdered);

    /**
     * @param type the type to set
     */
    void setType(String type);

    void setupSquidSessionSpecs();

    public void updateTableOfSelectedRatiosByMassStationIndex(int row, int col, boolean selected);

}
