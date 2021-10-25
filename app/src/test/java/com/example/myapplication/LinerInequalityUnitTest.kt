package com.example.myapplication

import org.junit.Test
import org.junit.Assert.*

class UnitTest {
    @Test
    fun test1() {
        val linerInequalityForTest = LinerInequality()
        val res = linerInequalityForTest.linerInequality(1.0 , 0.0)
        assertTrue (res==TypeOfLinerInequality.FromMinusBtoAToPlusInfinity)
        assertEquals(res.x, -0.0)
    }

    @Test
    fun test2() {
        val linerInequalityForTest = LinerInequality()
        val res = linerInequalityForTest.linerInequality(-1.0 , 0.0)
        assertTrue (res==TypeOfLinerInequality.FromMinusInfinityToMinusBToA)
        assertEquals(res.x, 0.0)
    }

    @Test
    fun test3() {
        val linerInequalityForTest = LinerInequality()
        val res = linerInequalityForTest.linerInequality(0.0 , 1.0)
        assertTrue (res==TypeOfLinerInequality.XBelongsToEverything)
    }

    @Test
    fun test4() {
        val linerInequalityForTest = LinerInequality()
        val res = linerInequalityForTest.linerInequality(0.0 , 0.0)
        assertTrue (res==TypeOfLinerInequality.NotResult)
    }

    @Test
    fun test5() {
        val linerInequalityForTest = LinerInequality()
        val res = linerInequalityForTest.linerInequality(-100.0 , 10.0)
        assertTrue(res == TypeOfLinerInequality.FromMinusInfinityToMinusBToA)
        assertEquals(res.x, 0.1)
    }
    @Test
    fun test6() {
        val linerInequalityForTest = LinerInequality()
        val res = linerInequalityForTest.linerInequality(100.0 , 10.0)
        assertTrue(res == TypeOfLinerInequality.FromMinusBtoAToPlusInfinity)
        assertEquals(res.x, -0.1)
    }
    @Test
    fun test7() {
        val linerInequalityForTest = LinerInequality()
        val res = linerInequalityForTest.linerInequality(100.0 , 0.0)
        assertTrue(res == TypeOfLinerInequality.FromMinusBtoAToPlusInfinity)
        assertEquals(res.x, -0.0)
    }
    @Test
    fun test8() {
        val linerInequalityForTest = LinerInequality()
        val res = linerInequalityForTest.linerInequality(0.0 , 100.0)
        assertTrue (res==TypeOfLinerInequality.XBelongsToEverything)
    }
}