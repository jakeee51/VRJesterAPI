package com.calicraft.vrjester.utils.tools;

import net.minecraft.util.math.vector.Vector3d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CalcsTest {

    @Test
    public void getMagnitude2DTest(){
        Vector3d v = new Vector3d(1, 2, 3);
        assertEquals(3.1622776601683795,Calcs.getMagnitude2D(v));
    }
    @Test
    public void getMagnitude3dTest(){
        Vector3d v = new Vector3d(1,1,1);
        assertEquals(1.7320508075688772, Calcs.getMagnitude3D(v));
    }
    @Test
    public void getAngle2DTest(){
        Vector3d v1 = new Vector3d(1,1,1);
        Vector3d v2 = new Vector3d(1,1,1);
        assertEquals(1.2074182697257333E-6, Calcs.getAngle2D(v1, v2));
    }
    @Test
    public void getAngle3DTest(){
        Vector3d v1 = new Vector3d(1,2,1);
        Vector3d v2 = new Vector3d(2,1,2);
        assertEquals(35.26438968275464, Calcs.getAngle3D(v1, v2));
    }
}
