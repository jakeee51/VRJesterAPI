package com.calicraft.vrjester;

import com.calicraft.vrjester.handlers.PickupEventHandler;
import com.calicraft.vrjester.handlers.PlaceEventHandler;
import com.calicraft.vrjester.handlers.PlayerInitEvent;
import net.minecraftforge.common.MinecraftForge;

public class EventsLoader {
    // Class for registering VR Jester API events

    public static void register() {
        // Register for relevant vrjester events
        MinecraftForge.EVENT_BUS.register(new PlayerInitEvent());
        MinecraftForge.EVENT_BUS.register(new PickupEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlaceEventHandler());
    }
}
