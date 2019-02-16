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
import static org.cirdles.squid.tasks.expressions.builtinExpressions.BuiltInExpressionsDataDictionary.COR_PREFIX;

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

    public static final String SQUID_LAB_DATA_SERIALIZED_NAME = "SquidLabData.ser";

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

    public enum IndexIsoptopesEnum {
        PB_204("PB_204", "204"),
        PB_207("PB_207", "207"),
        PB_208("PB_208", "208");

        private final String name;
        private final String isotope;

        private IndexIsoptopesEnum(String name, String isotope) {
            this.name = name;
            this.isotope = isotope;
        }

        /**
         * 
         * @return 
         */
        public String getName() {
            return name;
        }

        /**
         * @return the isotope
         */
        public String getIsotope() {
            return isotope;
        }

        public String getIsotopeCorrectionPrefixString() {
            return isotope.substring(2, 3) + COR_PREFIX;
        }
    }

    public enum TaskTypeEnum {
        GEOCHRON("GEOCHRON"),
        GENERAL("GENERAL");

        private final String name;

        private TaskTypeEnum(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

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

    public static enum SampleNameDelimetersEnum {

        HYPHEN("-"),
        DOT("."),
        UNDERSCORE("_"),
        COLON(":"),
        ONE("1"),
        TWO("2"),
        THREE("3"),
        FOUR("4"),
        FIVE("5"),
        SIX("6"),
        SEVEN("7"),
        EIGHT("8"),
        NINE("9");

        private final String name;

        private SampleNameDelimetersEnum(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static String[] names() {
            String[] names = new String[values().length];
            for (int i = 0; i < names.length; i++) {
                names[i] = " " + values()[i].getName();
            }
            return names;
        }

        public static SampleNameDelimetersEnum getByName(String name) {
            SampleNameDelimetersEnum retVal = null;
            for (SampleNameDelimetersEnum delim : SampleNameDelimetersEnum.values()) {
                if (delim.name.equals(name)) {
                    retVal = delim;
                }
            }
            return retVal;
        }
    }

    public final static String SUPERSCRIPT_R_FOR_REFMAT = "ᴿ";//\u1D3F";
    public final static String SUPERSCRIPT_C_FOR_CONCREFMAT = "\u1D9c";
    public final static String SUPERSCRIPT_U_FOR_UNKNOWN = "ᵁ";//\u1D41";
    public final static String SUPERSCRIPT_SPACE = " ";//\u02C9";
    
    // http://science.sciencemag.org/content/335/6076/1610
    public final static double PRESENT_238U235U_DEFAULT = 137.818;

}
