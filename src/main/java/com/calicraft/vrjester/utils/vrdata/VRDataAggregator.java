package com.calicraft.vrjester.utils.vrdata;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.config.Context;
import org.vivecraft.api.VRData;
import java.util.ArrayList;
import java.util.List;

public class VRDataAggregator {
    // Class for consuming VR Data from Vivecraft Tracker
    // to later pass to gesture recognition phase
    private final List<VRDataState> data = new ArrayList<>();
    public Context ctx;

    public VRDataAggregator() {
    }

    public List<VRDataState> getData() {
        return data;
    }

    public VRDataState listen() { // Consume VRDevicePose data from TRACKER
        VRData vrData = VrJesterApi.TRACKER.getVRData();
        assert vrData != null;
        VRDataState dataState = new VRDataState(vrData.hmd, vrData.c0, vrData.c1, vrData.c2);
        data.add(dataState);
        return dataState;
    }

    public void clear() { // Clear data only after sending
        data.clear();
    }
}
