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
import org.cirdles.squid.reports.reportSpecifications.ReportSpecificationsUPb;

/**
 *
 * @author James F. Bowring
 */
public class ReportSettings implements
        ReportSettingsInterface {

    private static final long serialVersionUID = 3742875572117123821L;
    private transient String reportSettingsXMLSchemaURL;
    /**
     * Each time the report specifications evolve in DataDictionary, this
     * version number is advanced so that any existing analysis will update its
     * report models upon opening in ET_Redux.
     */
    private static transient int CURRENT_VERSION_REPORT_SETTINGS_UPB = 367;

    // Fields
    private String name;
    private int version;
    private ReportCategoryInterface fractionCategory;
    private ReportCategoryInterface correctionIndependentCategory;
    private ReportCategoryInterface pb204CorrectedCategory;
    private ReportCategoryInterface isotopicRatiosPbcCorrCategory;
    private ReportCategoryInterface datesCategory;
    private ReportCategoryInterface datesPbcCorrCategory;
    private ReportCategoryInterface rhosCategory;

    private ReportCategoryInterface fractionCategory2;
    protected ArrayList<ReportCategoryInterface> reportCategories;
    private String reportSettingsComment;
    private boolean legacyData;

    /**
     * Creates a new instance of ReportSettings
     */
    public ReportSettings() {
        this("NONE");
    }

    /**
     * Creates a new instance of ReportSettings
     *
     * @param name
     * @param defaultReportSpecsType the value of defaultReportSpecsType
     */
    public ReportSettings(String name) {

        this.name = name;

        this.version = CURRENT_VERSION_REPORT_SETTINGS_UPB;

        this.reportSettingsComment = "";

        this.fractionCategory
                = new ReportCategory(//
                        "Fraction",
                        ReportSpecificationsAbstract.ReportCategory_Fraction, true);

        this.fractionCategory2
                = new ReportCategory(//
                        "Fraction",
                        ReportSpecificationsAbstract.ReportCategory_Fraction2, true);

//        this.datesCategory
//                = new ReportCategory(//
//                        "Dates",
//                        ReportSpecificationsUPb.ReportCategory_Dates, true);
//
//        this.datesPbcCorrCategory
//                = new ReportCategory(//
//                        "PbcCorr Dates",//
//                        ReportSpecificationsUPb.ReportCategory_PbcCorrDates, false);
        this.correctionIndependentCategory
                = new ReportCategory(//
                        "Correction-Independent Data",//
                        ReportSpecificationsUPb.ReportCategory_CorrectionIndependentData, true);

        this.pb204CorrectedCategory
                = new ReportCategory(//
                        "204Pb-Corrected",//
                        ReportSpecificationsUPb.ReportCategory_204PbCorrected, true);

//        this.isotopicRatiosPbcCorrCategory
//                = new ReportCategory(//
//                        "PbcCorr Isotopic Ratios",//
//                        ReportSpecificationsUPb.ReportCategory_PbcCorrIsotopicRatios, false);
//
//        this.rhosCategory
//                = new ReportCategory(//
//                        "Correlation Coefficients",//
//                        ReportSpecificationsUPb.ReportCategory_CorrelationCoefficients, true);
        legacyData = false;

        assembleReportCategories();
        normalizeReportCategories();

    }

    /**
     *
     * @return
     */
    public static ReportSettingsInterface EARTHTIMEReportSettingsUPb() {
        ReportSettingsInterface EARTHTIME
                = new ReportSettings("EARTHTIME UPb");

        return EARTHTIME;
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
     * @return
     */
    @Override
    public ReportCategoryInterface getDatesCategory() {
        return datesCategory;
    }

    /**
     *
     * @param datesCategory
     */
    @Override
    public void setDatesCategory(ReportCategoryInterface datesCategory) {
        this.datesCategory = datesCategory;
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
     * @return the rhosCategory
     */
    @Override
    public ReportCategoryInterface getRhosCategory() {
        return rhosCategory;
    }

    /**
     * @param rhosCategory the rhosCategory to set
     */
    @Override
    public void setRhosCategory(ReportCategoryInterface rhosCategory) {
        this.rhosCategory = rhosCategory;
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
     * @return the legacyData
     */
    @Override
    public boolean isLegacyData() {
        return legacyData;
    }

    /**
     * @param legacyData the legacyData to set
     */
    @Override
    public void setLegacyData(boolean legacyData) {
        this.legacyData = legacyData;

        getReportCategories().stream().filter((rc) -> (rc != null)).forEach((rc) -> {
            rc.setLegacyData(legacyData);
        });
    }

    /**
     *
     */
    @Override
    public void removeSelf() {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * @return the isotopicRatiosPbcCorrCategory
     */
    @Override
    public ReportCategoryInterface getIsotopicRatiosPbcCorrCategory() {
        return isotopicRatiosPbcCorrCategory;
    }

    /**
     * @param isotopicRatiosPbcCorrCategory the isotopicRatiosPbcCorrCategory to
     * set
     */
    @Override
    public void setIsotopicRatiosPbcCorrCategory(ReportCategoryInterface isotopicRatiosPbcCorrCategory) {
        this.isotopicRatiosPbcCorrCategory = isotopicRatiosPbcCorrCategory;
    }

    /**
     * @return the datesPbcCorrCategory
     */
    @Override
    public ReportCategoryInterface getDatesPbcCorrCategory() {
        return datesPbcCorrCategory;
    }

    /**
     * @param datesPbcCorrCategory the datesPbcCorrCategory to set
     */
    @Override
    public void setDatesPbcCorrCategory(ReportCategoryInterface datesPbcCorrCategory) {
        this.datesPbcCorrCategory = datesPbcCorrCategory;
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
}
