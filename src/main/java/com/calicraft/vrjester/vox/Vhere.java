package com.calicraft.vrjester.vox;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.gesture.GestureTrace;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class Vhere {
    // Class that represents a virtual sphere

    private final VRDevice vrDevice;
    private final Map<String, Vec3> vertices = new HashMap<>();
    public final Config config;
    private int id, previousId;
    private String movementDirection = "idle";
    private GestureTrace gestureTrace;
    public Vec3 centroid, faceDirection;
    public float sphereRadius = Constants.VIRTUAL_SPHERE_RADIUS;

    public Vhere(VRDevice vrDevice, Vec3[] centroidPose, String configPath) {
        config = Config.readConfig(configPath);
        if (config.VIRTUAL_SPHERE_RADIUS != sphereRadius) // Override defaults
            sphereRadius = config.VIRTUAL_SPHERE_RADIUS;

        this.setId(0); // Initialize Vhere Id
        this.previousId = id; // Initialize soon to be previous ID
        this.vrDevice = vrDevice; // Initialize VRDevice name
        this.faceDirection = centroidPose[1]; // Initialize facing angle of user
        this.gestureTrace = new GestureTrace(Integer.toString(id), vrDevice, centroidPose, faceDirection);
        // Initialize Center of Vhere
        this.updateVherePosition(centroidPose[0]);
    }

    // Check if point is within Vhere
    public boolean hasPoint(Vec3 point) {
        // Let the sphere's centre coordinates be (cx, cy, cz) and its radius be r,
        // then point (x, y, z) is in the sphere if sqrt( (x−cx)^2 +  (y−cy)^2 + (z−cz)^2 ) <= r.
        double xcx = Math.pow(point.x - centroid.x, 2);
        double ycy = Math.pow(point.y - centroid.y, 2);
        double zcz = Math.pow(point.z - centroid.z, 2);
        double radial_dist = Math.sqrt(xcx + ycy + zcz);
        return radial_dist <= sphereRadius;
    }

    // Checks if VRDevice is in this Vhere
    public void updateProximity(VRDataState vrDataRoomPre, VRDevice vrDevice) {
        Vec3 pos = VRDataState.getVRDevicePose(vrDataRoomPre, vrDevice, 0);
        if (hasPoint(pos)) {
            Map<String, Integer> devicesInProximity = gestureTrace.getDevicesInProximity();
            gestureTrace.updateDeviceInProximity(vrDevice.name(), devicesInProximity.getOrDefault(vrDevice.name(), 0));
        }
    }

    // When VRDevice is outside current Vhere, new Vhere is generated at neighboring position and returns the Trace data
    public Vec3[] generateVhere(VRDataState vrDataRoomPre) {
        Vec3[] pose = new Vec3[2];
        for (int i = 0; i < VRDevice.values().length-1; i++) { // Check which VRDevice this Vhere identifies with
            if (this.getVrDevice().equals(VRDevice.values()[i])) // Assign the current position of the identified VRDevice
                pose = VRDataState.getVRDevicePose(vrDataRoomPre, this.getVrDevice());
            else // Update the devicesInProximity for the other VRDevices (if they're within proximity)
                updateProximity(vrDataRoomPre, VRDevice.values()[i]);
        }
        if (!this.hasPoint(pose[0])) { // Check if point is outside of current Vhere
            updateVherePosition(pose[0]);
            setId(this.getId() + 1);
            gestureTrace.setMovement(movementDirection);
            movementDirection = "idle";
        } else {
            gestureTrace.addPose(pose); // Constantly update the current Trace
        }
        return pose;
    }

    // Update Vhere center
    public void updateVherePosition(Vec3 dif) {
        centroid = dif;
    }

    public GestureTrace getTrace() {
        return gestureTrace;
    }

    // Begin with a new Trace object
    public GestureTrace beginTrace(Vec3[] pose) {
        gestureTrace = new GestureTrace(Integer.toString(id), vrDevice, pose, faceDirection);
        return gestureTrace;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
         this.id = id;
    }

    public int getPreviousId() {
        return previousId;
    }

    public void setPreviousId(int previousId) {
        this.previousId = previousId;
    }

    public VRDevice getVrDevice() {
        return vrDevice;
    }

}
