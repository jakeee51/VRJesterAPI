package com.calicraft.vrjester;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.handlers.GestureEventHandler;
import com.calicraft.vrjester.handlers.TriggerEventHandler;
import com.calicraft.vrjester.utils.tools.GestureCommand;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;

public class VrJesterApi {
    // Main entry point
    public static final Logger LOGGER = LogManager.getLogger();
    public static boolean VIVECRAFT_LOADED = false;
    public static final String MOD_ID = "vrjester";
    public static final KeyMapping MOD_KEY = new KeyMapping(
            "key.vrjester.71", InputConstants.Type.KEYSYM,
            71, "category." + MOD_ID + ".gesture_trigger");
    public static HashMap<String, KeyMapping> KEY_MAPPINGS = new HashMap<>();

    public static void init() {
        LOGGER.info("Initializing VR Jester API");
        System.out.println("Config Path: " + ModExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
        setupConfig();
        setupEvents();
//        setupClient();
    }

    public static Minecraft getMCI() {
        return Minecraft.getInstance();
    }

    private static void setupConfig() {
        LOGGER.info("Setting up commands...");
        GestureCommand.init();
        LOGGER.info("Setting up keybindings...");
        KeyMappingRegistry.register(MOD_KEY);
        LOGGER.info("Setting up config files...");
        File configFile = new File(Constants.CONFIG_PATH);
        File gestureStoreFile = new File(Constants.GESTURE_STORE_PATH);
        if (!configFile.exists())
            Config.writeConfig();
        if (!gestureStoreFile.exists())
            Config.writeGestureStore();
    }

    public static void setupClient() {
        Config config = Config.readConfig();
        KeyMapping[] keyMappings = getMCI().options.keyMappings;
        HashMap<String, String> gestureMappings = config.GESTURE_KEY_MAPPINGS;
        for (String gestureMapping : gestureMappings.values()) {
            for (KeyMapping keyMapping : keyMappings) {
                if (keyMapping.getName().equals(gestureMapping)) {
                    LOGGER.info("Adding gesture key mapping -> mapping name: " + gestureMapping + " | key name: " + keyMapping.saveString());
                    KEY_MAPPINGS.put(gestureMapping, keyMapping);
                }
            }
        }
    }

    private static void setupEvents() {
        LOGGER.info("Setting up events...");
        GestureEventHandler.init();
        TriggerEventHandler.init();
    }
}