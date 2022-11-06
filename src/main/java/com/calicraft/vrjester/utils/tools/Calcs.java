package com.calicraft.vrjester.utils.tools;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.tracker.Tracker;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import net.minecraft.util.math.vector.Vector3d;
import org.vivecraft.utils.math.Vector3;

import java.util.ArrayList;

public class Calcs {
    public static double getMagnitude3D(Vector3d v) {
        return Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2) + Math.pow(v.z, 2));
    }

    public static double getMagnitude2D(Vector3d v) {
        return Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.z, 2));
    }

    public static double getAngle2D(Vector3d v1, Vector3d v2) {
        double dotProduct = v1.multiply((1), (0), (1)).dot(v2.multiply((1), (0), (1)));
        double radians = dotProduct / (getMagnitude2D(v1) * getMagnitude2D(v2));
        return Math.toDegrees(Math.acos(radians));
    }

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

    public static double getDiff(double slope1, double slope2) {
        // Get percent difference between two slopes
        double ret = Math.abs(slope1 - slope2);
        return ret / ((slope1 + slope2) / 2);
    }

    public static double getSlope(Vector3d p1, Vector3d p2) {
        // Get slope of two 3D points
        double xyd = Math.pow(Math.abs(p1.x - p2.x), 2) + Math.pow(Math.abs(p1.y - p2.y), 2);
        double run = Math.sqrt(xyd);
        double rise = Math.abs(p1.z - p2.z);
        double ret = 0;
        if (run != 0)
            ret = rise / run;
        return ret;
    }

    public static Vector3d getHeadPivot(VRDataState vrDataState) {
        Vector3d eye = vrDataState.getHmd()[0];
        Vector3 v3 = VrJesterApi.TRACKER.getVRDataWorldPre().hmd.getMatrix().transform(new Vector3(0,-.1f, .1f));
        return (new Vector3d(v3.getX()+eye.x, v3.getY()+eye.y, v3.getZ()+eye.z));
    }

    public static void rotateOriginAround(float degrees, Vector3d o){
        Vector3d pt = Tracker.getOrigin(VrJesterApi.TRACKER.getVRPlayer().toString());

        float rads = (float) Math.toRadians(degrees); // reverse rotate
        if(rads!=0)
            VrJesterApi.TRACKER.getVRPlayer().setRoomOrigin(
                    Math.cos(rads) * (pt.x-o.x) - Math.sin(rads) * (pt.z-o.z) + o.x,
                    pt.y,
                    Math.sin(rads) * (pt.x-o.x) + Math.cos(rads) * (pt.z-o.z) + o.z
                    ,false);
    }
}
