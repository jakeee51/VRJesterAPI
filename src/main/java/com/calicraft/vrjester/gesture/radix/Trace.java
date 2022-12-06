package com.calicraft.vrjester.gesture.radix;

import com.calicraft.vrjester.utils.vrdata.VRDevice;

import java.util.ArrayList;
import java.util.List;

public class Trace {
    // Class that represents a complete gesture trace from a single VRDevice
    // --- TODO - To be deleted
    public String rcMove = "", lcMove = "";
    public VRDevice vrDevice;
    public final List<Track> tracks = new ArrayList<>();
    public long rcElapsedTime = 0, lcElapsedTime = 0;
    public Trace(VRDevice vrDevice) {
        this.vrDevice = vrDevice;
    }
    public void add(Track track) {
        tracks.add(track);
    }
    // ---

    public List<Path> path;
    public Node next;

    public Trace(List<Path> path) {
        this(path, new Node(true));
    }

    public Trace(List<Path> path, Node next) {
        this.path = path;
        this.next = next;
    }

    @Override
    public String toString() {
        return "Trace[path=" + path + "]";
    }

}
