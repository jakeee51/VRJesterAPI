package com.calicraft.vrjester.utils.vrdata;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class VRDataWriter {
    // Class for writing VRData to debug and analyze

    public int pose; // pose = 0 (position) | pose = 1 (direction)
    public String fileName;
    public Config config;
    public ArrayList<File> files = new ArrayList<>();

    public VRDataWriter(String mode, int iteration) { // Setup file objects to create & write VRDevice data
        config = Config.readConfig(Constants.DEV_CONFIG_PATH);
        String[] devices;
        File path = new File(Constants.DEV_ARCHIVE_PATH + String.format("/iteration_%s", iteration));
        path.mkdir();
        devices = config.LOG.devices;
        pose = config.LOG.pose;
        if (mode.equals("trace"))
            fileName = "trace_" + config.LOG.gesture;
        else
            fileName = mode + "_" + config.LOG.name;
        for (int i = 0; i < devices.length; i++) {
            String device = devices[i];
            if (path.exists())
                files.add(new File(path.getPath() + String.format("/%s_%s_%s.csv", device, fileName, iteration)));
        }
    }

    public void write(VRDataState record) throws IOException { // Write specified VRDataState data to end of file
        this.create(); String cleanData;
        for (File file: files) {
            cleanData = parse(record, file.getName().substring(0,3).replaceAll("_", ""), pose);
            System.out.println(file.getPath() + " -> " + cleanData);
            try (FileWriter writer = new FileWriter(file.getPath(), true)) {
                writer.write(cleanData + "\n");
                writer.flush();
            }
        }
    }

    public void write(String[] record) throws IOException { // Write data to end of file
        this.create(); String cleanData;
        for (File file: files) {
            cleanData = parse(record, file.getName().substring(0,3).replaceAll("_", ""));
            System.out.println(file.getPath() + " -> " + cleanData);
            try (FileWriter writer = new FileWriter(file.getPath(), true)) {
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
        String ret = switch (device) {
            case "hmd" -> record.getHmd()[pose].toString();
            case "rc" -> record.getRc()[pose].toString();
            case "lc" -> record.getLc()[pose].toString();
            case "c2" -> record.getC2()[pose].toString();
            default -> "N/A";
        };
        ret = ret.replaceAll("(\\(|\\)| )", "");
        return ret;
    }

    private String parse(String[] record, String device) { // Parse VRData for specified device & pose
        String ret = switch (device) {
            case "rc" -> record[0].toString();
            case "lc" -> record[1].toString();
            default -> "N/A";
        };
        ret = ret.replaceAll("(\\(|\\)| )", "");
        return ret;
    }
}
