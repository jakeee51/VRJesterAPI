package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.api.GestureEvent;
import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.config.Test;
import com.calicraft.vrjester.gesture.Gesture;
import com.calicraft.vrjester.gesture.GestureComponent;
import com.calicraft.vrjester.gesture.Gestures;
import com.calicraft.vrjester.gesture.Recognition;
import com.calicraft.vrjester.tracker.PositionTracker;
import com.calicraft.vrjester.utils.vrdata.VRDataAggregator;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDataType;
import com.calicraft.vrjester.utils.vrdata.VRDataWriter;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.calicraft.vrjester.VrJesterApi.VIVECRAFT_LOADED;
import static com.calicraft.vrjester.VrJesterApi.getMCI;
import static com.calicraft.vrjester.utils.tools.SpawnParticles.moveParticles;


public class TriggerEventHandler {
    private static VRDataWriter vrDataWriter;
    private static boolean msgSentOnce = false;
    private static final Test test = new Test();
    private static Config devConfig = Config.readConfig(Constants.DEV_CONFIG_PATH);

    private static final Config config = Config.readConfig(Constants.CONFIG_PATH);
    private static final VRDataAggregator preRoomDataAggregator = new VRDataAggregator(VRDataType.VRDATA_ROOM_PRE, false);
    private static final VRDataAggregator preWorldDataAggregator = new VRDataAggregator(VRDataType.VRDATA_WORLD_PRE, false);
    private static final int DELAY = 20; // 1 second
    private static int sleep = 2 * DELAY; // 2 seconds
    private static int iter = 0;
    private static boolean listener = false;
    private long elapsedTime = 0;
    private static Gesture gesture;
    private static final Gestures gestures = new Gestures(devConfig);
    private static final Recognition recognition = new Recognition(gestures);
    private static LocalPlayer player;

    @SubscribeEvent
    public void onJesterTrigger(InputEvent.Key event) {
        if (event.getKey() == VrJesterApi.MOD_KEY.getKey().getValue()) {
            if (setupJesterComplete()) {
                // Trigger the gesture listening phase
                if (VIVECRAFT_LOADED) {
                    handleVrJester();
                } else {
                    handleNonVrJester();
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (VrJesterApi.MOD_KEY.isDown() && !VIVECRAFT_LOADED)
            moveParticles(ParticleTypes.FLAME, 0);

        if (listener) { // Capture VR data in real time after trigger
            VRDataState vrDataRoomPre = preRoomDataAggregator.listen();
            VRDataState vrDataWorldPre = preWorldDataAggregator.listen();
            if (gesture == null) {
                gesture = new Gesture(vrDataRoomPre);
            } else {
                gesture.track(vrDataRoomPre);
                if (devConfig.RECOGNIZE_ON.equals("RECOGNIZE")) {
                    String recognizedGesture = recognition.recognize(gesture);
                    if (!recognizedGesture.isEmpty()) {
                        MinecraftForge.EVENT_BUS.post(new GestureEvent(player, recognizedGesture, gesture, vrDataRoomPre, vrDataWorldPre));
                        sendDebugMsg("RECOGNIZED: " + recognizedGesture);
                        test.trigger(recognizedGesture, vrDataWorldPre, devConfig);
                        listener = false; gesture = null;
                    }
                }
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

    // Handle VR gesture listener
    private void handleVrJester() {
        if (VrJesterApi.MOD_KEY.isDown() && !listener) {
            System.out.println("JESTER TRIGGERED");
            listener = true; elapsedTime = System.nanoTime();
            devConfig = Config.readConfig(Constants.DEV_CONFIG_PATH);
            if (devConfig.WRITE_DATA)
                vrDataWriter = new VRDataWriter("room", iter);
        } else {
            System.out.println("JESTER RELEASED");
            if (devConfig.RECOGNIZE_ON.equals("RELEASE")) {
                String recognizedGesture = recognition.recognize(gesture);
                if (!recognizedGesture.isEmpty()) {
                    sendDebugMsg("RECOGNIZED: " + recognizedGesture);
                }
            }
            checkDevConfig();
            listener = false; elapsedTime = (System.nanoTime() - elapsedTime) / 1000000;
            gesture = null; msgSentOnce = false; elapsedTime = 0;
        }
    }

    // Handle NON-VR gesture listener
    private void handleNonVrJester() {
        if (VrJesterApi.MOD_KEY.isDown()) {
            System.out.println("NON-VR JESTER TRIGGERED");
        } else {
            System.out.println("JESTER RELEASED");
            checkDevConfig();
            List<GestureComponent> hmdGesture = new ArrayList<>();
            List<GestureComponent> rcGesture = new ArrayList<>();
            List<GestureComponent> lcGesture = new ArrayList<>();
            Vec3 dir = new Vec3((0),(0),(0));
            HashMap<String, Integer> devices = new HashMap<>();
            GestureComponent gestureComponent1 = new GestureComponent("RC", "forward",
                    0, 0.0, dir, devices);
            GestureComponent gestureComponent2 = new GestureComponent("LC", "forward",
                    0, 0.0, dir, devices);
            rcGesture.add(gestureComponent1);
            Gesture strikeGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
            System.out.println("RECOGNIZED: " + recognition.recognize(strikeGesture));
            lcGesture.add(gestureComponent2);
            Gesture pushGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
            System.out.println("RECOGNIZED: " + recognition.recognize(pushGesture));
        }
    }

    // Handle and update based on dev configurations
    private void checkDevConfig() {
        if (devConfig.READ_DATA) {
            gestures.clear();
            gestures.load();
        }
        if (devConfig.RECORD_MODE)
            gestures.store(gesture, devConfig.LOG.gesture);
        if (devConfig.WRITE_DATA)
            gestures.write();
        if (devConfig.WRITE_DATA)
            iter++;
        else
            iter = 0;
    }

    // Setup and ensure player is not null and VRData is loaded
    private boolean setupJesterComplete() {
        if (player == null) {
            player = getMCI().player; gestures.load();
            if (player == null)
                return false;
            try {
                VIVECRAFT_LOADED = PositionTracker.vrAPI.playerInVR(player);
            } catch (NullPointerException e) {
                System.out.println("Threw NullPointerException trying to call IVRAPI.playerInVR");
                return false;
            }
        }
        return true;
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

    public static void dataDebugger(VRDataState vrDataState) throws IOException { // For VRData Room
        if (devConfig.WRITE_DATA)
            vrDataWriter.write(vrDataState);
    }
}
