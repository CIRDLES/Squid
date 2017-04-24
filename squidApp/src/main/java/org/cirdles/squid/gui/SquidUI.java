/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.cirdles.calamari.core.PrawnFileHandler;
import org.cirdles.calamari.prawn.PrawnFile;
import org.cirdles.calamari.prawn.PrawnFileFilter;
import org.cirdles.calamari.utilities.FileHelper;
import org.cirdles.calamari.utilities.FileUtilities;
import org.cirdles.commons.util.ResourceExtractor;
import org.cirdles.squid.projects.SquidProject;

/**
 *
 * @author bowring
 */
public class SquidUI extends Application {

    public static final String VERSION;
    public static final String RELEASE_DATE;

    static {
        ResourceExtractor squidResourceExtractor
                = new ResourceExtractor(SquidUI.class);

        String version = "version";
        String releaseDate = "date";

        // get version number and release date written by pom.xml
        Path resourcePath = squidResourceExtractor.extractResourceAsPath("version.txt");
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
    }

    private Stage primaryStage;
    private SquidProject squidProject;

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource("SquidUIController.fxml"));

        Scene scene = new Scene(root);

        primaryStage.getIcons().add(new Image("org/cirdles/squid/gui/images/SquidLogoSansText.png"));

        primaryStage.setMinHeight(675.0);
        primaryStage.setMinWidth(935.0);
        primaryStage.setTitle("Squid 3.0");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest((WindowEvent e) -> {
            Platform.exit();
            System.exit(0);
        });

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static void initCalamari(PrawnFileHandler prawnFileHandler) {
        ResourceExtractor prawnFileResourceExtractor
                = new ResourceExtractor(PrawnFile.class);

        Path listOfPrawnFiles = prawnFileResourceExtractor.extractResourceAsPath("listOfPrawnFiles.txt");
        if (listOfPrawnFiles != null) {
            File exampleFolder = new File("ExamplePrawnXMLFiles");
            try {
                if (exampleFolder.exists()) {
                    FileUtilities.recursiveDelete(exampleFolder.toPath());
                }
                if (exampleFolder.mkdir()) {
                    List<String> fileNames = Files.readAllLines(listOfPrawnFiles, ISO_8859_1);
                    for (int i = 0; i < fileNames.size(); i++) {
                        // test for empty string
                        if (fileNames.get(i).trim().length() > 0) {
                            File prawnFileResource = prawnFileResourceExtractor.extractResourceAsFile(fileNames.get(i));
                            File prawnFile = new File(exampleFolder.getCanonicalPath() + File.separator + fileNames.get(i));

                            if (prawnFileResource.renameTo(prawnFile)) {
                                System.out.println("PrawnFile added: " + fileNames.get(i));
                            } else {
                                System.out.println("PrawnFile failed to add: " + fileNames.get(i));
                            }
                        }
                    }
                }
            } catch (IOException iOException) {
            }
            try {
                // point to directory, but no default choice
                prawnFileHandler.setCurrentPrawnFileLocation(exampleFolder.getCanonicalPath());
            } catch (IOException iOException) {
            }
        }

        File defaultCalamariReportsFolder = new File("CalamariReports_v" + SquidUI.VERSION);
        prawnFileHandler.getReportsEngine().setFolderToWriteCalamariReports(defaultCalamariReportsFolder);
        if (!defaultCalamariReportsFolder.exists()) {
            if (!defaultCalamariReportsFolder.mkdir()) {
                System.out.println("Failed to make Calamari reports directory");
            }
        }
    }

}
