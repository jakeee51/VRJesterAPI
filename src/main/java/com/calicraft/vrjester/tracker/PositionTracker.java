package com.calicraft.vrjester.tracker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vivecraft.api.VRData;
import org.vivecraft.gameplay.VRPlayer;

import java.lang.reflect.*;

import static com.calicraft.vrjester.VrJesterApi.getMCI;

public class PositionTracker implements Tracker {
    // Class for consuming & tracking VRPlayer data from Vivecraft

    private static final Logger LOGGER = LogManager.getLogger();
    private VRData vrdata_world_pre;

    public PositionTracker() {
        try {
//            MC.VRPlayer.VRData.VRDevicePose
//            mc.vrPlayer.vrdata_world_pre.c0
            Class<?> mc = Class.forName("net.minecraft.client.Minecraft");
            Field vrPlayer_field = mc.getDeclaredField("vrPlayer");
            VRPlayer vrPlayer = (VRPlayer) vrPlayer_field.get(getMCI());
            if (vrPlayer != null) {
                vrdata_world_pre = vrPlayer.vrdata_world_pre;
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

    public VRData getVRData() {
        return vrdata_world_pre;
    }
}
