package com.calicraft.vrjester.api;

import com.calicraft.vrjester.utils.vrdata.VRDataState;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class VRPlayerEvent extends PlayerEvent {
    // This class packages the VRData of the player

    private final VRDataState vrDataRoomPre;
    private final VRDataState vrDataWorldPre;

    public VRPlayerEvent(Player player, VRDataState vrDataRoomPre, VRDataState vrDataWorldPre) {
        super(player);
        this.vrDataRoomPre = vrDataRoomPre;
        this.vrDataWorldPre = vrDataWorldPre;
    }

    public VRDataState getVrDataRoomPre() {
        return vrDataRoomPre;
    }

    public VRDataState getVrDataWorldPre() {
        return vrDataWorldPre;
    }
}
