package com.calicraft.vrjester.fabric;

import com.calicraft.vrjester.tracker.PositionTracker;
import net.blf02.vrapi.api.IVRAPI;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

import java.util.List;

public class VRPlugin {
    public static void initVR() {
        List<EntrypointContainer<IVRAPI>> apis = FabricLoader.getInstance().getEntrypointContainers("vrapi", IVRAPI.class);
        if (!apis.isEmpty()) {
            PositionTracker.getVRAPI(apis.get(0).getEntrypoint());
        }
    }
}