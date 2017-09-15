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
package org.cirdles.squid.tasks;

import com.google.common.primitives.Doubles;
import com.thoughtworks.xstream.XStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import static org.cirdles.ludwig.squid25.SquidConstants.SQUID_ERROR_VALUE;
import org.cirdles.squid.algorithms.weightedMeans.WtdLinCorrResults;
import static org.cirdles.squid.algorithms.weightedMeans.WeightedMeanCalculators.wtdLinCorr;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.shrimp.MassStationDetail;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.shrimp.SquidSessionModel;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import org.cirdles.squid.tasks.expressions.Expression;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTree;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeWithRatiosInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeXMLConverter;
import org.cirdles.squid.tasks.expressions.constants.ConstantNode;
import static org.cirdles.squid.tasks.expressions.constants.ConstantNode.MISSING_EXPRESSION_STRING;
import org.cirdles.squid.tasks.expressions.constants.ConstantNodeXMLConverter;
import org.cirdles.squid.tasks.expressions.expressionTrees.BuiltInExpressionInterface;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.squid.tasks.expressions.isotopes.ShrimpSpeciesNodeXMLConverter;
import org.cirdles.squid.tasks.expressions.operations.OperationXMLConverter;
import org.cirdles.squid.utilities.xmlSerialization.XMLSerializerInterface;
import static org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface.convertObjectArrayToDoubles;
import org.cirdles.squid.tasks.expressions.functions.FunctionXMLConverter;
import org.cirdles.squid.tasks.expressions.operations.Operation;
import org.cirdles.squid.tasks.expressions.spots.SpotNode;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForIsotopicRatios;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForPerSpotTaskExpressions;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForSummary;
import org.cirdles.squid.tasks.expressions.variables.VariableXMLConverter;
import org.cirdles.squid.utilities.fileUtilities.PrawnFileUtilities;

/**
 *
 * @author James F. Bowring
 */
public class Task implements TaskInterface, Serializable, XMLSerializerInterface {

    private static final long serialVersionUID = 6522574920235718028L;

    /**
     *
     */
    protected String name;
    protected String type;
    protected String description;
    protected String authorName;
    protected String labName;
    protected String provenance;
    protected long dateRevised;
    protected List<String> ratioNames;
    // cannot be serialized because of JavaFX private final SimpleStringProperty fields
    protected transient Map<Integer, MassStationDetail> mapOfIndexToMassStationDetails;
    protected SquidSessionModel squidSessionModel;
    protected List<SquidSpeciesModel> squidSpeciesModelList;
    protected List<SquidRatiosModel> squidRatiosModelList;
    protected boolean[][] tableOfSelectedRatiosByMassStationIndex;

    /**
     *
     */
    protected List<ExpressionTree> taskExpressionTreesOrdered;
    protected List<Expression> taskExpressionsOrdered;
    protected List<Expression> taskExpressionsRemoved;
    protected Map<String, ExpressionTreeInterface> namedExpressionsMap;

    /**
     *
     */
    protected Map<String, SpotSummaryDetails> taskExpressionsEvaluationsPerSpotSet;

    protected PrawnFile prawnFile;

    protected boolean changed;

    public Task() {
        this("Default Empty Task", null);
    }

    public Task(String name) {
        this(name, null);
    }

    /**
     *
     * @param name
     * @param prawnFile
     */
    public Task(String name, PrawnFile prawnFile) {
        this.name = name;
        this.type = "specify Geochron or General";
        this.description = "describe task here";
        this.authorName = "author name";
        this.labName = "lab name";
        this.provenance = "provenance";
        this.dateRevised = 0l;
        this.ratioNames = new ArrayList<>();
        this.squidSessionModel = null;
        squidSpeciesModelList = new ArrayList<>();
        squidRatiosModelList = new ArrayList<>();
        tableOfSelectedRatiosByMassStationIndex = new boolean[0][];

        this.taskExpressionTreesOrdered = new ArrayList<>();
        this.taskExpressionsOrdered = new ArrayList<>();
        this.taskExpressionsRemoved = new ArrayList<>();
        this.namedExpressionsMap = new LinkedHashMap<>();
        this.taskExpressionsEvaluationsPerSpotSet = new TreeMap<>();

        this.prawnFile = prawnFile;

        this.changed = true;
    }

