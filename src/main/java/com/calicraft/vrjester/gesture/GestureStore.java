package com.calicraft.vrjester.gesture;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GestureStore {
    // Class for formatting the Gestures to be stored using Gson into a JSON file

    public HashMap<String, HashMap<String, List<GestureComponent>>> GESTURES = new HashMap<>();

    public GestureStore() {}

    // Add gesture to GestureStore based on VRDevice
    public void addGesture(String vrDevice, String gestureName, List<GestureComponent> gesture, List<String> validDevices) {
        HashMap<String, List<GestureComponent>> deviceGesture = GESTURES.getOrDefault(gestureName, new HashMap<>());
        if (validDevices != null) {
            vrDevice = String.join("|", validDevices);
            List<GestureComponent> newGesture = GestureComponent.copy(gesture, Map.of("vrDevice", vrDevice));
            deviceGesture.put(vrDevice, newGesture);
            GESTURES.put(gestureName, deviceGesture);
        } else {
            deviceGesture.put(vrDevice, gesture);
            GESTURES.put(gestureName, deviceGesture);
        }
    }
}