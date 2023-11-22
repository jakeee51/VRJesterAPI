package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.api.GestureEvent;
import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.gesture.Gesture;
import com.calicraft.vrjester.gesture.Gestures;
import com.calicraft.vrjester.gesture.recognition.Recognition;
import com.calicraft.vrjester.utils.vrdata.VRDataAggregator;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDataType;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;

import static com.calicraft.vrjester.VrJesterApi.*;


public class TriggerEventHandler {
    private static boolean msgSentOnce = false;

    public static Config config = Config.readConfig();
    private static VRDataState vrDataRoomPre;
    private static VRDataState vrDataWorldPre;
    private static final VRDataAggregator preRoomDataAggregator = new VRDataAggregator(VRDataType.VRDATA_ROOM_PRE, false);
//    private static final VRDataAggregator preWorldDataAggregator = new VRDataAggregator(VRDataType.VRDATA_WORLD_PRE, false);
    private static final int DELAY = config.INTERVAL_DELAY; // 0.75 second (15 ticks)
    private static int sleep = DELAY;
    private static int limiter = config.MAX_LISTENING_TIME; // 10 seconds (400 ticks)
    private static boolean listener = false;
    private static boolean nonVrListener = false;
    public static boolean oneRecorded = false;
    private long elapsedTime = 0;
    private static String previousGesture = "";
    private static Gesture gesture;
    public static final Gestures gestures = new Gestures(config, Constants.GESTURE_STORE_PATH);
    private static final Recognition recognition = new Recognition(gestures);
    private static ClientPlayerEntity player;

    @SubscribeEvent
    public void onJesterTrigger(InputEvent.KeyInputEvent event) {
        gestures.load();
        if (player == null)
            player = getMCI().player;
        if (event.getKey() == VrJesterApi.MOD_KEY.getKey().getValue()) {
            // Trigger the gesture listening phase
            if (VIVECRAFT_LOADED) {
                handleVrJester();
            } else {
                handleNonVrJester();
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (listener) { // Capture VR data in real time after trigger
            vrDataRoomPre = preRoomDataAggregator.listen();
//            vrDataWorldPre = preWorldDataAggregator.listen();
            if (gesture == null) { // For initial tick from trigger
                gesture = new Gesture(vrDataRoomPre);
            } else {
                gesture.track(vrDataRoomPre);
            }
//            if (config.READ_DATA && !msgSentOnce) {
//                assert getMCI().player != null; msgSentOnce = true;
//                getMCI().player.playSound(DEKU_SMASH, 1.0f, 1.0f);
//            }
            if (config.RECOGNIZE_ON.equals("RECOGNIZE")) { // Recognize gesture within delay interval.
                // If a gesture is recognized, wait for the next interval to see if another gesture is recognized
                HashMap<String, String> recognizedGesture = recognition.recognize(gesture);
                if (sleep != 0) { // Execute every tick
                    if (!recognizedGesture.isEmpty() && !previousGesture.equals(recognizedGesture.get("gestureName"))) {
                        previousGesture = recognizedGesture.get("gestureName");
                        MinecraftForge.EVENT_BUS.post(new GestureEvent(getMCI().player, recognizedGesture, gesture, vrDataRoomPre, vrDataWorldPre));
                        sleep = DELAY; // Reset ticker to extend listening time
                        limiter = config.MAX_LISTENING_TIME; // Reset limiter
                    }
                } else { // Reset trigger at the end of the delay interval
//                    System.out.println("JESTER DONE LISTENING");
                    sleep = DELAY;
                    if (!recognizedGesture.isEmpty()) { // Final gesture recognition check after delay interval reset
                        MinecraftForge.EVENT_BUS.post(new GestureEvent(getMCI().player, recognizedGesture, gesture, vrDataRoomPre, vrDataWorldPre));
                        if (config.DEBUG_MODE)
                            sendDebugMsg("RECOGNIZED: " + recognizedGesture.get("gestureName"));
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
//            System.out.println("JESTER TRIGGERED");
            listener = true; elapsedTime = System.nanoTime();
            config = Config.readConfig();
        } else {
            if (!MOD_KEY.isDown() && listener) {
//                System.out.println("JESTER RELEASED");
                if (config.RECOGNIZE_ON.equals("RELEASE")) { // Recognize gesture upon releasing
                    HashMap<String, String> recognizedGesture = recognition.recognize(gesture);
                    if (!recognizedGesture.isEmpty()) {
                        MinecraftForge.EVENT_BUS.post(new GestureEvent(getMCI().player, recognizedGesture, gesture, vrDataRoomPre, vrDataWorldPre));
                        if (config.DEBUG_MODE)
                            sendDebugMsg("RECOGNIZED: " + recognizedGesture.get("gestureName"));
                    }
                }
                checkConfig();
                stopJesterListener();
                for (KeyBinding keyMapping: KEY_MAPPINGS.values()) // Release all keys
                    MinecraftForge.EVENT_BUS.post(new InputEvent.KeyInputEvent(keyMapping.getKey().getValue(), 0, 0, 0));
//            elapsedTime = (System.nanoTime() - elapsedTime) / 1000000; // Total time to listen & recognize gesture
                msgSentOnce = false; elapsedTime = 0;
            }
        }
    }

    // Handle Non-VR gesture listener
    private void handleNonVrJester() {
        if (MOD_KEY.isDown() && !nonVrListener) {
            System.out.println("NON-VR JESTER TRIGGERED");
            nonVrListener = true;
            config = Config.readConfig();
            KeyBinding km = KEY_MAPPINGS.get(config.GESTURE_NAME);
            if (km != null) {
                MinecraftForge.EVENT_BUS.post(new InputEvent.KeyInputEvent(km.getKey().getValue(), 0, 1, 0));
                MinecraftForge.EVENT_BUS.post(new InputEvent.KeyInputEvent(km.getKey().getValue(), 0, 0, 0));
            }
        }
        if (!MOD_KEY.isDown() && nonVrListener) {
            System.out.println("JESTER RELEASED");
            nonVrListener = false;
            for (KeyBinding keyMapping: KEY_MAPPINGS.values()) // Release all keys
                MinecraftForge.EVENT_BUS.post(new InputEvent.KeyInputEvent(keyMapping.getKey().getValue(), 0, 0, 0));
            checkConfig();
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

    public static void sendDebugMsg(String msg) {
//        if (!msgSentOnce) {
        msgSentOnce = true;
        ClientPlayerEntity player = getMCI().player;
        ITextComponent text = new StringTextComponent(msg);
        assert player != null;
        player.sendMessage(text, player.getUUID());
//        }
    }
}
