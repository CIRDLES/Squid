/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.squid.algorithms;

import java.math.BigDecimal;


import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Last Updated 1/24/17
 * 
 * @author Griffin
 */
public class BigDecimalCustomAlgorithmsTest
{
    /**
     * Test of bigDecimalSqrtBabylonian method, of class BigDecimalCustomAlgorithms.
     */
    @Test
    public void testBigDecimalSqrtBabylonian()
    {
        System.out.println("bigDecimalSqrtBabylonian");
        BigDecimal S, expResult, result, difference, tolerance;

        //test sqrt(0)
        S = new BigDecimal("0");
        expResult = new BigDecimal("0");
        result = BigDecimalCustomAlgorithms.bigDecimalSqrtBabylonian(S);
        assertEquals("failed on sqrt(0)", expResult, result);

        //test sqrt(9)
        S = new BigDecimal(9);
        expResult = new BigDecimal(3);
        result = BigDecimalCustomAlgorithms.bigDecimalSqrtBabylonian(S);
        assertEquals("failed on sqrt(9)", expResult, result);

        //test sqrt(-1)
        S = new BigDecimal("-1");
        try {
            result = BigDecimalCustomAlgorithms.bigDecimalSqrtBabylonian(S);
            fail("failed on sqrt(-1), no NumberFormatException thrown");
        } catch (java.lang.NumberFormatException e) {}

        //test sqrt(0.467987649584799564673356876567446)
        S = new BigDecimal("0.467987649584799564673356876567446");
        expResult = new BigDecimal("0.6840962283076844772331409047304488");
        result = BigDecimalCustomAlgorithms.bigDecimalSqrtBabylonian(S);
        assertEquals("failed on sqrt(0.467987649584799564673356876567446)", expResult, result);

        //test sqrt(9999999999.9999999999)
        S = new BigDecimal("9999999999.9999999999");
        expResult = new BigDecimal("99999.99999999999999950000000000000");
        result = BigDecimalCustomAlgorithms.bigDecimalSqrtBabylonian(S);
        assertEquals("failed on sqrt(9999999999.9999999999) Ensure rounding mode is HALF_EVEN", expResult, result);

        //test with numbers outside of double's range

        //test sqrt(1E-1000) maybe too robust?
        S = new BigDecimal("1E-1000");
        expResult = new BigDecimal("1E-500");
        result = BigDecimalCustomAlgorithms.bigDecimalSqrtBabylonian(S, 1);
        assertEquals("failed on sqrt(1E-1000)", expResult, result);

        //test sqrt(1E1000) maybe too robust?
        S = new BigDecimal("1E1000");
        expResult = new BigDecimal("1E500");
        result = BigDecimalCustomAlgorithms.bigDecimalSqrtBabylonian(S, 1);
        assertEquals("failed on sqrt(1E1000)", expResult, result);
    }
}