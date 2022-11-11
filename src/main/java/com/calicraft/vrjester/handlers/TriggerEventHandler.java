package com.calicraft.vrjester.handlers;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.gesture.Gesture;
import com.calicraft.vrjester.tracker.PositionTracker;
import com.calicraft.vrjester.utils.vrdata.*;
import com.calicraft.vrjester.vox.Vox;
import com.minecraftserverzone.harrypotter.setup.Registrations;
import com.minecraftserverzone.harrypotter.setup.capabilities.PlayerStatsProvider;
import com.minecraftserverzone.harrypotter.setup.network.Networking;
import com.minecraftserverzone.harrypotter.setup.network.PacketSpells;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.*;
import java.io.IOException;

import static com.calicraft.vrjester.VrJesterApi.*;
import static com.calicraft.vrjester.utils.tools.SpawnParticles.moveParticles;


public class TriggerEventHandler {
    private static Config config = Config.readConfig(Constants.DEV_CONFIG_PATH);
    private static final VRDataAggregator preRoomDataAggregator = new VRDataAggregator(VRDataType.VRDATA_ROOM_PRE, false);
    private static final VRDataAggregator preWorldDataAggregator = new VRDataAggregator(VRDataType.VRDATA_WORLD_PRE, false);
    private static VRDataWriter vrDataWriter;
    private static VRDataWriter voxDataWriter;
    private static final int DELAY = 20; // 1 second
    private static int sleep = 2 * DELAY; // 2 seconds
    private static int iter = 0;
    private static boolean listener = false, toggled = false;
    private long elapsedTime = 0;

    private static Vox displayRCVox, displayLCVox;
    private static Gesture gesture;
    private static LocalPlayer player;

    @SubscribeEvent
    public void onJesterTrigger(InputEvent event) throws AWTException {
        if (player == null) {
            player = getMCI().player;
            try {
                VIVECRAFTLOADED = PositionTracker.vrAPI.playerInVR(player);
            } catch (NullPointerException e) {
                System.out.println("Threw NullPointerException trying to call IVRAPI.playerInVR");
            }
            return;
        }
        // Trigger the gesture listening phase
        if (VIVECRAFTLOADED) {
            if (VrJesterApi.MOD_KEY.isDown() && !listener) {
                toggleBattleStance();
                System.out.println("JESTER TRIGGERED");
                listener = true; elapsedTime = System.nanoTime();
                config = Config.readConfig(Constants.DEV_CONFIG_PATH);
                vrDataWriter = new VRDataWriter("room", iter);
                voxDataWriter = new VRDataWriter("vox", iter);
            } else {
                System.out.println("JESTER RELEASED");
                listener = false;
                elapsedTime = System.nanoTime() - elapsedTime;
                gesture = null;
                elapsedTime = 0;
                if (config.WRITE_DATA)
                    iter++;
                else
                    iter = 0;
            }
        }
//        else {
//            if (VrJesterApi.MOD_KEY.isDown() && !toggled) {
//                toggled = true;
//                toggleBattleStance();
//                selectSpell(1);
//                KeyMapping.click(InputConstants.Type.MOUSE.getOrCreate(1));
//                toggleBattleStance();
//                System.out.println("TRIGGERED");
//            } else {
//                toggled = false;
//            }
//        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) throws IOException {
        if (VrJesterApi.MOD_KEY.isDown() && !VIVECRAFTLOADED)
            moveParticles(ParticleTypes.FLAME, 0);

        if (listener) { // Capture VR data in real time after trigger
            VRDataState vrDataRoomPre = preRoomDataAggregator.listen();
            VRDataState vrDataWorldPre = preWorldDataAggregator.listen();
            if (gesture == null) {
                gesture = new Gesture(vrDataRoomPre);
                traceDebugger(gesture.rcGesture);
//                displayRCDebugger(vrDataWorldPre, VRDevice.RC, true);
//                displayLCDebugger(vrDataWorldPre, VRDevice.LC, true);
            } else {
                gesture.track(vrDataRoomPre, vrDataWorldPre);
                gesture.recognizeTest(vrDataWorldPre);
                traceDebugger(gesture.rcGesture);
//                displayRCDebugger(vrDataWorldPre, VRDevice.RC, false);
//                displayLCDebugger(vrDataWorldPre, VRDevice.LC, false);
//                dataDebugger(vrDataRoomPre);
            }

//            if (sleep % 20 == 0) // Print every 1 second
//                System.out.println("JESTER LISTENING");
//            if (sleep == 0) { // Reset trigger when done
//                System.out.println("JESTER DONE LISTENING");
//                sleep = 2 * DELAY;
//                data.clear(); listener = false;
//            }
//            sleep--;
        }
    }

