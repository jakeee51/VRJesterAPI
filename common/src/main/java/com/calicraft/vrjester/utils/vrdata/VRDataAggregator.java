package com.calicraft.vrjester.utils.vrdata;

import com.calicraft.vrjester.tracker.PositionTracker;
import net.blf02.vrapi.api.data.IVRPlayer;

import java.util.ArrayList;
import java.util.List;

public class VRDataAggregator {
    // Class for consuming VR Data from Tracker
    private final List<VRDataState> data = new ArrayList<>();
    private final VRDataType vrDataType;
    private final boolean saveState;

    public VRDataAggregator(VRDataType vrDataType, boolean saveState) {
        this.vrDataType = vrDataType;
        this.saveState = saveState;
    }

    public List<VRDataState> getData() {
        return data;
    }

    // Consume Vivecraft VRDevicePose data from TRACKER
    public VRDataState listen() {
        IVRPlayer ivrPlayer = switch (vrDataType) {
            case VRDATA_ROOM_PRE -> PositionTracker.getVRDataRoomPre();
            case VRDATA_WORLD_PRE -> PositionTracker.getVRDataWorldPre();
            default -> null;
        };
        assert ivrPlayer != null;
        VRDataState dataState = new VRDataState(ivrPlayer);
        if (saveState)
            data.add(dataState);
        return dataState;
    }

    // Clear data only after sending
    public void clear() {
        data.clear();
    }
}
