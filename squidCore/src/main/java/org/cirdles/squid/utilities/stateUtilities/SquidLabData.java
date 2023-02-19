/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.utilities.stateUtilities;

import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.parameters.ParametersModelComparator;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.CommonPbModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;
import org.cirdles.squid.squidReports.squidReportTables.SquidReportTableInterface;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.cirdles.squid.constants.Squid3Constants.SQUID_LAB_DATA_SERIALIZED_NAME;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_USERS_DATA_FOLDER_NAME;
import static org.cirdles.squid.utilities.stateUtilities.SquidPersistentState.getExistingPersistentState;

/**
 * @author ryanb
 */
public class SquidLabData implements Serializable {

    public static final String SQUID2_DEFAULT_PHYSICAL_CONSTANTS_MODEL_V1 = "Squid 2.5 Physical Constants Model";
    private static final long serialVersionUID = -6819591137651731346L;
    private static final int CURRENT_VERSION = 18;
    private List<ParametersModel> referenceMaterials;
    private List<ParametersModel> physicalConstantsModels;
    private List<ParametersModel> commonPbModels;
    private String laboratoryName;
    private ParametersModel commonPbDefault;
    private ParametersModel refMatDefault;
    private ParametersModel refMatConcDefault;
    private ParametersModel physConstDefault;
    private int version;

    private SquidReportTableInterface specialWMSortingReportTable;
    private SquidReportTableInterface specialRMWMSortingReportTable;
    private SquidReportTableInterface defaultReportTable;
    private SquidReportTableInterface defaultReportTableRM;

    public SquidLabData() throws SquidException {
        laboratoryName = "Your lab";

        referenceMaterials = new ArrayList<>();
        physicalConstantsModels = new ArrayList<>();
        commonPbModels = new ArrayList<>();

        updateSquidLabData();
    }

