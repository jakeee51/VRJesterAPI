package com.calicraft.vrjester.vox;

import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;

import static com.calicraft.vrjester.utils.tools.Constants.*;

public class VoxNet {
    public Vox ventroid;
    public ArrayList<Vox> voxGrid = new ArrayList<>();
    public final int VOX_TOTAL = VOX_GRID_LENGTH * VOX_GRID_WIDTH * VOX_GRID_HEIGHT;

    public VoxNet (Vector3d centroid) {
        ventroid = new Vox(centroid);
        ventroid.setId(new int[]{0,0,0});
        voxGrid.add(ventroid);

        this.updateVoxNet(centroid);
    }

    private void updateVoxNet(Vector3d center) {
        float half_length = VOX_LENGTH * (VOX_GRID_LENGTH/2F);
        float half_height = VOX_LENGTH * (VOX_GRID_HEIGHT/2F);
        float half_width = VOX_LENGTH * (VOX_GRID_WIDTH/2F);
        int half_pos_x = (int)-(VOX_GRID_LENGTH/2F);
        int half_pos_y = (int)-(VOX_GRID_HEIGHT/2F);
        int half_pos_z = (int)-(VOX_GRID_WIDTH/2F);
        Vector3d newCenter = center.subtract(half_length, half_height, half_width);
        int[] newId = new int[]{half_pos_x, half_pos_y, half_pos_z};
        for (int i = 1; i < VOX_GRID_LENGTH+1; i++) {
            if (newId[1] == VOX_GRID_HEIGHT) {
                newId[1] = half_pos_y;
                newCenter = center.subtract((0), (VOX_LENGTH * VOX_GRID_HEIGHT), (0));
            }
            for (int j = 1; j < VOX_GRID_HEIGHT+1; j++) {
                if (newId[2] == VOX_GRID_WIDTH) {
                    newId[2] = half_pos_z;
                    newCenter = center.subtract((0), (0), (VOX_LENGTH * VOX_GRID_WIDTH));
                }
                for (int k = 1; k < VOX_GRID_WIDTH+1; k++) {
                    createVox(newCenter, newId);
                    newId[2] += 1;
                    newCenter.add((0), (0), VOX_LENGTH);
                }
                newId[1] += 1;
                newCenter.add((0), VOX_LENGTH, (0));
            }
            newId[0] += 1;
            newCenter.add(VOX_LENGTH, (0), (0));
        }
    }

    private void createVox(Vector3d centroid, int[] id) {
        Vox vox = new Vox(centroid);
        vox.setId(id);
        voxGrid.add(vox);
    }

    public ArrayList<Vox> get() {
        return voxGrid;
    }

    private static boolean compareVector3d(Vector3d p1, Vector3d p2) {
        return (p1.x == p2.x && p1.y == p2.y && p1.z == p2.z);
    }
}
