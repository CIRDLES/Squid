/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.utilities.stateUtilities;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_LAB_DATA_SERIALIZED_NAME;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_USERS_DATA_FOLDER_NAME;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.parameters.parameterModels.physicalConstantsModels.PhysicalConstantsModel;
import org.cirdles.squid.parameters.parameterModels.referenceMaterials.ReferenceMaterial;

/**
 *
 * @author ryanb
 */
public class SquidLabData implements Serializable {

    List<ReferenceMaterial> referenceMaterials;
    List<PhysicalConstantsModel> physicalConstantsModels;
    String laboratoryName;

    public SquidLabData() {
        laboratoryName = "mystery lab";
        referenceMaterials = ReferenceMaterial.getDefaultModels();
        physicalConstantsModels = PhysicalConstantsModel.getDefaultModels();
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

    public void addReferenceMaterial(ReferenceMaterial refMat) {
        referenceMaterials.add(refMat);
    }

    public void addPhysicalConstantsModel(PhysicalConstantsModel model) {
        physicalConstantsModels.add(model);
    }

    public ReferenceMaterial getReferenceMaterial(int i) {
        return referenceMaterials.get(i);
    }

    public PhysicalConstantsModel getPhysicalConstantsModel(int i) {
        return physicalConstantsModels.get(i);
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

        return difference;
    }

    public boolean equals(Object o) {
        return o instanceof SquidLabData && this.compareTo(((SquidLabData) o)) == 0;
    }

    public static void main(String[] args) {
        try {
                SquidLabData data = getExistingSquidLabData();
                data.storeState();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
