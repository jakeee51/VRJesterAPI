package com.calicraft.vrjester.config;

import java.util.ArrayList;
import java.util.List;

public abstract class Constants {
    public static final String CONFIG_PATH = "config/VRJesterAPI.cfg";
    public static final String GESTURE_STORE_PATH = "config/gesture_store.json";
    public static final String DEV_GESTURE_STORE_PATH = "C:/Users/mosta/Documents/GitHub/VRJesterAPI/src/main/resources/data/vrjester/gesture_store.json";
    public static final String DEV_CONFIG_PATH = "C:/Users/mosta/Documents/GitHub/VRJesterAPI/src/main/resources/data/vrjester/config.json";
    public static final String DEV_ARCHIVE_PATH = "C:/Users/mosta/Documents/GitHub/VRJesterAPI/dev/archive";

    // RECOGNIZE -> fire event right when recognized | RELEASE -> fire event when key is released
    public static final String RECOGNIZE_ON = "RECOGNIZE";
    public static final boolean RECORD_MODE = false;
    public static final boolean READ_DATA = false;
    public static final boolean WRITE_DATA = false;
    public static final boolean DISPLAY_VOX = false;
    public static final float VOX_LENGTH = 0.6F;
    public static final int VOX_GRID_LENGTH = 6;
    public static final int VOX_GRID_WIDTH = 6;
    public static final int VOX_GRID_HEIGHT = 6;

    public static final float MAX_LISTENING_TIME = 6.0F;

    public static final float DEGREE_SPAN = 45.0F;

    public static final String HMD = "head_mounted_display";
    public static final String RC = "right_controller";
    public static final String LC = "left_controller";
    public static final String C2 = "extra_tracker";
    public static final List<String> DEVICES = List.of(HMD, RC, LC);
}
