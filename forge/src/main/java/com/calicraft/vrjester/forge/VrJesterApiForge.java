package com.calicraft.vrjester.forge;

import dev.architectury.platform.forge.EventBuses;
import com.calicraft.vrjester.VrJesterApi;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(VrJesterApi.MOD_ID)
public class VrJesterApiForge {
    public VrJesterApiForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(VrJesterApi.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        VrJesterApi.init();
    }
}
