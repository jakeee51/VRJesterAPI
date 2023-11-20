package com.calicraft.vrjester.config;

import com.calicraft.vrjester.gesture.Gesture;
import com.calicraft.vrjester.gesture.GestureComponent;
import com.calicraft.vrjester.handlers.TriggerEventHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.util.math.vector.Vector3d;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Config {
    public String RECOGNIZE_ON = Constants.RECOGNIZE_ON;
    public String GESTURE_NAME = Constants.SAMPLE_GESTURE_NAME;
    public boolean RECORD_MODE = Constants.RECORD_MODE;
    public boolean READ_DATA = Constants.READ_DATA;
    public boolean WRITE_DATA = Constants.WRITE_DATA;
    public boolean DEMO_MODE = Constants.DEMO_MODE;
    public boolean DEBUG_MODE = Constants.DEBUG_MODE;
    public float VIRTUAL_SPHERE_RADIUS = Constants.VIRTUAL_SPHERE_RADIUS;
    public int INTERVAL_DELAY = Constants.INTERVAL_DELAY;
    public int MAX_LISTENING_TIME = Constants.MAX_LISTENING_TIME;
    public HashMap<String, String> GESTURE_KEY_MAPPINGS = new HashMap<>();
    public HashMap<String, ParticleContext> TESTING_GESTURES = new HashMap<>();
//    public Log LOG = new Log();

    public class Log {
        // Class that represents log configuration
        public String name;
        public String gesture;
        public int pose;
        public String[] devices = new String[]{};
    }

    public class ParticleContext {
        // Class that represents a gesture event configuration
        public double velocity;
        public int rcParticle;
        public int lcParticle;

        public ParticleContext() {
            this.velocity = 1.0;
            this.rcParticle = -1;
            this.lcParticle = -1;
        }

        public ParticleContext(double velocity, int rcParticle, int lcParticle) {
            this.velocity = velocity;
            this.rcParticle = rcParticle;
            this.lcParticle = lcParticle;
        }
    }

    public static Config readConfig() {
        try {
            StringBuilder sb = new StringBuilder();
            File configFile = new File(Constants.CONFIG_PATH);
            Scanner myReader = new Scanner(configFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                sb.append(data);
//                System.out.println("CONFIG: " + data);
            }
            myReader.close();
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            return gson.fromJson(sb.toString(), Config.class);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred reading config json! Attempting to generate new config...");
            writeConfig();
        }
        return new Config();
    }

    public static Config readConfig(String configPath) {
        try {
            StringBuilder sb = new StringBuilder();
            File configFile = new File(configPath);
            Scanner myReader = new Scanner(configFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                sb.append(data);
//                System.out.println("CONFIG: " + data);
            }
            myReader.close();
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            return gson.fromJson(sb.toString(), Config.class);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred reading config json! Attempting to generate new config...");
            writeConfig();
        }
        return readConfig(); // Use default Minecraft config path
    }

    public static void writeConfig() {
        try {
            Config config = new Config();
            File configFile = new File(Constants.CONFIG_PATH);
            ParticleContext strikeContext = config.new ParticleContext(1.0, 0, 0);
            ParticleContext burstContext = config.new ParticleContext(1.0, 3, 3);
            ParticleContext uppercutContext = config.new ParticleContext(0.25, 3, 3);
            config.GESTURE_KEY_MAPPINGS.put("GESTURE 1", "examplemod.key.ability_1");
            config.TESTING_GESTURES.put("STRIKE", strikeContext);
            config.TESTING_GESTURES.put("BURST", burstContext);
            config.TESTING_GESTURES.put("UPPERCUT", uppercutContext);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter(configFile);
            gson.toJson(config, writer);
            writer.flush();
        } catch (IOException e) {
            System.out.println("An error occurred writing config json!");
            e.printStackTrace();
        }
    }

    public static void writeConfig(Config config) {
        try {
            File configFile = new File(Constants.CONFIG_PATH);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter(configFile);
            gson.toJson(config, writer);
            writer.flush();
        } catch (IOException e) {
            System.out.println("An error occurred writing config json!");
            e.printStackTrace();
        }
    }

    public static void writeGestureStore() {
        List<GestureComponent> hmdGesture = new ArrayList<>();
        List<GestureComponent> rcGesture = new ArrayList<>();
        List<GestureComponent> rcGesture2 = new ArrayList<>(); // To reproduce null error, use same rcGesture object
        List<GestureComponent> rcGesture3 = new ArrayList<>();
        List<GestureComponent> lcGesture = new ArrayList<>();
        Vector3d dir = new Vector3d((0),(0),(0));
        HashMap<String, Integer> devices = new HashMap<>();

        GestureComponent gestureComponent1 = new GestureComponent(Constants.RC, "forward",
                0, 0.0, dir, devices);
        GestureComponent gestureComponent2 = new GestureComponent(Constants.RC, "up",
                0, 0.0, dir, devices);
        GestureComponent gestureComponent3 = new GestureComponent(Constants.RC, "forward",
                2000, 0.0, dir, devices);
        GestureComponent gestureComponent4 = new GestureComponent(Constants.LC, "forward",
                2000, 0.0, dir, devices);

        rcGesture.add(gestureComponent1);
        Gesture strikeGesture = new Gesture(hmdGesture, rcGesture, lcGesture);
        strikeGesture.validDevices.add(Constants.RC);
        strikeGesture.validDevices.add(Constants.LC);
        TriggerEventHandler.gestures.store(strikeGesture, "STRIKE");
        rcGesture2.add(gestureComponent1);
        rcGesture2.add(gestureComponent2);
        Gesture uppercutGesture = new Gesture(hmdGesture, rcGesture2, lcGesture);
        uppercutGesture.validDevices.add(Constants.RC);
        uppercutGesture.validDevices.add(Constants.LC);
        TriggerEventHandler.gestures.store(uppercutGesture, "UPPERCUT");
        rcGesture3.add(gestureComponent3);
        lcGesture.add(gestureComponent4);
        Gesture burstGesture = new Gesture(hmdGesture, rcGesture3, lcGesture);
        TriggerEventHandler.gestures.store(burstGesture, "BURST");
        TriggerEventHandler.gestures.write();
    }
}
