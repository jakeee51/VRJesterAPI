package com.calicraft.vrjester.utils.vrdata;

import com.calicraft.vrjester.VrJesterApi;
import org.vivecraft.api.VRData;

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
    public VRDataState listen() { // Consume Vivecraft VRDevicePose data from TRACKER
        VRData vrData;
        switch(vrDataType) {
            case VRDATA_ROOM_PRE:
                vrData = VrJesterApi.TRACKER.getVRDataRoomPre(); break;
            case VRDATA_WORLD_PRE:
                vrData = VrJesterApi.TRACKER.getVRDataWorldPre(); break;
            default:
                vrData = null;
        }
        assert vrData != null;
        VRDataState dataState = new VRDataState(vrData);
        if (saveState)
            data.add(dataState);
        return dataState;
    }

    // Clear data only after sending
    public void clear() {
        data.clear();
    }
}
