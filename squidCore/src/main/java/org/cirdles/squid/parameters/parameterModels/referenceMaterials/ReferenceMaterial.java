/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.parameters.parameterModels.referenceMaterials;

import Jama.Matrix;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.ObjectStreamClass;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.cirdles.squid.parameters.matrices.CorrelationMatrixModel;
import org.cirdles.squid.parameters.matrices.CovarianceMatrixModel;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.util.Lambdas;
import org.cirdles.squid.parameters.util.ReferenceMaterialEnum;
import org.cirdles.squid.parameters.util.XStreamETReduxConverters.ETReduxRefMatConverter;
import org.cirdles.squid.parameters.valueModels.Age206_238r;
import org.cirdles.squid.parameters.valueModels.Age207_206r;
import org.cirdles.squid.parameters.valueModels.Age207_235r;
import org.cirdles.squid.parameters.valueModels.Age208_232r;
import org.cirdles.squid.parameters.valueModels.ValueModel;
import org.cirdles.squid.utilities.stateUtilities.SquidLabData;

/**
 *
 * @author ryanb
 */
public class ReferenceMaterial extends ParametersModel {
    
    private static long serialVersionUID = 1513337897720796891L;

    private static ReferenceMaterial defaultReferenceMaterial = getDefaultModels().get(1);

    ValueModel[] concentrations;
    boolean[] dataMeasured;
    private ValueModel[] apparentDates;
    private ConcurrentMap<String, BigDecimal> parDerivTerms;

    public ReferenceMaterial() {
        super();
        apparentDates = new ValueModel[0];
        parDerivTerms = new ConcurrentHashMap<>();
        generateDefaultValueModels();
        concentrations = new ValueModel[0];
    }

    public ReferenceMaterial clone() {
        ReferenceMaterial model = new ReferenceMaterial();

        model.setModelName(modelName + " - copy");
        model.setLabName(labName);
        model.setVersion(version);
        model.setDateCertified(dateCertified);
        model.setComments(comments);
        model.setReferences(references);
        model.setValues(values.clone());
        model.setCorrModel((CorrelationMatrixModel) corrModel.copy());
        model.setCovModel((CovarianceMatrixModel) covModel.copy());
        model.setConcentrations(concentrations.clone());
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

    public static List<ReferenceMaterial> getDefaultModels() {
        File folder = new File("SampleReferenceMaterials/");
        File[] files = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                boolean retVal;
                if (name.toLowerCase().endsWith(".xml")) {
                    retVal = true;
                } else {
                    retVal = false;
                }
                return retVal;
            }
        });
        List<ReferenceMaterial> models = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            models.add(ReferenceMaterial.getReferenceMaterialFromETReduxXML(files[i]));
        }

        return models;
    }

    public void calculateApparentDates() {

        ValueModel lambda232 = PhysicalConstantsModel.defaultPhysicalConstantsModel//
                .getDatumByName(Lambdas.lambda232.getName());

        ValueModel lambda235 = PhysicalConstantsModel.defaultPhysicalConstantsModel//
                .getDatumByName(Lambdas.lambda235.getName());
        ValueModel lambda238 = PhysicalConstantsModel.defaultPhysicalConstantsModel//
                .getDatumByName(Lambdas.lambda238.getName());

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

        apparentDates = new ValueModel[4];

        apparentDates[0] = new Age206_238r();
        getApparentDates()[0].calculateValue(
                new ValueModel[]{
                    r206_238r,
                    lambda238}, parDerivTerms);
        if (parDerivTerms.containsKey("dAge206_238r__dR206_238r")) {
            apparentDates[0].setOneSigma(//
                    parDerivTerms.get("dAge206_238r__dR206_238r") //
                            .abs()//
                            .multiply(r206_238r.getOneSigmaABS()));
        }

        apparentDates[1] = new Age207_206r();
        getApparentDates()[1].calculateValue(
                new ValueModel[]{
                    r238_235s,
                    r207_206r,
                    apparentDates[0],
                    lambda235,
                    lambda238}, parDerivTerms);
        if (parDerivTerms.containsKey("dAge207_206r__dR207_206r")) {
            apparentDates[1].setOneSigma(//
                    parDerivTerms.get("dAge207_206r__dR207_206r")//
                            .abs()//
                            .multiply(r207_206r.getOneSigmaABS()));
        }

        apparentDates[2] = new Age207_235r();
        getApparentDates()[2].calculateValue(
                new ValueModel[]{
                    r207_235r,
                    lambda235}, parDerivTerms);
        if (parDerivTerms.containsKey("dAge207_235r__dR207_235r")) {
            apparentDates[2].setOneSigma(//
                    parDerivTerms.get("dAge207_235r__dR207_235r")//
                            .abs()//
                            .multiply(r207_235r.getOneSigmaABS()));//******************************calc this with matrices
        }

        apparentDates[3] = new Age208_232r();
        getApparentDates()[3].calculateValue(
                new ValueModel[]{
                    r208_232r,
                    lambda232}, parDerivTerms);
        if (parDerivTerms.containsKey("dAge208_232r__dR208_232r")) {
            apparentDates[3].setOneSigma(//
                    parDerivTerms.get("dAge208_232r__dR208_232r")//
                            .abs()//
                            .multiply(r208_232r.getOneSigmaABS()));
        }
    }

    /**
     *
     * @return
     */
    public String listFormattedApparentDates() {
        calculateApparentDates();

        String retVal = "";

        for (int i = 0; i < apparentDates.length; i++) {
            if (apparentDates[i].hasPositiveValue()) {
                retVal += apparentDates[i].getName() + " : "
                        + apparentDates[i].formatValueAndTwoSigmaForPublicationSigDigMode( //
                                "ABS", -6, 2) //
                        + " (2\u03C3)  Ma\n";
            }
        }

        return retVal;
    }

    public ValueModel[] getApparentDates() {
        return apparentDates;
    }

    public void setApparentDates(ValueModel[] apparentDates) {
        this.apparentDates = apparentDates;
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
        xstream.alias("ReferenceMaterial", ReferenceMaterial.class);
        xstream.alias("MineralStandardUPbModel", ReferenceMaterial.class);
        return xstream;
    }

    public static ReferenceMaterial getReferenceMaterialFromETReduxXML(String input) {
        XStream xstream = getETReduxXStream();
        ReferenceMaterial model = (ReferenceMaterial) xstream.fromXML(input);
        return model;
    }

    public static ReferenceMaterial getReferenceMaterialFromETReduxXML(File input) {
        XStream xstream = getETReduxXStream();
        ReferenceMaterial model = (ReferenceMaterial) xstream.fromXML(input);
        return model;
    }

    public static void main(String[] args) {
        ObjectStreamClass stream = ObjectStreamClass.lookup(ReferenceMaterial.class);
        System.out.println("serialVersionUID: " + stream.getSerialVersionUID());
    }
}