package com.calicraft.vrjester.utils.tools;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.handlers.TriggerEventHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import static com.calicraft.vrjester.VrJesterApi.LOGGER;
import static com.calicraft.vrjester.VrJesterApi.VIVECRAFT_LOADED;

public class GestureCommand {
    // Class for registering the '/gesture' command
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("gesture")
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
