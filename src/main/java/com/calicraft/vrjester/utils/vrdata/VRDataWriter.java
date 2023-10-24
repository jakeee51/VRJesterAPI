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

    // Setup file objects to create & write VRDevice data
    public VRDataWriter(String mode, int iteration) {
        config = Config.readConfig();
        String[] devices;
        File path = new File(Constants.DEV_ARCHIVE_PATH + String.format("/iteration_%s", iteration));
        path.mkdir();
        devices = config.LOG.devices;
        pose = config.LOG.pose;
        if (mode.equals("trace"))
            fileName = "trace_" + config.LOG.gesture;
        else
            fileName = mode + "_" + config.LOG.name;
        for (String device : devices) {
            if (path.exists())
                files.add(new File(path.getPath() + String.format("/%s_%s_%s.csv", device, fileName, iteration)));
        }
    }

    // Write specified VRDataState data to end of file
    public void write(VRDataState record) throws IOException {
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

    // Write data to end of file
    public void write(String[] record) throws IOException {
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

    // Create VRData file
    private void create() throws IOException {
        for (File file: files) {
            if (file.createNewFile())
                System.out.println(file.getName() + " file created!");
        }
    }

    // Parse VRData for specified device & pose
    private String parse(VRDataState record, String device, int pose) {
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

    // Parse VRData for specified device & pose
    private String parse(String[] record, String device) {
        String ret = switch (device) {
            case "rc" -> record[0];
            case "lc" -> record[1];
            default -> "N/A";
        };
        ret = ret.replaceAll("(\\(|\\)| )", "");
        return ret;
    }
}
