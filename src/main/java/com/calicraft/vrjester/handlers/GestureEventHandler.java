package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.api.GestureEvent;
import com.calicraft.vrjester.api.VRPlayerEvent;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.calicraft.vrjester.VrJesterApi.KEY_MAPPINGS;

public class GestureEventHandler {
    // A class for testing received GestureEvents

    @SubscribeEvent
    public void onGestureEvent(GestureEvent event) {
        System.out.println("GESTURE EVENT POSTED & RECEIVED!");
        String gestureName = event.getGestureName();
        String gestureKeyMapping = TriggerEventHandler.config.GESTURE_KEY_MAPPINGS.get(gestureName);
        KeyMapping keyMapping = KEY_MAPPINGS.get(gestureKeyMapping);
        if (keyMapping != null)
            keyMapping.setDown(true);
    }

    @SubscribeEvent
    public void onVRPlayerEvent(VRPlayerEvent event) {
        System.out.println("VRPlayer EVENT POSTED & RECEIVED!");
//        System.out.println(event.getVrDataRoomPre());
//        System.out.println(event.getVrDataWorldPre());
    }
}