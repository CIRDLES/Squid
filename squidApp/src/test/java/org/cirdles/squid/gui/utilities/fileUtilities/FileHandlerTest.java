package org.cirdles.squid.gui.utilities.fileUtilities;

import junit.framework.TestCase;
import org.cirdles.squid.constants.Squid3Constants;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.projects.SquidProject;
import org.cirdles.squid.utilities.stateUtilities.SquidSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileHandlerTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
    }

    // April 2022 example test for Ian Robinson
    public void testSelectProjectFile() throws SquidException, IOException {
        SquidProject myProject = new SquidProject(Squid3Constants.TaskTypeEnum.GENERAL);
        SquidSerializer.serializeObjectToFile(myProject, "Test.ser");
        SquidProject myOtherProject = (SquidProject) SquidSerializer.getSerializedObjectFromFile("Test.ser", true);
        assertEquals(myProject.getProjectName(), myOtherProject.getProjectName());
        Files.deleteIfExists((new File("Test.ser")).toPath());
    }
}