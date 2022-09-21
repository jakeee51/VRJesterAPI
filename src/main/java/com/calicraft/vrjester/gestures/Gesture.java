package com.calicraft.vrjester.gestures;

import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import com.calicraft.vrjester.vox.Trace;
import com.calicraft.vrjester.vox.Vox;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.calicraft.vrjester.utils.tools.SpawnParticles.createParticles;

public class Gesture {
    // Class that represents the compiled trace of each VRDevice
    public Vector3d hmdOrigin, rcOrigin, lcOrigin;
    public Vox hmdVox, rcVox, lcVox;
    public List<Vox> voxList = new ArrayList<>();

    private static int particle = 0;
    private static final BasicParticleType[] particleTypes = new BasicParticleType[]{ParticleTypes.FLAME,
            ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.DRAGON_BREATH, ParticleTypes.BUBBLE_POP};

    public Gesture(VRDataState vrDataRoomPre, ClientPlayerEntity player) {
        hmdOrigin = vrDataRoomPre.getHmd()[0];
        rcOrigin = vrDataRoomPre.getRc()[0];
        lcOrigin = vrDataRoomPre.getLc()[0];
        hmdVox = new Vox(VRDevice.HMD, hmdOrigin, player.getYHeadRot(), player.getDirection().getName(), false);
        rcVox = new Vox(VRDevice.RC, rcOrigin, player.getYHeadRot(), player.getDirection().getName(), false);
        lcVox = new Vox(VRDevice.LC, lcOrigin, player.getYHeadRot(), player.getDirection().getName(), false);
        voxList.add(hmdVox);
        voxList.add(rcVox);
        voxList.add(lcVox);

        particle = 0;
        System.out.println("PLAYER YAW: " + player.getYHeadRot());
        System.out.println("PLAYER DIRECTION: " + player.getDirection().getName());
    }

    public void track(VRDataState vrDataRoomPre, VRDataState vrDataWorldPre) { // Record the Vox trace of each VRDevice and return the resulting data
        for (Vox vox: voxList) {
            Vector3d currentPoint = VRDataState.getVRDevicePose(vrDataRoomPre, vox.getVrDevice(), 0);
            int[] currentId = vox.generateVox(currentPoint); // TODO - Make this return movement direction as well
            if (!Arrays.equals(vox.getPreviousId(), currentId)) { // Update Vox Trace
                vox.setPreviousId(currentId.clone());
                Trace trace = new Trace(Arrays.toString(currentId), null);
                // TODO - Add trace to Tracer respective to the VRDevice
                //      - Tracer should store traces of all devices
                if (particle < particleTypes.length-2)
                    particle++;
                else
                    particle = 0;
            } else {
                createParticles(particleTypes[particle], VRDataState.getVRDevicePose(vrDataWorldPre, vox.getVrDevice(), 0));
            }
        }
    }
}
