package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.utils.vrdata.VRDataAggregator;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDataWriter;
import com.calicraft.vrjester.vox.Vattice;
import com.calicraft.vrjester.vox.Vox;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.particles.BasicParticleType;
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
    private static final VRDataWriter vrDataWriter = new VRDataWriter("VRJester_Data", new String[]{"rc"});
    private static final VRDataWriter voxDataWriter = new VRDataWriter();
    private static final int DELAY = 20; // 1 second
    private static int sleep = 2 * DELAY; // 2 seconds
    private static boolean listener = false;
    private long elapsed_time = 0;
    private static Vector3d origin;
    private static Vattice activeVattice;
    private static int[] previousId;
    private static int particle = 0;
    private static final BasicParticleType[] particleTypes = new BasicParticleType[]{ParticleTypes.FLAME,
            ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.DRAGON_BREATH, ParticleTypes.BUBBLE_POP};
    private static final ArrayList<int[]> voxIds = new ArrayList<>();
    private static String trace = "";
    private static final String[] gestures = new String[]{"[0, 0, 0][1, 0, 0][1, 1, 0]", "[0, 0, 0][-1, 0, 0][-1, 1, 0]",
                                                          "[0, 0, 0][0, 0, 1][0, 1, 1]", "[0, 0, 0][0, 0, -1][0, 1, -1]"};
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
                origin = null; activeVattice = null; voxIds.clear();
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
            if (origin == null && activeVattice == null) {
                origin = vrDataState.getRc()[0];
                activeVattice = new Vattice(origin);
                previousId = activeVattice.getId();
                particle = 0; trace = "[0, 0, 0]";
                voxIds.add(previousId);
                voxDataWriter.write("[0, 0, 0]");
            } else {
                vrDataWriter.write(vrDataState);

                // Note: The getDeltaMovement() initially returns player position before returning the actual delta movement like a sussy baka
                activeVattice.updateVoxPosition(player.getDeltaMovement()); // Try hardcoding a set # of vox#.updateVox()
                int[] currentId = activeVattice.updateVox(vrDataState.getRc()[0]);
                if (!Arrays.equals(previousId, currentId)) { // Update Vox Trace
                    voxDataWriter.write(Arrays.toString(currentId));
                    voxIds.add(currentId);
                    trace += Arrays.toString(currentId);
                    previousId = currentId;
                    if (particle < particleTypes.length-2)
                        particle++;
                    else
                        particle = 0;
                    System.out.println("TRACE: " + trace);
                } else {
                    createParticles(particleTypes[particle], vrDataState.getRc());
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

//                System.out.println("VOX IDS TRACED:");
//                for (int[] voxId : voxIds) { // Loop through current mapped trace of Vox Id's
//                    System.out.println("--> " + Arrays.toString(voxId));
//                }
            }


//            * Write data to file(s) to debug/analyze
//            vrDataWriter.write(vrDataState);
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
