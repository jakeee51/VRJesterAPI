package com.calicraft.vrjester.utils;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.tracker.PositionTracker;
import org.vivecraft.gameplay.VRPlayer;

import java.util.ArrayList;

public class VRDataAggregator {
    public static VRDataState listen() { //
        VRPlayer vrPlayer = VrJesterApi.TRACKER.getVrPlayer();
        assert vrPlayer != null;
        return new VRDataState(vrPlayer.vrdata_world_pre.hmd,
                vrPlayer.vrdata_world_pre.c0,
                vrPlayer.vrdata_world_pre.c1,
                vrPlayer.vrdata_world_pre.c2);
    }

    public static void send(ArrayList<VRDataState> data) { // Pass data over to recognize gesture
//        VRDataState vrdata = data.get(0);
    }
}
