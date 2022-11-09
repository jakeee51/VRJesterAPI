package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.gesture.Gesture;
import com.calicraft.vrjester.tracker.PositionTracker;
import com.calicraft.vrjester.utils.vrdata.*;
import com.calicraft.vrjester.vox.Vox;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.util.Arrays;

import static com.calicraft.vrjester.VrJesterApi.*;
import static com.calicraft.vrjester.utils.tools.SpawnParticles.createParticles;


public class TriggerEventHandler {
    private static Config config = Config.readConfig(Constants.DEV_CONFIG_PATH);
    private static final VRDataAggregator preRoomDataAggregator = new VRDataAggregator(VRDataType.VRDATA_ROOM_PRE, false);
    private static final VRDataAggregator preWorldDataAggregator = new VRDataAggregator(VRDataType.VRDATA_WORLD_PRE, false);
    private static VRDataWriter vrDataWriter;
    private static VRDataWriter voxDataWriter;
    private static final int DELAY = 20; // 1 second
    private static int sleep = 2 * DELAY; // 2 seconds
    private static int iter = 0;
    private static boolean listener = false;
    private long elapsedTime = 0;

    private static Vec3 offset;
    private static Vox displayRCVox, displayLCVox;
    private static Gesture gesture;
    private static LocalPlayer player;
    
    @SubscribeEvent
    public void onJesterTrigger(InputEvent.Key event) {
        if (player == null) {
            player = getMCI().player;
            try {
                VIVECRAFTLOADED = PositionTracker.vrAPI.playerInVR(player);
            } catch (NullPointerException e) {
                System.out.println("Threw NullPointerException trying to call IVRAPI.playerInVR" + e);
            }
        }
        // Trigger the gesture listening phase
        if (VIVECRAFTLOADED) {
            if (VrJesterApi.MOD_KEY.isDown() && !listener) {
                System.out.println("JESTER TRIGGERED");
                listener = true; elapsedTime = System.nanoTime();
                config = Config.readConfig(Constants.DEV_CONFIG_PATH);
                vrDataWriter = new VRDataWriter("room", iter);
                voxDataWriter = new VRDataWriter("vox", iter);
            } else {
                System.out.println("JESTER RELEASED");
                listener = false; elapsedTime = System.nanoTime() - elapsedTime;
                gesture = null; elapsedTime = 0;
                if (config.WRITE_DATA)
                    iter++;
                else
                    iter = 0;
                // Fire event or trigger something based on recognized gesture
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) throws IOException {
        // TODO - Attempt to recognize gesture after
        //  certain amount of data captured or stop listening
        //  after being idle for some time

        if (VrJesterApi.MOD_KEY.isDown() && !VIVECRAFTLOADED)
            createParticles(ParticleTypes.FLAME, null);

        if (listener) { // Capture VR data in real time after trigger
            VRDataState vrDataRoomPre = preRoomDataAggregator.listen();
            VRDataState vrDataWorldPre = preWorldDataAggregator.listen();
            if (gesture == null) {
                gesture = new Gesture(vrDataRoomPre);
//                voxDebugger(new int[]{0, 0, 0}, true);
//                displayRCDebugger(vrDataWorldPre, VRDevice.RC, true);
//                displayLCDebugger(vrDataWorldPre, VRDevice.LC, true);
            } else {
                gesture.track(vrDataRoomPre, vrDataWorldPre);
                gesture.recognizeTest();
//                voxDebugger(currentId, false);
//                displayRCDebugger(vrDataWorldPre, VRDevice.RC, false);
//                displayLCDebugger(vrDataWorldPre, VRDevice.LC, false);
                dataDebugger(vrDataRoomPre);
            }

//            if (sleep % 20 == 0) // Print every 1 second
//                System.out.println("JESTER LISTENING");
//            if (sleep == 0) { // Reset trigger when done
//                System.out.println("JESTER DONE LISTENING");
//                sleep = 2 * DELAY;
//                data.clear(); listener = false;
//            }
//            sleep--;
        }
    }

    public static void displayRCDebugger(VRDataState vrDataState, VRDevice vrDevice, boolean init) { // For VRData World
        Vec3[] displayOrigin, hmdOrigin = vrDataState.getHmd();
        if (config.DISPLAY_VOX) {
            if (init) {
                displayOrigin = VRDataState.getVRDevicePose(vrDataState, vrDevice);
                displayRCVox = new Vox(Constants.RC, vrDevice, displayOrigin, hmdOrigin[1], true);
            } else {
                displayRCVox.manifestVox(VRDataState.getVRDevicePose(vrDataState, vrDevice, 0), player.position().add(displayRCVox.getOffset()));
            }
        }
    }

    public static void displayLCDebugger(VRDataState vrDataState, VRDevice vrDevice, boolean init) { // For VRData World
        Vec3[] displayOrigin, hmdOrigin = vrDataState.getHmd();
        if (config.DISPLAY_VOX) {
            if (init) {
                displayOrigin = VRDataState.getVRDevicePose(vrDataState, vrDevice);
                displayLCVox = new Vox(Constants.LC, vrDevice, displayOrigin, hmdOrigin[1], true);
            } else {
                displayLCVox.manifestVox(VRDataState.getVRDevicePose(vrDataState, vrDevice, 0), player.position().add(displayLCVox.getOffset()));
            }
        }
    }

    public static void dataDebugger(VRDataState vrDataState) throws IOException { // For VRData Room
        if (config.WRITE_DATA)
            vrDataWriter.write(vrDataState);
    }

    public static void voxDebugger(int[] currentId, boolean init) throws IOException { // For Vox Data
        // TODO - Upgrade voxDebugger to write trace information
        if (config.WRITE_DATA)
            voxDataWriter.write(Arrays.toString(currentId));
    }

}
