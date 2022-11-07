package com.calicraft.vrjester.tracker;

import net.minecraft.world.phys.Vec3;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Tracker {
    // VRData Position Tracker Interface

    // TODO - To consume VRData from updated or additional sources
    //      - Overload static methods with new parameters

    public static Vec3 getOrigin(String vrData) {
        // origin: (0.0, 0.0, 0.0)
        Pattern pattern = Pattern.compile("origin: \\(.+, .+, .+\\)");
        Matcher matcher = pattern.matcher(vrData);
        if (matcher.find()) {
            String pos = matcher.group().replaceAll("(origin: \\(|\\)| )", "");
            String[] coords = pos.split(",");
            return new Vec3(Double.parseDouble(coords[0]),
                    Double.parseDouble(coords[1]),
                    Double.parseDouble(coords[2]));
        }
        return new Vec3((0), (0), (0));
    }

    public static Vec3 getPosition(VRData.VRDevicePose device) {
        // Get the 3D positional coordinate of passed device
        // pos:(1.0, 2.0, 3.0) dir: (4.0, 5.0, 6.0)
        String pose = device.toString();
        pose = pose.replaceAll("(Device: pos:\\(|\\) dir: \\(.+| )", "");
        String[] coords = pose.split(",");
        return new Vec3(Double.parseDouble(coords[0]),
                Double.parseDouble(coords[1]),
                Double.parseDouble(coords[2]));
    }

    public static Vec3 getDirection(VRData.VRDevicePose device) {
        // Get the 3D directional vector of passed device
        // pos:(1.0, 2.0, 3.0) dir: (4.0, 5.0, 6.0)
        String pose = device.toString();
        pose = pose.split("\\) dir: \\(")[1].replaceAll("[ )]", "");
        String[] coords = pose.split(",");

        return new Vec3(Double.parseDouble(coords[0]),
                Double.parseDouble(coords[1]),
                Double.parseDouble(coords[2]));
    }

    public static Vec3[] getPose(VRData.VRDevicePose device) {
        // Get both position & direction of device
        Vec3[] pose = new Vec3[2];
        pose[0] = getPosition(device);
        pose[1] = getDirection(device);
        return pose;
    }
}
