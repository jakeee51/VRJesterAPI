package com.calicraft.vrjester.vox;

import com.calicraft.vrjester.utils.vrdata.VRDevice;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class Trace {
    // POJO for traced Vox state per VRDevice
    private String voxId; // The Vox Id
    private VRDevice vrDevice; // The VRDevice
    public String movement; // Movement taken to get to Vox
    private long elapsedTime = 0; // Time spent within Vox (added on the fly while idle)
    private long speed; // Average speed within Vox (calculated on the fly while idle)
    private Vector3d direction; // Average direction within Vox (calculated on the fly while idle)
    private final List<Vector3d[]> poses = new ArrayList<>(); // Poses captured within Vox

    public Trace(String voxId, VRDevice vrDevice, String movement, Vector3d[] pose) {
        this.voxId = voxId;
        this.vrDevice = vrDevice;
        this.movement = movement;
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

    public void completeTrace() {
        setElapsedTime(System.nanoTime());
    }
}
