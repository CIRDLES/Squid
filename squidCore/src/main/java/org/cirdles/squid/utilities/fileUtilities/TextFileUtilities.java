/*
 * Copyright 2020 James F. Bowring and CIRDLES.org.
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static java.nio.file.attribute.PosixFilePermission.*;

/**
 * This utility is primarily used for manipulating XML files.
 *
 * @author bowring
 */

public class TextFileUtilities {

    /**
     * Writes text file from List< of Strings object, file name, and extension such as ".xml".
     *
     * @param stringLine
     * @param targetTextFileName
     * @param extension
     * @return
     * @throws IOException
     */
    public static File writeTextFileFromListOfStringsWithUnixLineEnd(List<String> stringLine, String targetTextFileName, String extension)
            throws IOException {
        File targetTextFile;

        // detect Operating System ... we need POSIX code for use on Ubuntu Server
        String OS = System.getProperty("os.name").toLowerCase(Locale.US);
        if (OS.toLowerCase(Locale.US).contains("win")) {
            // Feb 2021 Issue #580 force windows into suppressing CRLF for LF so Squid25 can open these files
            System.setProperty("line.separator", "\n");
            Path pathTempXML = Paths.get(targetTextFileName + extension).toAbsolutePath();
            try (BufferedWriter writer = Files.newBufferedWriter(pathTempXML, StandardCharsets.UTF_8)) {
                for (String line : stringLine) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            targetTextFile = pathTempXML.toFile();
            System.setProperty("line.separator", "\r\n");
        } else {
            // Posix attributes added to support web service on Linux
            Set<PosixFilePermission> perms = EnumSet.of(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE, GROUP_READ);
            Path config = Files.createTempFile(targetTextFileName, extension, PosixFilePermissions.asFileAttribute(perms));
            try (BufferedWriter writer = Files.newBufferedWriter(config, StandardCharsets.UTF_8)) {
                for (String line : stringLine) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            targetTextFile = config.toFile();
        }

        return targetTextFile;
    }
}