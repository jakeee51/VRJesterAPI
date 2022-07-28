package com.calicraft.vrjester.vox;

import com.calicraft.vrjester.utils.tools.Constants;
import net.minecraft.util.math.vector.Vector3d;

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

        // Initialize Neighboring Vox Location ID's
        // setVoxNeighbors(); // TODO - Implement this method

        // Initialize Vox Id
        this.setId(new int[]{0, 0, 0});

        // Initialize all points of Vox
        this.updateVox(centroid);
    }

    public boolean hasPoint(Vector3d point) { // Check if point is within Vox
        boolean ret = false;
        if (point.x > d1.x && point.y > d1.y && point.z > d1.z)
            if (point.x < d2.x && point.y < d2.y && point.z < d2.z)
                ret = true;
        return ret;
    }

    public int[] getNeighborVoxId(Vector3d point) {
        int[] ret = this.getId();
        if (point.x() < d1.x)
            ret[0]--;
        if (point.y() < d1.y)
            ret[1]--;
        if (point.z() < d1.z)
            ret[2]--;
        if (point.x() > d2.x)
            ret[0]++;
        if (point.y() > d2.y)
            ret[1]++;
        if (point.z() > d2.z)
            ret[2]++;
        return ret;
    }

    public int[] updateVoxId(Vector3d point) {
        int[] ret = this.getId();
        if (!this.hasPoint(point)) {
            ret = this.getNeighborVoxId(point);
            this.setId(ret);
        }
        return ret;
    }

    public void updateVox(Vector3d dif) { // Update Vox values
        // Center of Vox
        if (dif.x > 100) // Ignore initial player position
            dif = dif.multiply(0,0,0);
        dif = dif.multiply(1,0,1);
        centroid = centroid.add(dif);

        // Diagonals
        d1 = centroid.subtract((LENGTH/2), (LENGTH/2), (LENGTH/2));
        d2 = centroid.add((LENGTH/2), (LENGTH/2), (LENGTH/2));

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

    private void setVoxNeighbors() {

    }
}
