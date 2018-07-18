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

import java.util.HashMap;
import java.util.Map;

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

    public static String[] getDEFAULT_RATIOS_LIST_FOR_10_SPECIES() {
        return DEFAULT_RATIOS_LIST_FOR_10_SPECIES.clone();
    }

    public static final String DUPLICATE_STRING = "-DUP-";

    public static final String SQUID_DEFAULT_BACKGROUND_ISOTOPE_LABEL = "BKG";

    // names for Squid2.5 Primary (-1) and Secondary (-2) are interchangeable based on U or Th in Primary
    public static final String SQUID_PRIMARY_UTH_EQN_NAME_U = "UncorrPb/Uconst";
    public static final String SQUID_PRIMARY_UTH_EQN_NAME_TH = "UncorrPb/Thconst";
    // Squid2.5 Th/U equation (-3)
    public static final String SQUID_TH_U_EQN_NAME = "232Th/238U";
    public static final String SQUID_TH_U_EQN_NAME_S = "232Th/238US";
    // name for Squid2.5 Ppm parent eqn(-4) 
    public static final String SQUID_PPM_PARENT_EQN_NAME = "Ppm Parent Eqn";
    public static final String SQUID_MEAN_PPM_PARENT_NAME = "pdMeanParentEleA";
    // name for Squid2.5 Ppm chosen based on U or Th in Primary; then other is calculated
    public static final String SQUID_PPM_PARENT_EQN_NAME_U = "ppmU";
    public static final String SQUID_PPM_PARENT_EQN_NAME_TH = "ppmTh";
    
    public static final String SQUID_TOTAL_206_238_NAME = "Total 206Pb/238U";
    public static final String SQUID_TOTAL_208_232_NAME = "Total 208Pb/232Th";

    // holding spot until models are implemented
    public static final double lambda238 = 1.55125E-10;
    public static final double lambda235 = 9.8485E-10;
    public static final double lambda232 = 4.9475E-11;
    public static final double uRatio = 137.88;
    public static final double badAge = -1.23456789;
    public static final double sComm0_64 = 17.821;
    public static final double sComm0_76 = 0.8741;
    public static final double sComm0_86 = 2.1095;
    public static final double sComm0_74 = 15.5773361;
    public static final double sComm0_84 = 37.5933995;
    public static final double PRESENT_238U235U = 137.88;
    public static final double stdRad86fact = 0.309567309630921;
    public static final double std_76 = 0.0587838486664528;

    public enum IndexIsoptopesEnum {
        PB_204("204"),
        PB_207("207"),
        PB_208("208");

        private final String isotope;

        private IndexIsoptopesEnum(String isotope) {
            this.isotope = isotope;
        }
        
                /**
         * @return the isotope
         */
        public String getIsotope() {
            return isotope;
        }

        public String getIsotopeCorrectionPrefixString(){
            return isotope.substring(2, 3) + "-corr";
        }

    }
    
    public static final int EXPRESSIONBUILDERDEFAULTFONTSIZE = 13;
    public static final int EXPRESSIONBUILDERMINFONTSIZE = 11;
    public static final int EXPRESSIONBUILDERMAXFONTSIZE = 18;
    
        /**
     *
     */
    private static final Map<String, Integer> UnitConversions = new HashMap<>();

    // key = units, value = points to shift right
    static {

        UnitConversions.put("", 0);

        // mass is stored in grams
        UnitConversions.put("g", 0);
        UnitConversions.put("mg", 3);
        UnitConversions.put("\u03bcg", 6);
        UnitConversions.put("ng", 9);
        UnitConversions.put("pg", 12);
        UnitConversions.put("fg", 15);

        // concentrations
        UnitConversions.put("\u0025", 2);
        UnitConversions.put("\u2030", 3);
        UnitConversions.put("ppm", 6);
        UnitConversions.put("ppb", 9);
        UnitConversions.put("ppt", 12);
        UnitConversions.put("g/g", 0);
        UnitConversions.put("*1e5", 5);
        UnitConversions.put("*1", 0);
        UnitConversions.put("dpm/g", 0);
        UnitConversions.put("*1e3 dpm/g", 3);
        UnitConversions.put("*1e6 dpm/g", 6);
        UnitConversions.put("*1e9 dpm/g", 9);

        // dates are stored in years
        UnitConversions.put("a", 0);
        UnitConversions.put("ka", -3);
        UnitConversions.put("Ma", -6);
        UnitConversions.put("Ga", -9);

        // misc in % per amu
        UnitConversions.put("%/amu", 2);

        // time in seconds
        UnitConversions.put("ns", 9);
    }

    /**
     *
     * @param unit
     * @return
     */
    static public int getUnitConversionMoveCount(String unit) {
        return UnitConversions.get(unit);
    }

}