    @Override
    public String printSummaryData() {
        StringBuilder summary = new StringBuilder();
        summary.append("Squid3 Task Name: ");
        summary.append("\t");
        summary.append(name);
        summary.append("\n\n");

        summary.append("Provenance: ");
        summary.append("\t");
        summary.append(provenance);
        summary.append("\n\n");

        summary.append("Task Type: ");
        summary.append("\t");
        summary.append(type);
        summary.append("\n\n");

        summary.append("Task Description: ");
        summary.append("\t");
        summary.append(description.replaceAll(",", "\n\t\t\t\t"));
        summary.append("\n\n");

        summary.append("Task Ratios: ");
        summary.append("\t");
        summary.append((String) (ratioNames.size() > 0 ? String.valueOf(ratioNames.size()) : "None")).append(" chosen.");
        summary.append("\n\n");

        int count = 0;
        for (ExpressionTreeInterface exp : taskExpressionTreesOrdered) {
            if (exp.amHealthy()) {
                count++;
            }
        }
        summary.append("Task Expressions: ");
        summary.append("\n\tHealthy: ");
        summary.append((String) (count > 0 ? String.valueOf(count) : "None")).append(" included.");
        summary.append("\n\tUnHealthy: ");
        summary.append((String) ((taskExpressionTreesOrdered.size() - count) > 0
                ? String.valueOf(taskExpressionTreesOrdered.size() - count) : "None")).append(" included.");
        summary.append("\n\n");

        return summary.toString();
    }

    private void initializeTask() {
        if (prawnFile != null) {
            setupSquidSessionSpecs();
        }
    }

    @Override
    public Expression generateExpressionFromRawExcelStyleText(String name, String originalExpressionText) {
        Expression exp = new Expression(name, originalExpressionText);
        exp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);

