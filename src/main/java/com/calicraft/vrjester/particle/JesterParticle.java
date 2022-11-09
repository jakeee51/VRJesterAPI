//package com.calicraft.vrjester.particle;
//
//import com.mojang.blaze3d.vertex.IVertexBuilder;
//import net.minecraft.client.particle.IParticleFactory;
//import net.minecraft.client.particle.IParticleRenderType;
//import net.minecraft.client.particle.Particle;
//import net.minecraft.client.renderer.ActiveRenderInfo;
//import net.minecraft.world.level.Level;
//import net.minecraft.core.particles.SimpleParticleType;
//
//import javax.annotation.Nullable;
//import java.awt.*;
//
//public class JesterParticle extends Particle {
//
//    public static IParticleFactory<CustomParticleType.Data> Factory;
//
//    public JesterParticle(Level worldIn, Color color, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
//        super(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
//    }
//
//    @Override
//    public void render(IVertexBuilder builder, ActiveRenderInfo renderInfo, float f) {
//
//    }
//
//    @Override
//    public IParticleRenderType getRenderType() {
//        return null;
//    }
//
//    public static class Factory implements IParticleFactory<SimpleParticleType> {
//        private final Color color;
//        public Factory(Color color) {
//            this.color = color;
//        }
//
//        @Nullable
//        @Override
//        public Particle createParticle(SimpleParticleType typeIn, Level worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
//            return new JesterParticle(worldIn, this.color, x, y, z, xSpeed, ySpeed, zSpeed);
//        }
//    }
//}
