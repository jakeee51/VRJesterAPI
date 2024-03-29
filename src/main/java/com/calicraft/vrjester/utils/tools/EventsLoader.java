package com.calicraft.vrjester.utils.tools;

import com.calicraft.vrjester.handlers.GestureEventHandler;
import com.calicraft.vrjester.handlers.TriggerEventHandler;
import net.minecraftforge.common.MinecraftForge;

public class EventsLoader {
    // Class for registering VR Jester API events

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new TriggerEventHandler());
        MinecraftForge.EVENT_BUS.register(new GestureEventHandler());
//        MinecraftForge.EVENT_BUS.register(new PlayerInitHandler());
    }
}