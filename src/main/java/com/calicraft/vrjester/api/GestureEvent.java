package com.calicraft.vrjester.api;

import com.calicraft.vrjester.gesture.Gesture;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;

public class GestureEvent extends VRPlayerEvent {
    // This class packages gestures that were recognized along with the attributes that come with it

    private final String gestureName;
    private final Gesture gesture;

    public GestureEvent(PlayerEntity player, HashMap<String, String> gestureCtx, Gesture gesture, VRDataState vrDataRoomPre, VRDataState vrDataWorldPre) {
        super(player, vrDataRoomPre, vrDataWorldPre);
        this.gestureName = gestureCtx.get("gestureName");
        this.gesture = gesture;
    }

    public String getGestureName() {
        return gestureName;
    }

    public Gesture getGesture() {
        return gesture;
    }
}
