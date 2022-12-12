package com.calicraft.vrjester.gesture.radix;

import com.calicraft.vrjester.gesture.GestureComponent;

import java.util.List;

public class Path {
    // Class that represents a valid gesture path leading from a previous Node and stores the GestureComponent(s)

    public List<GestureComponent> gestureComponent;
    public Node next;

    public Path(List<GestureComponent> gestureComponent) {
        this(gestureComponent, new Node(true));
    }

    public Path(List<GestureComponent> gestureComponent, Node next) {
        this.gestureComponent = gestureComponent;
        this.next = next;
    }

    @Override
    public String toString() {
        return "Trace[path=" + gestureComponent + "]";
    }

}
