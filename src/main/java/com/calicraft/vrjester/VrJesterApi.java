package com.calicraft.vrjester;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.handlers.PlayerInitHandler;
import com.calicraft.vrjester.tracker.PositionTracker;
import com.calicraft.vrjester.utils.tools.EventsLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("vrjester")
public class VrJesterApi {
    // Main entry point
    public static final Logger LOGGER = LogManager.getLogger();
    public static PositionTracker TRACKER;
    public static boolean VIVECRAFT_LOADED = false;
    public static final String MOD_ID = "vrjester";
    public static final KeyBinding MOD_KEY = new KeyBinding("key.vrjester.71", 71, MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);
    public static SoundEvent DEKU_SMASH;
    public static HashMap<String, KeyBinding> KEY_MAPPINGS = new HashMap<>();

    public VrJesterApi() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the setup method for mod loading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the key bindings for mod loading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        try { // Check if Vivecraft is running
            Class.forName("org.vivecraft.api.VRData");
            VIVECRAFT_LOADED = true;
            System.out.println("Vivecraft has been loaded!");
            MinecraftForge.EVENT_BUS.register(new PlayerInitHandler());
            LOGGER.info("Events have been loaded!");
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            LOGGER.error("Vivecraft has failed to load!");
        }
        EventsLoader.register();
    }

    public static Minecraft getMCI() {
        return Minecraft.getInstance();
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Setting up config files...");
        File configFile = new File(Constants.CONFIG_PATH);
        File gestureStoreFile = new File(Constants.GESTURE_STORE_PATH);
        if (!configFile.exists())
            Config.writeConfig();
        if (!gestureStoreFile.exists())
            Config.writeGestureStore();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        LOGGER.info("Setting up sounds...");
        DEKU_SMASH = new SoundEvent(new ResourceLocation(MOD_ID, "deku_smash"));
        SOUNDS.register("deku_smash", () -> DEKU_SMASH);
        SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
        LOGGER.info("Setting up keybindings...");
        ClientRegistry.registerKeyBinding(MOD_KEY);
        LOGGER.info("Setting up key mappings...");
        Config config = Config.readConfig();
        KeyBinding[] keyMappings = getMCI().options.keyMappings;
        HashMap<String, String> gestureMappings = config.GESTURE_KEY_MAPPINGS;
        for (String gestureMapping: gestureMappings.values()) {
            for (KeyBinding keyMapping: keyMappings) {
                if (keyMapping.getName().equals(gestureMapping)) {
                    LOGGER.info("Adding gesture key mapping -> mapping name: " + gestureMapping + " | key name: " + keyMapping.saveString());
                    KEY_MAPPINGS.put(gestureMapping, keyMapping);
                }
            }
        }
    }
}
