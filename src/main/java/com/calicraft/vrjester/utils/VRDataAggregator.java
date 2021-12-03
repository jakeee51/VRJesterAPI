package com.calicraft.vrjester.utils;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.gestures.JesterRecognition;
import com.calicraft.vrjester.tracker.PositionTracker;
import net.minecraft.util.math.vector.Vector3d;
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

    public static void send(ArrayList<VRDataState> data) { // Pass data over to gesture recognizer
        Vector3d[] vectors = new Vector3d[data.size()];
        for (int i = 0; i < data.size(); i++) {
            vectors[i] = PositionTracker.getPosition(data.get(i).getRc());
        }
        JesterRecognition recognizer = new JesterRecognition(vectors);
        System.out.println("GESTURE: " + recognizer.getGesture());
    }
}