    public static SquidLabData getExistingSquidLabData() throws SquidException {
        SquidLabData retVal;
        try {
            File file = new File(File.separator + getExistingPersistentState().getSquidUserHomeDirectoryLocal()
                    + File.separator + SQUID_USERS_DATA_FOLDER_NAME + File.separator
                    + SQUID_LAB_DATA_SERIALIZED_NAME);
            if (file.exists() && !file.isDirectory()) {
                retVal = SquidLabData.deserialize(file);
                if (retVal == null) {
                    // bad labdata
                    retVal = new SquidLabData();
                }
            } else {
                retVal = new SquidLabData();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            retVal = new SquidLabData();
        }
        return retVal;
    }

    public static SquidLabData deserialize(File file) throws IOException, ClassNotFoundException, SquidException {
        return (SquidLabData) SquidSerializer.getSerializedObjectFromFile(file.getAbsolutePath(), false);
    }

    private void updateSquidLabData() throws SquidException {
        referenceMaterials.removeAll(ReferenceMaterialModel.getDefaultModels());
        referenceMaterials.addAll(ReferenceMaterialModel.getDefaultModels());
        referenceMaterials.sort(new ParametersModelComparator());

        physicalConstantsModels.removeAll(PhysicalConstantsModel.getDefaultModels());
        physicalConstantsModels.addAll(PhysicalConstantsModel.getDefaultModels());
        physicalConstantsModels.sort(new ParametersModelComparator());

        commonPbModels.removeAll(CommonPbModel.getDefaultModels());
        commonPbModels.addAll(CommonPbModel.getDefaultModels());
        commonPbModels.sort(new ParametersModelComparator());

        physConstDefault = PhysicalConstantsModel.getDefaultModel(SQUID2_DEFAULT_PHYSICAL_CONSTANTS_MODEL_V1, "1.0");
        refMatDefault = ReferenceMaterialModel.getDefaultModel("z6266 ID-TIMS (559.0 Ma)", "1.0");
        refMatConcDefault = ReferenceMaterialModel.getDefaultModel("z6266 ID-TIMS (559.0 Ma)", "1.0");
        commonPbDefault = CommonPbModel.getDefaultModel("Stacey-Kramers@559.0Ma (z6266)", "1.0");

        version = CURRENT_VERSION;

        specialWMSortingReportTable = null;
        specialRMWMSortingReportTable = null;
        defaultReportTable = null;
        defaultReportTableRM = null;

        storeState();
    }

    public void testVersionAndUpdate() throws SquidException {
        if (version < CURRENT_VERSION) {
            updateSquidLabData();
        }
    }

    public void storeState() throws SquidException {
        String mySerializedName
                = File.separator//
                + SquidPersistentState.getExistingPersistentState().getSquidUserHomeDirectoryLocal()//
                + File.separator//
                + SQUID_USERS_DATA_FOLDER_NAME //
                + File.separator + SQUID_LAB_DATA_SERIALIZED_NAME;

        // check if user data folder exists and create if it does not
        File dataFolder = new File(
                File.separator + SquidPersistentState.getExistingPersistentState().getSquidUserHomeDirectoryLocal() + File.separator + SQUID_USERS_DATA_FOLDER_NAME);
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        try {
            SquidSerializer.serializeObjectToFile(this, mySerializedName);
        } catch (SquidException squidException) {
            squidException.printStackTrace();
        }

    }

    public ParametersModel getCommonPbDefault() {
        ParametersModel retVal;
        if (commonPbDefault == null) {
            retVal = CommonPbModel.getDefaultModel("Stacey-Kramers@559.0Ma (z6266)", "1.0");
        } else {
            retVal = commonPbDefault;
        }
        return retVal;
    }

    public void setCommonPbDefault(ParametersModel commonPbDefault) {
        this.commonPbDefault = commonPbDefault;
    }

    public ParametersModel getRefMatDefault() {
        ParametersModel retVal;
        if (refMatDefault == null) {
            retVal = ReferenceMaterialModel.getDefaultModel("z6266 ID-TIMS (559.0 Ma)", "1.0");
        } else {
            retVal = refMatDefault;
        }
        return retVal;
    }

    public void setRefMatDefault(ParametersModel refMatDefault) {
        this.refMatDefault = refMatDefault;
    }

    public ParametersModel getRefMatConcDefault() {
        ParametersModel retVal;
        if (refMatConcDefault == null) {
            retVal = ReferenceMaterialModel.getDefaultModel("z6266 ID-TIMS (559.0 Ma)", "1.0");
        } else {
            retVal = refMatConcDefault;
        }
        return retVal;
    }

    public void setRefMatConcDefault(ParametersModel refMat) {
        refMatConcDefault = refMat;
    }

    public ParametersModel getPhysConstDefault() {
        ParametersModel retVal;
        if (physConstDefault == null) {
            retVal = PhysicalConstantsModel.getDefaultModel("EARTHTIME Physical Constants Model", "1.1");
        } else {
            retVal = physConstDefault;
        }
        return retVal;
    }

    public void setPhysConstDefault(ParametersModel physConstDefault) {
        this.physConstDefault = physConstDefault;
    }

    public List<ParametersModel> getReferenceMaterials() {
        return referenceMaterials;
    }

    public void setReferenceMaterials(List<ParametersModel> referenceMaterials) {
        this.referenceMaterials = referenceMaterials;
    }

    public List<ParametersModel> getReferenceMaterialsWithNonZeroDate() {
        List<ParametersModel> filteredRM = new ArrayList<>();
        for (ParametersModel pm : referenceMaterials) {
            if (((ReferenceMaterialModel) pm).hasAtLeastOneNonZeroApparentDate()) {
                filteredRM.add(pm);
            }
        }

        return filteredRM;
    }

    public List<ParametersModel> getReferenceMaterialsWithNonZeroConcentrations() {
        List<ParametersModel> filteredRM = new ArrayList<>();
        for (ParametersModel pm : referenceMaterials) {
            if (((ReferenceMaterialModel) pm).hasAtLeastOneNonZeroConcentration()) {
                filteredRM.add(pm);
            }
        }

        return filteredRM;
    }

    public List<ParametersModel> getPhysicalConstantsModels() {
        return physicalConstantsModels;
    }

    public void setPhysicalConstantsModels(List<ParametersModel> physicalConstantsModels) {
        this.physicalConstantsModels = physicalConstantsModels;
    }

    public List<ParametersModel> getCommonPbModels() {
        return commonPbModels;
    }

    public void setcommonPbModels(List<ParametersModel> commonPbModels) {
        this.commonPbModels = commonPbModels;
    }

    public String getLaboratoryName() {
        return laboratoryName;
    }

    public void setLaboratoryName(String laboratoryName) {
        this.laboratoryName = laboratoryName;
    }

    public void serialize(File file) throws IOException, SquidException {
        SquidSerializer.serializeObjectToFile(this, file.getAbsolutePath());
    }

    public void removeReferenceMaterial(ParametersModel refMat) {
        referenceMaterials.remove(refMat);
    }

    public void remvovePhysicalConstantsModel(ParametersModel model) {
        physicalConstantsModels.remove(model);
    }

    public void removecommonPbModel(ParametersModel model) {
        commonPbModels.remove(model);
    }

    public void addReferenceMaterial(ParametersModel refMat) {
        referenceMaterials.add(refMat);
    }

    public void addPhysicalConstantsModel(ParametersModel model) {
        physicalConstantsModels.add(model);
    }

    public void addcommonPbModel(ParametersModel model) {
        commonPbModels.add(model);
    }

    public ParametersModel getReferenceMaterial(int i) {
        return referenceMaterials.get(i);
    }

    public ParametersModel getPhysicalConstantsModel(int i) {
        return physicalConstantsModels.get(i);
    }

    public ParametersModel getcommonPbModel(int i) {
        return commonPbModels.get(i);
    }

    public int compareTo(SquidLabData data) {
        int difference = 0;
        difference = laboratoryName.compareTo(data.getLaboratoryName());

        for (int i = 0; difference == 0 && i < referenceMaterials.size(); i++) {
            difference = referenceMaterials.get(i).compareTo(data.getReferenceMaterial(i));
        }
        for (int i = 0; difference == 0 && i < physicalConstantsModels.size(); i++) {
            difference = physicalConstantsModels.get(i).compareTo(data.getPhysicalConstantsModel(i));
        }
        for (int i = 0; difference == 0 && i < commonPbModels.size(); i++) {
            difference = commonPbModels.get(i).compareTo(data.getcommonPbModel(i));
        }

        return difference;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SquidLabData && this.compareTo(((SquidLabData) o)) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceMaterials, physicalConstantsModels, commonPbModels, laboratoryName,
                commonPbDefault, refMatDefault, refMatConcDefault, physConstDefault, version,
                specialWMSortingReportTable, specialRMWMSortingReportTable, defaultReportTable, defaultReportTableRM);
    }

