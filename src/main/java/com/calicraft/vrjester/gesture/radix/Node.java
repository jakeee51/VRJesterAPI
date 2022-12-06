package com.calicraft.vrjester.gesture.radix;

import java.util.HashMap;
import java.util.List;

public class Node { //This code is nested inside the RadixTree class
    public boolean isGesture;
    public HashMap<Path, Trace> paths;

    public Node(boolean isGesture) {
        this.isGesture = isGesture;
        paths = new HashMap<>();
    }

    public Trace getTransition(Path transitionPath) {
        return paths.get(transitionPath);
    }

    public void addPath(List<Path> path, Node next) {
        paths.put(path.get(0), new Trace(path, next));
    }

    public int totalPaths() {
        return paths.size();
    }

    public Trace getTracedPath(Path transitionPath) {
        for (Path path: paths.keySet()) {
            if (path.equals(transitionPath))
                return paths.get(path);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Node[ isGesture=" + isGesture + ", paths=" + paths + "]";
    }
}
