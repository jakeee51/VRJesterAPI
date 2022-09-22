package com.calicraft.vrjester.utils.vrdata;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class VRDataWriter {
    // Class for writing VRData to debug and analyze

    public int pose; // pose = 0 (position) | pose = 1 (direction)
    public String fileName;
    public JSONObject config;
    public ArrayList<File> files = new ArrayList<>();

    public VRDataWriter(String mode, int iteration) { // Setup file objects to create & write VRDevice data
        config = new Config(Constants.DEV_CONFIG_PATH).readConfig();
        JSONArray devices = new JSONArray();
        File path = new File(Constants.DEV_ARCHIVE_PATH + String.format("/iteration_%s", iteration));
        boolean folderCreated = path.mkdir();
        try {
            if (config.has("log")) {
                JSONObject logConfig = config.getJSONObject("log");
                devices = logConfig.getJSONArray("devices");
                pose = logConfig.getInt("pose");
                if (mode.equals("vox"))
                    fileName = "vox_trace_" + logConfig.getString("gesture");
                else
                    fileName = mode + "_" + logConfig.getString("name");
            }
        } catch (NullPointerException | JSONException e) {
            System.err.println(e);
        }
        for (int i = 0; i < devices.length(); i++) {
            String device = devices.getString(i);
            if (folderCreated)
                files.add(new File(path.getPath() + String.format("/%s_%s_%s.csv", device, fileName, iteration)));
        }
    }

    public void write(VRDataState record) throws IOException { // Write specified VRDataState data to end of file
        this.create(); String cleanData;
        for (File file: files) {
            cleanData = this.parse(record, file.getName().substring(0,3).replaceAll("_", ""), pose);
            System.out.println(file.getPath() + " -> " + cleanData);
            try (FileWriter writer = new FileWriter(file.getPath(), true)) {
                writer.write(cleanData + "\n");
                writer.flush();
            }
        }
    }

    public void write(String record) throws IOException { // Write Vox ID data to end of file
        this.create(); String cleanData;
        for (File file: files) {
            cleanData = record.replaceAll("\\[", "").replaceAll("\\]", "");
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
}
