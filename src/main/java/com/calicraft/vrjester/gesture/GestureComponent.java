package com.calicraft.vrjester.gesture;

import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.utils.tools.Calcs;
import net.minecraft.util.math.vector.Vector3d;

import java.util.*;
import java.util.stream.Stream;

/**
 * @param vrDevice           = "RC"; // The VRDevice
 * @param movement           = "idle"; // Movement taken to get to Vox
 * @param elapsedTime        = 0; // Time spent within Vox in milliseconds (added on the fly while idle)
 * @param speed              = 0.0; // Average speed within a Vox in m/s (calculated on the fly while idle)
 * @param direction          = {0.0 , 0.0, 0.0}; // Average direction the VRDevice is facing
 * @param devicesInProximity = new HashMap<>(); // Other VRDevices within the Vox
 */

// This record represents a piece of a gesture & its attributes in an iteration in time per VRDevice
public record GestureComponent(String vrDevice, String movement,
                               long elapsedTime, double speed,
                               Vector3d direction, Map<String, Integer> devicesInProximity) {

    @Override
    public String toString() {
        return String.format("Path[ %s | movement=%s | time=%d | speed=%.2f | direction=(%.2f, %.2f, %.2f)]",
                vrDevice, movement, elapsedTime, speed, direction.x, direction.y, direction.z);
    }

    // Note to self: DO NOT include vrDevice in hashCode, this is how 'either or' functionality works.
    // This way either vrDevice can recognize the same gesture
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
               isWithinDirection(direction, gesturePath.direction) &&
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

    // Check if traced gesture has a direction within angle of the stored gesture (represented as a cone shape)
    private static boolean isWithinDirection(Vector3d direction, Vector3d otherDirection) {
        if (direction.equals(new Vector3d(0,0,0)))
            return true;
        else
            return Calcs.getAngle3D(direction, otherDirection) <= Constants.DIRECTION_DEGREE_SPAN;
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

    // Copy the given gesture and override fields with new values
    public static List<GestureComponent> copy(List<GestureComponent> gesture, Map<String, String> newValues) {
        List<GestureComponent> newGesture = new ArrayList<>();
        for (GestureComponent gestureComponent: gesture) {
            String vrDevice = newValues.get("vrDevice") == null ? gestureComponent.vrDevice() : newValues.get("vrDevice");
            String movement = gestureComponent.movement();
            long elapsedTime = gestureComponent.elapsedTime();
            double speed = gestureComponent.speed();
            Vector3d direction = gestureComponent.direction();
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
