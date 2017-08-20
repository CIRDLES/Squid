/*
 * SquidPersistentState.java
 *
 * Copyright 2017 CIRDLES.org
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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import static org.cirdles.squid.constants.Squid3Constants.SQUID_USERS_DATA_FOLDER_NAME;
import org.cirdles.squid.exceptions.SquidException;

/**
 *
 * @author James F. Bowring
 */
public class SquidPersistentState implements Serializable {

    // class variables
    private static final long serialVersionUID = 9131785805774520290L;
    private static final String SQUID_PERSISTENT_STATE_FILE_NAME = "SquidPersistentState.ser";
    private static SquidPersistentState instance = (SquidPersistentState) SquidSerializer.getSerializedObjectFromFile(SquidPersistentState.getMySerializedName());
    private static final int MRU_COUNT = 10;

    // instance variables
    private SquidUserPreferences squidUserPreferences;
    private File MRUProjectFile;
    private ArrayList<String> MRUProjectList;
    private String MRUProjectFolderPath;

    /**
     *
     */
    private SquidPersistentState() {

        initMRUProjectList();

        squidUserPreferences = new SquidUserPreferences();

        // check if user data folder exists and create if it does not
        File dataFolder = new File(
                File.separator + System.getProperty("user.home") + File.separator + SQUID_USERS_DATA_FOLDER_NAME);
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        MRUProjectFile = null;

        serializeSelf();
    }

    public static SquidPersistentState getInstance() {
        if (instance == null) {
            instance = new SquidPersistentState();
        }
        return instance;
    }

    private void serializeSelf() {
        // save initial persistent state serialized file
        try {
            SquidSerializer.serializeObjectToFile(this, getMySerializedName());
        } catch (SquidException squidException) {
        }
    }

    private void initMRUProjectList() {
        MRUProjectList = new ArrayList<>(MRU_COUNT);
    }

    /**
     *
     * @param projectFileMRU
     */
    public void updateProjectListMRU(File projectFileMRU) {

        try {
            // remove if exists in MRU list
            String MRUProjectFileName = projectFileMRU.getCanonicalPath();
            MRUProjectList.remove(MRUProjectFileName);
            MRUProjectList.add(0, MRUProjectFileName);

            // trim list
            if (MRUProjectList.size() > MRU_COUNT) {
                MRUProjectList.remove(MRU_COUNT);
            }

            // update MRU folder
            MRUProjectFolderPath = projectFileMRU.getParent();
            
            // update current file
            MRUProjectFile = projectFileMRU;
            
        } catch (IOException iOException) {
        }

        // save
        try {
            SquidSerializer.serializeObjectToFile(this, getMySerializedName());
        } catch (SquidException squidException) {
        }
    }

    public void removeFileNameFromProjectListMRU(String mruProjectFileName) {
        MRUProjectList.remove(mruProjectFileName);
    }

    /**
     *
     * @return
     */
    public static SquidPersistentState getExistingPersistentState() {
        // check if user data folder exists and create if it does not
        File dataFolder = new File(
                File.separator + System.getProperty("user.home") + File.separator + SQUID_USERS_DATA_FOLDER_NAME);
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        if (instance == null) {
            instance = new SquidPersistentState();
        }

        return instance;
    }

    //properties
    /**
     *
     * @return
     */
    public static String getMySerializedName() {
        String mySerializedName
                = File.separator//
                + System.getProperty("user.home")//
                + File.separator//
                + SQUID_USERS_DATA_FOLDER_NAME //
                + File.separator + SQUID_PERSISTENT_STATE_FILE_NAME;
        return mySerializedName;
    }

    /**
     * @return the MRUProjectFile
     */
    public File getMRUProjectFile() {
        return MRUProjectFile;
    }

    /**
     * @param MRUProjectFile the MRUProjectFile to set
     */
    public void setMRUProjectFile(File MRUProjectFile) {
        this.MRUProjectFile = MRUProjectFile;
    }

    /**
     *
     * @return
     */
    public ArrayList<String> getMRUProjectList() {
        cleanProjectListMRU();
        return MRUProjectList;
    }

    public void cleanProjectListMRU() {
        ArrayList<String> missingFileNames = new ArrayList<>();
        // test for missing files
        for (String projectFileName : MRUProjectList) {
            File projectFile = new File(projectFileName);
            if (!projectFile.exists()) {
                missingFileNames.add(projectFileName);
            }
        }

        // remove missing fileNames
        for (String projectFileName : missingFileNames) {
            MRUProjectList.remove(projectFileName);
        }
    }

    /**
     * @param MRUProjectList
     * @param MRUsampleList
     */
    public void setMRUProjectList(ArrayList<String> MRUProjectList) {
        this.MRUProjectList = MRUProjectList;
    }

    /**
     * @return the MRUProjectFolderPath
     */
    public String getMRUProjectFolderPath() {
        return MRUProjectFolderPath;
    }

    /**
     * @param MRUProjectFolderPath the MRUProjectFolderPath to set
     */
    public void setMRUProjectFolderPath(String MRUProjectFolderPath) {
        this.MRUProjectFolderPath = MRUProjectFolderPath;
    }

    /**
     * @return the squidUserPreferences
     */
    public SquidUserPreferences getSquidUserPreferences() {
        return squidUserPreferences;
    }

    /**
     * @param squidUserPreferences the squidUserPreferences to set
     */
    public void setSquidUserPreferences(SquidUserPreferences squidUserPreferences) {
        this.squidUserPreferences = squidUserPreferences;
    }

//    private void readObject(
//            ObjectInputStream stream)
//            throws IOException, ClassNotFoundException {
//        stream.defaultReadObject();
//        ObjectStreamClass myObject = ObjectStreamClass.lookup(Class.forName(SquidPersistentState.class.getCanonicalName()));
//        long theSUID = myObject.getSerialVersionUID();
//        System.out.println("Customized De-serialization of SquidPersistentState " + theSUID);
//    }
}
