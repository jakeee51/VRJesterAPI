package com.calicraft.vrjester.gesture;

import java.util.List;
import java.util.HashMap;


public class GestureStore {
    public HashMap<String, List<Path>> HMD = new HashMap<>();
    public HashMap<String, List<Path>> RC = new HashMap<>();
    public HashMap<String, List<Path>> LC = new HashMap<>();

    public GestureStore() {}

    public void addGesture(String vrDevice, String gestureName, List<Path> gesture) {
        switch(vrDevice) {
            case "HMD" -> HMD.put(gestureName, gesture);
            case "RC"  -> RC.put(gestureName, gesture);
            case "LC"  -> LC.put(gestureName, gesture);
        }
    }
}