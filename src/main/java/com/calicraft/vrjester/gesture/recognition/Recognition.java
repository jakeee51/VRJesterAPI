package com.calicraft.vrjester.gesture.recognition;

import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.gesture.Gesture;
import com.calicraft.vrjester.gesture.GestureComponent;
import com.calicraft.vrjester.gesture.Gestures;

import java.util.HashMap;
import java.util.List;

public class Recognition {
    // Class that handles identifying a gesture utilizing the RadixTree

    // TODO - Either Check constantly, everytime Path gets appended or check at end of gesture listening.
    //      - Note, I must determine how to know when to start & stop listening to a gesture.
    //      - There will be 2 modes of triggering & 3 modes of terminating the recognition listener
    //      - listenOnKey | listenOnPosition
    //      - recognizeOnTime | recognizeOnRecognize | recognizeOnRelease
    //      - Upon terminating the listener, a GestureRecognition Event
    //      will either be fired. As a traced gesture makes its way through
    //      the radix sort tree, each "isGesture node" will be fired to
    //      InterMod Event Bus to notify consumers of the API that a "step"
    //      in a gesture's path has been fulfilled. This allows a way for
    //      users/devs to know if and when their gestures are being recognized

    public Gestures gestures;

    public Recognition(Gestures gestures) {
        this.gestures = gestures;
    }

    // Recognize the gesture & return its name
    public HashMap<String, String> recognize(Gesture gesture) {
        HashMap<String, String> ctx = new HashMap<>();
        String gestureName, id = "";
        List<GestureComponent> foundHmdGesture = gestures.hmdGestures.search(gesture.hmdGesture);
        List<GestureComponent> foundRcGesture = gestures.rcGestures.search(gesture.rcGesture);
        List<GestureComponent> foundLcGesture = gestures.lcGestures.search(gesture.lcGesture);
        if (foundHmdGesture != null) {
            id += foundHmdGesture.hashCode();
            ctx.put(Constants.HMD, gestures.hmdGestureMapping.get(foundHmdGesture.hashCode()));
        }
        if (foundRcGesture != null) {
            id += foundRcGesture.hashCode();
            ctx.put(Constants.RC, gestures.rcGestureMapping.get(foundRcGesture.hashCode()));
        }
        if (foundLcGesture != null) {
            id += foundLcGesture.hashCode();
            ctx.put(Constants.LC, gestures.lcGestureMapping.get(foundLcGesture.hashCode()));
        }
//        FOR DEBUGGING:
        System.out.println(gesture);
//        System.out.println("foundHmdGesture: " + foundHmdGesture);
//        System.out.println("foundRcGesture: " + foundRcGesture);
//        System.out.println("foundLcGesture: " + foundLcGesture);
        System.out.println("RECOGNIZE ID:" + id);
        gestureName = gestures.gestureNameSpace.get(id);
        ctx.put("gestureName", gestureName);
        return gestureName != null ? ctx : new HashMap<>();
    }
}
