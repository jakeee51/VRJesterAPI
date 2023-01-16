package com.calicraft.vrjester.gesture;

import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import net.minecraft.world.phys.Vec3;

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
    public final Map<String, Integer> devicesInProximity = new HashMap<>(); // Other VRDevices within this Vox
    private Vec3 direction, front, back, right, left,
                     frontRight, frontLeft, backRight, backLeft;
    private final List<Vec3[]> poses = new ArrayList<>(); // Poses captured within Vox

    public GestureTrace(String voxId, VRDevice vrDevice, Vec3[] pose, Vec3 faceDirection) {
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

    // Convert Track object to GestureComponent
    public GestureComponent toGestureComponent() {
        return new GestureComponent(getVrDevice(), getMovement(), getElapsedTime(),
                getSpeed(), new com.calicraft.vrjester.utils.tools.Vec3(getDirection()), getDevicesInProximity());
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
    public void setMovement(Vec3 gestureDirection) {
        // TODO - Divide Constants.DEGREE_SPAN by 2 after adding diagonals handler
        if (!movement.equals("idle")) {
            // TODO - Possibly handle diagonal ups and downs using concatenation
        } else if (getAngle2D(front, gestureDirection) <= Constants.DEGREE_SPAN) {
            movement = "forward";
        } else if (getAngle2D(back, gestureDirection) <= Constants.DEGREE_SPAN) {
            movement = "back";
        } else if (getAngle2D(right, gestureDirection) <= Constants.DEGREE_SPAN) {
            movement = "right";
        } else if (getAngle2D(left, gestureDirection) <= Constants.DEGREE_SPAN) {
            movement = "left";
        } else if (getAngle2D(frontRight, gestureDirection) <= Constants.DEGREE_SPAN) {
            movement = "forward_right";
        } else if (getAngle2D(frontLeft, gestureDirection) <= Constants.DEGREE_SPAN) {
            movement = "forward_left";
        } else if (getAngle2D(backRight, gestureDirection) <= Constants.DEGREE_SPAN) {
            movement = "back_right";
        } else if (getAngle2D(backLeft, gestureDirection) <= Constants.DEGREE_SPAN) {
            movement = "back_left";
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
    public void setSpeed(Vec3 end) {
        this.speed = (getMagnitude3D(end.subtract(poses.get(0)[0])) / elapsedTime) * 1000000;
    }

    public double getSpeed() {
        return speed;
    }

    public Vec3 getDirection() {
        return direction;
    }

    public void setDirection(Vec3 direction) {
        this.direction = direction;
    }

    public void updateDeviceInProximity(String vrDevice, Integer times) {
        devicesInProximity.put(vrDevice, times+1);
    }

    public Map<String, Integer> getDevicesInProximity() {
        return devicesInProximity;
    }

    public void addPose(Vec3[] pose) {
        poses.add(pose);
    }

    // Set all final values resulting from a VRDevice moving into a new Vox
    public void completeTrace(Vec3[] end) {
        // Note: After this executes, it is ready to be converted into a GestureComponent
        Vec3 start = poses.get(0)[0];
        Vec3 gestureDirection = end[0].subtract(start).normalize();
        setMovement(gestureDirection);
        setElapsedTime(System.nanoTime());
        setSpeed(end[0]);
        setDirection(end[1]);
    }

    // Set all final values resulting from a VRDevice moving into a new Vox
    public void completeIdleTrace(Vec3[] end) {
        // Note: After this executes, it is ready to be converted into a GestureComponent
        setElapsedTime(System.nanoTime());
        setSpeed(end[0]);
        setDirection(end[1]);
    }

    // Set all movement directional buckets used to determine movement
    private void setMovementBuckets(Vec3 faceDirection) {
        front = faceDirection;
        back = new Vec3(-faceDirection.x, faceDirection.y, -faceDirection.z);
        right = new Vec3(-faceDirection.z, faceDirection.y, faceDirection.x);
        left = new Vec3(faceDirection.z, faceDirection.y, -faceDirection.x);
        frontRight = front.yRot(Constants.DEGREE_SPAN);
        frontLeft = front.yRot(-Constants.DEGREE_SPAN);
        backRight = back.yRot(Constants.DEGREE_SPAN);
        backLeft = back.yRot(-Constants.DEGREE_SPAN);
    }
}
