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
    private static volatile SquidPersistentState instance = (SquidPersistentState) SquidSerializer.getSerializedObjectFromFile(SquidPersistentState.getMySerializedName(), false);
    private static final int MRU_COUNT = 10;

    // instance variables
    private SquidUserPreferences squidUserPreferences;
    private String MRUPrawnFileFolderPath;

    private File MRUProjectFile;
    private ArrayList<String> MRUProjectList;
    private String MRUProjectFolderPath;

    private File MRUTaskFile;
    private ArrayList<String> MRUTaskList;
    private String MRUTaskFolderPath;

    private File MRUExpressionFile;
    private ArrayList<String> MRUExpressionList;
    private String MRUExpressionFolderPath;

    /**
     *
     */
    private SquidPersistentState() {

        initMRULists();

        squidUserPreferences = new SquidUserPreferences();

        // check if user data folder exists and create if it does not
        File dataFolder = new File(
                File.separator + System.getProperty("user.home") + File.separator + SQUID_USERS_DATA_FOLDER_NAME);
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        MRUProjectFile = null;
        MRUProjectFolderPath = "";
        MRUPrawnFileFolderPath = "";

        MRUTaskFile = null;
        MRUTaskFolderPath = "";

        MRUExpressionFile = null;
        MRUExpressionFolderPath = "";

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
     * @return the MRUPrawnFileFolderPath
     */
    public String getMRUPrawnFileFolderPath() {
        return MRUPrawnFileFolderPath;
    }

    /**
     * @param MRUPrawnFileFolderPath the MRUPrawnFileFolderPath to set
     */
    public void setMRUPrawnFileFolderPath(String MRUPrawnFileFolderPath) {
        this.MRUPrawnFileFolderPath = MRUPrawnFileFolderPath;
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

    private void initMRULists() {
        MRUProjectList = new ArrayList<>(MRU_COUNT);
        MRUTaskList = new ArrayList<>(MRU_COUNT);
        MRUExpressionList = new ArrayList<>(MRU_COUNT);
    }

    // MRU Project Data *********************************************************
    /**
     *
     * @param projectFileMRU
     */
    public void updateProjectListMRU(File projectFileMRU) {

        if (projectFileMRU != null) {
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
            removeProjectFileNameFromMRU(projectFileName);
        }

        serializeSelf();
    }

    public void removeProjectFileNameFromMRU(String projectFileName) {
        MRUProjectList.remove(projectFileName);
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

    // MRU Task Data ********************************************************
    /**
     *
     * @param taskFileMRU
     */
    public void updateTaskListMRU(File taskFileMRU) {

        if (MRUTaskList == null) {
            MRUTaskList = new ArrayList<>(MRU_COUNT);
        }

        if (taskFileMRU != null) {
            try {
                // remove if exists in MRU list
                String MRUTaskFileName = taskFileMRU.getCanonicalPath();
                MRUTaskList.remove(MRUTaskFileName);
                MRUTaskList.add(0, MRUTaskFileName);

                // trim list
                if (MRUTaskList.size() > MRU_COUNT) {
                    MRUTaskList.remove(MRU_COUNT);
                }

                // update MRU folder
                MRUTaskFolderPath = taskFileMRU.getParent();

                // update current file
                MRUTaskFile = taskFileMRU;

            } catch (IOException iOException) {
            }
        }

        // save
        try {
            SquidSerializer.serializeObjectToFile(this, getMySerializedName());
        } catch (SquidException squidException) {
        }
    }

    public void removeFileNameFromTaskListMRU(String mruTaskFileName) {
        MRUTaskList.remove(mruTaskFileName);
    }

    public void cleanTaskListMRU() {
        ArrayList<String> missingFileNames = new ArrayList<>();
        // test for missing files
        for (String taskFileName : MRUTaskList) {
            File taskFile = new File(taskFileName);
            if (!taskFile.exists()) {
                missingFileNames.add(taskFileName);
            }
        }

        // remove missing fileNames
        for (String taskFileName : missingFileNames) {
            removeTaskFileNameFromMRU(taskFileName);
        }

        serializeSelf();
    }

    public void removeTaskFileNameFromMRU(String taskFileName) {
        MRUTaskList.remove(taskFileName);
    }

    /**
     * @return the MRUTaskFile
     */
    public File getMRUTaskFile() {
        return MRUTaskFile;
    }

    /**
     * @param MRUTaskFile the MRUTaskFile to set
     */
    public void setMRUTaskFile(File MRUTaskFile) {
        this.MRUTaskFile = MRUTaskFile;
    }

    /**
     * @return the MRUTaskList
     */
    public ArrayList<String> getMRUTaskList() {
        if (MRUTaskList == null) {
            MRUTaskList = null;
        }
        return MRUTaskList;
    }

    /**
     * @param MRUTaskList the MRUTaskList to set
     */
    public void setMRUTaskList(ArrayList<String> MRUTaskList) {
        this.MRUTaskList = MRUTaskList;
    }

    /**
     * @return the MRUTaskFolderPath
     */
    public String getMRUTaskFolderPath() {
        if (MRUTaskFolderPath == null) {
            MRUTaskFolderPath = "";
        }
        return MRUTaskFolderPath;
    }

    /**
     * @param MRUTaskFolderPath the MRUTaskFolderPath to set
     */
    public void setMRUTaskFolderPath(String MRUTaskFolderPath) {
        this.MRUTaskFolderPath = MRUTaskFolderPath;
    }

    // MRU Expression Data ********************************************************
    /**
     *
     * @param expressionFileMRU
     */
    public void updateExpressionListMRU(File expressionFileMRU) {

        if (MRUExpressionList == null) {
            MRUExpressionList = new ArrayList<>(MRU_COUNT);
        }

        if (expressionFileMRU != null) {
            try {
                // remove if exists in MRU list
                String MRUExpressionFileName = expressionFileMRU.getCanonicalPath();
                MRUExpressionList.remove(MRUExpressionFileName);
                MRUExpressionList.add(0, MRUExpressionFileName);

                // trim list
                if (MRUExpressionList.size() > MRU_COUNT) {
                    MRUExpressionList.remove(MRU_COUNT);
                }

                // update MRU folder
                MRUExpressionFolderPath = expressionFileMRU.getParent();

                // update current file
                MRUExpressionFile = expressionFileMRU;

            } catch (IOException iOException) {
            }
        }

        // save
        try {
            SquidSerializer.serializeObjectToFile(this, getMySerializedName());
        } catch (SquidException squidException) {
            System.out.println(squidException.getMessage());
        }
    }

    public void removeFileNameFromExpressionListMRU(String mruExpressionFileName) {
        MRUExpressionList.remove(mruExpressionFileName);
    }

    public void cleanExpressionListMRU() {
        ArrayList<String> missingFileNames = new ArrayList<>();
        // test for missing files
        for (String expressionFileName : MRUExpressionList) {
            File expressionFile = new File(expressionFileName);
            if (!expressionFile.exists()) {
                missingFileNames.add(expressionFileName);
            }
        }

        // remove missing fileNames
        for (String expressionFileName : missingFileNames) {
            removeExpressionFileNameFromMRU(expressionFileName);
        }

        serializeSelf();
    }

    public void removeExpressionFileNameFromMRU(String expressionFileName) {
        MRUExpressionList.remove(expressionFileName);
    }

    /**
     * @return the MRUExpressionFile
     */
    public File getMRUExpressionFile() {
        return MRUExpressionFile;
    }

    /**
     * @param MRUExpressionFile the MRUExpressionFile to set
     */
    public void setMRUExpressionFile(File MRUExpressionFile) {
        this.MRUExpressionFile = MRUExpressionFile;
    }

    /**
     * @return the MRUExpressionList
     */
    public ArrayList<String> getMRUExpressionList() {
        if (MRUExpressionList == null) {
            MRUExpressionList = null;
        }
        return MRUExpressionList;
    }

    /**
     * @param MRUExpressionList the MRUExpressionList to set
     */
    public void setMRUExpressionList(ArrayList<String> MRUExpressionList) {
        this.MRUExpressionList = MRUExpressionList;
    }

    /**
     * @return the MRUExpressionFolderPath
     */
    public String getMRUExpressionFolderPath() {
        if (MRUExpressionFolderPath == null) {
            MRUExpressionFolderPath = "";
        }
        return MRUExpressionFolderPath;
    }

    /**
     * @param MRUExpressionFolderPath the MRUExpressionFolderPath to set
     */
    public void setMRUExpressionFolderPath(String MRUExpressionFolderPath) {
        this.MRUExpressionFolderPath = MRUExpressionFolderPath;
    }

}
