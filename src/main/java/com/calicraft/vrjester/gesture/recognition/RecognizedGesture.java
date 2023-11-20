package com.calicraft.vrjester.gesture.recognition;

public class RecognizedGesture {
    String gestureName;
    String hmdGesture;
    String rcGesture;
    String lcGesture;

    public RecognizedGesture(String gestureName, String hmdGesture, String rcGesture, String lcGesture) {
        this.gestureName = gestureName;
        this.hmdGesture = hmdGesture;
        this.rcGesture = rcGesture;
        this.lcGesture = lcGesture;
    }

    public boolean isFound() {
        return gestureName != null;
    }
}
