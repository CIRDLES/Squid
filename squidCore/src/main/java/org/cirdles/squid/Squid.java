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
package org.cirdles.squid;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cirdles.commons.util.ResourceExtractor;

/**
 *
 * @author James F. Bowring
 */
public final class Squid {

    public static final String VERSION;
    public static final String RELEASE_DATE;
    public static final File DEFAULT_SQUID3_REPORTS_FOLDER;

    public static final StringBuilder ABOUT_WINDOW_CONTENT = new StringBuilder();
    public static final StringBuilder CONTRIBUTORS_CONTENT = new StringBuilder();

    public static final ResourceExtractor SQUID_RESOURCE_EXTRACTOR
            = new ResourceExtractor(Squid.class);
    
   static {
        String version = "version";
        String releaseDate = "date";

        // get version number and release date written by pom.xml
        Path resourcePath = Squid.SQUID_RESOURCE_EXTRACTOR.extractResourceAsPath("version.txt");
        Charset charset = StandardCharsets.UTF_8; //Charset.forName("US-ASCII");
        try (BufferedReader reader = Files.newBufferedReader(resourcePath, charset)) {
            String line = reader.readLine();
            if (line != null) {
                String[] versionText = line.split("=");
                version = versionText[1];
            }

            line = reader.readLine();
            if (line != null) {
                String[] versionDate = line.split("=");
                releaseDate = versionDate[1];
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        VERSION = version;
        RELEASE_DATE = releaseDate;

        // get content for about window
        resourcePath = Squid.SQUID_RESOURCE_EXTRACTOR.extractResourceAsPath("/org/cirdles/squid/docs/aboutContent.txt");
        try (BufferedReader reader = Files.newBufferedReader(resourcePath, charset)) {
            String thisLine;

            while ((thisLine = reader.readLine()) != null) {
                Squid.ABOUT_WINDOW_CONTENT.append(thisLine);
            }

        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        
        resourcePath = Squid.SQUID_RESOURCE_EXTRACTOR.extractResourceAsPath("/org/cirdles/squid/docs/contributorsContent.txt");
        try (BufferedReader reader = Files.newBufferedReader(resourcePath, charset)) {
            String thisLine;

            while ((thisLine = reader.readLine()) != null) {
                Squid.CONTRIBUTORS_CONTENT.append(thisLine);
            }

        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        
        DEFAULT_SQUID3_REPORTS_FOLDER = new File("Squid3_Reports_v" + VERSION);
        if (!DEFAULT_SQUID3_REPORTS_FOLDER.exists()) {
            if (!DEFAULT_SQUID3_REPORTS_FOLDER.mkdir()) {
                System.out.println("Failed to make Squid3 Reports folder.");
            }
        }

    }
}
