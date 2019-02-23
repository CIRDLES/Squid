/*
 * SquidUserPreferences.java
 *
 * Copyright 2017 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.utilities.stateUtilities;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.constants.Squid3Constants.IndexIsoptopesEnum;
import org.cirdles.squid.constants.Squid3Constants.TaskTypeEnum;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.PARENT_ELEMENT_CONC_CONST_DEFAULT_EXPRESSION;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_DEFAULT;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.TH_U_EXP_DEFAULT_EXPRESSION;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR206PB238U_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR206PB238U_CALIB_CONST_DEFAULT_EXPRESSION;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR208PB232TH_CALIB_CONST;
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.UNCOR208PB232TH_CALIB_CONST_DEFAULT_EXPRESSION;

/**
 *
 * @author James F. Bowring
 */
public class SquidUserPreferences implements Serializable {

    // NOTE: java.utils.Properties should be considered for use here JFB
    // Jan 2019 - considered and rejected as too simplistic
    // Class variables
    private static final long serialVersionUID = -936841271782482788L;

    // instance variables
    private String geochronUserName;
    private String geochronPassWord;

    private TaskTypeEnum taskType;
    private String authorName;
    private String labName;
    private boolean useSBM;
    private boolean userLinFits;
    private Squid3Constants.IndexIsoptopesEnum selectedIndexIsotope;
    
    private boolean squidAllowsAutoExclusionOfSpots;
    
    private double extPErr;
    
    private String delimiterForUnknownNames;
    private String parentNuclide;
    private boolean directAltPD;
    
    private Map<String, String> specialSquidFourExpressionsMap;

    /**
     * Creates a new instance of ReduxPreferences
     */
    public SquidUserPreferences() {

        this.geochronUserName = "username";
        this.geochronPassWord = "longpassword";

        this.taskType = TaskTypeEnum.GEOCHRON;
        this.authorName = "";
        this.labName = "";
        
        this.useSBM = true;
        this.userLinFits = false;
        
        this.selectedIndexIsotope = IndexIsoptopesEnum.PB_204;
        
        this.squidAllowsAutoExclusionOfSpots = true;
        
        this.extPErr = 0.75;
        
        this.delimiterForUnknownNames = Squid3Constants.SampleNameDelimetersEnum.HYPHEN.getName().trim();
        
        this.specialSquidFourExpressionsMap = new TreeMap<>();
        this.specialSquidFourExpressionsMap.put(UNCOR206PB238U_CALIB_CONST, UNCOR206PB238U_CALIB_CONST_DEFAULT_EXPRESSION);
        this.specialSquidFourExpressionsMap.put(UNCOR208PB232TH_CALIB_CONST, UNCOR208PB232TH_CALIB_CONST_DEFAULT_EXPRESSION);
        this.specialSquidFourExpressionsMap.put(TH_U_EXP_DEFAULT, TH_U_EXP_DEFAULT_EXPRESSION);
        this.specialSquidFourExpressionsMap.put(PARENT_ELEMENT_CONC_CONST, PARENT_ELEMENT_CONC_CONST_DEFAULT_EXPRESSION);
        
    }

    /**
     * @return the geochronUserName
     */
    public String getGeochronUserName() {
        if (geochronUserName == null) {
            geochronUserName = "username";
        }
        return geochronUserName;
    }

    /**
     * @param geochronUserName the geochronUserName to set
     */
    public void setGeochronUserName(String geochronUserName) {
        this.geochronUserName = geochronUserName;
    }

    /**
     * @return the geochronPassWord
     */
    public String getGeochronPassWord() {
        if (geochronPassWord == null) {
            geochronPassWord = "longpassword";
        }
        return geochronPassWord;
    }

    /**
     * @param geochronPassWord the geochronPassWord to set
     */
    public void setGeochronPassWord(String geochronPassWord) {
        this.geochronPassWord = geochronPassWord;
    }

    /**
     *
     * @return
     */
    public TaskTypeEnum getTaskType() {
        if (taskType == null) {
            taskType = TaskTypeEnum.GEOCHRON;
        }
        return taskType;
    }

