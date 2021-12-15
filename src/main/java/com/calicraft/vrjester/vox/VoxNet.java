package com.calicraft.vrjester.vox;

import com.calicraft.vrjester.utils.tools.Constants;
import net.minecraft.util.math.vector.Vector3d;

import java.util.HashMap;

public class VoxNet {
    public Vox ventroid;
    public HashMap<String, Vox> voxGrid;
    public final float LENGTH = Constants.VOX_LENGTH;
    public final int LAYERS = (int) Math.pow(LENGTH, (1/3D));

    public VoxNet (Vector3d centroid) {
        ventroid = new Vox(centroid);

        this.updateVoxNet(centroid);
    }

    public void updateVoxNet(Vector3d position) {

    }

    public HashMap<String, Vox> get() {
        return voxGrid;
    }
}
