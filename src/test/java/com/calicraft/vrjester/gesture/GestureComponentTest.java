package com.calicraft.vrjester.gesture;

import com.calicraft.vrjester.config.Constants;
import net.minecraft.util.math.vector.Vector3d;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GestureComponentTest {

    @Test
    public void equalsTest(){
        Vector3d dir = new Vector3d((0),(0),(0));
        HashMap<String, Integer> devices = new HashMap<>();
        GestureComponent storedGesture = new GestureComponent(Constants.RC, "forward",
                0, 0.0, dir, devices);
        GestureComponent userGesture = new GestureComponent(Constants.RC, "forward",
                0, 0.0, dir, devices);
        assertEquals(storedGesture, userGesture);

    }

    @Test
    public void matchesTest(){
        Vector3d dir = new Vector3d((0),(0),(0));
        HashMap<String, Integer> devicesInProximity = new HashMap<>(); devicesInProximity.put(Constants.LC, 0);
        GestureComponent storedGesture = new GestureComponent(Constants.RC, "forward",
                0, 0.0, dir, devicesInProximity);
        GestureComponent userGesture = new GestureComponent(Constants.RC, "forward",
                5, 5.0, dir, devicesInProximity);
        assertTrue(storedGesture.matches(userGesture));
    }

}
