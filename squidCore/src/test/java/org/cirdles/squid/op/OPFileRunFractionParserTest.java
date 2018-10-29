package org.cirdles.squid.op;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OPFileRunFractionParserTest {
    @Test
    public void testOpFileRunFractionParser() {
        try {

            OPFileRunFractionParser.parseOPFile(new File("src/main/resources/org/cirdles/squid/op/180050_GA6392_18081912.13.op"));

            assertTrue(true);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
