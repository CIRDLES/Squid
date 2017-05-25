/*
 * Copyright 2017 CIRDLES.org.
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
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.stage.Window;
import org.cirdles.commons.util.ResourceExtractor;

/**
 *
 * @author James F. Bowring
 */
public final class Squid {

    public static final String VERSION;
    public static final String RELEASE_DATE;

    public static StringBuilder aboutWindowContent = new StringBuilder();

    public static ResourceExtractor squidResourceExtractor
            = new ResourceExtractor(Squid.class);

    static {
        String version = "version";
        String releaseDate = "date";

        // get version number and release date written by pom.xml
        Path resourcePath = Squid.squidResourceExtractor.extractResourceAsPath("version.txt");
        Charset charset = Charset.forName("US-ASCII");
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
        resourcePath = Squid.squidResourceExtractor.extractResourceAsPath("/org/cirdles/squid/docs/aboutContent.txt");
        try (BufferedReader reader = Files.newBufferedReader(resourcePath, charset)) {
            Squid.aboutWindowContent = new StringBuilder();
            String thisLine;

            while ((thisLine = reader.readLine()) != null) {
                Squid.aboutWindowContent.append(thisLine);
            }

        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

    }
}
