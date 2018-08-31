/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.utilities.stateUtilities;

import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.parameters.parameterModels.commonPbModels.CommonPbModel;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterials.ReferenceMaterial;

import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.List;

import static org.cirdles.squid.constants.Squid3Constants.SQUID_LAB_DATA_SERIALIZED_NAME;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_USERS_DATA_FOLDER_NAME;

/**
 * @author ryanb
 */
public class SquidLabData implements Serializable {

    private static long serialVersionUID = 2042394065348785942L;

    private List<ReferenceMaterial> referenceMaterials;
    private List<PhysicalConstantsModel> physicalConstantsModels;
    private List<CommonPbModel> commonPbModels;

    private String laboratoryName;

    private CommonPbModel commonPbDefault;
    private ReferenceMaterial refMatDefault;
    private ReferenceMaterial refMatConcDefault;
    private PhysicalConstantsModel physConstDefault;

    public SquidLabData() {
        laboratoryName = "Your lab";
        referenceMaterials = ReferenceMaterial.getDefaultModels();
        physicalConstantsModels = PhysicalConstantsModel.getDefaultModels();
        commonPbModels = CommonPbModel.getDefaultModels();

        physConstDefault = PhysicalConstantsModel.getDefaultModel("EARTHTIME Physical Constants Model", "1.1");
        refMatDefault = ReferenceMaterial.getDefaultModel("Zircon-91500", "1.0");
        refMatConcDefault = ReferenceMaterial.getDefaultModel("Zircon-91500", "1.0");
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
            } else {
                retVal = new SquidLabData();
            }
        } catch (Exception e) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CommonPbModel getCommonPbDefault() {
        CommonPbModel retVal;
        if (commonPbDefault == null)
            retVal = CommonPbModel.getDefaultModel("GA Common Lead 2018", "1.0");
        else
            retVal = commonPbDefault;
        return retVal;
    }

    public void setCommonPbDefault(CommonPbModel commonPbDefault) {
        this.commonPbDefault = commonPbDefault;
    }

    public ReferenceMaterial getRefMatDefault() {
        ReferenceMaterial retVal;
        if (refMatDefault == null)
            retVal = ReferenceMaterial.getDefaultModel("Zircon-91500", "1.0");
        else
            retVal = refMatDefault;
        return retVal;
    }

    public void setRefMatDefault(ReferenceMaterial refMatDefault) {
        this.refMatDefault = refMatDefault;
    }

    public ReferenceMaterial getRefMatConcDefault() {
        ReferenceMaterial retVal;
        if (refMatConcDefault == null)
            retVal = ReferenceMaterial.getDefaultModel("Zircon-91500", "1.0");
        else
            retVal = refMatConcDefault;
        return retVal;
    }

    public void setRefMatConcDefault(ReferenceMaterial refMat) {
        refMatConcDefault = refMat;
    }

    public PhysicalConstantsModel getPhysConstDefault() {
        PhysicalConstantsModel retVal;
        if (physConstDefault == null)
            retVal = PhysicalConstantsModel.getDefaultModel("EARTHTIME Physical Constants Model", "1.1");
        else
            retVal = physConstDefault;
        return retVal;
    }

    public void setPhysConstDefault(PhysicalConstantsModel physConstDefault) {
        this.physConstDefault = physConstDefault;
    }

    public List<ReferenceMaterial> getReferenceMaterials() {
        return referenceMaterials;
    }

    public void setReferenceMaterials(List<ReferenceMaterial> referenceMaterials) {
        this.referenceMaterials = referenceMaterials;
    }

    public List<PhysicalConstantsModel> getPhysicalConstantsModels() {
        return physicalConstantsModels;
    }

    public void setPhysicalConstantsModels(List<PhysicalConstantsModel> physicalConstantsModels) {
        this.physicalConstantsModels = physicalConstantsModels;
    }

    public List<CommonPbModel> getcommonPbModels() {
        return commonPbModels;
    }

    public void setcommonPbModels(List<CommonPbModel> commonPbModels) {
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

    public void removeReferenceMaterial(ReferenceMaterial refMat) {
        referenceMaterials.remove(refMat);
    }

    public void remvovePhysicalConstantsModel(PhysicalConstantsModel model) {
        physicalConstantsModels.remove(model);
    }

    public void removecommonPbModel(CommonPbModel model) {
        commonPbModels.remove(model);
    }

    public void addReferenceMaterial(ReferenceMaterial refMat) {
        referenceMaterials.add(refMat);
    }

    public void addPhysicalConstantsModel(PhysicalConstantsModel model) {
        physicalConstantsModels.add(model);
    }

    public void addcommonPbModel(CommonPbModel model) {
        commonPbModels.add(model);
    }

    public ReferenceMaterial getReferenceMaterial(int i) {
        return referenceMaterials.get(i);
    }

    public PhysicalConstantsModel getPhysicalConstantsModel(int i) {
        return physicalConstantsModels.get(i);
    }

    public CommonPbModel getcommonPbModel(int i) {
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

    public static void main(String[] args) {
        ObjectStreamClass stream = ObjectStreamClass.lookup(SquidLabData.class);
        System.out.println("serialVersionUID: " + stream.getSerialVersionUID());
    }

}
