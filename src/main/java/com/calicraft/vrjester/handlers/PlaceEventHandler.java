package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.vivecraft.api.VRData;
import org.vivecraft.gameplay.VRPlayer;

import static com.calicraft.vrjester.VrJesterApi.getMCI;

public class PlaceEventHandler {
    @SubscribeEvent
    public void onPlaceEvent(BlockEvent.EntityPlaceEvent event) {
        String pos;
        VRData vrData = VrJesterApi.TRACKER.getVRDataRoomPre();
        VRPlayer vrPlayer = VrJesterApi.TRACKER.getVRPlayer();
        if (vrPlayer == null) {
            pos = "null";
        } else {
            pos = vrPlayer.vrdata_world_pre.c0.toString();
        }
        LocalPlayer player = getMCI().player;
        Component text = Component.literal(("DEBUG: (Right Controller Position) " + pos));
        assert player != null;
        player.sendSystemMessage(text);
        System.out.println("PLACE EVENT TRACKER " + vrData.c0);
        System.out.println("VRPLAYER: " + vrPlayer.vrdata_world_pre.c0);
    }
}