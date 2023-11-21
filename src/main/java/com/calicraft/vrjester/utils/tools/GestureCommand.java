package com.calicraft.vrjester.utils.tools;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.handlers.TriggerEventHandler;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.calicraft.vrjester.VrJesterApi.VIVECRAFT_LOADED;
import static com.calicraft.vrjester.VrJesterApi.LOGGER;

@Mod.EventBusSubscriber
public class GestureCommand {
    // Class for registering the '/gesture' command

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        LOGGER.info("Setting up commands...");
        event.getDispatcher().register(Commands.literal("gesture")
                .then(Commands.literal("record")
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(c -> record(BoolArgumentType.getBool(c, "value")))
                        )
                        .executes(c -> record())
                )
                .then(Commands.literal("reload")
                        .executes(c -> reload())
                )
                .then(Commands.literal("save")
                        .executes(c -> save())
                )
                .executes(c -> {
                    TriggerEventHandler.sendDebugMsg("Possible modes: record, reload, save");
                    return 1;
                })
        );
    }

    private static int record(boolean mode) {
        if (VIVECRAFT_LOADED)
            TriggerEventHandler.config.RECORD_MODE = mode;
        TriggerEventHandler.oneRecorded = false;
        Config.writeConfig(TriggerEventHandler.config);
        if (mode)
            TriggerEventHandler.sendDebugMsg("Record mode enabled.");
        else
            TriggerEventHandler.sendDebugMsg("Record mode disabled.");
        return 1;
    }

    private static int record() {
        if (VIVECRAFT_LOADED)
            TriggerEventHandler.config.RECORD_MODE = true;
        TriggerEventHandler.oneRecorded = true;
        Config.writeConfig(TriggerEventHandler.config);
        TriggerEventHandler.sendDebugMsg("Record mode enabled. Only the next gesture will be recorded.");
        return 1;
    }

    private static int reload() {
        if (VIVECRAFT_LOADED)
            TriggerEventHandler.gestures.load();
        TriggerEventHandler.config = Config.readConfig();
        TriggerEventHandler.sendDebugMsg("Reloading gestures & config from files!");
        return 1;
    }

    private static int save() {
        if (VIVECRAFT_LOADED)
            TriggerEventHandler.gestures.write();
        TriggerEventHandler.sendDebugMsg("Writing all gestures to file!");
        return 1;
    }
}