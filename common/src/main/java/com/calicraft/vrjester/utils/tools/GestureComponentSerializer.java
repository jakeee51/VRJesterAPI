package com.calicraft.vrjester.utils.tools;

import com.calicraft.vrjester.gesture.GestureComponent;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;

public class GestureComponentSerializer implements JsonSerializer<GestureComponent> {
    public GestureComponentSerializer() {}

    public JsonElement serialize(GestureComponent src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        JsonObject vec3 = new JsonObject();
        JsonObject devicesInProximity = new JsonObject();

        vec3.addProperty("x", src.direction().x);
        vec3.addProperty("y", src.direction().y);
        vec3.addProperty("z", src.direction().z);

        for (String device: src.devicesInProximity().keySet()) {
            devicesInProximity.addProperty(device, src.devicesInProximity().get(device));
        }

        obj.addProperty("vrDevice", src.vrDevice());
        obj.addProperty("movement", src.movement());
        obj.addProperty("elapsedTime", src.elapsedTime());
        obj.addProperty("speed", src.speed());
        obj.add("direction", vec3);
        obj.add("devicesInProximity", devicesInProximity);
        return obj;
    }
}
