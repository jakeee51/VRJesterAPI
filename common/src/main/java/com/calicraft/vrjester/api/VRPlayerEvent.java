package com.calicraft.vrjester.api;

import com.calicraft.vrjester.utils.vrdata.VRDataState;
import dev.architectury.event.events.common.PlayerEvent;

public class VRPlayerEvent {
    // This class packages the VRData of the player

    private final VRDataState vrDataRoomPre;
    private final VRDataState vrDataWorldPre;

    public VRPlayerEvent(VRDataState vrDataRoomPre, VRDataState vrDataWorldPre) {
        this.vrDataRoomPre = vrDataRoomPre;
        this.vrDataWorldPre = vrDataWorldPre;
    }

    public VRDataState getVrDataRoomPre() {
        return vrDataRoomPre;
    }

    public VRDataState getVrDataWorldPre() {
        return vrDataWorldPre;
    }
}
