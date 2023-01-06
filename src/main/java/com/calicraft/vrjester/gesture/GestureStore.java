package com.calicraft.vrjester.gesture;

import java.util.List;
import java.util.HashMap;


public class GestureStore {
    // Class for formatting the Gestures to be stored using Gson into a JSON file
    // TODO - Reformat to allow gestures to be recognized based on the specified VRDevices.
    //  Devs should be able to recognize gestures dependent AND independent from other VRDevices.

    public HashMap<String, HashMap<String, List<GestureComponent>>> GESTURES = new HashMap<>();

    public GestureStore() {}

    // Add gesture to GestureStore based on VRDevice
    public void addGesture(String vrDevice, String gestureName, List<GestureComponent> gesture) {
        HashMap<String, List<GestureComponent>> deviceGesture = new HashMap<>();
        deviceGesture.put(vrDevice, gesture);
        GESTURES.put(gestureName, deviceGesture);
    }
}