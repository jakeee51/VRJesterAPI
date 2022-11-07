package com.calicraft.vrjester.utils.vrdata;

import com.calicraft.vrjester.tracker.Tracker;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.api.VRData;

public class VRDataState {
    // Class for encapsulating VRData devices

    private final Vec3 origin;
    private final Vec3[] hmd, rc, lc, c2;

    public VRDataState(VRData vrData) {
        origin = Tracker.getOrigin(vrData.toString());
        hmd = Tracker.getPose(vrData.hmd);
        rc = Tracker.getPose(vrData.c0);
        lc = Tracker.getPose(vrData.c1);
        c2 = Tracker.getPose(vrData.c2);
    }

    @Override
    public String toString() {
        return "data:" +
                "\r\n \t origin: " + origin +
                "\r\n \t hmd: " + hmd +
                "\r\n \t rc: " + rc +
                "\r\n \t lc: " + lc +
                "\r\n \t c2: " + c2;
    }

    public Vec3 getOrigin() {
        return origin;
    }
    public Vec3[] getHmd() {
        return hmd;
    }
    public Vec3[] getRc() {
        return rc;
    }
    public Vec3[] getLc() {
        return lc;
    }
    public Vec3[] getC2() {
        return c2;
    }

    public static Vec3 getVRDevicePose(VRDataState vrDataState, VRDevice vrDevice, int pose) { // Return pose based on VRDevice
        Vec3 ret;
        switch(vrDevice) {
            case HMD:
                ret = vrDataState.getHmd()[pose]; break;
            case RC:
                ret = vrDataState.getRc()[pose]; break;
            case LC:
                ret = vrDataState.getLc()[pose]; break;
            case C2:
                ret = vrDataState.getC2()[pose]; break;
            default:
                System.err.println("VRDevice not yet supported!");
                ret = new Vec3((0), (0), (0));
        }
        return ret;
    }

    public static Vec3[] getVRDevicePose(VRDataState vrDataState, VRDevice vrDevice) { // Return pose based on VRDevice
        Vec3[] ret;
        switch(vrDevice) {
            case HMD:
                ret = vrDataState.getHmd(); break;
            case RC:
                ret = vrDataState.getRc(); break;
            case LC:
                ret = vrDataState.getLc(); break;
            case C2:
                ret = vrDataState.getC2(); break;
            default:
                System.err.println("VRDevice not yet supported!");
                ret = new Vec3[]{new Vec3((0), (0), (0)), new Vec3((0), (0), (0))};
        }
        return ret;
    }
}
