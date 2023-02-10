package com.calicraft.vrjester.utils.tools;

import com.mojang.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class Vec3 extends net.minecraft.world.phys.Vec3 {
    // This class is to de-obfuscate the Minecraft Vec3 class

    public Vec3(double x, double y, double z) {
        super(x, y, z);
    }

    public Vec3(Vector3f vector3f) {
        super(vector3f);
    }

    public Vec3(net.minecraft.world.phys.Vec3 vector3) {
        super(vector3.x, vector3.y, vector3.z);
    }

    public @NotNull Vec3 normalize() {
        double d0 = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        return d0 < 1.0E-4D ? (Vec3) ZERO : new Vec3(this.x / d0, this.y / d0, this.z / d0);
    }

    public double dot(Vec3 vec3) {
        return this.x * vec3.x + this.y * vec3.y + this.z * vec3.z;
    }

    public Vec3 cross(Vec3 vec3) {
        return new Vec3(this.y * vec3.z - this.z * vec3.y, this.z * vec3.x - this.x * vec3.z, this.x * vec3.y - this.y * vec3.x);
    }

    public Vec3 subtract(Vec3 vec3) {
        return this.subtract(vec3.x, vec3.y, vec3.z);
    }

    public @NotNull Vec3 subtract(double x, double y, double z) {
        return this.add(-x, -y, -z);
    }

    public Vec3 add(Vec3 vec3) {
        return this.add(vec3.x, vec3.y, vec3.z);
    }

    public @NotNull Vec3 add(double x, double y, double z) {
        return new Vec3(this.x + x, this.y + y, this.z + z);
    }

    public @NotNull Vec3 scale(double scalar) {
        return this.multiply(scalar, scalar, scalar);
    }

    public @NotNull Vec3 reverse() {
        return this.scale(-1.0D);
    }

    public Vec3 multiply(Vec3 vec3) {
        return this.multiply(vec3.x, vec3.y, vec3.z);
    }

    public @NotNull Vec3 multiply(double x, double y, double z) {
        return new Vec3(this.x * x, this.y * y, this.z * z);
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double lengthSqr() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public double horizontalDistance() {
        return Math.sqrt(this.x * this.x + this.z * this.z);
    }

    public double horizontalDistanceSqr() {
        return this.x * this.x + this.z * this.z;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof Vec3 vec3)) {
            return false;
        } else {
            if (Double.compare(vec3.x, this.x) != 0) {
                return false;
            } else if (Double.compare(vec3.y, this.y) != 0) {
                return false;
            } else {
                return Double.compare(vec3.z, this.z) == 0;
            }
        }
    }
}
