package com.calicraft.vrjester.gesture;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @param vrDevice           ; // The VRDevice
 * @param movement           = "idle"; // Movement taken to get to Vox
 * @param elapsedTime
 * @param maxElapsedTime     = 0; // Time spent within Vox in ms (added on the fly while idle)
 * @param speed              ; // Average speed within Vox (calculated on the fly while idle)
 * @param maxSpeed
 * @param devicesInProximity = new HashMap<>(); // Other VRDevices within this Vox
 */

public record Path(String vrDevice, String movement,
                   long elapsedTime, long maxElapsedTime,
                   double speed, double maxSpeed,
                   Vec3 direction,
                   Map<String, Integer> devicesInProximity) {

    @Override
    public String toString() {
        return String.format("Path[ %s | movement=%s | time=%d -> %d | speed=%.2f -> %.2f ]",
                vrDevice, movement, elapsedTime, maxElapsedTime, speed, maxSpeed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vrDevice, movement, elapsedTime, maxElapsedTime,
                speed, maxSpeed, direction, devicesInProximity);
    }

    // TODO - Account for direction & devicesInProximity
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Path other)) {
            return false;
        } else {
            return Objects.equals(vrDevice, other.vrDevice) &&
                   Objects.equals(movement, other.movement) &&
                   isWithinTime(other.elapsedTime, other.maxElapsedTime, elapsedTime, maxElapsedTime) &&
                   isWithinSpeed(other.speed, other.maxSpeed, speed, maxSpeed) &&
                   isWithinProximity(other.maxElapsedTime, other.devicesInProximity, devicesInProximity);
        }
    }

    private static boolean isWithinTime(long otherMin, long otherMax, long min, long max) {
//        System.out.println("isWithinTime:");
//        System.out.println(min + " <= " + otherMin + " <= " + max + ", otherMax: " + otherMax);
        boolean ret;
        if (max == 0)
            ret = min <= otherMin;
        else if (otherMax == -1)
            ret = min <= otherMin && otherMin <= max;
        else
            ret = min == otherMin && otherMax == max;
        return ret;
    }

    private static boolean isWithinSpeed(double otherMin, double otherMax, double min, double max) {
//        System.out.println("isWithinSpeed:");
//        System.out.println(min + " <= " + otherMin + " <= " + max + ", otherMax: " + otherMax);
        boolean ret;
        if (max == 0)
            ret = min <= otherMin;
        else if (otherMax == -1.0)
            ret = min <= otherMin && otherMin <= max;
        else
            ret = min == otherMin && otherMax == max;
        return ret;
    }

    private static boolean isWithinProximity(long gestureInd, Map<String, Integer> otherDevices, Map<String, Integer> devices) {
        boolean ret;
        if (gestureInd == -1 && devices.isEmpty())
            ret = true;
        else
            ret = Objects.equals(devices.keySet(), otherDevices.keySet());
        return ret;
    }

    public static boolean startsWith(List<Path> path, List<Path> subPath) {
        try {
            return path.subList(0, subPath.size()).equals(subPath);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public static List<Path> concat(List<Path> path1, List<Path> path2) {
        if(path1 == null)
            path1 = new ArrayList<>();
        if(path2 == null)
            path2 = new ArrayList<>();
        return Stream.concat(path1.stream(), path2.stream()).toList();
    }
}
