package com.calicraft.vrjester.forge;

import com.calicraft.vrjester.tracker.PositionTracker;
import net.blf02.forge.VRAPIPlugin;
import net.blf02.forge.VRAPIPluginProvider;
import net.blf02.vrapi.api.IVRAPI;

@VRAPIPlugin
public class VRPlugin implements VRAPIPluginProvider {

    public static IVRAPI vrAPI;

    @Override
    public void getVRAPI(IVRAPI ivrapi) {
        PositionTracker.getVRAPI(ivrapi);
    }
}
