package com.calicraft.vrjester.gestures;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import com.calicraft.vrjester.vox.Trace;
import com.calicraft.vrjester.vox.Tracer;
import com.calicraft.vrjester.vox.Vox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.calicraft.vrjester.utils.tools.SpawnParticles.createParticles;

public class Gesture {
    // Class that represents the compiled trace of each VRDevice
    public Vector3d previousOrigin;
    public Vector3d[] hmdOrigin, rcOrigin, lcOrigin;
    public Vox hmdVox, rcVox, lcVox;
    public List<Vox> voxList = new ArrayList<>();
    public Tracer tracer;
    public Trace[] traceTray = new Trace[3];
    public String rcGesture = "", lcGesture = "";

    private static int rcParticle, lcParticle;
    private static final BasicParticleType[] particleTypes = new BasicParticleType[]{ParticleTypes.FLAME,
            ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.DRAGON_BREATH, ParticleTypes.CLOUD, ParticleTypes.BUBBLE_POP,
            ParticleTypes.FALLING_WATER};
    public final JSONObject config = new Config().readConfig();

    public Gesture(VRDataState vrDataState) {
        tracer = new Tracer();
        previousOrigin = vrDataState.getOrigin();
        hmdOrigin = vrDataState.getHmd(); rcOrigin = vrDataState.getRc(); lcOrigin = vrDataState.getLc();
        hmdVox = new Vox(Constants.HMD, VRDevice.HMD, hmdOrigin, hmdOrigin[1], false);
        rcVox = new Vox(Constants.RC, VRDevice.RC, rcOrigin, hmdOrigin[1], true);
        lcVox = new Vox(Constants.LC, VRDevice.LC, lcOrigin, hmdOrigin[1], false);
        voxList.add(hmdVox); voxList.add(rcVox); voxList.add(lcVox);
        rcParticle = 0; lcParticle = 0;
    }

    public void track(VRDataState vrDataRoomPre, VRDataState vrDataWorldPre) { // Record the Vox trace of each VRDevice and return the resulting data
        for (Vox vox: voxList) { // Loop through each VRDevice's Vox
            Vector3d[] currentPoint = VRDataState.getVRDevicePose(vrDataRoomPre, vox.getVrDevice());
            vox.generateVox(currentPoint);
            int[] currentId = vox.getId();
            if (!Arrays.equals(vox.getPreviousId(), currentId)) { // Append new Vox Trace object
                vox.setPreviousId(currentId.clone());
                Trace trace = vox.getTrace();
                trace.completeTrace(vox.centroid);
                traceTray[vox.getVrDevice().ordinal()] = trace;
//                System.out.println("BEFORE: " + vox.getName() + ": " + trace.toString());
                vox.beginTrace(currentPoint);
//                System.out.println("AFTER: " + vox.getName() + ": " + vox.getTrace().toString());
                if (vox.getVrDevice() == VRDevice.RC) {
                    tracer.rcMove = trace.movement;
                    rcGesture += trace.movement;
                    if (rcParticle < particleTypes.length-2)
                        rcParticle++;
                    else
                        rcParticle = 0;
                }
                if (vox.getVrDevice() == VRDevice.LC) {
                    tracer.lcMove = trace.movement;
                    lcGesture += trace.movement;
                    if (lcParticle < particleTypes.length-2)
                        lcParticle++;
                    else
                        lcParticle = 0;
                }
            }
        }
//        createParticles(particleTypes[rcParticle], VRDataState.getVRDevicePose(vrDataWorldPre, rcVox.getVrDevice(), 0));
        System.out.println("RC MOVE: " + tracer.rcMove);
        System.out.println("RC GESTURE: " + rcGesture);

        if (tracer.rcMove.equals("left") && tracer.lcMove.equals("right")) {
            createParticles(particleTypes[4], VRDataState.getVRDevicePose(vrDataWorldPre, rcVox.getVrDevice(), 0));
            createParticles(particleTypes[4], VRDataState.getVRDevicePose(vrDataWorldPre, lcVox.getVrDevice(), 0));
            sendDebugMsg("SHRINK"); tracer.rcMove = ""; tracer.lcMove = "";
        } else if (tracer.rcMove.equals("right") && tracer.lcMove.equals("left")) {
            createParticles(particleTypes[2], VRDataState.getVRDevicePose(vrDataWorldPre, rcVox.getVrDevice(), 0));
            createParticles(particleTypes[2], VRDataState.getVRDevicePose(vrDataWorldPre, lcVox.getVrDevice(), 0));
            sendDebugMsg("GROW"); tracer.rcMove = ""; tracer.lcMove = "";
        } else if (rcGesture.equals("forwardup") && lcGesture.equals("forwardup")) {
            createParticles(particleTypes[1], VRDataState.getVRDevicePose(vrDataWorldPre, rcVox.getVrDevice(), 0));
            createParticles(particleTypes[1], VRDataState.getVRDevicePose(vrDataWorldPre, lcVox.getVrDevice(), 0));
            sendDebugMsg("RAISE"); rcGesture = ""; lcGesture = ""; tracer.rcMove = ""; tracer.lcMove = "";
        } else if (rcGesture.equals("downback") && lcGesture.equals("downback")) {
            createParticles(particleTypes[5], VRDataState.getVRDevicePose(vrDataWorldPre, rcVox.getVrDevice(), 0));
            createParticles(particleTypes[5], VRDataState.getVRDevicePose(vrDataWorldPre, lcVox.getVrDevice(), 0));
            sendDebugMsg("PULL"); rcGesture = ""; lcGesture = "";
        } else if (rcGesture.equals("upforward") && lcGesture.equals("upforward")) {
            createParticles(particleTypes[3], VRDataState.getVRDevicePose(vrDataWorldPre, rcVox.getVrDevice(), 0));
            createParticles(particleTypes[3], VRDataState.getVRDevicePose(vrDataWorldPre, lcVox.getVrDevice(), 0));
            sendDebugMsg("BLAST"); rcGesture = ""; lcGesture = ""; tracer.rcMove = ""; tracer.lcMove = "";
        } else {
            if (tracer.rcMove.equals("forward")) {
                createParticles(particleTypes[0], VRDataState.getVRDevicePose(vrDataWorldPre, rcVox.getVrDevice(), 0));
                sendDebugMsg("STRIKE"); tracer.rcMove = "";
            }
            if (tracer.lcMove.equals("forward")) {
                createParticles(particleTypes[0], VRDataState.getVRDevicePose(vrDataWorldPre, lcVox.getVrDevice(), 0));
                sendDebugMsg("STRIKE"); tracer.lcMove = "";
            }
            if (tracer.rcMove.equals("down") && tracer.lcMove.equals("down")) {
                createParticles(particleTypes[1], VRDataState.getVRDevicePose(vrDataWorldPre, rcVox.getVrDevice(), 0));
                createParticles(particleTypes[1], VRDataState.getVRDevicePose(vrDataWorldPre, lcVox.getVrDevice(), 0));
                sendDebugMsg("LOWER"); tracer.rcMove = ""; tracer.lcMove = "";
            }
        }
    }

    public static void sendDebugMsg(String msg) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        ITextComponent text = new StringTextComponent(msg);
        assert player != null;
        player.sendMessage(text, player.getUUID());
    }
}
