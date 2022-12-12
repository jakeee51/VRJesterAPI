package com.calicraft.vrjester.gesture;

import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import com.calicraft.vrjester.vox.Vox;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Gesture {
    // Class that handles compiling the GestureComponent list for each VRDevice

    private final List<Vox> voxList = new ArrayList<>();
    public List<GestureComponent> hmdGesture = new ArrayList<>();
    public List<GestureComponent> rcGesture = new ArrayList<>();
    public List<GestureComponent> lcGesture = new ArrayList<>();

    public Gesture(VRDataState vrDataState) {
        Vec3[] hmdOrigin = vrDataState.getHmd(), rcOrigin = vrDataState.getRc(), lcOrigin = vrDataState.getLc();
        Vox hmdVox = new Vox(VRDevice.HMD, hmdOrigin, hmdOrigin[1], false);
        Vox rcVox = new Vox(VRDevice.RC, rcOrigin, hmdOrigin[1], true);
        Vox lcVox = new Vox(VRDevice.LC, lcOrigin, hmdOrigin[1], true);
        voxList.add(hmdVox); voxList.add(rcVox); voxList.add(lcVox);
    }

    public Gesture(List<GestureComponent> hmdGesture, List<GestureComponent> rcGesture, List<GestureComponent> lcGesture) {
        if (hmdGesture != null)
            this.hmdGesture = hmdGesture;
        if (rcGesture != null)
            this.rcGesture = rcGesture;
        if (lcGesture != null)
            this.lcGesture = lcGesture;

    }

    @Override
    public String toString() {
        return "Gesture:" +
                "\r\n \t hmdGesture: " + hmdGesture +
                "\r\n \t rcGesture: " + rcGesture +
                "\r\n \t lcGesture: " + lcGesture;
    }

    // Record the Vox trace of each VRDevice and return the resulting data
    public void track(VRDataState vrDataRoomPre) {
        for (Vox vox: voxList) { // Loop through each VRDevice's Vox
            Vec3[] currentPoint = vox.generateVox(vrDataRoomPre);
            int[] currentId = vox.getId();
            if (!Arrays.equals(vox.getPreviousId(), currentId)) { // Append Vox's new GestureComponent object to VRDevice
                vox.setPreviousId(currentId.clone());
                GestureTrace gestureTrace = vox.getTrace();
                gestureTrace.completeTrace(currentPoint);
//                System.out.println("BEFORE: " + vox.getName() + ": " + trace.toString());
                vox.beginTrace(currentPoint);
//                System.out.println("AFTER: " + vox.getName() + ": " + vox.getTrace().toString());
                switch (vox.getVrDevice()) {
                    case HMD -> hmdGesture.add(gestureTrace.toGestureComponent());
                    case RC  -> rcGesture.add(gestureTrace.toGestureComponent());
                    case LC  -> lcGesture.add(gestureTrace.toGestureComponent());
                }
            }
        }
    }
}
