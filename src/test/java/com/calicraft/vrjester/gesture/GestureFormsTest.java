package com.calicraft.vrjester.gesture;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.gesture.recognition.Recognition;
import com.calicraft.vrjester.utils.tools.Calcs;
import com.calicraft.vrjester.utils.tools.Vec3;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GestureFormsTest {
    private static final Config devConfig = Config.readConfig(Constants.DEV_CONFIG_PATH);
    private static final Gestures gestures = new Gestures(devConfig);
    private static final Recognition recognition = new Recognition(gestures);

    @BeforeAll
    static void checkDevConfig() {
        gestures.load();
    }

    @Test
    void testStrikeGesture() {
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 dir = new Vec3((0), (0), (0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent(Constants.RC, "forward",
                0, 0.0, dir, devices);
        rcGesture.add(gestureComponent1);
        Gesture strikeGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("STRIKE", recognition.recognize(strikeGesture).get("gestureName"));
    }

    @Test
    void testPushGesture() {
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 dir = new Vec3((0), (0), (0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent(Constants.RC, "forward",
                0, 0.0, dir, devices);
        GestureComponent gestureComponent2 = new GestureComponent(Constants.LC, "forward",
                0, 0.0, dir, devices);
        rcGesture.add(gestureComponent1);
        lcGesture.add(gestureComponent2);
        Gesture pushGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("PUSH", recognition.recognize(pushGesture).get("gestureName"));
    }

    @Test
    void testPullGesture() {
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 dir = new Vec3((0), (0), (0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent(Constants.RC, "back",
                0, 0.0, dir, devices);
        GestureComponent gestureComponent2 = new GestureComponent(Constants.LC, "back",
                0, 0.0, dir, devices);
        rcGesture.add(gestureComponent1);
        lcGesture.add(gestureComponent2);
        Gesture lowerGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("PULL", recognition.recognize(lowerGesture).get("gestureName"));
    }

    @Test
    void testLowerGesture() {
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 dir = new Vec3((0), (0), (0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent(Constants.RC, "down",
                0, 0.0, dir, devices);
        GestureComponent gestureComponent2 = new GestureComponent(Constants.LC, "down",
                0, 0.0, dir, devices);
        rcGesture.add(gestureComponent1);
        lcGesture.add(gestureComponent2);
        Gesture lowerGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("LOWER", recognition.recognize(lowerGesture).get("gestureName"));
    }

    @Test
    void testRaiseGesture() {
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 dir = new Vec3((0), (0), (0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent(Constants.RC, "up",
                0, 0.0, dir, devices);
        GestureComponent gestureComponent2 = new GestureComponent(Constants.LC, "up",
                0, 0.0, dir, devices);
        rcGesture.add(gestureComponent1);
        lcGesture.add(gestureComponent2);
        Gesture lowerGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("RAISE", recognition.recognize(lowerGesture).get("gestureName"));
    }

    @Test
    void testGrowGesture() {
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 dir = new Vec3((0), (0), (0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent(Constants.RC, "right",
                0, 0.0, dir, devices);
        GestureComponent gestureComponent2 = new GestureComponent(Constants.LC, "left",
                0, 0.0, dir, devices);
        rcGesture.add(gestureComponent1);
        lcGesture.add(gestureComponent2);
        Gesture lowerGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("GROW", recognition.recognize(lowerGesture).get("gestureName"));
    }

    @Test
    void testShrinkGesture() {
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 dir = new Vec3((0), (0), (0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent(Constants.RC, "left",
                0, 0.0, dir, devices);
        GestureComponent gestureComponent2 = new GestureComponent(Constants.LC, "right",
                0, 0.0, dir, devices);
        rcGesture.add(gestureComponent1);
        lcGesture.add(gestureComponent2);
        Gesture lowerGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("SHRINK", recognition.recognize(lowerGesture).get("gestureName"));
    }

    @Test
    void testBurstGesture() {
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 dir = new Vec3((0), (0), (0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent(Constants.RC, "forward",
                2000, 0.0, dir, devices);
        GestureComponent gestureComponent2 = new GestureComponent(Constants.LC, "forward",
                2000, 0.0, dir, devices);
        rcGesture.add(gestureComponent1);
        lcGesture.add(gestureComponent2);
        Gesture lowerGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("BURST", recognition.recognize(lowerGesture).get("gestureName"));
    }

    @Test
    void testUppercutGesture() {
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 dir = new Vec3((0), (0), (0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent(Constants.RC, "forward",
                0, 0.0, dir, devices);
        GestureComponent gestureComponent2 = new GestureComponent(Constants.RC, "up",
                0, 0.0, dir, devices);
        rcGesture.add(gestureComponent1);
        rcGesture.add(gestureComponent2);
        Gesture lowerGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("UPPERCUT", recognition.recognize(lowerGesture).get("gestureName"));
    }

    @Test
    void testBlockGesture() {
//        rcGesture: [Path[ RC | movement=up | time=301 | speed=945.38 | direction=(-0.02, 1.00, -0.06)]]
//        lcGesture: [Path[ LC | movement=up | time=301 | speed=967.70 | direction=(0.03, 1.00, 0.07)]]
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 userGestureDirection = new Vec3((-.02), (1), (-.06)).normalize();
        Vec3 userGestureDirection2 = new Vec3((.03), (1), (.07)).normalize();
        Vec3 storedGestureDirection = new Vec3((0), (1), (0)).normalize();
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent(Constants.RC, "up",
                301, 900.0, userGestureDirection, devices);
        GestureComponent gestureComponent2 = new GestureComponent(Constants.LC, "up",
                301, 900.0, userGestureDirection2, devices);
        rcGesture.add(gestureComponent1);
        lcGesture.add(gestureComponent2);
        Gesture blockGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        System.out.println(userGestureDirection + " | " + storedGestureDirection);
        System.out.println(Calcs.getAngle3D(userGestureDirection, storedGestureDirection) + "°");
        assertEquals("BLOCK", recognition.recognize(blockGesture).get("gestureName"));
    }

    @Test
    void testIdleUpGesture() {
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vec3 userGestureDirection = new Vec3((-.02), (1), (-.06)).normalize();
        Vec3 userGestureDirection2 = new Vec3((.03), (1), (.07)).normalize();
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent gestureComponent1 = new GestureComponent(Constants.RC, "idle",
                501, 200.0, userGestureDirection, devices);
        GestureComponent gestureComponent2 = new GestureComponent(Constants.LC, "idle",
                501, 200.0, userGestureDirection2, devices);
        rcGesture.add(gestureComponent1);
        lcGesture.add(gestureComponent2);
        Gesture idleGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("IDLE UP", recognition.recognize(idleGesture).get("gestureName"));
    }

}