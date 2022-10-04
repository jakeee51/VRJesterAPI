package com.calicraft.vrjester.gestures;

import com.calicraft.vrjester.config.Constants;
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
import java.util.HashMap;
import java.util.List;

import static com.calicraft.vrjester.utils.tools.SpawnParticles.createParticles;

public class Gesture {
    // Class that represents the compiled trace of each VRDevice
    public Vector3d[] hmdOrigin, rcOrigin, lcOrigin;
    public Vox hmdVox, rcVox, lcVox;
    public List<Vox> voxList = new ArrayList<>();
    public HashMap<VRDevice, Trace> currentTrace = new HashMap<>();

    private static int rcParticle, lcParticle;
    private static final BasicParticleType[] particleTypes = new BasicParticleType[]{ParticleTypes.FLAME,
            ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.DRAGON_BREATH, ParticleTypes.BUBBLE_POP};

    public Gesture(VRDataState vrDataRoomPre, ClientPlayerEntity player) {
        hmdOrigin = vrDataRoomPre.getHmd();
        rcOrigin = vrDataRoomPre.getRc();
        lcOrigin = vrDataRoomPre.getLc();
        hmdVox = new Vox(Constants.HMD, VRDevice.HMD, hmdOrigin, player.getYHeadRot(), player.getDirection(), false);
        rcVox = new Vox(Constants.RC, VRDevice.RC, rcOrigin, player.getYHeadRot(), player.getDirection(), false);
        lcVox = new Vox(Constants.LC, VRDevice.LC, lcOrigin, player.getYHeadRot(), player.getDirection(), false);
        voxList.add(hmdVox);
        voxList.add(rcVox);
        voxList.add(lcVox);
        rcParticle = 0; lcParticle = 0;
        // 0: SOUTH, +-180: NORTH, +-90: EAST, +-09: WEST
//        System.out.println("PLAYER YAW: " + player.getYHeadRot());
//        System.out.println("PLAYER DIRECTION: " + player.getDirection().getName());
    }

    public void track(VRDataState vrDataRoomPre, VRDataState vrDataWorldPre) { // Record the Vox trace of each VRDevice and return the resulting data
        for (Vox vox: voxList) { // Loop through each VRDevice's Vox
            Vector3d[] currentPoint = VRDataState.getVRDevicePose(vrDataRoomPre, vox.getVrDevice());
            vox.generateVox(currentPoint);
            int[] currentId = vox.getId();
            if (!Arrays.equals(vox.getPreviousId(), currentId)) { // Append Vox Trace
                vox.setPreviousId(currentId.clone());
                Trace trace = vox.getTrace();
                trace.completeTrace();
//                System.out.println("BEFORE: " + vox.getName() + ": " + trace.toString());
                vox.beginTrace(currentPoint);
//                System.out.println("AFTER: " + vox.getName() + ": " + vox.getTrace().toString());
                // TODO - Append Trace to Tracer object
                if (vox.getVrDevice() == VRDevice.RC) {
                    if (rcParticle < particleTypes.length-2)
                        rcParticle++;
                    else
                        rcParticle = 0;
                }
                if (vox.getVrDevice() == VRDevice.LC) {
                    if (lcParticle < particleTypes.length-2)
                        lcParticle++;
                    else
                        lcParticle = 0;
                }
            } else {
                if (vox.getVrDevice() != VRDevice.HMD && vox.getVrDevice() == VRDevice.RC)
                    createParticles(particleTypes[rcParticle], VRDataState.getVRDevicePose(vrDataWorldPre, vox.getVrDevice(), 0));
                if (vox.getVrDevice() != VRDevice.HMD && vox.getVrDevice() == VRDevice.LC)
                    createParticles(particleTypes[lcParticle], VRDataState.getVRDevicePose(vrDataWorldPre, vox.getVrDevice(), 0));
            }
        }
    }
}
