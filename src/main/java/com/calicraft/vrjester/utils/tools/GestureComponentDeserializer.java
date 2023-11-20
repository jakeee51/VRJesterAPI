package com.calicraft.vrjester.utils.tools;

import com.calicraft.vrjester.gesture.GestureComponent;
import com.google.gson.*;
import net.minecraft.util.math.vector.Vector3d;

import java.lang.reflect.Type;
import java.util.HashMap;

public class GestureComponentDeserializer implements JsonDeserializer<GestureComponent> {
    public GestureComponentDeserializer() {}

    public GestureComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonObject direction = jsonObject.get("direction").getAsJsonObject();
        JsonObject devices = jsonObject.get("devicesInProximity").getAsJsonObject();
        HashMap<String, Integer> devicesInProximity = new HashMap<>();

//        for (String device: devices.keySet()) {
//            devicesInProximity.put(device, devices.get(device).getAsInt());
//        }

        Vector3d vec3 = new Vector3d(
                direction.get("x").getAsDouble(),
                direction.get("y").getAsDouble(),
                direction.get("z").getAsDouble());

        return new GestureComponent(
                jsonObject.get("vrDevice").getAsString(),
                jsonObject.get("movement").getAsString(),
                jsonObject.get("elapsedTime").getAsLong(),
                jsonObject.get("speed").getAsDouble(),
                vec3, devicesInProximity);
    }
}
