package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.gestures.JesterRecognition;
import com.calicraft.vrjester.utils.vrdata.VRDataAggregator;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;

import static com.calicraft.vrjester.VrJesterApi.VIVECRAFTLOADED;
import static com.calicraft.vrjester.VrJesterApi.getMCI;
import static com.calicraft.vrjester.utils.tools.SpawnParticles.fireball;

public class TriggerEventHandler {
    private static final VRDataAggregator data_aggregator = new VRDataAggregator();
    //    private static final VRDataWriter vrDataWriter = new VRDataWriter(
//            "VRJester_Data", new String[]{"rc"});
    private static final int DELAY = 20; // 1 second
    private static int sleep = 2 * DELAY; // 2 seconds
    private static boolean listener = false;
    private long elapsed_time = 0;

    // TODO - Set maximum listening time

    @SubscribeEvent
    public void onJesterTrigger(InputEvent.KeyInputEvent event) {
        // Trigger the gesture listening phase
        if (VIVECRAFTLOADED) {
            if (VrJesterApi.MOD_KEY.isDown() && !listener) {
                System.out.println("JESTER TRIGGERED");
                listener = true;
                elapsed_time = System.nanoTime();
                ClientPlayerEntity player = getMCI().player;
                ITextComponent text = new StringTextComponent("Listening for gesture...");
                assert player != null;
                player.sendMessage(text, player.getUUID());
            } else { // Trigger the gesture recognition phase
                System.out.println("JESTER RELEASED");
                listener = false;
                elapsed_time = System.nanoTime() - elapsed_time;
                JesterRecognition recognizer = new JesterRecognition(data_aggregator.getData(), elapsed_time);
                recognizer.isLinearGesture(VRDevice.RC);
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
            fireball(ParticleTypes.FLAME, null);
        // Listen for VR data after trigger
        if (listener) { // Capture data in real time
            VRDataState vrDataState = data_aggregator.listen();
            fireball(ParticleTypes.FLAME, vrDataState.getRc());
//            vrDataWriter.write(data_state); // Write data to file(s) to debug/analyze
//            if (sleep % 20 == 0) // Print every 1 second
//                System.out.println("JESTER LISTENING");
//            if (sleep == 0) { // Reset trigger when done
//                System.out.println("JESTER DONE LISTENING");
//                sleep = 2 * DELAY;
//                VRDataAggregator.send(data);
//                data.clear(); listener = false;
//            }
//            sleep--;
        }
    }
}
