package com.carpet.rof.entity.merge;

import net.minecraft.world.phys.Vec3;

public class MergeSetting {
    public enum MergeTNTNextMode
    {
        TRUE, FALSE, SAFE, ALMOST_VANILLA, SAFE_PLUS
    }

    public record EntityPosAndVec(
            double posX, double posY, double posZ,
            double vecX, double vecY, double vecZ,
            int fuse){
        public EntityPosAndVec(Vec3 pos, Vec3 vec, int fuse){
            this(pos.x,pos.y,pos.z,vec.x,vec.y,vec.z,fuse);
        }
    }
}
