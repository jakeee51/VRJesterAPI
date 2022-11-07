package com.calicraft.vrjester.utils.tools;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

import static com.calicraft.vrjester.VrJesterApi.getMCI;

public class SpawnParticles {

    public static void createParticles(SimpleParticleType type, Vec3 position) {
        Random rand;
        double motionX, motionY, motionZ;
        LocalPlayer player = getMCI().player;
        assert player != null;
        if (position == null) { // Non-VR
            Vec3[] pose = new Vec3[]{player.position(), player.getLookAngle()};
            pose[1] = pose[1].scale(1).add((0), (player.getEyeHeight() - .5), (0));
            Vec3 newPos = pose[1].add(pose[0]);
            if (player.getCommandSenderWorld().isClientSide()) {
                Level clientWorld = (Level) player.getCommandSenderWorld();
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
        } else { // VR
            Vec3 newPos = position;
            if (player.getCommandSenderWorld().isClientSide()) {
                Level clientWorld = (Level) player.getCommandSenderWorld();
                for (int i = 0; i < 120; i++) {
                    rand = new Random();
                    if (type == ParticleTypes.BUBBLE) {
                        motionX = rand.nextGaussian() * 0.04D;
                        motionY = rand.nextGaussian() * 0.04D;
                        motionZ = rand.nextGaussian() * 0.04D;
                        clientWorld.addParticle(type,
                                newPos.x, newPos.y, newPos.z,
                                motionX, motionY, motionZ);
                    } else {
                        motionX = rand.nextGaussian() * 0.0004D;
                        motionY = rand.nextGaussian() * 0.0004D;
                        motionZ = rand.nextGaussian() * 0.0004D;
                        clientWorld.addParticle(type,
                                newPos.x, newPos.y, newPos.z,
                                motionX, motionY, motionZ);
                    }
                }
            }
        }
    }

    public static void createParticles(Vec3 position) {
        LocalPlayer player = getMCI().player;
        assert player != null;
        if (player.getCommandSenderWorld().isClientSide()) {
            Level clientWorld = (Level) player.getCommandSenderWorld();
            for (int i = 0; i < 5; i++) {
                clientWorld.addParticle(ParticleTypes.BUBBLE,
                        position.x, position.y, position.z,
                        (0.0D), (0.0D), (0.0D));
            }
        }
    }
}
