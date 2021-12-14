package com.calicraft.vrjester.vox;

import net.minecraft.util.math.vector.Vector3d;

public class Vox {
    private final Vector3d p1;
    private final Vector3d p2;
    private final Vector3d p3;
    private final Vector3d p4;
    private final Vector3d p5;
    private final Vector3d p6;
    private final Vector3d p7;
    private final Vector3d p8;
    public Vector3d d1, d2, centroid;
    public final float LENGTH = 0.6F;

    public Vox(Vector3d centroid) {
        // Center of 3D cube
        this.centroid = centroid;

        // Diagonals
        d1 = centroid.subtract((LENGTH/2), (LENGTH/2), (LENGTH/2));
        d2 = centroid.add((LENGTH/2), (LENGTH/2), (LENGTH/2));

        // All 8 points of 3D cube
        // Bottom square plane
        p1 = d1;
        p2 = centroid.subtract((0), (LENGTH/2), (0));
        p3 = centroid.subtract((LENGTH/2), (0), (0));
        p4 = centroid.subtract((LENGTH/2), (LENGTH/2), (0));
        // Top square plane
        p5 = centroid.add((0), (0), (LENGTH/2));
        p6 = centroid.add((0), (LENGTH/2), (LENGTH/2));
        p7 = centroid.add((LENGTH/2), (0), (LENGTH/2));
        p8 = d2;
    }

    public boolean hasPoint(Vector3d point) { // Check if point is within Vox
        boolean ret = false;
        if (point.x > d1.x && point.y > d1.y && point.z > d1.z){
            if (point.x < d2.x && point.y < d2.y && point.z < d2.z) {
                ret = true;
            }
        }
        return ret;
    }
}
