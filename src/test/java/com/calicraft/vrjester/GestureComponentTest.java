package com.calicraft.vrjester;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.gesture.Gesture;
import com.calicraft.vrjester.gesture.GestureComponent;
import com.calicraft.vrjester.gesture.Gestures;
import com.calicraft.vrjester.gesture.Recognition;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


class GestureComponentTest {
    private static final Config devConfig = Config.readConfig(Constants.DEV_CONFIG_PATH);
    private static final Gestures gestures = new Gestures(devConfig);
    private static final Recognition recognition = new Recognition(gestures);

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

    @Test
    void testStrikeGesture() {
//        checkDevConfig();
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 dir = new Vec3((0),(0),(0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent("RC", "forward",
                0, 0.0, dir, devices);
        rcGesture.add(gestureComponent1);
        Gesture strikeGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("STRIKE", recognition.recognize(strikeGesture));
    }
}