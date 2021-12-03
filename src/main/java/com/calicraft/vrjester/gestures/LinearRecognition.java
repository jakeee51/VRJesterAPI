package com.calicraft.vrjester.gestures;

import net.minecraft.util.math.vector.Vector3d;
import java.lang.Math;

public class LinearRecognition {
    public float velocity;
    public Vector3d direction;

    public LinearRecognition () {

    }

    public static boolean recognize(Vector3d[] data, float tolerance) {
        boolean ret = false;
        // Initialize valid variables
        Vector3d[] valid_vectors = new Vector3d[data.length];
        if (data.length == 0)
            return false;
        Vector3d valid = data[0]; double valid_slope = 0;
        valid_vectors[0] = valid;
        for (int i = 1; i < data.length - 1; i++) {
            Vector3d vector = data[i]; // Current 3D point in iteration
            double slope = getSlope(valid, vector);
            if (valid_slope == 0) {
                if (slope != 0)
                    valid_slope = slope; // Get 1st valid slope
                valid_vectors[i] = vector;
                continue;
            }
            // Get % difference between valid slope and slope of current point from the og point
            double percent_diff = getDiff(valid_slope, slope);
            System.out.println("VALID VECTORS LENGTH: " + valid_vectors.length);
            System.out.println("PERCENT DIFF: " + percent_diff);
            if (percent_diff < tolerance) { // Compare against tolerance ratio
                System.out.println("WITHIN TOLERANCE: " + i);
                valid_vectors[i] = vector; // Append to new dataset of valid points
                if (valid_vectors.length >= 6)
                    return true;
            } else {
                return false;
            }
        }
        // TODO - Tighten up linear recognizer by checking valid vectors
        //  to see if they're collinear
        if (valid_vectors.length >= 6)
            ret = true;
        return ret;
    }

    private static double getDiff(double slope1, double slope2) {
        // Get percent difference between two slopes
        double ret = Math.abs(slope1 - slope2);
        return ret / ((slope1 + slope2) / 2);
    }

    private static double getSlope(Vector3d p1, Vector3d p2) {
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
