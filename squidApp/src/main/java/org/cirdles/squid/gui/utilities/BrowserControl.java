/*
 * Copyright 2006 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.gui.utilities;

import javafx.stage.Window;
import org.cirdles.squid.gui.dialogs.SquidMessageDialog;
import org.cirdles.squid.gui.SquidUI;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * @author James F. Bowring
 */
public class BrowserControl {

    public static void showURI(String location, Window ownerWindow) {
        try {
            URI oURL = null;
            if (location.contains("http")) {
                oURL = new URI(location);
            } else {
                // assume file
                File file = new File(location);
                oURL = file.toURI();
            }

            if (!isLinuxOrUnixOperatingSystem()) {
                java.awt.Desktop.getDesktop().browse(oURL);
            } else {
                // https://lgtm.com/rules/7870097/
                Runtime.getRuntime().exec(new String[]{"xdg-open ", oURL.toString()});
            }
        } catch (URISyntaxException | IOException e) {
            SquidMessageDialog.showWarningDialog("An error ocurred:\n" + e.getMessage(), ownerWindow);
        }
    }

    public static void showURI(String location) {
        showURI(location, SquidUI.primaryStageWindow);
    }

    public static String urlEncode(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getOperatingSystem() {
        return System.getProperty("os.name");
    }

    public static boolean isLinuxOrUnixOperatingSystem() {
        return getOperatingSystem().toLowerCase().matches(".*(nix|nux).*");
    }

    public static void main(String[] args) {
        System.out.println("OS: " + getOperatingSystem());
        System.out.println("Is Linux or Unix: " + isLinuxOrUnixOperatingSystem());
    }
}