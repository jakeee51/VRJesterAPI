package com.calicraft.vrjester.gesture;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDataWriter;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import com.calicraft.vrjester.gesture.radix.Track;
import com.calicraft.vrjester.gesture.radix.Trace;
import com.calicraft.vrjester.vox.Vox;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.calicraft.vrjester.VrJesterApi.getMCI;
import static com.calicraft.vrjester.utils.tools.SpawnParticles.moveParticles;

public class Gesture {
    // Class that handles compiling the components of a gesture, populating the Tracer object for each VRDevice
    public Vec3[] hmdOrigin, rcOrigin, lcOrigin;
    public Vox hmdVox, rcVox, lcVox;
    public List<Vox> voxList = new ArrayList<>();
    public Trace[] deviceTraces;
    public Trace trace;

    public String rcGesture = "", lcGesture = "";
    private static int rcParticle, lcParticle;
    private static final SimpleParticleType[] particleTypes = new SimpleParticleType[]{ParticleTypes.FLAME,
            ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.DRAGON_BREATH, ParticleTypes.CLOUD, ParticleTypes.BUBBLE_POP,
            ParticleTypes.FALLING_WATER};
    public final Config config = Config.readConfig(Constants.DEV_CONFIG_PATH);
    private VRDataWriter traceDataWriter;

    public Gesture(VRDataState vrDataState, int iter) {
        trace = new Trace(VRDevice.C2);
        deviceTraces = new Trace[]{new Trace(VRDevice.HMD), new Trace(VRDevice.RC), new Trace(VRDevice.LC)};
        hmdOrigin = vrDataState.getHmd(); rcOrigin = vrDataState.getRc(); lcOrigin = vrDataState.getLc();
        hmdVox = new Vox(Constants.HMD, VRDevice.HMD, hmdOrigin, hmdOrigin[1], false);
        rcVox = new Vox(Constants.RC, VRDevice.RC, rcOrigin, hmdOrigin[1], true);
        lcVox = new Vox(Constants.LC, VRDevice.LC, lcOrigin, hmdOrigin[1], true);
        voxList.add(hmdVox); voxList.add(rcVox); voxList.add(lcVox);
        rcParticle = -1; lcParticle = -1;

        if (config.WRITE_DATA)
            traceDataWriter = new VRDataWriter("trace", iter);
    }

    public void track(VRDataState vrDataRoomPre, VRDataState vrDataWorldPre) throws IOException { // Record the Vox trace of each VRDevice and return the resulting data
        for (Vox vox: voxList) { // Loop through each VRDevice's Vox
            Vec3[] currentPoint = vox.generateVox(vrDataRoomPre);
            int[] currentId = vox.getId();
            if (!Arrays.equals(vox.getPreviousId(), currentId)) { // Append new Vox Trace object
                vox.setPreviousId(currentId.clone());
                Track track = vox.getTrace();
                track.completeTrace(currentPoint);
//                System.out.println("BEFORE: " + vox.getName() + ": " + trace.toString());
                vox.beginTrace(currentPoint);
//                System.out.println("AFTER: " + vox.getName() + ": " + vox.getTrace().toString());
                switch (vox.getVrDevice()) {
                    case HMD -> deviceTraces[0].add(track);
                    case RC -> deviceTraces[1].add(track);
                    case LC -> deviceTraces[2].add(track);
                }
                if (vox.getVrDevice() == VRDevice.RC) {
                    trace.rcMove = track.movement;
                    trace.rcElapsedTime += track.getElapsedTime();
                    rcGesture += track.movement;
                    System.out.println("RC GESTURE: " + rcGesture);
                    System.out.println("RC ELAPSED TIME: " + track.getElapsedTime());
                    System.out.println("RC SPEED: " + track.getSpeed());
                    System.out.println("RC DIRECTION: " + track.getDirection());
                    System.out.println("RC PROXIMITY: " + track.getDevicesInProximity());
                }
                if (vox.getVrDevice() == VRDevice.LC) {
                    lcGesture += track.getMovement();
                    trace.lcMove = track.movement;
                }
                if (config.WRITE_DATA)
                    traceDataWriter.write(new String[]{rcGesture, lcGesture});
            }
        }
        if (rcParticle >= 0)
            rcParticle = -1;
        if (lcParticle >= 0)
            lcParticle = -1;
    }

    public boolean recognizeTest(VRDataState vrDataWorldPre) {
        boolean ret = false;
        for (int i = 0; i < config.GESTURES.length; i++) {
            Config.SimpleGesture simpleGesture = config.GESTURES[i];
            if (rcGesture.equals(simpleGesture.rcMovements) && lcGesture.equals(simpleGesture.lcMovements)) {
                if (trace.rcElapsedTime >= simpleGesture.elapsedTime || trace.lcElapsedTime >= simpleGesture.elapsedTime) {
                    ret = true; trace.rcMove = trace.lcMove = "";
                    rcParticle = lcParticle = simpleGesture.particle;
                    Vec3 avgDir = vrDataWorldPre.getRc()[1].add(vrDataWorldPre.getLc()[1]).multiply((.5), (.5), (.5));
                    moveParticles(particleTypes[rcParticle],
                            vrDataWorldPre.getRc()[0],
                            avgDir,
                            config.GESTURES[i].velocity
                    );
                    moveParticles(particleTypes[lcParticle],
                            vrDataWorldPre.getLc()[0],
                            avgDir,
                            config.GESTURES[i].velocity
                    );
                    break;
                }
            }
        }
        if (!ret) {
            if (trace.rcMove.equals("forward")) {
                rcParticle = 0; trace.rcMove = ""; ret = true;
                Vec3 avgDir = vrDataWorldPre.getRc()[1].add(vrDataWorldPre.getHmd()[1]).multiply((.5), (.5), (.5));
                moveParticles(particleTypes[rcParticle],
                        vrDataWorldPre.getRc()[0],
                        avgDir,
                        1
                );
            }
            if (trace.lcMove.equals("forward")) {
                lcParticle = 0; trace.lcMove = ""; ret = true;
                Vec3 avgDir = vrDataWorldPre.getLc()[1].add(vrDataWorldPre.getHmd()[1]).multiply((.5), (.5), (.5));
                moveParticles(particleTypes[lcParticle],
                        vrDataWorldPre.getLc()[0],
                        avgDir,
                        1
                );
            }
        }
        return ret;
    }

    public static void sendDebugMsg(String msg) {
        LocalPlayer player = getMCI().player;
        Component text = Component.literal(msg);
        assert player != null;
        player.sendSystemMessage(text);
    }
}
