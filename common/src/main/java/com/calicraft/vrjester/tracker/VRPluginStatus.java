package com.calicraft.vrjester.tracker;

import net.minecraft.client.Minecraft;

public class VRPluginStatus {
    public static boolean hasPlugin = false;

    public static boolean clientInVR() {
        return hasPlugin && checkPlayerInVR();
    }

    public static boolean checkPlayerInVR() {
        return Minecraft.getInstance().player == null ||
                PositionTracker.vrAPI.playerInVR(Minecraft.getInstance().player);
    }
}