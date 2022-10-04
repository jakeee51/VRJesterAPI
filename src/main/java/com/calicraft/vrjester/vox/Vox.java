package com.calicraft.vrjester.vox;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import org.json.JSONObject;

import java.util.Arrays;

import static com.calicraft.vrjester.utils.tools.SpawnParticles.createParticles;

public class Vox {
    private VRDevice vrDevice;
    private int[] id, previousId;
    private String name, movementDirection = "idle";
    private Direction faceDirection;
    private Trace trace;
    private boolean display;
    private Vector3d p1, p2, p3, p4, p5, p6, p7, p8;
    public Vector3d d1, d2, centroid;
    public float LENGTH = Constants.VOX_LENGTH, yaw;
    public final JSONObject config = new Config().readConfig();
    public boolean previousCenterVoxVisited = false;

    public Vox(String name, VRDevice vrDevice, Vector3d[] centerPose, float yaw, Direction faceDirection, boolean display) {
        // Override defaults
        if (config.has("VOX_LENGTH")) {
            float configVoxLength = Float.parseFloat(config.getString("VOX_LENGTH"));
            if (configVoxLength != LENGTH)
                LENGTH = configVoxLength;
        }

        this.setId(new int[]{0, 0, 0}); // Initialize Vox Id
        this.previousId = id.clone(); // Initialize soon to be previous Id
        this.name = name; // Initialize name of Vox
        this.vrDevice = vrDevice; // Initialize VRDevice name
        this.centroid = centerPose[0]; // Initialize Center of Vox
        this.yaw = yaw; // Initialize facing angle of user
        this.faceDirection = faceDirection; // Initialize direction user is facing
        this.trace = new Trace(Arrays.toString(id), vrDevice, movementDirection, centerPose);
        this.display = display; // Initialize display flag
        // Initialize Diagonals of Vox
        this.d1 = this.centroid.subtract((LENGTH/2), (LENGTH/2), (LENGTH/2));
        this.d2 = this.centroid.add((LENGTH/2), (LENGTH/2), (LENGTH/2));
    }

    public boolean hasPoint(Vector3d point) { // Check if point is within Vox
        boolean ret = false;
        if (point.x > d1.x && point.y > d1.y && point.z > d1.z)
            if (point.x < d2.x && point.y < d2.y && point.z < d2.z)
                ret = true;
        return ret;
    }

    private String getMovement(String axis, int axisDirection) {
        String movement = movementDirection;
        System.out.println("AXIS MOVED ON: " + axis);
        System.out.println("FACING: " + faceDirection.toString());
        System.out.println("AXIS DIRECTION: " + axisDirection);
        switch(axis) {
            case "x":
                if (faceDirection.toString().equals("east")) {
                    if (axisDirection == 1)
                        movement = "right";
                    else
                        movement = "left";
                } else if (faceDirection.toString().equals("west")) {
                    if (axisDirection == 1)
                        movement = "right";
                    else
                        movement = "left";
                } else if (faceDirection.toString().equals("north")) {
                    if (axisDirection == 1)
                        movement = "right";
                    else
                        movement = "left";
                } else if (faceDirection.toString().equals("south")) {
                    if (axisDirection == 1)
                        movement = "left";
                    else
                        movement = "right";
                }
                break;
            case "z":
                if (faceDirection.toString().equals("south")) {
                    if (axisDirection == 1)
                        movement = "back";
                    else
                        movement = "front";
                } else if (faceDirection.toString().equals("north")) {
                    if (axisDirection == 1)
                        movement = "back";
                    else
                        movement = "front";
                } else if (faceDirection.toString().equals("east")) {
                    if (axisDirection == 1)
                        movement = "back";
                    else
                        movement = "front";
                } else if (faceDirection.toString().equals("west")) {
                    if (axisDirection == 1)
                        movement = "back";
                    else
                        movement = "front";
                }
                break;
        }
        return movement;
    }

