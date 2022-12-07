package com.calicraft.vrjester.gesture;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.gesture.radix.Node;
import com.calicraft.vrjester.gesture.radix.RadixTree;
import com.calicraft.vrjester.gesture.radix.Trace;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Gestures {
    // Class for storing the gestures
    public final HashMap<Integer, String> gestureMap = new HashMap<>();
    public final RadixTree hmdGestures = new RadixTree("HMD");
    public final RadixTree rcGestures = new RadixTree("RC");
    public final RadixTree lcGestures = new RadixTree("LC");
    private final File gestureManifestFile = new File(Constants.DEV_GESTURE_STORE_PATH);

    public Gestures() {}

    public void read() { // TODO - Finish this method
        try {
            StringBuilder sb = new StringBuilder();
            Scanner myReader = new Scanner(gestureManifestFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println("GESTURE MANIFEST: " + data);
            }
            myReader.close();
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            gson.fromJson(sb.toString(), Gestures.class); // Load Radix Tree's here
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred reading gesture manifest json!");
            e.printStackTrace();
        }
    }

    public void store(Gesture gesture, String name) {
        hmdGestures.insert(gesture.hmdGesture);
        rcGestures.insert(gesture.rcGesture);
        lcGestures.insert(gesture.lcGesture);
        gestureMap.put(gesture.hmdGesture.hashCode(), name);
        gestureMap.put(gesture.rcGesture.hashCode(), name);
        gestureMap.put(gesture.lcGesture.hashCode(), name);
    }

    public void write() {
        writeGestures(hmdGestures.root, new ArrayList<>());
        writeGestures(rcGestures.root, new ArrayList<>());
        writeGestures(lcGestures.root, new ArrayList<>());
    }

    private void writeGestures(Node current, List<Path> result) { // TODO - Write with Gson
        if (current.isGesture) {
            try (FileWriter writer = new FileWriter(gestureManifestFile.getPath(), true)) {
                writer.write(result + "\n");
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for (Trace trace : current.paths.values()) {
            writeGestures(trace.next, Path.concat(result, trace.path));
        }
    }
}
