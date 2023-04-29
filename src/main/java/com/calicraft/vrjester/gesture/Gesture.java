package com.calicraft.vrjester.gesture;

import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import com.calicraft.vrjester.vox.Vox;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class Gesture {
    // Class that handles compiling the GestureComponent list for each VRDevice
    // Note: A list of GestureComponent's represents a gesture for an individual VRDevice

    private final List<Vox> voxList = new ArrayList<>();
    public List<GestureComponent> hmdGesture = new ArrayList<>();
    public List<GestureComponent> rcGesture = new ArrayList<>();
    public List<GestureComponent> lcGesture = new ArrayList<>();
    public List<String> validDevices = new ArrayList<>();

    // Initialize the first gesture trace and continue tracking until completion of gesture
    public Gesture(VRDataState vrDataState) {
        // Note: Facing direction is set here, meaning all movements after tracing this Gesture object are relative to that
        Vec3[] hmdOrigin = vrDataState.getHmd(), rcOrigin = vrDataState.getRc(), lcOrigin = vrDataState.getLc();
        Vox hmdVox = new Vox(VRDevice.HMD, hmdOrigin, hmdOrigin[1], false);
        Vox rcVox = new Vox(VRDevice.RC, rcOrigin, hmdOrigin[1], true);
        Vox lcVox = new Vox(VRDevice.LC, lcOrigin, hmdOrigin[1], true);
        voxList.add(hmdVox); voxList.add(rcVox); voxList.add(lcVox);
    }

    // Initialize Gesture with already set gestures for each VRDevice
    public Gesture(List<GestureComponent> hmdGesture, List<GestureComponent> rcGesture, List<GestureComponent> lcGesture) {
        if (hmdGesture != null)
            this.hmdGesture = hmdGesture;
        if (rcGesture != null)
            this.rcGesture = rcGesture;
        if (lcGesture != null)
            this.lcGesture = lcGesture;

    }

    // Initialize Gesture with already set gestures for each VRDevice
    public Gesture(HashMap<String, List<GestureComponent>> gesture) {
        for (String vrDevice: gesture.keySet()) {
            List<String> devices = Arrays.asList(vrDevice.split("\\|"));
            if (devices.contains("HMD")) {
                Map<String, String> newValues = new HashMap<>();
                newValues.put("vrDevice", "HMD");
                if (devices.size() > 1)
                    validDevices.add("HMD");
                hmdGesture = GestureComponent.copy(gesture.get(vrDevice), newValues);
            }
            if (devices.contains("RC")) {
                Map<String, String> newValues = new HashMap<>();
                newValues.put("vrDevice", "RC");
                if (devices.size() > 1)
                    validDevices.add("RC");
                rcGesture = GestureComponent.copy(gesture.get(vrDevice), newValues);
            }
            if (devices.contains("LC")) {
                Map<String, String> newValues = new HashMap<>();
                newValues.put("vrDevice", "LC");
                if (devices.size() > 1)
                    validDevices.add("LC");
                lcGesture = GestureComponent.copy(gesture.get(vrDevice), newValues);
            }
        }
    }

    @Override
    public String toString() {
        return "Gesture:" +
                "\r\n \t hmdGesture: " + hmdGesture +
                "\r\n \t rcGesture: " + rcGesture +
                "\r\n \t lcGesture: " + lcGesture;
    }

    // Record the Vox trace of each VRDevice and store the resulting data
    public void track(VRDataState vrDataRoomPre) {
        for (Vox vox: voxList) { // Loop through each VRDevice's Vox
            Vec3[] currentPoint = vox.generateVox(vrDataRoomPre);
            int[] currentId = vox.getId();
            if (!Arrays.equals(vox.getPreviousId(), currentId)) { // Check if a VRDevice exited Vox
                vox.setPreviousId(currentId.clone());
                GestureTrace gestureTrace = vox.getTrace();
                gestureTrace.completeTrace(currentPoint);
//                System.out.println("BEFORE: " + vox.getName() + ": " + trace.toString());
                vox.beginTrace(currentPoint);
//                System.out.println("AFTER: " + vox.getName() + ": " + vox.getTrace().toString());
                switch (vox.getVrDevice()) {  // Append a Vox trace's new GestureComponent object per VRDevice
                    case HMD -> hmdGesture.add(gestureTrace.toGestureComponent());
                    case RC  -> rcGesture.add(gestureTrace.toGestureComponent());
                    case LC  -> lcGesture.add(gestureTrace.toGestureComponent());
                }
            }
        }
    }

    // Store the current data of each Vox for each VRDevice
    public void trackComplete(VRDataState vrDataRoomPre) {
        // TODO - Implement way to store idle gesture trace if VRDevice never exited Vox.
        //  Also make way to specify starter GestureTrace.
        //  Call separate Gesture method to signal gesture listening termination.
        for (Vox vox: voxList) { // Loop through each VRDevice's Vox
            GestureTrace gestureTrace = vox.getTrace();
            if (gestureTrace.getMovement().equals("idle")) {
                Vec3[] currentPoint = vox.generateVox(vrDataRoomPre);
                gestureTrace.completeIdleTrace(currentPoint);
                vox.beginTrace(currentPoint);
                switch (vox.getVrDevice()) {  // Append a Vox trace's new GestureComponent object per VRDevice
                    case HMD -> hmdGesture.add(gestureTrace.toGestureComponent());
                    case RC -> rcGesture.add(gestureTrace.toGestureComponent());
                    case LC -> lcGesture.add(gestureTrace.toGestureComponent());
                }
            }
        }
    }

    public List<GestureComponent> getHmdGesture() {
        return hmdGesture;
    }

    public List<GestureComponent> getRcGesture() {
        return rcGesture;
    }

    public List<GestureComponent> getLcGesture() {
        return lcGesture;
    }

    public List<GestureComponent> getGesture(String vrDevice) {
        List<GestureComponent> gesture = new ArrayList<>();
        switch(vrDevice) {
            case Constants.HMD -> gesture = hmdGesture;
            case Constants.RC  -> gesture = rcGesture;
            case Constants.LC  -> gesture = lcGesture;
        }
        return gesture;
    }
}
