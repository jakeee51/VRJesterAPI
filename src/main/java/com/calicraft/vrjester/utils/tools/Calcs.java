package com.calicraft.vrjester.utils.tools;

import com.calicraft.vrjester.VrJesterApi;
import com.calicraft.vrjester.tracker.Tracker;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import net.minecraft.util.math.vector.Vector3d;
import org.vivecraft.utils.math.Vector3;

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
