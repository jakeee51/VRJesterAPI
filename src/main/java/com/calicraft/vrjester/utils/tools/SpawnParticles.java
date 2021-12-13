package com.calicraft.vrjester.utils.tools;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Random;

import static com.calicraft.vrjester.VrJesterApi.getMCI;

public class SpawnParticles {

    public static void fireball(BasicParticleType type, Vector3d[] pose) {
        Random rand;
        double motionX, motionY, motionZ;
        ClientPlayerEntity player = getMCI().player;
        assert player != null;
        if (pose == null) {
            pose = new Vector3d[]{player.position(), player.getLookAngle()};
        }
        pose[1] = pose[1].scale(1).add(0, player.getEyeHeight()-.3, 0);
        Vector3d newPos = pose[1].add(pose[0]);
        if (player.getCommandSenderWorld().isClientSide()) {
            ClientWorld clientWorld = (ClientWorld) player.getCommandSenderWorld();
            for (int i = 0; i < 20; i++) {
                rand = new Random();
                motionX = rand.nextGaussian() * 0.005D;
                motionY = rand.nextGaussian() * 0.005D;
                motionZ = rand.nextGaussian() * 0.005D;
                clientWorld.addParticle(type,
                        newPos.x, newPos.y, newPos.z,
                        motionX, motionY, motionZ);
            }
        }
    }
}
