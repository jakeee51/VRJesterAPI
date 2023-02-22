package com.calicraft.vrjester.gesture;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.gesture.recognition.Recognition;
import com.calicraft.vrjester.utils.tools.Vec3;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class GestureFormsTest {
    private static final Config devConfig = Config.readConfig(Constants.DEV_CONFIG_PATH);
    private static final Gestures gestures = new Gestures(devConfig);
    private static final Recognition recognition = new Recognition(gestures);

    private void checkDevConfig() {
        if (devConfig.READ_DATA) {
            gestures.clear();
            gestures.load();
        }
        if (devConfig.WRITE_DATA)
            gestures.write();
    }

    @Test
    void testStrikeGesture() {
        checkDevConfig();
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 dir = new Vec3((0),(0),(0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent("RC", "forward",
                0, 0.0, dir, devices);
        rcGesture.add(gestureComponent1);
        Gesture strikeGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("STRIKE", recognition.recognize(strikeGesture).get("gestureName"));
    }

    @Test
    void testPushGesture() {
        checkDevConfig();
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 dir = new Vec3((0),(0),(0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent("RC", "forward",
                0, 0.0, dir, devices);
        GestureComponent gestureComponent2 = new GestureComponent("LC", "forward",
                0, 0.0, dir, devices);
        rcGesture.add(gestureComponent1);
        lcGesture.add(gestureComponent2);
        Gesture pushGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("PUSH", recognition.recognize(pushGesture).get("gestureName"));
    }
    @Test
    void testPullGesture() {
        checkDevConfig();
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 dir = new Vec3((0),(0),(0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent("RC", "back",
                0, 0.0, dir, devices);
        GestureComponent gestureComponent2 = new GestureComponent("LC", "back",
                0, 0.0, dir, devices);
        rcGesture.add(gestureComponent1);
        lcGesture.add(gestureComponent2);
        Gesture lowerGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("PULL", recognition.recognize(lowerGesture).get("gestureName"));
    }
    @Test
    void testLowerGesture() {
        checkDevConfig();
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 dir = new Vec3((0),(0),(0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent("RC", "down",
                0, 0.0, dir, devices);
        GestureComponent gestureComponent2 = new GestureComponent("LC", "down",
                0, 0.0, dir, devices);
        rcGesture.add(gestureComponent1);
        lcGesture.add(gestureComponent2);
        Gesture lowerGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("LOWER", recognition.recognize(lowerGesture).get("gestureName"));
    }
    @Test
    void testRaiseGesture() {
        checkDevConfig();
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 dir = new Vec3((0),(0),(0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent("RC", "up",
                0, 0.0, dir, devices);
        GestureComponent gestureComponent2 = new GestureComponent("LC", "up",
                0, 0.0, dir, devices);
        rcGesture.add(gestureComponent1);
        lcGesture.add(gestureComponent2);
        Gesture lowerGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("RAISE", recognition.recognize(lowerGesture).get("gestureName"));
    }
    @Test
    void testGrowGesture() {
        checkDevConfig();
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 dir = new Vec3((0),(0),(0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent("RC", "right",
                0, 0.0, dir, devices);
        GestureComponent gestureComponent2 = new GestureComponent("LC", "left",
                0, 0.0, dir, devices);
        rcGesture.add(gestureComponent1);
        lcGesture.add(gestureComponent2);
        Gesture lowerGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("GROW", recognition.recognize(lowerGesture).get("gestureName"));
    }
    @Test
    void testShrinkGesture() {
        checkDevConfig();
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 dir = new Vec3((0),(0),(0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent("RC", "left",
                0, 0.0, dir, devices);
        GestureComponent gestureComponent2 = new GestureComponent("LC", "right",
                0, 0.0, dir, devices);
        rcGesture.add(gestureComponent1);
        lcGesture.add(gestureComponent2);
        Gesture lowerGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("SHRINK", recognition.recognize(lowerGesture).get("gestureName"));
    }
    @Test
    void testBurstGesture() {
        checkDevConfig();
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 dir = new Vec3((0),(0),(0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent("RC", "forward",
                2000, 0.0, dir, devices);
        GestureComponent gestureComponent2 = new GestureComponent("LC", "forward",
                2000, 0.0, dir, devices);
        rcGesture.add(gestureComponent1);
        lcGesture.add(gestureComponent2);
        Gesture lowerGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("BURST", recognition.recognize(lowerGesture).get("gestureName"));
    }
    @Test
    void testUppercutGesture() {
        checkDevConfig();
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 dir = new Vec3((0),(0),(0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent("RC", "forward",
                0, 0.0, dir, devices);
        GestureComponent gestureComponent2 = new GestureComponent("RC", "up",
                0, 0.0, dir, devices);
        rcGesture.add(gestureComponent1);
        rcGesture.add(gestureComponent2);
        Gesture lowerGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("UPPERCUT", recognition.recognize(lowerGesture).get("gestureName"));
    }


}