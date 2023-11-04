package com.calicraft.vrjester.config;

import java.util.List;

public abstract class Constants {
    public static final String CONFIG_PATH = "config/VRJesterAPI.cfg";
    public static final String GESTURE_STORE_PATH = "config/gesture_store.json";
    public static final String DEV_ROOT_PATH = "C:/Users/jakem/Documents/GitHub";
    public static final String DEV_CONFIG_PATH = DEV_ROOT_PATH + "/VRJesterAPI/src/main/resources/data/vrjester/config.json";
    public static final String DEV_GESTURE_STORE_PATH = DEV_ROOT_PATH + "/VRJesterAPI/src/main/resources/data/vrjester/gesture_store.json";
    public static final String DEV_ARCHIVE_PATH = DEV_ROOT_PATH + "/VRJesterAPI/dev/archive";

    // RECOGNIZE -> fire event right when recognized | RELEASE -> fire event when key is released
    public static final String RECOGNIZE_ON = "RECOGNIZE";
    public static final String SAMPLE_GESTURE_NAME = "GESTURE 1";
    public static final boolean RECORD_MODE = false;
    public static final boolean READ_DATA = false;
    public static final boolean WRITE_DATA = false;
    public static final boolean DEMO_MODE = true;
    public static final float VOX_LENGTH = 0.6F;
    public static final float VIRTUAL_SPHERE_RADIUS = 0.3F;
    public static final int MAX_LISTENING_TIME = 400;

    public static final float MOVEMENT_DEGREE_SPAN = 45.0F;
    public static final float DIRECTION_DEGREE_SPAN = 30.0F;

    public static final String HMD = "HEAD_MOUNTED_DISPLAY";
    public static final String RC = "RIGHT_CONTROLLER";
    public static final String LC = "LEFT_CONTROLLER";
    public static final String C2 = "EXTRA_TRACKER";
    public static final List<String> DEVICES = List.of(HMD, RC, LC);
}
