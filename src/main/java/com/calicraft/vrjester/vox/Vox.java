package com.calicraft.vrjester.vox;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.gesture.radix.Trace;
import com.calicraft.vrjester.utils.tools.Calcs;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import net.minecraft.world.phys.Vec3;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.calicraft.vrjester.utils.tools.SpawnParticles.createParticles;

public class Vox {
    private final VRDevice vrDevice;
    private final Map<String, Vec3> vertices = new HashMap<>();
    public final JSONObject config = new Config().readConfig();
    private final boolean isDiamond;
    private int[] id, previousId;
    private String name, movementDirection = "idle";
    private Trace trace;
    public Vec3 centroid, faceDirection, offset = new Vec3((0), (0), (0));
    public float side_length = Constants.VOX_LENGTH;

    public Vox(String name, VRDevice vrDevice, Vec3[] centerPose, Vec3 faceDirection, boolean isDiamond) {
        // Override defaults
        if (config.has("VOX_LENGTH")) {
            float configVoxLength = Float.parseFloat(config.getString("VOX_LENGTH"));
            if (configVoxLength != side_length)
                side_length = configVoxLength;
        }

        this.setId(new int[]{0, 0, 0}); // Initialize Vox Id
        this.previousId = id.clone(); // Initialize soon to be previous Id
        this.name = name; // Initialize name of Vox
        this.vrDevice = vrDevice; // Initialize VRDevice name
        this.faceDirection = faceDirection; // Initialize facing angle of user
        this.trace = new Trace(Arrays.toString(id), vrDevice, centerPose, faceDirection);
        this.isDiamond = isDiamond;
        // Initialize Vertices of Vox
        this.updateVoxPosition(centerPose[0], false);
    }

    public boolean inProximity(Vec3 point) {
        // TODO - Check if VRDevice position has been within another VRDevices Vox
        boolean ret = false;

        return ret;
    }

    public boolean hasPoint(Vec3 point) { // Check if point is within Vox
        boolean ret = false;
        Vec3 p1 = vertices.get("p1"); Vec3 p2 = vertices.get("p2");
        Vec3 p3 = vertices.get("p3"); Vec3 p5 = vertices.get("p5");
        Vec3 dirX = p2.subtract(p1); Vec3 dirY = p3.subtract(p1); Vec3 dirZ = p5.subtract(p1);
        double lengthX = Calcs.getMagnitude3D(dirX);
        double lengthY = Calcs.getMagnitude3D(dirY);
        double lengthZ = Calcs.getMagnitude3D(dirZ);
        Vec3 localX = dirX.normalize(); Vec3 localY = dirY.normalize(); Vec3 localZ = dirZ.normalize();
        Vec3 V = point.subtract(centroid);
        double projectionX = Math.abs(V.dot(localX) * 2);
        double projectionY = Math.abs(V.dot(localY) * 2);
        double projectionZ = Math.abs(V.dot(localZ) * 2);
        if (projectionX <= lengthX && projectionY <= lengthY && projectionZ <= lengthZ)
            ret = true;
//        Vec3 d1 = vertices.get("d1");
//        Vec3 d2 = vertices.get("d2");
//        if (point.x >= d1.x && point.y >= d1.y && point.z >= d1.z)
//            if (point.x <= d2.x && point.y <= d2.y && point.z <= d2.z)
//                ret = true;
        return ret;
    }

    public boolean hasDiamondInRough(Vec3 point) { // Check if point is within Vox rotated 45 degrees
        boolean ret = false;
        Vec3 d1 = vertices.get("d1"); Vec3 d2 = vertices.get("d2");
        double dx = Math.abs(point.x - centroid.x);
        double dz = Math.abs(point.z - centroid.z);
        double diagonal_width = side_length * Math.sqrt(2);
        double d = dx / diagonal_width + dz / diagonal_width;
        if (d <= 0.5)
            if (point.y >= d1.y && point.y <= d2.y)
                ret = true;
        return ret;
    }

    private int[] getVoxNeighbor(Vec3 point) { // Get new Vox Id and set traced movement direction based on which side the point withdrew from the Vox
        int[] ret = this.getId().clone();
        Vec3 d1 = vertices.get("d1"); Vec3 d2 = vertices.get("d2");
        if (point.y < d1.y) { // Down
            ret[1]--; movementDirection = "down";
        }
        if (point.y > d2.y) { // Up
            ret[1]++; movementDirection = "up";
        }
        if (point.x < d1.x)
            ret[0]--;
        if (point.x > d2.x)
            ret[0]++;
        if (point.z < d1.z)
            ret[2]--;
        if (point.z > d2.z)
            ret[2]++;
        return ret;
    }

