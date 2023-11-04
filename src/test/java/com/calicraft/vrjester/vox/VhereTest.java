package com.calicraft.vrjester.vox;

import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VhereTest {

    @Test
    public void hasPointTest(){
        Vec3[] centroidPose = new Vec3[2];
        centroidPose[0] = new Vec3(0,0,0);
        centroidPose[1] = new Vec3(0,0,0);
        Vhere testVhere = new Vhere(VRDevice.RIGHT_CONTROLLER, centroidPose, Constants.DEV_CONFIG_PATH);
        Vec3 point = new Vec3(Constants.VIRTUAL_SPHERE_RADIUS, 0, 0);
        assertTrue(testVhere.hasPoint(point));
    }
}
