package com.calicraft.vrjester.utils.tools;

import com.calicraft.vrjester.handlers.TriggerEventHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import static com.calicraft.vrjester.VrJesterApi.VIVECRAFT_LOADED;

public class GestureCommand {
    // Class for registering the '/gesture' command

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (VIVECRAFT_LOADED) { // VR
            dispatcher.register(Commands.literal("gesture")
                    .then(Commands.literal("record")
                            .then(Commands.argument("value", BoolArgumentType.bool())
                                    .executes(c -> {
                                        TriggerEventHandler.oneRecorded = false;
                                        boolean mode = BoolArgumentType.getBool(c, "value");
                                        TriggerEventHandler.config.RECORD_MODE = mode;
                                        if (mode)
                                            TriggerEventHandler.sendDebugMsg("Record mode enabled.");
                                        else
                                            TriggerEventHandler.sendDebugMsg("Record mode disabled.");
                                        return 1;
                                    })
                            )
                            .executes(c -> {
                                TriggerEventHandler.oneRecorded = true;
                                TriggerEventHandler.config.RECORD_MODE = true;
                                TriggerEventHandler.sendDebugMsg("Record mode enabled. Only the next gesture will be recorded.");
                                return 1;
                            })
                    )
                    .then(Commands.literal("reload")
                            .executes(c -> {
                                TriggerEventHandler.gestures.load();
                                TriggerEventHandler.sendDebugMsg("Reloading gestures from file!");
                                return 1;
                            })
                    )
                    .then(Commands.literal("save")
                            .executes(c -> {
                                TriggerEventHandler.gestures.write();
                                TriggerEventHandler.sendDebugMsg("Writing all gestures to file!");
                                return 1;
                            })
                    )
                    .executes(c -> {
                        TriggerEventHandler.sendDebugMsg("Possible modes: record, reload, save");
                        return 1;
                    })
            );
        } else { // Non-VR
            dispatcher.register(Commands.literal("gesture")
                    .then(Commands.literal("record")
                            .then(Commands.argument("value", BoolArgumentType.bool())
                                    .executes(c -> {
                                        TriggerEventHandler.oneRecorded = false;
                                        boolean mode = BoolArgumentType.getBool(c, "value");
                                        if (mode)
                                            TriggerEventHandler.sendDebugMsg("Record mode enabled.");
                                        else
                                            TriggerEventHandler.sendDebugMsg("Record mode disabled.");
                                        return 1;
                                    })
                            )
                            .executes(c -> {
                                TriggerEventHandler.oneRecorded = true;
                                TriggerEventHandler.sendDebugMsg("Record mode enabled. Only the next gesture will be recorded.");
                                return 1;
                            })
                    )
                    .then(Commands.literal("reload")
                            .executes(c -> {
                                TriggerEventHandler.sendDebugMsg("Reloading gestures from file!");
                                return 1;
                            })
                    )
                    .then(Commands.literal("save")
                            .executes(c -> {
                                TriggerEventHandler.sendDebugMsg("Writing all gestures to file!");
                                return 1;
                            })
                    )
                    .executes(c -> {
                        TriggerEventHandler.sendDebugMsg("Possible modes: record, reload, save");
                        return 1;
                    })
            );
        }
    }
}
