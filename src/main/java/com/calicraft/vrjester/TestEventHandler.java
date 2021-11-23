package com.calicraft.vrjester;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TestEventHandler {
    @SubscribeEvent
    public void onPlaceEvent(BlockEvent.EntityPlaceEvent event) {
        PositionTracker tracker = new PositionTracker();
        ClientPlayerEntity player = Minecraft.getInstance().player;
        ITextComponent text = new StringTextComponent("DEBUG: " + event.getPlacedBlock().toString());
        assert player != null;
        player.sendMessage(text, player.getUUID());
        System.out.println("PLACE EVENT BLOCK " + event.getPlacedBlock().toString());
        System.out.println("PLACE EVENT TRACKER " + tracker.getVRPlayer());
    }
}
