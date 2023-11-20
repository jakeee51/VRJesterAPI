package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.api.GestureEvent;
import com.calicraft.vrjester.api.GestureEventCallback;
import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.gesture.Gesture;
import com.calicraft.vrjester.gesture.Gestures;
import com.calicraft.vrjester.gesture.recognition.Recognition;
import com.calicraft.vrjester.tracker.PositionTracker;
import com.calicraft.vrjester.utils.demo.TestJester;
import com.calicraft.vrjester.utils.vrdata.VRDataAggregator;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDataType;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.HashMap;

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
    private static boolean listening = false;
    private static boolean nonVrListening = false;
    public static boolean oneRecorded = false;
    private static long elapsedTime = 0;
    private static String previousGesture = "";
    private static Gesture gesture;
    public static final Gestures gestures = new Gestures(config, Constants.GESTURE_STORE_PATH);
    private static final Recognition recognition = new Recognition(gestures);
    private static LocalPlayer player;

    public static void init() {
        ClientRawInputEvent.KEY_PRESSED.register((client, keyCode, scanCode, action, modifiers) -> {
            if (keyCode == MOD_KEY.getDefaultKey().getValue()) {
                if (jesterSetupComplete()) {
                    // Trigger the gesture listening phase
                    if (VIVECRAFT_LOADED)
                        handleVrJester();
                    else
                        handleNonVrJester();
                }
            }
            return EventResult.pass();
        });
        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            gestureListener();
        });
    }

    private static void gestureListener() {
        if (listening) { // Capture VR data in real time after trigger
            vrDataRoomPre = preRoomDataAggregator.listen();
            vrDataWorldPre = preWorldDataAggregator.listen();
            if (gesture == null) { // For initial tick from trigger
                gesture = new Gesture(vrDataRoomPre);
            } else {
                gesture.track(vrDataRoomPre);
            }
            if (config.RECOGNIZE_ON.equals("RECOGNIZE")) { // Recognize gesture within delay interval.
                // If a gesture is recognized, wait for the next interval to see if another gesture is recognized
                HashMap<String, String> recognizedGesture = recognition.recognize(gesture);
                if (sleep != 0) { // Execute every tick
                    if (!recognizedGesture.isEmpty() && !previousGesture.equals(recognizedGesture.get("gestureName"))) {
                        previousGesture = recognizedGesture.get("gestureName");
                        GestureEventCallback.EVENT.invoker().interact(new GestureEvent(recognizedGesture, gesture, vrDataRoomPre, vrDataWorldPre));
                        sleep = DELAY; // Reset ticker to extend listening time
                        limiter = config.MAX_LISTENING_TIME; // Reset limiter
                    }
                } else { // Reset trigger at the end of the delay interval
//                    System.out.println("JESTER DONE LISTENING");
                    sleep = DELAY;
                    if (!recognizedGesture.isEmpty()) { // Final gesture recognition check after delay interval reset
                        GestureEventCallback.EVENT.invoker().interact(new GestureEvent(recognizedGesture, gesture, vrDataRoomPre, vrDataWorldPre));
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
    private static void handleVrJester() {
        if (MOD_KEY.isDown() && !listening) {
//            System.out.println("JESTER TRIGGERED");
            listening = true; elapsedTime = System.nanoTime();
            config = Config.readConfig();
        } else {
//            System.out.println("JESTER RELEASED");
            if (config.RECOGNIZE_ON.equals("RELEASE")) { // Recognize gesture upon releasing
                HashMap<String, String> recognizedGesture = recognition.recognize(gesture);
                if (!recognizedGesture.isEmpty()) {
//                    MinecraftForge.EVENT_BUS.post(new GestureEvent(recognizedGesture, gesture, vrDataRoomPre, vrDataWorldPre));
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
    private static void handleNonVrJester() {
        if (MOD_KEY.isDown() && !nonVrListening) {
            System.out.println("NON-VR JESTER TRIGGERED");
            nonVrListening = true;
        }
        if (!MOD_KEY.isDown() && nonVrListening) {
            System.out.println("JESTER RELEASED");
            nonVrListening = false;
            for (KeyMapping keyMapping: KEY_MAPPINGS.values()) // Release all keys
                keyMapping.setDown(false);
            checkConfig();
        }
    }

    // Clear and reset gesture listener
    private static void stopJesterListener() {
        gesture = null; listening = false;
        previousGesture = "";
        limiter = config.MAX_LISTENING_TIME;
    }

    // Handle and update based on dev configurations
    private static void checkConfig() {
        if (config.READ_DATA)
            gestures.load();
        if (config.RECORD_MODE) {
            if (gesture != null) {
                sendDebugMsg("Storing gesture: \n" + gesture.prettyString());
                gestures.store(gesture, config.GESTURE_NAME);
            } else
                sendDebugMsg("Error: gesture was null!");
        }
        if (config.WRITE_DATA)
            gestures.write();
        if (oneRecorded) {
            oneRecorded = false; config.RECORD_MODE = false;
            Config.writeConfig(config);
            sendDebugMsg("New gesture recorded!");
        }
    }

    // Setup and ensure player is not null and VRData is loaded
    private static boolean jesterSetupComplete() {
        if (player == null) {
            player = getMCI().player; gestures.load();
            VrJesterApi.setupClient();
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
