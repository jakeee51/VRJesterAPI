package com.calicraft.vrjester;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CalculatorTests {
    @Test
    @DisplayName("0 + 1 = 1")
    boolean addsTwoNumbers() {
        if (0 + 1 == 1){
            return true;
        }
        return false;
    }
}