    /**
     * @return the version
     */
    public int getVersion() {
        if (version == 0) {
            version = 1;
        }
        return version;
    }

    /**
     * @return the specialWMSortingReportTable
     */
    public SquidReportTableInterface getSpecialWMSortingReportTable() {
        return specialWMSortingReportTable;
    }

    /**
     * @param specialWMSortingReportTable the specialWMSortingReportTable to set
     */
    public void setSpecialWMSortingReportTable(SquidReportTableInterface specialWMSortingReportTable) {
        this.specialWMSortingReportTable = specialWMSortingReportTable;
    }

    public SquidReportTableInterface getSpecialRMWMSortingReportTable() {
        return specialRMWMSortingReportTable;
    }

    public void setSpecialRMWMSortingReportTable(SquidReportTableInterface specialRMWMSortingReportTable) {
        this.specialRMWMSortingReportTable = specialRMWMSortingReportTable;
    }

    /**
     * @return the defaultReportTable
     */
    public SquidReportTableInterface getDefaultReportTable() {
        return defaultReportTable;
    }

    /**
     * @param defaultReportTable the defaultReportTable to set
     */
    public void setDefaultReportTable(SquidReportTableInterface defaultReportTable) {
        this.defaultReportTable = defaultReportTable;
    }

    /**
     * @return the defaultReportTableRM
     */
    public SquidReportTableInterface getDefaultReportTableRM() {
        return defaultReportTableRM;
    }

    /**
     * @param defaultReportTableRM the defaultReportTableRM to set
     */
    public void setDefaultReportTableRM(SquidReportTableInterface defaultReportTableRM) {
        this.defaultReportTableRM = defaultReportTableRM;
    }


}