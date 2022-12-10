package com.calicraft.vrjester.gesture;

import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.gesture.radix.Node;
import com.calicraft.vrjester.gesture.radix.RadixTree;
import com.calicraft.vrjester.gesture.radix.Trace;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Gestures {
    // Class for storing the gestures
    public final GestureStore gestureStore = new GestureStore();
    public final HashMap<Integer, String> gestureMapping = new HashMap<>();
    public final RadixTree hmdGestures = new RadixTree("HMD");
    public final RadixTree rcGestures = new RadixTree("RC");
    public final RadixTree lcGestures = new RadixTree("LC");
    private final File gestureManifestFile = new File(Constants.DEV_GESTURE_STORE_PATH);

    public Gestures() {}

    public GestureStore read() {
        try {
            StringBuilder sb = new StringBuilder();
            Scanner myReader = new Scanner(gestureManifestFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                sb.append(data);
//                System.out.println("GESTURE MANIFEST: " + data);
            }
            myReader.close();
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapterFactory(new RecordTypeAdapterFactory());
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            return gson.fromJson(sb.toString(), GestureStore.class);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred reading gesture manifest json!");
            e.printStackTrace();
        }
        return null;
    }

    public void load() { // Load up all gestures from gesture store into radix trees to be ready for use
        GestureStore gestureStore = read();
        if (gestureStore != null) {
            for (String gestureName: gestureStore.HMD.keySet()) {
                store(hmdGestures, gestureStore.HMD.get(gestureName), gestureName);
            }
            for (String gestureName: gestureStore.RC.keySet()) {
                store(rcGestures, gestureStore.RC.get(gestureName), gestureName);
            }
            for (String gestureName: gestureStore.LC.keySet()) {
                store(lcGestures, gestureStore.LC.get(gestureName), gestureName);
            }
        }
        hmdGestures.printAllGestures();
        rcGestures.printAllGestures();
        lcGestures.printAllGestures();
    }

    public void store(RadixTree gestureTree, List<Path> gesture, String name) {
        gestureTree.insert(gesture);
        gestureMapping.put(gesture.hashCode(), name);
    }

    public void store(Gesture gesture, String name) {
        hmdGestures.insert(gesture.hmdGesture);
        rcGestures.insert(gesture.rcGesture);
        lcGestures.insert(gesture.lcGesture);
        gestureMapping.put(gesture.hmdGesture.hashCode(), name);
        gestureMapping.put(gesture.rcGesture.hashCode(), name);
        gestureMapping.put(gesture.lcGesture.hashCode(), name);
    }

    public void write() {
        writeGestures("HMD", hmdGestures.root, new ArrayList<>());
        writeGestures("RC", rcGestures.root, new ArrayList<>());
        writeGestures("LC", lcGestures.root, new ArrayList<>());
        try (FileWriter writer = new FileWriter(gestureManifestFile.getPath())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(gestureStore, writer);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeGestures(String vrDevice, Node current, List<Path> result) { // TODO - Write per device and use gesture name as key
        if (current.isGesture) {
            System.out.println(result);
            gestureStore.addGesture(vrDevice, gestureMapping.get(result.hashCode()), result);
        }

        for (Trace trace : current.paths.values()) {
            writeGestures(vrDevice, trace.next, Path.concat(result, trace.path));
        }
    }

    public static class RecordTypeAdapterFactory implements TypeAdapterFactory { // Gson 2.10 workaround for Java records

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            @SuppressWarnings("unchecked")
            Class<T> clazz = (Class<T>) type.getRawType();
            if (!clazz.isRecord()) {
                return null;
            }
            TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

            return new TypeAdapter<T>() {
                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    delegate.write(out, value);
                }

                @Override
                public T read(JsonReader reader) throws IOException {
                    if (reader.peek() == JsonToken.NULL) {
                        reader.nextNull();
                        return null;
                    } else {
                        var recordComponents = clazz.getRecordComponents();
                        var typeMap = new HashMap<String,TypeToken<?>>();
                        for (int i = 0; i < recordComponents.length; i++) {
                            typeMap.put(recordComponents[i].getName(), TypeToken.get(recordComponents[i].getGenericType()));
                        }
                        var argsMap = new HashMap<String,Object>();
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String name = reader.nextName();
                            argsMap.put(name, gson.getAdapter(typeMap.get(name)).read(reader));
                        }
                        reader.endObject();

                        var argTypes = new Class<?>[recordComponents.length];
                        var args = new Object[recordComponents.length];
                        for (int i = 0; i < recordComponents.length; i++) {
                            argTypes[i] = recordComponents[i].getType();
                            args[i] = argsMap.get(recordComponents[i].getName());
                        }
                        Constructor<T> constructor;
                        try {
                            constructor = clazz.getDeclaredConstructor(argTypes);
                            constructor.setAccessible(true);
                            return constructor.newInstance(args);
                        } catch (NoSuchMethodException | InstantiationException | SecurityException |
                                 IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            };
        }
    }
}
