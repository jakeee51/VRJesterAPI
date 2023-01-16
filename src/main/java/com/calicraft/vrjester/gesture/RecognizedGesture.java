package com.calicraft.vrjester.gesture;

public record RecognizedGesture(
        String gestureName,
        String hmdGesture,
        String rcGesture,
        String lcGesture) {

    public boolean isFound() {
        return gestureName != null;
    }
}
