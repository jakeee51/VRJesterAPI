package com.calicraft.vrjester.vox;

import com.calicraft.vrjester.utils.vrdata.VRDevice;
import com.calicraft.vrjester.vox.Vox;
import org.junit.jupiter.api.Test;
import net.minecraft.world.phys.Vec3;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VoxTest {

    @Test
    public void hasPointTest(){
        Vec3[] centroidPose = new Vec3[2];
        centroidPose[0] = new Vec3(0,0,0);
        centroidPose[1] = new Vec3(0,0,0);
        Vox testVox = new Vox(VRDevice.RC, centroidPose, centroidPose[1], false);
        assertTrue(testVox.hasPoint(centroidPose[0]));

    }
}
