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

import org.cirdles.squid.tasks.evaluationEngines.TaskExpressionEvaluatedPerSpotPerScanModelInterface;
import org.cirdles.squid.tasks.expressions.spots.SpotSummaryDetails;
import com.thoughtworks.xstream.XStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import javafx.collections.transformation.SortedList;
import org.apache.commons.collections4.list.TreeList;
import org.cirdles.squid.core.CalamariReportsEngine;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.prawn.PrawnFile;
import org.cirdles.squid.prawn.PrawnFileRunFractionParser;
import org.cirdles.squid.shrimp.MassStationDetail;
import org.cirdles.squid.shrimp.ShrimpFraction;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.shrimp.SquidRatiosModel;
import org.cirdles.squid.shrimp.SquidSessionModel;
import org.cirdles.squid.shrimp.SquidSpeciesModel;
import static org.cirdles.squid.shrimp.SquidSpeciesModel.SQUID_DEFAULT_BACKGROUND_ISOTOPE_LABEL;
import org.cirdles.squid.tasks.evaluationEngines.ExpressionEvaluator;
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
import static org.cirdles.squid.tasks.expressions.spots.SpotFieldNode.buildSpotNode;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForIsotopicRatios;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForPerSpotTaskExpressions;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForSummary;
import org.cirdles.squid.tasks.expressions.variables.VariableNodeForSummaryXMLConverter;
import org.cirdles.squid.utilities.fileUtilities.PrawnFileUtilities;

/**
 *
 * @author James F. Bowring
 */
public class Task implements TaskInterface, Serializable, XMLSerializerInterface {

    private static final long serialVersionUID = 6522574920235718028L;

    private static final PrawnFileRunFractionParser PRAWN_FILE_RUN_FRACTION_PARSER
            = new PrawnFileRunFractionParser();

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
    protected boolean useSBM;
    protected boolean userLinFits;
    protected int indexOfBackgroundSpecies;
    protected String filterForRefMatSpotNames;

    protected List<String> nominalMasses;
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
    protected SortedSet<ExpressionTree> taskExpressionTreesOrdered;
    protected SortedSet<Expression> taskExpressionsOrdered;
    protected SortedSet<Expression> taskExpressionsRemoved;
    protected Map<String, ExpressionTreeInterface> namedExpressionsMap;

    private List<ShrimpFractionExpressionInterface> shrimpFractions;
    private List<ShrimpFractionExpressionInterface> referenceMaterialSpots;
    private List<ShrimpFractionExpressionInterface> unknownSpots;

    /**
     *
     */
    protected Map<String, SpotSummaryDetails> taskExpressionsEvaluationsPerSpotSet;

    protected PrawnFile prawnFile;
    protected CalamariReportsEngine reportsEngine;

    protected boolean changed;

    public Task() {
        this("New Task", null, "", null);
    }

    public Task(String name) {
        this(name, null, "", null);
    }

    public Task(String name, CalamariReportsEngine reportsEngine) {
        this(name, null, "", reportsEngine);
    }

    /**
     *
     * @param name
     * @param prawnFile
     * @param filterForRefMatSpotNames
     * @param reportsEngine
     */
    public Task(String name, PrawnFile prawnFile, String filterForRefMatSpotNames, CalamariReportsEngine reportsEngine) {
        this.name = name;
        this.type = "geochron";
        this.description = "";
        this.authorName = "";
        this.labName = "";
        this.provenance = "";
        this.dateRevised = 0l;
        this.filterForRefMatSpotNames = "";
        this.useSBM = true;
        this.userLinFits = false;
        this.indexOfBackgroundSpecies = -1;

        this.nominalMasses = new ArrayList<>();
        this.ratioNames = new ArrayList<>();
        this.squidSessionModel = null;
        squidSpeciesModelList = new ArrayList<>();
        squidRatiosModelList = new ArrayList<>();
        tableOfSelectedRatiosByMassStationIndex = new boolean[0][];

        this.taskExpressionTreesOrdered = new TreeSet<>();
        this.taskExpressionsOrdered = new TreeSet<>();
        this.taskExpressionsRemoved = new TreeSet<>();
        this.namedExpressionsMap = new LinkedHashMap<>();
        this.taskExpressionsEvaluationsPerSpotSet = new TreeMap<>();

        this.shrimpFractions = new ArrayList<>();
        this.referenceMaterialSpots = new ArrayList<>();
        this.unknownSpots = new ArrayList<>();

        this.prawnFile = prawnFile;
        this.reportsEngine = reportsEngine;

        this.changed = true;
    }

