package com.calicraft.vrjester.gesture.radix;

import com.calicraft.vrjester.gesture.GestureComponent;
import com.calicraft.vrjester.utils.tools.Calcs;
import net.minecraft.util.math.vector.Vector3d;

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

    public Path getMatchedPath(GestureComponent transitionPath) {
        Path newTransition = null;
        long maxTime = 0; double maxSpeed = 0.0;
        double minDegree = 180.0D; Vector3d anyDirection = new Vector3d(0,0,0);
        for (GestureComponent gestureComponent : paths.keySet()) {
//            System.out.println("MATCHES: " + gestureComponent.matches(transitionPath));
//            System.out.println("gestureComponent: " + gestureComponent);
//            System.out.println("transitionPath: " + transitionPath);
            if (gestureComponent.matches(transitionPath)) {
                MetaData gestureMetaData = new MetaData(
                        gestureComponent.elapsedTime, gestureComponent.speed,
                        gestureComponent.direction, gestureComponent.devicesInProximity);
//                System.out.println("HERE: " + gestureMetaData.isClosestFit(maxTime, maxSpeed, minDegree, transitionPath.direction));
//                System.out.println("minDegree: " + minDegree);
                if (gestureMetaData.isClosestFit(maxTime, maxSpeed, minDegree, transitionPath.direction)) {
                    maxTime = gestureComponent.elapsedTime;
                    maxSpeed = gestureComponent.speed;
                    if (!gestureComponent.direction.equals(anyDirection)) {
                        minDegree = Calcs.getAngle3D(gestureComponent.direction, transitionPath.direction);
                    }
                    newTransition = paths.get(gestureComponent);
//                    System.out.println("NEW minDegree: " + minDegree);
                }
            }
        }
        return newTransition;
    }

    @Override
    public String toString() {
        return "Node[ isGesture=" + isGesture + ", paths=" + paths + "]";
    }
}
