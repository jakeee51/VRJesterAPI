package com.calicraft.vrjester.gesture;
import com.calicraft.vrjester.gesture.GestureComponent;
import com.calicraft.vrjester.utils.tools.Vec3;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GestureComponentTest {
    @Test
    public void equalsTest(){
        Vec3 dir = new Vec3((0),(0),(0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent storedGesture = new GestureComponent("RC", "forward",
                0, 0.0, dir, devices);
        GestureComponent userGesture = new GestureComponent("RC", "forward",
                0, 0.0, dir, devices);
        assertEquals(storedGesture, userGesture);

    }
    @Test
    public void matchesTest(){
        Vec3 dir = new Vec3((0),(0),(0));
        HashMap<String, Integer> devicesInProximity = new HashMap<>(); devicesInProximity.put("LC", 0);
        GestureComponent storedGesture = new GestureComponent("RC", "forward",
                0, 0.0, dir, devicesInProximity);
        GestureComponent userGesture = new GestureComponent("RC", "forward",
                5, 5.0, dir, devicesInProximity);
        assertTrue(storedGesture.matches(userGesture));
    }

}
