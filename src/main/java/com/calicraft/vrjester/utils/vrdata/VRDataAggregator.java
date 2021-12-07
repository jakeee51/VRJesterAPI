package com.calicraft.vrjester.utils.vrdata;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.gestures.JesterRecognition;
import org.vivecraft.gameplay.VRPlayer;

import java.util.ArrayList;

public class VRDataAggregator {
    // Class for consuming, formatting & sending VRData from
    // PositionTracker to the gesture recognition phase

    public static VRDataState listen() { // Consume VRDevicePose data from TRACKER
        VRPlayer vrPlayer = VrJesterApi.TRACKER.getVrPlayer();
        assert vrPlayer != null;
        return new VRDataState(vrPlayer.vrdata_world_pre.hmd,
                vrPlayer.vrdata_world_pre.c0,
                vrPlayer.vrdata_world_pre.c1,
                vrPlayer.vrdata_world_pre.c2);
    }

    public static void send(ArrayList<VRDataState> data, long elapsed_time) { // Pass data over to gesture recognizer
        VRDataState[] vrData = new VRDataState[data.size()];
        JesterRecognition recognizer = new JesterRecognition(data.toArray(vrData), elapsed_time);
        recognizer.isLinearGesture(VRDevice.RC);
    }
}
