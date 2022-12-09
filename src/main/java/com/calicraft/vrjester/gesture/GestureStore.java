package com.calicraft.vrjester.gesture;

import java.util.List;
import java.util.HashMap;


public class GestureStore {
    public VRDevice HMD = new VRDevice();
    public VRDevice RC = new VRDevice();
    public VRDevice LC = new VRDevice();

    public class VRDevice {
        public HashMap<String, List<Path>> gestures = new HashMap<>();
    }
}