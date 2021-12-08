package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.vivecraft.api.VRData;

public class PlaceEventHandler {
    @SubscribeEvent
    public void onPlaceEvent(BlockEvent.EntityPlaceEvent event) {
        String pos;
        VRData vrData = VrJesterApi.TRACKER.getVrData_Vivecraft116();
        if (vrData == null) {
            pos = "null";
        } else {
            pos = vrData.c0.toString();
        }
        ClientPlayerEntity player = Minecraft.getInstance().player;
        ITextComponent text = new StringTextComponent("DEBUG: (Right Controller Position) " + pos);
        assert player != null;
        player.sendMessage(text, player.getUUID());
        System.out.println("PLACE EVENT TRACKER " + pos);
    }
}
