package com.calicraft.vrjester.gesture;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.gesture.radix.Trace;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDataWriter;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
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
    public Vox hmdVox, rcVox, lcVox;
    public Vec3[] hmdOrigin, rcOrigin, lcOrigin;
    public List<Vox> voxList = new ArrayList<>();
    public List<Path> hmdGesture = new ArrayList<>();
    public List<Path> rcGesture = new ArrayList<>();
    public List<Path> lcGesture = new ArrayList<>();

    private static int rcParticle, lcParticle;
    private static final SimpleParticleType[] particleTypes = new SimpleParticleType[]{ParticleTypes.FLAME,
            ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.DRAGON_BREATH, ParticleTypes.CLOUD, ParticleTypes.BUBBLE_POP,
            ParticleTypes.FALLING_WATER};
    public final Config config = Config.readConfig(Constants.DEV_CONFIG_PATH);
    private VRDataWriter traceDataWriter;

    public Gesture() {}

    public Gesture(VRDataState vrDataState, int iter) {
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
                    case HMD -> hmdGesture.add(convertToPath(track));
                    case RC  -> rcGesture.add(convertToPath(track));
                    case LC  -> lcGesture.add(convertToPath(track));
                }
            }
        }
        if (rcParticle >= 0)
            rcParticle = -1;
        if (lcParticle >= 0)
            lcParticle = -1;
    }

//    public boolean recognizeTest(VRDataState vrDataWorldPre) {
//        boolean ret = false;
//        for (int i = 0; i < config.GESTURES.length; i++) {
//            Config.SimpleGesture simpleGesture = config.GESTURES[i];
//            if (rcGesture.equals(simpleGesture.rcMovements) && lcGesture.equals(simpleGesture.lcMovements)) {
//                if (trace.rcElapsedTime >= simpleGesture.elapsedTime || trace.lcElapsedTime >= simpleGesture.elapsedTime) {
//                    ret = true; trace.rcMove = trace.lcMove = "";
//                    rcParticle = lcParticle = simpleGesture.particle;
//                    Vec3 avgDir = vrDataWorldPre.getRc()[1].add(vrDataWorldPre.getLc()[1]).multiply((.5), (.5), (.5));
//                    moveParticles(particleTypes[rcParticle],
//                            vrDataWorldPre.getRc()[0],
//                            avgDir,
//                            config.GESTURES[i].velocity
//                    );
//                    moveParticles(particleTypes[lcParticle],
//                            vrDataWorldPre.getLc()[0],
//                            avgDir,
//                            config.GESTURES[i].velocity
//                    );
//                    break;
//                }
//            }
//        }
//        if (!ret) {
//            if (trace.rcMove.equals("forward")) {
//                rcParticle = 0; trace.rcMove = ""; ret = true;
//                Vec3 avgDir = vrDataWorldPre.getRc()[1].add(vrDataWorldPre.getHmd()[1]).multiply((.5), (.5), (.5));
//                moveParticles(particleTypes[rcParticle],
//                        vrDataWorldPre.getRc()[0],
//                        avgDir,
//                        1
//                );
//            }
//            if (trace.lcMove.equals("forward")) {
//                lcParticle = 0; trace.lcMove = ""; ret = true;
//                Vec3 avgDir = vrDataWorldPre.getLc()[1].add(vrDataWorldPre.getHmd()[1]).multiply((.5), (.5), (.5));
//                moveParticles(particleTypes[lcParticle],
//                        vrDataWorldPre.getLc()[0],
//                        avgDir,
//                        1
//                );
//            }
//        }
//        return ret;
//    }

    private Path convertToPath(Track track) {
        return new Path(track.getVrDevice(), track.getMovement(), track.getElapsedTime(), -1,
                track.getSpeed(), -1, track.getDirection(), track.getFaceDirection(),
                track.getDevicesInProximity());
    }

    public static void sendDebugMsg(String msg) {
        LocalPlayer player = getMCI().player;
        Component text = Component.literal(msg);
        assert player != null;
        player.sendSystemMessage(text);
    }
}
