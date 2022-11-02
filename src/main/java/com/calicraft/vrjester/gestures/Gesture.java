package com.calicraft.vrjester.gestures;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import com.calicraft.vrjester.vox.Trace;
import com.calicraft.vrjester.vox.Vox;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.calicraft.vrjester.utils.tools.SpawnParticles.createParticles;

public class Gesture {
    // Class that represents the compiled trace of each VRDevice
    public Vector3d currentOrigin, previousOrigin;
    public Vector3d[] hmdOrigin, rcOrigin, lcOrigin;
    public Vox hmdVox, rcVox, lcVox;
    public List<Vox> voxList = new ArrayList<>();
    public HashMap<VRDevice, Trace> currentTrace = new HashMap<>();

    private static int rcParticle, lcParticle;
    private static final BasicParticleType[] particleTypes = new BasicParticleType[]{ParticleTypes.FLAME,
            ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.DRAGON_BREATH, ParticleTypes.BUBBLE_POP};
    public final JSONObject config = new Config().readConfig();

    public Gesture(VRDataState vrDataState, ClientPlayerEntity player) {
        previousOrigin = vrDataState.getOrigin();
        hmdOrigin = vrDataState.getHmd(); rcOrigin = vrDataState.getRc(); lcOrigin = vrDataState.getLc();
        hmdVox = new Vox(Constants.HMD, VRDevice.HMD, hmdOrigin, hmdOrigin[1], hmdOrigin[0].subtract(previousOrigin), false);
        rcVox = new Vox(Constants.RC, VRDevice.RC, rcOrigin, hmdOrigin[1], rcOrigin[0].subtract(previousOrigin), false);
        lcVox = new Vox(Constants.LC, VRDevice.LC, lcOrigin, hmdOrigin[1], lcOrigin[0].subtract(previousOrigin), false);
        voxList.add(hmdVox); voxList.add(rcVox); voxList.add(lcVox);
        rcParticle = 0; lcParticle = 0;
    }

    public void track(VRDataState vrDataRoomPre, VRDataState vrDataWorldPre) { // Record the Vox trace of each VRDevice and return the resulting data
        for (Vox vox: voxList) { // Loop through each VRDevice's Vox
            Vector3d[] currentPoint = VRDataState.getVRDevicePose(vrDataRoomPre, vox.getVrDevice());
            currentOrigin = vrDataWorldPre.getOrigin();
            Vector3d delta = new Vector3d((0), (0), (0));
            if (!previousOrigin.equals(currentOrigin)) {
                delta = previousOrigin.subtract(currentOrigin);
//                System.out.println("DELTA: " + delta);
            }
            vox.generateVox(currentPoint);
            int[] currentId = vox.getId();
            if (!Arrays.equals(vox.getPreviousId(), currentId)) { // Append new Vox Trace object
                vox.setPreviousId(currentId.clone());
                Trace trace = vox.getTrace();
                trace.completeTrace(vox.centroid);
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