    @Override
    public String printTaskAudit() {
        StringBuilder summary = new StringBuilder();

        summary.append(" ")
                .append("Prawn File provides ")
                .append(String.valueOf(squidSpeciesModelList.size()))
                .append(" Species:");

        summary.append("\n      ");
        for (int i = 0; i < squidSpeciesModelList.size(); i++) {
            summary.append(squidSpeciesModelList.get(i).getPrawnFileIsotopeName());
            summary.append((i == (squidSpeciesModelList.size() - 1) ? "" : ", "));
        }

        summary.append("\n ")
                .append("Task File names ")
                .append(String.valueOf(nominalMasses.size()))
                .append(" Masses:");

        summary.append("\n      ");
        for (int i = 0; i < nominalMasses.size(); i++) {
            summary.append(nominalMasses.get(i));
            summary.append((i == (nominalMasses.size() - 1) ? "" : ", "));
        }
//
//        summary.append("\n ")
//                .append("Squid3 adopts these isotope names: ");
//
//        summary.append("\n  ");
//        for (int i = 0; i < squidSpeciesModelList.size(); i++) {
//            summary.append(squidSpeciesModelList.get(i).getIsotopeName());
//            summary.append((i == (squidSpeciesModelList.size() - 1) ? "" : ", "));
//        }

        summary.append("\n\n Task Ratios: ");
        summary.append((String) (ratioNames.size() > 0 ? String.valueOf(ratioNames.size()) : "None")).append(" chosen.");

        summary.append("\n\n ")
                .append(String.valueOf(referenceMaterialSpots.size()))
                .append(" Reference Material Spots extracted by filter: ' ")
                .append(filterForRefMatSpotNames)
                .append(" '.");

        summary.append("\n ")
                .append(String.valueOf(unknownSpots.size()))
                .append(" Unknown Spots.");

        int count = 0;
        for (ExpressionTreeInterface exp : taskExpressionTreesOrdered) {
            if (exp.amHealthy()) {
                count++;
            }
        }
        summary.append("\n\n Task Expressions: ");
        summary.append("\n\t Healthy: ");
        summary.append((String) (count > 0 ? String.valueOf(count) : "None")).append(" included.");
        summary.append("\n\t UnHealthy: ");
        summary.append((String) ((taskExpressionTreesOrdered.size() - count) > 0
                ? String.valueOf(taskExpressionTreesOrdered.size() - count) : "None")).append(" included.");

        return summary.toString();
    }

    @Override
    public Expression generateExpressionFromRawExcelStyleText(String name, String originalExpressionText, boolean eqnSwitchNU) {
        Expression exp = new Expression(name, originalExpressionText);
        exp.setSquidSwitchNU(eqnSwitchNU);
        exp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);

