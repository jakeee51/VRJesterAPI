package com.calicraft.vrjester.gesture.radix;

import com.calicraft.vrjester.gesture.GestureComponent;

import java.util.HashMap;
import java.util.List;

public class Node {
    // Class that represents a Node in a RadixTree with paths leading to next Nodes

    public boolean isGesture;
    public HashMap<GestureComponent, Path> paths;

    public Node(boolean isGesture) {
        this.isGesture = isGesture;
        paths = new HashMap<>();
    }

    public Path getTransition(GestureComponent transitionGestureComponent) {
        return paths.get(transitionGestureComponent);
    }

    public void addGestureComponent(List<GestureComponent> gestureComponent, Node next) {
        paths.put(gestureComponent.get(0), new Path(gestureComponent, next));
    }

    public int totalGestureComponent() {
        return paths.size();
    }

    public Path getTracedGestureComponent(GestureComponent transitionGestureComponent) {
        for (GestureComponent gestureComponent : paths.keySet()) {
            if (gestureComponent.equals(transitionGestureComponent))
                return paths.get(gestureComponent);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Node[ isGesture=" + isGesture + ", paths=" + paths + "]";
    }
}
