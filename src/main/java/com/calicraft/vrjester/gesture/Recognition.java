package com.calicraft.vrjester.gesture;

import java.util.HashMap;

public class Recognition {
    // Class that handles identifying a gesture utilizing the RadixTree
    // TODO - There will be 2 modes of triggering & 3 modes of terminating the recognition listener
    //      - listenOnKey | listenOnPosition
    //      - recognizeOnTime | recognizeOnRecognize | recognizeOnRelease
    //      - Upon terminating the listener, a GestureRecognition Event
    //      will either be fired. As a traced gesture makes its way through
    //      the radix sort tree, each "unlocked node" will be fired to
    //      InterMod Event Bus to notify consumers of the API that a "step"
    //      in a gesture's path has been fulfilled. This allows a way for
    //      users/devs to know if and when their gestures are being recognized

    public Recognition() {

    }

}
