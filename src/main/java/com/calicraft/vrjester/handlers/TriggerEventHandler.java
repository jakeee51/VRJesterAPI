package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.utils.VRDataAggregator;
import com.calicraft.vrjester.utils.VRDataState;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class TriggerEventHandler {
    private static final int DELAY = 20; // 1 second
    private static int sleep = 2 * DELAY; // 2 seconds
    private static boolean listener = false;
    private static final ArrayList<VRDataState> data = new ArrayList<VRDataState>();
    private final File file = new File("VRJester_Data.txt");

    @SubscribeEvent
    public void onJesterTrigger(InputEvent.KeyInputEvent event) {
        // Trigger the gesture listening phase
        if (VrJesterApi.MOD_KEY.isDown() && !listener) {
            System.out.println("JESTER TRIGGERED");
            listener = true;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) throws IOException {
        // Listen for a period after trigger
        if (listener) {
//            data.add(VRDataAggregator.listen()); // Capture data real time
            if (file.createNewFile())
                System.out.println("File Created!");
            else
                System.out.println("File not created!");
            try (FileWriter writer = new FileWriter("VRJester_Data.txt")) {
                writer.write("TEST\n");
                writer.flush();
            }
            if (sleep % 20 == 0) // Print every 1 second
                System.out.println("JESTER LISTENING");
            if (sleep == 0) { // Reset trigger when done
                System.out.println("JESTER DONE LISTENING");
                VRDataAggregator.send(data); // Trigger the gesture recognition phase
                sleep = 2 * DELAY; listener = false; data.clear();
            }
            sleep--;
        }
    }
}
