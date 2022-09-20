package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.utils.vrdata.VRDataAggregator;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDataType;
import com.calicraft.vrjester.utils.vrdata.VRDataWriter;
import com.calicraft.vrjester.vox.Tracer;
import com.calicraft.vrjester.vox.Vox;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import static com.calicraft.vrjester.VrJesterApi.VIVECRAFTLOADED;
import static com.calicraft.vrjester.VrJesterApi.getMCI;
import static com.calicraft.vrjester.utils.tools.SpawnParticles.createParticles;


public class TriggerEventHandler {
    private static JSONObject config;
    private static final VRDataAggregator preRoomDataAggregator = new VRDataAggregator(VRDataType.VRDATA_ROOM_PRE, false);
    private static final VRDataAggregator preWorldDataAggregator = new VRDataAggregator(VRDataType.VRDATA_WORLD_PRE, false);
    private static VRDataWriter vrDataWriter;
    private static VRDataWriter voxDataWriter;
    private static final int DELAY = 20; // 1 second
    private static int sleep = 2 * DELAY; // 2 seconds
    private static boolean listener = false;
    private long elapsed_time = 0;

    private static Vector3d origin, displayOrigin, offset;
    private static Vox activeVox, displayVox;
    private static int[] previousId;
    private static int particle = 0;
    private static final BasicParticleType[] particleTypes = new BasicParticleType[]{ParticleTypes.FLAME,
            ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.DRAGON_BREATH, ParticleTypes.BUBBLE_POP};
    private static String trace = "";
    private static Tracer gestureTrace;
    private static final String[] gestures = new String[]{"[0, 0, 0][1, 0, 0][1, 1, 0]", "[0, 0, 0][-1, 0, 0][-1, 1, 0]",
                                                          "[0, 0, 0][0, 0, 1][0, 1, 1]", "[0, 0, 0][0, 0, -1][0, 1, -1]"};
    private static ClientPlayerEntity player;


    @SubscribeEvent
    public void onJesterTrigger(InputEvent.KeyInputEvent event) {
        if (player == null)
            player = getMCI().player;
        // Trigger the gesture listening phase
        if (VIVECRAFTLOADED) {
            if (VrJesterApi.MOD_KEY.isDown() && !listener) {
                System.out.println("JESTER TRIGGERED");
                listener = true; elapsed_time = System.nanoTime();
                config = new Config().readConfig();
                vrDataWriter = new VRDataWriter(config);
            } else {
                System.out.println("JESTER RELEASED");
                listener = false; elapsed_time = System.nanoTime() - elapsed_time;
                activeVox = null; elapsed_time = 0;
//                JesterRecognition recognizer = new JesterRecognition(preRoomDataAggregator.getData(), elapsed_time);
//                recognizer.isLinearGesture(VRDevice.RC);
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
            if (activeVox == null) {
                System.out.println("PLAYER YAW: " + player.getYHeadRot());
                System.out.println("PLAYER DIRECTION: " + player.getDirection().getName());
                // 0: SOUTH, 180: NORTH, -90: EAST, 90: WEST
                gestureTrace = new Tracer();
                origin = vrDataRoomPre.getRc()[0];
                activeVox = new Vox(origin, player.getYHeadRot(), player.getDirection().getName(), false, gestureTrace);
                previousId = activeVox.getId();
                displayDebugger(vrDataWorldPre, true);
                voxDebugger(previousId, true);
                particle = 0; trace = "[0, 0, 0]";
            } else {
                displayDebugger(vrDataWorldPre, false);
                dataDebugger(vrDataRoomPre);

                int[] currentId = activeVox.generateVox(vrDataRoomPre.getRc()[0]);
                if (!Arrays.equals(previousId, currentId)) { // Update Vox Trace
                    trace += Arrays.toString(currentId);
                    previousId = currentId;
                    System.out.println("TRACE: " + trace);
                    voxDebugger(currentId, false);
                    if (particle < particleTypes.length-2)
                        particle++;
                    else
                        particle = 0;
                } else {
                    createParticles(particleTypes[particle], vrDataWorldPre.getRc());
                }


//                for (int i = 0; i < gestures.length; i++) { // CHECK SINGLE GESTURE (UPPERCUT PUNCH)
//                    if (trace.equals(gestures[i])) {
//                        particle = particleTypes.length - 1; trace = "[0, 0, 0]";
//                        ClientPlayerEntity player = getMCI().player;
//                        ITextComponent text = new StringTextComponent("UPPERCUT RECOGNIZED!");
//                        assert player != null;
//                        player.sendMessage(text, player.getUUID());
//                        break;
//                    }
//                }
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

    public static void voxDebugger(int[] currentId, boolean init) throws IOException { // For Vox Data
        try {
            if (init) {
                if (config.has("WRITE_DATA")) {
                    if (config.getBoolean("WRITE_DATA")) {
                        voxDataWriter = new VRDataWriter();
                        voxDataWriter.write(Arrays.toString(currentId));
                    }
                } else {
                    if (Constants.WRITE_DATA) {
                        voxDataWriter = new VRDataWriter();
                        voxDataWriter.write(Arrays.toString(currentId));
                    }
                }
            } else {
                if (config.has("WRITE_DATA")) {
                    if (config.getBoolean("WRITE_DATA"))
                        voxDataWriter.write(Arrays.toString(currentId));
                } else {
                    if (Constants.WRITE_DATA)
                        voxDataWriter.write(Arrays.toString(currentId));
                }
            }
        } catch (NullPointerException e) {
            System.err.println(e);
        }
    }

    public static void displayDebugger(VRDataState vrDataState, boolean init) { // For VRData World
        try {
            if (init) {
                if (config.has("DISPLAY_VOX")) {
                    if (config.getBoolean("DISPLAY_VOX")) {
                        displayOrigin = vrDataState.getRc()[0];
                        displayVox = new Vox(displayOrigin, player.getYHeadRot(), player.getDirection().getName(), true, null);
                        offset = displayOrigin.subtract(player.position());
                    }
                } else {
                    if (Constants.DISPLAY_VOX) {
                        displayOrigin = vrDataState.getRc()[0];
                        displayVox = new Vox(displayOrigin, player.getYHeadRot(), player.getDirection().getName(), true, null);
                        offset = displayOrigin.subtract(player.position());
                    }
                }
            } else {
                if (config.has("DISPLAY_VOX")) {
                    if (config.getBoolean("DISPLAY_VOX")) {
                        System.out.println("CENTROID 1: " + displayVox.centroid);
                        displayVox.manifestVox(vrDataState.getRc()[0], player.position().add(offset));
                        System.out.println("CENTROID 2: " + displayVox.centroid);
                    }
                } else {
                    displayVox.manifestVox(vrDataState.getRc()[0], player.position().add(offset));
                }
            }
        } catch (NullPointerException e) {
            System.err.println(e);
        }
    }

    public static void dataDebugger(VRDataState vrDataState) throws IOException { // For VRData Room
        try {
            if (config.has("WRITE_DATA")) {
                if (config.getBoolean("WRITE_DATA")) {
                    vrDataWriter.write(vrDataState);
                }
            } else {
                if (Constants.WRITE_DATA) {
                    vrDataWriter.write(vrDataState);
                }
            }
        } catch (NullPointerException e) {
            System.err.println(e);
        }
    }

}
