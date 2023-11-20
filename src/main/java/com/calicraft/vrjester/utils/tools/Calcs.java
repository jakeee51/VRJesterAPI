package com.calicraft.vrjester.utils.tools;

import net.minecraft.util.math.vector.Vector3d;
import java.util.ArrayList;

public class Calcs {
    // Class that holds mathematical functions

    // Get magnitude/length of 2D vector v
    public static double getMagnitude2D(Vector3d v) {
        return Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.z, 2));
    }

    // Get magnitude/length of 3D vector v
    public static double getMagnitude3D(Vector3d v) {
        return Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2) + Math.pow(v.z, 2));
    }

    // Get angle between two 2D vectors and return in ° (degrees)
    public static double getAngle2D(Vector3d v1, Vector3d v2) {
        double dotProduct = v1.multiply((1), (0), (1)).dot(v2.multiply((1), (0), (1)));
        double radians = dotProduct / (getMagnitude2D(v1) * getMagnitude2D(v2));
        return Math.toDegrees(Math.acos(radians));
    }

    // Get angle between two 3D vectors and return in ° (degrees)
    public static double getAngle3D(Vector3d v1, Vector3d v2) {
        double dotProduct = v1.dot(v2);
        double radians = dotProduct / (getMagnitude3D(v1) * getMagnitude3D(v2));
        return Math.toDegrees(Math.acos(radians));
    }

    // Check if list of point vectors are collinear with 0.15% error
    public static boolean isCollinear(ArrayList<Vector3d> vectors) {
        boolean ret = false;
        Vector3d p1 = vectors.get(0);
        Vector3d p2 = vectors.get(vectors.size()-1);
        Vector3d p3 = vectors.get((vectors.size()-1) / 2 );
        double s1 = getSlope(p1, p2);
        double s2 = getSlope(p1, p3);
        double dif = getDiff(s1, s2);
        System.out.println("VALID VECTORS LENGTH: " + vectors.size());
        System.out.println("COLLINEAR DIF: " + dif);
        if(dif < .15)
            ret = true;

        return ret;
    }

    // Get percent difference between two slopes
    public static double getDiff(double slope1, double slope2) {
        double ret = Math.abs(slope1 - slope2);
        return ret / ((slope1 + slope2) / 2);
    }

    // Get slope of two 3D points
    public static double getSlope(Vector3d p1, Vector3d p2) {
        double xyd = Math.pow(Math.abs(p1.x - p2.x), 2) + Math.pow(Math.abs(p1.y - p2.y), 2);
        double run = Math.sqrt(xyd);
        double rise = Math.abs(p1.z - p2.z);
        double ret = 0;
        if (run != 0)
            ret = rise / run;
        return ret;
    }
}
