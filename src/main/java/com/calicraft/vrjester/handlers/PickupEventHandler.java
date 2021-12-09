package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.vivecraft.api.VRData;

import static com.calicraft.vrjester.VrJesterApi.getMCI;

public class PickupEventHandler {
    @SubscribeEvent
    public void onPickUpEvent(EntityItemPickupEvent event) {
        String pos;
        VRData vrData = VrJesterApi.TRACKER.getVRData();
        if (vrData == null) {
            pos = "null";
        } else {
            pos = vrData.c0.toString();
        }
        ClientPlayerEntity player = getMCI().player;
        ITextComponent text = new StringTextComponent("DEBUG: (Right Controller Position) " + pos);
        assert player != null;
        player.sendMessage(text, player.getUUID());
        System.out.println("PICKUP EVENT TRACKER " + pos);
    }
}