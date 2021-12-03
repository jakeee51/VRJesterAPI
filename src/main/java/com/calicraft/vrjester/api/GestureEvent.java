package com.calicraft.vrjester.api;

import net.minecraftforge.eventbus.api.Event;

public class GestureEvent extends Event {
    // Cross-reference gesture with gesture_manifest.json file
    // If gesture pattern matches, trigger this event with the
    // gesture ID as context
    public GestureEvent() {
        super();
    }
}
