package com.calicraft.vrjester.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class VRDataWriter {
    // Class for writing VRData to debug and analyze

    String file_name;
    ArrayList<File> files = new ArrayList<>();

    public VRDataWriter(String file_name, String[] devices) { // Setup file objects to create & write to
        this.file_name = file_name; File file;
        for (String device : devices) {
            switch (device) {
                case "hmd":
                    System.out.println("HMD " + device);
                    files.add(new File("hmd_" + file_name + ".csv"));
                    break;
                case "rc":
                    System.out.println("RC " + device);
                    files.add(new File("rc_" + file_name + ".csv"));
                    break;
                case "lc":
                    System.out.println("LC " + device);
                    files.add(new File("lc_" + file_name + ".csv"));
                    break;
                case "c2":
                    files.add(new File("c2_" + file_name + ".csv"));
                    break;
                default:
                    System.out.println("HERE " + device);
                    System.err.println("Specified devices invalid: " + Arrays.toString(devices));
            }
        }
    }

    public void write(VRDataState record) throws IOException { // Write specified data to end of file
        this.create(); String clean_data;
        for (File file: files) {
            clean_data = this.clean(record, file.getName().substring(0,3).replaceAll("_", ""));
            System.out.println(file.getName() + " -> " + clean_data);
            try (FileWriter writer = new FileWriter(file.getName(), true)) {
                writer.write(clean_data + "\n");
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
}