        return exp;
    }

    @Override
    public void setupSquidSessionSpecsAndReduceAndReport() {

        if (changed) {
            reorderExpressions();

            buildSquidSpeciesModelList();

            populateTableOfSelectedRatiosFromRatiosList();

            buildSquidRatiosModelListFromMassStationDetails();

            squidSessionModel = new SquidSessionModel(
                    squidSpeciesModelList, squidRatiosModelList, true, false, indexOfBackgroundSpecies, filterForRefMatSpotNames);

            try {
                shrimpFractions = processRunFractions(prawnFile, squidSessionModel);
            } catch (Exception e) {
            }

            processAndSortExpressions();

            evaluateTaskExpressions();

            if (ratioNames.size() > 0) {
                produceSummaryReportsForGUI();
            } else {
                reportsEngine.clearReports();
            }

            changed = false;
        }
    }

    @Override
    public void updateRatioNames(List<String> ratioNames) {
        this.ratioNames = ratioNames;

        setChanged(true);
        setupSquidSessionSpecsAndReduceAndReport();
        updateAllExpressions(2);
    }

    private void produceSummaryReportsForGUI() {
        if (unknownSpots.size() > 0) {
            try {
                reportsEngine.produceReports(shrimpFractions,
                        (ShrimpFraction) unknownSpots.get(0),
                        referenceMaterialSpots.size() > 0
                        ? (ShrimpFraction) referenceMaterialSpots.get(0) : (ShrimpFraction) unknownSpots.get(0),
                        false, true);
            } catch (IOException iOException) {
            }
        }
    }

    /**
     * The original Calamari Reports
     */
    @Override
    public void produceSanityReportsToFiles() {
        if (unknownSpots.size() > 0) {
            try {
                reportsEngine.produceReports(shrimpFractions,
                        (ShrimpFraction) unknownSpots.get(0),
                        (ShrimpFraction) unknownSpots.get(0), true, false);
            } catch (IOException iOException) {
            }
        }
    }

    private void reorderExpressions() {
        Expression[] expArray = taskExpressionsOrdered.toArray(new Expression[0]);
        taskExpressionsOrdered.clear();
        taskExpressionTreesOrdered.clear();
        for (Expression listedExp : expArray) {
            taskExpressionsOrdered.add(listedExp);
            taskExpressionTreesOrdered.add((ExpressionTree) listedExp.getExpressionTree());
        }

    }

    public void processAndSortExpressions() {

        reorderExpressions();

        assembleNamedExpressionsMap();

        buildExpressions();

        evaluateTaskExpressions();

    }

    @Override
    /**
     *
     */
    public void updateAffectedExpressions(int repeats, Expression sourceExpression) {
        for (int i = 0; i < repeats; i++) {
            Expression[] expArray = taskExpressionsOrdered.toArray(new Expression[0]);
            for (Expression listedExp : expArray) {
                if (listedExp.getExpressionTree().usesAnotherExpression(sourceExpression.getExpressionTree())) {
                    updateSingleExpression(listedExp);
                }
            }
            processAndSortExpressions();
        }
        setChanged(true);
        setupSquidSessionSpecsAndReduceAndReport();
    }

    @Override
    /**
     * Updates expressions by parsing to detect new health or new sickness with
     * 'repeats' passes to capture side effects
     *
     */
    public void updateAllExpressions(int repeats) {
        for (int i = 0; i < repeats; i++) {
            Expression[] expArray = taskExpressionsOrdered.toArray(new Expression[0]);
            for (Expression listedExp : expArray) {
                updateSingleExpression(listedExp);
            }
            processAndSortExpressions();
        }

        setChanged(true);
        setupSquidSessionSpecsAndReduceAndReport();

        processAndSortExpressions();
    }

    private void updateSingleExpression(Expression listedExp) {
        ExpressionTreeInterface original = listedExp.getExpressionTree();
        listedExp.parseOriginalExpressionStringIntoExpressionTree(namedExpressionsMap);

        if (listedExp.getExpressionTree() instanceof BuiltInExpressionInterface) {
            ((BuiltInExpressionInterface) listedExp.getExpressionTree()).buildExpression(this);
        }
        if (original != null) {
            listedExp.getExpressionTree().setSquidSwitchSAUnknownCalculation(original.isSquidSwitchSAUnknownCalculation());
            listedExp.getExpressionTree().setSquidSwitchSTReferenceMaterialCalculation(original.isSquidSwitchSTReferenceMaterialCalculation());
            listedExp.getExpressionTree().setSquidSwitchSCSummaryCalculation(original.isSquidSwitchSCSummaryCalculation());
        }
    }

    @Override
    public void removeExpression(Expression expression) {
        if (expression != null) {
            // having issues with remove, so handling by hand
            // it appears java has a bug since even when coparator and equals have correct result
            SortedSet<Expression> taskBasket = new TreeSet<>();
            for (Expression exp : taskExpressionsOrdered) {
                if (!exp.equals(expression)) {
                    taskBasket.add(exp);
                }
            }   
            taskExpressionsOrdered = taskBasket;
            taskExpressionsRemoved.add(expression);
            processAndSortExpressions();
            updateAffectedExpressions(2, expression);
            setChanged(true);
            setupSquidSessionSpecsAndReduceAndReport();
        }
    }

    @Override
    public void restoreRemovedExpressions() {
        for (Expression exp : taskExpressionsRemoved) {
            addExpression(exp);
        }
        taskExpressionsRemoved.clear();
    }

    @Override
    public void addExpression(Expression exp) {
        taskExpressionsOrdered.add(exp);
        processAndSortExpressions();
        updateAllExpressions(3);
        setChanged(true);
        setupSquidSessionSpecsAndReduceAndReport();
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
        createMapOfIndexToMassStationDetails();
        if (mapOfIndexToMassStationDetails != null) {
            // update these if squidSpeciesModelList exists
            if (squidSpeciesModelList.size() > 0) {
                for (SquidSpeciesModel ssm : squidSpeciesModelList) {
                    MassStationDetail massStationDetail = mapOfIndexToMassStationDetails.get(ssm.getMassStationIndex());
                    // only these two fields change
                    massStationDetail.setIsotopeLabel(ssm.getIsotopeName());
                    massStationDetail.setIsBackground(ssm.getIsBackground());
                    if (ssm.getIsBackground()) {
                        indexOfBackgroundSpecies = massStationDetail.getMassStationIndex();
                    }
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

    private void buildSquidRatiosModelListFromMassStationDetails() {
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

        //TODO: Make a constants factory
        ExpressionTreeInterface testConstant = new ConstantNode("TEST_CONSTANT", 999.999);
        namedExpressionsMap.put(testConstant.getName(), testConstant);

        // TODO: make a SpotFieldNode factory
        ExpressionTreeInterface expHours = buildSpotNode("getHours");
        namedExpressionsMap.put(expHours.getName(), expHours);

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
        if ((findNumerator(ratioName) != null) && (findDenominator(ratioName) != null)) {
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

    public SquidSpeciesModel findNumerator(String ratioName) {
        String[] parts = ratioName.split("/");
        return lookUpSpeciesByName(parts[0]);
    }

    public SquidSpeciesModel findDenominator(String ratioName) {
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
    public Set<SquidSpeciesModel> extractUniqueSpeciesNumbers(List<String> ratiosOfInterest) {
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

        xstream.registerConverter(new VariableNodeForSummaryXMLConverter());
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

    @Override
    public List<ShrimpFractionExpressionInterface> processRunFractions(PrawnFile prawnFile, SquidSessionModel squidSessionSpecs) {
        shrimpFractions = new ArrayList<>();
        for (int f = 0; f < prawnFile.getRun().size(); f++) {
            PrawnFile.Run runFraction = prawnFile.getRun().get(f);

            ShrimpFraction shrimpFraction
                    = PRAWN_FILE_RUN_FRACTION_PARSER.processRunFraction(runFraction, squidSessionSpecs);
            if (shrimpFraction != null) {
                shrimpFraction.setSpotNumber(f + 1);
                String nameOfMount = prawnFile.getMount();
                if (nameOfMount == null) {
                    nameOfMount = "No-Mount-Name";
                }
                shrimpFraction.setNameOfMount(nameOfMount);
//
//                // preparing for field "Hours" specified as time in hours elapsed since first ref material analysis start = hh.###
//                if ((PRAWN_FILE_RUN_FRACTION_PARSER.getBaseTimeOfFirstRefMatForCalcHoursField() == 0l)
//                        && shrimpFraction.isReferenceMaterial()) {
//                    PRAWN_FILE_RUN_FRACTION_PARSER.setBaseTimeOfFirstRefMatForCalcHoursField(shrimpFraction.getDateTimeMilliseconds());
//                }
                shrimpFractions.add(shrimpFraction);
            }
        }

        // prepare for task expressions to be evaluated
        // setup spots
        shrimpFractions.forEach((spot) -> {
            List<TaskExpressionEvaluatedPerSpotPerScanModelInterface> taskExpressionsForScansEvaluated = new ArrayList<>();
            spot.setTaskExpressionsForScansEvaluated(taskExpressionsForScansEvaluated);
        });

        // subdivide spots and calculate hours
        referenceMaterialSpots = new ArrayList<>();
        unknownSpots = new ArrayList<>();
        boolean firstReferenceMaterial = true;
        long baseTimeOfFirstRefMatForCalcHoursField = 0l;
        for (ShrimpFractionExpressionInterface spot : shrimpFractions) {
            if (spot.isReferenceMaterial()) {
                referenceMaterialSpots.add(spot);
                if (firstReferenceMaterial) {
                    baseTimeOfFirstRefMatForCalcHoursField = spot.getDateTimeMilliseconds();
                    firstReferenceMaterial = false;
                }
            } else {
                unknownSpots.add(spot);
            }
        }

        for (ShrimpFractionExpressionInterface spot : shrimpFractions) {
            ((ShrimpFraction) spot).calculateSpotHours(baseTimeOfFirstRefMatForCalcHoursField);
        }

        return shrimpFractions;
    }

    /**
     *
     * @param shrimpFractions
     */
    @Override
    public void evaluateTaskExpressions() {

        taskExpressionsEvaluationsPerSpotSet = new TreeMap<>();
        // prep spots
        for (ShrimpFractionExpressionInterface spot : shrimpFractions) {
            spot.getTaskExpressionsForScansEvaluated().clear();
        }

        for (ExpressionTreeInterface expression : taskExpressionTreesOrdered) {
            if (expression.amHealthy() && expression.isRootExpressionTree()) {
                // determine subset of spots to be evaluated - default = all
                List<ShrimpFractionExpressionInterface> spotsForExpression = shrimpFractions;
                if (!((ExpressionTree) expression).isSquidSwitchSTReferenceMaterialCalculation()) {
                    spotsForExpression = unknownSpots;
                }
                if (!((ExpressionTree) expression).isSquidSwitchSAUnknownCalculation()) {
                    spotsForExpression = referenceMaterialSpots;
                }

                // now evaluate expression
                try {
                    evaluateExpressionForSpotSet(expression, spotsForExpression);
                } catch (SquidException | ArrayIndexOutOfBoundsException squidException) {

                }
            }
        }
    }

    /**
     *
     * @param expression
     * @param spotsForExpression
     * @throws SquidException
     */
    private void evaluateExpressionForSpotSet(
            ExpressionTreeInterface expression,
            List<ShrimpFractionExpressionInterface> spotsForExpression) throws SquidException {
        // determine type of expression
        if (((ExpressionTree) expression).isSquidSwitchSCSummaryCalculation()) {
            double[][] values = convertObjectArrayToDoubles(expression.eval(spotsForExpression, this));
            taskExpressionsEvaluationsPerSpotSet.put(expression.getName(),
                    new SpotSummaryDetails(((ExpressionTree) expression).getOperation(), values, spotsForExpression));
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
            ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();
            TaskExpressionEvaluatedPerSpotPerScanModelInterface taskExpressionEvaluatedPerSpotPerScanModel
                    = expressionEvaluator.evaluateTaskExpressionsPerSpotPerScan(this, expression, spot);

            // save scan-specific results
            spot.getTaskExpressionsForScansEvaluated().add(taskExpressionEvaluatedPerSpotPerScanModel);

            // save spot-specific results
            double[][] value = new double[][]{{taskExpressionEvaluatedPerSpotPerScanModel.getRatioVal(),
                taskExpressionEvaluatedPerSpotPerScanModel.getRatioFractErr()}};
            spot.getTaskExpressionsEvaluationsPerSpot().put(expression, value);
        } else {
            List<ShrimpFractionExpressionInterface> singleSpot = new ArrayList<>();
            singleSpot.add(spot);
            try {
                double[][] value = convertObjectArrayToDoubles(expression.eval(singleSpot, this));
                spot.getTaskExpressionsEvaluationsPerSpot().put(expression, value);
            } catch (Exception squidException) {
                System.out.println(squidException.getMessage());
            }
        }
    }

    /**
     * @return the listOfMassStationDetails
     */
    @Override
    public List<MassStationDetail> makeListOfMassStationDetails() {
        List<MassStationDetail> listOfMassStationDetails = new ArrayList<>();
        for (Map.Entry<Integer, MassStationDetail> entry : mapOfIndexToMassStationDetails.entrySet()) {
            listOfMassStationDetails.add(entry.getValue());
            if (entry.getValue().getIsBackground()) {
                entry.getValue().setIsotopeLabel(SQUID_DEFAULT_BACKGROUND_ISOTOPE_LABEL);
                indexOfBackgroundSpecies = entry.getKey();
            }
        }
        return listOfMassStationDetails;
    }

    private void populateTableOfSelectedRatiosFromRatiosList() {
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

        setChanged(true);
        setupSquidSessionSpecsAndReduceAndReport();
        updateAllExpressions(2);
//        processAndSortExpressions();
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
     * @param filterForRefMatSpotNames the filterForRefMatSpotNames to set
     */
    @Override
    public void setFilterForRefMatSpotNames(String filterForRefMatSpotNames) {
        this.filterForRefMatSpotNames = filterForRefMatSpotNames;
    }

    /**
     * @return the useSBM
     */
    @Override
    public boolean isUseSBM() {
        return useSBM;
    }

    /**
     * @param useSBM the useSBM to set
     */
    @Override
    public void setUseSBM(boolean useSBM) {
        this.useSBM = useSBM;
    }

    /**
     * @return the userLinFits
     */
    @Override
    public boolean isUserLinFits() {
        return userLinFits;
    }

    /**
     * @param userLinFits the userLinFits to set
     */
    @Override
    public void setUserLinFits(boolean userLinFits) {
        this.userLinFits = userLinFits;
    }

    /**
     * @return the indexOfBackgroundSpecies
     */
    public int getIndexOfBackgroundSpecies() {
        return indexOfBackgroundSpecies;
    }

    /**
     * @param indexOfBackgroundSpecies the indexOfBackgroundSpecies to set
     */
    public void setIndexOfBackgroundSpecies(int indexOfBackgroundSpecies) {
        this.indexOfBackgroundSpecies = indexOfBackgroundSpecies;
    }

    /**
     * @return the taskExpressionTreesOrdered
     */
    @Override
    public SortedSet<ExpressionTree> getTaskExpressionTreesOrdered() {
        return taskExpressionTreesOrdered;
    }

    /**
     * @param taskExpressionTreesOrdered the taskExpressionTreesOrdered to set
     */
    @Override
    public void setTaskExpressionTreesOrdered(SortedSet<ExpressionTree> taskExpressionTreesOrdered) {
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
     * @return the nominalMasses
     */
    public List<String> getNominalMasses() {
        return nominalMasses;
    }

    /**
     * @param nominalMasses the nominalMasses to set
     */
    public void setNominalMasses(List<String> nominalMasses) {
        this.nominalMasses = nominalMasses;
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

    private void resetTableOfSelectedRatiosByMassStationIndex() {
        tableOfSelectedRatiosByMassStationIndex = new boolean[squidSpeciesModelList.size()][squidSpeciesModelList.size()];
    }

    /**
     * @param prawnFile the prawnFile to set
     */
    @Override
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
    @Override
    public SortedSet<Expression> getTaskExpressionsOrdered() {
        return taskExpressionsOrdered;
    }

    /**
     * @param changed the changed to set
     */
    @Override
    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    /**
     * @return the shrimpFractions
     */
    @Override
    public List<ShrimpFractionExpressionInterface> getShrimpFractions() {
        return shrimpFractions;
    }

    /**
     * @return the referenceMaterialSpots
     */
    @Override
    public List<ShrimpFractionExpressionInterface> getReferenceMaterialSpots() {
        return referenceMaterialSpots;
    }

    /**
     * @return the unknownSpots
     */
    @Override
    public List<ShrimpFractionExpressionInterface> getUnknownSpots() {
        return unknownSpots;
    }

    /**
     * @param reportsEngine the reportsEngine to set
     */
    @Override
    public void setReportsEngine(CalamariReportsEngine reportsEngine) {
        this.reportsEngine = reportsEngine;
    }
}
