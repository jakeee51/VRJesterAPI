package com.calicraft.vrjester.gesture;

import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import com.calicraft.vrjester.vox.Vhere;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class Gesture {
    // Class that handles compiling the GestureComponent list for each VRDevice
    // Note: A list of GestureComponent's represents a gesture of an individual VRDevice

    private boolean completeGesture = true;
    private final List<Vhere> vhereList = new ArrayList<>();
    public List<GestureComponent> hmdGesture = new ArrayList<>();
    public List<GestureComponent> rcGesture = new ArrayList<>();
    public List<GestureComponent> lcGesture = new ArrayList<>();
    public List<String> validDevices = new ArrayList<>();

    // Initialize the first gesture trace and continue tracking until completion of gesture
    public Gesture(VRDataState vrDataState) {
        // Note: Facing direction is set here, meaning all movements after tracing this Gesture object are relative to that
        Vec3[] hmdOrigin = vrDataState.getHmd(), rcOrigin = vrDataState.getRc(), lcOrigin = vrDataState.getLc();
        Vhere hmdVhere = new Vhere(VRDevice.HEAD_MOUNTED_DISPLAY, hmdOrigin);
        Vhere rcVhere = new Vhere(VRDevice.RIGHT_CONTROLLER, rcOrigin);
        Vhere lcVhere = new Vhere(VRDevice.LEFT_CONTROLLER, lcOrigin);
        vhereList.add(hmdVhere); vhereList.add(rcVhere); vhereList.add(lcVhere);
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
            if (devices.contains(Constants.HMD)) {
                Map<String, String> newValues = new HashMap<>();
                newValues.put("vrDevice", Constants.HMD);
                if (devices.size() > 1)
                    validDevices.add(Constants.HMD);
                hmdGesture = GestureComponent.copy(gesture.get(vrDevice), newValues);
            }
            if (devices.contains(Constants.RC)) {
                Map<String, String> newValues = new HashMap<>();
                newValues.put("vrDevice", Constants.RC);
                if (devices.size() > 1)
                    validDevices.add(Constants.RC);
                rcGesture = GestureComponent.copy(gesture.get(vrDevice), newValues);
            }
            if (devices.contains(Constants.LC)) {
                Map<String, String> newValues = new HashMap<>();
                newValues.put("vrDevice", Constants.LC);
                if (devices.size() > 1)
                    validDevices.add(Constants.LC);
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
        for (Vhere vhere: vhereList) { // Loop through each VRDevice's Vox
            Vec3[] currentPoint = vhere.generateVhere(vrDataRoomPre);
            int currentId = vhere.getId();
            if (vhere.getPreviousId() != currentId) { // Check if a VRDevice exited Vox
                vhere.setPreviousId(currentId);
                GestureTrace gestureTrace = vhere.getTrace();
                gestureTrace.completeTrace(currentPoint);
//                System.out.println("COMPLETE TRACK: " + vhere.getId() + ": " + gestureTrace);
                vhere.beginTrace(currentPoint);
                switch (vhere.getVrDevice()) {  // Append a Vox trace's new GestureComponent object per VRDevice
                    case HEAD_MOUNTED_DISPLAY -> hmdGesture.add(gestureTrace.toGestureComponent());
                    case RIGHT_CONTROLLER -> rcGesture.add(gestureTrace.toGestureComponent());
                    case LEFT_CONTROLLER -> lcGesture.add(gestureTrace.toGestureComponent());
                }
            }
        }
    }

    // Store the current data of each Vox for each VRDevice
    public void trackComplete(VRDataState vrDataRoomPre) {
        // TODO - Implement way to store idle Gesture trace if VRDevice never exited Vox.
        //  And only add it once & complete the trace once it's done.
        //  Also make way to specify starter GestureTrace.
        for (Vhere vhere: vhereList) { // Loop through each VRDevice's Vox
            GestureTrace gestureTrace = vhere.getTrace();
//            System.out.println("GESTURE TRACE: " + gestureTrace);
            if (gestureTrace.getMovement().equals("idle")) {
                Vec3[] currentPoint = vhere.generateVhere(vrDataRoomPre);
                gestureTrace.completeIdleTrace(currentPoint);
                vhere.beginTrace(currentPoint);
                switch (vhere.getVrDevice()) {  // Append a Vox trace's new GestureComponent object per VRDevice
                    case HEAD_MOUNTED_DISPLAY -> hmdGesture.add(gestureTrace.toGestureComponent());
                    case RIGHT_CONTROLLER -> rcGesture.add(gestureTrace.toGestureComponent());
                    case LEFT_CONTROLLER -> lcGesture.add(gestureTrace.toGestureComponent());
                }
            }
        }
    }

    public void setComplete(boolean completedGesture) {
        completeGesture = completedGesture;
    }

    public boolean isComplete() {
        return completeGesture;
    }

    public void clear() {
        completeGesture = true;
        vhereList.clear();
        hmdGesture.clear();
        rcGesture.clear();
        lcGesture.clear();
        validDevices.clear();
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