    public static void selectSpell(int i) {
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAPABILITY).ifPresent((h) -> {
            if (h.getBattleTick() == 1) {
                h.setSelectedHotbar(i);
                Networking.sendToServer(new PacketSpells(100 + h.getSelectedHotbar()));
                if (h.getSelectedHotbar() < 0) {
                    h.setSelectedHotbar(8);
                    Networking.sendToServer(new PacketSpells(108));
                } else if (h.getSelectedHotbar() > 8) {
                    h.setSelectedHotbar(0);
                    Networking.sendToServer(new PacketSpells(100));
                }
            }
        });
    }

    public static void toggleBattleStance() {
        Options keys = getMCI().options;
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAPABILITY).ifPresent((h) -> {
            if (h.getBattleTick() == 0) {
                if (player.getMainHandItem().is((Item) Registrations.APPRENTICE_WAND.get())) {
                    h.setSelectedHotbar(player.getUseItem().getCount());
                    Networking.sendToServer(new PacketSpells(100 + h.getSelectedHotbar()));
                    h.setHotbarBeforeBattleStance(player.getUseItem().getCount());
                    h.setBattleTick(1);
                }
            } else {
                h.setBattleTick(0);
            }
        });

        for(int i = 0; i < 9; ++i) {
            if (keys.keyHotbarSlots[i].isDown()) {
                int finalI = i;
                player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAPABILITY).ifPresent((h) -> {
                    if (h.getBattleTick() == 1) {
                        h.setSelectedHotbar(finalI);
                        Networking.sendToServer(new PacketSpells(100 + h.getSelectedHotbar()));
                        keys.keyHotbarSlots[finalI].consumeClick();
                    }
                });
            }
        }
    }

    public static void displayRCDebugger(VRDataState vrDataState, VRDevice vrDevice, boolean init) { // For VRData World
        Vec3[] displayOrigin, hmdOrigin = vrDataState.getHmd();
        if (config.DISPLAY_VOX) {
            if (init) {
                displayOrigin = VRDataState.getVRDevicePose(vrDataState, vrDevice);
                displayRCVox = new Vox(Constants.RC, vrDevice, displayOrigin, hmdOrigin[1], true);
            } else {
                displayRCVox.manifestVox(VRDataState.getVRDevicePose(vrDataState, vrDevice, 0));
            }
        }
    }

    public static void displayLCDebugger(VRDataState vrDataState, VRDevice vrDevice, boolean init) { // For VRData World
        Vec3[] displayOrigin, hmdOrigin = vrDataState.getHmd();
        if (config.DISPLAY_VOX) {
            if (init) {
                displayOrigin = VRDataState.getVRDevicePose(vrDataState, vrDevice);
                displayLCVox = new Vox(Constants.LC, vrDevice, displayOrigin, hmdOrigin[1], true);
            } else {
                displayLCVox.manifestVox(VRDataState.getVRDevicePose(vrDataState, vrDevice, 0));
            }
        }
    }

    public static void dataDebugger(VRDataState vrDataState) throws IOException { // For VRData Room
        if (config.WRITE_DATA)
            vrDataWriter.write(vrDataState);
    }

    public static void traceDebugger(String data) throws IOException { // For Vox Data
        if (config.WRITE_DATA)
            voxDataWriter.write(data);
    }

}
