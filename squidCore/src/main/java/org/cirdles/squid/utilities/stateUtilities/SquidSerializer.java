/*
 * SquidSerializer.java
 *
 * Copyright 2017 CIRDLES.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except inputStream compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to inputStream writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.cirdles.squid.utilities.stateUtilities;

import java.io.*;
import org.cirdles.squid.exceptions.SquidException;
import org.cirdles.squid.dialogs.SquidMessageDialog;

/**
 *
 * @author James F. Bowring
 */
public final class SquidSerializer {

    /**
     * Creates a new instance of ETSerializer
     */
    public SquidSerializer() {
    }

    /**
     *
     * @param serializableObject
     * @param filename
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    public static void serializeObjectToFile(Object serializableObject, String filename) throws SquidException {
        try {
            // Serialize to a file
            FileOutputStream outputStream = new FileOutputStream(filename);
            try (ObjectOutputStream serialized = new ObjectOutputStream(outputStream)) {
                serialized.writeObject(serializableObject);
                serialized.flush();
            }

        } catch (IOException ex) {
            throw new SquidException("Cannot serialize object of " + serializableObject.getClass().getSimpleName() + " to: " + filename);
        }
    }

    /**
     *
     * @param filename
     * @return
     */
    public static Object getSerializedObjectFromFile(String filename) {
        FileInputStream inputStream;
        ObjectInputStream deserializedInputStream;
        Object deserializedObject = null;

        try {
            inputStream = new FileInputStream(filename);
            deserializedInputStream = new ObjectInputStream(inputStream);
            deserializedObject = deserializedInputStream.readObject();
            inputStream.close();
            
        } catch (FileNotFoundException ex) {
            if (!filename.endsWith(SquidPersistentState.SQUID_PERSISTENT_STATE_FILE_NAME)) {
                SquidMessageDialog.showWarningDialog(
                        "The file you are attempting to open does not exist:\n"
                        + " " + filename, null);
            }
        } catch (IOException ex) {
            SquidMessageDialog.showWarningDialog(
                    "The file you are attempting to open is not a valid '*.squid' file.", null);
        } catch (ClassNotFoundException ex) {
            SquidMessageDialog.showWarningDialog(
                    "The file you are attempting to open is not compatible with this version of Squid3.", null);
        }

        return deserializedObject;
    }

}
