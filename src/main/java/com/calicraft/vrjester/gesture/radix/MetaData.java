package com.calicraft.vrjester.gesture.radix;

import com.calicraft.vrjester.utils.tools.Calcs;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Map;


class MetaData {
    // Class that handles representing and checking metadata of a GestureComponent

    long elapsedTime;
    double speed;
    Vector3d direction;
    Map<String, Integer> devicesInProximity;

    protected MetaData(long elapsedTime, double speed,
                       Vector3d direction, Map<String, Integer> devicesInProximity) {
        this.elapsedTime = elapsedTime;
        this.speed = speed;
        this.direction = direction;
        this.devicesInProximity = devicesInProximity;
    }

    protected boolean isClosestFit(long maxTime, double maxSpeed, double minDegree, Vector3d gestureDirection) {
        return closestTime(maxTime) && closestSpeed(maxSpeed) && closestDirection(minDegree, gestureDirection);
    }

    protected boolean closestTime(long maxTime) {
        return elapsedTime >= maxTime;
    }

    protected boolean closestSpeed(double maxSpeed) {
        return speed >= maxSpeed;
    }

    protected boolean closestDirection(double minDegree, Vector3d gestureDirection) {
        boolean ret;
        double degree = Calcs.getAngle3D(direction, gestureDirection);
        if (Double.isNaN(degree))
            ret = minDegree == 180;
        else
            ret = degree < minDegree;
        return ret;
    }
}