    public void generateVox(Vec3[] pose) { // When VRDevice is outside current Vox, new Vox is generated at neighboring position and returns the Trace data
        if (!this.hasPoint(pose[0])) { // Check if point is outside of current Vox
            int[] newVoxId = this.getVoxNeighbor(pose[0]);
            double newX = side_length * (newVoxId[0] - id[0]);
            double newY = side_length * (newVoxId[1] - id[1]);
            double newZ = side_length * (newVoxId[2] - id[2]);
            Vec3 newPointDiff = new Vec3(newX, newY, newZ);
            // TODO - Try updating vox position immediately after we exited from previous vox
            updateVoxPosition(pose[0], false);
            setId(newVoxId);
            trace.setMovement(movementDirection);
            movementDirection = "idle";
        } else {
            trace.addPose(pose); // Constantly update the current Trace
        }
    }

    public void manifestVox(Vec3 point, Vec3 delta) { // When VRDevice is outside current Vox, new Vox is visualized at neighboring position
        Vec3 newPointDiff = new Vec3((0), (0), (0));
        if (!this.hasPoint(point)) { // Check if point is outside of current Vox
            int[] newVoxId = this.getVoxNeighbor(point);
            double newX = side_length * (newVoxId[0] - this.id[0]);
            double newY = side_length * (newVoxId[1] - this.id[1]);
            double newZ = side_length * (newVoxId[2] - this.id[2]);
            newPointDiff = new Vec3(newX, newY, newZ);
            this.updateVoxPosition(point, false);
            this.setId(newVoxId);
        }
        this.displayVox();
    }

    public void updateVoxPosition(Vec3 dif, boolean useDif) { // Update Vox position values based on delta movement
        if (dif.x == 0 && dif.y == 0 && dif.z == 0)
            return;

        // Center of Vox
        if (useDif)
            centroid = centroid.add(dif);
        else
            centroid = dif;

        // Diagonals
        vertices.put("d1", centroid.subtract((side_length /2), (side_length /2), (side_length /2)));
        vertices.put("d2", centroid.add((side_length /2), (side_length /2), (side_length /2)));

        // Bottom square plane
        vertices.put("p1", vertices.get("d1"));
        vertices.put("p2", centroid.add((side_length /2), -(side_length /2), -(side_length /2)));
        vertices.put("p3", centroid.add(-(side_length /2), -(side_length /2), (side_length /2)));
        vertices.put("p4", centroid.add((side_length /2), -(side_length /2), (side_length /2)));

        // Top square plane
        vertices.put("p5", centroid.add(-(side_length /2), (side_length /2), -(side_length /2)));
        vertices.put("p6", centroid.add((side_length /2), (side_length /2), -(side_length /2)));
        vertices.put("p7", centroid.add(-(side_length /2), (side_length /2), (side_length /2)));
        vertices.put("p8", vertices.get("d2"));

        // Rotate Vox to form diamond
        if (isDiamond)
            this.rotateVoxAround((45.0F));
    }

    public void rotateVoxAround(float degrees){
        for (String i: vertices.keySet()) {
            Vec3 pt = vertices.get(i);
            float rads = (float) Math.toRadians(degrees);
            if (rads != 0)
                vertices.put(i, new Vec3(
                        (Math.cos(rads) * (pt.x - centroid.x) - Math.sin(rads) * (pt.z - centroid.z) + centroid.x),
                        pt.y,
                        (Math.sin(rads) * (pt.x - centroid.x) + Math.cos(rads) * (pt.z - centroid.z) + centroid.z)
                ));
        }
    }

    public int[] getId() {
        return id;
    }

    public void setId(int[] id) {
         this.id = id;
    }

    public int[] getPreviousId() {
        return previousId;
    }

    public void setPreviousId(int[] previousId) {
        this.previousId = previousId;
    }

    public String getName() {
        return name;
    }

    public VRDevice getVrDevice() {
        return vrDevice;
    }

    public Vec3 getOffset() {
        return offset;
    }

    public Trace getTrace() {
        return trace;
    }

    public Trace beginTrace(Vec3[] pose) { // Begin with a new Trace object
        trace = new Trace(Arrays.toString(id), vrDevice, pose, faceDirection);
        return trace;
    }

    private void displayVox() {
        createParticles(vertices.get("p1"));
        createParticles(vertices.get("p2"));
        createParticles(vertices.get("p3"));
        createParticles(vertices.get("p4"));
        createParticles(vertices.get("p5"));
        createParticles(vertices.get("p6"));
        createParticles(vertices.get("p7"));
        createParticles(vertices.get("p8"));
    }
}
