package com.calicraft.vrjester.tracker;

import net.minecraft.util.math.vector.Vector3d;
import org.vivecraft.api.VRData;

public abstract class Tracker {
    // VRData Position Tracker Interface

    // TODO - To consume VRData from updated or additional sources
    //      - Overload static methods with new parameters

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
