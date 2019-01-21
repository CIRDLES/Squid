/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.utilities.stateUtilities;

import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.parameters.ParametersModelComparator;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.CommonPbModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterialModels.ReferenceMaterialModel;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import static org.cirdles.squid.constants.Squid3Constants.SQUID_LAB_DATA_SERIALIZED_NAME;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_USERS_DATA_FOLDER_NAME;
import org.cirdles.squid.parameters.parameterModels.ParametersModel;

/**
 * @author ryanb
 */
public class SquidLabData implements Serializable {

    private static final long serialVersionUID = -6819591137651731346L;

    private List<ParametersModel> referenceMaterials;
    private List<ParametersModel> physicalConstantsModels;
    private List<ParametersModel> commonPbModels;

    private String laboratoryName;

    private ParametersModel commonPbDefault;
    private ParametersModel refMatDefault;
    private ParametersModel refMatConcDefault;
    private ParametersModel physConstDefault;

    public SquidLabData() {
        laboratoryName = "Your lab";
        referenceMaterials = ReferenceMaterialModel.getDefaultModels();
        referenceMaterials.sort(new ParametersModelComparator());
        physicalConstantsModels = PhysicalConstantsModel.getDefaultModels();
        physicalConstantsModels.sort(new ParametersModelComparator());
        commonPbModels = CommonPbModel.getDefaultModels();
        commonPbModels.sort(new ParametersModelComparator());

        physConstDefault = PhysicalConstantsModel.getDefaultModel("GA Physical Constants Model Squid 2", "1.0");
        refMatDefault = ReferenceMaterialModel.getDefaultModel("GA Accepted BR266", "1.0");
        refMatConcDefault = ReferenceMaterialModel.getDefaultModel("GA Accepted BR266", "1.0");
        commonPbDefault = CommonPbModel.getDefaultModel("GA Common Lead 2018", "1.0");

        storeState();
    }

    public static SquidLabData getExistingSquidLabData() {
        SquidLabData retVal;
        try {
            File file = new File(File.separator + System.getProperty("user.home")
                    + File.separator + SQUID_USERS_DATA_FOLDER_NAME + File.separator
                    + SQUID_LAB_DATA_SERIALIZED_NAME);
            if (file.exists() && !file.isDirectory()) {
                retVal = SquidLabData.deserialize(file);
                if (retVal == null){
                    // bad labdata
                    // save off old lab data
                    // announce to user
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

    public void storeState() {
        try {
            File file = new File(File.separator + System.getProperty("user.home")
                    + File.separator + SQUID_USERS_DATA_FOLDER_NAME + File.separator
                    + SQUID_LAB_DATA_SERIALIZED_NAME);
            serialize(file);
        } catch (IOException | SquidException e) {
            e.printStackTrace();
        }
    }

    public ParametersModel getCommonPbDefault() {
        ParametersModel retVal;
        if (commonPbDefault == null) {
            retVal = CommonPbModel.getDefaultModel("GA Common Lead 2018", "1.0");
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
            retVal = ReferenceMaterialModel.getDefaultModel("GA Accepted BR266", "1.0");
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
            retVal = ReferenceMaterialModel.getDefaultModel("GA Accepted BR266", "1.0");
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

    public static SquidLabData deserialize(File file) throws IOException, ClassNotFoundException {
        return (SquidLabData) SquidSerializer.getSerializedObjectFromFile(file.getAbsolutePath(), false);
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

    public boolean equals(Object o) {
        return o instanceof SquidLabData && this.compareTo(((SquidLabData) o)) == 0;
    }
}