    /**
     *
     * @param taskType
     */
    public void setTaskType(TaskTypeEnum taskType) {
        this.taskType = taskType;
    }

    /**
     * @return the authorName
     */
    public String getAuthorName() {
        if (authorName == null) {
            authorName = "";
        }
        return authorName;
    }

    /**
     * @param authorName the authorName to set
     */
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    /**
     * @return the labName
     */
    public String getLabName() {
        if (labName == null) {
            labName = "";
        }
        return labName;
    }

    /**
     * @param labName the labName to set
     */
    public void setLabName(String labName) {
        this.labName = labName;
    }

    /**
     * @return the useSBM
     */
    public boolean isUseSBM() {
        return useSBM;
    }

    /**
     * @param useSBM the useSBM to set
     */
    public void setUseSBM(boolean useSBM) {
        this.useSBM = useSBM;
    }

    /**
     * @return the userLinFits
     */
    public boolean isUserLinFits() {
        return userLinFits;
    }

    /**
     * @param userLinFits the userLinFits to set
     */
    public void setUserLinFits(boolean userLinFits) {
        this.userLinFits = userLinFits;
    }

    /**
     * @return the selectedIndexIsotope
     */
    public Squid3Constants.IndexIsoptopesEnum getSelectedIndexIsotope() {
        if (selectedIndexIsotope == null){
            selectedIndexIsotope = IndexIsoptopesEnum.PB_204;
        }
        return selectedIndexIsotope;
    }

    /**
     * @param selectedIndexIsotope the selectedIndexIsotope to set
     */
    public void setSelectedIndexIsotope(Squid3Constants.IndexIsoptopesEnum selectedIndexIsotope) {
        this.selectedIndexIsotope = selectedIndexIsotope;
    }

    /**
     * @return the squidAllowsAutoExclusionOfSpots
     */
    public boolean isSquidAllowsAutoExclusionOfSpots() {
        return squidAllowsAutoExclusionOfSpots;
    }

    /**
     * @param squidAllowsAutoExclusionOfSpots the squidAllowsAutoExclusionOfSpots to set
     */
    public void setSquidAllowsAutoExclusionOfSpots(boolean squidAllowsAutoExclusionOfSpots) {
        this.squidAllowsAutoExclusionOfSpots = squidAllowsAutoExclusionOfSpots;
    }

    /**
     * @return the extPErr
     */
    public double getExtPErr() {
        return extPErr;
    }

    /**
     * @param extPErr the extPErr to set
     */
    public void setExtPErr(double extPErr) {
        this.extPErr = extPErr;
    }

    /**
     * @return the delimiterForUnknownNames
     */
    public String getDelimiterForUnknownNames() {
        return delimiterForUnknownNames;
    }

    /**
     * @param delimiterForUnknownNames the delimiterForUnknownNames to set
     */
    public void setDelimiterForUnknownNames(String delimiterForUnknownNames) {
        this.delimiterForUnknownNames = delimiterForUnknownNames;
    }

    /**
     * @return the parentNuclide
     */
    public String getParentNuclide() {
        return parentNuclide;
    }

    /**
     * @param parentNuclide the parentNuclide to set
     */
    public void setParentNuclide(String parentNuclide) {
        this.parentNuclide = parentNuclide;
    }

    /**
     * @return the directAltPD
     */
    public boolean isDirectAltPD() {
        return directAltPD;
    }

    /**
     * @param directAltPD the directAltPD to set
     */
    public void setDirectAltPD(boolean directAltPD) {
        this.directAltPD = directAltPD;
    }

    /**
     * @return the specialSquidFourExpressionsMap
     */
    public Map<String, String> getSpecialSquidFourExpressionsMap() {
        return specialSquidFourExpressionsMap;
    }

    /**
     * @param specialSquidFourExpressionsMap the specialSquidFourExpressionsMap to set
     */
    public void setSpecialSquidFourExpressionsMap(Map<String, String> specialSquidFourExpressionsMap) {
        this.specialSquidFourExpressionsMap = specialSquidFourExpressionsMap;
    }

}
