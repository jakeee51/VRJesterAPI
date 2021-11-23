package com.calicraft.vrjester;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vivecraft.api.VRData;
import org.vivecraft.gameplay.VRPlayer;
import org.vivecraft.utils.math.Matrix4f;

import java.lang.reflect.*;

//import static org.vivecraft.gameplay.screenhandlers.GuiHandler.mc;

public class PositionTracker {
    private static final Logger LOGGER = LogManager.getLogger();
    public static Vector3d RC_POS, LC_POS;
    public static Vector3d getVRPlayer() {
        try {
//            mc.vrPlayer.vrdata_room_pre.c0
            Class<?> mc = Class.forName("net.minecraft.client.Minecraft");
            System.out.println("HERE LOADED VIVECRAFT");
            Field vrp = mc.getDeclaredField("vrPlayer");
            System.out.println("HERE LOADED vrPlayer");
            VRPlayer vrPlayer = (VRPlayer) vrp.get(Minecraft.getInstance());
            System.out.println("HERE vrPlayer");
            System.out.println(vrPlayer);

            return new Vector3d(0,0,0);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            LOGGER.error("Failed to load Vivecraft class!");
        } catch ( NoSuchFieldException e) {
            LOGGER.error("Failed to get Vivecraft field");
        } catch (IllegalAccessException e) {
            LOGGER.error("Failed to access Vivecraft object");
        }

        return new Vector3d(0,0,0);
    }
    public static Vector3d getRC() { // Get Right Controller Position
        return new Vector3d(0,0,0);
    }
    public static Vector3d getLC() { // Get Left Controller Position
        try {
            Class<?> vrData = Class.forName("org.vivecraft.api.VRData");
            System.out.println("HERE VRData");
            Class<?>[] params = new Class[4];
            params[0] = Vector3d.class;
            params[1] = float.class;
            params[2] = float.class;
            params[3] = float.class;
            Constructor<?> vrData_CT = vrData.getConstructor(params);
            System.out.println("HERE Constructor 1");
            Object[] args = new Object[4];
            args[0] = new Vector3d(0,0,0);
            args[1] = 0;
            args[2] = 0;
            args[3] = 0;
            Object vrData_OBJ = vrData_CT.newInstance(args);
            System.out.println(vrData_OBJ);
            System.out.println("HERE Object 1");

            Class<?>[] vrClasses = vrData.getDeclaredClasses();
            System.out.println(vrClasses[0]);
            Class<?> vrDevicePose = vrClasses[0];
            System.out.println("HERE VRDevicePose");

            Class<?>[] partypes = new Class[5];
            partypes[0] = VRData.class;
            partypes[1] = VRData.class;
            partypes[2] = Matrix4f.class;
            partypes[3] = Vector3d.class;
            partypes[4] = Vector3d.class;
            Constructor<?> vrDevicePost_CT =  vrDevicePose.getConstructor(partypes);
            vrDevicePost_CT.setAccessible(true);
            System.out.println("HERE Constructor 2");
            Object[] arglist = new Object[5];
            arglist[0] = vrData_OBJ;
            arglist[1] = vrData_OBJ;
            arglist[2] = new Matrix4f();
            arglist[3] = new Vector3d(0, 0, 0);
            arglist[4] = new Vector3d(0, 0, 0);
            Object vrDevicePose_OBJ = vrDevicePost_CT.newInstance(arglist);
            System.out.println("HERE Object 2");
            Method getPos = vrDevicePose.getMethod("getPosition");
            getPos.setAccessible(true);
            System.out.println("HERE getPosition");

            return LC_POS;
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            LOGGER.error("Failed to load Vivecraft class!");
        } catch (NoSuchMethodException e) {
            LOGGER.error("Failed to get Vivecraft method!");
        } catch (InstantiationException e) {
            LOGGER.error("Failed to instantiate Vivecraft class!");
        } catch (InvocationTargetException e) {
            LOGGER.error("Failed to invoke Vivecraft getPos!");
        } catch (IllegalAccessException e) {
            LOGGER.error("Failed to access Vivecraft attribute!");
        }
        return new Vector3d(0,0,0);
    }
}

