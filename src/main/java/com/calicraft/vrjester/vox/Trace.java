package com.calicraft.vrjester.vox;

import com.calicraft.vrjester.utils.vrdata.VRDevice;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class Trace {
    // POJO for traced Vox state per VRDevice
    public String voxId; // The Vox Id
    public VRDevice vrDevice; // The VRDevice
    public String movement; // Movement taken to get to Vox
    private long elapsedTime = 0; // Time spent within Vox (added on the fly while idle)
    private long speed; // Average speed within Vox (calculated on the fly while idle)
    private Vector3d faceDirection, direction, front, back, right, left; // Average direction within Vox (calculated on the fly while idle)
    private final List<Vector3d[]> poses = new ArrayList<>(); // Poses captured within Vox

    public Trace(String voxId, VRDevice vrDevice, Vector3d[] pose, Vector3d faceDirection) {
        this.voxId = voxId;
        this.vrDevice = vrDevice;
        this.faceDirection = faceDirection;
        setMovementBuckets(faceDirection);
        setElapsedTime(System.nanoTime());
        poses.add(pose);
    }

    @Override
    public String toString() {
        return String.format("VOX %s MOVED: %s", voxId, movement);
    }

    public String getVoxId() {
        return voxId;
    }

    public VRDevice getVrDevice() {
        return vrDevice;
    }

    public String getMovement() {
        return movement;
    }

    public void setMovement(Vector3d gestureDirection) {
        Vector3d dif = gestureDirection.subtract(front);
        System.out.println("FRONT DIR DIF: " + dif);
        // TODO - Determine if gestureDirection falls within the movement buckets based on degree span
    }

    public void setElapsedTime(long currentTime) {
        this.elapsedTime = currentTime - elapsedTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public long getSpeed() {
        return speed;
    }

    public Vector3d getDirection() {
        return direction;
    }

    public void setDirection(Vector3d direction) {
        this.direction = direction;
    }

    public void addPose(Vector3d[] pose) {
        poses.add(pose);
    }

    public List<Vector3d[]> getPoses() {
        return poses;
    }

    public void completeTrace() {
        Vector3d start = poses.get(0)[0];
        Vector3d end = poses.get(poses.size()-1)[0];
        Vector3d gestureDirection = end.subtract(start).normalize();
        System.out.println("GESTURE DIR: " + gestureDirection);
        setMovement(gestureDirection);
        setElapsedTime(System.nanoTime());
    }

    private void setMovementBuckets(Vector3d faceDirection) {
        front = faceDirection;
        back = new Vector3d(-faceDirection.x, faceDirection.y, -faceDirection.z);
        right = new Vector3d(faceDirection.z, faceDirection.y, -faceDirection.x);
        left = new Vector3d(-faceDirection.z, faceDirection.y, faceDirection.x);
    }
}
