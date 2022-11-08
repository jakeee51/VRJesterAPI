package com.calicraft.vrjester.tracker;

import com.calicraft.vrjester.VrJesterApi;
import net.blf02.vrapi.api.IVRAPI;
import net.blf02.vrapi.api.VRAPIPlugin;
import net.blf02.vrapi.api.VRAPIPluginProvider;
import net.blf02.vrapi.api.data.IVRPlayer;


@VRAPIPlugin
public class PositionTracker implements VRAPIPluginProvider {
    // Class for consuming & tracking VRPlayer data from Vivecraft

    public static IVRPlayer vrdata_room_pre, vrdata_world_pre;
    public static IVRAPI vrAPI = null;

    @Override
    public void getVRAPI(IVRAPI ivrapi) {
        vrAPI = ivrapi;
        VRPluginStatus.hasPlugin = VrJesterApi.VIVECRAFTLOADED = true;
        vrdata_room_pre = vrAPI.getPreTickRoomVRPlayer();
        vrdata_world_pre = vrAPI.getPreTickVRPlayer();
    }

    // Note: VR data getters must be called later after initialization to avoid NullPointerException (i.e.: ExceptionInInitializerError: null)
    public static IVRPlayer getVRDataRoomPre() { // Return real world VR data pre-tick
        return vrdata_room_pre;
    }

    public static IVRPlayer getVRDataWorldPre() { // Return in-game world VR data pre-tick
        return vrdata_world_pre;
    }
}