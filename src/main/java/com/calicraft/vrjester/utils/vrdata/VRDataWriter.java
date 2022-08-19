package com.calicraft.vrjester.utils.vrdata;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class VRDataWriter {
    // Class for writing VRData to debug and analyze

    // TODO - write data in iterations
    //      - write data to dev/archive
    //      - make flag to disable writing
    //      - Setup Config.java

    public int pose; // pose = 0 (position) | pose = 1 (direction)
    public String fileName;
    public JSONObject config;
    public ArrayList<File> files = new ArrayList<>();

    public VRDataWriter(String fileName, String[] devices, int pose) { // Setup file objects to create & write VRDevice data
        this.fileName = fileName; this.pose = pose;
        for (String device : devices) {
            switch (device) {
                case "hmd":
                    System.out.println("HMD " + device);
                    files.add(new File("hmd_" + fileName + ".csv"));
                    break;
                case "rc":
                    System.out.println("RC " + device);
                    files.add(new File("rc_" + fileName + ".csv"));
                    break;
                case "lc":
                    System.out.println("LC " + device);
                    files.add(new File("lc_" + fileName + ".csv"));
                    break;
                case "c2":
                    files.add(new File("c2_" + fileName + ".csv"));
                    break;
                default:
                    System.out.println("HERE " + device);
                    System.err.println("Specified devices invalid: " + Arrays.toString(devices));
            }
        }
    }

    public VRDataWriter() { // Setup file objects to create & write Vox data
        readConfig("config/VRJesterAPI.cfg");
        fileName = config.getString("gesture");
        files.add(new File("vox_trace_data_" + fileName + ".csv"));
    }

    public void write(VRDataState record) throws IOException { // Write specified VRDataState data to end of file
        this.create(); String cleanData;
        for (File file: files) {
            cleanData = this.parse(record, file.getName().substring(0,3).replaceAll("_", ""), pose);
            System.out.println(file.getName() + " -> " + cleanData);
            try (FileWriter writer = new FileWriter(file.getName(), true)) {
                writer.write(cleanData + "\n");
                writer.flush();
            }
        }
    }

    public void write(String record) throws IOException { // Write Vox ID data to end of file
        this.create(); String cleanData;
        for (File file: files) {
            cleanData = record.replaceAll("\\[", "").replaceAll("\\]", "");
            System.out.println(file.getName() + " -> " + cleanData);
            try (FileWriter writer = new FileWriter(file.getName(), true)) {
                writer.write(cleanData + "\n");
                writer.flush();
            }
        }
    }

    private void create() throws IOException { // Create VRData file
        for (File file: files) {
            if (file.createNewFile())
                System.out.println(file.getName() + " file created!");
        }
    }

    private String parse(VRDataState record, String device, int pose) { // Parse VRData for specified device & pose
        String ret = "N/A";
        switch (device) {
            case "hmd":
                ret = record.getHmd()[pose].toString(); break;
            case "rc":
                ret = record.getRc()[pose].toString(); break;
            case "lc":
                ret = record.getLc()[pose].toString(); break;
            case "c2":
                ret = record.getC2()[pose].toString(); break;
        }
        ret = ret.replaceAll("(\\(|\\)| )", "");
        return ret;
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
