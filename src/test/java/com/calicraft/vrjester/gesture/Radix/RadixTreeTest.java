package com.calicraft.vrjester.gesture.Radix;

import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.gesture.GestureComponent;
import com.calicraft.vrjester.gesture.radix.RadixTree;
import com.calicraft.vrjester.utils.tools.Vec3;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RadixTreeTest {
    public static RadixTree TreeTest =new RadixTree("vrDevice");
    public static List<GestureComponent> TreeArray = new ArrayList<>();

    @BeforeAll
    public static void setup(){
        Map<String, Integer> HM = new HashMap<>();
        Vec3 v = new Vec3(1,1,1);
        GestureComponent punch = new GestureComponent(Constants.LC, "up", 5, 1.5,v, HM );
        TreeArray.add(punch);
        TreeTest.insert(TreeArray);
    }

    @Test
    public void insertTest(){
        List<GestureComponent> array = new ArrayList<>();
        Map<String, Integer> HM = new HashMap<>();
        Vec3 v = new Vec3(0,1,0);
        GestureComponent punch = new GestureComponent(Constants.LC, "up", 2, 2.5,v, HM );
        array.add(punch);
        TreeTest.insert(array);
        assertEquals(array, TreeTest.search(array));
    }

    @Test
    public void searchTest(){
        List<GestureComponent> array = new ArrayList<>();
        Map<String, Integer> HM = new HashMap<>();
        Vec3 v = new Vec3(1,1,3);
        GestureComponent punch = new GestureComponent(Constants.LC, "up", 6, 3.5, v, HM);
        array.add(punch);
        assertEquals(TreeArray, TreeTest.search(array));

    }
}
