package com.calicraft.vrjester;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vivecraft.api.VRData;
import org.vivecraft.gameplay.VRPlayer;

import java.lang.reflect.*;
import java.util.stream.Collectors;

import static org.vivecraft.gameplay.screenhandlers.GuiHandler.mc;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("vrjester")
public class VrJesterApi
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    private boolean isVivecraftLoaded = false;

    public VrJesterApi() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        try {
//            Class<?> vrData = Class.forName("org.vivecraft.api.VRData");
            Class<?> vrPlayer = Class.forName("org.vivecraft.gameplay.VRPlayer");
            isVivecraftLoaded = true;
            System.out.println(vrPlayer.getName() + " class has been loaded!");
            Field[] vrpf = vrPlayer.getFields();
            System.out.println("VRPlayer Field 1: " + vrpf[0].toString());
            Field vrdata_room_pre = vrPlayer.getDeclaredField("vrdata_room_pre");
            vrdata_room_pre.setAccessible(true);
            System.out.println("HERE 1");
            Field[] devices = vrdata_room_pre.getClass().getFields();
            for (Field device : devices) {
                System.out.println("VIVECRAFT FIELD: " + device);
            }
            Field right_controller = vrdata_room_pre.getClass().getDeclaredField("c0");
            right_controller.setAccessible(true);
            System.out.println("HERE 2");
            Method getPos = right_controller.getClass().getMethod("getPosition");
            getPos.setAccessible(true);
            System.out.println("HERE 3");
            Vector3d rc_pos = (Vector3d) getPos.invoke(right_controller);
            System.out.println("RIGHT CONTROLLER POS: " + rc_pos.toString());
//            mc.vrPlayer.vrdata_room_pre.c0.getPosition();
//            VRPlayer.get().vrdata_room_pre.c0.getPosition();
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            LOGGER.error("Failed to load Vivecraft class!");
        } catch (InvocationTargetException e) {
            LOGGER.error("Failed to invoke Vivecraft method!");
        } catch (IllegalAccessException e) {
            LOGGER.error("Failed to access Vivecraft!");
        } catch (NoSuchFieldException e) {
            LOGGER.error("Failed to get Vivecraft field!");
        } catch (NoSuchMethodException e) {
            LOGGER.error("Failed to get Method Vivecraft!");
        }

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().options);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("vrjesterapi", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }
}
