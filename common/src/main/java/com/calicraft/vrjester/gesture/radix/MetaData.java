package com.calicraft.vrjester.gesture.radix;

import com.calicraft.vrjester.utils.tools.Calcs;
import com.calicraft.vrjester.utils.tools.Vec3;

import java.util.Map;


class MetaData {
    // Class that handles representing and checking metadata of a GestureComponent

    long elapsedTime;
    double speed;
    Vec3 direction;
    Map<String, Integer> devicesInProximity;

    protected MetaData(long elapsedTime, double speed,
                       Vec3 direction, Map<String, Integer> devicesInProximity) {
        this.elapsedTime = elapsedTime;
        this.speed = speed;
        this.direction = direction;
        this.devicesInProximity = devicesInProximity;
    }

    protected boolean isClosestFit(long maxTime, double maxSpeed, double minDegree, Vec3 gestureDirection) {
        return closestTime(maxTime) && closestSpeed(maxSpeed) && closestDirection(minDegree, gestureDirection);
    }

    protected boolean closestTime(long maxTime) {
        return elapsedTime >= maxTime;
    }

    protected boolean closestSpeed(double maxSpeed) {
        return speed >= maxSpeed;
    }

    protected boolean closestDirection(double minDegree, Vec3 gestureDirection) {
        boolean ret;
        double degree = Calcs.getAngle3D(direction, gestureDirection);
        if (Double.isNaN(degree))
            ret = minDegree == 180;
        else
            ret = degree < minDegree;
        return ret;
    }
}
