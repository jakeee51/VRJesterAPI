package com.calicraft.vrjester.gesture;

import java.util.List;
import java.util.HashMap;


public class GestureStore {
    // Class for formatting the Gestures to be stored using Gson into a JSON file

    public HashMap<String, List<GestureComponent>> HMD = new HashMap<>();
    public HashMap<String, List<GestureComponent>> RC = new HashMap<>();
    public HashMap<String, List<GestureComponent>> LC = new HashMap<>();

    public GestureStore() {}

    // Add gesture to GestureStore based on VRDevice
    public void addGesture(String vrDevice, String gestureName, List<GestureComponent> gesture) {
        switch(vrDevice) {
            case "HMD" -> HMD.put(gestureName, gesture);
            case "RC"  -> RC.put(gestureName, gesture);
            case "LC"  -> LC.put(gestureName, gesture);
        }
    }
}