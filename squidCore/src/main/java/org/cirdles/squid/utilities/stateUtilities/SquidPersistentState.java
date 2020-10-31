/*
 * SquidPersistentState.java
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

import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.tasks.taskDesign.TaskDesign;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.cirdles.squid.constants.Squid3Constants.SQUID_USERS_DATA_FOLDER_NAME;

/**
 * @author James F. Bowring
 */
public class SquidPersistentState implements Serializable {

    // class variables
    private static final long serialVersionUID = 9131785805774520290L;
    private static final String SQUID_PERSISTENT_STATE_FILE_NAME = "SquidPersistentState.ser";
    private static volatile SquidPersistentState instance = (SquidPersistentState) SquidSerializer.getSerializedObjectFromFile(SquidPersistentState.getMySerializedName(), false);
    private static final int MRU_COUNT = 10;

    // instance variables
    private TaskDesign taskDesign;

    private File MRUProjectFile;
    private List<String> MRUProjectList;
    private String MRUProjectFolderPath;

    private File MRUPrawnFile;
    private List<String> MRUPrawnFileList;
    private String MRUPrawnFileFolderPath;

    private String MRUTaskFolderPath;

    private String MRUSquidTaskFolderPath;

    private File MRUExpressionFile;
    private List<String> MRUExpressionList;
    private String MRUExpressionFolderPath;

    private File MRUExpressionGraphFile;
    private List<String> MRUExpressionGraphList;
    private String MRUExpressionGraphFolderPath;

    private File MRUOPFile;
    private List<String> MRUOPFileList;
    private String MRUOPFileFolderPath;

    private File MRUTaskXMLFile;
    private List<String> MRUTaskXMLList;
    private String MRUTaskXMLFolderPath;

    private File customExpressionsFile;

    /**
     *
     */
    private SquidPersistentState() {

        initMRULists();

        taskDesign = new TaskDesign();

        // check if user data folder exists and create if it does not
        File dataFolder = new File(
                File.separator + System.getProperty("user.home") + File.separator + SQUID_USERS_DATA_FOLDER_NAME);
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        MRUProjectFile = null;
        MRUProjectList = new ArrayList<>();
        MRUProjectFolderPath = "";

        MRUPrawnFile = null;
        MRUPrawnFileList = new ArrayList<>();
        MRUPrawnFileFolderPath = "";

        MRUTaskFolderPath = "";

        MRUSquidTaskFolderPath = "";

        MRUExpressionFile = null;
        MRUExpressionList = new ArrayList<>();
        MRUExpressionFolderPath = "";

        MRUOPFile = null;
        MRUOPFileList = new ArrayList<>();
        MRUOPFileFolderPath = "";

        MRUTaskXMLFile = null;
        MRUTaskXMLList = new ArrayList<>();
        MRUTaskXMLFolderPath = "";

        customExpressionsFile = null;

        serializeSelf();
    }

    private void serializeSelf() {
        // save initial persistent state serialized file
        try {
            SquidSerializer.serializeObjectToFile(this, getMySerializedName());
        } catch (SquidException squidException) {
        }
    }

    public void updateSquidPersistentState() {
        serializeSelf();
    }

    /**
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

        // check to update TaskDesign
        TaskDesign sup = instance.getTaskDesign();
        if (sup == null) {
            sup = new TaskDesign();
            instance.setTaskDesign(sup);
        } else if (sup.getRatioNames() == null) {
            sup = new TaskDesign();
            instance.setTaskDesign(sup);
        }

        return instance;
    }
    //properties

    /**
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
     * @return the taskDesign
     */
    public TaskDesign getTaskDesign() {
        return taskDesign;
    }

    /**
     * @param taskDesign the taskDesign to set
     */
    public void setTaskDesign(TaskDesign taskDesign) {
        this.taskDesign = taskDesign;
    }

    // General methods *********************************************************
    private void initMRULists() {
        MRUProjectList = new ArrayList<>(MRU_COUNT);
        MRUPrawnFileList = new ArrayList<>(MRU_COUNT);
//        MRUTaskList = new ArrayList<>(MRU_COUNT);
        MRUExpressionList = new ArrayList<>(MRU_COUNT);
    }

