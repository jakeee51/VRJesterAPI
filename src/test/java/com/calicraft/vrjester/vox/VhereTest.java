package com.calicraft.vrjester.vox;

import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import net.minecraft.util.math.vector.Vector3d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VhereTest {

    @Test
    public void hasPointTest(){
        Vector3d[] centroidPose = new Vector3d[2];
        centroidPose[0] = new Vector3d(0,0,0);
        centroidPose[1] = new Vector3d(0,0,0);
        Vhere testVhere = new Vhere(VRDevice.RIGHT_CONTROLLER, centroidPose, Constants.DEV_CONFIG_PATH);
        Vector3d point = new Vector3d(Constants.VIRTUAL_SPHERE_RADIUS, 0, 0);
        assertTrue(testVhere.hasPoint(point));
    }
}
