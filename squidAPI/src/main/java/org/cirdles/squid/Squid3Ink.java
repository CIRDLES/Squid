/*
 * Copyright 2021 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.utilities.stateUtilities.SquidSerializer;
import org.cirdles.squid.projects.Squid3ProjectBasicAPI;
import org.cirdles.squid.projects.Squid3ProjectReportingAPI;

/**
 * Provides specialized class to implement API for Squid3Ink Virtual Squid3.
 *
 * @author bowring
 */
public class Squid3Ink implements Squid3API {

    private Squid3ProjectBasicAPI squid3Project;

    @Override
    public Squid3ProjectBasicAPI getSquid3Project() {
        return squid3Project;
    }

    private Squid3Ink() {
    }

    public static Squid3API spillSquid3Ink() {
        return new Squid3Ink();
    }

    /**
     * Loads existing Squid3 project file.
     *
     * @param projectFilePath
     * @return
     */
    @Override
    public void openSquid3Project(Path projectFilePath) {
        squid3Project
                = (SquidProject) SquidSerializer.getSerializedObjectFromFile(projectFilePath.toString(), true);
    }

    /**
     * Loads demonstration Squid3 project.
     *
     * @return SquidProject
     */
    @Override
    public void openDemonstrationSquid3Project() {
        ResourceExtractor extractor = new ResourceExtractor(SquidProject.class);
        File testingModelFile = extractor.extractResourceAsFile("SQUID3_demo_file.squid");
        openSquid3Project(testingModelFile.toPath());
    }

    @Override
    public void generateAllSquid3ProjectReports() throws IOException {
        ((Squid3ProjectReportingAPI) squid3Project).generateAllReports();
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        Squid3API squidInk = Squid3Ink.spillSquid3Ink();
        squidInk.openDemonstrationSquid3Project();
        squidInk.generateAllSquid3ProjectReports();

        System.out.println(squidInk.getSquid3Project().getProjectName());
    }
}