    private void cleanListMRU(List<String> MRUfileList) {
        ArrayList<String> missingFileNames = new ArrayList<>();
        // test for missing files
        for (String projectFileName : MRUfileList) {
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

    // MRU Project Data *********************************************************
    /**
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

    public void updateOPFileListMRU(File opFile) {
        if (opFile != null) {
            String name = opFile.getAbsolutePath();
            MRUOPFileList.remove(name);
            MRUOPFileList.add(0, name);
            if (MRUOPFileList.size() > MRU_COUNT) {
                MRUOPFileList.remove(MRU_COUNT);
            }
            MRUOPFileFolderPath = opFile.getParentFile().getAbsolutePath();
            MRUOPFile = opFile;
        }
        serializeSelf();
    }

    public void removeFileNameFromProjectListMRU(String mruProjectFileName) {
        MRUProjectList.remove(mruProjectFileName);
    }

    public void cleanProjectListMRU() {
        cleanListMRU(MRUProjectList);
    }

    public void cleanOPFileListMRU() {
        cleanListMRU(MRUOPFileList);
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
     * @return
     */
    public List<String> getMRUProjectList() {
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

    // MRU PrawnFile Data ***************************************************
    /**
     * @param PrawnFileMRU
     */
    public void updatePrawnFileListMRU(File PrawnFileMRU) {
        if (MRUPrawnFileList == null) {
            MRUPrawnFileList = new ArrayList<>();
        }

        if (PrawnFileMRU != null) {
            try {
                // remove if exists in MRU list
                String MRUPrawnFileName = PrawnFileMRU.getCanonicalPath();
                MRUPrawnFileList.remove(MRUPrawnFileName);
                MRUPrawnFileList.add(0, MRUPrawnFileName);

                // trim list
                if (MRUPrawnFileList.size() > MRU_COUNT) {
                    MRUPrawnFileList.remove(MRU_COUNT);
                }

                // update MRU folder
                MRUPrawnFileFolderPath = PrawnFileMRU.getParent();

                // update current file
                MRUPrawnFile = PrawnFileMRU;

            } catch (IOException iOException) {
            }
        }

        // save
        try {
            SquidSerializer.serializeObjectToFile(this, getMySerializedName());
        } catch (SquidException squidException) {
        }
    }

    public void removeFileNameFromPrawnFileListMRU(String mruPrawnFileName) {
        MRUProjectList.remove(mruPrawnFileName);
    }

    public void cleanPrawnFileListMRU() {
        cleanListMRU(MRUPrawnFileList);
    }

    public void removePrawnFileNameFromMRU(String prawnFileName) {
        MRUPrawnFileList.remove(prawnFileName);
    }

    /**
     * @return the MRUPrawnFile
     */
    public File getMRUPrawnFile() {
        return MRUPrawnFile;
    }

    /**
     * @param MRUPrawnFile the MRUPrawnFile to set
     */
    public void setMRUPrawnFile(File MRUPrawnFile) {
        this.MRUPrawnFile = MRUPrawnFile;
    }

    /**
     * @return the MRUPrawnFileList
     */
    public List<String> getMRUPrawnFileList() {
        return MRUPrawnFileList;
    }

    /**
     * @param MRUPrawnFileList the MRUPrawnFileList to set
     */
    public void setMRUPrawnFileList(List<String> MRUPrawnFileList) {
        this.MRUPrawnFileList = MRUPrawnFileList;
    }

    /**
     * @return the MRUPrawnFileFolderPath
     */
    public String getMRUPrawnFileFolderPath() {
        return MRUPrawnFileFolderPath;
    }

    // MRU Squid 2.5 Task Data ********************************************************
    /**
     * @param MRUPrawnFileFolderPath the MRUPrawnFileFolderPath to set
     */
    public void setMRUPrawnFileFolderPath(String MRUPrawnFileFolderPath) {
        this.MRUPrawnFileFolderPath = MRUPrawnFileFolderPath;
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

    // MRU Squid Task Data ********************************************************
    /**
     * @return the MRUTaskFolderPath
     */
    public String getMRUSquidTaskFolderPath() {
        if (MRUSquidTaskFolderPath == null) {
            MRUSquidTaskFolderPath = "";
        }
        return MRUSquidTaskFolderPath;
    }

    /**
     * @param MRUTaskFolderPath the MRUTaskFolderPath to set
     */
    public void setMRUSquidTaskFolderPath(String MRUSquidTaskFolderPath) {
        this.MRUSquidTaskFolderPath = MRUSquidTaskFolderPath;
    }

    // MRU Expression Data ********************************************************
    /**
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
            //System.out.println(squidException.getMessage());
        }
    }

    public void removeFileNameFromExpressionListMRU(String mruExpressionFileName) {
        MRUExpressionList.remove(mruExpressionFileName);
    }

    public void cleanExpressionListMRU() {
        cleanListMRU(MRUExpressionList);
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
    public List<String> getMRUExpressionList() {
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

    // MRU Expression Graph Data ********************************************************
    /**
     * @param expressionFileMRU
     */
    public void updateExpressionGraphListMRU(File expressionGraphFileMRU) {

        if (MRUExpressionGraphList == null) {
            MRUExpressionGraphList = new ArrayList<>(MRU_COUNT);
        }

        if (expressionGraphFileMRU != null) {
            try {
                // remove if exists in MRU list
                String MRUExpressionGraphFileName = expressionGraphFileMRU.getCanonicalPath();
                MRUExpressionGraphList.remove(MRUExpressionGraphFileName);
                MRUExpressionGraphList.add(0, MRUExpressionGraphFileName);

                // trim list
                if (MRUExpressionGraphList.size() > MRU_COUNT) {
                    MRUExpressionGraphList.remove(MRU_COUNT);
                }

                // update MRU folder
                MRUExpressionGraphFolderPath = expressionGraphFileMRU.getParent();

                // update current file
                MRUExpressionGraphFile = expressionGraphFileMRU;

            } catch (IOException iOException) {
            }
        }

        // save
        try {
            SquidSerializer.serializeObjectToFile(this, getMySerializedName());
        } catch (SquidException squidException) {
            //System.out.println(squidException.getMessage());
        }
    }

    public void removeFileNameFromExpressionGraphListMRU(String mruExpressionGraphFileName) {
        MRUExpressionGraphList.remove(mruExpressionGraphFileName);
    }

    public void cleanExpressionGraphListMRU() {
        cleanListMRU(MRUExpressionGraphList);
    }

    public void removeExpressionGraphFileNameFromMRU(String expressionGraphFileName) {
        MRUExpressionGraphList.remove(expressionGraphFileName);
    }

    /**
     * @return the MRUExpressionFile
     */
    public File getMRUExpressionGraphFile() {
        return MRUExpressionGraphFile;
    }

    /**
     * @param MRUExpressionGraphFile the MRUExpressionGraphFile to set
     */
    public void setMRUExpressionGraphFile(File MRUExpressionGraphFile) {
        this.MRUExpressionGraphFile = MRUExpressionGraphFile;
    }

    /**
     * @return the MRUExpressionList
     */
    public List<String> getMRUExpressionGraphList() {
        if (MRUExpressionGraphList == null) {
            MRUExpressionGraphList = null;
        }
        return MRUExpressionGraphList;
    }

    /**
     * @param MRUExpressionGraphList the MRUExpressionList to set
     */
    public void setMRUExpressionGraphList(ArrayList<String> MRUExpressionGraphList) {
        this.MRUExpressionGraphList = MRUExpressionGraphList;
    }

    /**
     * @return the MRUExpressionFolderPath
     */
    public String getMRUExpressionGraphFolderPath() {
        if (MRUExpressionGraphFolderPath == null) {
            MRUExpressionGraphFolderPath = "";
        }
        return MRUExpressionGraphFolderPath;
    }

    /**
     * @param MRUExpressionGraphFolderPath the MRUExpressionFolderPath to set
     */
    public void setMRUExpressionGraphFolderPath(String MRUExpressionGraphFolderPath) {
        this.MRUExpressionGraphFolderPath = MRUExpressionGraphFolderPath;
    }

    public File getCustomExpressionsFile() {
        return customExpressionsFile;
    }

    public void setCustomExpressionsFile(File file) {
        customExpressionsFile = file;
    }

    /**
     * @return the MRUOPFile
     */
    public File getMRUOPFile() {
        return MRUOPFile;
    }

    /**
     * @param MRUOPFile the MRUOPFile to set
     */
    public void setMRUOPFile(File MRUOPFile) {
        this.MRUOPFile = MRUOPFile;
    }

    public List<String> getMRUOPFileList() {
        if (MRUOPFileList == null) {
            MRUOPFileList = new ArrayList<>();
        }
        return MRUOPFileList;
    }

    public void setMRUOPFileList(List<String> MRUOPFileList) {
        this.MRUOPFileList = MRUOPFileList;
    }

    public String getMRUOPFileFolderPath() {
        if (MRUOPFileFolderPath == null) {
            MRUOPFileFolderPath = "";
        }
        return MRUOPFileFolderPath;
    }

    public void setMRUOPFileFolderPath(String MRUOPFileFolderPath) {
        this.MRUOPFileFolderPath = MRUOPFileFolderPath;
    }

    public void removeOPFileNameFromMRU(String opFileName) {
        MRUOPFileList.remove(opFileName);
    }

    // MRU TaskXML File Data ***************************************************
    /**
     * @param PrawnFileMRU
     */
    public void updateTaskXMLFileListMRU(File TaskXMLMRU) {
        if (MRUTaskXMLList == null) {
            MRUTaskXMLList = new ArrayList<>();
        }

        if (TaskXMLMRU != null) {
            try {
                // remove if exists in MRU list
                String MRUTaskXMLName = TaskXMLMRU.getCanonicalPath();
                MRUTaskXMLList.remove(MRUTaskXMLName);
                MRUTaskXMLList.add(0, MRUTaskXMLName);

                // trim list
                if (MRUTaskXMLList.size() > MRU_COUNT) {
                    MRUTaskXMLList.remove(MRU_COUNT);
                }

                // update MRU folder
                MRUPrawnFileFolderPath = TaskXMLMRU.getParent();

                // update current file
                MRUPrawnFile = TaskXMLMRU;

            } catch (IOException iOException) {
            }
        }

        // save
        try {
            SquidSerializer.serializeObjectToFile(this, getMySerializedName());
        } catch (SquidException squidException) {
        }
    }

    public void removeFileNameFromTaskXMLFileListMRU(String mruTaskXMLFileName) {
        MRUTaskXMLList.remove(mruTaskXMLFileName);
    }

    public void cleanTaskXMLFileListMRU() {
        cleanListMRU(MRUTaskXMLList);
    }

    /**
     * @return the MRUTaskXMLFile
     */
    public File getMRUTaskXMLFile() {
        return MRUTaskXMLFile;
    }

    /**
     * @param MRUTaskXMLFile the MRUTaskXMLFile to set
     */
    public void setMRUTaskXMLFile(File MRUTaskXMLFile) {
        this.MRUTaskXMLFile = MRUTaskXMLFile;
    }

    /**
     * @return the MRUTaskXMLList
     */
    public List<String> getMRUTaskXMLList() {
        if (MRUTaskXMLList == null) {
            MRUTaskXMLList = new ArrayList<>();
        }
        return MRUTaskXMLList;
    }

    /**
     * @param MRUTaskXMLList the MRUTaskXMLList to set
     */
    public void setMRUTaskXMLList(List<String> MRUTaskXMLList) {
        this.MRUTaskXMLList = MRUTaskXMLList;
    }

    /**
     * @return the MRUTaskXMLFolderPath
     */
    public String getMRUTaskXMLFolderPath() {
        if (MRUTaskXMLFolderPath == null) {
            MRUTaskXMLFolderPath = "";
        }
        return MRUTaskXMLFolderPath;
    }

    /**
     * @param MRUTaskXMLFolderPath the MRUTaskXMLFolderPath to set
     */
    public void setMRUTaskXMLFolderPath(String MRUTaskXMLFolderPath) {
        this.MRUTaskXMLFolderPath = MRUTaskXMLFolderPath;
    }

    public void removeTaskXMLFileNameFromMRU(String taskXMLFileName) {
        MRUTaskXMLList.remove(taskXMLFileName);
    }
}
