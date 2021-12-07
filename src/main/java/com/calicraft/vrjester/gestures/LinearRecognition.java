package com.calicraft.vrjester.gestures;

import com.calicraft.vrjester.tracker.PositionTracker;
import net.minecraft.util.math.vector.Vector3d;
import org.vivecraft.api.VRData;

import java.lang.Math;
import java.util.ArrayList;

public class LinearRecognition {
    // Class for recognizing linear gestures

    public boolean recognized = false;
    public double velocity; // (total distance traveled / elapsed time)
    public Vector3d direction; // direction of last point

    public LinearRecognition(VRData.VRDevicePose[] device_data, float tolerance, long elapsed_time) {
        // Collect position & direction data
        Vector3d[] pos = new Vector3d[device_data.length];
        Vector3d[] dir = new Vector3d[device_data.length];
        for (int i = 0; i < device_data.length; i++) {
            pos[i] = PositionTracker.getPosition(device_data[i]);
            dir[i] = PositionTracker.getDirection(device_data[i]);
        }
        if (device_data.length > 0)
            direction = dir[device_data.length - 1]; double total_distance = 0;
        // Initialize valid variables
        ArrayList<Vector3d> valid_vectors = new ArrayList<>();
        Vector3d valid = pos[0]; double valid_slope = 0;
        valid_vectors.add(valid);
        // Populate valid values
        for (int i = 1; i < pos.length - 1; i++) {
            Vector3d vector = pos[i]; // Current 3D point in iteration
            total_distance += vector.distanceTo(pos[i-1]);
            double slope = getSlope(valid, vector);
            if (valid_slope == 0) {
                if (slope != 0) {
                    valid_slope = slope; // Get 1st valid slope
                    valid_vectors.add(vector);
                }
                continue;
            }
            // Get % difference between valid slope and slope of current point from the og point
            double percent_diff = getDiff(valid_slope, slope);
//            System.out.println("PERCENT DIFF: " + percent_diff);
            if (percent_diff < tolerance) { // Compare against tolerance ratio
//                System.out.println("WITHIN TOLERANCE: " + i);
                valid_vectors.add(vector); // Append to new dataset of valid points
            } else { // Break out if outlier
                break;
            }
        }
        velocity = total_distance / elapsed_time;
        System.out.println("VELOCITY: " + velocity);
        if (isCollinear(valid_vectors))
            recognized = true;
    }

    public boolean isRecognized() {
        return recognized;
    }

    private static boolean isCollinear(ArrayList<Vector3d> vectors) {
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
