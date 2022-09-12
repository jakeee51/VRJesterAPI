package com.calicraft.vrjester.vox;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import net.minecraft.util.math.vector.Vector3d;
import org.json.JSONObject;

import static com.calicraft.vrjester.utils.tools.SpawnParticles.createParticles;

public class Vox {
    private int[] id = new int[3];
    private String category; // TODO - Sub Voxes
    private boolean display;
    private int[] front, back, left, right, up, down; // Neighboring Voxes based on face
    private Vector3d p1, p2, p3, p4, p5, p6, p7, p8;
    public Vector3d d1, d2, centroid;
    public float LENGTH = Constants.VOX_LENGTH;
    public final JSONObject config = new Config().readConfig();

    public Vox(Vector3d centroid, boolean display) {
        // Override defaults
        if (config.has("VOX_LENGTH")) {
            float configFloat = Float.parseFloat(config.getString("VOX_LENGTH"));
            if (configFloat != LENGTH)
                LENGTH = configFloat;
        }

        this.centroid = centroid; // Initialize Center of Vox
        this.display = display; // Initialize display flag
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

    public int[] getNeighborVoxId(Vector3d point) {
        int[] ret = this.getId().clone();
        if (point.x < d1.x)
            ret[0]--;
        if (point.y < d1.y)
            ret[1]--;
        if (point.z < d1.z)
            ret[2]--;
        if (point.x > d2.x)
            ret[0]++;
        if (point.y > d2.y)
            ret[1]++;
        if (point.z > d2.z)
            ret[2]++;
        return ret;
    }

    public int[] generateVox(Vector3d point) { // Generates new Vox at neighboring position and returns new Id if VRDevice is outside current Vox
        if (!this.hasPoint(point)) { // Check if point is outside of current Vox
            int[] newVoxId = this.getNeighborVoxId(point);
            double newX = LENGTH * (newVoxId[0] - this.id[0]);
            double newY = LENGTH * (newVoxId[1] - this.id[1]);
            double newZ = LENGTH * (newVoxId[2] - this.id[2]);
            Vector3d newPointDiff = new Vector3d(newX, newY, newZ);
            this.updateVoxPosition(newPointDiff, true);
            this.setId(newVoxId);
        }
        if (display)
            this.displayVox();
        return this.getId();
    }

    public void updateVoxPosition(Vector3d dif, boolean useDif) { // Update Vox position values based on player delta movement
        if (dif.x == 0 && dif.y == 0 && dif.z == 0)
            return;
//        if (dif.x > 100)// Ignore initial player position
//            dif = dif.multiply((0),(0),(0));
//        if (Math.abs(dif.y) <= 0.09) // Temporary strange Y delta value solution
//            dif = dif.multiply((1),(0),(1));

        // Center of Vox
        if(!display || useDif)
            this.centroid = centroid.add(dif);
        else
            this.centroid = dif;

        // Diagonals
        this.d1 = this.centroid.subtract((LENGTH/2), (LENGTH/2), (LENGTH/2));
        this.d2 = this.centroid.add((LENGTH/2), (LENGTH/2), (LENGTH/2));

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

    public int[] getId() {
        return id;
    }

    public void setId(int[] id) {
         this.id = id;
    }
}
