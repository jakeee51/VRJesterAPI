package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.api.GestureEvent;
import com.calicraft.vrjester.api.VRPlayerEvent;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.client.KeyMapping;

import static com.calicraft.vrjester.VrJesterApi.KEY_MAPPINGS;

public class GestureEventHandler {
    // A class for testing received GestureEvents

    public void onGestureEvent(GestureEvent event) {
        Event<GestureEvent> EVENT = EventFactory.createEventResult(GestureEvent.class); EVENT.register(event);
        String gestureName = event.getGestureName();
        System.out.println("GESTURE EVENT POSTED & RECEIVED! " + gestureName);
        String gestureKeyMapping = TriggerEventHandler.config.GESTURE_KEY_MAPPINGS.get(gestureName);
        KeyMapping keyMapping = KEY_MAPPINGS.get(gestureKeyMapping);
        if (keyMapping != null)
            keyMapping.setDown(true);
    }

    public void onVRPlayerEvent(VRPlayerEvent event) {
        System.out.println("VRPlayer EVENT POSTED & RECEIVED!");
//        System.out.println(event.getVrDataRoomPre());
//        System.out.println(event.getVrDataWorldPre());
    }
}