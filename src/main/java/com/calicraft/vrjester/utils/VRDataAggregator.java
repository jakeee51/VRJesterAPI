package com.calicraft.vrjester.utils;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.tracker.PositionTracker;

import java.util.ArrayList;

public class VRDataAggregator {
    public static VRDataState listen() { //
        PositionTracker vrData = VrJesterApi.TRACKER;
        assert vrData != null;
        return new VRDataState(vrData.hmd, vrData.c0, vrData.c1, vrData.c2);
    }

    public static void send(ArrayList<VRDataState> data) { // Pass data over to recognize gesture
//        VRDataState vrdata = data.get(0);
    }
}
