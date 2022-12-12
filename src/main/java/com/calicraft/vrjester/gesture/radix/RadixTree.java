package com.calicraft.vrjester.gesture.radix;

import com.calicraft.vrjester.gesture.GestureComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RadixTree {
    // Class that represents a gesture pattern namespace with each tree corresponding to a VRDevice

    // NOTE - Gestures are inserted distinctly based on hashed field values in GestureComponent record
    //      However, searches aren't distinct as they match based on order of tree traversal. Whichever
    //      gesture was inserted first will likely get found first since this RadixTree
    //      can store gestures that overlap. This is an intended function of being able to search
    //      using the same "tracked gesture" object based on whether their elapsedTime & speed are within
    //      a "stored gesture's" elapsedTime & speed range.

    public String vrDevice;
    private static final int NO_MISMATCH = -1;
    public Node root;

    public RadixTree(String vrDevice) {
        this.vrDevice = vrDevice;
        root = new Node(false);
    }

    private int getFirstMismatchGestureComponent(List<GestureComponent> gesture, List<GestureComponent> edgeGestureComponent) {
        int LENGTH = Math.min(gesture.size(), edgeGestureComponent.size());
        for (int i = 1; i < LENGTH; i++) {
            if (!gesture.get(i).equals(edgeGestureComponent.get(i))) {
                return i;
            }
        }
        return NO_MISMATCH;
    }

    //Helpful method to debug and to see all the gestures
    public void printAllGestures(HashMap<Integer, String> gestureMapping) {
        printAllGestures(root, new ArrayList<>(), gestureMapping);
    }

    private void printAllGestures(Node current, List<GestureComponent> result, HashMap<Integer, String> gestureMapping) {
        if (current.isGesture)
            System.out.println(gestureMapping.get(result.hashCode()) + ": " + result);

        for (Path path : current.paths.values())
            printAllGestures(path.next, GestureComponent.concat(result, path.gestureComponent), gestureMapping);
    }

    public void insert(List<GestureComponent> gesture) {
        Node current = root;
        int currIndex = 0;

        //Iterative approach
        while (currIndex < gesture.size()) {
            GestureComponent transitionGestureComponent = gesture.get(currIndex);
            Path currentPath = current.getTransition(transitionGestureComponent);
            //Updated version of the input gesture
            List<GestureComponent> currGestureComponent = gesture.subList(currIndex, gesture.size());

            //There is no associated edge with the first character of the current string
            //so simply add the rest of the string and finish
            if (currentPath == null) {
                current.paths.put(transitionGestureComponent, new Path(currGestureComponent));
                break;
            }

            int splitIndex = getFirstMismatchGestureComponent(currGestureComponent, currentPath.gestureComponent);
            if (splitIndex == NO_MISMATCH) {
                //The edge and leftover string are the same length
                //so finish and update the next node as a gesture node
                if (currGestureComponent.size() == currentPath.gestureComponent.size()) {
                    currentPath.next.isGesture = true;
                    break;
                } else if (currGestureComponent.size() < currentPath.gestureComponent.size()) {
                    //The leftover gesture is a prefix to the edge string, so split
                    List<GestureComponent> suffix = currentPath.gestureComponent.subList(currGestureComponent.size()-1, currGestureComponent.size());
                    currentPath.gestureComponent = currGestureComponent;
                    Node newNext = new Node(true);
                    Node afterNewNext = currentPath.next;
                    currentPath.next = newNext;
                    newNext.addGestureComponent(suffix, afterNewNext);
                    break;
                } else { //currStr.length() > currentEdge.label.length()
                    //There is leftover string after a perfect match
                    splitIndex = currentPath.gestureComponent.size();
                }
            } else {
                //The leftover string and edge string differed, so split at point
                List<GestureComponent> suffix = currentPath.gestureComponent.subList(splitIndex, currentPath.gestureComponent.size());
                currentPath.gestureComponent = currentPath.gestureComponent.subList(0, splitIndex);
                Node prevNext = currentPath.next;
                currentPath.next = new Node(false);
                currentPath.next.addGestureComponent(suffix, prevNext);
            }

            //Traverse the tree
            current = currentPath.next;
            currIndex += splitIndex;
        }
    }

    public void delete(List<GestureComponent> gesture) {
        root = delete(root, gesture);
    }

    private Node delete(Node current, List<GestureComponent> gesture) {
        //base case, all the characters have been matched from previous checks
        if (gesture.isEmpty()) {
            //Has no other edges,
            if (current.paths.isEmpty() && current != root) {
                return null;
            }
            current.isGesture = false;
            return current;
        }

        GestureComponent transitionGestureComponent = gesture.get(0);
        Path path = current.getTransition(transitionGestureComponent);
        //Has no edge for the current gesture or the gesture doesn't exist
        if (path == null || !GestureComponent.startsWith(gesture, path.gestureComponent)) {
            return current;
        }
        Node deleted = delete(path.next, gesture.subList(path.gestureComponent.size(), gesture.size()));
        if (deleted == null) {
            current.paths.remove(transitionGestureComponent);
            if (current.totalGestureComponent() == 0 && !current.isGesture && current != root) {
                return null;
            }
        } else if (deleted.totalGestureComponent() == 1 && !deleted.isGesture) {
            current.paths.remove(transitionGestureComponent);
            for (Path afterDeleted : deleted.paths.values()) {
                current.addGestureComponent(GestureComponent.concat(path.gestureComponent, afterDeleted.gestureComponent), afterDeleted.next);
            }
        }
        return current;
    }

    // Returns matched gesture is found and null if not found
    public List<GestureComponent> search(List<GestureComponent> gesture) {
        List<GestureComponent> ret = null;
        Node current = root;
        int currIndex = 0;
        while (currIndex < gesture.size()) {
            GestureComponent transitionGestureComponent = gesture.get(currIndex);
            Path path = current.getTracedGestureComponent(transitionGestureComponent);
            if (path == null)
                return null;

            List<GestureComponent> currSubGestureComponent = gesture.subList(currIndex, gesture.size());
            if (!GestureComponent.startsWith(path.gestureComponent, currSubGestureComponent))
                return null;

            currIndex += path.gestureComponent.size();
            current = path.next;
            ret = GestureComponent.concat(ret, path.gestureComponent);
        }
        return ret;
    }
}
