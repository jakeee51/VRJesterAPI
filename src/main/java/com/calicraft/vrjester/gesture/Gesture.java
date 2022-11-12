package com.calicraft.vrjester.gesture;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import com.calicraft.vrjester.gesture.radix.Trace;
import com.calicraft.vrjester.gesture.radix.Tracer;
import com.calicraft.vrjester.vox.Vox;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.calicraft.vrjester.VrJesterApi.getMCI;
import static com.calicraft.vrjester.utils.tools.SpawnParticles.moveParticles;

public class Gesture {
    // Class that handles compiling the attributes of a gesture utilizing the Tracer class
    public Vec3[] hmdOrigin, rcOrigin, lcOrigin;
    public Vox hmdVox, rcVox, lcVox;
    public List<Vox> voxList = new ArrayList<>();
    public Tracer tracer;

    public Trace[] traceTray = new Trace[3];
    public String rcGesture = "", lcGesture = "";
    private static int rcParticle, lcParticle;
    private static final SimpleParticleType[] particleTypes = new SimpleParticleType[]{ParticleTypes.FLAME,
            ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.DRAGON_BREATH, ParticleTypes.CLOUD, ParticleTypes.BUBBLE_POP,
            ParticleTypes.FALLING_WATER};
    public final Config config = Config.readConfig(Constants.DEV_CONFIG_PATH);

    public Gesture(VRDataState vrDataState) {
        tracer = new Tracer();
        hmdOrigin = vrDataState.getHmd(); rcOrigin = vrDataState.getRc(); lcOrigin = vrDataState.getLc();
        hmdVox = new Vox(Constants.HMD, VRDevice.HMD, hmdOrigin, hmdOrigin[1], false);
        rcVox = new Vox(Constants.RC, VRDevice.RC, rcOrigin, hmdOrigin[1], true);
        lcVox = new Vox(Constants.LC, VRDevice.LC, lcOrigin, hmdOrigin[1], true);
        voxList.add(hmdVox); voxList.add(rcVox); voxList.add(lcVox);
        rcParticle = -1; lcParticle = -1;
    }

    public void track(VRDataState vrDataRoomPre, VRDataState vrDataWorldPre) { // Record the Vox trace of each VRDevice and return the resulting data
        for (Vox vox: voxList) { // Loop through each VRDevice's Vox
            Vec3[] currentPoint = vox.generateVox(vrDataRoomPre);
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
                    tracer.rcElapsedTime += trace.getElapsedTime();
                    tracer.rcSpeed = trace.getSpeed();
                    rcGesture += trace.movement;
                    System.out.println("RC GESTURE: " + rcGesture);
                    System.out.println("RC ELAPSED TIME: " + tracer.rcElapsedTime);
                    System.out.println("RC SPEED: " + tracer.rcSpeed);
                }
                if (vox.getVrDevice() == VRDevice.LC) {
                    tracer.lcMove = trace.movement;
                    tracer.lcElapsedTime += trace.getElapsedTime();
                    tracer.lcSpeed = trace.getSpeed();
                    lcGesture += trace.movement;
                }
            }
        }
        if (rcParticle >= 0) {
            moveParticles(particleTypes[rcParticle],
                    vrDataWorldPre.getRc()[0],
                    vrDataWorldPre.getRc()[1],
                    tracer.rcSpeed
            );
            rcParticle = -1;
        }
        if (lcParticle >= 0) {
            moveParticles(particleTypes[lcParticle],
                    vrDataWorldPre.getLc()[0],
                    vrDataWorldPre.getLc()[1],
                    tracer.lcSpeed
            );
            lcParticle = -1;
        }
    }

    public boolean recognizeTest(VRDataState vrDataWorldPre) {
        boolean ret = false;
        for (int i = 0; i < config.GESTURES.length; i++) {
            Config.SimpleGesture simpleGesture = config.GESTURES[i];
            if (rcGesture.equals(simpleGesture.movements) || lcGesture.equals(simpleGesture.movements)) {
                if (tracer.rcElapsedTime >= simpleGesture.elapsedTime || tracer.lcElapsedTime >= simpleGesture.elapsedTime) {
                    ret = true;
                    rcParticle = lcParticle = 1;
                    tracer.rcMove = tracer.lcMove = "";
                    moveParticles(particleTypes[rcParticle],
                            vrDataWorldPre.getRc()[0],
                            vrDataWorldPre.getRc()[1],
                            config.GESTURES[i].velocity
                    );
                    moveParticles(particleTypes[lcParticle],
                            vrDataWorldPre.getLc()[0],
                            vrDataWorldPre.getLc()[1],
                            config.GESTURES[i].velocity
                    );
                    break;
                }
            }
        }
        if (!ret) {
            if (tracer.rcMove.equals("forward")) {
                rcParticle = 0; tracer.rcMove = ""; ret = true;
                moveParticles(particleTypes[rcParticle],
                        vrDataWorldPre.getRc()[0],
                        vrDataWorldPre.getRc()[1],
                        1
                );
            }
            if (tracer.lcMove.equals("forward")) {
                lcParticle = 0; tracer.lcMove = ""; ret = true;
                moveParticles(particleTypes[lcParticle],
                        vrDataWorldPre.getLc()[0],
                        vrDataWorldPre.getLc()[1],
                        1
                );
            }
        }

//        if (tracer.rcMove.equals("left") && tracer.lcMove.equals("right")) {
//            rcParticle = 4; lcParticle = 4;
//            sendDebugMsg("SHRINK"); tracer.rcMove = ""; tracer.lcMove = "";
//        } else if (tracer.rcMove.equals("right") && tracer.lcMove.equals("left")) {
//            rcParticle = 2; lcParticle = 2;
//            sendDebugMsg("GROW"); tracer.rcMove = ""; tracer.lcMove = "";
//        } else if (rcGesture.equals("forwardup") && lcGesture.equals("forwardup")) {
//            rcParticle = 1; lcParticle = 1;
//            sendDebugMsg("RAISE"); rcGesture = ""; lcGesture = ""; tracer.rcMove = ""; tracer.lcMove = "";
//        } else if (rcGesture.equals("downback") && lcGesture.equals("downback")) {
//            rcParticle = 5; lcParticle = 5;
//            sendDebugMsg("PULL"); rcGesture = ""; lcGesture = "";
//        } else if (rcGesture.equals("upforward") && lcGesture.equals("upforward")) {
//            rcParticle = 3; lcParticle = 3;
//            sendDebugMsg("BLAST"); rcGesture = ""; lcGesture = ""; tracer.rcMove = ""; tracer.lcMove = "";
//        } else {
//            if (tracer.rcMove.equals("forward")) {
//                rcParticle = 0;
//                sendDebugMsg("STRIKE"); tracer.rcMove = "";
//            }
//            if (tracer.lcMove.equals("forward")) {
//                lcParticle = 0;
//                sendDebugMsg("STRIKE"); tracer.lcMove = "";
//            }
//            if (tracer.rcMove.equals("down") && tracer.lcMove.equals("down")) {
//                rcParticle = 1; lcParticle = 1;
//                sendDebugMsg("LOWER"); tracer.rcMove = ""; tracer.lcMove = "";
//            }
//        }
        return ret;
    }

    public static void sendDebugMsg(String msg) {
        LocalPlayer player = getMCI().player;
        Component text = Component.literal(msg);
        assert player != null;
        player.sendSystemMessage(text);
    }
}
