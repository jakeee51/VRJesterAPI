package com.calicraft.vrjester.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Config {
    public String RECOGNIZE_ON = Constants.RECOGNIZE_ON;
    public boolean RECORD_MODE = Constants.RECORD_MODE;
    public boolean READ_DATA = Constants.READ_DATA;
    public boolean WRITE_DATA = Constants.WRITE_DATA;
    public boolean DISPLAY_VOX = Constants.DISPLAY_VOX;
    public float VOX_LENGTH = Constants.VOX_LENGTH;
    public int VOX_GRID_LENGTH = Constants.VOX_GRID_LENGTH;
    public int VOX_GRID_WIDTH = Constants.VOX_GRID_WIDTH;
    public int VOX_GRID_HEIGHT = Constants.VOX_GRID_HEIGHT;
    public float MAX_LISTENING_TIME = Constants.MAX_LISTENING_TIME;
    public HashMap<String, GestureContext> GESTURES = new HashMap<>();
    public Log LOG = new Log();

    public class Log {
        // Class that represents log configuration
        public String name;
        public String gesture;
        public int pose;
        public String[] devices = new String[]{};
    }

    public class GestureContext {
        // Class that represents a gesture event configuration
        public double velocity;
        public int rcParticle;
        public int lcParticle;
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
            System.out.println("An error occurred reading config json!");
            e.printStackTrace();
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
            System.out.println("An error occurred reading config json!");
            e.printStackTrace();
        }
        return readConfig(); // Use default Minecraft config path
    }
}
