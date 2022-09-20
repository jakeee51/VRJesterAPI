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

    // TODO - write data in iterations per trigger

    public int pose; // pose = 0 (position) | pose = 1 (direction)
    public String fileName;
    public JSONObject config;
    public ArrayList<File> files = new ArrayList<>();

    public VRDataWriter(JSONObject config) { // Setup file objects to create & write VRDevice data
        this.config = config; JSONArray devices = new JSONArray();
        String path = Constants.DEV_ARCHIVE_PATH;
        try {
            if (config.has("log")) {
                JSONObject logConfig = config.getJSONObject("log");
                this.fileName = logConfig.getString("name");
                this.pose = logConfig.getInt("pose");
                devices = logConfig.getJSONArray("devices");
            }
        } catch (NullPointerException e) {
            this.fileName = "VRJester_Data";
            this.pose = 0;
            devices.put("rc");
            System.err.println(e);
        }

        for (int i = 0; i < devices.length(); i++) {
            String device = devices.getString(i);
            switch (device) {
                case "hmd":
                    files.add(new File(path + "hmd_" + fileName + ".csv"));
                    break;
                case "rc":
                    files.add(new File(path + "rc_" + fileName + ".csv"));
                    break;
                case "lc":
                    files.add(new File(path + "lc_" + fileName + ".csv"));
                    break;
                case "c2":
                    files.add(new File(path + "c2_" + fileName + ".csv"));
                    break;
                default:
                    System.err.println("Specified devices invalid: " + devices.toString());
            }
        }
    }

    public VRDataWriter() { // Setup file objects to create & write Vox data
        config = new Config().readConfig();
        try {
            fileName = config.getJSONObject("log").getString("gesture");
        } catch (NullPointerException e) {
            System.err.println(e);
        } catch (JSONException e) {
            System.err.println(e);
        }
        files.add(new File(Constants.DEV_ARCHIVE_PATH + "vox_trace_data_" + fileName + ".csv"));
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
