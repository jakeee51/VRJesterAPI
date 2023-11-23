package com.calicraft.vrjester.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface GestureEventCallback {
    Event<GestureEventCallback> EVENT = EventFactory.createLoop();

    void post(GestureEvent gestureEvent);
}
