package com.calicraft.vrjester.gesture;

import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import com.calicraft.vrjester.vox.Vhere;
import net.minecraft.util.math.vector.Vector3d;

import java.util.*;

public class Gesture {
    // Class that handles compiling the GestureComponent list for each VRDevice
    // Note: A list of GestureComponent's represents a gesture of an individual VRDevice

    private final List<Vhere> vhereList = new ArrayList<>();
    public List<GestureComponent> hmdGesture = new ArrayList<>();
    public List<GestureComponent> rcGesture = new ArrayList<>();
    public List<GestureComponent> lcGesture = new ArrayList<>();
    public List<String> validDevices = new ArrayList<>();

    // Initialize the first gesture trace and continue tracking until completion of gesture
    public Gesture(VRDataState vrDataState) {
        // Note: Facing direction is set here, meaning all movements after tracing this Gesture object are relative to that
        Vector3d[] hmdOrigin = vrDataState.getHmd(), rcOrigin = vrDataState.getRc(), lcOrigin = vrDataState.getLc();
        Vhere hmdVhere = new Vhere(VRDevice.HEAD_MOUNTED_DISPLAY, hmdOrigin, Constants.CONFIG_PATH);
        Vhere rcVhere = new Vhere(VRDevice.RIGHT_CONTROLLER, rcOrigin, Constants.CONFIG_PATH);
        Vhere lcVhere = new Vhere(VRDevice.LEFT_CONTROLLER, lcOrigin, Constants.CONFIG_PATH);
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
                "\r\n  hmdGesture: " + hmdGesture +
                "\r\n  rcGesture: " + rcGesture +
                "\r\n  lcGesture: " + lcGesture;
    }

    // Record the Vox trace of each VRDevice and store the resulting data
    public void track(VRDataState vrDataRoomPre) {
        for (Vhere vhere: vhereList) { // Loop through each VRDevice's Vox
            Vector3d[] currentPoint = vhere.generateVhere(vrDataRoomPre);
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

    public String prettyString() {
        StringBuilder sb = new StringBuilder();
        if (!hmdGesture.isEmpty())
            sb.append("HMD: " + getMovementList(hmdGesture));
        if (!rcGesture.isEmpty())
            sb.append("\nRC: " + getMovementList(rcGesture));
        if (!lcGesture.isEmpty())
            sb.append("\nLC: " + getMovementList(lcGesture));
        return sb.toString();
    }

    public static List<String> getMovementList( List<GestureComponent> gesture) {
        List<String> ret = new ArrayList<>();
        for (GestureComponent component: gesture)
            ret.add(String.format("{%s, %d ms, %.2f m/s}", component.movement(), component.elapsedTime(), component.speed()));
        return ret;
    }
}
