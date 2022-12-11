package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.gesture.Gesture;
import com.calicraft.vrjester.gesture.Gestures;
import com.calicraft.vrjester.gesture.Recognition;
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

import static com.calicraft.vrjester.VrJesterApi.VIVECRAFTLOADED;
import static com.calicraft.vrjester.VrJesterApi.getMCI;
import static com.calicraft.vrjester.utils.tools.SpawnParticles.moveParticles;


public class TriggerEventHandler {
    private static Config config = Config.readConfig(Constants.DEV_CONFIG_PATH);
    private static final VRDataAggregator preRoomDataAggregator = new VRDataAggregator(VRDataType.VRDATA_ROOM_PRE, false);
    private static final VRDataAggregator preWorldDataAggregator = new VRDataAggregator(VRDataType.VRDATA_WORLD_PRE, false);
    private static VRDataWriter vrDataWriter;
    private static final int DELAY = 20; // 1 second
    private static int sleep = 2 * DELAY; // 2 seconds
    private static int iter = 0;
    private static boolean listener = false;
    private long elapsedTime = 0;

    private static Vox displayRCVox, displayLCVox;
    private static Gesture gesture;
    private static final Gestures gestures = new Gestures();
    private static Recognition recognition;
    private static LocalPlayer player;

    @SubscribeEvent
    public void onJesterTrigger(InputEvent event) {
        if (player == null) {
            if (recognition == null) {
                gestures.load();
                recognition = new Recognition(gestures);
            }
            player = getMCI().player;
            try {
                VIVECRAFTLOADED = PositionTracker.vrAPI.playerInVR(player);
            } catch (NullPointerException e) {
                System.out.println("Threw NullPointerException trying to call IVRAPI.playerInVR");
            }
            return;
        }
        // Trigger the gesture listening phase
        if (VIVECRAFTLOADED) {
            if (VrJesterApi.MOD_KEY.isDown() && !listener) {
                System.out.println("JESTER TRIGGERED");
                listener = true; elapsedTime = System.nanoTime();
                config = Config.readConfig(Constants.DEV_CONFIG_PATH);
                if (config.WRITE_DATA)
                    vrDataWriter = new VRDataWriter("room", iter);
            } else {
                System.out.println("JESTER RELEASED");
                if (config.READ_DATA) {
                    gestures.clear(); gestures.load();
                }
                if (config.RECORD_MODE && gesture != null)
                    gestures.store(gesture, config.LOG.gesture);
                if (config.WRITE_DATA)
                    gestures.write();
                listener = false;
                elapsedTime = (System.nanoTime() - elapsedTime) / 1000000;
                gesture = null;
                elapsedTime = 0;
                if (config.WRITE_DATA)
                    iter++;
                else
                    iter = 0;
            }
        }
//        else {
//            if (VrJesterApi.MOD_KEY.isDown() && !listener) {
//                System.out.println("NON VR JESTER TRIGGERED");
//                listener = true;
//                if (config.RECORD_MODE) {
//                    System.out.println("RECORD MODE ACTIVATED");
//                    Gesture gesture0 = new Gesture();
//                    Gesture gesture1 = new Gesture();
//                    Gesture gesture2 = new Gesture();
//                    Map<String, Integer> devices = new HashMap<>(); //devices.put("rc", 0);
//                    Vec3 dir = new Vec3((0), (0), (0));
//                    Path path0 = new Path("hmd", "idle", 0, 200 , 0, 0, dir, dir, devices);
//                    Path path1 = new Path("rc", "f", 0, 100 , 0, 10, dir, dir, devices);
//                    Path path2 = new Path("rc", "u", 0, 0, 0, 0, dir, dir, devices);
//                    Path path3 = new Path("lc", "f", 50, 200, 0, 0, dir, dir, devices);
//                    Path path4 = new Path("lc", "r", 0, 0, 0, 0, dir, dir, devices);
//                    gesture0.hmdGesture.add(path0);
//                    gesture1.rcGesture.add(path1);
//                    gesture1.rcGesture.add(path2);
//                    gesture2.lcGesture.add(path3);
//                    gesture2.lcGesture.add(path4);
//                    gestures.store(gesture0, "GESTURE 1");
//                    gestures.store(gesture1, "GESTURE 2");
//                    gestures.store(gesture2, "GESTURE 3");
//                }
//                if (config.WRITE_DATA)
//                    gestures.load();
//            } else {
//                if (!VrJesterApi.MOD_KEY.isDown() && listener) {
//                    System.out.println("JESTER RELEASED");
//                    listener = false;
//                }
//            }
//        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) throws IOException {
        if (VrJesterApi.MOD_KEY.isDown() && !VIVECRAFTLOADED)
            moveParticles(ParticleTypes.FLAME, 0);

        if (listener) { // Capture VR data in real time after trigger
            VRDataState vrDataRoomPre = preRoomDataAggregator.listen();
            VRDataState vrDataWorldPre = preWorldDataAggregator.listen();
            if (gesture == null) {
                gesture = new Gesture(vrDataRoomPre);
//                displayRCDebugger(vrDataWorldPre, VRDevice.RC, true);
//                displayLCDebugger(vrDataWorldPre, VRDevice.LC, true);
            } else {
                gesture.track(vrDataRoomPre);
                String recognizedGesture = recognition.recognize(gesture);
                System.out.println("recognizedGesture: " + recognizedGesture);
//                displayRCDebugger(vrDataWorldPre, VRDevice.RC, false);
//                displayLCDebugger(vrDataWorldPre, VRDevice.LC, false);
//                dataDebugger(vrDataRoomPre);
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
                displayRCVox.manifestVox(VRDataState.getVRDevicePose(vrDataState, vrDevice, 0));
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
                displayLCVox.manifestVox(VRDataState.getVRDevicePose(vrDataState, vrDevice, 0));
            }
        }
    }

    public static void dataDebugger(VRDataState vrDataState) throws IOException { // For VRData Room
        if (config.WRITE_DATA)
            vrDataWriter.write(vrDataState);
    }
}
