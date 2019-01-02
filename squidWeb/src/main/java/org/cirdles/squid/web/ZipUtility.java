/*
 * Copyright 2018 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class ZipUtility {

    private static final Map<String, String> ZIP_FILE_ENV;

    static {
        Map<String, String> zipFileEnv = new HashMap<>();
        zipFileEnv.put("create", "true");

        ZIP_FILE_ENV = Collections.unmodifiableMap(zipFileEnv);
    }

    public static Path recursivelyZip(Path target) throws IOException {
        Path zipFilePath = target.resolveSibling("reports.zip");
        Files.deleteIfExists(zipFilePath);

        try (FileSystem zipFileFileSystem = FileSystems.newFileSystem(
                URI.create("jar:file:" + zipFilePath), ZIP_FILE_ENV)) {

            zipDirectoryOrFile("/", target, zipFileFileSystem);
        }

        return zipFilePath;
    }

    private static void zipDirectoryOrFile(String level, Path target, FileSystem zipFileFileSystem) throws IOException {
        Files.list(target).forEach(new Consumer<Path>() {
            @Override
            public void accept(Path entry) {
                try {
                    Files.copy(entry, zipFileFileSystem.getPath(level + entry.getFileName()));

                    if (Files.isDirectory(entry, new LinkOption[]{})) {
                        zipDirectoryOrFile(level + entry.getFileName() + "/", entry, zipFileFileSystem);
                    }
                } catch (IOException iOException) {
                }
            }
        });
    }

    public static Path extractZippedFile(File inFile, File destination) throws IOException {
        File outFile = null;
        OutputStream out = null;
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(inFile))) {
            //open infile for reading
            ZipEntry entry;

            //checks first entry exists
            if ((entry = zis.getNextEntry()) != null) {
                String outFilename = entry.getName();
                outFile = new File(destination, outFilename);

                try {
                    //open outFile for writing
                    out = new FileOutputStream(outFile);
                    byte[] buff = new byte[2048];
                    int len;

                    while ((len = zis.read(buff)) > 0) {
                        out.write(buff, 0, len);
                    }
                } finally {
                    //close outFile
                    if (out != null) {
                        out.close();
                    }
                }
            }
        }

        return Paths.get(outFile.getPath());

    }
}
