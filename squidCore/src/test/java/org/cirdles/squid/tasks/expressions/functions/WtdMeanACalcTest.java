/*
 * Copyright 2018 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid.tasks.expressions.functions;

import java.util.List;
import static org.cirdles.ludwig.squid25.SquidConstants.SQUID_EPSILON;
import org.cirdles.squid.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.squid.tasks.TaskInterface;
import org.cirdles.squid.tasks.expressions.expressionTrees.ExpressionTreeInterface;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class WtdMeanACalcTest {

    public WtdMeanACalcTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of wtdMeanAcalc method, of class WtdMeanACalc.
     */
    @Test
    public void testWtdMeanAcalc() {
        System.out.println("wtdMeanAcalc");
        double[] values = new double[]{
            0.00884012768001511,
            0.00884482434708822,
            0.00894942461822356,
            0.00889112670953696,
            0.00890349557746501,
            0.00883138331975604,
            0.00876125771950047,
            0.00892612565709379,
            0.00889761850769793,
            0.00892993550196493,
            0.00890203514590812,
            0.00905772917568000,
            0.00887281165404440,
            0.00869497835253045,
            0.00878625290110216,
            0.00898639388860170,
            0.00891147007556454,
            0.00895363451243574,
            0.00883845165839725,
            0.00890646409897058,
            0.00879306700888207

        };

        double[] oneSigmaPctUnct = new double[]{
            0.580084916224033,
            0.318530700755801,
            0.304784915850586,
            0.499614559386961,
            0.307217150220516,
            0.314323819438596,
            0.307491828713402,
            0.562739415363749,
            0.578265872862096,
            0.349453015920481,
            0.313567592682458,
            0.564822124409578,
            0.855773017427626,
            0.319535292145000,
            0.317747355283710,
            0.313471282662508,
            0.381403634488646,
            0.315330262602451,
            0.328677825685174,
            0.319268071644238,
            0.335187788342414};
        
        boolean noUPbConstAutoReject = false;
        boolean pbCanDriftCorr = false;
        
        double[] oneSigmaAbsUnct = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            oneSigmaAbsUnct[i] = (oneSigmaPctUnct[i] * values[i]) / 100.0;
        }
        
        double[][] result = WtdMeanACalc.wtdMeanAcalc(values, oneSigmaAbsUnct, noUPbConstAutoReject, pbCanDriftCorr);
        
        double[][] expResult = new double[][]{{0.00887749449587791, 7.522980667607139E-6, 5.658307625943268, 3.953271043855011E-11, 
            1.8553844825270366E-5, 3.8381092164863615E-5, 0.008878829024487339, 5.8914250295570144E-5, 1.687608175425885E-5, 3.491041642807011E-5, 1.0},
            {0.0, 7.0, 8.0, 11.0, 12.0},
            {13.0}};
        
        
        assertArrayEquals(expResult[0], result[0], SQUID_EPSILON);
        assertArrayEquals(expResult[1], result[1], SQUID_EPSILON);
        assertArrayEquals(expResult[2], result[2], SQUID_EPSILON);

    }

}
