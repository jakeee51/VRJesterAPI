package com.calicraft.vrjester.vox;

public class Trace {
    // POJO for traced Vox state per VRDevice
    public String voxId;
    public String movement;
    public long elapsedTime;
    public long speed;

    public Trace(String voxId, String movement) {
        this.voxId = voxId;
        this.movement = movement;

    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
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
}