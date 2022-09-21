package com.calicraft.vrjester.utils.vrdata;

import com.calicraft.vrjester.tracker.Tracker;
import net.minecraft.util.math.vector.Vector3d;
import org.vivecraft.api.VRData;

public class VRDataState {
    // Class for encapsulating VRData devices

    private final Vector3d[] hmd;
    private final Vector3d[] rc;
    private final Vector3d[] lc;
    private final Vector3d[] c2;

    // TODO - Add another constructor with new params for upward compatibility
    public VRDataState(VRData.VRDevicePose head_mounted_display, VRData.VRDevicePose right_controller,
                       VRData.VRDevicePose left_controller, VRData.VRDevicePose extra_tracker) {
        hmd = Tracker.getPose(head_mounted_display);
        rc = Tracker.getPose(right_controller);
        lc = Tracker.getPose(left_controller);
        c2 = Tracker.getPose(extra_tracker);
    }

    @Override
    public String toString() {
        return "data:" +
                "\r\n \t hmd: " + hmd +
                "\r\n \t rc: " + rc +
                "\r\n \t lc: " + lc +
                "\r\n \t c2: " + c2;
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

    public static Vector3d getVRDevicePose(VRDataState vrDataState, VRDevice vrDevice, int pose) { // Return pose based on VRDevice
        Vector3d ret;
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
                ret = new Vector3d((0), (0), (0));
        }
        return ret;
    }
}
