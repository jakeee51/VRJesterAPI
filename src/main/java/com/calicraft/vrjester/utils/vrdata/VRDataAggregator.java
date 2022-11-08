package com.calicraft.vrjester.utils.vrdata;

import com.calicraft.vrjester.config.Context;
import com.calicraft.vrjester.tracker.PositionTracker;
import net.blf02.vrapi.api.data.IVRPlayer;
import java.util.ArrayList;
import java.util.List;

public class VRDataAggregator {
    // Class for consuming VR Data from Vivecraft Tracker
    // to later pass to gesture recognition phase
    private final List<VRDataState> data = new ArrayList<>();
    private VRDataType vrDataType;
    private boolean saveState;
    public Context ctx;

    public VRDataAggregator(VRDataType vrDataType, boolean saveState) {
        this.vrDataType = vrDataType;
        this.saveState = saveState;
    }

    public List<VRDataState> getData() {
        return data;
    }

    public VRDataState listen() { // Consume Vivecraft VRDevicePose data from TRACKER
        IVRPlayer ivrPlayer;
        switch(vrDataType) {
            case VRDATA_ROOM_PRE:
                ivrPlayer = PositionTracker.getVRDataRoomPre(); break;
            case VRDATA_WORLD_PRE:
                ivrPlayer = PositionTracker.getVRDataWorldPre(); break;
            default:
                ivrPlayer = null;
        }
        assert ivrPlayer != null;
        VRDataState dataState = new VRDataState(ivrPlayer);
        if (saveState)
            data.add(dataState);
        return dataState;
    }

    public void clear() { // Clear data only after sending
        data.clear();
    }
}
