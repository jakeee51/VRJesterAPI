package com.calicraft.vrjester.gesture;

import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import net.minecraft.util.math.vector.Vector3d;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GestureTraceTest {

    @Test
    void completeTraceTest() {
        Vector3d[] centroidPose = new Vector3d[2]; //initialized array of 2 vectors
        centroidPose[0] = new Vector3d(0,0,1);
        centroidPose[1] = new Vector3d(0,0,1);
        Vector3d[] endPose = new Vector3d[2]; //initialized array of 2 vectors
        endPose[0] = new Vector3d(100,0,1000);
        endPose[1] = new Vector3d(0,0,1).normalize();
        HashMap<String, Integer> gesturesTraced = new HashMap<>();
        Vector3d facingDirection = new Vector3d(1,0,2);
        GestureTrace ges = new GestureTrace("000", VRDevice.RIGHT_CONTROLLER, centroidPose, facingDirection);
        ges.completeTrace(endPose);
        GestureComponent val1 = new GestureComponent(Constants.RC, "forward",
                0, 0.0, centroidPose[1], gesturesTraced);
        GestureComponent val2 = ges.toGestureComponent();
        assertEquals(val1.movement, val2.movement);
        assertEquals(val1.vrDevice, val2.vrDevice);
        assertEquals(val1.direction, val2.direction);

    }
}
