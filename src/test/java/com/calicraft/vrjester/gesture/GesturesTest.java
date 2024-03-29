package com.calicraft.vrjester.gesture;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.gesture.recognition.Recognition;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GesturesTest {

    private static final Config devConfig = Config.readConfig(Constants.DEV_CONFIG_PATH);
    private static final Gestures gestures = new Gestures(devConfig, Constants.DEV_GESTURE_STORE_PATH);

    @Test
    void gestureLoadTest(){
        HashMap<String, String> gestureNamespace = new HashMap<>();
        gestureNamespace.put("-409853157" ,"KAMEHAMEHA");
        gestureNamespace.put("-747232807-747232807", "BLOCK");
        gestureNamespace.put( "21469640052146964005", "PULL");
        gestureNamespace.put("-295780073" , "UPPERCUT");
        gestureNamespace.put("1679261955", "STRIKE");
        gestureNamespace.put("1744444741-1543099174" , "SHRINK");
        gestureNamespace.put("16792619551679261955" , "PUSH");
        gestureNamespace.put("17388439551738843955", "BURST");
        gestureNamespace.put("156397030156397030", "IDLE UP");
        gestureNamespace.put("-813293095-813293095" , "RAISE");
        gestureNamespace.put("14531206081453120608" , "LOWER");
        gestureNamespace.put("-15430991741744444741" , "GROW");
        gestures.load();
        assertEquals(gestureNamespace, gestures.gestureNameSpace);
    }
}
