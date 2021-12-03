package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.utils.VRDataAggregator;
import com.calicraft.vrjester.utils.VRDataState;
import com.calicraft.vrjester.utils.VRDataWriter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.util.ArrayList;

public class TriggerEventHandler {
    private static final ArrayList<VRDataState> data = new ArrayList<>();
    private static final VRDataWriter vrDataWriter = new VRDataWriter(
            "VRJester_Data", new String[]{"rc"});
    private static final int DELAY = 20; // 1 second
    private static int sleep = 2 * DELAY; // 2 seconds
    private static boolean listener = false;

    @SubscribeEvent
    public void onJesterTrigger(InputEvent.KeyInputEvent event) {
        // Trigger the gesture listening phase
        if (VrJesterApi.MOD_KEY.isDown() && !listener) {
            System.out.println("JESTER TRIGGERED");
            listener = true;
            ClientPlayerEntity player = Minecraft.getInstance().player;
            ITextComponent text = new StringTextComponent("Listening for gesture...");
            assert player != null;
            player.sendMessage(text, player.getUUID());
        } else { // Trigger the gesture recognition phase
            System.out.println("JESTER RELEASED");
            listener = false;
            VRDataAggregator.send(data); // Fire away if recognized!
            data.clear();
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) throws IOException {
        // Listen for VR data after trigger
        if (listener) { // Capture data in real time
            VRDataState data_state = VRDataAggregator.listen();
            data.add(data_state);
//            vrDataWriter.write(data_state); // Write data to file(s) to debug/analyze
            // TODO - Attempt to recognize gesture after
            //  certain amount of data captured. Stop listening
            //  after being idle for some time
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
