package com.calicraft.vrjester;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.gesture.Gesture;
import com.calicraft.vrjester.gesture.GestureComponent;
import com.calicraft.vrjester.gesture.Gestures;
import com.calicraft.vrjester.gesture.Recognition;
import com.calicraft.vrjester.utils.tools.Vec3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class GestureComponentTest {
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
        GestureComponent gestureComponent1 = new GestureComponent("LC", "forward",
                0, 0.0, dir, devices);
        GestureComponent gestureComponent2 = new GestureComponent("RC", "forward",
                0, 0.0, dir, devices);
        rcGesture.add(gestureComponent2);
        lcGesture.add(gestureComponent1);
        Gesture PushGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        assertEquals("PUSH", recognition.recognize(PushGesture).get("gestureName"));
    }
}