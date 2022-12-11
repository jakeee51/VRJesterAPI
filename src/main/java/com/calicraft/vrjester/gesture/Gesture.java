package com.calicraft.vrjester.gesture;

import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import com.calicraft.vrjester.vox.Vox;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.calicraft.vrjester.VrJesterApi.getMCI;

public class Gesture {
    private final List<Vox> voxList = new ArrayList<>();
    public List<Path> hmdGesture = new ArrayList<>();
    public List<Path> rcGesture = new ArrayList<>();
    public List<Path> lcGesture = new ArrayList<>();

    public Gesture(VRDataState vrDataState) {
        Vec3[] hmdOrigin = vrDataState.getHmd(), rcOrigin = vrDataState.getRc(), lcOrigin = vrDataState.getLc();
        // Class that handles compiling the components of a gesture, populating the Tracer object for each VRDevice
        Vox hmdVox = new Vox(Constants.HMD, VRDevice.HMD, hmdOrigin, hmdOrigin[1], false);
        Vox rcVox = new Vox(Constants.RC, VRDevice.RC, rcOrigin, hmdOrigin[1], true);
        Vox lcVox = new Vox(Constants.LC, VRDevice.LC, lcOrigin, hmdOrigin[1], true);
        voxList.add(hmdVox); voxList.add(rcVox); voxList.add(lcVox);
    }

    public Gesture(List<Path> hmdGesture, List<Path> rcGesture, List<Path> lcGesture) {
        if (hmdGesture != null)
            this.hmdGesture = hmdGesture;
        if (rcGesture != null)
            this.rcGesture = rcGesture;
        if (lcGesture != null)
            this.hmdGesture = lcGesture;

    }

    @Override
    public String toString() {
        return "Gesture:" +
                "\r\n \t hmdGesture: " + hmdGesture +
                "\r\n \t rcGesture: " + rcGesture +
                "\r\n \t lcGesture: " + lcGesture;
    }

    public void track(VRDataState vrDataRoomPre) { // Record the Vox trace of each VRDevice and return the resulting data
        for (Vox vox: voxList) { // Loop through each VRDevice's Vox
            Vec3[] currentPoint = vox.generateVox(vrDataRoomPre);
            int[] currentId = vox.getId();
            if (!Arrays.equals(vox.getPreviousId(), currentId)) { // Append new Vox Trace object
                vox.setPreviousId(currentId.clone());
                Track track = vox.getTrace();
                track.completeTrace(currentPoint);
//                System.out.println("BEFORE: " + vox.getName() + ": " + trace.toString());
                vox.beginTrace(currentPoint);
//                System.out.println("AFTER: " + vox.getName() + ": " + vox.getTrace().toString());
                switch (vox.getVrDevice()) {
                    case HMD -> hmdGesture.add(convertToPath(track));
                    case RC  -> rcGesture.add(convertToPath(track));
                    case LC  -> lcGesture.add(convertToPath(track));
                }
            }
        }
    }

    private Path convertToPath(Track track) {
        return new Path(track.getVrDevice(), track.getMovement(), track.getElapsedTime(), -1,
                track.getSpeed(), -1, track.getDirection(), track.getFaceDirection(),
                track.getDevicesInProximity());
    }

    public static void sendDebugMsg(String msg) {
        LocalPlayer player = getMCI().player;
        Component text = Component.literal(msg);
        assert player != null;
        player.sendSystemMessage(text);
    }
}
