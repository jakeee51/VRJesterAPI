package com.calicraft.vrjester.gesture;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.gesture.radix.Node;
import com.calicraft.vrjester.gesture.radix.Path;
import com.calicraft.vrjester.gesture.radix.RadixTree;
import com.calicraft.vrjester.utils.tools.GestureComponentDeserializer;
import com.calicraft.vrjester.utils.tools.GestureComponentSerializer;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Gestures {
    // Class for storing the gestures in a namespace for each VRDevice
    // The gesture store, stores gestures for reading and writing to/from JSON
    // The namespace determines what's recognized as a complete gesture
    // The mappings determine which devices recognize which gestures
    // The radix trees store the actual gestures

    private final File gestureStoreFile;
    public final GestureStore gestureStore = new GestureStore();
    public HashMap<String, String> gestureNameSpace = new HashMap<>();
    public HashMap<Integer, String> hmdGestureMapping = new HashMap<>();
    public HashMap<Integer, String> rcGestureMapping = new HashMap<>();
    public HashMap<Integer, String> lcGestureMapping = new HashMap<>();
    public RadixTree hmdGestures = new RadixTree(Constants.HMD);
    public RadixTree rcGestures = new RadixTree(Constants.RC);
    public RadixTree lcGestures = new RadixTree(Constants.LC);
    public HashMap<String, List<String>> eitherDeviceGestures = new HashMap<>();

    public Config config;

    public Gestures(Config config, String gesture_store_path) {
        this.config = config;
        gestureStoreFile = new File(gesture_store_path);
    }

    // Read in gestures from gesture store file and return GestureStore object
    public GestureStore read() {
        try {
            StringBuilder sb = new StringBuilder();
            Scanner myReader = new Scanner(gestureStoreFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                sb.append(data);
//                System.out.println("GESTURE MANIFEST: " + data);
            }
            myReader.close();
            GsonBuilder builder = new GsonBuilder();
//            builder.registerTypeAdapterFactory(new RecordTypeAdapterFactory());
            builder.registerTypeAdapter(GestureComponent.class, new GestureComponentDeserializer());
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            return gson.fromJson(sb.toString(), GestureStore.class);
        } catch (FileNotFoundException | JsonSyntaxException e) {
            System.out.println("An error occurred reading gesture store json!");
            e.printStackTrace();
        }
        return null;
    }

    // Load up all gestures from gesture store into the radix trees & namespaces
    public void load() {
        GestureStore gestureStore = read(); clear();
        if (gestureStore != null) {
            Set<String> gestureNames = gestureStore.GESTURES.keySet();
            for (String gestureName: gestureNames) { // Iterate through & store each gesture
                try {
                    Gesture gesture = new Gesture(gestureStore.GESTURES.get(gestureName));
                    store(gesture, gestureName);
                } catch (NullPointerException e) {
                    System.err.println(e);
                    System.out.println("SKIPPING LOADING GESTURE: " + gestureName);
                }
            }
        }
//        FOR DEBUGGING:
//        System.out.println("GESTURE NAMESPACE: " + gestureNameSpace);
//        System.out.println("LOADED GESTURES:");
//        hmdGestures.printAllGestures(hmdGestureMapping);
//        rcGestures.printAllGestures(rcGestureMapping);
//        lcGestures.printAllGestures(lcGestureMapping);
//        System.out.println("HMD TREE:");
//        hmdGestures.printAllPaths();
//        System.out.println("RC TREE:");
//        rcGestures.printAllPaths();
//        System.out.println("LC TREE:");
//        lcGestures.printAllPaths();
    }

    // Store a new gesture encompassing all VRDevices
    public void store(Gesture gesture, String name) {
        String id;
        StringBuilder sb = new StringBuilder();
        for (String vrDevice: Constants.DEVICES) {
            if (!gesture.validDevices.isEmpty()) { // Gestures for either OR, VRDevice storage instance handler
                eitherDeviceGestures.put(name, gesture.validDevices);
                String newId = storeToMapping(gesture, name, vrDevice);
                if (!sb.toString().contains(newId))
                    sb.append(newId);
            } else {
                sb.append(storeToMapping(gesture, name, vrDevice));
            }
        }
        id = sb.toString();
        gestureNameSpace.put(id, name);
    }

    // Store a new gesture into a specified VRDevice namespace
    public void store(RadixTree gestureTree, HashMap<Integer, String> gestureMapping,
                      List<GestureComponent> gesture, String name) {
        gestureTree.insert(gesture);
        gestureMapping.put(gesture.hashCode(), name);
        String id = "" + gesture.hashCode();
        gestureNameSpace.put(id, name);
    }

    public String storeToMapping(Gesture gesture, String name, String vrDevice) {
        String id = "";
        RadixTree gestureTree = getRadixTree(vrDevice);
        List<GestureComponent> deviceGesture = gesture.getGesture(vrDevice);
        HashMap<Integer, String> gestureMapping = getGestureMapping(vrDevice);
        if (!deviceGesture.isEmpty()) {
            gestureTree.insert(deviceGesture);
            if (!gestureMapping.containsKey(deviceGesture.hashCode()))
                gestureMapping.put(deviceGesture.hashCode(), name);
            id += deviceGesture.hashCode();
        }
        return id;
    }

    // Write all stored gestures to gesture store file
    public void write() {
        writeGestures(Constants.HMD, hmdGestures.root, new ArrayList<>());
        writeGestures(Constants.RC, rcGestures.root, new ArrayList<>());
        writeGestures(Constants.LC, lcGestures.root, new ArrayList<>());
        try (FileWriter writer = new FileWriter(gestureStoreFile.getPath())) {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(GestureComponent.class, new GestureComponentSerializer());
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            gson.toJson(gestureStore, writer);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Add each gesture to GestureStore object for writing to gesture store file
    private void writeGestures(String vrDevice, Node current, List<GestureComponent> result) {
        if (current.isGesture) {
            String gestureName = getGestureMapping(vrDevice).get(result.hashCode());
            gestureStore.addGesture(vrDevice, gestureName, result, eitherDeviceGestures.get(gestureName));
        }
        for (Path path : current.paths.values()) {
            writeGestures(vrDevice, path.next, GestureComponent.concat(result, path.gesture));
        }
    }

    // Reset the gestures namespace
    public void clear() {
        gestureNameSpace = new HashMap<>();
        hmdGestures = new RadixTree(Constants.HMD);
        rcGestures = new RadixTree(Constants.RC);
        lcGestures = new RadixTree(Constants.LC);
        hmdGestureMapping = new HashMap<>();
        rcGestureMapping = new HashMap<>();
        lcGestureMapping = new HashMap<>();
        eitherDeviceGestures = new HashMap<>();
    }

    // Return gesture mapping based on VRDevice
    private HashMap<Integer, String> getGestureMapping(String vrDevice) {
        HashMap<Integer, String> gestureMapping = new HashMap<>();
        switch(vrDevice) {
            case Constants.HMD:
                gestureMapping = hmdGestureMapping; break;
            case Constants.RC:
                gestureMapping = rcGestureMapping; break;
            case Constants.LC:
                gestureMapping = lcGestureMapping; break;
        }
        return gestureMapping;
    }

    private RadixTree getRadixTree(String vrDevice) {
        RadixTree gestureTree = null;
        switch(vrDevice) {
            case Constants.HMD:
                gestureTree = hmdGestures; break;
            case Constants.RC:
                gestureTree = rcGestures; break;
            case Constants.LC:
                gestureTree = lcGestures; break;
        }
        return gestureTree;
    }
}
