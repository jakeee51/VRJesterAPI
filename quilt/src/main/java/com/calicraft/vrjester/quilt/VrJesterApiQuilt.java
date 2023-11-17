package com.calicraft.vrjester.quilt;

import com.calicraft.vrjester.VrJesterApi;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class VrJesterApiQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        VrJesterApi.init();
        try {
            Class.forName("net.blf02.vrapi.api.IVRAPI");
            VRPlugin.initVR();
        } catch (ClassNotFoundException e) {
            VrJesterApi.LOGGER.info("Not loading with mc-vr-api; it wasn't found!");
        }
    }
}
