package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.tracker.PositionTracker;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerInitEvent {
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        // Initialize VR PositionTracker ONLY after this event is fired
        System.out.println("LOGIN EVENT FIRED");
        PositionTracker tracker = new PositionTracker();
        VrJesterApi.TRACKER = tracker;
        String pos;
        if (tracker.getVrData_Vivecraft116() == null)
            pos = "null";
        else
            pos = tracker.getVrData_Vivecraft116().toString();
        System.out.println("LOGIN EVENT TRACKER " + pos);
    }
}
