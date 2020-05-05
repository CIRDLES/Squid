/*
 * Copyright 2017 James F. Bowring and CIRDLES.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.squid.utilities.fileUtilities;

import org.cirdles.squid.dialogs.SquidMessageDialog;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.utilities.stateUtilities.SquidSerializer;

import java.io.File;

/**
 *
 * @author James F. Bowring
 */
public final class ProjectFileUtilities {

    public static void serializeSquidProject(SquidProject squidProject, String projectFileName) {
        try {
            SquidSerializer.serializeObjectToFile(squidProject, projectFileName);
            squidProject.getPrawnFileHandler().getReportsEngine().setFolderToWriteCalamariReports(new File(projectFileName).getParentFile());
            SquidProject.setProjectChanged(false);
        } catch (SquidException ex) {
            SquidMessageDialog.showWarningDialog(ex.getMessage(), null);
        }
    }

}
