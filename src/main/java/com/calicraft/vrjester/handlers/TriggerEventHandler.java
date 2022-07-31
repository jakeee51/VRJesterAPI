package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.gestures.JesterRecognition;
import com.calicraft.vrjester.utils.vrdata.VRDataAggregator;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import com.calicraft.vrjester.vox.Vox;
import com.calicraft.vrjester.vox.VoxNet;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.calicraft.vrjester.VrJesterApi.VIVECRAFTLOADED;
import static com.calicraft.vrjester.VrJesterApi.getMCI;
import static com.calicraft.vrjester.utils.tools.SpawnParticles.createParticles;

public class TriggerEventHandler {
    private static final VRDataAggregator data_aggregator = new VRDataAggregator();
    //    private static final VRDataWriter vrDataWriter = new VRDataWriter(
    //            "VRJester_Data", new String[]{"rc"});
    private static final int DELAY = 20; // 1 second
    private static int sleep = 2 * DELAY; // 2 seconds
    private static boolean listener = false;
    private long elapsed_time = 0;
    private static Vector3d origin;
    private static Vox originVox;
    private static VoxNet voxNet;
    private static int[] previousId;
    private static ArrayList<int[]> voxIds = new ArrayList<>();
    private static ClientPlayerEntity player;

    // TODO - Set maximum listening time

    @SubscribeEvent
    public void onJesterTrigger(InputEvent.KeyInputEvent event) {
        if (player == null)
            player = getMCI().player;
        // Trigger the gesture listening phase
        if (VIVECRAFTLOADED) {
            if (VrJesterApi.MOD_KEY.isDown() && !listener) {
                System.out.println("JESTER TRIGGERED");
                listener = true;
                elapsed_time = System.nanoTime();
//                ClientPlayerEntity player = getMCI().player;
//                ITextComponent text = new StringTextComponent("Listening for gesture...");
//                assert player != null;
//                player.sendMessage(text, player.getUUID());
            } else { // Trigger the gesture recognition phase after key is released
                System.out.println("JESTER RELEASED");
                listener = false;
//                if (voxNet != null) {
//                    ArrayList<Vox> voxList = voxNet.get();
//                    for (Vox vox: voxList) {
//                        // VOX ID: {3, 8 , 26}
//                        System.out.println("VOX ID: " + Arrays.toString(vox.getId()));
//                        for (VRDataState vrDataState : data_aggregator.getData()) {
//                            System.out.println("VOX: " + vox.centroid);
//                            System.out.println("RC: " + vrDataState.getRc()[0]);
//                            if (vox.hasPoint(vrDataState.getRc()[0]))
//                                createParticles(ParticleTypes.FLAME, vrDataState.getRc());
//                        }
//                    }
//                }
                origin = null; originVox = null; voxNet = null; voxIds.clear();
                elapsed_time = System.nanoTime() - elapsed_time;
//                JesterRecognition recognizer = new JesterRecognition(data_aggregator.getData(), elapsed_time);
//                recognizer.isLinearGesture(VRDevice.RC);
                data_aggregator.clear(); elapsed_time = 0;
                // Fire event or trigger something based on recognized gesture
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) throws IOException {
        // TODO - Attempt to recognize gesture after
        //  certain amount of data captured. Stop listening
        //  after being idle for some time

        if (VrJesterApi.MOD_KEY.isDown() && !VIVECRAFTLOADED)
            createParticles(ParticleTypes.FLAME, null);
        // Listen for VR data after trigger
        if (listener) { // Capture data in real time
            VRDataState vrDataState = data_aggregator.listen();
            //            * Single Vox Recognition Working
            if (origin == null && originVox == null) {
                origin = vrDataState.getRc()[0];
                originVox = new Vox(origin);
                previousId = originVox.getId();
            } else {
                // Note: The getDeltaMovement() initially returns player position before returning the actual delta movement like a sussy baka
                originVox.updateVox(player.getDeltaMovement()); // Try hardcoding a set # of vox#.updateVox()
                int[] currentId = originVox.updateVoxId(vrDataState.getRc()[0]);
                System.out.println("PREVIOUS ID: " + Arrays.toString(previousId));
                System.out.println("CURRENT ID: " + Arrays.toString(currentId));
                if (!Arrays.equals(previousId, currentId)) {
                    voxIds.add(currentId); previousId = currentId;
                }
                if (originVox.hasPoint(vrDataState.getRc()[0]))
                    createParticles(ParticleTypes.FLAME, vrDataState.getRc());
                else
                    createParticles(ParticleTypes.SOUL_FIRE_FLAME, vrDataState.getRc());
                System.out.println("VOX IDS TRACED:");
                for (int[] voxId: voxIds) { // Loop through current mapped trace of Vox Id's
                    System.out.println("--> " + Arrays.toString(voxId));
                }
            }



//            if (origin == null && voxNet == null) {
//                origin = vrDataState.getRc()[0];
//                voxNet = new VoxNet(origin);
//            } else {
//                voxNet.updateVoxNet(player.getDeltaMovement()); // Update voxNet's position
//                ArrayList<Vox> voxList = voxNet.get();
//                for (Vox vox: voxList) {
//                    if (VoxNet.compareVector3d(origin, vox.centroid))
//                        continue;
//                    System.out.println("VOX ID: " + Arrays.toString(vox.getId()));
//                    System.out.println("VOX: " + vox.centroid);
//                    System.out.println("RC: " + vrDataState.getRc()[0]);
//                    if (vox.hasPoint(vrDataState.getRc()[0]))
//                        createParticles(ParticleTypes.SOUL_FIRE_FLAME, vrDataState.getRc());
//                }
//            }



//            * Write data to file(s) to debug/analyze
//            vrDataWriter.write(data_state);
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
}
