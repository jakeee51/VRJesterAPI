package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.tracker.PositionTracker;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerInitHandler {
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        // Initialize VR PositionTracker ONLY after this event is fired
        System.out.println("LOGIN EVENT FIRED");
        PositionTracker tracker = new PositionTracker();
        VrJesterApi.TRACKER = tracker;
        String pos;
        if (tracker.getVRDataRoomPre() == null)
            pos = "null";
        else
            pos = tracker.vrPlayer.toString();
        System.out.println("LOGIN EVENT TRACKER " + pos);
    }
}