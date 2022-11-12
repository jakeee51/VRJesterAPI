package com.calicraft.vrjester.utils.tools;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

import static com.calicraft.vrjester.VrJesterApi.getMCI;

public class SpawnParticles {

    // --- VR ---
    // For shooting particles as projectiles at a specified speed
    public static void moveParticles(SimpleParticleType type, Vec3 position, Vec3 direction, double speedMultiplier) {
        Random rand; Vec3 velocity;
        LocalPlayer player = getMCI().player;
        assert player != null;
        if (player.getCommandSenderWorld().isClientSide()) {
            Level clientWorld = player.getCommandSenderWorld();
            for (int i = 0; i < 100; i++) {
                rand = new Random();
                velocity = direction.add((rand.nextGaussian() * 0.005D),
                        (rand.nextGaussian() * 0.005D),
                        (rand.nextGaussian() * 0.005D));
                velocity = velocity.multiply((speedMultiplier), (speedMultiplier), (speedMultiplier));
                clientWorld.addParticle(type,
                        position.x, position.y, position.z,
                        velocity.x, velocity.y, velocity.z);
            }
        }
    }

    // For displaying particles at a specified position
    public static void createParticles(SimpleParticleType type, Vec3 position) {
        double motionX, motionY, motionZ;
        LocalPlayer player = getMCI().player;
        assert player != null;
        if (player.getCommandSenderWorld().isClientSide()) {
            Level clientWorld = (Level) player.getCommandSenderWorld();
            for (int i = 0; i < 100; i++) {
                Random rand = new Random();
                if (type == ParticleTypes.BUBBLE) {
                    motionX = rand.nextGaussian() * 0.04D;
                    motionY = rand.nextGaussian() * 0.04D;
                    motionZ = rand.nextGaussian() * 0.04D;
                    clientWorld.addParticle(type,
                            position.x, position.y, position.z,
                            motionX, motionY, motionZ);
                } else {
                    motionX = rand.nextGaussian() * 0.0004D;
                    motionY = rand.nextGaussian() * 0.0004D;
                    motionZ = rand.nextGaussian() * 0.0004D;
                    clientWorld.addParticle(type,
                            position.x, position.y, position.z,
                            motionX, motionY, motionZ);
                }
            }
        }
    }

    // --- NON-VR ---
    // For shooting particles as projectiles at a specified speed
    public static void moveParticles(SimpleParticleType type, double speed) {
        Random rand;
        LocalPlayer player = getMCI().player;
        assert player != null;
        Vec3[] pose = new Vec3[]{player.position(), player.getLookAngle()};
        pose[1] = pose[1].scale((1)).add((0), (player.getEyeHeight() - .5), (0));
        Vec3 newPos = pose[1].add(pose[0]);
        if (player.getCommandSenderWorld().isClientSide()) {
            Level clientWorld = (Level) player.getCommandSenderWorld();
            Vec3 dir = player.getLookAngle();
            for (int i = 0; i < 20; i++) {
                clientWorld.addParticle(type,
                        newPos.x, newPos.y, newPos.z,
                        (dir.x+speed), (dir.y+speed), (dir.z+speed));
            }
        }
    }

    // For displaying particles at a position player is looking
    public static void createParticles(SimpleParticleType type) {
        double motionX, motionY, motionZ;
        LocalPlayer player = getMCI().player;
        assert player != null;
        Vec3[] pose = new Vec3[]{player.position(), player.getLookAngle()};
        pose[1] = pose[1].scale((1)).add((0), (player.getEyeHeight() - .5), (0));
        Vec3 newPos = pose[1].add(pose[0]);
        if (player.getCommandSenderWorld().isClientSide()) {
            Level clientWorld = (Level) player.getCommandSenderWorld();
            Vec3 dir = player.getLookAngle();
            for (int i = 0; i < 20; i++) {
                Random rand = new Random();
                motionX = rand.nextGaussian() * 0.005D;
                motionY = rand.nextGaussian() * 0.005D;
                motionZ = rand.nextGaussian() * 0.005D;
                clientWorld.addParticle(type,
                        newPos.x, newPos.y, newPos.z,
                        motionX, motionY, motionZ);
            }
        }
    }

    // For displaying Vox
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
