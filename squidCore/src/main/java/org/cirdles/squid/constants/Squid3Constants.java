/*
 * Copyright 2016 James F. Bowring and CIRDLES.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.squid.constants;

/**
 *
 * @author bowring
 */
public final class Squid3Constants {

    /**
     *
     */
    public static final String XML_HEADER_FOR_PRAWN_FILES_USING_REMOTE_SCHEMA
            = "<?xml version=\"1.0\"?>\n"
            + "<!-- SHRIMP SW PRAWN Data File -->\n"
            + "<prawn_file xmlns=\"https://raw.githubusercontent.com\"\n"
            + "            xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "            xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + "            xsi:schemaLocation=\"https://raw.githubusercontent.com\n"
            + "                                https://raw.githubusercontent.com/CIRDLES/Squid/master/squidCore/src/main/resources/org/cirdles/squid/schema/SHRIMP_PRAWN.xsd\">";

    public static final String XML_HEADER_FOR_PRAWN_FILES_USING_LOCAL_SCHEMA
            = "<?xml version=\"1.0\"?>\n"
            + "<!-- SHRIMP SW PRAWN Data File -->\n"
            + "<prawn_file xmlns=\"https://raw.githubusercontent.com\"\n"
            + "            xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "            xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + "            xsi:schemaLocation=\"https://raw.githubusercontent.com\n"
            + "                                ../schema/SHRIMP_PRAWN.xsd\">";

    /**
     *
     */
    public static final String URL_STRING_FOR_PRAWN_XML_SCHEMA_REMOTE
            = "https://raw.githubusercontent.com/CIRDLES/Squid/master/squidCore/src/main/resources/org/cirdles/squid/schema/SHRIMP_PRAWN.xsd";

    public static final String URL_STRING_FOR_PRAWN_XML_SCHEMA_LOCAL
            = "Schema/SHRIMP_PRAWN.xsd";

    /**
     *
     */
    public static final String DEFAULT_PRAWNFILE_NAME = "NO_NAME_";

    public static final String SQUID_USERS_DATA_FOLDER_NAME = "Squid3 User Data";

    /**
     *
     */
    public static final String XML_HEADER_FOR_SQUIDTASK_EXPRESSION_FILES_USING_REMOTE_SCHEMA
            = "<?xml version=\"1.0\"?>\n"
            + "<!-- SQUIDTASK_EXPRESSION_DATA_FILE -->\n"
            + "<Expression xmlns=\"https://raw.githubusercontent.com\"\n"
            + " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n"
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + " xsi:schemaLocation=\"https://raw.githubusercontent.com\n"
            + "                                https://raw.githubusercontent.com/CIRDLES/Squid/master/squidCore/src/main/resources/org/cirdles/squid/schema/SquidTask_ExpressionXMLSchema.xsd\">";

    public static final String XML_HEADER_FOR_SQUIDTASK_EXPRESSION_FILES_USING_LOCAL_SCHEMA
            = "<?xml version=\"1.0\"?>\n"
            + "<!-- SQUIDTASK_EXPRESSION_DATA_FILE -->\n"
            + "<Expression xmlns=\"https://raw.githubusercontent.com\"\n"
            + "            xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "            xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + "            xsi:schemaLocation=\"https://raw.githubusercontent.com\n"
            + "                                ../schema/SquidTask_ExpressionXMLSchema.xsd\">";

    /**
     *
     */
    public static final String URL_STRING_FOR_SQUIDTASK_EXPRESSION_XML_SCHEMA_REMOTE
            = "https://raw.githubusercontent.com/CIRDLES/Squid/master/squidCore/src/main/resources/org/cirdles/squid/schema/SquidTask_ExpressionXMLSchema.xsd";

    public static final String URL_STRING_FOR_SQUIDTASK_EXPRESSION_XML_SCHEMA_LOCAL
            = "Schema/SquidTask_ExpressionXMLSchema.xsd";

    private static final String[] DEFAULT_RATIOS_LIST_FOR_10_SPECIES = new String[]{
            "204/206", "207/206", "208/206", "238/196", "206/238", "254/238", "248/254", "206/270", "270/254", "206/254", "238/206"};
    public static String[] getDEFAULT_RATIOS_LIST_FOR_10_SPECIES(){return DEFAULT_RATIOS_LIST_FOR_10_SPECIES.clone();}

    public static final String DUPLICATE_STRING = "-DUP-";

    public static final String SQUID_DEFAULT_BACKGROUND_ISOTOPE_LABEL = "BKG";

    // names for Squid2.5 Primary (-1) and Secondary (-2) are interchangeable based on U or Th in Primary
    public static final String SQUID_PRIMARY_UTH_EQN_NAME_U = "UncorrPb/Uconst";
    public static final String SQUID_PRIMARY_UTH_EQN_NAME_TH = "UncorrPb/Thconst";
    // Squid2.5 Th/U equation (-3)
    public static final String SQUID_TH_U_EQN_NAME = "232Th/238U";
    // name for Squid2.5 Ppm parent eqn(-4) 
    public static final String SQUID_PPM_PARENT_EQN_NAME = "Ppm Parent Eqn";
    public static final String SQUID_MEAN_PPM_PARENT_NAME = "pdMeanParentEleA";
    // name for Squid2.5 Ppm chosen based on U or Th in Primary; then other is calculated
    public static final String SQUID_PPM_PARENT_EQN_NAME_U = "ppmU";
    public static final String SQUID_PPM_PARENT_EQN_NAME_TH = "ppmTh";
    

}
