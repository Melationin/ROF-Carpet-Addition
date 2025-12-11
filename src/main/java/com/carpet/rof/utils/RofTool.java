package com.carpet.rof.utils;

import carpet.script.external.Vanilla;
import com.carpet.rof.ROFCarpetSettings;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestStorage;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import static net.minecraft.world.dimension.DimensionTypes.THE_NETHER;

public class RofTool {
    public static Boolean isNetherWorld(World world){
        return world.getDimensionEntry().matchesKey(THE_NETHER);
    }

    public static Path getSavePath(ServerWorld world) {
        return Vanilla.MinecraftServer_storageSource(world.getServer()).getWorldDirectory(world.getRegistryKey());
    }


    public static class EntityPosAndVec{
        final Vec3d pos;
        final Vec3d vec;
        final int Fuse;
        public EntityPosAndVec(Vec3d pos, Vec3d vec,int Fuse) {
            this.pos = pos;
            this.vec = vec;
            this.Fuse = Fuse;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            EntityPosAndVec that = (EntityPosAndVec) o;
            return Fuse == that.Fuse && Objects.equals(pos, that.pos) && Objects.equals(vec, that.vec);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos, vec, Fuse);
        }
    }

    public static int fastHash(int x){
        x = ((x >> 8) ^ x) * 0x119de1f3;
        x = ((x >> 8) ^ x) * 0x119de1f3;
        x = (x >> 8) ^ x;
        return x & 0xFFFF;
    }

    public static boolean canLoadAi(int id,int count,int max){
        return (fastHash(id) %(count+1)) <= max;
    }
}

