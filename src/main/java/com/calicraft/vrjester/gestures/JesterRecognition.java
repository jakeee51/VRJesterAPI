package com.calicraft.vrjester.gestures;

import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class JesterRecognition {
    // Class for handling gesture recognition phase

    // TODO - Create Context class that reads from config.
    //  It will specify which devices to aggregate to VRDataState,
    //  gestures to be bound to events, and w/e else that needs to
    //  be specified based on mod and user.
    //  Pass ctx on initialization or when calling recognizeGesture

    public VRDataState[] data;
    public int total_points;
    public long elapsed_time;

    public JesterRecognition (VRDataState[] data, long elapsed_time) {
        this.data = data;
        this.elapsed_time = elapsed_time;
        total_points = data.length;
    }
    public JesterRecognition (List<VRDataState> data, long elapsed_time) {
        VRDataState[] vrData = new VRDataState[data.size()];
        this.data =  data.toArray(vrData);
        this.elapsed_time = elapsed_time;
        total_points = data.size();
    }

    private void fireAway() { // Post GestureEvent
        // Specify which gesture was recognized
        // and what it's bound to
    }

    public String recognizeGesture() {
        String ret = null;

        return ret;
    }

    public boolean isLinearGesture(String device) {
        boolean ret = false;
        if (total_points < 0)
            return false;
        ClientPlayerEntity player = Minecraft.getInstance().player;
        assert player != null;
        Vector3d[][] device_data = this.getDeviceData(device);
        try {
            LinearRecognition result = new LinearRecognition(device_data, .8f, elapsed_time);
            if (result.isRecognized()) {
                ITextComponent text = new StringTextComponent("Linear gesture recognized!");
                player.sendMessage(text, player.getUUID());
                ret = true;
                System.out.println("YES U VICTORY");
            } else {
                ITextComponent text = new StringTextComponent("Gesture not recognized!");
                player.sendMessage(text, player.getUUID());
                System.out.println("NU U FAILURE");
            }
        } catch (IndexOutOfBoundsException ignored) {}
        return ret;
    }

    private Vector3d[][] getDeviceData(String device) {
        Vector3d[][] device_data = new Vector3d[4][this.total_points];
        for (int i = 0; i < this.total_points; i++) {
            switch (device) {
                case VRDevice.HMD:
                    device_data[i] = data[i].getHmd(); break;
                case VRDevice.RC:
                    device_data[i] = data[i].getRc(); break;
                case VRDevice.LC:
                    device_data[i] = data[i].getLc(); break;
                case VRDevice.C2:
                    device_data[i] = data[i].getC2(); break;
            }
        }
        return device_data;
    }
}
