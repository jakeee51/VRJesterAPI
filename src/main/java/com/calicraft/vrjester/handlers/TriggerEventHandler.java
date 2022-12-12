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
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
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
    private static Gesture gesture;
    private static final Gestures gestures = new Gestures();
    private static Recognition recognition;
    private static LocalPlayer player;

    private static Vox displayRCVox, displayLCVox;
    private static int rcParticle, lcParticle;
    private static boolean msgSentOnce = false;
    private static final SimpleParticleType[] particleTypes = new SimpleParticleType[]{ParticleTypes.FLAME,
            ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.DRAGON_BREATH, ParticleTypes.CLOUD, ParticleTypes.BUBBLE_POP,
            ParticleTypes.FALLING_WATER};

    @SubscribeEvent
    public void onJesterTrigger(InputEvent event) {
        if (player == null || recognition == null) {
            rcParticle = 0; lcParticle = 0;
            gestures.load();
            recognition = new Recognition(gestures);
            player = getMCI().player;
            if (player == null)
                return;
            try {
                VIVECRAFTLOADED = PositionTracker.vrAPI.playerInVR(player);
            } catch (NullPointerException e) {
                System.out.println("Threw NullPointerException trying to call IVRAPI.playerInVR");
                return;
            }
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
                if (config.RECOGNIZE_ON.equals("RELEASE")) {
                    String recognizedGesture = recognition.recognize(gesture);
                }
                if (config.READ_DATA) {
                    gestures.clear(); gestures.load();
                }
                if (config.RECORD_MODE)
                    gestures.store(gesture, config.LOG.gesture);
                if (config.WRITE_DATA)
                    gestures.write();
                listener = false; elapsedTime = (System.nanoTime() - elapsedTime) / 1000000;
                gesture = null; msgSentOnce = false; elapsedTime = 0;
                if (config.WRITE_DATA)
                    iter++;
                else
                    iter = 0;
            }
        }
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
                if (config.RECOGNIZE_ON.equals("RECOGNIZE")) {
                    String recognizedGesture = recognition.recognize(gesture);
                    if (recognizedGesture.equals("PUSH")) {
                        sendDebugMsg("RECOGNIZED: " + recognizedGesture);
                        Vec3 avgDir = vrDataWorldPre.getRc()[1].add(vrDataWorldPre.getHmd()[1]).multiply((.5), (.5), (.5));
                        moveParticles(particleTypes[rcParticle],
                                vrDataWorldPre.getRc()[0],
                                avgDir,
                                1
                        );
                        moveParticles(particleTypes[lcParticle],
                                vrDataWorldPre.getLc()[0],
                                avgDir,
                                1
                        );
                    }
                }
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

    public static void sendDebugMsg(String msg) {
        if (!msgSentOnce) {
            msgSentOnce = true;
            LocalPlayer player = getMCI().player;
            Component text = Component.literal(msg);
            assert player != null;
            player.sendSystemMessage(text);
        }
    }

    public static void displayRCDebugger(VRDataState vrDataState, VRDevice vrDevice, boolean init) { // For VRData World
        Vec3[] displayOrigin, hmdOrigin = vrDataState.getHmd();
        if (config.DISPLAY_VOX) {
            if (init) {
                displayOrigin = VRDataState.getVRDevicePose(vrDataState, vrDevice);
                displayRCVox = new Vox(vrDevice, displayOrigin, hmdOrigin[1], true);
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
                displayLCVox = new Vox(vrDevice, displayOrigin, hmdOrigin[1], true);
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
