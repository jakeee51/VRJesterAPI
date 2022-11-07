package com.calicraft.vrjester.tracker;

import net.blf02.vrapi.api.IVRAPI;
import net.blf02.vrapi.api.VRAPIPlugin;
import net.blf02.vrapi.api.VRAPIPluginProvider;
import net.blf02.vrapi.api.data.IVRData;
import net.blf02.vrapi.api.data.IVRPlayer;

import static com.calicraft.vrjester.VrJesterApi.getMCI;

@VRAPIPlugin
public class PositionTracker implements VRAPIPluginProvider {
    // Class for consuming & tracking VRPlayer data from Vivecraft

    public IVRPlayer vrPlayer;
    public static IVRAPI vrAPI = null;

    public PositionTracker() {
        vrPlayer = vrAPI.getVRPlayer(getMCI().player);
    }

    @Override
    public void getVRAPI(IVRAPI ivrapi) {
        vrAPI = ivrapi;
        VRPluginStatus.hasPlugin = true;
    }

    public class VRPluginStatus {
        public static boolean hasPlugin = false;
    }

    // Note: VR data getters must be called later after initialization to avoid NullPointerException (i.e.: ExceptionInInitializerError: null)
    public IVRData getVRDataRoomPre() { // Return real world VR data pre-tick
        return vrPlayer.vrdata_room_pre;
    }

    public IVRData getVRDataWorldPre() { // Return in-game world VR data pre-tick
        return vrPlayer.vrdata_world_pre;
    }

    public IVRPlayer getVRPlayer() {
        return vrPlayer;
    }
}