package com.calicraft.vrjester.config;

import com.calicraft.vrjester.utils.vrdata.VRDataState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;

import static com.calicraft.vrjester.utils.tools.SpawnParticles.moveParticles;

public class Test {

    private static final SimpleParticleType[] particleTypes = new SimpleParticleType[]{ParticleTypes.FLAME,
            ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.DRAGON_BREATH, ParticleTypes.CLOUD, ParticleTypes.BUBBLE_POP,
            ParticleTypes.FALLING_WATER};

    public Test() {}

    public void trigger(String gesture, VRDataState vrDataWorldPre, Config config) {
        Config.GestureContext gestureCtx = config.GESTURES.get(gesture);
        Vec3 avgDir = vrDataWorldPre.getRc()[1].add(vrDataWorldPre.getHmd()[1]).multiply((.5), (.5), (.5));
        if (gestureCtx.rcParticle > -1 && gestureCtx.rcParticle < particleTypes.length)
            moveParticles(particleTypes[gestureCtx.rcParticle],
                    vrDataWorldPre.getRc()[0],
                    avgDir,
                    gestureCtx.velocity
            );
        if (gestureCtx.lcParticle > -1 && gestureCtx.lcParticle < particleTypes.length)
            moveParticles(particleTypes[gestureCtx.lcParticle],
                    vrDataWorldPre.getLc()[0],
                    avgDir,
                    gestureCtx.velocity
            );
    }
}
