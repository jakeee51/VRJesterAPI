package com.calicraft.vrjester.gesture.radix;

import com.calicraft.vrjester.gesture.Path;

import java.util.List;

public class Trace {
    // Class that represents a valid gesture trace leading from a Node

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