package com.calicraft.vrjester.gesture.radix;

import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static com.calicraft.vrjester.utils.tools.Calcs.getAngle2D;

public class Trace {
    // POJO for traced Vox state per VRDevice in an iteration of time
    public String voxId; // The Vox Id
    public VRDevice vrDevice; // The VRDevice
    public String movement = "idle"; // Movement taken to get to Vox
    public String inProximityOf;
    private long elapsedTime = 0; // Time spent within Vox (added on the fly while idle)
    private long speed; // Average speed within Vox (calculated on the fly while idle)
    private Vec3 faceDirection, direction, front, back, right, left,
                     frontRight, frontLeft, backRight, backLeft;
    private final List<Vec3[]> poses = new ArrayList<>(); // Poses captured within Vox

    public Trace(String voxId, VRDevice vrDevice, Vec3[] pose, Vec3 faceDirection) {
        this.voxId = voxId;
        this.vrDevice = vrDevice;
        this.faceDirection = faceDirection;
        setMovementBuckets(faceDirection);
        setElapsedTime(System.nanoTime());
        poses.add(pose);
    }

    @Override
    public String toString() {
        return String.format("VOX: %s | MOVED: %s | Time Elapsed: %dl", voxId, movement, elapsedTime);
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

    public void setMovement(String movement) {
        this.movement = movement;
    }

    public void setMovement(Vec3 gestureDirection) {
        // TODO - Divide Constants.DEGREE_SPAN by 2 after adding diagonals handler
        if (!movement.equals("idle")) {
            // TODO - Possibly handle diagonal ups and downs using concatenation
            System.out.println("MOVED UP OR DOWN");
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

    public Vec3 getDirection() {
        return direction;
    }

    public void setDirection(Vec3 direction) {
        this.direction = direction;
    }

    public void addPose(Vec3[] pose) {
        poses.add(pose);
    }

    public List<Vec3[]> getPoses() {
        return poses;
    }

    public void completeTrace(Vec3 end) {
        Vec3 start = poses.get(0)[0];
        Vec3 gestureDirection = end.subtract(start).normalize();
        setMovement(gestureDirection);
        setElapsedTime(System.nanoTime());
    }

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
