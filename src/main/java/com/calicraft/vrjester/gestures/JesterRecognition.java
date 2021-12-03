package com.calicraft.vrjester.gestures;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class JesterRecognition {
    // Class for handling gesture recognition phase

    // TODO - Allow for specified device(s) to be passed here (pos & dir)

    public Vector3d[] data;

    public JesterRecognition (Vector3d[] data) {
        this.data = data;
    }

    public String getGesture() {
        String ret = "";
        boolean res = LinearRecognition.recognize(data, .8f);
        ClientPlayerEntity player = Minecraft.getInstance().player;
        assert player != null;
        if (res) {
            ITextComponent text = new StringTextComponent("Linear gesture recognized!");
            player.sendMessage(text, player.getUUID());
            ret = "LINEAR";
            System.out.println("TRIGGER THE EVENT!");
            System.out.println("YES U VICTORY");
        } else {
            ITextComponent text = new StringTextComponent("Gesture not recognized!");
            player.sendMessage(text, player.getUUID());
            System.out.println("NU U FAILURE");
        }
        return ret;
    }
}
