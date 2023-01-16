package com.calicraft.vrjester.gesture;

import com.calicraft.vrjester.utils.tools.Vec3;

import java.util.*;
import java.util.stream.Stream;

/**
 * @param vrDevice           = "RC"; // The VRDevice
 * @param movement           = "idle"; // Movement taken to get to Vox
 * @param elapsedTime        = 0; // Time spent within Vox in ms (added on the fly while idle)
 * @param speed              = 0.0; // Average speed within a Vox (calculated on the fly while idle)
 * @param direction          = {0.0 , 0.0, 0.0}; // Average direction the VRDevice is facing
 * @param devicesInProximity = new HashMap<>(); // Other VRDevices within the Vox
 */

// This record represents a piece of a gesture & its attributes in an iteration in time per VRDevice
public record GestureComponent(String vrDevice, String movement,
                               long elapsedTime, double speed,
                               Vec3 direction, Map<String, Integer> devicesInProximity) {

    @Override
    public String toString() {
        return String.format("Path[ %s | movement=%s | time=%d | speed=%.2f | direction=%s]",
                vrDevice, movement, elapsedTime, speed, direction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movement, elapsedTime, speed, direction, devicesInProximity);
    }

    // Check if the traced gesture is equal to a stored gesture
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof GestureComponent other)) {
            return false;
        } else {
            return Objects.equals(vrDevice, other.vrDevice) &&
                   Objects.equals(movement, other.movement) &&
                   Objects.equals(elapsedTime, other.elapsedTime) &&
                   Objects.equals(speed, other.speed) &&
                   Objects.equals(direction, other.direction) &&
                   Objects.equals(devicesInProximity.keySet(), other.devicesInProximity.keySet());
        }
    }

    // Check if the traced gesture is within the parameters of a stored gesture
    public boolean matches(GestureComponent gesturePath) {
        return vrDevice.equals(gesturePath.vrDevice) &&
               movement.equals(gesturePath.movement) &&
               elapsedTime <= gesturePath.elapsedTime &&
               speed <= gesturePath.speed &&
               isWithinProximity(devicesInProximity, gesturePath.devicesInProximity);
    }

    // Check if the gesture starts with the subGesture(i.e.: does 'cat' start with 'c')
    public static boolean startsWith(List<GestureComponent> gesture, List<GestureComponent> subGesture) {
        try {
            return gesture.subList(0, subGesture.size()).equals(subGesture);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    // Check if the gesture has a match with the subGesture
    public static boolean matchesWith(List<GestureComponent> gesture, List<GestureComponent> subGesture) {
        try {
            for (int i = 0; i < subGesture.size(); i++) {
                if (!subGesture.get(i).matches(gesture.get(i)))
                    return false;
            }
            return true;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    // Check if traced gesture has the same devices within proximity of the stored gesture
    private static boolean isWithinProximity(Map<String, Integer> devices, Map<String, Integer> otherDevices) {
        if (devices.isEmpty())
            return true;
        else
            return Objects.equals(devices.keySet(), otherDevices.keySet());
    }

    // Concatenate the 2 GestureComponent lists and return the new one
    public static List<GestureComponent> concat(List<GestureComponent> gestureComponent1, List<GestureComponent> gestureComponent2) {
        if(gestureComponent1 == null)
            gestureComponent1 = new ArrayList<>();
        if(gestureComponent2 == null)
            gestureComponent2 = new ArrayList<>();
        return Stream.concat(gestureComponent1.stream(), gestureComponent2.stream()).toList();
    }

    public static List<GestureComponent> copy(List<GestureComponent> gesture, Map<String, String> newValues) {
        List<GestureComponent> newGesture = new ArrayList<>();
        for (GestureComponent gestureComponent: gesture) {
            String vrDevice = newValues.get("vrDevice") == null ? gestureComponent.vrDevice() : newValues.get("vrDevice");
            String movement = gestureComponent.movement();
            long elapsedTime = gestureComponent.elapsedTime();
            double speed = gestureComponent.speed();
            Vec3 direction = gestureComponent.direction();
            Map<String, Integer> devicesInProximity = gestureComponent.devicesInProximity();

            GestureComponent newComponent = new GestureComponent(
                    vrDevice, movement,
                    elapsedTime, speed,
                    direction, devicesInProximity);
            newGesture.add(newComponent);
        }
        return newGesture;
    }
}