    private int[] getVoxNeighbor(Vector3d point) { // Get new Vox Id and set traced movement direction based on which side the point withdrew from the Vox
//        System.out.println("DIRECTION NAME: " + faceDirection.getName());
//        System.out.println("AXIS: " + faceDirection.getAxis().getName());
//        System.out.println("AXIS DIRECTION: " + faceDirection.getAxisDirection().toString());
//        System.out.println("RIGHT: " + faceDirection.getClockWise()); // Right
//        System.out.println("LEFT: " + faceDirection.getCounterClockWise()); // Left

//      DIRECTION NAME: north
//      AXIS: -z
//      DIRECTION NAME: east
//      AXIS: +x
//      DIRECTION NAME: south
//      AXIS: +z
//      DIRECTION NAME: west
//      AXIS: -x
        int[] ret = this.getId().clone();
        if (point.y < d1.y) { // Down
            ret[1]--; movementDirection = "down";
        }
        if (point.y > d2.y) { // Up
            ret[1]++; movementDirection = "up";
        }
        if (point.x < d1.x) {
            ret[0]--; movementDirection = getMovement("x", -1);
        }
        if (point.x > d2.x) {
            ret[0]++; movementDirection = getMovement("x", 1);
        }
        if (point.z < d1.z) {
            ret[2]--; movementDirection = getMovement("z", -1);
        }
        if (point.z > d2.z) {
            ret[2]++; movementDirection = getMovement("z", 1);
        }
        return ret;
    }

    public void generateVox(Vector3d[] pose) { // When VRDevice is outside current Vox, new Vox is generated at neighboring position and returns the Trace data
        if (!this.hasPoint(pose[0])) { // Check if point is outside of current Vox
            int[] newVoxId = this.getVoxNeighbor(pose[0]);
            System.out.println("MOVED: " + movementDirection);
            double newX = LENGTH * (newVoxId[0] - this.id[0]);
            double newY = LENGTH * (newVoxId[1] - this.id[1]);
            double newZ = LENGTH * (newVoxId[2] - this.id[2]);
            Vector3d newPointDiff = new Vector3d(newX, newY, newZ);
            updateVoxPosition(newPointDiff, true);
            setId(newVoxId);
        } else {
            trace.addPose(pose); // Constantly update the current Trace
        }
    }

    public void manifestVox(Vector3d point, Vector3d delta) { // When VRDevice is outside current Vox, new Vox is visualized at neighboring position
        Vector3d newPointDiff = new Vector3d((0), (0), (0));
        if (!this.hasPoint(point)) { // Check if point is outside of current Vox
            int[] newVoxId = this.getVoxNeighbor(point);
            double newX = LENGTH * (newVoxId[0] - this.id[0]);
            double newY = LENGTH * (newVoxId[1] - this.id[1]);
            double newZ = LENGTH * (newVoxId[2] - this.id[2]);
            newPointDiff = new Vector3d(newX, newY, newZ);
            this.setId(newVoxId);
        }
        this.updateVoxPosition(newPointDiff, true);
    }

    public void updateVoxPosition(Vector3d dif, boolean useDif) { // Update Vox position values based on delta movement
        if (dif.x == 0 && dif.y == 0 && dif.z == 0 && !display)
            return;

        // Center of Vox
        if (!display || useDif)
            centroid = centroid.add(dif);
        else
            centroid = dif;

        // Diagonals
        this.d1 = centroid.subtract((LENGTH/2), (LENGTH/2), (LENGTH/2));
        this.d2 = centroid.add((LENGTH/2), (LENGTH/2), (LENGTH/2));

        // Bottom square plane
        p1 = d1;
        p2 = centroid.add((LENGTH/2), -(LENGTH/2), -(LENGTH/2));
        p3 = centroid.add((LENGTH/2), (LENGTH/2), -(LENGTH/2));
        p4 = centroid.add(-(LENGTH/2), (LENGTH/2), -(LENGTH/2));

        // Top square plane
        p5 = centroid.add(-(LENGTH/2), (LENGTH/2), (LENGTH/2));
        p6 = centroid.add(-(LENGTH/2), -(LENGTH/2), (LENGTH/2));
        p7 = centroid.add((LENGTH/2), -(LENGTH/2), (LENGTH/2));
        p8 = d2;

        if (display)
            this.displayVox();
    }

    public int[] getId() {
        return id;
    }

    public void setId(int[] id) {
         this.id = id;
    }

    public int[] getPreviousId() {
        return previousId;
    }

    public void setPreviousId(int[] previousId) {
        this.previousId = previousId;
    }

    public String getName() {
        return name;
    }

    public VRDevice getVrDevice() {
        return vrDevice;
    }

    public Trace getTrace() {
        return trace;
    }

    public Trace beginTrace(Vector3d[] pose) { // Begin with a new Trace object
        trace = new Trace(Arrays.toString(id), vrDevice, movementDirection, pose);
        return trace;
    }

    private void displayVox() {
        createParticles(this.p1);
        createParticles(this.p2);
        createParticles(this.p3);
        createParticles(this.p4);
        createParticles(this.p5);
        createParticles(this.p6);
        createParticles(this.p7);
        createParticles(this.p8);
    }
}
