/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.utilities;

import com.google.common.io.ByteStreams;
import com.google.common.io.FileWriteMode;
//import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 *
 * @author James F. Bowring
 */
public class FileUtilities {

    /**
     *
     * @param directory
     * @throws IOException
     */
    public static void recursiveDelete(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(
                    Path file,
                    BasicFileAttributes attrs) throws IOException {

                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(
                    Path dir,
                    IOException exc) throws IOException {

                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void unpackZipFile(final File archive, final File targetDirectory)
            throws ZipException, IOException {
        ZipFile zipFile = new ZipFile(archive);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            final ZipEntry zipEntry = entries.nextElement();
            if (zipEntry.isDirectory()) {
                continue;
            }
            final File targetFile = new File(targetDirectory,
                    zipEntry.getName());
            com.google.common.io.Files.createParentDirs(targetFile);
            ByteStreams.copy(zipFile.getInputStream(zipEntry), 
                    com.google.common.io.Files.asByteSink(targetFile, FileWriteMode.APPEND ).openStream());
        }
    }
}
