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

    // TODO - Make way to write direction


    String fileName;
    JSONObject config;
    ArrayList<File> files = new ArrayList<>();
    public VRDataWriter(String fileName, String[] devices) { // Setup file objects to create & write VRDevice data
        this.fileName = fileName;
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
        readConfig("src/main/resources/config.json");
        fileName = config.getString("gesture");
        files.add(new File("vox_trace_data_" + fileName + ".csv"));
    }

    public void write(VRDataState record) throws IOException { // Write specified VRDataState data to end of file
        this.create(); String cleanData;
        for (File file: files) {
            cleanData = this.clean(record, file.getName().substring(0,3).replaceAll("_", ""));
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

    private String clean(VRDataState record, String device) { // Clean VRData for specified device
        String ret = "N/A";
        switch (device) {
            case "hmd":
                ret = record.getHmd().toString(); break;
            case "rc":
                ret = record.getRc().toString(); break;
            case "lc":
                ret = record.getLc().toString(); break;
            case "c2":
                ret = record.getC2().toString(); break;
        }
        ret = ret.replaceAll("(Device: pos:\\(|\\) dir: \\(.+| )", "");
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
