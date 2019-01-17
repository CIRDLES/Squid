/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.parameters.parameterModels.referenceMaterialModels;

import Jama.Matrix;
import com.thoughtworks.xstream.XStream;
import org.cirdles.squid.parameters.matrices.CorrelationMatrixModel;
import org.cirdles.squid.parameters.matrices.CovarianceMatrixModel;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.util.Lambdas;
import org.cirdles.squid.parameters.util.ReferenceMaterialEnum;
import org.cirdles.squid.parameters.util.XStreamETReduxConverters.ETReduxRefMatConverter;
import org.cirdles.squid.parameters.valueModels.*;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author ryanb
 */
public class ReferenceMaterialModel extends ParametersModel {

    private static final long serialVersionUID = 8791002391578871182L;

    ValueModel[] concentrations;
    boolean[] dataMeasured;
    private ValueModel[] dates;
    private ConcurrentMap<String, BigDecimal> parDerivTerms;

    public ReferenceMaterialModel() {
        super();
        dates = new ValueModel[0];
        parDerivTerms = new ConcurrentHashMap<>();
        generateDefaultValueModels();
        initializeDefaultConcentrations();
    }

    private void initializeDefaultConcentrations() {
        concentrations = new ValueModel[2];
        concentrations[0] = new ValueModel("concU", "ABS", BigDecimal.ZERO, BigDecimal.ZERO);
        concentrations[1] = new ValueModel("concTh", "ABS", BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Override
    public ReferenceMaterialModel clone() {
        ReferenceMaterialModel model = new ReferenceMaterialModel();

        model.setModelName(modelName);
        model.setIsEditable(isEditable);
        model.setLabName(labName);
        model.setVersion(version);
        model.setDateCertified(dateCertified);
        model.setComments(comments);
        model.setReferences(references);

        ValueModel[] vals = new ValueModel[values.length];
        for (int i = 0; i < vals.length; i++) {
            ValueModel curr = values[i];
            vals[i] = new ValueModel(curr.getName(), curr.getUncertaintyType(),
                    curr.getValue(), curr.getOneSigma());
        }
        model.setValues(vals);

        model.setCorrModel((CorrelationMatrixModel) corrModel.copy());
        model.setCovModel((CovarianceMatrixModel) covModel.copy());

        Map<String, BigDecimal> newRhos = new HashMap<>();
        model.setRhos(newRhos);
        Iterator<Entry<String, BigDecimal>> rhosIterator = rhos.entrySet().iterator();
        while (rhosIterator.hasNext()) {
            Entry<String, BigDecimal> entry = rhosIterator.next();
            newRhos.put(entry.getKey(), entry.getValue());
        }

        ValueModel[] concs = new ValueModel[concentrations.length];
        for (int i = 0; i < concs.length; i++) {
            ValueModel curr = concentrations[i];
            concs[i] = new ValueModel(curr.getName(), curr.getUncertaintyType(),
                    curr.getValue(), curr.getOneSigma());
        }
        model.setConcentrations(concs);

        model.setDataMeasured(dataMeasured.clone());

        return model;
    }

    private void generateDefaultValueModels() {
        values = new ValueModel[5];
        values[0] = new ValueModel("r206_207r");
        values[1] = new ValueModel("r206_208r");
        values[2] = new ValueModel("r206_238r");
        values[3] = new ValueModel("r208_232r");
        values[4] = new ValueModel("r238_235s");
        dataMeasured = new boolean[5];
        for (int i = 0; i < dataMeasured.length; i++) {
            dataMeasured[i] = false;
        }
    }

    public ValueModel getConcentrationByName(String name) {
        ValueModel retVal = new ValueModel(name);
        boolean found = false;
        for (int i = 0; i < concentrations.length && !found; i++) {
            if (concentrations[i].getName().equals(name)) {
                retVal = concentrations[i];
                found = true;
            }
        }
        return retVal;
    }

    public ValueModel getDateByName(String name) {
        ValueModel retVal = new ValueModel(name);
        boolean found = false;
        for (int i = 0; i < dates.length && !found; i++) {
            if (dates[i].getName().equals(name)) {
                retVal = dates[i];
                found = true;
            }
        }
        return retVal;
    }

    public final void initializeNewRatiosAndRhos() {
        ArrayList<ValueModel> holdRatios = new ArrayList<>();
        for (ReferenceMaterialEnum value : ReferenceMaterialEnum.values()) {
            holdRatios.add( //
                    new ValueModel(value.getName(),
                            "ABS", BigDecimal.ZERO,
                            BigDecimal.ZERO));
        }

        values = holdRatios.toArray(new ValueModel[holdRatios.size()]);

        Arrays.sort(values, new DataValueModelNameComparator());

        buildRhosMap();
    }

    public static List<ParametersModel> getDefaultModels() {
        File folder = new File("SampleReferenceMaterialModels");
        File[] files = new File[0];
        List<ParametersModel> models = new ArrayList<>();
        if (folder.exists()) {
            files = folder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".xml");
                }
            });
            for (int i = 0; i < files.length; i++) {
                models.add((ParametersModel) (new ReferenceMaterialModel()).readXMLObject(files[i].getAbsolutePath(), false));
            }
        }

        return models;
    }

    public static ParametersModel getDefaultModel(String modelName, String version) {
        ParametersModel retVal = null;
        List<ParametersModel> models = getDefaultModels();
        for (int i = 0; i < models.size() && retVal == null; i++) {
            if (models.get(i).getModelName().equals(modelName) && models.get(i).getVersion().equals(version)) {
                retVal = models.get(i);
            }
        }
        if (retVal == null) {
            retVal = new ReferenceMaterialModel();
        }
        return retVal;
    }

    public void calculateApparentDates() {

        ParametersModel defaultPhysConstModel =
                PhysicalConstantsModel.getDefaultModel("EARTHTIME Physical Constants Model", "1.1");
        ValueModel lambda232 = defaultPhysConstModel//
                .getDatumByName(Lambdas.LAMBDA_232.getName());

        ValueModel lambda235 = defaultPhysConstModel//
                .getDatumByName(Lambdas.LAMBDA_235.getName());
        ValueModel lambda238 = defaultPhysConstModel//
                .getDatumByName(Lambdas.LAMBDA_238.getName());

        ValueModel r206_238r = getDatumByName(ReferenceMaterialEnum.r206_238r.getName());

        // make inverted ratio
        ValueModel r206_207r = getDatumByName(ReferenceMaterialEnum.r206_207r.getName());
        BigDecimal invertedR206_207r = BigDecimal.ZERO;
        try {
            invertedR206_207r = BigDecimal.ONE.divide(r206_207r.getValue(), new MathContext(15, RoundingMode.HALF_UP));
        } catch (Exception e) {
        }

        ValueModel r207_206r = new ValueModel("r207_206r");
        r207_206r.setValue(invertedR206_207r);
        // percent uncertainty is constant between ratio and invert
        r207_206r.setUncertaintyType("PCT");
        r207_206r.setOneSigma(r206_207r.getOneSigmaPCT());

        ValueModel r238_235s = getDatumByName(ReferenceMaterialEnum.r238_235s.getName());

        ValueModel r207_235r = new ValueModel("r207_235r");
        try {
            r207_235r.setValue(r238_235s.getValue()//
                    .multiply(r206_238r.getValue()//
                            .divide(r206_207r.getValue(), new MathContext(15, RoundingMode.HALF_UP))));

            // calculate uncertainty
            double[][] v = new double[1][covModel.getRows().size()];
            Iterator<Integer> rowKeys = covModel.getRows().keySet().iterator();
            while (rowKeys.hasNext()) {
                int rowKey = rowKeys.next();

                if (covModel.getRows().get(rowKey).equalsIgnoreCase(//
                        ReferenceMaterialEnum.r206_207r.getName())) {
                    v[0][rowKey] = r206_238r.getValue()//
                            .multiply(r238_235s.getValue())//
                            .divide(r206_207r.getValue().pow(2), new MathContext(15, RoundingMode.HALF_UP))//
                            .doubleValue();
                }

                if (covModel.getRows().get(rowKey).equalsIgnoreCase(//
                        ReferenceMaterialEnum.r206_208r.getName())) {
                    v[0][rowKey] = 0.0;
                }

                if (covModel.getRows().get(rowKey).equalsIgnoreCase(//
                        ReferenceMaterialEnum.r206_238r.getName())) {
                    v[0][rowKey] = r238_235s.getValue()//
                            .divide(r206_207r.getValue(), new MathContext(15, RoundingMode.HALF_UP)).doubleValue();
                }

                if (covModel.getRows().get(rowKey).equalsIgnoreCase(//
                        ReferenceMaterialEnum.r208_232r.getName())) {
                    v[0][rowKey] = 0.0;
                }

                if (covModel.getRows().get(rowKey).equalsIgnoreCase(//
                        ReferenceMaterialEnum.r238_235s.getName())) {
                    v[0][rowKey] = r206_238r.getValue()//
                            .divide(r206_207r.getValue(), new MathContext(15, RoundingMode.HALF_UP)).doubleValue();
                }
            }

            Matrix V = new Matrix(v);

            double varianceR207_235r = V.times(covModel.getMatrix()).times(V.transpose()).get(0, 0);

            r207_235r.setOneSigma(new BigDecimal(Math.sqrt(varianceR207_235r)));

        } catch (Exception e) {
        }

        ValueModel r208_232r = getDatumByName(ReferenceMaterialEnum.r208_232r.getName());

        dates = new ValueModel[4];

        dates[0] = new Age206_238r();
        getDates()[0].calculateValue(
                new ValueModel[]{
                        r206_238r,
                        lambda238}, parDerivTerms);
        if (parDerivTerms.containsKey("dAge206_238r__dR206_238r")) {
            dates[0].setOneSigma(//
                    parDerivTerms.get("dAge206_238r__dR206_238r") //
                            .abs()//
                            .multiply(r206_238r.getOneSigmaABS()));
        }

        dates[1] = new Age207_206r();
        getDates()[1].calculateValue(
                new ValueModel[]{
                        r238_235s,
                        r207_206r,
                        dates[0],
                        lambda235,
                        lambda238}, parDerivTerms);
        if (parDerivTerms.containsKey("dAge207_206r__dR207_206r")) {
            dates[1].setOneSigma(//
                    parDerivTerms.get("dAge207_206r__dR207_206r")//
                            .abs()//
                            .multiply(r207_206r.getOneSigmaABS()));
        }

        dates[2] = new Age207_235r();
        getDates()[2].calculateValue(
                new ValueModel[]{
                        r207_235r,
                        lambda235}, parDerivTerms);
        if (parDerivTerms.containsKey("dAge207_235r__dR207_235r")) {
            dates[2].setOneSigma(//
                    parDerivTerms.get("dAge207_235r__dR207_235r")//
                            .abs()//
                            .multiply(r207_235r.getOneSigmaABS()));//******************************calc this with matrices
        }

        dates[3] = new Age208_232r();
        getDates()[3].calculateValue(
                new ValueModel[]{
                        r208_232r,
                        lambda232}, parDerivTerms);
        if (parDerivTerms.containsKey("dAge208_232r__dR208_232r")) {
            dates[3].setOneSigma(//
                    parDerivTerms.get("dAge208_232r__dR208_232r")//
                            .abs()//
                            .multiply(r208_232r.getOneSigmaABS()));
        }
    }

    /**
     * @return
     */
    public String listFormattedApparentDates() {
        calculateApparentDates();

        String retVal = "";

        for (int i = 0; i < dates.length; i++) {
            if (dates[i].hasPositiveValue()) {
                retVal += dates[i].getName() + " : "
                        + dates[i].formatValueAndTwoSigmaForPublicationSigDigMode( //
                        "ABS", -6, 2) //
                        + " (2\u03C3)  Ma\n";
            }
        }

        return retVal;
    }

    public ValueModel[] getDates() {
        return dates;
    }

    public void setDates(ValueModel[] dates) {
        this.dates = dates;
    }

    public ConcurrentMap<String, BigDecimal> getParDerivTerms() {
        return parDerivTerms;
    }

    public void setParDerivTerms(ConcurrentMap<String, BigDecimal> parDerivTerms) {
        this.parDerivTerms = parDerivTerms;
    }

    public ValueModel[] getConcentrations() {
        return concentrations;
    }

    public void setConcentrations(ValueModel[] concentrations) {
        this.concentrations = concentrations;
    }

    public boolean[] getDataMeasured() {
        return dataMeasured;
    }

    public void setDataMeasured(boolean[] dataMeasured) {
        this.dataMeasured = dataMeasured;
    }

    public static XStream getETReduxXStream() {
        XStream xstream = new XStream();
        xstream.registerConverter(new ETReduxRefMatConverter());
        xstream.alias("ReferenceMaterial", ReferenceMaterialModel.class);
        xstream.alias("MineralStandardUPbModel", ReferenceMaterialModel.class);
        return xstream;
    }

    public static ReferenceMaterialModel getReferenceMaterialFromETReduxXML(String input) {
        XStream xstream = getETReduxXStream();
        ReferenceMaterialModel model = (ReferenceMaterialModel) xstream.fromXML(input);
        return model;
    }

    public static ReferenceMaterialModel getReferenceMaterialFromETReduxXML(File input) {
        XStream xstream = getETReduxXStream();
        ReferenceMaterialModel model = (ReferenceMaterialModel) xstream.fromXML(input);
        return model;
    }
    
}
