package com.calicraft.vrjester.config;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Config {
    // Read in from config file
    // Initialize class in VrJesterApi
    // Refer to this when captured a gesture.

    public JSONObject config;

    public Config() {}

    public Config(String configPath) {
        readConfig(configPath);
    }

    public void readConfig(String fileName) {
        StringBuilder sb = new StringBuilder();
        try {
            File configFile = new File(fileName);
            Scanner myReader = new Scanner(configFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                sb.append(data);
                System.out.println("CONFIG: " + data);
            }
            myReader.close();
            config = new JSONObject(sb.toString());
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred!");
            e.printStackTrace();
        }
    }

}
