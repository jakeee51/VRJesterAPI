package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.api.GestureEvent;
import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.gesture.Gesture;
import com.calicraft.vrjester.gesture.GestureComponent;
import com.calicraft.vrjester.gesture.Gestures;
import com.calicraft.vrjester.gesture.recognition.Recognition;
import com.calicraft.vrjester.tracker.PositionTracker;
import com.calicraft.vrjester.utils.demo.TestJester;
import com.calicraft.vrjester.utils.tools.Vec3;
import com.calicraft.vrjester.utils.vrdata.VRDataAggregator;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDataType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.calicraft.vrjester.VrJesterApi.*;


public class TriggerEventHandler {
    private static boolean msgSentOnce = false;
    private static final TestJester test = new TestJester();

    public static Config config = Config.readConfig();
    private static VRDataState vrDataRoomPre;
    private static VRDataState vrDataWorldPre;
    private static final VRDataAggregator preRoomDataAggregator = new VRDataAggregator(VRDataType.VRDATA_ROOM_PRE, false);
    private static final VRDataAggregator preWorldDataAggregator = new VRDataAggregator(VRDataType.VRDATA_WORLD_PRE, false);
    private static final int DELAY = config.INTERVAL_DELAY; // 0.75 second (15 ticks)
    private static int sleep = DELAY;
    private static int limiter = config.MAX_LISTENING_TIME; // 10 seconds (400 ticks)
    private static boolean listener = false;
    public static boolean oneRecorded = false;
    private long elapsedTime = 0;
    private static String previousGesture = "";
    private static Gesture gesture;
    public static final Gestures gestures = new Gestures(config, Constants.GESTURE_STORE_PATH);
    private static final Recognition recognition = new Recognition(gestures);
    private static LocalPlayer player;

    @SubscribeEvent
    public void onJesterTrigger(InputEvent.KeyInputEvent event) {
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
        if (listener) { // Capture VR data in real time after trigger
            vrDataRoomPre = preRoomDataAggregator.listen();
            vrDataWorldPre = preWorldDataAggregator.listen();
            if (gesture == null) { // For initial tick from trigger
                gesture = new Gesture(vrDataRoomPre);
            } else {
                gesture.track(vrDataRoomPre);
//                if (config.RECOGNIZE_ON.equals("RECOGNIZE")) { // Recognize gesture right away
//                    HashMap<String, String> recognizedGesture = recognition.recognize(gesture);
//                    if (!recognizedGesture.isEmpty() && !previousGesture.equals(recognizedGesture.get("gestureName"))) {
//                        previousGesture = recognizedGesture.get("gestureName");
//                        MinecraftForge.EVENT_BUS.post(new GestureEvent(player, recognizedGesture, gesture, vrDataRoomPre, vrDataWorldPre));
//                        sendDebugMsg("RECOGNIZED: " + recognizedGesture.get("gestureName"));
//                        test.trigger(recognizedGesture, vrDataWorldPre, config);
//                        stopJesterListener();
//                    }
//                }
            }
            if (config.RECOGNIZE_ON.equals("RECOGNIZE")) { // Recognize gesture within delay interval.
                // If a gesture is recognized, wait for the next interval to see if another gesture is recognized
                HashMap<String, String> recognizedGesture = recognition.recognize(gesture);
                if (sleep != 0) { // Execute every tick
                    if (!recognizedGesture.isEmpty() && !previousGesture.equals(recognizedGesture.get("gestureName"))) {
                        previousGesture = recognizedGesture.get("gestureName");
                        MinecraftForge.EVENT_BUS.post(new GestureEvent(player, recognizedGesture, gesture, vrDataRoomPre, vrDataWorldPre));
                        sleep = DELAY; // Reset ticker to extend listening time
                        limiter = config.MAX_LISTENING_TIME; // Reset limiter
                    }
                } else { // Reset trigger at the end of the delay interval
//                    System.out.println("JESTER DONE LISTENING");
                    sleep = DELAY;
                    if (!recognizedGesture.isEmpty()) { // Final gesture recognition check after delay interval reset
                        MinecraftForge.EVENT_BUS.post(new GestureEvent(player, recognizedGesture, gesture, vrDataRoomPre, vrDataWorldPre));
                        if (config.DEBUG_MODE)
                            sendDebugMsg("RECOGNIZED: " + recognizedGesture.get("gestureName"));
                        if (config.DEMO_MODE)
                            test.trigger(recognizedGesture, vrDataWorldPre, config);
                        limiter = config.MAX_LISTENING_TIME;
                        stopJesterListener();
                    }
                }
                if (limiter == 0)
                    stopJesterListener();
                sleep--;
            }
            limiter--;
        }
    }

