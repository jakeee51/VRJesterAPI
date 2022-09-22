package com.calicraft.vrjester.config;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Config {

    public String configPath = Constants.CONFIG_PATH;

    public Config() {}

    public Config(String configPath) {
        this.configPath = configPath;
    }

    public JSONObject readConfig() {
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
            return new JSONObject(sb.toString());
//            return new JSONObject(Files.readString(Path.of(configPath)));
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred!");
            e.printStackTrace();
        }
//        } catch (IOException e) {
//            System.out.println("An error occurred!");
//            e.printStackTrace();
//        }
        return new JSONObject();
    }

}
