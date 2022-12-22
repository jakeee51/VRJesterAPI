package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.api.GestureEvent;
import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.config.Test;
import com.calicraft.vrjester.gesture.Gesture;
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
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;

import static com.calicraft.vrjester.VrJesterApi.VIVECRAFT_LOADED;
import static com.calicraft.vrjester.VrJesterApi.getMCI;
import static com.calicraft.vrjester.utils.tools.SpawnParticles.moveParticles;


public class TriggerEventHandler {
    private static Config config = Config.readConfig(Constants.DEV_CONFIG_PATH);
    private static final VRDataAggregator preRoomDataAggregator = new VRDataAggregator(VRDataType.VRDATA_ROOM_PRE, false);
    private static final VRDataAggregator preWorldDataAggregator = new VRDataAggregator(VRDataType.VRDATA_WORLD_PRE, false);
    private static final int DELAY = 20; // 1 second
    private static int sleep = 2 * DELAY; // 2 seconds
    private static int iter = 0;
    private static boolean listener = false;
    private long elapsedTime = 0;
    private static Gesture gesture;
    private static final Gestures gestures = new Gestures();
    private static final Recognition recognition = new Recognition(gestures);
    private static LocalPlayer player;

    private static VRDataWriter vrDataWriter;
    private static boolean msgSentOnce = false;
    private static final Test test = new Test();

    @SubscribeEvent
    public void onJesterTrigger(InputEvent.Key event) {
        // TODO - Apply separation of concerns and break this into methods
        if (event.getKey() == VrJesterApi.MOD_KEY.getKey().getValue()) {
            if (player == null) {
                player = getMCI().player; gestures.load();
                if (player == null)
                    return;
                try {
                    VIVECRAFT_LOADED = PositionTracker.vrAPI.playerInVR(player);
                } catch (NullPointerException e) {
                    System.out.println("Threw NullPointerException trying to call IVRAPI.playerInVR");
                    return;
                }
            }
            // Trigger the gesture listening phase
            if (VIVECRAFT_LOADED) {
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
                        if (!recognizedGesture.isEmpty()) {
                            sendDebugMsg("RECOGNIZED: " + recognizedGesture);
                        }
                    }
                    if (config.READ_DATA) {
                        gestures.clear();
                        gestures.load();
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
            } else {
                if (VrJesterApi.MOD_KEY.isDown()) {
                    System.out.println("NON-VR JESTER TRIGGERED");
                } else {
                    System.out.println("JESTER RELEASED");
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) throws IOException {
        if (VrJesterApi.MOD_KEY.isDown() && !VIVECRAFT_LOADED)
            moveParticles(ParticleTypes.FLAME, 0);

        if (listener) { // Capture VR data in real time after trigger
            VRDataState vrDataRoomPre = preRoomDataAggregator.listen();
            VRDataState vrDataWorldPre = preWorldDataAggregator.listen();
            if (gesture == null) {
                gesture = new Gesture(vrDataRoomPre);
            } else {
                gesture.track(vrDataRoomPre);
                if (config.RECOGNIZE_ON.equals("RECOGNIZE")) {
                    String recognizedGesture = recognition.recognize(gesture);
                    if (!recognizedGesture.isEmpty()) {
                        MinecraftForge.EVENT_BUS.post(new GestureEvent(player, recognizedGesture, gesture, vrDataRoomPre, vrDataWorldPre));
                        sendDebugMsg("RECOGNIZED: " + recognizedGesture);
                        test.trigger(recognizedGesture, vrDataWorldPre, config);
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
        if (config.WRITE_DATA)
            vrDataWriter.write(vrDataState);
    }
}
