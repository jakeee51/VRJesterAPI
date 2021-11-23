package com.calicraft.vrjester;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MyForgeEventHandler {
    @SubscribeEvent
    public void pickupItem(EntityItemPickupEvent event) {
        PositionTracker tracker = new PositionTracker();
        ClientPlayerEntity player = Minecraft.getInstance().player;
        ITextComponent text = new StringTextComponent("DEBUG: " + event.getItem().toString());
        assert player != null;
        player.sendMessage(text, player.getUUID());
        System.out.println("PICKUP EVENT ITEM " + event.getItem().toString());
        System.out.println("PICKUP EVENT TRACKER " + tracker.getVRPlayer());
    }
}