package com.calicraft.vrjester.gesture.radix;

import java.util.ArrayList;
import java.util.List;

public class RadixTree {
    // Class that represents the gesture pattern namespace with each tree corresponding to a VRDevice

    // NOTE - Gestures are inserted distinctly based on hashed field values in Path record
    //      However, searches aren't distinct as they match based on order of tree traversal. Whichever
    //      gesture was inserted first will likely get found first since this RadixTree
    //      can store gestures that overlap. This is an intended function of being able to search
    //      using the same "tracked gesture" object based on whether their elapsedTime & speed are within
    //      a "stored gesture's" elapsedTime & speed range.

    public String vrDevice;
    private static final int NO_MISMATCH = -1;
    private Node root;

    public RadixTree(String vrDevice) {
        this.vrDevice = vrDevice;
        root = new Node(false);
    }

    private int getFirstMismatchPath(List<Path> gesture, List<Path> edgePath) {
        int LENGTH = Math.min(gesture.size(), edgePath.size());
        for (int i = 1; i < LENGTH; i++) {
            if (!gesture.get(i).equals(edgePath.get(i))) {
                return i;
            }
        }
        return NO_MISMATCH;
    }

    //Helpful method to debug and to see all the gestures
    public void printAllWords() {
        printAllWords(root, new ArrayList<>());
    }

    private void printAllWords(Node current, List<Path> result) {
        if (current.isGesture) {
            System.out.println(result);
        }

        for (Trace trace : current.paths.values()) {
            printAllWords(trace.next, Path.concat(result, trace.path));
        }
    }

    public void insert(List<Path> gesture) {
        Node current = root;
        int currIndex = 0;

        //Iterative approach
        while (currIndex < gesture.size()) {
            Path transitionPath = gesture.get(currIndex);
            Trace currentTrace = current.getTransition(transitionPath);
            //Updated version of the input gesture
            List<Path> currPath = gesture.subList(currIndex, gesture.size());

            //There is no associated edge with the first character of the current string
            //so simply add the rest of the string and finish
            if (currentTrace == null) {
                current.paths.put(transitionPath, new Trace(currPath));
                break;
            }

            int splitIndex = getFirstMismatchPath(currPath, currentTrace.path);
            if (splitIndex == NO_MISMATCH) {
                //The edge and leftover string are the same length
                //so finish and update the next node as a gesture node
                if (currPath.size() == currentTrace.path.size()) {
                    currentTrace.next.isGesture = true;
                    break;
                } else if (currPath.size() < currentTrace.path.size()) {
                    //The leftover gesture is a prefix to the edge string, so split
                    List<Path> suffix = currentTrace.path.subList(currPath.size()-1, currPath.size());
                    currentTrace.path = currPath;
                    Node newNext = new Node(true);
                    Node afterNewNext = currentTrace.next;
                    currentTrace.next = newNext;
                    newNext.addPath(suffix, afterNewNext);
                    break;
                } else { //currStr.length() > currentEdge.label.length()
                    //There is leftover string after a perfect match
                    splitIndex = currentTrace.path.size();
                }
            } else {
                //The leftover string and edge string differed, so split at point
                List<Path> suffix = currentTrace.path.subList(splitIndex, currentTrace.path.size());
                currentTrace.path = currentTrace.path.subList(0, splitIndex);
                Node prevNext = currentTrace.next;
                currentTrace.next = new Node(false);
                currentTrace.next.addPath(suffix, prevNext);
            }

            //Traverse the tree
            current = currentTrace.next;
            currIndex += splitIndex;
        }
    }

    public void delete(List<Path> gesture) {
        root = delete(root, gesture);
    }

    private Node delete(Node current, List<Path> gesture) {
        //base case, all the characters have been matched from previous checks
        if (gesture.isEmpty()) {
            //Has no other edges,
            if (current.paths.isEmpty() && current != root) {
                return null;
            }
            current.isGesture = false;
            return current;
        }

        Path transitionPath = gesture.get(0);
        Trace trace = current.getTransition(transitionPath);
        //Has no edge for the current gesture or the gesture doesn't exist
        if (trace == null || !Path.startsWith(gesture, trace.path)) {
            return current;
        }
        Node deleted = delete(trace.next, gesture.subList(trace.path.size(), gesture.size()));
        if (deleted == null) {
            current.paths.remove(transitionPath);
            if (current.totalPaths() == 0 && !current.isGesture && current != root) {
                return null;
            }
        } else if (deleted.totalPaths() == 1 && !deleted.isGesture) {
            current.paths.remove(transitionPath);
            for (Trace afterDeleted : deleted.paths.values()) {
                current.addPath(Path.concat(trace.path, afterDeleted.path), afterDeleted.next);
            }
        }
        return current;
    }

    public List<Path> search(List<Path> gesture) { // Returns matched gesture is found and null if not found
        List<Path> ret = new ArrayList<>();
        Node current = root;
        int currIndex = 0;
        while (currIndex < gesture.size()) {
            Path transitionPath = gesture.get(currIndex);
            Trace trace = current.getTracedPath(transitionPath);
            if (trace == null)
                return null;

            List<Path> currSubPath = gesture.subList(currIndex, gesture.size());
            if (!Path.startsWith(trace.path, currSubPath))
                return null;

            currIndex += trace.path.size();
            current = trace.next;
            ret = Path.concat(ret, trace.path);
        }
        return ret;
    }
}
