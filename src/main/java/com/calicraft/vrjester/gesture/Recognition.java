package com.calicraft.vrjester.gesture;

import java.util.List;

public class Recognition {
    // Class that handles identifying a gesture utilizing the RadixTree

    // TODO - Either Check constantly, everytime Path gets appended or check at end of gesture listening.
    //      - Note, I must determine how to know when to start & stop listening to a gesture.
    // TODO - There will be 2 modes of triggering & 3 modes of terminating the recognition listener
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
    public String recognize(Gesture gesture) {
        String gestureName, id = "";
        List<GestureComponent> foundHmdGesture = gestures.hmdGestures.search(gesture.hmdGesture);
        List<GestureComponent> foundRcGesture = gestures.rcGestures.search(gesture.rcGesture);
        List<GestureComponent> foundLcGesture = gestures.lcGestures.search(gesture.lcGesture);
        if (foundHmdGesture != null)
            id += foundHmdGesture.hashCode();
        if (foundRcGesture != null)
            id += foundRcGesture.hashCode();
        if (foundLcGesture != null)
            id += foundLcGesture.hashCode();
//        System.out.println(gesture);
//        System.out.println("foundHmdGesture: " + foundHmdGesture);
//        System.out.println("foundRcGesture: " + foundRcGesture);
//        System.out.println("foundLcGesture: " + foundLcGesture);
//        System.out.println("RECOGNIZE ID:" + id);
//        System.out.println("GESTURE NAMESPACE: " + gestures.gestureNameSpace);
        gestureName = gestures.gestureNameSpace.get(id);
        return gestureName != null ? gestureName : "";
    }

}
