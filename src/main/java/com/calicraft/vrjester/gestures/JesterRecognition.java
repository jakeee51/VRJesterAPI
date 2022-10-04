package com.calicraft.vrjester.gestures;

import com.calicraft.vrjester.utils.vrdata.VRDataState;
import com.calicraft.vrjester.utils.vrdata.VRDevice;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.calicraft.vrjester.VrJesterApi.getMCI;

public class JesterRecognition {
    // Class for handling gesture recognition phase

    public VRDataState[] data;
    public int total_points;
    public long elapsed_time;

    public JesterRecognition (VRDataState[] data, long elapsed_time) {
        this.data = data;
        this.elapsed_time = elapsed_time;
        total_points = data.length;
    }
    public JesterRecognition (@NotNull List<VRDataState> data, long elapsed_time) {
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

    public boolean isLinearGesture(VRDevice device) {
        boolean ret = false;
        if (total_points < 0)
            return false;
        ClientPlayerEntity player = getMCI().player;
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

    private Vector3d[][] getDeviceData(VRDevice device) {
        Vector3d[][] device_data = new Vector3d[this.total_points][2];
        for (int i = 0; i < this.total_points; i++) {
            switch (device) {
                case HMD:
                    device_data[i] = data[i].getHmd(); break;
                case RC:
                    device_data[i] = data[i].getRc(); break;
                case LC:
                    device_data[i] = data[i].getLc(); break;
                case C2:
                    device_data[i] = data[i].getC2(); break;
            }
        }
        return device_data;
    }
}
