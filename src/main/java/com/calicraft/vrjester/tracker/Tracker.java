package com.calicraft.vrjester.tracker;

import net.blf02.vrapi.api.data.IVRData;
import net.minecraft.world.phys.Vec3;


public abstract class Tracker {
    // VRData Position Tracker Interface

    public static Vec3 getOrigin(String vrData) {
        // origin: (0.0, 0.0, 0.0)
        return new Vec3((0), (0), (0));
    }

    public static Vec3 getPosition(IVRData device) {
        // Get the 3D positional coordinate of passed device
        // pos:(1.0, 2.0, 3.0) dir: (4.0, 5.0, 6.0)
        return device.position();
    }

    public static Vec3 getDirection(IVRData device) {
        // Get the 3D directional vector of passed device
        // pos:(1.0, 2.0, 3.0) dir: (4.0, 5.0, 6.0)
        return device.getLookAngle();
    }

    public static Vec3[] getPose(IVRData device) {
        // Get both position & direction of device
        Vec3[] pose = new Vec3[2];
        pose[0] = getPosition(device);
        pose[1] = getDirection(device);
        return pose;
    }
}
