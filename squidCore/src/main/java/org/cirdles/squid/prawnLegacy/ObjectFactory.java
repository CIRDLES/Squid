/*
 * Copyright 2021 James F. Bowring and CIRDLES.org.
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
 *
 *
 * This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802
 * See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
 * Any modifications to this file will be lost upon recompilation of the source schema.
 * Generated on: 2021.01.21 at 01:20:39 PM EST
 */
package org.cirdles.squid.prawnLegacy;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the au.edu.anu.shrimp package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: au.edu.anu.shrimp
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PrawnLegacyFile }
     */
    public PrawnLegacyFile createPrawnLegacyFile() {
        return new PrawnLegacyFile();
    }

    /**
     * Create an instance of {@link PrawnLegacyFile.Run }
     */
    public PrawnLegacyFile.Run createPrawnLegacyFileRun() {
        return new PrawnLegacyFile.Run();
    }

    /**
     * Create an instance of {@link PrawnLegacyFile.Run.Set }
     */
    public PrawnLegacyFile.Run.Set createPrawnLegacyFileRunSet() {
        return new PrawnLegacyFile.Run.Set();
    }

    /**
     * Create an instance of {@link PrawnLegacyFile.Run.Set.Data }
     */
    public PrawnLegacyFile.Run.Set.Data createPrawnLegacyFileRunSetData() {
        return new PrawnLegacyFile.Run.Set.Data();
    }

    /**
     * Create an instance of {@link PrawnLegacyFile.Run.Set.Data.Peak }
     */
    public PrawnLegacyFile.Run.Set.Data.Peak createPrawnLegacyFileRunSetDataPeak() {
        return new PrawnLegacyFile.Run.Set.Data.Peak();
    }

    /**
     * Create an instance of {@link PrawnLegacyFile.Run.RunTable }
     */
    public PrawnLegacyFile.Run.RunTable createPrawnLegacyFileRunRunTable() {
        return new PrawnLegacyFile.Run.RunTable();
    }

    /**
     * Create an instance of {@link PrawnLegacyFile.Run.Set.Data.Peak.Scan }
     */
    public PrawnLegacyFile.Run.Set.Data.Peak.Scan createPrawnLegacyFileRunSetDataPeakScan() {
        return new PrawnLegacyFile.Run.Set.Data.Peak.Scan();
    }

}