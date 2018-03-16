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
        System.out.println("wtdMeanAcalc 238");
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

        double[][] result = WtdMeanACalc.wtdMeanAcalc(values, oneSigmaPctUnct, noUPbConstAutoReject, pbCanDriftCorr);

        double[][] expResult = new double[][]{{0.008878829024487339, 5.8914250295570144E-5, 1.687608175425885E-5, 3.491041642807011E-5, 5.658307625943268, 3.953271043855011E-11},
        {0.0, 7.0, 8.0, 11.0, 12.0},
        {13.0}};

        assertArrayEquals(expResult[0], result[0], SQUID_EPSILON);
        assertArrayEquals(expResult[1], result[1], SQUID_EPSILON);
        assertArrayEquals(expResult[2], result[2], SQUID_EPSILON);

        System.out.println("wtdMeanAcalc 232");
        values = new double[]{
            0.0131044345958688,
            0.0130045206497664,
            0.0135286049486212,
            0.0130153197041136,
            0.0128510063833588,
            0.0134733885858828,
            0.0134316457807052,
            0.0130829382212723,
            0.0136253150791807,
            0.0130408586752409,
            0.0140483033336357,
            0.0129582037044770,
            0.0131614457793579,
            0.0124175807980006,
            0.0128218132819698,
            0.0133113453061148,
            0.0127901991275686,
            0.0133115494677438,
            0.0123267634856391,
            0.0127937054488144,
            0.0128081280030099

        };

        oneSigmaPctUnct = new double[]{
            4.73263422379419,
            2.96878747344440,
            2.70249557630484,
            3.07262276766305,
            2.89028815997547,
            2.90631023565814,
            2.86412556074729,
            2.98178690055848,
            4.94424224421275,
            5.51694791918178,
            2.72408323321023,
            3.52277366334225,
            2.95144043643686,
            3.02208944342706,
            3.00874735410379,
            2.84434802879348,
            3.17407594622043,
            2.98427002833909,
            3.06609999337721,
            3.04908894148808,
            3.22906820874518
        };

        noUPbConstAutoReject = false;
        pbCanDriftCorr = false;

        result = WtdMeanACalc.wtdMeanAcalc(values, oneSigmaPctUnct, noUPbConstAutoReject, pbCanDriftCorr);

        expResult = new double[][]{{0.01307771811564064, 9.008745163259912E-5,
            9.006943414227261E-5, 1.7657140519989427E-4, 1.0893416768705497, 0.35375414119317605},
        {9.0},
        {}};

        assertArrayEquals(expResult[0], result[0], SQUID_EPSILON);
        assertArrayEquals(expResult[1], result[1], SQUID_EPSILON);
        assertArrayEquals(expResult[2], result[2], SQUID_EPSILON);
    }

}
