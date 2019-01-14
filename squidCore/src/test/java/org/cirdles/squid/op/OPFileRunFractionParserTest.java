package org.cirdles.squid.op;

import org.cirdles.commons.util.ResourceExtractor;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OPFileRunFractionParserTest {
    @Test
    public void testOpFileRunFractionParser() {
        try {
            ResourceExtractor extractor = new ResourceExtractor(OPFile.class);
            File opFile = extractor.extractResourceAsFile("180050_GA6392_18081912.13.op");
            OPFileRunFractionParser.parseOPFile(opFile);
            OPFileHandler.convertOPFileToShrimpFractions(opFile);
            OPFileHandler.convertOPFileToPrawnFile(opFile);

            assertTrue(true);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
