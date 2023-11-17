package com.calicraft.vrjester.quilt;

import com.calicraft.vrjester.tracker.PositionTracker;
import net.blf02.vrapi.api.IVRAPI;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.entrypoint.EntrypointContainer;

import java.util.List;

public class VRPlugin {
    public static void initVR() {
        List<EntrypointContainer<IVRAPI>> apis = QuiltLoader.getEntrypointContainers("vrapi", IVRAPI.class);
        if (!apis.isEmpty()) {
            PositionTracker.getVRAPI(apis.get(0).getEntrypoint());
        }
    }
}
