package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.utils.vrdata.VRDataAggregator;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDataType;
import com.calicraft.vrjester.utils.vrdata.VRDataWriter;
import com.calicraft.vrjester.vox.Vox;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.calicraft.vrjester.VrJesterApi.VIVECRAFTLOADED;
import static com.calicraft.vrjester.VrJesterApi.getMCI;
import static com.calicraft.vrjester.utils.tools.SpawnParticles.createParticles;
import static com.calicraft.vrjester.utils.vrdata.VRDataType.VRDATA_ROOM_PRE;

public class TriggerEventHandler {
    private static final VRDataAggregator preRoomDataAggregator = new VRDataAggregator(VRDataType.VRDATA_ROOM_PRE, false);
    private static final VRDataAggregator preWorldDataAggregator = new VRDataAggregator(VRDataType.VRDATA_WORLD_PRE, false);
    private static Config config = new Config(Constants.DEV_CONFIG_PATH);
//    private static final VRDataWriter vrDataWriter = new VRDataWriter("VRJester_Data", new String[]{"rc"}, 0);
//    private static VRDataWriter voxDataWriter;
    private static final int DELAY = 20; // 1 second
    private static int sleep = 2 * DELAY; // 2 seconds
    private static boolean listener = false;
    private long elapsed_time = 0;

    private static Vector3d origin;
    private static Vox activeVox;
    private static int[] previousId;
    private static int particle = 0;
    private static final BasicParticleType[] particleTypes = new BasicParticleType[]{ParticleTypes.FLAME,
            ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.DRAGON_BREATH, ParticleTypes.BUBBLE_POP};
    private static final ArrayList<int[]> voxIds = new ArrayList<>();
    private static String trace = "";
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
                listener = true;
                elapsed_time = System.nanoTime();
            } else { // Trigger the gesture recognition phase after key is released
                System.out.println("JESTER RELEASED");
                listener = false;
//                if (voxNet != null) {
//                    ArrayList<Vox> voxList = voxNet.get();
//                    for (Vox vox: voxList) {
//                        // VOX ID: {3, 8 , 26}
//                        System.out.println("VOX ID: " + Arrays.toString(vox.getId()));
//                        for (VRDataState vrDataState : preRoomDataAggregator.getData()) {
//                            System.out.println("VOX: " + vox.centroid);
//                            System.out.println("RC: " + vrDataState.getRc()[0]);
//                            if (vox.hasPoint(vrDataState.getRc()[0]))
//                                createParticles(ParticleTypes.FLAME, vrDataState.getRc());
//                        }
//                    }
//                }
                origin = null; activeVox = null; voxIds.clear();
                elapsed_time = System.nanoTime() - elapsed_time;
//                JesterRecognition recognizer = new JesterRecognition(preRoomDataAggregator.getData(), elapsed_time);
//                recognizer.isLinearGesture(VRDevice.RC);
                preRoomDataAggregator.clear(); elapsed_time = 0;
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

        if (listener) { // Capture VR data in real time after trigger
            VRDataState vrDataRoomPre = preRoomDataAggregator.listen();
            VRDataState vrDataWorldPre = preWorldDataAggregator.listen();
            if (origin == null && activeVox == null) {
                origin = vrDataRoomPre.getRc()[0];
                activeVox = new Vox(origin, false);
                previousId = activeVox.getId();
                particle = 0; trace = "[0, 0, 0]";
                voxIds.add(previousId);
//                voxDataWriter = new VRDataWriter();
//                voxDataWriter.write("[0, 0, 0]");
            } else {
//                vrDataWriter.write(vrDataRoomPre);

                // Note: The getDeltaMovement() initially returns player position before returning the actual delta movement like a sussy baka
//                activeVox.updateVoxPosition(player.getDeltaMovement());
                int[] currentId = activeVox.updateVox(vrDataRoomPre.getRc()[0]);
                if (!Arrays.equals(previousId, currentId)) { // Update Vox Trace
//                    voxDataWriter.write(Arrays.toString(currentId));
                    voxIds.add(currentId);
                    trace += Arrays.toString(currentId);
                    previousId = currentId;
                    if (particle < particleTypes.length-2)
                        particle++;
                    else
                        particle = 0;
                    System.out.println("TRACE: " + trace);
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

//                System.out.println("VOX IDS TRACED:");
//                for (int[] voxId : voxIds) { // Loop through current mapped trace of Vox Id's
//                    System.out.println("--> " + Arrays.toString(voxId));
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
}
