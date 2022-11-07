package com.calicraft.vrjester.utils.vrdata;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.config.Context;
import net.blf02.vrapi.api.data.IVRData;
import org.vivecraft.api.VRData;
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
        IVRData vrData;
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

    public void clear() { // Clear data only after sending
        data.clear();
    }
}
