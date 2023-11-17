package com.calicraft.vrjester.fabric;

import com.calicraft.vrjester.VrJesterApi;
import net.fabricmc.api.ModInitializer;

public class VrJesterApiFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        VrJesterApi.init();
        try {
            Class.forName("net.blf02.vrapi.api.IVRAPI");
            VRPlugin.initVR();
        } catch (ClassNotFoundException e) {
            VrJesterApi.LOGGER.info("Not loading with mc-vr-api; it wasn't found!");
        }
    }
}
