package com.calicraft.vrjester.tracker;

import net.blf02.vrapi.api.data.IVRData;
import net.minecraft.util.math.vector.Vector3d;

public abstract class Tracker {
    // VRData Position Tracker Interface

    // Get origin position
    public static Vector3d getOrigin(String vrData) {
        // origin: (0.0, 0.0, 0.0)
        return new Vector3d((0), (0), (0));
    }

    // Get the 3D positional coordinate of passed device
    public static Vector3d getPosition(IVRData device) {
        // pos:(1.0, 2.0, 3.0) dir: (4.0, 5.0, 6.0)
        return device.position();
    }

    // Get the 3D directional vector of passed device
    public static Vector3d getDirection(IVRData device) {
        // pos:(1.0, 2.0, 3.0) dir: (4.0, 5.0, 6.0)
        return device.getLookAngle();
    }

    // Get both position & direction of device
    public static Vector3d[] getPose(IVRData device) {
        Vector3d[] pose = new Vector3d[2];
        pose[0] = getPosition(device);
        pose[1] = getDirection(device);
        return pose;
    }
}
