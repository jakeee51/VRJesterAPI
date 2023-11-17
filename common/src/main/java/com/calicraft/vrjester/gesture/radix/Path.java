package com.calicraft.vrjester.gesture.radix;

import com.calicraft.vrjester.gesture.GestureComponent;

import java.util.List;

public class Path {
    // Class that represents a valid gesture path leading from a previous Node and stores the GestureComponent(s)

    public List<GestureComponent> gesture;
    public Node next;

    public Path(List<GestureComponent> gesture) {
        this(gesture, new Node(true));
    }

    public Path(List<GestureComponent> gesture, Node next) {
        this.gesture = gesture;
        this.next = next;
    }

    @Override
    public String toString() {
        return "Path[gesture=" + gesture + "]";
    }

}
