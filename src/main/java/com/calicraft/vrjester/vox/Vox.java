package com.calicraft.vrjester.vox;

import com.calicraft.vrjester.utils.tools.Constants;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Arrays;

public class Vox {
//    private Vector3d p1;
//    private Vector3d p2;
//    private Vector3d p3;
//    private Vector3d p4;
//    private Vector3d p5;
//    private Vector3d p6;
//    private Vector3d p7;
//    private Vector3d p8;
    private int[] id = new int[3];
    private int[] front, back, left, right, up, down; // Neighboring Voxes based on face
    public Vector3d d1, d2, centroid;
    public final float LENGTH = Constants.VOX_LENGTH;

    public Vox(Vector3d centroid) {
        // Initialize Center of Vox
        this.centroid = centroid;

        // Initialize Diagonals of Vox
        this.d1 = this.centroid.subtract((LENGTH/2), (LENGTH/2), (LENGTH/2));
        this.d2 = this.centroid.add((LENGTH/2), (LENGTH/2), (LENGTH/2));

        // Initialize Vox Id
        this.setId(new int[]{0, 0, 0});
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

    public int[] updateVoxId(Vector3d point) { // Generates new Vox position and returns new Id if VRDevice is outside Vox
        if (!this.hasPoint(point)) {
            int[] newVoxId = this.getNeighborVoxId(point);
            double newX = LENGTH * (newVoxId[0] - this.id[0]);
            double newY = LENGTH * (newVoxId[1] - this.id[1]);
            double newZ = LENGTH * (newVoxId[2] - this.id[2]);
            Vector3d newPointDiff = new Vector3d(newX, newY, newZ);
            this.updateVox(newPointDiff);
            this.setId(newVoxId);
        }
        return this.getId();
    }

    public void updateVox(Vector3d dif) { // Update Vox position values based on player delta movement
        if (dif.x == 0 && dif.y == 0 && dif.z == 0)
            return;
        if (dif.x > 100)// Ignore initial player position
            dif = dif.multiply((0),(0),(0));
        if (Math.abs(dif.y) <= 0.09) // Temporary strange Y delta value solution
            dif = dif.multiply((1),(0),(1));

        // Center of Vox
        this.centroid = centroid.add(dif);

        // Diagonals
        this.d1 = this.centroid.subtract((LENGTH/2), (LENGTH/2), (LENGTH/2));
        this.d2 = this.centroid.add((LENGTH/2), (LENGTH/2), (LENGTH/2));

//        // Bottom square plane
//        p1 = d1;
//        p2 = centroid.subtract((0), (LENGTH/2), (0));
//        p3 = centroid.subtract((LENGTH/2), (0), (0));
//        p4 = centroid.subtract((LENGTH/2), (LENGTH/2), (0));
//
//        // Top square plane
//        p5 = centroid.add((0), (0), (LENGTH/2));
//        p6 = centroid.add((0), (LENGTH/2), (LENGTH/2));
//        p7 = centroid.add((LENGTH/2), (0), (LENGTH/2));
//        p8 = d2;
    }

    public int[] getId() {
        return id;
    }

    public void setId(int[] id) {
         this.id = id;
    }
}
