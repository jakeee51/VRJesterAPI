package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.api.GestureEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.calicraft.vrjester.VrJesterApi.KEY_MAPPINGS;

public class GestureEventHandler {
    // A class for testing received GestureEvents

    @SubscribeEvent
    public void onGestureEvent(GestureEvent event) {
        String gestureName = event.getGestureName();
        System.out.println("GESTURE EVENT POSTED & RECEIVED! " + gestureName);
        String gestureKeyBinding = TriggerEventHandler.config.GESTURE_KEY_MAPPINGS.get(gestureName);
        KeyBinding keyMapping = KEY_MAPPINGS.get(gestureKeyBinding);
        if (keyMapping != null)
            MinecraftForge.EVENT_BUS.post(new InputEvent.KeyInputEvent(keyMapping.getKey().getValue(), 0, 1, 0));
    }
}