        return exp;
    }

    @Override
    public void setupSquidSessionSpecs() {
        // this is transient so needs re-creating if does not exist
        if (mapOfIndexToMassStationDetails == null) {
            createMapOfIndexToMassStationDetails();
        }

        if (changed) {

            createMapOfIndexToMassStationDetails();

            // populate taskExpressionsTreesOrdered
            taskExpressionTreesOrdered.clear();
            for (Expression exp : taskExpressionsOrdered) {
                taskExpressionTreesOrdered.add((ExpressionTree) exp.getExpressionTree());
            }
            buildSquidSpeciesModelList();

            populateTableOfSelectedRatiosFromRatiosList();

            buildSquidRatiosModelListFromMassStationDetails();

            processAndSortExpressions();

            squidSessionModel = new SquidSessionModel(squidSpeciesModelList, squidRatiosModelList, true, false, "T");

            changed = false;
        }
    }

    private void processAndSortExpressions() {
        // put expressions in execution order
        try {
            Collections.sort(taskExpressionTreesOrdered);
            Collections.sort(taskExpressionsOrdered);
        } catch (Exception e) {
        }
        // now use existing ratios as basis for building and checking expressions in ascending execution order
        assembleNamedExpressionsMap();

        // two passes needed to get in correct order worst case ***************************
        // since checking for dependencies after possible changes or new expressions
        buildExpressions();

        // put expressions in execution order
        try {
            Collections.sort(taskExpressionTreesOrdered);
            Collections.sort(taskExpressionsOrdered);
        } catch (Exception e) {
        }

        buildExpressions();

        // put expressions in execution order
        try {
            Collections.sort(taskExpressionTreesOrdered);
            Collections.sort(taskExpressionsOrdered);
        } catch (Exception e) {
        }
    }

    @Override
    public void removeExpression(Expression expression) {
        if (expression != null) {
            taskExpressionTreesOrdered.remove((ExpressionTree) expression.getExpressionTree());
            taskExpressionsOrdered.remove(expression);
            taskExpressionsRemoved.add(expression);
            processAndSortExpressions();
        }
    }

    @Override
    public void restoreRemovedExpressions() {
        for (Expression exp : taskExpressionsRemoved) {
            taskExpressionsOrdered.add(exp);
            taskExpressionTreesOrdered.add((ExpressionTree) exp.getExpressionTree());
        }
        taskExpressionsRemoved.clear();
        processAndSortExpressions();
    }

    private void createMapOfIndexToMassStationDetails() {
        // this is transient so needs re-creating if does not exist
        if (prawnFile != null) {
            mapOfIndexToMassStationDetails = PrawnFileUtilities.createMapOfIndexToMassStationDetails(prawnFile.getRun());
        }
    }

    private void buildExpressions() {
        // after map is built
        for (ExpressionTreeInterface exp : taskExpressionTreesOrdered) {
            if (exp instanceof BuiltInExpressionInterface) {
                ((BuiltInExpressionInterface) exp).buildExpression(this);
            }
        }
    }

    @Override
    public void buildSquidSpeciesModelList() {
        if (mapOfIndexToMassStationDetails != null) {
            // update these if squidSpeciesModelList exists
            if (squidSpeciesModelList.size() > 0) {
                for (SquidSpeciesModel ssm : squidSpeciesModelList) {
                    MassStationDetail massStationDetail = mapOfIndexToMassStationDetails.get(ssm.getMassStationIndex());
                    // only these two fields change
                    massStationDetail.setIsotopeLabel(ssm.getIsotopeName());
                    massStationDetail.setIsBackground(ssm.getIsBackground());
                }
            } else {
                buildSquidSpeciesModelListFromMassStationDetails();
            }
        }
    }

    private void buildSquidSpeciesModelListFromMassStationDetails() {
        squidSpeciesModelList = new ArrayList<>();
        for (Map.Entry<Integer, MassStationDetail> entry : mapOfIndexToMassStationDetails.entrySet()) {
            SquidSpeciesModel spm = new SquidSpeciesModel(
                    entry.getKey(), entry.getValue().getMassStationLabel(), entry.getValue().getIsotopeLabel(), entry.getValue().getElementLabel(), entry.getValue().getIsBackground());

            squidSpeciesModelList.add(spm);
        }
        if (tableOfSelectedRatiosByMassStationIndex.length == 0) {
            tableOfSelectedRatiosByMassStationIndex = new boolean[squidSpeciesModelList.size()][squidSpeciesModelList.size()];
        }
    }

    @Override
    public int selectBackgroundSpeciesReturnPreviousIndex(SquidSpeciesModel ssm) {
        // there is at most one
        int retVal = -1;

        for (SquidSpeciesModel squidSpeciesModel : squidSpeciesModelList) {
            if (squidSpeciesModel.getIsBackground()) {
                squidSpeciesModel.setIsBackground(false);
                retVal = squidSpeciesModel.getMassStationIndex();
                break;
            }
        }

        if (ssm != null) {
            ssm.setIsBackground(true);
        }

        return retVal;
    }

    @Override
    public void buildSquidRatiosModelListFromMassStationDetails() {
        squidRatiosModelList = new ArrayList<>();

        // TODO revise use of this in comparator of squidSpeciesModel
        int reportingOrderIndex = 0;
        for (int row = 0; row < tableOfSelectedRatiosByMassStationIndex.length; row++) {
            for (int col = 0; col < tableOfSelectedRatiosByMassStationIndex[0].length; col++) {
                if ((tableOfSelectedRatiosByMassStationIndex[row][col])
                        && (!squidSpeciesModelList.get(row).getIsBackground())
                        && (!squidSpeciesModelList.get(col).getIsBackground())) {
                    squidRatiosModelList.add(new SquidRatiosModel(squidSpeciesModelList.get(row), squidSpeciesModelList.get(col), reportingOrderIndex));
                    reportingOrderIndex++;
                }
            }
        }
    }

    private void assembleNamedExpressionsMap() {
        namedExpressionsMap.clear();

        // TODO: refactor SpotNode treatment after the extent of use is known
        SpotNode spotNode = SpotNode.buildSpotNode("getHours");
        namedExpressionsMap.put(spotNode.getName(), spotNode);

        for (SquidSpeciesModel spm : squidSpeciesModelList) {
            ShrimpSpeciesNode shrimpSpeciesNode = ShrimpSpeciesNode.buildShrimpSpeciesNode(spm);
            namedExpressionsMap.put(spm.getIsotopeName(), shrimpSpeciesNode);
        }

        for (SquidRatiosModel srm : squidRatiosModelList) {
            namedExpressionsMap.put(srm.getRatioName(), buildRatioExpression(srm.getRatioName()));
        }

        for (ExpressionTreeInterface exp : taskExpressionTreesOrdered) {
            namedExpressionsMap.put(exp.getName(), exp);
        }
    }

    private ExpressionTreeInterface buildRatioExpression(String ratioName) {
        // format of ratioName is "nnn/mmm"
        ExpressionTreeInterface ratioExpression = null;
        if ((findNumerator(ratioName) != null) & (findDenominator(ratioName) != null)) {
            ratioExpression
                    = new ExpressionTree(
                            ratioName,
                            ShrimpSpeciesNode.buildShrimpSpeciesNode(findNumerator(ratioName), "getPkInterpScanArray"),
                            ShrimpSpeciesNode.buildShrimpSpeciesNode(findDenominator(ratioName), "getPkInterpScanArray"),
                            Operation.divide());

            ((ExpressionTreeWithRatiosInterface) ratioExpression).getRatiosOfInterest().add(ratioName);
        }
        return ratioExpression;
    }

    @Override
    public ExpressionTreeInterface findNamedExpression(String ratioName) {
        ExpressionTreeInterface foundRatioExp = namedExpressionsMap.get(ratioName);
        if (foundRatioExp == null) {
            foundRatioExp = new ConstantNode(MISSING_EXPRESSION_STRING, ratioName);
        } else if (!foundRatioExp.amHealthy()) {
            foundRatioExp = new ConstantNode(MISSING_EXPRESSION_STRING, ratioName);
        }
        return foundRatioExp;
    }

    private SquidSpeciesModel findNumerator(String ratioName) {
        String[] parts = ratioName.split("/");
        return lookUpSpeciesByName(parts[0]);
    }

    private SquidSpeciesModel findDenominator(String ratioName) {
        String[] parts = ratioName.split("/");
        return lookUpSpeciesByName(parts[1]);
    }

    @Override
    public SquidSpeciesModel lookUpSpeciesByName(String isotopeName) {
        SquidSpeciesModel retVal = null;

        for (SquidSpeciesModel squidSpeciesModel : squidSpeciesModelList) {
            if (squidSpeciesModel.getIsotopeName().compareToIgnoreCase(isotopeName) == 0) {
                retVal = squidSpeciesModel;
                break;
            }
        }

        return retVal;
    }

    /**
     *
     * @param ratiosOfInterest
     * @return
     */
    private Set<SquidSpeciesModel> extractUniqueSpeciesNumbers(List<String> ratiosOfInterest) {
        // assume acquisition order is atomic weight order
        Set<SquidSpeciesModel> eqPkUndupeOrd = new TreeSet<>();
        for (int i = 0; i < ratiosOfInterest.size(); i++) {
            eqPkUndupeOrd.add(findNumerator(ratiosOfInterest.get(i)));
            eqPkUndupeOrd.add(findDenominator(ratiosOfInterest.get(i)));
        }
        return eqPkUndupeOrd;
    }

    /**
     *
     * @param xstream
     */
    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new ShrimpSpeciesNodeXMLConverter());
        xstream.alias("ShrimpSpeciesNode", ShrimpSpeciesNode.class);

        xstream.registerConverter(new ConstantNodeXMLConverter());
        xstream.alias("ConstantNode", ConstantNode.class);

        xstream.registerConverter(new VariableXMLConverter());
        xstream.alias("VariableNodeForSummary", VariableNodeForSummary.class);
        xstream.alias("VariableNodeForPerSpotTaskExpressions", VariableNodeForPerSpotTaskExpressions.class);
        xstream.alias("VariableNodeForIsotopicRatios", VariableNodeForIsotopicRatios.class);

        xstream.registerConverter(new OperationXMLConverter());
        xstream.registerConverter(new FunctionXMLConverter());

        xstream.registerConverter(new ExpressionTreeXMLConverter());
        xstream.alias("ExpressionTree", ExpressionTree.class);

        xstream.registerConverter(new TaskXMLConverter());
        xstream.alias("Task", Task.class);
        xstream.alias("Task", this.getClass());

        // Note: http://cristian.sulea.net/blog.php?p=2014-11-12-xstream-object-references
        xstream.setMode(XStream.NO_REFERENCES);

    }

    /**
     *
     * @param shrimpFractions
     */
    @Override
    public void evaluateTaskExpressions(List<ShrimpFractionExpressionInterface> shrimpFractions) {

        taskExpressionsEvaluationsPerSpotSet = new TreeMap<>();

        // setup spots
        shrimpFractions.forEach((spot) -> {
            List<TaskExpressionEvaluatedPerSpotPerScanModelInterface> taskExpressionsForScansEvaluated = new ArrayList<>();
            spot.setTaskExpressionsForScansEvaluated(taskExpressionsForScansEvaluated);
        });

        // subdivide spots
        List<ShrimpFractionExpressionInterface> referenceMaterialSpots = new ArrayList<>();
        List<ShrimpFractionExpressionInterface> unknownSpots = new ArrayList<>();
        shrimpFractions.forEach((spot) -> {
            if (spot.isReferenceMaterial()) {
                referenceMaterialSpots.add(spot);
            } else {
                unknownSpots.add(spot);
            }
        });

        for (ExpressionTreeInterface expression : taskExpressionTreesOrdered) {
            if (expression.amHealthy()) {
                // determine subset of spots to be evaluated - default = all
                List<ShrimpFractionExpressionInterface> spotsForExpression = shrimpFractions;
                if (!((ExpressionTree) expression).isSquidSwitchSTReferenceMaterialCalculation()) {
                    spotsForExpression = unknownSpots;
                }
                if (!((ExpressionTree) expression).isSquidSwitchSAUnknownCalculation()) {
                    spotsForExpression = referenceMaterialSpots;
                }

                try {
                    evaluateExpressionForSpotSet(expression, spotsForExpression);
                } catch (SquidException squidException) {
                    // TODO - log and report failure of expression
//                JOptionPane.showMessageDialog(null,
//                        "Expression failed: " + expression.getName() + " because: " + squidException.getMessage());
                }
            }
        }
    }

    private void evaluateExpressionForSpotSet(
            ExpressionTreeInterface expression,
            List<ShrimpFractionExpressionInterface> spotsForExpression) throws SquidException {
        // determine type of expression
        if (((ExpressionTree) expression).isSquidSwitchSCSummaryCalculation()) {
            double[][] value = convertObjectArrayToDoubles(expression.eval(spotsForExpression, this));
            taskExpressionsEvaluationsPerSpotSet.put(expression.getName(), new SpotSummaryDetails(value, spotsForExpression));
        } else {
            // perform expression on each spot
            for (ShrimpFractionExpressionInterface spot : spotsForExpression) {
                evaluateExpressionForSpot(expression, spot);
            }
        }
    }

    private void evaluateExpressionForSpot(
            ExpressionTreeInterface expression,
            ShrimpFractionExpressionInterface spot) throws SquidException {

        if (((ExpressionTree) expression).hasRatiosOfInterest()) {
            // case of Squid switch "NU"
            TaskExpressionEvaluatedPerSpotPerScanModelInterface taskExpressionEvaluatedPerSpotPerScanModel
                    = evaluateTaskExpressionsPerSpotPerScan(expression, spot);
            // save scan-specific results
            spot.getTaskExpressionsForScansEvaluated().add(taskExpressionEvaluatedPerSpotPerScanModel);
            // save spot-specific results
            double[][] value = new double[][]{{taskExpressionEvaluatedPerSpotPerScanModel.getRatioVal(),
                taskExpressionEvaluatedPerSpotPerScanModel.getRatioFractErr()}};
            spot.getTaskExpressionsEvaluationsPerSpot().put(expression.getName(), value);
        } else {
            List<ShrimpFractionExpressionInterface> singleSpot = new ArrayList<>();
            singleSpot.add(spot);
            double[][] value = convertObjectArrayToDoubles(expression.eval(singleSpot, this));
            spot.getTaskExpressionsEvaluationsPerSpot().put(expression.getName(), value);
        }
    }

    /**
     * see https://github.com/CIRDLES/ET_Redux/wiki/SHRIMP:-Sub-EqnInterp
     *
     * @param expression
     * @param shrimpFraction
     */
    private TaskExpressionEvaluatedPerSpotPerScanModelInterface
            evaluateTaskExpressionsPerSpotPerScan(
                    ExpressionTreeInterface expression,
                    ShrimpFractionExpressionInterface shrimpFraction) throws SquidException {

        TaskExpressionEvaluatedPerSpotPerScanModelInterface taskExpressionEvaluatedPerSpotPerScanModel = null;
        if (shrimpFraction != null) {

            // construct argument list of one spot
            List<ShrimpFractionExpressionInterface> singleSpot = new ArrayList<>();
            singleSpot.add(shrimpFraction);

            // first have to build pkInterp etc per expression and then evaluate by scan
            List<String> ratiosOfInterest = ((ExpressionTreeWithRatiosInterface) expression).getRatiosOfInterest();

            int[] isotopeIndices = new int[ratiosOfInterest.size() * 2];
            for (int i = 0; i < ratiosOfInterest.size(); i++) {
                if (findNumerator(ratiosOfInterest.get(i)) != null) {
                    isotopeIndices[2 * i] = findNumerator(ratiosOfInterest.get(i)).getMassStationIndex();
                } else {
                    isotopeIndices[2 * i] = -1;
                }
                if (findDenominator(ratiosOfInterest.get(i)) != null) {
                    isotopeIndices[2 * i + 1] = findDenominator(ratiosOfInterest.get(i)).getMassStationIndex();
                } else {
                    isotopeIndices[2 * i + 1] = -1;
                }
            }

            //TODO June 2017 temp hack until expression checking is in place
            for (int i = 0; i < isotopeIndices.length; i++) {
                if (isotopeIndices[i] == -1) {
                    throw new SquidException("Missing Isotope");
                }
            }

            int sIndx = shrimpFraction.getReducedPkHt().length - 1;
            double[][] pkInterp = new double[sIndx][shrimpFraction.getReducedPkHt()[0].length];
            double[][] pkInterpFerr = new double[sIndx][shrimpFraction.getReducedPkHt()[0].length];
            boolean singleScan = (sIndx == 1);
            double interpTime = 0.0;

            List<Double> eqValList = new ArrayList<>();
            List<Double> fractErrList = new ArrayList<>();
            List<Double> absErrList = new ArrayList<>();
            List<Double> eqTimeList = new ArrayList<>();

            for (int scanNum = 0; scanNum < sIndx; scanNum++) {
                boolean doProceed = true;
                if (!singleScan) {
                    double interpTimeSpan = 0.0;
                    for (int i = 0; i < isotopeIndices.length; i++) {
                        interpTimeSpan
                                += shrimpFraction.getTimeStampSec()[scanNum][isotopeIndices[i]]
                                + shrimpFraction.getTimeStampSec()[scanNum + 1][isotopeIndices[i]];
                    }
                    interpTime = interpTimeSpan / isotopeIndices.length / 2.0;
                } // end check singleScan

                for (int i = 0; i < isotopeIndices.length; i++) {
                    double fractInterpTime = 0.0;
                    double fractLessInterpTime = 0.0;
                    double redPk2Ht = 0.0;

                    if (!singleScan) {
                        // default value
                        pkInterp[scanNum][isotopeIndices[i]] = SQUID_ERROR_VALUE;
                        double pkTdelt
                                = shrimpFraction.getTimeStampSec()[scanNum + 1][isotopeIndices[i]]
                                - shrimpFraction.getTimeStampSec()[scanNum][isotopeIndices[i]];

                        doProceed = (pkTdelt > 0.0);

                        if (doProceed) {
                            fractInterpTime = (interpTime - shrimpFraction.getTimeStampSec()[scanNum][isotopeIndices[i]]) / pkTdelt;
                            fractLessInterpTime = 1.0 - fractInterpTime;
                            redPk2Ht = shrimpFraction.getReducedPkHt()[scanNum + 1][isotopeIndices[i]];
                        }
                    } // end check singleScan
                    if (doProceed) {
                        double redPk1Ht = shrimpFraction.getReducedPkHt()[scanNum][isotopeIndices[i]];

                        if (redPk1Ht == SQUID_ERROR_VALUE || redPk2Ht == SQUID_ERROR_VALUE) {
                            doProceed = false;
                        }

                        if (doProceed) {
                            double pkF1 = shrimpFraction.getReducedPkHtFerr()[scanNum][isotopeIndices[i]];

                            if (singleScan) {
                                pkInterp[scanNum][isotopeIndices[i]] = redPk1Ht;
                                pkInterpFerr[scanNum][isotopeIndices[i]] = pkF1;
                            } else {
                                pkInterp[scanNum][isotopeIndices[i]] = (fractLessInterpTime * redPk1Ht) + (fractInterpTime * redPk2Ht);
                                double pkF2 = shrimpFraction.getReducedPkHtFerr()[scanNum + 1][isotopeIndices[i]];
                                pkInterpFerr[scanNum][isotopeIndices[i]] = Math.sqrt((fractLessInterpTime * pkF1) * (fractLessInterpTime * pkF1)
                                        + (fractInterpTime * pkF2) * (fractInterpTime * pkF2));
                            }
                        }
                    }
                }

                // The next step is to evaluate the equation 'FormulaEval', 
                // documented separately, and approximate the uncertainties:
                shrimpFraction.setPkInterpScanArray(pkInterp[scanNum]);

                double eqValTmp = convertObjectArrayToDoubles(expression.eval(singleSpot, this))[0][0];

                double eqFerr;

                if (eqValTmp != 0.0) {
                    // numerical pertubation procedure
                    // EqPkUndupeOrd is here a List of the unique Isotopes in order of acquisition in the expression
                    Set<SquidSpeciesModel> eqPkUndupeOrd = extractUniqueSpeciesNumbers(((ExpressionTreeWithRatiosInterface) expression).getRatiosOfInterest());
                    Iterator<SquidSpeciesModel> species = eqPkUndupeOrd.iterator();

                    double fVar = 0.0;
                    while (species.hasNext()) {
                        SquidSpeciesModel specie = species.next();
                        int unDupPkOrd = specie.getMassStationIndex();

                        // clone pkInterp[scanNum] for use in pertubation
                        double[] perturbed = pkInterp[scanNum].clone();
                        perturbed[unDupPkOrd] *= 1.0001;
                        shrimpFraction.setPkInterpScanArray(perturbed);

                        double pertVal = convertObjectArrayToDoubles(expression.eval(singleSpot, this))[0][0];

                        double fDelt = (pertVal - eqValTmp) / eqValTmp; // improvement suggested by Bodorkos
                        double tA = pkInterpFerr[scanNum][unDupPkOrd];
                        double tB = 1.0001 - 1.0;// --note that Excel 16-bit floating binary gives 9.9999999999989E-05    
                        double tC = fDelt * (tA / tB); // Bodorkos rescaled tc and td April 2017    fDelt * fDelt;
                        double tD = tC * tC;         // Bodorkos rescaled tc and td April 2017(tA / tB) * (tA / tB) * tC;
                        fVar += tD;// --fractional internal variance

                    } // end of visiting each isotope and perturbing equation

                    eqFerr = Math.sqrt(fVar);

                    // now that expression and its error are calculated
                    if (eqFerr != 0.0) {
                        eqValList.add(eqValTmp);
                        absErrList.add(Math.abs(eqFerr * eqValTmp));
                        fractErrList.add(eqFerr);
                        double totRatTime = 0.0;
                        int numPksInclDupes = 0;

                        // reset iterator
                        species = eqPkUndupeOrd.iterator();
                        while (species.hasNext()) {
                            int unDupPkOrd = species.next().getMassStationIndex();

                            totRatTime += shrimpFraction.getTimeStampSec()[scanNum][unDupPkOrd];
                            numPksInclDupes++;

                            totRatTime += shrimpFraction.getTimeStampSec()[scanNum + 1][unDupPkOrd];
                            numPksInclDupes++;
                        }
                        eqTimeList.add(totRatTime / numPksInclDupes);
                    }
                } // end test of eqValTmp != 0.0 VBA calls this a bailout and has no logic

            } // end scanNum loop

            // The final step is to assemble outputs EqTime, EqVal and AbsErr, and
            // to define SigRho as input for the use of subroutine WtdLinCorr and its sub-subroutines: 
            // convert to arrays
            double[] eqVal = Doubles.toArray(eqValList);
            double[] absErr = Doubles.toArray(absErrList);
            double[] fractErr = Doubles.toArray(fractErrList);
            double[] eqTime = Doubles.toArray(eqTimeList);
            double[][] sigRho = new double[eqVal.length][eqVal.length];

            for (int i = 0; i < sigRho.length; i++) {
                sigRho[i][i] = absErr[i];
                if (i > 0) {
                    sigRho[i][i - 1] = 0.25;
                    sigRho[i - 1][i] = 0.25;
                }
            }

            WtdLinCorrResults wtdLinCorrResults;
            double meanEq;
            double meanEqSig;

            if (shrimpFraction.isUserLinFits() && eqVal.length > 3) {
                wtdLinCorrResults = wtdLinCorr(eqVal, sigRho, eqTime);

                double midTime
                        = (shrimpFraction.getTimeStampSec()[sIndx][shrimpFraction.getReducedPkHt()[0].length - 1]
                        + shrimpFraction.getTimeStampSec()[0][0]) / 2.0;
                double slope = wtdLinCorrResults.getSlope();
                double sigmaSlope = wtdLinCorrResults.getSigmaSlope();
                double sigmaIntercept = wtdLinCorrResults.getSigmaIntercept();

                meanEq = (slope * midTime) + wtdLinCorrResults.getIntercept();
                meanEqSig = Math.sqrt((midTime * sigmaSlope * midTime * sigmaSlope)//
                        + sigmaIntercept * sigmaIntercept //
                        + 2.0 * midTime * wtdLinCorrResults.getCovSlopeInter());

            } else {
                wtdLinCorrResults = wtdLinCorr(eqVal, sigRho, new double[0]);
                meanEq = wtdLinCorrResults.getIntercept();
                meanEqSig = wtdLinCorrResults.getSigmaIntercept();
            }

            double eqValFerr;
            if (meanEq == 0.0) {
                eqValFerr = 1.0;
            } else {
                eqValFerr = Math.abs(meanEqSig / meanEq);
            }

            // for consistency with Bodorkos documentation
            double[] ratEqVal = eqVal.clone();
            double[] ratEqTime = eqTime.clone();
            double[] ratEqErr = new double[eqVal.length];
            for (int i = 0; i < ratEqErr.length; i++) {
                ratEqErr[i] = Math.abs(eqVal[i] * fractErr[i]);
            }

            // April 20178 rounding of ratEqVal, meanEq, and eqValFerr occurs within this constructor
            taskExpressionEvaluatedPerSpotPerScanModel
                    = new TaskExpressionEvaluatedPerSpotPerScanModel(
                            expression, ratEqVal, ratEqTime, ratEqErr, meanEq, eqValFerr);
        }

        return taskExpressionEvaluatedPerSpotPerScanModel;
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    @Override
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the authorName
     */
    @Override
    public String getAuthorName() {
        return authorName;
    }

    /**
     * @param authorName the authorName to set
     */
    @Override
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    /**
     * @return the labName
     */
    @Override
    public String getLabName() {
        return labName;
    }

    /**
     * @param labName the labName to set
     */
    @Override
    public void setLabName(String labName) {
        this.labName = labName;
    }

    /**
     * @return the dateRevised
     */
    @Override
    public long getDateRevised() {
        return dateRevised;
    }

    /**
     * @param dateRevised the dateRevised to set
     */
    @Override
    public void setDateRevised(long dateRevised) {
        this.dateRevised = dateRevised;
    }

    /**
     * @return the taskExpressionTreesOrdered
     */
    @Override
    public List<ExpressionTree> getTaskExpressionTreesOrdered() {
        return taskExpressionTreesOrdered;
    }

    /**
     * @param taskExpressionTreesOrdered the taskExpressionTreesOrdered to set
     */
    @Override
    public void setTaskExpressionTreesOrdered(List<ExpressionTree> taskExpressionTreesOrdered) {
        this.taskExpressionTreesOrdered = taskExpressionTreesOrdered;
    }

    /**
     * @return the taskExpressionsEvaluationsPerSpotSet
     */
    @Override
    public Map<String, SpotSummaryDetails> getTaskExpressionsEvaluationsPerSpotSet() {
        return taskExpressionsEvaluationsPerSpotSet;
    }

    /**
     * @param taskExpressionsEvaluationsPerSpotSet the
     * taskExpressionsEvaluationsPerSpotSet to set
     */
    @Override
    public void setTaskExpressionsEvaluationsPerSpotSet(Map<String, SpotSummaryDetails> taskExpressionsEvaluationsPerSpotSet) {
        this.taskExpressionsEvaluationsPerSpotSet = taskExpressionsEvaluationsPerSpotSet;
    }

    /**
     * @return the provenance
     */
    @Override
    public String getProvenance() {
        return provenance;
    }

    /**
     * @param provenance the provenance to set
     */
    @Override
    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    /**
     * @return the ratioNames
     */
    @Override
    public List<String> getRatioNames() {
        return ratioNames;
    }

    /**
     * @param ratioNames the ratioNames to set
     */
    @Override
    public void setRatioNames(List<String> ratioNames) {
        this.ratioNames = ratioNames;
    }

    /**
     * @return the squidSessionModel
     */
    @Override
    public SquidSessionModel getSquidSessionModel() {
        return squidSessionModel;
    }

    /**
     * @param squidSessionModel the squidSessionModel to set
     */
    public void setSquidSessionModel(SquidSessionModel squidSessionModel) {
        this.squidSessionModel = squidSessionModel;
    }

    /**
     * @return the squidSpeciesModelList
     */
    @Override
    public List<SquidSpeciesModel> getSquidSpeciesModelList() {
        return squidSpeciesModelList;
    }

    /**
     * @return the squidRatiosModelList
     */
    @Override
    public List<SquidRatiosModel> getSquidRatiosModelList() {
        return squidRatiosModelList;
    }

    /**
     * @return the tableOfSelectedRatiosByMassStationIndex
     */
    @Override
    public boolean[][] getTableOfSelectedRatiosByMassStationIndex() {
        return tableOfSelectedRatiosByMassStationIndex;
    }

    @Override
    public void resetTableOfSelectedRatiosByMassStationIndex() {
        tableOfSelectedRatiosByMassStationIndex = new boolean[squidSpeciesModelList.size()][squidSpeciesModelList.size()];
    }

    @Override
    public boolean isEmptyTableOfSelectedRatiosByMassStationIndex() {
        boolean retVal = true;

        if (tableOfSelectedRatiosByMassStationIndex != null) {
            for (int row = 0; row < tableOfSelectedRatiosByMassStationIndex.length; row++) {
                for (int col = 0; col < tableOfSelectedRatiosByMassStationIndex[0].length; col++) {
                    retVal &= !tableOfSelectedRatiosByMassStationIndex[row][col];
                }
            }
        }

        return retVal;
    }

    /**
     * @return the mapOfIndexToMassStationDetails
     */
    @Override
    public Map<Integer, MassStationDetail> getMapOfIndexToMassStationDetails() {
        return mapOfIndexToMassStationDetails;
    }

    /**
     * @return the listOfMassStationDetails
     */
    @Override
    public List<MassStationDetail> makeListOfMassStationDetails() {
        List<MassStationDetail> listOfMassStationDetails = new ArrayList<>();
        for (Map.Entry<Integer, MassStationDetail> entry : mapOfIndexToMassStationDetails.entrySet()) {
            listOfMassStationDetails.add(entry.getValue());
        }
        return listOfMassStationDetails;
    }

    @Override
    public void populateTableOfSelectedRatiosFromRatiosList() {
        resetTableOfSelectedRatiosByMassStationIndex();

        for (int i = 0; i < ratioNames.size(); i++) {
            String[] ratio = ratioNames.get(i).split("/");
            if ((lookUpSpeciesByName(ratio[0]) != null)
                    && lookUpSpeciesByName(ratio[1]) != null) {
                int num = lookUpSpeciesByName(ratio[0]).getMassStationIndex();
                int den = lookUpSpeciesByName(ratio[1]).getMassStationIndex();
                tableOfSelectedRatiosByMassStationIndex[num][den] = true;
            }
        }
    }

    @Override
    public void updateTableOfSelectedRatiosByMassStationIndex(int row, int col, boolean selected) {
        tableOfSelectedRatiosByMassStationIndex[row][col] = selected;
        String num = mapOfIndexToMassStationDetails.get(row).getIsotopeLabel();
        String den = mapOfIndexToMassStationDetails.get(col).getIsotopeLabel();
        String ratioName = num + "/" + den;
        if (ratioNames.contains(ratioName)) {
            if (!selected) {
                ratioNames.remove(ratioName);
            }
        } else {
            if (selected) {
                ratioNames.add(ratioName);
            }
        }

        changed = true;
    }

    /**
     * @return the prawnFile
     */
    public PrawnFile getPrawnFile() {
        return prawnFile;
    }

    /**
     * @param prawnFile the prawnFile to set
     */
    public void setPrawnFile(PrawnFile prawnFile) {
        this.prawnFile = prawnFile;
    }

    /**
     * @return the namedExpressionsMap
     */
    @Override
    public Map<String, ExpressionTreeInterface> getNamedExpressionsMap() {
        return namedExpressionsMap;
    }

    /**
     * @return the taskExpressionsOrdered
     */
    public List<Expression> getTaskExpressionsOrdered() {
        return taskExpressionsOrdered;
    }

    /**
     * @param changed the changed to set
     */
    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
