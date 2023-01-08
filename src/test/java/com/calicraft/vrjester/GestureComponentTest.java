package com.calicraft.vrjester;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.sql.SQLOutput;


class GestureComponentTest {
    @Test
    @DisplayName("0 + 1 = 1")
    void addsTwoNumbers() {
        assertEquals(1, 1);
    }
    @Test
    @DisplayName("0 + 1 = 1")
    void add() {
        int first = 0;
        int second = 1;
        int expectedResult = first + second;
        int Result = 1;
        assertEquals(Result, expectedResult);
        }
}