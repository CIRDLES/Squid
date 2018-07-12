/*
 * ReportSettings.java
 *
 * Created on September 3, 2008, 9:18 AM
 *
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.cirdles.squid.reports.reportSettings;

import org.cirdles.squid.reports.reportColumns.ReportColumnXMLConverter;
import org.cirdles.squid.reports.reportColumns.ReportColumn;
import org.cirdles.squid.reports.reportCategories.ReportCategoryXMLConverter;
import org.cirdles.squid.reports.reportCategories.ReportCategoryInterface;
import org.cirdles.squid.reports.reportCategories.ReportCategory;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.cirdles.squid.reports.reportSpecifications.ReportSpecificationsAbstract;
import org.cirdles.squid.reports.reportSpecifications.ReportSpecificationsUPbReferenceMaterials;
import org.cirdles.squid.reports.reportSpecifications.ReportSpecificationsUPbSamples;
import org.cirdles.squid.tasks.TaskInterface;

/**
 *
 * @author James F. Bowring
 */
public class ReportSettings implements
        ReportSettingsInterface {

    //private static final long serialVersionUID = 3742875572117123821L;
    private transient String reportSettingsXMLSchemaURL;
    /**
     * Each time the report specifications evolve in DataDictionary, this
     * version number is advanced so that any existing analysis will update its
     * report models upon opening in ET_Redux.
     */
    private static transient int CURRENT_VERSION_REPORT_SETTINGS_UPB = 1;

    // Fields
    private String name;
    private int version;
    private TaskInterface task;
    private ReportCategoryInterface fractionCategory;

    // for reference materials
    private ReportCategoryInterface spotFundamentalsCategory;
    private ReportCategoryInterface cpsCategory;
    private ReportCategoryInterface rawRatiosCategory;
    private ReportCategoryInterface customExpressionsCategory;
    private ReportCategoryInterface correctionIndependentRMCategory;

    // for Unknown Samples
    private ReportCategoryInterface correctionIndependentCategory;
    private ReportCategoryInterface pb204CorrectedCategory;
    private ReportCategoryInterface pb207CorrectedCategory;
    private ReportCategoryInterface pb208CorrectedCategory;

    private ReportCategoryInterface fractionCategory2;
    protected ArrayList<ReportCategoryInterface> reportCategories;
    private String reportSettingsComment;
    private boolean referenceMaterial;

    /**
     * Creates a new instance of ReportSettings
     */
    public ReportSettings() {
        this("NONE", true, null);
    }

    /**
     * Creates a new instance of ReportSettings
     *
     * @param name
     * @param referenceMaterial
     * @param task
     */
    public ReportSettings(String name, boolean referenceMaterial, TaskInterface task) {

        this.name = name;
        this.referenceMaterial = referenceMaterial;
        this.task = task;

        this.version = CURRENT_VERSION_REPORT_SETTINGS_UPB;

        this.reportSettingsComment = "";

        this.fractionCategory
                = new ReportCategory(
                        "Fraction",
                        ReportSpecificationsAbstract.ReportCategory_Fraction, true, task);

        this.fractionCategory2
                = new ReportCategory(
                        "Fraction",
                        ReportSpecificationsAbstract.ReportCategory_Fraction2, true, task);

        if (referenceMaterial) {
            this.spotFundamentalsCategory
                    = new ReportCategory(
                            "Spot Fundamentals",
                            ReportSpecificationsUPbReferenceMaterials.ReportCategory_SpotFundamentals, true, task);
            this.cpsCategory
                    = new ReportCategory(
                            "CPS",
                            ReportSpecificationsUPbReferenceMaterials.ReportCategory_CPS, true, task);
            this.rawRatiosCategory
                    = new ReportCategory(
                            "Raw Nuclide Ratios",
                            ReportSpecificationsUPbReferenceMaterials.ReportCategory_RawRatios, true, task);
            this.customExpressionsCategory
                    = new ReportCategory(
                            "Custom",
                            ReportSpecificationsUPbReferenceMaterials.ReportCategory_CustomExpressions, true, task);
            this.correctionIndependentRMCategory
                    =new ReportCategory(
                            "Correction-Independent Built-In",
                            ReportSpecificationsUPbReferenceMaterials.ReportCategory_CorrectionIndependent, true, task);

        } else {
            this.correctionIndependentCategory
                    = new ReportCategory(
                            "Correction-Independent Data",
                            ReportSpecificationsUPbSamples.ReportCategory_CorrectionIndependentData, true, task);

            this.pb204CorrectedCategory
                    = new ReportCategory(
                            "204Pb-Corrected",
                            ReportSpecificationsUPbSamples.ReportCategory_204PbCorrected, true, task);

            this.pb207CorrectedCategory
                    = new ReportCategory(
                            "207Pb-Corrected",
                            ReportSpecificationsUPbSamples.ReportCategory_207PbCorrected, true, task);

            this.pb208CorrectedCategory
                    = new ReportCategory(
                            "208Pb-Corrected",
                            ReportSpecificationsUPbSamples.ReportCategory_208PbCorrected, true, task);

        }

        assembleReportCategories(this.referenceMaterial);
        normalizeReportCategories();

    }

    public static ReportSettingsInterface getReportSettingsModelUpdatedToLatestVersion(ReportSettingsInterface myReportSettingsModel) {
        ReportSettingsInterface reportSettingsModel = myReportSettingsModel;

//        if (myReportSettingsModel == null) {
//            reportSettingsModel = ReduxLabData.getInstance().getDefaultReportSettingsModelBySpecsType(myReportSettingsModel.getDefaultReportSpecsType());
//        } else // new approach oct 2014
//        {   // this provides for seamless updates to reportsettings implementation
//            String myReportSettingsName = myReportSettingsModel.getName();
//            if (myReportSettingsModel.isOutOfDate()) {
//                JOptionPane.showMessageDialog(null,
//                        new String[]{"As part of our ongoing development efforts,",
//                            "the report settings file you are using is being updated.",
//                            "You may lose some report customizations. Thank you for your patience."//,
//                        });
//
//                reportSettingsModel = new ReportSettings(myReportSettingsName, myReportSettingsModel.getDefaultReportSpecsType());
//            }
//        }
        //TODO http://www.javaworld.com/article/2077736/open-source-tools/xml-merging-made-easy.html
        return reportSettingsModel;
    }

    /**
     *
     * @param filename
     */
//    @Override
    public void serializeXMLObject(String filename) {
        XStream xstream = getXStreamWriter();

        String xml = xstream.toXML(this);

//        xml = ReduxConstants.XML_Header + xml;
//
//        xml = xml.replaceFirst("ReportSettings",
//                "ReportSettings  "//
//                + ReduxConstants.XML_ResourceHeader//
//                + getReportSettingsXMLSchemaURL() //
//
//                + "\"");
        try {
            FileWriter outFile = new FileWriter(filename);
            PrintWriter out = new PrintWriter(outFile);

            // Write xml to file
            out.println(xml);
            out.flush();
            out.close();
            outFile.close();

        } catch (IOException e) {
        }
    }

    /**
     *
     * @return
     */
    public ReportSettingsInterface deepCopy() {
        ReportSettingsInterface reportSettingsModel = null;

        String tempFileName = "TEMPreportSettings.xml";
        // write out the settings
        serializeXMLObject(tempFileName);

        // read them back in
//        try {
//            reportSettingsModel = (ReportSettingsInterface) readXMLObject(tempFileName, false);
//        } catch (FileNotFoundException | ETException | BadOrMissingXMLSchemaException fileNotFoundException) {
//        }
        File tempFile = new File(tempFileName);
        tempFile.delete();

        return reportSettingsModel;
    }

    /**
     *
     * @param reportSettingsModel
     * @return
     * @throws ClassCastException
     */
    @Override
    public int compareTo(ReportSettingsInterface reportSettingsModel)
            throws ClassCastException {
        String reportSettingsModelNameAndVersion
                = reportSettingsModel.getNameAndVersion();
        return this.getNameAndVersion().trim().//
                compareToIgnoreCase(reportSettingsModelNameAndVersion.trim());
    }

//  accessors
    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    @Override
    public int getVersion() {
        return version;
    }

    /**
     *
     * @param version
     */
    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     *
     * @return
     */
    @Override
    public ReportCategoryInterface getFractionCategory() {
        return fractionCategory;
    }

    /**
     *
     * @param fractionCategory
     */
    @Override
    public void setFractionCategory(ReportCategoryInterface fractionCategory) {
        this.fractionCategory = fractionCategory;
    }

    /**
     *
     * @return
     */
    @Override
    public ReportCategoryInterface getCorrectionIndependentCategory() {
        return correctionIndependentCategory;
    }

    /**
     *
     * @param correctionIndependentCategory
     */
    @Override
    public void setCorrectionIndependentCategory(ReportCategoryInterface correctionIndependentCategory) {
        this.correctionIndependentCategory = correctionIndependentCategory;
    }

    /**
     *
     * @return
     */
    @Override
    public ReportCategoryInterface getPb204CorrectedCategory() {
        return pb204CorrectedCategory;
    }

    /**
     *
     * @param pb204CorrectedCategory
     */
    @Override
    public void setPb204CorrectedCategory(ReportCategoryInterface pb204CorrectedCategory) {
        this.pb204CorrectedCategory = pb204CorrectedCategory;
    }

    /**
     *
     * @param reportSettingsModel
     * @return
     */
    @Override
    public boolean equals(Object reportSettingsModel) {
        //check for self-comparison
        if (this == reportSettingsModel) {
            return true;
        }

        if (!(reportSettingsModel instanceof ReportSettingsInterface)) {
            return false;
        }

        ReportSettingsInterface myReportSettings = (ReportSettingsInterface) reportSettingsModel;

        return (this.getNameAndVersion().trim().
                compareToIgnoreCase(myReportSettings.getNameAndVersion().trim()) == 0);

    }

// http://www.javaworld.com/javaworld/jw-01-1999/jw-01-object.html?page=4
    /**
     *
     * @return
     */
    @Override
    public int hashCode() {

        return 0;
    }

// XML Serialization
    /**
     * registers converter for argument <code>xstream</code> and sets aliases to
     * make the XML file more human-readable
     *
     * @pre argument <code>xstream</code> is a valid <code>XStream</code>
     * @post argument <code>xstream</code> is customized to produce a cleaner
     * output <code>file</code>
     *
     * @param xstream <code>XStream</code> to be customized
     */
    @Override
    public void customizeXstream(XStream xstream) {

        xstream.registerConverter(new ReportSettingsXMLConverter());
        xstream.registerConverter(new ReportCategoryXMLConverter());
        xstream.registerConverter(new ReportColumnXMLConverter());

        xstream.alias("ReportSettings", ReportSettings.class);
        xstream.alias("ReportCategory", ReportCategory.class);
        xstream.alias("ReportColumn", ReportColumn.class);

        setClassXMLSchemaURL();
    }

    /**
     * sets the XML schema. Initializes <code>UPbReduxConfigurator</code> and
     * sets the location of the XML Schema
     *
     * @pre <code>UPbReduxConfigurator</code> class is available
     * @post <code>TracerXMLSchemaURL</code> will be set
     */
    @Override
    public void setClassXMLSchemaURL() {
//        UPbReduxConfigurator myConfigurator = new UPbReduxConfigurator();
//
//        reportSettingsXMLSchemaURL
//                = myConfigurator.getResourceURI("URI_ReportSettingsXMLSchema");
    }

    /**
     *
     * @return
     */
    @Override
    public String getReportSettingsComment() {
        return reportSettingsComment;
    }

    /**
     *
     * @param reportSettingsComment
     */
    @Override
    public void setReportSettingsComment(String reportSettingsComment) {
        this.reportSettingsComment = reportSettingsComment;
    }

    /**
     * @return the fractionCategory2
     */
    @Override
    public ReportCategoryInterface getFractionCategory2() {
        return fractionCategory2;
    }

    /**
     * @param fractionCategory2 the fractionCategory2 to set
     */
    @Override
    public void setFractionCategory2(ReportCategoryInterface fractionCategory2) {
        this.fractionCategory2 = fractionCategory2;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isOutOfDate() {
        boolean retVal = isOutOfDateUPb();
        return retVal;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isOutOfDateUPb() {
        return this.version < CURRENT_VERSION_REPORT_SETTINGS_UPB;
    }

    /**
     *
     */
    @Override
    public void removeSelf() {
    }

    /**
     * @return the reportCategories
     */
    @Override
    public ArrayList<ReportCategoryInterface> getReportCategories() {
        return reportCategories;
    }

    /**
     * @return the reportSettingsXMLSchemaURL
     */
    @Override
    public String getReportSettingsXMLSchemaURL() {
        return reportSettingsXMLSchemaURL;
    }

    /**
     * @param reportCategories the reportCategories to set
     */
    @Override
    public void setReportCategories(ArrayList<ReportCategoryInterface> reportCategories) {
        this.reportCategories = reportCategories;
    }

    public static void main(String[] args) {

    }

    /**
     * @return the pb207CorrectedCategory
     */
    @Override
    public ReportCategoryInterface getPb207CorrectedCategory() {
        return pb207CorrectedCategory;
    }

    /**
     * @param pb207CorrectedCategory the pb207CorrectedCategory to set
     */
    @Override
    public void setPb207CorrectedCategory(ReportCategoryInterface pb207CorrectedCategory) {
        this.pb207CorrectedCategory = pb207CorrectedCategory;
    }

    /**
     * @return the pb208CorrectedCategory
     */
    @Override
    public ReportCategoryInterface getPb208CorrectedCategory() {
        return pb208CorrectedCategory;
    }

    /**
     * @param pb208CorrectedCategory the pb208CorrectedCategory to set
     */
    @Override
    public void setPb208CorrectedCategory(ReportCategoryInterface pb208CorrectedCategory) {
        this.pb208CorrectedCategory = pb208CorrectedCategory;
    }

    /**
     * @return the spotFundamentalsCategory
     */
    public ReportCategoryInterface getSpotFundamentalsCategory() {
        return spotFundamentalsCategory;
    }

    /**
     * @param spotFundamentalsCategory the spotFundamentalsCategory to set
     */
    public void setSpotFundamentalsCategory(ReportCategoryInterface spotFundamentalsCategory) {
        this.spotFundamentalsCategory = spotFundamentalsCategory;
    }

    /**
     * @return the cpsCategory
     */
    public ReportCategoryInterface getCpsCategory() {
        return cpsCategory;
    }

    /**
     * @param cpsCategory the cpsCategory to set
     */
    @Override
    public void setCpsCategory(ReportCategoryInterface cpsCategory) {
        this.cpsCategory = cpsCategory;
    }

    /**
     * @return the rawRatiosCategory
     */
    @Override
    public ReportCategoryInterface getRawRatiosCategory() {
        return rawRatiosCategory;
    }

    /**
     * @param rawRatiosCategory the rawRatiosCategory to set
     */
    @Override
    public void setRawRatiosCategory(ReportCategoryInterface rawRatiosCategory) {
        this.rawRatiosCategory = rawRatiosCategory;
    }

    /**
     * @return the customExpressionsCategory
     */
    @Override
    public ReportCategoryInterface getCustomExpressionsCategory() {
        return customExpressionsCategory;
    }

    /**
     * @param customExpressionsCategory the customExpressionsCategory to set
     */
    @Override
    public void setCustomExpressionsCategory(ReportCategoryInterface customExpressionsCategory) {
        this.customExpressionsCategory = customExpressionsCategory;
    }

    /**
     * @return the correctionIndependentRMCategory
     */
    public ReportCategoryInterface getCorrectionIndependentRMCategory() {
        return correctionIndependentRMCategory;
    }

    /**
     * @param correctionIndependentRMCategory the correctionIndependentRMCategory to set
     */
    public void setCorrectionIndependentRMCategory(ReportCategoryInterface correctionIndependentRMCategory) {
        this.correctionIndependentRMCategory = correctionIndependentRMCategory;
    }
}
