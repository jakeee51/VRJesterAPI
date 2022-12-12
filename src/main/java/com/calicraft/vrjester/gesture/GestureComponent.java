package com.calicraft.vrjester.gesture;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @param vrDevice           = "RC"; // The VRDevice
 * @param movement           = "idle"; // Movement taken to get to Vox
 * @param elapsedTime        = 0; // Time spent within Vox in ms (added on the fly while idle)
 * @param maxElapsedTime     = -1; // Is -1 if it's part of a traced gesture
 * @param speed              = 0.0; // Is -1 if it's part of a traced gesture
 * @param maxSpeed           = -1.0; // Average speed within Vox (calculated on the fly while idle)
 * @param devicesInProximity = new HashMap<>(); // Other VRDevices within the Vox
 */

// This record represents a piece of a gesture & its attributes in an iteration in time per VRDevice
public record GestureComponent(String vrDevice, String movement,
                               long elapsedTime, long maxElapsedTime,
                               double speed, double maxSpeed,
                               Vec3 direction, Map<String, Integer> devicesInProximity) {

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
    // Check if the traced gesture is within the parameters of a stored gesture
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof GestureComponent other)) {
            return false;
        } else {
            return Objects.equals(vrDevice, other.vrDevice) &&
                   Objects.equals(movement, other.movement) &&
                   isWithinTime(other.elapsedTime, other.maxElapsedTime, elapsedTime, maxElapsedTime) &&
                   isWithinSpeed(other.speed, other.maxSpeed, speed, maxSpeed) &&
                   isWithinProximity(other.maxElapsedTime, other.devicesInProximity, devicesInProximity);
        }
    }

    // Check if traced gesture is within the elapsed time range
    private static boolean isWithinTime(long otherMin, long otherMax, long min, long max) {
        boolean ret;
        if (max == 0)
            ret = min <= otherMin;
        else if (otherMax == -1)
            ret = min <= otherMin && otherMin <= max;
        else
            ret = min == otherMin && otherMax == max;
        return ret;
    }

    // Check if traced gesture is within the speed range
    private static boolean isWithinSpeed(double otherMin, double otherMax, double min, double max) {
        boolean ret;
        if (max == 0)
            ret = min <= otherMin;
        else if (otherMax == -1.0)
            ret = min <= otherMin && otherMin <= max;
        else
            ret = min == otherMin && otherMax == max;
        return ret;
    }

    // Check if traced gesture has the same devices within proximity of the stored gesture
    private static boolean isWithinProximity(long gestureInd, Map<String, Integer> otherDevices, Map<String, Integer> devices) {
        boolean ret;
        if (gestureInd == -1 && devices.isEmpty())
            ret = true;
        else
            ret = Objects.equals(devices.keySet(), otherDevices.keySet());
        return ret;
    }

    // Check if the gestureComponent starts with the subGestureComponent (i.e.: does 'cat' start with 'c')
    public static boolean startsWith(List<GestureComponent> gestureComponent, List<GestureComponent> subGestureComponent) {
        try {
            return gestureComponent.subList(0, subGestureComponent.size()).equals(subGestureComponent);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    // Concatenate the 2 GestureComponent lists and return the new one
    public static List<GestureComponent> concat(List<GestureComponent> gestureComponent1, List<GestureComponent> gestureComponent2) {
        if(gestureComponent1 == null)
            gestureComponent1 = new ArrayList<>();
        if(gestureComponent2 == null)
            gestureComponent2 = new ArrayList<>();
        return Stream.concat(gestureComponent1.stream(), gestureComponent2.stream()).toList();
    }
}
