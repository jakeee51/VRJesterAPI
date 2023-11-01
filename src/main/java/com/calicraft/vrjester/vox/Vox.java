// Deprecated...
//package com.calicraft.vrjester.vox;
//
//import com.calicraft.vrjester.config.Config;
//import com.calicraft.vrjester.config.Constants;
//import com.calicraft.vrjester.gesture.GestureTrace;
//import com.calicraft.vrjester.utils.tools.Calcs;
//import com.calicraft.vrjester.utils.vrdata.VRDataState;
//import com.calicraft.vrjester.utils.vrdata.VRDevice;
//import net.minecraft.world.phys.Vec3;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//
//import static com.calicraft.vrjester.utils.tools.SpawnParticles.createParticles;
//
//public class Vox {
//    // Class that represents a virtual box
//
//    private final VRDevice vrDevice;
//    private final Map<String, Vec3> vertices = new HashMap<>();
//    public final Config config = Config.readConfig();
//    private final boolean isDiamond;
//    private int[] id, previousId;
//    private String movementDirection = "idle";
//    private GestureTrace gestureTrace;
//    public Vec3 centroid, faceDirection;
//    public float voxLength = Constants.VOX_LENGTH;
//
//    public Vox(VRDevice vrDevice, Vec3[] centroidPose, Vec3 faceDirection, boolean isDiamond) {
//        // Override defaults
//        if (config.VOX_LENGTH != voxLength)
//            voxLength = config.VOX_LENGTH;
//
//        this.setId(new int[]{0, 0, 0}); // Initialize Vox Id
//        this.previousId = id.clone(); // Initialize soon to be previous ID
//        this.vrDevice = vrDevice; // Initialize VRDevice name
//        this.faceDirection = faceDirection; // Initialize facing angle of user
//        this.gestureTrace = new GestureTrace(Arrays.toString(id), vrDevice, centroidPose, faceDirection);
//        this.isDiamond = isDiamond;
//        // Initialize Vertices of Vox
//        this.updateVoxPosition(centroidPose[0], false);
//    }
//
//    // Check if point is within Vox
//    public boolean hasPoint(Vec3 point) {
//        boolean ret = false;
//        Vec3 p1 = vertices.get("p1"); Vec3 p2 = vertices.get("p2");
//        Vec3 p3 = vertices.get("p3"); Vec3 p5 = vertices.get("p5");
//        Vec3 dirX = p2.subtract(p1); Vec3 dirY = p3.subtract(p1); Vec3 dirZ = p5.subtract(p1);
//        double lengthX = Calcs.getMagnitude3D(dirX);
//        double lengthY = Calcs.getMagnitude3D(dirY);
//        double lengthZ = Calcs.getMagnitude3D(dirZ);
//        Vec3 localX = dirX.normalize(); Vec3 localY = dirY.normalize(); Vec3 localZ = dirZ.normalize();
//        Vec3 V = point.subtract(centroid);
//        double projectionX = Math.abs(V.dot(localX) * 2);
//        double projectionY = Math.abs(V.dot(localY) * 2);
//        double projectionZ = Math.abs(V.dot(localZ) * 2);
//        if (projectionX <= lengthX && projectionY <= lengthY && projectionZ <= lengthZ)
//            ret = true;
////        Vec3 d1 = vertices.get("d1");
////        Vec3 d2 = vertices.get("d2");
////        if (point.x >= d1.x && point.y >= d1.y && point.z >= d1.z)
////            if (point.x <= d2.x && point.y <= d2.y && point.z <= d2.z)
////                ret = true;
//        return ret;
//    }
//
//    // Checks if VRDevice is in this Vox
//    public void updateProximity(VRDataState vrDataRoomPre, VRDevice vrDevice) {
//        Vec3 pos = VRDataState.getVRDevicePose(vrDataRoomPre, vrDevice, 0);
//        if (hasPoint(pos)) {
//            Map<String, Integer> devicesInProximity = gestureTrace.getDevicesInProximity();
//            gestureTrace.updateDeviceInProximity(vrDevice.name(), devicesInProximity.getOrDefault(vrDevice.name(), 0));
//        }
//    }
//
//    // Get new Vox ID and set traced movement direction based on which side the point withdrew from the Vox
//    private int[] getVoxNeighbor(Vec3 point) {
//        int[] ret = this.getId().clone();
//        Vec3 d1 = vertices.get("d1"); Vec3 d2 = vertices.get("d2");
//        if (point.y < d1.y) { // Down
//            ret[1]--; movementDirection = "down";
//        }
//        if (point.y > d2.y) { // Up
//            ret[1]++; movementDirection = "up";
//        }
//        if (point.x < d1.x)
//            ret[0]--;
//        if (point.x > d2.x)
//            ret[0]++;
//        if (point.z < d1.z)
//            ret[2]--;
//        if (point.z > d2.z)
//            ret[2]++;
//        return ret;
//    }
//
//    // When VRDevice is outside current Vox, new Vox is generated at neighboring position and returns the Trace data
//    public Vec3[] generateVox(VRDataState vrDataRoomPre) {
//        Vec3[] pose = new Vec3[2];
//        for (int i = 0; i < VRDevice.values().length-1; i++) { // Check which VRDevice this Vox identifies with
//            if (this.getVrDevice().equals(VRDevice.values()[i])) // Assign the current position of the identified VRDevice
//                pose = VRDataState.getVRDevicePose(vrDataRoomPre, this.getVrDevice());
//            else // Update the devicesInProximity for the other VRDevices (if they're within proximity)
//                updateProximity(vrDataRoomPre, VRDevice.values()[i]);
//        }
//        if (!this.hasPoint(pose[0])) { // Check if point is outside of current Vox
//            int[] newVoxId = this.getVoxNeighbor(pose[0]);
//            updateVoxPosition(pose[0], false);
//            setId(newVoxId);
//            gestureTrace.setMovement(movementDirection);
//            movementDirection = "idle";
//        } else {
//            gestureTrace.addPose(pose); // Constantly update the current Trace
//        }
//        return pose;
//    }
//
//    // When VRDevice is outside current Vox, new Vox is visualized at neighboring position
//    public void manifestVox(Vec3 point) {
//        if (!this.hasPoint(point)) { // Check if point is outside of current Vox
//            int[] newVoxId = this.getVoxNeighbor(point);
//            this.updateVoxPosition(point, false);
//            this.setId(newVoxId);
//        }
//        this.displayVox();
//    }
//
//    // Update Vox position values based on delta movement
//    public void updateVoxPosition(Vec3 dif, boolean useDif) {
//        // Center of Vox
//        if (useDif)
//            centroid = centroid.add(dif);
//        else
//            centroid = dif;
//
//        // Diagonals
//        vertices.put("d1", centroid.subtract((voxLength /2), (voxLength /2), (voxLength /2)));
//        vertices.put("d2", centroid.add((voxLength /2), (voxLength /2), (voxLength /2)));
//
//        // Bottom square plane
//        vertices.put("p1", vertices.get("d1"));
//        vertices.put("p2", centroid.add((voxLength /2), -(voxLength /2), -(voxLength /2)));
//        vertices.put("p3", centroid.add(-(voxLength /2), -(voxLength /2), (voxLength /2)));
//        vertices.put("p4", centroid.add((voxLength /2), -(voxLength /2), (voxLength /2)));
//
//        // Top square plane
//        vertices.put("p5", centroid.add(-(voxLength /2), (voxLength /2), -(voxLength /2)));
//        vertices.put("p6", centroid.add((voxLength /2), (voxLength /2), -(voxLength /2)));
//        vertices.put("p7", centroid.add(-(voxLength /2), (voxLength /2), (voxLength /2)));
//        vertices.put("p8", vertices.get("d2"));
//        // Rotate Vox to form diamond
//        if (isDiamond)
//            this.rotateVoxAround((45.0F));
//    }
//
//    // Rotate Vox on Y-axis based on the degrees passed
//    public void rotateVoxAround(float degrees){
//        for (String i: vertices.keySet()) {
//            Vec3 pt = vertices.get(i);
//            float rads = (float) Math.toRadians(degrees);
//            if (rads != 0)
//                vertices.put(i, new Vec3(
//                        (Math.cos(rads) * (pt.x - centroid.x) - Math.sin(rads) * (pt.z - centroid.z) + centroid.x),
//                        pt.y,
//                        (Math.sin(rads) * (pt.x - centroid.x) + Math.cos(rads) * (pt.z - centroid.z) + centroid.z)
//                ));
//        }
//    }
//
//    public GestureTrace getTrace() {
//        return gestureTrace;
//    }
//
//    // Begin with a new Trace object
//    public GestureTrace beginTrace(Vec3[] pose) {
//        gestureTrace = new GestureTrace(Arrays.toString(id), vrDevice, pose, faceDirection);
//        return gestureTrace;
//    }
//
//    public int[] getId() {
//        return id;
//    }
//
//    public void setId(int[] id) {
//         this.id = id;
//    }
//
//    public int[] getPreviousId() {
//        return previousId;
//    }
//
//    public void setPreviousId(int[] previousId) {
//        this.previousId = previousId;
//    }
//
//    public VRDevice getVrDevice() {
//        return vrDevice;
//    }
//
//    private void displayVox() {
//        createParticles(vertices.get("p1"));
//        createParticles(vertices.get("p2"));
//        createParticles(vertices.get("p3"));
//        createParticles(vertices.get("p4"));
//        createParticles(vertices.get("p5"));
//        createParticles(vertices.get("p6"));
//        createParticles(vertices.get("p7"));
//        createParticles(vertices.get("p8"));
//    }
//}
