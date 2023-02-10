package com.calicraft.vrjester.utils.tools;

import com.calicraft.vrjester.config.Config;
import com.calicraft.vrjester.config.Constants;
import com.calicraft.vrjester.utils.vrdata.VRDataState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;

import static com.calicraft.vrjester.utils.tools.SpawnParticles.moveParticles;

public class TestJester {

    private static final SimpleParticleType[] particleTypes = new SimpleParticleType[]{ParticleTypes.FLAME,
            ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.DRAGON_BREATH, ParticleTypes.CLOUD, ParticleTypes.BUBBLE_POP,
            ParticleTypes.FALLING_WATER};

    public TestJester() {}

    public void trigger(HashMap<String, String> gesture, VRDataState vrDataWorldPre, Config config) {
        Config.GestureContext gestureCtx = config.GESTURES.get(gesture.get("gestureName"));
        if (gestureCtx == null)
            gestureCtx = config.new GestureContext(1.0, 0, 0);

        Vec3 avgDir = vrDataWorldPre.getRc()[1].add(vrDataWorldPre.getHmd()[1]).multiply((.5), (.5), (.5));
        if (gestureCtx.rcParticle > -1 && gestureCtx.rcParticle < particleTypes.length && gesture.containsKey(Constants.RC)) {
            moveParticles(particleTypes[gestureCtx.rcParticle],
                    vrDataWorldPre.getRc()[0],
                    avgDir,
                    gestureCtx.velocity
            );
        }
        if (gestureCtx.lcParticle > -1 && gestureCtx.lcParticle < particleTypes.length && gesture.containsKey(Constants.LC)) {
            if (!gesture.containsKey(Constants.RC))
                avgDir = vrDataWorldPre.getLc()[1].add(vrDataWorldPre.getHmd()[1]).multiply((.5), (.5), (.5));
            moveParticles(particleTypes[gestureCtx.lcParticle],
                    vrDataWorldPre.getLc()[0],
                    avgDir,
                    gestureCtx.velocity
            );
        }
    }
}
