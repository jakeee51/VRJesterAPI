package com.calicraft.vrjester.utils.vrdata;

import com.calicraft.vrjester.tracker.Tracker;
import net.blf02.vrapi.api.data.IVRPlayer;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Arrays;

public class VRDataState {
    // Class for encapsulating VRData devices

    private final Vector3d origin;
    private final Vector3d[] hmd, rc, lc, c2;

    public VRDataState(IVRPlayer ivrPlayer) {
        origin = new Vector3d((0), (0), (0));
        hmd = Tracker.getPose(ivrPlayer.getHMD());
        rc = Tracker.getPose(ivrPlayer.getController0());
        lc = Tracker.getPose(ivrPlayer.getController1());
        c2 = null;
    }

    @Override
    public String toString() {
        return "data:" +
                "\r\n \t origin: " + origin +
                "\r\n \t hmd: " + Arrays.toString(hmd) +
                "\r\n \t rc: " + Arrays.toString(rc) +
                "\r\n \t lc: " + Arrays.toString(lc) +
                "\r\n \t c2: " + c2;
    }

    public Vector3d getOrigin() {
        return origin;
    }
    public Vector3d[] getHmd() {
        return hmd;
    }
    public Vector3d[] getRc() {
        return rc;
    }
    public Vector3d[] getLc() {
        return lc;
    }
    public Vector3d[] getC2() {
        return c2;
    }

    // Return position or direction based on VRDevice
    public static Vector3d getVRDevicePose(VRDataState vrDataState, VRDevice vrDevice, int pose) {
        Vector3d ret;
        switch (vrDevice) {
            case HEAD_MOUNTED_DISPLAY -> ret = vrDataState.getHmd()[pose];
            case RIGHT_CONTROLLER -> ret = vrDataState.getRc()[pose];
            case LEFT_CONTROLLER -> ret = vrDataState.getLc()[pose];
            case EXTRA_TRACKER -> ret = vrDataState.getC2()[pose];
            default -> {
                System.err.println("VRDevice not yet supported!");
                ret = new Vector3d((0), (0), (0));
            }
        }
        return ret;
    }

    // Return pose based on VRDevice
    public static Vector3d[] getVRDevicePose(VRDataState vrDataState, VRDevice vrDevice) {
        Vector3d[] ret;
        switch (vrDevice) {
            case HEAD_MOUNTED_DISPLAY -> ret = vrDataState.getHmd();
            case RIGHT_CONTROLLER -> ret = vrDataState.getRc();
            case LEFT_CONTROLLER -> ret = vrDataState.getLc();
            case EXTRA_TRACKER -> ret = vrDataState.getC2();
            default -> {
                System.err.println("VRDevice not yet supported!");
                ret = new Vector3d[]{new Vector3d((0), (0), (0)), new Vector3d((0), (0), (0))};
            }
        }
        return ret;
    }
}
