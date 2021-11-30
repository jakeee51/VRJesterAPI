package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.utils.VRDataAggregator;
import com.calicraft.vrjester.utils.VRDataState;
import com.calicraft.vrjester.utils.VRDataWriter;
import net.java.games.input.Keyboard;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.util.ArrayList;

public class TriggerEventHandler {
    private static final ArrayList<VRDataState> data = new ArrayList<>();
    private static final VRDataWriter vrDataWriter = new VRDataWriter(
            "VRJester_Data", new String[]{"hmd", "rc"});
    private static final int DELAY = 20; // 1 second
    private static int sleep = 2 * DELAY; // 2 seconds
    private static boolean listener = false;

    @SubscribeEvent
    public void onJesterTrigger(InputEvent.KeyInputEvent event) {
        // Trigger the gesture listening phase
        if (VrJesterApi.MOD_KEY.isDown() && !listener) {
            System.out.println("JESTER TRIGGERED");
            listener = true;
        } else {
            System.out.println("JESTER RELEASED");
            listener = false;
//            VRDataAggregator.send(data); // Trigger the gesture recognition phase
//            data.clear();
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) throws IOException {
        // Listen for VR data after trigger
        if (listener) {
            VRDataState data_state = VRDataAggregator.listen();
//            data.add(data_point); // Capture data real time
            vrDataWriter.write(data_state);
//            if (sleep % 20 == 0) // Print every 1 second
//                System.out.println("JESTER LISTENING");
//            if (sleep == 0) { // Reset trigger when done
//                System.out.println("JESTER DONE LISTENING");
//                sleep = 2 * DELAY;
//                VRDataAggregator.send(data);
//            }
//            sleep--;
        }
    }
}
