/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.utilities;

import com.google.common.io.ByteStreams;
import com.google.common.io.FileWriteMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * @author James F. Bowring
 */
public class FileUtilities {

//    /**
//     *
//     * @param directory
//     * @throws IOException
//     */
//    public static void recursiveDelete(Path directory) throws IOException {
//        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
//            @Override
//            public FileVisitResult visitFile(
//                    Path file,
//                    BasicFileAttributes attrs) throws IOException {
//
//                Files.delete(file);
//                return FileVisitResult.CONTINUE;
//            }
//
//            @Override
//            public FileVisitResult postVisitDirectory(
//                    Path dir,
//                    IOException exc) throws IOException {
//
//                Files.delete(dir);
//                return FileVisitResult.CONTINUE;
//            }
//        });
//    }

    public static void recursiveDelete(Path pathToBeDeleted)
            throws IOException {
        Files.walk(pathToBeDeleted)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    public static boolean isFileClosedWindows(File file) {
        return file.renameTo(file);
    }

    public static boolean isFileClosedUnix(File file) {
        try {
            Process plsof = new ProcessBuilder(new String[]{"lsof", "|", "grep", file.getAbsolutePath()}).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(plsof.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(file.getAbsolutePath())) {
                    reader.close();
                    plsof.destroy();
                    return false;
                }
            }
            reader.close();
            plsof.destroy();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return true;
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
                    com.google.common.io.Files.asByteSink(targetFile, FileWriteMode.APPEND).openStream());
        }
    }
}