package com.calicraft.vrjester.gesture;

import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.calicraft.vrjester.utils.tools.Calcs.getAngle2D;
import static com.calicraft.vrjester.utils.tools.Calcs.getMagnitude3D;

public class GestureTrace {
    // POJO for tracing Vox state per VRDevice in an iteration of time

    public String voxId; // The Vox ID
    public String vrDevice; // The VRDevice
    public String movement = "idle"; // Movement taken to get to Vox
    public long elapsedTime = 0; // Time spent within Vox in ms (added on the fly while idle)
    public double speed; // Average speed within Vox (calculated on the fly while idle)
    public final Map<String, Integer> devicesInProximity = new HashMap<>(); // Time other VRDevices spent within this Vox
    private Vector3d direction, front, back, right, left;
    private final List<Vector3d[]> poses = new ArrayList<>(); // Poses captured within Vox

    public GestureTrace(String voxId, VRDevice vrDevice, Vector3d[] pose, Vector3d faceDirection) {
        this.voxId = voxId;
        this.vrDevice = vrDevice.name();
        setMovementBuckets(faceDirection);
        setElapsedTime(System.nanoTime());
        poses.add(pose);
    }

    @Override
    public String toString() {
        return String.format("VRDEVICE: %s | MOVED: %s | Time Elapsed: %dl", vrDevice, movement, elapsedTime);
    }

    // Convert Trace object to GestureComponent
    public GestureComponent toGestureComponent() {
        return new GestureComponent(getVrDevice(), getMovement(), getElapsedTime(),
                getSpeed(), getDirection(), getDevicesInProximity());
    }

    public String getVoxId() {
        return voxId;
    }

    public String getVrDevice() {
        return vrDevice;
    }

    public String getMovement() {
        return movement;
    }

    public void setMovement(String movement) {
        this.movement = movement;
    }

    // Set the movement the VRDevice took to arrive at this current Trace
    public void setMovement(Vector3d gestureDirection) {
        if (gestureDirection.y > 0.85D) {
            movement = "up";
        } else if (gestureDirection.y < -0.85D) {
            movement = "down";
        } else if (getAngle2D(front, gestureDirection) <= Constants.MOVEMENT_DEGREE_SPAN) {
            movement = "forward";
        } else if (getAngle2D(back, gestureDirection) <= Constants.MOVEMENT_DEGREE_SPAN) {
            movement = "back";
        } else if (getAngle2D(right, gestureDirection) <= Constants.MOVEMENT_DEGREE_SPAN) {
            movement = "right";
        } else if (getAngle2D(left, gestureDirection) <= Constants.MOVEMENT_DEGREE_SPAN) {
            movement = "left";
        } else {
            System.out.println("NO MOVEMENT RECOGNIZED!");
            System.out.println("ANGLE BETWEEN FACING DIRECTION AND GESTURE: " + getAngle2D(front, gestureDirection));
        }
    }

    // Set elapsed time in ms
    public void setElapsedTime(long currentTime) {
        if (elapsedTime == 0)
            elapsedTime = currentTime;
        else
            elapsedTime = (currentTime - elapsedTime) / 1000000;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    // Set speed in ms
    public void setSpeed(Vector3d end) {
        this.speed = (getMagnitude3D(end.subtract(poses.get(0)[0])) / elapsedTime) * 1000000;
    }

    public double getSpeed() {
        return speed;
    }

    public Vector3d getDirection() {
        return direction;
    }

    public void setDirection(Vector3d direction) {
        this.direction = direction;
    }

    public void updateDeviceInProximity(String vrDevice, Integer times) {
        devicesInProximity.put(vrDevice, times+1);
    }

    public Map<String, Integer> getDevicesInProximity() {
        return devicesInProximity;
    }

    public void addPose(Vector3d[] pose) {
        poses.add(pose);
    }

    // Set all final values resulting from a VRDevice moving into a new Vox
    public void completeTrace(Vector3d[] end) {
        // Note: After this executes, it is ready to be converted into a GestureComponent
        Vector3d start = poses.get(0)[0];
        Vector3d gestureDirection = end[0].subtract(start).normalize();
        setMovement(gestureDirection);
        setElapsedTime(System.nanoTime());
        setSpeed(end[0]);
        setDirection(end[1]);
    }

    // Set all final values resulting from a VRDevice completing its trace while idle
    public void completeIdleTrace(Vector3d[] end) {
        // Note: After this executes, it is ready to be converted into a GestureComponent
        setElapsedTime(System.nanoTime());
        setSpeed(end[0]);
        setDirection(end[1]);
    }

    // Set all movement directional buckets used to determine movement
    private void setMovementBuckets(Vector3d faceDirection) {
        front = faceDirection;
        back = new Vector3d(-faceDirection.x, faceDirection.y, -faceDirection.z);
        right = new Vector3d(-faceDirection.z, faceDirection.y, faceDirection.x);
        left = new Vector3d(faceDirection.z, faceDirection.y, -faceDirection.x);
        // yRot method causes following error: Unable to retrieve inform for type dumb-color
    }
}
