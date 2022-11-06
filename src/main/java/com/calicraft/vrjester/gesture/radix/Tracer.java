package com.calicraft.vrjester.gesture.radix;

import com.calicraft.vrjester.gesture.radix.Trace;

import java.util.ArrayList;
import java.util.List;

public class Tracer {
    // Class that represents a gesture traced from Voxes
    // Object will include the following attributes on each tracked state of a gesture:
    //   -> Vox Id's, 3D Joystick Directions, VRDevice Poses & Time Elapsed (per Vox)
    public List<Trace[]> traces = new ArrayList<>();
    public String rcMove = "", lcMove = "";
    public Tracer() {

    }


}
