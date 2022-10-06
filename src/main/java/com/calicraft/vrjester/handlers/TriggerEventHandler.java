package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.gestures.Gesture;
import com.calicraft.vrjester.utils.vrdata.*;
import com.calicraft.vrjester.vox.Vox;
import net.minecraft.client.entity.player.ClientPlayerEntity;
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
    private static int iter = 0;
    private static boolean listener = false;
    private long elapsedTime = 0;

    private static Vector3d offset;
    private static Vox displayRCVox, displayLCVox;
    private static Gesture gesture;
    private static ClientPlayerEntity player;


    @SubscribeEvent
    public void onJesterTrigger(InputEvent.KeyInputEvent event) {
        if (player == null)
            player = getMCI().player;
        // Trigger the gesture listening phase
        if (VIVECRAFTLOADED) {
            if (VrJesterApi.MOD_KEY.isDown() && !listener) {
                System.out.println("JESTER TRIGGERED");
                listener = true; elapsedTime = System.nanoTime();
                config = new Config(Constants.DEV_CONFIG_PATH).readConfig();
                vrDataWriter = new VRDataWriter("room", iter);
                voxDataWriter = new VRDataWriter("vox", iter);
            } else {
                System.out.println("JESTER RELEASED");
                listener = false; elapsedTime = System.nanoTime() - elapsedTime;
                gesture = null; elapsedTime = 0;
                if (config.has("WRITE_DATA"))
                    if (config.getBoolean("WRITE_DATA"))
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
                gesture = new Gesture(vrDataRoomPre, player);
//                voxDebugger(new int[]{0, 0, 0}, true);
                displayRCDebugger(vrDataWorldPre, VRDevice.RC, true);
                displayLCDebugger(vrDataWorldPre, VRDevice.LC, true);
            } else {
                gesture.track(vrDataRoomPre, vrDataWorldPre);
//                voxDebugger(currentId, false);
                displayRCDebugger(vrDataWorldPre, VRDevice.RC, false);
                displayLCDebugger(vrDataWorldPre, VRDevice.LC, false);
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
        Vector3d[] displayOrigin;
        try {
            if (init) {
                if (config.has("DISPLAY_VOX")) {
                    if (config.getBoolean("DISPLAY_VOX")) {
                        displayOrigin = VRDataState.getVRDevicePose(vrDataState, vrDevice);
                        displayRCVox = new Vox(Constants.RC, vrDevice, displayOrigin, player.getYHeadRot(), player.getDirection(), true);
                        offset = displayOrigin[0].subtract(player.position());
                    }
                } else {
                    if (Constants.DISPLAY_VOX) {
                        displayOrigin = VRDataState.getVRDevicePose(vrDataState, vrDevice);
                        displayRCVox = new Vox(Constants.RC, vrDevice, displayOrigin, player.getYHeadRot(), player.getDirection(), true);
                        offset = displayOrigin[0].subtract(player.position());
                    }
                }
            } else {
                if (config.has("DISPLAY_VOX")) {
                    if (config.getBoolean("DISPLAY_VOX"))
                        displayRCVox.manifestVox(VRDataState.getVRDevicePose(vrDataState, vrDevice, 0), player.position().add(offset));
                } else {
                    if (Constants.DISPLAY_VOX)
                        displayRCVox.manifestVox(VRDataState.getVRDevicePose(vrDataState, vrDevice, 0), player.position().add(offset));
                }
            }
        } catch (NullPointerException e) {
            System.err.println(e);
        }
    }

    public static void displayLCDebugger(VRDataState vrDataState, VRDevice vrDevice, boolean init) { // For VRData World
        Vector3d[] displayOrigin;
        try {
            if (init) {
                if (config.has("DISPLAY_VOX")) {
                    if (config.getBoolean("DISPLAY_VOX")) {
                        displayOrigin = VRDataState.getVRDevicePose(vrDataState, vrDevice);
                        displayLCVox = new Vox(Constants.LC, vrDevice, displayOrigin, player.getYHeadRot(), player.getDirection(), true);
                        offset = displayOrigin[0].subtract(player.position());
                    }
                } else {
                    if (Constants.DISPLAY_VOX) {
                        displayOrigin = VRDataState.getVRDevicePose(vrDataState, vrDevice);
                        displayLCVox = new Vox(Constants.LC, vrDevice, displayOrigin, player.getYHeadRot(), player.getDirection(), true);
                        offset = displayOrigin[0].subtract(player.position());
                    }
                }
            } else {
                if (config.has("DISPLAY_VOX")) {
                    if (config.getBoolean("DISPLAY_VOX"))
                        displayLCVox.manifestVox(VRDataState.getVRDevicePose(vrDataState, vrDevice, 0), player.position().add(offset));
                } else {
                    if (Constants.DISPLAY_VOX)
                        displayLCVox.manifestVox(VRDataState.getVRDevicePose(vrDataState, vrDevice, 0), player.position().add(offset));
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

    // TODO - Upgrade voxDebugger to write trace information
    public static void voxDebugger(int[] currentId, boolean init) throws IOException { // For Vox Data
        try {
            if (init) {
                if (config.has("WRITE_DATA")) {
                    if (config.getBoolean("WRITE_DATA")) {
                        voxDataWriter.write(Arrays.toString(currentId));
                    }
                } else {
                    if (Constants.WRITE_DATA) {
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

}
