package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.api.GestureEventCallback;
import net.minecraft.client.KeyMapping;

import static com.calicraft.vrjester.VrJesterApi.KEY_MAPPINGS;

public class GestureEventHandler {
    // A class for testing received GestureEvents

    public static void init() {
        GestureEventCallback.EVENT.register((gestureEvent) -> {
            handleGestureEvent(gestureEvent.getGestureName());
        });
    }

    private static void handleGestureEvent(String gestureName) {
        System.out.println("GESTURE EVENT POSTED & RECEIVED! " + gestureName);
        String gestureKeyMapping = TriggerEventHandler.config.GESTURE_KEY_MAPPINGS.get(gestureName);
        KeyMapping keyMapping = KEY_MAPPINGS.get(gestureKeyMapping);
        if (keyMapping != null)
            keyMapping.setDown(true);
    }
}