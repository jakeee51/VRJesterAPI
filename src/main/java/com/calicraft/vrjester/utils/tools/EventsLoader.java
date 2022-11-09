package com.calicraft.vrjester.utils.tools;

import com.calicraft.vrjester.handlers.PlayerInitHandler;
import com.calicraft.vrjester.handlers.TriggerEventHandler;
import net.minecraftforge.common.MinecraftForge;

public class EventsLoader {
    // Class for registering VR Jester API events

    public static void register() {
        // Register for relevant vrjester events
        MinecraftForge.EVENT_BUS.register(new TriggerEventHandler());
//        MinecraftForge.EVENT_BUS.register(new PlayerInitHandler());
    }
}