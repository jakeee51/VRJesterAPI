package com.calicraft.vrjester;

import com.calicraft.vrjester.handlers.TriggerEventHandler;
import com.calicraft.vrjester.tracker.PositionTracker;
import com.calicraft.vrjester.utils.tools.EventsLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("vrjester")
public class VrJesterApi {
    // Main entry point
    private static final Logger LOGGER = LogManager.getLogger();
    public static PositionTracker TRACKER;
    public static boolean VIVECRAFTLOADED = false;
    public static final String MOD_ID = "vrjester";
    public static final KeyMapping MOD_KEY = new KeyMapping("key.vrjester.71", 71, MOD_ID);

    public VrJesterApi() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerBindings);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        // Register deferred register for custom particles
//        PARTICLES.register(FMLJavaModLoadingContext.get().getModEventBus());
        try { // Check if Vivecraft is running
            Class.forName("org.vivecraft.api.VRData");
            VIVECRAFTLOADED = true;
            System.out.println("Vivecraft has been loaded!");
            EventsLoader.register();
            LOGGER.info("Events have been loaded!");
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            LOGGER.error("Vivecraft has failed to load!");
        }
        MinecraftForge.EVENT_BUS.register(new TriggerEventHandler());
    }

    public static Minecraft getMCI() {
        return VrJesterApi.getMCI();
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
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

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Get game settings: ");
    }

    public void registerBindings(RegisterKeyMappingsEvent event) {
        LOGGER.info("Registering KeyMappings");
        event.register(MOD_KEY);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
