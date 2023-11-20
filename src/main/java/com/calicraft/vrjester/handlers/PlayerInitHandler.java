package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.tracker.PositionTracker;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.calicraft.vrjester.VrJesterApi.getMCI;

public class PlayerInitHandler {
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        System.out.println("HANDLE PLAYER LOGIN EVENT");
        ClientPlayerEntity player = getMCI().player;
        assert player != null;
        VrJesterApi.VIVECRAFT_LOADED = PositionTracker.vrAPI.playerInVR(player);
    }
}