    // Handle VR gesture listener
    private void handleVrJester() {
        if (VrJesterApi.MOD_KEY.isDown() && !listener) {
            System.out.println("JESTER TRIGGERED");
            listener = true; elapsedTime = System.nanoTime();
            config = Config.readConfig();
        } else {
            System.out.println("JESTER RELEASED");
            if (config.RECOGNIZE_ON.equals("RELEASE")) { // Recognize gesture upon releasing
                HashMap<String, String> recognizedGesture = recognition.recognize(gesture);
                if (!recognizedGesture.isEmpty()) {
                    MinecraftForge.EVENT_BUS.post(new GestureEvent(player, recognizedGesture, gesture, vrDataRoomPre, vrDataWorldPre));
                    if (config.DEBUG_MODE)
                        sendDebugMsg("RECOGNIZED: " + recognizedGesture.get("gestureName"));
                }
            }
            checkConfig();
            stopJesterListener();
            for (KeyMapping keyMapping: KEY_MAPPINGS.values()) // Release all keys
                keyMapping.setDown(false);
//            elapsedTime = (System.nanoTime() - elapsedTime) / 1000000; // Total time to listen & recognize gesture
            msgSentOnce = false; elapsedTime = 0;
        }
    }

    // Handle Non-VR gesture listener
    private void handleNonVrJester() {
        if (VrJesterApi.MOD_KEY.isDown()) {
            System.out.println("NON-VR JESTER TRIGGERED");
            lcGesture.add(gestureComponent2);
            gesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        } else {
            System.out.println("JESTER RELEASED");
//            checkConfig();
            for (KeyMapping keyMapping: KEY_MAPPINGS.values()) // Release all keys
                keyMapping.setDown(false);
            checkConfig();
//            List<GestureComponent> hmdGesture = new ArrayList<>();
//            List<GestureComponent> rcGesture = new ArrayList<>();
//            List<GestureComponent> lcGesture = new ArrayList<>();
//            Vec3 dir = new Vec3((0),(0),(0));
//            Vec3 dir2 = new Vec3((0),(1),(0));
//            HashMap<String, Integer> devices = new HashMap<>();
//            GestureComponent gestureComponent1 = new GestureComponent(Constants.RC, "forward",
//                    0, 0.0, dir, devices);
//            GestureComponent gestureComponent2 = new GestureComponent(Constants.RC, "up",
//                    0, 0.0, dir, devices);
//            GestureComponent gestureComponent3 = new GestureComponent(Constants.RC, "up",
//                    0, 0.0, dir2, devices);
//            GestureComponent gestureComponent4 = new GestureComponent(Constants.LC, "up",
//                    0, 0.0, dir2, devices);
//            GestureComponent gestureComponent5 = new GestureComponent(Constants.RC, "idle",
//                    301, 0.0, dir2, devices);
//            GestureComponent gestureComponent6 = new GestureComponent(Constants.LC, "idle",
//                    301, 0.0, dir2, devices);
//
//            rcGesture.add(gestureComponent1);
//            Gesture strikeGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
//            System.out.println("RECOGNIZED: " + recognition.recognize(strikeGesture));
//
//            rcGesture.add(gestureComponent2);
//            Gesture uppercutGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
//            System.out.println("RECOGNIZED: " + recognition.recognize(uppercutGesture));
//            rcGesture.clear(); lcGesture.clear();
//            rcGesture.add(gestureComponent3);
//            lcGesture.add(gestureComponent4);
//            Gesture blockGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
//            System.out.println("RECOGNIZED: " + recognition.recognize(blockGesture));
//            rcGesture.clear(); lcGesture.clear();
//
//            rcGesture.add(gestureComponent5);
//            lcGesture.add(gestureComponent6);
//            Gesture idleGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
//            System.out.println("RECOGNIZED: " + recognition.recognize(idleGesture));
        }
    }

    // Clear and reset gesture listener
    private void stopJesterListener() {
        gesture = null; listener = false;
        previousGesture = "";
        limiter = config.MAX_LISTENING_TIME;
    }

    // Handle and update based on dev configurations
    private void checkConfig() {
        if (config.READ_DATA)
            gestures.load();
        if (config.RECORD_MODE) {
            sendDebugMsg("Storing gesture: " + gesture);
            gestures.store(gesture, config.GESTURE_NAME);
        }
        if (config.WRITE_DATA)
            gestures.write();
        if (oneRecorded) {
            oneRecorded = false; config.RECORD_MODE = false;
            sendDebugMsg("New gesture recorded!");
        }
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
//        if (!msgSentOnce) {
        msgSentOnce = true;
        LocalPlayer player = getMCI().player;
        Component text = new TextComponent(msg);
        assert player != null;
        player.sendMessage(text, player.getUUID());
//        }
    }
}
