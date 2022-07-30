/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.utilities;

import org.apache.poi.util.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
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
            throws IOException {
        ZipFile zipFile = new ZipFile(archive);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        // Dec 2021 this fix comes from https://cwe.mitre.org/data/definitions/23.html and SNYK CODE
        // via https://app.snyk.io/org/bowring/project/7dd848fc-362b-4514-a91c-3c04628633ac
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            Path entryPath = targetDirectory.toPath().resolve(entry.getName());
            if (!entryPath.normalize().startsWith(targetDirectory.toPath()))
                throw new IOException("Zip entry contained path traversal");
            if (entry.isDirectory()) {
                Files.createDirectories(entryPath);
            } else {
                Files.createDirectories(entryPath.getParent());
                try (InputStream in = zipFile.getInputStream(entry)) {
                    try (OutputStream out = new FileOutputStream(entryPath.toFile())) {
                        IOUtils.copy(in, out);
                    }
                }
            }
        }
        zipFile.close();
    }
}