/*
 * SquidSerializer.java
 *
 * Copyright 2017 James F. Bowring and CIRDLES.org.
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
     * Creates a new instance of SquidSerializer
     */
    private SquidSerializer() {
    }

    /**
     *
     * @param serializableObject
     * @param fileName
     * @throws org.cirdles.squid.exceptions.SquidException
     */
    public static void serializeObjectToFile(Object serializableObject, String fileName) throws SquidException {

        // https://dzone.com/articles/fast-java-file-serialization
        // Sept 2018 speedup per Rayner request
        ObjectOutputStream objectOutputStream = null;
        try {
            RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
            FileOutputStream fos = new FileOutputStream(raf.getFD());
            objectOutputStream = new ObjectOutputStream(fos);
            objectOutputStream.writeObject(serializableObject);
        } catch (IOException ex) {
            throw new SquidException("Cannot serialize object of " + serializableObject.getClass().getSimpleName() + " to: " + fileName);

        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException iOException) {
                }
            }
        }
    }

    public static void serializeObjectToFileRAF(Object serializableObject, String fileName) throws SquidException, IOException {
        ObjectOutputStream objectOutputStream = null;
        try {
            RandomAccessFile raf = new RandomAccessFile(fileName, "rwd");
            FileOutputStream fos = new FileOutputStream(raf.getFD());
            objectOutputStream = new ObjectOutputStream(fos);
            objectOutputStream.writeObject(serializableObject);
        } catch (IOException ex) {
            throw new SquidException("Cannot serialize object of " + serializableObject.getClass().getSimpleName() + " to: " + fileName);

        } finally {
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
        }
    }

    /**
     *
     * @param filename
     * @return
     */
    public static Object getSerializedObjectFromFile(String filename, boolean verbose) {
        //FileInputStream inputStream;
        ObjectInputStream deserializedInputStream;
        Object deserializedObject = null;

        try (FileInputStream inputStream = new FileInputStream(filename)) {
            deserializedInputStream = new ObjectInputStream(inputStream);
            deserializedObject = deserializedInputStream.readObject();
            inputStream.close();

        } catch (FileNotFoundException ex) {
            if (verbose) {
                SquidMessageDialog.showWarningDialog("The file you are attempting to open does not exist:\n"
                        + " " + filename, null);
            }
        } catch (IOException ex) {
            if (verbose) {
                SquidMessageDialog.showWarningDialog(
                        "The file you are attempting to open is not a valid '*.squid' file.", null);
            }
        } catch (ClassNotFoundException | ClassCastException ex) {
            if (verbose) {
                SquidMessageDialog.showWarningDialog(
                        "The file you are attempting to open is not compatible with this version of Squid3.", null);
            }
        }

        return deserializedObject;
    }

}
