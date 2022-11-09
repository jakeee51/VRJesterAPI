package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.tracker.PositionTracker;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.calicraft.vrjester.VrJesterApi.getMCI;

public class PlayerInitHandler {
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        System.out.println("HANDLE PLAYER LOGIN EVENT");
        LocalPlayer player = getMCI().player;
        assert player != null;
        VrJesterApi.VIVECRAFTLOADED = PositionTracker.vrAPI.playerInVR(player);
    }
}