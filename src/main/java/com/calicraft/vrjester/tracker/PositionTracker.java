package com.calicraft.vrjester.tracker;

import net.blf02.vrapi.api.IVRAPI;
import net.blf02.vrapi.api.VRAPIPlugin;
import net.blf02.vrapi.api.VRAPIPluginProvider;
import net.blf02.vrapi.api.data.IVRPlayer;

import static com.calicraft.vrjester.VrJesterApi.getMCI;

@VRAPIPlugin
public class PositionTracker implements VRAPIPluginProvider {
    // Class for consuming & tracking VRPlayer data from Vivecraft

    public static IVRAPI vrAPI = null;

    @Override
    public void getVRAPI(IVRAPI ivrapi) {
        vrAPI = ivrapi;
        VRPluginStatus.hasPlugin = true;
    }

    // Note: VR data getters must be called later after initialization to avoid NullPointerException (i.e.: ExceptionInInitializerError: null)
    // Return real world VR data pre-tick
    public static IVRPlayer getVRDataRoomPre() {
        return vrAPI.getVRPlayer(getMCI().player);
    }

    // Return in-game world VR data pre-tick
    public static IVRPlayer getVRDataWorldPre() {
//        return vrAPI.getPreTickVRPlayer();
        return null;
    }
}