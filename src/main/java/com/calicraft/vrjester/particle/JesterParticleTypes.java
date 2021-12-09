//package com.calicraft.vrjester.particle;
//
//import net.minecraft.particles.IParticleData;
//import net.minecraft.particles.ParticleType;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.RegistryObject;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.registries.DeferredRegister;
//import net.minecraftforge.registries.ForgeRegistries;
//
//import java.util.function.Supplier;
//
//import static com.calicraft.vrjester.VrJesterApi.MOD_ID;
//import static com.calicraft.vrjester.VrJesterApi.getMCI;
//
//@Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
//public class JesterParticleTypes {
//    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MOD_ID);
//    public static final RegistryObject<ParticleType<CustomParticleType.Data>> FIRE = registerParticle("fire", () -> new CustomParticleType(true));
//
//    private static <T extends IParticleData> RegistryObject<ParticleType<T>> registerParticle(String id, Supplier<? extends ParticleType<T>> particle) {
//        return PARTICLES.register(id, particle);
//    }
//
//    @SubscribeEvent
//    @OnlyIn(Dist.CLIENT)
//    public static void onParticleRegistry(final ParticleFactoryRegisterEvent particlRegistryEvent) {
//        getMCI().particleEngine.register(FIRE.get(), JesterParticle.Factory);
//    }
//}
