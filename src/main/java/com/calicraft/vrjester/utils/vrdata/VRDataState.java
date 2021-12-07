package com.calicraft.vrjester.utils.vrdata;

import org.vivecraft.api.VRData;

public class VRDataState {
    // Class for encapsulating VRData devices

    private VRData.VRDevicePose hmd;
    private VRData.VRDevicePose rc;
    private VRData.VRDevicePose lc;
    private VRData.VRDevicePose c2;

    public VRDataState(VRData.VRDevicePose head_mounted_display, VRData.VRDevicePose right_controller,
                       VRData.VRDevicePose left_controller, VRData.VRDevicePose extra_tracker) {
        hmd = head_mounted_display;
        rc = right_controller;
        lc = left_controller;
        c2 = extra_tracker;
    }

    @Override
    public String toString() {
        return "data:" +
                "\r\n \t hmd: " + hmd +
                "\r\n \t rc: " + rc +
                "\r\n \t lc: " + lc +
                "\r\n \t c2: " + c2;
    }

    public VRData.VRDevicePose getHmd() {
        return hmd;
    }
    public VRData.VRDevicePose getRc() {
        return rc;
    }
    public VRData.VRDevicePose getLc() {
        return lc;
    }
    public VRData.VRDevicePose getC2() {
        return c2;
    }
}
