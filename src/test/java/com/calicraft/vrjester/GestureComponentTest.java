package com.calicraft.vrjester;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;


class GestureComponentTest {
    @Test
    @DisplayName("0 + 1 = 1")
    void addsTwoNumbers() {
        assertEquals(0, 1);
    }
    @ParameterizedTest(name = "{0} + {1} = {2}")
    void add (int first, int second, int expectedResult){
        first = 0;
        second = 1;
        expectedResult = first + second;
        int Result = 2;
        assertEquals(Result, expectedResult);
    }
}