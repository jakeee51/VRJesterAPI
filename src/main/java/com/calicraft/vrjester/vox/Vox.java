package com.calicraft.vrjester.vox;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import net.minecraft.util.math.vector.Vector3d;
import org.json.JSONObject;

import static com.calicraft.vrjester.utils.tools.SpawnParticles.createParticles;

public class Vox {
    private int[] id = new int[3];
    private String face, category;
    private boolean display;
    private Vector3d p1, p2, p3, p4, p5, p6, p7, p8;
    public Vector3d d1, d2, centroid;
    public float LENGTH = Constants.VOX_LENGTH, yaw;
    public Tracer trace;
    public final JSONObject config = new Config().readConfig();

    public Vox(Vector3d centroid, float yaw, String face, boolean display, Tracer trace) {
        // Override defaults
        if (config.has("VOX_LENGTH")) {
            float configVoxLength = Float.parseFloat(config.getString("VOX_LENGTH"));
            if (configVoxLength != LENGTH)
                LENGTH = configVoxLength;
        }

        this.centroid = centroid; // Initialize Center of Vox
        this.yaw = yaw; // Initialize facing angle of user
        this.face = face; // Initialize direction user is facing
        this.display = display; // Initialize display flag
        this.trace = trace; // Initialize gesture tracer
        // Initialize Diagonals of Vox
        this.d1 = this.centroid.subtract((LENGTH/2), (LENGTH/2), (LENGTH/2));
        this.d2 = this.centroid.add((LENGTH/2), (LENGTH/2), (LENGTH/2));
        this.setId(new int[]{0, 0, 0}); // Initialize Vox Id
    }

    public boolean hasPoint(Vector3d point) { // Check if point is within Vox
        boolean ret = false;
        if (point.x > d1.x && point.y > d1.y && point.z > d1.z)
            if (point.x < d2.x && point.y < d2.y && point.z < d2.z)
                ret = true;
        return ret;
    }

    public int[] getNeighborVox(Vector3d point) { // Get new Vox Id and traced direction based on which side the point withdrew from the Vox
        // TODO - 3D Joystick: Use Direction object to tell when direction is diagonal & left from right
        int[] ret = this.getId().clone();
        if (point.x < d1.x)
            ret[0]--;
        if (point.y < d1.y) // Down
            ret[1]--;
        if (point.z < d1.z)
            ret[2]--;
        if (point.x > d2.x)
            ret[0]++;
        if (point.y > d2.y) // Up
            ret[1]++;
        if (point.z > d2.z)
            ret[2]++;
        return ret;
    }

    public int[] generateVox(Vector3d point) { // When VRDevice is outside current Vox, new Vox is generated at neighboring position and returns new Id
//        System.out.println("DIRECTION NAME: " + face.getName());
//        System.out.println("DIRECTION AXIS: " + face.getAxis());
//        System.out.println("DIRECTION AXIS: " + face.getAxisDirection());
//        System.out.println("CLOCKWISE: " + face.getClockWise()); // Right
//        System.out.println("CLOCKWISE: " + face.getCounterClockWise()); // Left
        if (!this.hasPoint(point)) { // Check if point is outside of current Vox
            int[] newVoxId = this.getNeighborVox(point);
            double newX = LENGTH * (newVoxId[0] - this.id[0]);
            double newY = LENGTH * (newVoxId[1] - this.id[1]);
            double newZ = LENGTH * (newVoxId[2] - this.id[2]);
            Vector3d newPointDiff = new Vector3d(newX, newY, newZ);
            this.updateVoxPosition(newPointDiff, true);
            this.setId(newVoxId);
        }
        return this.getId();
    }

    public void manifestVox(Vector3d point, Vector3d delta) { // When VRDevice is outside current Vox, new Vox is visualized at neighboring position
        System.out.println("OUT OF VOX: " + !this.hasPoint(point));
        Vector3d newPointDiff = new Vector3d((0), (0), (0));
        if (!this.hasPoint(point)) { // Check if point is outside of current Vox
            int[] newVoxId = this.getNeighborVox(point);
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
