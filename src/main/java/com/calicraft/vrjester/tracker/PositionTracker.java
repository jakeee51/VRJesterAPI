package com.calicraft.vrjester.tracker;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vivecraft.api.VRData;
import org.vivecraft.gameplay.VRPlayer;

import java.lang.reflect.*;

public class PositionTracker {
    private static final Logger LOGGER = LogManager.getLogger();
    public VRPlayer vrPlayer;
    public VRData vrdata_world_pre;
    public VRData.VRDevicePose hmd, c0, c1, c2;

    public PositionTracker() {
        try {
//            MC.VRPlayer.VRData.VRDevicePose
//            mc.vrPlayer.vrdata_world_pre.c0
            Class<?> mc = Class.forName("net.minecraft.client.Minecraft");
            Field vrPlayer = mc.getDeclaredField("vrPlayer");
            this.vrPlayer = (VRPlayer) vrPlayer.get(Minecraft.getInstance());
            if (this.vrPlayer != null) {
                vrdata_world_pre = this.vrPlayer.vrdata_world_pre;
                hmd = this.vrPlayer.vrdata_world_pre.hmd;
                c0 = this.vrPlayer.vrdata_world_pre.c0;
                c1 = this.vrPlayer.vrdata_world_pre.c1;
                c2 = this.vrPlayer.vrdata_world_pre.c2;
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

    public VRPlayer getVrPlayer() {
        // Get vrPlayer object for VRData consumption
        return vrPlayer;
    }
    public VRData.VRDevicePose getRightController() {
        // Get Right Controller Position
        return c0;
    }
    public VRData.VRDevicePose getLeftController() {
        // Get Left Controller Position
        return c1;
    }

    public static Vector3d getPosition(VRData.VRDevicePose device) {
        // Get the 3D positional coordinate of passed device
        // pos:(1.0, 2.0, 3.0) dir: (4.0, 5.0, 6.0)
        String pose = device.toString();
        pose = pose.replaceAll("(Device: pos:\\(|\\) dir: \\(.+| )", "");
        String[] coords = pose.split(",");

        return new Vector3d(Double.parseDouble(coords[0]),
                Double.parseDouble(coords[1]),
                Double.parseDouble(coords[2]));
    }

    public static Vector3d getDirection(VRData.VRDevicePose device) {
        // Get the 3D directional vector of passed device
        // pos:(1.0, 2.0, 3.0) dir: (4.0, 5.0, 6.0)
        String pose = device.toString();
        pose = pose.split("\\) dir: \\(")[1].replaceAll("[ )]", "");
        String[] coords = pose.split(",");

        return new Vector3d(Double.parseDouble(coords[0]),
                Double.parseDouble(coords[1]),
                Double.parseDouble(coords[2]));
    }

    public static Vector3d[] getPose(VRData.VRDevicePose device) {
        // Get both position & direction of device
        Vector3d[] pose = new Vector3d[2];
        pose[0] = getPosition(device);
        pose[1] = getDirection(device);
        return pose;
    }
}
