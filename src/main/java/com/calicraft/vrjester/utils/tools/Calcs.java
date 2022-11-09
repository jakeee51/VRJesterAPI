package com.calicraft.vrjester.utils.tools;

import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;

public class Calcs {
    public static double getMagnitude3D(Vec3 v) {
        return Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2) + Math.pow(v.z, 2));
    }

    public static double getMagnitude2D(Vec3 v) {
        return Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.z, 2));
    }

    public static double getAngle2D(Vec3 v1, Vec3 v2) {
        double dotProduct = v1.multiply((1), (0), (1)).dot(v2.multiply((1), (0), (1)));
        double radians = dotProduct / (getMagnitude2D(v1) * getMagnitude2D(v2));
        return Math.toDegrees(Math.acos(radians));
    }

    public static boolean isCollinear(ArrayList<Vec3> vectors) {
        boolean ret = false;
        Vec3 p1 = vectors.get(0);
        Vec3 p2 = vectors.get(vectors.size()-1);
        Vec3 p3 = vectors.get((vectors.size()-1) / 2 );
        double s1 = getSlope(p1, p2);
        double s2 = getSlope(p1, p3);
        double dif = getDiff(s1, s2);
        System.out.println("VALID VECTORS LENGTH: " + vectors.size());
        System.out.println("COLLINEAR DIF: " + dif);
        if(dif < .15)
            ret = true;

        return ret;
    }

    public static double getDiff(double slope1, double slope2) {
        // Get percent difference between two slopes
        double ret = Math.abs(slope1 - slope2);
        return ret / ((slope1 + slope2) / 2);
    }

    public static double getSlope(Vec3 p1, Vec3 p2) {
        // Get slope of two 3D points
        double xyd = Math.pow(Math.abs(p1.x - p2.x), 2) + Math.pow(Math.abs(p1.y - p2.y), 2);
        double run = Math.sqrt(xyd);
        double rise = Math.abs(p1.z - p2.z);
        double ret = 0;
        if (run != 0)
            ret = rise / run;
        return ret;
    }

}
