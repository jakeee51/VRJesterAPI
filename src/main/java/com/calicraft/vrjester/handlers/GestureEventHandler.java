package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.api.GestureEvent;
import com.calicraft.vrjester.api.VRPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GestureEventHandler {
    // A class for testing received GestureEvents

    @SubscribeEvent
    public void onGestureEvent(GestureEvent event) {
        System.out.println("GESTURE EVENT POSTED & RECEIVED!");
//        System.out.println(event.getGestureName());
    }

    @SubscribeEvent
    public void onVRPlayerEvent(VRPlayerEvent event) {
        System.out.println("VRPlayer EVENT POSTED & RECEIVED!");
//        System.out.println(event.getVrDataRoomPre());
//        System.out.println(event.getVrDataWorldPre());
    }
}