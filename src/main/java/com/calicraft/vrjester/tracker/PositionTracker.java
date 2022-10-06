package com.calicraft.vrjester.tracker;

import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vivecraft.api.VRData;
import org.vivecraft.gameplay.VRPlayer;

import java.lang.reflect.*;

public class PositionTracker {
    // Class for consuming & tracking VRPlayer data from Vivecraft

    private static final Logger LOGGER = LogManager.getLogger();
    public VRPlayer vrPlayer; // Field declared as part of Minecraft instance
    public String vrDataSource; // Which VR Data API being consumed

    public PositionTracker() {
        try {
//            MC.VRPlayer.VRData.VRDevicePose -> vr device
//            mc.vrPlayer.vrdata_world_pre.c0 -> right controller
            Class<?> mc = Class.forName("net.minecraft.client.Minecraft");
            Field vrPlayer = mc.getDeclaredField("vrPlayer");
            this.vrPlayer = (VRPlayer) vrPlayer.get(Minecraft.getInstance());
            if (this.vrPlayer != null) {
                vrDataSource = "vivecraft_116";
            } else {
                LOGGER.debug("vrPlayer null");
            }
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            LOGGER.error("Failed to load Vivecraft class!");
        } catch ( NoSuchFieldException e) {
            LOGGER.error("Failed to get Vivecraft field");
        } catch (IllegalAccessException e) {
            LOGGER.error("Failed to access Vivecraft object");
        }
    }

    // Note: VR data getters must be called later after initialization to avoid NullPointerException (i.e.: ExceptionInInitializerError: null)
    public VRData getVRDataRoomPre() { // Return real world VR data pre-tick
        return vrPlayer.vrdata_room_pre;
    }

    public VRData getVRDataWorldPre() { // Return in-game world VR data pre-tick
        return vrPlayer.vrdata_world_pre;
    }

    public VRPlayer getVRPlayer() {
        return vrPlayer;
    }
}